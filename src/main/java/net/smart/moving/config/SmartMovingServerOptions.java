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

package net.smart.moving.config;

import java.io.*;
import java.util.*;

import cpw.mods.fml.common.*;

import net.smart.moving.*;
import net.smart.properties.*;
import net.smart.properties.Properties;

public class SmartMovingServerOptions
{
	public final SmartMovingConfig config;
	public final File optionsPath;
	private final Property<Map<String,String>> _userConfigKeys;

	public SmartMovingServerOptions(SmartMovingConfig config, File optionsPath, int gameType)
	{
		this.config = config;
		this.optionsPath = optionsPath;

		config.loadFromOptionsFile(optionsPath);
		config.saveToOptionsFile(optionsPath);

		Property<String> configKey = null;
		Property<String[]> configKeys = null;
		switch(gameType)
		{
			default:
			case SmartMovingConfig.Survival:
				configKey = config._survivalDefaultConfigKey;
				configKeys = config._survivalConfigKeys;
				_userConfigKeys = config._survivalDefaultConfigUserKeys;
				break;
			case SmartMovingConfig.Creative:
				configKey = config._creativeDefaultConfigKey;
				configKeys = config._creativeConfigKeys;
				_userConfigKeys = config._creativeDefaultConfigUserKeys;
				break;
			case SmartMovingConfig.Adventure:
				configKey = config. _adventureDefaultConfigKey;
				configKeys = config._adventureConfigKeys;
				_userConfigKeys = config._adventureDefaultConfigUserKeys;
				break;
		}

		config.setKeys(configKeys.value);
		config.setCurrentKey(configKey != null && !configKey.value.isEmpty() ? configKey.value : null);

		logConfigState(config, null, false);
	}

	public void toggle(IEntityPlayerMP player)
	{
		config.toggle();
		config.saveToOptionsFile(optionsPath);
		logConfigState(config, player.getUsername(), true);
	}

	public void changeSpeed(int difference, IEntityPlayerMP player)
	{
		config.changeSpeed(difference);
		config.saveToOptionsFile(optionsPath);
		logSpeedState(config, player.getUsername());
	}

	private static void logConfigState(SmartMovingConfig config, String username, boolean reconfig)
	{
		String message = "Smart Moving ";
		if(config._globalConfig.value)
		{
			if(!reconfig)
				FMLLog.info(message + "overrides client configurations");

			String postfix = getPostfix(username);

			if(config.enabled)
			{
				String currentKey = config.getCurrentKey();

				message += reconfig ? "changed to " : "uses ";
				if(currentKey == null)
					FMLLog.info(message + "default server configuration" + postfix);
				else
				{
					String configName = config._configKeyName.value;

					message += "server configuration ";
					if(configName.isEmpty())
						FMLLog.info(message + "with key \"" + currentKey + "\"" + postfix);
					else
						FMLLog.info(message + "\"" + configName + "\"" + postfix);
				}
			}
			else
				FMLLog.info(message + "disabled" + postfix);
		}
		else
			FMLLog.info(message + "allows client configurations");
	}

	private static void logSpeedState(SmartMovingConfig config, String username)
	{
		FMLLog.info("Smart Moving speed set to " + config.getSpeedPercent() + "%" + getPostfix(username));
	}

	private static String getPostfix(String username)
	{
		if(username == null)
			return "";
		return " by user '" + username + "'";
	}

	public String[] writeToProperties()
	{
		return writeToProperties(null, null);
	}

	public String[] writeToProperties(IEntityPlayerMP mp, String key)
	{
		if(key == null ? !config.enabled : key == SmartMovingProperties.Disabled)
			return new String[] { config._globalConfig.getCurrentKey(), config._globalConfig.getValueString() };

		Properties properties = new Properties();
		config.write(properties, key);

		String[] result = new String[properties.size() * 2];
		Iterator<Map.Entry<Object, Object>> keys = properties.entrySet().iterator();

		String speedUserExponentKey = mp != null ? config._speedUserExponent.getCurrentKey() : null;
		int i = 0;
		while(keys.hasNext())
		{
			Map.Entry<Object, Object> entry = keys.next();
			String propertyKey = result[i++] = entry.getKey().toString();
			if(mp != null && propertyKey.equals(speedUserExponentKey))
			{
				Integer userExponent = config._speedUsersExponents.value.get(mp.getUsername());
				if(userExponent != null)
					entry.setValue(config._speedUserExponent.getValueString(userExponent));
			}
			result[i++] = entry.getValue().toString();
		}
		return result;
	}

	public void changeSingleSpeed(IEntityPlayerMP player, int difference)
	{
		Integer exponent = getPlayerSpeedExponent(player);
		if(exponent == null)
			exponent = config._speedUserExponent.value;

		exponent += difference;
		setPlayerSpeedExponent(player, exponent);
	}

	public Integer getPlayerSpeedExponent(IEntityPlayerMP player)
	{
		return config._speedUsersExponents.value.get(player.getUsername());
	}

	public synchronized void setPlayerSpeedExponent(IEntityPlayerMP player, Integer exponent)
	{
		config._speedUsersExponents.value.put(player.getUsername(), exponent);
		config.saveToOptionsFile(optionsPath);
	}

	public String[] writeToProperties(IEntityPlayerMP player, boolean toggle)
	{
		String key = getPlayerConfigurationKey(player);
		if(key == null || !config.hasKey(key))
		{
			key = config.getCurrentKey();
			setPlayerConfigurationKey(player, key);
		}

		if(toggle)
		{
			key = config.getNextKey(key);
			setPlayerConfigurationKey(player, key);
		}

		return writeToProperties(player, key);
	}

	public String getPlayerConfigurationKey(IEntityPlayerMP player)
	{
		return _userConfigKeys.value.get(player.getUsername());
	}

	public synchronized void setPlayerConfigurationKey(IEntityPlayerMP player, String key)
	{
		_userConfigKeys.value.put(player.getUsername(), key);
		config.saveToOptionsFile(optionsPath);
	}
}