package com.brandon3055.draconicevolution.common.blocks;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.blocks.itemblocks.CKeyStoneItemBlock;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.tileentities.TileCKeyStone;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Created by Brandon on 27/08/2014.
 */
public class CKeyStone extends BlockDE {

    private IIcon blockIcon1;

    public CKeyStone() {
        this.setBlockUnbreakable();
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);
        this.setBlockName(Strings.cKeyStoneName);
        ModBlocks.register(this, CKeyStoneItemBlock.class);
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileCKeyStone();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "key_stone_inactive");
        blockIcon1 = iconRegister.registerIcon(References.RESOURCESPREFIX + "key_stone_active");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        if (side == 0 || side == 1) return Blocks.furnace.getIcon(side, meta);
        else return blockIcon1;
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        if (side == 0 || side == 1) return Blocks.furnace.getIcon(side, 0);
        TileCKeyStone tile = world.getTileEntity(x, y, z) != null
                && world.getTileEntity(x, y, z) instanceof TileCKeyStone ? (TileCKeyStone) world.getTileEntity(x, y, z)
                        : null;
        if (tile != null) return tile.isActivated ? blockIcon1 : blockIcon;
        else return blockIcon;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_,
            float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        TileCKeyStone tile = world.getTileEntity(x, y, z) != null
                && world.getTileEntity(x, y, z) instanceof TileCKeyStone ? (TileCKeyStone) world.getTileEntity(x, y, z)
                        : null;

        if (tile != null) return tile.onActivated(player.getHeldItem(), player);
        return false;
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
        return true;
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int meta) {
        TileCKeyStone tile = world.getTileEntity(x, y, z) != null
                && world.getTileEntity(x, y, z) instanceof TileCKeyStone ? (TileCKeyStone) world.getTileEntity(x, y, z)
                        : null;
        if (tile != null) return tile.isActivated ? 15 : 0;
        return 0;
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int meta) {
        return 0;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
        TileCKeyStone tile = world.getTileEntity(x, y, z) != null
                && world.getTileEntity(x, y, z) instanceof TileCKeyStone ? (TileCKeyStone) world.getTileEntity(x, y, z)
                        : null;
        if (tile != null) {
            ItemStack key = new ItemStack(ModItems.key);
            ItemNBTHelper.setInteger(key, "KeyCode", tile.getKeyCode());
            ItemNBTHelper.setInteger(key, "X", x);
            ItemNBTHelper.setInteger(key, "Y", y);
            ItemNBTHelper.setInteger(key, "Z", z);
            return key;
        }
        return null;
    }

    @Override
    public boolean isBlockSolid(IBlockAccess p_149747_1_, int p_149747_2_, int p_149747_3_, int p_149747_4_,
            int p_149747_5_) {
        return true;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return true;
    }
}
