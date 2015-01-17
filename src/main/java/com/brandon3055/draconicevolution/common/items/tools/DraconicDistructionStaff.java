package com.brandon3055.draconicevolution.common.items.tools;

import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.items.tools.baseclasses.MiningTool;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.utills.ItemConfigField;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import java.util.List;

public class DraconicDistructionStaff extends MiningTool {


	public DraconicDistructionStaff() {
		super(ModItems.DRACONIUM_T3);
		this.setUnlocalizedName(Strings.draconicDStaffName);
		this.setHarvestLevel("pickaxe", 10);
		this.setHarvestLevel("shovel", 10);
		this.setHarvestLevel("axe", 10);
		this.setCapacity(References.DRACONICCAPACITY * 3);
		this.setMaxExtract(References.DRACONICTRANSFER * 3);
		this.setMaxReceive(References.DRACONICTRANSFER * 3);
		this.energyPerOperation = References.ENERGYPERBLOCK;
		ModItems.register(this);
	}

	@Override
	public float func_150893_a(ItemStack stack, Block block) {
		return this.efficiencyOnProperMaterial;
	}


	@Override
	public List<ItemConfigField> getFields(ItemStack stack, int slot) {
		List<ItemConfigField> list = super.getFields(stack, slot);
		list.add(new ItemConfigField(References.INT_ID, slot, References.DIG_AOE).setMinMaxAndIncromente(0, 4, 1).readFromItem(stack, 0).setModifier("AOE"));
		list.add(new ItemConfigField(References.INT_ID, slot, References.DIG_DEPTH).setMinMaxAndIncromente(1, 9, 1).readFromItem(stack, 1));
		list.add(new ItemConfigField(References.INT_ID, slot, References.ATTACK_AOE).setMinMaxAndIncromente(0, 12, 1).readFromItem(stack, 1).setModifier("AOE"));
		list.add(new ItemConfigField(References.BOOLEAN_ID, slot, References.OBLITERATE).readFromItem(stack, false));
		return list;
	}

	@Override
	public String getInventoryName() {
		return StatCollector.translateToLocal("info.de.toolInventory.txt");
	}

	@Override
	public int getInventorySlots() {
		return 9;
	}

	@Override
	public boolean isEnchantValid(Enchantment enchant) {
		return true;
	}


//	@SuppressWarnings({"unchecked"})
//	@Override
//	public void addInformation(final ItemStack stack, final EntityPlayer player, final List list, final boolean extraInformation) {
//		int size = (ItemNBTHelper.getShort(stack, "size", (short) 0) * 2) + 1;
//		boolean oblit = ItemNBTHelper.getBoolean(stack, "obliterate", false);
//		if (InfoHelper.holdShiftForDetails(list)){
//			InfoHelper.addEnergyInfo(stack, list);
//
//			list.add(InfoHelper.ITC() + StatCollector.translateToLocal("info.de.miningMode.txt") + ": " + InfoHelper.HITC() + size + "x" + size);
//			list.add(InfoHelper.ITC() + StatCollector.translateToLocal("info.de.changeMiningMode.txt"));
//			list.add(InfoHelper.ITC() + StatCollector.translateToLocal("info.de.obliterationMode.txt") + ": " + InfoHelper.HITC() + StatCollector.translateToLocal("info.de.obliterationMode"+oblit+".txt"));
//			list.add(InfoHelper.ITC() + StatCollector.translateToLocal("info.de.toggleOblit.txt"));
//			list.add(InfoHelper.ITC() + StatCollector.translateToLocal("info.de.oblitInfo.txt"));
//
//			InfoHelper.addLore(stack, list);
//		}
//	}
//
//	@Override
//	public EnumRarity getRarity(ItemStack stack) {
//		return EnumRarity.epic;
//	}
}
