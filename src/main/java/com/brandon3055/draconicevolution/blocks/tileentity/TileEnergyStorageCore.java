package com.brandon3055.draconicevolution.blocks.tileentity;

import codechicken.lib.data.MCDataInput;
import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.lib.datamanager.*;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.GuiHandler;
import com.brandon3055.draconicevolution.api.IExtendedRFStorage;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.brandon3055.draconicevolution.world.EnergyCoreStructure;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class TileEnergyStorageCore extends TileBCBase implements ITickable, IExtendedRFStorage, IMultiBlockPart {

    //region Constant Fields

    public static final byte ORIENT_UNKNOWN = 0;
    public static final byte ORIENT_UP_DOWN = 1;
    public static final byte ORIENT_NORTH_SOUTH = 2;
    public static final byte ORIENT_EAST_WEST = 3;

    public static final EnumFacing[][] STAB_ORIENTATIONS = new EnumFacing[][]{{},                                                                     // ORIENT_UNKNOWN
            EnumFacing.HORIZONTALS,                                                 // ORIENT_UP_DOWN
            {EnumFacing.UP, EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.WEST},     // ORIENT_NORTH_SOUTH
            {EnumFacing.UP, EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH}    // ORIENT_EAST_WEST
    };

    public static final long[] CAPACITY = new long[]{45500000L, 273000000L, 1640000000L, 9880000000L, 59300000000L, 356000000000L, 2140000000000L, Long.MAX_VALUE};

    //endregion

    public final EnergyCoreStructure coreStructure = new EnergyCoreStructure().initialize(this);
    public final ManagedBool active = register("active", new ManagedBool(false)).syncViaTile().saveToTile().trigerUpdate().finish();
    public final ManagedBool structureValid = register("structureValid", new ManagedBool(false)).syncViaTile().saveToTile().trigerUpdate().finish();
    public final ManagedBool coreValid = register("coreValid", new ManagedBool(false)).syncViaTile().saveToTile().trigerUpdate().finish();
    public final ManagedString invalidMessage = register("invalidMessage", new ManagedString("")).syncViaTile().finish();
    public final ManagedBool buildGuide = register("buildGuide", new ManagedBool(false)).syncViaTile().saveToTile().trigerUpdate().finish();
    public final ManagedBool stabilizersOK = register("stabilizersOK", new ManagedBool(false)).syncViaTile().saveToTile().trigerUpdate().finish();
    public final ManagedByte tier = register("tier", new ManagedByte(1)).syncViaTile().saveToTile().trigerUpdate().finish();
    public final ManagedLong energy = register("energy", new ManagedLong(0)).syncViaTile().saveToTile().finish();
    public final ManagedVec3I[] stabOffsets = new ManagedVec3I[4];
    public final ManagedLong transferRate = register("transferRate", new ManagedLong(0)).syncViaContainer().finish();


    private int ticksElapsed = 0;
    private long[] flowArray = new long[20];
    public float rotation = 0;

    public TileEnergyStorageCore() {
        setShouldRefreshOnBlockChange();

        for (int i = 0; i < stabOffsets.length; i++) {
            stabOffsets[i] = register("stabOffset" + i, new ManagedVec3I(new Vec3I(0, -1, 0))).saveToTile().syncViaTile().finish();
        }
    }

    @Override
    public void update() {
        super.update();
        if (!world.isRemote) {
            flowArray[ticksElapsed % 20] = (energy.value - energy.lastTickValue);
            long total = 0;
            for (long i : flowArray) {
                total += i;
            }
            transferRate.value = total / 20L;
        }
        else {
            rotation++;
        }

        if (ticksElapsed % 20 == 0 && !world.isRemote && transferRate.detectChanges()) {
            dataManager.forceSync(transferRate);
        }

        if (world.isRemote && active.value) {
            List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos, pos.add(1, 1, 1)).expand(10, 10, 10));
            for (EntityPlayer player : players) {
                double dist = player.getDistance(pos.getX() + 0.5, pos.getY() - 0.4, pos.getZ() + 0.5);
                double distNext = player.getDistance(pos.getX() + player.motionX + 0.5, pos.getY() + player.motionY - 0.4, pos.getZ() + player.motionZ + 0.5);
                double threshold = tier.value > 2 ? tier.value - 0.5 : tier.value + 0.5;
                double boundary = distNext - threshold;
                double dir = dist - distNext;

                if (boundary <= 0) {
                    if (dir < 0) {
                        player.move(MoverType.PLAYER, -player.motionX * 1.5, -player.motionY * 1.5, -player.motionZ * 1.5);
                    }

                    double multiplier = (threshold - dist) * 0.05;

                    double xm = ((pos.getX() + 0.5 - player.posX) / distNext) * multiplier;
                    double ym = ((pos.getY() - 0.4 - player.posY) / distNext) * multiplier;
                    double zm = ((pos.getZ() + 0.5 - player.posZ) / distNext) * multiplier;

                    player.move(MoverType.PLAYER, -xm, -ym, -zm);
                }
            }
        }

        ticksElapsed++;
    }

    //region Activation

    public void onStructureClicked(World world, BlockPos blockClicked, IBlockState state, EntityPlayer player) {
        if (!world.isRemote) {
            validateStructure();
            FMLNetworkHandler.openGui(player, DraconicEvolution.instance, GuiHandler.GUIID_ENERGY_CORE, world, pos.getX(), pos.getY(), pos.getZ());
        }
    }

    public void activateCore() {
        if (world.isRemote || !validateStructure()) {
            return;
        }

        if (energy.value > getCapacity()) {
            energy.value = getCapacity();
        }

        buildGuide.value = false;
        coreStructure.formTier(tier.value);
        active.value = true;
        updateStabilizers(true);
    }

    public void deactivateCore() {
        if (world.isRemote) {
            return;
        }

        coreStructure.revertTier(tier.value);
        active.value = false;
        updateStabilizers(false);
    }

    private long getCapacity() {
        if (tier.value <= 0 || tier.value > 8) {
            LogHelper.error("Tier not valid! WTF!!!");
            return 0;
        }
        return CAPACITY[tier.value - 1];
    }

    @Override
    public void receivePacketFromClient(MCDataInput data, EntityPlayerMP client, int id) {
        if (id == 0) { //Activate
            if (active.value) {
                deactivateCore();
            }
            else {
                activateCore();
            }
        }
        else if (id == 1) { //Tier Up
            if (!active.value && tier.value < 8) {
                tier.value++;
                validateStructure();
            }
        }
        else if (id == 2) { //Tier Down
            if (!active.value && tier.value > 1) {
                tier.value--;
                validateStructure();
            }
        }
        else if (id == 3) { //Toggle Guide
            if (!active.value) {
                buildGuide.value = !buildGuide.value;
            }
        }
        else if (id == 4) { //Toggle Guide
            if (!active.value && client.capabilities.isCreativeMode) {
                coreStructure.placeTier(tier.value);
                validateStructure();
            }
        }
    }


    /**
     * Sets the "isCoreActive" value in each of the stabilizers
     */
    private void updateStabilizers(boolean coreActive) {
        for (ManagedVec3I offset : stabOffsets) {
            BlockPos tilePos = pos.add(-offset.vec.x, -offset.vec.y, -offset.vec.z);
            TileEntity tile = world.getTileEntity(tilePos);

            if (tile instanceof TileEnergyCoreStabilizer) {
                ((TileEnergyCoreStabilizer) tile).isCoreActive.value = coreActive;
            }
        }
    }

    //endregion

    //region Structure

    /**
     * If the structure has already been validated this method will check that it is still valit.
     * Otherwise it will check if the structure is valid.
     */
    public boolean validateStructure() {
        boolean valid = checkStabilizers();

        if (!(coreValid.value = coreStructure.checkTier(tier.value))) {
            BlockPos pos = coreStructure.invalidBlock;
            invalidMessage.value = "Error At: " + "x:" + pos.getX() + ", y:" + pos.getY() + ", z:" + pos.getZ() + " Expected: " + coreStructure.expectedBlock;
            valid = false;
        }

        if (!valid && active.value) {
            active.value = false;
            deactivateCore();
        }

        structureValid.value = valid;

        if (valid) {
            invalidMessage.value = "";
        }

        return valid;
    }

    /**
     * If stabilizersOK is true this method will check to make sure the stabilisers are still valid.
     * Otherwise it will check for a valid stabilizer configuration.
     */
    public boolean checkStabilizers() {
        boolean flag = true;
        if (stabilizersOK.value) {
            for (ManagedVec3I offset : stabOffsets) {
                BlockPos tilePos = pos.add(-offset.vec.x, -offset.vec.y, -offset.vec.z);
                TileEntity tile = world.getTileEntity(tilePos);

                if (!(tile instanceof TileEnergyCoreStabilizer) || !((TileEnergyCoreStabilizer) tile).hasCoreLock.value || ((TileEnergyCoreStabilizer) tile).getCore() != this || !((TileEnergyCoreStabilizer) tile).isStabilizerValid(tier.value, this)) {
                    flag = false;
                    break;
                }
            }

            if (!flag) {
                stabilizersOK.value = false;
                releaseStabilizers();
            }
        }
        else {

            //Foe each of the 3 possible axises
            for (int orient = 1; orient < STAB_ORIENTATIONS.length; orient++) {
                EnumFacing[] dirs = STAB_ORIENTATIONS[orient];
                List<TileEnergyCoreStabilizer> stabsFound = new ArrayList<TileEnergyCoreStabilizer>();

                //For each of the 4 possible directions around the axis
                for (int fIndex = 0; fIndex < dirs.length; fIndex++) {
                    EnumFacing facing = dirs[fIndex];

                    for (int dist = 0; dist < 16; dist++) {
                        BlockPos pos1 = pos.add(facing.getFrontOffsetX() * dist, facing.getFrontOffsetY() * dist, facing.getFrontOffsetZ() * dist);
                        TileEntity stabilizer = world.getTileEntity(pos1);
                        if (stabilizer instanceof TileEnergyCoreStabilizer && (!((TileEnergyCoreStabilizer) stabilizer).hasCoreLock.value || ((TileEnergyCoreStabilizer) stabilizer).getCore().equals(this)) && ((TileEnergyCoreStabilizer) stabilizer).isStabilizerValid(tier.value, this)) {
                            stabsFound.add((TileEnergyCoreStabilizer) stabilizer);
                            break;
                        }
                    }
                }

                if (stabsFound.size() == 4) {
                    for (TileEnergyCoreStabilizer stab : stabsFound) {
                        stabOffsets[stabsFound.indexOf(stab)].vec = new Vec3I(pos.getX() - stab.getPos().getX(), pos.getY() - stab.getPos().getY(), pos.getZ() - stab.getPos().getZ());
                        stab.setCore(this);
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

    /**
     * Frees any stabilizers that are still linked to the core and clears the offset list
     */
    private void releaseStabilizers() {
        for (ManagedVec3I offset : stabOffsets) {
            BlockPos tilePos = pos.add(-offset.vec.x, -offset.vec.y, -offset.vec.z);
            TileEntity tile = world.getTileEntity(tilePos);

            if (tile instanceof TileEnergyCoreStabilizer) {
                ((TileEnergyCoreStabilizer) tile).hasCoreLock.value = false;
                ((TileEnergyCoreStabilizer) tile).coreOffset.vec.y = 0;
            }

            offset.vec = new Vec3I(0, -1, 0);
        }
    }

    //endregion

    //region Energy Transfer

    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (world.isRemote) {
            return 0;
        }
        long energyReceived = Math.min(getExtendedCapacity() - energy.value, maxReceive);

        if (!simulate) {
            energy.value += energyReceived;
        }
        return (int) energyReceived;
    }

    public int extractEnergy(int maxExtract, boolean simulate) {
        if (world.isRemote) {
            return 0;
        }
        long energyExtracted = Math.min(energy.value, maxExtract);

        if (!simulate) {
            energy.value -= energyExtracted;
        }
        return (int) energyExtracted;
    }

    @Override
    public long getExtendedStorage() {
        return energy.value;
    }

    @Override
    public long getExtendedCapacity() {
        return getCapacity();
    }

    //endregion

    //region IMultiBlock

    @Override
    public boolean isStructureValid() {
        return structureValid.value;
    }

    //
//    @Override
//    public boolean isController() {
//        return true;
//    }
//
//    @Override
//    public boolean hasSatelliteStructures() {
//        return false;
//    }
//
    @Override
    public IMultiBlockPart getController() {
        return this;
    }
//
//    @Override
//    public LinkedList<IMultiBlock> getSatelliteControllers() {
//        return null;
//    }


    //endregion

    //region Rendering

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 65536.0D;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return true;
    }

    //endregion
}

