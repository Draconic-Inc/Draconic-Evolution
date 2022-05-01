package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.inventory.ContainerBCTile;
import com.brandon3055.brandonscore.inventory.SlotCheckValid;
import com.brandon3055.draconicevolution.blocks.tileentity.chest.TileDraconiumChest;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by brandon3055 on 4/06/2017.
 */
public class ContainerDraconiumChest extends ContainerBCTile<TileDraconiumChest> {
    public List<Slot> mainSlots = new ArrayList<>();
    public List<Slot> playerSlots = new ArrayList<>();
    public List<Slot> craftInputSlots = new ArrayList<>();
    public List<Slot> furnaceInputSlots = new ArrayList<>();
    public Slot craftResultSlot;
    public Slot capacitorSlot;
    private CraftingInventoryWrapper craftInventory;
    private final CraftResultInventory resultInventory = new CraftResultInventory();

    public ContainerDraconiumChest(int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
        this(DEContent.container_draconium_chest, windowId, playerInv, getClientTile(extraData));
    }

    public ContainerDraconiumChest(@Nullable ContainerType<?> type, int windowId, PlayerInventory playerInv, TileDraconiumChest tile) {
        super(type, windowId, playerInv, tile);

        //Player Inventory
        for (int i = 0; i < playerInv.items.size(); i++) {
            playerSlots.add(addSlot(new SlotCheckValid.IInv(playerInv, i, 0, 0)));
        }

        //Main Inventory
        for (int i = 0; i < tile.mainInventory.getSlots(); i++) {
            mainSlots.add(addSlot(new SlotCheckValid(tile.mainInventory, i, 0, 0)));
        }

        //Crafting Inventory
        craftInventory = new CraftingInventoryWrapper(this, 3, 3, tile.craftingItems);
        this.addSlot(craftResultSlot = new CraftingResultSlot(playerInv.player, craftInventory, resultInventory, 0, 0, 0));
        for (int i = 0; i < 9; ++i) {
            craftInputSlots.add(addSlot(new Slot(craftInventory, i, 0, 0)));
        }

        //Furnace Inventory
        for (int i = 0; i < 5; i++) {
            furnaceInputSlots.add(addSlot(new SlotCheckValid(tile.furnaceItems, i, 0, 0)));
        }

        addSlot(capacitorSlot = new SlotCheckValid(tile.capacitorInv, 0, 0, 0));

        slotsChanged(playerInv);
    }


    protected void slotChangedCraftingGrid(int containerID, World world, PlayerEntity player, CraftingInventory craftingInventory, CraftResultInventory resultInventory) {
        if (!world.isClientSide) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) player;
            ItemStack itemstack = ItemStack.EMPTY;
            Optional<ICraftingRecipe> optional = world.getServer().getRecipeManager().getRecipeFor(IRecipeType.CRAFTING, craftingInventory, world);
            if (optional.isPresent()) {
                ICraftingRecipe icraftingrecipe = optional.get();
                if (resultInventory.setRecipeUsed(world, serverplayerentity, icraftingrecipe)) {
                    itemstack = icraftingrecipe.assemble(craftingInventory);
                }
            }

            resultInventory.setItem(0, itemstack);
            serverplayerentity.connection.send(new SSetSlotPacket(containerID, craftResultSlot.index, itemstack));
        }
    }

    @Override
    public void slotsChanged(IInventory inventory) {
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
