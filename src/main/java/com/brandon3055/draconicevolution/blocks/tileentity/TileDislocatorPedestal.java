package com.brandon3055.draconicevolution.blocks.tileentity;

import appeng.api.movable.IMovableTile;
import com.brandon3055.brandonscore.blocks.TileInventoryBase;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.brandonscore.lib.datamanager.ManagedInt;
import com.brandon3055.brandonscore.utils.InventoryUtils;
import com.brandon3055.brandonscore.utils.Teleporter.TeleportLocation;
import com.brandon3055.draconicevolution.api.ITeleportEndPoint;
import com.brandon3055.draconicevolution.handlers.DislocatorLinkHandler;
import com.brandon3055.draconicevolution.items.tools.Dislocator;
import com.brandon3055.draconicevolution.lib.DESoundHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Optional;

import static com.brandon3055.draconicevolution.DEFeatures.dislocatorBound;

/**
 * Created by brandon3055 on 27/09/2016.
 */
@Optional.Interface(modid = "appliedenergistics2", iface = "appeng.api.movable.IMovableTile")
public class TileDislocatorPedestal extends TileInventoryBase implements ITeleportEndPoint, IMovableTile {

    public final ManagedInt rotation = register("rotation", new ManagedInt(0)).saveToTile().syncViaTile().trigerUpdate().finish();

    public TileDislocatorPedestal() {
        setInventorySize(1);
    }

    public boolean onBlockActivated(EntityPlayer player) {
        if (world.isRemote) {
            return true;
        }
        ItemStack stack = getStackInSlot(0);
        if (!player.isSneaking() && !stack.isEmpty()) {
            if (stack.getItem() instanceof Dislocator) {
                TeleportLocation location = ((Dislocator) stack.getItem()).getLocation(stack, world);

                if (location == null) {
                    if (dislocatorBound.isValid(stack)) {
                        if (dislocatorBound.isPlayer(stack)) {
                            ChatHelper.translate(player, "info.de.bound_dislocator.cant_find_player", TextFormatting.RED);
                        }
                        else {
                            ChatHelper.translate(player, "info.de.bound_dislocator.cant_find_target", TextFormatting.RED);
                        }
                    }
                    return true;
                }

                if (dislocatorBound.isValid(stack)) {
                    location.setYaw(player.rotationYaw);
                    location.setPitch(player.rotationPitch);
                }

                boolean silenced = world.getBlockState(pos.down()).getBlock() == Blocks.WOOL;

                if (!silenced) {
                    DESoundHandler.playSoundFromServer(player.world, player.posX, player.posY, player.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
                }

                dislocatorBound.notifyArriving(stack, player.world, player);
                location.teleport(player);

                if (!silenced) {
                    DESoundHandler.playSoundFromServer(player.world, player.posX, player.posY, player.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
                }
            }

            return true;
        }

        InventoryUtils.handleHeldStackTransfer(0, this, player);

        //Transfer the dislocator that was in the pedestal to the players inventory
        if (dislocatorBound.isValid(stack) && !dislocatorBound.isPlayer(stack) && getStackInSlot(0).isEmpty()) {
            DislocatorLinkHandler.updateLink(world, stack, player);
        }

        checkIn();

        markDirty();
        updateBlock();

        return true;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return stack.getItem() instanceof Dislocator;
    }

    @Override
    public BlockPos getArrivalPos(String linkID) {
        if (!dislocatorBound.isValid(getStackInSlot(0)) || !dislocatorBound.getLinkID(getStackInSlot(0)).equals(linkID)) {
            return null;
        }
        return getPos();
    }

    @Override
    public void entityArriving(Entity entity) {

    }

    public void checkIn() {
        ItemStack stack = getStackInSlot(0);
        if (dislocatorBound.isValid(stack) && !dislocatorBound.isPlayer(stack)) {
            DislocatorLinkHandler.updateLink(world, stack, pos, world.provider.getDimension());
        }
    }

    @Override
    @Optional.Method(modid = "appliedenergistics2")
    public boolean prepareToMove() {
        return true;
    }

    @Override
    @Optional.Method(modid = "appliedenergistics2")
    public void doneMoving() {
        checkIn();
    }
}
