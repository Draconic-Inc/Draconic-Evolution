//package com.brandon3055.draconicevolution;
//
//import com.brandon3055.brandonscore.inventory.ContainerBCBase;
//import com.brandon3055.brandonscore.inventory.ContainerSlotLayout;
//import com.brandon3055.brandonscore.inventory.ContainerSlotLayout.LayoutFactory;
//import com.brandon3055.brandonscore.inventory.PlayerSlot;
//import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
//import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
//import com.brandon3055.draconicevolution.blocks.tileentity.*;
//import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFlowGate;
//import com.brandon3055.draconicevolution.client.gui.*;
//import com.brandon3055.draconicevolution.client.gui.toolconfig.GuiJunkFilter;
//import com.brandon3055.draconicevolution.inventory.*;
//import com.brandon3055.draconicevolution.items.tools.old.MiningToolBase;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.item.ItemStack;
//import net.minecraft.tileentity.TileEntity;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//import net.minecraftforge.fml.common.network.IGuiHandler;
//import net.minecraftforge.items.CapabilityItemHandler;
//import net.minecraftforge.items.IItemHandler;
//
//public class GuiHandler implements IGuiHandler {
//
//    public static final GuiHandler instance = new GuiHandler();
//
//    public static final int GUIID_CELESTIAL = 0;
//    public static final int GUIID_SUN_DIAL = 1;
//    public static final int GUIID_GRINDER = 2;
//    public static final int GUIID_TELEPORTER = 3;
//    public static final int GUIID_PARTICLEGEN = 5;
//    public static final int GUIID_ENTITY_DETECTOR = 6;
//    public static final int GUIID_ENERGY_INFUSER = 7;
//    public static final int GUIID_GENERATOR = 8;
//    public static final int GUIID_MANUAL = 9;
//    public static final int GUIID_DISSENCHANTER = 10;
//    public static final int GUIID_DRACONIUM_CHEST = 11;
//    public static final int GUIID_TOOL_CONFIG = 12;
//    public static final int GUIID_FLOW_GATE = 13;
//    public static final int GUIID_REACTOR = 14;
//    public static final int GUIID_UPGRADE_MODIFIER = 15;
//    public static final int GUIID_ENERGY_CORE = 16;
//    public static final int GUIID_FUSION_CRAFTING = 17;
//    public static final int GUIID_ENERGY_CRYSTAL = 18;
//    public static final int GUIID_JUNK_FILTER = 19;
//
//    public static final int GUIID_CONTAINER_TEMPLATE = 100;
//
//    public static void initialize() {
//        NetworkRegistry.INSTANCE.registerGuiHandler(DraconicEvolution.instance, instance);
//    }
//
//    @Override
//    public Object getServerGuiElement(int ID, PlayerEntity player, World world, int x, int y, int z) {
//        BlockPos pos = new BlockPos(x, y, z);
//        TileEntity tile = world.getTileEntity(pos);
//        switch (ID) {
//            case GUIID_CELESTIAL:
//                if (tile instanceof TileCelestialManipulator) {
//                    return new ContainerDummy(((TileCelestialManipulator) tile), player, 19, 120);
//                }
//                break;
//            case GUIID_GRINDER:
//                if (tile instanceof TileGrinder) {
//                    return new ContainerBCBase<>(player, (TileGrinder) tile, GRINDER_LAYOUT_FACTORY);
//                }
//                break;
//            case GUIID_ENTITY_DETECTOR:
//                if (tile instanceof TileEntityDetector) {
//                    return new ContainerDummy((TileEntityDetector) tile, player, 19, 120);
//                }
//                break;
//            case GUIID_ENERGY_INFUSER:
//                if (tile instanceof TileEnergyInfuser) {
//                    return new ContainerEnergyInfuser(player, (TileEnergyInfuser) tile);
//                }
//                break;
//            case GUIID_GENERATOR:
//                if (tile instanceof TileGenerator) {
//                    return new ContainerBCBase<>(player, (TileGenerator) tile, GENERATOR_LAYOUT_FACTORY);
//                }
//                break;
//            case GUIID_DISSENCHANTER:
//                if (tile instanceof TileDissEnchanter) {
//                    return new ContainerDissEnchanter(player.inventory, (TileDissEnchanter) tile);
//                }
//                break;
//            case GUIID_DRACONIUM_CHEST:
//                if (tile instanceof TileDraconiumChest) {
//                    return new ContainerDraconiumChest(player, (TileDraconiumChest) tile);
//                }
//                break;
//            case GUIID_REACTOR:
//                if (tile instanceof TileReactorCore) {
//                    return new ContainerReactor(player, (TileReactorCore) tile);
//                }
//                break;
//            case GUIID_UPGRADE_MODIFIER:
////                if (tile instanceof TileUpgradeModifier) {
////                    return new ContainerUpgradeModifier(player, (TileUpgradeModifier) tile);
////                }
//                break;
//            case GUIID_ENERGY_CORE:
//                if (tile instanceof TileEnergyStorageCore) {
//                    return new ContainerBCBase<>(player, (TileEnergyStorageCore) tile).addPlayerSlots(10, 116);
//                }
//                break;
//            case GUIID_FUSION_CRAFTING:
//                if (tile instanceof TileFusionCraftingCore) {
//                    return new ContainerFusionCraftingCore(player, (TileFusionCraftingCore) tile);
//                }
//                break;
//            case GUIID_FLOW_GATE:
//                if (tile instanceof TileFlowGate) {
//                    return new ContainerDummy((TileFlowGate) tile, player, -1, -1);
//                }
//                break;
//            case GUIID_ENERGY_CRYSTAL:
//                if (tile instanceof TileCrystalBase) {
//                    return new ContainerEnergyCrystal(player, (TileCrystalBase) tile);
//                }
//                break;
//            case GUIID_JUNK_FILTER:
//                PlayerSlot slot = PlayerSlot.fromIndexes(x, y);
//                ItemStack stack = slot.getStackInSlot(player);
//                if (stack.getItem() instanceof MiningToolBase && stack.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
//                    IItemHandler handler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
//                    return new ContainerJunkFilter(player, slot, handler);
//                }
//                break;
//
////			case GUIID_CONTAINER_TEMPLATE:
////				if (containerTemp != null && containerTemp instanceof TileContainerTemplate) {
////					return new ContainerTemplate(player.inventory, (TileContainerTemplate) containerTemp);
////				}
////				break;
//
//
//            case 2016: {
//                return new ContainerRecipeBuilder(player);
//            }
//        }
//
//        return null;
//    }
//
//    @Override
//    public Object getClientGuiElement(int ID, PlayerEntity player, World world, int x, int y, int z) {
//        BlockPos pos = new BlockPos(x, y, z);
//        TileEntity tile = world.getTileEntity(pos);
//        switch (ID) {
//            case GUIID_CELESTIAL:
//                if (tile instanceof TileCelestialManipulator) {
//                    return new GuiCelestialManipulator(player, (TileCelestialManipulator) tile);
//                }
//                break;
//            case GUIID_TELEPORTER:
//                return new GuiDislocator(player);
//            case GUIID_GRINDER:
//                if (tile instanceof TileGrinder) {
//                    return new GuiGrinder(player, (TileGrinder) tile, GRINDER_LAYOUT_FACTORY);
//                }
//                break;
//            case GUIID_PARTICLEGEN:
//                if (tile instanceof TileParticleGenerator) {
//                    return new GuiParticleGenerator((TileParticleGenerator) tile);
//                }
//            case GUIID_ENTITY_DETECTOR:
//                if (tile instanceof TileEntityDetector) {
//                    return new GuiEntityDetector(player, (TileEntityDetector) tile);
//                }
//                break;
//            case GUIID_ENERGY_INFUSER:
//                if (tile instanceof TileEnergyInfuser) {
//                    return new GuiEnergyinfuser(player, (TileEnergyInfuser) tile);
//                }
//                break;
//            case GUIID_GENERATOR:
//                if (tile instanceof TileGenerator) {
//                    return new GuiGenerator(player, (TileGenerator) tile, GENERATOR_LAYOUT_FACTORY);
//                }
//                break;
//            case GUIID_DISSENCHANTER:
//                if (tile instanceof TileDissEnchanter) {
//                    return new GuiDissEnchanter(player, new ContainerDissEnchanter(player.inventory, (TileDissEnchanter) tile));
//                }
//                break;
//            case GUIID_DRACONIUM_CHEST:
//                if (tile instanceof TileDraconiumChest) {
//                    return new GuiDraconiumChest((TileDraconiumChest) tile, new ContainerDraconiumChest(player, (TileDraconiumChest) tile));
//                }
//                break;
//            case GUIID_REACTOR:
//                if (tile instanceof TileReactorCore) {
//                    return new GuiReactor(player, (TileReactorCore) tile);
//                }
//                break;
//            case GUIID_FLOW_GATE:
//                if (tile instanceof TileFlowGate) {
//                    return new GuiFlowGate((TileFlowGate) tile, player);
//                }
//                break;
//            case GUIID_UPGRADE_MODIFIER:
////                if (tile instanceof TileUpgradeModifier) {
////                    return new GuiUpgradeModifier(player, (TileUpgradeModifier) tile, new ContainerUpgradeModifier(player, (TileUpgradeModifier) tile));
////                }
//                break;
//            case GUIID_ENERGY_CORE:
//                if (tile instanceof TileEnergyStorageCore) {
//                    return new GuiEnergyCore(player, (TileEnergyStorageCore) tile);
//                }
//                break;
//            case GUIID_FUSION_CRAFTING:
//                if (tile instanceof TileFusionCraftingCore) {
//                    return new GuiFusionCraftingCore(player, (TileFusionCraftingCore) tile);
//                }
//                break;
//            case GUIID_ENERGY_CRYSTAL:
//                if (tile instanceof TileCrystalBase) {
//                    return new GuiEnergyCrystal(player, (TileCrystalBase) tile);
//                }
//                break;
//            case GUIID_JUNK_FILTER:
//                PlayerSlot slot = PlayerSlot.fromIndexes(x, y);
//                ItemStack stack = slot.getStackInSlot(player);
//                if (stack.getItem() instanceof MiningToolBase && stack.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
//                    IItemHandler handler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
//                    return new GuiJunkFilter(player, slot, handler);
//                }
//                break;
//
//
////			case GUIID_CONTAINER_TEMPLATE:
////				if (containerTemp != null && containerTemp instanceof TileContainerTemplate) {
////					return new GUIContainerTemplate(player.inventory, (TileContainerTemplate) containerTemp);
////				}
////				break;
//
//
//            case 2016: {
//                return new GuiRecipeBuilder(player);
//            }
//        }
//
//        return null;
//    }
//
//
//    private static LayoutFactory<TileGenerator> GENERATOR_LAYOUT_FACTORY = (player, tile) -> new ContainerSlotLayout().playerMain(player).allTile(tile.itemHandler);
//    private static LayoutFactory<TileGrinder> GRINDER_LAYOUT_FACTORY = (player, tile) -> new ContainerSlotLayout().playerMain(player).allTile(tile.itemHandler);
//
//}
