package com.brandon3055.draconicevolution.common.blocks;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.items.tools.TeleporterMKI;
import com.brandon3055.draconicevolution.common.items.tools.TeleporterMKII;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.tileentities.TileTeleporterStand;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

/**
 * Created by Brandon on 27/06/2014.
 */
public class TeleporterStand extends BlockContainerDE {
	public TeleporterStand() {
		super(Material.iron);
		this.setBlockName(Strings.teleporterStandName);
		this.setCreativeTab(DraconicEvolution.tolkienTabBlocksItems);
		this.setStepSound(soundTypeStone);
		this.setHardness(1f);
		this.setResistance(200.0f);
		this.setBlockBounds(0.35f, 0f, 0.35f, 0.65f, 0.8f, 0.65f);
		ModBlocks.register(this);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		//blockIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "machine_side");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileTeleporterStand();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float prx, float pry, float prz) {
		TileTeleporterStand tile = world.getTileEntity(x, y, z) instanceof TileTeleporterStand ? (TileTeleporterStand)world.getTileEntity(x, y, z) : null;
		if (tile == null) return false;
		if (tile.getStackInSlot(0) == null && player.getHeldItem() != null && (player.getHeldItem().getItem() instanceof TeleporterMKI || player.getHeldItem().getItem() instanceof TeleporterMKII)) {
			ItemStack stack = player.getHeldItem();
			tile.setInventorySlotContents(0, stack.copy());
			player.getHeldItem().stackSize--;
			return true;
		}

		if(tile.getStackInSlot(0) != null && player.isSneaking()){
			EntityItem item = new EntityItem(world, x+0.5, y+0.9, z+0.5, tile.getStackInSlot(0).copy());
			item.motionX = 0;
			item.motionY = 0;
			item.motionZ = 0;
			item.delayBeforeCanPickup = 0;
			tile.setInventorySlotContents(0, null);
			if (!world.isRemote) world.spawnEntityInWorld(item);
			return true;
		}

		if(tile.getStackInSlot(0) != null && !player.isSneaking()){
			tile.getStackInSlot(0).useItemRightClick(world, player);
			return true;
		}

		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderType() {
		return References.idTeleporterStand;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int par1, int par2) {
		return Block.getBlockFromName("stone").getIcon(par1, 1);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if (te != null && te instanceof IInventory) {
			IInventory inventory = (IInventory) te;

			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				ItemStack stack = inventory.getStackInSlot(i);

				if (stack != null) {
					float spawnX = x + world.rand.nextFloat();
					float spawnY = y + world.rand.nextFloat();
					float spawnZ = z + world.rand.nextFloat();

					EntityItem droppedItem = new EntityItem(world, spawnX, spawnY, spawnZ, stack);

					float multiplier = 0.05F;

					droppedItem.motionX = (-0.5F + world.rand.nextFloat()) * multiplier;
					droppedItem.motionY = (4 + world.rand.nextFloat()) * multiplier;
					droppedItem.motionZ = (-0.5F + world.rand.nextFloat()) * multiplier;

					world.spawnEntityInWorld(droppedItem);
				}
			}
		}

		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack p_149689_6_) {
		super.onBlockPlacedBy(world, x, y, z, entity, p_149689_6_);
		TileTeleporterStand tile = world.getTileEntity(x, y, z) instanceof TileTeleporterStand ? (TileTeleporterStand)world.getTileEntity(x, y, z) : null;
		if (tile == null) return;
		tile.rotation = (int)entity.rotationYawHead;
	}
}
