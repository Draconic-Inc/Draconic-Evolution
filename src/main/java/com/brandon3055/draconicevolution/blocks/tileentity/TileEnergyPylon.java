package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedByte;
import com.brandon3055.brandonscore.lib.datamanager.ManagedVec3I;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEContent;
import com.brandon3055.draconicevolution.api.IExtendedRFStorage;
import com.brandon3055.draconicevolution.blocks.machines.EnergyPylon;
import com.brandon3055.draconicevolution.integration.computers.ArgHelper;
import com.brandon3055.draconicevolution.integration.computers.IDEPeripheral;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.SAVE_NBT_SYNC_TILE;
import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.TRIGGER_UPDATE;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class TileEnergyPylon extends TileBCore implements ITickableTileEntity, IMultiBlockPart, IExtendedRFStorage, IDEPeripheral {
    public final ManagedBool isOutputMode = register(new ManagedBool("is_output_mode", SAVE_NBT_SYNC_TILE, TRIGGER_UPDATE));
    public final ManagedBool structureValid = register(new ManagedBool("structure_valid", SAVE_NBT_SYNC_TILE, TRIGGER_UPDATE));
    public final ManagedVec3I coreOffset = register(new ManagedVec3I("core_offset", new Vec3I(0, -1, 0), SAVE_NBT_SYNC_TILE));
    public final ManagedBool sphereOnTop = register(new ManagedBool("sphere_on_top", true, SAVE_NBT_SYNC_TILE));
    private final ManagedBool hasCoreLock = register(new ManagedBool("has_core_lock", SAVE_NBT_SYNC_TILE));
    private final ManagedByte particleRate = register(new ManagedByte("particle_rate", SAVE_NBT_SYNC_TILE));
    private TileStorageCore core = null;
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

        if (!world.isRemote && isOutputMode.get()) {
            long extracted = getCore().extractEnergy(sendEnergyToAll(opAdapter.getEnergyStored(), opAdapter.getEnergyStored()), false);
            if (extracted > 0) {
                particleRate.set((byte) Math.min(20, extracted < 500 ? 1 : extracted / 500));
            }
        }

        if (world.isRemote) {
            spawnParticles();
        }

        if (!world.isRemote && (particleRate.get() > 1 || (particleRate.get() > 0 && world.rand.nextInt(2) == 0))) {
            particleRate.subtract((byte) 2);
        }
    }

    public void updateComparators() {
        int cOut = (int) (((double) getExtendedStorage() / getExtendedCapacity()) * 15D);
        if (cOut != lastCompOverride) {
            lastCompOverride = cOut;
            world.notifyNeighborsOfStateChange(pos, getBlockState().getBlock());
        }
    }

    //region MultiBlock Handling


    private TileStorageCore getCore() {
        if (hasCoreLock.get()) {
            BlockPos corePos = pos.subtract(coreOffset.get().getPos());
            Chunk coreChunk = world.getChunkAt(corePos);

            if (!world.isAreaLoaded(corePos, 16)) {
                core = null;
                return null;
            }

            TileEntity tileAtPos = coreChunk.getTileEntity(corePos, Chunk.CreateEntityType.CHECK);
            if (tileAtPos == null || core == null || tileAtPos != core) {
                TileEntity tile = world.getTileEntity(corePos);

                if (tile instanceof TileStorageCore) {
                    core = (TileStorageCore) tile;
                } else {
                    core = null;
                    hasCoreLock.set(false);
                }
            }
        }
        return core;
    }

    private List<TileStorageCore> findActiveCores() {
        List<TileStorageCore> list = new LinkedList<>();
        int yMod = sphereOnTop.get() ? 18 : -18;
        int range = 18;

        Iterable<BlockPos> positions = BlockPos.getAllInBoxMutable(pos.add(-range, -range + yMod, -range), pos.add(range, range + yMod, range));

        for (BlockPos blockPos : positions) {
            if (world.getBlockState(blockPos).getBlock() == DEContent.energy_core) {
                TileEntity tile = world.getTileEntity(blockPos);
                if (tile instanceof TileStorageCore && ((TileStorageCore) tile).active.get()) {
                    list.add(((TileStorageCore) tile));
                }
            }
        }

        return list;
    }

    public void selectNextCore() {
        if (world.isRemote) {
            return;
        }
        List<TileStorageCore> cores = findActiveCores();
        if (cores.size() == 0) {
            core = null;
            hasCoreLock.set(false);
            return;
        }

        if (coreSelection >= cores.size()) {
            coreSelection = 0;
        }

        TileStorageCore selectedCore = cores.get(coreSelection);
        coreOffset.set(new Vec3I(pos.subtract(selectedCore.getPos())));
        core = selectedCore;
        hasCoreLock.set(true);
        world.notifyNeighborsOfStateChange(pos, getBlockState().getBlock());
        coreSelection++;

        if (hasCoreLock.get()) {
            drawParticleBeam();
        }

        updateBlock();
    }

    @Override
    public boolean validateStructure() {
        if (!structureValid.get()) {
            if (world.getBlockState(pos.add(0, 1, 0)).getBlock() == Blocks.GLASS) {
                world.setBlockState(pos.add(0, 1, 0), DEContent.energy_core_structure.getDefaultState());
                TileEntity tile = world.getTileEntity(pos.add(0, 1, 0));
                if (tile instanceof TileCoreStructure) {
                    ((TileCoreStructure) tile).blockName = "minecraft:glass";
                    ((TileCoreStructure) tile).setController(this);
                }
                sphereOnTop.set(true);
                world.setBlockState(pos, world.getBlockState(pos).with(EnergyPylon.FACING, "up"));
            } else if (world.getBlockState(pos.add(0, -1, 0)).getBlock() == Blocks.GLASS) {
                world.setBlockState(pos.add(0, -1, 0), DEContent.energy_core_structure.getDefaultState());
                TileEntity tile = world.getTileEntity(pos.add(0, -1, 0));
                if (tile instanceof TileCoreStructure) {
                    ((TileCoreStructure) tile).blockName = "minecraft:glass";
                    ((TileCoreStructure) tile).setController(this);
                }
                sphereOnTop.set(false);
                world.setBlockState(pos, world.getBlockState(pos).with(EnergyPylon.FACING, "down"));
            } else {
                world.setBlockState(pos, world.getBlockState(pos).with(EnergyPylon.FACING, "null"));
                return false;
            }
        }

        structureValid.set(isStructureValid());
        if (structureValid.get() && !hasCoreLock.get()) {
            selectNextCore();
        } else if (!structureValid.get() && hasCoreLock.get()) {
            hasCoreLock.set(false);
        }

        if (hasCoreLock.get() && world.isRemote) {
            drawParticleBeam();
        }

        return structureValid.get();
    }

    @Override
    public boolean isStructureValid() {
        return (isGlass(pos.add(0, 1, 0)) || isGlass(pos.add(0, -1, 0))) && (!isGlass(pos.add(0, 1, 0)) || !isGlass(pos.add(0, -1, 0)));
    }

    private boolean isGlass(BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileCoreStructure && ((TileCoreStructure) tile).blockName.equals("minecraft:glass");
    }

    //endregion

    //region Rendering

    private void drawParticleBeam() {
        if (getCore() == null) return;

        BlockPos thisPos = pos.add(0, sphereOnTop.get() ? 1 : -1, 0);
        Vec3D coreVec = Vec3D.getDirectionVec(new Vec3D(thisPos).add(0.5, 0.5, 0.5), new Vec3D(getCore().getPos()).add(0.5, 0.5, 0.5));
        double coreDistance = Utils.getDistanceAtoB(new Vec3D(thisPos).add(0.5, 0.5, 0.5), new Vec3D(getCore().getPos().add(0.5, 0.5, 0.5)));

        for (int i = 0; i < 100; i++) {
            double location = i / 100D;
            Vec3D particlePos = new Vec3D(thisPos).add(0.5, 0.5, 0.5);
            particlePos.add(coreVec.x * coreDistance * location, coreVec.y * coreDistance * location, coreVec.z * coreDistance * location);

            double speed = 0.02F;
            double offset = 0.2F;
            double randX = world.rand.nextDouble() - 0.5D;
            double randY = world.rand.nextDouble() - 0.5D;
            double randZ = world.rand.nextDouble() - 0.5D;
            //TODO Particles
//            BCEffectHandler.spawnFX(DEParticles.LINE_INDICATOR, world, particlePos.add(randX * offset, randY * offset, randZ * offset), new Vec3D(randX * speed, randY * speed, randZ * speed), 150, 0, 255);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnParticles() {
        Random rand = world.rand;
        if (getCore() == null || particleRate.get() <= 0) return;
        if (particleRate.get() > 20) particleRate.set((byte) 20);

        Vec3D spawn;
        Vec3D dest;

        if (particleRate.get() > 10) {
            for (int i = 0; i <= particleRate.get() / 10; i++) {
                spawn = getParticleSpawn(rand);
                dest = getParticleDest(rand);

//                BCEffectHandler.spawnFX(DEParticles.ENERGY_PARTICLE, world, spawn, dest, 0, 200, 255, 200);
            }
        } else if (rand.nextInt(Math.max(1, 10 - particleRate.get())) == 0) {
            spawn = getParticleSpawn(rand);
            dest = getParticleDest(rand);

//            BCEffectHandler.spawnFX(DEParticles.ENERGY_PARTICLE, world, spawn, dest, 0, 200, 255, 200);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private Vec3D getParticleSpawn(Random random) {
        if (isOutputMode.get()) {
            double range = getCore().tier.get();
            return new Vec3D(getCore().getPos()).add((random.nextFloat() - 0.5F) * range, (random.nextFloat() - 0.5F) * range, (random.nextFloat() - 0.5F) * range);
        } else {
            return sphereOnTop.get() ? new Vec3D(pos).add(0.5, 1.5, 0.5) : new Vec3D(pos).add(0.5, -0.5, 0.5);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private Vec3D getParticleDest(Random random) {
        if (isOutputMode.get()) {
            return sphereOnTop.get() ? new Vec3D(pos).add(0.5, 1.5, 0.5) : new Vec3D(pos).add(0.5, -0.5, 0.5);
        } else {
            double range = getCore().tier.get() / 2D;
            return new Vec3D(getCore().getPos()).add((random.nextFloat() - 0.5F) * range, (random.nextFloat() - 0.5F) * range, (random.nextFloat() - 0.5F) * range);
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

    //region IDEPeripheral

    @Override
    public String getPeripheralName() {
        return "draconic_rf_storage";
    }

    @Override
    public String[] getMethodNames() {
        return new String[]{"getEnergyStored", "getMaxEnergyStored", "getTransferPerTick"};
    }

    @Override
    public Object[] callMethod(String method, ArgHelper args) {
        if (method.equals("getEnergyStored")) {
            return new Object[]{getExtendedStorage()};
        } else if (method.equals("getMaxEnergyStored")) {
            return new Object[]{getExtendedCapacity()};
        } else if (method.equals("getTransferPerTick")) {
            if (!hasCoreLock.get() || getCore() == null) {
                return new Object[0];
            }
            return new Object[]{getCore().transferRate.get()};
        }
        return new Object[0];
    }

    //endregion

//    @Override
//    public Iterable<BlockPos> getBlocksForFrameMove() {
//        if (structureValid.get()) {
//            return Collections.singleton(sphereOnTop.get() ? pos.up() : pos.down());
//        }
//        return Collections.emptyList();
//    }
}
