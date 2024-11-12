package com.brandon3055.draconicevolution.api.crafting;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.brandon3055.draconicevolution.api.crafting.IFusionStateMachine.FusionState;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by brandon3055 on 26/11/20
 */
public interface IFusionRecipe extends Recipe<IFusionInventory> {

    @Override
    default ItemStack getToastSymbol() {
        if (DraconicAPI.CRAFTING_CORE != null) {
            return new ItemStack(DraconicAPI.CRAFTING_CORE);
        } else {
            return new ItemStack(Blocks.CRAFTING_TABLE);
        }
    }

    @Override
    default RecipeType<?> getType() {
        return DraconicAPI.FUSION_RECIPE_TYPE.get();
    }

    TechLevel getRecipeTier();

    long getEnergyCost();

    /**
     * This method is only used to maintain compatibility with the IRecipe spec.
     * The actual crafting operation is handled via {@link #fusionIngredients()}
     *
     * @return A list of recipe ingredients NOT including the catalyst.
     */
    @Override
    default NonNullList<Ingredient> getIngredients() {
        return fusionIngredients().stream().map(IFusionIngredient::get).collect(Collectors.toCollection(NonNullList::create));
    }

    /**
     * Returns the same list of ingredients as {@link #getIngredients()} but wrapped in {@link IFusionIngredient}
     * which allows the ingredients to have control over whether or not they are consumed.
     *
     * @return A list of fusion ingredients.
     */
    List<IFusionIngredient> fusionIngredients();

    /**
     * @return The catalyst ingredient required by this recipe.
     */
    Ingredient getCatalyst();

    /**
     * Returns true if the ingredients in the inventory match this recipe.
     * This should be treated as a preliminary match used to lookup a recipe for a given set of ingredients.
     * It does not take into account the energy or crafting tier requirements.
     *
     * @param inv   The fusion crafting inventory.
     * @param level the world
     */
    @Override
    default boolean matches(IFusionInventory inv, Level level) {
        if (!getCatalyst().test(inv.getCatalystStack())) {
            return false;
        }

        List<IFusionInjector> injectors = new ArrayList<>(inv.getInjectors());
        for (Ingredient ingredient : getIngredients()) {
            IFusionInjector match = injectors.stream()
                    .filter(e -> ingredient.test(e.getInjectorStack()))
                    .findFirst()
                    .orElse(null);

            if (match == null) {
                return false;
            }
            injectors.remove(match);
        }

        return injectors.stream().allMatch(e -> e.getInjectorStack().isEmpty());
    }

    /**
     * So this is where the fusion magic happens! Fusion recipes are now much more powerful in 1.16+ because the
     * recipe itself now has almost total control over almost every aspect of the crafting operation.
     * This is via this tick method and the supplied state machine which can be used to manage the crafting process.
     * The default implementation is for a standard fusion craft but this can be overridden by custom recipes to do pretty much anything yo can think of.
     * Want a recipe that builds a physical structure in the world? Go for it!
     * Want a perpetual fusion process that runs indefinitely consuming power and providing some effect or bonus? No problem!
     * Want to make a fusion recipe that on completion teleports all nearby players into another dimension? Why not!
     *
     * @param stateMachine The fusion crafting state machine
     * @param inv          The fusion inventory
     * @param level        The world
     */
    default void tickFusionState(IFusionStateMachine stateMachine, IFusionInventory inv, Level level) {
        switch (stateMachine.getFusionState()) {
            case START:
                //<ake sure the recipe is valid. It should be but just in case.
                if (!matches(inv, level)) {
                    stateMachine.cancelCraft();
                    return;
                }
                //Calculate per item energy cost and tell the injectors to start charging
                long itemEnergy = (getEnergyCost() / fusionIngredients().size()) + inv.getInjectors().size(); //To avoid issues from rounding
                inv.getInjectors()
                        .stream()
                        .filter(e -> !e.getInjectorStack().isEmpty())
                        .forEach(e -> e.setEnergyRequirement(itemEnergy, itemEnergy / DEConfig.fusionChargeTime.get(e.getInjectorTier().index)));
                //Update Progress
                stateMachine.setFusionStatus(0, Component.translatable("fusion_status.draconicevolution.charging", 0).withStyle(ChatFormatting.GREEN));
                //Progress to next state
                stateMachine.setFusionState(FusionState.CHARGING);
                stateMachine.setCraftAnimation(0, 0);
                break;
            case CHARGING:
                long totalCharge = inv.getInjectors()
                        .stream()
                        .mapToLong(IFusionInjector::getInjectorEnergy)
                        .sum();
                stateMachine.setFusionStatus((double) totalCharge / getEnergyCost(), Component.translatable("fusion_status.draconicevolution.charging", Math.round(((double) totalCharge / getEnergyCost()) * 1000) / 10D).withStyle(ChatFormatting.GREEN));
                if (totalCharge >= getEnergyCost()) {
                    stateMachine.setFusionStatus(0, Component.translatable("fusion_status.draconicevolution.crafting", 0).withStyle(ChatFormatting.GREEN));
                    stateMachine.setCounter(0);
                    //Proceed to next step
                    int craftTime = DEConfig.fusionCraftTime.get(inv.getMinimumTier().index);
                    stateMachine.setCraftAnimation(0, craftTime); //Craft time needs to be set before state transition for rendering consistency.
                    stateMachine.setFusionState(FusionState.CRAFTING);
                }
                break;
            case CRAFTING:
                int craftTime = DEConfig.fusionCraftTime.get(inv.getMinimumTier().index);
                int counter = stateMachine.getCounter();
                stateMachine.setCraftAnimation(counter / (float)craftTime, craftTime);
                stateMachine.setCounter(counter + 1);
                stateMachine.setFusionStatus((double) counter / (double) craftTime, Component.translatable("fusion_status.draconicevolution.crafting", Math.round(((double) counter / (double) craftTime) * 1000) / 10D).withStyle(ChatFormatting.GREEN));

                if (counter >= craftTime) {
                    if (inv.getInjectors().stream().anyMatch(e -> !e.validate())) {
                        stateMachine.cancelCraft();
                        return;
                    }
                    if (!matches(inv, level) || !canStartCraft(inv, level, null)) {
                        stateMachine.cancelCraft();
                        return;
                    }
                    completeCraft(inv, level, this);
                    stateMachine.completeCraft();
                }
                break;
        }
    }

    static void completeCraft(IFusionInventory inv, Level level, IFusionRecipe recipe) {
        List<IFusionInjector> injectors = new ArrayList<>(inv.getInjectors());
        for (IFusionIngredient ingredient : recipe.fusionIngredients()) {
            for (IFusionInjector injector : injectors) {
                ItemStack stack = injector.getInjectorStack();
                if (!stack.isEmpty() && ingredient.get().test(stack) && injector.getInjectorTier().index >= recipe.getRecipeTier().index) {
                    if (!ingredient.consume()) {
                        break;
                    }
                    if (stack.hasCraftingRemainingItem()) {
                        stack = stack.getItem().getCraftingRemainingItem(stack);
                    } else {
                        stack.shrink(1);
                    }
                    injector.setInjectorStack(stack);
                    injector.setInjectorEnergy(0);
                    injectors.remove(injector);
                    break;
                }
            }
        }

        int catCount = 1;
        if (recipe.getCatalyst() instanceof StackIngredient) {
            catCount = ((StackIngredient) recipe.getCatalyst()).getCount();
        }

        ItemStack catalyst = inv.getCatalystStack();
        ItemStack result = recipe.assemble(inv, level.registryAccess());
        catalyst.shrink(catCount);
        inv.setCatalystStack(catalyst);
        ItemStack outputStack = inv.getOutputStack();
        if (outputStack.isEmpty()) {
            inv.setOutputStack(result.copy());
        } else {
            outputStack.grow(result.getCount());
        }
    }

    /**
     * Called before starting the fusion crafting operation. Used this to check things like injector tiers
     * and any other special requirements that must be met before crafting can begin.
     *
     * The userStatus Consumer (if supplied) will be given a short status message that can be displayed to the user
     * via the GUI for example. SOme examples of possible status messages may be...
     * "Ready to craft"
     * "Pedestal tier too low"
     * "Output obstructed"
     * etc.
     *
     * @param inv    The fusion crafting inventory.
     * @param level  The world
     * @param userStatus Give this the reason this recipe can not start crafting (if there is one)
     */
    default boolean canStartCraft(IFusionInventory inv, Level level, @Nullable Consumer<Component> userStatus) {
        ItemStack output = inv.getOutputStack();
        if (!output.isEmpty()) {
            ItemStack result = assemble(inv, level.registryAccess());
            if (!ItemStack.isSameItemSameTags(output, result) || output.getCount() + result.getCount() > result.getItem().getMaxStackSize(result)) {
                if (userStatus != null) {
                    userStatus.accept(Component.translatable("fusion_status.draconicevolution.output_obstructed").withStyle(ChatFormatting.RED));
                }
                return false;
            }
        }

        if (inv.getMinimumTier().index < getRecipeTier().index) {
            if (userStatus != null) {
                userStatus.accept(Component.translatable("fusion_status.draconicevolution.tier_low").withStyle(ChatFormatting.RED));
            }
            return false;
        }

        if (userStatus != null) {
            userStatus.accept(Component.translatable("fusion_status.draconicevolution.ready").withStyle(ChatFormatting.GREEN));
        }
        return true;
    }

    interface IFusionIngredient {
        /**
         * @return the required ingredient.
         */
        Ingredient get();

        /**
         * @return true if this ingredient should be consumed in the crafting operation.
         */
        boolean consume();

    }

    @Override
    default boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    default boolean isSpecial() {
        return true; //TODO i may want to implement proper support for hard mode (recipes must be unlocked) fusion recipes at some point.
                     // But i would need to actually plan out recipe progression properly
    }
}
