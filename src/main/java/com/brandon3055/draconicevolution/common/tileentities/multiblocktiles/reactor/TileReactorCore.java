package com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.common.handlers.ProcessHandler;
import com.brandon3055.brandonscore.common.utills.Utills;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.ReactorSound;
import com.brandon3055.draconicevolution.client.gui.GuiHandler;
import com.brandon3055.draconicevolution.client.render.particle.Particles;
import com.brandon3055.draconicevolution.common.blocks.multiblock.IReactorPart;
import com.brandon3055.draconicevolution.common.blocks.multiblock.MultiblockHelper.TileLocation;
import com.brandon3055.draconicevolution.common.handler.ConfigHandler;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.tileentities.TileObjectSync;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Created by Brandon on 16/6/2015.
 */
public class TileReactorCore extends TileObjectSync {

    public static final int MAX_SLAVE_RANGE = 10;
    public static final int STATE_OFFLINE = 0;
    public static final int STATE_START = 1;
    public static final int STATE_ONLINE = 2;
    public static final int STATE_STOP = 3;
    public static final int STATE_INVALID = 4;

    public int reactorState = 0;
    public float renderRotation = 0;
    public float renderSpeed = 0;
    public boolean isStructureValid = false;
    public float stabilizerRender = 0F;
    private boolean startupInitialized = false;
    public int tick = 0;

    // Key operational figures
    public int reactorFuel = 0;
    public int convertedFuel = 0; // The amount of fuel that has converted
    public double conversionUnit = 0; // used to smooth out the conversion between int and floating point. When >= 1
                                      // minus one and convert one
    // int worth of fuel

    public double reactionTemperature = 20;
    public double maxReactTemperature = 10000;

    public double fieldCharge = 0;
    public double maxFieldCharge = 0;

    public int energySaturation = 0;
    public int maxEnergySaturation = 0;

    @SideOnly(Side.CLIENT)
    private ReactorSound reactorSound;

    // #######################

    // TODO DONT FORGET TO ACTUALLY FINISH ALL THESE THINGS!!!!
    // Check //-Bounding box
    // Check //-Custom player collision
    // Check //-Finish stabilizer place and break mechanics
    // Check //-Have the GUI tell you if the structure is invalid
    // Check //-Config
    // TODO things for later
    // -GUI info (maby speed up gui sync via the container)
    // -GUI warning red bars
    // -Maby get around to setting the angle of the stabiliser elements
    // Check //-SOUND!!!!!
    // Check //-CC Integration
    // Check //-Redstone
    // -Add reactor and gates to tablet

    public List<TileLocation> stabilizerLocations = new ArrayList<TileLocation>();

    @Override
    public void updateEntity() {
        tick++;
        if (worldObj.isRemote) {
            updateSound();

            renderSpeed = (float) Math.min((reactionTemperature - 20) / 2000D, 1D);
            stabilizerRender = (float) Math.min(fieldCharge / (maxFieldCharge * 0.1D), 1D);
            renderRotation += renderSpeed;
            // LogHelper.info(renderSpeed +" "+(reactionTemperature));
            checkPlayerCollision();
            return;
        }
        // else injectEnergy(10000000);

        switch (reactorState) {
            case STATE_OFFLINE:
                offlineTick();
                break;
            case STATE_START:
                startingTick();
                break;
            case STATE_ONLINE:
                runTick();
                break;
            case STATE_STOP:
                runTick();
                break;
        }

        detectAndSendChanges();
    }

    @SideOnly(Side.CLIENT)
    private void updateSound() {
        if (reactorSound == null)
            reactorSound = (ReactorSound) DraconicEvolution.proxy.playISound(new ReactorSound(this));
    }

    private void checkPlayerCollision() {
        EntityPlayer player = BrandonsCore.proxy.getClientPlayer();
        double distance = Utills
                .getDistanceAtoB(player.posX, player.posY, player.posZ, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
        if (distance < (getCoreDiameter() / 2) + 0.5) {
            double dMod = 1D - (distance / Math.max(0.1, (getCoreDiameter() / 2) + 0.5));
            double offsetX = player.posX - xCoord + 0.5;
            double offsetY = player.posY - yCoord + 0.5;
            double offsetZ = player.posZ - zCoord + 0.5;
            double m = 1D * dMod;
            player.addVelocity(offsetX * m, offsetY * m, offsetZ * m);
        }
    }

    private void offlineTick() {
        if (reactionTemperature > 20) reactionTemperature -= 0.5;
        if (fieldCharge > 0) fieldCharge -= maxFieldCharge * 0.0005;
        else if (fieldCharge < 0) fieldCharge = 0;
        if (energySaturation > 0) energySaturation -= maxEnergySaturation * 0.000001;
        else if (energySaturation < 0) energySaturation = 0;
    }

    private void startingTick() {
        if (!startupInitialized) {
            int totalFuel = reactorFuel + convertedFuel;
            maxFieldCharge = totalFuel * 96.45061728395062 * 100;
            maxEnergySaturation = (int) (totalFuel * 96.45061728395062 * 1000);
            if (energySaturation > maxEnergySaturation) energySaturation = maxEnergySaturation;
            if (fieldCharge > maxFieldCharge) fieldCharge = maxFieldCharge;
            startupInitialized = true;
        }
    }

    private boolean hasExploded = false;

    public double tempDrainFactor;
    public double generationRate;
    public int fieldDrain;
    public double fieldInputRate;
    public double fuelUseRate;

    private void runTick() {
        // Inverted core saturation (if multiplied by 100 this creates infinite numbers which breaks the code)
        double saturation = ((double) energySaturation / (double) maxEnergySaturation);
        double saturationI = (1D - ((double) energySaturation / (double) maxEnergySaturation)) * 99D;
        double temp = ((reactionTemperature) / maxReactTemperature) * 50D;
        // The conversion level. Ranges from -0.3 to 1.0
        double conversion = (((double) convertedFuel + conversionUnit)
                / ((double) (convertedFuel + reactorFuel) - conversionUnit)
                * 1.3) - 0.3D;

        // Temperature Calculation
        double tempOffset = 444.7; // Adjusts where the temp falls to at 100% saturation
        // The exponential temperature rise which increases as the core saturation goes down
        double tempRiseExpo = (saturationI * saturationI * saturationI) / (100 - saturationI) + tempOffset;
        // This is used to add resistance as the temp rises because the hotter something gets the more energy it takes
        // to get it hotter
        double tempRiseResist = (temp * temp * temp * temp) / (100 - temp);
        // This puts all the numbers together and gets the value to raise or lower the temp by this tick. This is
        // dealing with very big numbers so the result is divided by 10000
        double riseAmount = (tempRiseExpo - (tempRiseResist * (1D - conversion)) + conversion * 1000) / 10000;
        if (reactorState == STATE_STOP) {
            if (reactionTemperature <= 2001) {
                reactorState = STATE_OFFLINE;
                startupInitialized = false;
                return;
            }
            if (energySaturation >= maxEnergySaturation * 0.99 && reactorFuel > 0)
                reactionTemperature -= 1D - conversion;
            else reactionTemperature += riseAmount * 10;
        } else reactionTemperature += riseAmount * 10;

        // ======================

        // Energy Calculation
        int baseMaxRFt = (int) ((maxEnergySaturation / 1000D) * ConfigHandler.reactorOutputMultiplier * 1.5D);
        int maxRFt = (int) (baseMaxRFt * (1D + (conversion * 2)));
        generationRate = (1D - saturation) * maxRFt;
        energySaturation += generationRate;

        // LogHelper.info((1D - saturation) * maxRFt);
        // ======================

        // When temp < 1000 power drain is 0, when temp > 2000 power drain is 1, when temp > 8000 power drain increases
        // exponentially
        tempDrainFactor = reactionTemperature > 8000
                ? 1 + ((reactionTemperature - 8000) * (reactionTemperature - 8000) * 0.0000025)
                : reactionTemperature > 2000 ? 1 : reactionTemperature > 1000 ? (reactionTemperature - 1000) / 1000 : 0;
        // todo add to guiInfo
        // -temp drain factor
        // -mass
        // -generation
        // -field drain

        // Field Drain Calculation
        fieldDrain = (int) Math
                .min(tempDrainFactor * (1D - saturation) * (baseMaxRFt / 10.923556), (double) Integer.MAX_VALUE); // <(baseMaxRFt/make
                                                                                                                  // smaller
                                                                                                                  // to
                                                                                                                  // increase
                                                                                                                  // field
                                                                                                                  // power
                                                                                                                  // drain)

        double fieldNegPercent = 1D - (fieldCharge / maxFieldCharge);
        fieldInputRate = fieldDrain / fieldNegPercent;

        // LogHelper.info(fieldDrain+" "+tempDrainFactor+" "+(1D-saturation)+" "+tempDrainFactor * (1D-saturation) *
        // (baseMaxRFt/10.923556));
        fieldCharge -= fieldDrain;
        // ======================

        // Calculate Fuel Usage
        fuelUseRate = tempDrainFactor * (1D - saturation) * (0.001 * ConfigHandler.reactorFuelUsageMultiplier); // <Last
                                                                                                                // number
                                                                                                                // is
                                                                                                                // base
                                                                                                                // fuel
                                                                                                                // usage
                                                                                                                // rate
        conversionUnit += fuelUseRate;
        if (conversionUnit >= 1 && reactorFuel > 0) {
            conversionUnit--;
            reactorFuel--;
            convertedFuel++;
        }

        // ====================

        // Make BOOM!!!
        if ((fieldCharge <= 0) && !hasExploded) {
            hasExploded = true;
            goBoom();
        }
        // ===========
    }

    private void goBoom() {
        if (!ConfigHandler.enableReactorBigBoom) {
            worldObj.createExplosion(null, xCoord, yCoord, zCoord, 5, true);
        } else {
            // float power = 10F + Math.min(10F, ((float) (convertedFuel + reactorFuel) / 10369F) * 10F);
            float power = 2F + (((float) (convertedFuel + reactorFuel) / 10369F) * 18F);
            ProcessHandler.addProcess(new ReactorExplosion(worldObj, xCoord, yCoord, zCoord, power));
            sendObjectToClient(
                    References.INT_ID,
                    100,
                    (int) (power * 10F),
                    new NetworkRegistry.TargetPoint(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 512));
        }
        worldObj.setBlockToAir(xCoord, yCoord, zCoord);
    }

    public void validateStructure() {
        boolean updateRequired = false;
        // Check that all of the stabilizers are still valid
        Iterator<TileLocation> i = stabilizerLocations.iterator();
        List<TileReactorStabilizer> stabilizers = new ArrayList<TileReactorStabilizer>();
        while (i.hasNext()) {
            TileLocation location = i.next();
            if (!(location.getTileEntity(worldObj) instanceof TileReactorStabilizer)
                    || !((TileReactorStabilizer) location.getTileEntity(worldObj)).masterLocation
                            .isThisLocation(xCoord, yCoord, zCoord)) {
                i.remove();
                updateRequired = true;
            } else stabilizers.add((TileReactorStabilizer) location.getTileEntity(worldObj));
        }

        // Check that there are 4 stabilizers in the correct configuration
        isStructureValid = false;
        List<TileReactorStabilizer> checkList = new ArrayList<TileReactorStabilizer>();
        for (TileReactorStabilizer stabilizer : stabilizers) {
            if (checkList.contains(stabilizer)) continue;

            for (TileReactorStabilizer comp : stabilizers) {
                if (!(comp == stabilizer || checkList.contains(comp))
                        && ForgeDirection.getOrientation(comp.facingDirection)
                                == ForgeDirection.getOrientation(stabilizer.facingDirection).getOpposite()) {
                    checkList.add(comp);
                    checkList.add(stabilizer);
                    if (checkList.size() == 4) {
                        isStructureValid = true;
                        if (reactorState == STATE_INVALID) reactorState = STATE_OFFLINE;
                        break;
                    }
                }
            }
        }

        for (TileReactorStabilizer stabilizer : stabilizers) {
            if (Utills.getDistanceAtoB(stabilizer.xCoord, stabilizer.yCoord, stabilizer.zCoord, xCoord, yCoord, zCoord)
                    < (getMaxCoreDiameter() / 2) + 1) {
                isStructureValid = false;
                break;
            }
        }

        if (!isStructureValid) {
            reactorState = STATE_INVALID;
            if (reactionTemperature >= 2000) {
                goBoom();
            }
        }

        if (updateRequired) worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void onPlaced() {
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            boolean flag = false;
            for (int i = 1; i < MAX_SLAVE_RANGE && !flag; i++) {
                TileLocation location = new TileLocation(
                        xCoord + direction.offsetX * i,
                        yCoord + direction.offsetY * i,
                        zCoord + direction.offsetZ * i);
                if (location.getTileEntity(worldObj) != null) {
                    if (location.getTileEntity(worldObj) instanceof IReactorPart
                            && !((IReactorPart) location.getTileEntity(worldObj)).getMaster().initialized)
                        ((IReactorPart) location.getTileEntity(worldObj)).checkForMaster();
                    flag = true;
                }
            }
        }
        validateStructure();
    }

    public void onBroken() {
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            for (int i = 1; i < MAX_SLAVE_RANGE; i++) {
                TileLocation location = new TileLocation(
                        xCoord + direction.offsetX * i,
                        yCoord + direction.offsetY * i,
                        zCoord + direction.offsetZ * i);
                if (location.getTileEntity(worldObj) instanceof IReactorPart
                        && ((IReactorPart) location.getTileEntity(worldObj)).getMaster()
                                .compareTo(new TileLocation(xCoord, yCoord, zCoord)) == 0)
                    ((IReactorPart) location.getTileEntity(worldObj)).shutDown();
            }
        }

        if (reactionTemperature >= 2000) {
            goBoom();
        }
    }

    public boolean onStructureRightClicked(EntityPlayer player) {
        if (!worldObj.isRemote)
            player.openGui(DraconicEvolution.instance, GuiHandler.GUIID_REACTOR, worldObj, xCoord, yCoord, zCoord);
        return true;
    }

    public int injectEnergy(int RF) {
        int received = 0;
        if (reactorState == STATE_START) {
            if (!startupInitialized) return 0;
            if (fieldCharge < (maxFieldCharge / 2)) {
                received = Math.min(RF, (int) (maxFieldCharge / 2) - (int) fieldCharge + 1);
                fieldCharge += received;
                if (fieldCharge > (maxFieldCharge / 2)) fieldCharge = (maxFieldCharge / 2);
            } else if (energySaturation < (maxEnergySaturation / 2)) {
                received = Math.min(RF, (maxEnergySaturation / 2) - energySaturation);
                energySaturation += received;
            } else if (reactionTemperature < 2000) {
                received = RF;
                reactionTemperature += ((double) received / (1000D + (reactorFuel * 10)));
                if (reactionTemperature > 2000) reactionTemperature = 2000;
            }
        } else if (reactorState == STATE_ONLINE || reactorState == STATE_STOP) {
            fieldCharge += (RF * (1D - (fieldCharge / maxFieldCharge)));
            if (fieldCharge > maxFieldCharge) fieldCharge = maxFieldCharge;
            return RF;
        }
        return received;
    }

    public boolean canStart() {
        validateStructure();
        return reactionTemperature >= 2000 && fieldCharge >= (maxFieldCharge / 2)
                && energySaturation >= (maxEnergySaturation / 2)
                && isStructureValid
                && convertedFuel + reactorFuel + conversionUnit >= 144;
    }

    public boolean canCharge() {
        validateStructure();
        return reactorState != STATE_ONLINE && isStructureValid && convertedFuel + reactorFuel + conversionUnit >= 144;
    }

    public boolean canStop() {
        validateStructure();
        return reactorState != STATE_OFFLINE && isStructureValid;
    }

    public void processButtonPress(int button) {
        if (button == 0 && canCharge()) reactorState = STATE_START;
        else if (button == 1 && canStart()) reactorState = STATE_ONLINE;
        else if (button == 2 && canStop()) reactorState = STATE_STOP;
    }

    public double getCoreDiameter() { // todo adjust so the core dose not expand before 1000>2000c
        // return (((1F + Math.sin((float)tick/50F)) / 2F) * 4) + 0.3;
        double volume = (double) (reactorFuel + convertedFuel) / 1296D;
        volume *= 1 + (reactionTemperature / maxReactTemperature) * 10D;
        return Math.cbrt(volume / (4 / 3 * Math.PI)) * 2;
    }

    public double getMaxCoreDiameter() {
        // return (((1F + Math.sin((float)tick/50F)) / 2F) * 4) + 0.3;
        double volume = (double) (reactorFuel + convertedFuel) / 1296D;
        volume *= 1 + 1 * 10D;
        return Math.cbrt(volume / (4 / 3 * Math.PI)) * 2;
    }

    private boolean isStructureValidCach = false;
    private boolean startupInitializedCach = false;
    private int reactorStateCach = -1;
    private int reactorFuelCach = -1;
    private int convertedFuelCach = -1;
    private int energySaturationCach = -1;
    private int maxEnergySaturationCach = -1;
    private double reactionTemperatureCach = -1;
    private double maxReactTemperatureCach = -1;
    private double fieldChargeCach = -1;
    private double maxFieldChargeCach = -1;

    private void detectAndSendChanges() {
        NetworkRegistry.TargetPoint tp = new NetworkRegistry.TargetPoint(
                worldObj.provider.dimensionId,
                xCoord,
                yCoord,
                zCoord,
                128);
        if (reactionTemperatureCach != reactionTemperature)
            reactionTemperatureCach = (Double) sendObjectToClient(References.DOUBLE_ID, 8, reactionTemperature, tp);
        if (tick % 10 != 0) return;
        if (isStructureValidCach != isStructureValid)
            isStructureValidCach = (Boolean) sendObjectToClient(References.BOOLEAN_ID, 0, isStructureValid, tp);
        // if (isActiveCach != isActive) isActiveCach = (Boolean) sendObjectToClient(References.BOOLEAN_ID,
        // 1, isActive, tp);
        if (startupInitializedCach != startupInitialized)
            startupInitializedCach = (Boolean) sendObjectToClient(References.BOOLEAN_ID, 2, startupInitialized, tp);
        if (reactorStateCach != reactorState)
            reactorStateCach = (Integer) sendObjectToClient(References.INT_ID, 3, reactorState, tp);
        if (reactorFuelCach != reactorFuel)
            reactorFuelCach = (Integer) sendObjectToClient(References.INT_ID, 4, reactorFuel, tp);
        if (convertedFuelCach != convertedFuel)
            convertedFuelCach = (Integer) sendObjectToClient(References.INT_ID, 5, convertedFuel, tp);
        if (energySaturationCach != energySaturation)
            energySaturationCach = (Integer) sendObjectToClient(References.INT_ID, 6, energySaturation, tp);
        if (maxEnergySaturationCach != maxEnergySaturation)
            maxEnergySaturationCach = (Integer) sendObjectToClient(References.INT_ID, 7, maxEnergySaturation, tp);
        if (maxReactTemperatureCach != maxReactTemperature)
            maxReactTemperatureCach = (Double) sendObjectToClient(References.DOUBLE_ID, 9, maxReactTemperature, tp);
        if (fieldChargeCach != fieldCharge)
            fieldChargeCach = (Double) sendObjectToClient(References.DOUBLE_ID, 10, fieldCharge, tp);
        if (maxFieldChargeCach != maxFieldCharge)
            maxFieldChargeCach = (Double) sendObjectToClient(References.DOUBLE_ID, 11, maxFieldCharge, tp);
    }

    public int getComparatorOutput(int rsMode) {
        switch (rsMode) {
            case IReactorPart.RMODE_TEMP:
                return toRSStrength(reactionTemperature, maxReactTemperature, rsMode);
            case IReactorPart.RMODE_TEMP_INV:
                return 15 - toRSStrength(reactionTemperature, maxReactTemperature, rsMode);
            case IReactorPart.RMODE_FIELD:
                return toRSStrength(fieldCharge, maxFieldCharge, rsMode);
            case IReactorPart.RMODE_FIELD_INV:
                return 15 - toRSStrength(fieldCharge, maxFieldCharge, rsMode);
            case IReactorPart.RMODE_SAT:
                return toRSStrength(energySaturation, maxEnergySaturation, rsMode);
            case IReactorPart.RMODE_SAT_INV:
                return 15 - toRSStrength(energySaturation, maxEnergySaturation, rsMode);
            case IReactorPart.RMODE_FUEL:
                return toRSStrength(convertedFuel + conversionUnit, reactorFuel - conversionUnit, rsMode);
            case IReactorPart.RMODE_FUEL_INV:
                return 15 - toRSStrength(convertedFuel + conversionUnit, reactorFuel - conversionUnit, rsMode);
        }
        return 0;
    }

    private int toRSStrength(double value, double maxValue, int mode) {
        if (maxValue == 0) return 0;
        double d = value / maxValue;
        int rs = (int) (d * 15D);
        switch (mode) {
            case IReactorPart.RMODE_FIELD:
            case IReactorPart.RMODE_FIELD_INV: {
                if (d < 0.1) rs = 0;
                break;
            }
            case IReactorPart.RMODE_FUEL:
            case IReactorPart.RMODE_FUEL_INV: {
                if (d > 0.9) rs = 15;
                break;
            }
        }
        return rs;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void receiveObjectFromServer(int index, Object object) {
        switch (index) {
            case 0:
                isStructureValid = (Boolean) object;
                break;
            // case 1: isActive = (Boolean) object; break;
            case 2:
                startupInitialized = (Boolean) object;
                break;
            case 3:
                reactorState = (Integer) object;
                break;
            case 4:
                reactorFuel = (Integer) object;
                break;
            case 5:
                convertedFuel = (Integer) object;
                break;
            case 6:
                energySaturation = (Integer) object;
                break;
            case 7:
                maxEnergySaturation = (Integer) object;
                break;
            case 8:
                reactionTemperature = (Double) object;
                break;
            case 9:
                maxReactTemperature = (Double) object;
                break;
            case 10:
                fieldCharge = (Double) object;
                break;
            case 11:
                maxFieldCharge = (Double) object;
                break;
            case 100:
                FMLClientHandler.instance().getClient().effectRenderer.addEffect(
                        new Particles.ReactorExplosionParticle(worldObj, xCoord, yCoord, zCoord, (Integer) object));
        }
        super.receiveObjectFromServer(index, object);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 40960.0D;
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        NBTTagList stabilizerList = new NBTTagList();
        for (TileLocation offset : stabilizerLocations) {
            NBTTagCompound compound1 = new NBTTagCompound();
            offset.writeToNBT(compound1, "tag");
            stabilizerList.appendTag(compound1);
        }
        if (stabilizerList.tagCount() > 0) compound.setTag("Stabilizers", stabilizerList);

        compound.setByte("State", (byte) reactorState);
        compound.setBoolean("isStructureValid", isStructureValid);
        compound.setBoolean("startupInitialized", startupInitialized);
        // compound.setBoolean("isActive", isActive);
        compound.setInteger("energySaturation", energySaturation);
        compound.setInteger("maxEnergySaturation", maxEnergySaturation);
        compound.setInteger("reactorFuel", reactorFuel);
        compound.setInteger("convertedFuel", convertedFuel);
        compound.setDouble("reactionTemperature", reactionTemperature);
        compound.setDouble("maxReactTemperature", maxReactTemperature);
        compound.setDouble("fieldCharge", fieldCharge);
        compound.setDouble("maxFieldCharge", maxFieldCharge);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        stabilizerLocations = new ArrayList<TileLocation>();
        if (compound.hasKey("Stabilizers")) {
            NBTTagList stabilizerList = compound.getTagList("Stabilizers", 10);
            for (int i = 0; i < stabilizerList.tagCount(); i++) {
                TileLocation offset = new TileLocation();
                offset.readFromNBT(stabilizerList.getCompoundTagAt(i), "tag");
                stabilizerLocations.add(offset);
            }
        }

        reactorState = compound.getByte("State");
        isStructureValid = compound.getBoolean("isStructureValid");
        startupInitialized = compound.getBoolean("startupInitialized");
        // isActive = compound.getBoolean("isActive");
        energySaturation = compound.getInteger("energySaturation");
        maxEnergySaturation = compound.getInteger("maxEnergySaturation");
        reactorFuel = compound.getInteger("reactorFuel");
        convertedFuel = compound.getInteger("convertedFuel");
        reactionTemperature = compound.getDouble("reactionTemperature");
        maxReactTemperature = compound.getDouble("maxReactTemperature");
        fieldCharge = compound.getDouble("fieldCharge");
        maxFieldCharge = compound.getDouble("maxFieldCharge");
    }
}
