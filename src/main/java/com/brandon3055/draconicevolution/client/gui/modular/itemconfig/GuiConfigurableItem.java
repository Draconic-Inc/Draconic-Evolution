package com.brandon3055.draconicevolution.client.gui.modular.itemconfig;

import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.client.BCSprites;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElement;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElementManager;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
import com.brandon3055.brandonscore.client.gui.modulargui.ThemedElements;
import com.brandon3055.brandonscore.client.gui.modulargui.ThemedElements.ContentRect;
import com.brandon3055.brandonscore.client.gui.modulargui.ThemedElements.ScrollBar;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiButton;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiScrollElement;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.*;
import com.brandon3055.brandonscore.client.utils.GuiHelper;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.inventory.ContainerConfigurableItem;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.brandon3055.brandonscore.client.gui.GuiToolkit.LayoutPos.BOTTOM_CENTER;
import static com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiScrollElement.ListMode.VERT_LOCK_POS_WIDTH;
import static com.brandon3055.brandonscore.client.gui.modulargui.lib.GuiAlign.CENTER;
import static net.minecraft.util.text.TextFormatting.*;


/**
 * Created by brandon3055 on 19/4/20.
 */
public class GuiConfigurableItem extends ModularGuiContainer<ContainerConfigurableItem> {
    private static final int HIDDEN_SIZE = 15;
    private static final int EXPANDED_SIZE = 230;
    private static final int COLLAPSED_SIZE = 97;

    private UUID selectedItem = null;
    private static float hideAnim = 0; //Hidden = 1
    private static float rePosAnim = 1; //Center = 1
    private static float resizeAnim = 1; //Full size = 1
    private static boolean advancedUI = false;
    private static boolean hideUI = false;
    private GuiLabel title;
    private GuiElement<?> mainUI;
    private GuiElement<?> playerSlots;
    private GuiScrollElement simpleViewList;
    private GuiToolkit.InfoPanel infoPanel;
    protected GuiElement<?> deleteZone;
    protected GuiElement<?> advancedContainer;
    protected List<PropertyContainer> propertyContainers = new ArrayList<>();
    protected GuiToolkit<GuiConfigurableItem> toolkit;
    protected PropertyData hoveredData = null;
    protected List<UpdateAnim> updateAnimations = new ArrayList<>();

    public GuiConfigurableItem(ContainerConfigurableItem container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
        this.toolkit = new GuiToolkit<>(this, 0, 0); //This size is irrelevant
        container.setOnInventoryChange(this::onInventoryUpdate);
        container.setSelectionListener(this::onItemSelected);
        this.setExperimentalSlotOcclusion(true);
    }

    @Override
    protected void drawSlotOverlay(Slot slot, boolean occluded) {
        ItemStack stack = slot.getStack();
        if (!stack.isEmpty()) {
            stack.getCapability(DECapabilities.PROPERTY_PROVIDER_CAPABILITY).ifPresent(provider -> {
                int y = slot.yPos;
                int x = slot.xPos;
                int light = 0xFFfbe555;
                int dark = 0xFFf45905;

                IRenderTypeBuffer.Impl getter = minecraft.getRenderTypeBuffers().getBufferSource();
                setZLevel(mainUI.displayZLevel);
                GuiHelper.drawShadedRect(getter.getBuffer(GuiHelper.TRANS_TYPE), x - 1, y - 1, 18, 18, 1, 0, dark, light, GuiElement.midColour(light, dark), mainUI.getRenderZLevel());
                getter.finish();

                if (!advancedUI && provider.getProviderID().equals(selectedItem)) {
                    drawOverlay(x, y, 0x80FF0000, occluded);
                } else if (hoveredData != null) {
                    ConfigProperty prop = hoveredData.getPropIfApplicable(provider);
                    if (prop != null) {
                        drawOverlay(x, y, hoveredData.doesDataMatch(prop) ? 0x8000FF00 : 0x80ff9100, occluded);
                    }
                }
                if (!updateAnimations.isEmpty()) {
                    updateAnimations.stream()
                            .filter(e -> e.data.getPropIfApplicable(provider) != null)
                            .forEach(e -> e.render(x, y));
                }
            });
        }
    }

    private void drawOverlay(int x, int y, int colour, boolean occluded) {
        RenderSystem.colorMask(true, true, true, false);
        if (occluded) RenderSystem.enableDepthTest();
        else RenderSystem.disableDepthTest();
        GuiHelper.drawGradientRect(x, y, x + 16, y + 16, colour, colour, 1F, 300);
        RenderSystem.colorMask(true, true, true, true);
        if (!occluded) RenderSystem.enableDepthTest();
    }

    @Override
    public void addElements(GuiElementManager manager) {
        addAdvancedUIElements(manager);

        mainUI = GuiTexture.newDynamicTexture(() -> BCSprites.getThemed("background_dynamic"));
        mainUI.setSize((11 * 18) + 6 + 14, 230);
        manager.addChild(mainUI, 90, false);

        title = toolkit.createHeading("gui.draconicevolution.item_config.name", mainUI, false);
        title.setAlignment(CENTER);
        title.setSize(mainUI.xSize(), 8);
        title.setPos(mainUI.xPos(), mainUI.yPos() + 4);

        GuiButton themeButton = toolkit.createThemeButton(mainUI);
        themeButton.onReload(() -> themeButton.setPos(mainUI.maxXPos() - 15, mainUI.yPos() + 3));

        GuiButton hideButton = toolkit.createResizeButton(mainUI);
        hideButton.setEnabledCallback(() -> advancedUI);
        hideButton.onPressed(() -> hideUI = !hideUI);
        hideButton.setHoverText(I18n.format("gui.draconicevolution.item_config.toggle_hidden.info"));
        hideButton.onReload(() -> hideButton.setPos(themeButton.xPos() - 12, mainUI.yPos() + 3));

        GuiButton advancedButton = toolkit.createAdvancedButton(mainUI);
        advancedButton.onPressed(this::toggleAdvanced);
        advancedButton.setHoverText(I18n.format("gui.draconicevolution.item_config.toggle_advanced.info"));
        advancedButton.onReload(() -> advancedButton.setPos(mainUI.xPos() + 3, mainUI.yPos() + 3));

        playerSlots = toolkit.createPlayerSlots(mainUI, false, true, true);

        simpleViewList = createPropertyList();
        simpleViewList.setInsets(2, 2, 2, 2);
        simpleViewList.addBackgroundChild(new ContentRect(false, false).bindSize(simpleViewList, false));
        simpleViewList.setEnabledCallback(() -> simpleViewList.ySize() > 20);
        simpleViewList.setPos(mainUI.xPos() + 15, mainUI.yPos() + 15);
        simpleViewList.setMaxXPos(mainUI.maxXPos() - 15, true);
        simpleViewList.setInsetScrollBars(true);
        mainUI.addChild(simpleViewList);

        GuiLabel getStarted = new GuiLabel(I18n.format("gui.draconicevolution.item_config.select_item_to_get_started"));
        getStarted.onReload(e -> e.setPosAndSize(0, mainUI.yPos() - 20, width, 8));
        getStarted.setEnabledCallback(() -> (advancedUI && propertyContainers.isEmpty()) || (!advancedUI && simpleViewList.getScrollingElements().isEmpty()));
        mainUI.addChild(getStarted);

        mainUI.onReload(this::updateUIGeometry);
        selectedItem = container.getSelectedId();
        loadSelectedItemProperties();
        loadPropertyConfig();
    }

    private void addAdvancedUIElements(GuiElementManager manager) {
        advancedContainer = new GuiElement<>();
        advancedContainer.onReload(() -> advancedContainer.setPosAndSize(0, 0, width, height));
        manager.addChild(advancedContainer);

        deleteZone = new GuiTexture(16, 16, () -> BCSprites.get("delete"));
        deleteZone.setEnabledCallback(() -> advancedUI);
        deleteZone.setYPos(0).setXPosMod(() -> advancedContainer.maxXPos() - 16);
        deleteZone.setHoverText(I18n.format("gui.draconicevolution.item_config.delete_zone.info"));
        toolkit.addHoverHighlight(deleteZone, 0, 0, true);
        advancedContainer.addChild(deleteZone);

        GuiButton addGroup = toolkit.createIconButton(advancedContainer, 16, BCSprites.getter("new_group"));
        toolkit.addHoverHighlight(addGroup, 0, 0, true);
        addGroup.setEnabledCallback(() -> advancedUI);
        addGroup.setHoverText(I18n.format("gui.draconicevolution.item_config.add_group.info"));
        addGroup.onReload(e -> e.setMaxXPos(width, false).setYPos(deleteZone.maxYPos() + 1));
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

    protected static GuiScrollElement createPropertyList() {
        GuiScrollElement element = new GuiScrollElement();
        element.setListMode(VERT_LOCK_POS_WIDTH);
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

            GuiSelectDialog<ConfigProperty> dialog = GuiToolkit.createStandardDialog(mainUI,
                    I18n.format("gui.draconicevolution.item_config.click_and_drag_to_place"),
                    e -> e.getDisplayName().getFormattedText(),
                    provider.getProperties());


//            GuiSelectDialog<ConfigProperty> dialog = new GuiSelectDialog<>(mainUI);
//            dialog.setRendererBuilder(e -> {
//                GuiLabel label = new GuiLabel(e.getDisplayName().getFormattedText()).setYSize(10).setTextColour(GRAY, YELLOW);
//                toolkit.addHoverHighlight(label);
//                return label;
//            });
//            dialog.addItems(provider.getProperties());
//            int width = provider.getProperties().stream()
//                    .map(ConfigProperty::getDisplayName)
//                    .map(ITextComponent::getFormattedText)
//                    .mapToInt(e -> font.getStringWidth(e))
//                    .max().orElse(50) + 6;
//
            int x = (int) mainUI.getMouseX();
//
//            dialog.setInsets(13, 2, 2, 2);
//            dialog.setSize(MathHelper.clip(width, 100, 200), (provider.getProperties().size() * 10) + 15);
//            dialog.addBackGroundChild(new GuiBorderedRect().setSize(dialog).setYSize(12).setBorderColour(0xFF000000 | DARK_AQUA.getColor()).setFillColour(0xFF101010));
//            dialog.addBackGroundChild(new GuiBorderedRect().setSize(dialog).setBorderColour(0xFF000000 | DARK_AQUA.getColor()).setFillColour(0xFF101010));
//            dialog.addChild(new GuiLabel(I18n.format("gui.draconicevolution.item_config.click_and_drag_to_place")).setSize(dialog.xSize(), 12).setAlignment(CENTER).setTextColour(WHITE));
            dialog.setPos(x, height - dialog.ySize());
//            dialog.setNoScrollBar();
            dialog.normalizePosition();

            dialog.setSelectionListener(property -> {
                PropertyContainer newContainer = new PropertyContainer(this, false);
                advancedContainer.addChild(newContainer);
                newContainer.setMaxXPos((int) advancedContainer.getMouseX() + 5, false).setYPos((int) advancedContainer.getMouseY() - 5);
                newContainer.updatePosition();
                newContainer.addProperty(new PropertyData(provider, property, true));
                newContainer.startDragging();
                GuiButton.playGenericClick();
                newContainer.setCancelZone(dialog.getRect().intersection(mainUI.getRect()));
            });

            dialog.setBlockOutsideClicks(true);
            dialog.setCloseOnOutsideClick(true);
            dialog.show(290);
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
    }

    private void updateUIGeometry() {
        toolkit.centerX(mainUI, advancedContainer, 0);
        mainUI.setYSize((int) MathHelper.map(resizeAnim, 0, 1, COLLAPSED_SIZE, EXPANDED_SIZE));
        float centerYPos = (height / 2F) - (mainUI.ySize() / 2F);
        float bottomYPos = height - mainUI.ySize();
        float centerAnim = MathHelper.map(rePosAnim, 0, 1, bottomYPos, centerYPos);
        float actualPos = MathHelper.map(hideAnim, 0, 1, centerAnim, height - HIDDEN_SIZE);
        mainUI.setYPos((int) actualPos);
        toolkit.placeInside(playerSlots, mainUI, BOTTOM_CENTER, 0, -7);
        simpleViewList.setMaxYPos(playerSlots.yPos() - 5, true);
        simpleViewList.updateScrollElement();
        simpleViewList.resetScrollPositions();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if ((isFullSize() ? 1 : 0) != resizeAnim || (advancedUI ? 0 : 1) != rePosAnim || (isUIHidden() ? 1 : 0) != hideAnim) {
            resizeAnim = MathHelper.clip(MathHelper.approachLinear(resizeAnim, (isFullSize() ? 1 : 0), 0.15F * partialTicks), 0, 1);
            hideAnim = MathHelper.clip(MathHelper.approachLinear(hideAnim, (isUIHidden() ? 1 : 0), 0.15F * partialTicks), 0, 1);
            rePosAnim = MathHelper.clip(MathHelper.approachLinear(rePosAnim, (advancedUI ? 0 : 1), 0.15F * partialTicks), 0, 1);
            updateUIGeometry();
        }
        updateAnimations.removeIf(UpdateAnim::isFinished);
        updateAnimations.forEach(e -> e.tick(partialTicks));
        super.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public void tick() {
        hoveredData = null;
        super.tick();
    }

    @Override
    public void onClose() {
        savePropertyConfig();
        super.onClose();
    }

    private void loadPropertyConfig() {
        CompoundNBT nbt = ItemConfigDataHandler.retrieveData();
        advancedUI = nbt.getBoolean("advanced");
        hideUI = nbt.getBoolean("hidden");
        propertyContainers.forEach(advancedContainer::removeChild);
        propertyContainers.clear();
        propertyContainers.addAll(nbt.getList("property_containers", 10)
                .stream()
                .map(e -> (CompoundNBT) e)
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
        CompoundNBT nbt = new CompoundNBT();
        nbt.putBoolean("advanced", advancedUI);
        nbt.putBoolean("hidden", hideUI);
        nbt.put("property_containers", propertyContainers
                .stream()
                .map(PropertyContainer::serialize)
                .collect(Collectors.toCollection(ListNBT::new))
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
                GuiHelper.drawGradientRect(x + offset, y + offset, x + 16 - offset, y + 16 - offset, 0x8000FFFF, 0x8000FFFF, 1F, 300);
                RenderSystem.colorMask(true, true, true, true);
                RenderSystem.enableDepthTest();
            }
        }
    }
}
