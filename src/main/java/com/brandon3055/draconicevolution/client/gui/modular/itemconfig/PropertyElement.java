package com.brandon3055.draconicevolution.client.gui.modular.itemconfig;

import com.brandon3055.brandonscore.client.BCSprites;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElement;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiButton;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiSlideControl;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiBorderedRect;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiLabel;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiSelectDialog;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.brandonscore.client.gui.modulargui.ThemedElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.resources.I18n;

import java.util.Collections;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.brandon3055.brandonscore.client.gui.GuiToolkit.LayoutPos.BOTTOM_CENTER;
import static com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiSlideControl.SliderRotation.VERTICAL;
import static net.minecraft.util.text.TextFormatting.*;

/**
 * Created by brandon3055 on 10/5/20.
 */
public class PropertyElement extends GuiElement<PropertyElement> {
    protected PropertyData data;
    private boolean advanced;
    private GuiConfigurableItem gui;
    private GuiLabel label;
    private GuiLabel valueLabel;
    private GuiButton decrement;
    private GuiButton increment;
    private GuiButton valueButton;
    private GuiButton globalButton;
    private GuiSlideControl slider;
    private Supplier<Boolean> enableToolTip = () -> true;
    private Supplier<Integer> opacitySupplier = () -> 0xFF000000;
    private int index = 0;
    protected GuiElement<?> dragZone;

    public PropertyElement(PropertyData data, GuiConfigurableItem gui, boolean advanced) {
        this.data = data;
        this.gui = gui;
        this.advanced = advanced;
        this.setHoverTextDelay(10);
        this.setYSize(22);
    }

    public void setEnableToolTip(Supplier<Boolean> enableToolTip) {
        this.enableToolTip = enableToolTip;
    }

    public void setOpacitySupplier(Supplier<Integer> opacitySupplier) {
        this.opacitySupplier = opacitySupplier;
    }

    @Override
    public void addChildElements() {
        label = addChild(new GuiLabel(data.displayName));
//        label.setMidTrim(true);
        label.setTextColour(GOLD);
        label.setShadow(false);
        label.setYSize(10).setPos(xPos() + 10, yPos());
        label.onReload(e -> e.setMaxXPos(maxXPos() - 10, true));
        label.setHoverTextDelay(10);
        label.setHoverText((e) -> enableToolTip.get() ? data.toolTip : Collections.emptyList());

        valueButton = addChild(new GuiButton());
        valueButton.setYSize(10).setYPos(10).setXSizeMod(this::xSize);
        valueButton.setInsets(0, 4, 0, 0);
        valueButton.setTextColour(DARK_AQUA, AQUA);
        valueButton.setEnabled(data.type == ConfigProperty.Type.BOOLEAN || data.type == ConfigProperty.Type.ENUM);
        valueButton.onPressed(this::valueClicked);
        valueButton.setClickEnabled(false);

        decrement = gui.toolkit.createIconButton(this, 10, 12, "dark/arrow_left");
        increment = gui.toolkit.createIconButton(this, 10, 12, "dark/arrow_right");
        decrement.onReload(() -> decrement.setPos(xPos() + 1, yPos() + 10));
        increment.onReload(() -> increment.setMaxXPos(maxXPos() - 1, false).setYPos(yPos() + 10));
        decrement.setEnabled(data.type != ConfigProperty.Type.BOOLEAN);
        increment.setEnabled(data.type != ConfigProperty.Type.BOOLEAN);
        increment.onPressed(() -> data.increment(1));
        decrement.onPressed(() -> data.increment(-1));

        slider = addChild(new GuiSlideControl());
        slider.setBackgroundElement(new SliderBackground());
        slider.onReload(() -> slider.setYSize(10).setYPos(yPos() + 10).setXPos(decrement.maxXPos()).setMaxXPos(increment.xPos(), true).setSliderSize(4).updateElements());
        slider.setInsets(1, 1, 1, 1);
        slider.setEnabled(data.type == ConfigProperty.Type.DECIMAL || data.type == ConfigProperty.Type.INTEGER);
        slider.setDefaultSlider(0xFFE0E0E0, 0xFF90FFFF, 0xFFE0E0E0, 0xFF90FFFF);
        slider.addScrollCheck((e, e1, e2) -> false);
        slider.setRange(data.minValue, data.maxValue);
        slider.setInputListener(e -> data.updateNumberValue(e.getPosition(), !e.isDragging()));

        valueLabel = addChild(new GuiLabel());
        valueLabel.setDisplaySupplier(() -> data.displayValue);
        valueLabel.setYSize(10).setYPos(10).setXSizeMod(this::xSize);
        valueLabel.setTextColour(DARK_AQUA, AQUA);
        valueLabel.setMidTrim(true);

        if (advanced && data.propUniqueName == null) {
            globalButton = gui.toolkit.createIconButton(this, 8, 8, () -> BCSprites.get(data.isGlobal ? "dark/global_icon" : "dark/global_icon_inactive"));
            globalButton.setHoverText(I18n.format("gui.draconicevolution.item_config.global.info"));
            globalButton.addChild(new GuiBorderedRect().setShadeColours(0, 0xFF409040, 0xFFBBFFBB).setRelPos(globalButton, -1, -1).setSize(10, 10).setEnabledCallback(() -> data.isGlobal));
            globalButton.onReload(e -> e.setPos(xPos() + 1, yPos() + 1));
            globalButton.onPressed(() -> data.toggleGlobal());
        }

        detectValueChanges();
    }

    @Override
    public void reloadElement() {
        super.reloadElement();
        GuiElement<?> parent = getParent();
        if (parent != null) {
            if (parent.getParent() instanceof PropertyContainer && ((PropertyContainer) parent.getParent()).dataList.contains(data)) {
                index = ((PropertyContainer) parent.getParent()).dataList.indexOf(data);
            } else if (parent.getChildElements().contains(this)) {
                index = parent.getChildElements().indexOf(this);
            }
        }
    }

    private void valueClicked() {
        if (data.type == ConfigProperty.Type.BOOLEAN) {
            data.toggleBooleanValue();
            GuiButton.playGenericClick();
        } else if (data.type == ConfigProperty.Type.ENUM && data.enumValueOptions.size() > 1) {
            GuiSelectDialog<Integer> dialog = new GuiSelectDialog<>(this);
            dialog.setRendererBuilder(e -> {
                GuiLabel label = new GuiLabel(data.getEnumDisplayName(e)).setYSize(10).setTextColour(DARK_AQUA, AQUA);
                GuiToolkit.addHoverHighlight(label, 16, 0);
                return label;
            });
            dialog.addItem(data.enumValueIndex);
            dialog.addItems(data.enumValueOptions.stream().filter(e -> e != data.enumValueIndex).collect(Collectors.toList()));
            dialog.setInsets(1, 1, 1, 1);
            dialog.setSize(increment.xPos() - decrement.maxXPos(), Math.min(120, (data.enumValueOptions.size() * 10) + 3));

            dialog.getScrollElement().setVerticalScrollBar(new GuiSlideControl(VERTICAL)
                    .setPos(dialog.maxXPos() - 7, dialog.yPos() + 1)
                    .setSize(7, dialog.ySize() - 2)
                    .setInsets(0, 0, 0, 0)
                    .setParentScroll(true)
                    .setBackgroundElement(new GuiBorderedRect().setFillColours(mixColours(ThemedElements.getBgFill(), 0xE0101010, true), mixColours(ThemedElements.getBgFill(), 0xB0101010, true)).setBorderColour(0))
                    .setSliderElement(new ThemedElements.ScrollBar(false)));

            dialog.addBackGroundChild(new GuiBorderedRect().setSize(dialog).setBorderColour(0xFF000000 | DARK_AQUA.getColor()).setFillColour(0xFF101010));
            gui.toolkit.placeOutside(dialog, this, BOTTOM_CENTER, 0, -13);
            dialog.setBlockOutsideClicks(true);
            dialog.normalizePosition();
            dialog.setSelectionListener(newIndex -> {
                data.updateEnumValue(newIndex);
                GuiButton.playGenericClick();
            });
            dialog.setCloseOnSelection(true);
            dialog.show();
        }
    }

    @Override
    public void renderElement(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        IRenderTypeBuffer.Impl getter = minecraft.getRenderTypeBuffers().getBufferSource();
        drawColouredRect(getter, xPos(), yPos(), xSize(), ySize(), (index % 2 == 0 ? 0x202020 : 0x101010) | opacitySupplier.get());

        if (advanced && gui.hoveredProvider != null && gui.hoveredProvider.getProviderName().equals(data.providerName)) {
            if (data.isGlobal) {
                drawBorderedRect(getter, xPos(), yPos(), xSize(), ySize(), 1, 0, 0x80ffff00);
            } else if (gui.hoveredProvider.getProviderID().equals(data.providerID)) {
                drawBorderedRect(getter, xPos(), yPos(), xSize(), ySize(), 1, 0, 0x8000ff00);
            }
        }

        getter.finish();
        super.renderElement(minecraft, mouseX, mouseY, partialTicks);
        if (!data.isPropertyAvailable() && !data.isGlobal) {
            zOffset += 10;
            drawColouredRect(getter, xPos(), yPos(), xSize(), ySize(), 0x80FF8080);
            zOffset -= 10;
            getter.finish();
        }
    }

    @Override
    public boolean renderOverlayLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        if (isMouseOver(mouseX, mouseY) && !data.isProviderAvailable && !data.isGlobal) {
            drawHoveringText(Collections.singletonList(I18n.format("gui.draconicevolution.item_config.provider_unavailable")), mouseX, mouseY, fontRenderer, screenWidth, screenHeight);
            return true;
        }
        return super.renderOverlayLayer(minecraft, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!data.isProviderAvailable && !data.isGlobal && (globalButton == null || !globalButton.isMouseOver(mouseX, mouseY)) && (dragZone == null || !dragZone.isMouseOver(mouseX, mouseY))) {
            return false;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void detectValueChanges() {
        if (slider.isEnabled()) {
            slider.setRange(data.minValue, data.maxValue);
            if (!slider.isDragging()) {
                if (data.type == ConfigProperty.Type.DECIMAL && slider.getPosition() != data.decimalValue) {
                    slider.updatePos(data.decimalValue);
                } else if (data.type == ConfigProperty.Type.INTEGER && slider.getPosition() != data.integerValue) {
                    slider.updatePos(data.integerValue);
                }
            }
        }
    }

    @Override
    public boolean onUpdate() {
        detectValueChanges();
        if (advanced && label.isMouseOver(getMouseX(), getMouseY())) {
            gui.hoveredData = data;
        }
        return super.onUpdate();
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
