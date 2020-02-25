package com.brandon3055.draconicevolution.blocks.itemblock;

import com.brandon3055.brandonscore.blocks.ItemBlockBCore;
import com.brandon3055.draconicevolution.DEContent;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.client.resources.I18n.format;

/**
 * Created by brandon3055 on 22/3/2016.
 */
public class ItemDraconiumBlock extends ItemBlockBCore {

    public ItemDraconiumBlock(Block block) {
        super(block, null);
    }

    //region IEnergyContainerItem
    protected final int capacity = 100000000;
    protected final int maxReceive = 10000000;

//	@Override
//	public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
//		if(container.getItemDamage() != 0 || container.stackSize <= 0) return 0;
//		maxReceive /= container.stackSize;
//
//		int energy = ItemNBTHelper.getInteger(container, "Energy", 0);
//		int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
//
//		if (!simulate) {
//			energy += energyReceived;
//			ItemNBTHelper.setInteger(container, "Energy", energy);
//		}
//		if (getEnergyStored(container) == getMaxEnergyStored(container)) {
//			container.setItemDamage(1);
//			container.setTag(null);
//		}
//		return energyReceived * container.stackSize;
//	}
//
//	@Override
//	public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
//		return 0;
//	}
//
//	@Override
//	public int getEnergyStored(ItemStack container) {
//		return ItemNBTHelper.getInteger(container, "Energy", 0);
//	}
//
//	@Override
//	public int getMaxEnergyStored(ItemStack container) {
//		return capacity;
//	}

//	@Override
//	public void onUpdate(ItemStack stack, World p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
//		if (stack.getItemDamage() == 0 && getEnergyStored(stack) == getMaxEnergyStored(stack)) stack.setItemDamage(1);
//		super.onUpdate(stack, p_77663_2_, p_77663_3_, p_77663_4_, p_77663_5_);
//	}

    //endregion

//    @Override
//    public boolean canCharge(ItemStack stack, PlayerEntity player) {
//        return HandHelper.getItemStack(player, stack) != null && player.isSneaking();
//    }

    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("unchecked")
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List list, ITooltipFlag p_77624_4_) {
//        if (stack.hasTagCompound()) {
//            list.add(Utils.addCommas(getEnergyStored(stack)) + " / " + Utils.addCommas(getMaxEnergyStored(stack)) + "RF");
//        }
        PlayerEntity player = Minecraft.getInstance().player;


//        if (player != null && (Minecraft.getInstance().player.inventory.hasItemStack(DEContent.wyvernCapacitor) || player.inventory.hasItemStack(DEContent.draconicCapacitor) || player.inventory.hasItemStack(DEContent.creativeCapacitor))) {
//            list.add(format("info.de.draconiumBlockCapacitorCharge.txt"));
//        }
    }

}
