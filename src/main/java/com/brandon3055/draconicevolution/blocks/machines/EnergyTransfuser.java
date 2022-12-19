package com.brandon3055.draconicevolution.blocks.machines;

import codechicken.lib.raytracer.IndexedVoxelShape;
import codechicken.lib.raytracer.MultiIndexedVoxelShape;
import codechicken.lib.raytracer.VoxelShapeCache;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Transformation;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.blocks.EntityBlockBCore;
import com.brandon3055.draconicevolution.init.DEContent;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Created by brandon3055 on 12/12/2020.
 */
public class EnergyTransfuser extends EntityBlockBCore {
    private static final VoxelShape SHAPE;

    private static final IndexedVoxelShape BASE_SHAPE;
    private static final IndexedVoxelShape[] SLOTS = new IndexedVoxelShape[4];

    static {
        double p = 1D / 16D, hp = p / 2D;

        BASE_SHAPE = new IndexedVoxelShape(Shapes.create(hp, hp, hp, 1 - hp, 1 - hp, 1 - hp), -1);
        Cuboid6 interactFace = new Cuboid6(p * 3, p * 3, hp / 2, 1 - (p * 3), 1 - (p * 3), p);

        Direction[] dirs = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        int i = 0;
        for (Direction dir : dirs) {
            Transformation rotation = Rotation.quarterRotations[dir.get2DDataValue() ^ 2].at(Vector3.CENTER);
            SLOTS[i] = new IndexedVoxelShape(VoxelShapeCache.getShape(interactFace.copy().apply(rotation)), i);
            i++;
        }

        ImmutableSet.Builder<IndexedVoxelShape> cuboids = ImmutableSet.builder();
        cuboids.add(BASE_SHAPE);
        cuboids.add(SLOTS);
        SHAPE = new MultiIndexedVoxelShape(BASE_SHAPE, cuboids.build());
    }

    public EnergyTransfuser(Properties properties) {
        super(properties);
        setBlockEntity(() -> DEContent.tile_energy_transfuser, true);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}