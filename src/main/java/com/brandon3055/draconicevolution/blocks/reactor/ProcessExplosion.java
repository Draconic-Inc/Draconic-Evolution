package com.brandon3055.draconicevolution.blocks.reactor;

import codechicken.lib.math.MathHelper;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.handlers.IProcess;
import com.brandon3055.brandonscore.lib.DelayedExecutor;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.utils.MathUtils;
import com.brandon3055.brandonscore.utils.SimplexNoise;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.lib.ExplosionHelper;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by brandon3055 on 11/03/2017.
 */
public class ProcessExplosion implements IProcess {

    public static DamageSource fusionExplosion = new DamageSource("damage.de.fusionExplode").setExplosion().bypassArmor().bypassMagic();

    /**
     * The origin of the explosion.
     */
    public final Vector3 origin;
    private final ServerLevel world;
    private final MinecraftServer server;
    private final int minimumDelay;
    public double[] angularResistance;
    public boolean isDead = false;
    public int radius = 0;
    public int maxRadius;
    public double circumference = 0;
    public double meanResistance = 0;
    public boolean enableEffect = true;
    protected boolean calculationComplete = false;
    protected boolean detonated = false;
    protected long startTime = -1;
    protected long calcWait = 0;
    /**
     * Set this to false to disable the lava dropped by the explosion.
     */
    public boolean lava = true;
    public HashSet<Long> blocksToUpdate = new HashSet<>();
    public LinkedList<HashSet<Long>> destroyedBlocks = new LinkedList<>();
    public HashSet<Long> lavaPositions = new HashSet<>();
    public HashSet<Long> destroyedCache = new HashSet<>();
    public HashSet<Long> scannedCache = new HashSet<>();
    public BlockPos.MutableBlockPos mPos = new BlockPos.MutableBlockPos();
    public Consumer<Double> progressMon = null;

    private BlockState lavaState;

    /**
     * This process is responsible for handling some extremely large explosions as efficiently as possible.
     * The absolute max recommended radius is 500 (that's 1000 across) However this has only been tested on a high end system and
     * may crash on systems with less ram and processing power. Recommended max is 350 - 400;
     *
     * @param origin           The origin of the explosion.
     * @param radius           The radius of the explosion (Note depending on terrain the actual destruction radius will be slightly less then this)
     * @param world            The server world.
     * @param minimumDelayTime The minimum delay in seconds before detonation.
     *                         If the explosion calculation completes before this time is up the process will wait till this amount of time has based before detonating.
     *                         Use -1 for manual detonation.
     */
    public ProcessExplosion(BlockPos origin, int radius, ServerLevel world, int minimumDelayTime) {
        this.origin = Vector3.fromBlockPosCenter(origin);
        this.world = world;
        this.server = world.getServer();
        this.minimumDelay = minimumDelayTime;
        this.angularResistance = new double[121];
        Arrays.fill(angularResistance, 100);

        LogHelper.info("Explosion Calculation Started for " + radius + " Block radius detonation!");
        maxRadius = radius;
        lavaState = Blocks.LAVA.defaultBlockState();
        //TODO pyrotheum
        if (ForgeRegistries.FLUIDS.containsKey(new ResourceLocation("cofhworld", "pyrotheum"))) {
            Fluid pyro = ForgeRegistries.FLUIDS.getValue(new ResourceLocation("cofhworld", "pyrotheum"));
//            if (pyro.canBePlacedInWorld()) {
//                lavaState = pyro.getAttributes().getBlock(world , pyro.getDefaultState())getBlock().getDefaultState();
//            }
        }
    }

    @Override
    public void updateProcess() {
        server.nextTickTime = Util.getMillis();
        if (startTime == -1) {
            startTime = System.currentTimeMillis();
        }

        if (calcWait > 0) {
            calcWait--;
            return;
        }

        if (!calculationComplete) {
            long t = System.currentTimeMillis();
            updateCalculation();
            t = System.currentTimeMillis() - t;
            calcWait = t / 40;
            LogHelper.dev("Calculation Progress: " + MathUtils.round((((double) radius / (double) maxRadius) * 100D), 100) + "% " + (Runtime.getRuntime().freeMemory() / 1000000));
            if (calcWait > 0) {
                LogHelper.dev("Explosion Calc loop took " + t + "ms! Waiting " + calcWait + " ticks before continuing");
            }
            if (progressMon != null) {
                progressMon.accept((double) radius / (double) maxRadius);
            }
        }
        else if (minimumDelay == -1) {
            isDead = true;
        }
        else {
            if ((System.currentTimeMillis() - startTime) / 1000 >= minimumDelay) {
                detonate();
            }
        }
    }

    public void updateCalculation() {
        BlockPos originPos = origin.pos();

        double maxCoreHeight = 20D * (maxRadius / 150D);

        Vector3 posVecUp = new Vector3();
        Vector3 posVecDown = new Vector3();
        for (int x = originPos.getX() - radius; x < originPos.getX() + radius; x++) {
            for (int z = originPos.getZ() - radius; z < originPos.getZ() + radius; z++) {
                double dist = Utils.getDistance(x, z, originPos.getX(), originPos.getZ());
                if (dist < radius && dist >= radius - 1) {
                    posVecUp.set(x + 0.5, origin.y, z + 0.5);
                    double radialAngle = getRadialAngle(posVecUp);
                    double radialResistance = getRadialResistance(radialAngle);
                    double angularLoad = (meanResistance / radialResistance) * 1;
                    double radialPos = 1D - (radius / (double) maxRadius);
                    double coreFalloff = Math.max(0, (radialPos - 0.8) * 5);
                    coreFalloff = 1 - ((1 - coreFalloff) * (1 - coreFalloff) * (1 - coreFalloff));
                    double coreHeight = coreFalloff * maxCoreHeight;
                    double edgeNoise = Math.max(0, (-radialPos + 0.2) * 5);
                    double edgeScatter = edgeNoise * world.random.nextInt(10);
                    double sim = SimplexNoise.noise(x / 50D, z / 50D);
                    edgeNoise = 1 + (Math.abs(sim) * edgeNoise * 8);

                    double power = (10000 * radialPos * radialPos * radialPos * angularLoad * edgeNoise) + edgeScatter;
                    double heightUp = 20 + ((5D + (radius / 10D)) * angularLoad);
                    double heightDown = coreHeight + ((5D + (radius / 10D)) * angularLoad * (1 - coreFalloff));
                    heightDown += (Math.abs(sim) * 4) + world.random.nextDouble();
                    heightUp += (Math.abs(sim) * 4) + world.random.nextDouble();

                    posVecDown.set(posVecUp);
                    double resist = trace(posVecUp, power/* * (1 + 8 * radialPos)*/, (int) heightUp * 3, 1, 0, 0);
                    resist += trace(posVecDown.subtract(0, 1, 0), power, (int) heightDown, -1, 0, 0);
                    resist *= 1 / angularLoad;

                    if (radialPos < 0.8) {
                        addRadialResistance(radialAngle, resist);
                    }
                }
            }
        }

        recalcResist();
        radius++;
        circumference = 2 * Math.PI * radius;

        destroyedBlocks.add(destroyedCache);
        destroyedCache = new HashSet<>();
        scannedCache = new HashSet<>();

        if (radius >= maxRadius) {
            LogHelper.dev("Explosion Calculation Completed!");
            calculationComplete = true;
        }
    }


    //region Math Stuff

    private void recalcResist() {
        double total = 0;
        for (double resist : angularResistance) {
            total += resist;
        }

        meanResistance = total / angularResistance.length;
    }

    public double getRadialAngle(Vector3 pos) {
        double theta = Math.atan2(pos.x - origin.x, origin.z - pos.z);

        if (theta < 0.0) {
            theta += Math.PI * 2;
        }

        return ((theta / (Math.PI * 2)) * (double) angularResistance.length);
    }

    public double getRadialResistance(double radialPos) {
        int min = MathHelper.floor(radialPos);
        if (min >= angularResistance.length) {
            min -= angularResistance.length;
        }
        int max = MathHelper.ceil(radialPos);
        if (max >= angularResistance.length) {
            max -= angularResistance.length;
        }

        double delta = radialPos - min;

        return (angularResistance[min] * (1 - delta)) + (angularResistance[max] * delta);
    }

    public void  addRadialResistance(double radialPos, double power) {
        int min = MathHelper.floor(radialPos);
        if (min >= angularResistance.length) {
            min -= angularResistance.length;
        }

        int max = MathHelper.ceil(radialPos);
        if (max >= angularResistance.length) {
            max -= angularResistance.length;
        }

        double delta = radialPos - min;
        angularResistance[min] += power * (1 - delta);
        angularResistance[max] += power * delta;
    }

    //endregion

    private double trace(Vector3 posVec, double power, int dist, int traceDir, double totalResist, int travel) {
        if (dist > 100) {
            dist = 100;
        }
        if (dist <= 0 || power <= 0 || posVec.y < world.getMinBuildHeight() || posVec.y > world.getMaxBuildHeight()) {
            return totalResist;
        }

        dist--;
        travel++;
        Long lPos = BlockPos.asLong((int)posVec.x, (int)posVec.y, (int)posVec.z);

        if (scannedCache.contains(lPos) || destroyedCache.contains(lPos)) {
            posVec.add(0, traceDir, 0);
            return trace(posVec, power, dist, traceDir, totalResist, travel);
        }

        mPos.set(lPos);

        double r = 1;

        BlockState state = world.getBlockState(mPos);
        Block block = state.getBlock();
        if (!state.isAir()) {
            Material mat = state.getMaterial();
            double effectivePower = (power / 10) * ((double) dist / (dist + travel));

            r = block.getExplosionResistance();
            double removeResist = r;
            //Helps ensure random blocks in the middle of the explosion that happen to have high resistance still get removed.
            if (removeResist > 25 && removeResist < 1000000 && dist < maxRadius * 0.75) {
                removeResist = 25;
            }

            if (effectivePower >= removeResist) {
                destroyedCache.add(lPos);
            }
            else if (mat == Material.WATER || mat == Material.LAVA) {
                if (effectivePower > 5) {
                    destroyedCache.add(lPos);
                }
                else {
                    blocksToUpdate.add(lPos);
                }
                r = 10;
            }
            else {
                if (block instanceof IFluidBlock || block instanceof FallingBlock) {
                    blocksToUpdate.add(lPos);
                }
                scannedCache.add(lPos);
            }

            if (r > 1000) {
                r = 1000;
            }
        }
        else {
            scannedCache.add(lPos);
        }

        r = (r / radius) / travel;//?

        totalResist += r;
        power -= r;

        if (dist == 1 && traceDir == -1 && lava && world.random.nextInt(250) == 0 && !world.isEmptyBlock(mPos.below())) {
            dist = 0;
            destroyedCache.remove(lPos);
            lavaPositions.add(lPos);
            blocksToUpdate.add(lPos);
            scannedCache.add(lPos);
        }

        posVec.add(0, traceDir, 0);
        return trace(posVec, power, dist, traceDir, totalResist, travel);
    }

    /**
     * @return true if explosion calculation is complete.
     */
    public boolean isCalculationComplete() {
        return calculationComplete;
    }

    /**
     * Call this once the explosion calculation has completed to manually detonate.
     *
     * @return false if calculation is not yet complete or detonation has already occurred.
     */
    public boolean detonate() {
        if (!isCalculationComplete() || detonated) {
            return false;
        }

        long l = System.currentTimeMillis();

        LogHelper.dev("Removing Blocks!");
        LogHelper.startTimer("Adding Blocks For Removal");

        ExplosionHelper removalHelper = new ExplosionHelper(world, origin.pos());
        int blocksRemoved = 0;

        removalHelper.setBlocksForRemoval(destroyedBlocks);

        LogHelper.stopTimer();

        LogHelper.startTimer("Adding update Blocks");
        removalHelper.addBlocksForUpdate(blocksToUpdate);
        LogHelper.dev("Blocks Removed: " + blocksRemoved);
        LogHelper.stopTimer();

        LogHelper.startTimer("Adding Lava");
        for (Long pos : lavaPositions) {
            world.setBlockAndUpdate(mPos.set(pos), lavaState);
        }
        LogHelper.stopTimer();

        removalHelper.finish();

        isDead = true;
        detonated = true;

        final BlockPos pos = origin.pos();
        if (enableEffect) {
            DraconicNetwork.sendExplosionEffect(world.dimension(), pos, radius * 4, true);
        }

        for (int i = 0; i <= radius; i+=10) {
        	double calcRadius = radius * (i / (double)radius);
            new DelayedExecutor(i + 30) {
                @Override
                public void execute(Object[] args) {
                    List<Entity> list = world.getEntitiesOfClass(Entity.class, new AABB(pos, pos.offset(1, 1, 1)).inflate(calcRadius * 2.5, calcRadius * 2.5, calcRadius * 2.5));
                    for (Entity e : list) {
                        double dist = Vec3D.getCenter(pos).distance(e);
                        float dmg = (1000) * (1F - (float) (dist / (calcRadius * 1.2D)));
                        if (dmg <= 0) continue;
                        e.hurt(fusionExplosion, dmg);
                    }
                }
            }.run();
        }

        LogHelper.dev("Total explosion time: " + (System.currentTimeMillis() - l) / 1000D + "s");
        return true;
    }

    @Override
    public boolean isDead() {
        return isDead;
    }

}
