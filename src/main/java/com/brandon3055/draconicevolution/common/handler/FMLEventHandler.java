package com.brandon3055.draconicevolution.common.handler;


import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.items.armor.ArmorEffectHandler;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.utills.LogHelper;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

import java.lang.reflect.Field;
import java.util.*;

public class FMLEventHandler {

	public static Map<EntityPlayer, Boolean> playersWithFlight = new WeakHashMap<EntityPlayer, Boolean>();
	public static List<String> playersWithUphillStep = new ArrayList<String>();
	public static Field walkSpeed;
	private static boolean mmGiven = false;

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
		if(eventArgs.modID.equalsIgnoreCase(References.MODID)) {
			ConfigHandler.syncConfig();
			LogHelper.info("Config Changed");
		}
	}

	@SubscribeEvent
	public void onEntityUpdate(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.START) return;
		EntityPlayer player = event.player;
		//Reset Walk Speed----------------------------------------------------------------------------------------------

		if (walkSpeed == null){
			walkSpeed = ReflectionHelper.findField(PlayerCapabilities.class, "walkSpeed", "g", "field_75097_g");
			walkSpeed.setAccessible(true);
		}
		if (walkSpeed != null) {
			try {
				walkSpeed.setFloat(player.capabilities, 0.1f);
			}
			catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			catch (ConcurrentModificationException e) {
				e.printStackTrace();
			}
		}

		//Apply Hill Step-----------------------------------------------------------------------------------------------

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

		//Apply Swiftness-----------------------------------------------------------------------------------------------

		if (ArmorEffectHandler.getHasSwiftness(player)) {
			int i = ArmorEffectHandler.getSwiftnessLevel(player);

			float percentIncrease = ArmorEffectHandler.getSwiftnessMultiplier(player) * ((i + 1) * 0.05f);

			if ((player.onGround || player.capabilities.isFlying) && player.moveForward > 0F)
			{
				player.moveFlying(0F, 1F, player.capabilities.isFlying ? (percentIncrease / 2.0f) : percentIncrease);

			}
			if ((!player.onGround && player.capabilities.isFlying) && player.motionY != 0)
			{
				if (DraconicEvolution.proxy.isSpaceDown() && !DraconicEvolution.proxy.isShiftDown())
				{
					player.motionY = 0.124D + (double)(percentIncrease * 3F);
				}

				if (DraconicEvolution.proxy.isShiftDown() && !DraconicEvolution.proxy.isSpaceDown())
				{
					player.motionY = -0.124D - (double)(percentIncrease * 3F);
				}
			}
			player.jumpMovementFactor = 0.02F + (percentIncrease * 0.2F);
		}

		//Apply Flight--------------------------------------------------------------------------------------------------

		if (ArmorEffectHandler.getHasFlight(player)) {
			playersWithFlight.put(player, true);
			player.capabilities.allowFlying = true;
			if (ArmorEffectHandler.getFlightLock(player)) player.capabilities.isFlying = true;

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

	@SubscribeEvent
	public void playerLogin(PlayerEvent.PlayerLoggedInEvent event)
	{
		if (!mmGiven && event.player.getCommandSenderName().toLowerCase().equals("dezil_nz"))
		{
			mmGiven = true;
			event.player.addChatComponentMessage(new ChatComponentText("Hello Dez! Here have a Marshmallow"));
			event.player.worldObj.spawnEntityInWorld(new EntityItem(event.player.worldObj, event.player.posX, event.player.posY, event.player.posZ, new ItemStack(ModItems.dezilsMarshmallow)));
		}
	}
}
