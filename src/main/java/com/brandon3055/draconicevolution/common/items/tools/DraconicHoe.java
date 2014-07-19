package com.brandon3055.draconicevolution.common.items.tools;

import java.util.List;

import cofh.api.energy.IEnergyContainerItem;
import com.brandon3055.draconicevolution.common.core.utills.ItemInfoHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.items.ModItems;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;

public class DraconicHoe extends ItemHoe implements IEnergyContainerItem{

	protected int capacity = References.DRACONICCAPACITY;
	protected int maxReceive = References.DRACONICTRANSFER;
	protected int maxExtract = References.DRACONICTRANSFER;

	public DraconicHoe() {
		super(ModItems.DRACONIUM_T1);
		this.setUnlocalizedName(Strings.draconicHoeName);
		this.setCreativeTab(DraconicEvolution.getCreativeTab(1));
		GameRegistry.registerItem(this, Strings.draconicHoeName);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(final IIconRegister iconRegister) {
		this.itemIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "draconic_hoe");
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10) {
		boolean successfull = false;
		Block clicked = world.getBlock(x, y, z);
		if (!player.isSneaking() && player.canPlayerEdit(x, y, z, par7, stack) && (clicked == Blocks.dirt || clicked == Blocks.grass || clicked == Blocks.farmland) && par7 == 1) {
			int size = 4;
			for (int x1 = -size; x1 <= size; x1++) {
				for (int z1 = -size; z1 <= size; z1++) {
					if (!(stack.getItem() instanceof IEnergyContainerItem) || ((IEnergyContainerItem)stack.getItem()).getEnergyStored(stack) < References.ENERGYPERBLOCK) {
						if (!player.capabilities.isCreativeMode)
							return false;
					}
					Block topBlock = world.getBlock(x + x1, y + 1, z + z1);
					if (topBlock.isReplaceable(world, x + x1, y + 1, z + z1)) {
						world.setBlockToAir(x + x1, y + 1, z + z1);
					}
					Block topBlock2 = world.getBlock(x + x1, y + 2, z + z1);
					if (topBlock2.isReplaceable(world, x + x1, y + 2, z + z1)) {
						world.setBlockToAir(x + x1, y + 2, z + z1);
					}
					Block block = world.getBlock(x + x1, y, z + z1);
					if (block.isReplaceable(world, x + x1, y, z + z1) && !block.getMaterial().equals(Material.water)) {
						world.setBlockToAir(x + x1, y, z + z1);
					}

					if (world.getBlock(x + x1, y, z + z1) == Blocks.air && world.getBlock(x + x1, y - 1, z + z1).isBlockSolid(world, x, y, z, 1)) {
						if (player.inventory.hasItem(Item.getItemFromBlock(Blocks.dirt)) || player.capabilities.isCreativeMode) {
							world.setBlock(x + x1, y, z + z1, Blocks.dirt);
							player.inventory.consumeInventoryItem(Item.getItemFromBlock(Blocks.dirt));
						}
					}

					if ((world.getBlock(x + x1, y + 1, z + z1) == Blocks.dirt || world.getBlock(x + x1, y + 1, z + z1) == Blocks.grass || world.getBlock(x + x1, y + 1, z + z1) == Blocks.farmland) && world.getBlock(x + x1, y + 2, z + z1) == Blocks.air) {
						if (!world.isRemote)
							world.spawnEntityInWorld(new EntityItem(world, player.posX, player.posY, player.posZ, new ItemStack(Item.getItemFromBlock(Blocks.dirt))));
						world.setBlock(x + x1, y + 1, z + z1, Blocks.air);
					}

					if (hoe(stack, player, world, x + x1, y, z + z1, par7)) successfull = true;
				}
			}
		} else successfull = hoe(stack, player, world, x, y, z, par7);
		Block block1 = Blocks.farmland;
		if (successfull)
			world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, block1.stepSound.getStepResourcePath(), (block1.stepSound.getVolume() + 1.0F) / 2.0F, block1.stepSound.getPitch() * 0.8F);
		return successfull;
	}

	private boolean hoe(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int par7) {
		if (!(stack.getItem() instanceof IEnergyContainerItem) || ((IEnergyContainerItem)stack.getItem()).getEnergyStored(stack) < References.ENERGYPERBLOCK) {
			if (!player.capabilities.isCreativeMode)
				return false;
		} else {
			if (!player.capabilities.isCreativeMode)
				((IEnergyContainerItem) stack.getItem()).extractEnergy(stack, References.ENERGYPERBLOCK, false);
		}
		if (!player.canPlayerEdit(x, y, z, par7, stack)) {
			return false;
		} else {
			UseHoeEvent event = new UseHoeEvent(player, stack, world, x, y, z);
			if (MinecraftForge.EVENT_BUS.post(event)) {
				return false;
			}

			if (event.getResult() == Result.ALLOW) {
				stack.damageItem(1, player);
				return true;
			}

			Block block = world.getBlock(x, y, z);

			if (par7 != 0 && world.getBlock(x, y + 1, z).isAir(world, x, y + 1, z) && (block == Blocks.grass || block == Blocks.dirt)) {
				Block block1 = Blocks.farmland;

				if (world.isRemote) {
					return true;
				} else {
					world.setBlock(x, y, z, block1);
					stack.damageItem(1, player);
					return true;
				}
			} else {
				return false;
			}
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void addInformation(final ItemStack stack, final EntityPlayer player, final List list, final boolean extraInformation) {

		ItemInfoHelper.energyDisplayInfo(stack, list);
		list.add(EnumChatFormatting.DARK_PURPLE + "" + EnumChatFormatting.ITALIC + StatCollector.translateToLocal("info.draconicLaw1.txt"));
		list.add(EnumChatFormatting.DARK_PURPLE + "" + EnumChatFormatting.ITALIC + StatCollector.translateToLocal("info.draconicLaw2.txt"));
	}

	public static void registerRecipe() {
		CraftingManager.getInstance().addRecipe(new ItemStack(ModItems.draconicHoe), "ISI", "DHD", "ITI", 'H', Items.diamond_hoe, 'T', ModItems.draconicCore, 'S', ModItems.sunFocus, 'D', ModItems.draconiumIngot, 'I', Items.diamond);
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
}
