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

import java.lang.reflect.*;

import api.player.client.*;

import net.minecraft.client.entity.*;
import net.minecraft.entity.player.*;

import net.smart.moving.config.*;
import net.smart.utilities.*;

public class SmartMovingSelf extends net.smart.moving.SmartMovingSelf
{
	public SmartMovingSelf(EntityPlayer sp, SmartMovingPlayerBase playerBase)
	{
		super(sp, playerBase);
	}

	@Override
	public boolean doFlyingAnimation()
	{
		return SmartMovingOptions.hasSinglePlayerCommands && isSPCFlying(esp) || super.doFlyingAnimation();
	}

	public static boolean isSPCFlying(EntityPlayerSP entityPlayer)
	{
		if(!SmartMovingOptions.hasSinglePlayerCommands)
			return false;

		ClientPlayerBase spcPlayerBase = ((IClientPlayerAPI)entityPlayer).getClientPlayerBase(SmartMoving.SPC_ID);
		if(spcPlayerBase == null)
			return false;

		if(playerHelperField == null)
			playerHelperField = Reflect.GetField(spcPlayerBase.getClass(), new Name("ph"), false);
		if(playerHelperField == null)
			return false;

		Object playerHelper = Reflect.GetField(playerHelperField, spcPlayerBase);
		if(playerHelper == null)
			return false;

		if(flyingField == null)
			flyingField = Reflect.GetField(playerHelper.getClass(), new Name("flying"), false);
		if(flyingField == null)
			return false;

		Object isFlying = Reflect.GetField(flyingField, playerHelper);
		if(isFlying == null)
			return false;

		return isFlying instanceof Boolean && (Boolean)isFlying;
	}

	private static Field playerHelperField = null;
	private static Field flyingField = null;
}