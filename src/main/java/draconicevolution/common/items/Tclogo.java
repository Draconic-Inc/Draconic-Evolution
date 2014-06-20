package draconicevolution.common.items;

import java.util.List;

import draconicevolution.common.items.tools.ToolHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import draconicevolution.common.core.handler.ParticleHandler;
import draconicevolution.common.lib.Strings;

public class Tclogo extends TolkienItem {
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
		//if (world.isRemote)
			//ParticleHandler.spawnParticle("testParticle", player.posX, player.posY, player.posZ, 0, 0, 0, 1);
		return stack;
	}
	
	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, int X, int Y, int Z, EntityPlayer player)
	{
		//player.worldObj.scheduleBlockUpdate(X, Y, Z, player.worldObj.getBlock(X, Y, Z), 10);
		MovingObjectPosition mop = ToolHandler.raytraceFromEntity(player.worldObj, player, true, 4.5D);
		System.out.println(mop.sideHit);
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
