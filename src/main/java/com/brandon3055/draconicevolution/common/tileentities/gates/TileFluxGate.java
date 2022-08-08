package com.brandon3055.draconicevolution.common.tileentities.gates;

import cofh.api.energy.IEnergyReceiver;
import com.brandon3055.brandonscore.common.utills.Utills;
import com.brandon3055.draconicevolution.common.lib.References;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Brandon on 29/6/2015.
 */
public class TileFluxGate extends TileGate implements IEnergyReceiver {

    private int transferredThisTick = 0;

    @Override
    public void updateEntity() {
        super.updateEntity();

        transferredThisTick = 0;
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        IEnergyReceiver target = getOutputTarget();
        //		int i = buffer.receiveEnergy(Math.max(0, Math.min(maxReceive, Math.min(getActualFlow(), getActualFlow() -
        // buffer.getEnergyStored()))), simulate);
        //		return i;
        int transfer = target == null
                ? 0
                : target.receiveEnergy(
                        from,
                        Math.min(
                                Math.max(0, getActualFlow() - transferredThisTick),
                                target.receiveEnergy(from, maxReceive, true)),
                        simulate);
        transferredThisTick += transfer;
        return transfer;
    }

    //	private EnergyStorage buffer = new EnergyStorage(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    //
    //	@Override
    //	public void updateEntity() {
    //		super.updateEntity();
    //
    //		IEnergyReceiver receiver = getOutputTarget();
    //		if (receiver != null && !worldObj.isRemote){
    //			buffer.extractEnergy(receiver.receiveEnergy(output.getOpposite(), getActualFlow(), false), false);
    //		}
    //	}
    //
    //	@Override
    //	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
    //		if (buffer.getEnergyStored() > 0) return 0;
    //		int i = buffer.receiveEnergy(Math.max(0, Math.min(maxReceive, Math.min(getActualFlow(), getActualFlow() -
    // buffer.getEnergyStored()))), simulate);
    //		return i;
    //	}

    @Override
    public int getEnergyStored(ForgeDirection from) {
        IEnergyReceiver target = getOutputTarget();
        return target == null ? 0 : target.getEnergyStored(from);
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        IEnergyReceiver target = getOutputTarget();
        return target == null ? 0 : target.getMaxEnergyStored(from);
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return from == output || from == output.getOpposite();
    }

    private IEnergyReceiver getOutputTarget() {
        TileEntity tile =
                worldObj.getTileEntity(xCoord + output.offsetX, yCoord + output.offsetY, zCoord + output.offsetZ);
        return tile instanceof IEnergyReceiver ? (IEnergyReceiver) tile : null;
    }

    @Override
    public String getFlowSetting(int selector) {
        return selector == 0 ? Utills.addCommas(flowRSLow) + " RF/t" : Utills.addCommas(flowRSHigh) + " RF/t";
    }

    @Override
    public void incrementFlow(int selector, boolean ctrl, boolean shift, boolean add, int button) {
        int amount =
                button == 0 ? shift ? ctrl ? 10000 : 1000 : ctrl ? 5 : 50 : shift ? ctrl ? 1000 : 100 : ctrl ? 1 : 10;

        if (selector == 0) {
            if (ctrl && shift && button == 0) amount += flowRSLow / 100000 * 1000;
            flowRSLow += (add ? amount : -amount);
            if (flowRSLow < 0) flowRSLow = 0;
            if (worldObj.isRemote) sendObjectToServer(References.INT_ID, 0, flowRSLow);
        } else {
            if (ctrl && shift && button == 0) amount += flowRSHigh / 100000 * 1000;
            flowRSHigh += add ? amount : -amount;
            if (flowRSHigh < 0) flowRSHigh = 0;
            if (worldObj.isRemote) sendObjectToServer(References.INT_ID, 1, flowRSHigh);
        }
    }

    @Override
    public String getToolTip(int selector, boolean shift, boolean ctrl) {
        int i = selector == 0 ? flowRSLow / 100000 * 10000 : flowRSHigh / 100000 * 10000;
        int b1 = shift ? ctrl ? 10000 + i : 1000 : ctrl ? 5 : 50;
        int b2 = shift ? ctrl ? 1000 : 100 : ctrl ? 1 : 10;
        return b1 + "/" + b2 + " RF/t";
    }

    @Override
    public String getName() {
        return "flux_gate";
    }
}
