package com.brandon3055.draconicevolution.common.items.armor;

import cofh.api.energy.IEnergyContainerItem;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.entity.EntityPersistentItem;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.utills.IConfigurableItem;
import com.brandon3055.draconicevolution.common.utills.InfoHelper;
import com.brandon3055.draconicevolution.common.utills.ItemConfigField;
import com.brandon3055.draconicevolution.common.utills.ItemNBTHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Brandon on 3/07/2014.
 */
public class DraconicArmor extends ItemArmor implements ISpecialArmor, IEnergyContainerItem, IConfigurableItem {//TODO Wings
	private IIcon helmIcon;
	private IIcon chestIcon;
	private IIcon leggsIcon;
	private IIcon bootsIcon;

	private double totalAbsorption = 2; // 1=100%
	private int maxTransfer = References.DRACONICTRANSFER;
	private int maxEnergy = References.DRACONICCAPACITY;
	private int energyPerDamage = 80;

	public DraconicArmor(ArmorMaterial material, int armorType, String name) {
		super(material, 0, armorType);
		this.setUnlocalizedName(name);
		this.setCreativeTab(DraconicEvolution.tabToolsWeapons);
		GameRegistry.registerItem(this, name);
	}

	@Override
	public boolean isItemTool(ItemStack p_77616_1_) {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void getSubItems(Item item, CreativeTabs p_150895_2_, List list) {
		list.add(ItemNBTHelper.setInteger(new ItemStack(item), "Energy", 0));
		list.add(ItemNBTHelper.setInteger(new ItemStack(item), "Energy", maxEnergy));
	}

	@Override
	public String getUnlocalizedName() {

		return String.format("item.%s%s", References.MODID.toLowerCase() + ":", super.getUnlocalizedName().substring(super.getUnlocalizedName().indexOf(".") + 1));
	}

	@Override
	public String getUnlocalizedName(final ItemStack itemStack) {
		return getUnlocalizedName();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		helmIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "draconic_helmet");
		chestIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "draconic_chestplate");
		leggsIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "draconic_leggings");
		bootsIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "draconic_boots");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
		if (stack.getItem() == ModItems.draconicHelm) return helmIcon;
		else if (stack.getItem() == ModItems.draconicChest) return chestIcon;
		else if (stack.getItem() == ModItems.draconicLeggs) return leggsIcon;
		else return bootsIcon;
	}

	@Override
	public IIcon getIconIndex(ItemStack stack) {
		if (stack.getItem() == ModItems.draconicHelm) return helmIcon;
		else if (stack.getItem() == ModItems.draconicChest) return chestIcon;
		else if (stack.getItem() == ModItems.draconicLeggs) return leggsIcon;
		else return bootsIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		if (stack.getItem() == ModItems.draconicHelm || stack.getItem() == ModItems.draconicChest || stack.getItem() == ModItems.draconicBoots) {
			return References.RESOURCESPREFIX + "textures/models/armor/draconic_layer_1.png";
		} else {
			return References.RESOURCESPREFIX + "textures/models/armor/draconic_layer_2.png";
		}
	}

	@Override
	public EnumRarity getRarity(ItemStack p_77613_1_) {
		return EnumRarity.epic;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return 1D - (double)ItemNBTHelper.getInteger(stack, "Energy", 0) / (double)maxEnergy;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return getEnergyStored(stack) < maxEnergy;
	}

	protected double getAbsorptionPercent() {
		switch (armorType) {
			case 0:
				return 0.15D;
			case 1:
				return 0.40D;
			case 2:
				return 0.30D;
			case 3:
				return 0.15D;
		}
		return 0;
	}

	/* ISpecialArmor */

	@Override
	public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
		int maxAbsorption = 25 * getEnergyStored(armor) / energyPerDamage;
		if (source.damageType.equals(DamageSource.fall.damageType) && armor.getItem() == ModItems.draconicBoots) return new ArmorProperties(0, 1D, maxAbsorption);
		if (source.isUnblockable()) return new ArmorProperties(0, (getAbsorptionPercent()*totalAbsorption)/2, maxAbsorption);
		return new ArmorProperties(0, getAbsorptionPercent()*totalAbsorption, maxAbsorption);
	}

	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
		return this.getEnergyStored(armor) > 10000 ? (int)(getAbsorptionPercent() * 20D) : (int) ((float)this.getEnergyStored(armor) / 10000F * (float)(getAbsorptionPercent() * 20D));
	}

	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
		extractEnergy(stack, damage * energyPerDamage, false);
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		if (stack == null) return;
		if (stack.getItem() == ModItems.draconicHelm) {
			if (world.isRemote) return;
			if (this.getEnergyStored(stack) >= 5000 && clearNegativeEffects(player)) this.extractEnergy(stack, 5000, false);
			if (player.worldObj.getBlockLightValue((int)Math.floor(player.posX), (int) player.posY, (int)Math.floor(player.posZ)) < 5 && ItemNBTHelper.getBoolean(stack, "ArmorNVActive", false))
				player.addPotionEffect(new PotionEffect(16, 419, 0, true));
			else if (player.isPotionActive(16)) player.removePotionEffect(16);

		}
		if (stack.getItem() == ModItems.draconicLeggs && player.isSprinting() && !player.capabilities.isCreativeMode) {
			this.extractEnergy(stack, player.capabilities.isFlying ? 160 : 80, false);
		}
	}

	@SuppressWarnings("unchecked")
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
		InfoHelper.addEnergyAndLore(stack, list);
	}

	@SuppressWarnings("unchecked")
	public boolean clearNegativeEffects(Entity par3Entity) {
		boolean flag = false;
		if (par3Entity.ticksExisted % 20 == 0) {
			if (par3Entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) par3Entity;

				Collection<PotionEffect> potions = player.getActivePotionEffects();

				if (player.isBurning()) {
					player.extinguish();
					flag = true;
				} else for (PotionEffect potion : potions) {
					int id = potion.getPotionID();
					if (ReflectionHelper.getPrivateValue(Potion.class, Potion.potionTypes[id], new String[]{"isBadEffect", "field_76418_K", "J"})) {
						if ((player.getHeldItem() == null || (player.getHeldItem().getItem() != ModItems.wyvernBow && player.getHeldItem().getItem() != ModItems.draconicBow)) || id != 2) {
							player.removePotionEffect(id);
							flag = true;
						}
						break;
					}
				}
			}
		}
		return flag;
	}

	/* IEnergyContainerItem */
	@Override
	public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
		int stored = ItemNBTHelper.getInteger(container, "Energy", 0);
		int receive = Math.min(maxReceive, Math.min(maxEnergy - stored, maxTransfer));

		if (!simulate) {
			stored += receive;
			ItemNBTHelper.setInteger(container, "Energy", stored);
		}
		return receive;
	}

	@Override
	public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {

		int stored = ItemNBTHelper.getInteger(container, "Energy", 0);
		int extract = Math.min(maxExtract, stored);

		if (!simulate) {
			stored -= extract;
			ItemNBTHelper.setInteger(container, "Energy", stored);
		}
		return extract;
	}

	@Override
	public int getEnergyStored(ItemStack container) {
		return ItemNBTHelper.getInteger(container, "Energy", 0);
	}

	@Override
	public int getMaxEnergyStored(ItemStack container) {

		return maxEnergy;
	}

	/* Misc */
	@Override
	public Entity createEntity(World world, Entity location, ItemStack itemstack) {
		return new EntityPersistentItem(world, location, itemstack);
	}

	@Override
	public boolean hasCustomEntity(ItemStack stack) {
		return true;
	}

	@Override
	public List<ItemConfigField> getFields(ItemStack stack, int slot) {
		List<ItemConfigField> list = new ArrayList<ItemConfigField>();
		if (armorType == 0)
			list.add(new ItemConfigField(References.BOOLEAN_ID, slot, "ArmorNVActive").readFromItem(stack, false));
		else if (armorType == 1)
		{
			list.add(new ItemConfigField(References.BOOLEAN_ID, slot, "ArmorFlightLock").readFromItem(stack, false));
		}
		else if (armorType == 2)
		{
			list.add(new ItemConfigField(References.FLOAT_ID, slot, "ArmorSpeedMult").setMinMaxAndIncromente(0f, 1f, 0.01f).readFromItem(stack, 1F));
			list.add(new ItemConfigField(References.BOOLEAN_ID, slot, "ArmorSprintOnly").readFromItem(stack, false));
		}
		else if (armorType == 3)
		{
			list.add(new ItemConfigField(References.FLOAT_ID, slot, "ArmorJumpMult").setMinMaxAndIncromente(0f, 1f, 0.01f).readFromItem(stack, 1f));
			list.add(new ItemConfigField(References.BOOLEAN_ID, slot, "ArmorSprintOnly").readFromItem(stack, false));
			list.add(new ItemConfigField(References.BOOLEAN_ID, slot, "ArmorHillStep").readFromItem(stack, true));
		}
		return list;
	}
}
