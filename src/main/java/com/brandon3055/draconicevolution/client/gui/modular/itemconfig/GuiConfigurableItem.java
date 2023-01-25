package com.brandon3055.draconicevolution.client.gui.modular.itemconfig;

import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.api.render.GuiHelper;
import com.brandon3055.brandonscore.client.BCGuiSprites;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.HudConfigGui;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElement;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElementManager;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
import com.brandon3055.brandonscore.client.gui.modulargui.StandardDialog;
import com.brandon3055.brandonscore.client.gui.modulargui.ThemedElements.ContentRect;
import com.brandon3055.brandonscore.client.gui.modulargui.ThemedElements.ScrollBar;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiButton;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiScrollElement;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiLabel;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiTexture;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.GuiAlign;
import com.brandon3055.brandonscore.client.render.RenderUtils;
import com.brandon3055.brandonscore.client.utils.GuiHelperOld;
import com.brandon3055.brandonscore.lib.Tripple;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.client.keybinding.KeyBindings;
import com.brandon3055.draconicevolution.inventory.ContainerConfigurableItem;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;


/**
 * Created by brandon3055 on 19/4/20.
 */
public class GuiConfigurableItem extends ModularGuiContainer<ContainerConfigurableItem> {
    private static final int HIDDEN_SIZE = 15;
    private static final int EXPANDED_SIZE = 230;
    private static final int COLLAPSED_SIZE = 97;
    private final Inventory inventory;

    private UUID selectedItem = null;
    private static float hideAnim = 0; //Hidden = 1
    private static float rePosAnim = 1; //Center = 1
    private static float resizeAnim = 1; //Full size = 1
    private static boolean advancedUI = false;
    private static boolean hideUI = false;
    private int holdTimer = 0;
    private boolean closeOnRelease = false;
    private boolean bindReleased = false;
    private GuiLabel title;
    private GuiButton toggleAdvanced;
    private GuiElement<?> mainUI;
    private GuiElement<?> playerSlots;
    private GuiScrollElement simpleViewList;
    private GuiToolkit.InfoPanel infoPanel;
    protected GuiElement<?> deleteZone;
    protected GuiElement<?> advancedContainer;
    protected PropertyData hoveredData = null;
    protected PropertyProvider hoveredProvider = null;
    protected List<UpdateAnim> updateAnimations = new ArrayList<>();
    protected GuiToolkit<GuiConfigurableItem> toolkit;
    protected List<PropertyContainer> propertyContainers = new ArrayList<>();
    protected static List<PropertyContainer> keyBindCache = null;

    public GuiConfigurableItem(ContainerConfigurableItem container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
        this.inventory = inv;
        this.toolkit = new GuiToolkit<>(this, 0, 0); //This size is irrelevant
        container.setOnInventoryChange(this::onInventoryUpdate);
        container.setSelectionListener(this::onItemSelected);
        this.setExperimentalSlotOcclusion(true);
    }

    @Override
    protected void drawSlotOverlay(Slot slot, boolean occluded) {
        ItemStack stack = slot.getItem();
        if (!stack.isEmpty()) {
            stack.getCapability(DECapabilities.PROPERTY_PROVIDER_CAPABILITY).ifPresent(provider -> {
                int y = slot.y;
                int x = slot.x;
                int light = 0xFFfbe555;
                int dark = 0xFFf45905;

                MultiBufferSource.BufferSource getter = RenderUtils.getGuiBuffers();
                setZLevel(mainUI.displayZLevel);
                GuiHelperOld.drawShadedRect(getter.getBuffer(GuiHelper.transColourType), x - 1, y - 1, 18, 18, 1, 0, dark, light, GuiElement.midColour(light, dark), mainUI.getRenderZLevel());

                if (!advancedUI && provider.getProviderID().equals(selectedItem)) {
                    GuiHelperOld.drawColouredRect(getter.getBuffer(GuiHelper.transColourType), x, y, 16, 16, 0x80FF0000, mainUI.displayZLevel);
                } else if (DEConfig.configUiEnableVisualization && hoveredData != null) {
                    ConfigProperty prop = hoveredData.getPropIfApplicable(provider);
                    if (prop != null) {
                        GuiHelperOld.drawColouredRect(getter.getBuffer(GuiHelper.transColourType), x, y, 16, 16, hoveredData.doesDataMatch(prop) ? 0x8000FF00 : 0x80ff9100, 0);
                    }
                }
                getter.endBatch();

                if (DEConfig.configUiEnableVisualization && !updateAnimations.isEmpty()) {
                    updateAnimations.stream()
                            .filter(e -> e.data.getPropIfApplicable(provider) != null)
                            .forEach(e -> e.render(x, y));
                }
            });
        }
    }

//    private void drawOverlay(int x, int y, int colour, boolean occluded) {
//        occluded = true;
//        RenderSystem.colorMask(true, true, true, false);
//        if (occluded) RenderSystem.enableDepthTest();
//        else RenderSystem.disableDepthTest();
//        GuiHelperOld.drawGradientRect(x, y, x + 16, y + 16, colour, colour, 1F, 300);
//        RenderSystem.colorMask(true, true, true, true);
//        if (!occluded) RenderSystem.enableDepthTest();
//    }

    @Override
    public void addElements(GuiElementManager manager) {
        addAdvancedUIElements(manager);

        mainUI = GuiTexture.newDynamicTexture(() -> BCGuiSprites.getThemed("background_dynamic"));
        mainUI.setSize((11 * 18) + 6 + 14, 230);
        manager.addChild(mainUI, 90, false);

        GuiButton themeButton = toolkit.createThemeButton(mainUI);
        themeButton.onReload(() -> themeButton.setPos(mainUI.maxXPos() - 15, mainUI.yPos() + 3));

        title = toolkit.createHeading("", mainUI, false);
        title.setDisplaySupplier(() -> {
            if (advancedUI || container.getLastStack().isEmpty()) return I18n.get("gui.draconicevolution.item_config.name");
            else {
                String name = container.getLastStack().getHoverName().getString();
                String prefix = I18n.get("gui.draconicevolution.item_config.configure") + " ";
                if (font.width(prefix + name) > (themeButton.xPos() - toggleAdvanced.maxXPos()) - 22) {
                    return name;
                }
                return prefix + name;
            }
        });
        title.setAlignment(GuiAlign.CENTER);
        title.setSize(mainUI.xSize() - 48, 8);
        title.setPos(mainUI.xPos() + 24, mainUI.yPos() + 4);

        GuiButton hideButton = toolkit.createResizeButton(mainUI);
        hideButton.setEnabledCallback(() -> advancedUI);
        hideButton.onPressed(() -> hideUI = !hideUI);
        hideButton.setHoverText(I18n.get("gui.draconicevolution.item_config.toggle_hidden.info"));
        hideButton.onReload(() -> hideButton.setPos(themeButton.xPos() - 12, mainUI.yPos() + 3));

        toggleAdvanced = toolkit.createAdvancedButton(mainUI);
        toggleAdvanced.onPressed(this::toggleAdvanced);
        toggleAdvanced.setHoverText(I18n.get("gui.draconicevolution.item_config.toggle_advanced.info"));
        toggleAdvanced.onReload(() -> toggleAdvanced.setPos(mainUI.xPos() + 3, mainUI.yPos() + 3));

        playerSlots = toolkit.createPlayerSlots(mainUI, false, true, true);

        GuiElement<?> equipModSlots = toolkit.createEquipModSlots(mainUI, inventory.player, true, e -> e.getCapability(DECapabilities.PROPERTY_PROVIDER_CAPABILITY).isPresent());
        equipModSlots.setPos(mainUI.xPos() - 28, mainUI.yPos());

        simpleViewList = createPropertyList();
        simpleViewList.setInsets(2, 2, 2, 2);
        simpleViewList.addBackgroundChild(new ContentRect(false, false).bindSize(simpleViewList, false));
        simpleViewList.setEnabledCallback(() -> simpleViewList.ySize() > 20);
        simpleViewList.setPos(mainUI.xPos() + 15, mainUI.yPos() + 15);
        simpleViewList.setMaxXPos(mainUI.maxXPos() - 15, true);
        simpleViewList.setInsetScrollBars(true);
        mainUI.addChild(simpleViewList);

        GuiLabel getStarted = new GuiLabel(I18n.get("gui.draconicevolution.item_config.select_item_to_get_started"));
        getStarted.onReload(e -> e.setPosAndSize(0, mainUI.yPos() - 20, width, 8));
        getStarted.setEnabledCallback(() -> (advancedUI && propertyContainers.isEmpty()) || (!advancedUI && simpleViewList.getScrollingElements().isEmpty()));
        mainUI.addChild(getStarted);

        GuiButton options = createOptionsButton();

        GuiButton moduleConfig = toolkit.createThemedIconButton(mainUI, "grid_small");
        moduleConfig.onReload(() -> moduleConfig.setPos(hideButton.isEnabled() ? hideButton.xPos() - 12 : themeButton.xPos() - 12, mainUI.yPos() + 3));
        moduleConfig.setHoverText(I18n.get("gui.draconicevolution.item_config.open_modules.info"));
        moduleConfig.onPressed(this::openModulesGui);

        GuiButton hudConfig = toolkit.createIconButton(mainUI, 16, 9, 16, 8, BCGuiSprites.themedGetter("hud_button"));
        hudConfig.onReload(e -> e.setPos((options.isEnabled() ? options : toggleAdvanced).maxXPos() + 1, moduleConfig.yPos() + 1));
        hudConfig.setHoverText(I18n.get("hud.draconicevolution.open_hud_config"));
        hudConfig.onPressed(() -> minecraft.setScreen(new HudConfigGui()));


        mainUI.onReload(this::updateUIGeometry);
        selectedItem = container.getSelectedId();
        loadSelectedItemProperties();
        loadPropertyConfig();
    }

    private void openModulesGui() {
        minecraft.player.closeContainer();
        removed();
        DraconicNetwork.sendOpenModuleConfig();
    }

    private void addAdvancedUIElements(GuiElementManager manager) {
        advancedContainer = new GuiElement<>();
        advancedContainer.setEnabledCallback(() -> advancedUI || DEConfig.configUiEnableAdvancedXOver);
        advancedContainer.onReload(() -> advancedContainer.setPosAndSize(0, 0, width, height));
        manager.addChild(advancedContainer);

        deleteZone = new GuiTexture(16, 16, () -> BCGuiSprites.get("delete"));
        deleteZone.setEnabledCallback(() -> advancedUI && DEConfig.configUiEnableDeleteZone);
        deleteZone.setYPos(0).setXPosMod(() -> advancedContainer.maxXPos() - 16);
        deleteZone.setHoverText(I18n.get("gui.draconicevolution.item_config.delete_zone.info"));
        GuiToolkit.addHoverHighlight(deleteZone, 0, 0, true);
        advancedContainer.addChild(deleteZone);

        GuiButton addGroup = toolkit.createIconButton(advancedContainer, 16, BCGuiSprites.getter("new_group"));
        GuiToolkit.addHoverHighlight(addGroup, 0, 0, true);
        addGroup.setEnabledCallback(() -> advancedUI && DEConfig.configUiEnableAddGroupButton);
        addGroup.setHoverText(I18n.get("gui.draconicevolution.item_config.add_group.info"));
        addGroup.onReload(e -> e.setMaxXPos(width, false).setYPos(deleteZone.isEnabled() ? deleteZone.maxYPos() + 1 : 0));
        addGroup.onPressed(() -> {
            PropertyContainer newGroup = new PropertyContainer(this, true);
            propertyContainers.add(newGroup);
            advancedContainer.addChild(newGroup);
            newGroup.setMaxXPos((int) advancedContainer.getMouseX() + 5, false).setYPos((int) advancedContainer.getMouseY() - 5);
            newGroup.updatePosition();
            newGroup.startDragging();
        });

//        advancedContainer.addChild(new ThemedElements.TestDialog());
    }

    private GuiButton createOptionsButton() {
        List<Tripple<Supplier<String>, Supplier<String>, Runnable>> options = new ArrayList<>();
        options.add(new Tripple<>(
                () -> DEConfig.configUiShowUnavailable ? "hide_unavailable" : "show_unavailable",
                () -> DEConfig.configUiShowUnavailable ? "hide_unavailable" : "show_unavailable",
                () -> {
                    DEConfig.modifyClientProperty("showUnavailable", tag -> tag.setBoolean(!DEConfig.configUiShowUnavailable), "itemConfigGUI");
                    advancedContainer.reloadElement();
                    advancedContainer.reloadElement(); //Avoids some annoying reload issues.
                })
        );
        options.add(new Tripple<>(
                () -> DEConfig.configUiEnableSnapping ? "disable_snapping" : "enable_snapping",
                () -> "disable_snapping",
                () -> DEConfig.modifyClientProperty("enableSnapping", tag -> tag.setBoolean(!DEConfig.configUiEnableSnapping), "itemConfigGUI"))
        );
        options.add(new Tripple<>(
                () -> DEConfig.configUiEnableVisualization ? "disable_visualization" : "enable_visualization",
                () -> "disable_visualization",
                () -> DEConfig.modifyClientProperty("enableVisualization", tag -> tag.setBoolean(!DEConfig.configUiEnableVisualization), "itemConfigGUI"))
        );
        options.add(new Tripple<>(
                () -> DEConfig.configUiEnableAddGroupButton ? "hide_group_button" : "show_group_button", null,
                () -> DEConfig.modifyClientProperty("enableAddGroupButton", tag -> tag.setBoolean(!DEConfig.configUiEnableAddGroupButton), "itemConfigGUI"))
        );
        options.add(new Tripple<>(
                () -> DEConfig.configUiEnableDeleteZone ? "hide_delete_zone" : "show_delete_zone", null,
                () -> {
                    DEConfig.modifyClientProperty("enableDeleteZone", tag -> tag.setBoolean(!DEConfig.configUiEnableDeleteZone), "itemConfigGUI");
                    advancedContainer.reloadElement();
                })
        );
        options.add(new Tripple<>(
                () -> DEConfig.configUiEnableAdvancedXOver ? "disable_adv_xover" : "enable_adv_xover",
                () -> DEConfig.configUiEnableAdvancedXOver ? "disable_adv_xover" : "enable_adv_xover",
                () -> {
                    DEConfig.modifyClientProperty("enableAdvancedXOver", tag -> tag.setBoolean(!DEConfig.configUiEnableAdvancedXOver), "itemConfigGUI");
                    advancedContainer.reloadElement();
                })
        );

        GuiButton optionsButton = toolkit.createThemedIconButton(mainUI, "gear");
        optionsButton.setPos(toggleAdvanced.maxXPos(), toggleAdvanced.yPos());
        optionsButton.setHoverText(I18n.get("gui.draconicevolution.item_config.options"));
        optionsButton.setEnabledCallback(() -> advancedUI);
        optionsButton.onPressed(() -> {
            StandardDialog<Tripple<Supplier<String>, Supplier<String>, Runnable>> dialog = new StandardDialog<>(advancedContainer);
            dialog.setHeading(I18n.get("gui.draconicevolution.item_config.options"));
            dialog.setDefaultRenderer(e -> I18n.get("gui.draconicevolution.item_config." + e.getA().get()));
            dialog.setToolTipHandler((key, element) -> {
                if (key.getB() != null) {
                    element.setHoverText(e -> I18n.get("gui.draconicevolution.item_config." + key.getB().get() + ".info")).setHoverTextDelay(20);
                }
            });
            dialog.setSelectionListener(e -> e.getC().run());
            dialog.addItems(options);
            dialog.setPos(optionsButton.maxXPos() + 2, optionsButton.yPos() - 2);
            dialog.show();
            dialog.normalizePosition();
        });
        return optionsButton;
    }

    protected static GuiScrollElement createPropertyList() {
        GuiScrollElement element = new GuiScrollElement();
        element.setListMode(GuiScrollElement.ListMode.VERT_LOCK_POS_WIDTH);
        element.setStandardScrollBehavior();
        element.setReloadOnUpdate(true);
        element.getVerticalScrollBar().setBackgroundElement(new ScrollBar(true));
        element.getVerticalScrollBar().setSliderElement(new ScrollBar(false));
        element.getVerticalScrollBar().setXSize(6).setInsets(0, 0, 0, 0);
        element.getVerticalScrollBar().updateElements();
        return element;
    }

    private void loadSelectedItemProperties() {
        simpleViewList.clearElements();
        simpleViewList.resetScrollPositions();
        PropertyProvider provider = container.findProvider(selectedItem);
        if (provider != null) {
            provider.getProperties().forEach(property -> {
                PropertyData data = new PropertyData(provider, property, true);
                data.setChangeListener(data::sendToServer);
                simpleViewList.addElement(new PropertyElement(data, this, false));
            });
        }
    }

    private void onInventoryUpdate() {
        simpleViewList.getScrollingElements().stream()
                .filter(e -> e instanceof PropertyElement)
                .map(e -> ((PropertyElement) e).data)
                .forEach(data -> data.pullData(container, true));
        propertyContainers.forEach(PropertyContainer::inventoryUpdate);
    }

    private void onItemSelected(boolean initialLoad) {
        if (selectedItem != container.getSelectedId() && !advancedUI) {
            selectedItem = container.getSelectedId();
            loadSelectedItemProperties();
        } else if (advancedUI && !initialLoad) {
            PropertyProvider provider = container.findProvider(container.getSelectedId());
            if (provider == null || provider.getProperties().isEmpty()) return;

            StandardDialog<ConfigProperty> dialog = new StandardDialog<>(mainUI);
            dialog.setHeading(I18n.get("gui.draconicevolution.item_config.click_and_drag_to_place"));
            dialog.setDefaultRenderer(e -> e.getDisplayName().getString());
            dialog.addItems(provider.getProperties());
            int x = (int) mainUI.getMouseX();
            dialog.setPos(x, height - dialog.ySize());
            dialog.setSelectionListener(property -> {
                PropertyContainer newContainer = new PropertyContainer(this, false);
                advancedContainer.addChild(newContainer);
                newContainer.setMaxXPos((int) advancedContainer.getMouseX() + 5, false).setYPos((int) advancedContainer.getMouseY() - 5);
                newContainer.updatePosition();
                newContainer.addProperty(new PropertyData(provider, property, true));
                newContainer.startDragging();
                newContainer.setCancelZone(dialog.getRect().intersection(mainUI.getRect()));
            });

            dialog.setBlockOutsideClicks(true);
            dialog.setCloseOnOutsideClick(true);
            dialog.show();
        }
        if (!initialLoad) {
            GuiButton.playGenericClick();
        }
    }

    private boolean isFullSize() {
        return !advancedUI;
    }

    private boolean isUIHidden() {
        return advancedUI && hideUI;
    }

    private void toggleAdvanced() {
        advancedUI = !advancedUI;
        if (!advancedUI) {
            loadSelectedItemProperties();
        }
        mainUI.reloadElement();
        advancedContainer.reloadElement();
    }

    private void updateUIGeometry() {
        toolkit.centerX(mainUI, advancedContainer, 0);
        mainUI.setYSize((int) MathHelper.map(resizeAnim, 0, 1, COLLAPSED_SIZE, EXPANDED_SIZE));
        float centerYPos = (height / 2F) - (mainUI.ySize() / 2F);
        float bottomYPos = height - mainUI.ySize();
        float centerAnim = MathHelper.map(rePosAnim, 0, 1, bottomYPos, centerYPos);
        float actualPos = MathHelper.map(hideAnim, 0, 1, centerAnim, height - HIDDEN_SIZE);
        mainUI.setYPos((int) actualPos);
        toolkit.placeInside(playerSlots, mainUI, GuiToolkit.LayoutPos.BOTTOM_CENTER, 0, -7);
        simpleViewList.setMaxYPos(playerSlots.yPos() - 5, true);
        simpleViewList.updateScrollElement();
        simpleViewList.resetScrollPositions();

    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        if ((isFullSize() ? 1 : 0) != resizeAnim || (advancedUI ? 0 : 1) != rePosAnim || (isUIHidden() ? 1 : 0) != hideAnim) {
            resizeAnim = MathHelper.clip(MathHelper.approachLinear(resizeAnim, (isFullSize() ? 1 : 0), 0.15F * partialTicks), 0, 1);
            hideAnim = MathHelper.clip(MathHelper.approachLinear(hideAnim, (isUIHidden() ? 1 : 0), 0.15F * partialTicks), 0, 1);
            rePosAnim = MathHelper.clip(MathHelper.approachLinear(rePosAnim, (advancedUI ? 0 : 1), 0.15F * partialTicks), 0, 1);
            updateUIGeometry();
        }
        updateAnimations.removeIf(UpdateAnim::isFinished);
        updateAnimations.forEach(e -> e.tick(partialTicks));
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void containerTick() {
        hoveredData = null;
        hoveredProvider = null;
        if (DEConfig.configUiEnableVisualization) {
            Slot hovered = container.slots.stream()
                    .filter(slot -> isHovering(slot, getMouseX(), getMouseY()))
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
                    minecraft.player.closeContainer();
                    removed();
                } else {
                    bindReleased = true;
                }
            } else if (holdTimer > 10) {
                closeOnRelease = true;
            }
            holdTimer++;
        }

        super.containerTick();
    }

    @Override
    public void removed() {
        savePropertyConfig();
        super.removed();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!bindReleased) {
            closeOnRelease = true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!bindReleased) {
            closeOnRelease = true;
        }

        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        InputConstants.Key input = InputConstants.getKey(keyCode, scanCode);
        List<PropertyContainer> targets = propertyContainers.stream()
                .filter(e -> e.isPreset)
                .filter(e -> !e.boundKey.isEmpty())
                .filter(e -> e.boundKey.equals(input.toString()))
                .filter(e -> e.modifier.isActive(null))
                .collect(Collectors.toList());
        targets.forEach(e -> e.dataList.forEach(data -> {
            data.sendToServer();
            updateAnimations.add(new UpdateAnim(data));
        }));
        if (!targets.isEmpty()) {
            GuiButton.playGenericClick();
        }

        if (KeyBindings.toolConfig.getKey().equals(input)) {
            minecraft.player.closeContainer();
            removed();
            return true;
        }


        return false;
    }

    public static void checkKeybinding(int keyCode, int scanCode) {
        if (Minecraft.getInstance().screen instanceof GuiConfigurableItem) {
            return;
        }
        InputConstants.Key input = InputConstants.getKey(keyCode, scanCode);
        if (keyBindCache == null) {
            keyBindCache = new ArrayList<>();
            CompoundTag nbt = ItemConfigDataHandler.retrieveData();
            List<PropertyContainer> containers = nbt.getList("property_containers", 10)
                    .stream()
                    .map(e -> (CompoundTag) e)
                    .map(e -> PropertyContainer.deserialize(null, e))
                    .toList();
            containers.stream()
                    .filter(e -> !e.boundKey.isEmpty() && e.globalKeyBind && e.isPreset)
                    .forEach(e -> keyBindCache.add(e));

            keyBindCache.sort(Comparator.comparing(e -> e.modifier.ordinal()));
        }

        for (PropertyContainer container : keyBindCache) {
            if (input.toString().equals(container.boundKey) && container.modifier.isActive(null)) {
                container.dataList.forEach(PropertyData::sendToServer);
                GuiButton.playGenericClick();
                return;
            }
        }
    }


    private void loadPropertyConfig() {
        CompoundTag nbt = ItemConfigDataHandler.retrieveData();
        advancedUI = nbt.getBoolean("advanced");
        hideUI = nbt.getBoolean("hidden");
        propertyContainers.forEach(advancedContainer::removeChild);
        propertyContainers.clear();
        propertyContainers.addAll(nbt.getList("property_containers", 10)
                .stream()
                .map(e -> (CompoundTag) e)
                .map(e -> PropertyContainer.deserialize(this, e))
                .collect(Collectors.toList())
        );
        propertyContainers.forEach(advancedContainer::addChild);
        resizeAnim = isFullSize() ? 1 : 0;
        hideAnim = isUIHidden() ? 1 : 0;
        rePosAnim = advancedUI ? 0 : 1;
        updateUIGeometry();
        propertyContainers.forEach(PropertyContainer::inventoryUpdate); //Just to be safe.
    }

    protected void savePropertyConfig() {
        keyBindCache = null;
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("advanced", advancedUI);
        nbt.putBoolean("hidden", hideUI);
        nbt.put("property_containers", propertyContainers
                .stream()
                .map(PropertyContainer::serialize)
                .collect(Collectors.toCollection(ListTag::new))
        );
        ItemConfigDataHandler.saveData(nbt);
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

        public void render(int x, int y) {
            if (!isFinished()) {
                float offset = (tick / 10F) * 8;
                RenderSystem.colorMask(true, true, true, false);
                RenderSystem.disableDepthTest();
                MultiBufferSource.BufferSource source = RenderUtils.getGuiBuffers();
                PoseStack poseStack = new PoseStack();
                poseStack.translate(0, 0, 300);
                GuiHelper.drawGradientRect(source, poseStack, x + offset, y + offset, x + 16 - offset, y + 16 - offset, 0x8000FFFF, 0x8000FFFF);
                RenderSystem.colorMask(true, true, true, true);
                RenderSystem.enableDepthTest();
                source.endBatch();
            }
        }
    }
}
