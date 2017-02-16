package com.brandon3055.draconicevolution.blocks.reactor.tileentity;

import com.brandon3055.brandonscore.blocks.TileEnergyBase;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.network.wrappers.SyncableBool;
import com.brandon3055.brandonscore.network.wrappers.SyncableByte;
import com.brandon3055.brandonscore.network.wrappers.SyncableEnum;
import com.brandon3055.brandonscore.network.wrappers.SyncableVec3I;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

import static com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore.COMPONENT_MAX_DISTANCE;

/**
 * Created by brandon3055 on 20/01/2017.
 */
public abstract class TileReactorComponent extends TileEnergyBase implements ITickable {

    private final SyncableVec3I coreOffset = new SyncableVec3I(new Vec3I(0, 0, 0), true, false);
    public final SyncableEnum<EnumFacing> facing = new SyncableEnum<>(EnumFacing.UP, true, false);
    public final SyncableBool isBound = new SyncableBool(false, true, false);
    public final SyncableByte rsMode = new SyncableByte((byte) 0, true, false); //TODO Make enum
    public float animRotation = 0;
    public float animRotationSpeed = 0;

    public TileReactorComponent() {
        registerSyncableObject(coreOffset);
        registerSyncableObject(facing);
        registerSyncableObject(isBound);
        registerSyncableObject(rsMode);
    }

    //region update

    @Override
    public void update() {
        detectAndSendChanges();

        if (worldObj.isRemote) {
            TileReactorCore core = tryGetCore();
            if (core != null) {
                animRotationSpeed = (float) core.animationState.value * 15F;
            }
            else {
                animRotationSpeed = 0;
            }

            animRotation += animRotationSpeed;
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
            if (!worldObj.isAirBlock(searchPos)) {
                TileEntity tile = worldObj.getTileEntity(searchPos);
                LogHelper.dev("Reactor-Comp: Try Poke Core | Found: " + tile);

                if (tile instanceof TileReactorCore && i > 1) {
                    //I want this to poke the core regardless of weather or not the core structure is already valid in case this is an energy injector. The core will decide what to do.
                    ((TileReactorCore) tile).pokeCore(this, facing.value.getOpposite());
                }
                return;
            }
        }
    }

    public void invalidate() {
        isBound.value = false;
    }
    
    //endregion ===================================

    //region Player Interaction

    public void onPlaced() {
        if (worldObj.isRemote) {
            return;
        }
        pokeCore();
    }

    public void onBroken() {
        if (worldObj.isRemote) {
            return;
        }

        TileReactorCore core = checkAndGetCore();
        if (core != null) {
            core.componentBroken(this, facing.value.getOpposite());
        }
    }

    public void onActivated(EntityPlayer player) {
        if (worldObj.isRemote) {
            return;
        }
        pokeCore();
        TileReactorCore core = checkAndGetCore();
        if (core != null) {
            core.onComponentClicked(player, this);
        }
    }
    
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
    private TileReactorCore checkAndGetCore(){
        if (!isBound.value) {
            return null;
        }
        
        TileEntity tile = worldObj.getTileEntity(getCorePos());
        if (tile instanceof TileReactorCore) {
            return (TileReactorCore) tile;
        }
        
        if (worldObj.getChunkFromBlockCoords(getCorePos()).isLoaded()) {
            invalidate();
        }
        
        return null;
    }

    public TileReactorCore tryGetCore() {
        if (!isBound.value) {
            return null;
        }

        TileEntity tile = worldObj.getTileEntity(getCorePos());
        if (tile instanceof TileReactorCore) {
            return (TileReactorCore) tile;
        }
        return null;
    }
    
    //endregion

    //region RS Modes TODO Change to enum

    public static final int RMODE_TEMP = 0;
    public static final int RMODE_TEMP_INV = 1;
    public static final int RMODE_FIELD = 2;
    public static final int RMODE_FIELD_INV = 3;
    public static final int RMODE_SAT = 4;
    public static final int RMODE_SAT_INV = 5;
    public static final int RMODE_FUEL = 6;
    public static final int RMODE_FUEL_INV = 7;

    //endregion

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return from == facing.value.getOpposite();
    }
}
