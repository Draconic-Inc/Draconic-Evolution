package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.EntityBlockBCore;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.lib.CustomTabHandling;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.world.EnderCometFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Created by brandon3055 on 23/05/2024
 */
public class CometSpawner extends EntityBlockBCore implements CustomTabHandling {
    public CometSpawner(Properties properties) {
        super(properties);
        setBlockEntity(DEContent.TILE_COMET_SPAWNER::get, true);
    }

    public static class TileCometSpawner extends TileBCore {
        public TileCometSpawner(BlockPos pos, BlockState state) {
            super(DEContent.TILE_COMET_SPAWNER.get(), pos, state);
        }

        @Override
        public void tick() {
            if (level.isClientSide) return;
            level.removeBlock(getBlockPos(), false);
            //TODO This is absolutely not the correct way to do this! I just couldn't get the structure system to cooperate..
            EnderCometFeature.buildComet(level, getBlockPos(), level.random);
        }
    }
}
