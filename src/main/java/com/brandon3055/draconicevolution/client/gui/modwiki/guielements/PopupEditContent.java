package com.brandon3055.draconicevolution.client.gui.modwiki.guielements;

import com.brandon3055.brandonscore.client.gui.modulargui.IModularGui;
import com.brandon3055.brandonscore.client.gui.modulargui.MGuiElementBase;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.EnumAlignment;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.IMGuiListener;
import com.brandon3055.brandonscore.client.gui.modulargui.modularelements.*;
import com.brandon3055.brandonscore.lib.StackReference;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.client.gui.modwiki.moddata.WikiDocManager;
import com.brandon3055.draconicevolution.client.gui.modwiki.moddata.guidoctree.TreeBranchContent;
import com.brandon3055.draconicevolution.client.gui.modwiki.moddata.guidoctree.TreeBranchRoot;
import com.google.common.base.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;
import org.w3c.dom.Element;
import scala.actors.threadpool.Arrays;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.brandon3055.draconicevolution.client.gui.modwiki.moddata.WikiDocManager.*;

/**
 * Created by brandon3055 on 10/09/2016.
 */
public class PopupEditContent extends MGuiPopUpDialog implements IMGuiListener {

    private final TreeBranchContent branch;
    public MGuiTextField nameField;
    public MGuiColourPicker colourA;
    public MGuiColourPicker colourB;
    public MGuiButtonSolid okButton;
    public MGuiButtonSolid cancelButton;
    public MGuiButtonSolid deleteButton;
    public MGuiButtonSolid confirmYes;
    public MGuiButtonSolid confirmNo;
    public MGuiLabel errorLabel;
    public MGuiButtonSolid clearStack;
    public MGuiButtonSolid pickStack;
    public MGuiButtonSolid pickStackBack;
    public MGuiButtonSolid clearStackBack;
    public MGuiTextField stackString;
    public MGuiStackIcon stackIcon;
    public MGuiBorderedRect stackBack;
    private StackSelector selector;
    public MGuiTextField weightField;

    public PopupEditContent(IModularGui modularGui, int xPos, int yPos, int xSize, int ySize, MGuiList parent, TreeBranchContent branch) {
        super(modularGui, xPos, yPos, xSize, ySize, parent);
        this.branch = branch;
    }

    @Override
    public void initElement() {
        toRemove.addAll(childElements);
        //region Edit Name

        MGuiLabel nameLabel = new MGuiLabel(modularGui, xPos, yPos + 2, xSize, 12, "Edit Name").setAlignment(EnumAlignment.LEFT);
        addChild(nameLabel);
        addChild(nameField = new MGuiTextField(modularGui, xPos + 2, nameLabel.yPos + 12, xSize - 4, 16, modularGui.getMinecraft().fontRendererObj));
        nameField.setMaxStringLength(512);
        nameField.setText(branch.branchName);

        //endregion

        //region Edit Icon

        MGuiLabel iconLabel = new MGuiLabel(modularGui, xPos, nameField.yPos + 18, xSize, 12, "Edit Icon").setAlignment(EnumAlignment.LEFT);
        addChild(iconLabel);
        StackReference stack = branch.getDisplayStack();
        List<String> toolTip = new ArrayList<String>();
        if (stack == null) {
            toolTip.add("Display stack not set");
            stack = new StackReference(new ItemStack(Blocks.BARRIER));
        }
        addChild(stackString = new MGuiTextField(modularGui, xPos + 2, iconLabel.yPos + 12, xSize - 4, 16, modularGui.getMinecraft().fontRendererObj));
        stackString.setMaxStringLength(512);
        stackString.setText(toolTip.size() > 0 ? "" : stack.toString());
        addChild(stackIcon = new MGuiStackIcon(modularGui, xPos + 3, stackString.yPos + 18, 18, 18, stack));

        if (toolTip.size() > 0) {
            stackIcon.setToolTip(false);
        }
        stackIcon.addChild(stackBack = new MGuiBorderedRect(modularGui));
        stackBack.setBorderColour(0).setFillColour(0);
        addChild(pickStack = new MGuiButtonSolid(modularGui, "PICK_STACK", stackIcon.xPos + 20, stackString.yPos + 18, 24, 18, "Set"));
        addChild(clearStack = new MGuiButtonSolid(modularGui, "CLEAR_STACK", stackIcon.xPos + 45, stackString.yPos + 18, 32, 18, "Clear"));
        pickStack.setToolTip(Arrays.asList(new String[] {"Select an item from your inventory."})).setListener(this);
        pickStack.addChild(new MGuiButtonSolid(modularGui, "CANCEL_PICK", xPos + xSize - 48, yPos + ySize - 22, 40, 20, "Cancel").setListener(this));
        clearStack.setListener(this);

        //endregion

        //region misc

        addChild(okButton = (MGuiButtonSolid) new MGuiButtonSolid(modularGui, "OK", xPos + 2, stackIcon.yPos + 32, xSize - 4, 14, "Save & Exit").setColours(0xFF00a000, 0xFF000000, 0xFFFFFFFF).setListener(this));
        addChild(cancelButton = (MGuiButtonSolid) new MGuiButtonSolid(modularGui, "CANCEL", xPos + 2, stackIcon.yPos + 48, xSize - 4, 14, "Cancel").setColours(0xFF400000, 0xFF000000, 0xFFFFFFFF).setListener(this));
        addChild(new MGuiButtonSolid(modularGui, "COPY_ID", xPos + 2, stackIcon.yPos + 64, xSize - 4, 14, "Copy ID for link").setColours(0xFF00a000, 0xFF000000, 0xFFFFFFFF).setListener(this).setToolTip(new String[] {"Copies this branch's id to your clipboard. It can then be used in links to link back to this branch."}));

        addChild(new MGuiLabel(modularGui, xPos, yPos + 140, xSize, 12, "Sorting Weight").setAlignment(EnumAlignment.LEFT));
        addChild(weightField = new MGuiTextField(modularGui, xPos + 2, yPos + 152, xSize - 4, 16, modularGui.getMinecraft().fontRendererObj));
        weightField.setMaxStringLength(10);
        weightField.setText(String.valueOf(branch.sortingWeight));
        weightField.setId("SET_WEIGHT");
        weightField.setListener(this);
        weightField.setValidator(new Predicate<String>() {
            @Override
            public boolean apply(@Nullable String input) {
                try {
                    return Utils.parseInt(input, false) >= 0;
                }
                catch (Exception e) {
                    return false;
                }
            }
        });
        weightField.addChild(new MGuiHoverPopup(modularGui, new String[] {"Controls the position of each item within the navigation list", "Branches with higher weight show bellow branches with lower weight", "Valid numbers range is 0-2147483647", "Is multiple branches have the same weight they should show in the order they were created."}, weightField));

        //endregion

        addChild(deleteButton = (MGuiButtonSolid) new MGuiButtonSolid(modularGui, "DELETE", xPos + 1 + (xSize / 2), yPos + ySize - 16, (xSize / 2) - 2, 14, "Delete").setColours(0xFFFF0000, 0xFF000000, 0xFFFFFFFF).setListener(this));
        MGuiLabel confirm = new MGuiLabel(modularGui, xPos, yPos + ySize - 30, xSize, 12, "Are You Sure?");
        confirm.id = "CONFIRM_LABEL";
        addChild(confirm);
        setChildIDEnabled("CONFIRM_LABEL", false);
        addChild(confirmYes = (MGuiButtonSolid) new MGuiButtonSolid(modularGui, "DELETE_YES", xPos + 2, yPos + ySize - 16, (xSize / 2) - 2, 14, "Yes").setColours(0xFFFF0000, 0xFF000000, 0xFFFFFFFF).setListener(this));
        addChild(confirmNo = (MGuiButtonSolid) new MGuiButtonSolid(modularGui, "DELETE_NO", xPos + 1 + (xSize / 2), yPos + ySize - 16, (xSize / 2) - 2, 14, "No").setColours(0xFFFF0000, 0xFF000000, 0xFFFFFFFF).setListener(this));
        confirmYes.setEnabled(false);
        confirmNo.setEnabled(false);

        super.initElement();
    }

    @Override
    public void renderBackgroundLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        drawBorderedRect(xPos, yPos, xSize, ySize, 1, 0xFF222222, 0xFFAA0000);
        super.renderBackgroundLayer(minecraft, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
//        errorLabel.setEnabled(false);
        return isMouseOver(mouseX, mouseY);
    }

    @Override
    public void onMGuiEvent(String event, MGuiElementBase element) {
        //region Pick Stack
        if (element == pickStack) {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            selector = new StackSelector(modularGui, xPos, yPos, xSize, ySize);
            selector.setListener(this);

            List<ItemStack> stacks = new LinkedList<ItemStack>();
            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                ItemStack stack = player.inventory.getStackInSlot(i);
                if (stack != null) {
                    stacks.add(stack);
                }
            }
            selector.setStacks(stacks);
            selector.addChild(new MGuiButtonSolid(modularGui, "CANCEL_PICK", xPos + xSize - 51, yPos + ySize - 22, 40, 20, "Cancel").setListener(this).setId("CANCEL_PICK"));
            selector.initElement();
            modularGui.getManager().add(selector, 2);
//            selector.schedualUpdate();

        }
        else if (element == clearStack) {
            stackIcon.setStack(new StackReference("null"));
            branch.initBranches();
            stackIcon.setToolTip(false);
            stackString.setText("");
        }
        else if (element.id.equals("CANCEL_PICK") && selector != null) {
            modularGui.getManager().remove(selector);
        }
        else if (event.equals("SELECTOR_PICK")) {
            if (element instanceof MGuiStackIcon) {
                StackReference reference = new StackReference(((MGuiStackIcon)element).getStack());
                stackIcon.setStack(reference);
                stackIcon.setToolTip(true);
                stackString.setText(reference.toString());
            }

            modularGui.getManager().remove(selector);
        }
        else if (element instanceof MGuiButton && ((MGuiButton) element).buttonName.equals("COPY_ID")) {
            GuiScreen.setClipboardString(branch.branchID);
        }
        //endregion

        //region Misc

        else if (element == okButton) {
            saveAndClose();
        }

        else if (element == cancelButton){
            close();
        }

        //endregion


        else if (element == deleteButton) {
            setChildIDEnabled("CONFIRM_LABEL", true);
            confirmYes.setEnabled(true);
            confirmNo.setEnabled(true);
            deleteButton.setEnabled(false);
        }
        else if (element == confirmYes) {
            try {
                branch.parent.branchData.removeChild(branch.branchData);
                branch.parent.save();
                WikiDocManager.reload(true, true, true);
                if (branch.guiWiki.wikiDataTree.idToBranchMap.containsKey(branch.parent.branchID)) {
                    branch.guiWiki.wikiDataTree.setActiveBranch(branch.guiWiki.wikiDataTree.idToBranchMap.get(branch.parent.branchID));
                }
                close();
            }
            catch (Exception e) {
                errorLabel.setDisplayString("Something went wrong while deleting the mod...\n\n" + e.getMessage() + "\n\nSee console for stack trace");
                e.printStackTrace();
            }
        }
        else if (element == confirmNo) {
            setChildIDEnabled("CONFIRM_LABEL", false);
            confirmYes.setEnabled(false);
            confirmNo.setEnabled(false);
            deleteButton.setEnabled(true);
        }
    }

    @Override
    public void close() {
        ((MGuiList) parent).disableList = false;
        parent.removeChild(this);
    }

    private void saveAndClose() {
        Element data = branch.branchData;

        if (StringUtils.isNullOrEmpty(stackString.getText())) {
            data.setAttribute(ATTRIB_ICON_TYPE, ICON_TYPE_OFF);
            data.removeAttribute(ATTRIB_ICON);
        }
        else {
            data.setAttribute(ATTRIB_ICON_TYPE, ICON_TYPE_STACK);
            data.setAttribute(ATTRIB_ICON, stackString.getText());
        }

        if (branch.sortingWeight != Utils.parseInt(weightField.getText())) {
            data.setAttribute(TreeBranchRoot.ATTRIB_WEIGHT, String.valueOf(Utils.parseInt(weightField.getText())));
        }

        data.setAttribute(ATTRIB_BRANCH_NAME, nameField.getText());


        try {
            WikiDocManager.saveChanges(data.getOwnerDocument());
            close();
            WikiDocManager.reload(false, true, true);
            branch.guiWiki.wikiDataTree.reOpenLast();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
