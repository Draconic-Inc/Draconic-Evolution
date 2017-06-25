package com.brandon3055.draconicevolution.api;

/**
 * Created by Brandon on 6/03/2015.
 * This is can be used to read the energy stored in blocks that store more then MaxInt such as the Energy Core
 * <p>
 * Any tiles that implement this will automatically be given Open Computers support when DE is installed.
 * Computercraft support will also be implemented when Computercraft updates to 1.10.2.
 */
public interface IExtendedRFStorage {
    /**
     * @return Returns that current amount of energy starred in this tile as a long.
     */
    long getExtendedStorage();

    /**
     * @return Return the max energy this tile can store as a long.
     */
    long getExtendedCapacity();
}
