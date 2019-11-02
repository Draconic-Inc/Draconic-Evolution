package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.inventory.ContainerBCBase;
import com.brandon3055.brandonscore.inventory.SlotCheckValid;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.items.IItemHandler;

public class ContainerGenerator extends ContainerBCBase<TileGenerator> {

    public ContainerGenerator(EntityPlayer player, TileGenerator tile) {
        super(player, tile);
        addPlayerSlots(8, 84);

        addSlotToContainer(new SlotCheckValid(tile.itemHandler, 0, 65, 29));
        addSlotToContainer(new SlotCheckValid(tile.itemHandler, 1, 83, 29));
        addSlotToContainer(new SlotCheckValid(tile.itemHandler, 2, 101, 29));
        addSlotToContainer(new SlotCheckValid(tile.itemHandler, 3, 26, 52));
    }

    @Override
    public IItemHandler getItemHandler() {
        return tile.itemHandler;
    }
}