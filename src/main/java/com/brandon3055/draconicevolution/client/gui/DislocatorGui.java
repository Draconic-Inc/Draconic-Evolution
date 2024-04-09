package com.brandon3055.draconicevolution.client.gui;

import codechicken.lib.gui.modular.ModularGui;
import codechicken.lib.gui.modular.ModularGuiScreen;
import codechicken.lib.gui.modular.elements.*;
import codechicken.lib.gui.modular.lib.*;
import codechicken.lib.gui.modular.lib.geometry.Align;
import codechicken.lib.gui.modular.lib.geometry.Axis;
import codechicken.lib.gui.modular.lib.geometry.Direction;
import codechicken.lib.gui.modular.sprite.Material;
import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.BCConfig;
import com.brandon3055.brandonscore.client.BCGuiTextures;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.ButtonRow;
import com.brandon3055.brandonscore.utils.DataUtils;
import com.brandon3055.draconicevolution.client.DEGuiTextures;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.items.tools.DislocatorAdvanced;
import com.brandon3055.draconicevolution.items.tools.DislocatorAdvanced.DislocatorTarget;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

import static codechicken.lib.gui.modular.lib.geometry.Constraint.*;
import static codechicken.lib.gui.modular.lib.geometry.GeoParam.*;
import static net.minecraft.ChatFormatting.*;

/**
 * Created by brandon3055 on 4/1/21
 */
public class DislocatorGui implements GuiProvider {
    private static final GuiToolkit TOOLKIT = new GuiToolkit("gui.draconicevolution.dislocator");
    public static final int GUI_WIDTH = 240;
    public static final int GUI_HEIGHT = 177;

    private final Player player;
    private List<DislocatorTarget> targetList = new ArrayList<>();
    private GuiList<Integer> scrollElement;
    private int selectedIndex = 0;
    private int lastAdded = -1;
    private int fuel = 0;
    private boolean editAdded = false;
    private static double lastPos = 0;
    private boolean posLoaded = false;
    private boolean draggingTarget = false;
    private boolean blinkMode = false;

    public DislocatorGui(Player player) {
        this.player = player;
    }

    @Override
    public GuiElement<?> createRootElement(ModularGui gui) {
        GuiManipulable root = new GuiManipulable(gui).addMoveHandle(3).enableCursors(true);
        GuiTexture bg = new GuiTexture(root.getContentElement(), DEGuiTextures.themedGetter("advanced_dislocator"));
        Constraints.bind(bg, root.getContentElement());
        return root;
    }

    @Override
    public void buildGui(ModularGui gui) {
        gui.initStandardGui(GUI_WIDTH, GUI_HEIGHT);
        GuiElement<?> root = gui.getRoot();

        ButtonRow buttonRow = ButtonRow.topRightInside(root, Direction.DOWN, 3, 3).setSpacing(1);
        buttonRow.addButton(TOOLKIT::createThemeButton);

        TOOLKIT.createHeading(root, gui.getGuiTitle(), true);

        int bgPad = 5;
        int scrollBarWidth = 10;

        //Left Content
        GuiRectangle posBG = TOOLKIT.shadedBorder(root)
                .constrain(LEFT, relative(root.get(LEFT), bgPad + scrollBarWidth + 1))
                .constrain(WIDTH, literal(122))
                .constrain(HEIGHT, literal(12 * 12 + 13))
                .constrain(BOTTOM, relative(root.get(BOTTOM), -bgPad));

        scrollElement = new GuiList<Integer>(root) {
            public SliderState scrollState() {
                SliderState state = super.scrollState();
                return SliderState.forScrollBar(state::getPos, e -> {
                    lastPos = e;
                    state.setPos(e);
                }, state::sliderRatio);
            }
        }
                .setDisplayBuilder(TargetElement::new)
                .setItemSpacing(1);
        Constraints.bind(scrollElement, posBG, 1);

        var vanillaBar = TOOLKIT.vanillaScrollBar(root, Axis.Y);
        vanillaBar.container()
                .constrain(TOP, match(posBG.get(TOP)))
                .constrain(BOTTOM, match(posBG.get(BOTTOM)))
                .constrain(RIGHT, relative(posBG.get(LEFT), -1))
                .constrain(WIDTH, literal(scrollBarWidth));
        vanillaBar.slider()
                .setSliderState(scrollElement.scrollState())
                .setScrollableElement(scrollElement);

        //Right Content
        GuiRectangle infoBG = TOOLKIT.shadedBorder(root)
                .fill(GuiToolkit.Palette.Slot::fill)
                .constrain(LEFT, relative(posBG.get(RIGHT), 2))
                .constrain(TOP, match(posBG.get(TOP)))
                .constrain(RIGHT, relative(root.get(RIGHT), -bgPad))
                .setTooltip(() -> List.of(
                        Component.literal(AQUA + getTarget().getName()),
                        Component.literal(GOLD + "X: " + (int) getTarget().getX()),
                        Component.literal(GOLD + "Y: " + (int) getTarget().getY()),
                        Component.literal(GOLD + "Z: " + (int) getTarget().getZ()),
                        Component.literal(GOLD + (Screen.hasShiftDown() ? getTarget().getDimension().location().toString() : getTarget().getDimension().location().getPath()))
                ));

        GuiText xLabel = new GuiText(infoBG)
                .setAlignment(Align.LEFT)
                .setShadow(() -> BCConfig.darkMode)
                .setTextSupplier(() -> Component.literal("X: " + (int) getTarget().getX()))
                .constrain(TOP, relative(infoBG.get(TOP), 2))
                .constrain(LEFT, relative(infoBG.get(LEFT), 2))
                .constrain(RIGHT, relative(infoBG.get(RIGHT), -1))
                .constrain(HEIGHT, literal(8))
                .setTextColour(GuiToolkit.Palette.Slot::text)
                .setEnabled(() -> selectedIndex >= 0 && selectedIndex < targetList.size());

        GuiText yLabel = new GuiText(infoBG)
                .setAlignment(Align.LEFT)
                .setShadow(() -> BCConfig.darkMode)
                .setTextSupplier(() -> Component.literal("Y: " + (int) getTarget().getY()))
                .constrain(TOP, relative(xLabel.get(BOTTOM), 2))
                .constrain(LEFT, relative(infoBG.get(LEFT), 2))
                .constrain(RIGHT, relative(infoBG.get(RIGHT), -1))
                .constrain(HEIGHT, literal(8))
                .setTextColour(GuiToolkit.Palette.Slot::text)
                .setEnabled(() -> selectedIndex >= 0 && selectedIndex < targetList.size());

        GuiText zLabel = new GuiText(infoBG)
                .setAlignment(Align.LEFT)
                .setShadow(() -> BCConfig.darkMode)
                .setTextSupplier(() -> Component.literal("Z: " + (int) getTarget().getZ()))
                .constrain(TOP, relative(yLabel.get(BOTTOM), 2))
                .constrain(LEFT, relative(infoBG.get(LEFT), 2))
                .constrain(RIGHT, relative(infoBG.get(RIGHT), -1))
                .constrain(HEIGHT, literal(8))
                .setTextColour(GuiToolkit.Palette.Slot::text)
                .setEnabled(() -> selectedIndex >= 0 && selectedIndex < targetList.size());

        GuiText dimLabel = new GuiText(infoBG)
                .setAlignment(Align.LEFT)
                .setShadow(() -> BCConfig.darkMode)
                .setTextSupplier(() -> Component.literal(getTarget().getDimension().location().getPath()))
                .constrain(TOP, relative(zLabel.get(BOTTOM), 2))
                .constrain(LEFT, relative(infoBG.get(LEFT), 2))
                .constrain(RIGHT, relative(infoBG.get(RIGHT), -1))
                .constrain(HEIGHT, literal(8))
                .setTextColour(GuiToolkit.Palette.Slot::text)
                .setEnabled(() -> selectedIndex >= 0 && selectedIndex < targetList.size());

        infoBG.constrain(BOTTOM, relative(dimLabel.get(BOTTOM), 2));

        GuiButton setHere = TOOLKIT.createFlat3DButton(root, () -> TOOLKIT.translate("update"))
                .setTooltip(TOOLKIT.translate("update.info"))
                .constrain(LEFT, match(infoBG.get(LEFT)))
                .constrain(RIGHT, match(infoBG.get(RIGHT)))
                .constrain(TOP, relative(infoBG.get(BOTTOM), 2))
                .constrain(HEIGHT, literal(15))
                .onPress(() -> DraconicNetwork.sendDislocatorMessage(7, output -> output.writeVarInt(selectedIndex)))
                .setDisabled(() -> !hasTarget() || getTarget().isLocked());

        //Fuel Add
        GuiButton fuel1 = TOOLKIT.createFlat3DButton(root, () -> TOOLKIT.translate("add_1"))
                .setTooltip(TOOLKIT.translate("fuel_add_1.info"))
                .constrain(LEFT, match(infoBG.get(LEFT)))
                .constrain(BOTTOM, relative(root.get(BOTTOM), -bgPad - 1))
                .onPress(() -> DraconicNetwork.sendDislocatorMessage(6, output -> output.writeBoolean(false).writeBoolean(false)));
        Constraints.size(fuel1, 20, 13);

        GuiButton fuel16 = TOOLKIT.createFlat3DButton(root, () -> TOOLKIT.translate("add_16"))
                .setTooltip(TOOLKIT.translate("fuel_add_16.info"))
                .constrain(LEFT, relative(fuel1.get(RIGHT), 1))
                .constrain(TOP, match(fuel1.get(TOP)))
                .onPress(() -> DraconicNetwork.sendDislocatorMessage(6, output -> output.writeBoolean(true).writeBoolean(false)));
        Constraints.size(fuel16, 28, 13);

        GuiButton fuelAll = TOOLKIT.createFlat3DButton(root, () -> TOOLKIT.translate("add_all"))
                .setTooltip(TOOLKIT.translate("fuel_add_all.info"))
                .constrain(LEFT, relative(fuel16.get(RIGHT), 1))
                .constrain(RIGHT, match(infoBG.get(RIGHT)))
                .constrain(TOP, match(fuel16.get(TOP)))
                .constrain(HEIGHT, literal(13))
                .onPress(() -> DraconicNetwork.sendDislocatorMessage(6, output -> output.writeBoolean(false).writeBoolean(true)));

        //Fuel Display
        GuiRectangle fuelBG = TOOLKIT.shadedBorder(root)
                .fill(GuiToolkit.Palette.Slot::fill)
                .constrain(LEFT, match(infoBG.get(LEFT)))
                .constrain(RIGHT, match(infoBG.get(RIGHT)))
                .constrain(BOTTOM, relative(fuel1.get(TOP), -4))
                .constrain(HEIGHT, literal(14));

        GuiText fuelLabel = new GuiText(fuelBG)
                .setAlignment(Align.LEFT)
                .setShadow(() -> BCConfig.darkMode)
                .setTextSupplier(() -> TOOLKIT.translate("fuel").append(" " + fuel))
                .setTooltip(TOOLKIT.translate("fuel.info"))
                .setTextColour(GuiToolkit.Palette.Slot::text);
        Constraints.bind(fuelLabel, fuelBG, 2);

        //Add New
        int subWidth = 18;
        GuiButton addButton = TOOLKIT.createFlat3DButton(root, () -> TOOLKIT.translate("add"))
                .setTooltip(TOOLKIT.translate("add.info"))
                .constrain(HEIGHT, literal(15))
                .constrain(LEFT, match(infoBG.get(LEFT)))
                .constrain(RIGHT, relative(infoBG.get(RIGHT), -subWidth))
                .constrain(BOTTOM, relative(fuelBG.get(TOP), -3))
                .onPress(() -> addNew(0));

        new GuiRectangle(root)
                .fill(0xFF000000)
                .constrain(LEFT, match(addButton.get(RIGHT)))
                .constrain(TOP, match(addButton.get(TOP)))
                .constrain(BOTTOM, match(addButton.get(BOTTOM)))
                .constrain(RIGHT, match(infoBG.get(RIGHT)));

        GuiButton upBtn = TOOLKIT.createBorderlessButton(root, Component.empty())
                .setTooltip(TOOLKIT.translate("add_top.info"))
                .constrain(TOP, relative(addButton.get(TOP), 1))
                .constrain(HEIGHT, literal(6))
                .constrain(LEFT, relative(addButton.get(RIGHT), 1))
                .constrain(RIGHT, relative(infoBG.get(RIGHT), -1))
                .onPress(() -> addNew(1));

        GuiTexture upTex = new GuiTexture(upBtn, DEGuiTextures.get("dislocator/add_top"));
        Constraints.size(upTex, 8, 8);
        Constraints.center(upTex, upBtn);

        GuiButton dwnBtn = TOOLKIT.createBorderlessButton(root, Component.empty())
                .setTooltip(TOOLKIT.translate("add_bottom.info"))
                .constrain(BOTTOM, relative(addButton.get(BOTTOM), -1))
                .constrain(HEIGHT, literal(6))
                .constrain(LEFT, relative(addButton.get(RIGHT), 1))
                .constrain(RIGHT, relative(infoBG.get(RIGHT), -1))
                .onPress(() -> addNew(2));

        GuiTexture dwnTex = new GuiTexture(dwnBtn, DEGuiTextures.get("dislocator/add_bottom"));
        Constraints.size(dwnTex, 8, 8);
        Constraints.center(dwnTex, dwnBtn);

        TOOLKIT.createFlat3DButton(root, () -> TOOLKIT.translate("mode_" + (blinkMode ? "blink" : "tp")))
                .setTooltip(TOOLKIT.translate("right_click_mode.info"))
                .constrain(HEIGHT, literal(24))
                .constrain(LEFT, match(infoBG.get(LEFT)))
                .constrain(RIGHT, match(infoBG.get(RIGHT)))
                .constrain(BOTTOM, relative(addButton.get(TOP), -2))
                .onPress(() -> DraconicNetwork.sendDislocatorMessage(5, output -> output.writeBoolean(!blinkMode)))
                .getLabel()
                .setWrap(true);

        gui.onTick(() -> detectChanges(gui));
    }

    private boolean hasTarget() {
        return selectedIndex >= 0 && selectedIndex < targetList.size();
    }

    private DislocatorTarget getTarget() {
        return DataUtils.safeGet(targetList, selectedIndex, () -> new DislocatorTarget(0, 0, 0, Level.OVERWORLD));
    }

    private void addNew(int mode) {
        String tempName = (int) player.getX() + " " + (int) player.getY() + " " + (int) player.getZ();
        lastAdded = mode == 0 ? selectedIndex + 1 : mode == 1 ? 0 : targetList.size();
        DraconicNetwork.sendDislocatorMessage(0, output -> output.writeByte(mode).writeVarInt(lastAdded).writeString(tempName));
    }

    private void detectChanges(ModularGui gui) {
        ItemStack stack = DislocatorAdvanced.findDislocator(player);
        if (stack.isEmpty() || !player.isAlive()) {
            gui.getScreen().onClose();
            return;
        }

        int lastSize = targetList.size();
        targetList = DEContent.DISLOCATOR_ADVANCED.get().getTargetList(stack);
        int count = Math.max(12, targetList.size());
        if (lastAdded != -1 && targetList.size() != lastSize) {
            editAdded = true;
        }

        double lastPos = scrollElement.scrollState().getPos();

        if (count != scrollElement.getList().size()) {
            scrollElement.getList().clear();
            for (int i = 0; i < count; i++) {
                scrollElement.add(i);
            }
        }

        scrollElement.scrollState().setPos(lastPos);
        selectedIndex = DEContent.DISLOCATOR_ADVANCED.get().getSelectedIndex(stack);
        fuel = DEContent.DISLOCATOR_ADVANCED.get().getFuel(stack);
        blinkMode = DEContent.DISLOCATOR_ADVANCED.get().getBlinkMode(stack);

        if (editAdded && lastAdded == 0) {
            scrollElement.scrollState().setPos(0);
        } else if (editAdded && lastAdded == targetList.size() - 1) {
            scrollElement.scrollState().setPos(1);
        }

        if (!posLoaded) {
            scrollElement.scrollState().setPos(DislocatorGui.lastPos);
            posLoaded = true;
        }
    }

    private class TargetElement extends GuiElement<TargetElement> implements BackgroundRender {
        private int index;
        private GuiTextField field;
        private String name = "";
        private GuiButton lock;
        private GuiButton delete;
        private int clickTime = 99;
        private boolean mousePressed = false;
        private boolean dragging = false;

        public TargetElement(GuiElement<?> parent, int index) {
            super(parent);
            this.index = index;
            constrain(HEIGHT, literal(12));
            setTooltip(List.of(
                    getName().withStyle(AQUA),
                    TOOLKIT.translate("right_click_tp").withStyle(GRAY),
                    TOOLKIT.translate("double_click_name").withStyle(GRAY),
                    TOOLKIT.translate("must_unlock").withStyle(GRAY),
                    TOOLKIT.translate("drag_to_move").withStyle(GRAY)
            ));

            lock = TOOLKIT.createIconButton(this, 8, 8, () -> DEGuiTextures.get("dislocator/" + (isLocked() ? "locked" : "unlocked")))
                    .setTooltip(TOOLKIT.translate("edit_lock.info"))
                    .constrain(LEFT, relative(get(RIGHT), -9))
                    .constrain(TOP, relative(get(TOP), 2))
                    .onPress(() -> DraconicNetwork.sendDislocatorMessage(3, e -> e.writeVarInt(index).writeBoolean(!isLocked())))
                    .setEnabled(this::hasTarget);

            delete = TOOLKIT.createIconButton(this, 8, 8, DEGuiTextures.getter("dislocator/delete"))
                    .setTooltip(TOOLKIT.translate("delete.info"))
                    .constrain(LEFT, relative(lock.get(LEFT), -9))
                    .constrain(TOP, relative(get(TOP), 2))
                    .onPress(() -> DraconicNetwork.sendDislocatorMessage(1, e -> e.writeVarInt(index)))
                    .setEnabled(() -> hasTarget() && !isLocked());

            field = TOOLKIT.createTextField(this, false)
                    .constrain(LEFT, relative(get(LEFT), 1))
                    .constrain(TOP, relative(get(TOP), 2))
                    .setFocusable(false)
                    .setEnterPressed(() -> field.setFocus(false))
                    .setEnabled(this::hasTarget);
            Constraints.size(field, 100, 10);
            field.setTextState(TextState.create(() -> name, this::setName));
        }

        private boolean hasTarget() {
            return index >= 0 && index < targetList.size();
        }

        private DislocatorTarget getTarget() {
            return targetList.get(index);
        }

        private boolean isLocked() {
            return hasTarget() && getTarget().isLocked();
        }

        private MutableComponent getName() {
            return Component.literal(hasTarget() ? getTarget().getName() : "[error]");
        }

        private void setName(String name) {
            this.name = name;
            DraconicNetwork.sendDislocatorMessage(2, e -> e.writeVarInt(index).writeString(name));
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            boolean ret = false;
            if (!hasTarget() || button == 2) return false;
            boolean mouseOver = field.isMouseOver();

            //Teleport on right click
            if (mouseOver && button == 1) {
                DraconicNetwork.sendDislocatorMessage(8, e -> e.writeVarInt(index));
                getModularGui().getScreen().onClose();
                return true;
            } else if (mouseOver && button == 0 && !getTarget().isLocked()) {
                //Edit name on double click
                if (clickTime <= 10) {
                    field.setFocus(true);
                    ret = true;
                } else {
                    clickTime = 0;
                }
            }

            ret = ret || super.mouseClicked(mouseX, mouseY, button);
            mousePressed = mouseOver;

            //Select
            if (!ret && mouseOver && index != selectedIndex) {
                DraconicNetwork.sendDislocatorMessage(4, e -> e.writeVarInt(index));
                return true;
            }
            return ret;
        }

        @Override
        public void mouseMoved(double mouseX, double mouseY) {
            if (mousePressed && !isMouseOver()) {
                dragging = draggingTarget = true;
            }
            super.mouseMoved(mouseX, mouseY);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            if (dragging) {
                TargetElement hovered = getHovered();
                if (hovered != null) {
                    int moveIndex = mouseY > hovered.yMin() + (hovered.ySize() / 2D) ? hovered.index + 1 : hovered.index;
                    DraconicNetwork.sendDislocatorMessage(10, e -> e.writeVarInt(moveIndex));
                }
            }

            dragging = mousePressed = draggingTarget = false;
            return super.mouseReleased(mouseX, mouseY, button);
        }

        @Override
        public void renderBackground(GuiRender render, double mouseX, double mouseY, float partialTicks) {
            boolean selected = false;
            boolean hovered = false;
            if (hasTarget() && (dragging || !draggingTarget)) {
                DislocatorTarget target = getTarget();
                int margin = target.isLocked() ? 10 : 18;
                hovered = (mouseX < xMax() - margin && isMouseOver());
                selected = index == selectedIndex;
            }

            Material mat = DEGuiTextures.get("dislocator/slot");
            Material matSelect = DEGuiTextures.get("dislocator/slot_selected");

            render.texRect((selected ? matSelect : mat), xMin(), yMin(), xSize(), ySize());
            if ((hovered && !selected)) {
                render.texRect(matSelect, xMin(), yMin(), xSize(), ySize(), 0x30FFFFFF);
            }
//
//            if (lock.isMouseOver() || (delete.isEnabled() && delete.isMouseOver())) {
//                hoverTime = 0;
//            }
        }

        @Override
        public boolean renderOverlay(GuiRender render, double mouseX, double mouseY, float partialTicks, boolean consumed) {
            if (dragging) {
                double yPos = MathHelper.clip(mouseY - 6, getParent().yMin(), getParent().yMax() - 12);
                TargetElement hovered = getHovered();
                if (hovered != null) {
                    double y = mouseY > hovered.yMin() + (hovered.ySize() / 2) ? hovered.yMax() : hovered.yMin() - 1;
                    render.rect(xMin(), y, xSize(), 3, 0x6000FF00);
                }

                render.texRect(DEGuiTextures.get("dislocator/slot_selected"), xMin(), yPos, xSize(), ySize(), 0x60FFFFFF);
                return true;
            } else if (draggingTarget) {
                return false;
            }
            return super.renderOverlay(render, mouseX, mouseY, partialTicks, consumed);
        }

        private TargetElement getHovered() {
            for (GuiElement<?> element : scrollElement.getElementMap().values()) {
                if (element != this && element.isMouseOver() && element instanceof TargetElement targetElement) {
                    return targetElement;
                }
            }
            return null;
        }

        boolean lastTickTarget = false;

        @Override
        public void tick(double mouseX, double mouseY) {
            boolean hasTarget = hasTarget();
            if (hasTarget) {
                if (!field.isFocused() || (lastAdded == index && editAdded)) {
                    name = getTarget().getName();
                }
                if (!field.isFocused()) {
                    field.setCursorPosition(0);
                    field.setHighlightPos(0);
                }
            } else {
                name = "";
            }
            clickTime++;
            lastTickTarget = hasTarget;

            if (lastAdded == index && editAdded) {
                lastAdded = -1;
                editAdded = false;
                field.setFocus(true);
                field.moveCursorToEnd(false); //TODO Test
                field.setHighlightPos(0);
            }
        }

        @Override
        public boolean isTooltipEnabled() {
            return hasTarget();
        }
    }

    public static class Screen extends ModularGuiScreen {
        public Screen(Component title, Player player) {
            super(new DislocatorGui(player));
            getModularGui().setGuiTitle(title);
        }
    }
}
