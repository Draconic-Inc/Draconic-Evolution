package com.brandon3055.draconicevolution.lib;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import com.brandon3055.brandonscore.api.power.IOInfo;
import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.api.power.IOTracker;
import com.brandon3055.brandonscore.api.power.IOTrackerSelfTimed;
import com.brandon3055.brandonscore.lib.IMCDataSerializable;
import com.brandon3055.brandonscore.lib.IValueHashable;
import com.brandon3055.brandonscore.utils.Utils;
import com.google.common.math.BigIntegerMath;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.VisibleForTesting;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Supplier;

/**
 * Created by brandon3055 on 21/09/2022
 */
public class OPStorageOP implements INBTSerializable<CompoundTag>, IValueHashable<OPStorageOP.ComparableValue>, IMCDataSerializable, IOPStorage {

    /**
     * The base storage for the current value minus overflow.
     * This is used for efficiency as most of the time the energy stored will never exceed Long.MAX_VALUE
     */
    @VisibleForTesting
    protected long valueStorage = 0;
    /**
     * Stores the number of times valueStorage has rolled over.
     */
    @VisibleForTesting
    protected BigInteger overflowCount = BigInteger.ZERO;

    protected IOTracker ioTracker;
    private Supplier<Long> capacity;

    /**
     * @param capacity supplier for the maximum capacity of this storage or -1 for unlimited.
     */
    public OPStorageOP(Supplier<Long> capacity) {
        this.capacity = capacity;
    }

    @Override
    public long extractOP(long maxExtract, boolean simulate) {
        //If this is true we have over Long.MAX_VALUE stored.
        if (!overflowCount.equals(BigInteger.ZERO)) {
            if (!simulate) {
                valueStorage -= maxExtract;
                if (valueStorage <= 0) {
                    valueStorage = Long.MAX_VALUE;
                    overflowCount = overflowCount.subtract(BigInteger.ONE);
                }
                if (ioTracker != null) {
                    ioTracker.energyExtracted(maxExtract);
                }
            }
            return maxExtract;
        }


        long energyExtracted = Math.min(valueStorage, maxExtract);
        if (!simulate) {
            valueStorage -= energyExtracted;
            if (ioTracker != null) {
                ioTracker.energyExtracted(energyExtracted);
            }
        }

        return energyExtracted;
    }

    @Override
    public long receiveOP(long maxReceive, boolean simulate) {
        long limit = capacity.get();
        //We act like a normal energy storage
        if (limit != -1) {
            long energyReceived = Math.min(limit - valueStorage, maxReceive);
            if (!simulate) {
                valueStorage += energyReceived;
                if (ioTracker != null) {
                    ioTracker.energyInserted(energyReceived);
                }
            }
            return energyReceived;
        }

        //Otherwise... We don't really need to worry about such things as 'limits'

        if (!simulate) {
            //The maximum energy that can be added before valueStorage overflows.
            long maxB4Over = Long.MAX_VALUE - valueStorage;
            if (maxReceive > maxB4Over) {
                valueStorage = maxReceive - maxB4Over;
                overflowCount = overflowCount.add(BigInteger.ONE);
            } else {
                valueStorage += maxReceive;
            }
            if (ioTracker != null) {
                ioTracker.energyInserted(maxReceive);
            }
        }

        return maxReceive;
    }

    //This is capped at Long.MAX_VALUE / 2 to account for senders that preemptively check available space before sending energy.
    @Override
    public long getOPStored() {
        if (capacity.get() == -1) {
            if (!overflowCount.equals(BigInteger.ZERO)) {
                return Long.MAX_VALUE / 2;
            }
            return Math.min(valueStorage, Long.MAX_VALUE / 2);
        }
        return valueStorage;
    }

    //Version of getOPStored for internal use that does not cap out at Long.MAX_VALUE / 2
    public long getUncappedStored() {
        if (!overflowCount.equals(BigInteger.ZERO)) {
            return Long.MAX_VALUE;
        }
        return valueStorage;
    }

    @Override
    public long getMaxOPStored() {
        long cap = capacity.get();
        return cap == -1 ? Long.MAX_VALUE : cap;
    }

    public void validateStorage() {
        long cap = capacity.get();
        if (cap != -1) {
            overflowCount = BigInteger.ZERO;
            valueStorage = Math.min(valueStorage, cap);
        }
    }

    @Override
    public void serializeMCD(MCDataOutput output) {
        output.writeVarLong(valueStorage);
        output.writeBytes(overflowCount.toByteArray());
        output.writeBoolean(ioTracker != null);
        if (ioTracker != null) {
            output.writeVarLong(ioTracker.currentInput());
            output.writeVarLong(ioTracker.currentOutput());
        }
    }

    @Override
    public void deSerializeMCD(MCDataInput input) {
        valueStorage = input.readVarLong();
        overflowCount = new BigInteger(input.readBytes());
        if (input.readBoolean()) {
            if (ioTracker == null) {
                ioTracker = new IOTrackerSelfTimed();
            }
            ioTracker.syncClientValues(input.readVarLong(), input.readVarLong());
        } else if (ioTracker != null) {
            ioTracker = null;
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("storage", valueStorage);
        if (!overflowCount.equals(BigInteger.ZERO)) {
            tag.putByteArray("overflow", overflowCount.toByteArray());
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        valueStorage = nbt.getLong("storage");
        if (nbt.contains("overflow", 7)) {
            overflowCount = new BigInteger(nbt.getByteArray("overflow"));
        }
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    public void setIOTracker(@javax.annotation.Nullable IOTracker ioTracker) {
        this.ioTracker = ioTracker;
    }

    @Nullable
    @Override
    public IOInfo getIOInfo() {
        return ioTracker;
    }

    @Override
    public long modifyEnergyStored(long amount) {
        if (amount > 0) {
            return receiveOP(amount, false);
        } else {
            return extractOP(-amount, false);
        }
    }

    @Override
    public ComparableValue getValueHash() {
        return new ComparableValue(this);
    }

    @Override
    public boolean checkValueHash(Object vh) {
        if (vh instanceof ComparableValue v) {
            boolean mainCheck = v.valueStorage == valueStorage && v.overflowCount.equals(overflowCount);
            if (ioTracker != null) {
                return mainCheck && v.currentInput == ioTracker.currentInput() && v.currentOutput == ioTracker.currentOutput();
            }
            return mainCheck;
        }

        return false;
    }

    public boolean isUnlimited() {
        return capacity.get() == -1;
    }

    protected static class ComparableValue {
        private long valueStorage;
        private BigInteger overflowCount;
        private long currentInput = 0;
        private long currentOutput = 0;

        public ComparableValue(OPStorageOP storage) {
            valueStorage = storage.valueStorage;
            overflowCount = storage.overflowCount;
            if (storage.ioTracker != null) {
                currentInput = storage.ioTracker.currentInput();
                currentOutput = storage.ioTracker.currentOutput();
            }
        }
    }

    private static BigInteger prefixEnd = new BigInteger("975781955236953990712502012356953416011859675");
    private static NumberFormat decimalFormat = new DecimalFormat("0.######E0", DecimalFormatSymbols.getInstance(Locale.ROOT));


    public String getReadable() {
        if (overflowCount.compareTo(prefixEnd) > 0) {
            return decimalFormat.format(overflowCount) + " x (2^64)";
        }

        BigInteger value = BigInteger.valueOf(valueStorage).add(overflowCount.multiply(BigInteger.valueOf(Long.MAX_VALUE)));
        if (value.equals(BigInteger.ZERO)) {
            return "0";
        }
        int digits = BigIntegerMath.log10(value, RoundingMode.DOWN);
        int prefixStep = (digits / 3) * 3;

        if (digits < 6) {
            return Utils.addCommas(value.longValue());
        }

        BigDecimal decimal = new BigDecimal(value).divide(BigDecimal.valueOf(10).pow(prefixStep), 3, RoundingMode.DOWN);
        return decimal.doubleValue() + I18n.get("numprefix.draconicevolution.10-" + prefixStep);
    }

    public String getReadableCapacity() {
        if (capacity.get() == -1) {
            return "~1x10^1300000000";
        } else {
            long cap = capacity.get();
            int digits = (int)Math.log10(cap);
            int prefixStep = (digits / 3) * 3;

            if (digits < 6) {
                return Utils.addCommas(cap) + " OP";
            }

            double decimal = cap / Math.pow(10, prefixStep);//new BigDecimal(value).divide(BigDecimal.valueOf(10).pow(prefixStep), 3, RoundingMode.DOWN);
            return (Math.round(decimal * 1000) / 1000D) + I18n.get("numprefix.draconicevolution.10-" + prefixStep);
        }
    }

    public String getScientific() {
        if (overflowCount.compareTo(prefixEnd) > 0) {
            return decimalFormat.format(overflowCount) + " x (2^64) OP";
        }

        return decimalFormat.format(getStoredBig());
    }

    public BigInteger getStoredBig() {
        return BigInteger.valueOf(valueStorage).add(overflowCount.multiply(BigInteger.valueOf(Long.MAX_VALUE)));
    }
}
