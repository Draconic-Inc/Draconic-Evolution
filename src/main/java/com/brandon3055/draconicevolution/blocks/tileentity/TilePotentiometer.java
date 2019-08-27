package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.brandonscore.lib.IActivatableTile;
import com.brandon3055.brandonscore.lib.IRedstoneEmitter;
import com.brandon3055.brandonscore.lib.datamanager.DataFlags;
import com.brandon3055.brandonscore.lib.datamanager.ManagedByte;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.Potentiometer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;

/**
 * Created by brandon3055 on 28/09/2016.
 */
public class TilePotentiometer extends TileBCBase implements IRedstoneEmitter, IActivatableTile {

    public final ManagedByte rotation = register(new ManagedByte("rotation", DataFlags.SAVE_BOTH_SYNC_TILE));
    public final ManagedByte power = register(new ManagedByte("power", DataFlags.SAVE_BOTH_SYNC_TILE));

    @Override
    public boolean hasFastRenderer() {
        return true;
    }

    public EnumFacing getRotation() {
        return EnumFacing.getFront(rotation.get());
    }

    public void setRotation(EnumFacing rotation) {
        this.rotation.set((byte) rotation.getIndex());
        super.update();
    }

    @Override
    public int getWeakPower(IBlockState blockState, EnumFacing side) {
        return power.get();
    }

    @Override
    public int getStrongPower(IBlockState blockState, EnumFacing side) {
        return power.get();
    }

    @Override
    public boolean onBlockActivated(IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (player.isSneaking()) {
            power.dec();
            if (power.get() < 0) {
                power.set((byte) 15);
            }
        }
        else {
            power.inc();
            if (power.get() > 15) {
                power.zero();
            }
        }

        if (world.isRemote) {
            ChatHelper.indexedMsg(player, power.toString());
        }
        else {
            world.playSound(null, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.5F + (power.get() / 20F));
        }

        world.notifyNeighborsOfStateChange(pos, getBlockType(), true);
        world.notifyNeighborsOfStateChange(pos.offset(getState(DEFeatures.potentiometer).getValue(Potentiometer.FACING).getOpposite()), getBlockType(), true);
        super.update();

        return true;
    }
}
