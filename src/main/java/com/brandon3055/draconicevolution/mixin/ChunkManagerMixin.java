package com.brandon3055.draconicevolution.mixin;

import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianPartEntity;
import net.minecraft.entity.Entity;
import net.minecraft.world.server.ChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created by covers1624 on 5/1/21.
 */
@Mixin (ChunkManager.class)
class ChunkManagerMixin {

    @Inject (
            method = "track",
            cancellable = true,
            at = @At ("HEAD")
    )
    public void onTrack(Entity entityIn, CallbackInfo ci) {
        if (entityIn instanceof DraconicGuardianPartEntity) {
            ci.cancel();
        }
    }

}
