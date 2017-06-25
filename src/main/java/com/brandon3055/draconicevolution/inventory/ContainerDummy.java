package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.inventory.ContainerBCBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

/**
 * Created by brandon3055 on 17/10/2016.
 */
public class ContainerDummy extends ContainerBCBase {

    public ContainerDummy(TileBCBase tile, EntityPlayer player, int invX, int invY) {
        super(player, tile);
        if (invX != -1) {
            addPlayerSlots(invX, invY);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return tile instanceof IInventory ? ((IInventory) tile).isUsableByPlayer(playerIn) : tile != null;
    }
}
