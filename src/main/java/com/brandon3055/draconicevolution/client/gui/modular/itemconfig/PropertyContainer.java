package com.brandon3055.draconicevolution.client.gui.modular.itemconfig;

import codechicken.lib.gui.modular.elements.*;
import codechicken.lib.gui.modular.lib.Constraints;
import codechicken.lib.gui.modular.lib.ForegroundRender;
import codechicken.lib.gui.modular.lib.GuiRender;
import codechicken.lib.gui.modular.lib.TextState;
import codechicken.lib.gui.modular.lib.geometry.Axis;
import codechicken.lib.gui.modular.lib.geometry.GeoParam;
import codechicken.lib.gui.modular.lib.geometry.GuiParent;
import codechicken.lib.gui.modular.lib.geometry.Rectangle;
import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.BCConfig;
import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.client.BCGuiTextures;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.lib.Pair;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.client.gui.modular.itemconfig.ConfigurableItemGui.UpdateAnim;
import com.brandon3055.draconicevolution.inventory.ConfigurableItemMenu;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static codechicken.lib.gui.modular.lib.geometry.Constraint.*;
import static codechicken.lib.gui.modular.lib.geometry.GeoParam.*;
import static com.brandon3055.brandonscore.client.gui.GuiToolkit.Palette.BG;

/**
 * Created by brandon3055 on 15/5/20.
 */
public class PropertyContainer extends GuiManipulable {
    private static String DEFAULT_NAME = "Group";
    private static MessageSignature PRESET_CHAT_SIG = Utils.uuidToSig(UUID.fromString("929cb525-22f3-4685-aa09-2c275d057f72"));

    /*TODO
    -✔ Fix collapsability
    -✔ Fix collapse when preset is enabled
    -✔ Fix Drag out,
    -✔ Fix Delete
    -✔ Fix Snapping (Do i really need this? Guess it depends how hard it is... Actually probably pretty simple, Just need a custom position validator that returns invalid if overlapping. Except when drop,
      Could maybe make it a less forceful restriction? Like if you push by more than 10px it lets you through?)
    -✔ Fix Settings dialog
    - Fix Keybinds and implement that thing!
    - Add chat message to say which preset you just activated! (Possibly also say which items it applied to for sanity sake. With item tooltip thing in chat! Indexed chat messages???)
    -✔ Fix tool/prop animation/visualization things.
    - Fix missing tool make property red thing
    */


    //    private int setXPos = 0;
    //    private int setYPos = 0;
    //    private int timeSinceMove = 0;
    //    private int expandedHeight = 0;
    //    private int prevUserHeight = 0;
    private final boolean isGroup;
    protected String boundKey = "";
    protected KeyModifier modifier = KeyModifier.NONE;
    private boolean binding = false;
    private boolean customHeight = false;
    //    private boolean removed = false;
    //
    private boolean collapsed = false;
    protected boolean presetMode = false;
    protected boolean globalKeyBind = false;
    private GuiButton applyPreset;
    //    private final GuiElement<?> parent;
    private GuiTextField groupName;
    private PropertyElement dropTargetElement = null;
    private GuiScrolling scrollElement;
    private PropertyContainer dropTarget = null;
    private GuiElement<?> cancelZone = null;
    private final ConfigurableItemGui gui;
    private Rectangle prevBounds = Rectangle.create(0, 0, 0, 0);
    //
    public LinkedList<PropertyData> dataList = new LinkedList<>();
    public List<PropertyElement> propertyElements = new ArrayList<>();
//    public LinkedHashMap<PropertyData, PropertyElement> dataElementMap = new LinkedHashMap<>();

    public PropertyContainer(@NotNull GuiParent<?> parent, @Nullable ConfigurableItemGui gui, boolean isGroup) {
        super(parent);
        this.enableCursors(true);
//        isGroup = true;

        constrain(GeoParam.WIDTH, literal(150));
        constrain(GeoParam.HEIGHT, literal(22 + (isGroup ? 15 : 2)));

        this.gui = gui;
        //        this.parent = gui == null ? null : gui.advancedContainer;
        this.isGroup = isGroup;
//        setSize(150, getMinSize().height);
        if (isGroup) {
//            setResizeBorders(3);
            addResizeHandles(3, true);
        } else {
            addLeftHandle(1);
            addRightHandle(1);
//            setHResizeBorders(1);
        }
//        setCanResizeV(() -> !collapsed);
//        setEnableCursors(true);
//        this.setCapturesClicks(true);

        constructElement();
        setupDragAndDrop();


        Rectangle min = getMinSize();
        Rectangle max = getMaxSize();
        if (yMax - yMin < min.height()) yMax = yMin + (int) min.height();
        if (yMax - yMin > max.height()) yMax = yMin + (int) max.height();
        if (yMax > scaledScreenHeight()) yMax = scaledScreenHeight();
        if (xMax - xMin < min.width()) xMax = xMin + (int) min.width();
        if (xMax - xMin > max.width()) xMax = xMin + (int) max.width();
        if (xMax > scaledScreenWidth()) xMax = scaledScreenWidth();

        if (gui != null) {
            gui.newContainer(this);
            setResetOnUiInit(false);

//        .setEnabledCallback(() -> advancedUI || DEConfig.configUiEnableAdvancedXOver)
            setEnabled(() -> gui.advancedUI || DEConfig.configUiEnableAdvancedXOver);
        }
    }

    @Override
    public void onScreenInit(Minecraft mc, Font font, int screenWidth, int screenHeight) {
        super.onScreenInit(mc, font, screenWidth, screenHeight);
        validatePosition(false);
    }

    public PropertyContainer setPos(int xPos, int yPos) {
        int width = xMax - xMin;
        int height = yMax - yMin;
        xMin = xPos;
        xMax = xMin + width;
        yMin = yPos;
        yMax = yMin + height;
        return this;
    }

    private void constructElement() {
        GuiElement<?> root = getContentElement();
        new DropZoneRender(root);
        GuiTexture background = new GuiTexture(root, BCGuiTextures.themedGetter("borderless_bg_dynamic_small"))
                .dynamicTexture()
                .setColour(() -> isMoving() ? 0x60FFFFFF : 0xFFFFFFFF);
        Constraints.bind(background, root);


        scrollElement = new GuiScrolling(root)
                .setEnabled(() -> !isGroup || (scrollElement.ySize() > 10 && !applyPreset.isEnabled()))
                .constrain(LEFT, relative(root.get(LEFT), isGroup ? 3 : 1))
                .constrain(TOP, relative(root.get(TOP), isGroup ? 12 : 1))
                .constrain(BOTTOM, relative(root.get(BOTTOM), isGroup ? -3 : -1));
        scrollElement.constrain(RIGHT, relative(root.get(RIGHT), () -> isGroup ? scrollElement.hiddenSize(Axis.Y) > 0 ? -8D : -3D : -1D));
        scrollElement.getContentElement().constrain(WIDTH, null);
        scrollElement.getContentElement().constrain(RIGHT, match(scrollElement.get(RIGHT)));

        GuiRectangle contentBorder = new GuiRectangle(root)
                .setEnabled(() -> isGroup && getContentElement().ySize() - 4 - 9 > 0 && !applyPreset.isEnabled())
                .setShadeBottomRight(() -> (BCConfig.darkMode ? 0x5b5b5b : 0xFFFFFF) | (isMoving() ? 0x60000000 : 0xFF000000))
                .setShadeTopLeft(() -> (BCConfig.darkMode ? 0x282828 : 0x505050) | (isMoving() ? 0x60000000 : 0xFF000000))
                .setShadeCornersAuto();
        Constraints.bind(contentBorder, scrollElement, -1);
        contentBorder.constrain(RIGHT, relative(scrollElement.get(RIGHT), () -> scrollElement.hiddenSize(Axis.Y) > 0 ? 6D : 1D));

        //Group Specific

        GuiSlider bar = new GuiSlider(root, Axis.Y)
                .setEnabled(() -> scrollElement.hiddenSize(Axis.Y) > 0)
                .setSliderState(scrollElement.scrollState(Axis.Y))
                .setScrollableElement(scrollElement)
                .constrain(TOP, match(scrollElement.get(TOP)))
                .constrain(BOTTOM, match(scrollElement.get(BOTTOM)))
                .constrain(WIDTH, literal(5))
                .constrain(RIGHT, relative(contentBorder.get(RIGHT), -1));
        bar.installSlider(new GuiRectangle(bar).fill(() -> BCConfig.darkMode ? 0x80FFFFFF : 0xBB000000))
                .bindSliderWidth()
                .bindSliderLength();

        GuiButton toggleHidden = ConfigurableItemGui.TOOLKIT.createIconButton(root, 8, () -> BCGuiTextures.getThemed(collapsed ? "expand_content" : "collapse_content"))
                .setEnabled(isGroup)
                .onPress(this::toggleCollapsed)
                .setTooltipSingle(() -> presetMode ? Component.translatable("gui.draconicevolution.item_config.edit_preset.info") : Component.translatable("gui.draconicevolution.item_config." + (collapsed ? "expand_group" : "collapse_group") + ".info"));
        Constraints.placeInside(toggleHidden, root, Constraints.LayoutPos.TOP_LEFT, 2, 2);

        GuiButton dragZone = ConfigurableItemGui.TOOLKIT.createIconButton(root, 8, () -> Screen.hasShiftDown() ? BCGuiTextures.get("dark/copy") : Screen.hasControlDown() ? BCGuiTextures.get("delete") : BCGuiTextures.getThemed("reposition"))
                .setTooltip(() -> isMoving() ? Collections.emptyList() : Collections.singletonList(Component.translatable(Screen.hasShiftDown() ? "gui.draconicevolution.item_config.copy_group.info" : Screen.hasControlDown() ? "gui.draconicevolution.item_config.delete_group.info" : "gui.draconicevolution.item_config.move_group.info")))
                .onClick(() -> {
                    if (Screen.hasShiftDown()) {
                        duplicateContainer();
                    } else if (Screen.hasControlDown()) {
                        deleteContainer();
                    } else {
                        startDragging();
                    }
                }); //Do other stiff in here
        Constraints.placeInside(dragZone, root, Constraints.LayoutPos.TOP_RIGHT, -2, 2);

        GuiButton togglePreset = ConfigurableItemGui.TOOLKIT.createIconButton(root, 8, BCGuiTextures.themedGetter("preset_icon"))
                .setEnabled(isGroup)
                .setTooltip(Component.translatable("gui.draconicevolution.item_config.toggle_preset.info"))
                .onClick(this::togglePreset);
        Constraints.placeOutside(togglePreset, dragZone, Constraints.LayoutPos.MIDDLE_LEFT, -2, 0);
        Constraints.bind(ConfigurableItemGui.TOOLKIT.shadedBorder(togglePreset).setEnabled(() -> presetMode), togglePreset, -1);

        GuiButton globalBinding = ConfigurableItemGui.TOOLKIT.createIconButton(root, 8, BCGuiTextures.themedGetter("global_key_icon"))
                .setTooltip(Component.translatable("gui.draconicevolution.item_config.toggle_global_binding.info"))
                .setEnabled(() -> !boundKey.isEmpty() && presetMode)
                .onClick(() -> {
                    globalKeyBind = !globalKeyBind;
                    saveGui();
                });
        Constraints.placeOutside(globalBinding, togglePreset, Constraints.LayoutPos.MIDDLE_LEFT, -2, 0);
        Constraints.bind(ConfigurableItemGui.TOOLKIT.shadedBorder(globalBinding).setEnabled(() -> globalKeyBind), globalBinding, -1);

        groupName = new GuiTextField(root)
                .setEnabled(isGroup)
                .setTextState(TextState.simpleState(DEFAULT_NAME, s -> saveGui()))
                .setTextColor(BG::text)
                .setShadow(() -> BCConfig.darkMode)
                .constrain(TOP, relative(root.get(TOP), 2))
                .constrain(LEFT, relative(toggleHidden.get(RIGHT), 2))
                .constrain(HEIGHT, literal(8))
                .constrain(RIGHT, relative(globalBinding.get(LEFT), -1));

        GuiButton bindButton = ConfigurableItemGui.TOOLKIT.createBorderlessButton(this, this::getBindingName)
                .setEnabled(isGroup)
                .setTooltip(Component.translatable("gui.draconicevolution.item_config.set_key_bind.info"))
                .onPress(() -> {
                    boundKey = "";
                    modifier = KeyModifier.NONE;
                    binding = !binding;
                    saveGui();
                })
                .constrain(HEIGHT, literal(12))
                .constrain(TOP, relative(root.get(TOP), 11))
                .constrain(WIDTH, dynamic(() -> Math.min((root.xSize() - 3) / 2, font().width(getBindingName()) + 6)))
                .constrain(RIGHT, relative(root.get(RIGHT), -2));

        applyPreset = ConfigurableItemGui.TOOLKIT.createBorderlessButton(this, Component.translatable("gui.draconicevolution.item_config.apply_preset"))
                .setEnabled(() -> presetMode && collapsed/* && ySize() == getTargetHeight()*/)
                .onPress(this::applyPreset)
                .constrain(HEIGHT, literal(12))
                .constrain(LEFT, relative(root.get(LEFT), 2))
                .constrain(TOP, relative(root.get(TOP), 11))
                .constrain(RIGHT, match(bindButton.get(LEFT)));
        applyPreset.getLabel().setTextColour(() -> GuiToolkit.Palette.Ctrl.textH(applyPreset.isMouseOver()));
        bindButton.setEnabled(applyPreset::isEnabled);


        Constraints.bind(new GuiText(contentBorder, Component.translatable("gui.draconicevolution.item_config.drop_prop_here")).setEnabled(() -> dataList.isEmpty()).setTextColour(BG::text).setShadow(BCConfig.darkMode), contentBorder);
    }

    private void setupDragAndDrop() {
        setOnMovedCallback(finished -> {
            double mouseX = getModularGui().computeMouseX();
            double mouseY = getModularGui().computeMouseY();
            if (finished) {
                if (dropTarget != null && !dataList.isEmpty()) {
                    if (dropTarget.isGroup) {
                        getParent().removeChild(this);
                        gui.propertyContainers.remove(this);
                        boolean before = dropTargetElement != null && mouseY < (dropTargetElement.yMin() + (dropTargetElement.ySize() / 2D));
                        dropTarget.addProperty(dataList.get(0), dropTargetElement == null ? null : dropTargetElement.data, before);
                    } else if (dropTargetElement != null) {
                        getParent().removeChild(dropTarget);
                        getParent().removeChild(this);
                        gui.propertyContainers.remove(dropTarget);
                        gui.propertyContainers.remove(this);
                        gui.cancelAutoPos();
                        PropertyContainer newGroup = new PropertyContainer(getParent(), gui, true);
                        gui.resumeAutoPos();
                        newGroup.setPos((int) dropTargetElement.xMin() - 2, (int) dropTargetElement.yMin() - 12);
                        newGroup.xMax = (int) (newGroup.xMin + dropTargetElement.xSize() + 3);
                        newGroup.validatePosition(false);
                        newGroup.addProperty(dropTargetElement.data);
                        newGroup.addProperty(dataList.get(0), dropTargetElement.data, mouseY < (dropTargetElement.yMin() + (dropTargetElement.ySize() / 2D)));
                    } else {
                        dropTarget = null;
                    }
                }

                saveGui();
            } else {
                if (dropTarget != null) {
                    if (!dropTarget.getContentElement().isMouseOver()) {
                        dropTarget = null;
                        dropTargetElement = null;
                    } else {
                        dropTargetElement = dropTarget.propertyElements.stream()
                                .filter(GuiElement::isMouseOver)
                                .findAny()
                                .orElse(null);
                    }
                } else {
                    for (PropertyContainer element : gui.propertyContainers) {
                        if (element == this) continue;
                        Rectangle other = element.getContentElement().getRectangle();
                        if (!isGroup && GuiRender.isInRect(other.x() + 8, other.y() + 8, other.width() - 16, other.height() - 16, mouseX, mouseY)) {
                            dropTarget = element;
                            dropTargetElement = element.propertyElements.stream()
                                    .filter(GuiElement::isMouseOver)
                                    .findAny()
                                    .orElse(null);
                        }
                    }
                }
            }
        });

        setOnResizedCallback(finished -> {
            if (!finished) return;
            customHeight = yMax - yMin != (int) getMaxSize().height();
        });

        PositionRestraint screenRestraint = positionRestraint;
        setPositionRestraint(draggable -> {
            screenRestraint.restrainPosition(draggable);
            if (!DEConfig.configUiEnableSnapping) return;
            Rectangle newPos = getContentElement().getRectangle();
            Rectangle originalPos = Rectangle.create(newPos.x(), newPos.y(), newPos.width(), newPos.height());

            for (PropertyContainer element : gui.propertyContainers) {
                if (element == this) continue;
                Rectangle other = element.getContentElement().getRectangle();
                if (newPos.intersects(other) && !prevBounds.intersects(other)) {
                    if (!isGroup && GuiRender.isInRect(other.x() + 8, other.y() + 8, other.width() - 16, other.height() - 16, getModularGui().computeMouseX(), getModularGui().computeMouseY())) {
                        setPos((int) originalPos.x(), (int) originalPos.y());
                        return;
                    }

                    Rectangle intersection = newPos.intersect(other);
                    if (intersection.width() / newPos.width() < intersection.height() / newPos.height()) {
                        int move = (int) (intersection.width() * (prevBounds.x() < other.x() ? -1 : 1));
                        xMin += move;
                        xMax += move;
                    } else {
                        int move = (int) (intersection.height() * (prevBounds.y() < other.y() ? -1 : 1));
                        yMin += move;
                        yMax += move;
                    }
                }
            }
        });
    }

//    @Override
//    protected void onFinishManipulation(double mouseX, double mouseY) {
//        if (removed) return;
//        updatePosition();
//        parent.modularGui.getManager().removeChild(this);
//        parent.addChild(this);
//        displayZLevel = 0;
//        modifyZOffset(-300);
//        semiTrans = false;
//        if (!collapsed) {
//            prevUserHeight = ySize();
//        }
//
//        if (dropTarget != null && !dataList.isEmpty()) {
//            if (dropTarget.isGroup) {
//                parent.removeChild(this);
//                gui.propertyContainers.remove(this);
//                boolean before = dropTargetElement != null && mouseY < (dropTargetElement.yPos() + (dropTargetElement.ySize() / 2D));
//                dropTarget.addProperty(dataList.get(0), dropTargetElement == null ? null : dropTargetElement.data, before);
//            } else if (dropTargetElement != null) {
//                parent.removeChild(dropTarget);
//                parent.removeChild(this);
//                gui.propertyContainers.remove(dropTarget);
//                gui.propertyContainers.remove(this);
//                PropertyContainer newGroup = new PropertyContainer(gui, true);
//                newGroup.setRelPos(dropTargetElement, -2, -12).setXSize(dropTargetElement.xSize());
//                newGroup.updatePosition();
//                gui.propertyContainers.add(newGroup);
//                parent.addChild(newGroup);
//                newGroup.addProperty(dropTargetElement.data);
//                newGroup.addProperty(dataList.get(0), dropTargetElement.data, mouseY < (dropTargetElement.yPos() + (dropTargetElement.ySize() / 2D)));
//            } else {
//                dropTarget = null;
//            }
//        }
//
//        gui.savePropertyConfig();
//    }


    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (isMoving() || isResizing()) {
            gui.root.clearGeometryCache();
            prevBounds = Rectangle.create(xMin, yMin, xMax - xMin, yMax - yMin);
        }
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button, boolean consumed) {
        if (isMoving() && (gui.deleteZone.isMouseOver() || (cancelZone != null && cancelZone.isMouseOver()))) {
            deleteContainer();
            return true;
        }
        cancelZone = null;
        return super.mouseReleased(mouseX, mouseY, button, consumed);
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
        boolean willResize = ySize() == getMaxSize().height();
        reloadPropertyList(willResize);


        //        dataList.forEach(e -> scrollElement.addElement(dataElementMap.computeIfAbsent(e, this::createElementFor)));
//        if (willResize) {
//            setYSize(getMaxSize().height);
//            prevUserHeight = expandedHeight = getMaxSize().height;
//            reloadElement();
//            scrollElement.reloadElement();
//        }
    }

    private void reloadPropertyList(boolean resize) {
//        dataElementMap.values().forEach(scrollElement.getContentElement()::removeChild);
        propertyElements.forEach(e -> e.getParent().removeChild(e));
        propertyElements.clear();
        GuiElement<?> content = scrollElement.getContentElement();

        double yOffset = 0;
        int i = 0;
        for (PropertyData data : dataList) {
            PropertyElement element = createPropertyElement(content, data, i)
                    .constrain(LEFT, match(content.get(LEFT)))
                    .constrain(RIGHT, match(content.get(RIGHT)))
                    .constrain(TOP, relative(content.get(TOP), yOffset));
            propertyElements.add(element);
            yOffset += element.ySize();
            i++;
        }


    }

    private PropertyElement createPropertyElement(GuiElement<?> parent, PropertyData data, int index) {
        PropertyElement element = new PropertyElement(parent, data, gui, index, true);
        element.setOpacitySupplier(() -> isMoving() ? 0x60000000 : 0xFF000000);
        element.setEnableToolTip(() -> !isMoving() && !isResizing());
        data.setChangeListener(() -> {
            if (!presetMode) {
                applyData(data);
                gui.updateAnimations.add(new UpdateAnim(data));
            }
        });

        //TODO I think this is redundant?
        GuiButton dragZone = ConfigurableItemGui.TOOLKIT.createIconButton(element, 8, () -> Screen.hasShiftDown() ? BCGuiTextures.get("dark/copy") : Screen.hasControlDown() ? BCGuiTextures.get("delete") : BCGuiTextures.get("reposition_gray"))
                .setTooltip(() -> isMoving() ? Collections.emptyList() : Collections.singletonList(Component.translatable(Screen.hasShiftDown() ? "gui.draconicevolution.item_config.copy_group.info" : Screen.hasControlDown() ? "gui.draconicevolution.item_config.delete_group.info" : "gui.draconicevolution.item_config.move_group.info")));
        Constraints.placeInside(dragZone, element, Constraints.LayoutPos.TOP_RIGHT, -1, 1);

        if (!isGroup) {
            //Ahh so drag zone is not really a thing in this case?
            dragZone.setEnabled(false);
//            dragZone.onClick(this::startDragging); //TODO, i can just do copy/delete here!
            setMoveHandle(dragZone);
            setEnabled(() -> (gui.advancedUI || DEConfig.configUiEnableAdvancedXOver) && (DEConfig.configUiShowUnavailable || (data.isGlobal || data.isPropertyAvailable())));

        } else {
            element.setEnabled(() -> DEConfig.configUiShowUnavailable || (data.isGlobal || data.isPropertyAvailable()));
            dragZone.onClick(() -> {

                if (!Screen.hasShiftDown()) {
                    dataList.remove(data);
                    reloadPropertyList(false);
                }
                if (Screen.hasShiftDown() || !Screen.hasControlDown()) {
                    PropertyContainer copy = new PropertyContainer(gui.root, gui, false);
                    copy.addProperty(data.copy());
                }
            });
        }

        return element;
    }

    @Override
    public void tick(double mouseX, double mouseY) {
        super.tick(mouseX, mouseY);

        if (!isMoving() && !isResizing() && !customHeight) {
            int targetHeight = (int) getMaxSize().height();
            if (yMax - yMin != targetHeight) {
                yMax = yMin + targetHeight;
                validatePosition(false);
                saveGui();
            }
        }
    }

    @Override
    public Rectangle getMinSize() {
        return Rectangle.create(0, 0, 80, 22 + (isGroup ? 15 : 2));
    }

    @Override
    public Rectangle getMaxSize() {
        int count = DEConfig.configUiShowUnavailable ? dataList.size() : (int) dataList.stream().filter(PropertyData::isPropertyAvailable).count();
        int maxY = MathHelper.clip((count * 22) + (isGroup ? 15 : 2), 24, 300);
        return Rectangle.create(0, 0, 250, collapsed ? presetMode ? 25 : 12 : maxY);
    }

    @Override
    public void startDragging() {
        super.startDragging();
        ((GuiElement<?>) getParent()).bringChildToForeground(this);
        gui.propertyContainers.remove(this);
        gui.propertyContainers.add(this);
    }

    public void setCancelZone(GuiElement<?> cancelZone) {
        this.cancelZone = cancelZone;
    }

    public void deleteContainer() {
        getParent().removeChild(this);
        gui.propertyContainers.remove(this);
        saveGui();
    }

    public void duplicateContainer() {
        PropertyContainer newGroup = PropertyContainer.deserialize(gui, getParent(), serialize());
    }

    @Override
    public boolean keyPressed(int key, int scancode, int modifiers) {
        if (binding) {
            modifier = KeyModifier.NONE;
            if (key == InputConstants.KEY_ESCAPE) {
                boundKey = "";
                binding = false;
                saveGui();
                return true;
            }

            KeyModifier mods = KeyModifier.getActiveModifier();
            InputConstants.Key input = InputConstants.getKey(key, scancode);
            boundKey = input.toString();
            if (!mods.matches(input)) {
                modifier = mods;
            }
            return true;
        }
        return super.keyPressed(key, scancode, modifiers);
    }

    @Override
    public boolean keyReleased(int key, int scancode, int modifiers) {
        if (binding) {
            saveGui();
            binding = false;
            return true;
        }
        return super.keyReleased(key, scancode, modifiers);
    }

    private Component getBindingName() {
        if (binding) {
            return Component.literal(">").append(boundKey.isEmpty() ? Component.literal("   ") : InputConstants.getKey(boundKey).getDisplayName()).append("<");
        } else if (boundKey.isEmpty()) {
            return Component.translatable("gui.draconicevolution.item_config.not_bound");
        }
        InputConstants.Key keyCode = InputConstants.getKey(boundKey);
        return modifier.getCombinedName(keyCode, keyCode::getDisplayName);
    }

    private void togglePreset() {
        presetMode = !presetMode;
        collapsed = presetMode;
        saveGui();
    }

    private void toggleCollapsed() {
        collapsed = !collapsed;
        if (!collapsed) {
            ((GuiElement<?>) getParent()).bringChildToForeground(this);
            gui.propertyContainers.remove(this);
            gui.propertyContainers.add(this);
        }
        saveGui();
    }

    public void applyPreset() {
        dataList.forEach(this::applyData);
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        List<ItemStack> effectedItems = ConfigurableItemMenu.getStackProviders(ConfigurableItemMenu.getPlayerInventory(player.getInventory()))
                .filter(e -> dataList.stream().anyMatch(data -> data.getPropIfApplicable(e.value()) != null))
                .map(Pair::key)
                .toList();

        MutableComponent message = Component.translatable("gui.draconicevolution.item_config.preset_applied.msg", Component.literal(groupName.getValue()).withStyle(ChatFormatting.GOLD)).withStyle(ChatFormatting.BLUE);
        if (effectedItems.isEmpty()) {
            message.append(Component.translatable("gui.draconicevolution.item_config.no_items_for_preset.msg"));
        } else {
            for (int i = 0; i < effectedItems.size(); i++) {
                ItemStack stack = effectedItems.get(i);
                Component stackComp = stack.getDisplayName().copy();
                stackComp.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(stack)));
                message.append(stackComp);
                if (i < effectedItems.size() - 1) {
                    message.append(", ");
                }
            }
        }

        BrandonsCore.proxy.sendIndexedMessage(Minecraft.getInstance().player, message, PRESET_CHAT_SIG);
    }

    private void applyData(PropertyData data) {
        data.sendToServer();
        if (gui != null) {
            gui.updateAnimations.add(new UpdateAnim(data));
        }
    }

    //
    public void inventoryUpdate() {
        dataList.forEach(e -> e.pullData(gui.menu, !presetMode && !e.isGlobal));
    }

    //
//    boolean isCopy = false;
//
//    @Override
//    protected boolean onStartMove(double mouseX, double mouseY) {
//        if (dragZone.validate(mouseX, mouseY) && !isCopy) {
//            if (Screen.hasShiftDown()) {
//                PropertyContainer newGroup = new PropertyContainer(gui, isGroup);
//                newGroup.isCopy = true;
//                gui.propertyContainers.add(newGroup);
//                gui.advancedContainer.addChild(newGroup);
//                dataList.forEach(e -> newGroup.addProperty(e.copy()));
//                newGroup.expandedHeight = expandedHeight;
//                newGroup.prevUserHeight = prevUserHeight;
//                newGroup.isPreset = isPreset;
//                newGroup.collapsed = collapsed;
//                newGroup.globalKeyBind = globalKeyBind;
//                if (isGroup){
//                    newGroup.groupName.setValue(groupName.getValue());
//                }
//                newGroup.setSize(this);
//                newGroup.setMaxXPos((int) mouseX + 5, false).setYPos((int) mouseY - 5);
//                newGroup.updatePosition();
//                newGroup.startDragging();
//                return true;
//            }
//            else if (Screen.hasControlDown()) {
//                removed = true;
//                parent.removeChild(this);
//                gui.propertyContainers.remove(this);
//                return true;
//            }
//        }
//        isCopy = false;
//        return false;
//    }
//
//    @Override
//    protected boolean onStartManipulation(double mouseX, double mouseY) {
//        parent.removeChild(this);
//        parent.modularGui.getManager().addChild(this, 300, false);
//        modifyZOffset(300);
//
//        //Move this container to the end of the list so it maintains its render position across save / load cycles.
//        gui.propertyContainers.remove(this);
//        gui.propertyContainers.add(this);
//        if (dragPos) {
//            semiTrans = true;
//        }
//        return false;
//    }
//

    //
//
//    @Override
//    public Dimension getMinSize() {
//        return new Dimension(80, 22 + (isGroup ? 15 : 2));
//    }
//
//    @Override
//    public Dimension getMaxSize() {
//        int count = DEConfig.configUiShowUnavailable ? dataElementMap.size() : (int) dataElementMap.keySet().stream().filter(PropertyData::isPropertyAvailable).count();
//        int maxY = MathHelper.clip((count * 22) + (isGroup ? 15 : 2), 24, 300);
//        return new Dimension(250, maxY);
//    }
//
//    @Override
//    protected void validateMove(Rectangle previous, double mouseX, double mouseY) {
//        if (!DEConfig.configUiEnableSnapping) return;
//        Rectangle newPos = getRect();
//        Rectangle originalPos = new Rectangle(newPos);
//        for (PropertyContainer element : gui.propertyContainers) {
//            if (element == this) continue;
//            Rectangle other = element.getRect();
//            if (newPos.intersects(other) && !previous.intersects(other)) {
//                if (!isGroup && GuiHelperOld.isInRect(other.x + 8, other.y + 8, other.width - 16, other.height - 16, mouseX, mouseY)) {
//                    setPos(originalPos.x, originalPos.y);
//                    return;
//                }
//
//                Rectangle2D intersection = newPos.createIntersection(other);
//                if (intersection.getWidth() / (double) newPos.width < intersection.getHeight() / (double) newPos.height) {
//                    translate((int) (intersection.getWidth() * (previous.x < other.x ? -1 : 1)), 0);
//                } else {
//                    translate(0, (int) (intersection.getHeight() * (previous.y < other.y ? -1 : 1)));
//                }
//                newPos = getRect();
//            }
//        }
//    }
//
//    private int getTargetHeight() {
//        if (!isDragging && prevUserHeight != expandedHeight && prevUserHeight <= getMaxSize().height) {
//            expandedHeight = prevUserHeight;
//        }
//        if (expandedHeight == 0) {
//            expandedHeight = ySize();
//        } else if (expandedHeight > getMaxSize().height) {
//            expandedHeight = getMaxSize().height;
//        }
//        return collapsed ? isPreset ? 25 : 12 : expandedHeight;
//    }
//
//    private float animHeight = 0;
//    private float animDistance = 0;
//
//    @Override
//    public boolean renderOverlayLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
//        PoseStack poseStack = new PoseStack();
//        poseStack.translate(0, 0, getRenderZLevel());
//        if (dropTarget != null && timeSinceMove > 10) {
//            if (!dropTarget.isGroup) {
//                renderTooltip(poseStack, Component.translatable("gui.draconicevolution.item_config.drop_create_group.info"), mouseX, mouseY);
//            } else {
//                renderTooltip(poseStack, Component.translatable("gui.draconicevolution.item_config.add_to_group.info"), mouseX, mouseY);
//            }
//            return true;
//        }
//        if (dragPos && gui.deleteZone.isMouseOver(mouseX, mouseY)) {
//            renderTooltip(poseStack, Component.translatable("gui.draconicevolution.item_config.drop_to_delete.info"), mouseX, mouseY);
//        }
//
//        return super.renderOverlayLayer(minecraft, mouseX, mouseY, partialTicks);
//    }
//
//    @Override
//    public void renderElement(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
//        int targetHeight = getTargetHeight();
//        if (targetHeight != ySize()) {
//            if (animDistance == 0) {
//                animDistance = targetHeight - ySize();
//                animHeight = ySize();
//                //No need for a drawn out animation for a tiny adjustment.
//                if (Math.abs(animDistance) <= 3) {
//                    setYSize((int) (animHeight = targetHeight));
//                    animDistance = 0;
//                }
//            }
//            animHeight = MathHelper.approachLinear(animHeight, targetHeight, Math.abs(animDistance) * 0.15F * partialTicks);
//            if (animDistance > 0 && animHeight > targetHeight) animHeight = targetHeight;
//            else if (animDistance < 0 && animHeight < targetHeight) animHeight = targetHeight;
//            setYSize((int) animHeight);
//            reloadElement();
//            if (animHeight == targetHeight) {
//                scrollElement.reloadElement();
//            }
//        } else {
//            animHeight = targetHeight;
//            animDistance = 0;
//        }
//
//        MultiBufferSource.BufferSource getter = RenderUtils.getGuiBuffers();
//
//        if (dropTarget != null) {
//            double zLevel = getRenderZLevel() - 10;
//            zOffset -= zLevel;
//            if (dropTargetElement != null) {
//                Rectangle rect = dropTargetElement.getRect();
//                if (mouseY < rect.y + (rect.height / 2D)) {
//                    drawGradientRect(getter, rect.x, rect.y, rect.x + rect.width, rect.y + 6, 0xFF00FF00, 0x0000FF00);
//                } else {
//                    drawGradientRect(getter, rect.x, rect.y + rect.height - 6, rect.x + rect.width, rect.y + rect.height, 0x0000FF00, 0xFF00FF00);
//                }
//            } else {
//                drawGradientRect(getter, dropTarget.xPos() + 3, dropTarget.yPos() + 13, dropTarget.maxXPos() - 3, dropTarget.yPos() + 15, 0xFF00FF00, 0x0000FF00);
//                drawGradientRect(getter, dropTarget.xPos() + 3, dropTarget.maxYPos() - 6, dropTarget.maxXPos() - 3, dropTarget.maxYPos() - 3, 0x0000FF00, 0xFF00FF00);
//            }
//            zOffset += zLevel;
//            getter.endBatch();
//        }
//
////        int alpha = semiTrans ? 0x60000000 : 0xFF000000;
////        Material mat = BCGuiTextures.getThemed("borderless_bg_dynamic_small");
////        drawDynamicSprite(getter.getBuffer(mat.renderType(e -> BCGuiTextures.GUI_TYPE)), mat.sprite(), xPos(), yPos(), xSize(), ySize(), 2, 2, 2, 2, 0xFFFFFF | alpha);
//        getter.endBatch();
//
//        int contentPos = yPos() + 2 + 9;
//        int contentHeight = ySize() - 4 - 9;
//        if (isGroup && contentHeight > 0 && !applyPreset.isEnabled()) {
//            int light = (BCConfig.darkMode ? 0x5b5b5b : 0xFFFFFF) | alpha;
//            int dark = (BCConfig.darkMode ? 0x282828 : 0x505050) | alpha;
//            drawShadedRect(getter, xPos() + 2, contentPos, xSize() - 4, contentHeight, 1, 0, dark, light, midColour(light, dark));
//            getter.endBatch();
//            if (dataList.isEmpty()) { //TODO Test this???
//                drawCustomString(fontRenderer, Component.translatable("gui.draconicevolution.item_config.drop_prop_here"), xPos() + 3, yPos() + 13, xSize() - 6, GuiToolkit.Palette.BG.text(), GuiAlign.CENTER, false, false, true, BCConfig.darkMode);
//                getter.endBatch();
//            }
//        }
//
//        super.renderElement(minecraft, mouseX, mouseY, partialTicks);
//
//        if (dragPos && gui.deleteZone.isMouseOver(mouseX, mouseY)) {
//            drawColouredRect(getter, xPos() + 1, yPos() + 1, xSize() - 2, ySize() - 3, 0x80FF8080);
//            getter.endBatch();
//        }
//    }
//
//    @Override
//    public boolean onUpdate() {
//        timeSinceMove++;
//        return super.onUpdate();
//    }
//
//    public void updatePosition() {
//        setXPos = xPos();
//        setYPos = yPos();
//    }
//
    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("group", isGroup);
        if (isGroup) {
            nbt.putBoolean("preset", presetMode);
            nbt.putBoolean("collapsed", collapsed);
            nbt.putString("name", groupName.getValue());
//            nbt.putInt("user_height", prevUserHeight);
            nbt.putBoolean("global_key", globalKeyBind);
            if (!boundKey.isEmpty()) {
                nbt.putString("binding", boundKey);
                nbt.putInt("modifier", modifier.ordinal());
            }
        }
        nbt.putInt("x_min", xMin);
        nbt.putInt("x_max", xMax);
        nbt.putInt("y_min", yMin);
        nbt.putInt("y_max", yMax);
        nbt.putBoolean("custom_height", customHeight);
//        nbt.putInt("x_size", xSize());
//        nbt.putInt("y_size", expandedHeight);
        nbt.put("data", dataList.stream()
                .map(PropertyData::serialize)
                .collect(Collectors.toCollection(ListTag::new)));

        return nbt;
    }

    //
    public static PropertyContainer deserialize(ConfigurableItemGui gui, GuiParent<?> parent, CompoundTag nbt) {
        boolean isGroup = nbt.getBoolean("group");
        PropertyContainer container = new PropertyContainer(parent, gui, isGroup);
        if (isGroup) {
            container.presetMode = nbt.getBoolean("preset");
            container.collapsed = nbt.getBoolean("collapsed");
            container.groupName.getTextState().setText(nbt.getString("name"));
//            container.prevUserHeight = nbt.getInt("user_height");
            container.boundKey = nbt.getString("binding");
            container.modifier = KeyModifier.values()[nbt.getInt("modifier")];
            container.globalKeyBind = nbt.getBoolean("global_key");
        }
        container.xMin = nbt.getInt("x_min");
        container.xMax = nbt.getInt("x_max");
        container.yMin = nbt.getInt("y_min");
        container.yMax = nbt.getInt("y_max");
        container.customHeight = nbt.getBoolean("custom_height");
//        container.expandedHeight = nbt.getInt("y_size");
//        container.setYSize(container.expandedHeight);

        container.dataList.addAll(nbt.getList("data", 10).stream()
                .map(e -> (CompoundTag) e)
                .map(PropertyData::deserialize)
                .filter(Objects::nonNull)
                .toList()
        );

        container.reloadPropertyList(false);
        return container;
    }

    private void saveGui() {
        if (gui != null) {
            gui.saveInterfaceState();
        }
    }

    public class DropZoneRender extends GuiElement<DropZoneRender> implements ForegroundRender {
        public DropZoneRender(@NotNull GuiParent<?> parent) {
            super(parent);
            Constraints.bind(this, (GuiElement<?>) parent);
        }

        @Override
        public void renderForeground(GuiRender render, double mouseX, double mouseY, float partialTicks) {
            if (dropTarget != null) {
                if (dropTargetElement != null) {
                    Rectangle rect = dropTargetElement.getRectangle();
                    if (mouseY < rect.y() + (rect.height() / 2D)) {
                        render.gradientFillV(rect.x(), rect.y(), rect.x() + rect.width(), rect.y() + 6, 0xFF00FF00, 0x0000FF00);
                    } else {
                        render.gradientFillV(rect.x(), rect.y() + rect.height() - 6, rect.x() + rect.width(), rect.y() + rect.height(), 0x0000FF00, 0xFF00FF00);
                    }
                } else {
                    GuiElement<?> ce = dropTarget.getContentElement();
                    render.gradientFillV(ce.xMin() + 3, ce.yMin() + 13, ce.xMax() - 3, ce.yMin() + 15, 0xFF00FF00, 0x0000FF00);
                    render.gradientFillV(ce.xMin() + 3, ce.yMax() - 6, ce.xMax() - 3, ce.yMax() - 3, 0x0000FF00, 0xFF00FF00);
                }
            }
        }
    }
}
