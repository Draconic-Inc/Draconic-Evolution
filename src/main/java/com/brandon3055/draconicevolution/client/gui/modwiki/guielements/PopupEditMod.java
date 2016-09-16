package com.brandon3055.draconicevolution.client.gui.modwiki.guielements;

import com.brandon3055.brandonscore.client.gui.modulargui.IModularGui;
import com.brandon3055.brandonscore.client.gui.modulargui.MGuiElementBase;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.EnumAlignment;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.IMGuiListener;
import com.brandon3055.brandonscore.client.gui.modulargui.modularelements.*;
import com.brandon3055.draconicevolution.client.gui.modwiki.moddata.WikiDocManager;
import com.brandon3055.draconicevolution.client.gui.modwiki.moddata.guidoctree.TreeBranchContent;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.brandon3055.draconicevolution.client.gui.modwiki.moddata.WikiDocManager.ATTRIB_MOD_NAME;

/**
 * Created by brandon3055 on 10/09/2016.
 */
public class PopupEditMod extends MGuiPopUpDialog implements IMGuiListener {

    private final TreeBranchContent branch;
    public MGuiTextField nameField;
    public MGuiButtonSolid okButton;
    public MGuiButtonSolid cancelButton;
    public MGuiButtonSolid deleteButton;
    public MGuiButtonSolid confirmYes;
    public MGuiButtonSolid confirmNo;
    public MGuiLabel errorLabel;

    public PopupEditMod(IModularGui modularGui, int xPos, int yPos, int xSize, int ySize, MGuiList parent, TreeBranchContent branch) {
        super(modularGui, xPos, yPos, xSize, ySize, parent);
        this.branch = branch;
    }

    @Override
    public void initElement() {
        int y = yPos + 2;
        MGuiLabel label = new MGuiLabel(modularGui, xPos, y, xSize, 12, "Edit Name").setAlignment(EnumAlignment.LEFT);
        y += 12;
        addChild(label);
        addChild(nameField = new MGuiTextField(modularGui, xPos + 2, y, xSize - 4, 16, modularGui.getMinecraft().fontRendererObj));
        nameField.setText(branch.branchName);
        y += 30;
        addChild(okButton = (MGuiButtonSolid) new MGuiButtonSolid(modularGui, "OK", xPos + 2, y, (xSize / 2) - 2, 14, "OK").setColours(0xFF00a000, 0xFF000000, 0xFFFFFFFF).setListener(this));
        addChild(cancelButton = (MGuiButtonSolid) new MGuiButtonSolid(modularGui, "CANCEL", xPos + 1 + (xSize / 2), y, (xSize / 2) - 2, 14, "Cancel").setColours(0xFF504040, 0xFF000000, 0xFFFFFFFF).setListener(this));
        addChild(deleteButton = (MGuiButtonSolid) new MGuiButtonSolid(modularGui, "DELETE", xPos + 1 + (xSize / 2), yPos + ySize - 16, (xSize / 2) - 2, 14, "Delete").setColours(0xFFFF0000, 0xFF000000, 0xFFFFFFFF).setListener(this));

        MGuiLabel confirm = new MGuiLabel(modularGui, xPos, yPos + ySize - 30, xSize, 12, "Are You Sure?");
        confirm.id = "CONFIRM_LABEL";
        addChild(confirm);
        setChildIDEnabled("CONFIRM_LABEL", false);

        addChild(confirmYes = (MGuiButtonSolid) new MGuiButtonSolid(modularGui, "DELETE_YES", xPos + 2, yPos + ySize - 16, (xSize / 2) - 2, 14, "Yes").setColours(0xFFFF0000, 0xFF000000, 0xFFFFFFFF).setListener(this));
        addChild(confirmNo = (MGuiButtonSolid) new MGuiButtonSolid(modularGui, "DELETE_NO", xPos + 1 + (xSize / 2), yPos + ySize - 16, (xSize / 2) - 2, 14, "No").setColours(0xFFFF0000, 0xFF000000, 0xFFFFFFFF).setListener(this));
        confirmYes.setEnabled(false);
        confirmNo.setEnabled(false);

        addChild(new MGuiLabel(modularGui, xPos, y, xSize, 100, "Mod ID can only be edited manually because its tied to every single branch. If you need to edit it I recommend opening the xml and Find&Replace modid: with newmodid:").setWrap(true));
        y += 80;
        addChild(errorLabel = new MGuiLabel(modularGui, xPos, y, xSize, 100, "").setWrap(true).setAlignment(EnumAlignment.LEFT).setTextColour(0xFFFF0000));
        errorLabel.setEnabled(true);

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
        errorLabel.setEnabled(false);
        return isMouseOver(mouseX, mouseY);
    }

    @Override
    public void onMGuiEvent(String event, MGuiElementBase element) {
        if (element == okButton) {
            try {
                editModBranch(nameField.getText(), false);
                close();
            }
            catch (Exception e) {
                errorLabel.setEnabled(true);
                errorLabel.setDisplayString("Something went wrong while saving changes...\n\n" + e.getMessage() + "\n\nSee console for stack trace");
                e.printStackTrace();
            }
        }
        else if (element == cancelButton){
            close();
        }
        else if (element == deleteButton) {
            setChildIDEnabled("CONFIRM_LABEL", true);
            confirmYes.setEnabled(true);
            confirmNo.setEnabled(true);
            deleteButton.setEnabled(false);
        }
        else if (element == confirmYes) {
            try {
                editModBranch("", true);
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
        super.close();
    }

    public void editModBranch(String newName, boolean delete) throws Exception {
        if (delete) {
            File modXML = WikiDocManager.documentToFileMap.get(branch.branchData.getOwnerDocument());
            if (modXML != null && modXML.exists()) {
                if (modXML.delete()) {
                    WikiDocManager.reload(true, true, true);
                }
                else {
                    throw new Exception("For some reason the file return false when deleting meaning it did not delete. Not sure why. Maby try deleting it manually");
                }
            }
            else {
                throw new FileNotFoundException("Could not find the file to delete... Maby hit the reload button and try again. If that fails delete the file manually.");
            }
        }
        else {
            if (!branch.branchName.equals(newName)) {
                branch.branchName = newName;
                branch.branchData.setAttribute(ATTRIB_MOD_NAME, branch.branchName);
                WikiDocManager.saveChanges(branch.branchData.getOwnerDocument());
                WikiDocManager.reload(true, true, true);
            }
        }

    }
}
