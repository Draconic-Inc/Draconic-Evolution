package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.gui.componentguis.GUIManual;
import com.brandon3055.draconicevolution.client.gui.componentguis.GUIReactor;
import com.brandon3055.draconicevolution.client.gui.componentguis.GUIToolConfig;
import com.brandon3055.draconicevolution.common.container.*;
import com.brandon3055.draconicevolution.common.inventory.InventoryTool;
import com.brandon3055.draconicevolution.common.tileentities.*;
import com.brandon3055.draconicevolution.common.tileentities.gates.TileGate;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor.TileReactorCore;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {

    public static final int GUIID_WEATHER_CONTROLLER = 0;
    public static final int GUIID_SUN_DIAL = 1;
    public static final int GUIID_GRINDER = 2;
    public static final int GUIID_TELEPORTER = 3;
    public static final int GUIID_PARTICLEGEN = 5;
    public static final int GUIID_PLAYERDETECTOR = 6;
    public static final int GUIID_ENERGY_INFUSER = 7;
    public static final int GUIID_GENERATOR = 8;
    public static final int GUIID_MANUAL = 9;
    public static final int GUIID_DISSENCHANTER = 10;
    public static final int GUIID_DRACONIC_CHEST = 11;
    public static final int GUIID_TOOL_CONFIG = 12;
    public static final int GUIID_FLOW_GATE = 13;
    public static final int GUIID_REACTOR = 14;
    public static final int GUIID_UPGRADE_MODIFIER = 15;
    public static final int GUIID_CONTAINER_TEMPLATE = 100;

    public GuiHandler() {
        NetworkRegistry.INSTANCE.registerGuiHandler(DraconicEvolution.instance, this);
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
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
            case GUIID_GENERATOR:
                TileEntity generator = world.getTileEntity(x, y, z);
                if (generator != null && generator instanceof TileGenerator) {
                    return new ContainerGenerator(player.inventory, (TileGenerator) generator);
                }
                break;
            case GUIID_DISSENCHANTER:
                TileEntity dissenchanter = world.getTileEntity(x, y, z);
                if (dissenchanter != null && dissenchanter instanceof TileDissEnchanter) {
                    return new ContainerDissEnchanter(player.inventory, (TileDissEnchanter) dissenchanter);
                }
                break;
            case GUIID_DRACONIC_CHEST:
                TileEntity containerChest = world.getTileEntity(x, y, z);
                if (containerChest != null && containerChest instanceof TileDraconiumChest) {
                    return new ContainerDraconiumChest(player.inventory, (TileDraconiumChest) containerChest);
                }
                break;
            case GUIID_REACTOR:
                TileEntity reactor = world.getTileEntity(x, y, z);
                if (reactor != null && reactor instanceof TileReactorCore) {
                    return new ContainerReactor(player, (TileReactorCore) reactor);
                }
                break;
            case GUIID_TOOL_CONFIG:
                return new ContainerAdvTool(player.inventory, new InventoryTool(player, null));
            case GUIID_UPGRADE_MODIFIER:
                TileEntity containerTemp = world.getTileEntity(x, y, z);
                if (containerTemp != null && containerTemp instanceof TileUpgradeModifier) {
                    return new ContainerUpgradeModifier(player.inventory, (TileUpgradeModifier) containerTemp);
                }
                break;

                //			case GUIID_CONTAINER_TEMPLATE:
                //				TileEntity containerTemp = world.getTileEntity(x, y, z);
                //				if (containerTemp != null && containerTemp instanceof TileContainerTemplate) {
                //					return new ContainerTemplate(player.inventory, (TileContainerTemplate) containerTemp);
                //				}
                //				break;
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
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
            case GUIID_PARTICLEGEN:
                TileEntity gen = world.getTileEntity(x, y, z);
                return (gen != null && gen instanceof TileParticleGenerator)
                        ? new GUIParticleGenerator((TileParticleGenerator) gen, player)
                        : null;
            case GUIID_PLAYERDETECTOR:
                TileEntity detector = world.getTileEntity(x, y, z);
                if (detector != null && detector instanceof TilePlayerDetectorAdvanced) {
                    return new GUIPlayerDetector(player.inventory, (TilePlayerDetectorAdvanced) detector);
                }
                break;
            case GUIID_ENERGY_INFUSER:
                TileEntity infuser = world.getTileEntity(x, y, z);
                if (infuser != null && infuser instanceof TileEnergyInfuser) {
                    return new GUIEnergyInfuser(player.inventory, (TileEnergyInfuser) infuser);
                }
                break;
            case GUIID_GENERATOR:
                TileEntity generator = world.getTileEntity(x, y, z);
                if (generator != null && generator instanceof TileGenerator) {
                    return new GUIGenerator(player.inventory, (TileGenerator) generator);
                }
                break;
            case GUIID_MANUAL:
                return new GUIManual();
            case GUIID_DISSENCHANTER:
                TileEntity dissenchanter = world.getTileEntity(x, y, z);
                if (dissenchanter != null && dissenchanter instanceof TileDissEnchanter) {
                    return new GUIDissEnchanter(player.inventory, (TileDissEnchanter) dissenchanter);
                }
                break;
            case GUIID_DRACONIC_CHEST:
                TileEntity containerChest = world.getTileEntity(x, y, z);
                if (containerChest != null && containerChest instanceof TileDraconiumChest) {
                    return new GUIDraconiumChest(player.inventory, (TileDraconiumChest) containerChest);
                }
                break;
            case GUIID_REACTOR:
                TileEntity reactor = world.getTileEntity(x, y, z);
                if (reactor != null && reactor instanceof TileReactorCore) {
                    return new GUIReactor(
                            player, (TileReactorCore) reactor, new ContainerReactor(player, (TileReactorCore) reactor));
                }
                break;
            case GUIID_TOOL_CONFIG:
                return new GUIToolConfig(
                        player, new ContainerAdvTool(player.inventory, new InventoryTool(player, null)));
            case GUIID_FLOW_GATE:
                return world.getTileEntity(x, y, z) instanceof TileGate
                        ? new GUIFlowGate((TileGate) world.getTileEntity(x, y, z))
                        : null;
            case GUIID_UPGRADE_MODIFIER:
                TileEntity containerTemp = world.getTileEntity(x, y, z);
                if (containerTemp != null && containerTemp instanceof TileUpgradeModifier) {
                    return new GUIUpgradeModifier(
                            player.inventory,
                            (TileUpgradeModifier) containerTemp,
                            new ContainerUpgradeModifier(player.inventory, (TileUpgradeModifier) containerTemp));
                }
                break;

                //			case GUIID_CONTAINER_TEMPLATE:
                //				TileEntity containerTemp = world.getTileEntity(x, y, z);
                //				if (containerTemp != null && containerTemp instanceof TileContainerTemplate) {
                //					return new GUIContainerTemplate(player.inventory, (TileContainerTemplate) containerTemp);
                //				}
                //				break;
        }

        return null;
    }
}
