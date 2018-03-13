package com.brandon3055.draconicevolution.api;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

/**
 * Created by brandon3055 on 2/26/2018.
 */
public interface ITeleportEndPoint {


    /**
     * @return the position of the block above the block you wish the player to spawn on.
     * The player will be spawned at x + 0.5, y + 0.2, z + 0.5 where x, y and z come from the pos provided by this method.
     */
    BlockPos getArrivalPos(String linkID);

    /**
     * This method should be called immediately before an entity is sent to the spawn pos.
     * @param entity the entity that is about to arrive.
     */
    void entityArriving(Entity entity);
}
