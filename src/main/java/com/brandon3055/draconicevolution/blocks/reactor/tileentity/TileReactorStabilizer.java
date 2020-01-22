package com.brandon3055.draconicevolution.blocks.reactor.tileentity;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.draconicevolution.integration.funkylocomotion.IMovableStructure;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by brandon3055 on 18/01/2017.
 */
public class TileReactorStabilizer extends TileReactorComponent implements /*IEnergyProvider,*/ IMovableStructure {

    public TileReactorStabilizer() {
        OPExtractor opExtractor = new OPExtractor(this);
        addRawEnergyCap(opExtractor);
        setCapSideValidator(opExtractor, face -> face == this.facing.get().getOpposite());
    }

    @Override
    public void update() {
        super.update();

        if (world.isRemote) {
            return;
        }

        TileReactorCore tile = getCachedCore();

<<<<<<< HEAD
        if (tile != null && tile.reactorState.get() == TileReactorCore.ReactorState.RUNNING) {
            TileEntity output = world.getTileEntity(pos.offset(facing.get().getOpposite()));
            if (output != null && EnergyUtils.canReceiveEnergy(output, facing.get())) {
                long sent = EnergyUtils.insertEnergy(output, tile.saturation.get(), facing.get(), false);
                tile.saturation.subtract(sent);
=======
        if (tile != null && tile.reactorState.value == TileReactorCore.ReactorState.RUNNING) {
            TileEntity output = world.getTileEntity(pos.offset(facing.value.getOpposite()));
            if (output != null && EnergyHelper.canReceiveEnergy(output, facing.value)) {
                int sent = EnergyHelper.insertEnergy(output, tile.saturation.value, facing.value, false);
                tile.saturation.value -= sent;
>>>>>>> parent of 9cd2c6a8... Implement Tile Data system changes.
            }
        }
    }

    private class OPExtractor implements IOPStorage {
        private TileReactorStabilizer tile;

        public OPExtractor(TileReactorStabilizer tile) {
            this.tile = tile;
        }

        @Override
        public long extractOP(long maxExtract, boolean simulate) {
            TileReactorCore core = getCachedCore();
            if (core != null && core.reactorState.get() == TileReactorCore.ReactorState.RUNNING) {
                long subtracted = Math.min(core.saturation.get(), maxExtract);
                if (!simulate) {
                    core.saturation.subtract(subtracted);
                }
                return subtracted;
            }

            return 0;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return 0;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return (int) extractOP(maxExtract, simulate);
        }

        @Override
        public long getMaxOPStored() {
            return Long.MAX_VALUE;
        }

        @Override
        public int getEnergyStored() {
            return 0;
        }

        @Override
        public int getMaxEnergyStored() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public boolean canReceive() {
            return false;
        }
    }
}
