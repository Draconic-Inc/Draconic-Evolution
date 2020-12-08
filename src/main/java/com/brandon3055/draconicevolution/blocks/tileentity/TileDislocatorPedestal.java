package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.inventory.TileItemStackHandler;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.brandonscore.lib.datamanager.DataFlags;
import com.brandon3055.brandonscore.lib.datamanager.ManagedInt;
import com.brandon3055.brandonscore.utils.InventoryUtils;
import com.brandon3055.brandonscore.utils.Teleporter.TeleportLocation;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.api.ITeleportEndPoint;
import com.brandon3055.draconicevolution.handlers.DislocatorLinkHandler;
import com.brandon3055.draconicevolution.items.tools.Dislocator;
import com.brandon3055.draconicevolution.handlers.DESoundHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.items.CapabilityItemHandler;

import static com.brandon3055.draconicevolution.init.DEContent.dislocator_p2p;

/**
 * Created by brandon3055 on 27/09/2016.
 */
//@Optional.Interface(modid = "appliedenergistics2", iface = "appeng.api.movable.IMovableTile")
public class TileDislocatorPedestal extends TileBCore implements ITeleportEndPoint/*, IMovableTile*/ {
    private static final ResourceLocation WOOL_TAG = new ResourceLocation("forge:wool");

    public final ManagedInt rotation = register(new ManagedInt("rotation", 0, DataFlags.SAVE_BOTH_SYNC_TILE, DataFlags.TRIGGER_UPDATE));
    public TileItemStackHandler itemHandler = new TileItemStackHandler(1);

    public TileDislocatorPedestal() {
        super(DEContent.tile_dislocator_pedestal);
        capManager.setManaged("inventory", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, itemHandler);
        itemHandler.setSlotValidator(0, stack -> stack.getItem() instanceof Dislocator);
    }

    public boolean onBlockActivated(PlayerEntity player) {
        if (world.isRemote) {
            return true;
        }
        ItemStack stack = itemHandler.getStackInSlot(0);
        if (!player.isSneaking() && !stack.isEmpty()) {
            if (stack.getItem() instanceof Dislocator) {
                TeleportLocation location = ((Dislocator) stack.getItem()).getLocation(stack, world);

                if (location == null) {
                    if (dislocator_p2p.isValid(stack)) {
                        if (dislocator_p2p.isPlayer(stack)) {
                            ChatHelper.translate(player, "info.de.bound_dislocator.cant_find_player", TextFormatting.RED);
                        }
                        else {
                            ChatHelper.translate(player, "info.de.bound_dislocator.cant_find_target", TextFormatting.RED);
                        }
                    }
                    return true;
                }

                if (dislocator_p2p.isValid(stack)) {
                    location.setYaw(player.rotationYaw);
                    location.setPitch(player.rotationPitch);
                }

                boolean silenced = world.getBlockState(pos.down()).getBlock().getTags().contains(WOOL_TAG);

                if (!silenced) {
                    DESoundHandler.playSoundFromServer(player.world, player.getPosX(), player.getPosY(), player.getPosZ(), DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
                }

                dislocator_p2p.notifyArriving(stack, player.world, player);
                location.teleport(player);

                if (!silenced) {
                    DESoundHandler.playSoundFromServer(player.world, player.getPosX(), player.getPosY(), player.getPosZ(), DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
                }
            }

            return true;
        }

        InventoryUtils.handleHeldStackTransfer(0, itemHandler, player);

        //Transfer the dislocator that was in the pedestal to the players inventory
        if (dislocator_p2p.isValid(stack) && !dislocator_p2p.isPlayer(stack) && itemHandler.getStackInSlot(0).isEmpty()) {
            DislocatorLinkHandler.updateLink(world, stack, player);
        }

        checkIn();

        markDirty();
        updateBlock();

        return true;
    }

    @Override
    public BlockPos getArrivalPos(String linkID) {
        if (!dislocator_p2p.isValid(itemHandler.getStackInSlot(0)) || !dislocator_p2p.getLinkID(itemHandler.getStackInSlot(0)).equals(linkID)) {
            return null;
        }
        return getPos();
    }

    @Override
    public void entityArriving(Entity entity) {

    }

    public void checkIn() {
        ItemStack stack = itemHandler.getStackInSlot(0);
        if (dislocator_p2p.isValid(stack) && !dislocator_p2p.isPlayer(stack)) {
            DislocatorLinkHandler.updateLink(world, stack, pos, world.getDimensionKey());
        }
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
