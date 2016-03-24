// ==================================================================
// This file is part of Smart Moving.
//
// Smart Moving is free software: you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation, either version 3 of the
// License, or (at your option) any later version.
//
// Smart Moving is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Smart Moving. If not, see <http://www.gnu.org/licenses/>.
// ==================================================================

package net.smart.moving.playerapi;

import api.player.client.*;

import net.minecraft.block.material.*;
import net.minecraft.client.*;
import net.minecraft.client.entity.*;
import net.minecraft.entity.player.EntityPlayer.*;
import net.minecraft.nbt.*;

import net.smart.moving.*;

public class SmartMovingPlayerBase extends ClientPlayerBase implements IEntityPlayerSP
{
	public static void registerPlayerBase()
	{
		ClientPlayerAPI.register(SmartMovingInfo.ModName, SmartMovingPlayerBase.class);
	}

	public static SmartMovingPlayerBase getPlayerBase(EntityPlayerSP player)
	{
		return (SmartMovingPlayerBase)((IClientPlayerAPI)player).getClientPlayerBase(SmartMovingInfo.ModName);
	}

	public SmartMovingPlayerBase(ClientPlayerAPI playerApi)
	{
		super(playerApi);
		moving = new SmartMovingSelf(player, this);
	}

	@Override
	public void beforeMoveEntity(double d, double d1, double d2)
	{
		moving.beforeMoveEntity(d, d1, d2);
	}

	@Override
	public void afterMoveEntity(double d, double d1, double d2)
	{
		moving.afterMoveEntity(d, d1, d2);
	}

	@Override
	public void localMoveEntity(double d, double d1, double d2)
	{
		super.moveEntity(d, d1, d2);
	}

	@Override
	public void beforeSleepInBedAt(int i, int j, int k)
	{
		moving.beforeSleepInBedAt(i, j, k);
	}

	@Override
	public EnumStatus localSleepInBedAt(int i, int j, int k)
	{
		return super.sleepInBedAt(i, j, k);
	}

	@Override
	public float getBrightness(float f)
	{
		return moving.getBrightness(f);
	}

	@Override
	public float localGetBrightness(float f)
	{
		return super.getBrightness(f);
	}

	@Override
	public int getBrightnessForRender(float f)
	{
		return moving.getBrightnessForRender(f);
	}

	@Override
	public int localGetBrightnessForRender(float f)
	{
		return super.getBrightnessForRender(f);
	}

	@Override
	public boolean pushOutOfBlocks(double d, double d1, double d2)
	{
		return moving.pushOutOfBlocks(d, d1, d2);
	}

	@Override
	public void beforeOnUpdate()
	{
		moving.beforeOnUpdate();
	}

	@Override
	public void afterOnUpdate()
	{
		moving.afterOnUpdate();
	}

	@Override
	public void beforeOnLivingUpdate()
	{
		moving.beforeOnLivingUpdate();
	}

	@Override
	public void afterOnLivingUpdate()
	{
		moving.afterOnLivingUpdate();
	}

	@Override
	public boolean getSleepingField()
	{
		return playerAPI.getSleepingField();
	}

	@Override
	public boolean getIsInWebField()
	{
		return playerAPI.getIsInWebField();
	}

	@Override
	public void setIsInWebField(boolean newIsInWeb)
	{
		playerAPI.setIsInWebField(newIsInWeb);
	}

	@Override
	public boolean getIsJumpingField()
	{
		return playerAPI.getIsJumpingField();
	}

	@Override
	public Minecraft getMcField()
	{
		return playerAPI.getMcField();
	}

	@Override
	public void moveEntityWithHeading(float f, float f1)
	{
		moving.moveEntityWithHeading(f, f1);
	}

	@Override
	public boolean canTriggerWalking()
	{
		return moving.canTriggerWalking();
	}

	@Override
	public boolean isOnLadder()
	{
		return moving.isOnLadderOrVine();
	}

	@Override
	public SmartMovingSelf getMoving()
	{
		return moving;
	}

	@Override
	public void updateEntityActionState()
	{
		moving.updateEntityActionState(false);
	}

	@Override
	public void localUpdateEntityActionState()
	{
		super.updateEntityActionState();
	}

	@Override
	public void setIsJumpingField(boolean flag)
	{
		playerAPI.setIsJumpingField(flag);
	}

	@Override
	public void setMoveForwardField(float f)
	{
		player.moveForward = f;
	}

	@Override
	public void setMoveStrafingField(float f)
	{
		player.moveStrafing = f;
	}

	@Override
	public boolean isInsideOfMaterial(Material material)
	{
		return moving.isInsideOfMaterial(material);
	}

	@Override
	public boolean localIsInsideOfMaterial(Material material)
	{
		return super.isInsideOfMaterial(material);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nBTTagCompound)
	{
		moving.writeEntityToNBT(nBTTagCompound);
	}

	@Override
	public void localWriteEntityToNBT(NBTTagCompound nBTTagCompound)
	{
		super.writeEntityToNBT(nBTTagCompound);
	}

	@Override
	public boolean isSneaking()
	{
		return moving.isSneaking();
	}

	@Override
	public boolean localIsSneaking()
	{
		return super.isSneaking();
	}

	@Override
	public float getFOVMultiplier()
	{
		return moving.getFOVMultiplier();
	}

	@Override
	public float localGetFOVMultiplier()
	{
		return playerAPI.localGetFOVMultiplier();
	}

	@Override
	public void beforeSetPositionAndRotation(double d, double d1, double d2, float f, float f1)
	{
		moving.beforeSetPositionAndRotation();
	}

	@Override
	public void beforeGetSleepTimer()
	{
		moving.beforeGetSleepTimer();
	}

	@Override
	public void jump()
	{
		moving.jump();
	}

	public SmartMovingSelf moving;
}