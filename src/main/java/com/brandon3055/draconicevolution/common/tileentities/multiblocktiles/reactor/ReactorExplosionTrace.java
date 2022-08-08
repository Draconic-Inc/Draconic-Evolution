package com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor;

import com.brandon3055.brandonscore.common.handlers.IProcess;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

/**
 * Created by brandon3055 on 12/8/2015.
 */
public class ReactorExplosionTrace implements IProcess {

    private World worldObj;
    private int xCoord;
    private int yCoord;
    private int zCoord;
    private float power;
    private Random random;

    public ReactorExplosionTrace(World world, int x, int y, int z, float power, Random random) {
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
            Block block = worldObj.getBlock(xCoord, y, zCoord);

            List<Entity> entities = worldObj.getEntitiesWithinAABB(
                    Entity.class, AxisAlignedBB.getBoundingBox(xCoord, y, zCoord, xCoord + 1, y + 1, zCoord + 1));
            for (Entity entity : entities) entity.attackEntityFrom(ReactorExplosion.fusionExplosion, power * 100);

            energy -= block instanceof BlockLiquid ? 10 : block.getExplosionResistance(null);

            boolean blockRemoved = false;
            if (energy >= 0 && block != Blocks.air) {
                worldObj.setBlockToAir(xCoord, y, zCoord);
                blockRemoved = true;
            }
            energy -= 0.5F + (0.1F * (yCoord - y));

            if (energy <= 0 && random.nextInt(20) == 0 && blockRemoved) {
                if (random.nextInt(3) > 0) worldObj.setBlock(xCoord, y, zCoord, Blocks.fire);
                else {
                    worldObj.setBlock(xCoord, y, zCoord, Blocks.flowing_lava);
                    // worldObj.scheduleBlockUpdate(xCoord, y, zCoord, Blocks.flowing_lava, 100);
                }
            }
        }

        energy = power * 20;
        yCoord++;
        for (int y = yCoord; y < 255 && energy > 0; y++) {
            Block block = worldObj.getBlock(xCoord, y, zCoord);

            List<Entity> entities = worldObj.getEntitiesWithinAABB(
                    Entity.class, AxisAlignedBB.getBoundingBox(xCoord, y, zCoord, xCoord + 1, y + 1, zCoord + 1));
            for (Entity entity : entities) entity.attackEntityFrom(ReactorExplosion.fusionExplosion, power * 100);

            energy -= block instanceof BlockLiquid ? 10 : block.getExplosionResistance(null);
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
