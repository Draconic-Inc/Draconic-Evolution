package com.brandon3055.draconicevolution.common.items;

import com.brandon3055.draconicevolution.common.lib.Strings;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import java.util.List;

public class Tclogo extends ItemDE {
	public Tclogo() {
		this.setUnlocalizedName(Strings.tclogoName);
		//this.setCreativeTab(draconicevolution.getCreativeTab());
		ModItems.register(this);
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack)
	{
		return 100;
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5)
	{
		//if (entity.isCollidedHorizontally)// && !world.isRemote)
			//entity.setLocationAndAngles(entity.posX+30D, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);;
		/*
		//System.out.println("Update Tick");
		int X = (int)entity.posX;
		int Y = (int)entity.posY;
		int Z = (int)entity.posZ;
		
		entity.worldObj.spawnParticle("flame", 613, 5, -822, 0, 0, 0);
		if (((EntityPlayer)entity).getHeldItem() != null && ((EntityPlayer)entity).getHeldItem().isItemEqual(new ItemStack(ModItems.tclogo)))
		{
			
			for(int x = X - 5; x <= X + 5; x++)
			{
				for(int y = Y; y <= Y; y++)
				{
					for(int z = Z - 5; z <= Z + 5; z++)
					{
						entity.worldObj.spawnParticle("flame", x - 0.5, y - 0.5, z - 0.5, 0, 0, 0);
						entity.worldObj.scheduleBlockUpdate(x, y, z, entity.worldObj.getBlock(x, y, z), 1);
						System.out.println("Update Tick " +X+ " " +Y+ " " +Z);
					}
				}
			}
		}*/
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack)
	{
		return EnumAction.block;
	}
	
	@Override
	public ItemStack onEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        return par1ItemStack;
    }

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		int xi = (int)player.posX;
		int yi = (int)player.posY;
		int zi = (int)player.posZ;
		int rad = 100;

		for (int x = xi-rad; x < xi+rad; x++){
			for (int y = yi-5; y < yi+5; y++){
				for (int z = zi-rad; z < zi+rad; z++){
					world.markBlockForUpdate(x, y, z);
				}
			}
		}
		world.markBlockRangeForRenderUpdate(xi-rad, yi-rad, zi-rad, xi+rad, yi+rad, zi+rad);

		return stack;
	}
	
	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, int X, int Y, int Z, EntityPlayer player)
	{
		//player.worldObj.scheduleBlockUpdate(X, Y, Z, player.worldObj.getBlock(X, Y, Z), 10);

		return true;
	}
	
	@Override
	public void onUsingTick(ItemStack stack, EntityPlayer player, int count)
	{
		System.out.println("Use Tick");
		super.onUsingTick(stack, player, count);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addInformation(final ItemStack stack, final EntityPlayer player, final List list, final boolean extraInformation)
	{
		list.add(EnumChatFormatting.AQUA + "AQUA");
		list.add(EnumChatFormatting.BLACK + "BLACK");
		list.add(EnumChatFormatting.BLUE + "BLUE");
		list.add(EnumChatFormatting.DARK_AQUA + "DARK_AQUA");
		list.add(EnumChatFormatting.DARK_BLUE + "DARK_BLUE");
		list.add(EnumChatFormatting.DARK_GRAY + "DARK_GRAY");
		list.add(EnumChatFormatting.DARK_GREEN + "DARK_GREEN");
		list.add(EnumChatFormatting.DARK_PURPLE + "DARK_PURPLE");
		list.add(EnumChatFormatting.DARK_RED + "DARK_RED");
		list.add(EnumChatFormatting.GOLD + "GOLD");
		list.add(EnumChatFormatting.GRAY + "GRAY");
		list.add(EnumChatFormatting.GREEN + "GREEN");
		list.add(EnumChatFormatting.LIGHT_PURPLE + "LIGHT_PURPLE");
		list.add(EnumChatFormatting.RED + "RED");
		list.add(EnumChatFormatting.WHITE + "WHITE");
		list.add(EnumChatFormatting.YELLOW + "YELLOW");
		list.add(EnumChatFormatting.BOLD + "BOLD");
		list.add(EnumChatFormatting.ITALIC + "ITALIC");
		list.add(EnumChatFormatting.OBFUSCATED + "OBFUSCATED");
		list.add(EnumChatFormatting.UNDERLINE + "UNDERLINE");
		list.add(EnumChatFormatting.STRIKETHROUGH + "STRIKETHROUGH");
	}
}
