package com.brandon3055.draconicevolution.client.gui.modular.itemconfig;

import codechicken.lib.gui.modular.elements.*;
import codechicken.lib.gui.modular.lib.BackgroundRender;
import codechicken.lib.gui.modular.lib.Constraints;
import codechicken.lib.gui.modular.lib.GuiRender;
import codechicken.lib.gui.modular.lib.SliderState;
import codechicken.lib.gui.modular.lib.geometry.Axis;
import codechicken.lib.gui.modular.lib.geometry.GuiParent;
import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.client.BCGuiTextures;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.function.Supplier;

import static codechicken.lib.gui.modular.lib.geometry.Constraint.*;
import static codechicken.lib.gui.modular.lib.geometry.GeoParam.*;
import static com.brandon3055.brandonscore.BCConfig.darkMode;
import static net.minecraft.ChatFormatting.AQUA;
import static net.minecraft.ChatFormatting.DARK_AQUA;

/**
 * Created by brandon3055 on 10/5/20.
 */
public class PropertyElement extends GuiElement<PropertyElement> {
    protected PropertyData data;
    private boolean advanced;
    private ConfigurableItemGui gui;
    private GuiText label;
    //    private GuiLabel valueLabel;
//    private GuiButton decrement;
//    private GuiButton increment;
//    private GuiButton valueButton;
//    private GuiButton globalButton;
//    private GuiSlideControl slider;
    private Supplier<Boolean> suppressToolTip = () -> false;
    private Supplier<Integer> opacitySupplier = () -> 0xFF000000;
    private int index = 0;
//    protected GuiElement<?> dragZone;

    public PropertyElement(GuiParent<?> parent, PropertyData data, ConfigurableItemGui gui, int index, boolean advanced) {
        super(parent);
        this.data = data;
        this.gui = gui;
        this.constrain(HEIGHT, literal(22));
        this.index = index;
        this.advanced = advanced;
        buildElement();
    }

    private void buildElement() {
        Constraints.bind(new GuiRectangle(this).fill(() -> (index % 2 == 0 ? (darkMode ? 0x101010 : 0x252525) : (darkMode ? 0x202020 : 0x404040)) | opacitySupplier.get()), this);

        label = new GuiText(this, data.displayName.copy().withStyle(ChatFormatting.GOLD))
                .setShadow(false)
                .setEnableToolTip(() -> !suppressToolTip.get())
                .setTooltip(() -> data.toolTip != null ? Collections.singletonList(data.toolTip) : Collections.emptyList())
                .constrain(HEIGHT, literal(10))
                .constrain(TOP, match(get(TOP)))
                .constrain(LEFT, relative(get(LEFT), 10))
                .constrain(RIGHT, relative(get(RIGHT), -10));

        GuiButton valueButton = new GuiButton(this)
                .setEnabled(() -> data.type == ConfigProperty.Type.BOOLEAN || data.type == ConfigProperty.Type.ENUM)
                .setPressSound(null)
                .onPress(this::valueClicked)
                .constrain(HEIGHT, literal(10))
                .constrain(LEFT, match(get(LEFT)))
                .constrain(RIGHT, match(get(RIGHT)))
                .constrain(TOP, relative(get(TOP), 10));

        GuiButton decrement = ConfigurableItemGui.TOOLKIT.createIconButton(this, 10, 12, "dark/arrow_left")
                .setEnabled(() -> data.type != ConfigProperty.Type.BOOLEAN)
                .onPress(() -> data.increment(-1));
        Constraints.placeInside(decrement, this, Constraints.LayoutPos.TOP_LEFT, 1, 10);

        GuiButton increment = ConfigurableItemGui.TOOLKIT.createIconButton(this, 10, 12, "dark/arrow_right")
                .setEnabled(() -> data.type != ConfigProperty.Type.BOOLEAN)
                .onPress(() -> data.increment(1));
        Constraints.placeInside(increment, this, Constraints.LayoutPos.TOP_RIGHT, -1, 10);

        SliderBackground slideBg = new SliderBackground(this)
                .setEnabled(() -> data.type == ConfigProperty.Type.DECIMAL || data.type == ConfigProperty.Type.INTEGER)
                .constrain(HEIGHT, literal(10))
                .constrain(LEFT, match(decrement.get(RIGHT)))
                .constrain(RIGHT, match(increment.get(LEFT)))
                .constrain(TOP, relative(get(TOP), 10));

        GuiSlider slider = new GuiSlider(slideBg, Axis.X)
                .setSliderState(new DataSlideState(data));
        slideBg.slider = slider;
        slider.getSlider().constrain(WIDTH, literal(6));
        Constraints.bind(slider, slideBg, 0, 1, 0, 1);
        Constraints.bind(new GuiRectangle(slider.getSlider()).fill(() -> slider.isMouseOver() || slider.isDragging() ? 0xFF90FFFF : 0xFFE0E0E0), slider.getSlider());

        GuiText valueLabel = new GuiText(this)
                .constrain(HEIGHT, literal(10))
                .constrain(LEFT, match(get(LEFT)))
                .constrain(RIGHT, match(get(RIGHT)))
                .constrain(TOP, relative(get(TOP), 10));
        valueLabel.setTextSupplier(() -> Component.literal(data.displayValue).withStyle(valueLabel.isMouseOver() ? AQUA : DARK_AQUA));

        if (advanced && data.propUniqueName == null) {
            GuiButton globalButton = ConfigurableItemGui.TOOLKIT.createIconButton(this, 8, 8, () -> BCGuiTextures.get(data.isGlobal ? "dark/global_icon" : "dark/global_icon_inactive"))
                    .setTooltip(Component.translatable("gui.draconicevolution.item_config.global.info"))
                    .onPress(() -> data.toggleGlobal());
            Constraints.placeInside(globalButton, this, Constraints.LayoutPos.TOP_LEFT, 1, 1);
            Constraints.bind(new GuiRectangle(globalButton).shadedRect(0xFF409040, 0xFFBBFFBB, 0).setEnabled(() -> data.isGlobal), globalButton, -1);
        }

        Constraints.bind(new GuiRectangle(this)
                .border(() -> data.isGlobal ? 0x80ffff00 : gui.hoveredProvider.getIdentity().equals(data.providerID) ? 0x8000ff00 : 0)
                .setEnabled(() -> advanced && gui.hoveredProvider != null && gui.hoveredProvider.getProviderName().equals(data.providerName)), this);

        Constraints.bind(new GuiRectangle(this)
                .fill(0x80FF8080)
                .setTooltip(Component.translatable("gui.draconicevolution.item_config.provider_unavailable"))
                .setEnabled(() -> !data.isPropertyAvailable() && !data.isGlobal), this);

//        detectValueChanges();
    }

    public void setSuppressToolTip(Supplier<Boolean> suppressToolTip) {
        this.suppressToolTip = suppressToolTip;
    }

    public void setOpacitySupplier(Supplier<Integer> opacitySupplier) {
        this.opacitySupplier = opacitySupplier;
    }

    @Override
    public void tick(double mouseX, double mouseY) {
        super.tick(mouseX, mouseY);
        if (advanced && label.isMouseOver()) {
            gui.hoveredData = data;
        }
    }

    //
//    @Override
//    public void reloadElement() {
//        super.reloadElement();
//        GuiElement<?> parent = getParent();
//        if (parent != null) {
//            if (parent.getParent() instanceof PropertyContainer && ((PropertyContainer) parent.getParent()).dataList.contains(data)) {
//                index = ((PropertyContainer) parent.getParent()).dataList.indexOf(data);
//            } else if (parent.getChildElements().contains(this)) {
//                index = parent.getChildElements().indexOf(this);
//            }
//        }
//    }
//
    private void valueClicked() {
        if (data.type == ConfigProperty.Type.BOOLEAN) {
            data.toggleBooleanValue();
            mc().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
        } else if (data.type == ConfigProperty.Type.ENUM && data.enumValueOptions.size() > 1) {
            //Cycle through options. [Temporary... Probably permanently temporary...]
            int i = data.enumValueOptions.indexOf(data.enumValueIndex);
            i++;
            if (i >= data.enumValueOptions.size()) {
                i = 0;
            }
            data.updateEnumValue(data.enumValueOptions.get(i));
            //TODO Enum Selection Dialog
//            GuiSelectDialog<Integer> dialog = new GuiSelectDialog<>(this);
//            dialog.setRendererBuilder(e -> {
//                GuiLabel label = new GuiLabel(data.getEnumDisplayName(e)).setYSize(10).setTextColour(ChatFormatting.DARK_AQUA, ChatFormatting.AQUA);
//                GuiToolkit.addHoverHighlight(label, 16, 0);
//                return label;
//            });
//            dialog.addItem(data.enumValueIndex);
//            dialog.addItems(data.enumValueOptions.stream().filter(e -> e != data.enumValueIndex).collect(Collectors.toList()));
//            dialog.setInsets(1, 1, 1, 1);
//            dialog.setSize(increment.xPos() - decrement.maxXPos(), Math.min(120, (data.enumValueOptions.size() * 10) + 3));
//
//            dialog.getScrollElement().setVerticalScrollBar(new GuiSlideControl(GuiSlideControl.SliderRotation.VERTICAL)
//                    .setPos(dialog.maxXPos() - 7, dialog.yPos() + 1)
//                    .setSize(7, dialog.ySize() - 2)
//                    .setInsets(0, 0, 0, 0)
//                    .setParentScroll(true)
//                    .setBackgroundElement(new GuiBorderedRect().setFillColours(mixColours(ThemedElements.getBgFill(), 0xE0101010, true), mixColours(ThemedElements.getBgFill(), 0xB0101010, true)).setBorderColour(0))
//                    .setSliderElement(new ThemedElements.ScrollBar(false)));
//
//            dialog.addBackGroundChild(new GuiBorderedRect().setSize(dialog).setBorderColour(0xFF000000 | ChatFormatting.DARK_AQUA.getColor()).setFillColour(0xFF101010));
//            gui.toolkit.placeOutside(dialog, this, GuiToolkit.LayoutPos.BOTTOM_CENTER, 0, -13);
//            dialog.setBlockOutsideClicks(true);
//            dialog.normalizePosition();
//            dialog.setSelectionListener(newIndex -> {
//                data.updateEnumValue(newIndex);
//                GuiButton.playGenericClick();
//            });
//            dialog.setCloseOnSelection(true);
//            dialog.show();
        }
    }

    //
//    @Override
//    public void renderElement(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
//        MultiBufferSource.BufferSource getter = RenderUtils.getGuiBuffers();
//        drawColouredRect(getter, xPos(), yPos(), xSize(), ySize(), (index % 2 == 0 ? 0x202020 : 0x101010) | opacitySupplier.get());
//
//        if (advanced && gui.hoveredProvider != null && gui.hoveredProvider.getProviderName().equals(data.providerName)) {
//            if (data.isGlobal) {
//                drawBorderedRect(getter, xPos(), yPos(), xSize(), ySize(), 1, 0, 0x80ffff00);
//            } else if (gui.hoveredProvider.getProviderID().equals(data.providerID)) {
//                drawBorderedRect(getter, xPos(), yPos(), xSize(), ySize(), 1, 0, 0x8000ff00);
//            }
//        }
//
//        getter.endBatch();
//        super.renderElement(minecraft, mouseX, mouseY, partialTicks);
//        if (!data.isPropertyAvailable() && !data.isGlobal) {
//            zOffset += 10;
//            drawColouredRect(getter, xPos(), yPos(), xSize(), ySize(), 0x80FF8080);
//            zOffset -= 10;
//            getter.endBatch();
//        }
//    }
//
//    @Override
//    public boolean renderOverlayLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
//        if (isMouseOver(mouseX, mouseY) && !data.isProviderAvailable && !data.isGlobal) {
//            PoseStack poseStack = new PoseStack();
//            poseStack.translate(0, 0, getRenderZLevel());
//            renderTooltip(poseStack, Component.translatable("gui.draconicevolution.item_config.provider_unavailable"), mouseX, mouseY);
//            return true;
//        }
//        return super.renderOverlayLayer(minecraft, mouseX, mouseY, partialTicks);
//    }
//
//    @Override
//    public boolean mouseClicked(double mouseX, double mouseY, int button) {
//        if (!data.isProviderAvailable && !data.isGlobal && (globalButton == null || !globalButton.isMouseOver(mouseX, mouseY)) && (dragZone == null || !dragZone.isMouseOver(mouseX, mouseY))) {
//            return false;
//        }
//        return super.mouseClicked(mouseX, mouseY, button);
//    }
//
//    private void detectValueChanges() {
//        if (slider.isEnabled()) {
//            slider.setRange(data.minValue, data.maxValue);
//            if (!slider.isDragging()) {
//                if (data.type == ConfigProperty.Type.DECIMAL && slider.getPosition() != data.decimalValue) {
//                    slider.updatePos(data.decimalValue);
//                } else if (data.type == ConfigProperty.Type.INTEGER && slider.getPosition() != data.integerValue) {
//                    slider.updatePos(data.integerValue);
//                }
//            }
//        }
//    }
//
//    @Override
//    public boolean onUpdate() {
//        detectValueChanges();
//        if (advanced && label.isMouseOver(getMouseX(), getMouseY())) {
//            gui.hoveredData = data;
//        }
//        return super.onUpdate();
//    }
//
    private static class DataSlideState implements SliderState {
        private final PropertyData prop;

        public DataSlideState(PropertyData prop) {
            this.prop = prop;
        }

        @Override
        public double getPos() {
            return MathHelper.map(prop.type == ConfigProperty.Type.INTEGER ? prop.integerValue : prop.decimalValue, prop.minValue, prop.maxValue, 0, 1);
        }

        @Override
        public void setPos(double pos) {
            prop.updateNumberValue(MathHelper.map(pos, 0, 1, prop.minValue, prop.maxValue), true);
        }

        @Override
        public double scrollSpeed() {
            return 0.05;
        }

        @Override
        public boolean canScroll(Axis scrollAxis) {
            return Screen.hasShiftDown();
        }
    }

    private static class SliderBackground extends GuiElement<SliderBackground> implements BackgroundRender {
        private GuiSlider slider;

        public SliderBackground(@NotNull GuiParent<?> parent) {
            super(parent);
        }

        @Override
        public void renderBackground(GuiRender render, double mouseX, double mouseY, float partialTicks) {
            if (slider != null && (slider.isDragging() || slider.isMouseOver())) {
                render.rect(xMin(), yMin(), xSize(), ySize(), 0x60475b6a);
            }
            render.rect(xMin(), yMin() + (ySize() / 2F) - 1, xSize(), 2, 0xFF808080);
            render.rect(xMin(), yMin(), 1, ySize(), 0xFF808080);
            render.rect(xMin() + xSize() - 1, yMin(), 1, ySize(), 0xFF808080);
        }
    }
}
