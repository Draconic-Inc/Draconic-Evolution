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
                entity.readFromItemStack(stack, getModuleContext());
                if (getGrid().attemptInstall(entity)) {
                    stack.shrink(1);
                    return ItemStack.EMPTY;
                }
            }
        }

        return super.transferStackInSlot(player, i);
    }

    @OnlyIn(Dist.CLIENT)
    public void clientTick() {}

    public abstract ModuleContext getModuleContext();

    public abstract void onGridChange();
}
