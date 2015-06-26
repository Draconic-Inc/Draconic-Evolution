package com.brandon3055.draconicevolution.common.blocks.machine;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.blocks.BlockDE;
import com.brandon3055.draconicevolution.common.blocks.itemblocks.ItemBlockFrowGate;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.utills.LogHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

/**
 * Created by Brandon on 25/6/2015.
 */
public class FlowGate extends BlockDE {

	IIcon icon_input;
	IIcon icon_output;
	IIcon[] icon_fluid = new IIcon[4];
	IIcon[] icon_flux = new IIcon[4];

	public FlowGate(){
		this.setBlockName("flowGate");
		this.setCreativeTab(DraconicEvolution.tabBlocksItems);

		ModBlocks.register(this, ItemBlockFrowGate.class);
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		LogHelper.info(metadata);
		return super.createTileEntity(world, metadata);
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs p_149666_2_, List list) {
		list.add(new ItemStack(item));
		list.add(new ItemStack(item, 1, 6));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "machine_side");
		icon_input = iconRegister.registerIcon(References.RESOURCESPREFIX + "machine_io_i");
		icon_output = iconRegister.registerIcon(References.RESOURCESPREFIX + "machine_io_o");
		for (int i = 0; i < 4; i++){
			icon_fluid[i] = iconRegister.registerIcon(References.RESOURCESPREFIX + "gates/fluidGate" + i);
			icon_flux[i] = iconRegister.registerIcon(References.RESOURCESPREFIX + "gates/fluxGate" + i);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int meta) {
		int f = meta % 6;
		int type = meta / 6;

		if (side == f) return icon_output;
		else if (ForgeDirection.getOrientation(side).getOpposite().ordinal() == f) return icon_input;
		else {
			int t = 0;
			if (f == 0) t = 1;
			else if (f == 1) t = 3;
			else if (f == 2) {
				t = (side == 0 ? 3 : (side == 1 ? 3 : (side == 4 ? 2 : 0)));
			}
			else if (f == 3) {
				t = (side == 0 ? 1 : (side == 1 ? 1 : (side == 4 ? 0 : 2)));
			}
			else if (f == 4) {
				t = (side == 0 ? 2 : (side == 1 ? 2 : (side == 2 ? 0  : 2)));
			}
			else if (f == 5) {
				t = (side == 0 ? 0 : (side == 1 ? 0 : (side == 2 ? 2 : 0)));
			}
			return type == 0 ? icon_flux[t] : icon_fluid[t];
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
		int d = determineOrientation(world, x, y, z, entity) + stack.getItemDamage();
		world.setBlockMetadataWithNotify(x, y, z, d, 2);
	}

	public static int determineOrientation(World world, int x, int y, int z, EntityLivingBase entity)
	{
		if (MathHelper.abs((float)entity.posX - (float)x) < 2.0F && MathHelper.abs((float)entity.posZ - (float)z) < 2.0F)
		{
			double d0 = entity.posY + 1.82D - (double)entity.yOffset;

			if (d0 - (double)y > 2.0D)
			{
				return 0;
			}

			if ((double)y - d0 > 0.0D)
			{
				return 1;
			}
		}

		int l = MathHelper.floor_double((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		return l == 0 ? 3 : (l == 1 ? 4 : (l == 2 ? 2 : (l == 3 ? 5 : 0)));
	}

	@Override
	public int damageDropped(int meta) {
		return (meta / 6) * 6;
	}

	@Override
	public boolean rotateBlock(World worldObj, int x, int y, int z, ForgeDirection axis) {
		int meta = worldObj.getBlockMetadata(x, y, z);
		int type = meta / 6;
		ForgeDirection facing = ForgeDirection.getOrientation(meta % 6);

		if (facing == axis || facing == axis.getOpposite()) return false;
		else if (axis == ForgeDirection.UP || axis == ForgeDirection.DOWN){
			if (facing == ForgeDirection.NORTH){
				facing = ForgeDirection.EAST;
			}
			else if (facing == ForgeDirection.SOUTH){
				facing = ForgeDirection.WEST;
			}
			else if (facing == ForgeDirection.EAST){
				facing = ForgeDirection.SOUTH;
			}
			else if (facing == ForgeDirection.WEST){
				facing = ForgeDirection.NORTH;
			}
		}
		else if (axis == ForgeDirection.NORTH || axis == ForgeDirection.SOUTH){
			if (facing == ForgeDirection.UP){
				facing = ForgeDirection.WEST;
			}
			else if (facing == ForgeDirection.DOWN){
				facing = ForgeDirection.EAST;
			}
			else if (facing == ForgeDirection.EAST){
				facing = ForgeDirection.UP;
			}
			else if (facing == ForgeDirection.WEST){
				facing = ForgeDirection.DOWN;
			}
		}
		else if (axis == ForgeDirection.EAST || axis == ForgeDirection.WEST){
			if (facing == ForgeDirection.UP){
				facing = ForgeDirection.NORTH;
			}
			else if (facing == ForgeDirection.DOWN){
				facing = ForgeDirection.SOUTH;
			}
			else if (facing == ForgeDirection.SOUTH){
				facing = ForgeDirection.UP;
			}
			else if (facing == ForgeDirection.NORTH){
				facing = ForgeDirection.DOWN;
			}
		}


		worldObj.setBlockMetadataWithNotify(x, y, z, facing.ordinal() + (type * 6), 2);
		return false;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
		return new ItemStack(this, 1, (world.getBlockMetadata(x, y, z) / 6) * 6);
	}
}
