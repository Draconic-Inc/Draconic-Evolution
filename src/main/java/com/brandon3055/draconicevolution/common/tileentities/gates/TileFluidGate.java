package com.brandon3055.draconicevolution.common.tileentities.gates;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import com.brandon3055.brandonscore.common.utills.Utills;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.utills.LogHelper;

/**
 * Created by Brandon on 29/6/2015.
 */
public class TileFluidGate extends TileGate implements IFluidHandler {

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        IFluidHandler target = getOutputTarget();
        if (target == null) return 0;
        int transfer = Math.min(getActualFlow(), target.fill(from, resource, false));
        if (transfer < resource.amount) {
            FluidStack newStack = resource.copy();
            newStack.amount = transfer;
            resource.amount -= transfer;
            return target.fill(from, newStack, doFill);
        }
        return target.fill(from, resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        IFluidHandler target = getOutputTarget();
        LogHelper.info(target != null && target.canFill(from, fluid));
        return target != null && target.canFill(from, fluid);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[0];
    }

    private IFluidHandler getOutputTarget() {
        TileEntity tile = worldObj
                .getTileEntity(xCoord + output.offsetX, yCoord + output.offsetY, zCoord + output.offsetZ);
        return tile instanceof IFluidHandler ? (IFluidHandler) tile : null;
    }

    @Override
    public String getFlowSetting(int selector) {
        return selector == 0 ? Utills.addCommas(flowRSLow) + " MB/t" : Utills.addCommas(flowRSHigh) + " MB/t";
    }

    @Override
    public void incrementFlow(int selector, boolean ctrl, boolean shift, boolean add, int button) {
        int amount = button == 0 ? shift ? ctrl ? 1000 : 100 : ctrl ? 50 : 5 : shift ? ctrl ? 100 : 50 : ctrl ? 10 : 1;
        if (selector == 0) {
            flowRSLow += add ? amount : -amount;
            if (flowRSLow < 0) flowRSLow = 0;
            if (worldObj.isRemote) sendObjectToServer(References.INT_ID, 0, flowRSLow);
        } else {
            flowRSHigh += add ? amount : -amount;
            if (flowRSHigh < 0) flowRSHigh = 0;
            if (worldObj.isRemote) sendObjectToServer(References.INT_ID, 1, flowRSHigh);
        }
    }

    @Override
    public String getToolTip(int selector, boolean shift, boolean ctrl) {
        int b1 = shift ? ctrl ? 1000 : 100 : ctrl ? 50 : 5;
        int b2 = shift ? ctrl ? 100 : 50 : ctrl ? 10 : 1;
        return b1 + "/" + b2 + " MB/t";
    }

    @Override
    public String getName() {
        return "fluid_gate";
    }
}
