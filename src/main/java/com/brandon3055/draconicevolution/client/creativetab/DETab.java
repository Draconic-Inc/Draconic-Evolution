package com.brandon3055.draconicevolution.client.creativetab;

import com.brandon3055.draconicevolution.common.blocks.ModBlocks;
import com.brandon3055.draconicevolution.common.core.utills.LogHelper;
import com.brandon3055.draconicevolution.common.items.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class DETab extends CreativeTabs {
	private String label;
	private int tab;
	public DETab(int id, String modid, String label, int tab) {
		super(id, modid);
		this.label = label;
		this.tab = tab;
	}

	@Override
	public Item getTabIconItem()
	{
		if (tab == 0){
			return ModItems.draconicBow;
		}else if (tab == 1){
			return Item.getItemFromBlock(ModBlocks.energyInfuser);
		}else
			return Items.apple;
	}
	
	@Override
	public String getTabLabel()
	{
		return this.label;
	}
}
