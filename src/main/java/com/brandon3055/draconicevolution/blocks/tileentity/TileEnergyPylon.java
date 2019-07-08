package com.brandon3055.draconicevolution.blocks.tileentity;

import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.blocks.TileEnergyBase;
import com.brandon3055.brandonscore.client.particle.BCEffectHandler;
import com.brandon3055.brandonscore.lib.EnergyHandlerWrapper;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedByte;
import com.brandon3055.brandonscore.lib.datamanager.ManagedVec3I;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.api.IExtendedRFStorage;
import com.brandon3055.draconicevolution.blocks.machines.EnergyPylon;
import com.brandon3055.draconicevolution.client.DEParticles;
import com.brandon3055.draconicevolution.integration.computers.ArgHelper;
import com.brandon3055.draconicevolution.integration.computers.IDEPeripheral;
import com.brandon3055.draconicevolution.integration.funkylocomotion.IMovableStructure;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.SAVE_NBT_SYNC_TILE;
import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.TRIGGER_UPDATE;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class TileEnergyPylon extends TileBCBase implements IEnergyReceiver, IEnergyProvider, ITickable, IMultiBlockPart, IExtendedRFStorage, IDEPeripheral, IMovableStructure {
    public final ManagedBool isOutputMode = register(new ManagedBool("isOutputMode", SAVE_NBT_SYNC_TILE, TRIGGER_UPDATE));
    public final ManagedBool structureValid = register(new ManagedBool("structureValid", SAVE_NBT_SYNC_TILE, TRIGGER_UPDATE));
    public final ManagedVec3I coreOffset = register(new ManagedVec3I("coreOffset", new Vec3I(0, -1, 0), SAVE_NBT_SYNC_TILE));
    public final ManagedBool sphereOnTop = register(new ManagedBool("sphereOnTop", true, SAVE_NBT_SYNC_TILE));
    private final ManagedBool hasCoreLock = register(new ManagedBool("hasCoreLock", SAVE_NBT_SYNC_TILE));
    private final ManagedByte particleRate = register(new ManagedByte("particleRate", SAVE_NBT_SYNC_TILE));
    private TileEnergyStorageCore core = null;
    private int coreSelection = 0;
    private int tick = 0;
    private int lastCompOverride = 0;

    public TileEnergyPylon() {
        this.setShouldRefreshOnBlockChange();
    }

    @Override
    public void update() {
        super.update();
        if (!structureValid.get() || !hasCoreLock.get() || getCore() == null || !getCore().active.get()) {
            return;
        }

        if (tick++ % 10 == 0 && getExtendedCapacity() > 0) {
            updateComparators();
        }

        if (!world.isRemote && isOutputMode.get()) {
            int extracted = getCore().extractEnergy(TileEnergyBase.sendEnergyToAll(world, pos, getEnergyStored(null)), false);
            if (extracted > 0) {
                particleRate.set((byte) Math.min(20, extracted < 500 && extracted > 0 ? 1 : extracted / 500));
            }
        }

        if (world.isRemote) {
            spawnParticles();
        }

        if (!world.isRemote && (particleRate.get() > 1 || (particleRate.get() > 0 && world.rand.nextInt(2) == 0))) {
            particleRate.subtract((byte)2);
        }
    }

    public void updateComparators() {
        int cOut = (int) (((double) getExtendedStorage() / getExtendedCapacity()) * 15D);
        if (cOut != lastCompOverride) {
            lastCompOverride = cOut;
            world.notifyNeighborsOfStateChange(pos, getBlockType(), true);
        }
    }

    //region MultiBlock Handling


    private TileEnergyStorageCore getCore() {
        if (hasCoreLock.get()) {
            BlockPos corePos = pos.subtract(coreOffset.get().getPos());
            Chunk coreChunk = world.getChunkFromBlockCoords(corePos);

            if (!coreChunk.isLoaded()) {
                core = null;
                return null;
            }

            TileEntity tileAtPos = coreChunk.getTileEntity(corePos, Chunk.EnumCreateEntityType.CHECK);
            if (tileAtPos == null || core == null || tileAtPos != core) {
                TileEntity tile = world.getTileEntity(corePos);

                if (tile instanceof TileEnergyStorageCore) {
                    core = (TileEnergyStorageCore) tile;
                }
                else {
                    core = null;
                    hasCoreLock.set(false);
                }
            }
        }
        return core;
    }

    private List<TileEnergyStorageCore> findActiveCores() {
        List<TileEnergyStorageCore> list = new LinkedList<>();
        int yMod = sphereOnTop.get() ? 18 : -18;
        int range = 18;

        Iterable<BlockPos> positions = BlockPos.getAllInBox(pos.add(-range, -range + yMod, -range), pos.add(range, range + yMod, range));

        for (BlockPos blockPos : positions) {
            if (world.getBlockState(blockPos).getBlock() == DEFeatures.energyStorageCore) {
                TileEntity tile = world.getTileEntity(blockPos);
                if (tile instanceof TileEnergyStorageCore && ((TileEnergyStorageCore) tile).active.get()) {
                    list.add(((TileEnergyStorageCore) tile));
                }
            }
        }

        return list;
    }

    public void selectNextCore() {
        if (world.isRemote) {
            return;
        }
        List<TileEnergyStorageCore> cores = findActiveCores();
        if (cores.size() == 0) {
            core = null;
            hasCoreLock.set(false);
            return;
        }

        if (coreSelection >= cores.size()) {
            coreSelection = 0;
        }

        TileEnergyStorageCore selectedCore = cores.get(coreSelection);
        coreOffset.set(new Vec3I(pos.subtract(selectedCore.getPos())));
        core = selectedCore;
        hasCoreLock.set(true);
        world.notifyNeighborsOfStateChange(pos, getBlockType(), true);
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
                world.setBlockState(pos.add(0, 1, 0), DEFeatures.invisECoreBlock.getDefaultState());
                TileEntity tile = world.getTileEntity(pos.add(0, 1, 0));
                if (tile instanceof TileInvisECoreBlock) {
                    ((TileInvisECoreBlock) tile).blockName = "minecraft:glass";
                    ((TileInvisECoreBlock) tile).setController(this);
                }
                sphereOnTop.set(true);
                world.setBlockState(pos, world.getBlockState(pos).withProperty(EnergyPylon.FACING, "up"));
            }
            else if (world.getBlockState(pos.add(0, -1, 0)).getBlock() == Blocks.GLASS) {
                world.setBlockState(pos.add(0, -1, 0), DEFeatures.invisECoreBlock.getDefaultState());
                TileEntity tile = world.getTileEntity(pos.add(0, -1, 0));
                if (tile instanceof TileInvisECoreBlock) {
                    ((TileInvisECoreBlock) tile).blockName = "minecraft:glass";
                    ((TileInvisECoreBlock) tile).setController(this);
                }
                sphereOnTop.set(false);
                world.setBlockState(pos, world.getBlockState(pos).withProperty(EnergyPylon.FACING, "down"));
            }
            else {
                world.setBlockState(pos, world.getBlockState(pos).withProperty(EnergyPylon.FACING, "null"));
                return false;
            }
        }

        structureValid.set(isStructureValid());
        if (structureValid.get() && !hasCoreLock.get()) {
            selectNextCore();
        }
        else if (!structureValid.get() && hasCoreLock.get()) {
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
        return tile instanceof TileInvisECoreBlock && ((TileInvisECoreBlock) tile).blockName.equals("minecraft:glass");
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
            BCEffectHandler.spawnFX(DEParticles.LINE_INDICATOR, world, particlePos.add(randX * offset, randY * offset, randZ * offset), new Vec3D(randX * speed, randY * speed, randZ * speed), 150, 0, 255);
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnParticles() {
        Random rand = world.rand;
        if (getCore() == null || particleRate.get() <= 0) return;
        if (particleRate.get() > 20) particleRate.set((byte)20);

        Vec3D spawn;
        Vec3D dest;

        if (particleRate.get() > 10) {
            for (int i = 0; i <= particleRate.get() / 10; i++) {
                spawn = getParticleSpawn(rand);
                dest = getParticleDest(rand);

                BCEffectHandler.spawnFX(DEParticles.ENERGY_PARTICLE, world, spawn, dest, 0, 200, 255, 200);
            }
        }
        else if (rand.nextInt(Math.max(1, 10 - particleRate.get())) == 0) {
            spawn = getParticleSpawn(rand);
            dest = getParticleDest(rand);

            BCEffectHandler.spawnFX(DEParticles.ENERGY_PARTICLE, world, spawn, dest, 0, 200, 255, 200);
        }
    }

    @SideOnly(Side.CLIENT)
    private Vec3D getParticleSpawn(Random random) {
        if (isOutputMode.get()) {
            double range = getCore().tier.get();
            return new Vec3D(getCore().getPos()).add((random.nextFloat() - 0.5F) * range, (random.nextFloat() - 0.5F) * range, (random.nextFloat() - 0.5F) * range);
        }
        else {
            return sphereOnTop.get() ? new Vec3D(pos).add(0.5, 1.5, 0.5) : new Vec3D(pos).add(0.5, -0.5, 0.5);
        }
    }

    @SideOnly(Side.CLIENT)
    private Vec3D getParticleDest(Random random) {
        if (isOutputMode.get()) {
            return sphereOnTop.get() ? new Vec3D(pos).add(0.5, 1.5, 0.5) : new Vec3D(pos).add(0.5, -0.5, 0.5);
        }
        else {
            double range = getCore().tier.get() / 2D;
            return new Vec3D(getCore().getPos()).add((random.nextFloat() - 0.5F) * range, (random.nextFloat() - 0.5F) * range, (random.nextFloat() - 0.5F) * range);
        }
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return true;
    }

    //endregion

    //region Unused IMultiBlock

    @Override
    public IMultiBlockPart getController() {
        return this;
    }

    //endregion

    //region Energy Handling

    @Override
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
        if (!hasCoreLock.get() || !isOutputMode.get() || getCore() == null || !getCore().active.get()) {
            return 0;
        }

        int extracted = getCore().extractEnergy(maxExtract, simulate);

        if (!simulate && extracted > 0) {
            particleRate.set((byte) Math.min(20, extracted < 500 && extracted > 0 ? 1 : extracted / 500));
        }

        return extracted;
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        if (!hasCoreLock.get() || isOutputMode.get() || getCore() == null || !getCore().active.get()) {
            return 0;
        }

        int received = getCore().receiveEnergy(maxReceive, simulate);

        if (!simulate && received > 0) {
            particleRate.set((byte) Math.min(20, received < 500 && received > 0 ? 1 : received / 500));
        }

        return received;
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        if (!hasCoreLock.get() || getCore() == null) {
            return 0;
        }
        return (int) Math.min(getCore().getExtendedStorage(), Integer.MAX_VALUE);
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        if (!hasCoreLock.get() || getCore() == null) {
            return 0;
        }
        return (int) Math.min(getCore().getExtendedCapacity(), Integer.MAX_VALUE);
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return hasCoreLock.get();
    }

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

    //endregion

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
        }
        else if (method.equals("getMaxEnergyStored")) {
            return new Object[]{getExtendedCapacity()};
        }
        else if (method.equals("getTransferPerTick")) {
            if (!hasCoreLock.get() || getCore() == null) {
                return new Object[0];
            }
            return new Object[]{getCore().transferRate.get()};
        }
        return new Object[0];
    }

    //endregion

    //region Capability

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(new EnergyHandlerWrapper(this, facing));
        }

        return super.getCapability(capability, facing);
    }

    //endregion


    @Override
    public Iterable<BlockPos> getBlocksForFrameMove() {
        if (structureValid.get()) {
            return Collections.singleton(sphereOnTop.get() ? pos.up() : pos.down());
        }
        return Collections.emptyList();
    }
}
