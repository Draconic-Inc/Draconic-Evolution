package draconicevolution.common.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import draconicevolution.client.interfaces.GuiHandler;
import draconicevolution.DraconicEvolution;
import draconicevolution.common.lib.References;
import draconicevolution.common.lib.Strings;
import draconicevolution.common.tileentities.TileWeatherController;

public class BlockWeatherController extends BlockContainer {

	public boolean blockState = true;
	public IIcon icon_top;
	public IIcon icon_bottom;
	public IIcon icon_side_rain_off;
	public IIcon icon_side_rain_on;
	public IIcon icon_side_thunder_off;
	public IIcon icon_side_thunder_on;
	public IIcon icon_side_sun_on;
	public IIcon icon_side_sun_off;

	public BlockWeatherController() {
		super(Material.rock);
		this.setBlockName(Strings.blockWeatherControllerName);
		this.setCreativeTab(DraconicEvolution.getCreativeTab(2));
		this.setStepSound(soundTypeStone);
		this.setHardness(1f);
		this.setResistance(200.0f);
		GameRegistry.registerBlock(this, this.getUnlocalizedName());
	}

	@Override
	public boolean hasTileEntity(final int meta)
	{
		return true;
	}

	@Override
	public String getUnlocalizedName()
	{
		return String.format("tile.%s", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	public String getUnwrappedUnlocalizedName(final String unlocalizedName)
	{
		return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(final IIconRegister iconRegister)
	{
		icon_top = iconRegister.registerIcon(References.RESOURCESPREFIX + "weather_controller/weather_controller_top");
		icon_bottom = iconRegister.registerIcon(References.RESOURCESPREFIX + "machine_side");
		icon_side_rain_off = iconRegister.registerIcon(References.RESOURCESPREFIX + "weather_controller/weather_controller_rain");
		icon_side_rain_on = iconRegister.registerIcon(References.RESOURCESPREFIX + "weather_controller/weatherController_rain_active");
		icon_side_thunder_off = iconRegister.registerIcon(References.RESOURCESPREFIX + "weather_controller/weatherController_thunderstorm");
		icon_side_thunder_on = iconRegister.registerIcon(References.RESOURCESPREFIX + "weather_controller/weatherController_thunderstorm_active");
		icon_side_sun_off = iconRegister.registerIcon(References.RESOURCESPREFIX + "weather_controller/weatherController_sun");
		icon_side_sun_on = iconRegister.registerIcon(References.RESOURCESPREFIX + "weather_controller/weatherController_sun_active");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
	{
		IIcon icon_side = null;
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile != null && tile instanceof TileWeatherController)
		{
			if (((TileWeatherController)tile).charges > 0)
			{
				if (((TileWeatherController)tile).mode == 0)
					icon_side = icon_side_sun_on;
				else if (((TileWeatherController)tile).mode == 1)
					icon_side = icon_side_rain_on;
				else
					icon_side = icon_side_thunder_on;
			}else{
				if (((TileWeatherController)tile).mode == 0)
					icon_side = icon_side_sun_off;
				else if (((TileWeatherController)tile).mode == 1)
					icon_side = icon_side_rain_off;
				else
					icon_side = icon_side_thunder_off;
			}
		}
		

	
			if (side > 1)
				return icon_side;
			else if (side == 0)
				return icon_bottom;
			else
				return icon_top;
		
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{

		if (side > 1)
			return icon_side_sun_on;
		else if (side == 0)
			return icon_bottom;
		else
			return icon_top;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(final Item item, final CreativeTabs tab, final List par3list)
	{
		par3list.add(new ItemStack(item, 1, 0));
	}

	@Override
	public boolean onBlockActivated(final World world, final int x, final int y, final int z, final EntityPlayer entityPlayer, final int par1, final float par2, final float par3, final float par4)
	{
		if (!world.isRemote) {
			FMLNetworkHandler.openGui(entityPlayer, DraconicEvolution.instance, GuiHandler.GUIID_WEATHER_CONTROLLER, world, x, y, z);
		}
		return true;
	}

	public String getUnlocalizedWeatherControllerName()
	{
		return String.format("tile.%s%s", References.RESOURCESPREFIX, "weatherController/weatherController");
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2)
	{
		return new TileWeatherController();
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

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
	{
		TileWeatherController tile = (TileWeatherController) world.getTileEntity(x, y, z);
		if (tile != null){
			if(!tile.lastTickInput){
				if (world.isBlockIndirectlyGettingPowered(x, y, z)){
					tile.activate();
				}
			}
			tile.lastTickInput = world.isBlockIndirectlyGettingPowered(x, y, z);
		}
	}
}
