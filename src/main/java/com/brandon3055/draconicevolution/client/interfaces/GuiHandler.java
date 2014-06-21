package draconicevolution.client.interfaces;

import draconicevolution.DraconicEvolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import draconicevolution.common.container.ContainerGrinder;
import draconicevolution.common.container.ContainerPlayerDetector;
import draconicevolution.common.container.ContainerReader;
import draconicevolution.common.container.ContainerSunDial;
import draconicevolution.common.container.ContainerWeatherController;
import draconicevolution.common.core.utills.InventoryReader;
import draconicevolution.common.tileentities.TileGrinder;
import draconicevolution.common.tileentities.TileParticleGenerator;
import draconicevolution.common.tileentities.TilePlayerDetectorAdvanced;
import draconicevolution.common.tileentities.TileSunDial;
import draconicevolution.common.tileentities.TileWeatherController;

public class GuiHandler implements IGuiHandler {

	public static final int GUIID_WEATHER_CONTROLLER = 0; 
	public static final int GUIID_SUN_DIAL = 1;
	public static final int GUIID_GRINDER = 2;
	public static final int GUIID_TELEPORTER = 3;
	public static final int GUIID_READER = 4;
	public static final int GUIID_PARTICLEGEN = 5;
	public static final int GUIID_PLAYERDETECTOR = 6;
	
	public GuiHandler() {
		NetworkRegistry.INSTANCE.registerGuiHandler(DraconicEvolution.instance, this);
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		switch (ID) {
		case GUIID_WEATHER_CONTROLLER:
			TileEntity te = world.getTileEntity(x, y, z);
			if (te != null && te instanceof TileWeatherController) {
				return new ContainerWeatherController(player.inventory, (TileWeatherController) te);
			}
			break;
		case GUIID_SUN_DIAL:
			TileEntity te1 = world.getTileEntity(x, y, z);
			if (te1 != null && te1 instanceof TileSunDial) {
				return new ContainerSunDial(player.inventory, (TileSunDial) te1);
			}
			break;
		case GUIID_GRINDER:
			TileEntity te2 = world.getTileEntity(x, y, z);
			if (te2 != null && te2 instanceof TileGrinder) {
				return new ContainerGrinder(player.inventory, (TileGrinder) te2);
			}
			break;
		case GUIID_READER:
			return new ContainerReader(player.inventory, new InventoryReader(player));
		case GUIID_PLAYERDETECTOR:
			TileEntity detector = world.getTileEntity(x, y, z);
			if (detector != null && detector instanceof TilePlayerDetectorAdvanced) {
				return new ContainerPlayerDetector(player.inventory, (TilePlayerDetectorAdvanced) detector);
			}
			break;
		}

		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		switch (ID) {
		case GUIID_WEATHER_CONTROLLER:
			TileEntity te = world.getTileEntity(x, y, z);
			if (te != null && te instanceof TileWeatherController) {
				return new GUIWeatherController(player.inventory, (TileWeatherController) te);
			}
			break;
		case GUIID_SUN_DIAL:
			TileEntity te1 = world.getTileEntity(x, y, z);
			if (te1 != null && te1 instanceof TileSunDial) {
				return new GUISunDial(player.inventory, (TileSunDial) te1);
			}
			break;
		case GUIID_TELEPORTER:
			return new GUITeleporter(player);
		case GUIID_GRINDER:
			TileEntity te2 = world.getTileEntity(x, y, z);
			if (te2 != null && te2 instanceof TileGrinder) {
				return new GUIGrinder(player.inventory, (TileGrinder) te2);
			}
			break;
		case GUIID_READER:
			return new GUIReader(player.inventory, new InventoryReader(player));
		case GUIID_PARTICLEGEN:
			TileEntity gen = world.getTileEntity(x, y, z);
			return (gen != null && gen instanceof TileParticleGenerator) ? new GUIParticleGenerator((TileParticleGenerator)gen, player) : null;
		case GUIID_PLAYERDETECTOR:
			TileEntity detector = world.getTileEntity(x, y, z);
			if (detector != null && detector instanceof TilePlayerDetectorAdvanced) {
				return new GUIPlayerDetector(player.inventory, (TilePlayerDetectorAdvanced) detector);
			}
		}

		return null;
	}

}
