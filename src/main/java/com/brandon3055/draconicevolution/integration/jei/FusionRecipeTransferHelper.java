package com.brandon3055.draconicevolution.integration.jei;

import codechicken.lib.inventory.InventoryUtils;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.crafting.IFusionInjector;
import com.brandon3055.draconicevolution.api.crafting.IFusionInventory;
import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;
import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe.IFusionIngredient;
import com.brandon3055.draconicevolution.api.crafting.IngredientStack;
import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore;
import com.brandon3055.draconicevolution.inventory.ContainerFusionCraftingCore;
import com.brandon3055.draconicevolution.lib.WTFException;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredient;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IStackHelper;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.util.Translator;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by brandon3055 on 15/7/21
 */
public class FusionRecipeTransferHelper implements IRecipeTransferHandler<ContainerFusionCraftingCore> {

    private final IStackHelper stackHelper;
    private final IRecipeTransferHandlerHelper handlerHelper;

    public FusionRecipeTransferHelper(IStackHelper stackHelper, IRecipeTransferHandlerHelper handlerHelper) {
        this.stackHelper = stackHelper;
        this.handlerHelper = handlerHelper;
    }

    @Override
    public Class<ContainerFusionCraftingCore> getContainerClass() {
        return ContainerFusionCraftingCore.class;
    }

    @Nullable
    @Override
    public IRecipeTransferError transferRecipe(ContainerFusionCraftingCore container, Object recipeObject, IRecipeLayout recipeLayout, PlayerEntity player, boolean maxTransfer, boolean doTransfer) {
        if (!(recipeObject instanceof IFusionRecipe)) {
            DraconicEvolution.LOGGER.error("Attempt to use JEI Fusion recipe transfer helper on a non IFusionRecipe");
            return handlerHelper.createInternalError(); //Should theoretically never need this but just in case.
        }
        IFusionRecipe recipe = (IFusionRecipe) recipeObject;
        TileFusionCraftingCore core = container.tile;
        core.updateInjectors();

        //Check Injector count and Tier
        int validInjectors = (int) core.getInjectors()
                .stream()
                .filter(e -> e.getInjectorTier().index >= recipe.getRecipeTier().index)
                .count();
        if (validInjectors < recipe.fusionIngredients().size()) {
            return handlerHelper.createUserErrorWithTooltip(I18n.get("gui.draconicevolution.fusion_craft.ne_tier_injectors", recipe.getRecipeTier().getDisplayName().getString()));
        }

        //Do... Pretty much everything else
        Map<Integer, Slot> inventorySlots = new HashMap<>();
        for (Slot slot : container.slots) {
            if (slot.index < 36) {
                inventorySlots.put(slot.index, slot);
            }
        }
        List<ItemStack> coreStacks = core.getInjectors().stream().map(IFusionInjector::getInjectorStack).collect(Collectors.toList());
        coreStacks.add(core.getCatalystStack());
        coreStacks.add(core.getOutputStack());

        int inputCount = 0;
        IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
        for (IGuiIngredient<ItemStack> ingredient : itemStackGroup.getGuiIngredients().values()) {
            if (ingredient.isInput() && !ingredient.getAllIngredients().isEmpty()) {
                inputCount++;
            }
        }

        Map<Integer, ItemStack> availableItemStacks = new HashMap<>();
        int filledCraftSlotCount = 0;
        int emptySlotCount = 0;

        for (Slot slot : inventorySlots.values()) {
            final ItemStack stack = slot.getItem();
            if (!stack.isEmpty()) {
                availableItemStacks.put(slot.index, stack.copy());
            } else {
                emptySlotCount++;
            }
        }

        int lastIndex = 36;
        for (ItemStack stack : coreStacks) {
            if (!stack.isEmpty()) {
                filledCraftSlotCount++;
                availableItemStacks.put(lastIndex++, stack.copy());
            }
        }

        // check if we have enough inventory space to shuffle items around to their final locations
        if (filledCraftSlotCount - inputCount > emptySlotCount) {
            String message = Translator.translateToLocal("jei.tooltip.error.recipe.transfer.inventory.full");
            return handlerHelper.createUserErrorWithTooltip(message);
        }

        List<Integer> missingStacks = checkForMissingIngredients(stackHelper, availableItemStacks, itemStackGroup.getGuiIngredients());
        if (missingStacks.size() > 0) {
            String message = Translator.translateToLocal("jei.tooltip.error.recipe.transfer.missing");
            return handlerHelper.createUserErrorForSlots(message, missingStacks);
        }

        if (doTransfer) {
            DraconicNetwork.sendFusionRecipeMove(recipe, maxTransfer);
        }

        return null;
    }

    private List<Integer> checkForMissingIngredients(IStackHelper stackhelper, Map<Integer, ItemStack> availableItemStacks, Map<Integer, ? extends IGuiIngredient<ItemStack>> ingredientsMap) {
        List<Integer> missing = new ArrayList<>();
        SortedSet<Integer> keys = new TreeSet<>(ingredientsMap.keySet());
        for (Integer key : keys) {
            IGuiIngredient<ItemStack> ingredient = ingredientsMap.get(key);
            if (!ingredient.isInput()) {
                continue;
            }
            List<ItemStack> requiredStacks = ingredient.getAllIngredients();
            if (requiredStacks.isEmpty()) {
                continue;
            }

            boolean foundIngredient = false;
            for (ItemStack stack : requiredStacks) {
                boolean hasItems = false;
                do {
                    boolean removing = hasItems;
                    int required = stack.getCount();
                    hasItems = false;
                    int found = 0;
                    for (ItemStack available : availableItemStacks.values()) {
                        if (stackhelper.isEquivalent(available, stack, UidContext.Ingredient)) {
                            if (removing) {
                                required -= available.getCount();
                                available.shrink(Math.min(required, available.getCount()));
                                if (required <= 0) break;
                            } else {
                                found += available.getCount();
                                if (found >= stack.getCount()) {
                                    hasItems = true;
                                    foundIngredient = true;
                                    break;
                                }
                            }
                        }
                    }
                    availableItemStacks.entrySet().removeIf(e -> e.getValue().getCount() <= 0);
                }
                while (hasItems);
                if (foundIngredient) {
                    break;
                }
            }
            if (!foundIngredient) {
                missing.add(key);
            }
        }
        return missing;
    }

    public static void doServerSideTransfer(ServerPlayerEntity player, ContainerFusionCraftingCore container, IFusionRecipe recipe, boolean maxTransfer) {
        TileFusionCraftingCore tile = container.tile;
        LazyOptional<IItemHandler> optionalHandler = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP);
        if (!optionalHandler.isPresent()) {
            DraconicEvolution.LOGGER.error("FusionRecipeTransferHelper: Player has no inventory capability");
            return;
        }
        IItemHandler playerItemHandler = optionalHandler.orElseThrow(WTFException::new);

        //Transfer any items in the core to the players inventory.
        for (int i = 0; i < tile.itemHandler.getSlots(); i++) {
            ItemStack stack = tile.itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                stack = InventoryUtils.insertItem(playerItemHandler, stack, false);
                tile.itemHandler.setStackInSlot(i, stack);
                if (!stack.isEmpty()) {
                    DraconicEvolution.LOGGER.error("FusionRecipeTransferHelper: Failed to transfer core inventory to player.");
                    return;
                }
            }
        }
        //Transfer any items in the injectors to the players inventory.
        for (IFusionInjector injector : tile.getInjectors()) {
            ItemStack stack = injector.getInjectorStack();
            if (!stack.isEmpty()) {
                stack = InventoryUtils.insertItem(playerItemHandler, stack, false);
                injector.setInjectorStack(stack);
                if (!stack.isEmpty()) {
                    DraconicEvolution.LOGGER.error("FusionRecipeTransferHelper: Failed to transfer core inventory to player.");
                    return;
                }
            }
        }

        transferIngredients(playerItemHandler, tile, recipe, maxTransfer);
    }

    private static void transferIngredients(IItemHandler playerInv, IFusionInventory fusionInv, IFusionRecipe recipe, boolean maxTransfer) {
        List<ItemStack> availableStacks = new ArrayList<>();
        for (int i = 0; i < playerInv.getSlots(); i++) {
            availableStacks.add(playerInv.getStackInSlot(i).copy());
        }
        int fullSets = 0;
        while (maxTransfer || fullSets == 0) {
            if (!checkIngredient(availableStacks, recipe.getCatalyst(), true)) {
                break;
            }
            boolean endCheck = false;
            for (IFusionIngredient ingred : recipe.fusionIngredients()) {
                if (!checkIngredient(availableStacks, ingred.get(), ingred.consume())) {
                    endCheck = true;
                    break;
                }
            }
            if (endCheck) {
                break;
            }
            fullSets++;
        }

        int catCount = recipe.getCatalyst() instanceof IngredientStack ? ((IngredientStack) recipe.getCatalyst()).getCount() : 1;
        int maxStack = recipe.getCatalyst().getItems().length > 0 ? recipe.getCatalyst().getItems()[0].getMaxStackSize() : 1;
        fullSets = Math.min(fullSets, maxStack / catCount);

        List<IFusionInjector> injectors = fusionInv.getInjectors()
                .stream()
                .sorted(Comparator.comparing(e -> ((IFusionInjector)e).getInjectorTier().index).reversed())
                .collect(Collectors.toList());

        List<IFusionIngredient> ingredients = recipe.fusionIngredients();;
        if (injectors.size() < ingredients.size()) {
            DraconicEvolution.LOGGER.error("FusionRecipeTransferHelper: Unexpected error while transferring recipe");
            return;
        }

        for (int i = 0; i < fullSets; i++) {
            ItemStack catalyst = getIngredient(fusionInv.getCatalystStack(), recipe.getCatalyst(), playerInv);
            fusionInv.setCatalystStack(catalyst);

            for (int fi = 0; fi < ingredients.size(); fi++) {
                IFusionInjector injector = injectors.get(fi);
                IFusionIngredient ingredient = ingredients.get(fi);
                if (!ingredient.consume() && i > 0) continue;
                ItemStack stack = getIngredient(injector.getInjectorStack(), ingredient.get(), playerInv);
                injector.setInjectorStack(stack);
            }
        }
    }

    private static ItemStack getIngredient(ItemStack existing, Ingredient ingred, IItemHandler playerInv) {
        int count = ingred instanceof IngredientStack ? ((IngredientStack) ingred).getCount() : 1;
        for (int i = 0; i < playerInv.getSlots(); i++) {
            ItemStack slot = playerInv.extractItem(i, count, true);
            if (ingred instanceof IngredientStack ? ((IngredientStack) ingred).itemTest(slot) : ingred.test(slot)) {
                if (InventoryUtils.canStack(existing, slot)) {
                    int maxInsert = existing.isEmpty() ? count : Math.min(count, existing.getMaxStackSize() - existing.getCount());
                    ItemStack extracted = playerInv.extractItem(i, maxInsert, false);
                    if (existing.isEmpty()) {
                        existing = extracted;
                    } else {
                        existing.grow(extracted.getCount());
                    }
                    count -= extracted.getCount();
                    if (count <= 0) {
                        return existing;
                    }
                }
            }
        }
        return existing;
    }

    private static boolean checkIngredient(List<ItemStack> availableStacks, Ingredient ingred, boolean consume) {
        int count = ingred instanceof IngredientStack ? ((IngredientStack) ingred).getCount() : 1;
        for (ItemStack stack : availableStacks) {
            if (ingred instanceof IngredientStack ? ((IngredientStack) ingred).itemTest(stack) : ingred.test(stack)) {
                int stackSize = stack.getCount();
                if (consume) {
                    stack.shrink(Math.min(count, stack.getCount()));
                }
                count -= stackSize;
                if (count <= 0) {
                    availableStacks.removeIf(ItemStack::isEmpty);
                    return true;
                }
            }
        }
        availableStacks.removeIf(ItemStack::isEmpty);
        return false;
    }
}
