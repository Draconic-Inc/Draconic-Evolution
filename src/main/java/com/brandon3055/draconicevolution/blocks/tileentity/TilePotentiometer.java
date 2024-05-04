package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.brandonscore.lib.IInteractTile;
import com.brandon3055.brandonscore.lib.IRedstoneEmitter;
import com.brandon3055.brandonscore.lib.datamanager.DataFlags;
import com.brandon3055.brandonscore.lib.datamanager.ManagedByte;
import com.brandon3055.brandonscore.lib.datamanager.ManagedEnum;
import com.brandon3055.draconicevolution.blocks.Potentiometer;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.UUID;

/**
 * Created by brandon3055 on 28/09/2016.
 */
public class TilePotentiometer extends TileBCore implements IRedstoneEmitter, IInteractTile {

    public static final UUID MSG_ID = UUID.fromString("8a45fb40-4316-4e4f-b435-867f44960966");
    public final ManagedEnum<Direction> rotation = register(new ManagedEnum<>("rotation", Direction.NORTH, DataFlags.SAVE_NBT_SYNC_TILE));
    public final ManagedByte power = register(new ManagedByte("power", DataFlags.SAVE_NBT_SYNC_TILE));

    public TilePotentiometer(BlockPos pos, BlockState state) {
        super(DEContent.TILE_POTENTIOMETER.get(), pos, state);
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
    public boolean onBlockActivated(BlockState state, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (player.isShiftKeyDown()) {
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

        if (level.isClientSide) {
            ChatHelper.sendIndexed(player, Component.literal(String.valueOf(power.get())), MSG_ID);
        }
        else {
            level.playSound(null, worldPosition, SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.BLOCKS, 0.3F, 0.5F + (power.get() / 20F));
        }

        level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
        level.updateNeighborsAt(worldPosition.relative(getBlockState().getValue(Potentiometer.FACING).getOpposite()), getBlockState().getBlock());
        super.tick();

        return true;
    }
}
