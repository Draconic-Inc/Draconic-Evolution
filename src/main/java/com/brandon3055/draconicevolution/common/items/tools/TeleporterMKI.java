package com.brandon3055.draconicevolution.common.items.tools;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.core.helper.ItemNBTHelper;
import com.brandon3055.draconicevolution.common.core.utills.Teleporter;
import com.brandon3055.draconicevolution.common.items.ModItems;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;

public class TeleporterMKI extends Item
{

	public TeleporterMKI() {
		this.setUnlocalizedName(Strings.teleporterMKIName);
		this.setCreativeTab(DraconicEvolution.getCreativeTab(1));
		this.setMaxDamage(19);
		this.setMaxStackSize(1);
		GameRegistry.registerItem(this, Strings.teleporterMKIName);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(final IIconRegister iconRegister)
	{
		this.itemIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + Strings.teleporterMKIName);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
	{
		boolean isSet = ItemNBTHelper.getBoolean(stack, "IsSet", false);
		double x = ItemNBTHelper.getDouble(stack, "X", 0);
		double y = ItemNBTHelper.getDouble(stack, "Y", 0);
		double z = ItemNBTHelper.getDouble(stack, "Z", 0);
		int dim = ItemNBTHelper.getIntager(stack, "Dimension", 0);

		if (!(entity instanceof EntityPlayer) && !(entity instanceof IBossDisplayData))
		{
			if (entity.dimension == dim)
			{
				if (isSet)
				{
					if (player.getHealth() > 2 || player.capabilities.isCreativeMode)
					{
						stack.damageItem(1, player);
						if (!player.capabilities.isCreativeMode)
							player.setHealth(player.getHealth() - 2);
						travelEffect(player.worldObj, entity);
						entity.setPosition(x, y, z);
						travelEffect(player.worldObj, entity);
						if (player.worldObj.isRemote)
							player.addChatMessage(new ChatComponentText(new ChatComponentTranslation("msg.teleporterSentMob.txt").getFormattedText() + "x:" + (int) x + " y:" + (int) y + " z:" + (int) z));
					} else if (player.worldObj.isRemote)
						player.addChatMessage(new ChatComponentTranslation("msg.teleporterLowHealth.txt"));
				} else if (player.worldObj.isRemote)
					player.addChatMessage(new ChatComponentTranslation("msg.teleporterUnSet.txt"));

			} else if (player.worldObj.isRemote)
			{
				player.addChatMessage(new ChatComponentTranslation("msg.teleporterEntityDimensional.txt"));
			}
		}

		return true;
	}

	@Override
	public ItemStack onItemRightClick(final ItemStack stack, final World world, final EntityPlayer player)
	{
		boolean isSet = ItemNBTHelper.getBoolean(stack, "IsSet", false);
		double x = ItemNBTHelper.getDouble(stack, "X", 0);
		double y = ItemNBTHelper.getDouble(stack, "Y", 0);
		double z = ItemNBTHelper.getDouble(stack, "Z", 0);
		float yaw = ItemNBTHelper.getFloat(stack, "Yaw", 0);
		float pitch = ItemNBTHelper.getFloat(stack, "Pitch", 0);
		int dim = ItemNBTHelper.getIntager(stack, "Dimension", 0);

		if (player.isSneaking())
		{
			if (!isSet)
			{
				ItemNBTHelper.setDouble(stack, "X", player.posX);
				ItemNBTHelper.setDouble(stack, "Y", player.posY);
				ItemNBTHelper.setDouble(stack, "Z", player.posZ);
				ItemNBTHelper.setFloat(stack, "Yaw", player.rotationYaw);
				ItemNBTHelper.setFloat(stack, "Pitch", player.rotationPitch);
				ItemNBTHelper.setIntager(stack, "Dimension", player.dimension);
				ItemNBTHelper.setBoolean(stack, "IsSet", true);
				if (world.isRemote)
					player.addChatMessage(new ChatComponentText(new ChatComponentTranslation("msg.teleporterBound.txt").getFormattedText() + "{X:" + (int) player.posX + " Y:" + (int) player.posY + " Z:" + (int) player.posZ + " Dim:" + player.worldObj.provider.getDimensionName() + "}"));

			} else if (world.isRemote)
				player.addChatMessage(new ChatComponentTranslation("msg.teleporterAlreadySet.txt"));
		} else
		{
			if (isSet)
			{
				if ((player.getHealth() > 2 || player.capabilities.isCreativeMode) && !player.isRiding())
				{
					stack.damageItem(1, player);
					if (!player.capabilities.isCreativeMode)
						player.setHealth(player.getHealth() - 2);

					travelEffect(world, player);
					Teleporter.teleport(player, x, y, z, yaw, pitch, dim);
					travelEffect(world, player);

				} else if (world.isRemote && player.getHealth() <= 2 && !player.capabilities.isCreativeMode)
					player.addChatMessage(new ChatComponentTranslation("msg.teleporterLowHealth.txt"));
			} else if (world.isRemote)
				player.addChatMessage(new ChatComponentTranslation("msg.teleporterUnSet.txt"));
		}

		return stack;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(final ItemStack stack, final EntityPlayer player, final List list, final boolean extraInformation)
	{
		if (!ItemNBTHelper.getBoolean(stack, "IsSet", false))
		{
			list.add(EnumChatFormatting.RED + StatCollector.translateToLocal("info.teleporterInfUnset1.txt"));
			list.add(EnumChatFormatting.WHITE + StatCollector.translateToLocal("info.teleporterInfUnset2.txt"));
			list.add(EnumChatFormatting.WHITE + StatCollector.translateToLocal("info.teleporterInfUnset3.txt"));
			list.add(EnumChatFormatting.WHITE + StatCollector.translateToLocal("info.teleporterInfUnset4.txt"));
			list.add(EnumChatFormatting.WHITE + StatCollector.translateToLocal("info.teleporterInfUnset5.txt"));
		} else
		{
			list.add(EnumChatFormatting.GREEN + StatCollector.translateToLocal("info.teleporterInfSet1.txt"));
			list.add(EnumChatFormatting.WHITE + "{x:" + (int) ItemNBTHelper.getDouble(stack, "X", 0) + " y:" + (int) ItemNBTHelper.getDouble(stack, "Y", 0) + " z:" + (int) ItemNBTHelper.getDouble(stack, "Z", 0) + " Dim:" + WorldProvider.getProviderForDimension(ItemNBTHelper.getIntager(stack, "Dimension", 0)).getDimensionName() + "}");
			list.add(EnumChatFormatting.BLUE + String.valueOf(stack.getMaxDamage() - stack.getItemDamage() + 1) + " " + StatCollector.translateToLocal("info.teleporterInfSet2.txt"));
		}
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
		CraftingManager.getInstance().addRecipe(new ItemStack(ModItems.teleporterMKI), "BDB", "DED", "BDB", 'D', ModItems.draconiumDust, 'E', Items.ender_eye, 'B', Items.blaze_powder);
	}

}
