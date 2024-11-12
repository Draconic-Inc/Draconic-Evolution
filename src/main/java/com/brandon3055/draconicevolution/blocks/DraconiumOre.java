package com.brandon3055.draconicevolution.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Created by brandon3055 on 18/3/2016.
 */
public class DraconiumOre extends DropExperienceBlock {

    public DraconiumOre(Properties properties) {
        super(ConstantInt.of(0), properties);
    }

    @Override
    public int getExpDrop(BlockState state, LevelReader level, RandomSource randomSource, BlockPos pos, int fortuneLevel, int silkTouchLevel) {
        return silkTouchLevel == 0 ? Mth.nextInt(randomSource, 5, 12) * fortuneLevel : 0;
    }
}
