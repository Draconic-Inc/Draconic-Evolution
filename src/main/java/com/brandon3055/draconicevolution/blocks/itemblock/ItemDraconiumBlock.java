package com.brandon3055.draconicevolution.blocks.itemblock;

import cofh.api.energy.IEnergyContainerItem;
import com.brandon3055.brandonscore.blocks.ItemBlockBasic;
import com.brandon3055.brandonscore.config.FeatureWrapper;
import com.brandon3055.brandonscore.handlers.HandHelper;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.api.IInvCharge;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Created by brandon3055 on 22/3/2016.
 */
public class ItemDraconiumBlock extends ItemBlockBasic implements IEnergyContainerItem, IInvCharge{

	public ItemDraconiumBlock(Block block) {
		super(block);
	}

	public ItemDraconiumBlock(Block block, FeatureWrapper featureWrapper){
		super(block, featureWrapper);
	}

	//region IEnergyContainerItem
	protected final int capacity = 100000000;
	protected final int maxReceive = 10000000;

	@Override
	public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
		if(container.getItemDamage() != 0 || container.stackSize <= 0) return 0;
		maxReceive /= container.stackSize;

		int energy = ItemNBTHelper.getInteger(container, "Energy", 0);
		int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));

		if (!simulate) {
			energy += energyReceived;
			ItemNBTHelper.setInteger(container, "Energy", energy);
		}
		if (getEnergyStored(container) == getMaxEnergyStored(container)) {
			container.setItemDamage(1);
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
		return ItemNBTHelper.getInteger(container, "Energy", 0);
	}

	@Override
	public int getMaxEnergyStored(ItemStack container) {
		return capacity;
	}

	@Override
	public void onUpdate(ItemStack stack, World p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
		if (stack.getItemDamage() == 0 && getEnergyStored(stack) == getMaxEnergyStored(stack)) stack.setItemDamage(1);
		super.onUpdate(stack, p_77663_2_, p_77663_3_, p_77663_4_, p_77663_5_);
	}

    //endregion

    @Override
    public boolean canCharge(ItemStack stack, EntityPlayer player) {
        return HandHelper.getItemStack(player, stack) != null && player.isSneaking();
    }

    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_) {
        if (stack.hasTagCompound()) {
            list.add(Utils.addCommas(getEnergyStored(stack)) + " / " + Utils.addCommas(getMaxEnergyStored(stack)) + "RF");
        }

        if (player.inventory.hasItemStack(DEFeatures.wyvernCapacitor) || player.inventory.hasItemStack(DEFeatures.draconicCapacitor) || player.inventory.hasItemStack(DEFeatures.creativeCapacitor)){
            list.add(I18n.translateToLocal("info.de.draconiumBlockCapacitorCharge.txt"));
        }
    }

}
