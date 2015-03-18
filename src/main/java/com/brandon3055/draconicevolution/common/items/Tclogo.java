package com.brandon3055.draconicevolution.common.items;

import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.utills.LogHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
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


		if (world.isRemote)
		{
			ResourceHandler.init(null);
			String str = "A String";
			IChatComponent localIChatComponent;


			localIChatComponent = IChatComponent.Serializer.func_150699_a("[{\"text\":\"" + str + "\",\"color\":\"aqua\"}," + "{\"text\":\" " + EnumChatFormatting.WHITE + "[" + EnumChatFormatting.GREEN +
					"info.cofh.updater.download" + EnumChatFormatting.WHITE + "]\"," + "\"color\":\"green\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":" + "{\"text\":\"" +

					"info.cofh.updater.tooltip" + ".\",\"color\":\"yellow\"}}," + "\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" + "www.google.com" + "\"}}]");

			//player.addChatMessage(localIChatComponent);
		}
		else
		{
			//for (Object o : EntityList.classToStringMapping.values()) LogHelper.info(o);
		}


		if (!player.isSneaking())
		{
			player.capabilities.allowFlying = true;
			//player.setPosition(player.posX, player.posY+1, player.posZ);
			player.onGround = false;
			player.capabilities.isFlying = true;
			player.noClip = !player.noClip;

		}
		else
		{
			int xi = (int)player.posX;
			int yi = (int)player.posY;
			int zi = (int)player.posZ;
			int rad = 100;

			for (int x = xi-rad; x < xi+rad; x++){
				for (int y = yi-10; y < yi+30; y++){
					for (int z = zi-rad; z < zi+rad; z++){
						world.markBlockForUpdate(x, y, z);
					}
				}
			}


			world.markBlockRangeForRenderUpdate(xi-rad, yi-20, zi-rad, xi+rad, yi+20, zi+rad);
		}
//
//		LogHelper.info("Downloading Image");
//
//		try {
//			URL url = new URL("http://i.imgur.com/oHRx1yQ.jpg");
//			String fileName = url.getFile();
//			String destName = ClientProxy.downloadLocation + fileName.substring(fileName.lastIndexOf("/"));
//			System.out.println(destName);
//
//			InputStream is = url.openStream();
//			OutputStream os = new FileOutputStream(destName);
//
//			byte[] b = new byte[2048];
//			int length;
//
//			while ((length = is.read(b)) != -1) {
//				os.write(b, 0, length);
//			}
//
//			is.close();
//			os.close();
//		}catch (IOException e){
//			LogHelper.info(e);
//		}




//		player.addPotionEffect(new PotionEffect(PotionHandler.potionFlight.id, 100, 0));
//		player.addPotionEffect(new PotionEffect(PotionHandler.potionFireResist.id, 100, 1));
//		player.addPotionEffect(new PotionEffect(PotionHandler.potionSpeed.id, 100, 1));
//		player.addPotionEffect(new PotionEffect(PotionHandler.potionUpHillStep.id, 100, 1));
		int xi = (int)player.posX;
		int yi = (int)player.posY;
		int zi = (int)player.posZ;
		int rad = 1000;

//		Block block;
//		for (int x = xi-rad; x < xi+rad; x++){
//			for (int y = yi-10; y < yi+30; y++){
//				for (int z = zi-rad; z < zi+rad; z++){
//				//	block = world.getBlock(x, y, z);
//					//if (block.getMaterial().equals(Material.vine) || block.getMaterial().equals(Material.plants)){
//						//world.setBlockToAir(x, y, z);
//					//}
//
//					//world.markBlockForUpdate(x, y, z);
//				}
//			}
//		}


		//world.markBlockRangeForRenderUpdate(xi-rad, yi-rad, zi-rad, xi+rad, yi+rad, zi+rad);
		//if (world.isRemote)player.displayGUIWorkbench((int)player.posX, (int)player.posY, (int)player.posZ);
		//world.setBlock(0, 0, 0, Blocks.crafting_table);
		//if (!world.isRemote)player.displayGUIWorkbench(0, 0, 0);


		return stack;
	}
	
	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, int X, int Y, int Z, EntityPlayer player)
	{
		LogHelper.info(player.worldObj.getBlock(X, Y, Z).getHarvestLevel(player.worldObj.getBlockMetadata(X, Y, Z)));
		//player.worldObj.scheduleBlockUpdate(X, Y, Z, player.worldObj.getBlock(X, Y, Z), 10);

		return false;
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
