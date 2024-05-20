package com.brandon3055.draconicevolution.mixin;

import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.world.ChaosIslandFeature;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Created by brandon3055 on 21/05/2024
 */
@Mixin (DensityFunctions.EndIslandDensityFunction.class)
public class EndIslandDensityFunctionMixin {

    @Inject (
            method = "getHeightValue(Lnet/minecraft/world/level/levelgen/synth/SimplexNoise;II)F",
            cancellable = true,
            at = @At ("RETURN")
    )
    private static void getHeightValue(SimplexNoise pNoise, int pX, int pZ, CallbackInfoReturnable<Float> cir) {
        if (!DEConfig.chaosIslandEnabled) return;
        int x = pX / 2;
        int z = pZ / 2;
        ChunkPos chunkPos = new ChunkPos(x, z);
        ChunkPos closestSpawn = ChaosIslandFeature.getClosestSpawn(chunkPos);
        if (closestSpawn.x == 0 && closestSpawn.z == 0) return;
        ChaosIslandFeature.overrideHeightValue(chunkPos, closestSpawn, cir.getReturnValue(), cir::setReturnValue);
    }
}
