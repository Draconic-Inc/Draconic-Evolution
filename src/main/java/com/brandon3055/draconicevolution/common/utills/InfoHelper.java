package com.brandon3055.draconicevolution.common.utills;

import cofh.api.energy.IEnergyContainerItem;
import com.brandon3055.draconicevolution.common.handler.ConfigHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Keyboard;

import java.util.List;

/**
 * Created by Brandon on 1/07/2014.
 */
public class InfoHelper {
	@SuppressWarnings("unchecked")
	public static void addEnergyInfo(ItemStack stack, List list) {
		IEnergyContainerItem item = (IEnergyContainerItem)stack.getItem();
		int energy = item.getEnergyStored(stack);
		int maxEnergy = item.getMaxEnergyStored(stack);
		String eS = "";
		String eM = "";
		if (energy < 1000)
			eS = String.valueOf(energy);
		else if (energy < 1000000)
			eS = String.valueOf(Math.round((float)energy / 10F)/100F)+"k";
		else
			eS = String.valueOf(Math.round((float)energy / 1000F)/1000F)+"m";
		if (maxEnergy < 1000)
			eM = String.valueOf(maxEnergy);
		else if (maxEnergy < 1000000)
			eM = String.valueOf(Math.round((float)maxEnergy / 100F)/10F)+"k";
		else
			eM = String.valueOf(Math.round((float)maxEnergy / 10000F)/100F)+"m";

		list.add(StatCollector.translateToLocal("info.de.charge.txt") + ": " + eS + " / " + eM + " RF");
	}

	@SuppressWarnings("unchecked")
	public static void addLore(ItemStack stack, List list, boolean addLeadingLine){
		if (ConfigHandler.disableLore) return;
		String[] lore = getLore(stack);
		if (addLeadingLine) list.add("");
		if (lore == null) {
			list.add("" + EnumChatFormatting.ITALIC + "" + EnumChatFormatting.DARK_PURPLE + "Invalid lore localization see console for details");
			return;
		}
		for (String s : lore) list.add("" + EnumChatFormatting.ITALIC + "" + EnumChatFormatting.DARK_PURPLE + s);
	}

	/**Add lore with a blank line above it*/
	public static void addLore(ItemStack stack, List list){
		addLore(stack, list, true);
	}

	/**Add the standard energy and lore information*/
	@SuppressWarnings("unchecked")
	public static void addEnergyAndLore(ItemStack stack, List list){
		if (!isShiftKeyDown()) list.add(StatCollector.translateToLocal("info.de.hold.txt")+" "+EnumChatFormatting.AQUA + "" + EnumChatFormatting.ITALIC + StatCollector.translateToLocal("info.de.shift.txt") + EnumChatFormatting.RESET + " " + EnumChatFormatting.GRAY + StatCollector.translateToLocal("info.de.forDetails.txt"));
		else {
			addEnergyInfo(stack, list);
			addLore(stack, list);
		}
	}

	/**returns lore text or an empty string if the lore is not set*/
	public static String[] getLore(ItemStack stack){
		String unlocalizeLore = stack.getItem().getUnlocalizedName() + ".lore";
		String rawLore = StatCollector.translateToLocal(unlocalizeLore);

		if (rawLore.contains(unlocalizeLore)){
			LogHelper.error("Invalid or missing Lore localization \""+unlocalizeLore+"\"");
			return null;
		}

		String lineCountS = rawLore.substring(0, 1);
		rawLore = rawLore.substring(1);
		int lineCount = 0;

		try {
			lineCount = Integer.parseInt(lineCountS);
		}catch (NumberFormatException e){
			LogHelper.error("Invalid Lore Format! Lore myst start with the number of lines \"3Line 1\\nLine 2\\nLine 3\"");
		}

		String[] loreLines = new String[lineCount];

		for (int i = 0; i < lineCount; i++){
			if (rawLore.contains("\\n")) loreLines[i] = rawLore.substring(0, rawLore.indexOf("\\n"));
			else loreLines[i] = rawLore;
			if (rawLore.contains("\\n")) rawLore = rawLore.substring(rawLore.indexOf("\\n")+2);
		}

		return loreLines;
	}

	public static boolean isShiftKeyDown() {
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
	}

	@SuppressWarnings("unchecked")
	public static boolean holdShiftForDetails(List list, boolean inverted){
		if (isShiftKeyDown() == inverted) list.add(StatCollector.translateToLocal("info.de.hold.txt")+" "+EnumChatFormatting.AQUA + "" + EnumChatFormatting.ITALIC + StatCollector.translateToLocal("info.de.shift.txt") + EnumChatFormatting.RESET + " " + EnumChatFormatting.GRAY + StatCollector.translateToLocal("info.de.forDetails.txt"));
		return isShiftKeyDown();
	}

	public static boolean holdShiftForDetails(List list){
		return holdShiftForDetails(list, false);
	}

	/**"Information Text Colour" The colour used for custom tool tip info*/
	public static String ITC(){return "" + EnumChatFormatting.RESET + "" + EnumChatFormatting.DARK_AQUA;}
	/**"Highlighted Information Text Colour" The colour used for parts that need to stand out*/
	public static String HITC(){return "" + EnumChatFormatting.RESET + "" + EnumChatFormatting.ITALIC + "" + EnumChatFormatting.GOLD;}
}
