package com.brandon3055.draconicevolution.api.crafting;

import com.brandon3055.brandonscore.api.TechLevel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

/**
 * Created by brandon3055 on 9/7/21
 * The sole purpose of this interface is to provide ingredient access for the fusion crafting operation.
 * And maybe one little hook for the state machine
 */
public interface IFusionInjector {

    /**
     * @return The injector tier.
     */
    TechLevel getInjectorTier();

    /**
     * @return The stack in this injector.
     */
    ItemStack getInjectorStack();

    /**
     * Sets the stack in the injector.
     */
    void setInjectorStack(ItemStack stack);

    /**
     * @return the current energy stored in this injector
     */
    long getInjectorEnergy();

    /**
     * Sets the energy stored in this injector.
     * This is primarily used by the recipe crafting operation to consume the required energy for the craft.
     */
    void setInjectorEnergy(long energy);

    /**
     * Sets the energy requirement for this injector.
     * And the maximum energy to accept per tick.
     *
     * @param maxEnergy The new energy target for this injector.
     * @param chargeRate The max rate at which the injector should accept energy.
     */
    void setEnergyRequirement(long maxEnergy, long chargeRate);

    long getEnergyRequirement();

    /**
     * Asks this injector if it is still valid. This should be called for all injectors used in a recipe
     * as a final step before the crafting operation is completed.
     *
     * @return true if this injector is still valid.
     */
    boolean validate();
}
