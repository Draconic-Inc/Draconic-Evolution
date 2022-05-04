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
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Created by brandon3055 on 27/09/2016.
 */
public class TileDislocatorPedestal extends TileBCore implements DislocatorEndPoint, IInteractTile {
    private static final ResourceLocation WOOL_TAG = new ResourceLocation("forge:wool");

    public final ManagedInt rotation = register(new ManagedInt("rotation", 0, DataFlags.SAVE_NBT_SYNC_TILE, DataFlags.TRIGGER_UPDATE));
    public TileItemStackHandler itemHandler = new TileItemStackHandler(1);

    public TileDislocatorPedestal() {
        super(DEContent.tile_dislocator_pedestal);
        capManager.setManaged("inventory", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, itemHandler).saveBoth().syncTile();
        itemHandler.setSlotValidator(0, stack -> stack.getItem() instanceof Dislocator);
    }

    @Override
    public ActionResultType onBlockUse(BlockState state, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (level.isClientSide) {
            return ActionResultType.SUCCESS;
        }
        ItemStack stack = itemHandler.getStackInSlot(0);
        if (!player.isShiftKeyDown() && !stack.isEmpty()) {
            if (stack.getItem() instanceof Dislocator) {
                TargetPos location = ((Dislocator) stack.getItem()).getTargetPos(stack, level);
                if (location == null) {
                    if (BoundDislocator.isValid(stack)) {
                        if (BoundDislocator.isPlayer(stack)) {
                            player.sendMessage(new TranslationTextComponent("dislocate.draconicevolution.bound.cant_find_player").withStyle(TextFormatting.RED), Util.NIL_UUID);
                        } else {
                            player.sendMessage(new TranslationTextComponent("dislocate.draconicevolution.bound.cant_find_target").withStyle(TextFormatting.RED), Util.NIL_UUID);
                        }
                    }

                    return ActionResultType.SUCCESS;
                }

                boolean silenced = level.getBlockState(worldPosition.below()).getBlock().getTags().contains(WOOL_TAG);

                if (!silenced) {
                    BCoreNetwork.sendSound(player.level, player.blockPosition(), DESounds.portal, SoundCategory.PLAYERS, 0.1F, player.level.random.nextFloat() * 0.1F + 0.9F, false);
                }

                BoundDislocator.notifyArriving(stack, player.level, player);
                location.teleport(player);

                if (!silenced) {
                    BCoreNetwork.sendSound(player.level, player.blockPosition(), DESounds.portal, SoundCategory.PLAYERS, 0.1F, player.level.random.nextFloat() * 0.1F + 0.9F, false);
                }
            }

            return ActionResultType.SUCCESS;
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

        return ActionResultType.SUCCESS;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level instanceof ServerWorld) {
            checkIn();
        }
    }

    @Override
    public void setLevelAndPosition(World world, BlockPos pos) {
        super.setLevelAndPosition(world, pos);
    }

    @Nullable
    @Override
    public Vector3d getArrivalPos(UUID linkID) {
        ItemStack stack = itemHandler.getStackInSlot(0);
        if (!BoundDislocator.isValid(stack) || !linkID.equals(BoundDislocator.getLinkId(stack))) {
            return null;
        }
        BlockPos pos = getBlockPos();
        return new Vector3d(pos.getX() + 0.5, pos.getY() + 0.25, pos.getZ() + 0.5);
    }

    public void checkIn() {
        ItemStack stack = itemHandler.getStackInSlot(0);
        if (BoundDislocator.isValid(stack) && BoundDislocator.isP2P(stack)) {
            DislocatorSaveData.updateLinkTarget(level, stack, new TileTarget(this));
        }
    }

}
