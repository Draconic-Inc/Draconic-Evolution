package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.inventory.ContainerBCore;
import com.brandon3055.brandonscore.inventory.ContainerSlotLayout;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleGrid;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 19/4/20.
 */
public abstract class ContainerModuleHost<T> extends ContainerBCore<T> {

    public ContainerModuleHost(@Nullable MenuType<?> type, int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
        super(type, windowId, playerInv, extraData);
    }

    public ContainerModuleHost(@Nullable MenuType<?> type, int windowId, Inventory player, FriendlyByteBuf extraData, ContainerSlotLayout.LayoutFactory<T> factory) {
        super(type, windowId, player, extraData, factory);
    }

    public ContainerModuleHost(@Nullable MenuType<?> type, int windowId, Inventory player) {
        super(type, windowId, player);
    }

    public ContainerModuleHost(@Nullable MenuType<?> type, int windowId, Inventory player, ContainerSlotLayout.LayoutFactory<T> factory) {
        super(type, windowId, player, factory);
    }

    public abstract ModuleGrid getGrid();

    public abstract ModuleHost getModuleHost();

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        Slot slot = getSlot(i);

        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
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

        return super.quickMoveStack(player, i);
    }

    @OnlyIn(Dist.CLIENT)
    public void clientTick() {}

    public abstract ModuleContext getModuleContext();

    public abstract void onGridChange();
}
