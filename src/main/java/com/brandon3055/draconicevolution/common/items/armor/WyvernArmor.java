package com.brandon3055.draconicevolution.common.items.armor;

import cofh.api.energy.IEnergyContainerItem;
import com.brandon3055.brandonscore.common.utills.InfoHelper;
import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.model.ModelDraconicArmorOld;
import com.brandon3055.draconicevolution.client.model.ModelWyvernArmor;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.entity.EntityPersistentItem;
import com.brandon3055.draconicevolution.common.handler.ConfigHandler;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.utills.IConfigurableItem;
import com.brandon3055.draconicevolution.common.utills.IInventoryTool;
import com.brandon3055.draconicevolution.common.utills.ItemConfigField;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brandon on 3/07/2014.
 */
public class WyvernArmor extends ItemArmor implements ISpecialArmor, IEnergyContainerItem, IConfigurableItem, IInventoryTool  {
	private IIcon helmIcon;
	private IIcon chestIcon;
	private IIcon leggsIcon;
	private IIcon bootsIcon;

	private double totalAbsorption = 1; // 1=100%
	private int maxTransfer = References.WYVERNTRANSFER;
	private int maxEnergy = References.WYVERNCAPACITY;
	private int energyPerDamage = 80;

	public WyvernArmor(ArmorMaterial material, int armorType, String name) {
		super(material, 0, armorType);
		this.setUnlocalizedName(name);
		this.setCreativeTab(DraconicEvolution.tabToolsWeapons);
		if (ModItems.isEnabled(this)) GameRegistry.registerItem(this, name);
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
	public String getUnlocalizedName(){

		return String.format("item.%s%s", References.MODID.toLowerCase() + ":", super.getUnlocalizedName().substring(super.getUnlocalizedName().indexOf(".") + 1));
	}

	@Override
	public String getUnlocalizedName(final ItemStack itemStack){
		return getUnlocalizedName();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		helmIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "wyvern_helmet");
		chestIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "wyvern_chestplate");
		leggsIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "wyvern_leggings");
		bootsIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "wyvern_boots");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
		if (stack.getItem() == ModItems.wyvernHelm) return helmIcon;
		else if (stack.getItem() == ModItems.wyvernChest) return chestIcon;
		else if (stack.getItem() == ModItems.wyvernLeggs) return leggsIcon;
		else return bootsIcon;
	}

	@Override
	public IIcon getIconIndex(ItemStack stack) {
		if (stack.getItem() == ModItems.wyvernHelm) return helmIcon;
		else if (stack.getItem() == ModItems.wyvernChest) return chestIcon;
		else if (stack.getItem() == ModItems.wyvernLeggs) return leggsIcon;
		else return bootsIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		if (!ConfigHandler.useOldArmorModel)  return References.RESOURCESPREFIX + "textures/models/armor/armorWyvern.png";
		if (stack.getItem() == ModItems.wyvernHelm || stack.getItem() == ModItems.wyvernChest || stack.getItem() == ModItems.wyvernBoots) {
			return References.RESOURCESPREFIX + "textures/models/armor/wyvern_layer_1.png";
		} else {
			return References.RESOURCESPREFIX + "textures/models/armor/wyvern_layer_2.png";
		}
	}

	@Override
	public EnumRarity getRarity(ItemStack p_77613_1_) {
		return EnumRarity.uncommon;
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
		if (source.damageType.equals(DamageSource.fall.damageType) && armor.getItem() == ModItems.wyvernBoots) return new ArmorProperties(0, 0.8D, maxAbsorption);
		if (source.isUnblockable()) return new ArmorProperties(0, (getAbsorptionPercent()*totalAbsorption)/4, maxAbsorption);
		return new ArmorProperties(0, getAbsorptionPercent()*totalAbsorption, maxAbsorption);
	}

	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
		return this.getEnergyStored(armor) > 10000 ? (int)(getAbsorptionPercent() * 20D) : (int) ((float)this.getEnergyStored(armor) / 10000F * (float)(getAbsorptionPercent() * 20D));

		//	return (int)(getAbsorptionPercent() * 20D);
	}

	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
		extractEnergy(stack, damage * energyPerDamage, false);
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
//		if(stack != null && stack.getItem() == ModItems.wyvernLeggs) {
//			if (player.isSprinting())
//				player.addPotionEffect(new PotionEffect(PotionHandler.potionSpeed.id, 10, 1, true));
//			else
//				player.addPotionEffect(new PotionEffect(PotionHandler.potionSpeed.id, 10, 0, true));
//		}
//		if(stack != null && stack.getItem() == ModItems.wyvernBoots) {
//			if (player.isSprinting())
//				player.addPotionEffect(new PotionEffect(PotionHandler.potionJumpBoost.id, 10, 1, true));
//			else
//				player.addPotionEffect(new PotionEffect(PotionHandler.potionJumpBoost.id, 10, 0, true));
//		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
		InfoHelper.addEnergyAndLore(stack, list);
	}

	@Override
	public boolean hasCustomEntity(ItemStack stack) {
		return true;
	}

	@Override
	public Entity createEntity(World world, Entity location, ItemStack itemstack) {
		return new EntityPersistentItem(world, location, itemstack);
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

	@Override
	public List<ItemConfigField> getFields(ItemStack stack, int slot) {
		List<ItemConfigField> list = new ArrayList<ItemConfigField>();
		if (armorType == 2)
		{
			list.add(new ItemConfigField(References.FLOAT_ID, slot, "ArmorSpeedMult").setMinMaxAndIncromente(0f, 1f, 0.01f).readFromItem(stack, 1F));
			list.add(new ItemConfigField(References.BOOLEAN_ID, slot, "ArmorSprintOnly").readFromItem(stack, false));
		}
		else if (armorType == 3)
		{
			list.add(new ItemConfigField(References.FLOAT_ID, slot, "ArmorJumpMult").setMinMaxAndIncromente(0f, 1f, 0.01f).readFromItem(stack, 1f));
			list.add(new ItemConfigField(References.BOOLEAN_ID, slot, "ArmorSprintOnly").readFromItem(stack, false));
		}
		return list;
	}

	@Override
	public String getInventoryName() {
		return StatCollector.translateToLocal("info.de.toolInventoryEnch.txt");
	}

	@Override
	public int getInventorySlots() {
		return 0;
	}

	@Override
	public boolean isEnchantValid(Enchantment enchant) {
		return enchant.type == EnumEnchantmentType.armor || (armorType == 0 && enchant.type == EnumEnchantmentType.armor_head) || (armorType == 1 && enchant.type == EnumEnchantmentType.armor_torso) || (armorType == 2 && enchant.type == EnumEnchantmentType.armor_legs) || (armorType == 3 && enchant.type == EnumEnchantmentType.armor_feet);
	}

	@SideOnly(Side.CLIENT)
	private ModelBiped model;

	@SideOnly(Side.CLIENT)
	@Override
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot) {
		if (ConfigHandler.useOldArmorModel) return super.getArmorModel(entityLiving, itemStack, armorSlot);

		if (!ConfigHandler.useOriginal3DArmorModel) {
			if (model == null) {
				if (armorType == 0) model = new ModelWyvernArmor(1.0F, true, false, false, false);
				else if (armorType == 1) model = new ModelWyvernArmor(1F, false, true, false, false);
				else if (armorType == 2) model = new ModelWyvernArmor(1F, false, false, true, false);
				else model = new ModelWyvernArmor(1F, false, false, false, true);
				this.model.bipedHead.showModel = (armorType == 0);
				this.model.bipedHeadwear.showModel = (armorType == 0);
				this.model.bipedBody.showModel = ((armorType == 1) || (armorType == 2));
				this.model.bipedLeftArm.showModel = (armorType == 1);
				this.model.bipedRightArm.showModel = (armorType == 1);
				this.model.bipedLeftLeg.showModel = (armorType == 2 || armorType == 3);
				this.model.bipedRightLeg.showModel = (armorType == 2 || armorType == 3);
			}
		}
		else {
			if (model == null) {
				if (armorType == 0) model = new ModelDraconicArmorOld(1.0F, true, false, false, false, false);
				else if (armorType == 1) model = new ModelDraconicArmorOld(1F, false, true, false, false, false);
				else if (armorType == 2) model = new ModelDraconicArmorOld(1F, false, false, true, false, false);
				else model = new ModelDraconicArmorOld(1F, false, false, false, true, false);

				this.model.bipedHead.showModel = (armorType == 0);
				this.model.bipedHeadwear.showModel = (armorType == 0);
				this.model.bipedBody.showModel = ((armorType == 1) || (armorType == 2));
				this.model.bipedLeftArm.showModel = (armorType == 1);
				this.model.bipedRightArm.showModel = (armorType == 1);
				this.model.bipedLeftLeg.showModel = (armorType == 2 || armorType == 3);
				this.model.bipedRightLeg.showModel = (armorType == 2 || armorType == 3);
			}
		}

		if (entityLiving == null) return model;

		this.model.isSneak = entityLiving.isSneaking();
		this.model.isRiding = entityLiving.isRiding();
		this.model.isChild = entityLiving.isChild();
		this.model.aimedBow = false;
		this.model.heldItemRight = (entityLiving.getHeldItem() != null ? 1 : 0);

		if ((entityLiving instanceof EntityPlayer))
		{
			if (((EntityPlayer) entityLiving).getItemInUseDuration() > 0)
			{
				EnumAction enumaction = ((EntityPlayer) entityLiving).getItemInUse().getItemUseAction();
				if (enumaction == EnumAction.block)
				{
					this.model.heldItemRight = 3;
				} else if (enumaction == EnumAction.bow)
				{
					this.model.aimedBow = true;
				}
			}
		}


		return model;
	}
}
