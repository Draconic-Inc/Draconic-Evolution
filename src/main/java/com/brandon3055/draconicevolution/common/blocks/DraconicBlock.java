package com.brandon3055.draconicevolution.common.blocks;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;

/**
 * Created by Brandon on 21/11/2014.
 */
public class DraconicBlock extends BlockDE {

    IIcon top;

    public DraconicBlock() {
        this.setHardness(20F);
        this.setResistance(1000F);
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);
        this.setBlockName(Strings.draconicBlockName);
        this.setHarvestLevel("pickaxe", 4);
        ModBlocks.register(this);
    }

    @Override
    public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
        return entity instanceof EntityPlayer;
    }

    @Override
    public void onBlockExploded(World world, int x, int y, int z, Explosion explosion) {}

    @Override
    public boolean canDropFromExplosion(Explosion p_149659_1_) {
        return false;
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "draconic_block");
        top = iconRegister.registerIcon(References.RESOURCESPREFIX + "draconic_block_blank");
    }

    @Override
    public IIcon getIcon(int side, int p_149691_2_) {
        if (side == 0 || side == 1) return top;
        else return blockIcon;
    }
}
