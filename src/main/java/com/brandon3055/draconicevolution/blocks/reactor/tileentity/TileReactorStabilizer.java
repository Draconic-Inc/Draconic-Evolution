package com.brandon3055.draconicevolution.blocks.reactor.tileentity;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Created by brandon3055 on 18/01/2017.
 */
public class TileReactorStabilizer extends TileReactorComponent {

    public TileReactorStabilizer(BlockPos pos, BlockState state) {
        super(DEContent.tile_reactor_stabilizer, pos, state);
        OPExtractor opExtractor = new OPExtractor(this);
        capManager.set(CapabilityOP.OP, opExtractor);
        capManager.setCapSideValidator(opExtractor, face -> face == this.facing.get().getOpposite());
    }

    @Override
    public void tick() {
        super.tick();

        if (level.isClientSide) {
            return;
        }

        TileReactorCore tile = getCachedCore();

        if (tile != null && tile.reactorState.get() == TileReactorCore.ReactorState.RUNNING) {
            BlockEntity output = level.getBlockEntity(worldPosition.relative(facing.get().getOpposite()));
            if (output != null && EnergyUtils.canReceiveEnergy(output, facing.get())) {
                long sent = EnergyUtils.insertEnergy(output, tile.saturation.get(), facing.get(), false);
                tile.saturation.subtract(sent);
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
        public long modifyEnergyStored(long amount) {
            return 0; //Invalid operation for this device
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
