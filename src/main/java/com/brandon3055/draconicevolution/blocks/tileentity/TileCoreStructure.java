package com.brandon3055.draconicevolution.blocks.tileentity;


import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.lib.datamanager.DataFlags;
import com.brandon3055.brandonscore.lib.datamanager.ManagedVec3I;
import com.brandon3055.draconicevolution.DEContent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Created by brandon3055 on 13/4/2016.
 */
public class TileCoreStructure extends TileBCore implements IMultiBlockPart/*, IMovableStructure*/ {

    public final ManagedVec3I coreOffset = register(new ManagedVec3I("core_offset", new Vec3I(0, -1, 0), DataFlags.SAVE_NBT_SYNC_CONTAINER));
    public String blockName = "";

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
        else {
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

    public boolean onTileClicked(PlayerEntity player, BlockState state) {
//        IMultiBlockPart controller = getController();
//
//        if (controller instanceof TileEnergyCoreStabilizer) {
//            ((TileEnergyCoreStabilizer) controller).onTileClicked(world, pos, state, player);
//        }
//        else if (controller instanceof TileEnergyStorageCore) {
//            ((TileEnergyStorageCore) controller).onStructureClicked(world, pos, state, player);
//        }
//        else if (controller instanceof TileEnergyPylon) {
//            ((TileEnergyPylon) controller).isOutputMode.invert();
//        }

        return true;
    }

    public void revert() {
        if (blockName.equals("draconicevolution:particle_generator")) {
//            world.setBlockState(pos, DEFeatures.particleGenerator.getDefaultState().with(ParticleGenerator.TYPE, "stabilizer"));
            return;
        }

        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName));
        if (block != Blocks.AIR) {
            world.setBlockState(pos, block.getDefaultState());
        }
        else {
            world.removeBlock(pos, false);
        }
    }

    public void setController(IMultiBlockPart controller) {
        coreOffset.set(new Vec3I(pos.subtract(((TileEntity) controller).getPos())));
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
