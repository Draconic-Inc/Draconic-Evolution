package com.brandon3055.draconicevolution.common.items.tools;

import java.util.List;

import cofh.api.energy.IEnergyContainerItem;
import com.brandon3055.draconicevolution.common.core.utills.ItemInfoHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.items.ModItems;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import org.lwjgl.input.Keyboard;

public class DraconicAxe extends ItemAxe implements IEnergyContainerItem{

	protected int capacity = References.DRACONICCAPACITY;
	protected int maxReceive = References.DRACONICTRANSFER;
	protected int maxExtract = References.DRACONICTRANSFER;

	public DraconicAxe() {
		super(ModItems.DRACONIUM_T1);
		this.setUnlocalizedName(Strings.draconicAxeName);
		this.setCreativeTab(DraconicEvolution.getCreativeTab(1));
		GameRegistry.registerItem(this, Strings.draconicAxeName);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(final IIconRegister iconRegister) {
		this.itemIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "draconic_axe");
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, int X, int Y, int Z, EntityPlayer player) {
		World world = player.worldObj;
		boolean tree = isTree(world, X, Y, Z);

		if (player.isSneaking()) {
			return false;
		}

		Block block = world.getBlock(X, Y, Z);
		Material mat = block.getMaterial();
		if (!ToolHandler.isRightMaterial(mat, ToolHandler.materialsAxe)) {
			return false;
		}

		if (!tree) {
			ToolHandler.disSquare(X, Y, Z, player, world, false, 0, ToolHandler.materialsAxe, stack);
			return false;
		}

		if (!world.isRemote) world.playAuxSFX(2001, X, Y, Z, Block.getIdFromBlock(world.getBlock(X, Y, Z)));
		trimLeavs(X, Y, Z, player, world, stack);
		chopTree(X, Y, Z, player, world, stack);

		return true;
	}

	private boolean isTree(World world, int X, int Y, int Z) {
		final Block wood = world.getBlock(X, Y, Z);
		if (wood == null || !wood.isWood(world, X, Y, Z)) {
			return false;
		} else {
			int top = Y;
			for (int y = Y; y <= Y + 50; y++) {
				if (!world.getBlock(X, y, Z).isWood(world, X, y, Z) && !world.getBlock(X, y, Z).isLeaves(world, X, y, Z)) {
					top += y;
					break;
				}
			}

			int leaves = 0;
			for (int xPos = X - 1; xPos <= X + 1; xPos++) {
				for (int yPos = Y; yPos <= top; yPos++) {
					for (int zPos = Z - 1; zPos <= Z + 1; zPos++) {
						if (world.getBlock(xPos, yPos, zPos).isLeaves(world, xPos, yPos, zPos)) leaves++;
					}
				}
			}
			if (leaves >= 3) return true;
		}

		return false;
	}

	void chopTree(int X, int Y, int Z, EntityPlayer player, World world, ItemStack stack) {
		for (int xPos = X - 1; xPos <= X + 1; xPos++) {
			for (int yPos = Y; yPos <= Y + 1; yPos++) {
				for (int zPos = Z - 1; zPos <= Z + 1; zPos++) {
					Block block = world.getBlock(xPos, yPos, zPos);
					int meta = world.getBlockMetadata(xPos, yPos, zPos);
					if (block.isWood(world, xPos, yPos, zPos)) {
						world.setBlockToAir(xPos, yPos, zPos);
						if (!player.capabilities.isCreativeMode) {
							if (block.removedByPlayer(world, player, xPos, yPos, zPos)) {
								block.onBlockDestroyedByPlayer(world, xPos, yPos, zPos, meta);
							}
							block.harvestBlock(world, player, xPos, yPos, zPos, meta);
							block.onBlockHarvested(world, xPos, yPos, zPos, meta, player);
							onBlockDestroyed(stack, world, block, xPos, yPos, zPos, player);
						}
						chopTree(xPos, yPos, zPos, player, world, stack);
					}//else
					//trimLeavs(xPos, yPos, zPos, player, world, stack);
				}
			}
		}
	}

	void trimLeavs(int X, int Y, int Z, EntityPlayer player, World world, ItemStack stack) {
		scedualUpdates(X, Y, Z, player, world, stack);
	}

	void scedualUpdates(int X, int Y, int Z, EntityPlayer player, World world, ItemStack stack) {
		for (int xPos = X - 15; xPos <= X + 15; xPos++) {
			for (int yPos = Y; yPos <= Y + 50; yPos++) {
				for (int zPos = Z - 15; zPos <= Z + 15; zPos++) {
					Block block = world.getBlock(xPos, yPos, zPos);
					if (block.isLeaves(world, xPos, yPos, zPos)) {
						world.scheduleBlockUpdate(xPos, yPos, zPos, block, 2 + world.rand.nextInt(10));
					}
				}
			}
		}
	}

	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int x, int y, int z, int side, float par8, float par9, float par10) {
		//System.out.println(x + " " + y + " " + z);
		return super.onItemUse(par1ItemStack, par2EntityPlayer, par3World, x, y, z, side, par8, par9, par10);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void addInformation(final ItemStack stack, final EntityPlayer player, final List list, final boolean extraInformation) {
		if ((!Keyboard.isKeyDown(42)) && (!Keyboard.isKeyDown(54))) {
			list.add(EnumChatFormatting.WHITE + StatCollector.translateToLocal("info.draconicAxe1.txt"));
			list.add(EnumChatFormatting.DARK_GREEN + "Hold shift for information");
			ItemInfoHelper.energyDisplayInfo(stack, list);
		} else {
			list.add(EnumChatFormatting.GREEN + StatCollector.translateToLocal("info.draconicAxe2.txt"));
			list.add(EnumChatFormatting.GREEN + StatCollector.translateToLocal("info.draconicAxe3.txt"));
			list.add(EnumChatFormatting.GREEN + StatCollector.translateToLocal("info.draconicAxe4.txt"));
			list.add("");
			list.add(EnumChatFormatting.DARK_PURPLE + "" + EnumChatFormatting.ITALIC + StatCollector.translateToLocal("info.draconicLaw1.txt"));
			list.add(EnumChatFormatting.DARK_PURPLE + "" + EnumChatFormatting.ITALIC + StatCollector.translateToLocal("info.draconicLaw2.txt"));
		}
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.rare;
	}

	public static void registerRecipe() {
		CraftingManager.getInstance().addRecipe(new ItemStack(ModItems.draconicAxe), "DFD", "CAC", "DTD", 'F', ModItems.sunFocus, 'C', ModItems.draconicCompound, 'D', ModItems.draconiumIngot, 'T', ModItems.draconicCore, 'A', Items.diamond_axe);
	}

	@Override
	public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {

		if (container.stackTagCompound == null) {
			container.stackTagCompound = new NBTTagCompound();
		}
		int energy = container.stackTagCompound.getInteger("EnergyHelper");
		int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));

		if (!simulate) {
			energy += energyReceived;
			container.stackTagCompound.setInteger("EnergyHelper", energy);
		}
		return energyReceived;
	}

	@Override
	public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {

		if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("EnergyHelper")) {
			return 0;
		}
		int energy = container.stackTagCompound.getInteger("EnergyHelper");
		int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));

		if (!simulate) {
			energy -= energyExtracted;
			container.stackTagCompound.setInteger("EnergyHelper", energy);
		}
		return energyExtracted;
	}

	@Override
	public int getEnergyStored(ItemStack container) {
		if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("EnergyHelper")) {
			return 0;
		}
		return container.stackTagCompound.getInteger("EnergyHelper");
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
}
