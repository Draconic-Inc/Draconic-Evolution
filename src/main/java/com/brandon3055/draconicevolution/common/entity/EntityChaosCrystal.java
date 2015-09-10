package com.brandon3055.draconicevolution.common.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;

import java.util.List;

/**
 * Created by brandon3055 on 30/8/2015.
 */
public class EntityChaosCrystal extends EntityLivingBase {
	public int innerRotation;
	public float deathAnimation = 1F;
	public int shieldTime = 0;
	public EntityChaosGuardian guardian;

	public EntityChaosCrystal(World p_i1698_1_) {
		super(p_i1698_1_);
		this.preventEntitySpawning = true;
		this.setSize(2.0F, 2.0F);
		//this.yOffset = -2;
		this.innerRotation = this.rand.nextInt(100000);
		this.setHealth(getMaxHealth());
		this.ignoreFrustumCheck = true;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(50F);
	}

	@Override
	public boolean canTriggerWalking() {
		return false;
	}

	@Override
	public void entityInit() {
		super.entityInit();
		dataWatcher.addObject(20, (short)shieldTime);
	}

	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if (!worldObj.isRemote)dataWatcher.updateObject(20, (short)shieldTime);
		else shieldTime = (int)dataWatcher.getWatchableObjectShort(20);
		//setHealth(0);
		if (getHealth() > 0){
			if (shieldTime > 0) shieldTime--;
			if (deathAnimation < 1F) deathAnimation += 0.1F;
			++this.innerRotation;

			int i = MathHelper.floor_double(this.posX);
			int j = MathHelper.floor_double(this.posY);
			int k = MathHelper.floor_double(this.posZ);

			if (this.worldObj.provider instanceof WorldProviderEnd && this.worldObj.getBlock(i, j, k) != Blocks.fire)
			{
				this.worldObj.setBlock(i, j, k, Blocks.fire);
			}
		}else if (deathAnimation > 0) deathAnimation -= 0.1F;

	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getShadowSize() {
		return 0.0F;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float dmg) {
		if (this.isEntityInvulnerable())
		{
			return false;
		} else if (source.getEntity() instanceof EntityPlayer)
		{
			if (shieldTime <= 0 && getHealth() > 0 && !this.worldObj.isRemote)
			{
				setHealth(getHealth() - Math.min(getHealth(), dmg));
				if (getHealth() <= 0) worldObj.createExplosion(null, this.posX, this.posY, this.posZ, 6.0F, false);
				else {
					shieldTime = 100 + rand.nextInt(100);
					worldObj.playSoundEffect(posX + 0.5D, posY + 0.5D, posZ + 0.5D, "draconicevolution:shieldUp", 10.0F, rand.nextFloat() * 0.1F + 1.055F);
				}
				if (getGuardian() != null) getGuardian().onCrystalTargeted((EntityPlayer)source.getEntity(), getHealth() <= 0);
				return true;
			}else if (shieldTime > 0) shieldTime = 100 + rand.nextInt(100);
		}
		return false;
	}

	private EntityChaosGuardian getGuardian(){
		if (guardian == null) {
			@SuppressWarnings("unchecked") List<EntityChaosGuardian> list = worldObj.getEntitiesWithinAABB(EntityChaosGuardian.class, AxisAlignedBB.getBoundingBox(posX, posY, posZ, posX, posY, posZ).expand(512, 512, 512));
			if (list.size() > 0) guardian = list.get(0);
			if (guardian != null && !guardian.crystals.contains(this)) {
				guardian.crystals.add(this);
				guardian.updateCrystals();
			}
		}
		return guardian;
	}

	public void revive(){
		setHealth(getMaxHealth());
		shieldTime = 50;
		worldObj.createExplosion(null, this.posX, this.posY, this.posZ, 6.0F, false);
		if (getGuardian() != null) getGuardian().updateCrystals();
	}

	public boolean isAlive() { return getHealth() > 0F; }

	@Override
	public ItemStack getHeldItem() {
		return null;
	}

	@Override
	public ItemStack getEquipmentInSlot(int p_71124_1_) {
		return null;
	}

	@Override
	public void setCurrentItemOrArmor(int p_70062_1_, ItemStack p_70062_2_) {

	}

	@Override
	public ItemStack[] getLastActiveItems() {
		return new ItemStack[0];
	}
}