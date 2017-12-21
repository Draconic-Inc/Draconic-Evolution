package com.brandon3055.draconicevolution.entity;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

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
        this.setItem(par8ItemStack);
        this.lifespan = 72000;
    }

    public EntityPersistentItem(World par1World) {
        super(par1World);
        this.isImmuneToFire = true;
        this.lifespan = 72000;
    }

    public EntityPersistentItem(World world, Entity original, ItemStack stack) {
        this(world, original.posX, original.posY, original.posZ);
        if (original instanceof EntityItem) {
            this.pickupDelay = ((EntityItem) original).pickupDelay;
        }
        else {
            setDefaultPickupDelay();
        }
        this.motionX = original.motionX;
        this.motionY = original.motionY;
        this.motionZ = original.motionZ;
        this.setItem(stack);
        this.lifespan = 72000;
    }

    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        if (par1DamageSource.getDamageType().equals("outOfWorld")) {
            setDead();
            return true;
        }

        return false;
    }

    @Override
    public boolean isInRangeToRenderDist(double p_70112_1_) {
        double d1 = this.getEntityBoundingBox().getAverageEdgeLength();
        d1 *= 64.0D * 4;
        return p_70112_1_ < d1 * d1;
    }

    @Override
    public void onUpdate() {
        if (age + 10 >= lifespan) {
            age = 0;
        }

        ItemStack stack = this.getDataManager().get(ITEM);
        if (!stack.isEmpty() && stack.getItem().onEntityItemUpdate(this)) {
            return;
        }
        if (stack.isEmpty()) {
            this.setDead();
        }
        else {
            super.onEntityUpdate();

            if (this.pickupDelay > 0 && this.pickupDelay != 32767) {
                --this.pickupDelay;
            }

            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            this.motionY -= 0.03999999910593033D;
            this.noClip = this.pushOutOfBlocks(this.posX, (this.getEntityBoundingBox().minY + this.getEntityBoundingBox().maxY) / 2.0D, this.posZ);

            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            boolean flag = (int) this.prevPosX != (int) this.posX || (int) this.prevPosY != (int) this.posY || (int) this.prevPosZ != (int) this.posZ;

            if (flag || this.ticksExisted % 25 == 0) {
                if (this.world.getBlockState(new BlockPos(this)).getMaterial() == Material.LAVA) {
                    this.motionY = 0.2D;
                    this.motionX = (double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
                    this.motionZ = (double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
                    this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
                }

                if (!this.world.isRemote) {
                    this.searchForOtherItemsNearby();
                }
            }

            float f = 0.98F;

            if (this.onGround) {
                f = this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ))).getBlock().slipperiness * 0.98F;
            }

            this.motionX *= (double) f;
            this.motionY *= 0.9800000190734863D;
            this.motionZ *= (double) f;

            if (this.onGround) {
                this.motionY *= -0.5D;
            }

            if (this.age != -32768) {
                ++this.age;
            }

            this.handleWaterMovement();

            ItemStack item = this.getDataManager().get(ITEM);

            if (!this.world.isRemote && this.age >= lifespan) {
                int hook = net.minecraftforge.event.ForgeEventFactory.onItemExpire(this, item);
                if (hook < 0) this.setDead();
                else this.lifespan += hook;
            }
            if (!item.isEmpty() && item.getCount() <= 0) {
                this.setDead();
            }
        }
    }
}
