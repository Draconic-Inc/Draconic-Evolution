package com.brandon3055.draconicevolution.common.blocks.machine;

import com.brandon3055.draconicevolution.common.blocks.BlockContainerDE;
import com.brandon3055.draconicevolution.common.blocks.ModBlocks;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import com.brandon3055.draconicevolution.client.interfaces.GuiHandler;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.tileentities.TilePlayerDetectorAdvanced;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class PlayerDetectorAdvanced extends BlockContainerDE
{
	IIcon side_inactive;
	IIcon side_active;
	IIcon top;
	IIcon bottom;

	public PlayerDetectorAdvanced() {
		super(Material.iron);
		this.setBlockName(Strings.playerDetectorAdvancedName);
		this.setCreativeTab(DraconicEvolution.tolkienTabBlocksItems);
		this.setStepSound(soundTypeStone);
		this.setHardness(1f);
		this.setResistance(200.0f);
		ModBlocks.register(this);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		side_inactive = iconRegister.registerIcon(References.RESOURCESPREFIX + "advanced_player_detector_side_inactive");
		side_active = iconRegister.registerIcon(References.RESOURCESPREFIX + "advanced_player_detector_side_active");
		top = iconRegister.registerIcon(References.RESOURCESPREFIX + "machine_top_0");
		bottom  = iconRegister.registerIcon(References.RESOURCESPREFIX + "machine_side");
	}

	@Override
	public boolean isBlockSolid(IBlockAccess p_149747_1_, int p_149747_2_, int p_149747_3_, int p_149747_4_, int p_149747_5_) {
		return true;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
	{
		IIcon side_icon;
		
		TileEntity tile = world.getTileEntity(x, y, z);
		TilePlayerDetectorAdvanced detector = (tile != null && tile instanceof TilePlayerDetectorAdvanced) ? (TilePlayerDetectorAdvanced)tile : null;
		if (detector != null && detector.getStackInSlot(0) != null)
		{
			ItemStack stack = detector.getStackInSlot(0);
			Block block = Block.getBlockFromItem(stack.getItem());
			if (block != null && block.renderAsNormalBlock())
				return block.getIcon(side, stack.getItemDamage());
		}else
		{
			if (detector != null && detector.output)
				side_icon = side_active;
			else
				side_icon = side_inactive;

			if (side == 0)
				return bottom;
			else if (side == 1)
				return top;
			else
				return side_icon;
		}
					
		return null;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int meta)
	{
		if (side == 0)
			return bottom;
		else if (side == 1)
			return top;
		else
			return side_active;
	}
	
	@Override
	public TileEntity createNewTileEntity(World var1, int var2)
	{
		return new TilePlayerDetectorAdvanced();
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
	{
		if (!world.isRemote) {
			FMLNetworkHandler.openGui(player, DraconicEvolution.instance, GuiHandler.GUIID_PLAYERDETECTOR, world, x, y, z);
		}
		return true;
	}
	
	@Override
	public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side)
	{
		if (side == 0 || side == 1)
			return false;
		else
			return true;
	}
	
	@Override
	public boolean canProvidePower()
	{
		return true;
	}
	
	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int meta)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		TilePlayerDetectorAdvanced detector = (te != null && te instanceof TilePlayerDetectorAdvanced) ? (TilePlayerDetectorAdvanced) te : null;
		if(detector != null)
            if (!detector.outputInverted)
			    return detector.output ? 15 : 0;
            else
                return detector.output ? 0 : 15;
		else
			return 0;
	}
	
	@Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int meta)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		TilePlayerDetectorAdvanced detector = (te != null && te instanceof TilePlayerDetectorAdvanced) ? (TilePlayerDetectorAdvanced) te : null;
		if(detector != null)
			if (!detector.outputInverted)
				return detector.output ? 15 : 0;
			else
				return detector.output ? 0 : 15;
		else
			return 0;
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

		world.notifyBlocksOfNeighborChange(x - 1, y, z, world.getBlock(x, y, z));
		world.notifyBlocksOfNeighborChange(x + 1, y, z, world.getBlock(x, y, z));
		world.notifyBlocksOfNeighborChange(x, y - 1, z, world.getBlock(x, y, z));
		world.notifyBlocksOfNeighborChange(x, y + 1, z, world.getBlock(x, y, z));
		world.notifyBlocksOfNeighborChange(x, y, z - 1, world.getBlock(x, y, z));
		world.notifyBlocksOfNeighborChange(x, y, z + 1, world.getBlock(x, y, z));
	}
}
