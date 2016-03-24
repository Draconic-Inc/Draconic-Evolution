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

public interface IPacketReceiver
{
	boolean processStatePacket(FMLProxyPacket packet, IEntityPlayerMP player, int entityId, long state);

	boolean processConfigInfoPacket(FMLProxyPacket packet, IEntityPlayerMP player, String info);

	boolean processConfigContentPacket(FMLProxyPacket packet, IEntityPlayerMP player, String[] content, String username);

	boolean processConfigChangePacket(FMLProxyPacket packet, IEntityPlayerMP player);

	boolean processSpeedChangePacket(FMLProxyPacket packet, IEntityPlayerMP player, int difference, String username);

	boolean processHungerChangePacket(FMLProxyPacket packet, IEntityPlayerMP player, float hunger);

	boolean processSoundPacket(FMLProxyPacket packet, IEntityPlayerMP player, String soundId, float distance, float pitch);
}