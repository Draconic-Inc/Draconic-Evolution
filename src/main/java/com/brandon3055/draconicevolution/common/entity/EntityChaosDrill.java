package com.brandon3055.draconicevolution.common.entity;

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.brandon3055.brandonscore.common.utills.Utills;
import com.brandon3055.draconicevolution.common.blocks.multiblock.MultiblockHelper;
import com.brandon3055.draconicevolution.common.utills.LogHelper;

/**
 * Created by Brandon on 14/09/2014.
 */
public class EntityChaosDrill extends Entity {

    public int MAX_AGE = 300;
    private int ENTITY_AGE = 0;
    private EntityPlayer PLAYER;

    public EntityChaosDrill(World world) {
        super(world);
    }

    public EntityChaosDrill(World world, EntityPlayer player) {
        super(world);
        this.PLAYER = player;
        this.setSize(0.1F, 0.1F);
        this.setPosition(player.posX, player.posY, player.posZ);
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    protected void entityInit() {
        LogHelper.info("entityInit");
    }

    @Override
    public void onUpdate() {
        // LogHelper.info("onUpdate");
        mineNextBlockInPattern();
        entityTick();
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {}

    @Override
    protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {}

    private void entityTick() {
        if (ENTITY_AGE >= MAX_AGE) setDead();
        ENTITY_AGE++;
    }

    private void mineNextBlockInPattern() {
        ArrayList<MultiblockHelper.TileLocation> blocks = getBlocksInSphere(5);
        LogHelper.info(blocks.size());
        if (blocks.size() > 0)
            worldObj.setBlockToAir(blocks.get(1).getXCoord(), blocks.get(1).getYCoord(), blocks.get(1).getZCoord());
    }

    private ArrayList<MultiblockHelper.TileLocation> getBlocksInSphere(int r) {
        ArrayList<MultiblockHelper.TileLocation> blocks = new ArrayList<MultiblockHelper.TileLocation>();
        int minDist = 100;

        for (int x = (int) posX - r; x <= (int) posX + r; x++) {
            for (int z = (int) posZ - r; z <= (int) posZ + r; z++) {
                for (int y = (int) posY - r; y <= (int) posY + r; y++) {
                    int dist = (int) (Utills.getDistanceAtoB(x, y, z, (int) posX, (int) posY, (int) posZ));
                    if (dist <= r) {
                        if (!worldObj.isAirBlock(x, y, z)) {
                            if (blocks.size() <= dist) {
                                blocks.add(new MultiblockHelper.TileLocation(x, y, z));
                            } else {
                                blocks.set(dist, new MultiblockHelper.TileLocation(x, y, z));
                            }
                            if (dist < minDist) minDist = dist;
                        }
                    }
                }
            }
        }
        LogHelper.info("dist: " + minDist);

        return blocks;
    }
}
