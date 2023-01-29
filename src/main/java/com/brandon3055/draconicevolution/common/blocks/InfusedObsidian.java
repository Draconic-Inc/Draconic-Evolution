package com.brandon3055.draconicevolution.common.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.lib.References;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Created by Brandon on 19/5/2015.
 */
public class InfusedObsidian extends BlockDE {

    public InfusedObsidian() {
        super(Material.rock);
        this.setHardness(100.0F);
        this.setResistance(4000.0F);
        this.setBlockName("infusedObsidian");
        this.setHarvestLevel("pickaxe", 4);
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);

        ModBlocks.register(this, InfusedObsidianItemBlock.class);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "animated/infusedObsidian");
    }

    @Override
    public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
        return entity instanceof EntityPlayer;
    }

    @Override
    public void onBlockExploded(World world, int x, int y, int z, Explosion explosion) {}

    @Override
    public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX,
            double explosionY, double explosionZ) {
        return super.getExplosionResistance(par1Entity, world, x, y, z, explosionX, explosionY, explosionZ);
    }

    @Override
    public boolean canDropFromExplosion(Explosion p_149659_1_) {
        return false;
    }

    public static class InfusedObsidianItemBlock extends ItemBlock {

        public InfusedObsidianItemBlock(Block p_i45328_1_) {
            super(p_i45328_1_);
        }

        @SideOnly(Side.CLIENT)
        @Override
        public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_, List list, boolean p_77624_4_) {
            list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("info.infusedObsidian.txt"));
            super.addInformation(p_77624_1_, p_77624_2_, list, p_77624_4_);
        }
    }
}
