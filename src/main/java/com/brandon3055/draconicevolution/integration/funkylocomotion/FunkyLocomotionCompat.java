//package com.brandon3055.draconicevolution.integration.funkylocomotion;
//
//import codechicken.lib.reflect.ObfMapping;
//import codechicken.lib.reflect.ReflectionManager;
//import com.brandon3055.draconicevolution.DraconicEvolution;
//import com.brandon3055.draconicevolution.blocks.tileentity.TileDislocatorPedestal;
//import com.brandon3055.draconicevolution.blocks.tileentity.TileDislocatorReceptacle;
//import com.brandon3055.draconicevolution.blocks.tileentity.TilePortal;
//import com.rwtema.funkylocomotion.api.IMoveFactory;
//import com.rwtema.funkylocomotion.compat.CompatHandler;
//import com.rwtema.funkylocomotion.compat.ModCompat;
//import net.minecraft.nbt.CompoundNBT;
//import net.minecraft.tileentity.TileEntity;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//
///**
// * Created by brandon3055 on 2/27/2018.
// */
//@ModCompat(modid = DraconicEvolution.MODID)
//public class FunkyLocomotionCompat extends CompatHandler {
//
//    private static WrappedMoveFactory moveFactory = new WrappedMoveFactory();
//
//    @Override
//    public void init() {
////        Validate.notNull(FunkyRegistry.INSTANCE).registerProxy(IMovableStructure.class, FunkyCapabilities.ADV_STICKY_BLOCK, (World world, BlockPos pos) -> {
////            TileEntity tile = world.getTileEntity(pos);
////            if (tile instanceof IMovableStructure) {
////                return ((IMovableStructure) tile).getBlocksForFrameMove();
////            }
////            return Collections.emptyList();
////        });
////
////        Validate.notNull(FunkyRegistry.INSTANCE).registerProxy(IMovableStructure.class, FunkyCapabilities.MOVE_CHECK, (World world, BlockPos pos, @Nullable GameProfile profile) -> {
////            TileEntity tile = world.getTileEntity(pos);
////            if (tile instanceof IMovableStructure) {
////                return ((IMovableStructure) tile).canMove();
////            }
////            return EnumActionResult.FAIL;
////        });
////
////        Validate.notNull(FunkyRegistry.INSTANCE).registerMoveFactoryBlock(DEFeatures.dislocatorReceptacle, moveFactory);
////        Validate.notNull(FunkyRegistry.INSTANCE).registerMoveFactoryBlock(DEFeatures.portal, moveFactory);
////        Validate.notNull(FunkyRegistry.INSTANCE).registerMoveFactoryBlock(DEFeatures.dislocatorPedestal, moveFactory);
//    }
//
//    public static class WrappedMoveFactory implements IMoveFactory {
//
//        private static IMoveFactory parent;
//
//        static {
//            ObfMapping mapping = new ObfMapping(//
//                    "com/rwtema/funkylocomotion/factory/FactoryRegistry",//
//                    "getDefaultFactory",//
//                    "()Lcom/rwtema/funkylocomotion/api/IMoveFactory;"//
//            );
//            parent = ReflectionManager.callMethod(mapping, IMoveFactory.class, null);
//        }
//
//        @Override
//        public CompoundNBT destroyBlock(World world, BlockPos pos) {
//            return parent.destroyBlock(world, pos);
//        }
//
//        @Override
//        public boolean recreateBlock(World world, BlockPos pos, CompoundNBT tag) {
//            parent.recreateBlock(world, pos, tag);
//
//            TileEntity tile = world.getTileEntity(pos);
//            if (tile instanceof TilePortal) {
//                ((TilePortal) tile).frameMoving = true;
//            }
//            else if (tile instanceof TileDislocatorReceptacle) {
//                ((TileDislocatorReceptacle) tile).frameMoving = true;
//            }
//            else if (tile instanceof TileDislocatorPedestal) {
//                ((TileDislocatorPedestal) tile).checkIn();
//            }
//
//            return true;
//        }
//    }
//}
