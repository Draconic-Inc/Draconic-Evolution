package com.brandon3055.draconicevolution.common.items;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.interfaces.GuiHandler;
import com.brandon3055.draconicevolution.common.blocks.ModBlocks;
import com.brandon3055.draconicevolution.common.lib.Strings;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class DraconicCore extends ItemDE {
	public DraconicCore() {
		this.setUnlocalizedName(Strings.draconicCoreName);
		this.setCreativeTab(DraconicEvolution.tolkienTabBlocksItems);
		ModItems.register(this);
	}

	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		par3List.add(StatCollector.translateToLocal("info.draconicCore.txt"));
		par3List.add("");
		par3List.add("" + EnumChatFormatting.DARK_PURPLE + EnumChatFormatting.ITALIC + StatCollector.translateToLocal("info.draconicCore1.txt"));
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

	@Override
	public ItemStack onItemRightClick(ItemStack p_77659_1_, World world, EntityPlayer player) {
		FMLNetworkHandler.openGui(player, DraconicEvolution.instance, GuiHandler.GUIID_MANUAL, world, (int) player.posX, (int) player.posY, (int) player.posZ);
		return super.onItemRightClick(p_77659_1_, world, player);
	}
}
