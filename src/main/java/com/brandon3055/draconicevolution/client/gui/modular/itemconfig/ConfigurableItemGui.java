package com.brandon3055.draconicevolution.client.gui.modular.itemconfig;

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
import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.client.BCGuiTextures;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.HudConfigGui;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.ButtonRow;
import com.brandon3055.brandonscore.client.render.RenderUtils;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.client.keybinding.KeyBindings;
import com.brandon3055.draconicevolution.integration.equipment.EquipmentManager;
import com.brandon3055.draconicevolution.inventory.ConfigurableItemMenu;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static codechicken.lib.gui.modular.lib.geometry.Constraint.*;
import static codechicken.lib.gui.modular.lib.geometry.GeoParam.*;
import static com.brandon3055.brandonscore.BCConfig.darkMode;


/**
 * Created by brandon3055 on 19/4/20.
 */
public class ConfigurableItemGui extends ContainerGuiProvider<ConfigurableItemMenu> {
    public static final GuiToolkit TOOLKIT = new GuiToolkit("gui.draconicevolution.item_config");
    private static final int MINIMIZE_HEIGHT = 15;
    private static final int NORMAL_HEIGHT = 230;
    private static final int ADVANCED_HEIGHT = 97;
    private static List<PropertyContainer> keyBindCache = null;

    public boolean advancedUI = false;
    private boolean minimize = false;
    private boolean cancelAutoPos = false;
    private boolean closeOnRelease = false;
    private boolean bindReleased = false;
    private int holdTimer = 0;

    private double minimizeAnim = 0; //1 = Minimized
    private double positionAnim = 1; //1 = Center
    private double resizeAnim = 1; //1 = Full Size

    protected ModularGui gui;
    protected GuiTexture mainUI;
    protected GuiElement<?> root;
    protected UUID selectedItem = null;
    protected ConfigurableItemMenu menu;
    protected ContainerScreenAccess<ConfigurableItemMenu> screenAccess;
    protected List<PropertyContainer> propertyContainers = new ArrayList<>();
    protected GuiScrolling simpleUIScroll;
    protected GuiElement<?> deleteZone;
    protected PropertyData hoveredData = null;
    protected PropertyProvider hoveredProvider = null;
    protected List<UpdateAnim> updateAnimations = new ArrayList<>();


    @Override
    public void buildGui(ModularGui gui, ContainerScreenAccess<ConfigurableItemMenu> screenAccess) {
        this.gui = gui;
        this.screenAccess = screenAccess;
        gui.initFullscreenGui();
        menu = screenAccess.getMenu();
        root = gui.getRoot();

        //Setup main UI background
        mainUI = new GuiTexture(root, BCGuiTextures.themedGetter("background_dynamic"))
                .constrain(WIDTH, literal(218)) //(11 * 18) + 6 + 14
                .constrain(HEIGHT, dynamic(() -> MathHelper.interpolate(ADVANCED_HEIGHT, NORMAL_HEIGHT, resizeAnim)))
                .constrain(LEFT, midPoint(root.get(LEFT), root.get(RIGHT), 218D / -2D))
                .dynamicTexture();

        Supplier<Double> normalPos = () -> root.yCenter() - (NORMAL_HEIGHT / 2D);
        Supplier<Double> advancedPos = () -> root.yMax() - MathHelper.interpolate(ADVANCED_HEIGHT, MINIMIZE_HEIGHT, minimizeAnim);
        mainUI.constrain(TOP, dynamic(() -> MathHelper.interpolate(advancedPos.get(), normalPos.get(), positionAnim)));

        //Setup top buttons
        ButtonRow leftButtons = ButtonRow.topLeftInside(mainUI, Direction.RIGHT, 3, 3).setSpacing(1);
        leftButtons.addButton(e -> TOOLKIT.createThemedIconButton(e, "advanced")
                .onPress(() -> advancedUI = !advancedUI)
                .setTooltip(TOOLKIT.translate("toggle_advanced.info"))
        );
        leftButtons.addButton(e -> TOOLKIT.createThemedIconButton(e, "gear")
                .onPress(this::openOptionsDialog)
                .setEnabled(() -> advancedUI)
                .setTooltip(TOOLKIT.translate("options"))
        );
        leftButtons.addButton(e -> TOOLKIT.createThemedIconButton(e, "hud_button")
                .onPress(() -> gui.mc().setScreen(new HudConfigGui.Screen()))
                .setTooltip(Component.translatable("hud.draconicevolution.open_hud_config"))
        );

        ButtonRow rightButtons = ButtonRow.topRightInside(mainUI, Direction.LEFT, 3, 3).setSpacing(1);
        rightButtons.addButton(TOOLKIT::createThemeButton);
        rightButtons.addButton(e -> TOOLKIT.createResizeButton(e)
                .setEnabled(() -> advancedUI)
                .onPress(() -> minimize = !minimize)
                .setTooltip(TOOLKIT.translate("toggle_hidden.info"))
        );
        rightButtons.addButton(e -> TOOLKIT.createThemedIconButton(e, "grid_small")
                .onPress(this::openModulesGui)
                .setTooltip(TOOLKIT.translate("open_modules.info"))
        );

        GuiText title = TOOLKIT.createHeading(mainUI, gui.getGuiTitle(), true)
                .constrain(LEFT, relative(leftButtons.get(RIGHT), 1))
                .constrain(RIGHT, match(rightButtons.get(LEFT)));
        title.setTextSupplier(() -> getGuiTitle(menu, title));

        var playInv = GuiSlots.playerAllSlots(mainUI, screenAccess, menu.main, menu.hotBar, menu.armor, menu.offhand);
        playInv.stream().forEach(e -> e.setSlotOverlay(this::renderSlotOverlay).setSlotTexture(slot -> BCGuiTextures.getThemed("slot")));

        Constraints.placeInside(playInv.container(), mainUI, Constraints.LayoutPos.BOTTOM_CENTER, 0, -7);

        setupCurioSlots();
        setupSimpleUI(playInv.container());
        setupAdvancedUI();

        selectedItem = menu.getSelectedIdentity();
        menu.setSelectionListener(this::onItemSelected);
        menu.setOnInventoryChange(this::onInventoryUpdate);
        loadSelectedItemProperties();
        loadInterfaceState();

        gui.onTick(this::tick);
        gui.onClose(this::saveInterfaceState);
    }

    private void setupCurioSlots() {
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
        background.constrain(HEIGHT, dynamic(() -> Math.min(mainUI.ySize(), slots.ySize() + 8)));
        background.constrain(RIGHT, relative(mainUI.get(LEFT), -2));
        background.constrain(TOP, match(mainUI.get(TOP)));

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

    private void setupSimpleUI(GuiElement<?> playerInv) {
        GuiRectangle border = TOOLKIT.embossBorder(mainUI)
                .constrain(TOP, relative(mainUI.get(TOP), 15))
                .constrain(LEFT, relative(mainUI.get(LEFT), 15))
                .constrain(RIGHT, relative(mainUI.get(RIGHT), -15))
                .constrain(BOTTOM, relative(playerInv.get(TOP), -4));
        border.setEnabled(() -> border.ySize() > 20);

        simpleUIScroll = new GuiScrolling(border);
        Constraints.bind(simpleUIScroll, border, 2);
        simpleUIScroll.constrain(RIGHT, relative(border.get(RIGHT), () -> simpleUIScroll.hiddenSize(Axis.Y) > 0 ? -7D : -2D));
        //We dont need x-scrolling so we lock the right of the content element to the right oof its parent.
        //This makes constraining width of content items easier because we can just constrain them to the content element.
        simpleUIScroll.getContentElement().constrain(WIDTH, null);
        simpleUIScroll.getContentElement().constrain(RIGHT, match(simpleUIScroll.get(RIGHT)));

        GuiSlider bar = new GuiSlider(border, Axis.Y)
                .setEnabled(() -> simpleUIScroll.hiddenSize(Axis.Y) > 0)
                .setSliderState(simpleUIScroll.scrollState(Axis.Y))
                .setScrollableElement(simpleUIScroll)
                .constrain(TOP, match(simpleUIScroll.get(TOP)))
                .constrain(BOTTOM, match(simpleUIScroll.get(BOTTOM)))
                .constrain(WIDTH, literal(5))
                .constrain(LEFT, relative(simpleUIScroll.get(RIGHT), 0));
        bar.installSlider(new GuiRectangle(bar).fill(() -> darkMode ? 0x80FFFFFF : 0xBB000000))
                .bindSliderWidth()
                .bindSliderLength();

    }

    private void loadSelectedItemProperties() {
        GuiElement<?> content = simpleUIScroll.getContentElement();
        content.getChildren().forEach(content::removeChild);
        simpleUIScroll.scrollState(Axis.Y).setPos(0);

        PropertyProvider provider = menu.findProvider(selectedItem);
        if (provider != null) {
            double yOffset = 0;
            int index = 0;
            for (ConfigProperty property : provider.getProperties()) {
                PropertyData data = new PropertyData(provider, property, true);
                data.setChangeListener(data::sendToServer);
                PropertyElement propEle = new PropertyElement(content, data, this, index, false)
                        .constrain(LEFT, match(content.get(LEFT)))
                        .constrain(RIGHT, match(content.get(RIGHT)))
                        .constrain(TOP, relative(content.get(TOP), yOffset));
                yOffset += propEle.ySize();
                index++;
            }
        }
    }

    private void setupAdvancedUI() {
        deleteZone = TOOLKIT.createIconButton(root, 16, BCGuiTextures.getter("delete"), true)
                .setEnabled(() -> advancedUI && DEConfig.configUiEnableDeleteZone)
                .setTooltip(Component.translatable("gui.draconicevolution.item_config.delete_zone.info"));
        Constraints.placeInside(deleteZone, root, Constraints.LayoutPos.TOP_RIGHT);

        GuiButton addGroup = TOOLKIT.createIconButton(root, 16, BCGuiTextures.getter("new_group"), true)
                .setEnabled(() -> advancedUI && DEConfig.configUiEnableAddGroupButton)
                .setTooltip(Component.translatable("gui.draconicevolution.item_config.add_group.info"))
                .constrain(RIGHT, match(root.get(RIGHT)))
                .constrain(TOP, relative(root.get(TOP), () -> deleteZone.isEnabled() ? 17D : 0D))
                .onClick(() -> new PropertyContainer(root, this, true));

    }


    private void onItemSelected(boolean initialLoad) {
        if (selectedItem != menu.getSelectedIdentity() && !advancedUI) {
            selectedItem = menu.getSelectedIdentity();
            loadSelectedItemProperties();
        } else if (advancedUI && !initialLoad) {
            PropertyProvider provider = menu.findProvider(menu.getSelectedIdentity());
            if (provider == null || provider.getProperties().isEmpty()) {
                return;
            }

            GuiContextMenu ctxMenu = GuiContextMenu.tooltipStyleMenu(mainUI)
                    .actionOnClick();
            for (ConfigProperty property : provider.getProperties()) {
                ctxMenu.addOption(property::getDisplayName, () -> {
                    PropertyContainer container = new PropertyContainer(root, this, false);
                    container.addProperty(new PropertyData(provider, property, true));
                });
            }
            ctxMenu.setNormalizedPos(gui.computeMouseX(), gui.computeMouseY());
        }
        if (!initialLoad) {
            gui.mc().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1F));
        }
    }

    private void onInventoryUpdate() {

    }

    public void newContainer(PropertyContainer container) {
        if (!cancelAutoPos) {
            container.setPos((int) (gui.computeMouseX() - (container.xSize() - 5)), (int) gui.computeMouseY() - 5);
            container.startDragging();
        }
        if (!propertyContainers.contains(container)) {
            propertyContainers.add(container);
        }
    }


    private void tick() {
        if ((isNormalUI() ? 1 : 0) != resizeAnim || (advancedUI ? 0 : 1) != positionAnim || (isMinimized() ? 1 : 0) != minimizeAnim) {
            resizeAnim = MathHelper.clip(MathHelper.approachLinear(resizeAnim, (isNormalUI() ? 1 : 0), 0.15F), 0, 1);
            minimizeAnim = MathHelper.clip(MathHelper.approachLinear(minimizeAnim, (isMinimized() ? 1 : 0), 0.15F), 0, 1);
            positionAnim = MathHelper.clip(MathHelper.approachLinear(positionAnim, (advancedUI ? 0 : 1), 0.15F), 0, 1);
        }
        hoveredData = null;
        hoveredProvider = null;
        if (DEConfig.configUiEnableVisualization) {
            Slot hovered = menu.slots.stream()
                    .filter(slot -> ((ModularGuiContainer<?>) gui.getScreen()).isHovering(slot, gui.computeMouseX(), gui.computeMouseY()))
                    .findAny()
                    .orElse(null);
            if (hovered != null) {
                PropertyProvider provider = hovered.getItem().getCapability(DECapabilities.Properties.ITEM);
                if (provider != null) {
                    hoveredProvider = provider;
                }
            }
        }

        if (!bindReleased) {
            InputConstants.Key bind = KeyBindings.toolConfig.getKey();
            if (!InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), bind.getValue())) {
                if (closeOnRelease) {
                    gui.getScreen().onClose();
                } else {
                    bindReleased = true;
                }
            } else if (holdTimer > 10) {
                closeOnRelease = true;
            }
            holdTimer++;
        }
    }


    private boolean isNormalUI() {
        return !advancedUI;
    }

    private boolean isMinimized() {
        return advancedUI && minimize;
    }

    private Component getGuiTitle(ConfigurableItemMenu menu, GuiElement<?> element) {
        if (advancedUI || menu.getLastStack().isEmpty()) {
            return TOOLKIT.translate("name");
        } else {
            MutableComponent prefix = TOOLKIT.translate("configure").append(" ");
            Component name = menu.getLastStack().getHoverName();
            if (element.font().width(prefix) + element.font().width(name) > element.xSize()) {
                return name;
            }
            return prefix.append(name);
        }
    }

    private void renderSlotOverlay(Slot slot, Position pos, GuiRender render) {
        ItemStack stack = slot.getItem();
        PropertyProvider provider = stack.getCapability(DECapabilities.Properties.ITEM);
        if (!stack.isEmpty() && provider != null) {
            int light = 0xFFfbe555;
            int dark = 0xFFf45905;
            render.shadedRect(pos.x() - 1, pos.y() - 1, 18, 18, 1, dark, light, 0);
            if (!advancedUI && provider.getIdentity().equals(menu.getSelectedIdentity())) {
                render.rect(pos.x(), pos.y(), 16, 16, 0x80FF0000);
            } else if (DEConfig.configUiEnableVisualization && hoveredData != null) {
                ConfigProperty prop = hoveredData.getPropIfApplicable(provider);
                if (prop != null) {
                    render.rect(pos.x(), pos.y(), 16, 16, hoveredData.doesDataMatch(prop) ? 0x8000FF00 : 0x80ff9100);
                }
            }

            if (DEConfig.configUiEnableVisualization && !updateAnimations.isEmpty()) {
                updateAnimations.stream()
                        .filter(e -> e.data.getPropIfApplicable(provider) != null)
                        .forEach(e -> e.render(pos.x(), pos.y(), render));
            }

        } else {
//            render.rect(pos.x() - 1, pos.y() - 1, 18, 18, 0xB0000000);
            render.rect(pos.x() - 1, pos.y() - 1, 18, 18, darkMode ? 0xB0000000 : 0xA0FFFFFF);
        }
    }

    private void openOptionsDialog() {
        GuiContextMenu.tooltipStyleMenu(root)
                .addOption(() -> Component.translatable("gui.draconicevolution.item_config." + (DEConfig.configUiShowUnavailable ? "hide_unavailable" : "show_unavailable")),
                        () -> Collections.singletonList(Component.translatable("gui.draconicevolution.item_config." + (DEConfig.configUiShowUnavailable ? "hide_unavailable" : "show_unavailable") + ".info")),
                        () -> DEConfig.modifyClientProperty("showUnavailable", tag -> tag.setBoolean(!DEConfig.configUiShowUnavailable), "itemConfigGUI"))

                .addOption(() -> Component.translatable("gui.draconicevolution.item_config." + (DEConfig.configUiEnableSnapping ? "disable_snapping" : "enable_snapping")),
                        () -> Collections.singletonList(Component.translatable("gui.draconicevolution.item_config.disable_snapping.info")),
                        () -> DEConfig.modifyClientProperty("enableSnapping", tag -> tag.setBoolean(!DEConfig.configUiEnableSnapping), "itemConfigGUI"))

                .addOption(() -> Component.translatable("gui.draconicevolution.item_config." + (DEConfig.configUiEnableVisualization ? "disable_visualization" : "enable_visualization")),
                        () -> Collections.singletonList(Component.translatable("gui.draconicevolution.item_config.disable_visualization.info")),
                        () -> DEConfig.modifyClientProperty("enableVisualization", tag -> tag.setBoolean(!DEConfig.configUiEnableVisualization), "itemConfigGUI"))

                .addOption(() -> Component.translatable("gui.draconicevolution.item_config." + (DEConfig.configUiEnableAddGroupButton ? "hide_group_button" : "show_group_button")),
                        () -> DEConfig.modifyClientProperty("enableAddGroupButton", tag -> tag.setBoolean(!DEConfig.configUiEnableAddGroupButton), "itemConfigGUI"))

                .addOption(() -> Component.translatable("gui.draconicevolution.item_config." + (DEConfig.configUiEnableDeleteZone ? "hide_delete_zone" : "show_delete_zone")),
                        () -> DEConfig.modifyClientProperty("enableDeleteZone", tag -> tag.setBoolean(!DEConfig.configUiEnableDeleteZone), "itemConfigGUI"))

                .addOption(() -> Component.translatable("gui.draconicevolution.item_config." + (DEConfig.configUiEnableAdvancedXOver ? "disable_adv_xover" : "enable_adv_xover")),
                        () -> Collections.singletonList(Component.translatable("gui.draconicevolution.item_config." + (DEConfig.configUiShowUnavailable ? "disable_adv_xover" : "enable_adv_xover") + ".info")),
                        () -> DEConfig.modifyClientProperty("enableAdvancedXOver", tag -> tag.setBoolean(!DEConfig.configUiEnableAdvancedXOver), "itemConfigGUI"))

                .setCloseOnItemClicked(false)
                .setNormalizedPos(gui.computeMouseX(), gui.computeMouseY());
    }

    private void loadInterfaceState() {
        cancelAutoPos();
        CompoundTag nbt = ItemConfigDataHandler.retrieveData();
        advancedUI = nbt.getBoolean("advanced");
        minimize = nbt.getBoolean("minimize");
        propertyContainers.forEach(root::removeChild);
        propertyContainers.clear();
        nbt.getList("property_containers", 10)
                .stream()
                .map(e -> (CompoundTag) e)
                .map(e -> PropertyContainer.deserialize(this, root, e))
                .toList();
        resizeAnim = isNormalUI() ? 1 : 0;
        minimizeAnim = isMinimized() ? 1 : 0;
        positionAnim = advancedUI ? 0 : 1;
        propertyContainers.forEach(PropertyContainer::inventoryUpdate); //Just to be safe.
        resumeAutoPos();
    }

    protected void saveInterfaceState() {
        keyBindCache = null;
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("advanced", advancedUI);
        nbt.putBoolean("minimize", minimize);
        nbt.put("property_containers", propertyContainers
                .stream()
                .map(PropertyContainer::serialize)
                .collect(Collectors.toCollection(ListTag::new))
        );
        ItemConfigDataHandler.saveData(nbt);
    }

    private void openModulesGui() {
        gui.getScreen().onClose();
        DraconicNetwork.sendOpenModuleConfig();
    }

    private static Map<InputConstants.Key, Integer> MULTI_BIND_INDEX_MAP = new HashMap<>();
    public static void checkKeybinding(int keyCode, int scanCode) {
        if (Minecraft.getInstance().screen instanceof ConfigurableItemGui.Screen) {
            return;
        }
        InputConstants.Key input = InputConstants.getKey(keyCode, scanCode);
        if (keyBindCache == null) {
            ModularGui dummy = new ModularGui(e -> {});
            keyBindCache = new ArrayList<>();
            CompoundTag nbt = ItemConfigDataHandler.retrieveData();
            List<PropertyContainer> containers = nbt.getList("property_containers", 10)
                    .stream()
                    .map(e -> (CompoundTag) e)
                    .map(e -> PropertyContainer.deserialize(null, dummy.getRoot(), e))
                    .toList();
            containers.stream()
                    .filter(e -> !e.boundKey.isEmpty() && e.globalKeyBind && e.presetMode)
                    .forEach(e -> keyBindCache.add(e));

            keyBindCache.sort(Comparator.comparing(e -> e.modifier.ordinal()));
        }

        List<PropertyContainer> presets = keyBindCache.stream()
                .filter(container -> input.toString().equals(container.boundKey) && container.modifier.isActive(null))
                .toList();
        if (presets.isEmpty()) {
            return;
        }

        if (presets.size() == 1) {
            MULTI_BIND_INDEX_MAP.remove(input);
            presets.get(0).applyPreset();
        } else {
            int next = (MULTI_BIND_INDEX_MAP.getOrDefault(input, -1) + 1) % presets.size();
            presets.get(next).applyPreset();
            MULTI_BIND_INDEX_MAP.put(input, next);
        }
    }

    protected static class UpdateAnim {
        private float tick = 0;
        private PropertyData data;

        public UpdateAnim(PropertyData data) {
            this.data = data;
        }

        private void tick(float partialTicks) {
            tick += partialTicks;
            if (tick > 10) tick = 20;
        }

        private boolean isFinished() {
            return tick >= 10;
        }

        public void render(double x, double y, GuiRender render) {
            if (!isFinished()) {
                float offset = (tick / 10F) * 8;
                render.fill(x + offset, y + offset, x + 16 - offset, y + 16 - offset, 0x5000FFFF);
            }
        }
    }

    public void cancelAutoPos() {
        this.cancelAutoPos = true;
    }

    public void resumeAutoPos() {
        this.cancelAutoPos = false;
    }

    public static class Screen extends ModularGuiContainer<ConfigurableItemMenu> {
        public Screen(ConfigurableItemMenu menu, Inventory inv, Component title) {
            super(menu, inv, new ConfigurableItemGui());
            getModularGui().setGuiTitle(title);
        }

        @Override
        public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
            super.render(graphics, mouseX, mouseY, partialTicks);
            ((ConfigurableItemGui) modularGui.getProvider()).updateAnimations.removeIf(UpdateAnim::isFinished);
            ((ConfigurableItemGui) modularGui.getProvider()).updateAnimations.forEach(e -> e.tick(partialTicks));
        }
    }
}
