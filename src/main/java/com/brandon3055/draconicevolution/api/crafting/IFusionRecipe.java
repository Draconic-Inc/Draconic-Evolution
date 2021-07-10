package com.brandon3055.draconicevolution.api.crafting;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.brandon3055.draconicevolution.api.crafting.IFusionStateMachine.FusionState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by brandon3055 on 26/11/20
 */
public interface IFusionRecipe extends IRecipe<IFusionInventory> {

    @Override
    default ItemStack getToastSymbol() {
        if (DraconicAPI.CRAFTING_CORE != null) {
            return new ItemStack(DraconicAPI.CRAFTING_CORE);
        } else {
            return new ItemStack(Blocks.CRAFTING_TABLE);
        }
    }

    @Override
    default IRecipeType<?> getType() {
        return DraconicAPI.FUSION_RECIPE_TYPE;
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
     * @param world the world
     */
    @Override
    boolean matches(IFusionInventory inv, World world);

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
     * @param world        The world
     */
    default void tickFusionState(IFusionStateMachine stateMachine, IFusionInventory inv, World world) {
        switch (stateMachine.getFusionState()) {
            case START:
                //<ake sure the recipe is valid. It should be but just in case.
                if (!matches(inv, world)) {
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
                stateMachine.setStateProgress(0, new TranslationTextComponent("fusion_state.draconicevolution.charging"));
                //Progress to next state
                stateMachine.setFusionState(FusionState.CHARGING);
                break;
            case CHARGING:
                long totalCharge = inv.getInjectors()
                        .stream()
                        .mapToLong(IFusionInjector::getInjectorEnergy)
                        .sum();
                stateMachine.setStateProgress((double) totalCharge / getEnergyCost(), new TranslationTextComponent("fusion_state.draconicevolution.charging"));
                if (totalCharge >= getEnergyCost()) {
                    stateMachine.setStateProgress(0, new TranslationTextComponent("fusion_state.draconicevolution.crafting"));
                    stateMachine.setCounter(0);
                    //Proceed to next step
                    stateMachine.setFusionState(FusionState.CRAFTING);
                }
                break;
            case CRAFTING:
                TechLevel minInjector = inv.getInjectors().stream()
                        .filter(e -> !e.getInjectorStack().isEmpty())
                        .sorted(Comparator.comparing(e -> e.getInjectorTier().index))
                        .map(IFusionInjector::getInjectorTier)
                        .findFirst()
                        .orElse(TechLevel.DRACONIUM);
                int craftTime = DEConfig.fusionCraftTime.get(minInjector.index);
                int counter = stateMachine.getCounter();
                stateMachine.setCounter(counter + 1);
                stateMachine.setStateProgress((double) counter / (double) craftTime, new TranslationTextComponent("fusion_state.draconicevolution.crafting"));
                if (counter >= craftTime) {
                    if (!matches(inv, world) || !canCraft(inv, world)) {
                        stateMachine.cancelCraft();
                        return;
                    }
                    completeCraft(inv, world, this);
                    stateMachine.completeCraft();
                }
                break;
        }
    }

    static void completeCraft(IFusionInventory inv, World world, IFusionRecipe recipe) {
        //Use Ingredients
        List<IFusionInjector> injectors = new ArrayList<>(inv.getInjectors());
        for (IFusionIngredient ingredient : recipe.fusionIngredients()) {
            for (IFusionInjector injector : injectors) {
                ItemStack stack = injector.getInjectorStack();
                if (!stack.isEmpty() && ingredient.get().test(stack) && injector.getInjectorTier().index >= recipe.getRecipeTier().index) {
                    if (!ingredient.consume()) {
                        break;
                    }
                    if (stack.hasContainerItem()) {
                        stack = stack.getItem().getContainerItem(stack);
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
        if (recipe.getCatalyst() instanceof IngredientStack) {
            catCount = ((IngredientStack) recipe.getCatalyst()).getCount();
        }

        ItemStack catalyst = inv.getCatalystStack();
        ItemStack result = recipe.assemble(inv);
        catalyst.shrink(catCount);
        inv.setCatalystStack(catalyst);
        inv.setOutputStack(result.copy());
    }


    /**
     * Returns the "pre-craft" status to display in the gui for the current recipe. For example:
     * "Ready to craft"
     * "Pedestal tier too low"
     * "Output obstructed"
     * etc.
     *
     * @param inv   The fusion crafting inventory.
     * @param world the world
     * @return the current recipe status
     */
    default ITextComponent getRecipeStatus(IFusionInventory inv, World world) {
        return null;
    }

    /**
     * Used to apply secondary checks such as crafting tier, and any "special" crafting requirements.
     * Does not care about energy because the final decision to craft is now controlled bu the recipe
     * via {@link #tickFusionState(IFusionStateMachine, IFusionInventory, World)}
     * If this is true then the crafting operation can be started.
     *
     * @param inv   The fusion crafting inventory.
     * @param world the world
     */
    default boolean canCraft(IFusionInventory inv, World world) {
        return matches(inv, world);//TODO
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
}
