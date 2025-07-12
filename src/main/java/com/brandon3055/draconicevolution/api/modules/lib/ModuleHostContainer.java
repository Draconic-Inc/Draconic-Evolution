package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.items.ModuleItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Created by brandon3055 on 17/11/2022
 */
public interface ModuleHostContainer {

    ModuleGrid getGrid();

    ModuleHost getModuleHost();

    int getGridWidth();

    int getGridHeight();

    ModuleContext getModuleContext();

    void onGridChange();

    default boolean quickMoveModule(ModuleHost host, Player player, Slot slot) {
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            Module<?> module = ModuleItem.getModule(stack);
            if (module != null) {
                ModuleEntity<?> entity = module.createEntity();
                entity.loadEntityFromStack(stack, getModuleContext());
                if (getGrid().attemptInstall(entity, host)) {
                    stack.shrink(1);
                    return true;
                }
            }
        }
        return false;
    }
}
