package com.brandon3055.draconicevolution.common.entity;

import com.brandon3055.brandonscore.common.handlers.IProcess;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor.ReactorExplosion;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

/**
 * Created by brandon3055 on 12/8/2015.
 */
public class ChaosImplosionTrace implements IProcess {

    private World worldObj;
    private int xCoord;
    private int yCoord;
    private int zCoord;
    private float power;
    private Random random;

    public ChaosImplosionTrace(World world, int x, int y, int z, float power, Random random) {
        this.worldObj = world;
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
        this.power = power;
        this.random = random;
    }

    @Override
    public void updateProcess() {

        float energy = power * 10;

        for (int y = yCoord; y > 0 && energy > 0; y--) {
            List<Entity> entities = worldObj.getEntitiesWithinAABB(
                    Entity.class, AxisAlignedBB.getBoundingBox(xCoord, y, zCoord, xCoord + 1, y + 1, zCoord + 1));
            for (Entity entity : entities) entity.attackEntityFrom(ChaosImplosion.chaosImplosion, power * 100);

            // energy -= block instanceof BlockLiquid ? 10 : block.getExplosionResistance(null);

            if (energy >= 0) worldObj.setBlockToAir(xCoord, y, zCoord);
            energy -= 0.5F + (0.1F * (yCoord - y));
        }

        energy = power * 20;
        yCoord++;
        for (int y = yCoord; y < 255 && energy > 0; y++) {
            List<Entity> entities = worldObj.getEntitiesWithinAABB(
                    Entity.class, AxisAlignedBB.getBoundingBox(xCoord, y, zCoord, xCoord + 1, y + 1, zCoord + 1));
            for (Entity entity : entities) entity.attackEntityFrom(ReactorExplosion.fusionExplosion, power * 100);

            // energy -= block instanceof BlockLiquid ? 10 : block.getExplosionResistance(null);
            if (energy >= 0) worldObj.setBlockToAir(xCoord, y, zCoord);

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
