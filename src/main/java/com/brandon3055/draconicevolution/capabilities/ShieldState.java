package com.brandon3055.draconicevolution.capabilities;

/**
 * Created by FoxMcloud5655 on 26/08/2019.
 */
public class ShieldState implements IShieldState {
	
	private byte shieldState = 1;
	
	@Override
	public byte getShieldState() {
		return shieldState;
	}
	
	@Override
	public void setShieldState(byte state) {
		if (state > 0) shieldState = 1;
		else shieldState = 0;
	}
}
