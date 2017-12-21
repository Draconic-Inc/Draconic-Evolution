package com.brandon3055.draconicevolution.blocks.reactor.tileentity;

import codechicken.lib.data.MCDataInput;
import com.brandon3055.brandonscore.blocks.TileEnergyBase;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedEnum;
import com.brandon3055.brandonscore.lib.datamanager.ManagedInt;
import com.brandon3055.brandonscore.lib.datamanager.ManagedVec3I;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.integration.computers.ArgHelper;
import com.brandon3055.draconicevolution.integration.computers.IDEPeripheral;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

import java.util.HashMap;
import java.util.Map;

import static com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore.COMPONENT_MAX_DISTANCE;
import static com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore.MAX_TEMPERATURE;

/**
 * Created by brandon3055 on 20/01/2017.
 */
public abstract class TileReactorComponent extends TileEnergyBase implements ITickable, IDEPeripheral {

    private final ManagedVec3I coreOffset = register("coreOffset", new ManagedVec3I(new Vec3I(0, 0, 0))).saveToTile().syncViaTile().finish();
    public final ManagedEnum<EnumFacing> facing = register("facing", new ManagedEnum<>(EnumFacing.UP)).saveToTile().syncViaTile().finish();
    public final ManagedBool isBound = register("isBound", new ManagedBool(false)).saveToTile().syncViaTile().finish();
    public final ManagedEnum<RSMode> rsMode = register("rsMode", new ManagedEnum<>(RSMode.TEMP)).saveToTile().syncViaTile().finish();
    public final ManagedInt rsPower = register("rsPower", new ManagedInt(0)).saveToTile().syncViaTile().trigerUpdate().finish();
    public float animRotation = 0;
    public float animRotationSpeed = 0;
    private TileReactorCore cachedCore = null;
    public boolean coreFalureIminent = false;

    //region update

    @Override
    public void update() {
        super.update();

        if (world.isRemote) {
            TileReactorCore core = tryGetCore();
            if (core != null) {
                animRotationSpeed = core.shieldAnimationState * 15F;
                coreFalureIminent = core.reactorState.value == TileReactorCore.ReactorState.BEYOND_HOPE;
            }
            else {
                coreFalureIminent = false;
                animRotationSpeed = 0;
            }

            animRotation += animRotationSpeed;
            if (coreFalureIminent && world.rand.nextInt(10) == 0) {
                animRotation += (world.rand.nextDouble() - 0.5) * 360;
                if (world.rand.nextBoolean()) {
                    world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX() + world.rand.nextDouble(), pos.getY() + world.rand.nextDouble(), pos.getZ() + world.rand.nextDouble(), 0, 0, 0);
                }
                else {
                    world.spawnParticle(EnumParticleTypes.CLOUD, pos.getX() + world.rand.nextDouble(), pos.getY() + world.rand.nextDouble(), pos.getZ() + world.rand.nextDouble(), 0, 0, 0);
                }
            }
        }
        else {
            TileReactorCore core = getCachedCore();

            if (core != null) {
                int rs = rsMode.value.getRSSignal(core);
                if (rs != rsPower.value) {
                    rsPower.value = rs;
                    world.notifyNeighborsOfStateChange(pos, getBlockType(), true);
                }
            }
        }
    }

    //endregion

    //region============== Structure ==============

    /**
     * Called by the core itself to validate this component and bind it to the core.
     * This should only be called once the core has determined that this component is pointed at the core.
     * This ignores this components current active isBound state because if the core is calling this method then this component can not possibly be bound to any other core.!
     */
    public void bindToCore(TileReactorCore core) {
        LogHelper.dev("Reactor-Comp: Bind To Core");
        isBound.value = true;
        coreOffset.vec = getCoreOffset(core.getPos());
    }

    /**
     * Finds the core if it iss location is not already stored and pokes it. Core then validates or revalidates the structure.
     */
    public void pokeCore() {
        LogHelper.dev("Reactor-Comp: Try Poke Core");
        if (isBound.value) {
            TileReactorCore core = checkAndGetCore();
            if (core != null) {
                core.pokeCore(this, facing.value.getOpposite());
                return;
            }
        }

        LogHelper.dev("Reactor-Comp: Try Poke Core | Find");
        for (int i = 1; i < COMPONENT_MAX_DISTANCE; i++) {
            BlockPos searchPos = pos.offset(facing.value, i);
            if (!world.isAirBlock(searchPos)) {
                TileEntity tile = world.getTileEntity(searchPos);
                LogHelper.dev("Reactor-Comp: Check: " + tile);
                LogHelper.dev("Reactor-Comp: Try Poke Core | Found: " + tile);

                if (tile instanceof TileReactorCore && i > 1) {
                    //I want this to poke the core regardless of weather or not the core structure is already valid in case this is an energy injector. The core will decide what to do.
                    ((TileReactorCore) tile).pokeCore(this, facing.value.getOpposite());
                }
                return;
            }
        }
    }

    public void invalidateComponent() {
        isBound.value = false;
    }

    //endregion ===================================

    //region Player Interaction

    public void onPlaced() {
        if (world.isRemote) {
            return;
        }
        pokeCore();
    }

    public void onBroken() {
        if (world.isRemote) {
            return;
        }

        TileReactorCore core = checkAndGetCore();
        if (core != null) {
            core.componentBroken(this, facing.value.getOpposite());
        }
    }

    public void onActivated(EntityPlayer player) {
        if (world.isRemote) {
            return;
        }

        pokeCore();
        TileReactorCore core = checkAndGetCore();
        if (core != null) {
            core.onComponentClicked(player, this);
        }
    }

    public void setRSMode(EntityPlayer player, RSMode rsMode) {
        if (world.isRemote) {
            sendPacketToServer(output -> output.writeString(rsMode.name()), 0);
        }
        else {
            this.rsMode.value = rsMode;
        }
    }

    @Override
    public void receivePacketFromClient(MCDataInput data, EntityPlayerMP client, int id) {
        if (id == 0) {
            setRSMode(client, RSMode.valueOf(data.readString()));
        }
    }

//    protected boolean verifyPlayerPermission(EntityPlayer player) {
//        PlayerInteractEvent.RightClickBlock event = new PlayerInteractEvent.RightClickBlock(player, EnumHand.MAIN_HAND, null, pos, EnumFacing.UP, player.getLookVec());
//        MinecraftForge.EVENT_BUS.post(event);
//        return !event.isCanceled();
//    }

    //endregion

    //region //Logic

//    public boolean isActive() {
//        return isBound.value;
//    }
//
//    public String getRedstoneModeString() {
//        return "msg.de.reactorRSMode." + rsMode.value + ".txt";
//    }
//
//    public void changeRedstoneMode() {
//        if (rsMode.value == RMODE_FUEL_INV) {
//            rsMode.value = 0;
//        }
//        else {
//            rsMode.value++;
//        }
//    }
//
//    public int getRedstoneMode() {
//        return rsMode.value;
//    }

    //endregion

    //region Getters & Setters

    protected BlockPos getCorePos() {
        return pos.subtract(coreOffset.vec.getPos());
    }

    protected Vec3I getCoreOffset(BlockPos corePos) {
        return new Vec3I(pos.subtract(corePos));
    }

    /**
     * @return The core this component is bound to or null if not bound or core is nolonger at bound position. Invalidates the block if the core could not be found.
     */
    protected TileReactorCore checkAndGetCore() {
        if (!isBound.value) {
            LogHelper.dev("Reactor-Comp: Not Bound");
            return null;
        }

        TileEntity tile = world.getTileEntity(getCorePos());
        if (tile instanceof TileReactorCore) {
            return (TileReactorCore) tile;
        }

        if (world.getChunkFromBlockCoords(getCorePos()).isLoaded()) {
            invalidateComponent();
        }

        LogHelper.dev("Reactor-Comp: Core Connection Lost");
        return null;
    }

    public TileReactorCore tryGetCore() {
        if (!isBound.value) {
            return null;
        }

        TileEntity tile = world.getTileEntity(getCorePos());
        if (tile instanceof TileReactorCore) {
            return (TileReactorCore) tile;
        }
        return null;
    }

    protected TileReactorCore getCachedCore() {
        if (isBound.value) {
            BlockPos corePos = getCorePos();
            Chunk coreChunk = world.getChunkFromBlockCoords(corePos);

            if (!coreChunk.isLoaded()) {
                cachedCore = null;
                return null;
            }

            TileEntity tileAtPos = coreChunk.getTileEntity(corePos, Chunk.EnumCreateEntityType.CHECK);
            if (tileAtPos == null || cachedCore == null || tileAtPos != cachedCore || tileAtPos.isInvalid()) {
                TileEntity tile = world.getTileEntity(corePos);

                if (tile instanceof TileReactorCore) {
                    cachedCore = (TileReactorCore) tile;
                }
                else {
                    cachedCore = null;
                    isBound.value = false;
                }
            }
        }
        return cachedCore;
    }

    //endregion

    @Override
    public boolean canRenderBreaking() {
        return true;
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return from == facing.value.getOpposite();
    }

    //region Peripheral

    @Override
    public String getPeripheralName() {
        return "draconic_reactor";
    }

    @Override
    public String[] getMethodNames() {
        return new String[]{"getReactorInfo", "chargeReactor", "activateReactor", "stopReactor", "setFailSafe"};
    }

    @Override
    public Object[] callMethod(String method, ArgHelper args) {
        TileReactorCore reactor = getCachedCore();

        if (reactor == null) {
            return null;
        }

        if (method.equals("getReactorInfo")) {
            Map<Object, Object> map = new HashMap<Object, Object>();
            map.put("temperature", Utils.round(reactor.temperature.value, 100));
            map.put("fieldStrength", Utils.round(reactor.shieldCharge.value, 100));
            map.put("maxFieldStrength", Utils.round(reactor.maxShieldCharge.value, 100));
            map.put("energySaturation", reactor.saturation.value);
            map.put("maxEnergySaturation", reactor.maxSaturation.value);
            map.put("fuelConversion", Utils.round(reactor.convertedFuel.value, 1000));
            map.put("maxFuelConversion", reactor.reactableFuel.value + reactor.convertedFuel.value);
            map.put("generationRate", (int) reactor.generationRate.value);
            map.put("fieldDrainRate", reactor.fieldDrain.value);
            map.put("fuelConversionRate", (int) Math.round(reactor.fuelUseRate.value * 1000000D));
            map.put("status", reactor.reactorState.value.name().toLowerCase());//reactor.reactorState.value == TileReactorCore.ReactorState.COLD ? "offline" : reactor.reactorState == 1 && !reactor.canStart() ? "charging" : reactor.reactorState == 1 && reactor.canStart() ? "charged" : reactor.reactorState == 2 ? "online" : reactor.reactorState == 3 ? "stopping" : "invalid");
            map.put("failSafe", reactor.failSafeMode.value);
            return new Object[]{map};
        }
        else if (method.equals("chargeReactor")) {
            if (reactor.canCharge()) {
                reactor.chargeReactor();
                return new Object[]{true};
            }
            else return new Object[]{false};
        }
        else if (method.equals("activateReactor")) {
            if (reactor.canActivate()) {
                reactor.activateReactor();
                return new Object[]{true};
            }
            else return new Object[]{false};
        }
        else if (method.equals("stopReactor")) {
            if (reactor.canStop()) {
                reactor.shutdownReactor();
                return new Object[]{true};
            }
            else return new Object[]{false};
        }
        else if (method.equals("setFailSafe")) {
            reactor.failSafeMode.value = args.checkBoolean(0);
            return new Object[]{true};
        }
        return new Object[]{};
    }

    //endregion

    public enum RSMode {
        TEMP {
            @Override
            public int getRSSignal(TileReactorCore tile) {
                return (int) ((tile.temperature.value / MAX_TEMPERATURE) * 15D);
            }
        }, TEMP_INV {
            @Override
            public int getRSSignal(TileReactorCore tile) {
                return 15 - TEMP.getRSSignal(tile);
            }
        }, FIELD {
            @Override
            public int getRSSignal(TileReactorCore tile) {
                double value = tile.shieldCharge.value / tile.maxShieldCharge.value;
                value -= 0.05;
                value *= 1.2;
                return (int) (value * 15);
            }
        }, FIELD_INV {
            @Override
            public int getRSSignal(TileReactorCore tile) {
                return 15 - FIELD.getRSSignal(tile);
            }
        }, SAT {
            @Override
            public int getRSSignal(TileReactorCore tile) {
                return (int) (((double) tile.saturation.value / (double) tile.maxSaturation.value) * 15D);
            }
        }, SAT_INV {
            @Override
            public int getRSSignal(TileReactorCore tile) {
                return 15 - SAT.getRSSignal(tile);
            }
        }, FUEL {
            @Override
            public int getRSSignal(TileReactorCore tile) {
                double value = tile.convertedFuel.value / (tile.convertedFuel.value + tile.reactableFuel.value);
                value += 0.1;
                value = Utils.map(value, 0.1, 1, 0, 1);
                return (int) (value * 15);
            }
        }, FUEL_INV {
            @Override
            public int getRSSignal(TileReactorCore tile) {
                return 15 - FUEL.getRSSignal(tile);
            }
        };

        public abstract int getRSSignal(TileReactorCore tile);
    }
}
