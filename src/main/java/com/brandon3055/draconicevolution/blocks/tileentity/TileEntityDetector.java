package com.brandon3055.draconicevolution.blocks.tileentity;

import codechicken.lib.data.MCDataInput;
import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.api.power.OPStorage;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.brandonscore.lib.IInteractTile;
import com.brandon3055.brandonscore.lib.IRedstoneEmitter;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.lib.datamanager.DataFlags;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedByte;
import com.brandon3055.brandonscore.lib.datamanager.ManagedShort;
import com.brandon3055.brandonscore.lib.entityfilter.EntityFilter;
import com.brandon3055.brandonscore.lib.entityfilter.FilterType;
import com.brandon3055.brandonscore.utils.MathUtils;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.client.render.particle.ParticleStarSpark;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 28/09/2016.
 */
public class TileEntityDetector extends TileBCore implements IInteractTile, IRedstoneEmitter {

    private final boolean advanced;
    public float hRot = 0;
    public float yRot = (float) Math.PI / 2;
    public float lthRot = 0;
    public float ltyRot = 0;

    //    public final ManagedBool ADVANCED = new ManagedBool(true, true, false, true);
    public final ManagedShort pulseRate = register(new ManagedShort("pulse_rate", (short) 30, DataFlags.SAVE_BOTH_SYNC_TILE));
    public final ManagedShort range = register(new ManagedShort("range", (short) 10, DataFlags.SAVE_BOTH_SYNC_TILE));
    public final ManagedByte rsMinDetection = register(new ManagedByte("rs_min_detection", (byte) 1, DataFlags.SAVE_BOTH_SYNC_TILE));
    public final ManagedByte rsMaxDetection = register(new ManagedByte("rs_max_detection", (byte) 1, DataFlags.SAVE_BOTH_SYNC_TILE));
    public final ManagedBool pulseRsMode = register(new ManagedBool("pulse_rs_mode", DataFlags.SAVE_BOTH_SYNC_TILE, DataFlags.TRIGGER_UPDATE));
    public final ManagedByte outputStrength = register(new ManagedByte("output_strength", DataFlags.SAVE_NBT));
    private int pulseTimer = -1;
    private int pulseDuration = 0;

    public OPStorage opStorage = new OPStorage(512000, 32000, 0);
    public EntityFilter entityFilter;

//    public TileEntityFilter entityFilter = new TileEntityFilter(this, (byte) 32) {
//        @Override
//        public boolean isListEnabled() {
//            return isAdvanced();
//        }
//
//        @Override
//        public boolean isOtherSelectorEnabled() {
//            return isAdvanced();
//        }
//
//        @Override
//        public boolean isTypeSelectionEnabled() {
//            return true;
//        }
//    };
    public List<String> playerNames = new ArrayList<>();//TODO Need this?


    public TileEntityDetector(BlockPos pos, BlockState state) {
        super(DEContent.tile_entity_detector, pos, state);
        this.advanced = false;
    }

    public TileEntityDetector(boolean advanced, BlockPos pos, BlockState state) {
        super(DEContent.tile_entity_detector, pos, state);
        this.advanced = advanced;
        capManager.setManaged("energy", CapabilityOP.OP, opStorage).saveBoth().syncContainer();

        entityFilter = new EntityFilter(true, FilterType.values());
        entityFilter.setDirtyHandler(this::setChanged);
        entityFilter.setupServerPacketHandling(() -> createClientBoundPacket(0), packet -> sendPacketToClients(getAccessingPlayers(), packet));
        entityFilter.setupClientPacketHandling(() -> createServerBoundPacket(0), packetCustom -> BrandonsCore.proxy.sendToServer(packetCustom));
        setClientSidePacketHandler(0, input -> entityFilter.receivePacketFromServer(input));
        setServerSidePacketHandler(0, (input, player) -> entityFilter.receivePacketFromClient(input));
        setSavedDataObject("entity_filter", entityFilter);
        setItemSavedDataObject("entity_filter", entityFilter);
    }



    @Override
    public void tick() {
        super.tick();

        if (level.isClientSide) {
            updateAnimation();
            return;
        }

        if (pulseTimer == -1) {
            pulseTimer = pulseRate.get();
        }
        else if (pulseTimer > 0) {
            pulseTimer--;
        }
        else if (pulseTimer <= 0) {
            if (opStorage.getEnergyStored() >= getPulseCost()) {
                pulseTimer = pulseRate.get();
                doScanPulse();
            }
            else {
                pulseTimer = 10;
            }
        }

        if (outputStrength.get() > 0 && pulseRsMode.get() && pulseDuration <= 0) {
            outputStrength.zero();
            level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
        }
        else {
            pulseDuration--;
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void updateAnimation() {
        //region Targeting

        List<Entity> entities = entityFilter.filterEntities(level.getEntitiesOfClass(Entity.class, new AABB(worldPosition, worldPosition.offset(1, 1, 1)).inflate(range.get(), range.get(), range.get())));
        Entity closest = null;
        double closestDist = -1;

        Vec3 posVec = new Vec3(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
        for (Entity entity : entities) {
            if (closest == null) {
                closest = entity;
                closestDist = entity.distanceToSqr(posVec);
            }
            else if (entity.distanceToSqr(posVec) < closestDist) {
                closest = entity;
                closestDist = entity.distanceToSqr(posVec);
            }
        }

        lthRot = hRot;
        ltyRot = yRot;

        if (closest != null) {

            double xDist = closest.getX() - (double) ((float) getBlockPos().getX() + 0.5F);
            double zDist = closest.getZ() - (double) ((float) getBlockPos().getZ() + 0.5F);
            double yDist = (closest.getY() + closest.getEyeHeight()) - (double) ((float) worldPosition.getY() + 0.5F);
            double dist = Utils.getDistanceAtoB(Vec3D.getCenter(worldPosition), new Vec3D(closest));


            float thRot = (float) Mth.atan2(zDist, xDist);
            float tyRot = (float) Mth.atan2(dist, yDist);

            hRot = thRot;

            if (hRot < 0 && lthRot > 0.5) {
                hRot += Math.PI * 2;
            }
            yRot = tyRot;

            if (hRot - lthRot > 0.5) {
                hRot = lthRot + 0.5F;
            }
            else if (hRot - lthRot < -0.5) {
                hRot = lthRot - 0.5F;
            }
            if (yRot - ltyRot > 0.1) {
                yRot = ltyRot + 0.1F;
            }
            else if (yRot - ltyRot < -0.1) {
                yRot = ltyRot - 0.1F;
            }
        }
        else {
            hRot += 0.02;
            hRot = hRot % (float) (Math.PI * 2);
            if (hRot < 0 && lthRot > 0.5) {
                hRot += Math.PI * 2;
            }

            if (yRot % Math.PI > Math.PI / 2) {
                yRot -= 0.02;
            }
            if (yRot % Math.PI < Math.PI / 2) {
                yRot += 0.02;
            }
        }

        //endregion

        //region Effects


        ParticleStarSpark spark = new ParticleStarSpark((ClientLevel)level, Vec3D.getCenter(worldPosition).add((-0.5 + level.random.nextDouble()) * 0.1, 0.005, (-0.5 + level.random.nextDouble()) * 0.1));
        spark.setSizeAndRandMotion(0.4F * (level.random.nextFloat() + 0.1), 0.02D, 0, 0.02D);
        spark.setMaxAge(30, 10);
        spark.setGravity(0.0002D);
        spark.setAirResistance(0.02F);
        spark.setColour(0, 1, 1);
        //TODO particles
//        BCEffectHandler.spawnFXDirect(DEParticles.DE_SHEET, spark);

        int i = level.random.nextInt(4);
        double x = i / 2;
        double z = i % 2;

        spark = new ParticleStarSpark((ClientLevel)level, new Vec3D(worldPosition).add(0.14 + (x * 0.72), 0.17, 0.14 + (z * 0.72)));
        spark.setSizeAndRandMotion(0.3F * (level.random.nextFloat() + 0.2), 0.002D, 0, 0.002D);
        spark.setGravity(0.0002D);
        spark.sparkSize = 0.15F;
        if (isAdvanced()) {
            spark.setColour(1, 0.7f, 0);
        }
        else {
            spark.setColour(0.3f, 0.0f, 1F);
        }

//        BCEffectHandler.spawnFXDirect(DEParticles.DE_SHEET, spark);


        //endregion
    }

    public void doScanPulse() {
        List<Entity> entities = entityFilter.filterEntities(level.getEntitiesOfClass(Entity.class, new AABB(worldPosition, worldPosition.offset(1, 1, 1)).inflate(range.get(), range.get(), range.get())));

        double min = rsMinDetection.get() - 1;
        double max = rsMaxDetection.get();
        int eCount = entities.size();
        int output;

        if (min == max) {
            output = eCount > min ? 15 : 0;
        }
        else if (max - min == 15) {
            output = (int) Math.max(0, Math.min(15, eCount - min));
        }
        else {
            output = (int) Math.max(0, Math.min(15, MathUtils.map(eCount, min, max, 0, 15)));
        }

        if (outputStrength.get() != output) {
            outputStrength.set((byte) output);
            level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
        }

        if (pulseRsMode.get()) {
            pulseDuration = 2;
        }

        opStorage.modifyEnergyStored(-getPulseCost());
    }

    //region GuiInteraction

    public void adjustPulseRate(boolean decrement, boolean shift) {
        sendPacketToServer(output -> output.writeBoolean(decrement), shift ? (byte) 1 : (byte) 0);
    }

    public void adjustRange(boolean decrement, boolean shift) {
        sendPacketToServer(output -> output.writeBoolean(decrement), shift ? (byte) 3 : (byte) 2);
    }

    public void adjustRSMin(boolean decrement, boolean shift) {
        sendPacketToServer(output -> output.writeBoolean(decrement), shift ? (byte) 5 : (byte) 4);
    }

    public void adjustRSMax(boolean decrement, boolean shift) {
        sendPacketToServer(output -> output.writeBoolean(decrement), shift ? (byte) 7 : (byte) 6);
    }

    public void togglePulsemode() {
        sendPacketToServer(output -> output.writeBoolean(false), 8);
    }

    @Override
    public void receivePacketFromClient(MCDataInput data, ServerPlayer client, int id) {
        if (id <= 8) {
            boolean decrement = data.readBoolean();
            boolean shift = id % 2 == 1;
            switch (id) {
                case 0:
                case 1:
                    int min = isAdvanced() ? 5 : 30;
                    int max = 1200;
                    int change = shift ? 100 : 5;
                    pulseRate.add(decrement ? (short) -change : (short) change);
                    if (pulseRate.get() < min) {
                        pulseRate.set((short) min);
                    }
                    else if (pulseRate.get() > max) {
                        pulseRate.set((short) max);
                    }
                    pulseTimer = pulseRate.get();
                    break;
                case 2:
                case 3:
                    min = 1;
                    max = isAdvanced() ? 64 : 16;
                    change = shift ? 5 : 1;
                    range.add(decrement ? (short) -change : (short) change);
                    if (range.get() < min) {
                        range.set((short) min);
                    }
                    else if (range.get() > max) {
                        range.set((short) max);
                    }
                    break;
                case 4:
                case 5:
                    change = shift ? 5 : 1;
                    int value = rsMinDetection.add(decrement ? (byte) -change : (byte) change);
                    max = rsMaxDetection.get();
                    if (value < 0) {
                        value = 0;
                    }
                    else if (value > max) {
                        value = max;
                    }
                    rsMinDetection.set((byte) value);
                    break;
                case 6:
                case 7:
                    change = shift ? 5 : 1;
                    value = rsMaxDetection.get() + (decrement ? -change : change);
                    min = rsMinDetection.get();
                    if (value < min) {
                        value = min;
                    }
                    else if (value > 127) {
                        value = 127;
                    }
                    rsMaxDetection.set((byte) value);
                    break;
                case 8:
                    pulseRsMode.set(!pulseRsMode.get());
                    break;
            }
        }

//        if (id == entityFilter.packetID) {
//            entityFilter.receiveConfigFromClient(data.readCompoundNBT());
//        }
    }

    //endregion

    //region Interfaces

    @Override
    public boolean onBlockActivated(BlockState state, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!level.isClientSide) {
//            FMLNetworkHandler.openGui(player, DraconicEvolution.instance, GuiHandler.GUIID_ENTITY_DETECTOR, world, pos.getX(), pos.getY(), pos.getZ());

            MinecraftServer server = player.getServer();
            if (server != null) {
                ListTag list = new ListTag();
                for (String name : server.getPlayerList().getPlayerNamesArray()) {
                    list.add(StringTag.valueOf(name));
                }
                CompoundTag compound = new CompoundTag();
                compound.put("List", list);
                sendPacketToClient((ServerPlayer) player, output -> output.writeCompoundNBT(compound), 16);
            }
        }
        return true;
    }

    @Override
    public int getWeakPower(BlockState blockState, Direction side) {
        return outputStrength.get();
    }

    @Override
    public int getStrongPower(BlockState blockState, Direction side) {
        return outputStrength.get();
    }

    //endregion

    //region Misc

    @Override
    public void receivePacketFromServer(MCDataInput data, int id) {
        if (id == 16) {
            ListTag list = data.readCompoundNBT().getList("List", 8);
            playerNames.clear();
            for (int i = 0; i < list.size(); i++) {
                playerNames.add(list.getString(i));
            }
        }
    }

    public int getPulseCost() {
        return (int) (125 * Math.pow(range.get(), 1.5));
    }

    public boolean isAdvanced() {
        return advanced;
    }

    //endregion

    private AABB AABB = new AABB(0, 0, 0, 1, 1, 1);

    @OnlyIn(Dist.CLIENT)
    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition, worldPosition.offset(1, 1, 1));
    }
}
