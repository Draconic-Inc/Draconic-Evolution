//package com.brandon3055.draconicevolution.blocks.tileentity;
//
//
//import com.brandon3055.brandonscore.blocks.TileBCore;
//import com.brandon3055.brandonscore.lib.IInteractTile;
//import com.brandon3055.brandonscore.lib.Vec3I;
//import com.brandon3055.brandonscore.lib.datamanager.DataFlags;
//import com.brandon3055.brandonscore.lib.datamanager.ManagedString;
//import com.brandon3055.brandonscore.lib.datamanager.ManagedVec3I;
//import com.brandon3055.draconicevolution.init.DEContent;
//import net.minecraft.core.BlockPos;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.InteractionHand;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.Blocks;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.phys.BlockHitResult;
//import net.minecraftforge.registries.ForgeRegistries;
//
///**
// * Created by brandon3055 on 13/4/2016.
// */
//public class TileCoreStructure extends TileBCore implements IMultiBlockPart, IInteractTile {
//
//    public final ManagedVec3I coreOffset = register(new ManagedVec3I("core_offset", new Vec3I(0, -1, 0), DataFlags.SAVE_NBT_SYNC_TILE, DataFlags.SYNC_ON_SET));
//    public final ManagedString blockName = register(new ManagedString("block_name", DataFlags.SAVE_NBT_SYNC_TILE, DataFlags.SYNC_ON_SET));
//
//    public TileCoreStructure(BlockPos pos, BlockState state) {
//        super(DEContent.tile_structure_block, pos, state);
//    }
//
//    //region IMultiBlock
//
////    @Override
////    public boolean isStructureValid() {
////        return getController() != null && getController().isStructureValid();
////    }
////
//    @Override
//    public IMultiBlockPart getController() { //Will just remove IMultiBlockPart and just have a TileStructureBLock. This will simply return BlockEntity
//        BlockEntity tile = level.getBlockEntity(getCorePos());
//        if (tile instanceof IMultiBlockPart) {
//            return (IMultiBlockPart) tile;
//        }
//        else if (!level.isClientSide) {
//            revert();
//        }
//
//        return null;
//    }
//
//    @Override
//    public boolean validateStructure() {
//        IMultiBlockPart master = getController();
//        if (master == null) {
//            revert();
//            return false;
//        }
//        else return master.validateStructure();
//    }
//
//    //endregion
//
//
//    @Override
//    public boolean onBlockActivated(BlockState state, Player player, InteractionHand handIn, BlockHitResult hit) {
//        IMultiBlockPart controller = getController();
//
//        if (controller instanceof TileEnergyCoreStabilizer) {
//            ((TileEnergyCoreStabilizer) controller).onBlockActivated(state, player, handIn, hit);
//        }
//        else if (controller instanceof TileEnergyCore) {
////            ((TileEnergyCore) controller).onStructureClicked(level, worldPosition, state, player);
//        }
//        else if (controller instanceof TileEnergyPylon) {
//            ((TileEnergyPylon) controller).invertIO();
//        }
//
//        return true;
//    }
//
//    public void revert() {
//        if (level.isClientSide) return;
//        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName.get()));
//        if (block != Blocks.AIR) {
//            level.setBlockAndUpdate(worldPosition, block.defaultBlockState());
//        }
//        else {
//            level.removeBlock(worldPosition, false);
//        }
//    }
//
//    public void setController(IMultiBlockPart controller) {
//        coreOffset.set(new Vec3I(worldPosition.subtract(((BlockEntity) controller).getBlockPos())));
////        DelayedTask.run(100, () -> dataManager.forceSync());
////        ProcessHandler.addProcess(new DelayedTask.Task(10, () -> dataManager.forceSync()));
//    }
//
//    private BlockPos getCorePos() {
//        return worldPosition.offset(-coreOffset.get().x, -coreOffset.get().y, -coreOffset.get().z);
//    }
//
////    @Override
////    public SPacketUpdateTileEntity getUpdatePacket() {
////        CompoundNBT compound = new CompoundNBT();
////        compound.putString("BlockName", blockName);
////        coreOffset.toNBT(compound);
////        return new SPacketUpdateTileEntity(pos, 0, compound);
////    }
////
////    @Override
////    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
////        blockName = pkt.getNbtCompound().getString("BlockName");
////        coreOffset.fromNBT(pkt.getNbtCompound());
////    }
////
////    @Override
////    public void writeExtraNBT(CompoundNBT compound) {
////        compound.putString("BlockName", blockName);
////    }
////
////    @Override
////    public void readExtraNBT(CompoundNBT compound) {
////        blockName = compound.getString("BlockName");
////    }
////
////    @Override
////    public Iterable<BlockPos> getBlocksForFrameMove() {
////        IMultiBlockPart controller = getController();
////        if (controller instanceof TileEnergyCoreStabilizer) {
////            return ((TileEnergyCoreStabilizer) controller).getBlocksForFrameMove();
////        }
////        return Collections.emptyList();
////    }
////
////    @Override
////    public EnumActionResult canMove() {
////        IMultiBlockPart controller = getController();
////        if (controller instanceof TileEnergyCoreStabilizer) {
////            return ((TileEnergyCoreStabilizer) controller).canMove();
////        }
////        else if (blockName.equals("draconicevolution:particle_generator")) {
////            return EnumActionResult.FAIL;
////        }
////        return EnumActionResult.SUCCESS;
////    }
//}
