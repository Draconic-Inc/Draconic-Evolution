package com.brandon3055.draconicevolution.inventory;

import codechicken.lib.gui.modular.lib.container.SlotGroup;
import codechicken.lib.inventory.container.modular.ModularSlot;
import com.brandon3055.brandonscore.inventory.TileItemStackHandler;
import com.brandon3055.draconicevolution.blocks.tileentity.chest.TileDraconiumChest;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Created by brandon3055 on 4/06/2017.
 */
public class DraconiumChestMenu extends DETileMenu<TileDraconiumChest> {
    public ModularResultSlot craftResultSlot;
    private final CraftingInventoryWrapper craftInventory;
    private final ResultContainerWrapper resultInventory;

    public final SlotGroup main = createSlotGroup(0, 1, 2, 3, 4);
    public final SlotGroup hotBar = createSlotGroup(0, 1, 2, 3, 4);

    public final SlotGroup chestInv = createSlotGroup(1, 0, 2, 3);

    public final SlotGroup furnaceInputs = createSlotGroup(2, 0, 1);
    public final SlotGroup capacitor = createSlotGroup(3, 0, 1);

    public final SlotGroup craftIn = createSlotGroup(4, 0, 1);
    public final SlotGroup craftOut = createSlotGroup(5, 0, 1);

    public DraconiumChestMenu(int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(DEContent.MENU_DRACONIUM_CHEST.get(), windowId, playerInv, getClientTile(playerInv, extraData));
    }

    public DraconiumChestMenu(@Nullable MenuType<?> type, int windowId, Inventory inv, TileDraconiumChest tile) {
        super(type, windowId, inv, tile);

        hotBar.addPlayerBar(inv);
        main.addPlayerMain(inv);

        chestInv.addSlots(tile.mainInventory.getSlots(), 0, slot -> new ModularSlot(tile.mainInventory, slot));

        //Crafting Inventory
        craftInventory = new CraftingInventoryWrapper(this, 3, 3, tile.craftingItems);
        resultInventory = new ResultContainerWrapper(tile.craftingItems, 9);
        craftOut.addSlot(craftResultSlot = new ModularResultSlot(inv.player, craftInventory, resultInventory, 0, 0, 0));
        craftIn.addSlots(craftInventory.getContainerSize(), 0, slot -> new ModularSlot(craftInventory, slot));

        furnaceInputs.addSlots(tile.furnaceItems.getSlots(), 0, slot -> new ModularSlot(tile.furnaceItems, slot));
        capacitor.addSlot(new ModularSlot(tile.capacitorInv, 0));

        slotsChanged(inv);
    }


    protected void slotChangedCraftingGrid(int containerID, Level level, Player player, CraftingContainer craftingInventory, ResultContainerWrapper resultInventory) {
        if (!level.isClientSide) {
            ServerPlayer serverplayerentity = (ServerPlayer) player;
            ItemStack itemstack = ItemStack.EMPTY;
            Optional<RecipeHolder<CraftingRecipe>> optional = level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftingInventory.asCraftInput(), level);
            if (optional.isPresent()) {
                RecipeHolder<CraftingRecipe> icraftingrecipe = optional.get();
                if (resultInventory.setRecipeUsed(level, serverplayerentity, icraftingrecipe)) {
                    itemstack = icraftingrecipe.value().assemble(craftingInventory.asCraftInput(), level.registryAccess());
                }
            }

            resultInventory.setItem(0, itemstack);
            serverplayerentity.connection.send(new ClientboundContainerSetSlotPacket(containerID, stateId, ((Slot) craftResultSlot).index, itemstack));
        }
    }

    @Override
    public void slotsChanged(Container inventory) {
        slotChangedCraftingGrid(this.containerId, tile.getLevel(), this.player, this.craftInventory, this.resultInventory);
    }

    public static class ResultContainerWrapper implements Container, RecipeCraftingHolder {
        private final TileItemStackHandler stackHandler;
        private final int slot;

        public ResultContainerWrapper(TileItemStackHandler stackHandler, int slot) {
            this.stackHandler = stackHandler;
            this.slot = slot;
        }

        @Nullable
        private RecipeHolder<?> recipeUsed;

        @Override
        public int getContainerSize() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return stackHandler.getStackInSlot(slot).isEmpty();
        }

        @Override
        public ItemStack getItem(int p_40147_) {
            return stackHandler.getStackInSlot(slot);
        }

        @Override
        public ItemStack removeItem(int p_40149_, int p_40150_) {
            ItemStack stack = stackHandler.getStackInSlot(slot);
            stackHandler.setStackInSlot(slot, ItemStack.EMPTY);
            return stack;
        }

        @Override
        public ItemStack removeItemNoUpdate(int p_40160_) {
            return removeItem(0, 0);
        }

        @Override
        public void setItem(int p_40152_, ItemStack stack) {
            stackHandler.setStackInSlot(slot, stack);
        }

        @Override
        public void setChanged() {
        }

        @Override
        public boolean stillValid(Player p_40155_) {
            return true;
        }

        @Override
        public void clearContent() {
            stackHandler.setStackInSlot(slot, ItemStack.EMPTY);
        }

        @Override
        public void setRecipeUsed(@Nullable RecipeHolder<?> p_301012_) {
            this.recipeUsed = p_301012_;
        }

        @Nullable
        @Override
        public RecipeHolder<?> getRecipeUsed() {
            return this.recipeUsed;
        }
    }
}
