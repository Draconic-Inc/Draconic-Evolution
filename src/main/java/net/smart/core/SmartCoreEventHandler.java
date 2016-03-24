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

package net.smart.core;

import java.util.*;

import net.minecraft.client.multiplayer.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.ItemStack;
import net.minecraft.network.*;
import net.minecraft.server.management.*;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.network.play.client.*;

public class SmartCoreEventHandler
{
	private final static Set<SmartCoreEventHandler> handlers = new HashSet<SmartCoreEventHandler>();

	public static void Add(SmartCoreEventHandler handler)
	{
		handlers.add(handler);
	}

	public static void Remove(SmartCoreEventHandler handler)
	{
		handlers.remove(handler);
	}

	@SuppressWarnings("unused")
	public static void NetHandlerPlayServer_beforeProcessPlayer(NetHandlerPlayServer netServerHandler, C03PacketPlayer packetPlayer)
	{
		for(SmartCoreEventHandler eventHandler : handlers)
			eventHandler.beforeProcessPlayer(netServerHandler);
	}

	@SuppressWarnings("unused")
	public static void NetHandlerPlayServer_afterProcessPlayer(NetHandlerPlayServer netServerHandler, C03PacketPlayer packetPlayer)
	{
		for(SmartCoreEventHandler eventHandler : handlers)
			eventHandler.afterProcessPlayer(netServerHandler);
	}

	public static void NetHandlerPlayServer_beforeProcessPlayerBlockPlacement(NetHandlerPlayServer netServerHandler, C08PacketPlayerBlockPlacement packet15place)
	{
		for(SmartCoreEventHandler eventHandler : handlers)
			eventHandler.beforeProcessPlayerBlockPlacement(netServerHandler, packet15place);
	}

	public static void NetHandlerPlayServer_afterProcessPlayerBlockPlacement(NetHandlerPlayServer netServerHandler, C08PacketPlayerBlockPlacement packet15place)
	{
		for(SmartCoreEventHandler eventHandler : handlers)
			eventHandler.afterProcessPlayerBlockPlacement(netServerHandler, packet15place);
	}

	@SuppressWarnings("unused")
	public static void PlayerControllerMP_beforeOnPlayerRightClick(PlayerControllerMP playerControllerMP, EntityPlayer entityPlayerSP, World world, ItemStack itemStack, int integer1, int integer2, int integer3, int integer4, Vec3 vec3)
	{
		for(SmartCoreEventHandler eventHandler : handlers)
			eventHandler.beforeOnPlayerRightClick(playerControllerMP, entityPlayerSP);
	}

	@SuppressWarnings("unused")
	public static void PlayerControllerMP_afterOnPlayerRightClick(PlayerControllerMP playerControllerMP, EntityPlayer entityPlayerSP, World world, ItemStack itemStack, int integer1, int integer2, int integer3, int integer4, Vec3 vec3)
	{
		for(SmartCoreEventHandler eventHandler : handlers)
			eventHandler.afterOnPlayerRightClick(playerControllerMP, entityPlayerSP);
	}

	@SuppressWarnings("unused")
	public static void ItemInWorldManager_beforeActivateBlockOrUseItem(ItemInWorldManager itemInWorldManager, EntityPlayer entityPlayer, World world, ItemStack itemStack, int integer1, int integer2, int integer3, int integer4, float float1, float float2, float float3)
	{
		for(SmartCoreEventHandler eventHandler : handlers)
			eventHandler.beforeActivateBlockOrUseItem(itemInWorldManager, entityPlayer);
	}

	@SuppressWarnings("unused")
	public static void ItemInWorldManager_afterActivateBlockOrUseItem(ItemInWorldManager itemInWorldManager, EntityPlayer entityPlayer, World world, ItemStack itemStack, int integer1, int integer2, int integer3, int integer4, float float1, float float2, float float3)
	{
		for(SmartCoreEventHandler eventHandler : handlers)
			eventHandler.afterActivateBlockOrUseItem(itemInWorldManager, entityPlayer);
	}

	@SuppressWarnings("unused")
	public void beforeProcessPlayer(NetHandlerPlayServer netServerHandler)
	{
	}

	@SuppressWarnings("unused")
	public void afterProcessPlayer(NetHandlerPlayServer netServerHandler)
	{
	}

	@SuppressWarnings("unused")
	public void beforeProcessPlayerBlockPlacement(NetHandlerPlayServer netServerHandler, C08PacketPlayerBlockPlacement packet15place)
	{
	}

	@SuppressWarnings("unused")
	public void afterProcessPlayerBlockPlacement(NetHandlerPlayServer netServerHandler, C08PacketPlayerBlockPlacement packet15place)
	{
	}

	@SuppressWarnings("unused")
	public void beforeOnPlayerRightClick(PlayerControllerMP playerControllerMP, EntityPlayer entityPlayerSP)
	{
	}

	@SuppressWarnings("unused")
	public void afterOnPlayerRightClick(PlayerControllerMP playerControllerMP, EntityPlayer entityPlayerSP)
	{
	}

	@SuppressWarnings("unused")
	public void beforeActivateBlockOrUseItem(ItemInWorldManager itemInWorldManager, EntityPlayer entityPlayer)
	{
	}

	@SuppressWarnings("unused")
	public void afterActivateBlockOrUseItem(ItemInWorldManager itemInWorldManager, EntityPlayer entityPlayer)
	{
	}
}