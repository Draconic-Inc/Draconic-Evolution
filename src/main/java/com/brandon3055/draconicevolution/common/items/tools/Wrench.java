package com.brandon3055.draconicevolution.common.items.tools;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.items.ItemDE;
import com.brandon3055.draconicevolution.common.lib.Strings;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Created by Brandon on 23/08/2014.
 */
public class Wrench extends ItemDE {

	public Wrench()
	{
		this.setUnlocalizedName(Strings.wrenchName);
		this.setCreativeTab(DraconicEvolution.tabToolsWeapons);
		this.setMaxStackSize(1);
		ModItems.register(this);
	}

	@SuppressWarnings("unchecked")
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_, List list, boolean p_77624_4_) {
		//list.add(InfoHelper.ITC() + StatCollector.translateToLocal("info.de.safetyMatch.txt"));
	}
}
