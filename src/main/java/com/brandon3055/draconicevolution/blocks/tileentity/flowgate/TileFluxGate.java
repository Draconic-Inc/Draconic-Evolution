package com.brandon3055.draconicevolution.blocks.tileentity.flowgate;

import cofh.api.energy.IEnergyReceiver;
import com.brandon3055.brandonscore.lib.EnergyHandlerWrapper;
import com.brandon3055.brandonscore.lib.EnergyHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

/**
 * Created by brandon3055 on 15/11/2016.
 */
public class TileFluxGate extends TileFlowGate implements IEnergyReceiver {

    //region Gate

    @Override
    public String getUnits() {
        return "RF/t";
    }

    //endregion

    //region IEnergy

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return from == getDirection() || from == getDirection().getOpposite();
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        if (getTarget() == null) {
            return 0;
        }
        return EnergyHelper.getEnergyStored(getTarget(), getDirection().getOpposite());
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        if (getTarget() == null) {
            return 0;
        }
        return EnergyHelper.getMaxEnergyStored(getTarget(), getDirection().getOpposite());
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        TileEntity target = getTarget();

        if (target == null) {
            return 0;
        }

        int sim = EnergyHelper.insertEnergy(target, maxReceive, getDirection().getOpposite(), true);
        int transfer = EnergyHelper.insertEnergy(target, Math.min(Math.max(0, getFlow() - transferThisTick), sim), getDirection().getOpposite(), simulate);

        if (!simulate) {
            transferThisTick += transfer;
        }
        return transfer;
    }

    //endregion

    //region Capability

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(new EnergyHandlerWrapper(this, facing));
        }

        return super.getCapability(capability, facing);
    }

    //endregion

    @Override
    public String getPeripheralName() {
        return "flux_gate";
    }
}
