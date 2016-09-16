package com.brandon3055.draconicevolution.client.gui.modwiki.moddata.displaycontent;

import com.brandon3055.brandonscore.client.gui.modulargui.MGuiElementBase;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.EnumAlignment;
import com.brandon3055.brandonscore.client.gui.modulargui.modularelements.*;
import com.brandon3055.brandonscore.lib.StackReference;
import com.brandon3055.draconicevolution.client.gui.modwiki.GuiModWiki;
import com.brandon3055.draconicevolution.client.gui.modwiki.guielements.StackSelector;
import com.brandon3055.draconicevolution.client.gui.modwiki.moddata.guidoctree.TreeBranchRoot;
import com.google.common.base.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.TextFormatting;
import org.w3c.dom.Element;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by brandon3055 on 8/09/2016.
 */
public class DCStack extends DisplayComponentBase {

    public static final String ATTRIB_SCALE = "scale";
    public static final String ATTRIB_TIP = "tooltip";
    public static final String ATTRIB_SLOT = "renderSlot";

    public MGuiStackIcon stackIcon;
    private MGuiElementBase iconBackground;
    public int scale = 100;
    public boolean toolTip = true;
    public boolean renderSlot = false;
    public String stackString;
    private StackSelector selector;

    public DCStack(GuiModWiki modularGui, String componentType, TreeBranchRoot branch) {
        super(modularGui, componentType, branch);
        ySize = 20;
        stackIcon = new MGuiStackIcon(modularGui, 0, 0, 18, 18, new StackReference("null"));
        addChild(stackIcon);
        iconBackground = new MGuiSlotRender(modularGui);
        stackIcon.setBackground(iconBackground);
    }

    //region List

    @Override
    public void setXSize(int xSize) {
        super.setXSize(xSize);
        int size = Math.min(xSize - 4, (int) ((scale / 100D) * 18D));
        stackIcon.ySize = stackIcon.xSize = size;
        iconBackground.setEnabled(renderSlot);
        iconBackground.xSize = iconBackground.ySize = size;
        iconBackground.yPos = stackIcon.yPos;

        int xOffset = 0;
        switch (alignment) {
            case LEFT:
                xOffset = 2;
                break;
            case CENTER:
                xOffset = (xSize / 2) - (size / 2);
                break;
            case RIGHT:
                xOffset = xSize - size - 2;
                break;
        }

        stackIcon.xPos = xPos + xOffset;
        iconBackground.xPos = stackIcon.xPos;
        ySize = stackIcon.ySize;
    }


    //endregion

    //region Edit

    @Override
    public LinkedList<MGuiElementBase> getEditControls() {
        LinkedList<MGuiElementBase> list = super.getEditControls();

        list.add(new MGuiButtonSolid(modularGui, "TOGGLE_ALIGN", 0, 0, 26, 12, "Align"){
            @Override
            protected int getBorderColour(boolean hovering, boolean disabled) {
                return hovering ? 0xFF00FF00 : 0xFFFF0000;
            }
        }.setListener(this).setToolTip(new String[]{"Toggle Horizontal Alignment"}));

        list.add(new MGuiButtonSolid(modularGui, "SELECT_STACK", 0, 0, 56, 12, "Pick Stack"){
            @Override
            protected int getBorderColour(boolean hovering, boolean disabled) {
                return hovering ? 0xFF00FF00 : 0xFFFF0000;
            }
        }.setListener(this).setToolTip(new String[] {"Select a stack from your inventory"}));

        String s = "Turn ToolTip: " + (toolTip ? "Off" : "On");
        list.add(new MGuiButtonSolid(modularGui, "TOGGLE_TOOLTIP", 0, 0, fontRenderer.getStringWidth(s) + 4, 12, s){
            @Override
            protected int getBorderColour(boolean hovering, boolean disabled) {
                return hovering ? 0xFF00FF00 : 0xFFFF0000;
            }
        }.setListener(this).setToolTip(new String[] {"Toggle item tool tip on or off"}));

        s = "Turn Slot: " + (toolTip ? "Off" : "On");
        list.add(new MGuiButtonSolid(modularGui, "TOGGLE_SLOT", 0, 0, fontRenderer.getStringWidth(s) + 4, 12, s){
            @Override
            protected int getBorderColour(boolean hovering, boolean disabled) {
                return hovering ? 0xFF00FF00 : 0xFFFF0000;
            }
        }.setListener(this).setToolTip(new String[] {"Toggle Slot Renderer on or off"}));

        list.add(new MGuiLabel(modularGui, 0, 0, 30, 12, "Scale:").setAlignment(EnumAlignment.CENTER));
        MGuiTextField scaleField = new MGuiTextField(modularGui, 0, 0, 36, 12, fontRenderer).setListener(this).setMaxStringLength(2048).setText(String.valueOf(scale));
        scaleField.addChild(new MGuiHoverPopup(modularGui, new String[] {"Set the stack scale (100 = normal stack size)", TextFormatting.GOLD + "The size of the image will be limited by both this and the width of the GUI.", TextFormatting.GOLD + "Whichever value is smaller will take priority.", TextFormatting.GREEN + "Will save as you type."}, scaleField));
        scaleField.setId("SCALE");
        scaleField.setValidator(new Predicate<String>() {
            @Override
            public boolean apply(@Nullable String input) {
                try {
                    Integer.parseInt(input);
                }
                catch (Exception e) {}
                return true;
            }
        });
        list.add(scaleField);

        return list;
    }

    @Override
    public void onMGuiEvent(String eventString, MGuiElementBase eventElement) {
        super.onMGuiEvent(eventString, eventElement);


        if (eventElement.id.equals("SCALE") && eventString.equals("TEXT_FIELD_CHANGED") && eventElement instanceof MGuiTextField) {
            if (StringUtils.isNullOrEmpty(((MGuiTextField) eventElement).getText())) {
                return;
            }

            int newScale = 1;
            try {
                newScale = Integer.parseInt(((MGuiTextField) eventElement).getText());
            }
            catch (Exception e) {}

            if (newScale < 30) {
                newScale = 30;
            }

            element.setAttribute(ATTRIB_SCALE, String.valueOf(newScale));
            int pos = ((MGuiTextField) eventElement).getCursorPosition();
            save();

            for (MGuiElementBase element : branch.guiWiki.contentWindow.editControls) {
                if (element instanceof MGuiTextField && element.id.equals("SCALE")) {
                    ((MGuiTextField) element).setFocused(true);
                    ((MGuiTextField) element).setCursorPosition(pos);
                    break;
                }
            }
        }
        else if (eventElement instanceof MGuiButtonSolid && ((MGuiButtonSolid) eventElement).buttonName.equals("TOGGLE_TOOLTIP")) {
            element.setAttribute(ATTRIB_TIP, String.valueOf(!toolTip));
            save();
        }
        else if (eventElement instanceof MGuiButtonSolid && ((MGuiButtonSolid) eventElement).buttonName.equals("TOGGLE_SLOT")) {
            element.setAttribute(ATTRIB_SLOT, String.valueOf(!renderSlot));
            save();
        }
        else if (eventElement instanceof MGuiButtonSolid && ((MGuiButtonSolid) eventElement).buttonName.equals("SELECT_STACK")) {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            selector = new StackSelector(modularGui, list.xPos + list.leftPadding, list.yPos + list.topPadding, list.xSize - list.leftPadding - list.rightPadding, list.ySize - list.topPadding - list.bottomPadding);
            selector.setListener(this);

            List<ItemStack> stacks = new LinkedList<ItemStack>();
            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                ItemStack stack = player.inventory.getStackInSlot(i);
                if (stack != null) {
                    stacks.add(stack);
                }
            }
            selector.setStacks(stacks);
            selector.addChild(new MGuiButtonSolid(modularGui, "CANCEL_PICK", selector.xPos + selector.xSize - 42, selector.yPos + selector.ySize - 22, 40, 20, "Cancel").setListener(this).setId("CANCEL_PICK"));
            selector.initElement();
            modularGui.getManager().add(selector, 2);
        }
        else if (eventElement.id.equals("CANCEL_PICK") && selector != null) {
            modularGui.getManager().remove(selector);
        }
        else if (eventString.equals("SELECTOR_PICK")) {
            boolean shouldSave = false;
            if (eventElement instanceof MGuiStackIcon) {
                StackReference reference = new StackReference(((MGuiStackIcon)eventElement).getStack());
                stackIcon.setStack(reference);
                element.setTextContent(reference.toString());
                shouldSave = true;
            }

            modularGui.getManager().remove(selector);
            if (shouldSave) {
                save();
            }
        }

    }

    @Override
    public void onCreated() {
        element.setAttribute(ATTRIB_SCALE, "100");
        element.setAttribute(ATTRIB_TIP, "true");
        element.setAttribute(ATTRIB_SLOT, "true");
    }

    //endregion

    //region XML & Factory

    @Override
    public void loadFromXML(Element element) {
        super.loadFromXML(element);
        toolTip = !element.hasAttribute(ATTRIB_TIP) || Boolean.parseBoolean(element.getAttribute(ATTRIB_TIP));
        renderSlot = !element.hasAttribute(ATTRIB_SLOT) || Boolean.parseBoolean(element.getAttribute(ATTRIB_SLOT));
        stackString = element.getTextContent();
        stackIcon.setStack(StackReference.fromString(stackString));
        stackIcon.setToolTip(toolTip);
        iconBackground.setEnabled(renderSlot);

        try {
            scale = Integer.parseInt(element.getAttribute(ATTRIB_SCALE));
        }
        catch (Exception e) {}

        if (scale < 30) {
            scale = 30;
        }
    }

    public static class Factory implements IDisplayComponentFactory {
        @Override
        public DisplayComponentBase createNewInstance(GuiModWiki guiWiki, TreeBranchRoot branch) {
            DisplayComponentBase component = new DCStack(guiWiki, getID(), branch);
            component.setWorldAndResolution(guiWiki.mc, guiWiki.screenWidth(), guiWiki.screenHeight());
            return component;
        }

        @Override
        public String getID() {
            return "stack";
        }
    }

    //endregion
}
