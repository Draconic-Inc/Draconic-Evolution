package com.brandon3055.draconicevolution.items.armor;

import cofh.redstoneflux.api.IEnergyContainerItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

/**
 * Created by brandon3055 on 5/2/2016.
 * //TODO (DE3) Split this up into separate interfaces e.g. IShieldProvider
 * //Also add methods to modify things like protection points because that should be handled by the item not CustomArmorHandler
 */
public interface ICustomArmor extends IEnergyContainerItem {

    /**
     * Returns the max number of protection points this armor piece can provide
     * 1 protection point equals 1 half heart of protection.
     */
    float getProtectionPoints(ItemStack stack);

    /**
     * Used to specify the rate at which the shields entropy should recover.
     *
     * @param stack The stack
     * @return the rate at which the entropy will be reduced. The number returned is n% over 5 seconds.
     */
    float getRecoveryRate(ItemStack stack);

    /**
     * Returns the movement speed modifier for this armor
     */
    float getSpeedModifier(ItemStack stack, PlayerEntity player);

    /**
     * Returns the jump ySize modifier for this armor
     */
    float getJumpModifier(ItemStack stack, PlayerEntity player);

    /**
     * Returns true if this armor has up-hill step enabled
     */
    boolean hasHillStep(ItemStack stack, PlayerEntity player);

    /**
     * Returns the fire resistance modifier for this armor
     * This is the percentage of fire damage this armor should absorb
     * If the total resistance for all armor pieces the player is wearing is >= 1 the player will not be damaged by fire.
     * Should return a number between 0 and 1
     */
    float getFireResistance(ItemStack stack);

    /**
     * Returns an array of 3 booleans.
     * The first allows you to enable or disable flight
     * The second allows you to enable or disable flight lock
     * And the second allows you to enable or disable inertia cancellation
     *///TODO (DE3) make this more sane
    boolean[] hasFlight(ItemStack stack);

    float getFlightSpeedModifier(ItemStack stack, PlayerEntity player);

    /**
     * Returns the vertical acceleration speed
     */
    float getFlightVModifier(ItemStack stack, PlayerEntity player);

    /**
     * Returns amount of energy, required to restore protection point
     */
    int getEnergyPerProtectionPoint();

    /**
     * Used to modify the energy stored within the item.
     *
     * @param stack  the stack
     * @param amount the amount to be added to the items storage (should have a min/max check)
     *               This value could be positive or negative.
     */
    void modifyEnergy(ItemStack stack, int amount);
}
