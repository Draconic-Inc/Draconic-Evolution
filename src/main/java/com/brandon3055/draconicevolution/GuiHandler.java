package com.brandon3055.draconicevolution;

import com.brandon3055.brandonscore.inventory.ContainerBCBase;
import com.brandon3055.draconicevolution.blocks.tileentity.*;
import com.brandon3055.draconicevolution.client.gui.*;
import com.brandon3055.draconicevolution.inventory.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class GuiHandler implements IGuiHandler {

	public static final GuiHandler instance = new GuiHandler();

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
    public static final int GUIID_ENERGY_CORE = 16;
    public static final int GUIID_FUSION_CRAFTING = 17;

	public static final int GUIID_CONTAINER_TEMPLATE = 100;

    public static void initialize(){
		NetworkRegistry.INSTANCE.registerGuiHandler(DraconicEvolution.instance, instance);
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity tileEntity = world.getTileEntity(pos);
		switch (ID) {
//			case GUIID_WEATHER_CONTROLLER:
//				if (te != null && te instanceof TileWeatherController) {
//					return new ContainerWeatherController(player.inventory, (TileWeatherController) te);
//				}
//				break;
//			case GUIID_SUN_DIAL:
//				if (te1 != null && te1 instanceof TileSunDial) {
//					return new ContainerSunDial(player.inventory, (TileSunDial) te1);
//				}
//				break;
			case GUIID_GRINDER:
				if (tileEntity != null && tileEntity instanceof TileGrinder) {
					return new ContainerGrinder(player.inventory, (TileGrinder) tileEntity);
				}
				break;
//			case GUIID_PLAYERDETECTOR:
//				if (detector != null && detector instanceof TilePlayerDetectorAdvanced) {
//					return new ContainerPlayerDetector(player.inventory, (TilePlayerDetectorAdvanced) detector);
//				}
//				break;
			case GUIID_ENERGY_INFUSER:
				if (tileEntity != null && tileEntity instanceof TileEnergyInfuser) {
					return new ContainerEnergyInfuser(player, (TileEnergyInfuser) tileEntity);
				}
				break;
			case GUIID_GENERATOR:
				if (tileEntity != null && tileEntity instanceof TileGenerator) {
					return new ContainerGenerator(player, (TileGenerator) tileEntity);
				}
				break;
//			case GUIID_DISSENCHANTER:
//				if (dissenchanter != null && dissenchanter instanceof TileDissEnchanter) {
//					return new ContainerDissEnchanter(player.inventory, (TileDissEnchanter) dissenchanter);
//				}
//				break;
//			case GUIID_DRACONIC_CHEST:
//				if (containerChest != null && containerChest instanceof TileDraconiumChest) {
//					return new ContainerDraconiumChest(player.inventory, (TileDraconiumChest) containerChest);
//				}
//				break;
//			case GUIID_REACTOR:
//				if (reactor != null && reactor instanceof TileReactorCore) {
//					return new ContainerReactor(player, (TileReactorCore) reactor);
//				}
//				break;
//			case GUIID_TOOL_CONFIG:
//				return new ContainerAdvTool(player.inventory, new InventoryTool(player, null));

			case GUIID_UPGRADE_MODIFIER:
				if (tileEntity != null && tileEntity instanceof TileUpgradeModifier) {
					return new ContainerUpgradeModifier(player, (TileUpgradeModifier) tileEntity);
				}
				break;
			case GUIID_ENERGY_CORE:
				if (tileEntity != null && tileEntity instanceof TileEnergyStorageCore) {
					return new ContainerBCBase<TileEnergyStorageCore>(player, (TileEnergyStorageCore) tileEntity).addPlayerSlots(10, 116);
				}
				break;
            case GUIID_FUSION_CRAFTING:
                if (tileEntity != null && tileEntity instanceof TileFusionCraftingCore) {
                    return new ContainerFusionCraftingCore(player, (TileFusionCraftingCore) tileEntity);
                }
                break;

//			case GUIID_CONTAINER_TEMPLATE:
//				if (containerTemp != null && containerTemp instanceof TileContainerTemplate) {
//					return new ContainerTemplate(player.inventory, (TileContainerTemplate) containerTemp);
//				}
//				break;
		}

		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity tileEntity = world.getTileEntity(pos);
		switch (ID) {
			case GUIID_WEATHER_CONTROLLER:
//				if (te != null && te instanceof TileWeatherController) {
//					return new GUIWeatherController(player.inventory, (TileWeatherController) te);
//				}
//				break;
//			case GUIID_SUN_DIAL:
//				if (te1 != null && te1 instanceof TileSunDial) {
//					return new GUISunDial(player.inventory, (TileSunDial) te1);
//				}
//				break;
			case GUIID_TELEPORTER:
				return new GuiDislocator(player);
			case GUIID_GRINDER:
				if (tileEntity != null && tileEntity instanceof TileGrinder) {
					return new GuiGrinder(player.inventory, (TileGrinder) tileEntity);
				}
				break;
//			case GUIID_PARTICLEGEN:
//				return (gen != null && gen instanceof TileParticleGenerator) ? new GUIParticleGenerator((TileParticleGenerator) gen, player) : null;
//			case GUIID_PLAYERDETECTOR:
//				if (detector != null && detector instanceof TilePlayerDetectorAdvanced) {
//					return new GUIPlayerDetector(player.inventory, (TilePlayerDetectorAdvanced) detector);
//				}
//				break;
			case GUIID_ENERGY_INFUSER:
				if (tileEntity != null && tileEntity instanceof TileEnergyInfuser) {
					return new GuiEnergyinfuser(player, (TileEnergyInfuser) tileEntity);
				}
				break;
			case GUIID_GENERATOR:
				if (tileEntity != null && tileEntity instanceof TileGenerator) {
					return new GuiGenerator(player, (TileGenerator) tileEntity);
				}
				break;
//			case GUIID_MANUAL:
//				return new GUIManual();
//			case GUIID_DISSENCHANTER:
//				if (dissenchanter != null && dissenchanter instanceof TileDissEnchanter) {
//					return new GUIDissEnchanter(player.inventory, (TileDissEnchanter) dissenchanter);
//				}
//				break;
//			case GUIID_DRACONIC_CHEST:
//				if (containerChest != null && containerChest instanceof TileDraconiumChest) {
//					return new GUIDraconiumChest(player.inventory, (TileDraconiumChest) containerChest);
//				}
//				break;
//			case GUIID_REACTOR:
//				if (reactor != null && reactor instanceof TileReactorCore) {
//					return new GUIReactor(player, (TileReactorCore) reactor, new ContainerReactor(player, (TileReactorCore) reactor));
//				}
//				break;
//			case GUIID_TOOL_CONFIG:
//				return new GUIToolConfig(player, new ContainerAdvTool(player.inventory, new InventoryTool(player, null)));
//			case GUIID_FLOW_GATE:
//				return world.tileEntity(x, y, z) instanceof TileGate ? new GUIFlowGate((TileGate)world.tileEntity(x, y, z)) : null;
			case GUIID_UPGRADE_MODIFIER:
				if (tileEntity != null && tileEntity instanceof TileUpgradeModifier) {
					return new GuiUpgradeModifier(player, (TileUpgradeModifier) tileEntity, new ContainerUpgradeModifier(player, (TileUpgradeModifier) tileEntity));
				}
				break;
            case GUIID_ENERGY_CORE:
                if (tileEntity != null && tileEntity instanceof TileEnergyStorageCore) {
                    return new GuiEnergyCore(player, (TileEnergyStorageCore) tileEntity);
                }
                break;
            case GUIID_FUSION_CRAFTING:
                if (tileEntity != null && tileEntity instanceof TileFusionCraftingCore) {
                    return new GuiFusionCraftingCore(player, (TileFusionCraftingCore) tileEntity);
                }
                break;
//			case GUIID_CONTAINER_TEMPLATE:
//				if (containerTemp != null && containerTemp instanceof TileContainerTemplate) {
//					return new GUIContainerTemplate(player.inventory, (TileContainerTemplate) containerTemp);
//				}
//				break;
		}

		return null;
	}

}
