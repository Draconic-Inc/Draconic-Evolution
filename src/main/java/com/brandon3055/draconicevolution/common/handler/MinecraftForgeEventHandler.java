package com.brandon3055.draconicevolution.common.handler;


import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.achievements.Achievements;
import com.brandon3055.draconicevolution.common.entity.EntityCustomDragon;
import com.brandon3055.draconicevolution.common.entity.EntityDragonHeart;
import com.brandon3055.draconicevolution.common.entity.ExtendedPlayer;
import com.brandon3055.draconicevolution.common.items.armor.ArmorEffectHandler;
import com.brandon3055.draconicevolution.common.network.MountUpdatePacket;
import com.brandon3055.draconicevolution.common.tileentities.TileGrinder;
import com.brandon3055.draconicevolution.common.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.common.utills.LogHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.event.world.WorldEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

@SuppressWarnings("unused")
public class MinecraftForgeEventHandler {

	Random random = new Random();
	private static Method becomeAngryAt;

	@SubscribeEvent
	public void onLivingJumpEvent(LivingEvent.LivingJumpEvent event) {
		if (!(event.entityLiving instanceof EntityPlayer)) return;
		EntityPlayer player = (EntityPlayer) event.entityLiving;

		if (ArmorEffectHandler.getHasJumpBoost(player))
		{
			int i = ArmorEffectHandler.getJumpLevel(player);
			event.entityLiving.motionY += ArmorEffectHandler.getJumpMultiplier(player) * ((0.1f) * (2 + i));
		}
	}

	@SubscribeEvent
	public void onEntityDamaged(LivingAttackEvent event) {
		if (!(event.entityLiving instanceof EntityPlayer)) return;

		EntityPlayer player = (EntityPlayer) event.entityLiving;
		if (event.source.isFireDamage() && ArmorEffectHandler.getFireImunity(player))
		{
			event.setCanceled(true);
			event.entityLiving.extinguish();
		}

		if (event.source.damageType.equals("fall") && ArmorEffectHandler.getHasJumpBoost(player))
		{
			if (event.ammount < (ArmorEffectHandler.getJumpLevel(player) + 1) * 2) event.setCanceled(true);
		}

		if ((event.source.damageType.equals("inWall") || event.source.damageType.equals("drown")) && (ArmorEffectHandler.isWyvernArmor(player, 4) || ArmorEffectHandler.isDraconicArmor(player, 4)))
		{
			if (event.ammount <= 2f) event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onDropEvent(LivingDropsEvent event) {

		if (!event.entity.worldObj.isRemote && ((event.entity instanceof EntityDragon) || (EntityList.getEntityString(event.entity) != null && !EntityList.getEntityString(event.entity).isEmpty() && EntityList.getEntityString(event.entity).equals("HardcoreEnderExpansion.Dragon"))))
		{
			EntityItem item = new EntityItem(event.entity.worldObj, event.entity.posX, event.entity.posY, event.entity.posZ, new ItemStack(ModItems.dragonHeart));
			event.entity.worldObj.spawnEntityInWorld(new EntityDragonHeart(event.entity.worldObj, ((int) event.entity.posX) + 0.5, event.entity.posY, ((int) event.entity.posZ) + 0.5));
			if (event.entity instanceof EntityCustomDragon && ((EntityCustomDragon) event.entity).getIsUber())
				event.entity.worldObj.spawnEntityInWorld(new EntityDragonHeart(event.entity.worldObj, event.entity.posX, event.entity.posY + 2, event.entity.posZ));

			for (Object o : event.entity.worldObj.playerEntities)
			{
				LogHelper.info(o);
				if (o instanceof EntityPlayer)
					((EntityPlayer) o).addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("msg.de.dragonDeath.txt")));

			}

			int count = 30 + event.entity.worldObj.rand.nextInt(30);
			for (int i = 0; i < count; i++)
			{
				float mm = 0.3F;
				EntityItem item2 = new EntityItem(event.entity.worldObj, event.entity.posX - 2 + event.entity.worldObj.rand.nextInt(4), event.entity.posY - 2 + event.entity.worldObj.rand.nextInt(4), event.entity.posZ - 2 + event.entity.worldObj.rand.nextInt(4), new ItemStack(ModItems.draconiumDust));
				item.motionX = mm * ((((float) event.entity.worldObj.rand.nextInt(100)) / 100F) - 0.5F);
				item.motionY = mm * ((((float) event.entity.worldObj.rand.nextInt(100)) / 100F) - 0.5F);
				item.motionZ = mm * ((((float) event.entity.worldObj.rand.nextInt(100)) / 100F) - 0.5F);
				event.entity.worldObj.spawnEntityInWorld(item2);
			}
		}

		if (event.entity.worldObj.isRemote || !(event.source.damageType.equals("player") || event.source.damageType.equals("arrow")) || !isValidEntity(event.entityLiving))
		{
			return;
		}

		EntityLivingBase entity = event.entityLiving;
		Entity attacker = event.source.getEntity();

		if (attacker == null || !(attacker instanceof EntityPlayer))
		{
			return;
		}

		int dropChanceModifier = getDropChanceFromItem(((EntityPlayer) attacker).getHeldItem());

		if (dropChanceModifier == 0) return;

		World world = entity.worldObj;
		int rand = random.nextInt(Math.max(ConfigHandler.soulDropChance / dropChanceModifier, 1));
		int rand2 = random.nextInt(Math.max(ConfigHandler.passiveSoulDropChance / dropChanceModifier, 1));
		boolean isAnimal = entity instanceof EntityAnimal;

		if ((rand == 0 && !isAnimal) || (rand2 == 0 && isAnimal))
		{
			ItemStack soul = new ItemStack(ModItems.mobSoul);
			String name = EntityList.getEntityString(entity);
			ItemNBTHelper.setString(soul, "Name", name);
			if (entity instanceof EntitySkeleton)
				ItemNBTHelper.setInteger(soul, "SkeletonType", ((EntitySkeleton) entity).getSkeletonType());
			world.spawnEntityInWorld(new EntityItem(world, entity.posX, entity.posY, entity.posZ, soul));
			Achievements.triggerAchievement((EntityPlayer) attacker, "draconicevolution.soul");
		}
	}

	private int getDropChanceFromItem(ItemStack stack) {
		int chance = 0;
		if (stack == null) return 0;
		if (stack.getItem().equals(ModItems.wyvernBow) || stack.getItem().equals(ModItems.wyvernSword)) chance++;
		if (stack.getItem().equals(ModItems.draconicSword) || stack.getItem().equals(ModItems.draconicBow)) chance += 2;
		if (stack.getItem().equals(ModItems.draconicDestructionStaff)) chance += 3;

		chance += EnchantmentHelper.getEnchantmentLevel(ConfigHandler.reaperEnchantID, stack);
		return chance;
	}

	private boolean isValidEntity(EntityLivingBase entity) {
		if (entity instanceof IBossDisplayData)
		{
			return false;
		}
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
		if (ConfigHandler.spawnerListType)
		{
			return false;
		} else
		{
			return true;
		}
	}

	private boolean changeBlock(ExtendedBlockStorage storage, int x, int y, int z) {

		if (storage.getBlockByExtId(x, y, z).getUnlocalizedName().equals("tile.tile.particleGenerator"))
			return makeChange(storage, x, y, z, ModBlocks.particleGenerator);
		else if (storage.getBlockByExtId(x, y, z).getUnlocalizedName().equals("tile.tile.customSpawner"))
			return makeChange(storage, x, y, z, ModBlocks.customSpawner);
		else if (storage.getBlockByExtId(x, y, z).getUnlocalizedName().equals("tile.tile.generator"))
			return makeChange(storage, x, y, z, ModBlocks.generator);
		else if (storage.getBlockByExtId(x, y, z).getUnlocalizedName().equals("tile.tile.playerDetectorAdvanced"))
			return makeChange(storage, x, y, z, ModBlocks.playerDetectorAdvanced);
		else if (storage.getBlockByExtId(x, y, z).getUnlocalizedName().equals("tile.tile.energyInfuser"))
			return makeChange(storage, x, y, z, ModBlocks.energyInfuser);
		else if (storage.getBlockByExtId(x, y, z).getUnlocalizedName().equals("tile.tile.draconiumOre"))
			return makeChange(storage, x, y, z, ModBlocks.draconiumOre);
		else if (storage.getBlockByExtId(x, y, z).getUnlocalizedName().equals("tile.tile.weatherController"))
			return makeChange(storage, x, y, z, ModBlocks.weatherController);
		else if (storage.getBlockByExtId(x, y, z).getUnlocalizedName().equals("tile.tile.longRangeDislocator"))
			return makeChange(storage, x, y, z, ModBlocks.longRangeDislocator);
		else if (storage.getBlockByExtId(x, y, z).getUnlocalizedName().equals("tile.tile.grinder"))
			return makeChange(storage, x, y, z, ModBlocks.grinder);
		return false;
	}

	private boolean makeChange(ExtendedBlockStorage storage, int x, int y, int z, Block block) {
		if (block == null) return false;
		LogHelper.info("Changing block at [X:" + x + " Y:" + y + " Z:" + z + "] from " + storage.getBlockByExtId(x, y, z).getUnlocalizedName() + " to " + block.getUnlocalizedName());
		storage.func_150818_a(x, y, z, block);
		return true;
	}

	@SubscribeEvent
	public void onEntityConstructing(EntityEvent.EntityConstructing event) {
		if (event.entity instanceof EntityPlayer && ExtendedPlayer.get((EntityPlayer) event.entity) == null)
			ExtendedPlayer.register((EntityPlayer) event.entity);
	}

	@SubscribeEvent
	public void itemTooltipEvent(ItemTooltipEvent event) {
		if (ConfigHandler.showUnlocalizedNames) event.toolTip.add(event.itemStack.getUnlocalizedName());
		if (DraconicEvolution.debug && event.itemStack.hasTagCompound()){
			String s = event.itemStack.getTagCompound().toString();
			int escape = 0;
			while (s.contains(",")){
				event.toolTip.add(s.substring(0, s.indexOf(",")+1));
				s = s.substring(s.indexOf(",")+1, s.length());

				if (escape++ >= 100) break;
			}
			event.toolTip.add(s);
		}
	}

	@SubscribeEvent
	public void stopUsingEvent(PlayerUseItemEvent.Start event) {
		if (!ConfigHandler.pigmenBloodRage || event.item == null || event.item.getItem() == null) return;
		if (event.item.getItem() == Items.porkchop || event.item.getItem() == Items.cooked_porkchop)
		{
			World world = event.entityPlayer.worldObj;
			if (world.isRemote) return;
			EntityPlayer player = event.entityPlayer;
			List list = world.getEntitiesWithinAABB(EntityPigZombie.class, AxisAlignedBB.getBoundingBox(player.posX - 32, player.posY - 32, player.posZ - 32, player.posX + 32, player.posY + 32, player.posZ + 32));

			EntityZombie entityAtPlayer = new EntityPigZombie(world);
			entityAtPlayer.setPosition(player.posX, player.posY, player.posZ);

			boolean flag = false;

			for (Object o : list)
			{
				if (o instanceof EntityPigZombie)
				{
					EntityPigZombie zombie = (EntityPigZombie) o;
					if (becomeAngryAt == null)
					{
						becomeAngryAt = ReflectionHelper.findMethod(EntityPigZombie.class, zombie, new String[]{"becomeAngryAt", "func_70835_c"}, Entity.class);
						becomeAngryAt.setAccessible(true);
					}

					try
					{
						becomeAngryAt.invoke(zombie, player);
					}
					catch (IllegalAccessException e)
					{
						e.printStackTrace();
					}
					catch (InvocationTargetException e)
					{
						e.printStackTrace();
					}

					if (Math.abs(zombie.posX - player.posX) < 14 && Math.abs(zombie.posY - player.posY) < 14 && Math.abs(zombie.posZ - player.posZ) < 14)
						flag = true;
					zombie.addPotionEffect(new PotionEffect(5, 10000, 3));
					zombie.addPotionEffect(new PotionEffect(11, 10000, 2));
				}
			}

			if (flag) player.addPotionEffect(new PotionEffect(2, 500, 3));
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void joinWorld(EntityJoinWorldEvent event) {
		if (event.entity instanceof EntityPlayerSP)
		{
			DraconicEvolution.network.sendToServer(new MountUpdatePacket(0));
		}
	}

	@SubscribeEvent
	public void getBreakSpeed(PlayerEvent.BreakSpeed event) {
		if (event.entityPlayer != null)
		{
			float newDigSpeed = event.originalSpeed;
			if (event.entityPlayer.isInsideOfMaterial(Material.water))
			{
				if (ArmorEffectHandler.isDraconicArmor(event.entityPlayer, 4)) newDigSpeed *= 5f;
			}
			if (!event.entityPlayer.onGround)
			{
				if (ArmorEffectHandler.isDraconicArmor(event.entityPlayer, 3)) newDigSpeed *= 5f;
			}
			event.newSpeed = newDigSpeed;
		}
	}

	@SubscribeEvent
	public void worldUnload(WorldEvent.Unload e) {
		TileGrinder.fakePlayer = null;
	}
}
