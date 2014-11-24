package com.brandon3055.draconicevolution.common.items.tools;

import cofh.api.energy.IEnergyContainerItem;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.entity.EntityPersistentItem;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.utills.InfoHelper;
import com.brandon3055.draconicevolution.common.utills.ItemNBTHelper;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;
import java.util.Set;

public class DraconicDistructionStaff extends ItemTool implements IEnergyContainerItem {
	public IIcon itemIcon0;
	public IIcon itemIcon1;
	public IIcon itemIcon2;
	protected int capacity = References.DRACONICCAPACITY * 3;
	protected int maxReceive = References.DRACONICTRANSFER;
	protected int maxExtract = References.DRACONICTRANSFER * 50;

	private static Set<Block> minableBlocks = Sets.newHashSet();
/*	private static final Set<Block> field_150915_c = Sets.newHashSet(new Block[]{Blocks.cobblestone, Blocks.double_stone_slab, Blocks.stone_slab, Blocks.stone, Blocks.sandstone, Blocks.mossy_cobblestone, Blocks.iron_ore, Blocks.iron_block, Blocks.coal_ore, Blocks.gold_block, Blocks.gold_ore, Blocks.diamond_ore, Blocks.diamond_block, Blocks.ice, Blocks.netherrack, Blocks.lapis_ore, Blocks.lapis_block, Blocks.redstone_ore, Blocks.lit_redstone_ore, Blocks.rail, Blocks.detector_rail, Blocks.golden_rail, Blocks.activator_rail});
	private static final Set<Block> field_150916_c = Sets.newHashSet(new Block[]{Blocks.grass, Blocks.dirt, Blocks.sand, Blocks.gravel, Blocks.snow_layer, Blocks.snow, Blocks.clay, Blocks.farmland, Blocks.soul_sand, Blocks.mycelium});
	private static final Set<Block> field_150917_c = Sets.newHashSet(new Block[]{Blocks.planks, Blocks.bookshelf, Blocks.log, Blocks.log2, Blocks.chest, Blocks.pumpkin, Blocks.lit_pumpkin});

	static {
		for (Block block : field_150915_c) {
			minableBlocks.add(block);
		}
		for (Block block : field_150916_c) {

			minableBlocks.add(block);
		}
		for (Block block : field_150917_c) {

			minableBlocks.add(block);
		}
	}*/

	public DraconicDistructionStaff() {
		super(0F, ModItems.DRACONIUM_T3, minableBlocks);
		this.setUnlocalizedName(Strings.draconicDStaffName);
		this.setCreativeTab(DraconicEvolution.tabToolsWeapons);
		this.setHarvestLevel("pickaxe", 4);
		this.setHarvestLevel("shovel", 4);
		this.setHarvestLevel("axe", 4);
		GameRegistry.registerItem(this, Strings.draconicDStaffName);
	}

	@Override
	public boolean isItemTool(ItemStack p_77616_1_) {
		return true;
	}

	@SideOnly(Side.CLIENT)
	@SuppressWarnings("unchecked")
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		list.add(ItemNBTHelper.setInteger(new ItemStack(item, 1, 0), "Energy", 0));
		list.add(ItemNBTHelper.setInteger(new ItemStack(item, 1, 0), "Energy", capacity));
	}

	@Override
	public String getUnlocalizedName(){

		return String.format("item.%s%s", References.MODID.toLowerCase() + ":", super.getUnlocalizedName().substring(super.getUnlocalizedName().indexOf(".") + 1));
	}

	@Override
	public String getUnlocalizedName(final ItemStack itemStack){
		return getUnlocalizedName();
	}

	@Override
	public boolean func_150897_b(Block block) {
		return true;
	}

	@Override
	public float func_150893_a(ItemStack stack, Block block) {
		return this.efficiencyOnProperMaterial;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		this.itemIcon0 = iconRegister.registerIcon(References.RESOURCESPREFIX + "draconic_staff");
		this.itemIcon1 = iconRegister.registerIcon(References.RESOURCESPREFIX + "draconic_staff_active");
		this.itemIcon2 = iconRegister.registerIcon(References.RESOURCESPREFIX + "draconic_staff_obliterate");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
		if (ItemNBTHelper.getShort(stack, "size", (short) 0) > 0 && ItemNBTHelper.getBoolean(stack, "obliterate", false))
			return itemIcon2;
		else if (ItemNBTHelper.getShort(stack, "size", (short) 0) > 0) return itemIcon1;
		else return itemIcon0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconIndex(ItemStack stack) {
		if (ItemNBTHelper.getShort(stack, "size", (short) 0) > 0 && ItemNBTHelper.getBoolean(stack, "obliterate", false))
			return itemIcon2;
		else if (ItemNBTHelper.getShort(stack, "size", (short) 0) > 0) return itemIcon1;
		else return itemIcon0;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
		World world = player.worldObj;
		Block block = world.getBlock(x, y, z);
		Material mat = block.getMaterial();
		if (!ToolHandler.isRightMaterial(mat, ToolHandler.materialsDStaff)) {
			return false;
		}
		int fortune = EnchantmentHelper.getFortuneModifier(player);
		boolean silk = EnchantmentHelper.getSilkTouchModifier(player);
		ToolHandler.disSquare(x, y, z, player, world, silk, fortune, ToolHandler.materialsDStaff, stack);
		return false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		return ToolHandler.changeMode(stack, player, true, 4);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		ToolHandler.AOEAttack(player, entity, stack, 25, ItemNBTHelper.getShort(stack, "size", (short) 0) * 2);
		ToolHandler.damageEntityBasedOnHealth(entity, player, 0.5F);
		return true;
	}

	@SuppressWarnings({"unchecked"})
	@Override
	public Multimap getItemAttributeModifiers() {
		Multimap multimap = super.getItemAttributeModifiers();
		multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Weapon modifier", (double) 4.0F + ModItems.DRACONIUM_T2.getDamageVsEntity(), 0));
		return multimap;
	}

	@SuppressWarnings({"unchecked"})
	@Override
	public void addInformation(final ItemStack stack, final EntityPlayer player, final List list, final boolean extraInformation) {
		int size = (ItemNBTHelper.getShort(stack, "size", (short) 0) * 2) + 1;
		boolean oblit = ItemNBTHelper.getBoolean(stack, "obliterate", false);
		if (InfoHelper.holdShiftForDetails(list)){
			InfoHelper.addEnergyInfo(stack, list);

			list.add(InfoHelper.ITC() + StatCollector.translateToLocal("info.de.miningMode.txt") + ": " + InfoHelper.HITC() + size + "x" + size);
			list.add(InfoHelper.ITC() + StatCollector.translateToLocal("info.de.changeMiningMode.txt"));
			list.add(InfoHelper.ITC() + StatCollector.translateToLocal("info.de.obliterationMode.txt") + ": " + InfoHelper.HITC() + StatCollector.translateToLocal("info.de.obliterationMode"+oblit+".txt"));
			list.add(InfoHelper.ITC() + StatCollector.translateToLocal("info.de.toggleOblit.txt"));
			list.add(InfoHelper.ITC() + StatCollector.translateToLocal("info.de.oblitInfo.txt"));

			InfoHelper.addLore(stack, list);
		}
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.epic;
	}

	@Override
	public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {

		if (container.stackTagCompound == null) {
			container.stackTagCompound = new NBTTagCompound();
		}
		int energy = container.stackTagCompound.getInteger("Energy");
		int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));

		if (!simulate) {
			energy += energyReceived;
			container.stackTagCompound.setInteger("Energy", energy);
		}
		return energyReceived;
	}

	@Override
	public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {

		if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Energy")) {
			return 0;
		}
		int energy = container.stackTagCompound.getInteger("Energy");
		int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));

		if (!simulate) {
			energy -= energyExtracted;
			container.stackTagCompound.setInteger("Energy", energy);
		}
		return energyExtracted;
	}

	@Override
	public int getEnergyStored(ItemStack container) {
		if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Energy")) {
			return 0;
		}
		return container.stackTagCompound.getInteger("Energy");
	}

	@Override
	public int getMaxEnergyStored(ItemStack container) {
		return capacity;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return !(getEnergyStored(stack) == getMaxEnergyStored(stack));
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return 1D - ((double)getEnergyStored(stack) / (double)getMaxEnergyStored(stack));
	}

	@Override
	public float getDigSpeed(ItemStack stack, Block block, int meta) {
		if ((stack.getItem() instanceof IEnergyContainerItem) && ((IEnergyContainerItem)stack.getItem()).getEnergyStored(stack) >= References.ENERGYPERBLOCK)
			return super.getDigSpeed(stack, block, meta);
		else
			return 1F;
	}

	@Override
	public boolean hasCustomEntity(ItemStack stack) {
		return true;
	}

	@Override
	public Entity createEntity(World world, Entity location, ItemStack itemstack) {
		return new EntityPersistentItem(world, location, itemstack);
	}
}
