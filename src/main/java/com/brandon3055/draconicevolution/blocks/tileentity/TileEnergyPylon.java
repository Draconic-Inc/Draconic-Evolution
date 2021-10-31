package com.brandon3055.draconicevolution.blocks.tileentity;

import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.SAVE_NBT_SYNC_TILE;
import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.TRIGGER_UPDATE;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.brandon3055.brandonscore.api.power.IExtendedRFStorage;
import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.brandonscore.client.particle.IntParticleType;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedByte;
import com.brandon3055.brandonscore.lib.datamanager.ManagedVec3I;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.blocks.machines.EnergyPylon;
import com.brandon3055.draconicevolution.client.DEParticles;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.integration.computers.PeripheralEnergyPylon;

import net.minecraft.block.Blocks;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class TileEnergyPylon extends TileBCore implements ITickableTileEntity, IMultiBlockPart, IExtendedRFStorage {
    public final ManagedBool isOutputMode = register(new ManagedBool("is_output_mode", SAVE_NBT_SYNC_TILE, TRIGGER_UPDATE));
    public final ManagedBool structureValid = register(new ManagedBool("structure_valid", SAVE_NBT_SYNC_TILE, TRIGGER_UPDATE));
    public final ManagedVec3I coreOffset = register(new ManagedVec3I("core_offset", new Vec3I(0, -1, 0), SAVE_NBT_SYNC_TILE));
    public final ManagedBool sphereOnTop = register(new ManagedBool("sphere_on_top", true, SAVE_NBT_SYNC_TILE));
    public final ManagedBool hasCoreLock = register(new ManagedBool("has_core_lock", SAVE_NBT_SYNC_TILE));
    private final ManagedByte particleRate = register(new ManagedByte("particle_rate", SAVE_NBT_SYNC_TILE));
    private TileEnergyCore core = null;
    private int coreSelection = 0;
    private int tick = 0;
    private int lastCompOverride = 0;

    private IOPStorage opAdapter = new IOPStorage() {
        @Override
        public boolean canExtract() {
            return getExtendedStorage() > 0;
        }

        @Override
        public boolean canReceive() {
            return getExtendedStorage() < getExtendedCapacity();
        }

        @Override
        public long receiveOP(long maxReceive, boolean simulate) {
            if (!hasCoreLock.get() || isOutputMode.get() || getCore() == null || !getCore().active.get()) {
                return 0;
            }

            long received = getCore().receiveEnergy(maxReceive, simulate);

            if (!simulate && received > 0) {
                particleRate.set((byte) Math.min(20, received < 500 ? 1 : received / 500));
            }

            return received;
        }

        @Override
        public long extractOP(long maxExtract, boolean simulate) {
            if (!hasCoreLock.get() || !isOutputMode.get() || getCore() == null || !getCore().active.get()) {
                return 0;
            }

            long extracted = getCore().extractEnergy(maxExtract, simulate);

            if (!simulate && extracted > 0) {
                particleRate.set((byte) Math.min(20, extracted < 500 && extracted > 0 ? 1 : extracted / 500));
            }

            return extracted;
        }

        @Override
        public long getOPStored() {
            return getExtendedStorage();
        }

        @Override
        public long getMaxOPStored() {
            return getExtendedCapacity();
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
    };

    public TileEnergyPylon() {
        super(DEContent.tile_energy_pylon);
        capManager.set(CapabilityOP.OP, opAdapter);
    }

    @Override
    public void tick() {
        super.tick();
        if (!structureValid.get() || !hasCoreLock.get() || getCore() == null || !getCore().active.get()) {
            return;
        }

        if (tick++ % 10 == 0 && getExtendedCapacity() > 0) {
            updateComparators();
        }

        if (!level.isClientSide && isOutputMode.get()) {
            long extracted = getCore().extractEnergy(sendEnergyToAll(opAdapter.getOPStored(), opAdapter.getOPStored()), false);
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
        int cOut = (int) (((double) getExtendedStorage() / getExtendedCapacity()) * 15D);
        if (cOut != lastCompOverride) {
            lastCompOverride = cOut;
            level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
        }
    }

    //region MultiBlock Handling

    public void invertIO() {
        isOutputMode.invert();
        level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(EnergyPylon.OUTPUT, isOutputMode.get()));
    }

    public TileEnergyCore getCore() {
        if (hasCoreLock.get()) {
            BlockPos corePos = worldPosition.subtract(coreOffset.get().getPos());
            Chunk coreChunk = level.getChunkAt(corePos);

            if (!level.isAreaLoaded(corePos, 16)) {
                core = null;
                return null;
            }

            TileEntity tileAtPos = coreChunk.getBlockEntity(corePos, Chunk.CreateEntityType.CHECK);
            if (tileAtPos == null || core == null || tileAtPos != core) {
                TileEntity tile = level.getBlockEntity(corePos);

                if (tile instanceof TileEnergyCore) {
                    core = (TileEnergyCore) tile;
                } else {
                    core = null;
                    hasCoreLock.set(false);
                }
            }
        }
        return core;
    }

    private List<TileEnergyCore> findActiveCores() {
        List<TileEnergyCore> list = new LinkedList<>();
        int yMod = sphereOnTop.get() ? 18 : -18;
        int range = 18;

        Iterable<BlockPos> positions = BlockPos.betweenClosed(worldPosition.offset(-range, -range + yMod, -range), worldPosition.offset(range, range + yMod, range));

        for (BlockPos blockPos : positions) {
            if (level.getBlockState(blockPos).getBlock() == DEContent.energy_core) {
                TileEntity tile = level.getBlockEntity(blockPos);
                if (tile instanceof TileEnergyCore && ((TileEnergyCore) tile).active.get()) {
                    list.add(((TileEnergyCore) tile));
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
            hasCoreLock.set(false);
            return;
        }

        if (coreSelection >= cores.size()) {
            coreSelection = 0;
        }

        TileEnergyCore selectedCore = cores.get(coreSelection);
        coreOffset.set(new Vec3I(worldPosition.subtract(selectedCore.getBlockPos())));
        core = selectedCore;
        hasCoreLock.set(true);
        level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
        coreSelection++;

        if (hasCoreLock.get()) {
            drawParticleBeam();
        }

        updateBlock();
    }

    @Override
    public boolean validateStructure() {
        if (!structureValid.get()) {
            if (level.getBlockState(worldPosition.offset(0, 1, 0)).getBlock() == Blocks.GLASS) {
                level.setBlockAndUpdate(worldPosition.offset(0, 1, 0), DEContent.energy_core_structure.defaultBlockState());
                TileEntity tile = level.getBlockEntity(worldPosition.offset(0, 1, 0));
                if (tile instanceof TileCoreStructure) {
                    ((TileCoreStructure) tile).blockName.set("minecraft:glass");
                    ((TileCoreStructure) tile).setController(this);
                }
                sphereOnTop.set(true);
                level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(EnergyPylon.FACING, "up"));
            } else if (level.getBlockState(worldPosition.offset(0, -1, 0)).getBlock() == Blocks.GLASS) {
                level.setBlockAndUpdate(worldPosition.offset(0, -1, 0), DEContent.energy_core_structure.defaultBlockState());
                TileEntity tile = level.getBlockEntity(worldPosition.offset(0, -1, 0));
                if (tile instanceof TileCoreStructure) {
                    ((TileCoreStructure) tile).blockName.set("minecraft:glass");
                    ((TileCoreStructure) tile).setController(this);
                }
                sphereOnTop.set(false);
                level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(EnergyPylon.FACING, "down"));
            } else {
                level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(EnergyPylon.FACING, "null"));
                return false;
            }
        }

        structureValid.set(isStructureValid());
        if (structureValid.get() && !hasCoreLock.get()) {
            selectNextCore();
        } else if (!structureValid.get() && hasCoreLock.get()) {
            hasCoreLock.set(false);
        }

        if (hasCoreLock.get() && level.isClientSide) {
            drawParticleBeam();
        }

        return structureValid.get();
    }

    @Override
    public boolean isStructureValid() {
        return (isGlass(worldPosition.offset(0, 1, 0)) || isGlass(worldPosition.offset(0, -1, 0))) && (!isGlass(worldPosition.offset(0, 1, 0)) || !isGlass(worldPosition.offset(0, -1, 0)));
    }

    private boolean isGlass(BlockPos pos) {
        TileEntity tile = level.getBlockEntity(pos);
        return tile instanceof TileCoreStructure && ((TileCoreStructure) tile).blockName.get().equals("minecraft:glass");
    }

    //endregion

    //region Rendering

    private void drawParticleBeam() {
        if (getCore() == null) return;

        BlockPos thisPos = worldPosition.offset(0, sphereOnTop.get() ? 1 : -1, 0);
        Vec3D coreVec = Vec3D.getDirectionVec(new Vec3D(thisPos).add(0.5, 0.5, 0.5), new Vec3D(getCore().getBlockPos()).add(0.5, 0.5, 0.5));
        double coreDistance = Utils.getDistanceAtoB(new Vec3D(thisPos).add(0.5, 0.5, 0.5), new Vec3D(getCore().getBlockPos().offset(0.5, 0.5, 0.5)));

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

        if (particleRate.get() > 10) {
            for (int i = 0; i <= particleRate.get() / 10; i++) {
                spawn = getParticleSpawn(rand);
                dest = getParticleDest(rand);
//                BCEffectHandler.spawnFX(DEParticles.ENERGY_PARTICLE, world, spawn, dest, 0, 200, 255, 200);
                level.addParticle(new IntParticleType.IntParticleData(DEParticles.energy, 0, 200, 255, 200), spawn.x, spawn.y, spawn.z, dest.x, dest.y, dest.z);

            }
        } else if (rand.nextInt(Math.max(1, 10 - particleRate.get())) == 0) {
            spawn = getParticleSpawn(rand);
            dest = getParticleDest(rand);
//            BCEffectHandler.spawnFX(DEParticles.ENERGY_PARTICLE, world, spawn, dest, 0, 200, 255, 200);
            level.addParticle(new IntParticleType.IntParticleData(DEParticles.energy, 0, 200, 255, 200), spawn.x, spawn.y, spawn.z, dest.x, dest.y, dest.z);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private Vec3D getParticleSpawn(Random random) {
        if (isOutputMode.get()) {
            double range = getCore().tier.get();
            return new Vec3D(getCore().getBlockPos()).add((random.nextFloat() - 0.5F) * range, (random.nextFloat() - 0.5F) * range, (random.nextFloat() - 0.5F) * range);
        } else {
            return sphereOnTop.get() ? new Vec3D(worldPosition).add(0.5, 1.5, 0.5) : new Vec3D(worldPosition).add(0.5, -0.5, 0.5);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private Vec3D getParticleDest(Random random) {
        if (isOutputMode.get()) {
            return sphereOnTop.get() ? new Vec3D(worldPosition).add(0.5, 1.5, 0.5) : new Vec3D(worldPosition).add(0.5, -0.5, 0.5);
        } else {
            double range = getCore().tier.get() / 2D;
            return new Vec3D(getCore().getBlockPos()).add(0.5, 0.5, 0.5).add((random.nextFloat() - 0.5F) * range, (random.nextFloat() - 0.5F) * range, (random.nextFloat() - 0.5F) * range);
        }
    }

//    @Override
//    public boolean shouldRenderInPass(int pass) {
//        return true;
//    }

    //endregion

    //region Unused IMultiBlock

    @Override
    public IMultiBlockPart getController() {
        return this;
    }

    //endregion

    @Override
    public long getExtendedStorage() {
        if (!hasCoreLock.get() || getCore() == null) {
            return 0;
        }
        return getCore().getExtendedStorage();
    }

    @Override
    public long getExtendedCapacity() {
        if (!hasCoreLock.get() || getCore() == null) {
            return 0;
        }
        return getCore().getExtendedCapacity();
    }

//    @Override
//    public Iterable<BlockPos> getBlocksForFrameMove() {
//        if (structureValid.get()) {
//            return Collections.singleton(sphereOnTop.get() ? pos.up() : pos.down());
//        }
//        return Collections.emptyList();
//    }
}
