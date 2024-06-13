package com.brandon3055.draconicevolution.client.gui;

import codechicken.lib.gui.modular.ModularGui;
import codechicken.lib.gui.modular.ModularGuiContainer;
import codechicken.lib.gui.modular.elements.*;
import codechicken.lib.gui.modular.lib.BackgroundRender;
import codechicken.lib.gui.modular.lib.Constraints;
import codechicken.lib.gui.modular.lib.GuiRender;
import codechicken.lib.gui.modular.lib.container.ContainerGuiProvider;
import codechicken.lib.gui.modular.lib.container.ContainerScreenAccess;
import codechicken.lib.gui.modular.lib.geometry.Direction;
import codechicken.lib.gui.modular.lib.geometry.GuiParent;
import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.model.OBJParser;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import com.brandon3055.brandonscore.client.BCGuiTextures;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.InfoPanel;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.ButtonRow;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGenerator;
import com.brandon3055.draconicevolution.client.DEGuiTextures;
import com.brandon3055.draconicevolution.inventory.GeneratorMenu;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static codechicken.lib.gui.modular.lib.geometry.Constraint.*;
import static codechicken.lib.gui.modular.lib.geometry.GeoParam.*;
import static net.minecraft.ChatFormatting.GOLD;
import static net.minecraft.ChatFormatting.GRAY;

public class GeneratorGui extends ContainerGuiProvider<GeneratorMenu> {
    private static final GuiToolkit TOOLKIT = new GuiToolkit("gui.draconicevolution.generator");
    public static final int GUI_WIDTH = 176;
    public static final int GUI_HEIGHT = 166;

    private static final RenderType modelType = RenderType.entitySolid(new ResourceLocation(DraconicEvolution.MODID, "textures/block/generator/generator_2.png"));
    private static final CCModel storageModel;

//    private ModuleGridPanel gridPanel;

    static {
        Map<String, CCModel> map = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/block/generator/generator_storage.obj")).quads().ignoreMtl().parse();
        storageModel = CCModel.combine(map.values());
        storageModel.computeNormals();
    }

    @Override
    public GuiElement<?> createRootElement(ModularGui gui) {
        GuiManipulable root = new GuiManipulable(gui).addMoveHandle(3).enableCursors(true);
        GuiTexture bg = new GuiTexture(root.getContentElement(), DEGuiTextures.themedGetter("generator"));
        Constraints.bind(bg, root.getContentElement());
        return root;
    }

    @Override
    public void buildGui(ModularGui gui, ContainerScreenAccess<GeneratorMenu> screenAccess) {
        gui.initStandardGui(GUI_WIDTH, GUI_HEIGHT);
        GeneratorMenu menu = screenAccess.getMenu();
        TileGenerator tile = menu.tile;
        GuiElement<?> root = gui.getRoot();
        TOOLKIT.createHeading(root, gui.getGuiTitle(), true);

        InfoPanel infoPanel = InfoPanel.create(root);
        infoPanel.labeledValue(TOOLKIT.translate("fuel_efficiency").withStyle(GOLD), () -> Component.literal(tile.mode.get().getEfficiency() + "%").withStyle(GRAY));
        infoPanel.labeledValue(TOOLKIT.translate("output_power").withStyle(GOLD), () -> Component.literal(tile.productionRate.get() + " / " + tile.mode.get().powerOutput + " OP/t").withStyle(GRAY));
        infoPanel.labeledValue(TOOLKIT.translate("current_fuel_value").withStyle(GOLD), () -> Component.literal(tile.fuelRemaining.get() == 0 ? "n/a" : tile.fuelRemaining.get() + " / " + tile.fuelValue.get()).withStyle(GRAY));

//        gridPanel = new ModuleGridPanel(root, menu)
//                .setGridPos(ModuleGridPanel.GridPos.BOTTOM_RIGHT, 2);

        ButtonRow buttonRow = ButtonRow.topRightInside(root, Direction.DOWN, 3, 3).setSpacing(1);
        buttonRow.addButton(e -> TOOLKIT.createThemeButton(e));
        buttonRow.addButton(e -> TOOLKIT.createInfoButton(e, infoPanel));
        buttonRow.addButton(e -> TOOLKIT.createRSSwitch(e, screenAccess.getMenu().tile));
//        buttonRow.addButton(e -> TOOLKIT.createThemedIconButton(e, "grid_small").onPress(gridPanel::toggleExpanded));

        StorageRenderer fancyBg = new StorageRenderer(root);
        Constraints.bind(fancyBg, root);

        GuiSlots fuelInv = new GuiSlots(fancyBg, screenAccess, menu.fuel, 3)
                .setSlotTexture(slot -> BCGuiTextures.getThemed("slot"))
                .setEmptyIcon(BCGuiTextures.get("slots/fuel"));
        Constraints.placeInside(fuelInv, root, Constraints.LayoutPos.TOP_LEFT, 64, 28);

        var playInv = GuiSlots.player(root, screenAccess, menu.main, menu.hotBar);
        playInv.stream().forEach(e -> e.setSlotTexture(slot -> BCGuiTextures.getThemed("slot")));
        Constraints.placeInside(playInv.container(), root, Constraints.LayoutPos.BOTTOM_CENTER, 0, -7);
        TOOLKIT.playerInvTitle(playInv.container());

        GuiSlots capInv = GuiSlots.singleSlot(root, screenAccess, menu.capacitor, 0)
                .setSlotTexture(slot -> BCGuiTextures.getThemed("slot"))
                .setEmptyIcon(BCGuiTextures.get("slots/energy"));

        //Energy Bar
        var energyBar = TOOLKIT.createEnergyBar(root, tile.opStorage);
        energyBar.container()
                .constrain(TOP, relative(root.get(TOP), 6))
                .constrain(BOTTOM, relative(playInv.container().get(TOP), -14))
                .constrain(LEFT, match(playInv.container().get(LEFT)))
                .constrain(WIDTH, literal(14));
        Constraints.placeInside(capInv, energyBar.container(), Constraints.LayoutPos.BOTTOM_RIGHT, 20, 0);
        Constraints.placeOutside(TOOLKIT.energySlotArrow(root, true, false), capInv, Constraints.LayoutPos.TOP_CENTER, -2, -2);

        //Mode Button
        GuiButton modButton = TOOLKIT.createFlat3DButton(root, () -> Component.translatable(tile.mode.get().unlocalizedName()));
        modButton.onPress(() -> tile.mode.set(tile.mode.get().next(Screen.hasShiftDown())), GuiButton.LEFT_CLICK);
        modButton.onPress(() -> tile.mode.set(tile.mode.get().next(true)), GuiButton.RIGHT_CLICK);
        Constraints.size(modButton, 100, 14);
        Constraints.placeInside(modButton, playInv.container(), Constraints.LayoutPos.TOP_RIGHT, 0, -16);
    }

    private static class StorageRenderer extends GuiElement<StorageRenderer> implements BackgroundRender {

        public StorageRenderer(@NotNull GuiParent<?> parent) {
            super(parent);
        }

        @Override
        public double getBackgroundDepth() {
            return 100;
        }

        @Override
        public void renderBackground(GuiRender render, double mouseX, double mouseY, float partialTicks) {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(modelType, render.buffers());

            Matrix4 mat = new Matrix4(render.pose());
            mat.translate(xMin() + 90, yMin() + 45, 50);
            double mx = (((mouseX - getModularGui().xMin()) / (float) getModularGui().xSize()) - 0.5F) * .1F;
            double my = (((mouseY - getModularGui().yMin()) / (float) getModularGui().ySize()) - 0.5F) * .1F;
            mat.apply(new Rotation(150 * MathHelper.torad, 1, 0, 0).with(new Rotation(10 * MathHelper.torad, -my, 1 + mx, 0)));
            mat.scale(7.5);
            storageModel.render(ccrs, mat);
        }
    }

    public static class Screen extends ModularGuiContainer<GeneratorMenu> {
        public Screen(GeneratorMenu menu, Inventory inv, Component title) {
            super(menu, inv, new GeneratorGui());
            getModularGui().setGuiTitle(title);
        }

//        @Override
//        public void renderFloatingItem(GuiRender render, ItemStack itemStack, int x, int y, String string) {
//            GeneratorGui gui = (GeneratorGui) modularGui.getProvider();
//            if (!gui.gridPanel.gridRenderer.renderStackOverride(render, itemStack, x, y, string)) {
//                super.renderFloatingItem(render, itemStack, x, y, string);
//            }
//        }
    }
}
