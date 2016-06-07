package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.api.IMultiBlock;
import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.network.wrappers.SyncableVec3I;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.ParticleGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.util.LinkedList;

/**
 *  Created by brandon3055 on 13/4/2016.
 */
public class TileInvisECoreBlock extends TileBCBase implements IMultiBlock {

    public final SyncableVec3I coreOffset = new SyncableVec3I(new Vec3I(0, -1, 0), true, false, true);//todo change core to controller
    public String blockName = "";

    public TileInvisECoreBlock(){
        registerSyncableObject(coreOffset, true);
    }

    //region IMultiBlock

    @Override
    public boolean isStructureValid() {
        return getController() != null && getController().isStructureValid();
    }

    @Override
    public boolean isController() {
        return false;
    }

    @Override
    public IMultiBlock getController() {
        TileEntity tile = worldObj.getTileEntity(getCorePos());
        if (tile instanceof IMultiBlock){
            return (IMultiBlock)tile;
        }
        else {
            revert();
        }

        return null;
    }

    @Override
    public boolean hasSatelliteStructures() {
        return false;
    }

    @Override
    public LinkedList<IMultiBlock> getSatelliteControllers() {
        return null;
    }

    @Override
    public boolean validateStructure() {
        IMultiBlock master = getController();
        if (master == null){
            revert();
            return false;
        }
        else return master.validateStructure();
    }

    //endregion

    public boolean onTileClicked(EntityPlayer player, IBlockState state){
        IMultiBlock controller = getController();

        if (controller instanceof TileEnergyCoreStabilizer){
            ((TileEnergyCoreStabilizer)controller).onTileClicked(worldObj, pos, state, player);
        }
        else if (controller instanceof TileEnergyStorageCore){
            ((TileEnergyStorageCore)controller).onStructureClicked(worldObj, pos, state, player);
        }
        else if (controller instanceof TileEnergyPylon){
            ((TileEnergyPylon)controller).isOutputMode.value = !((TileEnergyPylon)controller).isOutputMode.value;
        }

        return true;
    }

    public void revert(){
        if (blockName.equals("draconicevolution:particleGenerator")){
            worldObj.setBlockState(pos, DEFeatures.particleGenerator.getDefaultState().withProperty(ParticleGenerator.TYPE, "stabilizer"));
            return;
        }

        Block block = Block.REGISTRY.getObject(new ResourceLocation(blockName));
        if (block != null) {
            worldObj.setBlockState(pos, block.getDefaultState());
        }
        else {
            worldObj.setBlockToAir(pos);
        }
    }

    public void setController(IMultiBlock controller) {
        if (controller instanceof TileEntity){
            coreOffset.vec = new Vec3I(pos.subtract(((TileEntity)controller).getPos()));
        }
    }

    private BlockPos getCorePos(){
        return pos.add(-coreOffset.vec.x, -coreOffset.vec.y, -coreOffset.vec.z);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("BlockName", blockName);
        coreOffset.toNBT(compound);
        return new SPacketUpdateTileEntity(pos, 0, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        blockName = pkt.getNbtCompound().getString("BlockName");
        coreOffset.fromNBT(pkt.getNbtCompound());
    }

    @Override
    public void writeExtraNBT(NBTTagCompound compound) {
        compound.setString("BlockName", blockName);
    }

    @Override
    public void readExtraNBT(NBTTagCompound compound) {
        blockName = compound.getString("BlockName");
    }

}
