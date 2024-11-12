package com.brandon3055.draconicevolution.blocks.tileentity;


import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.api.TimeKeeper;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.client.particle.IntParticleType;
import com.brandon3055.brandonscore.lib.IInteractTile;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.lib.datamanager.DataFlags;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedEnum;
import com.brandon3055.brandonscore.lib.datamanager.ManagedPos;
import com.brandon3055.brandonscore.utils.FacingUtils;
import com.brandon3055.draconicevolution.blocks.StructureBlock;
import com.brandon3055.draconicevolution.blocks.machines.EnergyCoreStabilizer;
import com.brandon3055.draconicevolution.client.DEParticles;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class TileEnergyCoreStabilizer extends TileBCore implements IInteractTile, MultiBlockController {

    public final ManagedPos coreOffset = register(new ManagedPos("core_offset", (BlockPos) null, DataFlags.SAVE_NBT_SYNC_TILE));
    public final ManagedBool isCoreActive = register(new ManagedBool("is_core_active", DataFlags.SAVE_NBT_SYNC_TILE));
    public final ManagedBool isValidMultiBlock = register(new ManagedBool("is_valid_multi_block", DataFlags.SAVE_NBT_SYNC_TILE, DataFlags.TRIGGER_UPDATE));
    public final ManagedEnum<Direction.Axis> multiBlockAxis = register(new ManagedEnum<>("multi_block_axis", Direction.Axis.Y, DataFlags.SAVE_NBT_SYNC_TILE, DataFlags.TRIGGER_UPDATE));
    public final ManagedEnum<Direction> coreDirection = register(new ManagedEnum<>("core_direction", Direction.DOWN, DataFlags.SAVE_NBT_SYNC_TILE, DataFlags.TRIGGER_UPDATE));
    public float rotation = 0;
    public float rotationSpeed = 0;
    private boolean moveCheckComplete = false;

    public TileEnergyCoreStabilizer(BlockPos pos, BlockState state) {
        super(DEContent.TILE_CORE_STABILIZER.get(), pos, state);
    }


    // ### Interaction

    @Override
    public InteractionResult handleRemoteClick(Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        TileEnergyCore core = getCore();
        if (core == null) {
            core = findCore();
        }

        if (core != null) {
            core.handleRemoteClick(player, hand, hit);
        } else {
            player.sendSystemMessage(Component.translatable("msg.draconicevolution.energy_core.core_not_found").withStyle(ChatFormatting.DARK_RED));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult onBlockUse(BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        return handleRemoteClick(player, hand, hit);
    }

    // ### Form Multi-block

    public void onPlaced() {
        if (level.isClientSide || checkAndFormMultiBlock()) {
            return;
        }

        for (BlockPos offset : FacingUtils.AROUND_ALL) {
            BlockEntity blockEntity = level.getBlockEntity(worldPosition.offset(offset));
            if (blockEntity instanceof TileEnergyCoreStabilizer && ((TileEnergyCoreStabilizer) blockEntity).checkAndFormMultiBlock()) {
                return;
            }
        }
    }

    /**
     * Checks if this block is at the center of a valid multiblock and if so activates the structure.
     *
     * @return true if structure was activated.
     */
    private boolean checkAndFormMultiBlock() {
        TileEnergyCore core = getCore();
        if (core != null && core.active.get()) {
            return false;
        }

        for (Direction.Axis axis : Direction.Axis.values()) {
            if (checkAxisValid(axis)) {
                buildMultiBlock(axis);
                return true;
            }
        }
        return false;
    }

    private boolean checkAxisValid(Direction.Axis axis) {
        for (BlockPos offset : FacingUtils.getAroundAxis(axis)) {
            if (!isAvailable(worldPosition.offset(offset))) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return true if there is a stabilizer at the given pos and it is available for use in a structure.
     * If structure is already formed will check if the block is an invisible tile with this as its master
     * In the case of the structure already formed this should be called from the controller.
     */
    private boolean isAvailable(BlockPos pos) {
        BlockEntity tile = level.getBlockEntity(pos);
        if (isValidMultiBlock.get()) {
            return tile instanceof TileStructureBlock && ((TileStructureBlock) tile).getController() == this;
        }

        return tile instanceof TileEnergyCoreStabilizer && (((TileEnergyCoreStabilizer) tile).getCore() == null || !((TileEnergyCoreStabilizer) tile).getCore().active.get());
    }

    private void buildMultiBlock(Direction.Axis axis) {
        coreOffset.set(null);
        StructureBlock.buildingLock = true;
        for (BlockPos offset : FacingUtils.getAroundAxis(axis)) {
            level.setBlockAndUpdate(worldPosition.offset(offset), DEContent.STRUCTURE_BLOCK.get().defaultBlockState());
            if (level.getBlockEntity(worldPosition.offset(offset)) instanceof TileStructureBlock tile) {
                tile.blockName.set(DEContent.ENERGY_CORE_STABILIZER.getId());
                tile.setController(this);
            }
        }

        level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(EnergyCoreStabilizer.LARGE, true));
        isValidMultiBlock.set(true);
        multiBlockAxis.set(axis);
        StructureBlock.buildingLock = false;
    }

    // ### Validate Multi-block

    @Override
    public boolean validateStructure() {
        if (checkAxisValid(multiBlockAxis.get())) {
            return true;
        }

        revertStructure();

        if (isCoreActive.get()) {
            TileEnergyCore core = getCore();
            if (core == null || !core.active.get()) {
                isCoreActive.set(false);
            }
        }
        return false;
    }

    @Override
    public boolean isStructureValid() {
        return isValidMultiBlock.get();
    }

    public boolean isSuitableForCore(int coreTier, TileEnergyCore core) {
        if (coreTier < TileEnergyCore.ADV_STABILIZER_TIER && !isValidMultiBlock.get()) {
            return true;
        } else if (coreTier >= TileEnergyCore.ADV_STABILIZER_TIER && isValidMultiBlock.get()) {
            BlockPos offset = worldPosition.subtract(core.getBlockPos());
            Direction direction = Direction.getNearest(offset.getX(), offset.getY(), offset.getZ()).getOpposite();
            return direction.getAxis() == multiBlockAxis.get();
        }
        return false;
    }

    // ### Revert Multi-block

    public void revertStructure() {
        if (level.getBlockState(worldPosition).is(DEContent.ENERGY_CORE_STABILIZER.get())) {
            level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(EnergyCoreStabilizer.LARGE, false));
        }
        isValidMultiBlock.set(false);

        if (getCore() != null) {
            getCore().deactivateCore();
        }

        for (BlockPos offset : FacingUtils.getAroundAxis(multiBlockAxis.get())) {
            BlockEntity tile = level.getBlockEntity(worldPosition.offset(offset));
            if (tile instanceof TileStructureBlock) {
                ((TileStructureBlock) tile).revert();
            }
        }
    }

    // ### Core Access

    public TileEnergyCore findCore() {
        //If we are already bound to a core then return it.
        if (getCore() != null) {
            return getCore();
        }

        //Otherwise look for a valid inactive core.
        for (Direction facing : Direction.values()) {
            for (int i = 0; i < 16; i++) {
                BlockEntity tile = level.getBlockEntity(worldPosition.offset(facing.getStepX() * i, facing.getStepY() * i, facing.getStepZ() * i));
                if (tile instanceof TileEnergyCore) {
                    TileEnergyCore core = (TileEnergyCore) tile;
                    core.validateStructure();
                    if (core.active.get()) {
                        continue;
                    }
                    return core;
                }
            }
        }

        return null;
    }

    public TileEnergyCore getCore() {
        BlockPos corePos = getCorePos();
        if (corePos != null) {
            BlockEntity tile = level.getBlockEntity(corePos);
            if (tile instanceof TileEnergyCore) {
                return (TileEnergyCore) tile;
            } else {
                coreOffset.set(null);
            }
        }
        return null;
    }

    @Nullable
    private BlockPos getCorePos() {
        return coreOffset.get() == null ? null : worldPosition.subtract(Objects.requireNonNull(coreOffset.get()));
    }

    public void setCore(@Nullable TileEnergyCore core) {
        if (core == null) {
            coreOffset.set(null);
            return;
        }
        BlockPos offset = worldPosition.subtract(core.getBlockPos());
        coreOffset.set(offset);
        coreDirection.set(Direction.getNearest(offset.getX(), offset.getY(), offset.getZ()).getOpposite());
    }

    // ### Tick / Rendering

    @Override
    public VoxelShape getShapeForPart(BlockPos pos, CollisionContext context) {
        BlockState stabState = level.getBlockState(worldPosition);
        if (stabState.is(DEContent.ENERGY_CORE_STABILIZER.get())) {
            BlockPos offset = worldPosition.subtract(pos);
            return stabState.getBlock().getShape(stabState, level, worldPosition, context).move(offset.getX(), offset.getY(), offset.getZ());
        }
        return Shapes.block();
    }


        @Override
    public void tick() {
        super.tick();
        if (level.isClientSide && coreOffset.get() != null && isCoreActive.get()) {
            rotation = TimeKeeper.getClientTick();
            updateVisual();
            if (isValidMultiBlock.get()) {
                updateVisual();
            }
        }
    }

    @OnlyIn (Dist.CLIENT)
    private void updateVisual() {
        Vec3D spawn = new Vec3D(worldPosition);
        spawn.add(0.5, 0.5, 0.5);
        double rand = level.random.nextInt(100) / 12D;
        double randOffset = rand * (Math.PI * 2D);
        double offsetX = Math.sin((ClientEventHandler.elapsedTicks / 180D * Math.PI) + randOffset);
        double offsetY = Math.cos((ClientEventHandler.elapsedTicks / 180D * Math.PI) + randOffset);

        if (!isValidMultiBlock.get() || level.random.nextBoolean()) {
            double d = isValidMultiBlock.get() ? 1.1 : 0.25;
            double inset = isValidMultiBlock.get() ? 1 : 0;
            if (coreDirection.get().getAxis() == Direction.Axis.Z) {
                spawn.add(offsetX * d, offsetY * d, (level.random.nextBoolean() ? -0.38 : 0.38) * inset);
            } else if (coreDirection.get().getAxis() == Direction.Axis.Y) {
                spawn.add(offsetX * d, (level.random.nextBoolean() ? -0.38 : 0.38) * inset, offsetY * d);
            } else if (coreDirection.get().getAxis() == Direction.Axis.X) {
                spawn.add((level.random.nextBoolean() ? -0.38 : 0.38) * inset, offsetY * d, offsetX * d);
            }
            Vector3 target = Vector3.fromBlockPosCenter(worldPosition).subtract(coreOffset.get());
            level.addParticle(new IntParticleType.IntParticleData(DEParticles.ENERGY_CORE.get(), 1, (int) (randOffset * 100D), isValidMultiBlock.get() ? 1 : 0), spawn.x, spawn.y, spawn.z, target.x, target.y, target.z);
        } else {
            if (coreDirection.get().getAxis() == Direction.Axis.Z) {
                spawn.add(offsetX * 1.2, offsetY * 1.2, level.random.nextBoolean() ? -0.38 : 0.38);
            } else if (coreDirection.get().getAxis() == Direction.Axis.Y) {
                spawn.add(offsetX * 1.2, level.random.nextBoolean() ? -0.38 : 0.38, offsetY * 1.2);
            } else if (coreDirection.get().getAxis() == Direction.Axis.X) {
                spawn.add(level.random.nextBoolean() ? -0.38 : 0.38, offsetY * 1.2, offsetX * 1.2);
            }
            Vector3 target = Vector3.fromBlockPosCenter(worldPosition);
            level.addParticle(new IntParticleType.IntParticleData(DEParticles.ENERGY_CORE.get(), 0), spawn.x, spawn.y, spawn.z, target.x, target.y, target.z);
        }
    }
////
////    //endregion
////
////    //region Activation
//
//

//
//    public boolean isStabilizerValid(int coreTier, TileEnergyCore core) {
//        if (coreTier < 5 && !isValidMultiBlock.get()) {
//            return true;
//        } else if (coreTier >= 5 && isValidMultiBlock.get()) {
//            BlockPos offset = worldPosition.subtract(core.getBlockPos());
//            Direction direction = Direction.getNearest(offset.getX(), offset.getY(), offset.getZ()).getOpposite();
//            return direction.getAxis() == multiBlockAxis.get();
//        }
//        return false;
//    }
//
//    //endregion
//
//    //region MultiBlock
//
//

//

    //

//

//

//    //region Unused IMultiBlock
//
//    @Override
//    public boolean isStructureValid() {
//        return isValidMultiBlock.get();
//    }
//
//    @Override
//    public IMultiBlockPart getController() {
//        return this;
//    }
//
//    //endregion
//
//    //endregion
//
//    //region Getters & Setters
//


}
