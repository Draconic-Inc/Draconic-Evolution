package com.brandon3055.draconicevolution.common.items;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.core.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.common.lib.Strings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Created by Brandon on 7/07/2014.
 */
public class MobSoul extends DraconicEvolutionItem {
	public MobSoul() {
		this.setUnlocalizedName(Strings.MobSoulName);
		ModItems.register(this);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
		String name = ItemNBTHelper.getString(stack, "Name", "Pig");
		list.add(name);
	}
}
