package com.brandon3055.draconicevolution.blocks.machines;

import javax.annotation.Nullable;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.blocks.EntityBlockBCore;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Created by brandon3055 on 25/09/2016.
 */
public class EntityDetector extends EntityBlockBCore {
    public EntityDetector(Properties properties, boolean advanced) {
        super(properties);
        this.canProvidePower = true;
        setBlockEntity(() -> DEContent.tile_entity_detector, true);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return Shapes.box(0.0626, 0, 0.0626, 0.9375, 0.125, 0.9375);
    }
    
    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, @Nullable Direction side) {
        return true;
    }
    
//    @OnlyIn(Dist.CLIENT)
//    @Override
//    public void registerRenderer(Feature feature) {
//        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDetector.class, new RenderTileEntityDetector());
//    }
//
//    @Override
//    public boolean registerNormal(Feature feature) {
//        return true;
//    }
}
