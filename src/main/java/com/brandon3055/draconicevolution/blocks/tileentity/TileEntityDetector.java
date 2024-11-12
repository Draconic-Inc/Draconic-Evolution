package com.brandon3055.draconicevolution.blocks.tileentity;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.math.MathHelper;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.api.power.OPStorage;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.brandonscore.client.particle.IntParticleType.IntParticleData;
import com.brandon3055.brandonscore.lib.IInteractTile;
import com.brandon3055.brandonscore.lib.IRedstoneEmitter;
import com.brandon3055.brandonscore.lib.datamanager.DataFlags;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedByte;
import com.brandon3055.brandonscore.lib.datamanager.ManagedShort;
import com.brandon3055.brandonscore.lib.entityfilter.EntityFilter;
import com.brandon3055.brandonscore.lib.entityfilter.FilterType;
import com.brandon3055.brandonscore.utils.MathUtils;
import com.brandon3055.draconicevolution.api.modules.lib.ModularOPStorage;
import com.brandon3055.draconicevolution.client.DEParticles;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.inventory.EntityDetectorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 28/09/2016.
 */
public class TileEntityDetector extends TileBCore implements MenuProvider, IInteractTile, IRedstoneEmitter {

    public float lookYaw = 0;
    public float lookPitch = (float) Math.PI / 2;
    public float lastLookYaw = 0;
    public float lastLookPitch = 0;

    //    public final ManagedBool ADVANCED = new ManagedBool(true, true, false, true);
    public final ManagedShort pulseRate = register(new ManagedShort("pulse_rate", (short) 30, DataFlags.SAVE_BOTH_SYNC_TILE));
    public final ManagedShort range = register(new ManagedShort("range", (short) 10, DataFlags.SAVE_BOTH_SYNC_TILE));
    public final ManagedByte rsMinDetection = register(new ManagedByte("rs_min_detection", (byte) 1, DataFlags.SAVE_BOTH_SYNC_TILE));
    public final ManagedByte rsMaxDetection = register(new ManagedByte("rs_max_detection", (byte) 1, DataFlags.SAVE_BOTH_SYNC_TILE));
    public final ManagedBool pulseRsMode = register(new ManagedBool("pulse_rs_mode", DataFlags.SAVE_BOTH_SYNC_TILE));
    public final ManagedByte outputStrength = register(new ManagedByte("output_strength", DataFlags.SAVE_NBT));
    private int pulseTimer = -1;
    private int pulseDuration = 0;

    public OPStorage opStorage = new ModularOPStorage(this, 512000, 32000, 0);
    public EntityFilter entityFilter;

    public List<String> playerNames = new ArrayList<>(); //TODO Need this?

    public TileEntityDetector(BlockPos pos, BlockState state) {
        super(DEContent.TILE_ENTITY_DETECTOR.get(), pos, state);
        capManager.setManaged("energy", CapabilityOP.BLOCK, opStorage).saveBoth().syncContainer();
        if (isAdvanced()) {
            entityFilter = new EntityFilter(false, FilterType.values());
        } else {
            entityFilter = new EntityFilter(true, FilterType.PLAYER, FilterType.HOSTILE);
        }
        entityFilter.setDirtyHandler(this::setChanged);
        entityFilter.setupServerPacketHandling(() -> createClientBoundPacket(9), packet -> sendPacketToClients(getAccessingPlayers(), packet));
        entityFilter.setupClientPacketHandling(() -> createServerBoundPacket(9));
        setClientSidePacketHandler(9, input -> entityFilter.receivePacketFromServer(input));
        setServerSidePacketHandler(9, (input, player) -> entityFilter.receivePacketFromClient(input));
        setSavedDataObject("entity_filter", entityFilter);
        setItemSavedDataObject("entity_filter", entityFilter);
    }

    public static void register(RegisterCapabilitiesEvent event) {
        capability(event, DEContent.TILE_ENTITY_DETECTOR, CapabilityOP.BLOCK);
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
        } else if (pulseTimer > 0) {
            pulseTimer--;
        } else if (pulseTimer <= 0) {
            if (opStorage.getEnergyStored() >= getPulseCost()) {
                pulseTimer = pulseRate.get();
                doScanPulse();
            } else {
                pulseTimer = 10;
            }
        }

        if (outputStrength.get() > 0 && pulseRsMode.get() && pulseDuration <= 0) {
            outputStrength.zero();
            level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
        } else {
            pulseDuration--;
        }
    }

    @Override
    public void onPlayerOpenContainer(Player player) {
        super.onPlayerOpenContainer(player);
        if (player instanceof ServerPlayer) {
            entityFilter.syncClient((ServerPlayer) player);
        }
    }

    @OnlyIn (Dist.CLIENT)
    private void updateAnimation() {
        //region Targeting

        List<Entity> entities = entityFilter.filterEntities(level.getEntitiesOfClass(Entity.class, new AABB(worldPosition).inflate(range.get(), range.get(), range.get())));
        Entity closest = null;
        double closestDist = -1;

        Vec3 posVec = new Vec3(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
        for (Entity entity : entities) {
            if (closest == null) {
                closest = entity;
                closestDist = entity.distanceToSqr(posVec);
            } else if (entity.distanceToSqr(posVec) < closestDist) {
                closest = entity;
                closestDist = entity.distanceToSqr(posVec);
            }
        }

        lastLookYaw = lookYaw;
        lastLookPitch = lookPitch;

        if (closest != null) {
            Vector3 closePos = new Vector3(closest.getEyePosition());
            Vector3 relative = closePos.copy().subtract(Vector3.fromBlockPosCenter(getBlockPos()));
            double dist = closePos.distance(Vector3.fromBlockPosCenter(getBlockPos()));
            float targetYaw = (float) (Mth.atan2(relative.x, relative.z) * MathHelper.todeg) + 180;
            float deviation = targetYaw - lookYaw;

            if (deviation < -180) {
                lookYaw -= 360;
                lastLookYaw -= 360;
            } else if (deviation > 180) {
                lookYaw += 360;
                lastLookYaw += 360;
            }

            lookYaw += (targetYaw - lookYaw) * 0.2;

            float pitchAngle = (float) (Mth.atan2(relative.y, dist) * MathHelper.todeg);
            lookPitch += (pitchAngle - lookPitch) * 0.2;
        } else {
            lookYaw += 1.15;
            if (lookYaw >= 360) {
                lookYaw -= 360;
                lastLookYaw -= 360;
            }

            if (lookPitch % 360 > 0) {
                lookPitch -= 1.15;
            } else if (lookPitch % 360 < 0) {
                lookPitch += 1.15;
            }
        }

        //endregion

        IntParticleData data = new IntParticleData(DEParticles.SPARK.get(),
                0, 255, 255, //Colour
                (int) (0.4F * (level.random.nextFloat() + 0.1) * 100), //Scale
                (int) (0.15F*100), //Spark scale
                30, 10, //Max Age, Additional random age
                (int) (-0.005*1000) //Gravity
        );
        Vector3 pos = Vector3.fromTileCenter(this).add((-0.5 + level.random.nextDouble()) * 0.1, 0.005, (-0.5 + level.random.nextDouble()) * 0.1);
        level.addParticle(data, pos.x, pos.y, pos.z, 0.02D, 0, 0.02D);

        int i = level.random.nextInt(4);
        double x = i / 2;
        double z = i % 2;

        boolean advanced = isAdvanced();
        data = new IntParticleData(DEParticles.SPARK.get(),
                advanced ? 255 : 76, //R
                advanced ? 178 : 0,  //G
                advanced ? 0 : 255,  //B
                (int) (0.4F * (level.random.nextFloat() + 0.1) * 100), //Scale
                (int) (0.15F*100), //Spark scale
                30, 10, //Max Age, Additional random age
                (int) (-0.005*1000) //Gravity
        );
        pos = Vector3.fromTile(this).add(0.14 + (x * 0.72), 0.17, 0.14 + (z * 0.72));
        level.addParticle(data, pos.x, pos.y, pos.z, 0.002D, 0, 0.002D);

        //endregion
    }

    public void doScanPulse() {
        List<Entity> entities = entityFilter.filterEntities(level.getEntitiesOfClass(Entity.class, new AABB(worldPosition).inflate(range.get(), range.get(), range.get())));

        double min = rsMinDetection.get() - 1;
        double max = rsMaxDetection.get();
        int eCount = entities.size();
        int output;

        if (min == max) {
            output = eCount > min ? 15 : 0;
        } else if (max - min == 15) {
            output = (int) Math.max(0, Math.min(15, eCount - min));
        } else {
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
        super.receivePacketFromClient(data, client, id);
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
                    } else if (pulseRate.get() > max) {
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
                    } else if (range.get() > max) {
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
                    } else if (value > max) {
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
                    } else if (value > 127) {
                        value = 127;
                    }
                    rsMaxDetection.set((byte) value);
                    break;
                case 8:
                    pulseRsMode.set(!pulseRsMode.get());
                    break;
            }
        }
    }

    //endregion

    //region Interfaces

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int currentWindowIndex, Inventory playerInventory, Player player) {
        return new EntityDetectorMenu(currentWindowIndex, playerInventory, this);
    }

    @Override
    public InteractionResult onBlockUse(BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        if (player instanceof ServerPlayer) {
            player.openMenu(this, worldPosition);
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
        return InteractionResult.SUCCESS;
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

    @Override
    public void receivePacketFromServer(MCDataInput data, int id) {
        super.receivePacketFromServer(data, id);
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
        return getBlockState().is(DEContent.ENTITY_DETECTOR_ADVANCED.get());
    }
}
