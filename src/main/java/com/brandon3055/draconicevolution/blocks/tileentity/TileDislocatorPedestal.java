package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileInventoryBase;
import com.brandon3055.brandonscore.network.wrappers.SyncableInt;
import com.brandon3055.brandonscore.utils.InventoryUtils;
import com.brandon3055.brandonscore.utils.Teleporter;
import com.brandon3055.draconicevolution.items.tools.Dislocator;
import com.brandon3055.draconicevolution.lib.DESoundHandler;
import com.google.common.base.Predicate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 27/09/2016.
 */
public class TileDislocatorPedestal extends TileInventoryBase {

    public final SyncableInt rotation = new SyncableInt(0, true, false, true);

    public TileDislocatorPedestal() {
        registerSyncableObject(rotation, true);
        setInventorySize(1);
    }

    public boolean onBlockActivated(EntityPlayer player) {
        if (worldObj.isRemote) {
            return true;
        }
        ItemStack stack = getStackInSlot(0);
        if (stack != null && !player.isSneaking()) {
            if (stack.getItem() instanceof Dislocator) {
                Teleporter.TeleportLocation location = ((Dislocator) stack.getItem()).getLocation(stack);

                if (location == null) {
                    return true;
                }

                boolean silenced = worldObj.getBlockState(pos.down()).getBlock() == Blocks.WOOL;

                if (!silenced) {
                    DESoundHandler.playSoundFromServer(player.worldObj, player.posX, player.posY, player.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.worldObj.rand.nextFloat() * 0.1F + 0.9F, false, 32);
                }

                ((Dislocator) stack.getItem()).getLocation(stack).teleport(player);

                if (!silenced) {
                    DESoundHandler.playSoundFromServer(player.worldObj, player.posX, player.posY, player.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.worldObj.rand.nextFloat() * 0.1F + 0.9F, false, 32);
                }
            }

            return true;
        }

        InventoryUtils.handleAddOrTakeStack(0, this, player, new Predicate<ItemStack>() {
            @Override
            public boolean apply(@Nullable ItemStack input) {
                return input != null && input.getItem() instanceof Dislocator;
            }
        });

        updateBlock();

        return true;
    }

 }
