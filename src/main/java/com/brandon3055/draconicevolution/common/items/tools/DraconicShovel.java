package com.brandon3055.draconicevolution.common.items.tools;

import java.util.List;

import cofh.api.energy.IEnergyContainerItem;
import com.brandon3055.draconicevolution.common.core.utills.ItemInfoHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.core.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.common.items.ModItems;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;

public class DraconicShovel extends ItemSpade implements IEnergyContainerItem{
	public IIcon itemIcon0;
	public IIcon itemIcon1;
	public IIcon itemIcon2;
	protected int capacity = References.DRACONICCAPACITY;
	protected int maxReceive = References.DRACONICTRANSFER;
	protected int maxExtract = References.DRACONICTRANSFER;

	public DraconicShovel() {
		super(ModItems.DRACONIUM_T2);
		this.setUnlocalizedName(Strings.draconicShovelName);
		this.setCreativeTab(DraconicEvolution.getCreativeTab(1));
		GameRegistry.registerItem(this, Strings.draconicShovelName);
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
	public void registerIcons(final IIconRegister iconRegister)
	{
		this.itemIcon0 = iconRegister.registerIcon(References.RESOURCESPREFIX + "draconic_shovel");
		this.itemIcon1 = iconRegister.registerIcon(References.RESOURCESPREFIX + "draconic_shovel_active");
		this.itemIcon2 = iconRegister.registerIcon(References.RESOURCESPREFIX + "draconic_shovel_obliterate");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining)
	{
		if (ItemNBTHelper.getShort(stack, "size", (short)0) > 0 && ItemNBTHelper.getBoolean(stack, "obliterate", false))
			return itemIcon2;
		else if (ItemNBTHelper.getShort(stack, "size", (short)0) > 0)
			return itemIcon1;
		else
			return itemIcon0;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconIndex(ItemStack stack)
	{
		if (ItemNBTHelper.getShort(stack, "size", (short)0) > 0 && ItemNBTHelper.getBoolean(stack, "obliterate", false))
			return itemIcon2;
		else if (ItemNBTHelper.getShort(stack, "size", (short)0) > 0)
			return itemIcon1;
		else
			return itemIcon0;
	}

	@Override
	public boolean onBlockStartBreak(final ItemStack stack, final int x, final int y, final int z, final EntityPlayer player)
	{
		World world = player.worldObj;
		Block block = world.getBlock(x, y, z);
		Material mat = block.getMaterial();
		if (!ToolHandler.isRightMaterial(mat, ToolHandler.materialsShovel)) {
			return false;
		}
		int fortune = EnchantmentHelper.getFortuneModifier(player);
		boolean silk = EnchantmentHelper.getSilkTouchModifier(player);
		ToolHandler.disSquare(x, y, z, player, world, silk, fortune, ToolHandler.materialsShovel, stack);
		return false;
	}

	@Override
	public ItemStack onItemRightClick(final ItemStack stack, final World world, final EntityPlayer player)
	{
		return ToolHandler.changeMode(stack, player, true, 3);
	}

	public static int getMode(final ItemStack tool)
	{
		return tool.getItemDamage();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(final ItemStack stack, final EntityPlayer player, final List list, final boolean extraInformation)
	{
		int size = (ItemNBTHelper.getShort(stack, "size", (short)0) * 2) + 1;
		boolean oblit = ItemNBTHelper.getBoolean(stack, "obliterate", false);
		if ((!Keyboard.isKeyDown(42)) && (!Keyboard.isKeyDown(54))) {
			list.add(EnumChatFormatting.DARK_GREEN + "Hold shift for information");
			ItemInfoHelper.energyDisplayInfo(stack, list);
		}else {
			list.add(EnumChatFormatting.GREEN + "Mining Mode: " + EnumChatFormatting.BLUE + size + "x" + size);
			list.add(EnumChatFormatting.GREEN + "Shift Right-click to change minning mode");
			list.add(StatCollector.translateToLocal("msg.oblit" + oblit + ".txt"));
			list.add(EnumChatFormatting.GREEN + "Right-click to toggle Obliteration mode");
			list.add(EnumChatFormatting.GREEN + "Obliteration mode destroys low value blocks");
			list.add("");
			list.add(EnumChatFormatting.DARK_PURPLE + "" + EnumChatFormatting.ITALIC + "Further Draconic research has allowed");
			list.add(EnumChatFormatting.DARK_PURPLE + "" + EnumChatFormatting.ITALIC + "you to unlock even better methods");
		}
	}

	@Override
	public EnumRarity getRarity(ItemStack stack)
	{
		return EnumRarity.rare;
	}

	public static void registerRecipe()
	{
		CraftingManager.getInstance().addRecipe(new ItemStack(ModItems.draconicShovel), "ISI", "DPD", "ITI", 'P', ModItems.wyvernShovel, 'D', ModItems.draconicCompound, 'S', ModItems.sunFocus, 'T', ModItems.draconicCore, 'I', ModItems.draconiumIngot);
	}

	@Override
	public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {

		if (container.stackTagCompound == null) {
			container.stackTagCompound = new NBTTagCompound();
		}
		int energy = container.stackTagCompound.getInteger("EnergyHelper");
		int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));

		if (!simulate) {
			energy += energyReceived;
			container.stackTagCompound.setInteger("EnergyHelper", energy);
		}
		return energyReceived;
	}

	@Override
	public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {

		if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("EnergyHelper")) {
			return 0;
		}
		int energy = container.stackTagCompound.getInteger("EnergyHelper");
		int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));

		if (!simulate) {
			energy -= energyExtracted;
			container.stackTagCompound.setInteger("EnergyHelper", energy);
		}
		return energyExtracted;
	}

	@Override
	public int getEnergyStored(ItemStack container) {
		if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("EnergyHelper")) {
			return 0;
		}
		return container.stackTagCompound.getInteger("EnergyHelper");
	}

	@Override
	public int getMaxEnergyStored(ItemStack container) {
		return capacity;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return !(getEnergyStored(stack) == getMaxEnergyStored(stack));
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return 1D - ((double)getEnergyStored(stack) / (double)getMaxEnergyStored(stack));
	}

	@Override
	public float getDigSpeed(ItemStack stack, Block block, int meta) {
		if ((stack.getItem() instanceof IEnergyContainerItem) && ((IEnergyContainerItem)stack.getItem()).getEnergyStored(stack) >= References.ENERGYPERBLOCK)
			return super.getDigSpeed(stack, block, meta);
		else
			return 1F;
	}
}
