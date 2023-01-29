package com.brandon3055.draconicevolution.common.blocks;

import java.util.Random;

import net.minecraft.block.BlockAir;
import net.minecraft.entity.Entity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.entity.EntityChaosBolt;

/**
 * Created by brandon3055 on 30/9/2015.
 */
public class ChaosShardAtmos extends BlockAir {

    public ChaosShardAtmos() {
        this.setTickRandomly(true);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (world.isRemote || world.getClosestPlayer(x, y, z, 24) == null) {
            return;
        }

        if (random.nextInt(25) == 0) {
            for (int searchX = x - 15; searchX < x + 15; searchX++) {
                for (int searchZ = z - 15; searchZ < z + 15; searchZ++) {
                    if (world.getBlock(searchX, 80, searchZ) == ModBlocks.chaosCrystal) {
                        EntityChaosBolt bolt = new EntityChaosBolt(
                                world,
                                x + 0.5,
                                y + 0.5,
                                z + 0.5,
                                searchX + 0.5,
                                80.5,
                                searchZ + 0.5);
                        world.spawnEntityInWorld(bolt);
                        return;
                    }
                }
            }
            world.setBlockToAir(x, y, z);
        }
    }

    @Override
    public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
        return false;
    }
}
