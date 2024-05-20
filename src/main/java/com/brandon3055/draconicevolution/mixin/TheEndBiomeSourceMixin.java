package com.brandon3055.draconicevolution.mixin;

import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.world.ChaosIslandFeature;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Created by brandon3055 on 21/05/2024
 */
@Mixin(TheEndBiomeSource.class)
public class TheEndBiomeSourceMixin {

    @Shadow @Final private Holder<Biome> end;

    @Inject (
            method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;",
            cancellable = true,
            at = @At ("HEAD")
    )
    public void getNoiseBiome(int pX, int pY, int pZ, Climate.Sampler pSampler, CallbackInfoReturnable<Holder<Biome>> cir) {
        if (!DEConfig.chaosIslandEnabled) return;
        int x = QuartPos.toBlock(pX);
        int z = QuartPos.toBlock(pZ);
        ChunkPos chunkPos = new ChunkPos(x / 16, z / 16);
        ChunkPos closestSpawn = ChaosIslandFeature.getClosestSpawn(chunkPos);
        if (closestSpawn.x == 0 && closestSpawn.z == 0) return;
        if (ChaosIslandFeature.overrideBiome(chunkPos, closestSpawn)) {
            cir.setReturnValue(this.end);
        }
    }
}
