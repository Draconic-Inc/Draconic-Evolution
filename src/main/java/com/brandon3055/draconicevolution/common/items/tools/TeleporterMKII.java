package com.brandon3055.draconicevolution.common.items.tools;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.handler.ToolHudHandler;
import com.brandon3055.draconicevolution.client.interfaces.GuiHandler;
import com.brandon3055.draconicevolution.common.utills.Teleporter.TeleportLocation;
import com.brandon3055.draconicevolution.common.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.common.entity.EntityPersistentItem;
import com.brandon3055.draconicevolution.common.items.ItemDE;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class TeleporterMKII extends ItemDE
{

	public TeleporterMKII() {
		this.setUnlocalizedName(Strings.teleporterMKIIName);
		this.setCreativeTab(DraconicEvolution.tolkienTabToolsWeapons);
		this.setMaxStackSize(1);
		ModItems.register(this);
		//GameRegistry.registerItem(this, Strings.teleporterMKIIName);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(final IIconRegister iconRegister)
	{
		this.itemIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + Strings.teleporterMKIIName);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack teleporter, EntityPlayer player, Entity entity)
	{
		World world = player.worldObj;
		short selected = ItemNBTHelper.getShort(teleporter, "Selection", (short) 0);
		int selrctionOffset = ItemNBTHelper.getInteger(teleporter, "SelectionOffset", 0);
		int fuel = ItemNBTHelper.getInteger(teleporter, "Fuel", 0);

		NBTTagCompound compound = teleporter.getTagCompound();
		if(compound == null) compound = new NBTTagCompound();
		NBTTagList list = (NBTTagList)compound.getTag("Locations");
		if (list == null) list = new NBTTagList();

		TeleportLocation destination = new TeleportLocation();
		destination.readFromNBT(list.getCompoundTagAt(selected+selrctionOffset));

		if (!player.capabilities.isCreativeMode && fuel <= 0) {
			if (world.isRemote) player.addChatMessage(new ChatComponentTranslation("msg.teleporterOutOfFuel.txt"));
			return true;
		}

		if (entity instanceof EntityPlayer){
			if (entity.isSneaking()){
				destination.sendEntityToCoords((EntityPlayer) entity);
				if (!player.capabilities.isCreativeMode && fuel > 0) ItemNBTHelper.setInteger(teleporter, "Fuel", fuel - 1);
			}else{
				if (world.isRemote) player.addChatMessage(new ChatComponentTranslation("msg.teleporterPlayerConsent.txt"));
			}
			return true;
		}

		if (entity instanceof EntityLivingBase){
			destination.sendEntityToCoords((EntityLivingBase) entity);
			if (!player.capabilities.isCreativeMode && fuel > 0) ItemNBTHelper.setInteger(teleporter, "Fuel", fuel - 1);
		}

		return true;
	}

	@Override
	public ItemStack onItemRightClick(final ItemStack teleporter, final World world, final EntityPlayer player)
	{
		short selected = ItemNBTHelper.getShort(teleporter, "Selection", (short) 0);
		int selrctionOffset = ItemNBTHelper.getInteger(teleporter, "SelectionOffset", 0);
		int fuel = ItemNBTHelper.getInteger(teleporter, "Fuel", 0);

		NBTTagCompound compound = teleporter.getTagCompound();
		if(compound == null) compound = new NBTTagCompound();
		NBTTagList list = (NBTTagList)compound.getTag("Locations");
		if (list == null) list = new NBTTagList();

		TeleportLocation destination = new TeleportLocation();
		destination.readFromNBT(list.getCompoundTagAt(selected+selrctionOffset));

		boolean onStand = !(player.getHeldItem() != null && player.getHeldItem().getItem() instanceof TeleporterMKII);

		if (player.isSneaking())
		{
			if (world.isRemote)
			{
				FMLNetworkHandler.openGui(player, DraconicEvolution.instance, GuiHandler.GUIID_TELEPORTER, world, (int) player.posX, (int) player.posY, (int) player.posZ);
			}
		} else
		{
			if (player.isRiding() && player.dimension != destination.getDimension()) return teleporter;

			if (destination.getName().isEmpty() && !onStand){
				if (world.isRemote) FMLNetworkHandler.openGui(player, DraconicEvolution.instance, GuiHandler.GUIID_TELEPORTER, world, (int) player.posX, (int) player.posY, (int) player.posZ);
				return teleporter;
			}

			if (destination.getName().isEmpty()) return teleporter;

			if (!player.capabilities.isCreativeMode && fuel <= 0 && !onStand)
			{
				if (world.isRemote) player.addChatMessage(new ChatComponentTranslation("msg.teleporterOutOfFuel.txt"));
				return teleporter;
			}

			if (!player.capabilities.isCreativeMode && fuel > 0 && !onStand) ItemNBTHelper.setInteger(teleporter, "Fuel", fuel - 1);

			destination.sendEntityToCoords(player);
		}

		return teleporter;
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(final ItemStack teleporter, final EntityPlayer player, final List list2, final boolean extraInformation)
	{
		short selected = ItemNBTHelper.getShort(teleporter, "Selection", (short) 0);
		int selrctionOffset = ItemNBTHelper.getInteger(teleporter, "SelectionOffset", 0);
		NBTTagCompound compound = teleporter.getTagCompound();
		if(compound == null) compound = new NBTTagCompound();
		NBTTagList list = (NBTTagList)compound.getTag("Locations");
		if (list == null) list = new NBTTagList();
		String selectedDest = list.getCompoundTagAt(selected+selrctionOffset).getString("Name");

		list2.add(EnumChatFormatting.GOLD + "" + selectedDest);
		if ((!Keyboard.isKeyDown(42)) && (!Keyboard.isKeyDown(54))) {
			list2.add(EnumChatFormatting.DARK_GREEN + "Hold shift for information");
		}
		else {
			list2.add(EnumChatFormatting.WHITE + StatCollector.translateToLocal("info.teleporterInfFuel.txt") + " " + ItemNBTHelper.getInteger(teleporter, "Fuel", 0));
			list2.add(EnumChatFormatting.DARK_PURPLE + "" + EnumChatFormatting.ITALIC + StatCollector.translateToLocal("info.teleporterInfGUI.txt"));
			list2.add(EnumChatFormatting.DARK_PURPLE + "" + EnumChatFormatting.ITALIC + StatCollector.translateToLocal("info.teleporterInfScroll.txt"));
		}
	}

	@Override
	public EnumRarity getRarity(ItemStack stack)
	{
		return EnumRarity.rare;
	}

	public static void registerRecipe()
	{
		CraftingManager.getInstance().addRecipe(new ItemStack(ModItems.teleporterMKII), "BIB", "DED", "BIB", 'D', ModItems.draconicCore, 'E', ModItems.teleporterMKI, 'B', ModItems.draconiumIngot, 'I', ModItems.infusedCompound);
	}

	@Override
	public boolean hasCustomEntity(ItemStack stack) {
		return true;
	}

	@Override
	public Entity createEntity(World world, Entity location, ItemStack itemstack) {
		return new EntityPersistentItem(world, location, itemstack);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public String getItemStackDisplayName(ItemStack teleporter) {
		short selected = ItemNBTHelper.getShort(teleporter, "Selection", (short) 0);
		int selrctionOffset = ItemNBTHelper.getInteger(teleporter, "SelectionOffset", 0);
		NBTTagCompound compound = teleporter.getTagCompound();
		if(compound == null) compound = new NBTTagCompound();
		NBTTagList list = (NBTTagList)compound.getTag("Locations");
		if (list == null) list = new NBTTagList();
		String selectedDest = list.getCompoundTagAt(selected+selrctionOffset).getString("Name");
		ToolHudHandler.setTooltip(selectedDest);
		return super.getItemStackDisplayName(teleporter);
	}
}
