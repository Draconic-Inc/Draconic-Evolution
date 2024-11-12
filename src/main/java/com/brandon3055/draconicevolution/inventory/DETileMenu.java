package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.inventory.ContainerBCTile;
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
import net.neoforged.fml.util.thread.EffectiveSide;
import org.jetbrains.annotations.Nullable;

/**
 * Created by brandon3055 on 18/11/2022
 */
public abstract class DETileMenu<T extends TileBCore> extends ContainerBCTile<T> implements ModuleHostContainer {

    protected ModuleGrid moduleGrid;
    protected ModuleHost moduleHost = null;

    public DETileMenu(@Nullable MenuType<?> type, int windowId, Inventory player, FriendlyByteBuf extraData) {
        super(type, windowId, player, extraData);
        initHost(tile, player);
    }

    public DETileMenu(@Nullable MenuType<?> type, int windowId, Inventory player, T tile) {
        super(type, windowId, player, tile);
        initHost(tile, player);
    }

    protected void initHost(T tile, Inventory player) {
        ModuleHost host = DECapabilities.Host.fromBlockEntity(tile);
        if (host != null) {
            this.moduleHost = host;
            this.moduleGrid = new ModuleGrid(this, player);
        }
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
