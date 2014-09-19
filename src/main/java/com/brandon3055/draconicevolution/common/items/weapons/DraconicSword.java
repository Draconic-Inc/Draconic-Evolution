package com.brandon3055.draconicevolution.common.items.weapons;

import cofh.api.energy.IEnergyContainerItem;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.core.utills.EnergyHelper;
import com.brandon3055.draconicevolution.common.core.utills.ItemInfoHelper;
import com.brandon3055.draconicevolution.common.entity.EntityPersistentItem;
import com.brandon3055.draconicevolution.common.items.ModItems;
import com.brandon3055.draconicevolution.common.items.tools.ToolHandler;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import java.util.List;

public class DraconicSword extends ItemSword implements IEnergyContainerItem{
	protected int capacity = References.DRACONICCAPACITY;
	protected int maxReceive = References.DRACONICTRANSFER;
	protected int maxExtract = References.DRACONICTRANSFER * 50;

	public DraconicSword() {
		super(ModItems.DRACONIUM_T2);
		this.setUnlocalizedName(Strings.draconicSwordName);
		this.setCreativeTab(DraconicEvolution.tolkienTabToolsWeapons);
		GameRegistry.registerItem(this, Strings.draconicSwordName);
	}

	@SuppressWarnings("all")
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		list.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(item, 1, 0), 0));
		list.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(item, 1, 0), capacity));
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
		this.itemIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "draconic_sword");
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
	{
		ToolHandler.AOEAttack(player, entity, stack, 15, 3);
		ToolHandler.damageEntityBasedOnHealth(entity, player, 0.3F);
		return true;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(final ItemStack stack, final EntityPlayer player, final List list, final boolean extraInformation)
	{
		list.add(EnumChatFormatting.DARK_RED + "Your enemy's strength will be their undoing");
		ItemInfoHelper.energyDisplayInfo(stack, list);
		list.add(EnumChatFormatting.DARK_PURPLE + "" + EnumChatFormatting.ITALIC + "Further Draconic research has allowed");
		list.add(EnumChatFormatting.DARK_PURPLE + "" + EnumChatFormatting.ITALIC + "you to unlock even better methods");
	}

	@Override
	public EnumRarity getRarity(ItemStack stack)
	{
		return EnumRarity.rare;
	}

	public static void registerRecipe()
	{
		CraftingManager.getInstance().addRecipe(new ItemStack(ModItems.draconicSword), "ISI", "DPD", "ITI", 'P', ModItems.wyvernSword, 'D', ModItems.draconicCompound, 'S', ModItems.sunFocus, 'T', ModItems.draconicCore, 'I', ModItems.draconiumIngot);
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
	public boolean hasCustomEntity(ItemStack stack) {
		return true;
	}

	@Override
	public Entity createEntity(World world, Entity location, ItemStack itemstack) {
		return new EntityPersistentItem(world, location, itemstack);
	}
}
