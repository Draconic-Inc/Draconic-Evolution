package com.brandon3055.draconicevolution.api;

/**
 * Created by Brandon on 6/03/2015.
 * This is can be used to read the energy stored in blocks that store more then MaxInt such as the Energy Core
 */
public interface IExtendedRFStorage
{
	public long getExtendedStorage();

	public long getExtendedCapacity();
}
