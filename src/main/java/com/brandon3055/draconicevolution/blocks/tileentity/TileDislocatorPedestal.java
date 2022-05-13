package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.inventory.TileItemStackHandler;
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
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Created by brandon3055 on 27/09/2016.
 */
public class TileDislocatorPedestal extends TileBCore implements DislocatorEndPoint, IInteractTile {
//    private static final ResourceLocation WOOL_TAG = new ResourceLocation("forge:wool");

    public final ManagedInt rotation = register(new ManagedInt("rotation", 0, DataFlags.SAVE_NBT_SYNC_TILE, DataFlags.TRIGGER_UPDATE));
    public TileItemStackHandler itemHandler = new TileItemStackHandler(1);

    public TileDislocatorPedestal(BlockPos pos, BlockState state) {
        super(DEContent.tile_dislocator_pedestal, pos, state);
        capManager.setManaged("inventory", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, itemHandler).saveBoth().syncTile();
        itemHandler.setSlotValidator(0, stack -> stack.getItem() instanceof Dislocator);
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
                            player.sendMessage(new TranslatableComponent("dislocate.draconicevolution.bound.cant_find_player").withStyle(ChatFormatting.RED), Util.NIL_UUID);
                        } else {
                            player.sendMessage(new TranslatableComponent("dislocate.draconicevolution.bound.cant_find_target").withStyle(ChatFormatting.RED), Util.NIL_UUID);
                        }
                    }

                    return InteractionResult.SUCCESS;
                }

                boolean silenced = level.getBlockState(worldPosition.below()).is(BlockTags.WOOL);

                if (!silenced) {
                    BCoreNetwork.sendSound(player.level, player.blockPosition(), DESounds.portal, SoundSource.PLAYERS, 0.1F, player.level.random.nextFloat() * 0.1F + 0.9F, false);
                }

                BoundDislocator.notifyArriving(stack, player.level, player);
                location.teleport(player);

                if (!silenced) {
                    BCoreNetwork.sendSound(player.level, player.blockPosition(), DESounds.portal, SoundSource.PLAYERS, 0.1F, player.level.random.nextFloat() * 0.1F + 0.9F, false);
                }
            }

            return InteractionResult.SUCCESS;
        }

        InventoryUtils.handleHeldStackTransfer(0, itemHandler, player);
        detectAndSendChanges();

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
