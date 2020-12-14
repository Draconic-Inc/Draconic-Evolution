package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.brandonscore.lib.IActivatableTile;
import com.brandon3055.brandonscore.lib.IRedstoneEmitter;
import com.brandon3055.brandonscore.lib.datamanager.DataFlags;
import com.brandon3055.brandonscore.lib.datamanager.ManagedByte;
import com.brandon3055.brandonscore.lib.datamanager.ManagedEnum;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.blocks.Potentiometer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockRayTraceResult;

/**
 * Created by brandon3055 on 28/09/2016.
 */
public class TilePotentiometer extends TileBCore implements IRedstoneEmitter, IActivatableTile {

    public final ManagedEnum<Direction> rotation = register(new ManagedEnum<>("rotation", Direction.NORTH, DataFlags.SAVE_NBT_SYNC_TILE));
    public final ManagedByte power = register(new ManagedByte("power", DataFlags.SAVE_NBT_SYNC_TILE));

    public TilePotentiometer() {
        super(DEContent.tile_potentiometer);
    }

    public Direction getRotation() {
        return rotation.get();
    }

    public void setRotation(Direction rotation) {
        this.rotation.set(rotation);
        super.tick();
    }

    @Override
    public int getWeakPower(BlockState blockState, Direction side) {
        return power.get();
    }

    @Override
    public int getStrongPower(BlockState blockState, Direction side) {
        return power.get();
    }

    @Override
    public boolean onBlockActivated(BlockState state, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
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
            ChatHelper.indexedMsg(player, String.valueOf(power.get()));
        }
        else {
            world.playSound(null, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.5F + (power.get() / 20F));
        }

        world.notifyNeighborsOfStateChange(pos, getBlockState().getBlock());
        world.notifyNeighborsOfStateChange(pos.offset(getBlockState().get(Potentiometer.FACING).getOpposite()), getBlockState().getBlock());
        super.tick();

        return true;
    }
}
