package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.inventory.ContainerBCBase;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by brandon3055 on 10/02/2017.
 */
public class ContainerReactor extends ContainerBCBase<TileReactorCore> {

    public ContainerReactor(EntityPlayer player, TileReactorCore tile) {
        super(player, tile);
    }
}
