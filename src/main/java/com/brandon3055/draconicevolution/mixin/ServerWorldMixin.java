package com.brandon3055.draconicevolution.mixin;

import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianPartEntity;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

/**
 * Created by brandon3055 on 16/12/20
 */
@Mixin(ServerWorld.class)
public class ServerWorldMixin {

    @Shadow
    boolean tickingEntities;
    @Final
    @Shadow
    private Queue<Entity> entitiesToAdd;
    @Final
    @Shadow
    private Int2ObjectMap<Entity> entitiesById;
    @Final
    @Shadow
    private Map<UUID, Entity> entitiesByUuid;
    @Final
    @Shadow
    private Set<PathNavigator> navigations;

    @Inject(
            method = "removeEntityComplete(Lnet/minecraft/entity/Entity;Z)V",
            at = @At("HEAD"),
            cancellable = false
    )
    public void removeEntityComplete(Entity entityIn, boolean keepData, CallbackInfo ci) {
        if (entityIn instanceof DraconicGuardianEntity) {
            for(DraconicGuardianPartEntity enderdragonpartentity : ((DraconicGuardianEntity)entityIn).getDragonParts()) {
                enderdragonpartentity.remove(keepData);
            }
        }
    }

    @Inject(
            method = "onEntityAdded(Lnet/minecraft/entity/Entity;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onEntityAdded(Entity entityIn, CallbackInfo ci) {
        ci.cancel();

        if (this.tickingEntities) {
            this.entitiesToAdd.add(entityIn);
        } else {
            this.entitiesById.put(entityIn.getEntityId(), entityIn);
            if (entityIn instanceof EnderDragonEntity) {
                for(EnderDragonPartEntity enderdragonpartentity : ((EnderDragonEntity)entityIn).getDragonParts()) {
                    this.entitiesById.put(enderdragonpartentity.getEntityId(), enderdragonpartentity);
                }
            }
            else if (entityIn instanceof DraconicGuardianEntity) {
                for(DraconicGuardianPartEntity enderdragonpartentity : ((DraconicGuardianEntity)entityIn).getDragonParts()) {
                    this.entitiesById.put(enderdragonpartentity.getEntityId(), enderdragonpartentity);
                }
            }

            this.entitiesByUuid.put(entityIn.getUniqueID(), entityIn);
            ((ServerWorld)(Object)this).getChunkProvider().track(entityIn);
            if (entityIn instanceof MobEntity) {
                this.navigations.add(((MobEntity)entityIn).getNavigator());
            }
        }

        entityIn.onAddedToWorld();
    }

}
