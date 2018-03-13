package com.brandon3055.draconicevolution.blocks.reactor.tileentity;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.handlers.ProcessHandler;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.lib.datamanager.*;
import com.brandon3055.brandonscore.utils.FacingUtils;
import com.brandon3055.brandonscore.utils.HolidayHelper;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.GuiHandler;
import com.brandon3055.draconicevolution.blocks.reactor.ProcessExplosion;
import com.brandon3055.draconicevolution.blocks.reactor.ReactorEffectHandler;
import com.brandon3055.draconicevolution.client.gui.GuiReactor;
import com.brandon3055.draconicevolution.lib.DESoundHandler;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by brandon3055 on 6/11/2016.
 */
public class TileReactorCore extends TileBCBase implements ITickable {

    //Frame Movement
    public int frameMoveContactPoints = 0;
    public boolean isFrameMoving = false;
    public boolean moveBlocksProvided = false;
    //region =========== Structure Fields ============

    public static final int COMPONENT_MAX_DISTANCE = 8;
    public final ManagedVec3I[] componentPositions = new ManagedVec3I[6]; //Invalid position is 0, 0, 0
    private final ManagedEnum<Axis> stabilizerAxis = register("stabilizerAxis", new ManagedEnum<>(Axis.Y)).saveToTile().syncViaTile().finish();
    public final ManagedBool structureValid = register("structureValid", new ManagedBool(false)).saveToTile().syncViaTile().finish();
    public final ManagedString structureError = register("structureError", new ManagedString("")).saveToTile().syncViaTile().finish();

    private int tick = 0;
    private Map<BlockPos, Integer> blockIntrusions = new HashMap<>();

    //endregion ======================================

    //region =========== Core Logic Fields ===========

    /**
     * This is the current operational state of the reactor.
     */
    public final ManagedEnum<ReactorState> reactorState = register("reactorState", new ManagedEnum<>(ReactorState.INVALID)).saveToTile().syncViaTile().finish();

    /**
     * Remaining fuel that is yet to be consumed by the reaction.
     */
    public final ManagedDouble reactableFuel = register("reactableFuel", new ManagedDouble(0)).saveToTile().saveToItem().syncViaTile().finish();
    /**
     * Fuel that has been converted to chaos by the reaction.
     */
    public final ManagedDouble convertedFuel = register("convertedFuel", new ManagedDouble(0)).saveToTile().saveToItem().syncViaTile().finish();
    public final ManagedDouble temperature = register("temperature", new ManagedDouble(20)).saveToTile().syncViaTile().finish();
    public static final double MAX_TEMPERATURE = 10000;

    public final ManagedDouble shieldCharge = register("shieldCharge", new ManagedDouble(0)).saveToTile().syncViaTile().finish();
    public final ManagedDouble maxShieldCharge = register("maxShieldCharge", new ManagedDouble(0)).saveToTile().syncViaTile().finish();

    /**
     * This is how saturated the core is with energy.
     */
    public final ManagedInt saturation = register("saturation", new ManagedInt(0)).saveToTile().syncViaContainer().finish();
    public final ManagedInt maxSaturation = register("maxSaturation", new ManagedInt(0)).saveToTile().syncViaContainer().finish();


    public final ManagedDouble tempDrainFactor = register("tempDrainFactor", new ManagedDouble(0)).saveToTile().syncViaContainer().finish();
    public final ManagedDouble generationRate = register("generationRate", new ManagedDouble(0)).saveToTile().syncViaContainer().finish();
    public final ManagedInt fieldDrain = register("fieldDrain", new ManagedInt(0)).saveToTile().syncViaContainer().finish();
    public final ManagedDouble fieldInputRate = register("fieldInputRate", new ManagedDouble(0)).saveToTile().syncViaContainer().finish();
    public final ManagedDouble fuelUseRate = register("fuelUseRate", new ManagedDouble(0)).saveToTile().syncViaContainer().finish();

    public final ManagedBool startupInitialized = register("startupInitialized", new ManagedBool(false)).saveToTile().syncViaContainer().finish();
    public final ManagedBool failSafeMode = register("failSafeMode", new ManagedBool(false)).saveToTile().syncViaTile().finish();

    //Explody Stuff!
    private ProcessExplosion explosionProcess = null;
    public final ManagedInt explosionCountdown = register("explosionCountdown", new ManagedInt(-1)).saveToTile().syncViaContainer().finish();
    private int minExplosionDelay = 0;

    //endregion ======================================

    //region ========= Visual Control Fields =========

    public float coreAnimation = 0;
    public float shieldAnimation = 0;
    public float shieldAnimationState = 0;
    /**
     * This controls the activation (fade in/fade out) of the beam and stabilizer animations.
     */
    public final ManagedDouble shaderAnimationState = register("shaderAnimationState", new ManagedDouble(0)).saveToTile().syncViaTile().finish();
    public final ManagedDouble animExtractState = register("animExtractState", new ManagedDouble(0)).saveToTile().syncViaTile().finish();
    private final ReactorEffectHandler effectHandler;

    //endregion ======================================

    public TileReactorCore() {
        for (int i = 0; i < componentPositions.length; i++) {
            componentPositions[i] = register("componentPosition" + i, new ManagedVec3I(new Vec3I(0, 0, 0))).saveToTile().syncViaTile().finish();
        }

        effectHandler = DraconicEvolution.proxy.createReactorFXHandler(this);
    }

    //region Update Logic

    @Override
    public void update() {
        super.update();
        updateCoreLogic();
        frameMoveContactPoints = 0;
        isFrameMoving = false;
        moveBlocksProvided = false;

        if (world.isRemote && effectHandler != null) {
            effectHandler.updateEffects();

            if (HolidayHelper.isAprilFools()) {
                if (inView) {
                    viewTicks++;
                    if (viewTicks > 100 && roller == null) {
                        if (world.rand.nextInt(25) == 0) {
                            roller = new Roller(Vec3D.getCenter(this), world, getCoreDiameter());
                        }
                        else {
                            viewTicks = 0;
                        }
                    }
                }
                else {
                    if (viewTicks > 0 && roller != null && roller.age > 100) {
                        roller = null;
                    }

                    viewTicks = 0;
                }

                if (roller != null) {
                    roller.update();
                }
            }
        }

        tick++;
    }

    //endregion

    //region ################# Core Logic ##################

    private void updateCoreLogic() {
        if (world.isRemote) {
            checkPlayerCollision();
            coreAnimation += shaderAnimationState.value;
            if (maxShieldCharge.value == 0) {
                shaderAnimationState.value = shieldAnimation = 0;
            }
            else {
                shieldAnimationState = MathHelper.clip((float) (shieldCharge.value / maxShieldCharge.value) * 10, 0F, 1F);
                shieldAnimation += shieldAnimationState;
            }

            if (reactorState.value == ReactorState.BEYOND_HOPE) {
                shieldAnimationState = world.rand.nextInt(10) == 0 ? 0 : 1;
                shieldAnimation += shieldAnimationState;
            }
            return;
        }

        shaderAnimationState.value = Math.min(1D, (temperature.value - 20) / 1000D);
        animExtractState.value = 0;

//        shieldCharge.value = 0;

//        convertedFuel.value = 0;
//        reactableFuel.value = 10000;
//        temperature.value = 2010;
//saturation.value = maxSaturation.value;

        switch (reactorState.value) {
            case INVALID:
                updateOfflineState();
                break;
            case COLD:
                updateOfflineState();
                break;
            case WARMING_UP:
                initializeStartup();
                checkBlockIntrusions();
                break;
            case RUNNING:
                updateOnlineState();
                checkBlockIntrusions();

                if (failSafeMode.value && temperature.value < 2500 && (double) saturation.value / (double) maxSaturation.value >= 0.99) {
                    LogHelper.dev("Reactor: Initiating Fail-Safe Shutdown!");
                    shutdownReactor();
                }

                double sat = 1D - (saturation.value / (double) maxSaturation.value);
                animExtractState.value = Math.min(1, sat * 10);
                break;
            case STOPPING:
                updateOnlineState();
                checkBlockIntrusions();
                if (temperature.value <= 2000) {
                    reactorState.value = ReactorState.COOLING;
                }
                break;
            case COOLING:
                updateOfflineState();
                if (temperature.value <= 100) {
                    reactorState.value = ReactorState.COLD;
                }
                break;
            case BEYOND_HOPE:
                checkBlockIntrusions();
                updateCriticalState();
                break;
        }

//        reactorState.value = ReactorState.BEYOND_HOPE;
    }

    /**
     * Update the reactors offline state.
     * This is responsible for things like returning the core temperature to minimum and draining remaining charge after the reactor shuts down.
     */
    private void updateOfflineState() {
        if (temperature.value > 20) {
            temperature.value -= 0.5;
        }
        if (shieldCharge.value > 0) {
            shieldCharge.value -= maxShieldCharge.value * 0.0005;
        }
        else if (shieldCharge.value < 0) {
            shieldCharge.value = 0;
        }
        if (saturation.value > 0) {
            saturation.value -= maxSaturation.value * 0.000001D;
        }
        else if (saturation.value < 0) {
            saturation.value = 0;
        }
    }

    /**
     * This method is fired when the reactor enters the warm up state.
     * The first time this method is fired if initializes all of the reactors key fields.
     */
    private void initializeStartup() {
        if (!startupInitialized.value) {
            double totalFuel = reactableFuel.value + convertedFuel.value;
            maxShieldCharge.value = totalFuel * 96.45061728395062 * 100;
            maxSaturation.value = (int) (totalFuel * 96.45061728395062 * 1000);

            if (saturation.value > maxSaturation.value) {
                saturation.value = maxSaturation.value;
            }

            if (shieldCharge.value > maxShieldCharge.value) {
                shieldCharge.value = maxShieldCharge.value;
            }

            startupInitialized.value = true;
        }
    }

    private void updateOnlineState() {
////        convertedFuel.value += reactableFuel.value;
//        reactableFuel.value = 0;

        double coreSat = (double) saturation.value / (double) maxSaturation.value;         //1 = Max Saturation
        double negCSat = (1D - coreSat) * 99D;                                             //99 = Min Saturation. I believe this tops out at 99 because at 100 things would overflow and break.
        double temp50 = Math.min((temperature.value / MAX_TEMPERATURE) * 50, 99);          //50 = Max Temp. Why? TBD
        double tFuel = convertedFuel.value + reactableFuel.value;                          //Total Fuel.
        double convLVL = ((convertedFuel.value / tFuel) * 1.3D) - 0.3D;                    //Conversion Level sets how much the current conversion level boosts power gen. Range: -0.3 to 1.0

        //region ============= Temperature Calculation =============

        double tempOffset = 444.7;    //Adjusts where the temp falls to at 100% saturation

        //The exponential temperature rise which increases as the core saturation goes down
        double tempRiseExpo = (negCSat * negCSat * negCSat) / (100 - negCSat) + tempOffset; //This is just terrible... I cant believe i wrote this stuff...

        //This is used to add resistance as the temp rises because the hotter something gets the more energy it takes to get it hotter
        double tempRiseResist = (temp50 * temp50 * temp50 * temp50) / (100 - temp50);       //^ Mostly Correct... The hotter an object gets the faster it dissipates heat into its surroundings to the more energy it takes to compensate for that energy loss.

        //This puts all the numbers together and gets the value to raise or lower the temp by this tick. This is dealing with very big numbers so the result is divided by 10000
        double riseAmount = (tempRiseExpo - (tempRiseResist * (1D - convLVL)) + convLVL * 1000) / 10000;

        //Apply energy calculations.
        if (reactorState.value == ReactorState.STOPPING && convLVL < 1) {
            if (temperature.value <= 2001) {
                reactorState.value = ReactorState.COOLING;
                startupInitialized.value = false;
                return;
            }
            if (saturation.value >= maxSaturation.value * 0.99D && reactableFuel.value > 0D) {
                temperature.value -= 1D - convLVL;
            }
            else {
                temperature.value += riseAmount * 10;
            }
        }
        else {
            temperature.value += riseAmount * 10;
        }

//        temperature.value = 18000;

        //endregion ================================================

        //region ============= Energy Calculation =============

        int baseMaxRFt = (int) ((maxSaturation.value / 1000D) * DEConfig.reactorOutputMultiplier * 1.5D);
        int maxRFt = (int) (baseMaxRFt * (1D + (convLVL * 2)));
        generationRate.value = (1D - coreSat) * maxRFt;
        saturation.value += generationRate.value;

        //endregion ===========================================

        //region ============= Shield Calculation =============

        tempDrainFactor.value = temperature.value > 8000 ? 1 + ((temperature.value - 8000) * (temperature.value - 8000) * 0.0000025) : temperature.value > 2000 ? 1 : temperature.value > 1000 ? (temperature.value - 1000) / 1000 : 0;
//        double drain = Math.min(tempDrainFactor.value * Math.max(0.01, (1D - convLVL)) * (baseMaxRFt / 10.923556), Double.MAX_VALUE);
        fieldDrain.value = (int) Math.min(tempDrainFactor.value * Math.max(0.01, (1D - coreSat)) * (baseMaxRFt / 10.923556), (double) Integer.MAX_VALUE); //<(baseMaxRFt/make smaller to increase field power drain)

        double fieldNegPercent = 1D - (shieldCharge.value / maxShieldCharge.value);
        fieldInputRate.value = fieldDrain.value / fieldNegPercent;
        shieldCharge.value -= Math.min(fieldDrain.value, shieldCharge.value);

        //endregion ===========================================

        //region ============== Fuel Calculation ==============

        fuelUseRate.value = tempDrainFactor.value * (1D - coreSat) * (0.001 * DEConfig.reactorFuelUsageMultiplier); //<Last number is base fuel usage rate
        if (reactableFuel.value > 0) {
            convertedFuel.value += fuelUseRate.value;
            reactableFuel.value -= fuelUseRate.value;
        }

        //endregion ===========================================

        //region Explosion
        if ((shieldCharge.value <= 0) && temperature.value > 2000 && reactorState.value != ReactorState.BEYOND_HOPE) {
            reactorState.value = ReactorState.BEYOND_HOPE;
            for (int i = 0; i < componentPositions.length; i++) {
                ManagedVec3I v = componentPositions[i];
                if (v.vec.sum() > 0) {
                    BlockPos p = getOffsetPos(v.vec).offset(EnumFacing.getFront(i).getOpposite());
                    world.newExplosion(null, p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5, 4, true, true);
                }
            }
        }
        //endregion ======
    }

    public void updateCriticalState() {
        if (!(world instanceof WorldServer)) {
            return;
        }

        shieldCharge.value = world.rand.nextInt(Math.max(1, (int) (maxShieldCharge.value * 0.01)));
        animExtractState.value = 1;
        temperature.value = MathHelper.approachExp(temperature.value, MAX_TEMPERATURE * 1.2, 0.0005);

        if (DEConfig.disableLargeReactorBoom) {
            if (explosionCountdown.value == -1) {
                explosionCountdown.value = 1200 + world.rand.nextInt(2400);
            }

            if (explosionCountdown.value-- <= 0) {
                minimalBoom();
            }

            return;
        }

//        ProcessHandler.clearHandler();
//        explosionProcess = null;
//        if (explosionCountdown.value > 200) explosionCountdown.value = 200;
//        LogHelper.dev("");

        if (explosionProcess == null) {
            double radius = Utils.map(convertedFuel.value + reactableFuel.value, 144, 10368, 50D, 350D) * DEConfig.reactorExplosionScale;
            explosionProcess = new ProcessExplosion(pos, (int) radius, (WorldServer) world, -1);
            ProcessHandler.addProcess(explosionProcess);
            explosionCountdown.value = -1;
            minExplosionDelay = 1200 + world.rand.nextInt(2400);
            return;
        }

        minExplosionDelay--;
        
        if (!explosionProcess.isCalculationComplete()) {
            return;
        }

        if (explosionCountdown.value == -1) {
            explosionCountdown.value = (60 * 20) + Math.max(0, minExplosionDelay);
        }

        if (explosionCountdown.value-- <= 0) {
//            explosionProcess = null;
            explosionProcess.detonate();
            world.setBlockToAir(pos);
        }
    }

    public boolean canCharge() {
        if (!world.isRemote && !validateStructure()) {
            return false;
        }
        else if (reactorState.value == ReactorState.BEYOND_HOPE) {
            return false;
        }

        return (reactorState.value == ReactorState.COLD || reactorState.value == ReactorState.COOLING) && reactableFuel.value + convertedFuel.value >= 144;
    }

    public boolean canActivate() {
        if (!world.isRemote && !validateStructure()) {
            return false;
        }
        else if (reactorState.value == ReactorState.BEYOND_HOPE) {
            return false;
        }

        return (reactorState.value == ReactorState.WARMING_UP || reactorState.value == ReactorState.STOPPING) && temperature.value >= 2000 && ((saturation.value >= maxSaturation.value / 2 && shieldCharge.value >= maxShieldCharge.value / 2) || reactorState.value == ReactorState.STOPPING);
    }

    public boolean canStop() {
        if (reactorState.value == ReactorState.BEYOND_HOPE) {
            return false;
        }

        return reactorState.value == ReactorState.RUNNING || reactorState.value == ReactorState.WARMING_UP;
    }

    //region Notes for V2 Logic
    /*
    *
    * Calculation Order: WIP
    *
    * 1: Calculate conversion modifier
    *
    * 2: Saturation calculations
    *
    * 3: Temperature Calculations
    *
    * 4: Energy Calculations then recalculate saturation so the new value is reflected in the shield calculations
    *
    * 5: Shield Calculation
    *
    *
    */// endregion*/

    //endregion ############################################

    //region ############## User Interaction ###############

    private static final byte ID_CHARGE = 0;
    private static final byte ID_ACTIVATE = 1;
    private static final byte ID_SHUTDOWN = 2;
    private static final byte ID_FAIL_SAFE = 3;

    public void chargeReactor() {
        if (world.isRemote) {
            LogHelper.dev("Reactor: Start Charging");
            sendPacketToServer(output -> output.writeByte(ID_CHARGE), 0);
        }
        else if (canCharge()) {
            LogHelper.dev("Reactor: Start Charging");
            reactorState.value = ReactorState.WARMING_UP;
        }
    }

    public void activateReactor() {
        if (world.isRemote) {
            LogHelper.dev("Reactor: Activate");
            sendPacketToServer(output -> output.writeByte(ID_ACTIVATE), 0);
        }
        else if (canActivate()) {
            LogHelper.dev("Reactor: Activate");
            reactorState.value = ReactorState.RUNNING;
        }
    }

    public void shutdownReactor() {
        if (world.isRemote) {
            LogHelper.dev("Reactor: Shutdown");
            sendPacketToServer(output -> output.writeByte(ID_SHUTDOWN), 0);
        }
        else if (canStop()) {
            LogHelper.dev("Reactor: Shutdown");
            reactorState.value = ReactorState.STOPPING;
        }
    }

    public void toggleFailSafe() {
        if (world.isRemote) {
            sendPacketToServer(output -> output.writeByte(ID_FAIL_SAFE), 0);
        }
        else {
            failSafeMode.value = !failSafeMode.value;
        }
    }

    public void onComponentClicked(EntityPlayer player, TileReactorComponent component) {
        if (!world.isRemote) {
            player.openGui(DraconicEvolution.instance, GuiHandler.GUIID_REACTOR, world, pos.getX(), pos.getY(), pos.getZ());
//            NBTTagCompound tag = new NBTTagCompound();
//            tag.setInteger("x", component.getPos().getX());
//            tag.setInteger("y", component.getPos().getY());
//            tag.setInteger("z", component.getPos().getZ());
            sendPacketToClient((EntityPlayerMP) player, output -> output.writePos(component.getPos()), 1);
        }
    }

    @Override
    public void receivePacketFromClient(MCDataInput data, EntityPlayerMP client, int id) {
        byte func = data.readByte();
        if (id == 0 && func == ID_CHARGE) {
            chargeReactor();
        }
        else if (id == 0 && func == ID_ACTIVATE) {
            activateReactor();
        }
        else if (id == 0 && func == ID_SHUTDOWN) {
            shutdownReactor();
        }
        else if (id == 0 && func == ID_FAIL_SAFE) {
            toggleFailSafe();
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void receivePacketFromServer(MCDataInput data, int id) {
        if (id == 1) {
            BlockPos pos = data.readPos();
            TileEntity tile = world.getTileEntity(pos);
            GuiScreen screen = Minecraft.getMinecraft().currentScreen;
            if (tile instanceof TileReactorComponent && screen instanceof GuiReactor) {
                ((GuiReactor) screen).component = (TileReactorComponent) tile;
            }
        }
    }


    private void checkPlayerCollision() {
        EntityPlayer player = BrandonsCore.proxy.getClientPlayer();
        double distance = Math.min(Utils.getDistanceAtoB(new Vec3D(player).add(0, player.eyeHeight, 0), Vec3D.getCenter(pos)), Utils.getDistanceAtoB(new Vec3D(player), Vec3D.getCenter(pos)));
        if (distance < (getCoreDiameter() / 2) + 0.5) {
            double dMod = 1D - (distance / Math.max(0.1, (getCoreDiameter() / 2) + 0.5));
            double offsetX = player.posX - pos.getX() + 0.5;
            double offsetY = player.posY - pos.getY() + 0.5;
            double offsetZ = player.posZ - pos.getZ() + 0.5;
            double m = 1D * dMod;
            player.addVelocity(offsetX * m, offsetY * m, offsetZ * m);
        }
    }

    //endregion ############################################

    //region ################# Multi-block #################

    /**
     * Called when the core is poked by a reactor component.
     * If the structure is already initialized this validates the structure.
     * Otherwise it attempts to initialize the structure.
     *
     * @param component The component that poked the core.
     */
    public void pokeCore(TileReactorComponent component, EnumFacing pokeFrom) {
        LogHelper.dev("Reactor: Core Poked, StructValid: " + structureValid);
        if (structureValid.value) {
            //If the component is an unbound injector and there is no component bound on the same side then bind it.
            if (component instanceof TileReactorEnergyInjector && !component.isBound.value) {
                TileEntity tile = world.getTileEntity(getOffsetPos(componentPositions[pokeFrom.getIndex()].vec));
                if (tile == this) {
                    componentPositions[pokeFrom.getIndex()].vec = getOffsetVec(component.getPos());
                    component.bindToCore(this);
                    LogHelper.dev("Reactor: Injector Added!");
                }
            }

            validateStructure();
        }
        else {
            attemptInitialization();
        }
    }

    /**
     * Called when a component is physically broken
     */
    public void componentBroken(TileReactorComponent component, EnumFacing componentSide) {
        if (!structureValid.value || reactorState.value == ReactorState.BEYOND_HOPE) {
            return;
        }

        if (component instanceof TileReactorEnergyInjector) {
            LogHelper.dev("Reactor: Component broken! (Injector)");
            TileEntity tile = world.getTileEntity(getOffsetPos(componentPositions[componentSide.getIndex()].vec));

            if (tile == component || tile == null) {
                LogHelper.dev("Reactor: -Removed");
                componentPositions[componentSide.getIndex()].vec.set(0, 0, 0);
            }
        }
        else if (reactorState.value != ReactorState.COLD) {
            LogHelper.dev("Reactor: Component broken, Structure Invalidated (Unsafe!!!!)");
            if (temperature.value > 2000) {
                reactorState.value = ReactorState.BEYOND_HOPE;
            }
            else if (temperature.value >= 350) {
                minimalBoom();
            }
            else {
                reactorState.value = ReactorState.INVALID;
            }
            structureValid.value = false;
        }
        else {
            LogHelper.dev("Reactor: Component broken, Structure Invalidated (Safe)");
            structureValid.value = false;
            reactorState.value = ReactorState.INVALID;
        }
    }

    public void checkBlockIntrusions() {
        if (!(world instanceof WorldServer)) {
            return;
        }

        if (tick % 100 == 0) {
            double rad = (getCoreDiameter() * 1.05) / 2;
            Iterable<BlockPos> inRange = BlockPos.getAllInBox(pos.add(-rad, -rad, -rad), pos.add(rad + 1, rad + 1, rad + 1));

            for (BlockPos p : inRange) {
                if (p.equals(pos) || Utils.getDistanceAtoB(p.getX(), p.getY(), p.getZ(), pos.getX(), pos.getY(), pos.getZ()) - 0.5 >= rad) {
                    continue;
                }

                if (!world.isAirBlock(p) && !blockIntrusions.containsKey(p)) {
                    blockIntrusions.put(p, 0);
                }
            }
        }

        if (blockIntrusions.size() > 0) {
            Iterator<Map.Entry<BlockPos, Integer>> i = blockIntrusions.entrySet().iterator();

            while (i.hasNext()) {
                Map.Entry<BlockPos, Integer> entry = i.next();
                final Vec3D iPos = new Vec3D(entry.getKey());

                if (world.rand.nextInt(10) == 0) {
                    ((WorldServer) world).spawnParticle(EnumParticleTypes.FLAME, false, iPos.x, iPos.y, iPos.z, 5, world.rand.nextDouble(), world.rand.nextDouble(), world.rand.nextDouble(), 0.01D);
                }

                entry.setValue(entry.getValue() + 1);
                if (entry.getValue() > 100) {
                    i.remove();
                    world.playEvent(2001, entry.getKey(), Block.getStateId(world.getBlockState(entry.getKey())));
                    world.setBlockToAir(entry.getKey());
                }
            }

            if (tick % 20 == 0 || world.rand.nextInt(40) == 0) {
                DESoundHandler.playSoundFromServer(world, Vec3D.getCenter(pos), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1, 0.9F + world.rand.nextFloat() * 0.2F, false, 64);
            }
        }
    }

    //region Initialization

    /**
     * Will will check if the structure is valid and if so will initialize the structure.
     */
    public void attemptInitialization() {
        LogHelper.dev("Reactor: Attempt Initialization");

        if (!findComponents()) {
            LogHelper.dev("Failed fo find components");
            return;
        }

        if (!checkStabilizerAxis()) {
            LogHelper.dev("Failed stabilizer check");
            return;
        }

        if (!bindComponents()) {
            LogHelper.dev("Failed fo bind components");
            return;
        }

        structureValid.value = true;
        if (reactorState.value == ReactorState.INVALID) {
            if (temperature.value <= 100) {
                reactorState.value = ReactorState.COLD;
            }
            else {
                reactorState.value = ReactorState.COOLING;
            }
        }
        LogHelper.dev("Reactor: Structure Successfully Initialized!\n");
    }

    /**
     * Finds all Reactor Components available to this core and
     *
     * @return true if exactly 4 stabilizers were found.
     */
    public boolean findComponents() {
        LogHelper.dev("Reactor: Find Components");
        int stabilizersFound = 0;
        for (EnumFacing facing : EnumFacing.VALUES) {
            componentPositions[facing.getIndex()].vec.set(0, 0, 0);
            for (int i = 4; i < COMPONENT_MAX_DISTANCE; i++) {
                BlockPos searchPos = pos.offset(facing, i);

                if (!world.isAirBlock(searchPos)) {
                    TileEntity searchTile = world.getTileEntity(searchPos);
                    LogHelper.dev("Reactor: -Found: " + searchTile);

                    if (searchTile instanceof TileReactorComponent && ((TileReactorComponent) searchTile).facing.value == facing.getOpposite() && i >= 2) {
                        LogHelper.dev("Set " + facing.getIndex() + " " + getOffsetVec(searchPos));
                        componentPositions[facing.getIndex()].vec = getOffsetVec(searchPos);
                        if (searchTile instanceof TileReactorStabilizer) {
                            stabilizersFound++;
                        }
                    }

                    break;
                }
            }
        }

        LogHelper.dev("Reactor: Find Components | found " + stabilizersFound + " Stabilizers");
        return stabilizersFound == 4;
    }

    /**
     * Checks the layout of the stabilizers and sets the stabilizer axis accordingly.
     *
     * @return true if the stabilizer configuration is valid.
     */
    public boolean checkStabilizerAxis() {
        LogHelper.dev("Reactor: Check Stabilizer Axis");
        for (Axis axis : Axis.values()) {
            boolean axisValid = true;
            for (EnumFacing facing : FacingUtils.getFacingsAroundAxis(axis)) {
                TileEntity tile = world.getTileEntity(getOffsetPos(componentPositions[facing.getIndex()].vec));
                //The facing check should not be needed here but does not heart to be to careful.
                if (!(tile instanceof TileReactorStabilizer && ((TileReactorStabilizer) tile).facing.value == facing.getOpposite())) {
                    axisValid = false;
                    break;
                }
            }

            if (axisValid) {
                stabilizerAxis.value = axis;
                LogHelper.dev("Reactor: -Found Valid Axis: " + axis);
                return true;
            }
        }

        return false;
    }

    /**
     * At this point we know there are at least 4 stabilizers in a valid configuration and possibly some injectors.
     * This method binds them to the core.
     *
     * @return false if failed to bind all 4 stabilizers. //Just in case...
     */
    public boolean bindComponents() {
        LogHelper.dev("Reactor: Binding Components");
        int stabilizersBound = 0;
        for (int i = 0; i < 6; i++) {
            TileEntity tile = world.getTileEntity(getOffsetPos(componentPositions[i].vec));
            if (tile instanceof TileReactorComponent) {
                ((TileReactorComponent) tile).bindToCore(this);

                if (tile instanceof TileReactorStabilizer) {
                    stabilizersBound++;
                }
            }
        }

        return stabilizersBound == 4;
    }

    //endregion

    //region Structure Validation

    /**
     * Checks if the structure is still valid and carries out the appropriate action if it is not.
     */
    public boolean validateStructure() {
        LogHelper.dev("Reactor: Validate Structure");
        for (EnumFacing facing : FacingUtils.getFacingsAroundAxis(stabilizerAxis.value)) {
            BlockPos pos = getOffsetPos(componentPositions[facing.getIndex()].vec);
            if (!world.getChunkFromBlockCoords(pos).isLoaded()) {
                return true;
            }

            TileEntity tile = world.getTileEntity(pos);
            LogHelper.dev("Reactor: Validate Stabilizer: " + tile);
            if (!(tile instanceof TileReactorStabilizer) || !((TileReactorStabilizer) tile).getCorePos().equals(this.pos)) {
                LogHelper.dev("Reactor: Structure Validation Failed!!!");
                return false;
            }

            for (ManagedVec3I vec : componentPositions) {
                pos = getOffsetPos(vec.vec);
                tile = world.getTileEntity(pos);

                if (tile instanceof TileReactorEnergyInjector && !((TileReactorEnergyInjector) tile).isBound.value) {
                    ((TileReactorEnergyInjector) tile).bindToCore(this);
                }

                if (tile instanceof TileReactorComponent && ((TileReactorComponent) tile).getCorePos().equals(this.pos) && !((TileReactorComponent) tile).isBound.value) {
                    LogHelper.warn("Detected a reactor component in an invalid state. This is likely due to a recent bug that has since been fixed. The state of this component will now be corrected.");
                    ((TileReactorComponent) tile).bindToCore(this);
                }
            }
        }

        LogHelper.dev("Reactor: Structure Validated!");
        return true;
    }

    //endregion

    private void minimalBoom() {
        IBlockState lava = Blocks.FLOWING_LAVA.getDefaultState();
        LogHelper.dev(FluidRegistry.isFluidRegistered("pyrotheum"));
        if (FluidRegistry.isFluidRegistered("pyrotheum")) {
            Fluid pyro = FluidRegistry.getFluid("pyrotheum");
            if (pyro.canBePlacedInWorld()) {
                lava = pyro.getBlock().getDefaultState();
            }
        }

        Vec3D vec = Vec3D.getCenter(pos);
        world.setBlockToAir(pos);
        world.createExplosion(null, vec.x, vec.y, vec.z, 8, true);
        int c = 25 + world.rand.nextInt(25);
        for (int i = 0; i < c; i++) {
            EntityFallingBlock entity = new EntityFallingBlock(world, vec.x, vec.y, vec.z, lava);
            entity.fallTime = 1;
            entity.shouldDropItem = false;
            double vMod = 0.5 + (2 * world.rand.nextDouble());
            entity.addVelocity((world.rand.nextDouble() - 0.5) * vMod, (world.rand.nextDouble() / 1.5) * vMod, (world.rand.nextDouble() - 0.5) * vMod);
            world.spawnEntity(entity);
        }
    }

    //region Getters & Setters

    private BlockPos getOffsetPos(Vec3I vec) {
        return pos.subtract(vec.getPos());
    }

    private Vec3I getOffsetVec(BlockPos offsetPos) {
        return new Vec3I(pos.subtract(offsetPos));
    }

    public TileReactorComponent getComponent(EnumFacing facing) {
        TileEntity tile = world.getTileEntity(getOffsetPos(componentPositions[facing.getIndex()].vec));

        if (tile instanceof TileReactorComponent && ((TileReactorComponent) tile).facing.value == facing.getOpposite()) {
            return (TileReactorComponent) tile;
        }

        return null;
    }

    //endregion

    //endregion ############################################

    //region ################# Other Logic ##################

    public int injectEnergy(int rf) {
        int received = 0;
        if (reactorState.value == ReactorState.WARMING_UP) {
            if (!startupInitialized.value) {
                return 0;
            }
            if (shieldCharge.value < (maxShieldCharge.value / 2)) {
                received = Math.min(rf, (int) (maxShieldCharge.value / 2) - (int) shieldCharge.value + 1);
                shieldCharge.value += received;
                if (shieldCharge.value > (maxShieldCharge.value / 2)) {
                    shieldCharge.value = (maxShieldCharge.value / 2);
                }
            }
            else if (saturation.value < (maxSaturation.value / 2)) {
                received = Math.min(rf, (maxSaturation.value / 2) - saturation.value);
                saturation.value += received;
            }
            else if (temperature.value < 2000) {
                received = rf;
                temperature.value += ((double) received / (1000D + (reactableFuel.value * 10)));
                if (temperature.value > 2500) {
                    temperature.value = 2500;
                }
            }
        }
        else if (reactorState.value == ReactorState.RUNNING || reactorState.value == ReactorState.STOPPING) {
            double tempFactor = 1;

            //If the temperature goes past 15000 force the shield to drain by the time it hits 25000 regardless of input.
            if (temperature.value > 15000) {
                tempFactor = 1D - Math.min(1, (temperature.value - 15000D) / 10000D);
            }

            shieldCharge.value += Math.min((rf * (1D - (shieldCharge.value / maxShieldCharge.value))), maxShieldCharge.value - shieldCharge.value) * tempFactor;
            if (shieldCharge.value > maxShieldCharge.value) {
                shieldCharge.value = maxShieldCharge.value;
            }

            return rf;
        }
        return received;
    }

    //endregion #############################################

    //region Rendering

    @Override
    public boolean canRenderBreaking() {
        return true;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return true;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 40960.0D;
    }

    public double getCoreDiameter() {
        double volume = (reactableFuel.value + convertedFuel.value) / 1296D;
        volume *= 1 + (temperature.value / MAX_TEMPERATURE) * 10D;
        double diameter = Math.cbrt(volume / (4 / 3 * Math.PI)) * 2;
        return Math.max(0.5, diameter);
    }

    //endregion

    public boolean inView = false;
    public int viewTicks = 0;
    public Roller roller = null;

    public static class Roller {
        public Vec3D pos;
        public Vec3D lastPos;
        public double direction;
        private World world;
        private double diameter;
        public double fallVelocity = 0;
        public double speed = 0;
        public int age = 0;

        public Roller(Vec3D pos, World world, double diameter) {
            this.pos = pos;
            this.lastPos = pos.copy();
            this.direction = world.rand.nextDouble() * Math.PI * 2;
            this.world = world;
            this.diameter = (diameter / 2) + 1;
            this.speed = -0.3;
        }

        public void update() {
            lastPos = pos.copy();
            double x = Math.cos(direction);
            double z = Math.sin(direction);

            BlockPos p = pos.getPos();
            if (world.isAirBlock(p) || world.getBlockState(p).getBlock() == DEFeatures.reactorCore) {
                while ((world.isAirBlock(p) || world.getBlockState(p).getBlock() == DEFeatures.reactorCore) && p.getY() > 0) {
                    p = p.down();
                }
            }
            else {
                while (!world.isAirBlock(p)) p = p.up();
            }

            int y = p.getY();

            if (pos.y > y + diameter) {
                fallVelocity += 0.1;
            }
            else {
                fallVelocity = 0;
            }

            pos.y -= fallVelocity;
            if (pos.y < y + diameter && fallVelocity > 0) {
                pos.y = y + diameter;
            }

            if (y + diameter > pos.y) {
                pos.y += ((y + diameter) - pos.y) * Math.max(speed, 0.1);
            }

            if (speed < 0.5 && fallVelocity == 0) {
                speed += 0.01;
            }

            if (speed > 0) {
                pos.add(x * speed, 0, z * speed);
            }

            age++;
        }
    }

    public enum ReactorState {
        INVALID(false),
        /**
         * The reactor is offline and cold.
         * In this state it is possible to add/remove fuel.
         */
        COLD(false),
        /**
         * Reactor is heating up in preparation for startup.
         */
        WARMING_UP(true),
        //AT_TEMP(true), Dont think i need this i can just have a "Can Start" check that checks the reactor is in the warm up state and temp is at minimum required startup temp.
        /**
         * Reactor is online.
         */
        RUNNING(true),
        /**
         * The reactor is shutting down..
         */
        STOPPING(true),
        /**
         * The reactor is offline but is still cooling down.
         */
        COOLING(true),
        BEYOND_HOPE(true);

        private final boolean shieldActive;

        /**
         * @param shieldActive Indicates that the reactor is in any state other that COLD or INVALID. If it is active in any way this is true.
         */
        ReactorState(boolean shieldActive) {
            this.shieldActive = shieldActive;
        }

        public boolean isShieldActive() {
            return shieldActive;
        }

        @SideOnly(Side.CLIENT)
        public String localize() {
            TextFormatting[] colours = {TextFormatting.RED, TextFormatting.DARK_AQUA, TextFormatting.LIGHT_PURPLE, TextFormatting.GREEN, TextFormatting.LIGHT_PURPLE, TextFormatting.LIGHT_PURPLE, TextFormatting.DARK_RED};
            return colours[ordinal()] + I18n.format("gui.reactor.status." + name().toLowerCase() + ".info");
        }
    }
}
