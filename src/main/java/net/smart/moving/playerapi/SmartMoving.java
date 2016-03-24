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

import net.minecraft.client.entity.*;
import net.minecraft.entity.player.*;
import net.smart.moving.*;

public abstract class SmartMoving
{
	public static final String SPC_ID = "Single Player Commands";

	public static void register()
	{
		SmartMovingPlayerBase.registerPlayerBase();
		SmartMovingServerPlayerBase.registerPlayerBase();
	}

	public static IEntityPlayerSP getPlayerBase(EntityPlayer entityPlayer)
	{
		if(entityPlayer instanceof EntityPlayerSP)
			return SmartMovingPlayerBase.getPlayerBase((EntityPlayerSP)entityPlayer);
		return null;
	}

	public static IEntityPlayerMP getServerPlayerBase(EntityPlayer entityPlayer)
	{
		if(entityPlayer instanceof EntityPlayerMP)
			return SmartMovingServerPlayerBase.getPlayerBase(entityPlayer);
		return null;
	}
}