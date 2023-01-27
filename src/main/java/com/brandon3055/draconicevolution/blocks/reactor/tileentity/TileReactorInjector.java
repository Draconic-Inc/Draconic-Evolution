package com.brandon3055.draconicevolution.blocks.reactor.tileentity;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Created by brandon3055 on 18/01/2017.
 */
public class TileReactorInjector extends TileReactorComponent {

    public TileReactorInjector(BlockPos pos, BlockState state) {
        super(DEContent.tile_reactor_injector, pos, state);
        OPInjector opInjector = new OPInjector(this);
        capManager.set(CapabilityOP.OP, opInjector);
        capManager.setCapSideValidator(opInjector, face -> face == this.facing.get().getOpposite());
    }

    private static class OPInjector implements IOPStorage {
        private TileReactorInjector tile;

        public OPInjector(TileReactorInjector tile) {
            this.tile = tile;
        }

        @Override
        public long receiveOP(long maxReceive, boolean simulate) {
            if (simulate) {
                return maxReceive;
            }

            TileReactorCore core = tile.getCachedCore();

            if (core != null) {
                return core.injectEnergy(maxReceive);
            }
            return 0;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return (int) receiveOP(maxReceive, simulate);
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return 0;
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
        public long modifyEnergyStored(long amount) {
            return 0; //Invalid operation for this device
        }

        @Override
        public boolean canExtract() {
            return false;
        }

        @Override
        public boolean canReceive() {
            return true;
        }
    }
}
