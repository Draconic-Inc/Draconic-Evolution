package com.brandon3055.draconicevolution.common.items.armor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import cofh.api.energy.IEnergyContainerItem;

/**
 * Created by brandon3055 on 5/2/2016.
 */
public interface ICustomArmor extends IEnergyContainerItem {

    /**
     * Returns the max number of protection points this armor piece can provide 1 protection point equals 1 half heart
     * of protection.
     */
    float getProtectionPoints(ItemStack stack);

    /** Returns the number of Upgrade points applied to shield recovery */
    int getRecoveryPoints(ItemStack stack);

    /** Returns the movement speed modifier for this armor */
    float getSpeedModifier(ItemStack stack, EntityPlayer player);

    /** Returns the jump height modifier for this armor */
    float getJumpModifier(ItemStack stack, EntityPlayer player);

    /** Returns true if this armor has up-hill step enabled */
    boolean hasHillStep(ItemStack stack, EntityPlayer player);

    /**
     * Returns the fire resistance modifier for this armor This is the percentage of fire damage this armor should
     * absorb If the total resistance for all armor peaces the player is wearing is >= 1 the player will not be damaged
     * by fire. Should return a number between 0 and 1
     */
    float getFireResistance(ItemStack stack);

    /**
     * Returns {true, false} if this armor gives the player flight Returns {true, true} if flight lock is also enables
     * on this armor
     */
    boolean[] hasFlight(ItemStack stack);

    float getFlightSpeedModifier(ItemStack stack, EntityPlayer player);

    /** Returns the vertical acceleration speed */
    float getFlightVModifier(ItemStack stack, EntityPlayer player);

    /** Returns amount of energy, required to restore protection point */
    int getEnergyPerProtectionPoint();
}
