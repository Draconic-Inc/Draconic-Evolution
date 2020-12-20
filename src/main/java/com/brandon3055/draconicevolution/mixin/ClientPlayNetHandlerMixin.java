package com.brandon3055.draconicevolution.mixin;

import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianPartEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.BeeAngrySound;
import net.minecraft.client.audio.BeeFlightSound;
import net.minecraft.client.audio.BeeSound;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.server.SSpawnMobPacket;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created by brandon3055 on 16/12/20
 */
@Mixin(ClientPlayNetHandler.class)
public class ClientPlayNetHandlerMixin {

    @Final
    @Shadow
    private static Logger LOGGER;
    @Shadow
    private Minecraft client;
    @Shadow
    private ClientWorld world;


//    @Inject(
//            method = "handleSpawnMob(Lnet/minecraft/network/play/server/SSpawnMobPacket;)V",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/entity/EntityType;create(ILnet/minecraft/world/World;)Lnet/minecraft/entity/Entity;",
//                    shift = At.Shift.AFTER
//            ),
//            cancellable = false
//    )
//    public void handleSpawnMob(SSpawnMobPacket packetIn, CallbackInfo ci) {
//        LivingEntity livingentity = null;
//        LogHelper.dev("MIXIN!!! - " + livingentity);
//
//    }


    @Inject(
            method = "handleSpawnMob(Lnet/minecraft/network/play/server/SSpawnMobPacket;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    public void handleSpawnMob(SSpawnMobPacket packetIn, CallbackInfo ci) {
        ci.cancel();

        PacketThreadUtil.checkThreadAndEnqueue(packetIn, (ClientPlayNetHandler) (Object) this, this.client);
        double d0 = packetIn.getX();
        double d1 = packetIn.getY();
        double d2 = packetIn.getZ();
        float f = (float) (packetIn.getYaw() * 360) / 256.0F;
        float f1 = (float) (packetIn.getPitch() * 360) / 256.0F;
        LivingEntity livingentity = (LivingEntity) EntityType.create(packetIn.getEntityType(), this.client.world);
        if (livingentity != null) {
            livingentity.setPacketCoordinates(d0, d1, d2);
            livingentity.renderYawOffset = (float) (packetIn.getHeadPitch() * 360) / 256.0F;
            livingentity.rotationYawHead = (float) (packetIn.getHeadPitch() * 360) / 256.0F;
            if (livingentity instanceof EnderDragonEntity) {
                EnderDragonPartEntity[] aenderdragonpartentity = ((EnderDragonEntity) livingentity).getDragonParts();
                for (int i = 0; i < aenderdragonpartentity.length; ++i) {
                    aenderdragonpartentity[i].setEntityId(i + packetIn.getEntityID());
                }
            }
            else if (livingentity instanceof DraconicGuardianEntity) {
                DraconicGuardianPartEntity[] aenderdragonpartentity = ((DraconicGuardianEntity) livingentity).getDragonParts();
                for (int i = 0; i < aenderdragonpartentity.length; ++i) {
                    aenderdragonpartentity[i].setEntityId(i + packetIn.getEntityID());
                }
            }

            livingentity.setEntityId(packetIn.getEntityID());
            livingentity.setUniqueId(packetIn.getUniqueId());
            livingentity.setPositionAndRotation(d0, d1, d2, f, f1);
            livingentity.setMotion((double) ((float) packetIn.getVelocityX() / 8000.0F), (double) ((float) packetIn.getVelocityY() / 8000.0F), (double) ((float) packetIn.getVelocityZ() / 8000.0F));
            this.world.addEntity(packetIn.getEntityID(), livingentity);
            if (livingentity instanceof BeeEntity) {
                boolean flag = ((BeeEntity) livingentity).func_233678_J__();
                BeeSound beesound;
                if (flag) {
                    beesound = new BeeAngrySound((BeeEntity) livingentity);
                } else {
                    beesound = new BeeFlightSound((BeeEntity) livingentity);
                }

                this.client.getSoundHandler().playOnNextTick(beesound);
            }
        } else {
            LOGGER.warn("Skipping Entity with id {}", (int) packetIn.getEntityType());
        }
    }

}

