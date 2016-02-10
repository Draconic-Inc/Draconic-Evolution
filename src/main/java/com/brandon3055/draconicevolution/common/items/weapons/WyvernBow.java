package com.brandon3055.draconicevolution.common.items.weapons;

import com.brandon3055.brandonscore.common.utills.InfoHelper;
import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.entity.EntityPersistentItem;
import com.brandon3055.draconicevolution.common.items.tools.baseclasses.ToolBase;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.utills.IInventoryTool;
import com.brandon3055.draconicevolution.common.utills.IUpgradableItem;
import com.brandon3055.draconicevolution.common.utills.ItemConfigField;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowNockEvent;

import java.util.ArrayList;
import java.util.List;

public class WyvernBow extends ItemBow implements IInventoryTool, IUpgradableItem {

	public static final String[] bowPullIconNameArray = new String[] { "pulling_0", "pulling_1", "pulling_2" };
	@SideOnly(Side.CLIENT)
	private IIcon[] iconArray;

	public WyvernBow() {
		this.maxStackSize = 1;
		this.setMaxDamage(-1);
		this.setCreativeTab(DraconicEvolution.tabToolsWeapons);
		this.setUnlocalizedName(Strings.wyvernBowName);
		if (ModItems.isEnabled(this)) GameRegistry.registerItem(this, Strings.wyvernBowName);
	}

	@Override
	public boolean isItemTool(ItemStack p_77616_1_) {
		return true;
	}

	@Override
	public String getUnlocalizedName(){

		return String.format("item.%s%s", References.MODID.toLowerCase() + ":", super.getUnlocalizedName().substring(super.getUnlocalizedName().indexOf(".") + 1));
	}

	@Override
	public String getUnlocalizedName(final ItemStack itemStack){
		return getUnlocalizedName();
	}

	/* ======================================== CUSTOMEBOW START ===================================== */
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!player.isSneaking()) {
			ArrowNockEvent event = new ArrowNockEvent(player, stack);
			MinecraftForge.EVENT_BUS.post(event);
			if (event.isCanceled()) {
				return event.result;
			}

			if (player.capabilities.isCreativeMode || player.inventory.hasItem(Items.arrow) || player.inventory.hasItem(ModItems.enderArrow) || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0) {
				player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
			}
		} else
			changeMode(stack, player);
		return stack;
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityPlayer player, int count)
	{
		String currentMode = ItemNBTHelper.getString(stack, "mode", "rapidfire");
		if (player.inventory.hasItem(ModItems.enderArrow)) currentMode = "ender";
		if (currentMode.equals("rapidfire"))
			BowHandler.rapidFire(player, count, 18);
//		else if (currentMode.equals("sharpshooter"))
//			player.addPotionEffect(new PotionEffect(2, 2, 10, true));
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int count)
	{
		String currentMode = ItemNBTHelper.getString(stack, "mode", "rapidfire");
		if (player.inventory.hasItem(ModItems.enderArrow)) currentMode = "ender";
		if (currentMode.equals("rapidfire"))
			BowHandler.standerdShot(stack, world, player, count, itemRand, 19F, 1F, false, 0D, 1F, false, 0);
		else if (currentMode.equals("sharpshooter"))
			BowHandler.standerdShot(stack, world, player, count, itemRand, 30F, 5F, true, 20D, 0.7F, false, 30);
		else if (currentMode.equals("ender"))
			BowHandler.enderShot(stack, world, player, count, itemRand, 30, 1F, 1F, 0);
	}

	public void changeMode(ItemStack stack, EntityPlayer player)
	{
		String currentMode = ItemNBTHelper.getString(stack, "mode", "rapidfire");

		if (currentMode.equals("rapidfire"))
			ItemNBTHelper.setString(stack, "mode", "sharpshooter"); 
		else
			ItemNBTHelper.setString(stack, "mode", "rapidfire");

		if (player.worldObj.isRemote)
			player.addChatMessage(new ChatComponentTranslation("msg.bowmode" + ItemNBTHelper.getString(stack, "mode", "rapidfire") + ".txt"));
	}

	/* ======================================== TEXTURE START =====================================*/

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister)
	{
		this.itemIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "wyvern_bow" + "_standby");
		this.iconArray = new IIcon[bowPullIconNameArray.length];

		for (int i = 0; i < this.iconArray.length; ++i) {
			this.iconArray[i] = iconRegister.registerIcon(References.RESOURCESPREFIX + "wyvern_bow" + "_" + bowPullIconNameArray[i]);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining)
	{
		String currentMode = ItemNBTHelper.getString(stack, "mode", "rapidfire");
		if (player.inventory.hasItem(ModItems.enderArrow)) currentMode = "sharpshooter";
		int j = stack.getMaxItemUseDuration() - useRemaining;
		if (usingItem == null) {
			return this.itemIcon;
		}
		if (currentMode.equals("rapidfire")) {
			if (j >= 13)
				return getItemIconForUseDuration(2);
			else if (j > 7)
				return getItemIconForUseDuration(1);
			else if (j > 0)
				return getItemIconForUseDuration(0);
		} else if (currentMode.equals("sharpshooter")) {
			if (j >= 30)
				return getItemIconForUseDuration(2);
			else if (j > 15)
				return getItemIconForUseDuration(1);
			else if (j > 0)
				return getItemIconForUseDuration(0);
		}
		return this.itemIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getItemIconForUseDuration(int par1)
	{
		return this.iconArray[par1];
	}

	/* ======================================== TEXTURE END =====================================*/
	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addInformation(final ItemStack stack, final EntityPlayer player, final List list, final boolean extraInformation)
	{
		if (InfoHelper.holdShiftForDetails(list)) {
			list.add(StatCollector.translateToLocal("msg.bowmode" + ItemNBTHelper.getString(stack, "mode", "rapidfire") + ".txt"));
			list.add(InfoHelper.ITC() + StatCollector.translateToLocal("info.de.bowEnchants.txt"));
			InfoHelper.addLore(stack, list);
		}
		ToolBase.holdCTRLForUpgrades(list, stack);
	}

	@Override
	public boolean hasCustomEntity(ItemStack stack) {
		return true;
	}

	@Override
	public Entity createEntity(World world, Entity location, ItemStack itemstack) {
		return new EntityPersistentItem(world, location, itemstack);
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
		return enchant.type == EnumEnchantmentType.bow || enchant.effectId == DraconicEvolution.reaperEnchant.effectId;
	}

	@Override
	public List<ItemConfigField> getFields(ItemStack stack, int slot) {
		return new ArrayList<ItemConfigField>();
	}

	@Override
	public List<EnumUpgrade> getUpgrades(ItemStack itemstack) {
		return new ArrayList<EnumUpgrade>(){{
		//	add(EnumUpgrade.RF_CAPACITY);
			add(EnumUpgrade.DRAW_SPEED);
			add(EnumUpgrade.ARROW_SPEED);
			add(EnumUpgrade.ARROW_DAMAGE);
		}};
	}

	@Override
	public int getUpgradeCap(ItemStack itemstack) {
		return References.MAX_WYVERN_UPGRADES;
	}

	@Override
	public int getMaxTier(ItemStack itemstack) {
		return 1;
	}

	@Override
	public int getMaxUpgradePoints(int upgradeIndex) {
		return 50;
	}

	@Override
	public int getBaseUpgradePoints(int upgradeIndex) {
		if (upgradeIndex == EnumUpgrade.RF_CAPACITY.index) return 2;
		return 0;
	}

	@Override
	public List<String> getUpgradeStats(ItemStack itemstack) {
		List<String> strings = new ArrayList<String>();

	//	strings.add(StatCollector.translateToLocal("gui.de.RFCapacity.txt")+": "+ Utills.formatNumber(getMaxEnergyStored(itemstack)));

		return strings;
	}
}

