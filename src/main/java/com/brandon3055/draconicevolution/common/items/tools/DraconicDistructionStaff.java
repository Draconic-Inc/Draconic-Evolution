package com.brandon3055.draconicevolution.common.items.tools;

import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.client.render.IRenderTweak;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.items.tools.baseclasses.MiningTool;
import com.brandon3055.draconicevolution.common.items.tools.baseclasses.ToolHandler;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.utills.IInventoryTool;
import com.brandon3055.draconicevolution.common.utills.ItemConfigField;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class DraconicDistructionStaff extends MiningTool implements IInventoryTool, IRenderTweak {


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
		return StatCollector.translateToLocal("info.de.toolInventoryOblit.txt");
	}

	@Override
	public int getInventorySlots() {
		return 9;
	}

	@Override
	public boolean isEnchantValid(Enchantment enchant) {
		return enchant.type == EnumEnchantmentType.digger || enchant.type == EnumEnchantmentType.weapon;
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		ToolHandler.damageEntityBasedOnHealth(entity, player, 0.3F);
		ToolHandler.AOEAttack(player, entity, stack, ItemNBTHelper.getInteger(stack, References.ATTACK_AOE, 0));
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		return super.onItemRightClick(stack, world, player);
	}

	@SuppressWarnings("unchecked")
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean extended) {
		super.addInformation(stack, player, list, extended);

		list.add("");
		list.add(EnumChatFormatting.BLUE + "+" + ToolHandler.getBaseAttackDamage(stack) + " " + StatCollector.translateToLocal("info.de.attackDamage.txt"));
		list.add(EnumChatFormatting.BLUE + "+30%" + " " + StatCollector.translateToLocal("info.de.bonusHealthDamage.txt"));
	}

	@Override
	public void tweakRender(IItemRenderer.ItemRenderType type) {
		GL11.glTranslated(0.77, 0.19, -0.15);
		GL11.glRotatef(90, 1, 0, 0);
		GL11.glRotatef(-35, 0, -1, 0);
		GL11.glScaled(0.7, 0.7, 0.7);

		if (type == IItemRenderer.ItemRenderType.INVENTORY){
			GL11.glScalef(6F, 6F, 6F);
			GL11.glRotatef(145, 0, 1, 0);
			GL11.glTranslated(-1.7, 0, 1.8);
		}
		else if (type == IItemRenderer.ItemRenderType.ENTITY){
			GL11.glRotatef(-34.5F, 0, 1, 0);
			GL11.glTranslated(-1.1, 0, -0.2);
		}
		else if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
			GL11.glTranslated(0, 0.4, 0);
		}
	}

	@Override
	public int getUpgradeCap() {
		return References.MAX_STAFF_UPGRADES;
	}

	@Override
	public int getMaxTier() {
		return 3;
	}

	@Override
	public List<EnumUpgrade> getUpgrades() {
		List<EnumUpgrade> list = super.getUpgrades();
		list.add(EnumUpgrade.ATTACK_AOE);
		list.add(EnumUpgrade.ATTACK_DAMAGE);

		return list;
	}
}
