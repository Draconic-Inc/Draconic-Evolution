package com.brandon3055.draconicevolution.blocks.machines;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileCraftingCore;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 11/06/2016.
 */
public class FusionCraftingCore extends BlockBCore /*implements IRenderOverride, ITileEntityProvider*/ {

    private static final VoxelShape SHAPE = VoxelShapes.box(0.0625, 0.0625, 0.0625, 0.9375, 0.9375, 0.9375);

    public FusionCraftingCore(Properties properties) {
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
        return new TileCraftingCore();
    }

//    @Override
//    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
//        TileEntity tile = world.getTileEntity(pos);
//
//        if (tile instanceof TileFusionCraftingCore) {
//            ((TileFusionCraftingCore) tile).updateInjectors();
//        }
//
//        if (!world.isRemote) {
//            FMLNetworkHandler.openGui(player, DraconicEvolution.instance, GuiHandler.GUIID_FUSION_CRAFTING, world, pos.getX(), pos.getY(), pos.getZ());
//        }
//        return true;
//    }
//
//    @OnlyIn(Dist.CLIENT)
//    @Override
//    public void registerRenderer(Feature feature) {
//        ClientRegistry.bindTileEntitySpecialRenderer(TileFusionCraftingCore.class, new RenderTileFusionCraftingCore());
//    }
//
//    @Override
//    public boolean registerNormal(Feature feature) {
//        return true;
//    }


    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }


    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (!world.isClientSide()) {
            if (isBlockPowered(world, pos)) {
                TileEntity tile = world.getBlockEntity(pos);
                if (tile instanceof TileCraftingCore) {
                    ((TileCraftingCore) tile).startCraft();
                }
            }
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getBlockEntity(pos);
        if (tile instanceof TileCraftingCore) {
            return ((TileCraftingCore) tile).getComparatorOutput();
        }
        return super.getAnalogOutputSignal(blockState, worldIn, pos);
    }
}
