package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class ParticleGenerator extends BlockBCore /*implements EntityBlock*/ {
//    public static final PropertyString TYPE = new PropertyString("type", "normal", "inverted", "stabilizer", "stabilizer2");

    public ParticleGenerator(Block.Properties properties) {
        super(properties);
//        setBlockEntity(() -> DEContent.tile_particle_generator, true);
    }

    //region BlockState
//    @Override
//    protected BlockStateContainer createBlockState() {
//        return new BlockStateContainer(this, TYPE);
//    }
//
//    @Override
//    public BlockState getActualState(BlockState state, IBlockAccess worldIn, BlockPos pos) {
//        return state;
//    }
//
//    @Override
//    public BlockState getStateFromMeta(int meta) {
//        return this.getDefaultState().withProperty(TYPE, TYPE.fromMeta(meta));
//    }
//
//    @Override
//    public int getMetaFromState(BlockState state) {
//        return TYPE.toMeta(state.getValue(TYPE));
//    }

    //endregion

    //region Standard Block Methods


//    @Override
//    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
//        items.add(new ItemStack(this, 1, 0));
//        items.add(new ItemStack(this, 1, 2));
//    }


//    @Override
//    public boolean isOpaqueCube(BlockState state) {
//        return false;
//    }
//
//    @Override
//    public boolean isNormalCube(BlockState state, IBlockAccess world, BlockPos pos) {
//        return false;
//    }
//
//    @Override
//    public boolean isFullCube(BlockState state) {
//        return false;
//    }
//
//    @Override
//    public int damageDropped(BlockState state) {
//        return Math.min(getMetaFromState(state), 2);
//    }


//    @Override
//    public boolean hasTileEntity(BlockState state) {
//        return false;
//    }
//
//    @Nullable
//    @Override
//    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
////        return (meta == 0 || meta == 1) ? new TileParticleGenerator() : meta == 2 || meta == 3 ? new TileEnergyCoreStabilizer() : null;
//        return null;
//    }
//
//    @Override
//    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
//        return state.get(TYPE).equals("normal") || state.get(TYPE).equals("inverted");
//
//    }
//
//    //endregion
//
//    //region Render
//
//
//    @Override
//    public int getLightValue(BlockState state) {
//        return (state.get(TYPE).equals("stabilizer") || state.get(TYPE).equals("stabilizer2")) ? 10 : 0;
//    }
//
//    @Override
//    public BlockRenderType getRenderType(BlockState state) {
//        return state.get(TYPE).equals("stabilizer2") ? BlockRenderType.INVISIBLE : super.getRenderType(state);
//    }
//
//    @Override
//    public VoxelShape getRaytraceShape(BlockState state, IBlockReader world, BlockPos pos) {
////        TileEntity tile = world.getTileEntity(pos);
////
////        if (tile instanceof TileEnergyCoreStabilizer) {
////            if (((TileEnergyCoreStabilizer) tile).isValidMultiBlock.get()) {
////                AxisAlignedBB bb = new AxisAlignedBB(tile.getPos());
////
////                if (((TileEnergyCoreStabilizer) tile).multiBlockAxis.getPlane() == Direction.Plane.HORIZONTAL) {
////                    if (((TileEnergyCoreStabilizer) tile).multiBlockAxis == Direction.Axis.X) {
////                        bb = bb.grow(0, 1, 1);
////                    } else {
////                        bb = bb.grow(1, 1, 0);
////                    }
////                } else {
////                    bb = bb.grow(1, 0, 1);
////                }
////                return bb;
////            }
////        }
//        return super.getRaytraceShape(state, world, pos);
//    }


    //
//    @Override
//    public AxisAlignedBB getSelectedBoundingBox(BlockState state, World world, BlockPos pos) {
//        TileEntity tile = world.getTileEntity(pos);
//
//        if (tile instanceof TileEnergyCoreStabilizer) {
//            if (((TileEnergyCoreStabilizer) tile).isValidMultiBlock.get()) {
//                AxisAlignedBB bb = new AxisAlignedBB(tile.getPos());
//
//                if (((TileEnergyCoreStabilizer) tile).multiBlockAxis.getPlane() == Direction.Plane.HORIZONTAL) {
//                    if (((TileEnergyCoreStabilizer) tile).multiBlockAxis == Direction.Axis.X) {
//                        bb = bb.grow(0, 1, 1);
//                    }
//                    else {
//                        bb = bb.grow(1, 1, 0);
//                    }
//                }
//                else {
//                    bb = bb.grow(1, 0, 1);
//                }
//                return bb;
//            }
//        }
//
//        return super.getSelectedBoundingBox(state, world, pos);
//    }

    //endregion

    //region Place/Break stuff

//
//    @Override
//    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
//        if (state.get(TYPE).equals("normal") || state.get(TYPE).equals("inverted")) {
//            if (player.isShiftKeyDown()) {
//                world.setBlockState(pos, state.with(TYPE, state.get(TYPE).equals("normal") ? "inverted" : "normal"));
//            } else if (world.isRemote) {
////                player.openGui(DraconicEvolution.instance, GuiHandler.GUIID_PARTICLEGEN, world, pos.getX(), pos.getY(), pos.getZ());
//            }
//        } else {
//            TileEntity tile = world.getTileEntity(pos);
//
//            if (tile instanceof TileEnergyCoreStabilizer) {
//                ((TileEnergyCoreStabilizer) tile).onTileClicked(world, pos, state, player);
//            }
//
//        }
//        return true;
//    }
//
//    @Override
//    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
////        world.setBlockState(pos, state.with(TYPE, TYPE.fromMeta(stack.getItemDamage())));
//
//        TileEntity tile = world.getTileEntity(pos);
//
//        if (tile instanceof TileEnergyCoreStabilizer) {
//            ((TileEnergyCoreStabilizer) tile).onPlaced();
//        } else {
//            super.onBlockPlacedBy(world, pos, state, placer, stack);
//        }
//    }
//
//    @Override
//    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
//        TileEntity tile = world.getTileEntity(pos);
//
//        if (tile instanceof TileEnergyCoreStabilizer) {
//            if (((TileEnergyCoreStabilizer) tile).isValidMultiBlock.get()) {
//                ((TileEnergyCoreStabilizer) tile).deFormStructure();
//            }
//            TileEnergyStorageCore core = ((TileEnergyCoreStabilizer) tile).getCore();
//
//            if (core != null) {
//                world.removeTileEntity(pos);
//                ((TileEnergyCoreStabilizer) tile).validateStructure();
//            }
//
//        }
//        super.onReplaced(state, world, pos, newState, isMoving);
//    }


    //endregion

//    //region Registry
//
//
//    @Override
//    public void handleCustomRegistration(Feature feature) {
//        GameRegistry.registerTileEntity(TileParticleGenerator.class, feature.getRegistryName() + ".particle");
//        GameRegistry.registerTileEntity(TileEnergyCoreStabilizer.class, feature.getRegistryName() + ".stabilize");
//    }
//
//    @OnlyIn(Dist.CLIENT)
//    @Override
//    public void registerRenderer(Feature feature) {
//        ClientRegistry.bindTileEntitySpecialRenderer(TileEnergyCoreStabilizer.class, new RenderTileECStabilizer());
//        //ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 10, new ModelResourceLocation(getRegistryName(), "type=stabilizer2"));
//    }
//
//    @Override
//    public boolean registerNormal(Feature feature) {
//        return true;
//    }
//
//    //endregion
}
