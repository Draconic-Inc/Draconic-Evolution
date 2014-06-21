package com.brandon3055.draconicevolution.common.items.tools;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.brandon3055.draconicevolution.client.interfaces.GuiHandler;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.core.helper.ItemNBTHelper;
import com.brandon3055.draconicevolution.common.core.utills.Teleporter;
import com.brandon3055.draconicevolution.common.items.ModItems;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;

public class TeleporterMKII extends Item
{

	public TeleporterMKII() {
		this.setUnlocalizedName(Strings.teleporterMKIIName);
		this.setCreativeTab(DraconicEvolution.getCreativeTab(1));
		this.setMaxStackSize(1);
		GameRegistry.registerItem(this, Strings.teleporterMKIIName);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(final IIconRegister iconRegister)
	{
		this.itemIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + Strings.teleporterMKIIName);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
	{
		short selection = ItemNBTHelper.getShort(stack, "Selection", (short) 0);

		double x = ItemNBTHelper.getDouble(stack, "X_" + selection, 0);
		double y = ItemNBTHelper.getDouble(stack, "Y_" + selection, 0);
		double z = ItemNBTHelper.getDouble(stack, "Z_" + selection, 0);
		boolean isSet = ItemNBTHelper.getBoolean(stack, "IsSet_" + selection, false);
		int dim = ItemNBTHelper.getIntager(stack, "Dimension_" + selection, 0);
		int fuel = ItemNBTHelper.getIntager(stack, "Fuel", 0);

		if (entity instanceof EntityPlayer || entity instanceof IBossDisplayData)
		{
			return true;
		}

		if (entity.dimension != dim)
		{
			if (player.worldObj.isRemote)
				player.addChatMessage(new ChatComponentTranslation("msg.teleporterEntityDimensional.txt"));
			return true;
		}

		if (!isSet)
		{
			if (player.worldObj.isRemote)
				player.addChatMessage(new ChatComponentTranslation("msg.teleporterUnSet.txt"));
			return true;
		}

		if (!player.capabilities.isCreativeMode && fuel <= 0)
		{
			if (player.worldObj.isRemote)
				player.addChatMessage(new ChatComponentTranslation("msg.teleporterOutOfFuel.txt"));
			return true;
		}
		
		if (!player.capabilities.isCreativeMode && fuel > 0)
			ItemNBTHelper.setIntager(stack, "Fuel", fuel - 1);
		
		travelEffect(player.worldObj, entity);
		entity.setPosition(x, y, z);
		travelEffect(player.worldObj, entity);
		if (player.worldObj.isRemote)
			player.addChatMessage(new ChatComponentText(new ChatComponentTranslation("msg.teleporterSentMob.txt").getFormattedText() + "x:" + (int) x + " y:" + (int) y + " z:" + (int) z));

		return true;
	}

	@Override
	public ItemStack onItemRightClick(final ItemStack stack, final World world, final EntityPlayer player)
	{
		short selection = ItemNBTHelper.getShort(stack, "Selection", (short) 0);

		double x = ItemNBTHelper.getDouble(stack, "X_" + selection, 0);
		double y = ItemNBTHelper.getDouble(stack, "Y_" + selection, 0);
		double z = ItemNBTHelper.getDouble(stack, "Z_" + selection, 0);
		boolean isSet = ItemNBTHelper.getBoolean(stack, "IsSet_" + selection, false);
		float yaw = ItemNBTHelper.getFloat(stack, "Yaw_" + selection, 0);
		float pitch = ItemNBTHelper.getFloat(stack, "Pitch_" + selection, 0);
		int dim = ItemNBTHelper.getIntager(stack, "Dimension_" + selection, 0);
		int fuel = ItemNBTHelper.getIntager(stack, "Fuel", 0);

		if (player.isSneaking())
		{
			if (world.isRemote)
			{
				FMLNetworkHandler.openGui(player, DraconicEvolution.instance, GuiHandler.GUIID_TELEPORTER, world, (int) player.posX, (int) player.posY, (int) player.posZ);
			}
		} else
		{
			if (player.isRiding())
			{
				return stack;
			}
			
			if (!isSet)
			{
				if (world.isRemote)
					player.addChatMessage(new ChatComponentTranslation("msg.teleporterUnSet.txt"));
				return stack;
			}

			if (!player.capabilities.isCreativeMode && fuel <= 0)
			{
				if (world.isRemote)
					player.addChatMessage(new ChatComponentTranslation("msg.teleporterOutOfFuel.txt"));
				return stack;
			}

			if (!player.capabilities.isCreativeMode && fuel > 0)
				ItemNBTHelper.setIntager(stack, "Fuel", fuel - 1);
		
			travelEffect(world, player);
			Teleporter.teleport(player, x, y, z, yaw, pitch, dim);
			player.fallDistance = 0;
			travelEffect(world, player);
		}

		return stack;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(final ItemStack stack, final EntityPlayer player, final List list, final boolean extraInformation)
	{
		int selected = ItemNBTHelper.getShort(stack, "Selection", (short) 0);
			list.add(EnumChatFormatting.WHITE + ItemNBTHelper.getString(stack, "Dest_" + selected, "Destination " + selected));
			list.add(EnumChatFormatting.WHITE + StatCollector.translateToLocal("info.teleporterInfFuel.txt") + " " + ItemNBTHelper.getIntager(stack, "Fuel", 0));
	}

	public void travelEffect(World world, Entity entity)
	{
		entity.worldObj.playSoundEffect(entity.posX, entity.posY, entity.posZ, "portal.travel", 0.1F, entity.worldObj.rand.nextFloat() * 0.1F + 0.9F);
		/*
		for (int i = 0; i < 100; i++)
		{
			if (entity instanceof EntityPlayer)
			{
				world.spawnParticle("portal", entity.posX + (world.rand.nextFloat() - 0.5), entity.posY + (world.rand.nextFloat() - 0.5), entity.posZ + (world.rand.nextFloat() - 0.5), 0D, 0D, 0D);
				world.spawnParticle("portal", entity.posX + (world.rand.nextFloat() - 0.5), entity.posY - 1 + (world.rand.nextFloat() - 0.5), entity.posZ + (world.rand.nextFloat() - 0.5), 0D, 0D, 0D);
			} else
			{
				world.spawnParticle("portal", entity.posX + (world.rand.nextFloat() - 0.5), entity.posY + (world.rand.nextFloat() - 0.5), entity.posZ + (world.rand.nextFloat() - 0.5), 0D, 0D, 0D);
				world.spawnParticle("portal", entity.posX + (world.rand.nextFloat() - 0.5), entity.posY + 1 + (world.rand.nextFloat() - 0.5), entity.posZ + (world.rand.nextFloat() - 0.5), 0D, 0D, 0D);
			}
		}
		*/
	}

	@Override
	public EnumRarity getRarity(ItemStack stack)
	{
		return EnumRarity.uncommon;
	}

	public static void registerRecipe()
	{
		CraftingManager.getInstance().addRecipe(new ItemStack(ModItems.teleporterMKII), "BIB", "DED", "BIB", 'D', ModItems.draconicCore, 'E', ModItems.teleporterMKI, 'B', ModItems.draconiumIngot, 'I', ModItems.infusedCompound);
	}

}
