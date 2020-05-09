package com.brandon3055.draconicevolution.client.gui.modular;

import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.client.BCSprites;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElement;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElementManager;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiButton;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiScrollElement;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiSlideControl;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiLabel;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiTexture;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.GuiAlign;
import com.brandon3055.brandonscore.client.utils.GuiHelper;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import com.brandon3055.draconicevolution.api.config.*;
import com.brandon3055.draconicevolution.api.config.ConfigProperty.BooleanFormatter;
import com.brandon3055.draconicevolution.api.config.ConfigProperty.DecimalFormatter;
import com.brandon3055.draconicevolution.api.config.ConfigProperty.IntegerFormatter;
import com.brandon3055.draconicevolution.api.config.ConfigProperty.Type;
import com.brandon3055.draconicevolution.inventory.ContainerConfigurableItem;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import java.util.*;
import java.util.stream.Collectors;

import static com.brandon3055.brandonscore.client.gui.GuiToolkit.LayoutPos.BOTTOM_CENTER;
import static com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiScrollElement.ListMode.VERT_LOCK_POS_WIDTH;
import static com.brandon3055.draconicevolution.client.gui.modular.ThemedElements.*;
import static net.minecraft.util.text.TextFormatting.*;


/**
 * Created by brandon3055 on 19/4/20.
 */
public class GuiConfigurableItem extends ModularGuiContainer<ContainerConfigurableItem> {
    private static final int HIDDEN_SIZE = 15;
    private static final int EXPANDED_SIZE = 230;
    private static final int COLLAPSED_SIZE = 97;

    private UUID lastSelected = null;
    private float hideAnim = 0; //Hidden = 1
    private float rePosAnim = 1; //Center = 1
    private float resizeAnim = 1; //Full size = 1
    private boolean advancedUI = false;
    private boolean hideUI = false;
    private GuiLabel title;
    private GuiElement<?> mainUI;
    private GuiElement<?> baseScreen;
    private GuiElement<?> playerSlots;
    private GuiScrollElement simpleViewList;
    private GuiToolkit.InfoPanel infoPanel;
    private GuiToolkit<GuiConfigurableItem> toolkit;

    public GuiConfigurableItem(ContainerConfigurableItem container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
        this.toolkit = new GuiToolkit<>(this, 0, 0); //This size is irrelevant
    }

    @Override
    protected void drawSlotOverlay(Slot slot) {
        ItemStack stack = slot.getStack();
        if (!stack.isEmpty()) {
            stack.getCapability(DECapabilities.PROPERTY_PROVIDER_CAPABILITY).ifPresent(provider -> {
                int y = slot.yPos;
                int x = slot.xPos;
                int light = 0xFFfbe555;
                int dark = 0xFFf45905;

                IRenderTypeBuffer.Impl getter = minecraft.getRenderTypeBuffers().getBufferSource();
                GuiHelper.drawShadedRect(getter.getBuffer(GuiHelper.TRANS_TYPE), x - 1, y - 1, 18, 18, 1, 0, dark, light, GuiElement.midColour(light, dark), 0);
                getter.finish();

                if (provider.getProviderID().equals(container.selectedId)) {
                    RenderSystem.disableLighting();
                    RenderSystem.disableDepthTest();
                    fill(x, y, x + 16, y + 16, 0x80FF0000);
                    RenderSystem.enableLighting();
                    RenderSystem.enableDepthTest();
                }
            });
        }
    }

    @Override
    public void addElements(GuiElementManager manager) {
        baseScreen = new GuiElement<>();
        baseScreen.onReload(() -> baseScreen.setPosAndSize(0, 0, width, height));
        manager.addChild(baseScreen);

        mainUI = GuiTexture.newDynamicTexture(() -> BCSprites.getThemed("background_dynamic"));
        mainUI.setSize((11 * 18) + 6 + 14, 230);
        baseScreen.addBackGroundChild(mainUI);

        title = toolkit.createHeading("gui.draconicevolution.configure_items.name", mainUI, false);
        title.setAlignment(GuiAlign.CENTER);
        title.setSize(mainUI.xSize(), 8);
        title.setPos(mainUI.xPos(), mainUI.yPos() + 4);

        GuiButton themeButton = toolkit.createThemeButton(mainUI);
        themeButton.onReload(() -> themeButton.setPos(mainUI.maxXPos() - 15, mainUI.yPos() + 3));

        GuiButton hideButton = toolkit.createResizeButton(mainUI);
        hideButton.setEnabledCallback(() -> advancedUI);
        hideButton.onPressed(() -> hideUI = !hideUI);
        hideButton.setHoverText(I18n.format("gui.draconicevolution.item_config.toggle_hidden"));
        hideButton.onReload(() -> hideButton.setPos(themeButton.xPos() - 12, mainUI.yPos() + 3));

        GuiButton advancedButton = toolkit.createAdvancedButton(mainUI);
        advancedButton.onPressed(() -> advancedUI = !advancedUI);
        advancedButton.setHoverText(I18n.format("gui.draconicevolution.item_config.toggle_advanced"));
        advancedButton.onReload(() -> advancedButton.setPos(mainUI.xPos() + 3, mainUI.yPos() + 3));

        playerSlots = toolkit.createPlayerSlots(mainUI, false, true, true);

        simpleViewList = new GuiScrollElement();
        simpleViewList.setListMode(VERT_LOCK_POS_WIDTH);
        simpleViewList.setInsets(2, 2, 2, 2);
//        simpleViewList.setStandardScrollBehavior();
        simpleViewList.setReloadOnUpdate(true);
        simpleViewList.addBackgroundChild(new BorderedContainer().bindSize(simpleViewList, false));
        simpleViewList.setEnabledCallback(() -> simpleViewList.ySize() > 20);
        simpleViewList.setPos(mainUI.xPos() + 15, mainUI.yPos() + 15);
        simpleViewList.setMaxXPos(mainUI.maxXPos() - 15, true);
        simpleViewList.setInsetScrollBars(true);
        simpleViewList.setListSpacing(1);
        simpleViewList.getVerticalScrollBar().setXSize(8).setInsets(0, 0, 0, 0);
        simpleViewList.getVerticalScrollBar().setBackgroundElement(new ScrollBar(true));
        simpleViewList.getVerticalScrollBar().setSliderElement(new ScrollBar(false));
        mainUI.addChild(simpleViewList);


        //Called last because several elements depend on this initial call.
        mainUI.onReload(this::updateUIGeometry);
        loadSelectedItemProperties();
    }

    private void updateUIGeometry() {
        toolkit.centerX(mainUI, baseScreen, 0);
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

    private void loadSelectedItemProperties() {
        simpleViewList.clearElements();
        simpleViewList.resetScrollPositions();
        PropertyProvider provider = container.findProvider(container.selectedId);
        if (provider != null) {
            provider.getProperties().forEach(property -> simpleViewList.addElement(new PropertyElement(new PropertyData(provider.getProviderID(), property, true))));
        }
    }

    private void onInventoryUpdate() {
        simpleViewList.getScrollingElements().stream()
                .filter(e -> e instanceof PropertyElement)
                .map(e -> ((PropertyElement) e).data)
                .forEach(data -> data.pullData(true));
    }

    private boolean isFullSize() {
        return !advancedUI;
    }

    private boolean isUIHidden() {
        return advancedUI && hideUI;
    }

    @Override
    public void tick() {
        super.tick();
        if (lastSelected != container.selectedId) {
            lastSelected = container.selectedId;
            loadSelectedItemProperties();
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if ((isFullSize() ? 1 : 0) != resizeAnim || (advancedUI ? 0 : 1) != rePosAnim || (isUIHidden() ? 1 : 0) != hideAnim) {
            resizeAnim = MathHelper.clip(MathHelper.approachLinear(resizeAnim, (isFullSize() ? 1 : 0), 0.15F * partialTicks), 0, 1);
            hideAnim = MathHelper.clip(MathHelper.approachLinear(hideAnim, (isUIHidden() ? 1 : 0), 0.15F * partialTicks), 0, 1);
            rePosAnim = MathHelper.clip(MathHelper.approachLinear(rePosAnim, (advancedUI ? 0 : 1), 0.15F * partialTicks), 0, 1);
            updateUIGeometry();
        }
        super.render(mouseX, mouseY, partialTicks);
    }

    public class PropertyData {
        private final Type type;
        private final UUID providerID;
        private final String propertyName;
        private ITextComponent toolTip;
        private ITextComponent displayName;
        //Logic
        private boolean isPreset = false;
        private boolean isPropertyAvailable = false;
        private boolean isProviderAvailable = false;
        //Value
        private int integerValue = 0;
        private double decimalValue = 0;
        private String displayValue = "value";
        private boolean booleanValue = false;
        private double minValue = 0;
        private double maxValue = 0;
        //Formatters
        private BooleanFormatter booleanFormatter;
        private IntegerFormatter integerFormatter;
        private DecimalFormatter decimalFormatter;
        //Enum Stuff
        private String enumValueName;
        private List<String> enumValueNames;
        private Map<String, String> enumDisplayValues;

        public PropertyData(UUID providerID, ConfigProperty property, boolean pullValue) {
            this(providerID, property.getName(), property.getType());
            this.displayName = property.getDisplayName();
            this.toolTip = property.getToolTip();
            pullData(property, pullValue);
        }

        public PropertyData(UUID providerID, String propertyName, Type type) {
            this.providerID = providerID;
            this.propertyName = propertyName;
            this.type = type;
        }

        public void pullData(ConfigProperty property, boolean pullValue) {
            isPropertyAvailable = property != null;
            if (isPropertyAvailable) {
                displayName = property.getDisplayName();
                toolTip = property.getToolTip();
                displayValue = property.getDisplayValue();

                switch (property.getType()) {
                    case BOOLEAN: {
                        BooleanProperty prop = (BooleanProperty) property;
                        booleanFormatter = prop.getFormatter();
                        if (pullValue) {
                            booleanValue = prop.getValue();
                        }
                        break;
                    }
                    case INTEGER: {
                        IntegerProperty prop = (IntegerProperty) property;
                        integerFormatter = prop.getFormatter();
                        if (pullValue) {
                            integerValue = prop.getValue();
                        }
                        break;
                    }
                    case DECIMAL: {
                        DecimalProperty prop = (DecimalProperty) property;
                        decimalFormatter = prop.getFormatter();
                        if (pullValue) {
                            decimalValue = prop.getValue();
                        }
                        break;
                    }
                    case ENUM: {
                        EnumProperty<?> prop = (EnumProperty<?>) property;
                        enumValueNames = prop.getAllowedValues().stream().map(Enum::name).collect(Collectors.toList());
                        enumDisplayValues = prop.generateValueDisplayMap();
                        if (pullValue) {
                            enumValueName = prop.getValue().name();
                        }
                        break;
                    }
                }
            }
            updateDisplayValue();
        }

        public void pullData(boolean pullValue) {
            PropertyProvider provider = container.findProvider(providerID);
            isProviderAvailable = provider != null;
            if (isProviderAvailable) {
                pullData(provider.getProperty(propertyName), pullValue);
            }
        }

        public void updateDisplayValue() {
            switch (type) {
                case BOOLEAN:
                    if (booleanFormatter != null) {
                        displayValue = booleanFormatter.format(booleanValue);
                    }
                    break;
                case INTEGER:
                    if (integerFormatter != null) {
                        displayValue = integerFormatter.format(integerValue);
                    }
                    break;
                case DECIMAL:
                    if (decimalFormatter != null) {
                        displayValue = decimalFormatter.format(decimalValue);
                    }
                    break;
                case ENUM:
                    if (enumDisplayValues != null && enumValueName != null) {
                        displayValue = enumDisplayValues.getOrDefault(enumValueName, "[Error]");
                    }
                    break;
            }
        }
    }

    public class PropertyElement extends GuiElement<PropertyElement> {
        private PropertyData data;
        private GuiLabel label;
        private GuiLabel valueLabel;
        private GuiButton booleanControl;
        private GuiSlideControl slider;

        public PropertyElement(PropertyData data) {
            this.data = data;
            this.setHoverTextDelay(10);
            this.setYSize(22);
        }

        @Override
        public void addChildElements() {
            label = addChild(new GuiLabel(data.displayName.getFormattedText()));
            label.setTextColour(GOLD);
            label.setShadow(false);
            label.setInsets(0, 2, 0, 2);
            label.setYSize(10).setXSizeMod(this::xSize);
//            label.setAlignment(GuiAlign.LEFT);
            label.setHoverText((e) -> data.toolTip);

            booleanControl = addChild(new GuiButton());
//            booleanControl.setDisplaySupplier(() -> data.displayValue);
            booleanControl.setYSize(10).setYPos(10).setXSizeMod(this::xSize);
//            booleanControl.setAlignment(GuiAlign.LEFT);
            booleanControl.setInsets(0, 4, 0, 0);
            booleanControl.setTextColour(DARK_AQUA, AQUA);
            booleanControl.setEnabled(data.type == Type.BOOLEAN);

            GuiButton decrement = toolkit.createIconButton(this, 10, 12, "dark/arrow_left");
            GuiButton increment = toolkit.createIconButton(this, 10, 12, "dark/arrow_right");
            decrement.onReload(() -> decrement.setPos(xPos() + 1, yPos() + 10));
            increment.onReload(() -> increment.setMaxXPos(maxXPos() - 1, false).setYPos(yPos() + 10));
            decrement.setEnabled(data.type != Type.BOOLEAN);
            increment.setEnabled(data.type != Type.BOOLEAN);

            slider = addChild(new GuiSlideControl());
            slider.setBackgroundElement(new SliderBackground());
            slider.onReload(() -> slider.setYSize(10).setYPos(yPos() + 10).setXPos(decrement.maxXPos()).setMaxXPos(increment.xPos(), true).setSliderSize(4).updateElements());
            slider.setInsets(1, 1, 1, 1);
            slider.setScrollSpeed(-slider.getScrollSpeed());
            slider.setEnabled(data.type == Type.DECIMAL || data.type == Type.INTEGER);
//            slider.setDefaultSlider(0xFF90C0C0, 0xFF90FFFF, 0xFF90C0C0, 0xFF90FFFF);
            slider.setDefaultSlider(0xFFE0E0E0, 0xFF90FFFF, 0xFFE0E0E0, 0xFF90FFFF);


            valueLabel = addChild(new GuiLabel());
            valueLabel.setDisplaySupplier(() -> data.displayValue);
            valueLabel.setYSize(10).setYPos(10).setXSizeMod(this::xSize);
            valueLabel.setTextColour(DARK_AQUA, AQUA);

        }

        @Override
        public void renderElement(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
            IRenderTypeBuffer.Impl getter = minecraft.getRenderTypeBuffers().getBufferSource();
//            drawColouredRect(getter, xPos(), yPos(), xSize(), ySize(), darkMode ? 0xFF505050 : 0xFF707070);
//            drawColouredRect(getter, xPos(), yPos(), xSize(), ySize(), 0xFF000000);
            drawColouredRect(getter, xPos(), yPos(), xSize(), ySize(), 0xFF101010);
            getter.finish();
            super.renderElement(minecraft, mouseX, mouseY, partialTicks);
        }

        private class SliderBackground extends GuiElement<SliderBackground> {
            @Override
            public void renderElement(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
                IRenderTypeBuffer.Impl getter = minecraft.getRenderTypeBuffers().getBufferSource();
                if (isMouseOver(mouseX, mouseY) || slider.isDragging()) {
                    drawColouredRect(getter, xPos(), yPos(), xSize(), ySize(), 0x60475b6a);
                }
                drawColouredRect(getter, xPos(), yPos() + (ySize() / 2F) - 1, xSize(), 2, 0xFF808080);
                drawColouredRect(getter, xPos(), yPos(), 1, ySize(), 0xFF808080);
                drawColouredRect(getter, xPos() + xSize() - 1, yPos(), 1, ySize(), 0xFF808080);
                getter.finish();
                super.renderElement(minecraft, mouseX, mouseY, partialTicks);
            }
        }
    }
}
