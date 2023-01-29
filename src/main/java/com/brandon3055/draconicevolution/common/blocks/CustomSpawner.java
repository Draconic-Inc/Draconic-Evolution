package com.brandon3055.draconicevolution.common.blocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.brandon3055.brandonscore.common.utills.InfoHelper;
import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.handler.ConfigHandler;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.tileentities.TileCustomSpawner;
import com.brandon3055.draconicevolution.common.utills.IHudDisplayBlock;
import com.brandon3055.draconicevolution.common.utills.LogHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Created by Brandon on 5/07/2014.
 */
public class CustomSpawner extends BlockDE implements IHudDisplayBlock {

    public CustomSpawner() {
        this.setBlockName(Strings.customSpawnerName);
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);
        this.setHardness(10F);
        this.setResistance(2000F);
        this.setHarvestLevel("pickaxe", 1);
        ModBlocks.register(this);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_,
            float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        TileCustomSpawner spawner = (world.getTileEntity(x, y, z) != null
                && world.getTileEntity(x, y, z) instanceof TileCustomSpawner)
                        ? (TileCustomSpawner) world.getTileEntity(x, y, z)
                        : null;
        if (spawner != null) {
            ItemStack item = player.getHeldItem();
            if (item != null && item.getItem().equals(ModItems.mobSoul)) {
                String name = ItemNBTHelper.getString(item, "Name", "Pig");
                if ((!ConfigHandler.spawnerListType && Arrays.asList(ConfigHandler.spawnerList).contains(name))
                        || (ConfigHandler.spawnerListType
                                && !Arrays.asList(ConfigHandler.spawnerList).contains(name))) {
                    if (!world.isRemote) player.addChatComponentMessage(
                            new ChatComponentText(EnumChatFormatting.RED + "[Error] soul disabled in config!"));
                    return false;
                }

                if (name.equals(spawner.getBaseLogic().entityName)) {
                    return false;
                }
                spawner.getBaseLogic().entityName = name;
                spawner.isSetToSpawn = true;
                spawner.getBaseLogic().skeletonType = ItemNBTHelper.getInteger(item, "SkeletonType", 0);
                world.markBlockForUpdate(x, y, z);
                item.splitStack(1);
                return true;
            } else
                if (item != null && item.getItem().equals(Items.nether_star) && spawner.getBaseLogic().requiresPlayer) {
                    spawner.getBaseLogic().requiresPlayer = false;
                    world.markBlockForUpdate(x, y, z);
                    item.splitStack(1);
                    return true;
                } else if (item != null && item.getItem().equals(ModItems.wyvernCore)
                        && spawner.getBaseLogic().spawnSpeed == 1) {
                            spawner.getBaseLogic().setSpawnRate(2);
                            world.markBlockForUpdate(x, y, z);
                            item.splitStack(1);
                            return true;
                        } else
                    if (item != null && item.getItem().equals(ModItems.awakenedCore)
                            && spawner.getBaseLogic().spawnSpeed == 2) {
                                spawner.getBaseLogic().setSpawnRate(3);
                                world.markBlockForUpdate(x, y, z);
                                item.splitStack(1);
                                return true;
                            } else
                        if (item != null && item.getItem().equals(Items.golden_apple)
                                && item.getItemDamage() == 1
                                && !spawner.getBaseLogic().ignoreSpawnRequirements) {
                                    spawner.getBaseLogic().ignoreSpawnRequirements = true;
                                    world.markBlockForUpdate(x, y, z);
                                    item.splitStack(1);
                                    return true;
                                } else {
                                    if (world.isRemote && !player.isSneaking()) {
                                        player.addChatMessage(
                                                new ChatComponentText(
                                                        EnumChatFormatting.GOLD + "#################################"));
                                        player.addChatMessage(
                                                new ChatComponentTranslation("msg.spawnerInfo1.txt").appendText(
                                                        ": " + EnumChatFormatting.DARK_AQUA
                                                                + spawner.getBaseLogic().entityName));
                                        player.addChatMessage(
                                                new ChatComponentTranslation("msg.spawnerInfo2.txt").appendText(
                                                        ": " + EnumChatFormatting.DARK_AQUA
                                                                + spawner.getBaseLogic().requiresPlayer));
                                        player.addChatMessage(
                                                new ChatComponentTranslation("msg.spawnerInfo3.txt").appendText(
                                                        ": " + EnumChatFormatting.DARK_AQUA
                                                                + spawner.getBaseLogic().ignoreSpawnRequirements));
                                        player.addChatMessage(
                                                new ChatComponentTranslation("msg.spawnerInfo4.txt").appendText(
                                                        ": " + EnumChatFormatting.DARK_AQUA
                                                                + spawner.getBaseLogic().spawnSpeed));
                                        player.addChatMessage(new ChatComponentTranslation("msg.spawnerInfo5.txt"));
                                        player.addChatMessage(
                                                new ChatComponentText(
                                                        EnumChatFormatting.GOLD + "#################################"));
                                    } else if (world.isRemote && player.isSneaking()) {
                                        player.addChatMessage(
                                                new ChatComponentText(
                                                        EnumChatFormatting.GOLD + "#################################"));
                                        player.addChatMessage(new ChatComponentTranslation("msg.spawnerInfo6.txt"));
                                        player.addChatMessage(new ChatComponentTranslation("msg.spawnerInfo7.txt"));
                                        player.addChatMessage(new ChatComponentTranslation("msg.spawnerInfo8.txt"));
                                        player.addChatMessage(new ChatComponentTranslation("msg.spawnerInfo9.txt"));
                                        player.addChatMessage(
                                                new ChatComponentText(
                                                        EnumChatFormatting.GOLD + "#################################"));
                                    }
                                    return true;
                                }
        }
        LogHelper.error("Invalid or nonexistent TileEntity");
        return false;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileCustomSpawner();
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
    public int getHarvestLevel(int metadata) {
        return 4;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return true;
    }

    @Override
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
        return Item.getItemFromBlock(ModBlocks.customSpawner);
    }

    @Override
    public int quantityDropped(Random p_149745_1_) {
        return 1;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon("mob_spawner");
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block p_149695_5_) {
        TileCustomSpawner spawner = (world.getTileEntity(x, y, z) != null
                && world.getTileEntity(x, y, z) instanceof TileCustomSpawner)
                        ? (TileCustomSpawner) world.getTileEntity(x, y, z)
                        : null;
        if (spawner != null) {
            spawner.getBaseLogic().powered = world.isBlockIndirectlyGettingPowered(x, y, z);
            if (spawner.getBaseLogic().powered != spawner.getBaseLogic().ltPowered) {
                spawner.getBaseLogic().ltPowered = spawner.getBaseLogic().powered;
                world.markBlockForUpdate(x, y, z);
                spawner.getBaseLogic().setSpawnRate(spawner.getBaseLogic().spawnSpeed);
            }
        }
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
        return new ItemStack(ModBlocks.customSpawner);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block p_149749_5_, int p_149749_6_) {
        TileCustomSpawner spawner = (world.getTileEntity(x, y, z) != null
                && world.getTileEntity(x, y, z) instanceof TileCustomSpawner)
                        ? (TileCustomSpawner) world.getTileEntity(x, y, z)
                        : null;
        if (spawner != null && !world.isRemote) {
            float multiplyer = 0.05F;

            if (spawner.getBaseLogic().ignoreSpawnRequirements) {
                EntityItem item = new EntityItem(
                        world,
                        x + 0.5,
                        y + 0.5,
                        z + 0.5,
                        new ItemStack(Items.golden_apple, 1, 1));
                item.motionX = (-0.5F + world.rand.nextFloat()) * multiplyer;
                item.motionY = (4 + world.rand.nextFloat()) * multiplyer;
                item.motionZ = (-0.5F + world.rand.nextFloat()) * multiplyer;
                world.spawnEntityInWorld(item);
            }
            if (spawner.getBaseLogic().spawnSpeed > 1) {
                EntityItem item = new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, new ItemStack(ModItems.wyvernCore));
                item.motionX = (-0.5F + world.rand.nextFloat()) * multiplyer;
                item.motionY = (4 + world.rand.nextFloat()) * multiplyer;
                item.motionZ = (-0.5F + world.rand.nextFloat()) * multiplyer;
                world.spawnEntityInWorld(item);
            }
            if (spawner.getBaseLogic().spawnSpeed > 2) {
                EntityItem item = new EntityItem(
                        world,
                        x + 0.5,
                        y + 0.5,
                        z + 0.5,
                        new ItemStack(ModItems.awakenedCore));
                item.motionX = (-0.5F + world.rand.nextFloat()) * multiplyer;
                item.motionY = (4 + world.rand.nextFloat()) * multiplyer;
                item.motionZ = (-0.5F + world.rand.nextFloat()) * multiplyer;
                world.spawnEntityInWorld(item);
            }
            if (!spawner.getBaseLogic().requiresPlayer) {
                EntityItem item = new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, new ItemStack(Items.nether_star));
                item.motionX = (-0.5F + world.rand.nextFloat()) * multiplyer;
                item.motionY = (4 + world.rand.nextFloat()) * multiplyer;
                item.motionZ = (-0.5F + world.rand.nextFloat()) * multiplyer;
                world.spawnEntityInWorld(item);
            }
        }
        super.breakBlock(world, x, y, z, p_149749_5_, p_149749_6_);
    }

    @Override
    public List<String> getDisplayData(World world, int x, int y, int z) {
        TileCustomSpawner spawner = (world.getTileEntity(x, y, z) != null
                && world.getTileEntity(x, y, z) instanceof TileCustomSpawner)
                        ? (TileCustomSpawner) world.getTileEntity(x, y, z)
                        : null;
        List<String> list = new ArrayList<String>();
        if (spawner != null) {
            list.add(InfoHelper.HITC() + getLocalizedName());
            if (world.isRemote && !Minecraft.getMinecraft().thePlayer.isSneaking()) {
                list.add(
                        StatCollector.translateToLocal("msg.spawnerInfo1.txt") + ": "
                                + EnumChatFormatting.DARK_AQUA
                                + spawner.getBaseLogic().entityName);
                list.add(
                        StatCollector.translateToLocal("msg.spawnerInfo2.txt") + ": "
                                + EnumChatFormatting.DARK_AQUA
                                + spawner.getBaseLogic().requiresPlayer);
                list.add(
                        StatCollector.translateToLocal("msg.spawnerInfo3.txt") + ": "
                                + EnumChatFormatting.DARK_AQUA
                                + spawner.getBaseLogic().ignoreSpawnRequirements);
                list.add(
                        StatCollector.translateToLocal("msg.spawnerInfo4.txt") + ": "
                                + EnumChatFormatting.DARK_AQUA
                                + spawner.getBaseLogic().spawnSpeed);
                list.add(StatCollector.translateToLocal("msg.spawnerInfo5.txt"));
            } else if (world.isRemote && Minecraft.getMinecraft().thePlayer.isSneaking()) {
                list.add(StatCollector.translateToLocal("msg.spawnerInfo6.txt"));
                list.add(StatCollector.translateToLocal("msg.spawnerInfo7.txt"));
                list.add(StatCollector.translateToLocal("msg.spawnerInfo8.txt"));
                list.add(StatCollector.translateToLocal("msg.spawnerInfo9.txt"));
            }
        }
        return list;
    }
}
