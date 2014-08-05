package com.brandon3055.draconicevolution.common.items.armor;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.core.utills.LogHelper;
import com.brandon3055.draconicevolution.common.items.ModItems;
import com.brandon3055.draconicevolution.common.lib.References;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import org.lwjgl.input.Keyboard;

import java.util.List;

/**
 * Created by Brandon on 3/07/2014.
 */
public class WyvernArmor extends ItemArmor implements ISpecialArmor{
	private IIcon helmIcon;
	private IIcon chestIcon;
	private IIcon leggsIcon;
	private IIcon bootsIcon;

	public WyvernArmor(ArmorMaterial material, int armorType, String name) {
		super(material, 0, armorType);
		this.setUnlocalizedName(name);
		this.setCreativeTab(DraconicEvolution.getCreativeTab(1));
		GameRegistry.registerItem(this, name);
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

	//#####################################################################################

	@Override
	public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
		switch(slot){
			case 0:
				return ArmorPropertiesHandler.wyvernBoots(player, armor, source);
			case 1:
				return ArmorPropertiesHandler.wyvernLeggs(player, armor, source);
			case 2:
				return ArmorPropertiesHandler.wyvernChest(player, armor, source);
			case 3:
				return ArmorPropertiesHandler.wyvernHelm(player, armor, source);
			default:
				LogHelper.error("[Draconic Armor] Invalid slot");
				return new ArmorProperties(0, 0, 0);

		}
	}

	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
		switch(slot){
			case 0:
				return 3;
			case 1:
				return 6;
			case 2:
				return 8;
			case 3:
				return 3;
			default:
				return 0;
		}
	}

	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {

	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		if(stack != null && stack.getItem() == ModItems.wyvernLeggs) {
			if (player.isSprinting())
				player.addPotionEffect(new PotionEffect(1, 10, 1, true));
			else
				player.addPotionEffect(new PotionEffect(1, 10, 0, true));
		}
		if(stack != null && stack.getItem() == ModItems.wyvernBoots) {
			if (player.isSprinting())
				player.addPotionEffect(new PotionEffect(8, 10, 1, true));
			else
				player.addPotionEffect(new PotionEffect(8, 10, 0, true));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
		list.add("" + EnumChatFormatting.DARK_PURPLE + "" + EnumChatFormatting.ITALIC + StatCollector.translateToLocal("info.wyvernArmorLegend1.txt"));
		list.add("" + EnumChatFormatting.DARK_PURPLE + "" + EnumChatFormatting.ITALIC + StatCollector.translateToLocal("info.wyvernArmorLegend2.txt"));
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
}
