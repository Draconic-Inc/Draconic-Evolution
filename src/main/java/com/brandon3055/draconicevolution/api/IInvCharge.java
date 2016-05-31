package com.brandon3055.draconicevolution.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Created by brandon3055 on 31/05/2016.
 * Can be implemented by IEnergyContainerItems to control weather or not they can be charged by a DE Capacitor
 */
public interface IInvCharge {
    boolean canCharge(ItemStack stack, EntityPlayer player);
}
