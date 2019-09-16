package com.brandon3055.draconicevolution.capabilities;

/**
 * Created by FoxMcloud5655 on 26/08/2019.
 */
public class ShieldState implements IShieldState {
	
	private short shieldState = 1;

	@Override
	public short getShieldStateRAW() {
		return shieldState;
	}
	
	@Override
	public boolean getShieldState() {
		return shieldState > 0;
	}

	@Override
	public void setShieldStateRAW(short state) {
		if (state > 0) shieldState = 1;
		else shieldState = 0;
	}
	
	@Override
	public void setShieldState(boolean state) {
		setShieldStateRAW((short)(state ? 1 : 0));
	}
}
