package com.brandon3055.draconicevolution.mixin;

import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianPartEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Created by covers1624 on 4/1/21.
 */
@Mixin (PlayerEntity.class)
class PlayerEntityMixin {

    @ModifyVariable (
            method = "attackTargetEntityWithCurrentItem",
            ordinal = 1,
            at = @At (
                    value = "FIELD",
                    ordinal = 0,
                    target = "Lnet/minecraft/world/World;isRemote:Z"
            )
    )
    public Entity getParent(Entity entity) {
        if (entity instanceof DraconicGuardianPartEntity) {
            return ((DraconicGuardianPartEntity) entity).dragon;
        }
        return entity;
    }

}
