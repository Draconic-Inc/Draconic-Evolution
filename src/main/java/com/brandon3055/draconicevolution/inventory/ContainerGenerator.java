package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.inventory.ContainerBCBase;
import com.brandon3055.brandonscore.inventory.SlotCheckValid;
import com.brandon3055.draconicevolution.DEContent;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGenerator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class ContainerGenerator extends ContainerBCBase<TileGenerator> {

    public ContainerGenerator(int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
        this(DEContent.container_generator, windowId, playerInv.player, getClientTile(extraData));
    }

    public ContainerGenerator(@Nullable ContainerType<?> type, int windowId, PlayerEntity player, TileGenerator tile) {
        super(type, windowId, player, tile);
        addPlayerSlots(8, 84);
        addSlot(new SlotCheckValid(tile.itemHandler, 0, 65, 29));
        addSlot(new SlotCheckValid(tile.itemHandler, 1, 83, 29));
        addSlot(new SlotCheckValid(tile.itemHandler, 2, 101, 29));
        addSlot(new SlotCheckValid(tile.itemHandler, 3, 26, 52));
    }


    @Override
    public LazyOptional<IItemHandler> getItemHandler() {
        return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
    }
}