package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileInventoryBase;
import com.brandon3055.brandonscore.network.wrappers.SyncableByte;
import net.minecraft.item.ItemStack;

/**
 * Created by brandon3055 on 10/06/2016.
 */
public class TileCraftingPedestal extends TileInventoryBase {

    public final SyncableByte facing = new SyncableByte((byte)0, true, false, true);

    public TileCraftingPedestal(){
        this.setInventorySize(1);
        registerSyncableObject(facing, true);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        super.setInventorySlotContents(index, stack);
        updateBlock();
    }

    @Override
    public void updateBlock() {
        super.updateBlock();
        detectAndSendChanges();
    }
}
