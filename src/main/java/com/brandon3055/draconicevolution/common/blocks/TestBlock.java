package com.brandon3055.draconicevolution.common.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.blocks.itemblocks.TestItemBlock;
import com.brandon3055.draconicevolution.common.tileentities.TileTestBlock;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TestBlock extends BlockDE {

    public TestBlock() {
        super(Material.rock);
        this.setBlockName("testBlock");
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);
        this.setHardness(5f);
        this.setResistance(200.0f);
        // this.setBlockBounds(0.4f, 0f, 0.4f, 0.6f, 1f, 0.6f);
        ModBlocks.register(this, TestItemBlock.class);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess p_149673_1_, int p_149673_2_, int p_149673_3_, int p_149673_4_, int p_149673_5_) {
        return null;
        // return super.getIcon(p_149673_1_, p_149673_2_, p_149673_3_, p_149673_4_, p_149673_5_);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
        return null;
        // return super.getIcon(p_149691_1_, p_149691_2_);
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileTestBlock();
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        return new ItemStack(Item.getItemFromBlock(world.getBlock(x, y, z)), 1, world.getBlockMetadata(x, y, z));
        // return super.getPickBlock(target, world, x, y, z);
    }

    @Override
    public boolean isBeaconBase(IBlockAccess worldObj, int x, int y, int z, int beaconX, int beaconY, int beaconZ) {
        return true;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float px, float py,
            float pz) {
        EntityLightningBolt bolt = new EntityLightningBolt(world, x, y, z + 5);
        // System.out.println(world.getBlockMetadata(x,y,z));
        world.spawnEntityInWorld(bolt);
        /*
         * if (!world.isRemote && !player.isSneaking()) { System.out.println("Sending from server");
         * DraconicEvolution.channelHandler.sendToAll(new ExamplePacket()); } if (world.isRemote && player.isSneaking())
         * { System.out.println("Sending from client"); DraconicEvolution.channelHandler.sendToServer(new
         * ExamplePacket()); }
         */
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item item, CreativeTabs p_149666_2_, List list) {
        // for (int i = 0; i < 16; i++) {
        list.add(new ItemStack(item, 1, 0));
        // }
    }

    @Override
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
        return super.getItemDropped(p_149650_1_, p_149650_2_, p_149650_3_);
    }

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {}

    @Override
    public int getRenderType() {
        return -1; // super.getRenderType();
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int p_149646_2_, int p_149646_3_, int p_149646_4_,
            int p_149646_5_) {
        return true;
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        return 0;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }
}
