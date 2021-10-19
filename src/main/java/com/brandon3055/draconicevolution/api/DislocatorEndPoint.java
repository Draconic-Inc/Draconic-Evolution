package com.brandon3055.draconicevolution.api;

import com.brandon3055.brandonscore.api.math.Vector2;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Created by brandon3055 on 2/26/2018.
 * This interface can only be implemented by a tile entity.
 */
public interface DislocatorEndPoint {

    /**
     * @param linkID The link id for the incoming teleport.
     * @return The exact position to which an entity should be teleported when traveling to this end point. Or null if teleport can not proceed for some reason.
     */
    @Nullable
    Vector3d getArrivalPos(UUID linkID);

    @Nullable
    default Vector2f getArrivalFacing(UUID linkID) {
        return null;
    }

    /**
     * This method is called immediately before an entity is sent to the arrival pos.
     * @param entity the entity that is about to arrive.
     */
    default void entityArriving(Entity entity) {}

//    /**
//     * This method is called immediately after an entity is sent to the arrival pos.
//     * @param entity the entity that has just arrived.
//     */
//    default void entityArrived(Entity entity) {}
}
