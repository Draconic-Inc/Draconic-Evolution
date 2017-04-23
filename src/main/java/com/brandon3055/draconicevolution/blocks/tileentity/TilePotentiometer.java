package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.brandonscore.lib.IActivatableTile;
import com.brandon3055.brandonscore.lib.IRedstoneEmitter;
import com.brandon3055.brandonscore.network.wrappers.SyncableByte;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.Potentiometer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 28/09/2016.
 */
public class TilePotentiometer extends TileBCBase implements IRedstoneEmitter, IActivatableTile {

    public final SyncableByte ROTATION = new SyncableByte((byte)0, true, false, true);
    public final SyncableByte POWER = new SyncableByte((byte)0, true, false, true);

    public TilePotentiometer() {
        registerSyncableObject(ROTATION, true);
        registerSyncableObject(POWER, true);
    }

    @Override
    public boolean hasFastRenderer() {
        return true;
    }

    public EnumFacing getRotation() {
        return EnumFacing.getFront(ROTATION.value);
    }

    public void setRotation(EnumFacing rotation) {
        this.ROTATION.value = (byte) rotation.getIndex();
        detectAndSendChanges();
    }

    @Override
    public int getWeakPower(IBlockState blockState, EnumFacing side) {
        return POWER.value;
    }

    @Override
    public int getStrongPower(IBlockState blockState, EnumFacing side) {
        return POWER.value;
    }

    @Override
    public boolean onBlockActivated(IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (player.isSneaking()) {
            POWER.value--;
            if (POWER.value < 0) {
                POWER.value = 15;
            }
        }
        else {
            POWER.value++;
            if (POWER.value > 15) {
                POWER.value = 0;
            }
        }

        if (worldObj.isRemote) {
            ChatHelper.indexedMsg(player, POWER.toString(), -442611624);
        }
        else {
            worldObj.playSound(null, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.5F + (POWER.value / 20F));
        }

        worldObj.notifyNeighborsOfStateChange(pos, getBlockType());
        worldObj.notifyNeighborsOfStateChange(pos.offset(getState(DEFeatures.potentiometer).getValue(Potentiometer.FACING).getOpposite()), getBlockType());
        detectAndSendChanges();

        return true;
    }
}
