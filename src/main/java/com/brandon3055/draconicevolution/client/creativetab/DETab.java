package com.brandon3055.draconicevolution.client.creativetab;

import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.common.ModItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class DETab extends CreativeTabs {
	private String label;
	private int tab;

	static ItemStack iconStackStaff;

	public static void initialize() {
		iconStackStaff = ItemNBTHelper.setInteger(new ItemStack(ModItems.draconicDestructionStaff), "Energy", 30000000);
	}

	public DETab(int id, String modid, String label, int tab) {
		super(id, modid);
		this.label = label;
		this.tab = tab;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getIconItemStack() {

		if (tab == 0) return iconStackStaff;
		else return new ItemStack(ModBlocks.energyInfuser);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem()
	{
		return getIconItemStack().getItem();
	}
	
	@Override
	public String getTabLabel()
	{
		return this.label;
	}
}
