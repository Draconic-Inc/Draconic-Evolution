package com.brandon3055.draconicevolution.inventory;

//
///**
// * Created by brandon3055 on 31/05/2016.
// */
//public class ContainerUpgradeModifier extends ContainerBCBase<TileUpgradeModifier> {
//
//    private boolean slotsActive = true;
//
//    public ContainerUpgradeModifier(PlayerEntity player, TileUpgradeModifier tile){
//        super(player, tile);
//
//        for (int x = 0; x < 9; x++) {
//            addSlotToContainer(new Slot(player.inventory, x, 8 + 18 * x, 109 + 58));
//        }
//
//        for (int y = 0; y < 3; y++) {
//            for (int x = 0; x < 9; x++) {
//                addSlotToContainer(new Slot(player.inventory, x + y * 9 + 9, 8 + 18 * x, 111 + y * 18));
//            }
//        }
//
//        addSlotToContainer(new SlotUpgradable(tile, 0, 112, 48));
//    }
//
//    @Override
//    public void detectAndSendChanges() {
//        super.detectAndSendChanges();
//        for (int i = 0; i < this.listeners.size(); ++i)
//        {
//            IContainerListener icrafting = this.listeners.get(i);
//            icrafting.sendProgressBarUpdate(this, 0, 0);
//        }
//        updateSlotState();
//    }
//
//    @Override
//    public void updateProgressBar(int index, int value) {
//        super.updateProgressBar(index, value);
//        if (index == 0) {
//            updateSlotState();
//        }
//    }
//
//    private void updateSlotState(){
//        if (tile.getStackInSlot(0) != null && tile.getStackInSlot(0).getItem() instanceof IUpgradableItem && slotsActive) {
//            for (Object o : inventorySlots) {
//                if (o instanceof Slot && !(o instanceof SlotUpgradable)) ((Slot)o).xDisplayPosition += 1000;
//            }
//            slotsActive = false;
//        }
//        else if ((tile.getStackInSlot(0) == null || !(tile.getStackInSlot(0).getItem() instanceof IUpgradableItem)) && !slotsActive) {
//            for (Object o : inventorySlots) {
//                if (o instanceof Slot && !(o instanceof SlotUpgradable)) ((Slot)o).xDisplayPosition -= 1000;
//            }
//            slotsActive = true;
//        }
//    }
//
//    @Override
//    public boolean canInteractWith(PlayerEntity playerIn) {
//        return tile.isUseableByPlayer(playerIn);
//    }
//
//    @Override
//    public ItemStack transferStackInSlot(PlayerEntity player, int i)
//    {
//        Slot slot = getSlot(i);
//
//        if (slot != null && slot.getHasStack())
//        {
//            ItemStack stack = slot.getStack();
//            ItemStack result = stack.copy();
//
//            if (i >= 36){
//                if (!mergeItemStack(stack, 0, 36, false)){
//                    return null;
//                }
//            }else if (!mergeItemStack(stack, 36, 36 + tile.getSizeInventory(), false)){
//                return null;
//            }
//
//            if (stack.stackSize == 0) {
//                slot.putStack(null);
//            }else{
//                slot.onSlotChanged();
//            }
//
//            slot.onPickupFromSlot(player, stack);
//
//            return result;
//        }
//
//        return null;
//    }
//
//    public static class SlotUpgradable extends Slot {
//
//        public SlotUpgradable(IInventory inventory1, int slot, int x, int y) {
//            super(inventory1, slot, x, y);
//        }
//
//        @Override
//        public boolean isItemValid(ItemStack stack) {
//            return super.isItemValid(stack);
//        }
//    }
//}
