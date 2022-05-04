package com.brandon3055.draconicevolution.blocks.machines;

import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.raytracer.SubHitVoxelShape;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Transformation;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyTransfuser;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 12/12/2020.
 */
public class EnergyTransfuser extends BlockBCore {
    private static final VoxelShape SHAPE;
    static {
        double p = 1D / 16D, hp = p / 2D;
        IndexedCuboid6 BASE = new IndexedCuboid6(-1, new Cuboid6(hp, hp, hp, 1 - hp, 1 - hp, 1 - hp));
        VoxelShape BASE_SHAPE = VoxelShapes.create(BASE.aabb());
        List<IndexedCuboid6> cuboids = new ArrayList<>();
        cuboids.add(BASE);
        Cuboid6 interactFace = new Cuboid6(p * 3, p * 3, hp / 2, 1 - (p * 3), 1 - (p * 3), p);
        Direction[] dirs = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        int i = 0;
        for (Direction dir : dirs) {
            Transformation rotation = Rotation.quarterRotations[dir.get2DDataValue() ^ 2].at(Vector3.CENTER);
            cuboids.add(new IndexedCuboid6(i, interactFace.copy().apply(rotation)));
            i++;
        }
        SHAPE = new SubHitVoxelShape(BASE_SHAPE, cuboids);
    }

    public EnergyTransfuser(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isBlockFullCube() {
        return false;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEnergyTransfuser();
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }
//
}