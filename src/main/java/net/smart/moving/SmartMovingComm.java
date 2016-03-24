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
import net.minecraft.client.*;
import net.minecraft.client.entity.*;
import net.minecraft.entity.*;
import net.minecraft.network.play.client.*;
import net.minecraft.server.*;
import net.smart.moving.config.*;
import net.smart.properties.*;

public class SmartMovingComm extends SmartMovingContext implements IPacketReceiver, IPacketSender
{
	@Override
	public boolean processStatePacket(FMLProxyPacket packet, IEntityPlayerMP player, int entityId, long state)
	{
		Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(entityId);
		if(entity == null)
			return true;

		SmartMovingOther moving = SmartMovingFactory.getOtherSmartMoving((EntityOtherPlayerMP)entity);
		if(moving != null)
			moving.processStatePacket(state);
		return true;
	}

	@Override
	public boolean processConfigInfoPacket(FMLProxyPacket packet, IEntityPlayerMP player, String info)
	{
		return false;
	}

	@Override
	public boolean processConfigContentPacket(FMLProxyPacket packet, IEntityPlayerMP player, String[] content, String username)
	{
		processConfigPacket(content, username, false);
		return true;
	}

	@Override
	public boolean processConfigChangePacket(FMLProxyPacket packet, IEntityPlayerMP player)
	{
		SmartMovingOptions.writeNoRightsToChangeConfigMessageToChat(isConnectedToRemoteServer());
		return true;
	}

	@Override
	public boolean processSpeedChangePacket(FMLProxyPacket packet, IEntityPlayerMP player, int difference, String username)
	{
		if(difference == 0)
			SmartMovingOptions.writeNoRightsToChangeSpeedMessageToChat(isConnectedToRemoteServer());
		else
		{
			Config.changeSpeed(difference);
			Options.writeServerSpeedMessageToChat(username, Config._globalConfig.value);
		}
		return true;
	}

	@Override
	public boolean processHungerChangePacket(FMLProxyPacket packet, IEntityPlayerMP player, float hunger)
	{
		return false;
	}

	@Override
	public boolean processSoundPacket(FMLProxyPacket packet, IEntityPlayerMP player, String soundId, float distance, float pitch)
	{
		return false;
	}

	private static boolean isConnectedToRemoteServer()
	{
		return MinecraftServer.getServer() == null || Minecraft.getMinecraft().getIntegratedServer() == null || !Minecraft.getMinecraft().getIntegratedServer().isSinglePlayer();
	}

	public static void processConfigPacket(String[] content, String username, boolean blockCode)
	{
		boolean isGloballyConfigured = false;
		if(content != null && content.length == 2 && Options._globalConfig.getCurrentKey().equals(content[0]))
		{
			isGloballyConfigured = "true".equals(content[1]);
			content = null;
		}

		boolean wasEnabled = Config.enabled;
		boolean first = Config != ServerConfig;
		if(first)
			ServerConfig.reset();

		if(content != null)
			if(content.length != 0)
			{
				ServerConfig.loadFromProperties(content, blockCode);
				isGloballyConfigured = ServerConfig._globalConfig.value;
			}
			else
			{
				Config = Options;
				Options.writeServerDeconfigMessageToChat();
				return;
			}
		else
		{
			ServerConfig.load(false);
			ServerConfig.setCurrentKey(null);
		}

		ServerConfig._globalConfig.value = isGloballyConfigured;

		if(!first)
		{
			Options.writeServerReconfigMessageToChat(wasEnabled, username, isGloballyConfigured);
			return;
		}

		Config = ServerConfig;
		Options.writeServerConfigMessageToChat();
		if (!blockCode)
			SmartMovingPacketStream.sendConfigInfo(SmartMovingComm.instance, SmartMovingConfig._sm_current);
	}

	@Override
	public void sendPacket(byte[] data)
	{
		Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C17PacketCustomPayload(SmartMovingPacketStream.Id, data));
	}

	public static boolean processBlockCode(String text)
	{
		if(!text.startsWith("§0§1") || !text.endsWith("§f§f"))
			return false;

		String codes = text.substring(4, text.length() - 4);
		processBlockCode(codes, "§0", Options._baseClimb, "standard");
		processBlockCode(codes, "§1", Options._freeClimb);
		processBlockCode(codes, "§2", Options._ceilingClimbing);
		processBlockCode(codes, "§3", Options._swim);
		processBlockCode(codes, "§4", Options._dive);
		processBlockCode(codes, "§5", Options._crawl);
		processBlockCode(codes, "§6", Options._slide);
		processBlockCode(codes, "§7", Options._fly);
		processBlockCode(codes, "§8", Options._jumpCharge);
		processBlockCode(codes, "§9", Options._headJump);
		processBlockCode(codes, "§a", Options._angleJumpSide);
		processBlockCode(codes, "§b", Options._angleJumpBack);
		return true;
	}

	private static void processBlockCode(String text, String blockCode, Property<?> property, String... value)
	{
		if(text.contains(blockCode))
			processConfigPacket(new String[] { property.getCurrentKey(), value.length > 0 ? value[0] : "false" }, null, true);
	}

	public static final SmartMovingComm instance = new SmartMovingComm();
}