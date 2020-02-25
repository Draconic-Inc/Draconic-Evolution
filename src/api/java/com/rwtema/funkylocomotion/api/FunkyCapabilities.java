package com.rwtema.funkylocomotion.api;

import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.items.IItemHandler;

import java.util.concurrent.Callable;

public class FunkyCapabilities {
	@CapabilityInject(IMoveCheck.class)
	public static Capability<IMoveCheck> MOVE_CHECK = null;

	@CapabilityInject(IStickyBlock.class)
	public static Capability<IStickyBlock> STICKY_BLOCK = null;

	@CapabilityInject(ISlipperyBlock.class)
	public static Capability<ISlipperyBlock> SLIPPERY_BLOCK = null;

	@CapabilityInject(IAdvStickyBlock.class)
	public static Capability<IAdvStickyBlock> ADV_STICKY_BLOCK = null;

	@CapabilityInject(IItemHandler.class)
	public static <T> void initializeCapabilities(Capability<T> ignore) {
		register(IMoveCheck.class, () -> (worldObj, pos, profile) -> ActionResultType.PASS);
		register(IStickyBlock.class, () -> (world, pos, side) -> false);
		register(ISlipperyBlock.class, () -> (world, pos, dir) -> false);
		register(IAdvStickyBlock.class, () -> (world, pos) -> ImmutableList.of(pos));
	}

	private static <T> void register(Class<T> clazz, Callable<T> callable) {
		CapabilityManager.INSTANCE.register(clazz, new Capability.IStorage<T>() {

			@Override
			public INBT writeNBT(Capability<T> capability, T instance, Direction side) {
				return new ByteNBT((byte) 0);
			}

			@Override
			public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {

			}
		}, callable);
	}

}