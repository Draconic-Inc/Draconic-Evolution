package com.brandon3055.draconicevolution.entity;

import com.brandon3055.brandonscore.handlers.IProcess;
import com.brandon3055.brandonscore.handlers.ProcessHandler;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.lib.DEDamageSources;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Random;

/**
 * Created by brandon3055 on 12/8/2015.
 */
public class ProcessChaosImplosion implements IProcess {

    public static DamageSource chaosImplosion = new DamageSource("chaosImplosion").setExplosion().bypassArmor().bypassMagic();

    private Level world;
    private int xCoord;
    private int yCoord;
    private int zCoord;
    private float power;
    private Random random = new Random();

    private double expansion = 0;

    public ProcessChaosImplosion(Level world, int x, int y, int z) {
        this.world = world;
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
        this.power = 30F;
        isDead = world.isClientSide;
    }

    @Override
    public void updateProcess() {
        int OD = (int) expansion;
        int ID = OD - 1;
        int size = (int) expansion;

        for (int x = xCoord - size; x < xCoord + size; x++) {
            for (int z = zCoord - size; z < zCoord + size; z++) {
                double dist = Utils.getDistance(x, z, xCoord, zCoord);
                if (dist < OD && dist >= ID) {
                    float tracePower = power - (float) (expansion / 10D);
                    tracePower *= 1F + ((random.nextFloat() - 0.5F) * 0.2);
                    ProcessHandler.addProcess(new ChaosImplosionTrace(world, x, yCoord, z, tracePower, random));
                }
            }
        }

        isDead = expansion >= power * 10;
        expansion += 1;
    }

    private boolean isDead = false;

    @Override
    public boolean isDead() {
        return isDead;
    }

    public class ChaosImplosionTrace implements IProcess {

        private Level world;
        private int xCoord;
        private int yCoord;
        private int zCoord;
        private float power;
        private Random random;

        public ChaosImplosionTrace(Level world, int x, int y, int z, float power, Random random) {
            this.world = world;
            this.xCoord = x;
            this.yCoord = y;
            this.zCoord = z;
            this.power = power;
            this.random = random;
        }

        @Override
        public void updateProcess() {

            float energy = power * 10;

            for (int y = yCoord; y >= 0 && energy > 0; y--) {
                List<Entity> entities = world.getEntitiesOfClass(Entity.class, new AABB(xCoord, y, zCoord, xCoord + 1, y + 1, zCoord + 1));
                for (Entity entity : entities) entity.hurt(ProcessChaosImplosion.chaosImplosion, power * 100);

                //energy -= block instanceof BlockLiquid ? 10 : block.getExplosionResistance(null);

                if (energy >= 0) world.removeBlock(new BlockPos(xCoord, y, zCoord), false);
                energy -= 0.5F + (0.1F * (yCoord - y));
            }

            energy = power * 20;
            yCoord++;
            for (int y = yCoord; y < 255 && energy > 0; y++) {
                List<Entity> entities = world.getEntitiesOfClass(Entity.class, new AABB(xCoord, y, zCoord, xCoord + 1, y + 1, zCoord + 1));

                for (Entity entity : entities) {
                    entity.hurt(DEDamageSources.CHAOS_ISLAND_IMPLOSION, power * 100);
                }

                //energy -= block instanceof BlockLiquid ? 10 : block.getExplosionResistance(null);
                if (energy >= 0) world.removeBlock(new BlockPos(xCoord, y, zCoord), false);

                energy -= 0.5F + (0.1F * (y - yCoord));
            }

            isDead = true;
        }

        private boolean isDead = false;

        @Override
        public boolean isDead() {
            return isDead;
        }
    }
}
