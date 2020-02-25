package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.inventory.ContainerBCBase;
import com.brandon3055.draconicevolution.DEContent;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 21/12/2016.
 */
public class ContainerEnergyCrystal extends ContainerBCBase<TileCrystalBase> {

    public ContainerEnergyCrystal(int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
        this(DEContent.container_energy_crystal, windowId, playerInv.player, getClientTile(extraData));
    }

    public ContainerEnergyCrystal(@Nullable ContainerType<?> type, int windowId, PlayerEntity player, TileCrystalBase tile) {
        super(type, windowId, player, tile);
    }

//    public ContainerEnergyCrystal(PlayerEntity player, TileCrystalBase tile) {
//        super(player, tile);
//    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        tile.detectAndSendContainerChanges(listeners);
    }
}
