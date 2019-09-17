package com.brandon3055.draconicevolution.capabilities;

/**
 * Created by FoxMcloud5655 on 26/08/2019.
 */
public class ShieldState implements IShieldState {
	
	private boolean shieldState = true;
	
	@Override
	public boolean getShieldState() {
		return shieldState;
	}
	
	@Override
	public void setShieldState(boolean state) {
		shieldState = state;
	}
}
