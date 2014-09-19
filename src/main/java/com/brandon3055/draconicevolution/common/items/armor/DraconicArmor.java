package com.brandon3055.draconicevolution.common.items.armor;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.core.utills.LogHelper;
import com.brandon3055.draconicevolution.common.entity.EntityPersistentItem;
import com.brandon3055.draconicevolution.common.items.ModItems;
import com.brandon3055.draconicevolution.common.lib.References;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import org.lwjgl.input.Keyboard;

import java.util.Collection;
import java.util.List;

/**
 * Created by Brandon on 3/07/2014.
 */
public class DraconicArmor extends ItemArmor implements ISpecialArmor {
	private IIcon helmIcon;
	private IIcon chestIcon;
	private IIcon leggsIcon;
	private IIcon bootsIcon;

	public DraconicArmor(ArmorMaterial material, int armorType, String name) {
		super(material, 0, armorType);
		this.setUnlocalizedName(name);
		this.setCreativeTab(DraconicEvolution.tolkienTabToolsWeapons);
		GameRegistry.registerItem(this, name);
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

	//#####################################################################################

	@Override
	public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
		switch (slot) {
			case 0:
				return ArmorPropertiesHandler.draconicBoots(player, armor, source);
			case 1:
				return ArmorPropertiesHandler.draconicLeggs(player, armor, source);
			case 2:
				return ArmorPropertiesHandler.draconicChest(player, armor, source);
			case 3:
				return ArmorPropertiesHandler.draconicHelm(player, armor, source);
			default:
				LogHelper.error("[Draconic Armor] Invalid slot");
				return new ArmorProperties(0, 0, 0);

		}
	}

	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
		switch (slot) {
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
		if (player.isBurning() && ArmorPropertiesHandler.hasFullSetDraconic(player)) {
			player.addPotionEffect(new PotionEffect(12, 10, 0, true));
		}
		if (stack != null && stack.getItem() == ModItems.draconicHelm) {
			if (world.isRemote) return;
			clearNegativeEffects(player);
			if (player.worldObj.getBlockLightValue((int) player.posX, (int) player.posY, (int) player.posZ) < 5)
				player.addPotionEffect(new PotionEffect(16, 210, 0, true));
			else if (player.isPotionActive(16)) player.removePotionEffect(16);
		}
		if (stack != null && stack.getItem() == ModItems.draconicLeggs) {
			if (player.isSprinting()) player.addPotionEffect(new PotionEffect(1, 10, 3, true));
			else player.addPotionEffect(new PotionEffect(1, 10, 1, true));
		}
		if (stack != null && stack.getItem() == ModItems.draconicBoots) {
			if (player.isSprinting()) player.addPotionEffect(new PotionEffect(8, 10, 3, true));
			else player.addPotionEffect(new PotionEffect(8, 10, 1, true));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
		if ((!Keyboard.isKeyDown(42)) && (!Keyboard.isKeyDown(54))) {
			list.add(EnumChatFormatting.DARK_GREEN + "Hold shift for information");
		} else {
			if (stack.getItem() == ModItems.draconicHelm) {
				list.add(StatCollector.translateToLocal("info.draconicArmorInfoHelm.txt"));
			} else if (stack.getItem() == ModItems.draconicChest) {
				list.add(StatCollector.translateToLocal("info.draconicArmorInfoChest.txt"));
				list.add(StatCollector.translateToLocal("info.draconicArmorInfoChest1.txt"));
				list.add(StatCollector.translateToLocal("info.draconicArmorInfoChest2.txt"));
				list.add(StatCollector.translateToLocal("info.draconicArmorInfoChest3.txt"));
			} else if (stack.getItem() == ModItems.draconicLeggs) {
				list.add(StatCollector.translateToLocal("info.draconicArmorInfoLeggs.txt"));
			} else if (stack.getItem() == ModItems.draconicBoots) {
				list.add(StatCollector.translateToLocal("info.draconicArmorInfoBoots.txt"));
				list.add(StatCollector.translateToLocal("info.draconicArmorInfoBoots1.txt"));
			}
			list.add("");
			list.add("" + EnumChatFormatting.DARK_PURPLE + "" + EnumChatFormatting.ITALIC + StatCollector.translateToLocal("info.draconicArmorLegend1.txt"));
			list.add("" + EnumChatFormatting.DARK_PURPLE + "" + EnumChatFormatting.ITALIC + StatCollector.translateToLocal("info.draconicArmorLegend2.txt"));
		}
	}

	public static void registerRecipe() {
		CraftingManager.getInstance().addRecipe(new ItemStack(ModItems.draconicHelm), "ISI", "DAD", "CIC", 'I', ModItems.draconiumIngot, 'S', ModItems.sunFocus, 'D', ModItems.draconicCompound, 'C', ModItems.draconicCore, 'A', ModItems.wyvernHelm);

		CraftingManager.getInstance().addRecipe(new ItemStack(ModItems.draconicChest), "ISI", "DAD", "CDC", 'I', ModItems.draconiumIngot, 'S', ModItems.sunFocus, 'D', ModItems.draconicCompound, 'C', ModItems.draconicCore, 'A', ModItems.wyvernChest);

		CraftingManager.getInstance().addRecipe(new ItemStack(ModItems.draconicLeggs), "ISI", "DAD", "CIC", 'I', ModItems.draconiumIngot, 'S', ModItems.sunFocus, 'D', ModItems.draconicCompound, 'C', ModItems.draconicCore, 'A', ModItems.wyvernLeggs);

		CraftingManager.getInstance().addRecipe(new ItemStack(ModItems.draconicBoots), "ISI", "DAD", "CIC", 'I', ModItems.draconiumIngot, 'S', ModItems.sunFocus, 'D', ModItems.draconicCompound, 'C', ModItems.draconicCore, 'A', ModItems.wyvernBoots);
	}

	@Override
	public boolean hasCustomEntity(ItemStack stack) {
		return true;
	}

	@Override
	public Entity createEntity(World world, Entity location, ItemStack itemstack) {
		return new EntityPersistentItem(world, location, itemstack);
	}

	public void clearNegativeEffects(Entity par3Entity) {
		if (par3Entity.ticksExisted % 20 == 0) {
			if (par3Entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) par3Entity;

				Collection<PotionEffect> potions = player.getActivePotionEffects();

				if (player.isBurning()) {
					player.extinguish();
				} else for (PotionEffect potion : potions) {
					int id = potion.getPotionID();
					if (ReflectionHelper.getPrivateValue(Potion.class, Potion.potionTypes[id], new String[]{"isBadEffect", "field_76418_K", "J"})) {
						if ((player.getHeldItem() == null || (player.getHeldItem().getItem() != ModItems.wyvernBow && player.getHeldItem().getItem() != ModItems.draconicBow)) || id != 2) player.removePotionEffect(id);
						break;
					}
				}
			}
		}
	}

}
