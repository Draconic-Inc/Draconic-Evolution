package com.brandon3055.draconicevolution.api.modules.entities.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

/**
 * Created by brandon3055 on 31/01/2023
 */
public interface IHarvestHandler {

    /**
     * Start harvest operation
     * @param origin Starting position of the harvest operation.
     * @param level The level
     */
    void start(BlockPos origin, Level level, ServerPlayer player);

    /**
     * Update the harvest operation.
     */
    void tick(Level level, ServerPlayer player);

    /**
     * @return true if the operation is complete.
     */
    boolean isDone();

    /**
     * Interrupt the current harvest operation and clear any data stores.
     */
    void stop(Level level, ServerPlayer player);

}
