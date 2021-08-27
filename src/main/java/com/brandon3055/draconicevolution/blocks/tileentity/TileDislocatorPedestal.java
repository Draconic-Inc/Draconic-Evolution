package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.inventory.TileItemStackHandler;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.brandonscore.lib.IInteractTile;
import com.brandon3055.brandonscore.lib.datamanager.DataFlags;
import com.brandon3055.brandonscore.lib.datamanager.ManagedInt;
import com.brandon3055.brandonscore.network.BCoreNetwork;
import com.brandon3055.brandonscore.utils.InventoryUtils;
import com.brandon3055.brandonscore.utils.TargetPos;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.api.ITeleportEndPoint;
import com.brandon3055.draconicevolution.handlers.DislocatorLinkHandler;
import com.brandon3055.draconicevolution.items.tools.Dislocator;
import com.brandon3055.draconicevolution.handlers.DESounds;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.CapabilityItemHandler;

import static com.brandon3055.draconicevolution.init.DEContent.dislocator_p2p;

/**
 * Created by brandon3055 on 27/09/2016.
 */
//@Optional.Interface(modid = "appliedenergistics2", iface = "appeng.api.movable.IMovableTile")
public class TileDislocatorPedestal extends TileBCore implements ITeleportEndPoint, IInteractTile/*, IMovableTile*/ {
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
//                    if (dislocator_p2p.isValid(stack)) {
//                        if (dislocator_p2p.isPlayer(stack)) {
//                            ChatHelper.sendIndexed(player, new TranslationTextComponent("info.de.bound_dislocator.cant_find_player").withStyle(TextFormatting.RED), 34);
//                        }
//                        else {
//                            ChatHelper.sendIndexed(player, new TranslationTextComponent("info.de.bound_dislocator.cant_find_player").withStyle(TextFormatting.RED), 34);
//                        }
//                    }
                    return ActionResultType.SUCCESS;
                }

//                if (dislocator_p2p.isValid(stack)) {
//                    location.setYaw(player.yRot);
//                    location.setPitch(player.xRot);
//                }

                boolean silenced = level.getBlockState(worldPosition.below()).getBlock().getTags().contains(WOOL_TAG);

                if (!silenced) {
                    BCoreNetwork.sendSound(player.level, player.blockPosition(), DESounds.portal, SoundCategory.PLAYERS, 0.1F, player.level.random.nextFloat() * 0.1F + 0.9F, false);
                }

//                dislocator_p2p.notifyArriving(stack, player.level, player);
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
//        if (dislocator_p2p.isValid(stack) && !dislocator_p2p.isPlayer(stack) && itemHandler.getStackInSlot(0).isEmpty()) {
//            DislocatorLinkHandler.updateLink(level, stack, player);
//        }

//        checkIn();

        setChanged();
        updateBlock();

        return ActionResultType.SUCCESS;
    }

    @Override
    public BlockPos getArrivalPos(String linkID) {
        if (!dislocator_p2p.isValid(itemHandler.getStackInSlot(0)) || !dislocator_p2p.getLinkID(itemHandler.getStackInSlot(0)).equals(linkID)) {
            return null;
        }
        return getBlockPos();
    }

    @Override
    public void entityArriving(Entity entity) {

    }

    public void checkIn() {
//        ItemStack stack = itemHandler.getStackInSlot(0);
//        if (dislocator_p2p.isValid(stack) && !dislocator_p2p.isPlayer(stack)) {
//            DislocatorLinkHandler.updateLink(level, stack, worldPosition, level.dimension());
//        }
    }

//    @Override
//    @Optional.Method(modid = "appliedenergistics2")
//    public boolean prepareToMove() {
//        return true;
//    }

//    @Override
//    @Optional.Method(modid = "appliedenergistics2")
//    public void doneMoving() {
//        checkIn();
//    }
}
