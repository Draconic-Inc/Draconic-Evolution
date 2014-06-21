package com.brandon3055.draconicevolution.client.creativetab;

import com.brandon3055.draconicevolution.common.blocks.ModBlocks;
import com.brandon3055.draconicevolution.common.core.handler.ConfigHandler;
import com.brandon3055.draconicevolution.common.items.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class TolkienTab extends CreativeTabs {
	private String label;
	public TolkienTab(final int id, final String modid, final String label) {
		super(id, modid);
		this.label = label;
	}

	@Override
	public Item getTabIconItem()
	{
		if(this.label.equals("toolsAndWeapons"))
			return ModItems.draconicDistructionStaff;
		else if(this.label.equals("blocksAndItems"))
			return ConfigHandler.disableSunDial == 2 ? Item.getItemFromBlock(ModBlocks.weatherController) : Item.getItemFromBlock(ModBlocks.sunDial);
		else
			return ModItems.tclogo;
	}
	
	@Override
	public String getTabLabel()
	{
		return this.label;
	}
}
