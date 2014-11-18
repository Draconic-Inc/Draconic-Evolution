package com.brandon3055.draconicevolution.common.items.tools;

import com.brandon3055.draconicevolution.common.items.ItemDE;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.world.WorldGenEnderComet;
import com.brandon3055.draconicevolution.common.world.WorldGenEnderIsland;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

/**
 * Created by Brandon on 29/08/2014.
 */
public class CreativeStructureSpawner extends ItemDE {
	public CreativeStructureSpawner() {
		this.setUnlocalizedName(Strings.creativeStructureSpawnerName);
		this.hasSubtypes = true;
		ModItems.register(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void getSubItems(Item item, CreativeTabs p_150895_2_, List list) {
		list.add(new ItemStack(item, 1, 0));
		list.add(new ItemStack(item, 1, 1));
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (world.isRemote) return stack;

		switch (stack.getItemDamage()){
			case 0:
				new WorldGenEnderComet().generate(world, new Random(), (int)player.posX, (int)player.posY + 10, (int)player.posZ);
				break;
			case 1:
				new WorldGenEnderIsland().generate(world, new Random(), (int)player.posX, (int)player.posY + 10, (int)player.posZ);
				break;
			case 2:
				break;
			case 3:
				break;
		}
		return stack;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		String name = "null";
		switch (itemStack.getItemDamage()){
			case 0:
				name = "Comet";
				break;
			case 1:
				name = "EnderIsland";
				break;
			case 2:
				break;
			case 3:
				break;
		}
		return super.getUnlocalizedName(itemStack)+name;
	}

	@SideOnly(Side.CLIENT)
	@SuppressWarnings("all")
	@Override
	public void addInformation(ItemStack stack, EntityPlayer p_77624_2_, List list, boolean p_77624_4_) {
		if (stack.getItemDamage() == 1) list.add("Warning this will take between 5-10 minutes (maby longer) to generate");
		if (stack.getItemDamage() == 1) list.add("depending on the speed of your pc");
	}
}
