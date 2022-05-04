package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

/**
 * Created by brandon3055 on 18/3/2016.
 */
public class DraconiumBlock extends BlockBCore {

    public DraconiumBlock(Block.Properties properties) {
        super(properties);
    }

    @Override
    public float getEnchantPowerBonus(BlockState state, IWorldReader world, BlockPos pos) {
        return 4f;
    }
}
