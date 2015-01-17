package com.brandon3055.draconicevolution.common.items.tools.baseclasses;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.utills.InfoHelper;
import com.brandon3055.draconicevolution.common.utills.ItemConfigField;
import com.brandon3055.draconicevolution.common.utills.ItemNBTHelper;
import com.google.common.collect.Sets;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Brandon on 2/01/2015.
 */
public class ToolBase extends RFItemBase {

	private static final Set SHOVEL_OVERRIDES = Sets.newHashSet(Blocks.grass, Blocks.dirt, Blocks.sand, Blocks.gravel, Blocks.snow_layer, Blocks.snow, Blocks.clay, Blocks.farmland, Blocks.soul_sand, Blocks.mycelium);
	private static final Set PICKAXE_OVERRIDES = Sets.newHashSet(Blocks.cobblestone, Blocks.double_stone_slab, Blocks.stone_slab, Blocks.stone, Blocks.sandstone, Blocks.mossy_cobblestone, Blocks.iron_ore, Blocks.iron_block, Blocks.coal_ore, Blocks.gold_block, Blocks.gold_ore, Blocks.diamond_ore, Blocks.diamond_block, Blocks.ice, Blocks.netherrack, Blocks.lapis_ore, Blocks.lapis_block, Blocks.redstone_ore, Blocks.lit_redstone_ore, Blocks.rail, Blocks.detector_rail, Blocks.golden_rail, Blocks.activator_rail, Material.iron, Material.anvil, Material.rock);
	private static final Set AXE_OVERRIDES = Sets.newHashSet(Blocks.planks, Blocks.bookshelf, Blocks.log, Blocks.log2, Blocks.chest, Blocks.pumpkin, Blocks.lit_pumpkin, Material.wood, Material.leaves, Material.coral, Material.cactus, Material.plants, Material.vine);


	/** A list of blocks this tool can mine that are not covered by the tool class. */
	private Set blockOverrides;
	protected float efficiencyOnProperMaterial = 4.0F;
	/** Damage versus entities. */
	public float damageVsEntity;
	/** The material this tool is made from. */
	protected Item.ToolMaterial toolMaterial;
	/** The amount of energy required to dig one block, damage a mob. */
	public int energyPerOperation = 0;

	protected ToolBase(float baseDamage, Item.ToolMaterial material, Set blockOverrides)
	{
		this.toolMaterial = material;
		this.blockOverrides = blockOverrides == null ? new HashSet() : blockOverrides;
		this.maxStackSize = 1;
		this.setMaxDamage(material.getMaxUses());
		this.efficiencyOnProperMaterial = material.getEfficiencyOnProperMaterial();
		this.damageVsEntity = baseDamage + material.getDamageVsEntity();
		this.setCreativeTab(DraconicEvolution.tabToolsWeapons);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setHarvestLevel(String toolClass, int level) {
		if (toolClass.equals("pickaxe")) blockOverrides.addAll(PICKAXE_OVERRIDES);
		if (toolClass.equals("shovel")) blockOverrides.addAll(SHOVEL_OVERRIDES);
		if (toolClass.equals("axe")) blockOverrides.addAll(AXE_OVERRIDES);
		super.setHarvestLevel(toolClass, level);
	}

	/**
	 * Get strength vs block
	 */
	@Override
	public float func_150893_a(ItemStack stack, Block block)
	{
		return blockOverrides.contains(block) || blockOverrides.contains(block.getMaterial()) ? efficiencyOnProperMaterial : 1.0F;
	}

	/**
	 * Can Harvest Block
	 */
	@Override
	public boolean func_150897_b(Block block)
	{
		if (getToolClasses(null).contains("pickaxe")) return true;
		return blockOverrides.contains(block);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isFull3D()
	{
		return true;
	}

	@Override
	public int getItemEnchantability()
	{
		return this.toolMaterial.getEnchantability();
	}


	@Override
	public float getDigSpeed(ItemStack stack, Block block, int meta)
	{
		float speed;
		if (ForgeHooks.isToolEffective(stack, block, meta))
		{
			speed = efficiencyOnProperMaterial;
		}
		else
		{
			speed = super.getDigSpeed(stack, block, meta);
		}

		if (getEnergyStored(stack) >= energyPerOperation)
		{
			float f = ItemNBTHelper.getFloat(stack, References.DIG_SPEED_MULTIPLIER, 1f);
			if (speed > 50f) f *= f;
			return f * speed;
		}
		else
		{
			return 0.5f;
		}
	}

	@Override
	public List<ItemConfigField> getFields(ItemStack stack, int slot) {
		List<ItemConfigField> list = super.getFields(stack, slot);
		if (!getToolClasses(stack).isEmpty()) list.add(new ItemConfigField(References.FLOAT_ID, slot, References.DIG_SPEED_MULTIPLIER).setMinMaxAndIncromente(0f, 1f, 0.01f).readFromItem(stack, 1f));
		return list;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean extended) {
		boolean show = InfoHelper.holdShiftForDetails(list);
		if (show) InfoHelper.addEnergyInfo(stack, list);
		addAditionalInformation(stack, player, list, extended);
		if (show) InfoHelper.addLore(stack, list, true);
	}

	@SideOnly(Side.CLIENT)
	public void addAditionalInformation(ItemStack stack, EntityPlayer player, List list, boolean extended) {
	}
}
