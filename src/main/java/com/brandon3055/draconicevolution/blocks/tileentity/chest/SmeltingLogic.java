package com.brandon3055.draconicevolution.blocks.tileentity.chest;

import codechicken.lib.inventory.InventorySimple;
import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.api.power.OPStorage;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.lib.datamanager.DataFlags;
import com.brandon3055.brandonscore.lib.datamanager.ManagedEnum;
import com.brandon3055.brandonscore.lib.datamanager.ManagedFloat;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import java.util.Locale;

/**
 * Created by brandon3055 on 19/04/2022
 */
public class SmeltingLogic {
    private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();
    public final ManagedEnum<FeedMode> feedMode;
    public final ManagedFloat smeltProgress;
    /**
     * Based on available power (range is 0F to 1F)
     */
    public final ManagedFloat smeltingPower;

    private final TileBCore tile;
    private final IItemHandler inputInv;
    private final IItemHandler outputInv;
    private final InventorySlotMapper[] wrappedSlots;

    private IItemHandlerModifiable feedSourceInv;
    private OPStorage opStorage;

    private int feedSchedule = 0;
    private boolean outputBlocked = false; // Output is blocked if there is no room for ANY of the current recipes.
    private boolean inputInvalid = false; // When there are no valid items in any of the input slots.

    private int energyPerSmeltTick = 32;

    /**
     * @param tile      The tile housing this smelting logic.
     * @param inputInv  The furnace input slot (or slots)
     * @param outputInv The furnace output slot (or slots)
     * @param opStorage The power supply for this furnace.
     */
    public SmeltingLogic(TileBCore tile, IItemHandlerModifiable inputInv, IItemHandler outputInv, OPStorage opStorage) {
        this.tile = tile;
        this.inputInv = inputInv;
        this.outputInv = outputInv;
        this.opStorage = opStorage;
        this.wrappedSlots = new InventorySlotMapper[inputInv.getSlots()];
        for (int i = 0; i < inputInv.getSlots(); i++) {
            wrappedSlots[i] = new InventorySlotMapper(inputInv, i);
        }

        feedMode = tile.register(new ManagedEnum<>("feed_mode", FeedMode.OFF, DataFlags.SAVE_BOTH_SYNC_CONTAINER, DataFlags.CLIENT_CONTROL));
        smeltProgress = tile.register(new ManagedFloat("smelt_progress", 0F, DataFlags.SAVE_NBT_SYNC_CONTAINER));
        smeltingPower = tile.register(new ManagedFloat("smelt_speed", 0F, DataFlags.SAVE_NBT_SYNC_CONTAINER));
        feedMode.addValueListener(e -> inputInventoryChanged().attemptAutoFeed());
    }

    /**
     * @param feedSourceInv The inventory for auto feed function to pull from.
     */
    public void setFeedSourceInv(IItemHandlerModifiable feedSourceInv) {
        this.feedSourceInv = feedSourceInv;
    }

    public void tick(boolean enableSmelting) {
        long opStored = opStorage.getOPStored();
        long opMax = opStorage.getMaxOPStored();
        enableSmelting &= opStored > 0;

        if (enableSmelting && feedSchedule > 0 && --feedSchedule == 0) {
            attemptAutoFeed();
        }

        float availablePower = opStored < opMax / 4F ? opStored / (opMax / 4F) : 1F;
        boolean isSmelting = enableSmelting && !inputInvalid && !outputBlocked;
        smeltingPower.set(MathHelper.approachLinear(smeltingPower.get(), isSmelting ? availablePower : 0, 1 / 10F));

        float maxEfficiency = 4; //This will be where things like speed modules get applied
        float speedModifier = smeltingPower.get() * maxEfficiency;

        boolean blockedAt100 = outputBlocked && smeltProgress.get() >= 1;
        if (enableSmelting && !blockedAt100 && !inputInvalid) {
            updateSmelting(speedModifier);
        } else if (smeltProgress.get() > 0 && (!outputBlocked || !enableSmelting)) {
            smeltProgress.set(Math.max(0, smeltProgress.get() - (1F / (5F * 20F)))); //Takes 5 seconds for full smelt progress to be lost
        }
    }

    private void updateSmelting(float speedModifier) {
        Level world = tile.getLevel();
        int validRecipes = 0;
        int completedSmelts = 0;
        int slowestRecipe = 0;

        boolean attemptSmelt = smeltProgress.get() >= 1;

        for (int i = 0; i < wrappedSlots.length; i++) {
            InventorySlotMapper slot = wrappedSlots[i];
            ItemStack stack = slot.getItem(0);
            if (stack.isEmpty() || (stack.getCount() == 1 && feedMode.get() == FeedMode.STiCKY)) {
                continue;
            }

            RecipeHolder<SmeltingRecipe> holder = world.getRecipeManager().getRecipeFor(RecipeType.SMELTING, slot, world).orElse(null);
            if (holder == null) {
                continue;
            }
            SmeltingRecipe recipe = holder.value();
            validRecipes++;
            slowestRecipe = Math.max(slowestRecipe, recipe.getCookingTime());

            if (attemptSmelt) {
                ItemStack result = recipe.assemble(slot, tile.getLevel().registryAccess());
                if (InventoryUtils.insertItem(outputInv, result, true).isEmpty() && !inputInv.extractItem(i, 1, false).isEmpty()) {
                    InventoryUtils.insertItem(outputInv, result, false);
                    recipesUsed.addTo(holder.id(), 1);
                    completedSmelts++;
                }
            }
        }

        if (validRecipes == 0) {
            inputInvalid = true;
            return;
        }

        float smeltingSpeed = (1F / slowestRecipe) * speedModifier; //Because by default with no upgrades this furnace is twice as fast as vanilla
        int energy = Math.max(1, (int) (energyPerSmeltTick * (speedModifier < 1F ? speedModifier : speedModifier * speedModifier))) * validRecipes;
        if (attemptSmelt) {
            // At this point smelt operations should have been attempted
            opStorage.modifyEnergyStored(-energy);
            if (completedSmelts > 0) {
                smeltProgress.set(0);
                attemptAutoFeed();
            } else {
                outputBlocked = true;
            }
        } else {
            long extracted = opStorage.modifyEnergyStored(-energy);
            if (energy > extracted) {
                smeltingSpeed *= extracted / (float) energy;
            }
            smeltProgress.set(Math.min(1, smeltProgress.get() + smeltingSpeed));
        }
    }

    private void attemptAutoFeed() {
        if (feedSourceInv == null || feedMode.get() == FeedMode.OFF || isInputFull()) {
            return;
        }
        feedSchedule = 0;

        for (int i = 0; i < feedSourceInv.getSlots(); i++) {
            ItemStack stack = feedSourceInv.getStackInSlot(i);
            if (stack.isEmpty() || !isSmeltable(stack)) {
                continue;
            }
            ItemStack remainder = insertItem(inputInv, stack);
            feedSourceInv.setStackInSlot(i, remainder);
            if ((remainder.isEmpty() || remainder.getCount() < stack.getCount()) && isInputFull()) {
                inputInvalid = false;
                outputBlocked = false;
                return;
            }
        }
    }

    public ItemStack insertItem(IItemHandler handler, ItemStack insert) {
        insert = insert.copy();
        for (int pass = 0; pass < 2; pass++) {
            for (int slot = 0; slot < handler.getSlots(); slot++) {
                ItemStack stack = handler.getStackInSlot(slot);
                if ((pass == 0 || feedMode.get() != FeedMode.ALL) && stack.isEmpty()) {
                    continue;
                }
                insert = handler.insertItem(slot, insert, false);
                if (insert.isEmpty()) {
                    return ItemStack.EMPTY;
                }
            }
        }

        return insert;
    }

    private boolean isInputFull() {
        int fullSlots = 0;
        for (int i = 0; i < inputInv.getSlots(); i++) {
            ItemStack stack = inputInv.getStackInSlot(i);
            if (stack.getCount() >= stack.getMaxStackSize() || (stack.isEmpty() && feedMode.get() != FeedMode.ALL)) {
                fullSlots++;
            }
        }
        return fullSlots == inputInv.getSlots();
    }

    private boolean isInputEmpty() {
        for (int i = 0; i < inputInv.getSlots(); i++) {
            if (!inputInv.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public SmeltingLogic scheduleAutoFeed() {
        if (feedSchedule == 0 && feedMode.get() != FeedMode.OFF) {
            feedSchedule = 10;
        }
        return this;
    }

    public SmeltingLogic inputInventoryChanged() {
        inputInvalid = inputInvalid && isInputEmpty();
        outputBlocked = false;
        if (!isInputEmpty()) {
            scheduleAutoFeed();
        }
        return this;
    }

    public SmeltingLogic outputInventoryChanged() {
        outputBlocked = false;
        return this;
    }

    public void saveAdditionalNBT(CompoundTag nbt) {
        CompoundTag compound = new CompoundTag();
        this.recipesUsed.forEach((recipe, count) -> compound.putInt(recipe.toString(), count));
        nbt.put("recipes_used", compound);
    }

    public void loadAdditionalNBT(CompoundTag nbt) {
        CompoundTag compound = nbt.getCompound("recipes_used");
        for (String s : compound.getAllKeys()) {
            this.recipesUsed.put(new ResourceLocation(s), compound.getInt(s));
        }
    }

    private InventorySimple smeltTestInv = new InventorySimple(new ItemStack[1]);

    public boolean isSmeltable(ItemStack stack) {
        smeltTestInv.setItem(0, stack);
        boolean ret = tile.getLevel().getRecipeManager().getRecipeFor(RecipeType.SMELTING, smeltTestInv, tile.getLevel()).isPresent();
        smeltTestInv.setItem(0, ItemStack.EMPTY);
        return ret;
    }

    public enum FeedMode {
        OFF("chest/feed_off"),
        ALL("chest/feed_all"),
        FILTER("chest/feed_filter"),
        STiCKY("chest/feed_filter_sticky");

        private String sprite;

        FeedMode(String sprite) {
            this.sprite = sprite;
        }

        public String getSprite() {
            return sprite;
        }

        public String localKey() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    private static class InventorySlotMapper implements Container {
        private final IItemHandlerModifiable itemHandler;
        private final int slot;

        public InventorySlotMapper(IItemHandlerModifiable itemHandler, int slot) {
            this.itemHandler = itemHandler;
            this.slot = slot;
        }

        @Override
        public void clearContent() {
            itemHandler.setStackInSlot(slot, ItemStack.EMPTY);
        }

        @Override
        public int getContainerSize() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return itemHandler.getStackInSlot(slot).isEmpty();
        }

        @Override
        public ItemStack getItem(int index) {
            return index == 0 ? itemHandler.getStackInSlot(slot) : ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItem(int index, int count) {
            if (index != 0) {
                return ItemStack.EMPTY;
            }

            return itemHandler.extractItem(slot, count, false);
        }

        @Override
        public ItemStack removeItemNoUpdate(int index) {
            if (index != 0) {
                return ItemStack.EMPTY;
            }
            ItemStack stack = itemHandler.getStackInSlot(slot);
            itemHandler.setStackInSlot(slot, ItemStack.EMPTY);
            return stack;
        }

        @Override
        public void setItem(int index, ItemStack stack) {
            if (index == 0) itemHandler.setStackInSlot(slot, stack);
        }

        @Override
        public void setChanged() {}

        @Override
        public boolean stillValid(Player player) {return true;}
    }
}
