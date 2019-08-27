package com.brandon3055.draconicevolution.capabilities;

/**
 * Created by FoxMcloud5655 on 26/08/2019.
 */
public interface IPlayerCapabilities {
	
	/**
	 * Gets the current shield state as a short.
	 */
	public short getShieldStateRAW();
	
	/**
	 * Sets the current shield state as a short.
	 */
	public void setShieldStateRAW(short state);
	
	/**
	 * Gets the current shield state.
	 */
	public boolean getShieldState();
	
	/**
	 * Sets the current shield state.
	 */
	public void setShieldState(boolean state);
}
