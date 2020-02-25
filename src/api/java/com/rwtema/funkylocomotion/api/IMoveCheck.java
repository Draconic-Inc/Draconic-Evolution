package com.rwtema.funkylocomotion.api;

import com.mojang.authlib.GameProfile;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface IMoveCheck {
	ActionResultType canMove(World worldObj, BlockPos pos, @Nullable GameProfile profile);
}