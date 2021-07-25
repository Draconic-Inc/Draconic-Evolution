package com.brandon3055.draconicevolution.mixin;

import com.brandon3055.brandonscore.network.BCoreNetwork;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.entity.projectile.DraconicArrowEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SEntityPacket;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.world.TrackedEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;

/**
 * Created by covers1624 on 18/7/21.
 */
@Mixin(TrackedEntity.class)
public abstract class TrackedEntityMixin {

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
            method = "sendPairingData(Ljava/util/function/Consumer;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V",
                    ordinal = 3
            )
    )
    public void onPairingData(Consumer<Object> consumer, Object t) {
        if (!(entity instanceof DraconicArrowEntity)) {
            consumer.accept(t);
            return;
        }
        consumer.accept(BCoreNetwork.sendEntityVelocity(entity, false));
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
        if (!(t instanceof SEntityPacket.MovePacket) || !(entity instanceof DraconicArrowEntity)) {
            consumer.accept(t);
            return;
        }
        consumer.accept(BCoreNetwork.sendEntityVelocity(entity, true));
    }
}