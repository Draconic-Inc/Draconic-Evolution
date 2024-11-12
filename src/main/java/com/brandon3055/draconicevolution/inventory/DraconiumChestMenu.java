package com.brandon3055.draconicevolution.inventory;

import codechicken.lib.gui.modular.lib.container.SlotGroup;
import codechicken.lib.inventory.container.modular.ModularSlot;
import com.brandon3055.draconicevolution.blocks.tileentity.chest.TileDraconiumChest;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
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
//    public List<Slot> mainSlots = new ArrayList<>();
//    public List<Slot> playerSlots = new ArrayList<>();
//    public List<Slot> craftInputSlots = new ArrayList<>();
//    public List<Slot> furnaceInputSlots = new ArrayList<>();
    public ModularResultSlot craftResultSlot;
//    public Slot capacitorSlot;
    private CraftingInventoryWrapper craftInventory;
    private final ResultContainer resultInventory = new ResultContainer();

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



//        Player Inventory
//        for (int i = 0; i < inv.items.size(); i++) {
//            playerSlots.add(addSlot(new SlotCheckValid.IInv(inv, i, 0, 0)));
//        }

        //Main Inventory
//        for (int i = 0; i < tile.mainInventory.getSlots(); i++) {
//            mainSlots.add(addSlot(new SlotCheckValid(tile.mainInventory, i, 0, 0)));
//        }

        //Crafting Inventory
        craftInventory = new CraftingInventoryWrapper(this, 3, 3, tile.craftingItems);
//        this.addSlot(craftResultSlot = new ModularResultSlot(inv.player, craftInventory, resultInventory, 0, 0, 0));
//        for (int i = 0; i < 9; ++i) {
//            craftInputSlots.add(addSlot(new Slot(craftInventory, i, 0, 0)));
//        }
        craftOut.addSlot(craftResultSlot = new ModularResultSlot(inv.player, craftInventory, resultInventory, 0, 0, 0));
        craftIn.addSlots(craftInventory.getContainerSize(), 0, slot -> new ModularSlot(craftInventory, slot));

        furnaceInputs.addSlots(tile.furnaceItems.getSlots(), 0, slot -> new ModularSlot(tile.furnaceItems, slot));

        //Furnace Inventory
//        for (int i = 0; i < 5; i++) {
//            furnaceInputSlots.add(addSlot(new SlotCheckValid(tile.furnaceItems, i, 0, 0)));
//        }
        capacitor.addSlot(new ModularSlot(tile.capacitorInv, 0));
//        addSlot(capacitorSlot = new SlotCheckValid(tile.capacitorInv, 0, 0, 0));

        slotsChanged(inv);
    }


    protected void slotChangedCraftingGrid(int containerID, Level level, Player player, CraftingContainer craftingInventory, ResultContainer resultInventory) {
        if (!level.isClientSide) {
            ServerPlayer serverplayerentity = (ServerPlayer) player;
            ItemStack itemstack = ItemStack.EMPTY;
            Optional<RecipeHolder<CraftingRecipe>> optional = level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftingInventory, level);
            if (optional.isPresent()) {
                RecipeHolder<CraftingRecipe> icraftingrecipe = optional.get();
                if (resultInventory.setRecipeUsed(level, serverplayerentity, icraftingrecipe)) {
                    itemstack = icraftingrecipe.value().assemble(craftingInventory, level.registryAccess());
                }
            }

            resultInventory.setItem(0, itemstack);
            serverplayerentity.connection.send(new ClientboundContainerSetSlotPacket(containerID, stateId, ((Slot)craftResultSlot).index, itemstack));
        }
    }

    @Override
    public void slotsChanged(Container inventory) {
        slotChangedCraftingGrid(this.containerId, tile.getLevel(), this.player, this.craftInventory, this.resultInventory);
    }


//    @Override
//    public void slotsChanged(@Nonnull IInventory inventory) {
//        if (!Objects.requireNonNull(tile.getLevel()).isClientSide()) {
//            ItemStack stack = ItemStack.EMPTY;
//            Optional<ICraftingRecipe> optional = Objects.requireNonNull(tile.getLevel().getServer()).getRecipeManager().getRecipeFor(IRecipeType.CRAFTING, craftMatrix, tile.getLevel());
//            if (optional.isPresent()) {
//                ICraftingRecipe recipe = optional.get();
//                if (craftResult.setRecipeUsed(tile.getLevel(), (ServerPlayerEntity) this.player, recipe)) {
//                    stack = recipe.assemble(craftMatrix);
//                }
//            }
//            craftResult.setItem(1, stack);
//            super.slotsChanged(inventory);
//            ((ServerPlayerEntity)this.player).connection.send(new SSetSlotPacket(containerId, 267, stack));
//        }
//    }
//
//    @Nullable
//    @Override
//    public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {
//        ItemStack itemstack = ItemStack.EMPTY;
//        Slot slot = slots.get(index);
//        if (slot != null && slot.hasItem()) {
//            ItemStack itemstack1 = slot.getItem();
//            itemstack = itemstack1.copy();
//
//            //Transferring from Main Container
//            if (index < 260) {
//                if (!moveItemStackTo(itemstack1, 277, slots.size(), false)) {
//                    return ItemStack.EMPTY;
//                }
//                slot.onQuickCraft(itemstack1, itemstack);
//            }
//            //Transferring from a crafting inventory
//            else if (index == 267 || index == 265 || index == 266) {
//                //First try placing the stack in the players inventory
//                if (!moveItemStackTo(itemstack1, 277, slots.size(), false)) {
//                    //If that fails try the chest inventory
//                    if (!moveItemStackTo(itemstack1, 0, 259, false)) {
//                        return ItemStack.EMPTY;
//                    }
//                }
//                slot.onQuickCraft(itemstack1, itemstack);
//            }
//            else if (index >= 260 && index < 277) {
//                //First try the players inventory
//                if (!moveItemStackTo(itemstack1, 0, 259, false)) {
//                    //If that fails try the chest inventory
//                    if (!moveItemStackTo(itemstack1, 277, slots.size(), false)) {
//                        return ItemStack.EMPTY;
//                    }
//                }
//                slot.onQuickCraft(itemstack1, itemstack);
//            }
//            //Transferring from Player Inventory
//            else if (!DraconiumChest.isStackValid(itemstack1) || !moveItemStackTo(itemstack1, 0, 259, false)) {
//                return ItemStack.EMPTY;
//            }
//
//            if (itemstack1.getCount() == 0) {
//                slot.set(ItemStack.EMPTY);
//            }
//            else {
//                slot.setChanged();
//            }
//
//            slot.onTake(player, itemstack1);
//        }
//        return itemstack;
//    }
//
//    @Nullable
//    @Override
//    public ItemStack clicked(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
//        ItemStack stack = super.clicked(slotId, dragType, clickTypeIn, player);
//
//        if (dragType == 1 && clickTypeIn == ClickType.PICKUP && slotId >= 260 && slotId <= 264) {
////            tile.validateSmelting();
//        }
//
//        return stack;
//    }
//
//
//    public class SlotSmeltable extends SlotItemHandler {
//        public SlotSmeltable(IItemHandler p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_) {
//            super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
//        }
//
//        @Override
//        public boolean mayPlace(ItemStack stack) {
//            return false;//tile.getSmeltResult(stack) != null;
//        }
//    }
//
//    public class SlotRFCapacitor extends SlotItemHandler {
//        public SlotRFCapacitor(IItemHandler inventory, int id, int x, int y) {
//            super(inventory, id, x, y);
//
//        }
//
//        @Override
//        public boolean mayPlace(ItemStack stack) {
//            if (super.mayPlace(stack)) {
//                return EnergyUtils.canExtractEnergy(stack);
//            }
//            return false;
//        }
//
//        @Override
//        public int getMaxStackSize() {
//            return 1;
//        }
//    }
//
//    public class SlotCore extends SlotItemHandler {
//        public SlotCore(IItemHandler inventory, int id, int x, int y) {
//            super(inventory, id, x, y);
//
//        }
//
//        @Override
//        public boolean mayPlace(ItemStack stack) {
//            if (super.mayPlace(stack)) {
//                return !stack.isEmpty() && stack.getItem() instanceof ItemCore /*&& stack.getItem() != DEContent.draconicCore*/;
//            }
//            return false;
//        }
//
//        @Override
//        public int getMaxStackSize() {
//            return 1;
//        }
//    }
}
