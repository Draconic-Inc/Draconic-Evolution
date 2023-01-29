package com.brandon3055.draconicevolution.api;

import java.util.Random;

import net.minecraft.world.World;

import com.brandon3055.brandonscore.common.utills.DataUtills;
import com.brandon3055.draconicevolution.common.world.ChaosWorldGenHandler;
import com.brandon3055.draconicevolution.common.world.DraconicWorldGenerator;
import com.brandon3055.draconicevolution.common.world.WorldGenEnderComet;
import com.brandon3055.draconicevolution.common.world.WorldGenEnderIsland;

/**
 * Created by Brandon on 11/7/2015.
 */
public class GeneratorOverride {

    /**
     * Spawns a comet at the given x y z coordinates
     */
    public void spawnCometAt(World world, int x, int y, int z, Random rand) {
        new WorldGenEnderComet().generate(world, rand, x, y, z);
    }

    /**
     * This nolonger dose anything!
     */
    @Deprecated
    public void spawnChaosIslendAt(World world, int x, int y, int z, Random rand) {
        new WorldGenEnderIsland().generate(world, rand, x, y, z);
    }

    /**
     * This can be used to generate a chaos island at a given set of coordinates This method needs to be called foe
     * every chunk in a 360 x 360 block area. Its ok to run it on a larger area it just wont do anything to chunks
     * outside that area.
     *
     * @param chunkX  the x pos of the chunk being generated (this is the xCoord divided by 16 not the xCoord itself)
     * @param chunkZ  the z pos of the chunk being generated (this is the zCoord divided by 16 not the zCoord itself)
     * @param centerX this should be the center of the area you are spawning the island it (this is the actual xCoord)
     * @param centerZ this should be the center of the area you are spawning the island it (this is the actual zCoord)
     * @param random  a random
     */
    public static void generateChaosChunk(World world, int chunkX, int chunkZ, int centerX, int centerZ,
            Random random) {
        ChaosWorldGenHandler.generateChunk(
                world,
                chunkX,
                chunkZ,
                new DataUtills.XZPair<Integer, Integer>(centerX, centerZ),
                random);
    }

    /**
     * Use this at startup to disable / re enable default chaos island generation
     */
    public static void setChaosIslandsEnabled(boolean chaosIslandsEnabled) {
        DraconicWorldGenerator.chaosIslandsEnabled = chaosIslandsEnabled;
    }

    /**
     * Use this at startup to disable / re enable default comet
     */
    public static void setCometsEnabled(boolean cometsEnabled) {
        DraconicWorldGenerator.cometsEnabled = cometsEnabled;
    }

    /**
     * Use this at startup to disable / re enable default draconium generation in the end
     */
    public static void setOreEnabledInEnd(boolean cometsEnabled) {
        DraconicWorldGenerator.oreEnabledInEnd = cometsEnabled;
    }
}
