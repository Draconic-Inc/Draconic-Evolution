//package com.brandon3055.draconicevolution.integration;
//
//import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalDirectIO;
//import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalRelay;
//import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalWirelessIO;
//import com.brandon3055.draconicevolution.client.gui.*;
//import com.brandon3055.draconicevolution.utils.LogHelper;
//import com.brandon3055.projectintelligence.api.IGuiDocHandler;
//import com.brandon3055.projectintelligence.api.IGuiDocRegistry;
//import com.brandon3055.projectintelligence.api.PiAPI;
//import net.minecraftforge.fml.common.Loader;
//import net.minecraftforge.fml.common.Optional;
//
//import java.awt.*;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by brandon3055 on 20/09/18.
// */
//public class PIIntegration {
//
//    public static void loadPIIntegration() {
//        if (Loader.isModLoaded("projectintelligence")) {
//            LogHelper.dev("Loading PI Integration...");
//            load();
//        }
//    }
//
//    @Optional.Method(modid = "projectintelligence")
//    private static void load() {
//        IGuiDocRegistry registry = PiAPI.getGuiDocRegistry();
//        if (registry == null) return;
//
//        registry.registerGuiDocPages(GuiReactor.class, "draconicevolution:draconic_reactor", "draconicevolution:draconic_reactor/basic_operation", "draconicevolution:draconic_reactor/regulation_and_safety", "draconicevolution:draconic_reactor/reactor_explosion");
//        registry.registerGuiHandler(GuiReactor.class, new IGuiDocHandler<GuiReactor>() {
//            @Override
//            public Rectangle getCollapsedArea(GuiReactor gui) {
//                return new Rectangle(gui.guiLeft() - 25, gui.guiTop() + 3, 25, 25);
//            }
//
//            @Override
//            public Rectangle getExpandedArea(GuiReactor gui) {
//                return new Rectangle(gui.guiLeft + 4, gui.guiTop + 4, gui.xSize - 8, gui.ySize - 8);
//            }
//        });
//
//        registry.registerGuiDocPages(GuiFusionCraftingCore.class, "draconicevolution:fusion_crafting", "draconicevolution:fusion_crafting/fusion_crafting_injectors", "draconicevolution:fusion_crafting/fusion_crafting_setup");
//        registry.registerGuiDocPages(GuiDraconiumChest.class, "draconicevolution:draconium_chest");
//        registry.registerGuiHandler(GuiDraconiumChest.class, new IGuiDocHandler<GuiDraconiumChest>() {
//            @Override
//            public Rectangle getCollapsedArea(GuiDraconiumChest gui) {
//                return new Rectangle(gui.guiLeft + gui.xSize - 30, gui.guiTop + gui.ySize - 55, 25, 25);
//            }
//
//            @Override
//            public Rectangle getExpandedArea(GuiDraconiumChest gui) {
//                return new Rectangle(gui.guiLeft + 5, gui.guiTop + 5, gui.xSize - 10, 181);
//            }
//        });
//        registry.registerGuiDocPages(GuiEnergyCore.class, "draconicevolution:energy_storage_core", "draconicevolution:energy_storage_core/energy_core_setup", "draconicevolution:energy_storage_core/energy_core_stabilizer", "draconicevolution:energy_storage_core/energy_pylon");
//
//        registry.registerGuiDocPages(GuiEnergyCrystal.class, gui -> {
//            List<String> list = new ArrayList<>();
//            list.add("draconicevolution:energy_network");
//            list.add("draconicevolution:energy_network/the_basics");
//            if (gui.tile instanceof TileCrystalDirectIO) {
//                list.add("draconicevolution:energy_network/energy_io_crystals");
//            }
//            else if (gui.tile instanceof TileCrystalRelay) {
//                list.add("draconicevolution:energy_network/relay_crystals");
//            }
//            else if (gui.tile instanceof TileCrystalWirelessIO) {
//                list.add("draconicevolution:energy_network/wireless_crystals");
//            }
//            return list;
//        });
//
//        registry.registerGuiDocPages(GuiFlowGate.class, "draconicevolution:flow_gates");
//        registry.registerGuiDocPages(GuiDissEnchanter.class, "draconicevolution:disenchanter");
//
//        registry.registerGuiDocPages(GuiDislocator.class, "draconicevolution:dislocation/advanced_dislocator");
//        registry.registerGuiHandler(GuiDislocator.class, new IGuiDocHandler<GuiDislocator>() {
//            @Override
//            public Rectangle getCollapsedArea(GuiDislocator gui) {
//                return new Rectangle(gui.guiLeft() - 25, gui.guiTop() + 3, 25, 25);
//            }
//
//            @Override
//            public Rectangle getExpandedArea(GuiDislocator gui) {
//                int availWidth = gui.guiLeft();
//                if (availWidth < 160) {
//                    int width = Math.max(200, gui.xSize);
//                    int height = Math.max(200, gui.ySize);
//                    return new Rectangle(gui.width / 2 - width / 2, gui.height / 2 - height / 2, width, height);
//                }
//                int width = Math.max(availWidth - 25, Math.min(200, availWidth));
//                int height = Math.max(gui.ySize, 200);
//                return new Rectangle(availWidth - width, gui.height / 2 - height / 2, width, height);
//            }
//        });
//
//        registry.registerGuiDocPages(GuiGenerator.class,"draconicevolution:generator");
//        registry.registerGuiDocPages(GuiEnergyinfuser. class,"draconicevolution:energy_infuser");
//        registry.registerGuiDocPages(GuiGrinder. class,"draconicevolution:mob_grinder");
//        registry.registerGuiDocPages(GuiEntityDetector. class,"draconicevolution:entity_detector");
//
//        LogHelper.dev("Loaded PI Integration");
//    }
//}
