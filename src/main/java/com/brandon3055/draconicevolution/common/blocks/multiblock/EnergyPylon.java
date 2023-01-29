package com.brandon3055.draconicevolution.common.blocks.multiblock;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.blocks.BlockDE;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.TileEnergyPylon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Created by Brandon on 28/07/2014.
 */
public class EnergyPylon extends BlockDE { // todo fix sphere renderer

    @SideOnly(Side.CLIENT)
    public IIcon icon_active_face;

    public IIcon icon_input;
    public IIcon icon_output;

    public EnergyPylon() {
        super(Material.iron);
        this.setHardness(10F);
        this.setResistance(20F);
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);
        this.setBlockName(Strings.energyPylonName);
        ModBlocks.register(this);
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return metadata == 1 || metadata == 2;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        if (metadata == 1 || metadata == 2) return new TileEnergyPylon();
        else return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icon_input = iconRegister.registerIcon(References.RESOURCESPREFIX + "energy_pylon_input");
        icon_output = iconRegister.registerIcon(References.RESOURCESPREFIX + "energy_pylon_output");
        icon_active_face = iconRegister.registerIcon(References.RESOURCESPREFIX + "energy_pylon_active_face");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (meta == 1 && side == 1) return icon_active_face;
        if (meta == 2 && side == 0) return icon_active_face;
        return icon_input;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 1 && side == 1) return icon_active_face;
        if (meta == 2 && side == 0) return icon_active_face;
        TileEnergyPylon thisTile = (world.getTileEntity(x, y, z) != null
                && world.getTileEntity(x, y, z) instanceof TileEnergyPylon)
                        ? (TileEnergyPylon) world.getTileEntity(x, y, z)
                        : null;
        if (thisTile == null) return icon_input;
        return !thisTile.reciveEnergy ? icon_output : icon_input;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block p_149695_5_) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0) {
            if (world.getBlock(x, y + 1, z) == Blocks.glass) {
                world.setBlockMetadataWithNotify(x, y, z, 1, 2);
                world.setBlock(x, y + 1, z, ModBlocks.invisibleMultiblock, 2, 2);
            } else if (world.getBlock(x, y - 1, z) == Blocks.glass) {
                world.setBlockMetadataWithNotify(x, y, z, 2, 2);
                world.setBlock(x, y - 1, z, ModBlocks.invisibleMultiblock, 2, 2);
            }
        } else {
            TileEnergyPylon thisTile = (world.getTileEntity(x, y, z) != null
                    && world.getTileEntity(x, y, z) instanceof TileEnergyPylon)
                            ? (TileEnergyPylon) world.getTileEntity(x, y, z)
                            : null;
            if (thisTile == null || (meta == 1 && !isGlass(world, x, y + 1, z))
                    || (meta == 2 && !isGlass(world, x, y - 1, z))) {
                world.setBlockMetadataWithNotify(x, y, z, 0, 2);
            }
        }
        TileEnergyPylon thisTile = (world.getTileEntity(x, y, z) != null
                && world.getTileEntity(x, y, z) instanceof TileEnergyPylon)
                        ? (TileEnergyPylon) world.getTileEntity(x, y, z)
                        : null;
        if (thisTile != null) {
            thisTile.onActivated();
        }
        if (world.getBlockMetadata(x, y, z) == 0 && world.getTileEntity(x, y, z) != null)
            world.removeTileEntity(x, y, z);
    }

    private boolean isGlass(World world, int x, int y, int z) {
        return world.getBlock(x, y, z) == ModBlocks.invisibleMultiblock && world.getBlockMetadata(x, y, z) == 2;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_,
            float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0) return false;
        TileEnergyPylon thisTile = (world.getTileEntity(x, y, z) != null
                && world.getTileEntity(x, y, z) instanceof TileEnergyPylon)
                        ? (TileEnergyPylon) world.getTileEntity(x, y, z)
                        : null;
        if (thisTile != null) {
            if (!player.isSneaking()) thisTile.onActivated();
            else thisTile.nextCore();
            return true;
        }
        return false;
    }

    @Override
    public void onPostBlockPlaced(World world, int x, int y, int z, int p_149714_5_) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0) {
            if (world.getBlock(x, y + 1, z) == Blocks.glass) {
                world.setBlockMetadataWithNotify(x, y, z, 1, 2);
                world.setBlock(x, y + 1, z, ModBlocks.invisibleMultiblock, 2, 2);
            } else if (world.getBlock(x, y - 1, z) == Blocks.glass) {
                world.setBlockMetadataWithNotify(x, y, z, 2, 2);
                world.setBlock(x, y - 1, z, ModBlocks.invisibleMultiblock, 2, 2);
            }
        } else {
            TileEnergyPylon thisTile = (world.getTileEntity(x, y, z) != null
                    && world.getTileEntity(x, y, z) instanceof TileEnergyPylon)
                            ? (TileEnergyPylon) world.getTileEntity(x, y, z)
                            : null;
            if (thisTile == null || (meta == 1 && !isGlass(world, x, y + 1, z))
                    || (meta == 2 && !isGlass(world, x, y - 1, z))) {
                world.setBlockMetadataWithNotify(x, y, z, 0, 2);
            }
        }
        TileEnergyPylon thisTile = (world.getTileEntity(x, y, z) != null
                && world.getTileEntity(x, y, z) instanceof TileEnergyPylon)
                        ? (TileEnergyPylon) world.getTileEntity(x, y, z)
                        : null;
        if (thisTile != null) {
            thisTile.onActivated();
        }
        if (world.getBlockMetadata(x, y, z) == 0 && world.getTileEntity(x, y, z) != null)
            world.removeTileEntity(x, y, z);
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int meta) {
        TileEnergyPylon tile = world.getTileEntity(x, y, z) instanceof TileEnergyPylon
                ? (TileEnergyPylon) world.getTileEntity(x, y, z)
                : null;
        if (tile != null) return (int) (tile.getEnergyStored() / tile.getMaxEnergyStored() * 15D);
        return 0;
    }
}
