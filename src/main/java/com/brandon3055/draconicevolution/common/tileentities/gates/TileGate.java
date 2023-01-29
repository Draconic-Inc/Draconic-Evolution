package com.brandon3055.draconicevolution.common.tileentities.gates;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import com.brandon3055.brandonscore.common.utills.Utills;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.tileentities.TileObjectSync;
import com.brandon3055.draconicevolution.integration.computers.IDEPeripheral;

/**
 * Created by Brandon on 29/6/2015.
 */
public abstract class TileGate extends TileObjectSync implements IDEPeripheral {

    public ForgeDirection output = ForgeDirection.DOWN;
    public int flowRSLow = 0;
    public int flowRSHigh = 1000;
    public int signal = -1;
    public boolean flowOverridden = false;
    protected int flowOverride = 0;

    public abstract String getFlowSetting(int selector);

    public abstract void incrementFlow(int selector, boolean ctrl, boolean shift, boolean add, int button);

    public abstract String getToolTip(int selector, boolean shift, boolean ctrl);

    public int getActualFlow() {
        if (flowOverridden) return flowOverride;
        if (signal == -1) signal = worldObj.getStrongestIndirectPower(xCoord, yCoord, zCoord);
        return flowRSLow + (int) (((double) signal / 15D) * (double) (flowRSHigh - flowRSLow));
    }

    @Override
    public void receiveObjectFromClient(int index, Object object) {
        if (index == 0) flowRSLow = (Integer) object;
        else if (index == 1) flowRSHigh = (Integer) object;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void receiveObjectFromServer(int index, Object object) {
        if (index == 2) {
            flowOverridden = (Boolean) object;
        } else if (index == 3) {
            flowOverride = (Integer) object;
        } else if (index == 4) {
            flowRSHigh = (Integer) object;
        } else if (index == 5) {
            flowRSLow = (Integer) object;
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("Output", output.ordinal());
        compound.setInteger("FlowRSLow", flowRSLow);
        compound.setInteger("FlowRSHigh", flowRSHigh);
        compound.setInteger("Signal", signal);
        compound.setInteger("FlowOverride", flowOverride);
        compound.setBoolean("FlowOverridden", flowOverridden);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        output = ForgeDirection.getOrientation(compound.getInteger("Output"));
        flowRSLow = compound.getInteger("FlowRSLow");
        flowRSHigh = compound.getInteger("FlowRSHigh");
        signal = compound.getInteger("Signal");
        flowOverride = compound.getInteger("FlowOverride");
        flowOverridden = compound.getBoolean("FlowOverridden");
    }

    @Override
    public String[] getMethodNames() {
        return new String[] { "getFlow", "setOverrideEnabled", "getOverrideEnabled", "setFlowOverride",
                "setSignalHighFlow", "getSignalHighFlow", "setSignalLowFlow", "getSignalLowFlow" };
    }

    @Override
    public Object[] callMethod(String method, Object... args) {
        if (method.equals("getFlow")) {
            return new Object[] { getActualFlow() };
        } else if (method.equals("setOverrideEnabled")) {
            if (args.length == 0 || !(args[0] instanceof Boolean)) throw new IllegalArgumentException(
                    "Expected Boolean got " + (args.length == 0 ? "nil" : args[0].getClass().getSimpleName()));
            flowOverridden = (Boolean) args[0];
            if (!worldObj.isRemote) sendObjectToClient(References.BOOLEAN_ID, 2, flowOverridden);
        } else if (method.equals("getOverrideEnabled")) {
            return new Object[] { flowOverridden };
        } else if (method.equals("setFlowOverride")) {
            if (args.length == 0 || !(args[0] instanceof Number)) throw new IllegalArgumentException(
                    "Expected Number got " + (args.length == 0 ? "nil" : args[0].getClass().getSimpleName()));
            flowOverride = Utills.toInt((Double) args[0]);
            if (!worldObj.isRemote) sendObjectToClient(References.INT_ID, 3, flowOverride);
        } else if (method.equals("setSignalHighFlow")) {
            if (args.length == 0 || !(args[0] instanceof Number)) throw new IllegalArgumentException(
                    "Expected Number got " + (args.length == 0 ? "nil" : args[0].getClass().getSimpleName()));
            flowRSHigh = Utills.toInt((Double) args[0]);
            if (!worldObj.isRemote) sendObjectToClient(References.INT_ID, 4, flowRSHigh);
        } else if (method.equals("getSignalHighFlow")) {
            return new Object[] { flowRSHigh };
        } else if (method.equals("setSignalLowFlow")) {
            if (args.length == 0 || !(args[0] instanceof Number)) throw new IllegalArgumentException(
                    "Expected Number got " + (args.length == 0 ? "nil" : args[0].getClass().getSimpleName()));
            flowRSLow = Utills.toInt((Double) args[0]);
            if (!worldObj.isRemote) sendObjectToClient(References.INT_ID, 5, flowRSLow);
        } else if (method.equals("getSignalLowFlow")) {
            return new Object[] { flowRSLow };
        }

        return new Object[0];
    }
}
