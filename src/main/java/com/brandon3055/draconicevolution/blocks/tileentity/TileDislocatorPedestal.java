package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.inventory.TileItemStackHandler;
import com.brandon3055.brandonscore.lib.DelayedTask;
import com.brandon3055.brandonscore.lib.IInteractTile;
import com.brandon3055.brandonscore.lib.datamanager.DataFlags;
import com.brandon3055.brandonscore.lib.datamanager.ManagedInt;
import com.brandon3055.brandonscore.network.BCoreNetwork;
import com.brandon3055.brandonscore.utils.InventoryUtils;
import com.brandon3055.brandonscore.utils.TargetPos;
import com.brandon3055.draconicevolution.api.DislocatorEndPoint;
import com.brandon3055.draconicevolution.handlers.DESounds;
import com.brandon3055.draconicevolution.handlers.dislocator.DislocatorSaveData;
import com.brandon3055.draconicevolution.handlers.dislocator.PlayerTarget;
import com.brandon3055.draconicevolution.handlers.dislocator.TileTarget;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.items.tools.BoundDislocator;
import com.brandon3055.draconicevolution.items.tools.Dislocator;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Created by brandon3055 on 27/09/2016.
 */
public class TileDislocatorPedestal extends TileBCore implements DislocatorEndPoint, IInteractTile {
//    private static final ResourceLocation WOOL_TAG = new ResourceLocation("forge:wool");

    public final ManagedInt rotation = register(new ManagedInt("rotation", 0, DataFlags.SAVE_NBT_SYNC_TILE, DataFlags.TRIGGER_UPDATE));
    public TileItemStackHandler itemHandler = new TileItemStackHandler(this, 1);

    public TileDislocatorPedestal(BlockPos pos, BlockState state) {
        super(DEContent.TILE_DISLOCATOR_PEDESTAL.get(), pos, state);
        capManager.setManaged("inventory", Capabilities.ItemHandler.BLOCK, itemHandler).saveBoth().syncTile();
        itemHandler.setSlotValidator(0, stack -> stack.getItem() instanceof Dislocator);
    }

    public static void register(RegisterCapabilitiesEvent event) {
        capability(event, DEContent.TILE_DISLOCATOR_PEDESTAL, Capabilities.ItemHandler.BLOCK);
    }

    @Override
    public InteractionResult onBlockUse(BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        ItemStack stack = itemHandler.getStackInSlot(0);
        if (!player.isShiftKeyDown() && !stack.isEmpty()) {
            if (stack.getItem() instanceof Dislocator) {
                TargetPos location = ((Dislocator) stack.getItem()).getTargetPos(stack, level);
                if (location == null) {
                    if (BoundDislocator.isValid(stack)) {
                        if (BoundDislocator.isPlayer(stack)) {
                            player.sendSystemMessage(Component.translatable("dislocate.draconicevolution.bound.cant_find_player").withStyle(ChatFormatting.RED));
                        } else {
                            player.sendSystemMessage(Component.translatable("dislocate.draconicevolution.bound.cant_find_target").withStyle(ChatFormatting.RED));
                        }
                    }

                    return InteractionResult.SUCCESS;
                }

                boolean silenced = level.getBlockState(worldPosition.below()).is(BlockTags.WOOL);

                if (!silenced) {
                    BCoreNetwork.sendSound(player.level(), player.blockPosition(), DESounds.PORTAL.get(), SoundSource.PLAYERS, 0.1F, player.level().random.nextFloat() * 0.1F + 0.9F, false);
                }

                BoundDislocator.notifyArriving(stack, player.level(), player);
                location.teleport(player);

                if (!silenced) {
                    DelayedTask.run(1, () -> BCoreNetwork.sendSound(player.level(), player.blockPosition(), DESounds.PORTAL.get(), SoundSource.PLAYERS, 0.1F, player.level().random.nextFloat() * 0.1F + 0.9F, false));
                }
            }

            return InteractionResult.SUCCESS;
        }

        InventoryUtils.handleHeldStackTransfer(0, itemHandler, player);
        detectAndSendChanges(false);

        //Transfer the dislocator that was in the pedestal to the players inventory
        if (BoundDislocator.isValid(stack) && BoundDislocator.isP2P(stack) && itemHandler.getStackInSlot(0).isEmpty()) {
            DislocatorSaveData.updateLinkTarget(level, stack, new PlayerTarget(player));
        }

        checkIn();

        setChanged();
        updateBlock();

        return InteractionResult.SUCCESS;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level instanceof ServerLevel) {
            checkIn();
        }
    }

    @Nullable
    @Override
    public Vec3 getArrivalPos(UUID linkID) {
        ItemStack stack = itemHandler.getStackInSlot(0);
        if (!BoundDislocator.isValid(stack) || !linkID.equals(BoundDislocator.getLinkId(stack))) {
            return null;
        }
        BlockPos pos = getBlockPos();
        return new Vec3(pos.getX() + 0.5, pos.getY() + 0.25, pos.getZ() + 0.5);
    }

    public void checkIn() {
        ItemStack stack = itemHandler.getStackInSlot(0);
        if (BoundDislocator.isValid(stack) && BoundDislocator.isP2P(stack)) {
            DislocatorSaveData.updateLinkTarget(level, stack, new TileTarget(this));
        }
    }

}
