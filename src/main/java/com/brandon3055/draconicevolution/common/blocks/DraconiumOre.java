package com.brandon3055.draconicevolution.common.blocks;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DraconiumOre extends BlockDE {

    public IIcon icon;
    public IIcon iconEnd;
    public IIcon iconNether;

    public DraconiumOre() {
        super(Material.rock);
        this.setBlockName(Strings.draconiumOreName);
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);
        this.setHardness(10f);
        this.setResistance(20.0f);

        this.setHarvestLevel("pickaxe", 3);
        ModBlocks.register(this);
    }

    @Override
    public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
        return entity instanceof EntityPlayer;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icon = iconRegister.registerIcon(References.RESOURCESPREFIX + "animated/draconium_ore");
        iconEnd = iconRegister.registerIcon(References.RESOURCESPREFIX + "animated/draconium_ore_end");
        iconNether = iconRegister.registerIcon(References.RESOURCESPREFIX + "animated/draconium_ore_nether");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        World world = Minecraft.getMinecraft().theWorld;
        int dim = world != null && world.provider != null ? world.provider.dimensionId : 0;

        if (dim == -1) return iconNether;
        else if (dim == 1) return iconEnd;
        else return icon;
    }

    @Override
    public int quantityDropped(Random random) {
        return 1 + random.nextInt(2);
    }

    @Override
    public int quantityDroppedWithBonus(int fortune, Random random) {
        return fortune == 0 ? this.quantityDropped(random)
                : this.quantityDropped(random) + fortune + random.nextInt(fortune * 2);
    }

    @Override
    protected boolean canSilkHarvest() {
        return true;
    }

    @Override
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
        return ModItems.draconiumDust;
    }
}
