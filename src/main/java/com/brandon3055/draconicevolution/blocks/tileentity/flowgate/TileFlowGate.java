package com.brandon3055.draconicevolution.blocks.tileentity.flowgate;

import codechicken.lib.data.MCDataInput;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.lib.IActivatableTile;
import com.brandon3055.brandonscore.lib.IChangeListener;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedByte;
import com.brandon3055.brandonscore.lib.datamanager.ManagedLong;
import com.brandon3055.draconicevolution.blocks.machines.FlowGate;
import com.brandon3055.draconicevolution.integration.computers.ArgHelper;
import com.brandon3055.draconicevolution.integration.computers.IDEPeripheral;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.SAVE_BOTH_SYNC_TILE;
import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.SAVE_NBT_SYNC_TILE;

/**
 * Created by brandon3055 on 15/11/2016.
 */
public abstract class TileFlowGate extends TileBCore implements ITickableTileEntity, IChangeListener, IDEPeripheral, INamedContainerProvider, IActivatableTile {

    protected long transferThisTick = 0;

    public final ManagedLong minFlow = register(new ManagedLong("min_flow", SAVE_BOTH_SYNC_TILE ));
    public final ManagedLong maxFlow = register(new ManagedLong("max_flow", SAVE_BOTH_SYNC_TILE));
    public final ManagedLong flowOverride = register(new ManagedLong("flow_override", SAVE_NBT_SYNC_TILE));
    public final ManagedBool flowOverridden = register(new ManagedBool("flow_overridden", SAVE_NBT_SYNC_TILE));
    public final ManagedByte rsSignal = register(new ManagedByte("rs_signal", (byte) -1, SAVE_NBT_SYNC_TILE));

    private Direction rotationCache = null;

    public TileFlowGate(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public void tick() {
        super.tick();
        transferThisTick = 0;
    }

    //region Gate

    public abstract String getUnits();

    @OnlyIn(Dist.CLIENT)
    public void setMin(String value) {
        sendPacketToServer(output -> output.writeString(value), 0);
    }

    @OnlyIn(Dist.CLIENT)
    public void setMax(String value) {
        sendPacketToServer(output -> output.writeString(value), 1);
    }

    public long getFlow() {
        if (flowOverridden.get()) {
            return flowOverride.get();
        }
        if (rsSignal.get() == -1) {
            rsSignal.set((byte) level.getBestNeighborSignal(worldPosition));
        }
        return minFlow.get() + (long) (((double) rsSignal.get() / 15D) * (double) (maxFlow.get() - minFlow.get()));
    }

    //endregion


    @Override
    public void receivePacketFromClient(MCDataInput data, ServerPlayerEntity client, int id) {
        if (flowOverridden.get()) {
            return;
        }

        try {
            String value = data.readString();
            long l = Long.parseLong(value);
            if (l < 0) {
                l = 0;
            }
            else if (l > Integer.MAX_VALUE) {
                l = Integer.MAX_VALUE;
            }

            if (id == 0) {
                minFlow.set((int) l);
            }
            else if (id == 1) {
                maxFlow.set((int) l);
            }
        }
        catch (NumberFormatException ignored) {
        }
    }

    public TileEntity getTarget() {
        return level.getBlockEntity(worldPosition.relative(getDirection()));
    }

    public TileEntity getSource() {
        return level.getBlockEntity(worldPosition.relative(getDirection().getOpposite()));
    }

    @Override
    public void clearCache() {
        super.clearCache();
        rotationCache = null;
    }

    public Direction getDirection() {
        if (rotationCache == null) {
            rotationCache = getBlockState().getValue(FlowGate.FACING);
        }
        return rotationCache;
    }

    @Override
    public void onNeighborChange(BlockPos neighbor) {
        rsSignal.set((byte) level.getBestNeighborSignal(worldPosition));
    }

    //region Peripheral

    @Override
    public String[] getMethodNames() {
        return new String[]{"getFlow", "setOverrideEnabled", "getOverrideEnabled", "setFlowOverride", "setSignalHighFlow", "getSignalHighFlow", "setSignalLowFlow", "getSignalLowFlow"};
    }

    @Override
    public Object[] callMethod(String method, ArgHelper args) {
        switch (method) {
            case "getFlow":
                return new Object[]{getFlow()};
            case "setOverrideEnabled":
                flowOverridden.set(args.checkBoolean(0));
                break;
            case "getOverrideEnabled":
                return new Object[]{flowOverridden};
            case "setFlowOverride":
                flowOverride.set(args.checkLong(0));
                break;
            case "setSignalHighFlow":
                maxFlow.set(args.checkLong(0));
                break;
            case "getSignalHighFlow":
                return new Object[]{maxFlow.get()};
            case "setSignalLowFlow":
                minFlow.set(args.checkLong(0));
                break;
            case "getSignalLowFlow":
                return new Object[]{minFlow.get()};
        }

        return new Object[0];
    }

    //endregion
}
