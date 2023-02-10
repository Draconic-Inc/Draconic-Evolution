package com.brandon3055.draconicevolution.blocks.tileentity;

import codechicken.lib.colour.EnumColour;
import codechicken.lib.data.MCDataInput;
import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.brandonscore.client.particle.IntParticleType;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.lib.datamanager.*;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.blocks.StructureBlock;
import com.brandon3055.draconicevolution.blocks.machines.EnergyPylon;
import com.brandon3055.draconicevolution.blocks.machines.EnergyPylon.Mode;
import com.brandon3055.draconicevolution.client.DEParticles;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.DrawSelectionEvent;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class TileEnergyPylon extends TileBCore implements MultiBlockController {
    private static final VoxelShape SPHERE_SHAPE = Shapes.box(0.2, 0.2, 0.2, 0.8, 0.8, 0.8);

    public final ManagedEnum<Mode> ioMode = register(new ManagedEnum<>("io_mode", Mode.OUTPUT, DataFlags.SAVE_NBT_SYNC_TILE));
    public final ManagedEnum<Direction> direction = register(new ManagedEnum<>("direction", Direction.UP, DataFlags.SAVE_NBT_SYNC_TILE));
    public final ManagedEnum<EnumColour> colour = register(new ManagedEnum<>("colour", EnumColour.class, null, DataFlags.SAVE_NBT_SYNC_TILE));
    public final ManagedBool structureValid = register(new ManagedBool("structure_valid", DataFlags.SAVE_NBT_SYNC_TILE));
    public final ManagedPos coreOffset = register(new ManagedPos("core_offset", (BlockPos) null, DataFlags.SAVE_NBT_SYNC_TILE));
    private final ManagedByte particleRate = register(new ManagedByte("particle_rate", DataFlags.SYNC_TILE));

    private TileEnergyCore core = null;
    private int coreSelection = 0;
    private int tick = 0;
    private int lastCompOverride = 0;

    public IOPStorage opAdapter = new IOPStorage() {
        @Override
        public boolean canExtract() {
            return ioMode.get().canExtract();
        }

        @Override
        public boolean canReceive() {
            return ioMode.get().canReceive();
        }

        @Override
        public long receiveOP(long maxReceive, boolean simulate) {
            if (coreOffset.isNull() || !canReceive() || getCore() == null || !getCore().active.get()) {
                return 0;
            }
            long received = getCore().energy.receiveOP(maxReceive, simulate);
            if (!simulate && received > 0) {
                particleRate.set((byte) Math.min(20, received < 500 ? 1 : received / 500));
            }
            return received;
        }

        @Override
        public long extractOP(long maxExtract, boolean simulate) {
            if (coreOffset.isNull() || !canExtract() || getCore() == null || !getCore().active.get()) {
                return 0;
            }
            long extracted = getCore().energy.extractOP(maxExtract, simulate);
            if (!simulate && extracted > 0) {
                particleRate.set((byte) Math.min(20, extracted < 500 ? 1 : extracted / 500));
            }
            return extracted;
        }

        @Override
        public long getOPStored() {
            return coreOffset.notNull() && getCore() != null ? getCore().energy.getOPStored() : 0;
        }

        @Override
        public long getMaxOPStored() {
            return coreOffset.notNull() && getCore() != null ? getCore().energy.getMaxOPStored() : 0;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return (int) receiveOP(maxReceive, simulate);
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return (int) extractOP(maxExtract, simulate);
        }

        @Override
        public int getEnergyStored() {
            return (int) Math.min(getOPStored(), Integer.MAX_VALUE);
        }

        @Override
        public int getMaxEnergyStored() {
            return (int) Math.min(getMaxOPStored(), Integer.MAX_VALUE);
        }

        @Override
        public long modifyEnergyStored(long amount) {
            return 0; //Invalid operation for this device
        }
    };


    public TileEnergyPylon(BlockPos pos, BlockState state) {
        super(DEContent.tile_energy_pylon, pos, state);
        capManager.set(CapabilityOP.OP, opAdapter);
    }

    @Override
    public void tick() {
        super.tick();
        if (!structureValid.get() || coreOffset.isNull() || getCore() == null || !getCore().active.get()) {
            return;
        }

        if (tick++ % 10 == 0 && opAdapter.getMaxOPStored() > 0) {
            updateComparators();
        }

        if (!level.isClientSide && ioMode.get().canExtract()) {
            long extracted = core.energy.extractOP(sendEnergyToAll(core.energy.getUncappedStored(), core.energy.getUncappedStored()), false);
            if (extracted > 0) {
                particleRate.set((byte) Math.min(20, extracted < 500 ? 1 : extracted / 500));
            }
        }

        if (level.isClientSide) {
            spawnParticles();
        }

        if (!level.isClientSide && (particleRate.get() > 1 || (particleRate.get() > 0 && level.random.nextInt(2) == 0))) {
            particleRate.subtract((byte) 2);
        }
    }

    public void updateComparators() {
        int cOut = 0;
        if (getCore() != null) {
            cOut = (int) (((double) core.energy.getUncappedStored() / core.energy.getMaxOPStored()) * 15D);
        }
        if (cOut != lastCompOverride) {
            lastCompOverride = cOut;
            level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
        }
    }
    // ### Core Connection Handling


    @Override
    public InteractionResult handleRemoteClick(Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            ioMode.set(ioMode.get().reverse());
            level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(EnergyPylon.MODE, ioMode.get()));
        }
        return InteractionResult.SUCCESS;
    }

    public TileEnergyCore getCore() {
        BlockPos pos = coreOffset.get();
        if (pos != null) {
            BlockPos corePos = worldPosition.subtract(pos);
            LevelChunk coreChunk = level.getChunkAt(corePos);

            if (!level.isAreaLoaded(corePos, 16)) {
                core = null;
                return null;
            }

            BlockEntity tileAtPos = coreChunk.getBlockEntity(corePos, LevelChunk.EntityCreationType.CHECK);
            if (tileAtPos == null || core == null || tileAtPos != core) {
                BlockEntity tile = level.getBlockEntity(corePos);

                if (tile instanceof TileEnergyCore) {
                    core = (TileEnergyCore) tile;
                } else {
                    core = null;
                    coreOffset.set(null);

                }
            }
        }
        return core;
    }

    private List<TileEnergyCore> findActiveCores() {
        List<TileEnergyCore> list = new LinkedList<>();
        int range = 18;
        Direction dir = direction.get();
        BlockPos offset = new BlockPos(dir.getStepX() * range, dir.getStepY() * range, dir.getStepZ() * range);
        BlockPos min = worldPosition.offset(-18, -18, -18).offset(offset);
        BlockPos max = worldPosition.offset(18, 18, 18).offset(offset);

        for (BlockPos blockPos : BlockPos.betweenClosed(min, max)) {
            if (level.getBlockState(blockPos).getBlock() == DEContent.energy_core) {
                if (level.getBlockEntity(blockPos) instanceof TileEnergyCore tile && tile.active.get()) {
                    list.add(tile);
                }
            }
        }

        return list;
    }

    public void selectNextCore() {
        if (level.isClientSide) {
            return;
        }
        List<TileEnergyCore> cores = findActiveCores();
        if (cores.size() == 0) {
            core = null;
            coreOffset.set(null);
            return;
        }

        if (coreSelection >= cores.size()) {
            coreSelection = 0;
        }

        TileEnergyCore selectedCore = cores.get(coreSelection);
        coreOffset.set(new BlockPos(worldPosition.subtract(selectedCore.getBlockPos())));
        core = selectedCore;
        level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
        coreSelection++;
        detectAndSendChanges(false);
        drawParticleBeam();
    }
    // ### MultiBlock Handling


    @Override
    public boolean validateStructure() {
        if (!structureValid.get()) {
            boolean found = false;
            for (Direction dir : Direction.values()) {
                BlockPos pos = worldPosition.relative(dir);
                BlockState testState = level.getBlockState(pos);
                if (testState.is(Tags.Blocks.GLASS)) {
                    colour.set(getGlassColour(testState));
                    StructureBlock.buildingLock = true;
                    level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(EnergyPylon.FACING, dir));
                    level.setBlockAndUpdate(pos, DEContent.structure_block.defaultBlockState());
                    StructureBlock.buildingLock = false;
                    if (level.getBlockEntity(pos) instanceof TileStructureBlock tile) {
                        tile.blockName.set(testState.getBlock().getRegistryName());
                        tile.setController(this);
                        direction.set(dir);
                        found = true;
                        break;
                    }
                }
            }

            if (!found) {
                level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(EnergyPylon.FACING, Direction.UP));
                colour.set(null);
            }
        }

        structureValid.set(isStructureValid());
        if (structureValid.get() && coreOffset.isNull()) {
            selectNextCore();
        } else if (!structureValid.get() && coreOffset.notNull()) {
            coreOffset.set(null);
        }

        return structureValid.get();
    }

    @Override
    public boolean isStructureValid() {
        return isGlass(worldPosition.relative(direction.get()));
    }

    private boolean isGlass(BlockPos pos) {
        return level.getBlockEntity(pos) instanceof TileStructureBlock tile && tile.getOriginalBlock().defaultBlockState().is(Tags.Blocks.GLASS);
    }

    @Override
    public void receivePacketFromServer(MCDataInput data, int id) {
        if (id == 0) drawParticleBeam();
    }
    // ### Rendering


    public void drawParticleBeam() {
        if (!level.isClientSide) {
            sendPacketToChunk(mcDataOutput -> {}, 0);
            return;
        }
        if (getCore() == null) return;

        BlockPos thisPos = worldPosition.relative(direction.get());
        Vec3D coreVec = Vec3D.getDirectionVec(new Vec3D(thisPos).add(0.5, 0.5, 0.5), new Vec3D(getCore().getBlockPos()).add(0.5, 0.5, 0.5));
        double coreDistance = Utils.getDistance(new Vec3D(thisPos).add(0.5, 0.5, 0.5), new Vec3D(getCore().getBlockPos().offset(0.5, 0.5, 0.5)));

        for (int i = 0; i < 100; i++) {
            double location = i / 100D;
            Vec3D particlePos = new Vec3D(thisPos).add(0.5, 0.5, 0.5);
            particlePos.add(coreVec.x * coreDistance * location, coreVec.y * coreDistance * location, coreVec.z * coreDistance * location);

            double speed = 0.02F;
            double offset = 0.2F;
            double randX = level.random.nextDouble() - 0.5D;
            double randY = level.random.nextDouble() - 0.5D;
            double randZ = level.random.nextDouble() - 0.5D;
            particlePos.add(randX * offset, randY * offset, randZ * offset);

            level.addParticle(new IntParticleType.IntParticleData(DEParticles.line_indicator, 150, 0, 255, 40 + level.random.nextInt(20)), particlePos.x, particlePos.y, particlePos.z, randX * speed, randY * speed, randZ * speed);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnParticles() {
        Random rand = level.random;
        if (getCore() == null || particleRate.get() <= 0) return;
        if (particleRate.get() > 20) particleRate.set((byte) 20);

        Vec3D spawn;
        Vec3D dest;

        int r = 0;
        int g = 200;
        int b = 255;

        if (colour.notNull()) {
            r = (int) (colour.get().rF() * 255);
            g = (int) (colour.get().gF() * 255);
            b = (int) (colour.get().bF() * 255);
        }

        if (particleRate.get() > 10) {
            for (int i = 0; i <= particleRate.get() / 10; i++) {
                spawn = getParticleSpawn(rand);
                dest = getParticleDest(rand);
                level.addParticle(new IntParticleType.IntParticleData(DEParticles.energy, r, g, b, 200), spawn.x, spawn.y, spawn.z, dest.x, dest.y, dest.z);

            }
        } else if (rand.nextInt(Math.max(1, 10 - particleRate.get())) == 0) {
            spawn = getParticleSpawn(rand);
            dest = getParticleDest(rand);
            level.addParticle(new IntParticleType.IntParticleData(DEParticles.energy, r, g, b, 200), spawn.x, spawn.y, spawn.z, dest.x, dest.y, dest.z);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private Vec3D getParticleSpawn(Random random) {
        if (ioMode.get().canExtract()) {
            double range = getCore().tier.get();
            return new Vec3D(getCore().getBlockPos()).add((random.nextFloat() - 0.5F) * range, (random.nextFloat() - 0.5F) * range, (random.nextFloat() - 0.5F) * range);
        } else {
            return Vec3D.getCenter(worldPosition.relative(direction.get()));
        }
    }

    @OnlyIn(Dist.CLIENT)
    private Vec3D getParticleDest(Random random) {
        if (ioMode.get().canExtract()) {
            return Vec3D.getCenter(worldPosition.relative(direction.get()));
        } else {
            double range = getCore().tier.get() / 2D;
            return new Vec3D(getCore().getBlockPos()).add(0.5, 0.5, 0.5).add((random.nextFloat() - 0.5F) * range, (random.nextFloat() - 0.5F) * range, (random.nextFloat() - 0.5F) * range);
        }
    }

    @Override
    public VoxelShape getShapeForPart(BlockPos pos, CollisionContext context) {
        return SPHERE_SHAPE;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean renderSelectionBox(DrawSelectionEvent.HighlightBlock event) {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition.offset(-1, -1, -1), worldPosition.offset(2, 2, 2));
    }

    @Nullable
    public static EnumColour getGlassColour(BlockState state) {
        if (state.is(Tags.Blocks.GLASS_WHITE)) return EnumColour.WHITE;
        else if (state.is(Tags.Blocks.GLASS_ORANGE)) return EnumColour.ORANGE;
        else if (state.is(Tags.Blocks.GLASS_MAGENTA)) return EnumColour.MAGENTA;
        else if (state.is(Tags.Blocks.GLASS_LIGHT_BLUE)) return EnumColour.LIGHT_BLUE;
        else if (state.is(Tags.Blocks.GLASS_YELLOW)) return EnumColour.YELLOW;
        else if (state.is(Tags.Blocks.GLASS_LIME)) return EnumColour.LIME;
        else if (state.is(Tags.Blocks.GLASS_PINK)) return EnumColour.PINK;
        else if (state.is(Tags.Blocks.GLASS_GRAY)) return EnumColour.GRAY;
        else if (state.is(Tags.Blocks.GLASS_LIGHT_GRAY)) return EnumColour.LIGHT_GRAY;
        else if (state.is(Tags.Blocks.GLASS_CYAN)) return EnumColour.CYAN;
        else if (state.is(Tags.Blocks.GLASS_PURPLE)) return EnumColour.PURPLE;
        else if (state.is(Tags.Blocks.GLASS_BLUE)) return EnumColour.BLUE;
        else if (state.is(Tags.Blocks.GLASS_BROWN)) return EnumColour.BROWN;
        else if (state.is(Tags.Blocks.GLASS_GREEN)) return EnumColour.GREEN;
        else if (state.is(Tags.Blocks.GLASS_RED)) return EnumColour.RED;
        else if (state.is(Tags.Blocks.GLASS_BLACK)) return EnumColour.BLACK;
        return null;
    }
}
