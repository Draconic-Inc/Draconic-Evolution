package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.inventory.ContainerBCBase;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by brandon3055 on 21/12/2016.
 */
public class ContainerEnergyCrystal extends ContainerBCBase<TileCrystalBase> {

    public ContainerEnergyCrystal(EntityPlayer player, TileCrystalBase tile) {
        super(player, tile);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        tile.detectAndSendContainerChanges(listeners);
    }
}
