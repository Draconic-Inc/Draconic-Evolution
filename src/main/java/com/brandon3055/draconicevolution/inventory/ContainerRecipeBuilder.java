package com.brandon3055.draconicevolution.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by brandon3055 on 21/07/2016.
 */
public class ContainerRecipeBuilder extends AbstractContainerMenu {

    private Player player;
    public InventoryCache inventoryCache = new InventoryCache(20);
    private List<Slot> craftingSlots = new LinkedList<>();

    public ContainerRecipeBuilder(@Nullable MenuType<?> type, int id, Player player) {
        super(type, id);
        this.player = player;
    }

//    public ContainerRecipeBuilder(PlayerEntity player) {
//        this.player = player;
//        addSlots();
//        arangeCraftingSlots(0);
//    }

    public void addSlots() {
        int posX = 20;
        int posY = 145;
        for (int x = 0; x < 9; x++) {
            addSlot(new Slot(player.getInventory(), x, posX + 18 * x, posY + 58));
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new Slot(player.getInventory(), x + y * 9 + 9, posX + 18 * x, posY + y * 18));
            }
        }

        for (int i = 0; i < inventoryCache.getContainerSize(); i++) {
            Slot slot = new Slot(inventoryCache, i, 1000, 1000);
            addSlot(slot);
            craftingSlots.add(slot);
        }
    }

    public void arangeCraftingSlots(int craftingType) {
        for (Slot slot : craftingSlots) {
            slot.x = slot.y = 1000;
        }

        if (craftingType == 0) {
            int posX = 20;
            int posY = 30;
            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 3; x++) {
                    craftingSlots.get(x + y * 3 + 1).x = posX + 18 * x;
                    craftingSlots.get(x + y * 3 + 1).y = posY + y * 18;
                }
            }

            craftingSlots.get(0).x = 107;
            craftingSlots.get(0).y = posY + 18;
        }
        else if (craftingType == 1) {
            craftingSlots.get(0).x = 90 - 18;
            craftingSlots.get(0).y = 20;
            craftingSlots.get(1).x = 90 + 18;
            craftingSlots.get(1).y = 20;

            for (int i = 2; i < 11; i++) {
                craftingSlots.get(i).x = 20 + (i - 2) * 18;
                craftingSlots.get(i).y = 50;

                craftingSlots.get(i + 9).x = 20 + (i - 2) * 18;
                craftingSlots.get(i + 9).y = 68;
            }
        }
    }

    @Nullable
    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot slot = getSlot(index);

        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            ItemStack result = stack.copy();

            if (index >= 36) {
                if (!moveItemStackTo(stack, 0, 36, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (stack.getBurnTime(null) == 0 || !moveItemStackTo(stack, 36, 36 + inventoryCache.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }

            if (stack.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            }
            else {
                slot.setChanged();
            }

            slot.onTake(player, stack);

            return result;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return true;
    }

}
