package com.brandon3055.draconicevolution.blocks.reactor.tileentity;

import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.network.wrappers.SyncableBool;
import com.brandon3055.brandonscore.network.wrappers.SyncableByte;
import com.brandon3055.brandonscore.network.wrappers.SyncableEnum;
import com.brandon3055.brandonscore.network.wrappers.SyncableVec3I;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

/**
 * Created by brandon3055 on 20/01/2017.
 */
public abstract class TileReactorComponent extends TileBCBase implements ITickable {

    public final SyncableVec3I coreOffset = new SyncableVec3I(new Vec3I(0, 0, 0), true, false);
    public final SyncableEnum<EnumFacing> coreDir = new SyncableEnum<>(EnumFacing.UP, true, false);
    public final SyncableBool isValid = new SyncableBool(false, true, false);
    public final SyncableByte rsMode = new SyncableByte((byte) 0, true, false);

    public TileReactorComponent() {
        registerSyncableObject(coreOffset);
        registerSyncableObject(coreDir);
        registerSyncableObject(isValid);
        registerSyncableObject(rsMode);
    }

    //region update

    @Override
    public void update() {

    }

    //endregion

    //region Logic

    public boolean isActive() {
        return isValid.value;
    }

    public String getRedstoneModeString() {
        return "msg.de.reactorRSMode." + rsMode.value + ".txt";
    }

    public void changeRedstoneMode() {
        if (rsMode.value == RMODE_FUEL_INV) {
            rsMode.value = 0;
        }
        else {
            rsMode.value++;
        }
    }

    public int getRedstoneMode() {
        return rsMode.value;
    }

    //endregion

    //region Initialization & Interaction

    public abstract boolean checkForMaster();

    public void onPlaced() {
        checkForMaster();
    }

    public void onBroken() {

    }

    public void onActivated(EntityPlayer player) {

    }

    public void shutDown() {
        coreOffset.vec.set(0, 0, 0);
        isValid.value = false;
        updateBlock();
    }

    //endregion

    //region Getters & Setters

    protected BlockPos getCorePos() {
        return pos.subtract(coreOffset.vec.getPos());
    }

    protected Vec3I getCoreOffset(BlockPos corePos) {
        return new Vec3I(pos.subtract(corePos));
    }

    //endregion


//    public MultiblockHelper.TileLocation getMaster();
//
//
//
//
//
//
//
//

    //region RS Modes

    public static final int RMODE_TEMP = 0;
    public static final int RMODE_TEMP_INV = 1;
    public static final int RMODE_FIELD = 2;
    public static final int RMODE_FIELD_INV = 3;
    public static final int RMODE_SAT = 4;
    public static final int RMODE_SAT_INV = 5;
    public static final int RMODE_FUEL = 6;
    public static final int RMODE_FUEL_INV = 7;

    //endregion
}
