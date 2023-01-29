package com.brandon3055.draconicevolution.common.blocks.machine;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.blocks.BlockDE;
import com.brandon3055.draconicevolution.common.blocks.itemblocks.LRDItemBlock;
import com.brandon3055.draconicevolution.common.handler.ConfigHandler;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.utills.LogHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Created by Brandon on 20/07/2014.
 */
public class LongRangeDislocator extends BlockDE {

    IIcon blockIconTop;

    public LongRangeDislocator() {
        this.setBlockName(Strings.longRangeDislocatorName);
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);
        ModBlocks.register(this, LRDItemBlock.class);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "teleporter_front");
        blockIconTop = iconRegister.registerIcon(References.RESOURCESPREFIX + "machine_top_0");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(final int side, final int meta) {
        if (side == 0 || side == 1) return blockIconTop;
        else return blockIcon;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item item, CreativeTabs p_149666_2_, List list) {
        // list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_,
            float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0) return false; // return teleportPlayer(world, player, false);
        else if (meta == 1) return false;
        else {
            LogHelper.error("Invalid LRT Metadata");
            return false;
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block p_149695_5_) {
        EntityPlayer player;
        if (world.getBlockMetadata(x, y, z) == 1 && world.isBlockIndirectlyGettingPowered(x, y, z)) {
            player = world.getClosestPlayer(x, y, z, ConfigHandler.admin_dislocator_Detect_Range);
            if (player != null) {
                teleportPlayer(world, player, true);
            }
        }
    }

    private boolean teleportPlayer(World world, EntityPlayer player, boolean admin) {
        if (world.isRemote) return true;
        Random rand = new Random();

        if (admin) {
            int randX = ConfigHandler.admin_dislocator_Min_Range
                    + rand.nextInt(ConfigHandler.admin_dislocator_Max_Range - ConfigHandler.admin_dislocator_Min_Range);
            int randZ = ConfigHandler.admin_dislocator_Min_Range
                    + rand.nextInt(ConfigHandler.admin_dislocator_Max_Range - ConfigHandler.admin_dislocator_Min_Range);
            randX = rand.nextBoolean() ? randX * -1 : randX * 1;
            randZ = rand.nextBoolean() ? randZ * -1 : randZ * 1;
            randX += (int) player.posX;
            randZ += (int) player.posZ;
            int y = 255;
            for (int i = 255; i > 1; i--) {
                if (world.getBlock(randX, i, randZ) != Blocks.air) {
                    y = i;
                    break;
                }
            }
            player.addChatComponentMessage(
                    new ChatComponentText(
                            "" + EnumChatFormatting.RED
                                    + "[WARNING]"
                                    + EnumChatFormatting.WHITE
                                    + " Do not move until the world loads!"));
            if (world.getBlock(randX, y, randZ).isBlockSolid(world, randX, y, randZ, 0)) {
                world.setBlock(randX, y, randZ, world.getBlock(randX, y, randZ));
                player.setLocationAndAngles(randX + 0.5, y + 1.5, randZ + 0.5, 0F, 0F);
                player.setPositionAndUpdate(randX + 0.5, y + 1.5, randZ + 0.5);
            } else {
                world.setBlock(randX, y, randZ, Blocks.stone);
                player.setLocationAndAngles(randX + 0.5, y + 1.5, randZ + 0.5, 0F, 0F);
                player.setPositionAndUpdate(randX + 0.5, y + 1.5, randZ + 0.5);
            }

            return true;
        } else {
            int randX = ConfigHandler.dislocator_Min_Range
                    + rand.nextInt(ConfigHandler.dislocator_Max_Range - ConfigHandler.dislocator_Min_Range);
            int randZ = ConfigHandler.dislocator_Min_Range
                    + rand.nextInt(ConfigHandler.dislocator_Max_Range - ConfigHandler.dislocator_Min_Range);
            randX = rand.nextBoolean() ? randX * -1 : randX * 1;
            randZ = rand.nextBoolean() ? randZ * -1 : randZ * 1;
            int y = 255;
            for (int i = 255; i > 1; i--) {
                if (world.getBlock(randX, i, randZ) != Blocks.air) {
                    // LogHelper.info(world.getBlock(randX, i, randZ));
                    y = i;
                    break;
                }
            }

            if (world.getBlock(randX, y, randZ).isBlockSolid(world, randX, y, randZ, 0)) {
                world.setBlock(randX, y, randZ, world.getBlock(randX, y, randZ));
                player.setLocationAndAngles(randX + 0.5, y + 1.5, randZ + 0.5, 0F, 0F);
                player.setPositionAndUpdate(randX + 0.5, y + 1.5, randZ + 0.5);
            } else {
                world.setBlock(randX, y, randZ, Blocks.stone);
                player.setLocationAndAngles(randX + 0.5, y + 1.5, randZ + 0.5, 0F, 0F);
                player.setPositionAndUpdate(randX + 0.5, y + 1.5, randZ + 0.5);
            }
        }
        return true;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        return new ItemStack(ModBlocks.longRangeDislocator, 1, world.getBlockMetadata(x, y, z));
    }
}
