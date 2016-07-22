package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.utils.LinkedHashList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

import java.util.List;

/**
 * Created by brandon3055 on 21/07/2016.
 */
public class ContainerRecipeBuilder extends Container {

    private EntityPlayer player;
    public InventoryCache inventoryCache = new InventoryCache(20);
    private List<Slot> craftingSlots = new LinkedHashList<Slot>();

    public ContainerRecipeBuilder(EntityPlayer player) {
        this.player = player;
        addSlots();
        arangeCraftingSlots(0);
    }

    public void addSlots() {
        int posX = 20;
        int posY = 145;
        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(player.inventory, x, posX + 18 * x, posY + 58));
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(player.inventory, x + y * 9 + 9, posX + 18 * x, posY + y * 18));
            }
        }

        for (int i = 0; i < inventoryCache.getSizeInventory(); i++) {
            Slot slot = new Slot(inventoryCache, i, 1000, 1000);
            addSlotToContainer(slot);
            craftingSlots.add(slot);
        }
    }

    public void arangeCraftingSlots(int craftingType) {
        for (Slot slot : craftingSlots) {
            slot.xDisplayPosition = slot.yDisplayPosition = 1000;
        }

        if (craftingType == 0) {
            int posX = 20;
            int posY = 30;
            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 3; x++) {
                    craftingSlots.get(x + y * 3 + 1).xDisplayPosition = posX + 18 * x;
                    craftingSlots.get(x + y * 3 + 1).yDisplayPosition = posY + y * 18;
                }
            }

            craftingSlots.get(0).xDisplayPosition = 107;
            craftingSlots.get(0).yDisplayPosition = posY + 18;
        }
        else if (craftingType == 1) {
            craftingSlots.get(0).xDisplayPosition = 90 - 18;
            craftingSlots.get(0).yDisplayPosition = 20;
            craftingSlots.get(1).xDisplayPosition = 90 + 18;
            craftingSlots.get(1).yDisplayPosition = 20;

            for (int i = 2; i < 11; i++){
                craftingSlots.get(i).xDisplayPosition = 20 + (i - 2) * 18;
                craftingSlots.get(i).yDisplayPosition = 50;

                craftingSlots.get(i + 9).xDisplayPosition = 20 + (i - 2) * 18;
                craftingSlots.get(i + 9).yDisplayPosition = 68;
            }
        }
    }



    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

}
