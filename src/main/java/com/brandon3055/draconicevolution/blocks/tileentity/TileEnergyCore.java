package com.brandon3055.draconicevolution.blocks.tileentity;

import codechicken.lib.colour.Colour;
import codechicken.lib.data.MCDataInput;
import com.brandon3055.brandonscore.api.power.IExtendedRFStorage;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.inventory.ContainerBCTile;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.lib.datamanager.*;
import com.brandon3055.draconicevolution.DEOldConfig;
import com.brandon3055.draconicevolution.blocks.machines.EnergyCore;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.inventory.GuiLayoutFactories;
import com.brandon3055.draconicevolution.lib.EnergyCoreBuilder;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.brandon3055.draconicevolution.world.EnergyCoreStructure;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.*;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class TileEnergyCore extends TileBCore implements IExtendedRFStorage, IMultiBlockPart, MenuProvider {

    //Frame Movement
    public int frameMoveContactPoints = 0;
    public boolean isFrameMoving = false;
    public boolean moveBlocksProvided = false;

    //region Constant Fields

    public static final byte ORIENT_UNKNOWN = 0;
    public static final byte ORIENT_UP_DOWN = 1;
    public static final byte ORIENT_NORTH_SOUTH = 2;
    public static final byte ORIENT_EAST_WEST = 3;

    public static final Direction[][] STAB_ORIENTATIONS = new Direction[][]{{},   // ORIENT_UNKNOWN
            Direction.BY_2D_DATA,                                                 // ORIENT_UP_DOWN //TODO is 'BY_HORIZONTAL_INDEX' correct?
            {Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST},     // ORIENT_NORTH_SOUTH
            {Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH}    // ORIENT_EAST_WEST
    };

    //endregion

    public final EnergyCoreStructure coreStructure = new EnergyCoreStructure().initialize(this);
    public final ManagedBool active = register(new ManagedBool("active", SAVE_NBT_SYNC_TILE, TRIGGER_UPDATE));
    public final ManagedBool structureValid = register(new ManagedBool("structure_valid", SAVE_NBT_SYNC_TILE, TRIGGER_UPDATE));
    public final ManagedBool coreValid = register(new ManagedBool("core_valid", SAVE_NBT_SYNC_TILE, TRIGGER_UPDATE));
    public final ManagedString invalidMessage = register(new ManagedString("invalid_message", DataFlags.SAVE_NBT));
    public final ManagedBool buildGuide = register(new ManagedBool("build_guide", SAVE_NBT_SYNC_TILE, TRIGGER_UPDATE, CLIENT_CONTROL));
    public final ManagedBool stabilizersOK = register(new ManagedBool("stabilizers_ok", SAVE_NBT_SYNC_TILE, TRIGGER_UPDATE));
    public final ManagedByte tier = register(new ManagedByte("tier", (byte) 1, SAVE_NBT_SYNC_TILE, TRIGGER_UPDATE, CLIENT_CONTROL));
    public final ManagedLong energy = register(new ManagedLong("energy", SAVE_NBT_SYNC_TILE));
    public final ManagedVec3I[] stabOffsets = new ManagedVec3I[4];
    public final ManagedLong transferRate = register(new ManagedLong("transfer_rate", DataFlags.SYNC_CONTAINER));
    public final ManagedDouble inputRate = register(new ManagedDouble("input_rate", DataFlags.SYNC_CONTAINER));
    public final ManagedDouble outputRate = register(new ManagedDouble("output_rate", DataFlags.SYNC_CONTAINER));

    private int ticksElapsed = 0;
    private long[] flowArray = new long[20];
    private EnergyCoreBuilder activeBuilder = null;
    public float rotation = 0;
    private long lastTickEnergy = 0;
    private long lastTickInput = 0;
    private long lastTickOutput = 0;

    public TileEnergyCore(BlockPos pos, BlockState state) {
        super(DEContent.tile_storage_core, pos, state);

        for (int i = 0; i < stabOffsets.length; i++) {
            stabOffsets[i] = register(new ManagedVec3I("stab_offset" + i, new Vec3I(0, -1, 0), SAVE_NBT_SYNC_TILE));
        }

        active.addValueListener(active -> {
            if (level != null && level.getBlockState(worldPosition).getBlock() == DEContent.energy_core) level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(EnergyCore.ACTIVE, active));
        });
    }

    @Override
    public int getAccessDistanceSq() {
        return 1024;
    }

    @Override
    public void tick() {
        if (!level.isClientSide) {
            flowArray[ticksElapsed % 20] = (energy.get() - lastTickEnergy);
            lastTickEnergy = energy.get();

            long total = 0;
            for (long i : flowArray) {
                total += i;
            }
            transferRate.set(total / 20L);

            if (activeBuilder != null) {
                if (activeBuilder.isDead()) {
                    activeBuilder = null;
                } else {
                    activeBuilder.updateProcess();
                }
            }

            if (ticksElapsed % 500 == 0) {
                validateStructure();
            }

            double diff = lastTickInput - inputRate.get();
            inputRate.add(diff / 10);
            diff = lastTickOutput - outputRate.get();
            outputRate.add(diff / 10);

            lastTickInput = lastTickOutput = 0;
        } else {
            rotation++;
        }

        super.tick();

        if (ticksElapsed % 20 == 0 && !level.isClientSide && transferRate.isDirty(true)) {
            dataManager.forceSync(transferRate);
        }

        if (level.isClientSide && active.get()) {
            List<Player> players = level.getEntitiesOfClass(Player.class, new AABB(worldPosition, worldPosition.offset(1, 1, 1)).inflate(10, 10, 10));
            for (Player player : players) {
                double dist = Vec3D.getCenter(this).distance(new Vec3D(player));
                double distNext = new Vec3D(player).distance(new Vec3D(worldPosition.getX() + player.getDeltaMovement().x + 0.5, worldPosition.getY() + player.getDeltaMovement().y - 0.4, worldPosition.getZ() + player.getDeltaMovement().z + 0.5));
                double threshold = tier.get() > 2 ? tier.get() - 0.5 : tier.get() + 0.5;
                double boundary = distNext - threshold;
                double dir = dist - distNext;

                if (boundary <= 0) {
                    if (dir < 0) {
                        player.move(MoverType.PLAYER, new Vec3(-player.getDeltaMovement().x * 1.5, -player.getDeltaMovement().y * 1.5, -player.getDeltaMovement().z * 1.5));
                    }

                    double multiplier = (threshold - dist) * 0.05;

                    double xm = ((worldPosition.getX() + 0.5 - player.getX()) / distNext) * multiplier;
                    double ym = ((worldPosition.getY() - 0.4 - player.getY()) / distNext) * multiplier;
                    double zm = ((worldPosition.getZ() + 0.5 - player.getZ()) / distNext) * multiplier;

                    player.move(MoverType.PLAYER, new Vec3(-xm, -ym, -zm));
                }
            }
        }

        ticksElapsed++;
    }

    //region Activation

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int currentWindowIndex, Inventory playerInventory, Player player) {
        return new ContainerBCTile<>(DEContent.container_energy_core, currentWindowIndex, player.inventory, this, GuiLayoutFactories.ENERGY_CORE_LAYOUT);
    }

    public void onStructureClicked(Level world, BlockPos blockClicked, BlockState state, Player player) {
        if (!world.isClientSide) {
            validateStructure();
            if (player instanceof ServerPlayer) {
                NetworkHooks.openGui((ServerPlayer) player, this, worldPosition);
            }
        }
    }

    public void activateCore() {
        if (level.isClientSide || !validateStructure()) {
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
        if (level.isClientSide) {
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
        return (long) DEOldConfig.coreCapacity[tier.get() - 1];
    }

    @Override
    public void receivePacketFromClient(MCDataInput data, ServerPlayer client, int id) {
        if (id == 0) { //Activate
            if (active.get()) {
                deactivateCore();
            } else {
                activateCore();
            }
        } else if (id == 1) { //Tier Up
            if (!active.get() && tier.get() < 8) {
                tier.inc();
                validateStructure();
            }
        } else if (id == 2) { //Tier Down
            if (!active.get() && tier.get() > 1) {
                tier.dec();
                validateStructure();
            }
        } else if (id == 3) { //Toggle Guide
            if (!active.get()) {
                buildGuide.set(!buildGuide.get());
            }
        } else if (id == 4) { //Build
            if (!active.get()) {
                startBuilder(client);
            }
        }
    }

    private void startBuilder(Player player) {
        if (activeBuilder != null && !activeBuilder.isDead()) {
            player.sendMessage(new TranslatableComponent("ecore.de.already_assembling.txt").withStyle(ChatFormatting.RED), Util.NIL_UUID);
        } else {
            activeBuilder = new EnergyCoreBuilder(this, player);
        }
    }

    /**
     * Sets the "isCoreActive" value in each of the stabilizers
     */
    private void updateStabilizers(boolean coreActive) {
        for (ManagedVec3I offset : stabOffsets) {
            BlockPos tilePos = worldPosition.offset(-offset.get().x, -offset.get().y, -offset.get().z);
            BlockEntity tile = level.getBlockEntity(tilePos);

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
                BlockPos tilePos = worldPosition.subtract(offset.get().getPos());
                BlockEntity tile = level.getBlockEntity(tilePos);

                if (!(tile instanceof TileEnergyCoreStabilizer) || !((TileEnergyCoreStabilizer) tile).hasCoreLock.get() || ((TileEnergyCoreStabilizer) tile).getCore() != this || !((TileEnergyCoreStabilizer) tile).isStabilizerValid(tier.get(), this)) {
                    flag = false;
                    break;
                }
            }

            if (!flag) {
                stabilizersOK.set(false);
                releaseStabilizers();
            }
        } else {

            //Foe each of the 3 possible axises
            for (int orient = 1; orient < STAB_ORIENTATIONS.length; orient++) {
                Direction[] dirs = STAB_ORIENTATIONS[orient];
                List<TileEnergyCoreStabilizer> stabsFound = new ArrayList<TileEnergyCoreStabilizer>();

                //For each of the 4 possible directions around the axis
                for (int fIndex = 0; fIndex < dirs.length; fIndex++) {
                    Direction facing = dirs[fIndex];

                    for (int dist = 0; dist < 16; dist++) {
                        BlockPos pos1 = worldPosition.offset(facing.getStepX() * dist, facing.getStepY() * dist, facing.getStepZ() * dist);
                        BlockEntity tile = level.getBlockEntity(pos1);
                        if (!(tile instanceof TileEnergyCoreStabilizer)) {
                            continue;
                        }
                        TileEnergyCoreStabilizer stabilizer = (TileEnergyCoreStabilizer) tile;

                        TileEnergyCore currentCore = stabilizer.getCore();
                        if ((currentCore == null || stabilizer.getCore() == this) && stabilizer.isStabilizerValid(tier.get(), this)) {
                            stabsFound.add(stabilizer);
                            break;
                        }
                    }
                }

                if (stabsFound.size() == 4) {
                    for (TileEnergyCoreStabilizer stab : stabsFound) {
                        stabOffsets[stabsFound.indexOf(stab)].set(new Vec3I(worldPosition.getX() - stab.getBlockPos().getX(), worldPosition.getY() - stab.getBlockPos().getY(), worldPosition.getZ() - stab.getBlockPos().getZ()));
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
            BlockPos tilePos = worldPosition.offset(-offset.get().x, -offset.get().y, -offset.get().z);
            BlockEntity tile = level.getBlockEntity(tilePos);

            if (tile instanceof TileEnergyCoreStabilizer) {
                ((TileEnergyCoreStabilizer) tile).hasCoreLock.set(false);
                ((TileEnergyCoreStabilizer) tile).coreOffset.get().y = 0;
            }

            offset.set(new Vec3I(0, -1, 0));
        }
    }

    //endregion

    //region Energy Transfer

    public long receiveEnergy(long maxReceive, boolean simulate) {
        if (level.isClientSide) {
            return 0;
        }
        long energyReceived = Math.min(getExtendedCapacity() - energy.get(), maxReceive);

        if (!simulate) {
            energy.add(energyReceived);
            lastTickInput += energyReceived;
            setChanged();
        }
        return energyReceived;
    }

    public long extractEnergy(long maxExtract, boolean simulate) {
        if (level.isClientSide) {
            return 0;
        }
        long energyExtracted = Math.min(energy.get(), maxExtract);

        if (!simulate) {
            energy.subtract(energyExtracted);
            lastTickOutput += energyExtracted;
            setChanged();
        }
        return energyExtracted;
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
    @OnlyIn(Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    public int getColour() {
        if (tier.get() == 8) {
            return Colour.packRGBA(1F, 0.28F, 0.05F, 1F);
        }

        float colour = 1F - ((float) getExtendedStorage() / (float) getExtendedCapacity());
        return Colour.packRGBA(1F, colour * 0.3f, colour * 0.7f, 1F);
    }

    //endregion
}

