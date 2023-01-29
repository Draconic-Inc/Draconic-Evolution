package com.brandon3055.draconicevolution.common.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.tileentities.TileChaosShard;

/**
 * Created by brandon3055 on 24/9/2015.
 */
public class ChaosCrystal extends BlockDE {

    public ChaosCrystal() {
        super(Material.rock);
        this.setHardness(100.0F);
        this.setResistance(4000.0F);
        this.setBlockUnbreakable();
        this.setBlockName("chaosCrystal");
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);

        ModBlocks.register(this);
    }

    @Override
    public float getBlockHardness(World world, int x, int y, int z) {
        TileChaosShard tile = world.getTileEntity(x, y, z) instanceof TileChaosShard
                ? (TileChaosShard) world.getTileEntity(x, y, z)
                : null;
        if (tile != null) return tile.guardianDefeated ? 100F : -1F;
        return super.getBlockHardness(world, x, y, z);
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "transparency");
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileChaosShard();
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
        return ModItems.chaosShard;
    }

    @Override
    public int quantityDropped(int meta, int fortune, Random random) {
        return 5;
    }

    @Override
    public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
        return false;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int i) {
        if (!world.isRemote && world.getTileEntity(x, y, z) instanceof TileChaosShard) {
            ((TileChaosShard) world.getTileEntity(x, y, z)).detonate();
        }
        super.breakBlock(world, x, y, z, block, i);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        entity.attackEntityFrom(punishment, Float.MAX_VALUE);
    }

    private static String[] naughtyList = new String[] { "item.blockMover", "tile.CardboardBox", "item.WandCasting" };
    private static DamageSource punishment = new DamageSource("chrystalMoved").setDamageAllowedInCreativeMode()
            .setDamageBypassesArmor().setDamageIsAbsolute();

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        List<EntityPlayer> players = world.getEntitiesWithinAABB(
                EntityPlayer.class,
                AxisAlignedBB.getBoundingBox(x, y, z, x, y, z).expand(15, 15, 15));

        for (EntityPlayer player : players) {
            if (player.getHeldItem() != null) {
                for (String s : naughtyList) {
                    if (player.getHeldItem().getUnlocalizedName().equals(s)) {
                        player.attackEntityFrom(punishment, Float.MAX_VALUE);
                    }
                }
            }
        }
    }
}
