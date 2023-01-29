package com.brandon3055.draconicevolution.common.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.tileentities.TilePlacedItem;

/**
 * Created by Brandon on 13/08/2014.
 */
public class PlacedItem extends BlockDE {

    public PlacedItem() {
        super(Material.circuits);
        this.setHardness(5F);
        this.setResistance(20F);
        this.setBlockName(Strings.placedItemName);
        ModBlocks.register(this);
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon("glass");
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        switch (world.getBlockMetadata(x, y, z)) {
            case 0:
                setBlockBounds(0.0F, 0.8F, 0.0F, 1.0F, 1.0F, 1.0F);
                break;
            case 1:
                setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.2F, 1.0F);
                break;
            case 2:
                setBlockBounds(0.0F, 0.0F, 0.8F, 1.0F, 1.0F, 1.0F);
                break;
            case 3:
                setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.2F);
                break;
            case 4:
                setBlockBounds(0.8F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                break;
            case 5:
                setBlockBounds(0.0F, 0.0F, 0.0F, 0.2F, 1.0F, 1.0F);
                break;
            default:
                super.setBlockBoundsBasedOnState(world, x, y, z);
        }
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        TilePlacedItem tile = (world.getTileEntity(x, y, z) != null
                && world.getTileEntity(x, y, z) instanceof TilePlacedItem)
                        ? (TilePlacedItem) world.getTileEntity(x, y, z)
                        : null;
        int meta = world.getBlockMetadata(x, y, z);
        if (tile != null && tile.getStack() != null) {
            if (tile.getStack().getItem() instanceof ItemBlock) {
                switch (meta) {
                    case 0:
                        return AxisAlignedBB
                                .getBoundingBox(x + 0.25D, y + 0.5D, z + 0.25D, x + 0.75D, y + 1D, z + 0.75D);
                    case 1:
                        return AxisAlignedBB
                                .getBoundingBox(x + 0.25D, y + 0D, z + 0.25D, x + 0.75D, y + 0.5D, z + 0.75D);
                    case 2:
                        return AxisAlignedBB
                                .getBoundingBox(x + 0.25D, y + 0.25D, z + 0.5D, x + 0.75D, y + 0.75D, z + 1D);
                    case 3:
                        return AxisAlignedBB
                                .getBoundingBox(x + 0.25D, y + 0.25D, z + 0D, x + 0.75D, y + 0.75D, z + 0.5D);
                    case 4:
                        return AxisAlignedBB
                                .getBoundingBox(x + 0.5D, y + 0.25D, z + 0.25D, x + 1D, y + 0.75D, z + 0.75D);
                    case 5:
                        return AxisAlignedBB
                                .getBoundingBox(x + 0D, y + 0.25D, z + 0.25D, x + 0.5D, y + 0.75D, z + 0.75D);
                }
            } else {
                switch (meta) {
                    case 0:
                        return AxisAlignedBB
                                .getBoundingBox(x + 0.25D, y + 0.9D, z + 0.25D, x + 0.75D, y + 1D, z + 0.75D);
                    case 1:
                        return AxisAlignedBB
                                .getBoundingBox(x + 0.25D, y + 0D, z + 0.25D, x + 0.75D, y + 0.1D, z + 0.75D);
                    case 2:
                        return AxisAlignedBB
                                .getBoundingBox(x + 0.25D, y + 0.25D, z + 0.9D, x + 0.75D, y + 0.75D, z + 1D);
                    case 3:
                        return AxisAlignedBB
                                .getBoundingBox(x + 0.25D, y + 0.25D, z + 0D, x + 0.75D, y + 0.75D, z + 0.1D);
                    case 4:
                        return AxisAlignedBB
                                .getBoundingBox(x + 0.9D, y + 0.25D, z + 0.25D, x + 1D, y + 0.75D, z + 0.75D);
                    case 5:
                        return AxisAlignedBB
                                .getBoundingBox(x + 0D, y + 0.25D, z + 0.25D, x + 0.1D, y + 0.75D, z + 0.75D);
                }
            }
        }
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TilePlacedItem();
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TilePlacedItem && ((TilePlacedItem) te).getStack() != null) {
            TilePlacedItem tile = (TilePlacedItem) te;

            float spawnX = x + world.rand.nextFloat();
            float spawnY = y + world.rand.nextFloat();
            float spawnZ = z + world.rand.nextFloat();

            EntityItem droppedItem = new EntityItem(world, spawnX, spawnY, spawnZ, tile.getStack());
            tile.setStack(null);

            float multiplier = 0.05F;

            droppedItem.motionX = (-0.5F + world.rand.nextFloat()) * multiplier;
            droppedItem.motionY = (4 + world.rand.nextFloat()) * multiplier;
            droppedItem.motionZ = (-0.5F + world.rand.nextFloat()) * multiplier;

            world.spawnEntityInWorld(droppedItem);
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
        return null;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_,
            float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        if (player.isSneaking()) {
            TilePlacedItem tile = (world.getTileEntity(x, y, z) != null
                    && world.getTileEntity(x, y, z) instanceof TilePlacedItem)
                            ? (TilePlacedItem) world.getTileEntity(x, y, z)
                            : null;
            if (tile == null) {
                world.setBlockToAir(x, y, z);
            }
            tile.rotation += 5.625F;
        } else {
            if (!world.isRemote) breakBlock(world, x, y, z, this, world.getBlockMetadata(x, y, z));
            world.setBlockToAir(x, y, z);
        }
        world.markBlockForUpdate(x, y, z);
        return true;
    }

    @Override
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
        if (player.isSneaking()) {
            TilePlacedItem tile = (world.getTileEntity(x, y, z) != null
                    && world.getTileEntity(x, y, z) instanceof TilePlacedItem)
                            ? (TilePlacedItem) world.getTileEntity(x, y, z)
                            : null;
            if (tile == null) {
                world.setBlockToAir(x, y, z);
            }
            tile.rotation += 22.5F;
        } else {
            TilePlacedItem tile = (world.getTileEntity(x, y, z) != null
                    && world.getTileEntity(x, y, z) instanceof TilePlacedItem)
                            ? (TilePlacedItem) world.getTileEntity(x, y, z)
                            : null;
            if (tile == null) {
                world.setBlockToAir(x, y, z);
            }
            tile.rotation -= 22.5F;
        }
        world.markBlockForUpdate(x, y, z);
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TilePlacedItem && ((TilePlacedItem) te).getStack() != null) {
            TilePlacedItem tile = (TilePlacedItem) te;
            if (tile.getStack() != null && tile.getStack().getItem() instanceof ItemBlock)
                return Block.getBlockFromItem(tile.getStack().getItem()).getLightValue();
        }
        return 0;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TilePlacedItem && ((TilePlacedItem) te).getStack() != null) {
            TilePlacedItem tile = (TilePlacedItem) te;
            return tile.getStack();
        }
        return null;
    }
}
