package com.brandon3055.draconicevolution.client.interfaces;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.container.*;
import com.brandon3055.draconicevolution.common.tileentities.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import com.brandon3055.draconicevolution.common.core.utills.InventoryReader;

public class GuiHandler implements IGuiHandler {

	public static final int GUIID_WEATHER_CONTROLLER = 0; 
	public static final int GUIID_SUN_DIAL = 1;
	public static final int GUIID_GRINDER = 2;
	public static final int GUIID_TELEPORTER = 3;
	public static final int GUIID_READER = 4;
	public static final int GUIID_PARTICLEGEN = 5;
	public static final int GUIID_PLAYERDETECTOR = 6;
	public static final int GUIID_ENERGY_INFUSER = 7;
	
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
		case GUIID_ENERGY_INFUSER:
		TileEntity infuser = world.getTileEntity(x, y, z);
		if (infuser != null && infuser instanceof TileEnergyInfuser) {
			return new ContainerEnergyInfuser(player.inventory, (TileEnergyInfuser) infuser);
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
		case GUIID_ENERGY_INFUSER:
			TileEntity infuser = world.getTileEntity(x, y, z);
			if (infuser != null && infuser instanceof TileEnergyInfuser) {
				return new GUIEnergyInfuser(player.inventory, (TileEnergyInfuser) infuser);
			}
		}

		return null;
	}

}
