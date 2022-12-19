package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.blocks.EntityBlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileCreativeOPCapacitor;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 19/07/2016.
 */
public class CreativeOPSource extends EntityBlockBCore {

    public CreativeOPSource(Properties properties) {
        super(properties);
        setBlockEntity(() -> DEContent.tile_creative_op_capacitor, true);
    }
}
