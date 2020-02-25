package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.inventory.ContainerBCBase;
import com.brandon3055.brandonscore.inventory.ContainerSlotLayout.LayoutFactory;
import com.brandon3055.brandonscore.inventory.SlotCheckValid;
import com.brandon3055.draconicevolution.DEContent;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGrinder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class ContainerGrinder extends ContainerBCBase<TileGrinder> {

    public ContainerGrinder(int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
        this(DEContent.container_grinder, windowId, playerInv.player, getClientTile(extraData));
    }

    public ContainerGrinder(@Nullable ContainerType<?> type, int windowId, PlayerEntity player, TileGrinder tile) {
        super(type, windowId, player, tile);
    }

    //    public ContainerGrinder(PlayerEntity player, TileGrinder tile, LayoutFactory<TileGrinder> factory) {
//        super(player, tile, factory);
//    }
//
//    public ContainerGrinder(PlayerEntity player, TileGrinder tile) {
//        super(player, tile);
//
//        addPlayerSlots(8, 84);
//        addSlot(new SlotCheckValid(tile.itemHandler, 0, 26, 52));
//    }

    @Override
    public LazyOptional<IItemHandler> getItemHandler() {
        return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
    }
}
