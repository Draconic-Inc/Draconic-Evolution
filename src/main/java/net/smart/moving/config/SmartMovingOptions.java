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
import java.lang.reflect.*;

import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.client.resources.*;
import net.minecraft.client.settings.*;
import net.minecraft.util.*;
import net.minecraft.world.WorldSettings.*;

import net.smart.moving.*;
import net.smart.properties.*;
import net.smart.utilities.*;

public class SmartMovingOptions extends SmartMovingClientConfig
{
	public final Property<Boolean> _localUserHasChangeConfigRight = Unmodified("move.global.config.right.local.user").comment("Whether the current local user has the right to change the global configuration in-game (despite of the names listed in \"move.global.config.right.user.names\"").section();
	public final Property<Boolean> _localUserHasChangeSpeedRight = Unmodified("move.global.speed.right.local.user").comment("Whether the current local user has the right to change the global speed in-game (despite of the names listed in \"move.global.config.right.user.names\"");



	public final Property<Float> _perspectiveFadeFactor = PositiveFactor("move.perspective.fade.factor").values(0.5F, 0.1F, 1F).comment("Fading speed factor between the different perspectives (>= 0.1, <= 1, set to '1' to switch off)").book("Viewpoint perspective", "Below you find the options to manipulate the viewpoint perspective");
	public final Property<Float> _perspectiveRunFactor = Float("move.perspective.run.factor").key("move.run.perspective.factor", _pre_sm_2_1).defaults(1F).comment("Standard sprinting perspective (set to '0' to switch off)");
	public final Property<Float> _perspectiveSprintFactor = Float("move.perspective.sprint.factor").key("move.sprint.perspective.factor", _pre_sm_2_1).defaults(1.5F).comment("Smart on ground sprinting perspective (set to '0' to switch off)");



	public final Property<Float> _angleJumpDoubleClickTicks = Positive("move.jump.angle.double.click.ticks").singular().up(3F, 2F).comment("The maximum number of ticks between two clicks to trigger a side or back jump (>= 2)").book("User interface", "Below you find the options to manipulate Smart Moving's user interface");

	public final Property<Boolean> _wallJumpDoubleClick = Unmodified("move.jump.wall.double.click").singular().comment("Whether wall jumping should be triggered by single or double clicking (and then press and holding) the jump button").section();
	public final Property<Float> _wallJumpDoubleClickTicks = Positive("move.jump.wall.double.click.ticks").singular().up(3F, 2F).comment("The maximum number of ticks between two clicks to trigger a wall jump (>= 2, depends on \"move.jump.wall.double.click\")");

	public final Property<Boolean> _climbJumpBackHeadOnGrab = Unmodified("move.jump.climb.back.head.on.grab").singular().comment("Whether pressing or not pressing the grab button while climb jumping back results in a head jump").section();

	public final Property<Boolean> _displayExhaustionBar = Unmodified("move.gui.exhaustion.bar").singular().comment("Whether to display the exhaustion bar in the game overlay").section();
	public final Property<Boolean> _displayJumpChargeBar = Unmodified("move.gui.jump.charge.bar").singular().comment("Whether to display the jump charge bar in the game overlay");

	public final Property<Boolean> _sneakToggle = Modified("move.sneak.toggle").comment("To switch on/off sneak toggling").section();
	public final Property<Boolean> _crawlToggle = Modified("move.crawl.toggle").comment("To switch on/off crawl toggling");

	public final Property<Boolean> _flyCloseToGround = Modified("move.fly.ground.close").comment("To switch on/off flying close to the ground").section();
	public final Property<Boolean> _flyWhileOnGround = Modified("move.fly.ground.collide").depends(_flyCloseToGround).comment("To switch on/off flying while colliding with the grond (Relevant only if \"move.fly.ground.close\" is true)");

	public final Property<Boolean> _flyControlVertical = Unmodified("move.fly.control.vertical").comment("Whether flying control also depends on where the player looks vertically.").section();
	public final Property<Boolean> _diveControlVertical = Unmodified("move.dive.control.vertical").comment("Whether diving control also depends on where the player looks vertically.");

	private final Property<Integer> _old_toggleKeyCode = Integer("move.toggle.key", _pre_sm_1_7).singular().defaults(67);
	private final Property<String> _defaultConfigToggleKeyName = String("move.config.toggle.default.key.name").key("move.toggle.key.name", _pre_sm_3_2).singular().defaults("F9").source(_old_toggleKeyCode.toKeyName(), _pre_sm_1_7).singular().comment("Key name to toggle Smart Moving features in-game (default: \"F9\")").section();
	private final Property<String> _defaultGrabKeyName = String("move.grab.default.key.name").singular().defaults("LCONTROL").singular().comment("Default key name to \"grab\" (default: \"LCONTROL\")");
	private final Property<String> _speedIncreaseKeyName = String("move.speed.increase.default.key.name").key("move.speed.increase.key.name", _pre_sm_3_2).singular().defaults("O").singular().comment("Key name to increase the moving speed ingame (default: \"O\")");
	private final Property<String> _speedDecreaseKeyName = String("move.speed.decrease.default.key.name").key("move.speed.decrease.key.name", _pre_sm_3_2).singular().defaults("I").singular().comment("Key name to decrease the moving speed ingame (default: \"I\")");

	public final Property<Integer> _defaultConfigToggleKeyCode = _defaultConfigToggleKeyName.toKeyCode(67);
	public final Property<Integer> _defaultGrabKeyCode = _defaultGrabKeyName.toKeyCode(29);
	public final Property<Integer> _defaultSpeedIncreaseKeyCode = _speedIncreaseKeyName.toKeyCode(24);
	public final Property<Integer> _defaultSpeedDecreaseKeyCode = _speedDecreaseKeyName.toKeyCode(23);



	public final Property<Boolean> _configChat = Unmodified("move.config.chat").singular().comment("To switch on/off option status messages via chat system").book("Message Management", "Below you find the options to define in which case Smart Moving should write messages about its current behavior to the ingame chat");
	public final Property<Boolean> _configChatInit = Unmodified("move.config.chat.init").depends(_configChat).singular().comment("To switch on/off the initial option status message when starting a game (Relevant only if \"move.config.chat\" is not false)");
	public final Property<Boolean> _configChatInitHelp = Unmodified("move.config.chat.init.help").depends(_configChatInit).singular().comment("To switch on/off the initial option help message (Relevant only if \"move.config.chat.init\" is not false and no improved keybinding GUI (Minecraft Forge or the Macros/Keybind mod) is installed)");
	public final Property<Boolean> _configChatServer = Unmodified("move.config.chat.server").depends(_configChat).singular().comment("To switch on/off the server config overridden status message when joining a multiplayer game (Relevant only if \"move.config.chat\" is not false)");

	public final Property<Boolean> _speedChat = Unmodified("move.speed.chat").singular().comment("To switch on/off speed messages via chat system").section();
	public final Property<Boolean> _speedChatInit = Unmodified("move.speed.chat.init").depends(_speedChat).singular().comment("To switch on/off the intial speed message when starting a game (Relevant only if \"move.speed.chat\" is not false)");
	public final Property<Boolean> _speedChatInitHelp = Unmodified("move.speed.chat.init.help").depends(_speedChatInit).singular().comment("To switch on/off the initial speed help message (Relevant only if \"move.speed.chat.init\" is not false and no improved keybinding GUI (Minecraft Forge or the Macros/Keybind mod) is installed))");
	public final Property<Boolean> _speedChatServer = Unmodified("move.config.chat.server").depends(_speedChat).singular().comment("To switch on/off the server speed change message when joining a multiplayer game (Relevant only if \"move.speed.chat\" is not false)");


	public KeyBinding keyBindGrab;
	public KeyBinding keyBindConfigToggle;
	public KeyBinding keyBindSpeedIncrease;
	public KeyBinding keyBindSpeedDecrease;

	public static final File optionsPath = net.minecraft.client.Minecraft.getMinecraft().mcDataDir;

	public SmartMovingOptions()
	{
		loadFromOptionsFile(optionsPath);
		saveToOptionsFile(optionsPath);

		keyBindGrab = new KeyBinding("key.climb", _defaultGrabKeyCode.value, "key.categories.gameplay");
		keyBindConfigToggle = new KeyBinding("key.config.toggle", _defaultConfigToggleKeyCode.value, "key.categories.smartmoving");
		keyBindSpeedIncrease = new KeyBinding("key.speed.increase", _defaultSpeedIncreaseKeyCode.value, "key.categories.smartmoving");
		keyBindSpeedDecrease = new KeyBinding("key.speed.decrease", _defaultSpeedDecreaseKeyCode.value, "key.categories.smartmoving");
	}

	public boolean isSneakToggleEnabled()
	{
		return _sneakToggle.value && enabled;
	}

	public boolean isCrawlToggleEnabled()
	{
		return _crawlToggle.value && enabled;
	}

	public int angleJumpDoubleClickTicks()
	{
		return (int)Math.ceil(_angleJumpDoubleClickTicks.value);
	}

	public int wallJumpDoubleClickTicks()
	{
		return (int)Math.ceil(_wallJumpDoubleClickTicks.value);
	}

	@Override
	public void toggle()
	{
		super.toggle();
		if(_configChat.value)
			writeClientConfigMessageToChat(false);

		Property<String> defaultKey = null;
		switch(gameType)
		{
			default:
			case Survival:
				defaultKey = _survivalDefaultConfigKey;
				break;
			case Creative:
				defaultKey = _creativeDefaultConfigKey;
				break;
			case Adventure:
				defaultKey = _adventureDefaultConfigKey;
				break;
		}

		if(defaultKey != null)
		{
			String currentKey = getCurrentKey();
			defaultKey.setValue(currentKey);
			saveToOptionsFile(optionsPath);
		}
	}

	@Override
	public void changeSpeed(int difference)
	{
		super.changeSpeed(difference);
		writeClientSpeedMessageToChat(false);
		saveToOptionsFile(optionsPath);
	}

	private void writeClientConfigMessageToChat(boolean everyone)
	{
		String prefix = getClientEveryonePrefix("move.config.chat.client", everyone);
		if(SmartMovingContext.Config.enabled)
		{
			String name = SmartMovingContext.Config._configKeyName.value;
			if(name.isEmpty())
				name = null;

			boolean unnamed = name == null;
			if(unnamed)
				name = getCurrentKey();

			if(name == SmartMovingProperties.Enabled || (unnamed && getKeyCount() == 1))
				writeToChat(prefix + "enabled", SmartMovingInfo.ConfigChatId);
			else
				writeToChat(prefix + (unnamed ? "unnamed" : "named"), SmartMovingInfo.ConfigChatId, new Object[] { name });
		}
		else
			writeToChat(prefix + "disabled", SmartMovingInfo.ConfigChatId);
	}

	public void writeClientSpeedMessageToChat(boolean everyone)
	{
		if(!_speedChat.value)
			return;

		Object percent = SmartMovingContext.Config.getSpeedPercent();
		String prefix = getClientEveryonePrefix("move.speed.chat.client", everyone);
		String key = prefix + (percent.equals(SmartMovingConfig.defaultSpeedPercent) ? "reset" : "change");
		writeToChat(key, SmartMovingInfo.SpeedChatId, percent);
	}

	private static String getClientEveryonePrefix(String base, boolean everyone)
	{
		String result = base + ".";
		if(everyone)
			result += "everyone.";
		return result;
	}

	public void writeServerConfigMessageToChat()
	{
		if(!_configChatServer.value)
			return;

		if(SmartMovingContext.Config.enabled)
		{
			String configName = SmartMovingContext.Config._configKeyName.value;
			if(configName != null && !configName.isEmpty())
				writeToChat("move.config.chat.server.global.named", SmartMovingInfo.DefaultChatId, configName);
			else
				writeToChat("move.config.chat.server.global.unnamed", SmartMovingInfo.DefaultChatId);
		}
		else
			writeToChat("move.config.chat.server.disable", SmartMovingInfo.DefaultChatId);
	}

	public void writeServerReconfigMessageToChat(boolean wasEnabled, String username, boolean everyone)
	{
		if(Minecraft.getMinecraft().thePlayer.getGameProfile().getName().equals(username))
			writeClientConfigMessageToChat(everyone);
		else if(_configChatServer.value)
		{
			if(SmartMovingContext.Config.enabled)
			{
				String configname = SmartMovingContext.Config._configKeyName.value;
				boolean hasConfigName = configname != null && !configname.isEmpty();
				if(wasEnabled)
					if(hasConfigName)
						if(username != null)
							writeToChat("move.config.chat.server.update.named.user", SmartMovingInfo.ConfigChatId, configname, username);
						else
							writeToChat("move.config.chat.server.update.named", SmartMovingInfo.ConfigChatId, configname);
					else
						if(username != null)
							writeToChat("move.config.chat.server.update.unnamed.user", SmartMovingInfo.ConfigChatId, username);
						else
							writeToChat("move.config.chat.server.update.unnamed", SmartMovingInfo.ConfigChatId);
				else
					if(hasConfigName)
						if(username != null)
							writeToChat("move.config.chat.server.update.named.user", SmartMovingInfo.ConfigChatId, configname, username);
						else
							writeToChat("move.config.chat.server.update.named", SmartMovingInfo.ConfigChatId, configname);
					else
						if(username != null)
							writeToChat("move.config.chat.server.enable.user", SmartMovingInfo.ConfigChatId, username);
						else
							writeToChat("move.config.chat.server.enable", SmartMovingInfo.ConfigChatId);
			}
			else if(wasEnabled)
				if(username != null)
					writeToChat("move.config.chat.server.disable.user", SmartMovingInfo.ConfigChatId, username);
				else
					writeToChat("move.config.chat.server.disable", SmartMovingInfo.ConfigChatId);
		}
	}

	public void writeServerDeconfigMessageToChat()
	{
		if(_configChatServer.value)
			writeToChat("move.config.chat.server.local", SmartMovingInfo.DefaultChatId);
	}

	public void writeServerSpeedMessageToChat(String username, boolean everyone)
	{
		if(Minecraft.getMinecraft().thePlayer.getGameProfile().getName().equals(username))
			writeClientSpeedMessageToChat(everyone);
		else if(_speedChatServer.value)
		{
			Object percent = SmartMovingContext.Config.getSpeedPercent();
			String prefix = "move.speed.chat.server.";
			if(percent.equals(SmartMovingConfig.defaultSpeedPercent))
				writeToChat(prefix + "reset", SmartMovingInfo.SpeedChatId, username);
			else
				writeToChat(prefix + "change", SmartMovingInfo.SpeedChatId, percent, username);
		}
	}

	public static void writeNoRightsToChangeConfigMessageToChat(boolean isRemote)
	{
		writeToChat("move.config.chat.server.illegal." + (isRemote ? "remote" : "local"), SmartMovingInfo.ConfigChatId);
	}

	public static void writeNoRightsToChangeSpeedMessageToChat(boolean isRemote)
	{
		writeToChat("move.speed.chat.server.illegal." + (isRemote ? "remote" : "local"), SmartMovingInfo.SpeedChatId);
	}

	private static void writeToChat(String key, int id, Object... parameters)
	{
		String message = parameters == null || parameters.length == 0
			? I18n.format(key)
			: I18n.format(key, parameters);

		GuiNewChat guiChat = Minecraft.getMinecraft().ingameGUI.getChatGUI();

		// bugfix: also delete multi-lined chat messages
		if(id != 0)
			for(int i=0; i<5; i++)
				guiChat.deleteChatLine(id);

		guiChat.printChatMessageWithOptionalDeletion(new ChatComponentText(message), id);
	}

	public static void initialize(boolean redPowerWiring, boolean buildCraftTransportation, boolean finiteLiquid, boolean betterThanWolves, boolean singlePlayerCommands, boolean ropesPlus, boolean aSGrapplingHook, boolean betterMisc)
	{
		hasRedPowerWire = redPowerWiring;
		hasBuildCraftTransportation = buildCraftTransportation;
		hasFiniteLiquid = finiteLiquid;
		hasBetterThanWolves = betterThanWolves;
		hasSinglePlayerCommands = singlePlayerCommands;
		hasRopesPlus = ropesPlus;
		hasASGrapplingHook = aSGrapplingHook;
		hasBetterMisc = betterMisc;
	}

	public void resetForNewGame()
	{
		gameType = -1;
	}

	public void initializeForGameIfNeccessary()
	{
		PlayerControllerMP controller = Minecraft.getMinecraft().playerController;
		if(controller == null)
			return;

		int currentGameType = ((GameType)Reflect.GetField(_currentGameType, controller)).getID();
		if(currentGameType == gameType)
			return;

		gameType = currentGameType;

		String[] keys = null;
		String defaultKey = null;

		switch(gameType)
		{
			case Survival:
				keys = _survivalConfigKeys.value;
				defaultKey = _survivalDefaultConfigKey.value;
				break;
			case Creative:
				keys = _creativeConfigKeys.value;
				defaultKey = _creativeDefaultConfigKey.value;
				break;
			case Adventure:
				keys = _adventureConfigKeys.value;
				defaultKey = _adventureDefaultConfigKey.value;
				break;
			default:
				defaultKey = "";
		}

		setKeys(keys);
		if(!defaultKey.isEmpty())
			setCurrentKey(defaultKey);

		if(_configChatInit.value)
			writeClientConfigMessageToChat(false);

		if(isUserSpeedEnabled() && _speedChatInit.value)
		{
			Object speedPercent = getSpeedPercent();

			if(!speedPercent.equals(defaultSpeedPercent))
				writeToChat("move.speed.chat.client.init", SmartMovingInfo.DefaultChatId, speedPercent);
		}
	}

	public static boolean hasRedPowerWire = false;
	public static boolean hasBuildCraftTransportation = false;
	public static boolean hasFiniteLiquid = false;
	public static boolean hasBetterThanWolves = false;
	public static boolean hasSinglePlayerCommands = false;
	public static boolean hasRopesPlus = false;
	public static boolean hasASGrapplingHook = false;
	public static boolean hasBetterMisc = false;

	public int gameType;

	private static Field _currentGameType = Reflect.GetField(PlayerControllerMP.class, SmartMovingInstall.PlayerControllerMP_currentGameType);
}