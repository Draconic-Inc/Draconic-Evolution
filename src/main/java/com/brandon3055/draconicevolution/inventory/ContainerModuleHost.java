package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.inventory.ContainerBCore;
import com.brandon3055.brandonscore.inventory.ContainerSlotLayout;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleGrid;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 19/4/20.
 */
public abstract class ContainerModuleHost<T> extends ContainerBCore<T> {

    public ContainerModuleHost(@Nullable ContainerType<?> type, int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
        super(type, windowId, playerInv, extraData);
    }

    public ContainerModuleHost(@Nullable ContainerType<?> type, int windowId, PlayerInventory player, PacketBuffer extraData, ContainerSlotLayout.LayoutFactory<T> factory) {
        super(type, windowId, player, extraData, factory);
    }

    public ContainerModuleHost(@Nullable ContainerType<?> type, int windowId, PlayerInventory player) {
        super(type, windowId, player);
    }

    public ContainerModuleHost(@Nullable ContainerType<?> type, int windowId, PlayerInventory player, ContainerSlotLayout.LayoutFactory<T> factory) {
        super(type, windowId, player, factory);
    }

    public abstract ModuleGrid getGrid();

    public abstract ModuleHost getModuleHost();

    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int i) {
        Slot slot = getSlot(i);

        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            Module<?> module = ModuleItem.getModule(stack);
            if (module != null) {
                ModuleEntity entity = module.createEntity();
                entity.readFromItemStack(stack);
                if (getGrid().attemptInstall(entity)) {
                    stack.shrink(1);
                    return ItemStack.EMPTY;
                }
            }
        }

        return super.transferStackInSlot(player, i);

//        LazyOptional<IItemHandler> optional = getItemHandler();
//        if (optional.isPresent()) {
//            IItemHandler handler = optional.orElse(EmptyHandler.INSTANCE);
//            Slot slot = getSlot(i);
//
//            if (slot != null && slot.getHasStack()) {
//                ItemStack stack = slot.getStack();
//                ItemStack result = stack.copy();
//
//                //Transferring from tile to player
//                if (i >= 36) {
//                    if (!mergeItemStack(stack, 0, 36, false)) {
//                        return ItemStack.EMPTY; //Return if failed to merge
//                    }
//                }
//                else {
//                    //Transferring from player to tile
//                    if (!mergeItemStack(stack, 36, 36 + handler.getSlots(), false)) {
//                        return ItemStack.EMPTY;  //Return if failed to merge
//                    }
//                }
//
//                if (stack.getCount() == 0) {
//                    slot.putStack(ItemStack.EMPTY);
//                }
//                else {
//                    slot.onSlotChanged();
//                }
//
//                slot.onTake(player, stack);
//
//                return result;
//            }
//        }
//        return ItemStack.EMPTY;
    }

    @OnlyIn(Dist.CLIENT)
    public void clientTick() {}

    public abstract ModuleContext getModuleContext();
}
