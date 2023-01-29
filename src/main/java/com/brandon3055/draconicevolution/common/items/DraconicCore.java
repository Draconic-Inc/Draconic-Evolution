package com.brandon3055.draconicevolution.common.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.brandon3055.brandonscore.common.utills.InfoHelper;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.tileentities.TileCustomSpawner;
import com.brandon3055.draconicevolution.common.utills.LogHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DraconicCore extends ItemDE {

    public DraconicCore() {
        this.setUnlocalizedName(Strings.draconicCoreName);
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);
        ModItems.register(this);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        par3List.add(InfoHelper.ITC() + StatCollector.translateToLocal("info.draconicCore.txt"));
        par3List.add(InfoHelper.ITC() + StatCollector.translateToLocal("info.draconicCore1.txt"));
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int par7,
            float par8, float par9, float par10) {
        if (world.getBlock(x, y, z) == Blocks.mob_spawner) {
            TileEntityMobSpawner oldSpawner = world.getTileEntity(x, y, z) instanceof TileEntityMobSpawner
                    ? (TileEntityMobSpawner) world.getTileEntity(x, y, z)
                    : null;
            if (oldSpawner == null) return false;
            String mobName = oldSpawner.func_145881_a().getEntityNameToSpawn();
            LogHelper.info(mobName);

            world.setBlock(x, y, z, ModBlocks.customSpawner);
            TileCustomSpawner newSpawner = world.getTileEntity(x, y, z) instanceof TileCustomSpawner
                    ? (TileCustomSpawner) world.getTileEntity(x, y, z)
                    : null;
            if (newSpawner != null) {
                newSpawner.getBaseLogic().entityName = mobName;
                newSpawner.isSetToSpawn = true;
                world.markBlockForUpdate(x, y, z);
            }
            LogHelper.info(newSpawner.getBaseLogic().entityName);

            stack.splitStack(1);
            return true;
        }
        return false;
    }
}
