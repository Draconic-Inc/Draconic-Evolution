package com.brandon3055.draconicevolution.blocks.tileentity;

import cofh.api.energy.IEnergyReceiver;
import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.blocks.TileEnergyBase;
import com.brandon3055.brandonscore.client.particle.BCEffectHandler;
import com.brandon3055.brandonscore.lib.IActivatableTile;
import com.brandon3055.brandonscore.lib.IRedstoneEmitter;
import com.brandon3055.brandonscore.lib.TileEntityFilter;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.network.PacketTileMessage;
import com.brandon3055.brandonscore.network.wrappers.SyncableBool;
import com.brandon3055.brandonscore.network.wrappers.SyncableByte;
import com.brandon3055.brandonscore.network.wrappers.SyncableObject;
import com.brandon3055.brandonscore.network.wrappers.SyncableShort;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.GuiHandler;
import com.brandon3055.draconicevolution.blocks.machines.EntityDetector;
import com.brandon3055.draconicevolution.client.DEParticles;
import com.brandon3055.draconicevolution.client.render.particle.ParticleStarSpark;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 28/09/2016.
 */
public class TileEntityDetector extends TileEnergyBase implements IActivatableTile, IRedstoneEmitter, ITickable, IEnergyReceiver {

    public float hRot = 0;
    public float yRot = (float) Math.PI / 2;
    public float lthRot = 0;
    public float ltyRot = 0;

    //    public final SyncableBool ADVANCED = new SyncableBool(true, true, false, true);
    public final SyncableShort PULSE_RATE = new SyncableShort((short) 30, true, false);
    public final SyncableShort RANGE = new SyncableShort((short) 10, true, false);
    public final SyncableByte RS_MIN_DETECTION = new SyncableByte((byte) 1, true, false);
    public final SyncableByte RS_MAX_DETECTION = new SyncableByte((byte) 1, true, false);
    public final SyncableBool PULSE_RS_MODE = new SyncableBool(false, true, false, true);
    public final SyncableByte OUTPUT_STRENGTH = new SyncableByte((byte) 0, false, false);
    private int pulseTimer = -1;
    private int pulseDuration = 0;

    public TileEntityFilter entityFilter = new TileEntityFilter(this, (byte) 32) {
        @Override
        public boolean isListEnabled() {
            return isAdvanced();
        }

        @Override
        public boolean isOtherSelectorEnabled() {
            return isAdvanced();
        }

        @Override
        public boolean isTypeSelectionEnabled() {
            return true;
        }
    };
    public List<String> playerNames = new ArrayList<>();

    public TileEntityDetector() {   //The registrations that are set to false are being saved to NBT via IDataRetainerTile so they are saved to the item when the block is broken.
        registerSyncableObject(OUTPUT_STRENGTH, true);
//        registerSyncableObject(ADVANCED, false);
        registerSyncableObject(PULSE_RATE, false);
        registerSyncableObject(RANGE, false);
        registerSyncableObject(RS_MIN_DETECTION, false);
        registerSyncableObject(RS_MAX_DETECTION, false);
        registerSyncableObject(energyStored, false);
        registerSyncableObject(PULSE_RS_MODE, false);
        setCapacityAndTransfer(512000, 32000, 0);
    }


    @Override
    public void update() {
        detectAndSendChanges();

        if (worldObj.isRemote) {
            updateAnimation();
            return;
        }

        if (pulseTimer == -1) {
            pulseTimer = PULSE_RATE.value;
        }
        else if (pulseTimer > 0) {
            pulseTimer--;
        }
        else if (pulseTimer <= 0) {
            if (energyStorage.getEnergyStored() >= getPulseCost()) {
                pulseTimer = PULSE_RATE.value;
                doScanPulse();
            }
            else {
                pulseTimer = 10;
            }
        }

        if (OUTPUT_STRENGTH.value > 0 && PULSE_RS_MODE.value && pulseDuration <= 0) {
            OUTPUT_STRENGTH.value = 0;
            worldObj.notifyNeighborsOfStateChange(pos, getBlockType());
        }
        else {
            pulseDuration--;
        }
    }

    @SideOnly(Side.CLIENT)
    private void updateAnimation() {
        //region Targeting

        List<Entity> entities = entityFilter.filterEntities(worldObj.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos, pos.add(1, 1, 1)).expand(RANGE.value, RANGE.value, RANGE.value)));
        Entity closest = null;
        double closestDist = -1;

        for (Entity entity : entities) {
            if (closest == null) {
                closest = entity;
                closestDist = entity.getDistanceSqToCenter(pos);
            }
            else if (entity.getDistanceSqToCenter(pos) < closestDist) {
                closest = entity;
                closestDist = entity.getDistanceSqToCenter(pos);
            }
        }

        lthRot = hRot;
        ltyRot = yRot;

        if (closest != null) {

            double xDist = closest.posX - (double) ((float) getPos().getX() + 0.5F);
            double zDist = closest.posZ - (double) ((float) getPos().getZ() + 0.5F);
            double yDist = (closest.posY + closest.getEyeHeight()) - (double) ((float) pos.getY() + 0.5F);
            double dist = Utils.getDistanceAtoB(Vec3D.getCenter(pos), new Vec3D(closest));


            float thRot = (float) MathHelper.atan2(zDist, xDist);
            float tyRot = (float) MathHelper.atan2(dist, yDist);

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


        ParticleStarSpark spark = new ParticleStarSpark(worldObj, Vec3D.getCenter(pos).add((-0.5 + worldObj.rand.nextDouble()) * 0.1, 0.005, (-0.5 + worldObj.rand.nextDouble()) * 0.1));
        spark.setSizeAndRandMotion(0.4F * (worldObj.rand.nextFloat() + 0.1), 0.02D, 0, 0.02D);
        spark.setMaxAge(30, 10);
        spark.setGravity(0.0002D);
        spark.setAirResistance(0.02F);
        spark.setColour(0, 1, 1);
        BCEffectHandler.spawnFXDirect(DEParticles.DE_SHEET, spark);

        int i = worldObj.rand.nextInt(4);
        double x = i / 2;
        double z = i % 2;

        spark = new ParticleStarSpark(worldObj, new Vec3D(pos).add(0.14 + (x * 0.72), 0.17, 0.14 + (z * 0.72)));
        spark.setSizeAndRandMotion(0.3F * (worldObj.rand.nextFloat() + 0.2), 0.002D, 0, 0.002D);
        spark.setGravity(0.0002D);
        spark.sparkSize = 0.15F;
        if (isAdvanced()) {
            spark.setColour(1, 0.7f, 0);
        }
        else {
            spark.setColour(0.3f, 0.0f, 1F);
        }

        BCEffectHandler.spawnFXDirect(DEParticles.DE_SHEET, spark);


        //endregion
    }

    public void doScanPulse() {
        List<Entity> entities = entityFilter.filterEntities(worldObj.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos, pos.add(1, 1, 1)).expand(RANGE.value, RANGE.value, RANGE.value)));

        double min = RS_MIN_DETECTION.value - 1;
        double max = RS_MAX_DETECTION.value;
        int eCount = entities.size();
        int output;

        if (min == max) {
            output = eCount > min ? 15 : 0;
        }
        else if (max - min == 15) {
            output = (int) Math.max(0, Math.min(15, eCount - min));
        }
        else {
            output = (int) Math.max(0, Math.min(15, Utils.map(eCount, min, max, 0, 15)));
        }

        if (OUTPUT_STRENGTH.value != output) {
            OUTPUT_STRENGTH.value = (byte) output;
            worldObj.notifyNeighborsOfStateChange(pos, getBlockType());
        }

        if (PULSE_RS_MODE.value) {
            pulseDuration = 2;
        }

        energyStorage.modifyEnergyStored(-getPulseCost());
    }

    //region GuiInteraction

    public void adjustPulseRate(boolean decrement, boolean shift) {
        sendPacketToServer(new PacketTileMessage(this, shift ? (byte) 1 : (byte) 0, decrement, false));
    }

    public void adjustRange(boolean decrement, boolean shift) {
        sendPacketToServer(new PacketTileMessage(this, shift ? (byte) 3 : (byte) 2, decrement, false));
    }

    public void adjustRSMin(boolean decrement, boolean shift) {
        sendPacketToServer(new PacketTileMessage(this, shift ? (byte) 5 : (byte) 4, decrement, false));
    }

    public void adjustRSMax(boolean decrement, boolean shift) {
        sendPacketToServer(new PacketTileMessage(this, shift ? (byte) 7 : (byte) 6, decrement, false));
    }

    public void togglePulsemode() {
        sendPacketToServer(new PacketTileMessage(this, (byte) 8, false, false));
    }

    @Override
    public void receivePacketFromClient(PacketTileMessage packet, EntityPlayerMP client) {
        if (packet.getIndex() <= 8) {
            boolean decrement = packet.booleanValue;
            boolean shift = packet.getIndex() % 2 == 1;
            switch (packet.getIndex()) {
                case 0:
                case 1:
                    int min = isAdvanced() ? 5 : 30;
                    int max = 1200;
                    int change = shift ? 100 : 5;
                    PULSE_RATE.value += decrement ? -change : change;
                    if (PULSE_RATE.value < min) {
                        PULSE_RATE.value = (short) min;
                    }
                    else if (PULSE_RATE.value > max) {
                        PULSE_RATE.value = (short) max;
                    }
                    pulseTimer = PULSE_RATE.value;
                    break;
                case 2:
                case 3:
                    min = 1;
                    max = isAdvanced() ? 64 : 16;
                    change = shift ? 5 : 1;
                    RANGE.value += decrement ? -change : change;
                    if (RANGE.value < min) {
                        RANGE.value = (short) min;
                    }
                    else if (RANGE.value > max) {
                        RANGE.value = (short) max;
                    }
                    break;
                case 4:
                case 5:
                    change = shift ? 5 : 1;
                    int value = RS_MIN_DETECTION.value + (decrement ? -change : change);
                    max = RS_MAX_DETECTION.value;
                    if (value < 0) {
                        value = 0;
                    }
                    else if (value > max) {
                        value = max;
                    }
                    RS_MIN_DETECTION.value = (byte) value;
                    break;
                case 6:
                case 7:
                    change = shift ? 5 : 1;
                    value = RS_MAX_DETECTION.value + (decrement ? -change : change);
                    min = RS_MIN_DETECTION.value;
                    if (value < min) {
                        value = min;
                    }
                    else if (value > 127) {
                        value = 127;
                    }
                    RS_MAX_DETECTION.value = (byte) value;
                    break;
                case 8:
                    PULSE_RS_MODE.value = !PULSE_RS_MODE.value;
                    break;
            }
        }

        if (packet.getIndex() == entityFilter.packetID && packet.isNBT()) {
            entityFilter.receiveConfigFromClient(packet.compound);
        }
    }

    //endregion

    //region Interfaces

    @Override
    public boolean onBlockActivated(IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!worldObj.isRemote) {
            FMLNetworkHandler.openGui(player, DraconicEvolution.instance, GuiHandler.GUIID_ENTITY_DETECTOR, worldObj, pos.getX(), pos.getY(), pos.getZ());

            MinecraftServer server = BrandonsCore.proxy.getMCServer();
            if (server != null) {
                NBTTagList list = new NBTTagList();
                for (String name : server.getPlayerList().getAllUsernames()) {
                    list.appendTag(new NBTTagString(name));
                }
                NBTTagCompound compound = new NBTTagCompound();
                compound.setTag("List", list);
                sendPacketToClients(new PacketTileMessage(this, (byte) 16, compound, false), new NetworkRegistry.TargetPoint(worldObj.provider.getDimension(), player.posX, player.posY, player.posZ, 8));
            }

        }
        return true;
    }

    @Override
    public int getWeakPower(IBlockState blockState, EnumFacing side) {
        return OUTPUT_STRENGTH.value;
    }

    @Override
    public int getStrongPower(IBlockState blockState, EnumFacing side) {
        return OUTPUT_STRENGTH.value;
    }

    //endregion

    //region Misc

    @Override
    public void writeRetainedData(NBTTagCompound dataCompound) {
        entityFilter.writeToNBT(dataCompound);
        super.writeRetainedData(dataCompound);

        for (SyncableObject syncableObject : syncableObjectMap.values()) {
            if (syncableObject != energyStored && syncableObject != OUTPUT_STRENGTH) { //These dont need to be saved
                syncableObject.toNBT(dataCompound);
            }
        }
    }

    @Override
    public void readRetainedData(NBTTagCompound dataCompound) {
        entityFilter.readFromNBT(dataCompound);
        super.readRetainedData(dataCompound);

        for (SyncableObject syncableObject : syncableObjectMap.values()) {
            if (syncableObject != energyStored && syncableObject != OUTPUT_STRENGTH) { //These dont need to be saved
                syncableObject.fromNBT(dataCompound);
            }
        }
    }

    @Override
    public void receivePacketFromServer(PacketTileMessage packet) {
        if (packet.getIndex() == 16 && packet.isNBT()) {
            NBTTagList list = packet.compound.getTagList("List", 8);
            playerNames.clear();
            for (int i = 0; i < list.tagCount(); i++) {
                playerNames.add(list.getStringTagAt(i));
            }
        }
    }


    public int getPulseCost() {
        return (int) (125 * Math.pow(RANGE.value, 1.5));
    }

    @Override
    public boolean hasFastRenderer() {
        return false;
    }

    private boolean hasCheckedAdvanced = false;
    private boolean advanced = false;

    public boolean isAdvanced() {
        if (!hasCheckedAdvanced && worldObj != null) {
            IBlockState state = worldObj.getBlockState(pos);
            advanced = state.getValue(EntityDetector.ADVANCED);
            hasCheckedAdvanced = true;
        }

        return advanced;
    }

    //endregion

    private AxisAlignedBB AABB = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos, pos.add(1, 1, 1));
    }
}
