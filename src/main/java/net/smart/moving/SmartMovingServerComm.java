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

package net.smart.moving;

import cpw.mods.fml.common.network.internal.*;

public class SmartMovingServerComm implements IPacketReceiver
{
	public static ILocalUserNameProvider localUserNameProvider = null;

	@Override
	public boolean processStatePacket(FMLProxyPacket packet, IEntityPlayerMP player, int entityId, long state)
	{
		player.getMoving().processStatePacket(packet, state);
		return true;
	}

	@Override
	public boolean processConfigInfoPacket(FMLProxyPacket packet, IEntityPlayerMP player, String info)
	{
		player.getMoving().processConfigPacket(info);
		return true;
	}

	@Override
	public boolean processConfigContentPacket(FMLProxyPacket packet, IEntityPlayerMP player, String[] content, String username)
	{
		return false;
	}

	@Override
	public boolean processConfigChangePacket(FMLProxyPacket packet, IEntityPlayerMP player)
	{
		player.getMoving().processConfigChangePacket(localUserNameProvider != null ? localUserNameProvider.getLocalConfigUserName() : null);
		return true;
	}

	@Override
	public boolean processSpeedChangePacket(FMLProxyPacket packet, IEntityPlayerMP player, int difference, String username)
	{
		player.getMoving().processSpeedChangePacket(difference, localUserNameProvider != null ? localUserNameProvider.getLocalSpeedUserName() : null);
		return true;
	}

	@Override
	public boolean processHungerChangePacket(FMLProxyPacket packet, IEntityPlayerMP player, float hunger)
	{
		player.getMoving().processHungerChangePacket(hunger);
		return true;
	}

	@Override
	public boolean processSoundPacket(FMLProxyPacket packet, IEntityPlayerMP player, String soundId, float volume, float pitch)
	{
		player.getMoving().processSoundPacket(soundId, volume, pitch);
		return true;
	}

	public static final SmartMovingServerComm instance = new SmartMovingServerComm();
}