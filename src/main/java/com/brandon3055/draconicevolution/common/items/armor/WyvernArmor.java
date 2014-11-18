package com.brandon3055.draconicevolution.common.items.armor;

import cofh.api.energy.IEnergyContainerItem;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.utills.InfoHelper;
import com.brandon3055.draconicevolution.common.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.common.entity.EntityPersistentItem;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.lib.References;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;

import java.util.List;

/**
 * Created by Brandon on 3/07/2014.
 */
public class WyvernArmor extends ItemArmor implements ISpecialArmor, IEnergyContainerItem{
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
		this.setCreativeTab(DraconicEvolution.tolkienTabToolsWeapons);
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
		return (int)(getAbsorptionPercent() * 20D);
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

	public static void registerRecipe(){
		CraftingManager.getInstance().addRecipe(new ItemStack(ModItems.wyvernHelm),
				" I ",
				"IDI",
				" C ",
				'I', ModItems.infusedCompound, 'D', Items.diamond_helmet, 'C', ModItems.draconicCore);

		CraftingManager.getInstance().addRecipe(new ItemStack(ModItems.wyvernChest),
				"PIP",
				"IDI",
				"ICI",
				'I', ModItems.infusedCompound, 'D', Items.diamond_chestplate, 'C', ModItems.draconicCore, 'P', new ItemStack(Items.potionitem, 1, 8259));

		CraftingManager.getInstance().addRecipe(new ItemStack(ModItems.wyvernLeggs),
				"PIP",
				"IDI",
				" C ",
				'I', ModItems.infusedCompound, 'D', Items.diamond_leggings, 'C', ModItems.draconicCore, 'P', new ItemStack(Items.potionitem, 1, 8258));

		CraftingManager.getInstance().addRecipe(new ItemStack(ModItems.wyvernBoots),
				" I ",
				"IDI",
				"PCP",
				'I', ModItems.infusedCompound, 'D', Items.diamond_boots, 'C', ModItems.draconicCore, 'P', Blocks.piston);
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

}
