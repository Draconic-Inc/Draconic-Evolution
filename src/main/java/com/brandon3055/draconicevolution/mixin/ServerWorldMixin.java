package com.brandon3055.draconicevolution.mixin;

import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianPartEntity;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.Entity;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created by covers1624 on 5/1/21.
 */
@Mixin (ServerWorld.class)
class ServerWorldMixin {

    @Final
    @Shadow
    private Int2ObjectMap<Entity> entitiesById;

    @Inject (
            method = "updateEntity",
            cancellable = true,
            at = @At ("HEAD")
    )
    public void onUpdateEntity(Entity entityIn, CallbackInfo ci) {
        if (entityIn instanceof DraconicGuardianPartEntity) {
            ci.cancel();
        }
    }

    @Inject (
            method = "removeEntityComplete",
            remap = false,
            at = @At ("HEAD")
    )
    public void removeEntityComplete(Entity entityIn, boolean keepData, CallbackInfo ci) {
        if (entityIn instanceof DraconicGuardianEntity) {
            for (DraconicGuardianPartEntity part : ((DraconicGuardianEntity) entityIn).getParts()) {
                part.remove(keepData);
            }
        }
    }

    @Inject (
            method = "onEntityAdded",
            at = @At (
                    value = "INVOKE",
                    shift = At.Shift.AFTER,
                    ordinal = 0,
                    target = "Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;put(ILjava/lang/Object;)Ljava/lang/Object;"
            )
    )
    public void onEntityAdded(Entity entityIn, CallbackInfo ci) {
        if (entityIn instanceof DraconicGuardianEntity) {
            for (DraconicGuardianPartEntity part : ((DraconicGuardianEntity) entityIn).getParts()) {
                entitiesById.put(part.getEntityId(), part);
            }
        }
    }
}
