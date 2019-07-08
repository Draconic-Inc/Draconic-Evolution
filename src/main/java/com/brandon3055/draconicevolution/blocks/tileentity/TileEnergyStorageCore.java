package com.brandon3055.draconicevolution.blocks.tileentity;

import codechicken.lib.data.MCDataInput;
import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.lib.datamanager.*;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.GuiHandler;
import com.brandon3055.draconicevolution.api.IExtendedRFStorage;
import com.brandon3055.draconicevolution.lib.EnergyCoreBuilder;
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
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.*;
import static net.minecraft.util.text.TextFormatting.RED;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class TileEnergyStorageCore extends TileBCBase implements ITickable, IExtendedRFStorage, IMultiBlockPart {

    //Frame Movement
    public int frameMoveContactPoints = 0;
    public boolean isFrameMoving = false;
    public boolean moveBlocksProvided = false;

    //region Constant Fields

    public static final byte ORIENT_UNKNOWN = 0;
    public static final byte ORIENT_UP_DOWN = 1;
    public static final byte ORIENT_NORTH_SOUTH = 2;
    public static final byte ORIENT_EAST_WEST = 3;

    public static final EnumFacing[][] STAB_ORIENTATIONS = new EnumFacing[][]{{},   // ORIENT_UNKNOWN
            EnumFacing.HORIZONTALS,                                                 // ORIENT_UP_DOWN
            {EnumFacing.UP, EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.WEST},     // ORIENT_NORTH_SOUTH
            {EnumFacing.UP, EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH}    // ORIENT_EAST_WEST
    };

    //endregion

    public final EnergyCoreStructure coreStructure = new EnergyCoreStructure().initialize(this);
    public final ManagedBool active = register(new ManagedBool("active", SAVE_NBT_SYNC_TILE, TRIGGER_UPDATE));
    public final ManagedBool structureValid = register(new ManagedBool("structureValid", SAVE_NBT_SYNC_TILE, TRIGGER_UPDATE));
    public final ManagedBool coreValid = register(new ManagedBool("coreValid", SAVE_NBT_SYNC_TILE, TRIGGER_UPDATE));
    public final ManagedString invalidMessage = register(new ManagedString("invalidMessage", SAVE_NBT));
    public final ManagedBool buildGuide = register(new ManagedBool("buildGuide", SAVE_NBT_SYNC_TILE, TRIGGER_UPDATE));
    public final ManagedBool stabilizersOK = register(new ManagedBool("stabilizersOK", SAVE_NBT_SYNC_TILE, TRIGGER_UPDATE));
    public final ManagedByte tier = register(new ManagedByte("tier", (byte)1, SAVE_NBT_SYNC_TILE, TRIGGER_UPDATE));
    public final ManagedLong energy = register(new ManagedLong("energy", SAVE_NBT_SYNC_TILE));
    public final ManagedVec3I[] stabOffsets = new ManagedVec3I[4];
    public final ManagedLong transferRate = register(new ManagedLong("transferRate", SYNC_CONTAINER));

    private int ticksElapsed = 0;
    private long[] flowArray = new long[20];
    private EnergyCoreBuilder activeBuilder = null;
    public float rotation = 0;

    public TileEnergyStorageCore() {
        setShouldRefreshOnBlockChange();

        for (int i = 0; i < stabOffsets.length; i++) {
            stabOffsets[i] = register(new ManagedVec3I("stabOffset" + i, new Vec3I(0, -1, 0), SAVE_NBT_SYNC_TILE));
        }
    }

    @Override
    public void update() {
        if (!world.isRemote) {
            flowArray[ticksElapsed % 20] = (energy.get() - energy.get());
            long total = 0;
            for (long i : flowArray) {
                total += i;
            }
            transferRate.set(total / 20L);

            if (activeBuilder != null) {
                if (activeBuilder.isDead()) {
                    activeBuilder = null;
                }
                else {
                    activeBuilder.updateProcess();
                }
            }

            if (ticksElapsed % 500 == 0) {
                validateStructure();
            }
        }
        else {
            rotation++;
        }

        super.update();

        if (ticksElapsed % 20 == 0 && !world.isRemote && transferRate.isDirty(true)) {
            dataManager.forceSync(transferRate);
        }

        if (world.isRemote && active.get()) {
            List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos, pos.add(1, 1, 1)).grow(10, 10, 10));
            for (EntityPlayer player : players) {
                double dist = player.getDistance(pos.getX() + 0.5, pos.getY() - 0.4, pos.getZ() + 0.5);
                double distNext = player.getDistance(pos.getX() + player.motionX + 0.5, pos.getY() + player.motionY - 0.4, pos.getZ() + player.motionZ + 0.5);
                double threshold = tier.get() > 2 ? tier.get() - 0.5 : tier.get() + 0.5;
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

        if (energy.get() > getCapacity()) {
            energy.set(getCapacity());
        }

        buildGuide.set(false);
        coreStructure.formTier(tier.get());
        active.set(true);
        updateStabilizers(true);
    }

    public void deactivateCore() {
        if (world.isRemote) {
            return;
        }

        coreStructure.revertTier(tier.get());
        active.set(false);
        updateStabilizers(false);
    }

    private long getCapacity() {
        if (tier.get() <= 0 || tier.get() > 8) {
            LogHelper.error("Tier not valid! WTF!!!");
            return 0;
        }
        return (long) DEConfig.coreCapacity[tier.get() - 1];
    }

    @Override
    public void receivePacketFromClient(MCDataInput data, EntityPlayerMP client, int id) {
        if (id == 0) { //Activate
            if (active.get()) {
                deactivateCore();
            }
            else {
                activateCore();
            }
        }
        else if (id == 1) { //Tier Up
            if (!active.get() && tier.get() < 8) {
                tier.inc();
                validateStructure();
            }
        }
        else if (id == 2) { //Tier Down
            if (!active.get() && tier.get() > 1) {
                tier.dec();
                validateStructure();
            }
        }
        else if (id == 3) { //Toggle Guide
            if (!active.get()) {
                buildGuide.set(!buildGuide.get());
            }
        }
        else if (id == 4) { //Build
            if (!active.get()) {
                startBuilder(client);
            }
        }
    }

    private void startBuilder(EntityPlayer player) {
        if (activeBuilder != null && !activeBuilder.isDead()) {
            player.sendMessage(new TextComponentTranslation("ecore.de.already_assembling.txt").setStyle(new Style().setColor(RED)));
        }
        else {
            activeBuilder = new EnergyCoreBuilder(this, player);
        }
    }

    /**
     * Sets the "isCoreActive" value in each of the stabilizers
     */
    private void updateStabilizers(boolean coreActive) {
        for (ManagedVec3I offset : stabOffsets) {
            BlockPos tilePos = pos.add(-offset.get().x, -offset.get().y, -offset.get().z);
            TileEntity tile = world.getTileEntity(tilePos);

            if (tile instanceof TileEnergyCoreStabilizer) {
                ((TileEnergyCoreStabilizer) tile).isCoreActive.set(coreActive);
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

        if (!(coreValid.set(coreStructure.checkTier(tier.get())))) {
            BlockPos pos = coreStructure.invalidBlock;
            invalidMessage.set("Error At: " + "x:" + pos.getX() + ", y:" + pos.getY() + ", z:" + pos.getZ() + " Expected: " + coreStructure.expectedBlock);
            valid = false;
        }

        if (!valid && active.get()) {
            active.set(false);
            deactivateCore();
        }

        structureValid.set(valid);

        if (valid) {
            invalidMessage.set("");
        }

        return valid;
    }

    /**
     * If stabilizersOK is true this method will check to make sure the stabilisers are still valid.
     * Otherwise it will check for a valid stabilizer configuration.
     */
    public boolean checkStabilizers() {
        boolean flag = true;
        if (stabilizersOK.get()) {
            for (ManagedVec3I offset : stabOffsets) {
                BlockPos tilePos = pos.subtract(offset.get().getPos());
                TileEntity tile = world.getTileEntity(tilePos);

                if (!(tile instanceof TileEnergyCoreStabilizer) || !((TileEnergyCoreStabilizer) tile).hasCoreLock.get() || ((TileEnergyCoreStabilizer) tile).getCore() != this || !((TileEnergyCoreStabilizer) tile).isStabilizerValid(tier.get(), this)) {
                    flag = false;
                    break;
                }
            }

            if (!flag) {
                stabilizersOK.set(false);
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
                        if (stabilizer instanceof TileEnergyCoreStabilizer && (!((TileEnergyCoreStabilizer) stabilizer).hasCoreLock.get() || ((TileEnergyCoreStabilizer) stabilizer).getCore().equals(this)) && ((TileEnergyCoreStabilizer) stabilizer).isStabilizerValid(tier.get(), this)) {
                            stabsFound.add((TileEnergyCoreStabilizer) stabilizer);
                            break;
                        }
                    }
                }

                if (stabsFound.size() == 4) {
                    for (TileEnergyCoreStabilizer stab : stabsFound) {
                        stabOffsets[stabsFound.indexOf(stab)].set(new Vec3I(pos.getX() - stab.getPos().getX(), pos.getY() - stab.getPos().getY(), pos.getZ() - stab.getPos().getZ()));
                        stab.setCore(this);
                    }
                    stabilizersOK.set(true);
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
            BlockPos tilePos = pos.add(-offset.get().x, -offset.get().y, -offset.get().z);
            TileEntity tile = world.getTileEntity(tilePos);

            if (tile instanceof TileEnergyCoreStabilizer) {
                ((TileEnergyCoreStabilizer) tile).hasCoreLock.set(false);
                ((TileEnergyCoreStabilizer) tile).coreOffset.get().y = 0;
            }

            offset.set(new Vec3I(0, -1, 0));
        }
    }

    //endregion

    //region Energy Transfer

    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (world.isRemote) {
            return 0;
        }
        long energyReceived = Math.min(getExtendedCapacity() - energy.get(), maxReceive);

        if (!simulate) {
            energy.add(energyReceived);
            markDirty();
        }
        return (int) energyReceived;
    }

    public int extractEnergy(int maxExtract, boolean simulate) {
        if (world.isRemote) {
            return 0;
        }
        long energyExtracted = Math.min(energy.get(), maxExtract);

        if (!simulate) {
            energy.subtract(energyExtracted);
            markDirty();
        }
        return (int) energyExtracted;
    }

    @Override
    public long getExtendedStorage() {
        return energy.get();
    }

    @Override
    public long getExtendedCapacity() {
        return getCapacity();
    }

    //endregion

    //region IMultiBlock

    @Override
    public boolean isStructureValid() {
        return structureValid.get();
    }

    @Override
    public IMultiBlockPart getController() {
        return this;
    }

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

