package com.brandon3055.draconicevolution.mixin;

import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianPartEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Predicate;

import static org.spongepowered.asm.mixin.injection.callback.LocalCapture.CAPTURE_FAILHARD;

/**
 * Created by covers1624 on 4/1/21.
 */
@Mixin (Chunk.class)
class ChunkMixin {

    @Inject (
            method = "getEntitiesWithinAABBForEntity",
            locals = CAPTURE_FAILHARD,
            at = @At (
                    value = "INVOKE",
                    shift = At.Shift.AFTER,
                    target = "Ljava/util/List;get(I)Ljava/lang/Object;"
            )
    )
    public void fillBoxes(Entity entityIn, AxisAlignedBB aabb, List<Entity> listToFill, Predicate<? super Entity> filter, CallbackInfo ci, int i, int j, int k, ClassInheritanceMultiMap<Entity> classinheritancemultimap, List<Entity> list, int l, int i1) {
        Entity entity = list.get(i1);
        if (entity instanceof DraconicGuardianEntity) {
            for (DraconicGuardianPartEntity part : ((DraconicGuardianEntity) entity).getParts()) {
                if (part != entityIn && part.getBoundingBox().intersects(aabb) && (filter == null || filter.test(part))) {
                    listToFill.add(part);
                }
            }
        }
    }

}
