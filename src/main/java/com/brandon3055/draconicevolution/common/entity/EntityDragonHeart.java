package com.brandon3055.draconicevolution.common.entity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import com.brandon3055.brandonscore.common.utills.Utills;
import com.brandon3055.draconicevolution.client.handler.ParticleHandler;
import com.brandon3055.draconicevolution.client.render.particle.Particles;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.blocks.DraconiumBlock;
import com.brandon3055.draconicevolution.common.blocks.multiblock.MultiblockHelper.TileLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Created by Brandon on 21/11/2014.
 */
public class EntityDragonHeart extends Entity {

    public int age = 0;
    public float rotation = 0f;
    public float rotationInc = 0.5f;
    public int opPhase = 0;
    private double yStop;
    private int coresConsumed = 0;
    private List<TileLocation> blocks = new ArrayList<TileLocation>();

    public EntityDragonHeart(World world) {
        super(world);
        this.setSize(0.25F, 0.25F);
    }

    public EntityDragonHeart(World par1World, double x, double y, double z) {
        super(par1World);
        this.setPosition(x, y, z);
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;
        this.setSize(0.25F, 0.25F);
        this.yStop = y + 1.5D;
    }

    @Override
    protected void entityInit() {
        renderDistanceWeight = 10;
        getDataWatcher().addObject(11, (float) yStop);
        getDataWatcher().addObject(12, rotationInc);
        getDataWatcher().addObject(13, coresConsumed);
        getDataWatcher().addObject(14, opPhase);
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float dmg) {
        return false;
    }

    @Override
    public void onUpdate() {

        if (!worldObj.isRemote) getDataWatcher().updateObject(14, opPhase);
        if (!worldObj.isRemote) getDataWatcher().updateObject(13, coresConsumed);
        if (!worldObj.isRemote) getDataWatcher().updateObject(12, rotationInc);
        if (!worldObj.isRemote) getDataWatcher().updateObject(11, (float) yStop);
        yStop = getDataWatcher().getWatchableObjectFloat(11);
        rotationInc = getDataWatcher().getWatchableObjectFloat(12);
        coresConsumed = getDataWatcher().getWatchableObjectInt(13);
        opPhase = getDataWatcher().getWatchableObjectInt(14);
        motionX = 0;
        motionZ = 0;
        motionY = 0;

        age++;
        rotation += rotationInc;
        super.onUpdate();

        switch (opPhase) {
            case 0: { // Rises with particle ring
                motionY = 0.01f;
                rotationInc += 0.1f;
                if (posY > yStop) {
                    opPhase = 1;
                    motionY = 0f;
                }
                break;
            }
            case 1: { // Particle ring retracts
                opPhase = 2;
                break;
            }
            case 2: { // Particle field accepting items
                if (coresConsumed == 16 || age > 1240) {
                    age = 1240;
                    opPhase = 3;
                    if (coresConsumed < 4) {
                        EntityPersistentItem item = new EntityPersistentItem(
                                worldObj,
                                posX,
                                posY,
                                posZ,
                                new ItemStack(ModItems.dragonHeart));
                        item.motionX = 0;
                        item.motionY = 0;
                        item.motionZ = 0;
                        item.delayBeforeCanPickup = 0;
                        if (!worldObj.isRemote) worldObj.spawnEntityInWorld(item);
                        this.worldObj.playSoundAtEntity(
                                this,
                                "random.pop",
                                0.2F,
                                ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                        this.setDead();
                    }
                    break;
                }
                List<EntityItem> items = worldObj.getEntitiesWithinAABB(
                        EntityItem.class,
                        AxisAlignedBB.getBoundingBox(posX - 5, posY - 5, posZ - 5, posX + 5, posY + 5, posZ + 5));
                for (EntityItem item : items) {
                    ItemStack stack = item.getEntityItem();
                    if (Utills.getDistanceAtoB(posX, posY + 0.5, posZ, item.posX, item.posY, item.posZ) < 1) {
                        if (coresConsumed == 16 || worldObj.isRemote) break;
                        if (stack.getItem() != ModItems.draconicCore) {
                            item.motionX = 1;
                            item.motionY = 6;
                            item.motionZ = 1;
                            continue;
                        }
                        int needed = 16 - coresConsumed;
                        if (stack.stackSize >= needed) {
                            coresConsumed = 16;
                            stack.stackSize -= needed;
                        } else {
                            coresConsumed += stack.stackSize;
                            stack.stackSize = 0;
                            item.setDead();
                        }
                        this.worldObj.playSoundAtEntity(
                                this,
                                "random.pop",
                                0.2F,
                                ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    } else {
                        item.motionX = ((posX - item.posX)) * 0.1;
                        item.motionY = ((posY + 0.5 - item.posY)) * 0.1;
                        item.motionZ = ((posZ - item.posZ)) * 0.1;
                    }
                }
                break;
            }
            case 3: {
                rotationInc += 0.2f;
                int maxBlocks = coresConsumed / 4;
                if (age % 10 == 0) {
                    blocks = new ArrayList<TileLocation>();
                    for (int x = (int) posX - 5; x <= (int) posX + 5; x++) {
                        for (int y = (int) posY - 5; y <= (int) posY + 5; y++) {
                            for (int z = (int) posZ - 5; z <= (int) posZ + 5; z++) {
                                if (worldObj.getBlock(x, y, z) instanceof DraconiumBlock
                                        && worldObj.getBlockMetadata(x, y, z) == 2) {
                                    TileLocation block = new TileLocation(x, y, z);
                                    if (!blocks.contains(block) && blocks.size() < maxBlocks) blocks.add(block);
                                }
                            }
                        }
                    }
                }
                if (age > 1600) opPhase = 4;
                break;
            }
            case 4: {
                if (blocks.size() == 0) {
                    if (!worldObj.isRemote) {
                        EntityPersistentItem item = new EntityPersistentItem(
                                worldObj,
                                posX,
                                posY,
                                posZ,
                                new ItemStack(ModItems.dragonHeart));
                        item.motionX = 0;
                        item.motionY = 0;
                        item.motionZ = 0;
                        item.delayBeforeCanPickup = 0;
                        worldObj.spawnEntityInWorld(item);
                    }
                    this.worldObj.playSoundAtEntity(
                            this,
                            "random.pop",
                            0.2F,
                            ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    this.setDead();
                    break;
                }
                for (TileLocation tile : blocks) {
                    if (!worldObj.isRemote) worldObj.setBlock(
                            tile.getXCoord(),
                            tile.getYCoord(),
                            tile.getZCoord(),
                            ModBlocks.draconicBlock,
                            0,
                            2);
                    worldObj.createExplosion(null, tile.getXCoord(), tile.getYCoord(), tile.getZCoord(), 4, false);
                }
                worldObj.createExplosion(null, posX, posY, posZ, 4, false);
                this.setDead();
            }
        }

        if (worldObj.isRemote) spawnParticles();
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
    }

    @SideOnly(Side.CLIENT)
    private void spawnParticles() {
        if (opPhase == 0) {
            double correctY = posY + 0.5;
            for (int i = 0; i < 10; i++) {
                int nextFloat = rand.nextInt();
                double offsetX = Math.sin(nextFloat);
                double offsetZ = Math.cos(nextFloat);
                EntityFX particle = new Particles.AdvancedSeekerParticle(
                        worldObj,
                        posX + offsetX,
                        correctY,
                        posZ + offsetZ,
                        posX,
                        correctY,
                        posZ,
                        1,
                        1f,
                        1f,
                        1f,
                        10);
                ParticleHandler.spawnCustomParticle(particle, 64);
            }
        }
        if (opPhase == 1) {
            double correctY = posY + 0.5;
            for (int i = 0; i < 100; i++) {
                int nextFloat = rand.nextInt();
                double offsetX = Math.sin(nextFloat);
                double offsetZ = Math.cos(nextFloat);
                EntityFX particle = new Particles.AdvancedSeekerParticle(
                        worldObj,
                        posX + offsetX,
                        correctY,
                        posZ + offsetZ,
                        posX,
                        correctY,
                        posZ,
                        3,
                        1f,
                        1f,
                        1f,
                        100,
                        -100);
                ParticleHandler.spawnCustomParticle(particle, 64);
            }
        }
        if (opPhase == 2) {
            double correctY = posY + 0.5;
            for (int i = 0; i < 10; i++) {
                int nextFloat = rand.nextInt();
                double offsetX = Math.sin(nextFloat) * (rand.nextFloat() * 10);
                double offsetZ = Math.cos(nextFloat) * (rand.nextFloat() * 10);
                EntityFX particle = new Particles.AdvancedSeekerParticle(
                        worldObj,
                        posX + offsetX,
                        correctY,
                        posZ + offsetZ,
                        posX,
                        correctY,
                        posZ,
                        3,
                        1f,
                        1f,
                        1f,
                        100,
                        -100);
                ParticleHandler.spawnCustomParticle(particle, 64);
            }
        }
        if (opPhase == 3) {

            float colourMod = (float) (age - 1240) / 360f;
            double correctY = posY + 0.5;
            int nextFloat = rand.nextInt();
            double offsetX = Math.sin(nextFloat) * (rand.nextFloat() * 10);
            double offsetZ = Math.cos(nextFloat) * (rand.nextFloat() * 10);
            EntityFX particle = new Particles.AdvancedSeekerParticle(
                    worldObj,
                    posX,
                    correctY,
                    posZ,
                    posX + offsetX,
                    correctY,
                    posZ + offsetZ,
                    3,
                    1f,
                    1f - colourMod,
                    1f - colourMod,
                    100,
                    -100);
            ParticleHandler.spawnCustomParticle(particle, 64);

            for (TileLocation tile : blocks) {
                particle = new Particles.AdvancedSeekerParticle(
                        worldObj,
                        posX,
                        correctY,
                        posZ,
                        tile.getXCoord() + rand.nextDouble(),
                        tile.getYCoord() + rand.nextDouble(),
                        tile.getZCoord() + rand.nextDouble(),
                        3,
                        1f,
                        1f - (colourMod * 0.5f),
                        1f - colourMod,
                        100,
                        -100);
                ParticleHandler.spawnCustomParticle(particle, 64);
            }
        }
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer player) {
        if (age < 2000) {
            age = 2000;
            opPhase = 2;
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("Age", age);
        compound.setInteger("Phase", opPhase);
        compound.setFloat("RotationSpeed", rotationInc);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        age = compound.getInteger("Age");
        opPhase = compound.getInteger("Phase");
        rotationInc = compound.getFloat("RotationSpeed");
    }
}
