package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.inventory.ContainerSlotLayout;
import com.brandon3055.brandonscore.inventory.PlayerSlot;
import com.brandon3055.draconicevolution.api.modules.capability.IModuleHost;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleGrid;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.init.ModuleCapability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.LazyOptional;

/**
 * Created by brandon3055 on 19/4/20.
 */
public class ContainerModularItem extends ContainerModuleHost<TileBCore> {

    private PlayerSlot slot;
    private ItemStack hostStack;
    private IModuleHost moduleHost;

    public ModuleGrid moduleGrid;

    public ContainerModularItem(int windowId, PlayerInventory player, PacketBuffer extraData, ContainerSlotLayout.LayoutFactory<TileBCore> factory) {
        super(DEContent.container_modular_item, windowId, player, extraData, factory);
        this.slot = PlayerSlot.fromBuff(extraData);
        this.onContainerOpen();
    }

    public ContainerModularItem(int windowId, PlayerInventory player, PlayerSlot itemSlot, ContainerSlotLayout.LayoutFactory<TileBCore> factory) {
        super(DEContent.container_modular_item, windowId, player, factory);
        this.slot = itemSlot;
        this.onContainerOpen();
    }

    public IModuleHost getModuleHost() {
        return moduleHost;
    }

    private void onContainerOpen() {
        hostStack = slot.getStackInSlot(player);
        LazyOptional<IModuleHost> optional = hostStack.getCapability(ModuleCapability.MODULE_HOST_CAPABILITY);
        if (optional.isPresent()) {
            moduleHost = optional.orElseThrow(RuntimeException::new);
            moduleGrid = new ModuleGrid(moduleHost);
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        if (moduleHost == null || hostStack != slot.getStackInSlot(player)) {
            return false;
        }
        if (moduleHost != hostStack.getCapability(ModuleCapability.MODULE_HOST_CAPABILITY).orElse(null)) {
            return false; //I dont think this is actually possible... But just in case.
        }
        return super.canInteractWith(playerIn);
    }
}
