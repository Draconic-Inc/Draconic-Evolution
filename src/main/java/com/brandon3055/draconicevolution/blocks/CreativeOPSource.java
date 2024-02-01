package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.EntityBlockBCore;
import com.brandon3055.draconicevolution.init.DEContent;

/**
 * Created by brandon3055 on 19/07/2016.
 */
public class CreativeOPSource extends EntityBlockBCore {

    public CreativeOPSource(Properties properties) {
        super(properties);
        setBlockEntity(DEContent.TILE_CREATIVE_OP_CAPACITOR::get, true);
    }
}
