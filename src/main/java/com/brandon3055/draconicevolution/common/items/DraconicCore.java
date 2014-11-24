package com.brandon3055.draconicevolution.common.items;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.utills.InfoHelper;
import com.brandon3055.draconicevolution.common.lib.Strings;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class DraconicCore extends ItemDE {//todo new description
	public DraconicCore() {
		this.setUnlocalizedName(Strings.draconicCoreName);
		this.setCreativeTab(DraconicEvolution.tabBlocksItems);
		ModItems.register(this);
	}

	@SuppressWarnings("unchecked")
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		par3List.add(InfoHelper.ITC() + StatCollector.translateToLocal("info.draconicCore.txt"));
		par3List.add(InfoHelper.ITC() + StatCollector.translateToLocal("info.draconicCore1.txt"));
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10) {
		if (world.getBlock(x, y, z) == Blocks.mob_spawner)
		{
			world.setBlock(x, y, z, ModBlocks.customSpawner);
			stack.splitStack(1);
			return true;
		}
		return false;
	}
}
