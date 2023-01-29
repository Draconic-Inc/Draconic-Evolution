package com.brandon3055.draconicevolution.common.blocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.brandon3055.brandonscore.common.utills.InventoryUtils;
import com.google.common.collect.Lists;

/**
 * Created by Brandon on 13/11/2014.
 */
public abstract class BlockCustomDrop extends BlockContainerDE {

    public BlockCustomDrop(final Material material) {
        super(material);
    }

    /**
     * If true the tile will drop its inventory when broken
     */
    protected abstract boolean dropInventory();

    /**
     * If true the tile will drop a custom block with custom data when broken
     */
    protected abstract boolean hasCustomDropps();

    /**
     * Use to specify custom drops
     */
    protected abstract void getCustomTileEntityDrops(TileEntity te, List<ItemStack> droppes);

    private void getCustomDrops(TileEntity te, List<ItemStack> droppes) {
        if (te == null) return;
        if (hasCustomDropps()) {
            getCustomTileEntityDrops(te, droppes);
        }
        if (dropInventory() && te instanceof IInventory) {
            droppes.addAll(InventoryUtils.getInventoryContents((IInventory) te));
            for (int i = 0; i < ((IInventory) te).getSizeInventory(); i++)
                ((IInventory) te).setInventorySlotContents(i, null);
        }
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        if (willHarvest) {
            TileEntity te = world.getTileEntity(x, y, z);

            boolean result = super.removedByPlayer(world, player, x, y, z, willHarvest);

            if (result) {
                List<ItemStack> teDrops = Lists.newArrayList();
                getCustomDrops(te, teDrops);
                for (ItemStack drop : teDrops) dropBlockAsItem(world, x, y, z, drop);
            }

            return result;
        }

        return super.removedByPlayer(world, player, x, y, z, willHarvest);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> result = Lists.newArrayList();
        if (!hasCustomDropps()) result.addAll(super.getDrops(world, x, y, z, metadata, fortune));
        if (hasCustomDropps() || dropInventory()) {
            TileEntity te = world.getTileEntity(x, y, z);
            getCustomDrops(te, result);
        }

        return result;
    }
}
