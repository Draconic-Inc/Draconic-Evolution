package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.draconicevolution.DEContent;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;

/**
 * Created by brandon3055 on 19/07/2016.
 */
public class TileCreativeOPCapacitor extends TileBCore implements ITickableTileEntity {

    public TileCreativeOPCapacitor() {
        super(DEContent.tile_creative_op_capacitor);

        capManager.set(CapabilityOP.OP, new IOPStorage() {
            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return maxReceive;
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return maxExtract;
            }

            @Override
            public int getEnergyStored() {
                return Integer.MAX_VALUE / 2;
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
                return true;
            }

            @Override
            public long getOPStored() {
                return Long.MAX_VALUE / 2;
            }

            @Override
            public long getMaxOPStored() {
                return Long.MAX_VALUE;
            }

            @Override
            public long receiveOP(long maxReceive, boolean simulate) {
                return maxReceive;
            }

            @Override
            public long extractOP(long maxExtract, boolean simulate) {
                return maxExtract;
            }
        });

    }

    @Override
    public void tick() {
        if (world.isRemote) {
            return;
        }

        for (Direction direction : Direction.values()) {
            sendEnergyTo(world, pos, Long.MAX_VALUE, direction);
        }
    }
}
