package com.brandon3055.draconicevolution.blocks.tileentity;

import codechicken.lib.colour.Colour;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.math.MathHelper;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.api.power.IOTracker;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.brandonscore.inventory.ContainerBCTile;
import com.brandon3055.brandonscore.lib.IInteractTile;
import com.brandon3055.brandonscore.lib.datamanager.*;
import com.brandon3055.brandonscore.multiblock.*;
import com.brandon3055.brandonscore.utils.FacingUtils;
import com.brandon3055.brandonscore.utils.MathUtils;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.machines.EnergyCore;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileEnergyCore;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.inventory.ContainerDETile;
import com.brandon3055.draconicevolution.inventory.GuiLayoutFactories;
import com.brandon3055.draconicevolution.lib.MultiBlockBuilder;
import com.brandon3055.draconicevolution.lib.OPStorageOP;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.DrawSelectionEvent;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.*;


/**
 * Created by brandon3055 on 30/3/2016.
 */
public class TileEnergyCore extends TileBCore implements MenuProvider, IInteractTile, MultiBlockController {
    public static final int MAX_TIER = 8;
    public static final int MSG_TOGGLE_ACTIVATION = 1;
    public static final int MSG_BUILD_CORE = 2;
    public static final int ADV_STABILIZER_TIER = 5;
    public static final int MAX_STABILIZER_DIST = 16;

    public static final int DEFAULT_FRAME_COLOUR = 0x191919;
    public static final int DEFAULT_TRIANGLE_COLOUR = 0x660099;
    public static final int DEFAULT_EFFECT_COLOUR = 0x00f2f2;

    public static final int DEFAULT_FRAME_COLOUR_T8 = 0x191919;
    public static final int DEFAULT_TRIANGLE_COLOUR_T8 = 0xa52600;
    public static final int DEFAULT_EFFECT_COLOUR_T8 = 0xff7f00;

    public final ManagedByte tier = register(new ManagedByte("tier", (byte) 1, SAVE_NBT_SYNC_TILE, CLIENT_CONTROL));
    public final ManagedBool active = register(new ManagedBool("active", SAVE_NBT_SYNC_TILE));
    public final ManagedBool coreValid = register(new ManagedBool("core_valid", SAVE_NBT_SYNC_TILE)); //The core structure is valid
    public final ManagedBool stabilizersValid = register(new ManagedBool("stabilizers_valid", SAVE_NBT_SYNC_TILE)); //The stabilizer configuration is valid.
    public final ManagedPos[] stabilizerPositions = new ManagedPos[4]; //Relative stabilizer positions

    public final ManagedBool buildGuide = register(new ManagedBool("build_guide", SAVE_NBT_SYNC_TILE, CLIENT_CONTROL));
    public final ManagedFloat fillPercent = register(new ManagedFloat("fill_percent", 0, SYNC_TILE)); //Not saved just for rendering.
    public final ManagedString energyTarget = register(new ManagedString("user_target", SAVE_NBT_SYNC_CONTAINER, CLIENT_CONTROL));

    //Custom Rendering
    public final ManagedBool legacyRender = register(new ManagedBool("legacy_render", false, SAVE_NBT_SYNC_TILE, CLIENT_CONTROL));
    public final ManagedBool customColour = register(new ManagedBool("custom_colour", false, SAVE_NBT_SYNC_TILE, CLIENT_CONTROL));
    public final ManagedInt frameColour = register(new ManagedInt("frame_colour", DEFAULT_FRAME_COLOUR, SAVE_NBT_SYNC_TILE, CLIENT_CONTROL));
    public final ManagedInt innerColour = register(new ManagedInt("inner_colour", DEFAULT_TRIANGLE_COLOUR, SAVE_NBT_SYNC_TILE, CLIENT_CONTROL));
    public final ManagedInt effectColour = register(new ManagedInt("effect_colour", DEFAULT_EFFECT_COLOUR, SAVE_NBT_SYNC_TILE, CLIENT_CONTROL));

    public OPStorageOP energy = new OPStorageOP(this::getCapacity);

    private MultiBlockDefinition definitionCache = null;
    private MultiBlockBuilder activeBuilder = null;
    private int defCacheLastTier = 1;

    public TileEnergyCore(BlockPos pos, BlockState state) {
        super(DEContent.tile_storage_core, pos, state);
        capManager.setInternalManaged("energy", CapabilityOP.OP, energy).saveTile().syncContainer();
        energy.setIOTracker(addTickable(new IOTracker()));

        for (int i = 0; i < stabilizerPositions.length; i++) {
            stabilizerPositions[i] = register(new ManagedPos("stabilizer_pos_" + i, (BlockPos) null, SAVE_NBT_SYNC_TILE));
        }
        tier.addValueListener(e -> {
            stabilizersValid.set(false);
            validateStructure();
        });

        energyTarget.setCCSCS();
    }

    @Override
    public void writeExtraNBT(CompoundTag nbt) {
        super.writeExtraNBT(nbt);
    }

    @Override
    public void readExtraNBT(CompoundTag nbt) {
        super.readExtraNBT(nbt);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide) {
            if (activeBuilder != null) {
                if (activeBuilder.isDead()) {
                    activeBuilder = null;
                } else {
                    activeBuilder.updateProcess();
                }
            }

            if (tier.get() == 8) {
                fillPercent.set(0F);
            } else {
                fillPercent.set(((float) energy.getOPStored() / (float) energy.getMaxOPStored()));
            }
        }
    }

    // ### Interaction

    @Override
    public InteractionResult handleRemoteClick(Player player, InteractionHand hand, BlockHitResult hit) {
        if (player instanceof ServerPlayer) {
            validateStructure();
            NetworkHooks.openGui((ServerPlayer) player, this, worldPosition);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void receivePacketFromClient(MCDataInput data, ServerPlayer client, int id) {
        super.receivePacketFromClient(data, client, id);
        switch (id) {
            case MSG_TOGGLE_ACTIVATION -> toggleActivation();
            case MSG_BUILD_CORE -> attemptAutoBuild(client);
        }
    }

    @Override
    public InteractionResult onBlockUse(BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        return handleRemoteClick(player, hand, hit);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int currentWindowIndex, Inventory playerInventory, Player player) {
        return new ContainerDETile<>(DEContent.container_energy_core, currentWindowIndex, player.getInventory(), this, GuiLayoutFactories.ENERGY_CORE_LAYOUT);
    }

    @Override
    public int getAccessDistanceSq() {
        return 1024;
    }


    // ### Form/Revert Multi-block

    public void toggleActivation() {
        if (!level.isClientSide) {
            if (active.get()) {
                deactivateCore();
                return;
            } else if (!validateStructure()) {
                return;
            }

            MultiBlockDefinition definition = getMultiBlockDef();
            if (definition == null) {
                return;
            }

            for (Map.Entry<BlockPos, MultiBlockPart> entry : definition.getBlocksAt(worldPosition).entrySet()) {
                BlockPos pos = entry.getKey();
                MultiBlockPart part = entry.getValue();
                if (pos.equals(worldPosition) || level.isEmptyBlock(pos) || part instanceof EmptyPart) {
                    continue;
                }

                BlockState state = level.getBlockState(pos);
                level.setBlockAndUpdate(pos, DEContent.structure_block.defaultBlockState());
                BlockEntity tile = level.getBlockEntity(pos);
                if (tile instanceof TileStructureBlock) {
                    ((TileStructureBlock) tile).blockName.set(state.getBlock().getRegistryName());
                    ((TileStructureBlock) tile).setController(this);
                }
            }

            buildGuide.set(false);
            active.set(true);
            updateStabilizers(true);
            energy.validateStorage();
            level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(EnergyCore.ACTIVE, true));
        }
    }

    /**
     * Sets the "isCoreActive" value in each of the stabilizers
     */
    private void updateStabilizers(boolean coreActive) {
        for (ManagedPos offset : stabilizerPositions) {
            if (offset.get() == null) continue;
            BlockPos tilePos = worldPosition.offset(-offset.get().getX(), -offset.get().getY(), -offset.get().getZ());
            BlockEntity tile = level.getBlockEntity(tilePos);

            if (tile instanceof TileEnergyCoreStabilizer stabilizer) {
                stabilizer.isCoreActive.set(coreActive);
            }
        }
    }

    //
    public void deactivateCore() {
        if (level.isClientSide) {
            return;
        }
        level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(EnergyCore.ACTIVE, false));
        MultiBlockDefinition definition = getMultiBlockDef();
        if (definition == null) {
            return;
        }

        definition.getBlocksAt(worldPosition).keySet().forEach(pos -> {
            BlockEntity tile = level.getBlockEntity(pos);
            if (tile instanceof TileStructureBlock) {
                ((TileStructureBlock) tile).revert();
            }
        });

        active.set(false);
        updateStabilizers(false);
    }

    /**
     * Frees any stabilizers that are still linked to the core and clears the offset list
     */
    private void releaseStabilizers() {
        for (ManagedPos offset : stabilizerPositions) {
            if (offset.get() == null) continue;
            BlockPos tilePos = worldPosition.offset(-offset.get().getX(), -offset.get().getY(), -offset.get().getZ());
            BlockEntity tile = level.getBlockEntity(tilePos);

            if (tile instanceof TileEnergyCoreStabilizer stabilizer) {
                stabilizer.setCore(null);
            }

            offset.set(null);
        }
    }

    public void onRemoved() {
        releaseStabilizers();
    }

    // ### Validate Multi-block

    /**
     * This method will check if the structure is valid.
     * If the structure has already been validated this method will check that it is still valid.
     */
    @Override
    public boolean validateStructure() {
        if (level == null || level.isClientSide) return true;
        coreValid.set(isCoreValidForTier(tier.get()));
        checkStabilizers();
        boolean structureValid = isStructureValid();

        if (!structureValid && active.get()) {
            active.set(false);
            deactivateCore();
        }

        return structureValid;
    }

    @Override
    public boolean isStructureValid() {
        return stabilizersValid.get() && coreValid.get();
    }

    private boolean isCoreValidForTier(int tier) {
        MultiBlockDefinition definition = getMultiBlockDef();
        if (definition == null) {
            DraconicEvolution.LOGGER.error("Unable to find multi block definition for tier " + tier + " energy core. Something is broken...");
            return false;
        }

        List<InvalidPart> invalidParts = definition.test(level, worldPosition);
        //TODO Maybe let the player know which parts are invalid?
        return invalidParts.isEmpty();
    }

    /**
     * If stabilizersOK is true this method will check to make sure the stabilisers are still valid.
     * Otherwise, it will check for a valid stabiliser configuration.
     */
    public boolean checkStabilizers() {
        boolean valid = true;
        //Check existing stabilizers are still valid.
        if (stabilizersValid.get()) {
            for (ManagedPos offset : stabilizerPositions) {
                if (offset.get() == null) {
                    valid = false;
                } else {
                    BlockPos tilePos = worldPosition.subtract(offset.get());
                    BlockEntity tile = level.getBlockEntity(tilePos);
                    if (tile instanceof TileEnergyCoreStabilizer stabilizer) {
                        if (stabilizer.getCore() != this || (reqAdvStabilizers() && !stabilizer.isStructureValid())) {
                            valid = false;
                        }
                    } else {
                        valid = false;
                    }
                }
                if (!valid) {
                    break;
                }
            }

            if (!valid) {
                stabilizersValid.set(false);
                releaseStabilizers();
            }
            //Otherwise look for available valid stabilizers.
        } else {
            //For each axis
            for (Direction.Axis axis : Direction.Axis.VALUES) {
                Direction[] dirs = FacingUtils.getFacingsAroundAxis(axis);
                List<TileEnergyCoreStabilizer> found = new ArrayList<>();

                //For each of the 4 possible directions around the axis
                for (int fIndex = 0; fIndex < dirs.length; fIndex++) {
                    Direction facing = dirs[fIndex];
                    for (int dist = 1; dist < MAX_STABILIZER_DIST; dist++) {
                        BlockPos testPos = worldPosition.offset(facing.getStepX() * dist, facing.getStepY() * dist, facing.getStepZ() * dist);
                        BlockEntity tile = level.getBlockEntity(testPos);
                        if (!(tile instanceof TileEnergyCoreStabilizer stabilizer)) {
                            continue;
                        }
                        TileEnergyCore currentCore = stabilizer.getCore();
                        if ((currentCore == null || stabilizer.getCore() == this) && stabilizer.isSuitableForCore(tier.get(), this)) {
                            found.add(stabilizer);
                            break;
                        }
                    }
                }

                if (found.size() == 4) {
                    for (TileEnergyCoreStabilizer stab : found) {
                        stabilizerPositions[found.indexOf(stab)].set(new BlockPos(worldPosition.getX() - stab.getBlockPos().getX(), worldPosition.getY() - stab.getBlockPos().getY(), worldPosition.getZ() - stab.getBlockPos().getZ()));
                        stab.setCore(this);
                    }
                    stabilizersValid.set(true);
                    break;
                }
            }

            if (!stabilizersValid.get()) {
                valid = false;
            }
        }
        return valid;
    }

    public boolean reqAdvStabilizers() {
        return tier.get() >= ADV_STABILIZER_TIER;
    }

    // ### Energy handling ###

    private long getCapacity() {
        if (tier.get() <= 0 || tier.get() > 8) {
            DraconicEvolution.LOGGER.error("Tier not valid! WTF!!!");
            return 0;
        }
        return DEConfig.coreCapacity[tier.get() - 1];
    }


//    //Frame Movement
//    public int frameMoveContactPoints = 0;
//    public boolean isFrameMoving = false;
//    public boolean moveBlocksProvided = false;
//
//    //region Constant Fields
//
//    public static final byte ORIENT_UNKNOWN = 0;
//    public static final byte ORIENT_UP_DOWN = 1;
//    public static final byte ORIENT_NORTH_SOUTH = 2;
//    public static final byte ORIENT_EAST_WEST = 3;
//
//    public static final Direction[][] STAB_ORIENTATIONS = new Direction[][]{{},   // ORIENT_UNKNOWN
//            Direction.BY_2D_DATA,                                                 // ORIENT_UP_DOWN //TODO is 'BY_HORIZONTAL_INDEX' correct?
//            {Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST},     // ORIENT_NORTH_SOUTH
//            {Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH}    // ORIENT_EAST_WEST
//    };
//
//    //endregion
//
//    public final EnergyCoreStructure coreStructure = new EnergyCoreStructure().initialize(this);
//
//    public final ManagedString invalidMessage = register(new ManagedString("invalid_message", DataFlags.SAVE_NBT));
//    public final ManagedBool stabilizersOK = register(new ManagedBool("stabilizers_ok", SAVE_NBT_SYNC_TILE, TRIGGER_UPDATE));
//    public final ManagedLong energy = register(new ManagedLong("energy", SAVE_NBT_SYNC_TILE));

//    @Deprecated //Not sure how i'm going to handle this yet
//    public final ManagedLong transferRate = register(new ManagedLong("transfer_rate", DataFlags.SYNC_CONTAINER));

//    public final ManagedDouble inputRate = register(new ManagedDouble("input_rate", DataFlags.SYNC_CONTAINER));
//    public final ManagedDouble outputRate = register(new ManagedDouble("output_rate", DataFlags.SYNC_CONTAINER));
//
//    private int ticksElapsed = 0;
//    private long[] flowArray = new long[20];
//
//    public float rotation = 0;
//    private long lastTickEnergy = 0;
//    private long lastTickInput = 0;
//    private long lastTickOutput = 0;
//
//    public TileEnergyCore(BlockPos pos, BlockState state) {
//        super(DEContent.tile_storage_core, pos, state);
//

//
//        active.addValueListener(active -> {
//            if (level != null && level.getBlockState(worldPosition).getBlock() == DEContent.energy_core) level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(EnergyCore.ACTIVE, active));
//        });
//    }
    //    @Override
//    public void tick() {
//        if (!level.isClientSide) {
//            flowArray[ticksElapsed % 20] = (energy.get() - lastTickEnergy);
//            lastTickEnergy = energy.get();
//
//            long total = 0;
//            for (long i : flowArray) {
//                total += i;
//            }
//            transferRate.set(total / 20L);
//
//            if (activeBuilder != null) {
//                if (activeBuilder.isDead()) {
//                    activeBuilder = null;
//                } else {
//                    activeBuilder.updateProcess();
//                }
//            }
//
//            if (ticksElapsed % 500 == 0) {
//                validateStructure();
//            }
//
//            double diff = lastTickInput - inputRate.get();
//            inputRate.add(diff / 10);
//            diff = lastTickOutput - outputRate.get();
//            outputRate.add(diff / 10);
//
//            lastTickInput = lastTickOutput = 0;
//        } else {
//            rotation++;
//        }
//
//        super.tick();
//
//        if (ticksElapsed % 20 == 0 && !level.isClientSide && transferRate.isDirty(true)) {
//            dataManager.forceSync(transferRate);
//        }
//
//        if (level.isClientSide && active.get()) {
//            List<Player> players = level.getEntitiesOfClass(Player.class, new AABB(worldPosition, worldPosition.offset(1, 1, 1)).inflate(10, 10, 10));
//            for (Player player : players) {
//                double dist = Vec3D.getCenter(this).distance(new Vec3D(player));
//                double distNext = new Vec3D(player).distance(new Vec3D(worldPosition.getX() + player.getDeltaMovement().x + 0.5, worldPosition.getY() + player.getDeltaMovement().y - 0.4, worldPosition.getZ() + player.getDeltaMovement().z + 0.5));
//                double threshold = tier.get() > 2 ? tier.get() - 0.5 : tier.get() + 0.5;
//                double boundary = distNext - threshold;
//                double dir = dist - distNext;
//
//                if (boundary <= 0) {
//                    if (dir < 0) {
//                        player.move(MoverType.PLAYER, new Vec3(-player.getDeltaMovement().x * 1.5, -player.getDeltaMovement().y * 1.5, -player.getDeltaMovement().z * 1.5));
//                    }
//
//                    double multiplier = (threshold - dist) * 0.05;
//
//                    double xm = ((worldPosition.getX() + 0.5 - player.getX()) / distNext) * multiplier;
//                    double ym = ((worldPosition.getY() - 0.4 - player.getY()) / distNext) * multiplier;
//                    double zm = ((worldPosition.getZ() + 0.5 - player.getZ()) / distNext) * multiplier;
//
//                    player.move(MoverType.PLAYER, new Vec3(-xm, -ym, -zm));
//                }
//            }
//        }
//
//        ticksElapsed++;
//    }
//
//
//    @Override
//    public void receivePacketFromClient(MCDataInput data, ServerPlayer client, int id) {
//        if (id == 0) { //Activate
//            if (active.get()) {
//                deactivateCore();
//            } else {
//                activateCore();
//            }
//        } else if (id == 1) { //Tier Up
//            if (!active.get() && tier.get() < 8) {
//                tier.inc();
//                validateStructure();
//            }
//        } else if (id == 2) { //Tier Down
//            if (!active.get() && tier.get() > 1) {
//                tier.dec();
//                validateStructure();
//            }
//        } else if (id == 3) { //Toggle Guide
//            if (!active.get()) {
//                buildGuide.set(!buildGuide.get());
//            }
//        } else if (id == 4) { //Build
//            if (!active.get()) {
//                startBuilder(client);
//            }
//        }
//    }

//
//    //endregion
//
//    //region Structure
//

//

//

//
//    //endregion
//
//    //region Energy Transfer
//
//    public long receiveEnergy(long maxReceive, boolean simulate) {
//        if (level.isClientSide) {
//            return 0;
//        }
//        long energyReceived = Math.min(getExtendedCapacity() - energy.get(), maxReceive);
//
//        if (!simulate) {
//            energy.add(energyReceived);
//            lastTickInput += energyReceived;
//            setChanged();
//        }
//        return energyReceived;
//    }
//
//    public long extractEnergy(long maxExtract, boolean simulate) {
//        if (level.isClientSide) {
//            return 0;
//        }
//        long energyExtracted = Math.min(energy.get(), maxExtract);
//
//        if (!simulate) {
//            energy.subtract(energyExtracted);
//            lastTickOutput += energyExtracted;
//            setChanged();
//        }
//        return energyExtracted;
//    }
//
//    @Override
//    public long getExtendedStorage() {
//        return energy.get();
//    }
//
//    @Override
//    public long getExtendedCapacity() {
//        return getCapacity();
//    }
//
//    //endregion
//
//    //region IMultiBlock
//
//    @Override
//    public boolean isStructureValid() {
//        return structureValid.get();
//    }
//
//    @Override
//    public IMultiBlockPart getController() {
//        return this;
//    }
//
//    //endregion
//
//    //region Rendering

//
//    //endregion


    // MultiBlock

    @Nullable
    public MultiBlockDefinition getMultiBlockDef() {
        if (definitionCache == null || defCacheLastTier != tier.get()) {
            defCacheLastTier = tier.get();
            definitionCache = MultiBlockManager.getDefinition(new ResourceLocation(DraconicEvolution.MODID, "energy_core_" + tier.get()));
        }
        return definitionCache;
    }

    private void attemptAutoBuild(ServerPlayer player) {
        if (activeBuilder != null && !activeBuilder.isDead()) {
            player.sendMessage(new TranslatableComponent("msg.draconicevolution.energy_core.already_building").withStyle(ChatFormatting.RED), Util.NIL_UUID);
        } else {
            MultiBlockDefinition definition = getMultiBlockDef();
            if (definition != null) {
                activeBuilder = new MultiBlockBuilder(level, getBlockPos(), definition, player, this);
            }
        }
    }

    // ### Rendering

    public int getColour() {
        if (tier.get() == 8) {
            return Colour.packRGBA(1F, 0.28F, 0.05F, 1F);
        }

        float colour = 1F - fillPercent.get();
        return Colour.packRGBA(1F, colour * 0.3f, colour * 0.7f, 1F);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean renderSelectionBox(DrawSelectionEvent.HighlightBlock event) {
        return false;
    }

    //    VoxelShape shape = null;
//    VoxelShape shape2 = null;
    @Override
    public VoxelShape getShapeForPart(BlockPos pos, CollisionContext context) {
        //Todo efficiently generate a shape that represents the entire core.
//        Vector3 offset = Vector3.fromBlockPosCenter(worldPosition);
//        offset.subtract(pos);
//
//        if (shape2 == null || shape == null) {
//            double resolution = 1;//0.25;
//            double hr = resolution / 2;
//            double rad = 3.75;//Math.round((RenderTileEnergyCore.SCALES[tier.get() - 1] / 2) / resolution) * resolution;
//
//            shape = Shapes.empty();
//            List<VoxelShape> shapes = new ArrayList<>();
//
//            for (double x = -rad; x < rad; x += resolution) {
//                for (double y = -rad; y < rad; y += resolution) {
//                    for (double z = -rad; z < rad; z += resolution) {
//                        if (MathUtils.distanceSq(new Vector3(0, 0, 0), new Vector3(x, y, z)) <= rad * rad) {
//                            shapes.add(Shapes.box(x - hr, y - hr, z - hr, x + hr, y + hr, z + hr));
//                        }
//                    }
//                }
//            }
//
//            shape = Shapes.or(shape, shapes.toArray(new VoxelShape[0]));
//        }
//
//        shape2 = shape;
//        return shape.move(offset.x, offset.y, offset.z);
        return Shapes.block();
    }
}

