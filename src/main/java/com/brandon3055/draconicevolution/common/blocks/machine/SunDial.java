package com.brandon3055.draconicevolution.common.blocks.machine;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.blocks.BlockDE;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.tileentities.TileSunDial;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SunDial extends BlockDE {

    // @SideOnly(Side.CLIENT)
    public IIcon icon_front;
    public IIcon icon_front_active;
    public IIcon icon_side;
    // public boolean blockState = true;

    public SunDial() {
        this.setBlockName(Strings.blockSunDialName);
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);
        this.setStepSound(soundTypeStone);
        ModBlocks.register(this);
    }

    @Override
    public boolean hasTileEntity(final int meta) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(final World world, final int meta) {
        return new TileSunDial();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister iconRegister) {
        icon_front = iconRegister.registerIcon(References.RESOURCESPREFIX + "sun_dial_front");
        icon_front_active = iconRegister.registerIcon(References.RESOURCESPREFIX + "animated/sun_dial_front_active");
        icon_side = iconRegister.registerIcon(References.RESOURCESPREFIX + "machine_side");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(final int side, final int meta) {
        if (side == 0 || side == 1 || side == 4 || side == 5) return icon_side;
        else return icon_front;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity tile = world.getTileEntity(x, y, z);
        IIcon front = null;
        if (tile != null && tile instanceof TileSunDial)
            front = ((TileSunDial) tile).running == true ? icon_front_active : icon_front;

        if (side == 0 || side == 1 || side == 4 || side == 5) return icon_side;
        else return front;
    }

    @Override
    public void onNeighborBlockChange(final World world, final int x, final int y, final int z, final Block block) {
        // if (!world.isRemote) {
        // world.setBlock(x, y, z, ModBlocks.sunDial, 0, 2);
        // }
    }

    @Override
    public boolean onBlockActivated(final World world, final int x, final int y, final int z,
            final EntityPlayer entityPlayer, final int par1, final float par2, final float par3, final float par4) {
        if (!world.isRemote) {
            // FMLNetworkHandler.openGui(entityPlayer, DraconicEvolution.instance, 1, world, x, y, z);
        }
        return true;
    }
}
