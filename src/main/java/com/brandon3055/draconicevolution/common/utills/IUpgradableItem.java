package com.brandon3055.draconicevolution.common.utills;

import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import java.util.List;

/**
 * Created by brandon3055 on 23/12/2015.
 */
public interface IUpgradableItem {

	/**@return a list of upgrades for this item.*/
	public List<EnumUpgrade> getUpgrades();

	/**@return the maximum number of upgrades for this item*/
	public int getUpgradeCap();

	/**@return the max upgrade tier allowed for this item
	 * 0 = Draconic Core
	 * 1 = Wyvern Core
	 * 2 = Awakened Core
	 * 3 = Chaotic Core*/
	public int getMaxTier();

	public static enum EnumUpgrade {
		RF_CAPACITY(0, "RFCapacity"),
		DIG_SPEED(1, "DigSpeed"),
		DIG_AOE(2, "DigAOE"),
		DIG_DEPTH(3, "DigDepth"),
		ATTACK_DAMAGE(4, "AttackDamage"),
		ATTACK_AOE(5, "AttackAOE"),
		ARROW_DAMAGE(6, "ArrowDamage"),
		DRAW_SPEED(7, "DrawSpeed"),
		ARROW_SPEED(8, "ArrowSpeed"),
		SHIELD_CAPACITY(9, "ShieldCapacity"),
		SHIELD_RECOVERY(10, "ShieldRecovery"),
		MOVE_SPEED(11, "MoveSpeed"),
		JUMP_BOOST(12, "JumpBoost");

		public int index;
		public String name;
		private final String COMPOUND_NAME =  "Upgrades";

		private EnumUpgrade(int index, String name) {
			this.index = index;
			this.name = name;
		}

		/**Get the number of cores applied to this upgrade
		 * @return an int[4] containing the number of cores that have been applied
		 * in order from index 0 which is the number od draconic cores to index 3 which is the number of chaotic cores
		 * with wyvern and awakened in between*/
		public int[] getCoresApplied(ItemStack stack){
			if (stack == null) return new int[] {0, 0, 0, 0};
			NBTTagCompound compound = ItemNBTHelper.getCompound(stack);
			if (!compound.hasKey(COMPOUND_NAME)) return new int[] {0, 0, 0, 0};
			NBTTagCompound upgrades = compound.getCompoundTag(COMPOUND_NAME);
			if (upgrades.hasKey(name) && upgrades.getIntArray(name).length == 4) return upgrades.getIntArray(name);
			return new int[] {0, 0, 0, 0};
		}

		/**Sets the number of each core type applied
		 * takes an ItemStack and an int[4]*/
		public void setCoresApplied(ItemStack stack, int[] cores){
			if (cores.length != 4){
				LogHelper.error("[EnumUpgrade] Error applying upgrades to stack.");
				return;
			}

			NBTTagCompound compound = ItemNBTHelper.getCompound(stack);
			NBTTagCompound upgrades;
			if (compound.hasKey(COMPOUND_NAME)) upgrades = compound.getCompoundTag(COMPOUND_NAME);
			else upgrades = new NBTTagCompound();
			upgrades.setIntArray(name, cores);
			compound.setTag(COMPOUND_NAME, upgrades);
		}

		public String getLocalizedName() { return StatCollector.translateToLocal("gui.de."+name+".txt"); }

		public static EnumUpgrade getUpgradeByIndex(int index) {
			for (EnumUpgrade upgrade : EnumUpgrade.values()) {
				if (upgrade.index == index) return upgrade;
			}
			return null;
		}

	}
}
