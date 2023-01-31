package com.brandon3055.draconicevolution.api.modules.entities.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

/**
 * Created by brandon3055 on 31/01/2023
 */
public class TreeHarvestHandler implements IHarvestHandler {

    @Override
    public void start(BlockPos origin, Level level, ServerPlayer player) {

    }

    @Override
    public void tick(Level level, ServerPlayer player) {

    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public void stop(Level level, ServerPlayer player) {

    }
}
