package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileInventoryBase;
import com.brandon3055.brandonscore.lib.datamanager.ManagedInt;
import com.brandon3055.brandonscore.utils.InventoryUtils;
import com.brandon3055.brandonscore.utils.Teleporter;
import com.brandon3055.draconicevolution.items.tools.Dislocator;
import com.brandon3055.draconicevolution.lib.DESoundHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;

/**
 * Created by brandon3055 on 27/09/2016.
 */
public class TileDislocatorPedestal extends TileInventoryBase {

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
                Teleporter.TeleportLocation location = ((Dislocator) stack.getItem()).getLocation(stack);

                if (location == null) {
                    return true;
                }

                boolean silenced = world.getBlockState(pos.down()).getBlock() == Blocks.WOOL;

                if (!silenced) {
                    DESoundHandler.playSoundFromServer(player.world, player.posX, player.posY, player.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
                }

                ((Dislocator) stack.getItem()).getLocation(stack).teleport(player);

                if (!silenced) {
                    DESoundHandler.playSoundFromServer(player.world, player.posX, player.posY, player.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
                }
            }

            return true;
        }

        InventoryUtils.handleHeldStackTransfer(0, this, player);
        markDirty();
        updateBlock();

        return true;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return stack.getItem() instanceof Dislocator;
    }
}
