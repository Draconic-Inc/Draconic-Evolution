package com.brandon3055.draconicevolution.common.items.tools;

import cofh.api.energy.IEnergyContainerItem;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.core.utills.InfoHelper;
import com.brandon3055.draconicevolution.common.core.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.common.entity.EntityPersistentItem;
import com.brandon3055.draconicevolution.common.items.ModItems;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class WyvernShovel extends ItemSpade implements IEnergyContainerItem{
	public IIcon itemIcon0;
	public IIcon itemIcon1;
	protected int capacity = References.WYVERNCAPACITY;
	protected int maxReceive = References.WYVERNTRANSFER;
	protected int maxExtract = References.WYVERNTRANSFER;

	public WyvernShovel() {
		super(ModItems.DRACONIUM_T1);
		this.setUnlocalizedName(Strings.wyvernShovelName);
		this.setCreativeTab(DraconicEvolution.tolkienTabToolsWeapons);
		GameRegistry.registerItem(this, Strings.wyvernShovelName);
	}

	@Override
	public boolean isItemTool(ItemStack p_77616_1_) {
		return true;
	}

	@SuppressWarnings("all")
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		list.add(ItemNBTHelper.setInteger(new ItemStack(item, 1, 0), "Energy", 0));
		list.add(ItemNBTHelper.setInteger(new ItemStack(item, 1, 0), "Energy", capacity));
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
		this.itemIcon0 = iconRegister.registerIcon(References.RESOURCESPREFIX + "wyvern_shovel");
		this.itemIcon1 = iconRegister.registerIcon(References.RESOURCESPREFIX + "wyvern_shovel_active");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining)
	{
		if (ItemNBTHelper.getShort(stack, "size", (short)0) > 0)
			return itemIcon1;
		else
			return itemIcon0;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconIndex(ItemStack stack)
	{
		if (ItemNBTHelper.getShort(stack, "size", (short)0) > 0)
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
		return ToolHandler.changeMode(stack, player, false, 1);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(final ItemStack stack, final EntityPlayer player, final List list, final boolean extraInformation)
	{
		int size = (ItemNBTHelper.getShort(stack, "size", (short) 0) * 2) + 1;
		if (InfoHelper.holdShiftForDetails(list)){
			InfoHelper.addEnergyInfo(stack, list);

			list.add(InfoHelper.ITC() + StatCollector.translateToLocal("info.de.miningMode.txt") + ": " + InfoHelper.HITC() + size + "x" + size);
			list.add(InfoHelper.ITC() + StatCollector.translateToLocal("info.de.changeMiningMode.txt"));

			InfoHelper.addLore(stack, list);
		}
	}

	@Override
	public EnumRarity getRarity(ItemStack stack)
	{
		return EnumRarity.uncommon;
	}

	public static void registerRecipe()
	{
		CraftingManager.getInstance().addRecipe(new ItemStack(ModItems.wyvernShovel), " C ", "CDC", " C ", 'C', ModItems.infusedCompound, 'D', Items.diamond_shovel);
	}

	@Override
	public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {

		if (container.stackTagCompound == null) {
			container.stackTagCompound = new NBTTagCompound();
		}
		int energy = container.stackTagCompound.getInteger("Energy");
		int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));

		if (!simulate) {
			energy += energyReceived;
			container.stackTagCompound.setInteger("Energy", energy);
		}
		return energyReceived;
	}

	@Override
	public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {

		if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Energy")) {
			return 0;
		}
		int energy = container.stackTagCompound.getInteger("Energy");
		int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));

		if (!simulate) {
			energy -= energyExtracted;
			container.stackTagCompound.setInteger("Energy", energy);
		}
		return energyExtracted;
	}

	@Override
	public int getEnergyStored(ItemStack container) {
		if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Energy")) {
			return 0;
		}
		return container.stackTagCompound.getInteger("Energy");
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

	@Override
	public boolean hasCustomEntity(ItemStack stack) {
		return true;
	}

	@Override
	public Entity createEntity(World world, Entity location, ItemStack itemstack) {
		return new EntityPersistentItem(world, location, itemstack);
	}
}

