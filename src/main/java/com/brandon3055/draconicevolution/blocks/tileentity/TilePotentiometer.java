package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.brandonscore.lib.IActivatableTile;
import com.brandon3055.brandonscore.lib.IRedstoneEmitter;
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

    public final ManagedByte ROTATION = register("ROTATION", new ManagedByte(0)).saveToTile().saveToItem().syncViaTile().finish();
    public final ManagedByte POWER = register("POWER", new ManagedByte(0)).saveToTile().saveToItem().syncViaTile().finish();


    @Override
    public boolean hasFastRenderer() {
        return true;
    }

    public EnumFacing getRotation() {
        return EnumFacing.getFront(ROTATION.value);
    }

    public void setRotation(EnumFacing rotation) {
        this.ROTATION.value = (byte) rotation.getIndex();
        super.update();
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
    public boolean onBlockActivated(IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
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

        if (world.isRemote) {
            ChatHelper.indexedMsg(player, POWER.toString(), -442611624);
        }
        else {
            world.playSound(null, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.5F + (POWER.value / 20F));
        }

        world.notifyNeighborsOfStateChange(pos, getBlockType(), true);
        world.notifyNeighborsOfStateChange(pos.offset(getState(DEFeatures.potentiometer).getValue(Potentiometer.FACING).getOpposite()), getBlockType(), true);
        super.update();

        return true;
    }
}
