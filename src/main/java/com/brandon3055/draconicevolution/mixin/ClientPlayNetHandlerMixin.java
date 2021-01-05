package com.brandon3055.draconicevolution.mixin;

import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianPartEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.play.server.SSpawnMobPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.spongepowered.asm.mixin.injection.callback.LocalCapture.CAPTURE_FAILHARD;

/**
 * Created by covers1624 on 4/1/21.
 */
@Mixin (ClientPlayNetHandler.class)
class ClientPlayNetHandlerMixin {

    @Inject (
            method = "handleSpawnMob",
            locals = CAPTURE_FAILHARD,
            at = @At (
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;setEntityId(I)V"
            )
    )
    public void onSpawnMob(SSpawnMobPacket packetIn, CallbackInfo ci, double d0, double d1, double d2, float f, float f1, LivingEntity livingentity) {
        if (livingentity instanceof DraconicGuardianEntity) {
            DraconicGuardianPartEntity[] parts = ((DraconicGuardianEntity) livingentity).getParts();
            for (int i = 0; i < parts.length; i++) {
                parts[i].setEntityId(i + packetIn.getEntityID());
            }
        }
    }
}
