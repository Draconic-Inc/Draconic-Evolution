package com.brandon3055.draconicevolution.common.items.tools;

import java.util.List;
import java.util.Random;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.items.ItemDE;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.tileentities.TileChaosShard;
import com.brandon3055.draconicevolution.common.world.WorldGenEnderComet;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Created by Brandon on 29/08/2014.
 */
public class CreativeStructureSpawner extends ItemDE {

    public CreativeStructureSpawner() {
        this.setUnlocalizedName(Strings.creativeStructureSpawnerName);
        this.hasSubtypes = true;
        ModItems.register(this);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item item, CreativeTabs p_150895_2_, List list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote) return stack;

        switch (stack.getItemDamage()) {
            case 0:
                new WorldGenEnderComet()
                        .generate(world, new Random(), (int) player.posX, (int) player.posY + 10, (int) player.posZ);
                break;
            case 1:
                // new WorldGenEnderIsland().generate(world, new Random(), (int)player.posX, (int)player.posY + 10,
                // (int)player.posZ);
                break;
            case 2:
                break;
            case 3:
                break;
        }
        return stack;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int p_77648_7_,
            float p_77648_8_, float p_77648_9_, float p_77648_10_) {

        if (world.isRemote) return false;

        switch (stack.getItemDamage()) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                world.setBlock(x, y, z, ModBlocks.chaosCrystal);
                TileChaosShard tileChaosShard = (TileChaosShard) world.getTileEntity(x, y, z);
                tileChaosShard.locationHash = tileChaosShard.getLocationHash(x, y, z, player.dimension);
                tileChaosShard.guardianDefeated = true;
                world.markBlockForUpdate(x, y, z);
                break;
            case 3:
                break;
        }
        return false;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player) {
        if (player.worldObj.getBlock(x, y, z) == ModBlocks.chaosCrystal) {
            player.worldObj.removeTileEntity(x, y, z);
            player.worldObj.setBlockToAir(x, y, z);
        }
        return true;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        String name = "null";
        switch (itemStack.getItemDamage()) {
            case 0:
                name = "Comet";
                break;
            case 1:
                name = "EnderIsland";
                break;
            case 2:
                name = "ChaosCrystal";
                break;
            case 3:
                break;
        }
        return super.getUnlocalizedName(itemStack) + name;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer p_77624_2_, List list, boolean p_77624_4_) {
        if (stack.getItemDamage() == 1)
            list.add("This nolonger works due to some changes to the island generation code");
        String name = "null";
        switch (stack.getItemDamage()) {
            case 0:
                name = "Comet";
                break;
            case 1:
                name = "EnderIsland";
                break;
            case 2:
                name = "ChaosCrystal";
                break;
            case 3:
                break;
        }
        list.add("Spawns: " + name);
        list.add("This item can safely delete chaos crystals");
    }
}
