package com.brandon3055.draconicevolution.api;

/**
 * Created by Brandon on 6/03/2015.
 */
public interface IExtendedRFStorage {

    /**
     * I will probably leave these here forever but i highly recommend switching as i may remove then at some point.
     */
    @Deprecated
    double getEnergyStored();

    @Deprecated
    double getMaxEnergyStored();

    long getExtendedStorage();

    long getExtendedCapacity();
}
