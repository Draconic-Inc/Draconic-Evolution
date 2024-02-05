//package com.brandon3055.draconicevolution.blocks.tileentity;
//
//import com.brandon3055.brandonscore.blocks.TileBCore;
//import com.brandon3055.brandonscore.lib.datamanager.DataFlags;
//import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
//import com.brandon3055.brandonscore.lib.datamanager.ManagedDouble;
//import com.brandon3055.brandonscore.lib.datamanager.ManagedInt;
//import com.brandon3055.draconicevolution.init.DEContent;
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.level.block.state.BlockState;
//
//import java.util.Random;
//
///**
// * Created by brandon3055 on 30/3/2016.
// */
//public class TileParticleGenerator extends TileBCore {
//
//    //@formatter:off
//    public final ManagedInt    red           = register(new ManagedInt("red", 0, DataFlags.SAVE_BOTH_SYNC_TILE));
//    public final ManagedInt    green         = register(new ManagedInt("green", 0, DataFlags.SAVE_BOTH_SYNC_TILE));
//    public final ManagedInt    blue          = register(new ManagedInt("blue", 0, DataFlags.SAVE_BOTH_SYNC_TILE));
//    public final ManagedInt    alpha         = register(new ManagedInt("alpha", 0, DataFlags.SAVE_BOTH_SYNC_TILE));
//    public final ManagedInt    randomRed     = register(new ManagedInt("randomRed", 255, DataFlags.SAVE_BOTH_SYNC_TILE));
//    public final ManagedInt    randomGreen   = register(new ManagedInt("randomGreen", 255, DataFlags.SAVE_BOTH_SYNC_TILE));
//    public final ManagedInt    randomBlue    = register(new ManagedInt("randomBlue", 255, DataFlags.SAVE_BOTH_SYNC_TILE));
//    public final ManagedInt    randomAlpha   = register(new ManagedInt("randomAlpha", 255, DataFlags.SAVE_BOTH_SYNC_TILE));
//    public final ManagedDouble scale         = register(new ManagedDouble("scale", 0d, DataFlags.SAVE_BOTH_SYNC_TILE));
//    public final ManagedDouble randomScale   = register(new ManagedDouble("randomScale", 10d, DataFlags.SAVE_BOTH_SYNC_TILE));
//    public final ManagedDouble motionX       = register(new ManagedDouble("motionX", -.1d, DataFlags.SAVE_BOTH_SYNC_TILE));
//    public final ManagedDouble motionY       = register(new ManagedDouble("motionY", -.1d, DataFlags.SAVE_BOTH_SYNC_TILE));
//    public final ManagedDouble motionZ       = register(new ManagedDouble("motionZ", -.1d, DataFlags.SAVE_BOTH_SYNC_TILE));
//    public final ManagedDouble randomMotionX = register(new ManagedDouble("randomMotionX", .2d, DataFlags.SAVE_BOTH_SYNC_TILE));
//    public final ManagedDouble randomMotionY = register(new ManagedDouble("randomMotionY", .2d, DataFlags.SAVE_BOTH_SYNC_TILE));
//    public final ManagedDouble randomMotionZ = register(new ManagedDouble("randomMotionZ", .2d, DataFlags.SAVE_BOTH_SYNC_TILE));
//    public final ManagedDouble gravity       = register(new ManagedDouble("gravity", 0d, DataFlags.SAVE_BOTH_SYNC_TILE));
//    public final ManagedDouble randomGravity = register(new ManagedDouble("randomGravity", 0d, DataFlags.SAVE_BOTH_SYNC_TILE));
//    public final ManagedDouble spawnX        = register(new ManagedDouble("spawnX", -1d, DataFlags.SAVE_BOTH_SYNC_TILE));
//    public final ManagedDouble spawnY        = register(new ManagedDouble("spawnY", -1d, DataFlags.SAVE_BOTH_SYNC_TILE));
//    public final ManagedDouble spawnZ        = register(new ManagedDouble("spawnZ", -1d, DataFlags.SAVE_BOTH_SYNC_TILE));
//    public final ManagedDouble randomSpawnX  = register(new ManagedDouble("randomSpawnX", 2d, DataFlags.SAVE_BOTH_SYNC_TILE));
//    public final ManagedDouble randomSpawnY  = register(new ManagedDouble("randomSpawnY", 2d, DataFlags.SAVE_BOTH_SYNC_TILE));
//    public final ManagedDouble randomSpawnZ  = register(new ManagedDouble("randomSpawnZ", 2d, DataFlags.SAVE_BOTH_SYNC_TILE));
//    public final ManagedInt    life          = register(new ManagedInt("life", 20, DataFlags.SAVE_BOTH_SYNC_TILE));
//    public final ManagedInt    randomLife    = register(new ManagedInt("randomLife", 20, DataFlags.SAVE_BOTH_SYNC_TILE));
//    public final ManagedInt    fade          = register(new ManagedInt("fade", 0, DataFlags.SAVE_BOTH_SYNC_TILE));
//    public final ManagedInt    randomFade    = register(new ManagedInt("randomFade", 0, DataFlags.SAVE_BOTH_SYNC_TILE));
//    public final ManagedInt    delay         = register(new ManagedInt("delay", 40, DataFlags.SAVE_BOTH_SYNC_TILE));
//    public final ManagedInt    type          = register(new ManagedInt("type", 0, DataFlags.SAVE_BOTH_SYNC_TILE));
//    public final ManagedBool   collision     = register(new ManagedBool("collision", false, DataFlags.SAVE_BOTH_SYNC_TILE));
//
//    public final ManagedBool   inverted      = register(new ManagedBool("inverted", false, DataFlags.SAVE_BOTH_SYNC_TILE));
//    //@formatter:on
//
//    private int tick = 0;
//
//    public TileParticleGenerator(BlockPos pos, BlockState state) {
//        super(DEContent.TILE_PARTICLE_GENERATOR.get(), pos, state);
//    }
//
//    @Override
//    public void tick() {
//        super.tick();
//
//        if (level.isClientSide && (inverted.get() != level.hasNeighborSignal(worldPosition))) {
//            if (tick >= delay.get()) {
//                Random rand = level.random;
//
//                final double X = worldPosition.getX() + spawnX.get() + randomSpawnX.get() * rand.nextDouble() + .5D;
//                final double Y = worldPosition.getY() + spawnY.get() + randomSpawnY.get() * rand.nextDouble() + .5D;
//                final double Z = worldPosition.getZ() + spawnZ.get() + randomSpawnZ.get() * rand.nextDouble() + .5D;
//                final double MX = motionX.get() + randomMotionX.get() * rand.nextDouble();
//                final double MY = motionY.get() + randomMotionY.get() * rand.nextDouble();
//                final double MZ = motionZ.get() + randomMotionZ.get() * rand.nextDouble();
//                final int R = red.get() + rand.nextInt(randomRed.get() + 1);
//                final int G = green.get() + rand.nextInt(randomGreen.get() + 1);
//                final int B = blue.get() + rand.nextInt(randomBlue.get() + 1);
//                final int alpha = this.alpha.get() + rand.nextInt(randomAlpha.get() + 1);
//                final int scale = (int) Math.round((this.scale.get() + randomScale.get() * rand.nextDouble()) * 10000);
//                final int life = this.life.get() + rand.nextInt(randomLife.get() + 1);
//                final int gravity = (int) Math.round((this.gravity.get() + randomGravity.get() * rand.nextDouble()) * 10000);
//                final int fade = this.fade.get() + rand.nextInt(randomFade.get() + 1);
//
////                BCEffectHandler.spawnFX(DEParticles.CUSTOM, world, X, Y, Z, MX, MY, MZ, 32 * Math.max(1, this.scale.get()), R, G, B, alpha, scale, life, gravity, fade, type.get(), collision.get() ? 1 : 0);
//
//                tick = 0;
//            }
//
//            tick++;
//        }
//    }
//}
