package com.brandon3055.draconicevolution.common.blocks;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.blocks.itemblocks.DraconiumItemBlock;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.TileEnderResurrection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Created by Brandon on 25/07/2014.
 */
public class DraconiumBlock extends BlockDE {

    private final int DRACONIUM_META = 0;
    private final int SUMMON_BLOCK_META = 1;
    private final int CHARGED_DRACONIUM_META = 2;
    IIcon icons[] = new IIcon[3];

    public DraconiumBlock() {
        this.setHardness(10F);
        this.setResistance(500F);
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);
        this.setBlockName(Strings.draconiumBlockName);
        this.setHarvestLevel("pickaxe", 4);
        ModBlocks.register(this, DraconiumItemBlock.class);
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
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        for (int i = 0; i < 3; i++) {
            icons[i] = iconRegister.registerIcon(References.RESOURCESPREFIX + "draconium_block_" + i);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta >= icons.length) return icons[0];
        return icons[meta];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item item, CreativeTabs p_149666_2_, List list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
        list.add(new ItemStack(item, 1, 2));
    }

    @Override
    public boolean isBeaconBase(IBlockAccess worldObj, int x, int y, int z, int beaconX, int beaconY, int beaconZ) {
        return true;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_,
            float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0) return false;
        if (meta == 1) {
            TileEnderResurrection tile = (world.getTileEntity(x, y, z) != null
                    && world.getTileEntity(x, y, z) instanceof TileEnderResurrection)
                            ? (TileEnderResurrection) world.getTileEntity(x, y, z)
                            : null;
            if (tile != null) return tile.onActivated(player);
        }
        return false;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        if (metadata == 1) return true;
        return false;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        if (metadata == 1) return new TileEnderResurrection();
        return null;
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public float getEnchantPowerBonus(World world, int x, int y, int z) {
        if (world.getBlockMetadata(x, y, z) == 0) return 4f;
        if (world.getBlockMetadata(x, y, z) == 2) return 12f;
        return 0;
    }
}
