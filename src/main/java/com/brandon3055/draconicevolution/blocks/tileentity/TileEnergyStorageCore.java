package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.IDataRetainerTile;
import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.network.PacketTileMessage;
import com.brandon3055.brandonscore.network.wrappers.SyncableBool;
import com.brandon3055.brandonscore.network.wrappers.SyncableByte;
import com.brandon3055.brandonscore.network.wrappers.SyncableLong;
import com.brandon3055.brandonscore.network.wrappers.SyncableVec3I;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.GuiHandler;
import com.brandon3055.draconicevolution.api.IExtendedRFStorage;
import com.brandon3055.draconicevolution.utills.LogHelper;
import com.brandon3055.draconicevolution.world.EnergyCoreStructure;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;

import java.util.ArrayList;
import java.util.List;

/**
 *  Created by brandon3055 on 30/3/2016.
 */
public class TileEnergyStorageCore extends TileBCBase implements IDataRetainerTile, ITickable, IExtendedRFStorage{

    //region Constant Fields

    public static final byte ORIENT_UNKNOWN = 0;
    public static final byte ORIENT_UP_DOWN = 1;
    public static final byte ORIENT_NORTH_SOUTH = 2;
    public static final byte ORIENT_EAST_WEST = 3;

    public static final EnumFacing[][] STAB_ORIENTATIONS = new EnumFacing[][] {
            {},                                                                     // ORIENT_UNKNOWN
            EnumFacing.HORIZONTALS,                                                 // ORIENT_UP_DOWN
            {EnumFacing.UP, EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.WEST},     // ORIENT_NORTH_SOUTH
            {EnumFacing.UP, EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH}    // ORIENT_EAST_WEST
    };

    //endregion

	public final EnergyCoreStructure coreStructure = new EnergyCoreStructure().initialize(this);
    public final SyncableBool active = new SyncableBool(false, true, false, true);
    public final SyncableBool structureValid = new SyncableBool(false, true, false, true);
    public final SyncableBool buildGuide = new SyncableBool(false, true, false, true);
    public final SyncableBool stabilizersOK = new SyncableBool(false, true, false, true);
    public final SyncableByte tier = new SyncableByte((byte)1, true, true, true);
    public final SyncableByte stabOrient = new SyncableByte((byte)0, false, false, false);
    public final SyncableVec3I[] stabOffsets = new SyncableVec3I[4];
    public final SyncableLong energy = new SyncableLong(0, true, false, false);
    public final SyncableLong capacity = new SyncableLong(1, true, false, false);

    public TileEnergyStorageCore() {
        registerSyncableObject(active, true);
        registerSyncableObject(structureValid, true);
        registerSyncableObject(buildGuide, true);
        registerSyncableObject(stabilizersOK, true);
        registerSyncableObject(tier, true);
        registerSyncableObject(stabOrient, true);
        for (int i = 0; i < stabOffsets.length; i++){
            stabOffsets[i] = new SyncableVec3I(new Vec3I(0, -1, 0), true, false, false);
            registerSyncableObject(stabOffsets[i], true);
        }

    }

	@Override
	public void update() {
        detectAndSendChanges();
        //tier.value = 5; buildGuide.value = true;
        //updateBlock();
        //if (!worldObj.isRemote) buildGuide.value = true;
       // coreStructure.initialize(this);
	}


    //region Activation

    public void onStructureClicked(World world, BlockPos stabilizerPos, IBlockState state, EntityPlayer player) {
        //validateStructure();
        if (!world.isRemote) {
            FMLNetworkHandler.openGui(player, DraconicEvolution.instance, GuiHandler.GUIID_ENERGY_CORE, world, pos.getX(), pos.getY(), pos.getZ());
        }
    }

    private void activateCore(){
        if (!validateStructure()){
            return;
        }

        updateCapacity();

        LogHelper.info("Activate Core");//todo remove
        buildGuide.value = false;
        coreStructure.formTier(tier.value);
        active.value = true;
    }

    private void deactivateCore(){
        LogHelper.info("Deactivate Core");//todo remove
        active.value = false;
    }

    private void updateCapacity(){
        long cap = 1;

        //region Cap Switch
        switch (tier.value){
            case 1:
                cap = 45500000L;
                break;
            case 2:
                cap = 273000000L;
                break;
            case 3:
                cap = 1640000000L;
                break;
            case 4:
                cap = 9880000000L;
                break;
            case 5:
                cap = 59300000000L;
                break;
            case 6:
                cap = 356000000000L;
                break;
            case 7:
                cap = 2140000000000L;
                break;
            case 8:
                cap = Long.MAX_VALUE;
                break;
        }
        //endregion

        capacity.value = cap;
        if (energy.value > cap) {
            energy.value = cap;
        }
    }

    @Override
    public void receivePacketFromClient(PacketTileMessage packet, EntityPlayerMP client) {
        if (packet.getIndex() == (byte)0){ //Activate
            if (active.value){
                deactivateCore();
            }
            else {
                activateCore();
            }
        }
        else if (packet.getIndex() == (byte)1){ //Tier Up
            if (!active.value && tier.value < 8) {
                tier.value++;
                validateStructure();
            }
        }
        else if (packet.getIndex() == (byte)2){ //Tier Down
            if (!active.value && tier.value > 1) {
                tier.value--;
                validateStructure();
            }
        }
        else if (packet.getIndex() == (byte)3){ //Toggle Guide
            if (!active.value){
                buildGuide.value = !buildGuide.value;
            }
        }
    }

    //endregion

    //region Structure

    /**
     * If the structure has already been validated this method will check that it is still valit.
     * Otherwise it will check if the structure is valid.
     * */
    public boolean validateStructure() {
        boolean valid = checkStabilizers();

        if (!coreStructure.checkTier(tier.value)){
            valid = false;
        }

        structureValid.value = valid;
        return valid;
    }

    /**
     * If stabilizersOK is true this method will check to make sure the stabilisers are still valid.
     * Otherwise it will check for a valid stabilizer configuration.
     * */
    public boolean checkStabilizers() {
        boolean flag = true;
        if (stabilizersOK.value){

            for (SyncableVec3I offset : stabOffsets){
                BlockPos tilePos = pos.add(-offset.vec.x, -offset.vec.y, -offset.vec.z);
                TileEnergyCoreStabilizer tile = TileBCBase.getCastTileAt(worldObj, tilePos, TileEnergyCoreStabilizer.class);


                if (tile == null || !tile.hasCoreLock.value || tile.getCore() != this || !tile.isStabilizerValid(tier.value) /*|| tile.coreDirection != dir.getOpposite()todo Do i really deed this?*/){
                    flag = false;
                    break;
                }
            }

            if (stabOrient.value == ORIENT_UNKNOWN) {
                flag = false;
            }

            if (!flag){
                stabilizersOK.value = structureValid.value = active.value = false;
                stabOrient.value = ORIENT_UNKNOWN;
                releaseStabilizers();
            }
        }
        else {

            //Foe each of the 3 possible axises
            for (int orient = 1; orient < STAB_ORIENTATIONS.length; orient++){
                EnumFacing[] dirs = STAB_ORIENTATIONS[orient];
                List<TileEnergyCoreStabilizer> stabsFound = new ArrayList<TileEnergyCoreStabilizer>();

                //For each of the 4 possible directions around the axis
                for (int fIndex = 0; fIndex < dirs.length; fIndex++) {
                    EnumFacing facing = dirs[fIndex];

                    for (int dist = 0; dist < 16; dist++){
                        BlockPos pos1 = pos.add(facing.getFrontOffsetX() * dist, facing.getFrontOffsetY() * dist, facing.getFrontOffsetZ() * dist);
                        TileEnergyCoreStabilizer stabilizer = TileBCBase.getCastTileAt(worldObj, pos1, TileEnergyCoreStabilizer.class);
                        if (stabilizer != null && (!stabilizer.hasCoreLock.value || stabilizer.getCore().equals(this)) && stabilizer.isStabilizerValid(tier.value)){
                            stabsFound.add(stabilizer);
                            break;
                        }
                    }
                }

                if (stabsFound.size() == 4){
                    for (TileEnergyCoreStabilizer stab : stabsFound){
                        stabOffsets[stabsFound.indexOf(stab)].vec = new Vec3I(pos.getX() - stab.getPos().getX(), pos.getY() - stab.getPos().getY(), pos.getZ() - stab.getPos().getZ());
                        stab.setCore(this);
                        stabOrient.value = (byte)orient;
                    }
                    stabilizersOK.value = true;
                    break;
                }

                //Did not find 4 stabilizers
                flag = false;
            }
        }

        return flag;
    }

    /**Frees any stabilizers that are still linked to the core and clears the offset list*/
    private void releaseStabilizers(){
        for (SyncableVec3I offset : stabOffsets){
            BlockPos tilePos = pos.add(offset.vec.x, offset.vec.y, offset.vec.z);
            TileEnergyCoreStabilizer tile = TileBCBase.getCastTileAt(worldObj, tilePos, TileEnergyCoreStabilizer.class);

            if (tile != null){
                tile.hasCoreLock.value = false;
                tile.coreYOffset.value = 0;
            }

            offset.vec = new Vec3I(0, -1, 0);
        }
    }

    //endregion

    //region Energy Transfer

    public int receiveEnergy(int maxReceive, boolean simulate) {
        long energyReceived = Math.min(getExtendedCapacity() - energy.value, maxReceive);

        if (!simulate) {
            energy.value += energyReceived;
        }
        return (int)energyReceived;
    }

    public int extractEnergy(int maxExtract, boolean simulate) {
        long energyExtracted = Math.min(energy.value, maxExtract);

        if (!simulate) {
            energy.value -= energyExtracted;
        }
        return (int)energyExtracted;
    }

    @Override
    public long getExtendedStorage() {
        return energy.value;
    }

    @Override
    public long getExtendedCapacity() {
        return capacity.value;
    }

    //endregion

	//region Sync & Save

	@Override
	public void writeDataToNBT(NBTTagCompound compound) {

	}

	@Override
	public void readDataFromNBT(NBTTagCompound compound) {

	}

    //endregion
}
