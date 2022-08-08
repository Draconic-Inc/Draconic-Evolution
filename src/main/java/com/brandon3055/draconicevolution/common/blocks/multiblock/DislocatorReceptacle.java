package com.brandon3055.draconicevolution.common.blocks.multiblock;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.blocks.BlockCustomDrop;
import com.brandon3055.draconicevolution.common.items.tools.TeleporterMKI;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.TileDislocatorReceptacle;
import java.util.List;
import java.util.Random;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Created by Brandon on 19/5/2015.
 */
public class DislocatorReceptacle extends BlockCustomDrop implements ITileEntityProvider {
    IIcon textureInactive;

    public DislocatorReceptacle() {
        super(Material.rock);
        this.setHardness(50.0F);
        this.setResistance(2000.0F);
        this.setBlockName("dislocatorReceptacle");
        this.setHarvestLevel("pickaxe", 3);
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);

        ModBlocks.register(this);
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        textureInactive =
                iconRegister.registerIcon(References.RESOURCESPREFIX + "animated/dislocatorReceptacle_inactive");
        blockIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "animated/dislocatorReceptacle_active");
    }

    @Override
    public IIcon getIcon(IBlockAccess access, int x, int y, int z, int side) {
        TileDislocatorReceptacle tile = access.getTileEntity(x, y, z) instanceof TileDislocatorReceptacle
                ? (TileDislocatorReceptacle) access.getTileEntity(x, y, z)
                : null;
        if (tile != null) return tile.isActive ? blockIcon : textureInactive;
        return blockIcon;
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        TileDislocatorReceptacle tile = (TileDislocatorReceptacle) world.getTileEntity(x, y, z);
        if (tile != null) tile.updateState();
    }

    @Override
    public IIcon getIcon(int p_149691_1_, int meta) {
        return blockIcon;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return new TileDislocatorReceptacle();
    }

    @Override
    public boolean onBlockActivated(
            World world,
            int x,
            int y,
            int z,
            EntityPlayer player,
            int side,
            float p_149727_7_,
            float p_149727_8_,
            float p_149727_9_) {
        if (world.isRemote) return true;
        TileDislocatorReceptacle tile = (TileDislocatorReceptacle) world.getTileEntity(x, y, z);
        if (tile == null) return false;

        if (tile.getStackInSlot(0) != null) {
            if (player.getHeldItem() == null) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, tile.getStackInSlot(0));
                tile.setInventorySlotContents(0, null);
            } else {
                world.spawnEntityInWorld(
                        new EntityItem(world, player.posX, player.posY, player.posZ, tile.getStackInSlot(0)));
                tile.setInventorySlotContents(0, null);
            }
            world.markBlockForUpdate(x, y, z);
            world.notifyBlockChange(x, y, z, this);

        } else {
            ItemStack stack = player.getHeldItem();
            if (stack != null
                    && stack.getItem() instanceof TeleporterMKI
                    && ((TeleporterMKI) stack.getItem()).getLocation(stack) != null) {
                tile.setInventorySlotContents(0, player.getHeldItem());
                player.destroyCurrentEquippedItem();
            }
        }

        return true;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int p_149736_5_) {
        TileDislocatorReceptacle tile = (TileDislocatorReceptacle) world.getTileEntity(x, y, z);
        return tile == null ? 0 : tile.getStackInSlot(0) != null ? 15 : 0;
    }

    @Override
    protected boolean dropInventory() {
        return true;
    }

    @Override
    protected boolean hasCustomDropps() {
        return false;
    }

    @Override
    protected void getCustomTileEntityDrops(TileEntity te, List<ItemStack> droppes) {}
}
