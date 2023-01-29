package com.brandon3055.draconicevolution.common.entity;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemExpireEvent;

import com.brandon3055.draconicevolution.common.items.DragonHeart;

/**
 * Created by Brandon on 15/08/2014.
 */
public class EntityPersistentItem extends EntityItem {

    public EntityPersistentItem(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
        this.isImmuneToFire = true;
        this.lifespan = 72000;
    }

    public EntityPersistentItem(World par1World, double par2, double par4, double par6, ItemStack par8ItemStack) {
        this(par1World, par2, par4, par6);
        this.setEntityItemStack(par8ItemStack);
        this.lifespan = 72000;
    }

    public EntityPersistentItem(World par1World) {
        super(par1World);
        this.isImmuneToFire = true;
        this.lifespan = 72000;
    }

    public EntityPersistentItem(World world, Entity original, ItemStack stack) {
        this(world, original.posX, original.posY, original.posZ);
        if (original instanceof EntityItem) this.delayBeforeCanPickup = ((EntityItem) original).delayBeforeCanPickup;
        else this.delayBeforeCanPickup = 20;
        this.motionX = original.motionX;
        this.motionY = original.motionY;
        this.motionZ = original.motionZ;
        this.setEntityItemStack(stack);
        this.lifespan = 72000;
    }

    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        if (getEntityItem().getItem() instanceof DragonHeart && par1DamageSource.isExplosion()
                && par2 > 10f
                && !this.isDead) {
            worldObj.spawnEntityInWorld(new EntityDragonHeart(worldObj, posX, posY, posZ));
            this.setDead();
        }

        return par1DamageSource.getDamageType().equals("outOfWorld");
    }

    @Override
    public boolean isInRangeToRenderDist(double p_70112_1_) {
        double d1 = this.boundingBox.getAverageEdgeLength();
        d1 *= 64.0D * 4;
        return p_70112_1_ < d1 * d1;
    }

    @Override
    public boolean isInRangeToRender3d(double p_145770_1_, double p_145770_3_, double p_145770_5_) {
        return super.isInRangeToRender3d(p_145770_1_, p_145770_3_, p_145770_5_);
    }

    @Override
    public void onUpdate() {
        if (age + 10 >= lifespan) age = 0;
        boolean flag2 = false;
        if (this.worldObj.getBlock(
                MathHelper.floor_double(this.posX),
                MathHelper.floor_double(this.posY - 1),
                MathHelper.floor_double(this.posZ)) == Blocks.end_portal)
            flag2 = true;
        ItemStack stack = this.getDataWatcher().getWatchableObjectItemStack(10);
        if (stack != null && stack.getItem() != null) {
            if (stack.getItem().onEntityItemUpdate(this)) {
                return;
            }
        }

        if (this.getEntityItem() == null) {
            this.setDead();
        } else {
            super.onEntityUpdate();

            if (this.delayBeforeCanPickup > 0) {
                --this.delayBeforeCanPickup;
            }

            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            this.motionY -= 0.03999999910593033D;
            if (flag2) {
                motionX = 0;
                motionY = 0;
                motionZ = 0;
            }
            this.noClip = this
                    .func_145771_j(this.posX, (this.boundingBox.minY + this.boundingBox.maxY) / 2.0D, this.posZ);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            boolean flag = (int) this.prevPosX != (int) this.posX || (int) this.prevPosY != (int) this.posY
                    || (int) this.prevPosZ != (int) this.posZ;

            if (flag || this.ticksExisted % 25 == 0) {
                if (this.worldObj.getBlock(
                        MathHelper.floor_double(this.posX),
                        MathHelper.floor_double(this.posY),
                        MathHelper.floor_double(this.posZ)).getMaterial() == Material.lava) {
                    this.motionY = 0.20000000298023224D;
                    this.motionX = (double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
                    this.motionZ = (double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
                    this.playSound("random.fizz", 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
                }
            }

            float f = 0.98F;

            if (this.onGround) {
                f = this.worldObj.getBlock(
                        MathHelper.floor_double(this.posX),
                        MathHelper.floor_double(this.boundingBox.minY) - 1,
                        MathHelper.floor_double(this.posZ)).slipperiness * 0.98F;
            }

            this.motionX *= (double) f;
            this.motionY *= 0.9800000190734863D;
            this.motionZ *= (double) f;
            if (flag2) {
                motionX = 0;
                motionY = 0;
                motionZ = 0;
            }

            if (this.onGround) {
                this.motionY *= -0.5D;
            }

            ++this.age;

            ItemStack item = getDataWatcher().getWatchableObjectItemStack(10);

            if (!this.worldObj.isRemote && this.age >= lifespan) {
                if (item != null) {
                    ItemExpireEvent event = new ItemExpireEvent(
                            this,
                            (item.getItem() == null ? 6000 : item.getItem().getEntityLifespan(item, worldObj)));
                    if (MinecraftForge.EVENT_BUS.post(event)) {
                        lifespan += event.extraLife;
                    } else {
                        this.setDead();
                    }
                } else {
                    this.setDead();
                }
            }

            if (item != null && item.stackSize <= 0) {
                this.setDead();
            }
        }
    }
}
