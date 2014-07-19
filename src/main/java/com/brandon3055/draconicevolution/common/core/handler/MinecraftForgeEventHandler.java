package com.brandon3055.draconicevolution.common.core.handler;


import com.brandon3055.draconicevolution.common.core.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.common.core.utills.LogHelper;
import com.brandon3055.draconicevolution.common.items.ModItems;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MinecraftForgeEventHandler {

	Random random = new Random();
	public static List<String> playersWithUphillStep = new ArrayList();
	public static List<String> playersWithFlight = new ArrayList();

	@SubscribeEvent
	public void onEntityUpdate(LivingUpdateEvent event){
		EntityLivingBase entityLiving = event.entityLiving;

		if (entityLiving.worldObj.isRemote && entityLiving instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) entityLiving;
			boolean hasUphillStep = playersWithUphillStep.contains(player.getDisplayName());
			boolean shouldHaveUphillStep = player.getEquipmentInSlot(1) != null && player.getEquipmentInSlot(1).getItem() == ModItems.draconicBoots && !player.isSneaking();

			if (!hasUphillStep && shouldHaveUphillStep)
			{
				playersWithUphillStep.add(player.getDisplayName());
				player.stepHeight = 1;
			}
			if (hasUphillStep && !shouldHaveUphillStep)
			{
				playersWithUphillStep.remove(player.getDisplayName());
				player.stepHeight = 0.5F;
			}
		}

		if(entityLiving instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) entityLiving;
			boolean hasFlight = playersWithFlight.contains(player.getDisplayName());
			boolean shouldHaveFlight = player.getEquipmentInSlot(3) != null && player.getEquipmentInSlot(3).getItem() == ModItems.draconicChest;

			if ((!hasFlight || !player.capabilities.allowFlying) && shouldHaveFlight)
			{
				if (!playersWithFlight.contains(player.getDisplayName()))
					playersWithFlight.add(player.getDisplayName());
				player.capabilities.allowFlying = true;
			}
			if (hasFlight && !shouldHaveFlight)
			{
				playersWithFlight.remove(player.getDisplayName());
				player.capabilities.allowFlying = false;
				player.capabilities.isFlying = false;
				if (player.capabilities.isCreativeMode)
					player.capabilities.allowFlying = true;
				player.sendPlayerAbilities();
			}
		}
	}

	@SubscribeEvent
	public void onDropEvent(LivingDropsEvent event){
		if (event.entity.worldObj.isRemote || !(event.source.damageType.equals("player") || event.source.damageType.equals("arrow")) || !isValidEntity(event.entityLiving)){ return; }
		EntityLivingBase entity = event.entityLiving;
		Entity attacker = event.source.getEntity();
		if (attacker == null || !(attacker instanceof EntityPlayer)) { return; }
		if (!(((EntityPlayer) attacker).getHeldItem().getItem().equals(ModItems.draconicSword) || ((EntityPlayer) attacker).getHeldItem().getItem().equals(ModItems.draconicDistructionStaff) || ((EntityPlayer) attacker).getHeldItem().getItem().equals(ModItems.draconicBow) || ((EntityPlayer) attacker).getHeldItem().getItem().equals(ModItems.wyvernBow) || ((EntityPlayer) attacker).getHeldItem().getItem().equals(ModItems.wyvernSword))) { return; }
		World world = entity.worldObj;
		int rand = random.nextInt(ConfigHandler.soulDropChance);
		if (rand == 0) {
			ItemStack soul = new ItemStack(ModItems.mobSoul);
			ItemNBTHelper.setString(soul, "Name", entity.getCommandSenderName());
			world.spawnEntityInWorld(new EntityItem(world, entity.posX, entity.posY, entity.posZ, soul));
		}
	}

	private boolean isValidEntity(EntityLivingBase entity)
	{
		if (entity instanceof IBossDisplayData) { return false; }
		for (int i = 0; i < ConfigHandler.spawnerList.length; i++)
		{
			if (ConfigHandler.spawnerList[i].equals(entity.getCommandSenderName()) && ConfigHandler.spawnerListType)
			{
				return true;
			} else if (ConfigHandler.spawnerList[i].equals(entity.getCommandSenderName()) && !ConfigHandler.spawnerListType)
			{
				return false;
			}
		}
		if (ConfigHandler.spawnerListType) {
			return false;
		}else {
			return true;
		}
	}
	
}
