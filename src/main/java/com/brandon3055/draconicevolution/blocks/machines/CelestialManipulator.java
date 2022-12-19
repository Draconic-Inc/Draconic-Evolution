package com.brandon3055.draconicevolution.blocks.machines;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.blocks.EntityBlockBCore;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Created by brandon3055 on 25/09/2016.
 */
public class CelestialManipulator extends EntityBlockBCore {

	protected static final VoxelShape SHAPE = Block.box(1.0, 0, 1.0, 15.0, 8.5, 15.0);

    public CelestialManipulator(Properties properties) {
        super(properties);
        canProvidePower = true;
        setBlockEntity(() -> DEContent.tile_celestial_manipulator, true);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
