package com.brandon3055.draconicevolution.integration.funkylocomotion;

import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;

/**
 * Created by brandon3055 on 2/28/2018.
 */
public interface IMovableStructure {

    Iterable<BlockPos> getBlocksForFrameMove();

    default EnumActionResult canMove() {
        return EnumActionResult.SUCCESS;
    }
}
