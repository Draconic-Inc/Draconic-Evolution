package com.brandon3055.draconicevolution.common.entity;

import com.brandon3055.draconicevolution.common.items.DragonHeart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * Created by Brandon on 15/08/2014.
 */
public class EntityPersistentItem extends EntityItem {
	public EntityPersistentItem(World par1World, double par2, double par4, double par6)
	{
		super(par1World, par2, par4, par6);
		this.isImmuneToFire = true;
		this.lifespan = 72000;
	}

	public EntityPersistentItem(World par1World, double par2, double par4, double par6, ItemStack par8ItemStack)
	{
		this(par1World, par2, par4, par6);
		this.setEntityItemStack(par8ItemStack);
		this.lifespan = 72000;
	}

	public EntityPersistentItem(World par1World)
	{
		super(par1World);
		this.isImmuneToFire = true;
		this.lifespan = 72000;
	}

	public EntityPersistentItem(World world, Entity original, ItemStack stack)
	{
		this(world, original.posX, original.posY, original.posZ);
		if (original instanceof EntityItem) this.delayBeforeCanPickup = ((EntityItem)original).delayBeforeCanPickup;
		else this.delayBeforeCanPickup = 20;
		this.motionX = original.motionX;
		this.motionY = original.motionY;
		this.motionZ = original.motionZ;
		this.setEntityItemStack(stack);
		this.lifespan = 72000;
	}

	@Override
	public boolean attackEntityFrom (DamageSource par1DamageSource, float par2)
	{
		if (getEntityItem().getItem() instanceof DragonHeart && par1DamageSource.isExplosion() && par2 > 35f){
			worldObj.spawnEntityInWorld(new EntityDragonHeart(worldObj, posX, posY, posZ));
			this.setDead();
		}

		return par1DamageSource.getDamageType().equals("outOfWorld");
	}

	@Override
	public void onUpdate() {
		if (age+10 >= lifespan) age = 0;
		super.onUpdate();
	}
}
