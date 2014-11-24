package com.brandon3055.draconicevolution.common.handler;


import com.brandon3055.draconicevolution.common.items.armor.ArmorEffectHandler;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.utills.LogHelper;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class FMLEventHandler {

	public static Map<EntityPlayer, Boolean> playersWithFlight = new WeakHashMap<EntityPlayer, Boolean>();
	public static List<String> playersWithUphillStep = new ArrayList<String>();
	public static Field walkSpeed;

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
		if(eventArgs.modID.equalsIgnoreCase(References.MODID)) {
			ConfigHandler.syncConfig();
			LogHelper.info("Config Changed");
		}
	}

	@SubscribeEvent
	public void onEntityUpdate(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.START)return;
		EntityPlayer player = event.player;

		if (walkSpeed == null){
			walkSpeed = ReflectionHelper.findField(PlayerCapabilities.class, "walkSpeed", "g", "field_75097_g");
			walkSpeed.setAccessible(true);
		}
		if (walkSpeed != null) {
			try {
				walkSpeed.setFloat(player.capabilities, 0.1f);
			}
			catch (IllegalAccessException e) {
				LogHelper.error(e);
			}
		}

		if (player.worldObj.isRemote) {
			boolean highStepListed = playersWithUphillStep.contains(player.getDisplayName()) && player.stepHeight >= 1f;
			boolean hasHighStep = ArmorEffectHandler.getHasHighStep(player);

			if (hasHighStep && !highStepListed) {
				playersWithUphillStep.add(player.getDisplayName());
				player.stepHeight = 1f;
			}

			if (!hasHighStep && highStepListed) {
				playersWithUphillStep.remove(player.getDisplayName());
				player.stepHeight = 0.5F;
			}
		}


		if (ArmorEffectHandler.getHasSwiftness(player)) {
			int i = ArmorEffectHandler.getSwiftnessLevel(player);

			float percentIncrease = (i + 1) * 0.05f;


			if ((player.onGround || player.capabilities.isFlying) && player.moveForward > 0F)
				player.moveFlying(0F, 1F, player.capabilities.isFlying ? (percentIncrease / 2.0f) : percentIncrease);

		}


		if (ArmorEffectHandler.getHasFlight(player)) {
			playersWithFlight.put(player, true);
			player.capabilities.allowFlying = true;

		} else {

			if (!playersWithFlight.containsKey(player)) {
				playersWithFlight.put(player, false);
			}

			if (playersWithFlight.get(player) && !player.worldObj.isRemote) {
				playersWithFlight.put(player, false);

				if (!player.capabilities.isCreativeMode) {
					player.capabilities.allowFlying = false;
					player.capabilities.isFlying = false;
					player.sendPlayerAbilities();
				}
			}
		}
	}
}
