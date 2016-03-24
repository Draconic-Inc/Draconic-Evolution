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

import net.minecraft.entity.player.*;

import net.smart.moving.*;

public class SmartMovingFactory extends net.smart.moving.SmartMovingFactory
{
	public static void initialize()
	{
		if(!isInitialized())
			new SmartMovingFactory();
	}

	@Override
	protected net.smart.moving.SmartMoving doGetInstance(EntityPlayer entityPlayer)
	{
		net.smart.moving.SmartMoving moving = super.doGetInstance(entityPlayer);
		if(moving != null)
			return moving;

		IEntityPlayerSP playerBase = SmartMoving.getPlayerBase(entityPlayer);
		if(playerBase != null)
			return playerBase.getMoving();

		return null;
	}
}