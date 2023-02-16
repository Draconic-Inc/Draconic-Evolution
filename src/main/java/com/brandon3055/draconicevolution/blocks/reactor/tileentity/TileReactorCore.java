package com.brandon3055.draconicevolution.blocks.reactor.tileentity;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.handlers.ProcessHandler;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.lib.datamanager.*;
import com.brandon3055.brandonscore.utils.FacingUtils;
import com.brandon3055.brandonscore.utils.HolidayHelper;
import com.brandon3055.brandonscore.utils.MathUtils;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.reactor.ProcessExplosion;
import com.brandon3055.draconicevolution.blocks.reactor.ReactorEffectHandler;
import com.brandon3055.draconicevolution.client.gui.GuiReactor;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.inventory.ContainerReactor;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ChunkHolder.FullChunkStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * Created by brandon3055 on 6/11/2016.
 */
public class TileReactorCore extends TileBCore implements MenuProvider {

    //Frame Movement
    public int frameMoveContactPoints = 0;
    public boolean isFrameMoving = false;
    public boolean moveBlocksProvided = false;
    //region =========== Structure Fields ============

    public static final int COMPONENT_MAX_DISTANCE = 8;
    public final ManagedVec3I[] componentPositions = new ManagedVec3I[6]; //Invalid position is 0, 0, 0
    private final ManagedEnum<Axis> stabilizerAxis = register(new ManagedEnum<>("stabilizer_axis", Axis.Y, DataFlags.SAVE_NBT_SYNC_TILE));
    public final ManagedBool structureValid = register(new ManagedBool("structure_valid", false, DataFlags.SAVE_NBT_SYNC_TILE));
    public final ManagedString structureError = register(new ManagedString("structure_error", "", DataFlags.SAVE_NBT_SYNC_TILE));

    private int tick = 0;
    private Map<BlockPos, Integer> blockIntrusions = new HashMap<>();

    //endregion ======================================

    //region =========== Core Logic Fields ===========

    /**
     * This is the current operational state of the reactor.
     */
    public final ManagedEnum<ReactorState> reactorState = register(new ManagedEnum<>("reactor_state", ReactorState.INVALID, DataFlags.SAVE_NBT_SYNC_TILE));

    /**
     * Remaining fuel that is yet to be consumed by the reaction.
     */
    public final ManagedDouble reactableFuel = register(new ManagedDouble("reactable_fuel", DataFlags.SAVE_BOTH_SYNC_TILE));
    /**
     * Fuel that has been converted to chaos by the reaction.
     */
    public final ManagedDouble convertedFuel = register(new ManagedDouble("converted_fuel", DataFlags.SAVE_BOTH_SYNC_TILE));
    public final ManagedDouble temperature = register(new ManagedDouble("temperature", 20D, DataFlags.SAVE_NBT_SYNC_TILE));
    public static final double MAX_TEMPERATURE = 10000;

    public final ManagedDouble shieldCharge = register(new ManagedDouble("shield_charge", DataFlags.SAVE_NBT_SYNC_TILE));
    public final ManagedDouble maxShieldCharge = register(new ManagedDouble("max_shield_charge", DataFlags.SAVE_NBT_SYNC_TILE));

    /**
     * This is how saturated the core is with energy.
     */
    public final ManagedLong saturation = register(new ManagedLong("saturation", DataFlags.SAVE_NBT_SYNC_CONTAINER));
    public final ManagedLong maxSaturation = register(new ManagedLong("max_saturation", DataFlags.SAVE_NBT_SYNC_CONTAINER));


    public final ManagedDouble tempDrainFactor = register(new ManagedDouble("temp_drain_factor", DataFlags.SAVE_NBT_SYNC_CONTAINER));
    public final ManagedDouble generationRate = register(new ManagedDouble("generation_rate", DataFlags.SAVE_NBT_SYNC_CONTAINER));
    public final ManagedInt fieldDrain = register(new ManagedInt("field_drain", DataFlags.SAVE_NBT_SYNC_CONTAINER));
    public final ManagedDouble fieldInputRate = register(new ManagedDouble("field_input_rate", DataFlags.SAVE_NBT_SYNC_CONTAINER));
    public final ManagedDouble fuelUseRate = register(new ManagedDouble("fuel_use_rate", DataFlags.SAVE_NBT_SYNC_CONTAINER));

    public final ManagedBool startupInitialized = register(new ManagedBool("startup_initialized", DataFlags.SAVE_NBT_SYNC_CONTAINER));
    public final ManagedBool failSafeMode = register(new ManagedBool("fail_safe_mode", DataFlags.SAVE_NBT_SYNC_TILE));

    //Explody Stuff!
    private ProcessExplosion explosionProcess = null;
    public final ManagedInt explosionCountdown = register(new ManagedInt("explosion_countdown", -1, DataFlags.SAVE_NBT_SYNC_CONTAINER));
    private int minExplosionDelay = 0;

    //endregion ======================================

    //region ========= Visual Control Fields =========

    public float coreAnimation = 0;
    public float shieldAnimation = 0;
    public float shieldAnimationState = 0;
    /**
     * This controls the activation (fade in/fade out) of the beam and stabilizer animations.
     */
    public final ManagedDouble shaderAnimationState = register(new ManagedDouble("shader_animation_state", 0D, DataFlags.SAVE_NBT_SYNC_TILE));
    public final ManagedDouble animExtractState = register(new ManagedDouble("anim_extract_state", 0D, DataFlags.SAVE_NBT_SYNC_TILE));
    private final ReactorEffectHandler effectHandler;

    //endregion ======================================

    public TileReactorCore(BlockPos pos, BlockState state) {
        super(DEContent.tile_reactor_core, pos, state);
        for (int i = 0; i < componentPositions.length; i++) {
            componentPositions[i] = register(new ManagedVec3I("component_position" + i, new Vec3I(0, 0, 0), DataFlags.SAVE_NBT_SYNC_TILE));
        }

        shaderAnimationState.setCCSCS();
        effectHandler = DraconicEvolution.proxy.createReactorFXHandler(this);
        dataManager.setMaxSaveInterval(10);
    }

    @Override
    public int getAccessDistanceSq() {
        return 16*16;
    }

    //region Update Logic

    @Override
    public void tick() {
//        if (explosionProcess != null) {
//            if (explosionProcess.isCalculationComplete()) {
//                explosionCountdown.set(Math.min(explosionCountdown.get(), 100));
//            }
////            explosionProcess.isDead = true;
////            explosionProcess = null;
//        }
//        reactorState.set(ReactorState.COLD);
//        temperature.set(20);

        //        if (!world.isRemote) {
////            LogHelper.dev("Temp: " + temperature.get() + ", Sat: " + saturation.get() + ", Shield: " + shieldCharge.get());
//
//            if (reactableFuel.get() == 0 || reactorState.get() == ReactorState.COLD) {
////                reactableFuel.set(8000);
////                reactorState.set(ReactorState.WARMING_UP);
//            } else if (reactorState.get() == ReactorState.WARMING_UP && saturation.get() >= maxSaturation.get() / 3) {
//                reactorState.set(ReactorState.RUNNING);
//            }e
////            shieldCharge.set(maxShieldCharge.get() / 2);
////            if (reactorState.get() == ReactorState.RUNNING || saturation.get() > maxSaturation.get() * .1) {
////                temperature.set(2000);
////                saturation.set((long) (maxSaturation.get() * .1));
////                reactorState.set(ReactorState.COOLING);
////            }
//        }



        super.tick();
        updateCoreLogic();
        frameMoveContactPoints = 0;
        isFrameMoving = false;
        moveBlocksProvided = false;

        if (level.isClientSide && effectHandler != null) {
            effectHandler.updateEffects();

            if (HolidayHelper.isAprilFools()) {
                if (inView) {
                    viewTicks++;
                    if (viewTicks > 100 && roller == null) {
                        if (level.random.nextInt(25) == 0) {
                            roller = new Roller(Vec3D.getCenter(this), level, getCoreDiameter());
                        } else {
                            viewTicks = 0;
                        }
                    }
                } else {
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
        if (level.isClientSide) {
            checkPlayerCollision();
            coreAnimation += shaderAnimationState.get();
            if (maxShieldCharge.get() == 0) {
                shaderAnimationState.set(0D);
                shieldAnimation = 0;
            } else {
                shieldAnimationState = MathHelper.clip((float) (shieldCharge.get() / maxShieldCharge.get()) * 10, 0F, 1F);
                shieldAnimation += shieldAnimationState;
            }

            if (reactorState.get() == ReactorState.BEYOND_HOPE) {
                shieldAnimationState = level.random.nextInt(10) == 0 ? 0 : 1;
                shieldAnimation += shieldAnimationState;
            }
            return;
        }

        shaderAnimationState.set(Math.min(1D, (temperature.get() - 20) / 1000D));
        animExtractState.set(0D);

        switch (reactorState.get()) {
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

                if (failSafeMode.get() && temperature.get() < 2500 && (double) saturation.get() / (double) maxSaturation.get() >= 0.99) {
                    LogHelper.dev("Reactor: Initiating Fail-Safe Shutdown!");
                    shutdownReactor();
                }

                double sat = 1D - (saturation.get() / (double) maxSaturation.get());
                animExtractState.set(Math.min(1, sat * 10));
                break;
            case STOPPING:
                updateOnlineState();
                checkBlockIntrusions();
                if (temperature.get() <= 2000) {
                    reactorState.set(ReactorState.COOLING);
                }
                break;
            case COOLING:
                updateOfflineState();
                if (temperature.get() <= 100) {
                    reactorState.set(ReactorState.COLD);
                }
                break;
            case BEYOND_HOPE:
                checkBlockIntrusions();
                updateCriticalState();
                break;
        }
    }

    /**
     * Update the reactors offline state.
     * This is responsible for things like returning the core temperature to minimum and draining remaining charge after the reactor shuts down.
     */
    private void updateOfflineState() {
        if (temperature.get() > 20) {
            temperature.subtract(0.5);
        }
        if (shieldCharge.get() > 0) {
            shieldCharge.subtract(maxShieldCharge.get() * 0.0005 * level.random.nextDouble());
        } else if (shieldCharge.get() < 0) {
            shieldCharge.zero();
        }
        if (saturation.get() > 0) {
            saturation.subtract((int) (maxSaturation.get() * 0.000002D * level.random.nextDouble()));
        } else if (saturation.get() < 0) {
            saturation.zero();
        }
    }

    /**
     * This method is fired when the reactor enters the warm up state.
     * The first time this method is fired if initializes all of the reactors key fields.
     */
    private void initializeStartup() {
        if (!startupInitialized.get()) {
            double totalFuel = reactableFuel.get() + convertedFuel.get();
            maxShieldCharge.set(totalFuel * 96.45061728395062 * 100);
            maxSaturation.set((int) (totalFuel * 96.45061728395062 * 1000));

            if (saturation.get() > maxSaturation.get()) {
                saturation.set(maxSaturation.get());
            }

            if (shieldCharge.get() > maxShieldCharge.get()) {
                shieldCharge.set(maxShieldCharge.get());
            }

            startupInitialized.set(true);
        }
    }

    private void updateOnlineState() {
        double coreSat = (double) saturation.get() / (double) maxSaturation.get();         //1 = Max Saturation
        double negCSat = (1D - coreSat) * 99D;                                             //99 = Min Saturation. I believe this tops out at 99 because at 100 things would overflow and break.
        double temp50 = Math.min((temperature.get() / MAX_TEMPERATURE) * 50, 99);          //50 = Max Temp. Why? TBD
        double tFuel = convertedFuel.get() + reactableFuel.get();                          //Total Fuel.
        double convLVL = ((convertedFuel.get() / tFuel) * 1.3D) - 0.3D;                    //Conversion Level sets how much the current conversion level boosts power gen. Range: -0.3 to 1.0

        //region ============= Temperature Calculation =============

        double tempOffset = 444.7;    //Adjusts where the temp falls to at 100% saturation

        //The exponential temperature rise which increases as the core saturation goes down
        double tempRiseExpo = (negCSat * negCSat * negCSat) / (100 - negCSat) + tempOffset; //This is just terrible... I cant believe i wrote this stuff...

        //This is used to add resistance as the temp rises because the hotter something gets the more energy it takes to get it hotter
        double tempRiseResist = (temp50 * temp50 * temp50 * temp50) / (100 - temp50);       //^ Mostly Correct... The hotter an object gets the faster it dissipates heat into its surroundings to the more energy it takes to compensate for that energy loss.

        //This puts all the numbers together and gets the value to raise or lower the temp by this tick. This is dealing with very big numbers so the result is divided by 10000
        double riseAmount = (tempRiseExpo - (tempRiseResist * (1D - convLVL)) + convLVL * 1000) / 10000;

        //Apply energy calculations.
        if (reactorState.get() == ReactorState.STOPPING && convLVL < 1) {
            if (temperature.get() <= 2001) {
                reactorState.set(ReactorState.COOLING);
                startupInitialized.set(false);
                return;
            }
            if (saturation.get() >= maxSaturation.get() * 0.99D && reactableFuel.get() > 0D) {
                temperature.subtract(1D - convLVL);
            } else {
                temperature.add(riseAmount * 10);
            }
        } else {
            temperature.add(riseAmount * 10);
        }

//        temperature.value = 18000;

        //endregion ================================================

        //region ============= Energy Calculation =============

        int baseMaxRFt = (int) ((maxSaturation.get() / 1000D) * DEConfig.reactorOutputMultiplier * 1.5D * 10);
        int maxRFt = (int) (baseMaxRFt * (1D + (convLVL * 2)));
        generationRate.set((1D - coreSat) * maxRFt);
        saturation.add((int) generationRate.get());

        //endregion ===========================================

        //region ============= Shield Calculation =============

        tempDrainFactor.set(temperature.get() > 8000 ? 1 + ((temperature.get() - 8000) * (temperature.get() - 8000) * 0.0000025) : temperature.get() > 2000 ? 1 : temperature.get() > 1000 ? (temperature.get() - 1000) / 1000 : 0);
//        double drain = Math.min(tempDrainFactor.value * Math.max(0.01, (1D - convLVL)) * (baseMaxRFt / 10.923556), Double.MAX_VALUE);
        fieldDrain.set((int) Math.min(tempDrainFactor.get() * Math.max(0.01, (1D - coreSat)) * (baseMaxRFt / 10.923556), (double) Integer.MAX_VALUE)); //<(baseMaxRFt/make smaller to increase field power drain)

        double fieldNegPercent = 1D - (shieldCharge.get() / maxShieldCharge.get());
        fieldInputRate.set(fieldDrain.get() / fieldNegPercent);
        shieldCharge.subtract(Math.min(fieldDrain.get(), shieldCharge.get()));

        //endregion ===========================================

        //region ============== Fuel Calculation ==============

        fuelUseRate.set(tempDrainFactor.get() * (1D - coreSat) * (0.001 * DEConfig.reactorFuelUsageMultiplier * 5)); //<Last number is base fuel usage rate
        if (reactableFuel.get() > 0) {
            convertedFuel.add(fuelUseRate.get());
            reactableFuel.subtract(fuelUseRate.get());
        }

        //endregion ===========================================

        //region Explosion
        if ((shieldCharge.get() <= 0) && temperature.get() > 2000 && reactorState.get() != ReactorState.BEYOND_HOPE) {
            reactorState.set(ReactorState.BEYOND_HOPE);
            for (int i = 0; i < componentPositions.length; i++) {
                ManagedVec3I v = componentPositions[i];
                if (v.get().sum() > 0) {
                    BlockPos p = getOffsetPos(v.get()).relative(Direction.from3DDataValue(i).getOpposite());
                    level.explode(null, p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5, 4, true, Explosion.BlockInteraction.DESTROY);
                }
            }
        }
        //endregion ======
    }

    public void updateCriticalState() {
        if (!(level instanceof ServerLevel)) {
            return;
        }

        shieldCharge.set((double) level.random.nextInt(Math.max(1, (int) (maxShieldCharge.get() * 0.01))));
        animExtractState.set(1D);
        temperature.set(MathHelper.approachExp(temperature.get(), MAX_TEMPERATURE * 1.2, 0.0005));

        if (DEConfig.disableLargeReactorBoom) {
            if (explosionCountdown.get() == -1) {
                explosionCountdown.set(1200 + level.random.nextInt(2400));
            }

            if (explosionCountdown.dec() <= 0) {
                minimalBoom();
            }

            return;
        }

        if (explosionProcess == null) {
            double radius = MathUtils.map(convertedFuel.get() + reactableFuel.get(), 144, 10368, 50D, 350D) * DEConfig.reactorExplosionScale;
            explosionProcess = new ProcessExplosion(worldPosition, (int) radius, (ServerLevel) level, -1);
            ProcessHandler.addProcess(explosionProcess);
            explosionCountdown.set(-1);
            minExplosionDelay = 1200 + level.random.nextInt(2400);
            return;
        }

        minExplosionDelay--;

        if (!explosionProcess.isCalculationComplete()) {
            return;
        }

        LogHelper.dev(explosionCountdown.get() / 20);

        if (explosionCountdown.get() == -1) {
            explosionCountdown.set((60 * 20) + Math.max(0, minExplosionDelay));
        }

        if (explosionCountdown.dec() <= 0) {
            explosionProcess.detonate();
            level.removeBlock(worldPosition, false);
        }
    }

    public boolean canCharge() {
        if (!level.isClientSide && !validateStructure()) {
            return false;
        } else if (reactorState.get() == ReactorState.BEYOND_HOPE) {
            return false;
        }

        return (reactorState.get() == ReactorState.COLD || reactorState.get() == ReactorState.COOLING) && reactableFuel.get() + convertedFuel.get() >= 144;
    }

    public boolean canActivate() {
        if (!level.isClientSide && !validateStructure()) {
            return false;
        } else if (reactorState.get() == ReactorState.BEYOND_HOPE) {
            return false;
        }

        return (reactorState.get() == ReactorState.WARMING_UP || reactorState.get() == ReactorState.STOPPING) && temperature.get() >= 2000 && ((saturation.get() >= maxSaturation.get() / 2 && shieldCharge.get() >= maxShieldCharge.get() / 2) || reactorState.get() == ReactorState.STOPPING);
    }

    public boolean canStop() {
        if (reactorState.get() == ReactorState.BEYOND_HOPE) {
            return false;
        }

        return reactorState.get() == ReactorState.RUNNING || reactorState.get() == ReactorState.WARMING_UP;
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

    public static final byte ID_CHARGE = 0;
    public static final byte ID_ACTIVATE = 1;
    public static final byte ID_SHUTDOWN = 2;
    public static final byte ID_FAIL_SAFE = 3;

    public void chargeReactor() {
        if (level.isClientSide) {
            LogHelper.dev("Reactor: Start Charging");
            sendPacketToServer(output -> output.writeByte(ID_CHARGE), 0);
        } else if (!level.getServer().isSameThread()) {
            level.getServer().executeBlocking(this::chargeReactor);
        } else if (canCharge()) {
            LogHelper.dev("Reactor: Start Charging");
            reactorState.set(ReactorState.WARMING_UP);
        }
    }

    public void activateReactor() {
        if (level.isClientSide) {
            LogHelper.dev("Reactor: Activate");
            sendPacketToServer(output -> output.writeByte(ID_ACTIVATE), 0);
        } else if (!level.getServer().isSameThread()) {
            level.getServer().executeBlocking(this::activateReactor);
        } else if (canActivate()) {
            LogHelper.dev("Reactor: Activate");
            reactorState.set(ReactorState.RUNNING);
        }
    }

    public void shutdownReactor() {
        if (level.isClientSide) {
            LogHelper.dev("Reactor: Shutdown");
            sendPacketToServer(output -> output.writeByte(ID_SHUTDOWN), 0);
        } else if (canStop()) {
            LogHelper.dev("Reactor: Shutdown");
            reactorState.set(ReactorState.STOPPING);
        }
    }

    public void toggleFailSafe() {
        if (level.isClientSide) {
            sendPacketToServer(output -> output.writeByte(ID_FAIL_SAFE), 0);
        } else {
            failSafeMode.set(!failSafeMode.get());
        }
    }

    public void onComponentClicked(Player player, TileReactorComponent component) {
        if (!level.isClientSide) {
            NetworkHooks.openGui((ServerPlayer) player, this, worldPosition);
            sendPacketToClient((ServerPlayer) player, output -> output.writePos(component.getBlockPos()), 1);
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int currentWindowIndex, Inventory playerInventory, Player player) {
        return new ContainerReactor(DEContent.container_reactor, currentWindowIndex, player.getInventory(), this);
    }

    @Override
    public void receivePacketFromClient(MCDataInput data, ServerPlayer client, int id) {
        //Quick work around for the fact that due to recent changes to packet security in BCore client>server packets must go through the container for the tile the player is accessing.
        if (id == 99) {
            TileReactorComponent.RSMode mode = TileReactorComponent.RSMode.valueOf(data.readString());
            BlockPos pos = data.readPos();
            BlockEntity tile = level.getBlockEntity(pos);
            if (tile instanceof TileReactorComponent && ((TileReactorComponent) tile).tryGetCore() == this) {
                ((TileReactorComponent) tile).setRSMode(client, mode);
            }
            return;
        }

        byte func = data.readByte();
        if (id == 0 && func == ID_CHARGE) {
            chargeReactor();
        } else if (id == 0 && func == ID_ACTIVATE) {
            activateReactor();
        } else if (id == 0 && func == ID_SHUTDOWN) {
            shutdownReactor();
        } else if (id == 0 && func == ID_FAIL_SAFE) {
            toggleFailSafe();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void receivePacketFromServer(MCDataInput data, int id) {
        if (id == 1) {
            BlockPos pos = data.readPos();
            BlockEntity tile = level.getBlockEntity(pos);
            Screen screen = Minecraft.getInstance().screen;
            if (tile instanceof TileReactorComponent && screen instanceof GuiReactor) {
                ((GuiReactor) screen).component = (TileReactorComponent) tile;
            }
        }
    }

    private void checkPlayerCollision() {
        Player player = BrandonsCore.proxy.getClientPlayer();
        double distance = Math.min(Utils.getDistance(new Vec3D(player).add(0, player.getEyeHeight(), 0), Vec3D.getCenter(worldPosition)), Utils.getDistance(new Vec3D(player), Vec3D.getCenter(worldPosition)));
        if (distance < (getCoreDiameter() / 2) + 0.5 && !player.isSpectator()) {
            double dMod = 1D - (distance / Math.max(0.1, (getCoreDiameter() / 2) + 0.5));
            double offsetX = player.getX() - worldPosition.getX() + 0.5;
            double offsetY = player.getY() - worldPosition.getY() + 0.5;
            double offsetZ = player.getZ() - worldPosition.getZ() + 0.5;
            double m = 1D * dMod;
            player.push(offsetX * m, offsetY * m, offsetZ * m);
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
    public void pokeCore(TileReactorComponent component, Direction pokeFrom) {
        LogHelper.dev("Reactor: Core Poked, StructValid: " + structureValid);
        if (structureValid.get()) {
            //If the component is an unbound injector and there is no component bound on the same side then bind it.
            if (component instanceof TileReactorInjector && !component.isBound.get()) {
                BlockEntity tile = level.getBlockEntity(getOffsetPos(componentPositions[pokeFrom.get3DDataValue()].get()));
                if (tile == this) {
                    componentPositions[pokeFrom.get3DDataValue()].set(getOffsetVec(component.getBlockPos()));
                    component.bindToCore(this);
                    LogHelper.dev("Reactor: Injector Added!");
                }
            }

            if (!validateStructure()) {
                structureValid.set(false);
                attemptInitialization();
            }
        } else {
            attemptInitialization();
        }
    }

    /**
     * Called when a component is physically broken
     */
    public void componentBroken(TileReactorComponent component, Direction componentSide) {
        if (!structureValid.get() || reactorState.get() == ReactorState.BEYOND_HOPE) {
            return;
        }

        if (component instanceof TileReactorInjector) {
            LogHelper.dev("Reactor: Component broken! (Injector)");
            BlockEntity tile = level.getBlockEntity(getOffsetPos(componentPositions[componentSide.get3DDataValue()].get()));

            if (tile == component || tile == null) {
                LogHelper.dev("Reactor: -Removed");
                componentPositions[componentSide.get3DDataValue()].get().set(0, 0, 0);
            }
        } else if (reactorState.get() != ReactorState.COLD) {
            LogHelper.dev("Reactor: Component broken, Structure Invalidated (Unsafe!!!!)");
            if (temperature.get() > 2000) {
                reactorState.set(ReactorState.BEYOND_HOPE);
            } else if (temperature.get() >= 350) {
                minimalBoom();
            } else {
                reactorState.set(ReactorState.INVALID);
            }
            structureValid.set(false);
        } else {
            LogHelper.dev("Reactor: Component broken, Structure Invalidated (Safe)");
            structureValid.set(false);
            reactorState.set(ReactorState.INVALID);
        }
    }

    public void checkBlockIntrusions() {
        if (!(level instanceof ServerLevel)) {
            return;
        }

        if (tick % 100 == 0) {
            double rad = (getCoreDiameter() * 1.05) / 2;
            Iterable<BlockPos> inRange = BlockPos.betweenClosed(worldPosition.offset(-rad, -rad, -rad), worldPosition.offset(rad + 1, rad + 1, rad + 1));

            for (BlockPos p : inRange) {
                if (p.equals(worldPosition) || Utils.getDistance(p.getX(), p.getY(), p.getZ(), worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()) - 0.5 >= rad) {
                    continue;
                }

                if (!level.isEmptyBlock(p) && !blockIntrusions.containsKey(p)) {
                    blockIntrusions.put(p.immutable(), 0);
                }
            }
        }

        if (blockIntrusions.size() > 0) {
            Iterator<Map.Entry<BlockPos, Integer>> i = blockIntrusions.entrySet().iterator();

            while (i.hasNext()) {
                Map.Entry<BlockPos, Integer> entry = i.next();
                final Vec3D iPos = new Vec3D(entry.getKey());

                if (level.random.nextInt(10) == 0) {
                    ((ServerLevel) level).sendParticles(ParticleTypes.FLAME, iPos.x + 0.5, iPos.y + 0.5, iPos.z + 0.5, 5, 0.5, 0.5, 0.5, 0.01D);
                }

                entry.setValue(entry.getValue() + 1);
                if (entry.getValue() > 100) {
                    i.remove();
                    level.levelEvent(2001, entry.getKey(), Block.getId(level.getBlockState(entry.getKey())));
                    level.removeBlock(entry.getKey(), false);
                }
            }

            if (tick % 20 == 0 || level.random.nextInt(40) == 0) {
                level.playSound(null, worldPosition, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1, 0.9F + level.random.nextFloat() * 0.2F);
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

        structureValid.set(true);
        if (reactorState.get() == ReactorState.INVALID) {
            if (temperature.get() <= 100) {
                reactorState.set(ReactorState.COLD);
            } else {
                reactorState.set(ReactorState.COOLING);
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
        for (Direction facing : Direction.values()) {
            componentPositions[facing.get3DDataValue()].get().set(0, 0, 0);
            for (int i = 4; i < COMPONENT_MAX_DISTANCE; i++) {
                BlockPos searchPos = worldPosition.relative(facing, i);

                if (!level.isEmptyBlock(searchPos)) {
                    BlockEntity searchTile = level.getBlockEntity(searchPos);
                    LogHelper.dev("Reactor: -Found: " + searchTile);

                    if (searchTile instanceof TileReactorComponent && ((TileReactorComponent) searchTile).facing.get() == facing.getOpposite()) {
                        LogHelper.dev("Set " + facing.get3DDataValue() + " " + getOffsetVec(searchPos));
                        componentPositions[facing.get3DDataValue()].set(getOffsetVec(searchPos));
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
            for (Direction facing : FacingUtils.getFacingsAroundAxis(axis)) {
                BlockEntity tile = level.getBlockEntity(getOffsetPos(componentPositions[facing.get3DDataValue()].get()));
                //The facing check should not be needed here but does not heart to be to careful.
                if (!(tile instanceof TileReactorStabilizer && ((TileReactorStabilizer) tile).facing.get() == facing.getOpposite())) {
                    axisValid = false;
                    break;
                }
            }

            if (axisValid) {
                stabilizerAxis.set(axis);
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
            BlockEntity tile = level.getBlockEntity(getOffsetPos(componentPositions[i].get()));
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
        for (Direction facing : FacingUtils.getFacingsAroundAxis(stabilizerAxis.get())) {
            BlockPos pos = getOffsetPos(componentPositions[facing.get3DDataValue()].get());
            if (!Utils.isAreaLoaded(level, pos, FullChunkStatus.TICKING)) {
                return true;
            }

            BlockEntity tile = level.getBlockEntity(pos);
            LogHelper.dev("Reactor: Validate Stabilizer: " + tile);
            if (!(tile instanceof TileReactorStabilizer) || !((TileReactorStabilizer) tile).getCorePos().equals(this.worldPosition)) {
                LogHelper.dev("Reactor: Structure Validation Failed!!!");
                return false;
            }

            for (ManagedVec3I vec : componentPositions) {
                pos = getOffsetPos(vec.get());
                tile = level.getBlockEntity(pos);

                if (tile instanceof TileReactorInjector && !((TileReactorInjector) tile).isBound.get()) {
                    ((TileReactorInjector) tile).bindToCore(this);
                }

                if (tile instanceof TileReactorComponent && ((TileReactorComponent) tile).getCorePos().equals(this.worldPosition) && !((TileReactorComponent) tile).isBound.get()) {
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
        BlockState lava = Blocks.LAVA.defaultBlockState();
        //TODO pyrotheum
//        LogHelper.dev(FluidRegistry.isFluidRegistered("pyrotheum"));
//        if (FluidRegistry.isFluidRegistered("pyrotheum")) {
//            Fluid pyro = FluidRegistry.getFluid("pyrotheum");
//            if (pyro.canBePlacedInWorld()) {
//                lava = pyro.getBlock().getDefaultState();
//            }
//        }

        Vec3D vec = Vec3D.getCenter(worldPosition);
        level.removeBlock(worldPosition, false);
        level.explode(null, vec.x, vec.y, vec.z, 8, Explosion.BlockInteraction.BREAK);
        int c = 25 + level.random.nextInt(25);
        for (int i = 0; i < c; i++) {
            FallingBlockEntity entity = FallingBlockEntity.fall(level, vec.getPos(), lava);
            entity.time = 1;
            entity.dropItem = false;
            double vMod = 0.5 + (2 * level.random.nextDouble());
            entity.push((level.random.nextDouble() - 0.5) * vMod, (level.random.nextDouble() / 1.5) * vMod, (level.random.nextDouble() - 0.5) * vMod);
            level.addFreshEntity(entity);
        }
    }

    //region Getters & Setters

    private BlockPos getOffsetPos(Vec3I vec) {
        return worldPosition.subtract(vec.getPos());
    }

    private Vec3I getOffsetVec(BlockPos offsetPos) {
        return new Vec3I(worldPosition.subtract(offsetPos));
    }

    public TileReactorComponent getComponent(Direction facing) {
        BlockEntity tile = level.getBlockEntity(getOffsetPos(componentPositions[facing.get3DDataValue()].get()));

        if (tile instanceof TileReactorComponent && ((TileReactorComponent) tile).facing.get() == facing.getOpposite()) {
            return (TileReactorComponent) tile;
        }

        return null;
    }

    //endregion

    //endregion ############################################

    //region ################# Other Logic ##################

    public long injectEnergy(long energy) {
        long received = 0;
        if (reactorState.get() == ReactorState.WARMING_UP) {
            if (!startupInitialized.get()) {
                return 0;
            }
            if (shieldCharge.get() < (maxShieldCharge.get() / 2)) {
                received = Math.min(energy, (int) (maxShieldCharge.get() / 2) - (int) shieldCharge.get() + 1);
                shieldCharge.add((double) received);
                if (shieldCharge.get() > (maxShieldCharge.get() / 2)) {
                    shieldCharge.set(maxShieldCharge.get() / 2);
                }
            } else if (saturation.get() < (maxSaturation.get() / 2)) {
                received = Math.min(energy, (maxSaturation.get() / 2) - saturation.get());
                saturation.add(received);
            } else if (temperature.get() < 2000) {
                received = energy;
                temperature.add((double) received / (1000D + (reactableFuel.get() * 10)));
                if (temperature.get() > 2500) {
                    temperature.set(2500D);
                }
            }
        } else if (reactorState.get() == ReactorState.RUNNING || reactorState.get() == ReactorState.STOPPING) {
            double tempFactor = 1;

            //If the temperature goes past 15000 force the shield to drain by the time it hits 25000 regardless of input.
            if (temperature.get() > 15000) {
                tempFactor = 1D - Math.min(1, (temperature.get() - 15000D) / 10000D);
            }

            shieldCharge.add(Math.min((energy * (1D - (shieldCharge.get() / maxShieldCharge.get()))), maxShieldCharge.get() - shieldCharge.get()) * tempFactor);
            if (shieldCharge.get() > maxShieldCharge.get()) {
                shieldCharge.set(maxShieldCharge.get());
            }

            return energy;
        }
        return received;
    }

    //endregion #############################################

    //region Rendering

    @Override
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    public double getCoreDiameter() {
        double volume = (reactableFuel.get() + convertedFuel.get()) / 1296D;
        volume *= 1 + (temperature.get() / MAX_TEMPERATURE) * 10D;
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
        private Level world;
        private double diameter;
        public double fallVelocity = 0;
        public double speed = 0;
        public int age = 0;

        public Roller(Vec3D pos, Level world, double diameter) {
            this.pos = pos;
            this.lastPos = pos.copy();
            this.direction = world.random.nextDouble() * Math.PI * 2;
            this.world = world;
            this.diameter = (diameter / 2) + 1;
            this.speed = -0.3;
        }

        public void update() {
            lastPos = pos.copy();
            double x = Math.cos(direction);
            double z = Math.sin(direction);

            BlockPos p = pos.getPos();
            if (world.isEmptyBlock(p) || world.getBlockState(p).getBlock() == DEContent.reactor_core) {
                while ((world.isEmptyBlock(p) || world.getBlockState(p).getBlock() == DEContent.reactor_core) && p.getY() > 0) {
                    p = p.below();
                }
            } else {
                while (!world.isEmptyBlock(p)) p = p.above();
            }

            int y = p.getY();

            if (pos.y > y + diameter) {
                fallVelocity += 0.1;
            } else {
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

        @OnlyIn(Dist.CLIENT)
        public String localize() {
            ChatFormatting[] colours = {ChatFormatting.RED, ChatFormatting.DARK_AQUA, ChatFormatting.LIGHT_PURPLE, ChatFormatting.GREEN, ChatFormatting.LIGHT_PURPLE, ChatFormatting.LIGHT_PURPLE, ChatFormatting.DARK_RED};
            return colours[ordinal()] + I18n.get("gui.reactor.status." + name().toLowerCase(Locale.ENGLISH) + ".info");
        }
    }
}
