package com.brandon3055.draconicevolution.common.items.tools;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.items.ItemDE;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.utills.IHudDisplayItem;
import com.brandon3055.draconicevolution.common.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.common.utills.UpdateChecker;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brandon on 23/08/2014.
 */
public class Wrench extends ItemDE implements IHudDisplayItem{

	public static final String BIND_MODE = "bind";
	public static final String UNBIND_MODE = "unBind";
	public static final String CLEAR_BINDINGS = "unBindAll";
	public static final String MODE_SWITCH = "modeSwitch";

	public Wrench()
	{
		this.setUnlocalizedName(Strings.wrenchName);
		this.setCreativeTab(DraconicEvolution.tabToolsWeapons);
		this.setMaxStackSize(1);
		ModItems.register(this);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {

		new UpdateChecker();

		if (player.isSneaking()) cycleMode(stack, world, player);
		else if (ItemNBTHelper.getCompound(stack).hasKey("LinkData") && ItemNBTHelper.getCompound(stack).getCompoundTag("LinkData").getBoolean("Bound")) ItemNBTHelper.getCompound(stack).getCompoundTag("LinkData").setBoolean("Bound", false);
		return super.onItemRightClick(stack, world, player);
	}

	static final String[] modes = new String[] {BIND_MODE, UNBIND_MODE, CLEAR_BINDINGS, MODE_SWITCH};

	private static void cycleMode(ItemStack stack, World world, EntityPlayer player)
	{
		String currentMode = ItemNBTHelper.getString(stack, "Mode", "bind");
		int mode = 0;
		for (String s : modes)
		{
			if (s.equals(currentMode))
			{
				if (mode + 1 >= modes.length) currentMode = modes[0];
				else currentMode = modes[mode + 1];
				break;
			}
			mode++;
		}
		ItemNBTHelper.setString(stack, "Mode", currentMode);
		if (world.isRemote) player.addChatComponentMessage(new ChatComponentTranslation("msg.de.wrenchMode." + currentMode + ".txt"));
	}

	@SuppressWarnings("unchecked")
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer p_77624_2_, List list, boolean bool) {
		list.add(StatCollector.translateToLocal("msg.de.wrenchMode." + ItemNBTHelper.getString(stack, "Mode", "bind") + ".txt"));
		NBTTagCompound linkDat = null;
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("LinkData")) linkDat = stack.getTagCompound().getCompoundTag("LinkData");
		if (linkDat != null && linkDat.getBoolean("Bound"))
		{
			list.add(StatCollector.translateToLocal("msg.de.boundTo.txt") + ": [X:" + linkDat.getInteger("XCoord") + ", Y:" + linkDat.getInteger("YCoord") + ", Z:" + linkDat.getInteger("ZCoord") + "]");
			list.add(StatCollector.translateToLocal("msg.de.rightClickUnbind.txt"));
		}
	}

	@Override
	public List<String> getDisplayData(ItemStack stack) {
		List<String> list = new ArrayList<String>();
		list.add(StatCollector.translateToLocal("msg.de.wrenchMode." + ItemNBTHelper.getString(stack, "Mode", "bind") + ".txt"));
		NBTTagCompound linkDat = null;
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("LinkData")) linkDat = stack.getTagCompound().getCompoundTag("LinkData");
		if (linkDat != null && linkDat.getBoolean("Bound"))
		{
			list.add(StatCollector.translateToLocal("msg.de.boundTo.txt") + ": [X:" + linkDat.getInteger("XCoord") + ", Y:" + linkDat.getInteger("YCoord") + ", Z:" + linkDat.getInteger("ZCoord") + "]");
			list.add(StatCollector.translateToLocal("msg.de.rightClickUnbind.txt"));
		}
		return list;
	}

	public static String getMode(ItemStack stack)
	{
		return ItemNBTHelper.getString(stack, "Mode", "bind");
	}
}
