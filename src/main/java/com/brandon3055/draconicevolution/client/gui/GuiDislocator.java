package com.brandon3055.draconicevolution.client.gui;

import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.BCConfig;
import com.brandon3055.brandonscore.client.BCGuiSprites;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElement;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElementManager;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiScreen;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiButton;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiScrollElement;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiSlideControl;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiBorderedRect;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiLabel;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiTextField;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiTexture;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.GuiAlign;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.TGuiBase;
import com.brandon3055.brandonscore.client.render.RenderUtils;
import com.brandon3055.brandonscore.utils.DataUtils;
import com.brandon3055.draconicevolution.client.DEGuiSprites;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.items.tools.DislocatorAdvanced;
import com.brandon3055.draconicevolution.items.tools.DislocatorAdvanced.DislocatorTarget;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.Material;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by brandon3055 on 4/1/21
 */
public class GuiDislocator extends ModularGuiScreen {
    private final Player player;
    private List<DislocatorTarget> targetList = new ArrayList<>();
    private LinkedList<TargetElement> targetElements = new LinkedList();
    private GuiScrollElement scrollElement;
    private int selectedIndex = 0;
    private int lastAdded = -1;
    private int fuel = 0;
    private boolean editAdded = false;
    private static double lastPos = 0;
    private boolean posLoaded = false;
    private boolean draggingTarget = false;
    private boolean blinkMode = false;

    protected GuiToolkit<GuiDislocator> toolkit = new GuiToolkit<>(this, 240, 177).setTranslationPrefix("gui.draconicevolution.dislocator");

    public GuiDislocator(Component title, Player player) {
        super(title);
        this.player = player;
    }

    @Override
    public void addElements(GuiElementManager manager) {
        TGuiBase temp = new TGuiBase(this);
        temp.background = GuiTexture.newDynamicTexture(xSize(), ySize(), () -> BCGuiSprites.getThemed("background_dynamic"));
        temp.background.onReload(guiTex -> guiTex.setPos(guiLeft(), guiTop()));
        toolkit.loadTemplate(temp);
        int bgPad = 5;
        int scrollBarWidth = 10;

        //Left Content
        GuiBorderedRect posBG = new GuiBorderedRect()
                .set3DGetters(GuiToolkit.Palette.Slot::fill, GuiToolkit.Palette.Slot::accentDark, GuiToolkit.Palette.Slot::accentLight)
                .setBorderColourL(GuiToolkit.Palette.Slot::border3D)
                .setSize(122, 12 * 12 + 13)
                .setXPos(temp.background.xPos() + bgPad + scrollBarWidth + 1)
                .setMaxYPos(temp.background.maxYPos() - bgPad, false);
        temp.background.addChild(posBG);

        GuiSlideControl scrollBar = toolkit.createVanillaScrollBar()
                .setPos(temp.background.xPos() + bgPad, posBG.yPos())
                .setMaxYPos(temp.background.maxYPos() - bgPad, true)
                .setXSize(scrollBarWidth);

        scrollElement = new GuiScrollElement().setListMode(GuiScrollElement.ListMode.VERT_LOCK_POS_WIDTH)
                .setListSpacing(1)
                .setInsets(1, 1, 1, 1);
        posBG.addChild(scrollElement)
                .setPos(posBG)
                .setSize(posBG.getInsetRect()).bindSize(posBG, true)
                .setVerticalScrollBar(scrollBar)
                .setStandardScrollBehavior();

        //Right Content
        GuiBorderedRect infoBG = temp.background.addChild(new GuiBorderedRect())
                .set3DGetters(GuiToolkit.Palette.Slot::fill, GuiToolkit.Palette.Slot::accentDark, GuiToolkit.Palette.Slot::accentLight)
                .setBorderColourL(GuiToolkit.Palette.Slot::border3D)
                .setPos(posBG.maxXPos() + 2, posBG.yPos())
                .setMaxXPos(temp.background.maxXPos() - bgPad, true)
                .setHoverText(e -> new String[]{
                        ChatFormatting.AQUA + getTarget().getName(),
                        ChatFormatting.GOLD + "X: " + (int) getTarget().getX(),
                        ChatFormatting.GOLD + "Y: " + (int) getTarget().getY(),
                        ChatFormatting.GOLD + "Z: " + (int) getTarget().getZ(),
                        ChatFormatting.GOLD + (hasShiftDown() ? getTarget().getDimension().location().toString() : getTarget().getDimension().location().getPath())});

        GuiLabel xLabel = infoBG.addChild(new GuiLabel().setAlignment(GuiAlign.LEFT).setShadowStateSupplier(() -> BCConfig.darkMode))
                .setDisplaySupplier(() -> "X: " + (int) getTarget().getX())
                .setPos(infoBG.xPos() + 2, infoBG.yPos() + 2)
                .setYSize(8)
                .setTextColGetter(GuiToolkit.Palette.Slot::text)
                .setMaxXPos(infoBG.maxXPos() - 1, true)
                .setEnabledCallback(() -> selectedIndex >= 0 && selectedIndex < targetList.size());

        GuiLabel yLabel = infoBG.addChild(new GuiLabel().setAlignment(GuiAlign.LEFT).setShadowStateSupplier(() -> BCConfig.darkMode))
                .setDisplaySupplier(() -> "Y: " + (int) getTarget().getY())
                .setPos(infoBG.xPos() + 2, xLabel.maxYPos() + 2)
                .setYSize(8)
                .setTextColGetter(GuiToolkit.Palette.Slot::text)
                .setMaxXPos(infoBG.maxXPos() - 1, true)
                .setEnabledCallback(() -> selectedIndex >= 0 && selectedIndex < targetList.size());

        GuiLabel zLabel = infoBG.addChild(new GuiLabel().setAlignment(GuiAlign.LEFT).setShadowStateSupplier(() -> BCConfig.darkMode))
                .setDisplaySupplier(() -> "Z: " + (int) getTarget().getZ())
                .setPos(infoBG.xPos() + 2, yLabel.maxYPos() + 2)
                .setYSize(8)
                .setTextColGetter(GuiToolkit.Palette.Slot::text)
                .setMaxXPos(infoBG.maxXPos() - 1, true)
                .setEnabledCallback(() -> selectedIndex >= 0 && selectedIndex < targetList.size());

        GuiLabel dimLabel = infoBG.addChild(new GuiLabel().setAlignment(GuiAlign.LEFT).setShadowStateSupplier(() -> BCConfig.darkMode))
                .setDisplaySupplier(() -> getTarget().getDimension().location().getPath())
                .setPos(infoBG.xPos() + 2, zLabel.maxYPos() + 2)
                .setYSize(8)
                .setTextColGetter(GuiToolkit.Palette.Slot::text)
                .setMaxXPos(infoBG.maxXPos() - 1, true)
                .setEnabledCallback(() -> selectedIndex >= 0 && selectedIndex < targetList.size());

        infoBG.setMaxYPos(dimLabel.maxYPos() + 2, true);

        GuiButton setHere = toolkit.createButton_old(toolkit.i18n("update"), temp.background, true)
                .setHoverText(toolkit.i18n("update.info"))
                .setPos(posBG.maxXPos() + 2, infoBG.maxYPos() + 2)
                .setMaxXPos(temp.background.maxXPos() - bgPad, true)
                .setYSize(15)
                .onPressed(() -> DraconicNetwork.sendDislocatorMessage(7, output -> output.writeVarInt(selectedIndex)))
                .setDisabledStateSupplier(() -> !hasTarget() || getTarget().isLocked());

        //Fuel Add
        GuiButton fuel1 = toolkit.createButton_old(toolkit.i18n("add_1"), temp.background, true, 0)
                .setHoverText(toolkit.i18n("fuel_add_1.info"))
                .setXPos(posBG.maxXPos() + 2)
                .setSize(20, 13)
                .setMaxYPos(temp.background.maxYPos() - bgPad - 1, false)
                .onPressed(() -> DraconicNetwork.sendDislocatorMessage(6, output -> output.writeBoolean(false).writeBoolean(false)));

        GuiButton fuel16 = toolkit.createButton_old(toolkit.i18n("add_16"), temp.background, true, 0)
                .setHoverText(toolkit.i18n("fuel_add_16.info"))
                .setSize(28, 13)
                .setPos(fuel1.maxXPos() + 1, fuel1.yPos())
                .onPressed(() -> DraconicNetwork.sendDislocatorMessage(6, output -> output.writeBoolean(true).writeBoolean(false)));

        GuiButton fuelAll = toolkit.createButton_old(toolkit.i18n("add_all"), temp.background, true, 0)
                .setHoverText(toolkit.i18n("fuel_add_all.info"))
                .setYSize(13)
                .setPos(fuel16.maxXPos() + 1, fuel16.yPos())
                .setMaxXPos(temp.background.maxXPos() - bgPad - 1, true)
                .onPressed(() -> DraconicNetwork.sendDislocatorMessage(6, output -> output.writeBoolean(false).writeBoolean(true)));

        temp.background.addBackGroundChild(new GuiBorderedRect()
                .setFillColour(0xFF000000)
                .setPos(fuel1.xPos() - 1, fuel1.yPos() - 1)
                .setMaxPos(fuelAll.maxXPos() + 1, fuelAll.maxYPos() + 1, true));

        //Fuel Display
        GuiBorderedRect fuelBG = temp.background.addChild(new GuiBorderedRect())
                .set3DGetters(GuiToolkit.Palette.Slot::fill, GuiToolkit.Palette.Slot::accentDark, GuiToolkit.Palette.Slot::accentLight)
                .setBorderColourL(GuiToolkit.Palette.Slot::border3D)
                .setYSize(14)
                .setXPos(posBG.maxXPos() + 2)
                .setMaxYPos(fuel1.yPos() - 4, false)
                .setMaxXPos(temp.background.maxXPos() - bgPad, true);

        GuiLabel fuelLabel = fuelBG.addChild(new GuiLabel().setAlignment(GuiAlign.LEFT).setShadowStateSupplier(() -> BCConfig.darkMode))
                .setDisplaySupplier(() -> toolkit.i18n("fuel") + " " + fuel)
                .setHoverText(toolkit.i18n("fuel.info"))
                .setPos(posBG.maxXPos() + 4, fuelBG.yPos())
                .setMaxXPos(temp.background.maxXPos() - bgPad, true)
                .setTextColGetter(GuiToolkit.Palette.Slot::text)
                .setYSize(14);

        //Add New
        int subWidth = 18;
        GuiButton addButton = toolkit.createButton_old(toolkit.i18n("add"), temp.background, true)
                .setHoverText(toolkit.i18n("add.info"))
                .setYSize(15)
                .setXPos(posBG.maxXPos() + 2)
                .setMaxYPos(fuelBG.yPos() - 3, false)
                .setMaxXPos(temp.background.maxXPos() - bgPad - subWidth, true)
                .onPressed(() -> addNew(0));

        addButton.addChild(new GuiBorderedRect()
                .setFillColour(0xFF000000)
                .setPos(addButton.maxXPos(), addButton.yPos())
                .setSize(subWidth, addButton.ySize()));

        toolkit.createButton_old("", addButton, true, 0)
                .setHoverText(toolkit.i18n("add_top.info"))
                .setSize(subWidth - 1, 6)
                .setXPos(addButton.maxXPos())
                .setYPos(addButton.yPos() + 1)
                .onPressed(() -> addNew(1))
                .addChild(new GuiTexture(8, 8, DEGuiSprites.get("dislocator/add_top"))
                        .onReload(GuiTexture::centerOnParent));

        toolkit.createButton_old("", addButton, true, 0)
                .setHoverText(toolkit.i18n("add_bottom.info"))
                .setSize(subWidth - 1, 6)
                .setXPos(addButton.maxXPos())
                .setMaxYPos(addButton.maxYPos() - 1, false)
                .onPressed(() -> addNew(2))
                .addChild(new GuiTexture(8, 8, DEGuiSprites.get("dislocator/add_bottom"))
                        .onReload(GuiTexture::centerOnParent));

        toolkit.createButton_old("", addButton, true)
                .setHoverText(toolkit.i18n("right_click_mode.info"))
                .setDisplaySupplier(() -> toolkit.i18n("mode_" + (blinkMode ? "blink" : "tp")))
                .setWrap(true)
                .setYSize(24)
                .setXPos(addButton.xPos())
                .setMaxXPos(temp.background.maxXPos() - bgPad, true)
                .setMaxYPos(addButton.yPos() - 2, false)
                .onPressed(() -> DraconicNetwork.sendDislocatorMessage(5, output -> output.writeBoolean(!blinkMode)));

        manager.onTick(this::detectChanges);
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

    private void detectChanges() {
        ItemStack stack = DislocatorAdvanced.findDislocator(player);
        if (stack.isEmpty() || !player.isAlive()) {
            onClose();
            return;
        }

        int lastSize = targetList.size();
        targetList = DEContent.dislocator_advanced.getTargetList(stack);
        int count = Math.max(12, targetList.size());
        if (lastAdded != -1 && targetList.size() != lastSize) {
            editAdded = true;
        }

        double lastPos = scrollElement.getVerticalScrollBar().getRawPos();

        while (targetElements.size() > count) {
            scrollElement.removeElement(targetElements.removeLast());
            scrollElement.reloadElement();
        }
        while (targetElements.size() < count) {
            TargetElement newElement = new TargetElement(targetElements.size());
            targetElements.addLast(newElement);
            scrollElement.addElement(newElement);
            scrollElement.reloadElement();
        }

        scrollElement.getVerticalScrollBar().updateRawPos(lastPos);
        selectedIndex = DEContent.dislocator_advanced.getSelectedIndex(stack);
        fuel = DEContent.dislocator_advanced.getFuel(stack);
        blinkMode = DEContent.dislocator_advanced.getBlinkMode(stack);

        if (editAdded && lastAdded == 0) {
            scrollElement.getVerticalScrollBar().updateRawPos(0);
        } else if (editAdded && lastAdded == targetList.size() - 1) {
            scrollElement.getVerticalScrollBar().updateRawPos(1);
        }

        if (!posLoaded) {
            scrollElement.getVerticalScrollBar().updateRawPos(GuiDislocator.lastPos);
            scrollElement.getVerticalScrollBar().addInputListener(e -> GuiDislocator.lastPos = e.getRawPos());
            posLoaded = true;
        }
    }

    @Override
    public void render(PoseStack mStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(mStack);
        super.render(mStack, mouseX, mouseY, partialTicks);
    }

    private class TargetElement extends GuiElement<TargetElement> {
        private int index;
        private GuiTextField field;
        private GuiButton lock;
        private GuiButton delete;
        private int clickTime = 99;
        private boolean mousePressed = false;
        private boolean dragging = false;

        public TargetElement(int index) {
            this.index = index;
            setYSize(12);
            setHoverText(e -> new String[]{
                    ChatFormatting.AQUA + getName(),
                    ChatFormatting.GRAY + toolkit.i18n("right_click_tp"),
                    ChatFormatting.GRAY + toolkit.i18n("double_click_name"),
                    ChatFormatting.GRAY + toolkit.i18n("must_unlock"),
                    ChatFormatting.GRAY + toolkit.i18n("drag_to_move")
            });
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

        private String getName() {
            return hasTarget() ? getTarget().getName() : "[error]";
        }

        private void setName(String name) {
            DraconicNetwork.sendDislocatorMessage(2, e -> e.writeVarInt(index).writeString(name));
        }

        @Override
        public void addChildElements() {
            lock = toolkit.createIconButton(this, 8, 8, () -> DEGuiSprites.get("dislocator/" + (isLocked() ? "locked" : "unlocked")))
                    .setHoverText(toolkit.i18n("edit_lock.info"))
                    .setXPosMod(() -> maxXPos() - 9)
                    .setYPosMod(() -> yPos() + 2)
                    .onPressed(() -> DraconicNetwork.sendDislocatorMessage(3, e -> e.writeVarInt(index).writeBoolean(!isLocked())))
                    .setEnabledCallback(this::hasTarget);

            delete = toolkit.createIconButton(this, 8, 8, DEGuiSprites.getter("dislocator/delete"))
                    .setHoverText(toolkit.i18n("delete.info"))
                    .setXPosMod(() -> lock.xPos() - 9)
                    .setYPosMod(() -> yPos() + 2)
                    .onPressed(() -> DraconicNetwork.sendDislocatorMessage(1, e -> e.writeVarInt(index)))
                    .setEnabledCallback(() -> hasTarget() && !isLocked());

            field = toolkit.createTextField(this, false)
                    .onValueChanged(this::setName)
                    .setPos(xPos() + 1, yPos() + 2)
                    .setYSize(10)
                    .setXSize(100)
                    .setFocusable(false)
                    .onReturnPressed(e -> field.setFocus(false))
                    .setEnabledCallback(this::hasTarget);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            boolean ret = false;
            if (!hasTarget() || button == 2) return false;
            boolean mouseOver = field.isMouseOver(mouseX, mouseY);

            //Teleport on right click
            if (mouseOver && button == 1) {
                DraconicNetwork.sendDislocatorMessage(8, e -> e.writeVarInt(index));
                onClose();
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
        public boolean mouseDragged(double mouseX, double mouseY, int clickedMouseButton, double dragX, double dragY) {
            if (mousePressed && !isMouseOver(mouseX, mouseY)) {
                dragging = draggingTarget = true;
            }
            return super.mouseDragged(mouseX, mouseY, clickedMouseButton, dragX, dragY);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            if (dragging) {
                TargetElement hovered = getHovered((int) mouseX, (int) mouseY);
                if (hovered != null) {
                    int moveIndex = mouseY > hovered.yPos() + (hovered.ySize() / 2D) ? hovered.index + 1 : hovered.index;
                    DraconicNetwork.sendDislocatorMessage(10, e -> e.writeVarInt(moveIndex));
                }
            }

            dragging = mousePressed = draggingTarget = false;
            return super.mouseReleased(mouseX, mouseY, button);
        }

        @Override
        public void renderElement(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
            boolean selected = false;
            boolean hovered = false;
            if (hasTarget() && (dragging || !draggingTarget)) {
                DislocatorTarget target = getTarget();
                int margin = target.isLocked() ? 10 : 18;
                hovered = (mouseX < maxXPos() - margin && hoverTime > 0);
                selected = index == selectedIndex;
            }

            Material mat = DEGuiSprites.get("dislocator/slot");
            Material matSelect = DEGuiSprites.get("dislocator/slot_selected");
            MultiBufferSource.BufferSource getter = RenderUtils.getGuiBuffers();
            drawSprite(getter.getBuffer(BCGuiSprites.GUI_TYPE), xPos(), yPos(), (selected ? matSelect : mat).sprite());
            if ((hovered && !selected)) {
                getter.endBatch();
                drawSprite(getter.getBuffer(BCGuiSprites.GUI_TYPE), xPos(), yPos(), matSelect.sprite(), 0x30FFFFFF);
            }
            getter.endBatch();
            super.renderElement(minecraft, mouseX, mouseY, partialTicks);

            if (lock.isMouseOver(mouseX, mouseY) || (delete.isEnabled() && delete.isMouseOver(mouseX, mouseY))) {
                hoverTime = 0;
            }
        }

        @Override
        public boolean renderOverlayLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
            if (dragging) {
                int yPos = MathHelper.clip(mouseY - 6, getParent().yPos(), getParent().maxYPos() - 12);
                MultiBufferSource.BufferSource getter = RenderUtils.getGuiBuffers();

                TargetElement hovered = getHovered(mouseX, mouseY);
                if (hovered != null) {
                    int y = mouseY > hovered.yPos() + (hovered.ySize() / 2) ? hovered.maxYPos() : hovered.yPos() - 1;
                    drawColouredRect(getter, xPos(), y, xSize(), 3, 0x6000FF00);
                    getter.endBatch();
                }

                drawSprite(getter.getBuffer(BCGuiSprites.GUI_TYPE), xPos(), yPos, DEGuiSprites.get("dislocator/slot_selected").sprite(), 0x60FFFFFF);
                getter.endBatch();


                return true;
            } else if (draggingTarget) {
                return false;
            }

            return super.renderOverlayLayer(minecraft, mouseX, mouseY, partialTicks);
        }

        private TargetElement getHovered(int mouseX, int mouseY) {
            for (TargetElement element : targetElements) {
                if (element != this && element.isMouseOver(mouseX, mouseY) || element.isMouseOver(mouseX, mouseY + 1) || element.isMouseOver(mouseX, mouseY - 1)) {
                    return element;
                }
            }
            return null;
        }

        boolean lastTickTarget = false;

        @Override
        public boolean onUpdate() {
            boolean hasTarget = hasTarget();
            if (hasTarget) {
                if (!field.isFocused() || (lastAdded == index && editAdded)) {
                    field.setValueQuietly(getTarget().getName());
                }
            } else {
                field.setValueQuietly("");
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

            return super.onUpdate();
        }

        @Override
        public boolean isHoverTextEnabled() {
            return hasTarget();
        }
    }
}
