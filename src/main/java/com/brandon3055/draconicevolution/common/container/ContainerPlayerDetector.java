package com.brandon3055.draconicevolution.common.container;

import com.brandon3055.draconicevolution.client.gui.GUIPlayerDetector;
import com.brandon3055.draconicevolution.common.inventory.SlotOpaqueBlock;
import com.brandon3055.draconicevolution.common.tileentities.TilePlayerDetectorAdvanced;
import java.util.Iterator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerPlayerDetector extends Container {

    private TilePlayerDetectorAdvanced tileDetector;
    private GUIPlayerDetector gui = null;

    public ContainerPlayerDetector(InventoryPlayer invPlayer, TilePlayerDetectorAdvanced tileDetector) {
        this.tileDetector = tileDetector;

        bindPlayerInventory(invPlayer);
        addContainerSlots(tileDetector);
        updateContainerSlots();
    }

    public ContainerPlayerDetector(
            InventoryPlayer invPlayer, TilePlayerDetectorAdvanced tileDetector, GUIPlayerDetector gui) {
        this.tileDetector = tileDetector;
        this.gui = gui;

        bindPlayerInventory(invPlayer);
        addContainerSlots(tileDetector);
        updateContainerSlots();
    }

    private void bindPlayerInventory(InventoryPlayer invPlayer) {
        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(invPlayer, x, 8 + 18 * x, 174));
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 8 + 18 * x, 116 + y * 18));
            }
        }
    }

    public void addContainerSlots(TilePlayerDetectorAdvanced tileDetector) {
        addSlotToContainer(new SlotOpaqueBlock(tileDetector, 0, 145, 15));
    }

    public void updateContainerSlots() {
        if (gui == null) {
            return;
        }

        Iterator<Slot> i1 = inventorySlots.iterator();
        while (i1.hasNext()) {
            Slot sl = i1.next();
            if (sl instanceof SlotOpaqueBlock) {
                if (gui.showInvSlots) {
                    sl.xDisplayPosition = 143;
                    sl.yDisplayPosition = 20;
                } else {
                    sl.xDisplayPosition = -1000;
                    sl.yDisplayPosition = -1000;
                }
            } else {
                if (gui.showInvSlots) {
                    if (sl.slotNumber < 9) {
                        sl.xDisplayPosition = 8 + 18 * sl.slotNumber;
                        sl.yDisplayPosition = 174;
                    } else if (sl.slotNumber < 18) {
                        sl.xDisplayPosition = 8 + 18 * (sl.slotNumber - 9);
                        sl.yDisplayPosition = 116 + 0 * 18;
                    } else if (sl.slotNumber < 27) {
                        sl.xDisplayPosition = 8 + 18 * (sl.slotNumber - 18);
                        sl.yDisplayPosition = 116 + 1 * 18;
                    } else if (sl.slotNumber < 36) {
                        sl.xDisplayPosition = 8 + 18 * (sl.slotNumber - 27);
                        sl.yDisplayPosition = 116 + 2 * 18;
                    }

                } else {
                    sl.xDisplayPosition = -1000;
                    sl.yDisplayPosition = -1000;
                }
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileDetector.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int i) {
        //		Slot slot = getSlot(i);
        //
        //
        //		if (slot != null && slot.getHasStack())
        //		{
        //			ItemStack stack = slot.getStack();
        //			ItemStack result = stack.copy();
        //			Block block = Block.getBlockFromItem(stack.getItem());
        //
        //			if (i >= 36){
        //				if (!mergeItemStack(stack, 0, 36, false)){
        //					return null;
        //				}
        //			}else if (!(block.isOpaqueCube() && block.renderAsNormalBlock()) || !mergeItemStack(stack, 36, 36 +
        // tileDetector.getSizeInventory(), false)){
        //				return null;
        //			}
        //
        //			if (stack.stackSize == 0) {
        //				slot.putStack(null);
        //			}else{
        //				slot.onSlotChanged();
        //			}
        //
        //			slot.onPickupFromSlot(player, stack);
        //
        //			return result;
        //		}

        return null;
    }

    public TilePlayerDetectorAdvanced getTileDetector() {
        return tileDetector;
    }

    @Override
    public ItemStack slotClick(int slot, int button, int par3, EntityPlayer par4EntityPlayer) {
        return super.slotClick(slot, button, par3, par4EntityPlayer);
        //		if (par3 == 4)
        //			return super.slotClick(slot, button, 0, par4EntityPlayer);
        //		else
        //			return super.slotClick(slot, button, par3, par4EntityPlayer);
    }
}
