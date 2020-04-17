package com.brandon3055.draconicevolution.blocks.tileentity;


import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.lib.IActivatableTile;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.lib.datamanager.DataFlags;
import com.brandon3055.brandonscore.lib.datamanager.ManagedString;
import com.brandon3055.brandonscore.lib.datamanager.ManagedVec3I;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Created by brandon3055 on 13/4/2016.
 */
public class TileCoreStructure extends TileBCore implements IMultiBlockPart, IActivatableTile {

    public final ManagedVec3I coreOffset = register(new ManagedVec3I("core_offset", new Vec3I(0, -1, 0), DataFlags.SAVE_NBT_SYNC_TILE, DataFlags.SYNC_ON_SET));
    public final ManagedString blockName = register(new ManagedString("block_name", DataFlags.SAVE_NBT_SYNC_TILE, DataFlags.SYNC_ON_SET));

    public TileCoreStructure() {
        super(DEContent.tile_core_structure);
    }

    //region IMultiBlock

    @Override
    public boolean isStructureValid() {
        return getController() != null && getController().isStructureValid();
    }
//
    @Override
    public IMultiBlockPart getController() {
        TileEntity tile = world.getTileEntity(getCorePos());
        if (tile instanceof IMultiBlockPart) {
            return (IMultiBlockPart) tile;
        }
        else if (!world.isRemote) {
            revert();
        }

        return null;
    }

    @Override
    public boolean validateStructure() {
        IMultiBlockPart master = getController();
        if (master == null) {
            revert();
            return false;
        }
        else return master.validateStructure();
    }

    //endregion


    @Override
    public boolean onBlockActivated(BlockState state, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        IMultiBlockPart controller = getController();

        if (controller instanceof TileEnergyCoreStabilizer) {
            ((TileEnergyCoreStabilizer) controller).onBlockActivated(state, player, handIn, hit);
        }
        else if (controller instanceof TileEnergyCore) {
            ((TileEnergyCore) controller).onStructureClicked(world, pos, state, player);
        }
        else if (controller instanceof TileEnergyPylon) {
            ((TileEnergyPylon) controller).invertIO();
        }

        return true;
    }

    public void revert() {
        if (world.isRemote) return;
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName.get()));
        if (block != Blocks.AIR) {
            world.setBlockState(pos, block.getDefaultState());
        }
        else {
            world.removeBlock(pos, false);
        }
    }

    public void setController(IMultiBlockPart controller) {
        coreOffset.set(new Vec3I(pos.subtract(((TileEntity) controller).getPos())));
//        DelayedTask.run(100, () -> dataManager.forceSync());
//        ProcessHandler.addProcess(new DelayedTask.Task(10, () -> dataManager.forceSync()));
    }

    private BlockPos getCorePos() {
        return pos.add(-coreOffset.get().x, -coreOffset.get().y, -coreOffset.get().z);
    }

//    @Override
//    public SPacketUpdateTileEntity getUpdatePacket() {
//        CompoundNBT compound = new CompoundNBT();
//        compound.putString("BlockName", blockName);
//        coreOffset.toNBT(compound);
//        return new SPacketUpdateTileEntity(pos, 0, compound);
//    }
//
//    @Override
//    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
//        blockName = pkt.getNbtCompound().getString("BlockName");
//        coreOffset.fromNBT(pkt.getNbtCompound());
//    }
//
//    @Override
//    public void writeExtraNBT(CompoundNBT compound) {
//        compound.putString("BlockName", blockName);
//    }
//
//    @Override
//    public void readExtraNBT(CompoundNBT compound) {
//        blockName = compound.getString("BlockName");
//    }
//
//    @Override
//    public Iterable<BlockPos> getBlocksForFrameMove() {
//        IMultiBlockPart controller = getController();
//        if (controller instanceof TileEnergyCoreStabilizer) {
//            return ((TileEnergyCoreStabilizer) controller).getBlocksForFrameMove();
//        }
//        return Collections.emptyList();
//    }
//
//    @Override
//    public EnumActionResult canMove() {
//        IMultiBlockPart controller = getController();
//        if (controller instanceof TileEnergyCoreStabilizer) {
//            return ((TileEnergyCoreStabilizer) controller).canMove();
//        }
//        else if (blockName.equals("draconicevolution:particle_generator")) {
//            return EnumActionResult.FAIL;
//        }
//        return EnumActionResult.SUCCESS;
//    }
}
