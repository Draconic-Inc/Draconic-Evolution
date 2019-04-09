package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.client.particle.BCEffectHandler;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedDouble;
import com.brandon3055.brandonscore.lib.datamanager.ManagedInt;
import com.brandon3055.draconicevolution.client.DEParticles;
import net.minecraft.util.ITickable;

import java.util.Random;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class TileParticleGenerator extends TileBCBase implements ITickable {

    //@formatter:off
    public final ManagedInt           RED       = register("RED", new ManagedInt(0)).saveToTile().saveToItem().syncViaTile().finish();
    public final ManagedInt           GREEN     = register("GREEN", new ManagedInt(0)).saveToTile().saveToItem().syncViaTile().finish();
    public final ManagedInt           BLUE      = register("BLUE", new ManagedInt(0)).saveToTile().saveToItem().syncViaTile().finish();
    public final ManagedInt           ALPHA     = register("ALPHA", new ManagedInt(0)).saveToTile().saveToItem().syncViaTile().finish();
    public final ManagedInt    RANDOM_RED       = register("RANDOM_RED", new ManagedInt(255)).saveToTile().saveToItem().syncViaTile().finish();
    public final ManagedInt    RANDOM_GREEN     = register("RANDOM_GREEN", new ManagedInt(255)).saveToTile().saveToItem().syncViaTile().finish();
    public final ManagedInt    RANDOM_BLUE      = register("RANDOM_BLUE", new ManagedInt(255)).saveToTile().saveToItem().syncViaTile().finish();
    public final ManagedInt    RANDOM_ALPHA     = register("RANDOM_ALPHA", new ManagedInt(255)).saveToTile().saveToItem().syncViaTile().finish();
    public final ManagedDouble        SCALE     = register("SCALE", new ManagedDouble(0d)).saveToTile().saveToItem().syncViaTile().finish();
    public final ManagedDouble RANDOM_SCALE     = register("RANDOM_SCALE", new ManagedDouble(10d)).saveToTile().saveToItem().syncViaTile().finish();
    public final ManagedDouble        MOTION_X  = register("MOTION_X", new ManagedDouble(-.1d)).saveToTile().saveToItem().syncViaTile().finish();
    public final ManagedDouble        MOTION_Y  = register("MOTION_Y", new ManagedDouble(-.1d)).saveToTile().saveToItem().syncViaTile().finish();
    public final ManagedDouble        MOTION_Z  = register("MOTION_Z", new ManagedDouble(-.1d)).saveToTile().saveToItem().syncViaTile().finish();
    public final ManagedDouble RANDOM_MOTION_X  = register("RANDOM_MOTION_X", new ManagedDouble(.2d)).saveToTile().saveToItem().syncViaTile().finish();
    public final ManagedDouble RANDOM_MOTION_Y  = register("RANDOM_MOTION_Y", new ManagedDouble(.2d)).saveToTile().saveToItem().syncViaTile().finish();
    public final ManagedDouble RANDOM_MOTION_Z  = register("RANDOM_MOTION_Z", new ManagedDouble(.2d)).saveToTile().saveToItem().syncViaTile().finish();
    public final ManagedDouble        GRAVITY   = register("GRAVITY", new ManagedDouble(0d)).saveToTile().saveToItem().syncViaTile().finish();
    public final ManagedDouble RANDOM_GRAVITY   = register("RANDOM_GRAVITY", new ManagedDouble(0d)).saveToTile().saveToItem().syncViaTile().finish();
    public final ManagedDouble        SPAWN_X   = register("SPAWN_X", new ManagedDouble(-1d)).saveToTile().saveToItem().syncViaTile().finish();
    public final ManagedDouble        SPAWN_Y   = register("SPAWN_Y", new ManagedDouble(-1d)).saveToTile().saveToItem().syncViaTile().finish();
    public final ManagedDouble        SPAWN_Z   = register("SPAWN_Z", new ManagedDouble(-1d)).saveToTile().saveToItem().syncViaTile().finish();
    public final ManagedDouble RANDOM_SPAWN_X   = register("RANDOM_SPAWN_X", new ManagedDouble(2d)).saveToTile().saveToItem().syncViaTile().finish();
    public final ManagedDouble RANDOM_SPAWN_Y   = register("RANDOM_SPAWN_Y", new ManagedDouble(2d)).saveToTile().saveToItem().syncViaTile().finish();
    public final ManagedDouble RANDOM_SPAWN_Z   = register("RANDOM_SPAWN_Z", new ManagedDouble(2d)).saveToTile().saveToItem().syncViaTile().finish();
    public final ManagedInt           LIFE      = register("LIFE", new ManagedInt(20)).saveToTile().saveToItem().syncViaTile().finish();
    public final ManagedInt    RANDOM_LIFE      = register("RANDOM_LIFE", new ManagedInt(20)).saveToTile().saveToItem().syncViaTile().finish();
    public final ManagedInt           FADE      = register("FADE", new ManagedInt(0)).saveToTile().saveToItem().syncViaTile().finish();
    public final ManagedInt    RANDOM_FADE      = register("RANDOM_FADE", new ManagedInt(0)).saveToTile().saveToItem().syncViaTile().finish();
    public final ManagedInt           DELAY     = register("DELAY", new ManagedInt(40)).saveToTile().saveToItem().syncViaTile().finish();
    public final ManagedInt           TYPE      = register("TYPE", new ManagedInt(0)).saveToTile().saveToItem().syncViaTile().finish();
    public final ManagedBool          COLLISION = register("COLLISION", new ManagedBool(false)).saveToTile().saveToItem().syncViaTile().finish();
    //@formatter:on

    private int tick = 0;

    public TileParticleGenerator() {
        setShouldRefreshOnBlockChange();
    }

    @Override
    public void update() {
        super.update();

        if (world.isRemote && (getBlockMetadata() == 1 != world.isBlockIndirectlyGettingPowered(getPos()) > 0)) {
            if (tick >= DELAY.value) {
                Random rand = world.rand;

                final double X = pos.getX() + SPAWN_X.value + RANDOM_SPAWN_X.value * rand.nextDouble() + .5D;
                final double Y = pos.getY() + SPAWN_Y.value + RANDOM_SPAWN_Y.value * rand.nextDouble() + .5D;
                final double Z = pos.getZ() + SPAWN_Z.value + RANDOM_SPAWN_Z.value * rand.nextDouble() + .5D;
                final double MX = MOTION_X.value + RANDOM_MOTION_X.value * rand.nextDouble();
                final double MY = MOTION_Y.value + RANDOM_MOTION_Y.value * rand.nextDouble();
                final double MZ = MOTION_Z.value + RANDOM_MOTION_Z.value * rand.nextDouble();
                final int R = RED.value + rand.nextInt(RANDOM_RED.value + 1);
                final int G = GREEN.value + rand.nextInt(RANDOM_GREEN.value + 1);
                final int B = BLUE.value + rand.nextInt(RANDOM_BLUE.value + 1);
                final int alpha = ALPHA.value + rand.nextInt(RANDOM_ALPHA.value + 1);
                final int scale = (int) Math.round((SCALE.value + RANDOM_SCALE.value * rand.nextDouble()) * 10000);
                final int life = LIFE.value + rand.nextInt(RANDOM_LIFE.value + 1);
                final int gravity = (int) Math.round((GRAVITY.value + RANDOM_GRAVITY.value * rand.nextDouble()) * 10000);
                final int fade = FADE.value + rand.nextInt(RANDOM_FADE.value + 1);

                BCEffectHandler.spawnFX(DEParticles.CUSTOM, world, X, Y, Z, MX, MY, MZ, 32 * Math.max(1, SCALE.value), R, G, B, alpha, scale, life, gravity, fade, TYPE.value, COLLISION.value ? 1 : 0);

                tick = 0;
            }

            tick++;
        }
    }
}
