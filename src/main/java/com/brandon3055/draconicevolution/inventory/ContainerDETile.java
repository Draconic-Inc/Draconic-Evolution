package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.inventory.ContainerBCTile;
import com.brandon3055.brandonscore.inventory.ContainerSlotLayout;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleGrid;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleHostContainer;
import com.brandon3055.draconicevolution.api.modules.lib.TileModuleContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import org.jetbrains.annotations.Nullable;

/**
 * Created by brandon3055 on 18/11/2022
 */
public class ContainerDETile<T extends TileBCore> extends ContainerBCTile<T> implements ModuleHostContainer {

    private ModuleGrid moduleGrid;
    private ModuleHost moduleHost = null;

    public ContainerDETile(@Nullable MenuType<?> type, int windowId, Inventory player, FriendlyByteBuf extraData) {
        super(type, windowId, player, extraData);
        initHost(tile, player);
    }

    public ContainerDETile(@Nullable MenuType<?> type, int windowId, Inventory player, FriendlyByteBuf extraData, ContainerSlotLayout.LayoutFactory<T> factory) {
        super(type, windowId, player, extraData, factory);
        initHost(tile, player);
    }

    public ContainerDETile(@Nullable MenuType<?> type, int windowId, Inventory player, T tile) {
        super(type, windowId, player, tile);
        initHost(tile, player);
    }

    public ContainerDETile(@Nullable MenuType<?> type, int windowId, Inventory player, T tile, ContainerSlotLayout.LayoutFactory<T> factory) {
        super(type, windowId, player, tile, factory);
        initHost(tile, player);
    }

    private void initHost(T tile, Inventory player) {
        LazyOptional<ModuleHost> opt = tile.getCapability(DECapabilities.MODULE_HOST_CAPABILITY);
        opt.ifPresent(host -> {
            this.moduleHost = host;
            this.moduleGrid = new ModuleGrid(this, player);
        });
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        if (moduleHost != null && quickMoveModule(player, getSlot(i))) {
            return ItemStack.EMPTY;
        }
        return super.quickMoveStack(player, i);
    }

    @Override
    public ModuleGrid getGrid() {
        return moduleGrid;
    }

    @Override
    public ModuleHost getModuleHost() {
        return moduleHost;
    }

    @Override
    public ModuleContext getModuleContext() {
        return new TileModuleContext(tile);
    }

    @Override
    public void clicked(int p_150400_, int p_150401_, ClickType p_150402_, Player p_150403_) {
        super.clicked(p_150400_, p_150401_, p_150402_, p_150403_);
    }

    @Override
    public void onGridChange() {
        if (EffectiveSide.get().isServer()) {
            for (int i = 0; i < this.slots.size(); ++i) {
                ItemStack itemstack = this.slots.get(i).getItem();
                ItemStack itemstack1 = this.lastSlots.get(i);
                if (!ItemStack.matches(itemstack1, itemstack)) {
                    itemstack1 = itemstack.copy();
                    this.lastSlots.set(i, itemstack1);
                    for (ContainerListener icontainerlistener : this.containerListeners) {
                        icontainerlistener.slotChanged(this, i, itemstack1);
                    }
                }
            }
        }
    }
}
