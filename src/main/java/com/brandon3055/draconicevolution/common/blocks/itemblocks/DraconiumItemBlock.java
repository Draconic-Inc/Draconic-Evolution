package com.brandon3055.draconicevolution.common.blocks.itemblocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import cofh.api.energy.IEnergyContainerItem;

import com.brandon3055.draconicevolution.common.handler.BalanceConfigHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Created by Brandon on 8/08/2014.
 */
public class DraconiumItemBlock extends ItemBlock implements IEnergyContainerItem {

    public DraconiumItemBlock(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    protected int capacity = BalanceConfigHandler.draconiumBlockEnergyToChange;
    protected int maxReceive = BalanceConfigHandler.draconiumBlockChargingSpeed;
    protected int maxExtract = BalanceConfigHandler.draconiumBlockChargingSpeed;

    @Override
    public void getSubItems(Item item, CreativeTabs p_150895_2_, List list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
        list.add(new ItemStack(item, 1, 2));
    }

    @Override
    public int getMetadata(int par1) {
        return par1;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + stack.getItemDamage();
    }

    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        if (container.getItemDamage() != 0 || container.stackSize <= 0) return 0;
        maxReceive /= container.stackSize;
        if (container.stackTagCompound == null) {
            container.stackTagCompound = new NBTTagCompound();
        }
        int energy = container.stackTagCompound.getInteger("Energy");
        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));

        if (!simulate) {
            energy += energyReceived;
            container.stackTagCompound.setInteger("Energy", energy);
        }
        if (getEnergyStored(container) == getMaxEnergyStored(container)) {
            container.setItemDamage(2);
            container.setTagCompound(null);
        }
        return energyReceived * container.stackSize;
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored(ItemStack container) {
        if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Energy")) {
            return 0;
        }
        return container.stackTagCompound.getInteger("Energy");
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return capacity;
    }

    @Override
    public void onUpdate(ItemStack stack, World p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
        if (stack.getItemDamage() == 0 && getEnergyStored(stack) == getMaxEnergyStored(stack)) stack.setItemDamage(2);
        super.onUpdate(stack, p_77663_2_, p_77663_3_, p_77663_4_, p_77663_5_);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer p_77624_2_, List list, boolean p_77624_4_) {
        if (stack.hasTagCompound()) list.add(getEnergyStored(stack) + " / " + getMaxEnergyStored(stack) + "RF");
    }
}
