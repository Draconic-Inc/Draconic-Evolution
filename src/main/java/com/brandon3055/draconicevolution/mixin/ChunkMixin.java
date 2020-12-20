package com.brandon3055.draconicevolution.mixin;

import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianPartEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by brandon3055 on 16/12/20
 */
@Mixin(Chunk.class)
public class ChunkMixin {

    @Final
    @Shadow
    private World world;

    @Final
    @Shadow
    private ClassInheritanceMultiMap<Entity>[] entityLists;

    @Inject(
            method = "getEntitiesWithinAABBForEntity(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/List;Ljava/util/function/Predicate;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    public void getEntitiesWithinAABBForEntity(@Nullable Entity entityIn, AxisAlignedBB aabb, List<Entity> listToFill, @Nullable Predicate<? super Entity> filter, CallbackInfo ci) {
        ci.cancel();

        int i = MathHelper.floor((aabb.minY - this.world.getMaxEntityRadius()) / 16.0D);
        int j = MathHelper.floor((aabb.maxY + this.world.getMaxEntityRadius()) / 16.0D);
        i = MathHelper.clamp(i, 0, this.entityLists.length - 1);
        j = MathHelper.clamp(j, 0, this.entityLists.length - 1);

        for (int k = i; k <= j; ++k) {
            ClassInheritanceMultiMap<Entity> classinheritancemultimap = this.entityLists[k];
            List<Entity> list = classinheritancemultimap.func_241289_a_();
            int l = list.size();

            for (int i1 = 0; i1 < l; ++i1) {
                Entity entity = list.get(i1);
                if (entity.getBoundingBox().intersects(aabb) && entity != entityIn) {
                    if (filter == null || filter.test(entity)) {
                        listToFill.add(entity);
                    }

                    if (entity instanceof EnderDragonEntity) {
                        for (EnderDragonPartEntity enderdragonpartentity : ((EnderDragonEntity) entity).getDragonParts()) {
                            if (enderdragonpartentity != entityIn && enderdragonpartentity.getBoundingBox().intersects(aabb) && (filter == null || filter.test(enderdragonpartentity))) {
                                listToFill.add(enderdragonpartentity);
                            }
                        }
                    } else if (entity instanceof DraconicGuardianEntity) {
                        for (DraconicGuardianPartEntity enderdragonpartentity : ((DraconicGuardianEntity) entity).getDragonParts()) {
                            if (enderdragonpartentity != entityIn && enderdragonpartentity.getBoundingBox().intersects(aabb) && (filter == null || filter.test(enderdragonpartentity))) {
                                listToFill.add(enderdragonpartentity);
                            }
                        }
                    }
                }
            }
        }
    }

}
