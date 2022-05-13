package com.brandon3055.draconicevolution.blocks.energynet.rendering;

import com.brandon3055.draconicevolution.api.energy.IENetEffectTile;
import com.brandon3055.draconicevolution.network.CrystalUpdateBatcher.BatchedCrystalUpdate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Created by brandon3055 on 29/11/2016.
 * This is the base class of a 2 sided energy net FX handler built to render and update energy crystals.
 * The server side is responsible for monitoring the crystal and sending any needed updates to the client via {@link com.brandon3055.draconicevolution.network.CrystalUpdateBatcher}
 * The client side is responsible for creating and updating the render FX.
 */
@Deprecated //Want to switch to ITileFXHandler
public abstract class ENetFXHandler<T extends BlockEntity & IENetEffectTile> {

    protected final T tile;

    public ENetFXHandler(T tile) {
        this.tile = tile;
    }

    public abstract void update();

    public void detectAndSendChanges() {
    }

    public void tileUnload() {
    }

    public void updateReceived(BatchedCrystalUpdate update) {
    }

    public abstract void reloadConnections();

    public void writeToNBT(CompoundTag compound) {

    }

    public void readFromNBT(CompoundTag compound) {

    }
}
