package com.brandon3055.draconicevolution.common.items.tools;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;

public class Reader extends Item
{
	public Reader() {
		this.setUnlocalizedName(Strings.readerName);
		this.setCreativeTab(DraconicEvolution.getCreativeTab(1));
		this.setMaxStackSize(1);
		GameRegistry.registerItem(this, Strings.readerName);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(final IIconRegister iconRegister)
	{
		this.itemIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + Strings.readerName);
	}
	
	@Override
	public IIcon getIcon(ItemStack stack, int pass)
	{
		return itemIcon;//(new ItemStack(Items.apple)).getIconIndex();
	}
	
	@Override
	public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining)
	{
		ItemStack selectedItem = getSelectedItem(player.worldObj, player, 1);
		if (selectedItem != null) 
			return selectedItem.getIconIndex();
		else
			return itemIcon;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		player.displayGUIBook(new ItemStack(Items.writable_book));
		if (!world.isRemote && player.isSneaking())
		{
			//FMLNetworkHandler.openGui(player, DraconicEvolution.instance, GuiHandler.GUIID_READER, world, (int) player.posX, (int) player.posY, (int) player.posZ);
		}else if (!player.isSneaking()){
			//System.out.println((getSelectedItem(world, player, 1).useItemRightClick(world, player)));
		}
		
		/*
		ItemStack book = new ItemStack(ModItems.teleporterMKII);
		book.useItemRightClick(par2World, par3EntityPlayer);
		*/
		return super.onItemRightClick(stack, world, player);
	}
	
	public ItemStack getSelectedItem(World world, EntityPlayer player, int item)  {
		
			ItemStack reader = player != null? player.getCurrentEquippedItem(): null;
			ItemStack selectedItem = null;
			
			if(reader != null && reader.getItem() instanceof Reader) {
				NBTTagCompound inventoryTagCompound = reader.hasTagCompound() ? reader.stackTagCompound: new NBTTagCompound();
				NBTTagList inventoryTagList = inventoryTagCompound.getTagList("Inventory", 10);
				
				if (inventoryTagList != null) {
					for (int i = 0; i < inventoryTagList.tagCount(); i++) {
						NBTTagCompound itemTagCompound = inventoryTagList.getCompoundTagAt(i);
						byte byte0 = itemTagCompound.getByte("Slot");
						if (byte0 == item) {
							selectedItem = ItemStack.loadItemStackFromNBT(itemTagCompound);
						}
					}

					return selectedItem;
				}
			}

		return selectedItem;
	}

}
