package com.brandon3055.draconicevolution.common.items.weapons;

import cofh.api.energy.IEnergyContainerItem;
import com.brandon3055.brandonscore.common.utills.InfoHelper;
import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.render.IRenderTweak;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.entity.EntityPersistentItem;
import com.brandon3055.draconicevolution.common.items.tools.baseclasses.ToolHandler;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.utills.IInventoryTool;
import com.brandon3055.draconicevolution.common.utills.ItemConfigField;
import com.google.common.collect.Multimap;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class WyvernSword extends ItemSword implements IEnergyContainerItem, IInventoryTool, IRenderTweak {

	protected int capacity = References.WYVERNCAPACITY;
	protected int maxReceive = References.WYVERNTRANSFER;
	protected int maxExtract = References.WYVERNTRANSFER;

	public WyvernSword() {
		super(ModItems.DRACONIUM_T1);
		this.setUnlocalizedName(Strings.wyvernSwordName);
		this.setCreativeTab(DraconicEvolution.tabToolsWeapons);
		if (ModItems.isEnabled(this)) GameRegistry.registerItem(this, Strings.wyvernSwordName);
	}

	@Override
	public boolean isItemTool(ItemStack p_77616_1_) {
		return true;
	}


	
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
	@SideOnly(Side.CLIENT)
	public void registerIcons(final IIconRegister iconRegister)
	{
		this.itemIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "sword_wyvern");
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
	{
		ToolHandler.AOEAttack(player, entity, stack, 1);
		ToolHandler.damageEntityBasedOnHealth(entity, player, 0.1F);
		return true;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(final ItemStack stack, final EntityPlayer player, final List list, final boolean extraInformation)
	{
		if (InfoHelper.holdShiftForDetails(list)) {
			InfoHelper.addEnergyInfo(stack, list);
			list.add(InfoHelper.ITC() + StatCollector.translateToLocal("info.de.sword.txt"));
			InfoHelper.addLore(stack, list);
		}
		list.add("");
		list.add(EnumChatFormatting.BLUE + "+" + ToolHandler.getBaseAttackDamage(stack) + " " + StatCollector.translateToLocal("info.de.attackDamage.txt"));
		list.add(EnumChatFormatting.BLUE + "+10%" + " " + StatCollector.translateToLocal("info.de.bonusHealthDamage.txt"));
	}

	@Override
	public EnumRarity getRarity(ItemStack stack)
	{
		return EnumRarity.uncommon;
	}

	@Override
	public boolean hitEntity(ItemStack par1ItemStack, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase)
	{
		System.out.println(par2EntityLivingBase.isEntityAlive());
		System.out.println(par3EntityLivingBase.isEntityAlive());
		return super.hitEntity(par1ItemStack, par2EntityLivingBase, par3EntityLivingBase);
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
	public boolean hasCustomEntity(ItemStack stack) {
		return true;
	}

	@Override
	public Entity createEntity(World world, Entity location, ItemStack itemstack) {
		return new EntityPersistentItem(world, location, itemstack);
	}

	@Override
	public String getInventoryName() {
		return StatCollector.translateToLocal("info.de.toolInventoryEnch.txt");
	}

	@Override
	public int getInventorySlots() {
		return 0;
	}

	@Override
	public boolean isEnchantValid(Enchantment enchant) {
		return enchant.type == EnumEnchantmentType.weapon;
	}

	@Override
	public List<ItemConfigField> getFields(ItemStack stack, int slot) {
		return new ArrayList<ItemConfigField>();
	}

	@Override
	public Multimap getAttributeModifiers(ItemStack stack) {
		Multimap map = super.getAttributeModifiers(stack);
		map.clear();
		return map;
	}

	@Override
	public void tweakRender(IItemRenderer.ItemRenderType type) {
		GL11.glTranslated(0.25, 0.8, 0.05);
		GL11.glRotatef(90, 1, 0, 0);
		GL11.glRotatef(140, 0, -1, 0);
		GL11.glRotatef(180, 0, 0, 1);
		GL11.glScaled(0.6, 0.6, 0.6);

		if (type == IItemRenderer.ItemRenderType.INVENTORY){
			GL11.glScalef(11.8F, 11.8F, 11.8F);
			GL11.glRotatef(180, 0, 1, 0);
			GL11.glTranslated(-1.5, 0, -0.1);
		}
		else if (type == IItemRenderer.ItemRenderType.ENTITY){
			GL11.glRotatef(90.5F, 0, 1, 0);
			GL11.glTranslated(0.2, 0, -0.8);
		}
	}
}

