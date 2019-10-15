package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.inventory.ContainerBCBase;
import com.brandon3055.brandonscore.inventory.SlotCheckValid;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGenerator;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerGenerator extends ContainerBCBase<TileGenerator> {

    public ContainerGenerator(EntityPlayer player, TileGenerator tile) {
        super(player, tile);
        addPlayerSlots(8, 84);

        addSlotToContainer(new SlotCheckValid(tile.itemHandler, 0, 64, 35));
    }
}