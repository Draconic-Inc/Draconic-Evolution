package com.brandon3055.draconicevolution.client.handler;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.items.weapons.DraconicBow;
import com.brandon3055.draconicevolution.common.items.weapons.WyvernBow;
import com.brandon3055.draconicevolution.common.network.MountUpdatePacket;
import com.brandon3055.draconicevolution.common.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.common.utills.LogHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.FOVUpdateEvent;

/**
 * Created by Brandon on 28/10/2014.
 */
public class ClientEventHandler {

	public static int elapsedTicks;
	private static float previousFOB = 0f;
	public static float previousSensitivity = 0;
	public static boolean bowZoom = false;
	public static boolean lastTickBowZoom = false;
	public static int tickSet = 0;
	private static int remountTicksRemaining = 0;
	private static int remountEntityID = 0;

	@SubscribeEvent
	public void tickEnd(TickEvent event) {
		if (event.phase != TickEvent.Phase.START || event.side != Side.CLIENT) return;
		elapsedTicks++;
		HudHandler.clientTick();

		if (bowZoom && !lastTickBowZoom){
			previousSensitivity = Minecraft.getMinecraft().gameSettings.mouseSensitivity;
			Minecraft.getMinecraft().gameSettings.mouseSensitivity = previousSensitivity / 3;
		}else if (!bowZoom && lastTickBowZoom){
			Minecraft.getMinecraft().gameSettings.mouseSensitivity = previousSensitivity;
		}

		lastTickBowZoom = bowZoom;
		if (elapsedTicks - tickSet > 10) bowZoom = false;

		searchForPlayerMount();
	}

	@SubscribeEvent
	public void fovUpdate(FOVUpdateEvent event){
		if (event.entity.getItemInUse() != null && (event.entity.getItemInUse().getItem() instanceof WyvernBow || event.entity.getItemInUse().getItem() instanceof DraconicBow)){
			float f = 1f;
			int i = event.entity.getItemInUseDuration();
			float f1 = (float)i / 20.0F;

			float zMax = 1f;

			if (ItemNBTHelper.getString(event.entity.getItemInUse(), "mode", "").equals("sharpshooter")){
				if (event.entity.getItemInUse().getItem() instanceof WyvernBow) zMax = 1.35f;
				else if (event.entity.getItemInUse().getItem() instanceof DraconicBow) zMax = 2.5f;
				bowZoom = true;
				tickSet = elapsedTicks;
			}


			if (f1 < zMax)
			{
				f1 *= (f1*(zMax*2));
			}
			else f1 = previousFOB;

			previousFOB = f1;

			f *= 1.0F - f1 * 0.15F;
			event.newfov = f;
		}
	}


	private void searchForPlayerMount(){
		if (remountTicksRemaining > 0){
			Entity e = Minecraft.getMinecraft().theWorld.getEntityByID(remountEntityID);
			if (e != null){
				Minecraft.getMinecraft().thePlayer.mountEntity(e);
				LogHelper.info("Successfully placed player on mount after "+(500 - remountTicksRemaining)+" ticks");
				remountTicksRemaining = 0;
				return;
			}
			remountTicksRemaining--;
			if (remountTicksRemaining == 0){
				LogHelper.error("Unable to locate player mount after 500 ticks! Aborting");
				DraconicEvolution.network.sendToServer(new MountUpdatePacket(-1));
			}
		}
	}

	public static void tryRepositionPlayerOnMount(int id){
		if (remountTicksRemaining == 500) return;
		remountTicksRemaining = 500;
		remountEntityID = id;
		LogHelper.info("Started checking for player mount");
	}
}
