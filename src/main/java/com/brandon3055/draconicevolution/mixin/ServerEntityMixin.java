package com.brandon3055.draconicevolution.mixin;

import com.brandon3055.brandonscore.network.BCoreNetwork;
import com.brandon3055.draconicevolution.entity.projectile.DraconicArrowEntity;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.bundle.PacketAndPayloadAcceptor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;

/**
 * Created by covers1624 on 18/7/21.
 */
@Mixin(ServerEntity.class)
public abstract class ServerEntityMixin {

    @Final
    @Shadow
    private Entity entity;

    @Redirect(
            method = "sendChanges()V",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V",
                    ordinal = 2
            )
    )
    public void onVelocity(Consumer<Object> consumer, Object t) {
        if (!(entity instanceof DraconicArrowEntity)) {
            consumer.accept(t);
            return;
        }
        consumer.accept(BCoreNetwork.sendEntityVelocity(entity, false));
    }

    @Redirect(
            method = "Lnet/minecraft/server/level/ServerEntity;sendPairingData(Lnet/minecraft/server/level/ServerPlayer;Lnet/neoforged/neoforge/network/bundle/PacketAndPayloadAcceptor;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/network/bundle/PacketAndPayloadAcceptor;accept(Lnet/minecraft/network/protocol/Packet;)Lnet/neoforged/neoforge/network/bundle/PacketAndPayloadAcceptor;",
                    ordinal = 3
            )
    )
    public PacketAndPayloadAcceptor<?> onPairingData(PacketAndPayloadAcceptor<ClientGamePacketListener> instance, Packet t) {
        if (!(entity instanceof DraconicArrowEntity)) {
            instance.accept(t);
            return null;
        }
        instance.accept((Packet<? super ClientGamePacketListener>) BCoreNetwork.sendEntityVelocity(entity, false));
        return null;
    }

    @Redirect(
            method = "sendChanges()V",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V",
                    ordinal = 3
            )
    )
    public void onMovePacket(Consumer<Object> consumer, Object t) {
        if (!(t instanceof ClientboundMoveEntityPacket.PosRot) || !(entity instanceof DraconicArrowEntity)) {
            consumer.accept(t);
            return;
        }
        consumer.accept(BCoreNetwork.sendEntityVelocity(entity, true));
    }
}