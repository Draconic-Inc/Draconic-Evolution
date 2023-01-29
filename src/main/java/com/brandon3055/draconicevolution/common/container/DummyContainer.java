package com.brandon3055.draconicevolution.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

/**
 * Created by Brandon on 16/09/2014.
 */
public class DummyContainer extends Container {

    @Override
    public boolean canInteractWith(EntityPlayer p_75145_1_) {
        return true;
    }

    @Override
    public void putStackInSlot(int p_75141_1_, ItemStack p_75141_2_) {}
}
