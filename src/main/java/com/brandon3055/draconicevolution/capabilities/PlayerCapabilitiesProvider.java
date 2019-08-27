package com.brandon3055.draconicevolution.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

/**
 * Created by FoxMcloud5655 on 27/08/2019.
 */
public class PlayerCapabilitiesProvider implements ICapabilitySerializable<NBTBase> {

	@CapabilityInject(IPlayerCapabilities.class)
	public static final Capability<IPlayerCapabilities> PLAYER_CAP = null;
	
	private IPlayerCapabilities instance = PLAYER_CAP.getDefaultInstance();
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == PLAYER_CAP;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return capability == PLAYER_CAP ? PLAYER_CAP.<T> cast(this.instance) : null;
	}

	@Override
	public NBTBase serializeNBT() {
		return PLAYER_CAP.getStorage().writeNBT(PLAYER_CAP, this.instance, null);
	}

	@Override
	public void deserializeNBT(NBTBase nbt) {
		PLAYER_CAP.getStorage().readNBT(PLAYER_CAP, this.instance, null, nbt);
	}
}
