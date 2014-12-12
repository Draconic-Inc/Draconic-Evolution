package com.brandon3055.draconicevolution.common.items.tools;

import cofh.api.energy.IEnergyContainerItem;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.entity.EntityPersistentItem;
import com.brandon3055.draconicevolution.common.items.ItemDE;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.utills.InfoHelper;
import com.brandon3055.draconicevolution.common.utills.ItemNBTHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by Brandon on 24/11/2014.
 */
public class DraconiumFluxCapacitor extends ItemDE implements IEnergyContainerItem{
	IIcon[] icons = new IIcon[2];
	public DraconiumFluxCapacitor()
	{
		this.setUnlocalizedName(Strings.draconiumFluxCapacitorName);
		this.setCreativeTab(DraconicEvolution.tabToolsWeapons);
		this.setHasSubtypes(true);
		this.setMaxStackSize(1);
		ModItems.register(this);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconRegister) {
		icons[0] = iconRegister.registerIcon(getUnwrappedUnlocalizedName(super.getUnlocalizedName())+0);
		icons[1] = iconRegister.registerIcon(getUnwrappedUnlocalizedName(super.getUnlocalizedName())+1);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int damage) {
		return icons[damage];
	}

	@SideOnly(Side.CLIENT)
	@SuppressWarnings("unchecked")
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		list.add(ItemNBTHelper.setInteger(new ItemStack(item, 1, 0), "Energy", 0));
		list.add(ItemNBTHelper.setInteger(new ItemStack(item, 1, 0), "Energy", getCapacity(0)));
		list.add(ItemNBTHelper.setInteger(new ItemStack(item, 1, 1), "Energy", 0));
		list.add(ItemNBTHelper.setInteger(new ItemStack(item, 1, 1), "Energy", getCapacity(1)));
	}

	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		return super.getUnlocalizedName(itemStack)+itemStack.getItemDamage();
	}

	private int getCapacity(int damage){
		return damage == 0 ? 80000000 : damage == 1 ? 250000000 : 0;
 	}

	private int getTransfer(int damage){
		return damage == 0 ? 100000 : damage == 1 ? 1000000 : 0;
	}

	@Override
	public void onUpdate(ItemStack container, World world, Entity entity, int var1, boolean b) {
		if (!(entity instanceof EntityPlayer)) return;
		EntityPlayer player = (EntityPlayer) entity;

		int mode = ItemNBTHelper.getShort(container, "Mode", (short)0);

		if (mode == 1 || mode == 3){ //Charge Hotbar
			for (int i = 0; i < 9; i++){
				int max = Math.min(getEnergyStored(container), getTransfer(container.getItemDamage()));
				ItemStack stack = player.inventory.getStackInSlot(i);

				if (stack != null && stack.getItem() instanceof IEnergyContainerItem && stack.getItem() != ModItems.draconiumFluxCapacitor) {
					IEnergyContainerItem item = (IEnergyContainerItem)stack.getItem();
					extractEnergy(container, item.receiveEnergy(stack, max, false), false);
				}
			}
		}

		if (mode == 2 || mode == 3){ //Charge Armor and held item
			for (int i = mode == 3 ? 1 : 0; i < 5; i++){
				int max = Math.min(getEnergyStored(container), getTransfer(container.getItemDamage()));
				ItemStack stack = player.getEquipmentInSlot(i);

				if (stack != null && stack.getItem() instanceof IEnergyContainerItem && stack.getItem() != ModItems.draconiumFluxCapacitor) {
					IEnergyContainerItem item = (IEnergyContainerItem)stack.getItem();
					extractEnergy(container, item.receiveEnergy(stack, max, false), false);
				}
			}
		}
	}

	@Override
	public boolean hasEffect(ItemStack stack, int pass) {
		return ItemNBTHelper.getShort(stack, "Mode", (short)0) > 0;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (player.isSneaking()){
			int mode = ItemNBTHelper.getShort(stack, "Mode", (short)0);
			int newMode = mode == 3 ? 0 : mode + 1;
			ItemNBTHelper.setShort(stack, "Mode", (short) newMode);
			if (world.isRemote) player.addChatComponentMessage(new ChatComponentTranslation(InfoHelper.ITC()+StatCollector.translateToLocal("info.de.capacitorMode.txt")+": "+InfoHelper.HITC()+StatCollector.translateToLocal("info.de.capacitorMode"+ItemNBTHelper.getShort(stack, "Mode", (short)0)+".txt")));
		}
		return stack;
	}

	@Override
	public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {

		if (container.stackTagCompound == null) {
			container.stackTagCompound = new NBTTagCompound();
		}
		int energy = container.stackTagCompound.getInteger("Energy");
		int energyReceived = Math.min(getCapacity(container.getItemDamage()) - energy, Math.min(getTransfer(container.getItemDamage()), maxReceive));

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
		int energyExtracted = Math.min(energy, Math.min(getTransfer(container.getItemDamage()), maxExtract));

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
		return getCapacity(container.getItemDamage());
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return !(getEnergyStored(stack) == getMaxEnergyStored(stack));
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return 1D - ((double)getEnergyStored(stack) / (double)getMaxEnergyStored(stack));
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void addInformation(final ItemStack stack, final EntityPlayer player, final List list, final boolean extraInformation)
	{
		if (InfoHelper.holdShiftForDetails(list)){
			InfoHelper.addEnergyInfo(stack, list);
			list.add(StatCollector.translateToLocal("info.de.changwMode.txt"));
			list.add(InfoHelper.ITC()+StatCollector.translateToLocal("info.de.capacitorMode.txt")+": "+InfoHelper.HITC()+StatCollector.translateToLocal("info.de.capacitorMode"+ItemNBTHelper.getShort(stack, "Mode", (short)0)+".txt"));
			//InfoHelper.addLore(stack, list);
		}
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
