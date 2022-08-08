package com.brandon3055.draconicevolution.common.tileentities.energynet;

import cofh.api.energy.IEnergyHandler;
import com.brandon3055.draconicevolution.common.utills.EnergyStorage;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by Brandon on 10/02/2015.
 */
public interface IRemoteEnergyHandler extends IEnergyHandler {
    // todo add method for linking

    /**
     * @param player       The player binding the tiles
     * @param x            the xCoord of the other tile
     * @param y            the yCoord of the other tile
     * @param z            the zCoord of the other tile
     * @param callOtherEnd whether or not to call handleBinding in the other tile
     */
    boolean handleBinding(EntityPlayer player, int x, int y, int z, boolean callOtherEnd);

    double getCapacity();

    double getBeamX();

    double getBeamY();

    double getBeamZ();

    EnergyStorage getStorage();

    int getMaxConnections();
}
