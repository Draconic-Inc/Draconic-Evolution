package com.brandon3055.draconicevolution.common.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.brandon3055.draconicevolution.client.interfaces.GuiHandler;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.tileentities.TileGrinder;

public class Grinder extends BlockContainer
{
	public IIcon icon_front;
	public IIcon icon_side;
	public IIcon icon_back;
	public IIcon icon_back_inactive;
	public IIcon icon_front_inactive;
	public IIcon icon_top[] = new IIcon[4];

	protected Grinder() {
		super(Material.rock);
		this.setBlockName(Strings.grinderName);
		this.setCreativeTab(DraconicEvolution.getCreativeTab(2));
		this.setStepSound(soundTypeStone);
		this.setHardness(1f);
		this.setResistance(2000.0f);
		GameRegistry.registerBlock(this, this.getUnlocalizedName());

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(final IIconRegister iconRegister)
	{
		icon_front = iconRegister.registerIcon(References.RESOURCESPREFIX + "animated/grinder_front_active");
		icon_front_inactive = iconRegister.registerIcon(References.RESOURCESPREFIX + "grinder_front");
		icon_side = iconRegister.registerIcon(References.RESOURCESPREFIX + "machine_side");
		icon_back = iconRegister.registerIcon(References.RESOURCESPREFIX + "animated/machine_fan");
		icon_back_inactive = iconRegister.registerIcon(References.RESOURCESPREFIX + "machine_fan");
		for (int i = 0; i < 4; i++)
		{
			icon_top[i] = iconRegister.registerIcon(References.RESOURCESPREFIX + "machine_top_" + i);
		}

	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(final Item item, final CreativeTabs tab, final List par3list)
	{
		par3list.add(new ItemStack(item, 1, 3));
	}
	
	@Override
	public int damageDropped(int p_149692_1_)
	{
		return 3;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
	{
		TileGrinder tile = (TileGrinder) world.getTileEntity(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		
		IIcon back;
		IIcon front;
		
		if (!tile.disabled && tile.energy >= 50)
		{
			back = icon_back;
			front = icon_front;
		}
		else
		{
			back = icon_back_inactive;
			front = icon_front_inactive;
		}
		
		switch (side)
		{
		case 0:
			return icon_side;
		case 1:
			return icon_top[meta];
		case 2:
			if (meta == 0)
				return front;
			else if (meta == 2)
				return back;
			else
				return icon_side;
		case 3:
			if (meta == 2)
				return front;
			else if (meta == 0)
				return back;
			else
				return icon_side;
		case 4:
			if (meta == 3)
				return front;
			else if (meta == 1)
				return back;
			else
				return icon_side;
		case 5:
			if (meta == 1)
				return front;
			else if (meta == 3)
				return back;
			else
				return icon_side;
		}
		return null;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		switch (side)
		{
		case 0:
			return icon_side;
		case 1:
			return icon_top[meta];
		case 2:
			if (meta == 0)
				return icon_front;
			else if (meta == 2)
				return icon_back;
			else
				return icon_side;
		case 3:
			if (meta == 2)
				return icon_front;
			else if (meta == 0)
				return icon_back;
			else
				return icon_side;
		case 4:
			if (meta == 3)
				return icon_front;
			else if (meta == 1)
				return icon_back;
			else
				return icon_side;
		case 5:
			if (meta == 1)
				return icon_front;
			else if (meta == 3)
				return icon_back;
			else
				return icon_side;
		}
		return null;
	}
	
	@Override
	public TileEntity createNewTileEntity(World var1, int var2)
	{
		return new TileGrinder();
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float prx, float pry, float prz)
	{
		if (!world.isRemote) {
			FMLNetworkHandler.openGui(player, DraconicEvolution.instance, GuiHandler.GUIID_GRINDER, world, x, y, z);
		}
		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack)
    {
        int l = MathHelper.floor_double((double)(placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        if (l == 0)
        {
            world.setBlockMetadataWithNotify(x, y, z, 0, 2);
        }

        if (l == 1)
        {
            world.setBlockMetadataWithNotify(x, y, z, 1, 2);
        }

        if (l == 2)
        {
            world.setBlockMetadataWithNotify(x, y, z, 2, 2);
        }

        if (l == 3)
        {
            world.setBlockMetadataWithNotify(x, y, z, 3, 2);
        }
    }

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileGrinder)
		{
			((TileGrinder)tile).disabled = world.isBlockIndirectlyGettingPowered(x, y, z);
			world.markBlockForUpdate(x, y, z);
		}
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

					float mult = 0.05F;

					droppedItem.motionX = (-0.5F + world.rand.nextFloat()) * mult;
					droppedItem.motionY = (4 + world.rand.nextFloat()) * mult;
					droppedItem.motionZ = (-0.5F + world.rand.nextFloat()) * mult;

					world.spawnEntityInWorld(droppedItem);
				}
			}
		}

		super.breakBlock(world, x, y, z, block, meta);
	}

}
