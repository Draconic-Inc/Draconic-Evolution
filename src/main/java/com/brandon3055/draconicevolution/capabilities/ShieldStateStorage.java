package com.brandon3055.draconicevolution.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

/**
 * Created by FoxMcloud5655 on 26/08/2019.
 */
public class ShieldStateStorage implements IStorage<IShieldState> {

	@Override
	public NBTBase writeNBT(Capability capability, IShieldState instance, EnumFacing side) {
		return new NBTTagShort(instance.getShieldStateRAW());
	}

	@Override
	public void readNBT(Capability capability, IShieldState instance, EnumFacing side, NBTBase nbt) {
		instance.setShieldStateRAW(((NBTTagShort)nbt).getShort());
	}
}
