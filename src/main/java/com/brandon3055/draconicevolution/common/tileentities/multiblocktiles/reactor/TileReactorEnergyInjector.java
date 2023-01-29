package com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyReceiver;

import com.brandon3055.brandonscore.common.utills.Utills;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.render.particle.ParticleReactorBeam;
import com.brandon3055.draconicevolution.common.blocks.multiblock.IReactorPart;
import com.brandon3055.draconicevolution.common.blocks.multiblock.MultiblockHelper;
import com.brandon3055.draconicevolution.integration.computers.IDEPeripheral;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Created by Brandon on 23/7/2015.
 */
public class TileReactorEnergyInjector extends TileEntity implements IReactorPart, IEnergyReceiver, IDEPeripheral {

    public float modelIllumination = 1F;
    public int facingDirection = ForgeDirection.UP.ordinal();
    public MultiblockHelper.TileLocation masterLocation = new MultiblockHelper.TileLocation();
    public boolean isValid = false;
    public int tick = 0;

    @SideOnly(Side.CLIENT)
    private ParticleReactorBeam beam;

    private TileReactorCore core = null;
    private int redstoneMode = 0;
    private int rs = -1;
    private int rsCach = -1;

    @Override
    public void updateEntity() {
        if (worldObj.isRemote && isValid) {
            updateBeam();
        }

        TileEntity master = masterLocation.getTileEntity(worldObj);
        if (!worldObj.isRemote && master instanceof TileReactorCore) {
            rs = ((TileReactorCore) master).getComparatorOutput(redstoneMode);
            if (rs != rsCach) {
                rsCach = rs;
                Utills.updateNeabourBlocks(worldObj, xCoord, yCoord, zCoord);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void updateBeam() {
        beam = DraconicEvolution.proxy.reactorBeam(this, beam, true);
    }

    public void onPlaced() {
        checkForMaster();
    }

    public boolean checkForMaster() {
        for (int i = 1; i < 10; i++) {
            ForgeDirection dir = ForgeDirection.getOrientation(facingDirection);
            int x = xCoord + (dir.offsetX * i);
            int y = yCoord + (dir.offsetY * i);
            int z = zCoord + (dir.offsetZ * i);
            if (!worldObj.isAirBlock(x, y, z)) {
                TileEntity tile = worldObj.getTileEntity(x, y, z);
                if (tile instanceof TileReactorCore) {
                    masterLocation.set(x, y, z);
                    isValid = true;
                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                    return true;
                } else {
                    isValid = false;
                    return false;
                }
            }
        }
        isValid = false;
        return false;
    }

    @Override
    public boolean isActive() {
        return isValid;
    }

    @Override
    public String getRedstoneModeString() {
        return StatCollector.translateToLocal("msg.de.reactorRSMode." + redstoneMode + ".txt");
    }

    @Override
    public void changeRedstoneMode() {
        if (redstoneMode == IReactorPart.RMODE_FUEL_INV) redstoneMode = 0;
        else redstoneMode++;
    }

    @Override
    public int getRedstoneMode() {
        return redstoneMode;
    }

    @Override
    public void shutDown() {
        masterLocation = new MultiblockHelper.TileLocation();
        isValid = false;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound compound = new NBTTagCompound();
        masterLocation.writeToNBT(compound, "Master");
        compound.setInteger("Facing", facingDirection);
        compound.setBoolean("IsValid", isValid);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        NBTTagCompound compound = pkt.func_148857_g();
        masterLocation.readFromNBT(compound, "Master");
        facingDirection = compound.getInteger("Facing");
        isValid = compound.getBoolean("IsValid");
        super.onDataPacket(net, pkt);
    }

    public TileReactorCore getCore() {
        if (core == null || core.isInvalid()) {
            if (getMaster().getTileEntity(worldObj) instanceof TileReactorCore)
                core = (TileReactorCore) getMaster().getTileEntity(worldObj);
            else core = null;
        }
        return core;
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        masterLocation.writeToNBT(compound, "Master");
        compound.setInteger("Facing", facingDirection);
        compound.setBoolean("IsValid", isValid);
        compound.setInteger("RedstoneMode", redstoneMode);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        masterLocation.readFromNBT(compound, "Master");
        facingDirection = compound.getInteger("Facing");
        isValid = compound.getBoolean("IsValid");
        redstoneMode = compound.getInteger("RedstoneMode");
    }

    @Override
    public MultiblockHelper.TileLocation getMaster() {
        return masterLocation;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        if (simulate) return Integer.MAX_VALUE;
        else if (getCore() != null) return getCore().injectEnergy(maxReceive);
        return 0;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return 0;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return from == ForgeDirection.getOrientation(facingDirection).getOpposite();
    }

    @Override
    public String getName() {
        return "draconic_reactor";
    }

    @Override
    public String[] getMethodNames() {
        return new String[] { "getReactorInfo", "chargeReactor", "activateReactor", "stopReactor" };
    }

    @Override
    public Object[] callMethod(String method, Object... args) {
        if (!(getMaster().getTileEntity(worldObj) instanceof TileReactorCore)) return null;
        TileReactorCore reactor = (TileReactorCore) getMaster().getTileEntity(worldObj);

        if (args.length > 0) throw new IllegalArgumentException("This method dose not accept arguments");

        if (method.equals("getReactorInfo")) {
            Map<Object, Object> map = new HashMap<Object, Object>();
            map.put("temperature", Utills.round(reactor.reactionTemperature, 100));
            map.put("fieldStrength", Utills.round(reactor.fieldCharge, 100));
            map.put("maxFieldStrength", Utills.round(reactor.maxFieldCharge, 100));
            map.put("energySaturation", reactor.energySaturation);
            map.put("maxEnergySaturation", reactor.maxEnergySaturation);
            map.put("fuelConversion", Utills.round((double) reactor.convertedFuel + reactor.conversionUnit, 1000));
            map.put("maxFuelConversion", reactor.reactorFuel + reactor.convertedFuel);
            map.put("generationRate", (int) reactor.generationRate);
            map.put("fieldDrainRate", reactor.fieldDrain);
            map.put("fuelConversionRate", (int) Math.round(reactor.fuelUseRate * 1000000D));
            map.put(
                    "status",
                    reactor.reactorState == 0 ? "offline"
                            : reactor.reactorState == 1 && !reactor.canStart() ? "charging"
                                    : reactor.reactorState == 1 && reactor.canStart() ? "charged"
                                            : reactor.reactorState == 2 ? "online"
                                                    : reactor.reactorState == 3 ? "stopping" : "invalid");
            return new Object[] { map };
        } else if (method.equals("chargeReactor")) {
            if (reactor.canCharge()) {
                reactor.reactorState = TileReactorCore.STATE_START;
                return new Object[] { true };
            } else return new Object[] { false };
        } else if (method.equals("activateReactor")) {
            if (reactor.canStart()) {
                reactor.reactorState = TileReactorCore.STATE_ONLINE;
                return new Object[] { true };
            } else return new Object[] { false };
        } else if (method.equals("stopReactor")) {
            if (reactor.canStop()) {
                reactor.reactorState = TileReactorCore.STATE_STOP;
                return new Object[] { true };
            } else return new Object[] { false };
        }
        return new Object[] {};
    }
}
