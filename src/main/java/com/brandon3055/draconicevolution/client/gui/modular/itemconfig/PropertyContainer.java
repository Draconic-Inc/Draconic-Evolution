package com.brandon3055.draconicevolution.client.gui.modular.itemconfig;

import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.client.BCSprites;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElement;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiButton;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiScrollElement;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiManipulable;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiTextField;
import com.brandon3055.brandonscore.client.utils.GuiHelper;
import com.brandon3055.brandonscore.client.gui.modulargui.ThemedElements;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.client.gui.modular.itemconfig.GuiConfigurableItem.UpdateAnim;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.InputMappings;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.brandon3055.brandonscore.BCConfig.darkMode;
import static com.brandon3055.brandonscore.client.gui.GuiToolkit.LayoutPos.TOP_RIGHT;
import static com.brandon3055.brandonscore.client.gui.modulargui.lib.GuiAlign.CENTER;
import static com.brandon3055.brandonscore.client.gui.modulargui.lib.GuiAlign.TextRotation.NORMAL;

/**
 * Created by brandon3055 on 15/5/20.
 */
public class PropertyContainer extends GuiManipulable {
    private int setXPos = 0;
    private int setYPos = 0;
    private int timeSinceMove = 0;
    private int expandedHeight = 0;
    private int prevUserHeight = 0;
    private final boolean isGroup;
    protected String boundKey = "";
    private String defaultName = "Group";
    private boolean binding = false;
    private boolean removed = false;
    protected boolean isPreset = false;
    private boolean semiTrans = false;
    private boolean collapsed = false;
    protected boolean globalKeyBind = false;
    private GuiButton applyPreset;
    private final GuiElement<?> parent;
    protected KeyModifier modifier = KeyModifier.NONE;
    private GuiTextField groupName;
    private PropertyElement dropTargetElement = null;
    private GuiScrollElement scrollElement;
    private PropertyContainer dropTarget = null;
    private Rectangle cancelZone = null;
    private final GuiConfigurableItem gui;

    public LinkedList<PropertyData> dataList = new LinkedList<>();
    public LinkedHashMap<PropertyData, PropertyElement> dataElementMap = new LinkedHashMap<>();

    public PropertyContainer(GuiConfigurableItem gui, boolean isGroup) {
        this.gui = gui;
        this.parent = gui == null ? null : gui.advancedContainer;
        this.isGroup = isGroup;
        setSize(150, getMinSize().height);
        if (isGroup) {
            setResizeBorders(3);
        } else {
            setHResizeBorders(1);
        }
        setCanResizeV(() -> !collapsed);
        setEnableCursors(true);
        this.setCapturesClicks(true);
    }

    public void setCancelZone(Rectangle cancelZone) {
        this.cancelZone = cancelZone;
    }

    @Override
    public void addChildElements() {
        scrollElement = GuiConfigurableItem.createPropertyList();
        if (isGroup) {
            scrollElement.setPos(xPos() + 3, yPos() + 3 + 9);
            scrollElement.onReload(e -> e.setMaxPos(maxXPos() - 3, maxYPos() - 3, true));
            scrollElement.setEnabledCallback(() -> scrollElement.ySize() > 10 && !applyPreset.isEnabled());

            GuiButton toggleHidden = gui.toolkit.createIconButton(this, 8, () -> BCSprites.getThemed(collapsed ? "expand_content" : "collapse_content"));
            toggleHidden.setPos(xPos() + 2, yPos() + 2);
            toggleHidden.onPressed(this::toggleCollapsed);
            toggleHidden.setHoverText(e -> {
                if (isPreset) return I18n.format("gui.draconicevolution.item_config.edit_preset.info");
                else return I18n.format("gui.draconicevolution.item_config." + (collapsed ? "expand_group" : "collapse_group") + ".info");
            });

            GuiElement<?> dragZone = gui.toolkit.createHighlightIcon(this, 8, 8, 2, 2, BCSprites.themedGetter("reposition"), e -> e.getHoverTime() > 0 || dragPos);
            dragZone.setHoverText(e -> dragPos ? Collections.emptyList() : I18n.format("gui.draconicevolution.item_config.move_group.info"));
            dragZone.setHoverTextDelay(10);
            dragZone.onReload(e -> e.setMaxXPos(maxXPos() - 2, false).setYPos(yPos() + 2));
            setDragZone(dragZone::isMouseOver);

            GuiButton togglePreset = gui.toolkit.createIconButton(this, 8, BCSprites.themedGetter("preset_icon"));
            GuiElement<?> hoverRect = GuiToolkit.addHoverHighlight(togglePreset, 2, 2);
            togglePreset.setHoverText(I18n.format("gui.draconicevolution.item_config.toggle_preset.info"));
            togglePreset.onReload(e -> e.setMaxXPos(dragZone.xPos() - 2, false).setYPos(yPos() + 2));
            togglePreset.onPressed(this::togglePreset);
            togglePreset.addChild(new ThemedElements.ShadedRect(true, false).setPosAndSize(hoverRect).setEnabledCallback(() -> isPreset));

            GuiButton globalBinding = gui.toolkit.createIconButton(this, 8, BCSprites.themedGetter("global_key_icon"));
            hoverRect = GuiToolkit.addHoverHighlight(globalBinding, 2, 2);
            globalBinding.setHoverText(I18n.format("gui.draconicevolution.item_config.toggle_global_binding.info"));
            globalBinding.onReload(e -> e.setMaxXPos(togglePreset.xPos() - 2, false).setYPos(yPos() + 2));
            globalBinding.onPressed(() -> {
                globalKeyBind = !globalKeyBind;
                gui.savePropertyConfig();
            });
            globalBinding.addChild(new ThemedElements.ShadedRect(true, false).setPosAndSize(hoverRect).setEnabledCallback(() -> globalKeyBind));
            globalBinding.setEnabledCallback(() -> !boundKey.isEmpty() && isPreset);

            groupName = new GuiTextField();
            groupName.setPos(xPos() + 12, yPos() + 2);
            groupName.onReload(e -> e.setMaxPos(globalBinding.isEnabled() ? globalBinding.xPos() - 2 : togglePreset.xPos() - 2, e.yPos() + 8, true));
            groupName.setText(defaultName);
            groupName.setChangeListener(gui::savePropertyConfig);
            groupName.setEnableBackgroundDrawing(false);
            groupName.setBlinkingCursor(true);
            groupName.setTextColor(GuiToolkit.Palette.BG::text);
            groupName.setShadowSupplier(() -> darkMode);
            groupName.setCursorColor(0xFFFFFFFF);
            groupName.onFinishEdit(gui::savePropertyConfig);
            addChild(groupName);

            GuiButton bindButton = gui.toolkit.createBorderlessButton(this, "");
            bindButton.setHoverText(I18n.format("gui.draconicevolution.item_config.set_key_bind.info"));
            bindButton.setHoverTextDelay(10);
            bindButton.setYSize(12).setYPos(yPos() + 11);
            bindButton.onReload(e -> e
                    .setText(getBindingName())
                    .setXSize(Math.min((xSize() - 3) / 2, fontRenderer.getStringWidth(bindButton.getDisplayString()) + 6))
                    .setMaxXPos(maxXPos() - 2, false)
            );

            bindButton.onPressed(() -> {
                boundKey = "";
                modifier = KeyModifier.NONE;
                binding = !binding;
                gui.savePropertyConfig();
                reloadElement();
            });
            addChild(bindButton);

            applyPreset = gui.toolkit.createBorderlessButton(this, "gui.draconicevolution.item_config.apply_preset");
            applyPreset.setEnabledCallback(() -> isPreset && collapsed && ySize() == getTargetHeight());
            applyPreset.setPos(xPos() + 2, yPos() + 11).setYSize(12);
            applyPreset.onReload(e -> e.setMaxXPos(bindButton.xPos(), true));
            applyPreset.setTextColGetter((hovering, disabled) -> GuiToolkit.Palette.Ctrl.textH(hovering));
            applyPreset.onPressed(() -> dataElementMap.keySet().forEach(data -> {
                applyData(data);
                gui.updateAnimations.add(new UpdateAnim(data));
            }));
            GuiToolkit.addHoverHighlight(applyPreset);
            bindButton.setEnabledCallback(() -> applyPreset.isEnabled());
            addChild(applyPreset);


        } else {
            scrollElement.setPos(xPos() + 1, yPos() + 1);
            scrollElement.onReload(e -> e.setMaxPos(maxXPos() - 1, maxYPos() - 1, true));
        }
        addChild(scrollElement);
    }

    @Override
    protected boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (binding) {
            modifier = KeyModifier.NONE;
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                boundKey = "";
                binding = false;
                reloadElement();
                gui.savePropertyConfig();
                return true;
            }

            KeyModifier activeMod = KeyModifier.getActiveModifier();
            InputMappings.Input input = InputMappings.getInputByCode(keyCode, scanCode);
            boundKey = input.toString();
            if (activeMod.matches(input)) {
                reloadElement();
                gui.savePropertyConfig();
                return true;
            } else {
                modifier = activeMod;
                binding = false;
                gui.savePropertyConfig();
                reloadElement();
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private String getBindingName() {
        if (binding) return ">" + (boundKey.isEmpty() ? "   " : I18n.format(InputMappings.getInputByName(boundKey).getTranslationKey())) + "<";
        else if (boundKey.isEmpty()) return I18n.format("gui.draconicevolution.item_config.not_bound");
        InputMappings.Input keyCode = InputMappings.getInputByName(boundKey);
        return modifier.getLocalizedComboName(keyCode, () -> {
            String s = keyCode.getTranslationKey();
            int i = keyCode.getKeyCode();
            String s1 = null;
            switch (keyCode.getType()) {
                case KEYSYM:
                    s1 = InputMappings.getKeynameFromKeycode(i);
                    break;
                case SCANCODE:
                    s1 = InputMappings.getKeyNameFromScanCode(i);
                    break;
                case MOUSE:
                    String s2 = I18n.format(s);
                    s1 = Objects.equals(s2, s) ? I18n.format(InputMappings.Type.MOUSE.getName(), i + 1) : s2;
            }

            return s1 == null ? I18n.format(s) : s1;
        });
    }

    @Override
    protected boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (binding) {
            binding = false;
            reloadElement();
            return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public void reloadElement() {
        //This is only ever called after first load to initialize the elements that were just loaded.
        if (dataList.size() != dataElementMap.size()) {
            scrollElement.clearElements();
            dataList.forEach(e -> scrollElement.addElement(dataElementMap.computeIfAbsent(e, this::createElementFor)));
            scrollElement.reloadElement();
        }

        super.reloadElement();
        setPos(setXPos, setYPos);
        validatePosition();
    }

    private void togglePreset() {
        isPreset = !isPreset;
        collapsed = isPreset;
        gui.savePropertyConfig();
    }

    private void toggleCollapsed() {
        collapsed = !collapsed;
        if (!collapsed) {
            parent.bringToForeground(this);
            gui.propertyContainers.remove(this);
            gui.propertyContainers.add(this);
        }
        gui.savePropertyConfig();
    }

    public void addProperty(PropertyData data) {
        addProperty(data, null, false);
    }

    public void addProperty(PropertyData data, @Nullable PropertyData relativeTo, boolean addBefore) {
        if (relativeTo == null || !dataList.contains(relativeTo)) {
            dataList.add(data);
        } else if (addBefore) {
            dataList.add(dataList.indexOf(relativeTo), data);
        } else {
            dataList.add(dataList.indexOf(relativeTo) + 1, data);
        }
        boolean willResize = ySize() == getMaxSize().height;
        scrollElement.clearElements();
        dataList.forEach(e -> scrollElement.addElement(dataElementMap.computeIfAbsent(e, this::createElementFor)));
        if (willResize) {
            setYSize(getMaxSize().height);
            prevUserHeight = expandedHeight = getMaxSize().height;
            reloadElement();
            scrollElement.reloadElement();
        }
    }

    private PropertyElement createElementFor(PropertyData data) {
        PropertyElement element = new PropertyElement(data, gui, true);
        element.setOpacitySupplier(() -> semiTrans ? 0x60000000 : 0xFF000000);
        element.setEnableToolTip(() -> !isDragging);
        data.setChangeListener(() -> {
            if (!isPreset) {
                applyData(data);
                gui.updateAnimations.add(new UpdateAnim(data));
            }
        });

        GuiButton dragZone = gui.toolkit.createIconButton(element, 8, 8, "reposition_gray");
        element.dragZone = dragZone;
        dragZone.setHoverText((e) -> dragPos ? Collections.emptyList() : I18n.format("gui.draconicevolution.item_config.move_prop" + (isGroup ? "_in_group" : "") + ".info"));
        dragZone.onReload(e -> e.setMaxXPos(element.maxXPos() - 1, false).setYPos(element.yPos() + 1));
        if (!isGroup) {
            dragZone.setDisabled(true);
            setDragZone(dragZone::isMouseOver);
            this.setEnabledCallback(() -> DEConfig.configUiShowUnavailable || (data.isGlobal || data.isPropertyAvailable()));
        } else {
            element.setEnabledCallback(() -> DEConfig.configUiShowUnavailable || (data.isGlobal || data.isPropertyAvailable()));
            dragZone.onPressed(() -> {
                boolean willResize = ySize() == getMaxSize().height;
                dataList.remove(data);
                dataElementMap.remove(data);
                scrollElement.clearElements();
                dataList.forEach(e -> scrollElement.addElement(dataElementMap.computeIfAbsent(e, this::createElementFor)));
                if (willResize) {
                    setYSize(getMaxSize().height);
                    prevUserHeight = expandedHeight = getMaxSize().height;
                }
                scrollElement.reloadElement();

                PropertyContainer newContainer = new PropertyContainer(gui, false);
                newContainer.setXSize(Math.max(element.xSize(), newContainer.getMinSize().width));
                gui.toolkit.placeInside(newContainer, element, TOP_RIGHT, 0, 0);
                newContainer.updatePosition();
                parent.addChild(newContainer);
                newContainer.addProperty(data);
                newContainer.startDragging();
            });
        }

        return element;
    }

    private void applyData(PropertyData data) {
        data.sendToServer();
        //Do visualization
    }

    public void inventoryUpdate() {
        dataElementMap.keySet().forEach(e -> e.pullData(gui.getContainer(), !isPreset && !e.isGlobal));
    }

    @Override
    protected void onStartManipulation(double mouseX, double mouseY) {
        parent.removeChild(this);
        parent.modularGui.getManager().addChild(this, 300, false);
        modifyZOffset(300);

        //Move this container to the end of the list so it maintains its render position across save / load cycles.
        gui.propertyContainers.remove(this);
        gui.propertyContainers.add(this);
        if (dragPos) {
            semiTrans = true;
        }
    }

    @Override
    protected void onManipulated(double mouseX, double mouseY) {
        getChildElements().forEach(GuiElement::reloadElement);
        if (!collapsed) {
            expandedHeight = ySize();
        }
        if (dropTarget != null) {
            if (!dropTarget.isMouseOver(mouseX, mouseY)) {
                dropTarget = null;
                dropTargetElement = null;
            } else {
                dropTargetElement = dropTarget.dataElementMap.values().stream()
                        .filter(e -> e.isMouseOver(mouseX, mouseY))
                        .findAny()
                        .orElse(null);
            }
        } else {
            for (PropertyContainer element : gui.propertyContainers) {
                if (element == this) continue;
                Rectangle other = element.getRect();
                if (!isGroup && GuiHelper.isInRect(other.x + 8, other.y + 8, other.width - 16, other.height - 16, mouseX, mouseY)) {
                    dropTarget = element;
                    dropTargetElement = element.dataElementMap.values().stream()
                            .filter(e -> e.isMouseOver(mouseX, mouseY))
                            .findAny()
                            .orElse(null);
                }
            }
        }
        timeSinceMove = 0;
    }

    @Override
    protected void onFinishManipulation(double mouseX, double mouseY) {
        if (removed) return;
        updatePosition();
        parent.modularGui.getManager().removeChild(this);
        parent.addChild(this);
        displayZLevel = 0;
        modifyZOffset(-300);
        semiTrans = false;
        if (!collapsed) {
            prevUserHeight = ySize();
        }

        if (dropTarget != null && !dataList.isEmpty()) {
            if (dropTarget.isGroup) {
                parent.removeChild(this);
                gui.propertyContainers.remove(this);
                boolean before = dropTargetElement != null && mouseY < (dropTargetElement.yPos() + (dropTargetElement.ySize() / 2D));
                dropTarget.addProperty(dataList.get(0), dropTargetElement == null ? null : dropTargetElement.data, before);
            } else if (dropTargetElement != null) {
                parent.removeChild(dropTarget);
                parent.removeChild(this);
                gui.propertyContainers.remove(dropTarget);
                gui.propertyContainers.remove(this);
                PropertyContainer newGroup = new PropertyContainer(gui, true);
                newGroup.setRelPos(dropTargetElement, -2, -12).setXSize(dropTargetElement.xSize());
                newGroup.updatePosition();
                gui.propertyContainers.add(newGroup);
                parent.addChild(newGroup);
                newGroup.addProperty(dropTargetElement.data);
                newGroup.addProperty(dataList.get(0), dropTargetElement.data, mouseY < (dropTargetElement.yPos() + (dropTargetElement.ySize() / 2D)));
            } else {
                dropTarget = null;
            }
        }

        gui.savePropertyConfig();
    }

    @Override
    protected void onFinishMove(double mouseX, double mouseY) {
        if (gui.deleteZone.isMouseOver(mouseX, mouseY) || (cancelZone != null && cancelZone.contains(mouseX, mouseY))) {
            removed = true;
            parent.modularGui.getManager().removeChild(this);
            gui.propertyContainers.remove(this);
        }
        cancelZone = null;
    }

    @Override
    public Dimension getMinSize() {
        return new Dimension(80, 22 + (isGroup ? 15 : 2));
    }

    @Override
    public Dimension getMaxSize() {
        int count = DEConfig.configUiShowUnavailable ? dataElementMap.size() : (int) dataElementMap.keySet().stream().filter(PropertyData::isPropertyAvailable).count();
        int maxY = MathHelper.clip((count * 22) + (isGroup ? 15 : 2), 24, 300);
        return new Dimension(250, maxY);
    }

    @Override
    protected void validateMove(Rectangle previous, double mouseX, double mouseY) {
        if (!DEConfig.configUiEnableSnapping) return;
        Rectangle newPos = getRect();
        Rectangle originalPos = new Rectangle(newPos);
        for (PropertyContainer element : gui.propertyContainers) {
            if (element == this) continue;
            Rectangle other = element.getRect();
            if (newPos.intersects(other) && !previous.intersects(other)) {
                if (!isGroup && GuiHelper.isInRect(other.x + 8, other.y + 8, other.width - 16, other.height - 16, mouseX, mouseY)) {
                    setPos(originalPos.x, originalPos.y);
                    return;
                }

                Rectangle2D intersection = newPos.createIntersection(other);
                if (intersection.getWidth() / (double) newPos.width < intersection.getHeight() / (double) newPos.height) {
                    translate((int) (intersection.getWidth() * (previous.x < other.x ? -1 : 1)), 0);
                } else {
                    translate(0, (int) (intersection.getHeight() * (previous.y < other.y ? -1 : 1)));
                }
                newPos = getRect();
            }
        }
    }

    private int getTargetHeight() {
        if (!isDragging && prevUserHeight != expandedHeight && prevUserHeight <= getMaxSize().height) {
            expandedHeight = prevUserHeight;
        }
        if (expandedHeight == 0) {
            expandedHeight = ySize();
        } else if (expandedHeight > getMaxSize().height) {
            expandedHeight = getMaxSize().height;
        }
        return collapsed ? isPreset ? 25 : 12 : expandedHeight;
    }

    private float animHeight = 0;
    private float animDistance = 0;

    @Override
    public boolean renderOverlayLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        if (dropTarget != null && timeSinceMove > 10) {
            if (!dropTarget.isGroup) {
                drawHoveringText(Collections.singletonList(I18n.format("gui.draconicevolution.item_config.drop_create_group.info")), mouseX, mouseY, fontRenderer);
            } else {
                drawHoveringText(Collections.singletonList(I18n.format("gui.draconicevolution.item_config.add_to_group.info")), mouseX, mouseY, fontRenderer);
            }
            return true;
        }
        if (dragPos && gui.deleteZone.isMouseOver(mouseX, mouseY)) {
            drawHoveringText(Collections.singletonList(I18n.format("gui.draconicevolution.item_config.drop_to_delete.info")), mouseX, mouseY, fontRenderer);
        }

        return super.renderOverlayLayer(minecraft, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderElement(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        int targetHeight = getTargetHeight();
        if (targetHeight != ySize()) {
            if (animDistance == 0) animDistance = targetHeight - ySize();
            animHeight = MathHelper.approachLinear(animHeight, targetHeight, Math.abs(animDistance) * 0.15F * partialTicks);
            if (animDistance > 0 && animHeight > targetHeight) animHeight = targetHeight;
            else if (animDistance < 0 && animHeight < targetHeight) animHeight = targetHeight;
            setYSize((int) animHeight);
            reloadElement();
            if (animHeight == targetHeight) {
                scrollElement.reloadElement();
            }
        } else {
            animHeight = targetHeight;
            animDistance = 0;
        }

        IRenderTypeBuffer.Impl getter = minecraft.getRenderTypeBuffers().getBufferSource();

        if (dropTarget != null) {
            double zLevel = getRenderZLevel() - 10;
            zOffset -= zLevel;
            if (dropTargetElement != null) {
                Rectangle rect = dropTargetElement.getRect();
                if (mouseY < rect.y + (rect.height / 2D)) {
                    drawGradientRect(getter, rect.x, rect.y, rect.x + rect.width, rect.y + 6, 0xFF00FF00, 0x0000FF00);
                } else {
                    drawGradientRect(getter, rect.x, rect.y + rect.height - 6, rect.x + rect.width, rect.y + rect.height, 0x0000FF00, 0xFF00FF00);
                }
            } else {
                drawGradientRect(getter, dropTarget.xPos() + 3, dropTarget.yPos() + 13, dropTarget.maxXPos() - 3, dropTarget.yPos() + 15, 0xFF00FF00, 0x0000FF00);
                drawGradientRect(getter, dropTarget.xPos() + 3, dropTarget.maxYPos() - 6, dropTarget.maxXPos() - 3, dropTarget.maxYPos() - 3, 0x0000FF00, 0xFF00FF00);
            }
            zOffset += zLevel;
        }

        int alpha = semiTrans ? 0x60000000 : 0xFF000000;
        Material mat = BCSprites.getThemed("borderless_bg_dynamic_small");
        drawDynamicSprite(mat.getBuffer(getter, e -> BCSprites.guiTexType), mat.getSprite(), xPos(), yPos(), xSize(), ySize(), 2, 2, 2, 2, 0xFFFFFF | alpha);

        int contentPos = yPos() + 2 + 9;
        int contentHeight = ySize() - 4 - 9;
        if (isGroup && contentHeight > 0 && !applyPreset.isEnabled()) {
//            if (applyPreset.isEnabled() && !applyPreset.isPressed()) {
//                mat = BCSprites.getThemed("button_borderless" + (applyPreset.isPressed() ? "_invert" : ""));
//                drawDynamicSprite(mat.getBuffer(getter, e -> BCSprites.guiTexType), mat.getSprite(), xPos() + 2, contentPos, xSize() - 4, contentHeight, 2, 2, 2, 2, 0xFFFFFF | alpha);
//            } else {
            int light = (darkMode ? 0x5b5b5b : 0xFFFFFF) | alpha;
            int dark = (darkMode ? 0x282828 : 0x505050) | alpha;
            drawShadedRect(getter, xPos() + 2, contentPos, xSize() - 4, contentHeight, 1, 0, dark, light, midColour(light, dark));
//            }
            if (dataList.isEmpty()) {
                drawCustomString(fontRenderer, I18n.format("gui.draconicevolution.item_config.drop_prop_here"), xPos() + 3, yPos() + 13, xSize() - 6, GuiToolkit.Palette.BG.text(), CENTER, NORMAL, false, true, darkMode);
            }
        }

        getter.finish();
        super.renderElement(minecraft, mouseX, mouseY, partialTicks);

        if (dragPos && gui.deleteZone.isMouseOver(mouseX, mouseY)) {
            drawColouredRect(getter, xPos() + 1, yPos() + 1, xSize() - 2, ySize() - 3, 0x80FF8080);
            getter.finish();
        }
    }

    @Override
    public boolean onUpdate() {
        timeSinceMove++;
        return super.onUpdate();
    }

    public void updatePosition() {
        setXPos = xPos();
        setYPos = yPos();
    }

    public CompoundNBT serialize() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putBoolean("group", isGroup);
        if (isGroup) {
            nbt.putBoolean("preset", isPreset);
            nbt.putBoolean("collapsed", collapsed);
            nbt.putString("name", groupName.getText());
            nbt.putInt("user_height", prevUserHeight);
            nbt.putBoolean("global_key", globalKeyBind);
            if (!boundKey.isEmpty()) {
                nbt.putString("binding", boundKey);
                nbt.putInt("modifier", modifier.ordinal());
            }
        }
        nbt.putInt("x_pos", setXPos);
        nbt.putInt("y_pos", setYPos);
        nbt.putInt("x_size", xSize());
        nbt.putInt("y_size", expandedHeight);
        nbt.put("data", dataList.stream()
                .map(PropertyData::serialize)
                .collect(Collectors.toCollection(ListNBT::new)));

        return nbt;
    }

    public static PropertyContainer deserialize(GuiConfigurableItem gui, CompoundNBT nbt) {
        boolean isGroup = nbt.getBoolean("group");
        PropertyContainer container = new PropertyContainer(gui, isGroup);
        if (isGroup) {
            container.isPreset = nbt.getBoolean("preset");
            container.collapsed = nbt.getBoolean("collapsed");
            container.defaultName = nbt.getString("name");
            container.prevUserHeight = nbt.getInt("user_height");
            container.boundKey = nbt.getString("binding");
            container.modifier = KeyModifier.values()[nbt.getInt("modifier")];
            container.globalKeyBind = nbt.getBoolean("global_key");
        }
        container.setXPos = nbt.getInt("x_pos");
        container.setYPos = nbt.getInt("y_pos");
        container.setXSize(nbt.getInt("x_size"));
        container.expandedHeight = nbt.getInt("y_size");
        container.setYSize(container.expandedHeight);

        container.dataList.addAll(nbt.getList("data", 10).stream()
                .map(e -> (CompoundNBT) e)
                .map(PropertyData::deserialize)
                .filter(Objects::nonNull)
                .collect(Collectors.toList())
        );

        return container;
    }
}
