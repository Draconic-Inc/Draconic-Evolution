package com.brandon3055.draconicevolution.client.gui.modular;

import codechicken.lib.gui.modular.ModularGui;
import codechicken.lib.gui.modular.ModularGuiContainer;
import codechicken.lib.gui.modular.elements.*;
import codechicken.lib.gui.modular.lib.Constraints;
import codechicken.lib.gui.modular.lib.GuiRender;
import codechicken.lib.gui.modular.lib.container.ContainerGuiProvider;
import codechicken.lib.gui.modular.lib.container.ContainerScreenAccess;
import codechicken.lib.gui.modular.lib.geometry.Axis;
import codechicken.lib.gui.modular.lib.geometry.Direction;
import codechicken.lib.gui.modular.lib.geometry.Position;
import codechicken.lib.gui.modular.sprite.Material;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.client.BCGuiTextures;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.HudConfigGui;
import com.brandon3055.brandonscore.client.gui.InfoPanel;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.ButtonRow;
import com.brandon3055.brandonscore.client.render.RenderUtils;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleGrid;
import com.brandon3055.draconicevolution.client.gui.ModuleGridRenderer;
import com.brandon3055.draconicevolution.integration.equipment.EquipmentManager;
import com.brandon3055.draconicevolution.inventory.ModularItemMenu;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static codechicken.lib.gui.modular.lib.geometry.Constraint.*;
import static codechicken.lib.gui.modular.lib.geometry.Constraint.relative;
import static codechicken.lib.gui.modular.lib.geometry.GeoParam.*;
import static com.brandon3055.brandonscore.BCConfig.darkMode;
import static net.minecraft.ChatFormatting.GOLD;
import static net.minecraft.ChatFormatting.GRAY;


/**
 * Created by brandon3055 on 19/4/20.
 */
public class ModularItemGui extends ContainerGuiProvider<ModularItemMenu> {
    public static final GuiToolkit TOOLKIT = new GuiToolkit("gui.draconicevolution.modular_item");
    private static AtomicBoolean infoExpanded = new AtomicBoolean(true);
    private ModuleGrid grid;
    private Inventory playerInv;
    private ModuleGridRenderer gridRenderer;
    private ModularItemMenu menu;
    private InfoPanel infoPanel;

    @Override
    public GuiElement<?> createRootElement(ModularGui gui) {
        return new GuiTexture(gui, BCGuiTextures.themedGetter("background_dynamic")).dynamicTexture();
    }

    @Override
    public void buildGui(ModularGui gui, ContainerScreenAccess<ModularItemMenu> screenAccess) {
        menu = screenAccess.getMenu();
        GuiElement<?> root = gui.getRoot();

        //Setup Gui Bounds
        this.grid = menu.getGrid();
        this.playerInv = menu.inventory;
        int maxGridWidth = 226;
        int maxGridHeight = 145;
        int minXPadding = 30;
        int yPadding = 112;
        int cellSize = Math.min(Math.min(maxGridWidth / grid.getWidth(), maxGridHeight / grid.getHeight()), 16);
        int width = Math.max((11 * 18) + 6 + 14, (cellSize * grid.getWidth()) + minXPadding);
        int height = yPadding + (cellSize * grid.getHeight());
        grid.setCellSize(cellSize);
        gui.initStandardGui(width, height);

        GuiText title = TOOLKIT.createHeading(root, gui.getGuiTitle(), true);

        var playInv = GuiSlots.playerAllSlots(root, screenAccess, menu.main, menu.hotBar, menu.armor, menu.offhand);
        playInv.stream().forEach(e -> e.setSlotOverlay(this::renderSlotOverlay).setSlotTexture(slot -> BCGuiTextures.getThemed("slot")));
        Constraints.placeInside(playInv.container(), root, Constraints.LayoutPos.BOTTOM_CENTER, 0, -7);

        setupCurioSlots(root, screenAccess);

        gridRenderer = new ModuleGridRenderer(root, menu.getGrid(), playerInv);
        Constraints.placeOutside(gridRenderer, title, Constraints.LayoutPos.BOTTOM_CENTER, 0, 3);

        grid.setPosition((int) (gridRenderer.xMin() - root.xMin()), (int) (gridRenderer.yMin() - root.yMin()));
        grid.setOnGridChange(this::updateInfoPanel);

        infoPanel = InfoPanel.create(root)
                .setExpandedStateHolder(infoExpanded);

        //Setup top buttons
        ButtonRow leftButtons = ButtonRow.topLeftInside(root, Direction.RIGHT, 3, 3).setSpacing(1);
        leftButtons.addButton(e -> TOOLKIT.createThemedIconButton(e, "item_config")
                .onPress(() -> DraconicNetwork.sendOpenItemConfig(false))
                .setTooltip(TOOLKIT.translate("open_item_config.info"))
        );
        leftButtons.addButton(e -> TOOLKIT.createThemedIconButton(e, "hud_button")
                .onPress(() -> gui.mc().setScreen(new HudConfigGui.Screen()))
                .setTooltip(Component.translatable("hud.draconicevolution.open_hud_config"))
        );

        ButtonRow rightButtons = ButtonRow.topRightInside(root, Direction.DOWN, 3, 3).setSpacing(1);
        rightButtons.addButton(TOOLKIT::createThemeButton);
        rightButtons.addButton(e -> TOOLKIT.createInfoButton(e, infoPanel));
        rightButtons.addButton(e -> new SupportedModulesIcon(e, menu.getModuleHost()));

        updateInfoPanel();
    }

    private void setupCurioSlots(GuiElement<?> root, ContainerScreenAccess<ModularItemMenu> screenAccess) {
        if (!EquipmentManager.equipModLoaded() || menu.curios.slots().isEmpty()) return;
        GuiTexture background = new GuiTexture(root, BCGuiTextures.themedGetter("background_dynamic"))
                .dynamicTexture();
        GuiScrolling scroll = new GuiScrolling(root);
        Constraints.bind(scroll, background, 4);

        GuiSlots slots = new GuiSlots(scroll.getContentElement(), screenAccess, menu.curios, 1)
                .setSlotOverlay(this::renderSlotOverlay)
                .setSlotTexture(slot -> BCGuiTextures.getThemed("slot"))
                .setEmptyIconI(i -> RenderUtils.fromRawTexture(EquipmentManager.getIcons(menu.inventory.player).get(i)))
                .constrain(LEFT, match(scroll.getContentElement().get(LEFT)))
                .constrain(TOP, match(scroll.getContentElement().get(TOP)));

        background.constrain(WIDTH, relative(slots.get(WIDTH), () -> scroll.hiddenSize(Axis.Y) > 0 ? 12 : 8D));
        background.constrain(HEIGHT, dynamic(() -> Math.min(root.ySize(), slots.ySize() + 8)));
        background.constrain(RIGHT, relative(root.get(LEFT), -2));
        background.constrain(TOP, match(root.get(TOP)));

        GuiSlider bar = new GuiSlider(background, Axis.Y)
                .setEnabled(() -> scroll.hiddenSize(Axis.Y) > 0)
                .setSliderState(scroll.scrollState(Axis.Y))
                .setScrollableElement(slots)
                .constrain(TOP, relative(background.get(TOP), 4))
                .constrain(BOTTOM, relative(background.get(BOTTOM), -4))
                .constrain(WIDTH, literal(4))
                .constrain(LEFT, relative(slots.get(RIGHT), 1));
        bar.installSlider(new GuiRectangle(bar).fill(() -> darkMode ? 0x80FFFFFF : 0xBB000000))
                .bindSliderWidth()
                .bindSliderLength();
    }


    private void updateInfoPanel() {
        infoPanel.clear();

        TechLevel techLevel = menu.getModuleHost().getHostTechLevel();
        Component label = Component.literal(grid.getWidth() + "x" + grid.getHeight())
                .append(" ")
                .append(techLevel.getDisplayName().plainCopy().withStyle(techLevel.getTextColour()))
                .append(" ")
                .append(Component.translatable("gui.draconicevolution.modular_item.module_grid"));
        infoPanel.label(label);

        Map<Component, Component> nameStatMap = new LinkedHashMap<>();
        grid.getModuleHost().addInformation(nameStatMap, menu.getModuleContext());
        for (Component name : nameStatMap.keySet()) {
            infoPanel.labeledValue(name.copy().withStyle(GOLD), () -> nameStatMap.get(name).copy().withStyle(GRAY));
        }
    }

    private void renderSlotOverlay(Slot slot, Position pos, GuiRender render) {
        if (slot.hasItem() && slot.getItem().getCapability(DECapabilities.Host.ITEM) != null) {
            int y = slot.y;
            int x = slot.x;
            int light = 0xFFfbe555;
            int dark = 0xFFf45905;

            render.shadedRect(pos.x() - 1, pos.y() - 1, 18, 18, 1, dark, light, 0);

            if (slot.getItem() == menu.hostStack) {
                render.borderRect(pos.x(), pos.y(), 16, 16, 1, 0x50FF0000, 0xFFFF0000);
            }
        }
    }

    public static class Screen extends ModularGuiContainer<ModularItemMenu> {
        public Screen(ModularItemMenu menu, Inventory inv, Component title) {
            super(menu, inv, new ModularItemGui());
            getModularGui().setGuiTitle(title);
        }

        @Override
        public void renderFloatingItem(GuiRender render, ItemStack itemStack, int x, int y, String string) {
            ModularItemGui gui = (ModularItemGui) modularGui.getProvider();
            if (!gui.gridRenderer.renderStackOverride(render, itemStack, x, y, string)) {
                super.renderFloatingItem(render, itemStack, x, y, string);
            }
        }
    }
}
