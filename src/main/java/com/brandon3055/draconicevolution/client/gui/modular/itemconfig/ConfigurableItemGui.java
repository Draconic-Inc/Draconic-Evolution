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
import codechicken.lib.gui.modular.sprite.Material;
import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.client.BCGuiTextures;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.HudConfigGui;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.ButtonRow;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.client.keybinding.KeyBindings;
import com.brandon3055.draconicevolution.integration.equipment.EquipmentManager;
import com.brandon3055.draconicevolution.inventory.ConfigurableItemMenu;
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
import net.minecraftforge.common.util.LazyOptional;
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

        selectedItem = menu.getSelectedId();
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
                .setEmptyIconI(i -> Material.fromRawTexture(EquipmentManager.getIcons(menu.inventory.player).get(i)))
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

    // TODO ###################################################################################
    //  #######################################################################################
    //  DO THE THING WHERE IF MULTIPLE PRESETS HAVE THE SAME BINDING IT WILL CYCLE BETWEEN THEM!
    //  Oh yea and i can display the active preset name in chat!
    //  #######################################################################################
    //  #######################################################################################

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
        if (selectedItem != menu.getSelectedId() && !advancedUI) {
            selectedItem = menu.getSelectedId();
            loadSelectedItemProperties();
        } else if (advancedUI && !initialLoad) {
            PropertyProvider provider = menu.findProvider(menu.getSelectedId());
            if (provider == null || provider.getProperties().isEmpty()) {
                return;
            }

            GuiContextMenu ctxMenu = GuiContextMenu.tooltipStyleMenu(mainUI)
                    .actionOnClick();
            for (ConfigProperty property : provider.getProperties()) {
                ctxMenu.addOption(property::getDisplayName, () -> {
                    PropertyContainer container = new PropertyContainer(root, this, false);
                    container.addProperty(new PropertyData(provider, property, true));
//                    container.setCancelZone(ctxMenu);
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
                LazyOptional<PropertyProvider> optionalCap = hovered.getItem().getCapability(DECapabilities.PROPERTY_PROVIDER_CAPABILITY);
                optionalCap.ifPresent(e -> hoveredProvider = e);
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
        LazyOptional<PropertyProvider> opt = stack.getCapability(DECapabilities.PROPERTY_PROVIDER_CAPABILITY);
        if (!stack.isEmpty() && opt.isPresent()) {
            PropertyProvider provider = opt.orElseThrow(IllegalStateException::new);
            int light = 0xFFfbe555;
            int dark = 0xFFf45905;
            render.shadedRect(pos.x() - 1, pos.y() - 1, 18, 18, 1, dark, light, 0);
            if (!advancedUI && provider.getProviderID().equals(menu.getSelectedId())) {
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
//        updateUIGeometry();
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


    //    private final Inventory inventory;
//
//    private UUID selectedItem = null;
//    private static float hideAnim = 0; //Hidden = 1
//    private static float rePosAnim = 1; //Center = 1
//    private static float resizeAnim = 1; //Full size = 1
//    private static boolean advancedUI = false;
//    private static boolean hideUI = false;
//    private int holdTimer = 0;
//    private boolean closeOnRelease = false;
//    private boolean bindReleased = false;
//    private GuiLabel title;
//    private GuiButton toggleAdvanced;
//    private GuiElement<?> mainUI;
//    private GuiElement<?> playerSlots;
//    private GuiScrollElement simpleViewList;
//    private GuiToolkit.InfoPanel infoPanel;
//    protected GuiElement<?> deleteZone;
//    protected GuiElement<?> advancedContainer;
//    protected PropertyData hoveredData = null;
//    protected PropertyProvider hoveredProvider = null;
//    protected List<UpdateAnim> updateAnimations = new ArrayList<>();
//    protected GuiToolkit<GuiConfigurableItem> toolkit;
//    protected List<PropertyContainer> propertyContainers = new ArrayList<>();
//
//    public GuiConfigurableItem(ContainerConfigurableItem container, Inventory inv, Component titleIn) {
//        super(container, inv, titleIn);
//        this.inventory = inv;
//        this.toolkit = new GuiToolkit<>(this, 0, 0); //This size is irrelevant
//        container.setOnInventoryChange(this::onInventoryUpdate);
//        container.setSelectionListener(this::onItemSelected);
//        this.setExperimentalSlotOcclusion(true);
//    }
//
//    @Override
//    protected void drawSlotOverlay(Slot slot, boolean occluded) {
//        ItemStack stack = slot.getItem();
//        if (!stack.isEmpty()) {
//            stack.getCapability(DECapabilities.PROPERTY_PROVIDER_CAPABILITY).ifPresent(provider -> {
//                int y = slot.y;
//                int x = slot.x;
//                int light = 0xFFfbe555;
//                int dark = 0xFFf45905;
//
//                MultiBufferSource.BufferSource getter = RenderUtils.getGuiBuffers();
//                setZLevel(mainUI.displayZLevel);
//                GuiHelperOld.drawShadedRect(getter.getBuffer(GuiHelper.transColourType), x - 1, y - 1, 18, 18, 1, 0, dark, light, GuiElement.midColour(light, dark), mainUI.getRenderZLevel());
//
//                if (!advancedUI && provider.getProviderID().equals(selectedItem)) {
//                    GuiHelperOld.drawColouredRect(getter.getBuffer(GuiHelper.transColourType), x, y, 16, 16, 0x80FF0000, mainUI.displayZLevel);
//                } else if (DEConfig.configUiEnableVisualization && hoveredData != null) {
//                    ConfigProperty prop = hoveredData.getPropIfApplicable(provider);
//                    if (prop != null) {
//                        GuiHelperOld.drawColouredRect(getter.getBuffer(GuiHelper.transColourType), x, y, 16, 16, hoveredData.doesDataMatch(prop) ? 0x8000FF00 : 0x80ff9100, 0);
//                    }
//                }
//                getter.endBatch();
//
//                if (DEConfig.configUiEnableVisualization && !updateAnimations.isEmpty()) {
//                    updateAnimations.stream()
//                            .filter(e -> e.data.getPropIfApplicable(provider) != null)
//                            .forEach(e -> e.render(x, y));
//                }
//            });
//        }
//    }
//
////    private void drawOverlay(int x, int y, int colour, boolean occluded) {
////        occluded = true;
////        RenderSystem.colorMask(true, true, true, false);
////        if (occluded) RenderSystem.enableDepthTest();
////        else RenderSystem.disableDepthTest();
////        GuiHelperOld.drawGradientRect(x, y, x + 16, y + 16, colour, colour, 1F, 300);
////        RenderSystem.colorMask(true, true, true, true);
////        if (!occluded) RenderSystem.enableDepthTest();
////    }
//
//    @Override
//    public void addElements(GuiElementManager manager) {
//        addAdvancedUIElements(manager);

//

    //
//        playerSlots = toolkit.createPlayerSlots(mainUI, false, true, true);
//
//        GuiElement<?> equipModSlots = toolkit.createEquipModSlots(mainUI, inventory.player, true, e -> e.getCapability(DECapabilities.PROPERTY_PROVIDER_CAPABILITY).isPresent());
//        equipModSlots.setPos(mainUI.xPos() - 28, mainUI.yPos());
//
//        simpleViewList = createPropertyList();
//        simpleViewList.setInsets(2, 2, 2, 2);
//        simpleViewList.addBackgroundChild(new ContentRect(false, false).bindSize(simpleViewList, false));
//        simpleViewList.setEnabledCallback(() -> simpleViewList.ySize() > 20);
//        simpleViewList.setPos(mainUI.xPos() + 15, mainUI.yPos() + 15);
//        simpleViewList.setMaxXPos(mainUI.maxXPos() - 15, true);
//        simpleViewList.setInsetScrollBars(true);
//        mainUI.addChild(simpleViewList);
//
//        GuiLabel getStarted = new GuiLabel(I18n.get("gui.draconicevolution.item_config.select_item_to_get_started"));
//        getStarted.onReload(e -> e.setPosAndSize(0, mainUI.yPos() - 20, width, 8));
//        getStarted.setEnabledCallback(() -> (advancedUI && propertyContainers.isEmpty()) || (!advancedUI && simpleViewList.getScrollingElements().isEmpty()));
//        mainUI.addChild(getStarted);
//
//        GuiButton options = createOptionsButton();
//
//        GuiButton moduleConfig = toolkit.createThemedIconButton(mainUI, "grid_small");
//        moduleConfig.onReload(() -> moduleConfig.setPos(hideButton.isEnabled() ? hideButton.xPos() - 12 : themeButton.xPos() - 12, mainUI.yPos() + 3));
//        moduleConfig.setHoverText(I18n.get("gui.draconicevolution.item_config.open_modules.info"));
//        moduleConfig.onPressed(this::openModulesGui);
//
//        GuiButton hudConfig = toolkit.createIconButton(mainUI, 16, 9, 16, 8, BCGuiTextures.themedGetter("hud_button"));
//        hudConfig.onReload(e -> e.setPos((options.isEnabled() ? options : toggleAdvanced).maxXPos() + 1, moduleConfig.yPos() + 1));
//        hudConfig.setHoverText(I18n.get("hud.draconicevolution.open_hud_config"));
//        hudConfig.onPressed(() -> minecraft.setScreen(new HudConfigGui()));
//
//
//        mainUI.onReload(this::updateUIGeometry);
//        selectedItem = container.getSelectedId();
//        loadSelectedItemProperties();
//        loadPropertyConfig();
//    }
//
    private void openModulesGui() {
//        minecraft.player.closeContainer();
//        removed();
//        DraconicNetwork.sendOpenModuleConfig();
    }
//
//    private void addAdvancedUIElements(GuiElementManager manager) {
//        advancedContainer = new GuiElement<>();
//        advancedContainer.setEnabledCallback(() -> advancedUI || DEConfig.configUiEnableAdvancedXOver);
//        advancedContainer.onReload(() -> advancedContainer.setPosAndSize(0, 0, width, height));
//        manager.addChild(advancedContainer);
//
//        deleteZone = new GuiTexture(16, 16, () -> BCGuiTextures.get("delete"));
//        deleteZone.setEnabledCallback(() -> advancedUI && DEConfig.configUiEnableDeleteZone);
//        deleteZone.setYPos(0).setXPosMod(() -> advancedContainer.maxXPos() - 16);
//        deleteZone.setHoverText(I18n.get("gui.draconicevolution.item_config.delete_zone.info"));
//        GuiToolkit.addHoverHighlight(deleteZone, 0, 0, true);
//        advancedContainer.addChild(deleteZone);
//
//        GuiButton addGroup = toolkit.createIconButton(advancedContainer, 16, BCGuiTextures.getter("new_group"));
//        GuiToolkit.addHoverHighlight(addGroup, 0, 0, true);
//        addGroup.setEnabledCallback(() -> advancedUI && DEConfig.configUiEnableAddGroupButton);
//        addGroup.setHoverText(I18n.get("gui.draconicevolution.item_config.add_group.info"));
//        addGroup.onReload(e -> e.setMaxXPos(width, false).setYPos(deleteZone.isEnabled() ? deleteZone.maxYPos() + 1 : 0));
//        addGroup.onPressed(() -> {
//            PropertyContainer newGroup = new PropertyContainer(this, true);
//            propertyContainers.add(newGroup);
//            advancedContainer.addChild(newGroup);
//            newGroup.setMaxXPos((int) advancedContainer.getMouseX() + 5, false).setYPos((int) advancedContainer.getMouseY() - 5);
//            newGroup.updatePosition();
//            newGroup.startDragging();
//        });
//
////        advancedContainer.addChild(new ThemedElements.TestDialog());
//    }
//
//    private GuiButton createOptionsButton() {
//        List<Tripple<Supplier<String>, Supplier<String>, Runnable>> options = new ArrayList<>();
//        options.add(new Tripple<>(
//                () -> DEConfig.configUiShowUnavailable ? "hide_unavailable" : "show_unavailable",
//                () -> DEConfig.configUiShowUnavailable ? "hide_unavailable" : "show_unavailable",
//                () -> {
//                    DEConfig.modifyClientProperty("showUnavailable", tag -> tag.setBoolean(!DEConfig.configUiShowUnavailable), "itemConfigGUI");
//                    advancedContainer.reloadElement();
//                    advancedContainer.reloadElement(); //Avoids some annoying reload issues.
//                })
//        );
//        options.add(new Tripple<>(
//                () -> DEConfig.configUiEnableSnapping ? "disable_snapping" : "enable_snapping",
//                () -> "disable_snapping",
//                () -> DEConfig.modifyClientProperty("enableSnapping", tag -> tag.setBoolean(!DEConfig.configUiEnableSnapping), "itemConfigGUI"))
//        );
//        options.add(new Tripple<>(
//                () -> DEConfig.configUiEnableVisualization ? "disable_visualization" : "enable_visualization",
//                () -> "disable_visualization",
//                () -> DEConfig.modifyClientProperty("enableVisualization", tag -> tag.setBoolean(!DEConfig.configUiEnableVisualization), "itemConfigGUI"))
//        );
//        options.add(new Tripple<>(
//                () -> DEConfig.configUiEnableAddGroupButton ? "hide_group_button" : "show_group_button", null,
//                () -> DEConfig.modifyClientProperty("enableAddGroupButton", tag -> tag.setBoolean(!DEConfig.configUiEnableAddGroupButton), "itemConfigGUI"))
//        );
//        options.add(new Tripple<>(
//                () -> DEConfig.configUiEnableDeleteZone ? "hide_delete_zone" : "show_delete_zone", null,
//                () -> {
//                    DEConfig.modifyClientProperty("enableDeleteZone", tag -> tag.setBoolean(!DEConfig.configUiEnableDeleteZone), "itemConfigGUI");
//                    advancedContainer.reloadElement();
//                })
//        );
//        options.add(new Tripple<>(
//                () -> DEConfig.configUiEnableAdvancedXOver ? "disable_adv_xover" : "enable_adv_xover",
//                () -> DEConfig.configUiEnableAdvancedXOver ? "disable_adv_xover" : "enable_adv_xover",
//                () -> {
//                    DEConfig.modifyClientProperty("enableAdvancedXOver", tag -> tag.setBoolean(!DEConfig.configUiEnableAdvancedXOver), "itemConfigGUI");
//                    advancedContainer.reloadElement();
//                })
//        );
//
//        GuiButton optionsButton = toolkit.createThemedIconButton(mainUI, "gear");
//        optionsButton.setPos(toggleAdvanced.maxXPos(), toggleAdvanced.yPos());
//        optionsButton.setHoverText(I18n.get("gui.draconicevolution.item_config.options"));
//        optionsButton.setEnabledCallback(() -> advancedUI);
//        optionsButton.onPressed(() -> {
//            StandardDialog<Tripple<Supplier<String>, Supplier<String>, Runnable>> dialog = new StandardDialog<>(advancedContainer);
//            dialog.setHeading(I18n.get("gui.draconicevolution.item_config.options"));
//            dialog.setDefaultRenderer(e -> I18n.get("gui.draconicevolution.item_config." + e.getA().get()));
//            dialog.setToolTipHandler((key, element) -> {
//                if (key.getB() != null) {
//                    element.setHoverText(e -> I18n.get("gui.draconicevolution.item_config." + key.getB().get() + ".info")).setHoverTextDelay(20);
//                }
//            });
//            dialog.setSelectionListener(e -> e.getC().run());
//            dialog.addItems(options);
//            dialog.setPos(optionsButton.maxXPos() + 2, optionsButton.yPos() - 2);
//            dialog.show();
//            dialog.normalizePosition();
//        });
//        return optionsButton;
//    }
//
//    protected static GuiScrollElement createPropertyList() {
//        GuiScrollElement element = new GuiScrollElement();
//        element.setListMode(GuiScrollElement.ListMode.VERT_LOCK_POS_WIDTH);
//        element.setStandardScrollBehavior();
//        element.setReloadOnUpdate(true);
//        element.getVerticalScrollBar().setBackgroundElement(new ScrollBar(true));
//        element.getVerticalScrollBar().setSliderElement(new ScrollBar(false));
//        element.getVerticalScrollBar().setXSize(6).setInsets(0, 0, 0, 0);
//        element.getVerticalScrollBar().updateElements();
//        return element;
//    }
//
//    private void loadSelectedItemProperties() {
//        simpleViewList.clearElements();
//        simpleViewList.resetScrollPositions();
//        PropertyProvider provider = container.findProvider(selectedItem);
//        if (provider != null) {
//            provider.getProperties().forEach(property -> {
//                PropertyData data = new PropertyData(provider, property, true);
//                data.setChangeListener(data::sendToServer);
//                simpleViewList.addElement(new PropertyElement(data, this, false));
//            });
//        }
//    }
//
//    private void onInventoryUpdate() {
//        simpleViewList.getScrollingElements().stream()
//                .filter(e -> e instanceof PropertyElement)
//                .map(e -> ((PropertyElement) e).data)
//                .forEach(data -> data.pullData(container, true));
//        propertyContainers.forEach(PropertyContainer::inventoryUpdate);
//    }
//
//    private void onItemSelected(boolean initialLoad) {
//        if (selectedItem != container.getSelectedId() && !advancedUI) {
//            selectedItem = container.getSelectedId();
//            loadSelectedItemProperties();
//        } else if (advancedUI && !initialLoad) {
//            PropertyProvider provider = container.findProvider(container.getSelectedId());
//            if (provider == null || provider.getProperties().isEmpty()) return;
//
//            StandardDialog<ConfigProperty> dialog = new StandardDialog<>(mainUI);
//            dialog.setHeading(I18n.get("gui.draconicevolution.item_config.click_and_drag_to_place"));
//            dialog.setDefaultRenderer(e -> e.getDisplayName().getString());
//            dialog.addItems(provider.getProperties());
//            int x = (int) mainUI.getMouseX();
//            dialog.setPos(x, height - dialog.ySize());
//            dialog.setSelectionListener(property -> {
//                PropertyContainer newContainer = new PropertyContainer(this, false);
//                advancedContainer.addChild(newContainer);
//                newContainer.setMaxXPos((int) advancedContainer.getMouseX() + 5, false).setYPos((int) advancedContainer.getMouseY() - 5);
//                newContainer.updatePosition();
//                newContainer.addProperty(new PropertyData(provider, property, true));
//                newContainer.startDragging();
//                newContainer.setCancelZone(dialog.getRect().intersection(mainUI.getRect()));
//            });
//
//            dialog.setBlockOutsideClicks(true);
//            dialog.setCloseOnOutsideClick(true);
//            dialog.show();
//        }
//        if (!initialLoad) {
//            GuiButton.playGenericClick();
//        }
//    }

    //    private void toggleAdvanced() {
//        advancedUI = !advancedUI;
//        if (!advancedUI) {
//            loadSelectedItemProperties();
//        }
//        mainUI.reloadElement();
//        advancedContainer.reloadElement();
//    }
//
//    private void updateUIGeometry() {
//        toolkit.centerX(mainUI, advancedContainer, 0);
//        mainUI.setYSize((int) MathHelper.map(resizeAnim, 0, 1, COLLAPSED_SIZE, EXPANDED_SIZE));
//        float centerYPos = (height / 2F) - (mainUI.ySize() / 2F);
//        float bottomYPos = height - mainUI.ySize();
//        float centerAnim = MathHelper.map(rePosAnim, 0, 1, bottomYPos, centerYPos);
//        float actualPos = MathHelper.map(hideAnim, 0, 1, centerAnim, height - HIDDEN_SIZE);
//        mainUI.setYPos((int) actualPos);
//        toolkit.placeInside(playerSlots, mainUI, GuiToolkit.LayoutPos.BOTTOM_CENTER, 0, -7);
//        simpleViewList.setMaxYPos(playerSlots.yPos() - 5, true);
//        simpleViewList.updateScrollElement();
//        simpleViewList.resetScrollPositions();
//
//    }
//
//    @Override
//    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
//        if ((isFullSize() ? 1 : 0) != resizeAnim || (advancedUI ? 0 : 1) != rePosAnim || (isUIHidden() ? 1 : 0) != hideAnim) {
//            resizeAnim = MathHelper.clip(MathHelper.approachLinear(resizeAnim, (isFullSize() ? 1 : 0), 0.15F * partialTicks), 0, 1);
//            hideAnim = MathHelper.clip(MathHelper.approachLinear(hideAnim, (isUIHidden() ? 1 : 0), 0.15F * partialTicks), 0, 1);
//            rePosAnim = MathHelper.clip(MathHelper.approachLinear(rePosAnim, (advancedUI ? 0 : 1), 0.15F * partialTicks), 0, 1);
//            updateUIGeometry();
//        }
//        updateAnimations.removeIf(UpdateAnim::isFinished);
//        updateAnimations.forEach(e -> e.tick(partialTicks));
//        super.render(poseStack, mouseX, mouseY, partialTicks);
//    }
//
//    @Override
//    public void containerTick() {
//        hoveredData = null;
//        hoveredProvider = null;
//        if (DEConfig.configUiEnableVisualization) {
//            Slot hovered = container.slots.stream()
//                    .filter(slot -> isHovering(slot, getMouseX(), getMouseY()))
//                    .findAny()
//                    .orElse(null);
//            if (hovered != null) {
//                LazyOptional<PropertyProvider> optionalCap = hovered.getItem().getCapability(DECapabilities.PROPERTY_PROVIDER_CAPABILITY);
//                optionalCap.ifPresent(e -> hoveredProvider = e);
//            }
//        }
//
//        if (!bindReleased) {
//            InputConstants.Key bind = KeyBindings.toolConfig.getKey();
//            if (!InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), bind.getValue())) {
//                if (closeOnRelease) {
//                    minecraft.player.closeContainer();
//                    removed();
//                } else {
//                    bindReleased = true;
//                }
//            } else if (holdTimer > 10) {
//                closeOnRelease = true;
//            }
//            holdTimer++;
//        }
//
//        super.containerTick();
//    }
//
//    @Override
//    public void removed() {
//        savePropertyConfig();
//        super.removed();
//    }
//
//    @Override
//    public boolean mouseClicked(double mouseX, double mouseY, int button) {
//        if (!bindReleased) {
//            closeOnRelease = true;
//        }
//        return super.mouseClicked(mouseX, mouseY, button);
//    }
//
//    @Override
//    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
//        if (!bindReleased) {
//            closeOnRelease = true;
//        }
//
//        if (super.keyPressed(keyCode, scanCode, modifiers)) {
//            return true;
//        }
//
//        InputConstants.Key input = InputConstants.getKey(keyCode, scanCode);
//        List<PropertyContainer> targets = propertyContainers.stream()
//                .filter(e -> e.isPreset)
//                .filter(e -> !e.boundKey.isEmpty())
//                .filter(e -> e.boundKey.equals(input.toString()))
//                .filter(e -> e.modifier.isActive(null))
//                .collect(Collectors.toList());
//        targets.forEach(e -> e.dataList.forEach(data -> {
//            data.sendToServer();
//            updateAnimations.add(new UpdateAnim(data));
//        }));
//        if (!targets.isEmpty()) {
//            GuiButton.playGenericClick();
//        }
//
//        if (KeyBindings.toolConfig.getKey().equals(input)) {
//            minecraft.player.closeContainer();
//            removed();
//            return true;
//        }
//
//
//        return false;
//    }
//

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

    //
//
//    private void loadPropertyConfig() {
//        CompoundTag nbt = ItemConfigDataHandler.retrieveData();
//        advancedUI = nbt.getBoolean("advanced");
//        hideUI = nbt.getBoolean("hidden");
//        propertyContainers.forEach(advancedContainer::removeChild);
//        propertyContainers.clear();
//        propertyContainers.addAll(nbt.getList("property_containers", 10)
//                .stream()
//                .map(e -> (CompoundTag) e)
//                .map(e -> PropertyContainer.deserialize(this, e))
//                .collect(Collectors.toList())
//        );
//        propertyContainers.forEach(advancedContainer::addChild);
//        resizeAnim = isFullSize() ? 1 : 0;
//        hideAnim = isUIHidden() ? 1 : 0;
//        rePosAnim = advancedUI ? 0 : 1;
//        updateUIGeometry();
//        propertyContainers.forEach(PropertyContainer::inventoryUpdate); //Just to be safe.
//    }
//
//    protected void savePropertyConfig() {
//        keyBindCache = null;
//        CompoundTag nbt = new CompoundTag();
//        nbt.putBoolean("advanced", advancedUI);
//        nbt.putBoolean("hidden", hideUI);
//        nbt.put("property_containers", propertyContainers
//                .stream()
//                .map(PropertyContainer::serialize)
//                .collect(Collectors.toCollection(ListTag::new))
//        );
//        ItemConfigDataHandler.saveData(nbt);
//    }
//
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
