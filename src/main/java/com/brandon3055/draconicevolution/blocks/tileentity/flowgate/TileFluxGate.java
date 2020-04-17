package com.brandon3055.draconicevolution.blocks.tileentity.flowgate;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;

/**
 * Created by brandon3055 on 15/11/2016.
 */
public class TileFluxGate extends TileFlowGate {

    private OPRegulator inputReg = new OPRegulator(this, true);
    private OPRegulator outputReg = new OPRegulator(this, false);

    public TileFluxGate() {
        super(DEContent.tile_flux_gate);
    }

    @Override
    public String getUnits() {
        return "RF/t";
    }

//    public boolean canConnectEnergy(Direction from) {
//        return from == getDirection() || from == getDirection().getOpposite();
//    }

    public long getEnergyStored(Direction from) {
        if (getTarget() == null) {
            return 0;
        }
        return EnergyUtils.getEnergyStored(getTarget(), getDirection().getOpposite());
    }

    public long getMaxEnergyStored(Direction from) {
        if (getTarget() == null) {
            return 0;
        }
        return EnergyUtils.getMaxEnergyStored(getTarget(), getDirection().getOpposite());
    }

    public long receiveEnergy(Direction from, int maxReceive, boolean simulate) {
        if (from != getDirection().getOpposite()) {
            return 0;
        }

        TileEntity target = getTarget();

        if (target == null) {
            return 0;
        }

        long sim = EnergyUtils.insertEnergy(target, maxReceive, getDirection().getOpposite(), true);
        long transfer = EnergyUtils.insertEnergy(target, Math.min(Math.max(0, getFlow() - transferThisTick), sim), getDirection().getOpposite(), simulate);

        if (!simulate) {
            transferThisTick += transfer;
        }
        return transfer;
    }

    //region Capability

//    @Override
//    public boolean hasCapability(Capability<?> capability, Direction facing) {
//        if (facing == getDirection() || facing == getDirection().getOpposite()) {
//            if (capability == CapabilityEnergy.ENERGY || capability == CapabilityOP.OP) {
//                return true;
//            }
//        }
//
//        return super.hasCapability(capability, facing);
//    }
//
//    @Override
//    public <T> T getCapability(Capability<T> capability, Direction facing) {
//        Direction dir = getDirection();
//        if (facing == dir || facing == dir.getOpposite()) {
//            if (capability == CapabilityEnergy.ENERGY) {
//                return CapabilityEnergy.ENERGY.cast(facing != dir ? outputReg : inputReg);
//            }
//            else if (capability == CapabilityOP.OP) {
//                return CapabilityOP.OP.cast(facing == dir ? outputReg : inputReg);
//
//            }
//        }
//
//        return super.getCapability(capability, facing);
//    }

    //endregion

    @Override
    public String getPeripheralName() {
        return "flux_gate";
    }

    private static class OPRegulator implements IOPStorage {

        private TileFluxGate tile;
        private boolean isInput;

        public OPRegulator(TileFluxGate tile, boolean isInput) {
            this.tile = tile;
            this.isInput = isInput;
        }

        @Override
        public long receiveOP(long maxReceive, boolean simulate) {
            if (isInput) {
                TileEntity target = tile.getTarget();

                if (target == null) {
                    return 0;
                }

                Direction tInputSide = tile.getDirection().getOpposite();
                long sim = EnergyUtils.insertEnergy(target, maxReceive, tInputSide, true);
                long transfer = EnergyUtils.insertEnergy(target, Math.min(Math.max(0, tile.getFlow() - tile.transferThisTick), sim), tInputSide, simulate);

                if (!simulate) {
                    tile.transferThisTick += transfer;
                }
                return transfer;
            }

            return 0;
        }

        @Override
        public long extractOP(long maxExtract, boolean simulate) {
            if (!isInput) {
                TileEntity source = tile.getSource();

                if (source == null) {
                    return 0;
                }

                Direction tExtractSide = tile.getDirection();
                long sim = EnergyUtils.extractEnergy(source, maxExtract, tExtractSide, true);
                long transfer = EnergyUtils.extractEnergy(source, Math.min(Math.max(0, tile.getFlow() - tile.transferThisTick), sim), tExtractSide, simulate);

                if (!simulate) {
                    tile.transferThisTick += transfer;
                }
                return transfer;
            }

            return 0;
        }

        @Override
        public long getOPStored() {
            if (isInput) {
                return 0;
            }

            TileEntity target = tile.getTarget();

            if (target == null) {
                return 0;
            }

            return EnergyUtils.getEnergyStored(target, tile.getDirection().getOpposite());
        }

        @Override
        public long getMaxOPStored() {
            if (isInput) {
                return 0;
            }

            TileEntity target = tile.getTarget();

            if (target == null) {
                return 0;
            }

            return EnergyUtils.getMaxEnergyStored(target, tile.getDirection().getOpposite());
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return (int) receiveOP(maxReceive, simulate);
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return (int) extractOP(maxExtract, simulate);
        }

        @Override
        public int getEnergyStored() {
            return (int) Math.min(Integer.MAX_VALUE, getOPStored());
        }

        @Override
        public int getMaxEnergyStored() {
            return (int) Math.min(Integer.MAX_VALUE, getMaxOPStored());
        }

        @Override
        public boolean canExtract() {
            return !isInput;
        }

        @Override
        public boolean canReceive() {
            return isInput;
        }
    }

}
