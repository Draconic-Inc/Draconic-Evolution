package com.brandon3055.draconicevolution.api.crafting;

import com.brandon3055.brandonscore.api.TechLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by brandon3055 on 26/11/20
 * This is simplified for 1.16+
 * Its now simply and way to access and consume the crafting ingredients (including power) and nothing more.
 * So only what is needed by IFusionRecipe
 */
public interface IFusionInventory extends RecipeInput {

    /**
     * Gets the stack in the catalyst slot of the core<br>
     */
    @Nonnull
    ItemStack getCatalystStack();

    /**
     * Gets the stack in the output slot of the core<br>
     */
    @Nonnull
    ItemStack getOutputStack();

    /**
     * Sets the stack in the catalyst slot of the core<br>
     */
    void setCatalystStack(@Nonnull ItemStack stack);

    /**
     * Sets the stack in the output slot of the core<br>
     */
    void setOutputStack(@Nonnull ItemStack stack);

    /**
     * Returns a list of all valid crafting injectors.
     */
    List<IFusionInjector> getInjectors();

    /**
     * @return the {@link TechLevel} of the lowest tier injector that is currently holding an item.
     */
    TechLevel getMinimumTier();
}
