package com.brandon3055.draconicevolution.client.gui.modwiki.guielements;

import com.brandon3055.brandonscore.client.gui.modulargui_old.IModularGui;
import com.brandon3055.brandonscore.client.gui.modulargui_old.modularelements.MGuiVerticalButton;
import com.brandon3055.draconicevolution.client.gui.modwiki.WikiConfig;
import com.brandon3055.draconicevolution.client.gui.modwiki.moddata.guidoctree.TreeBranchRoot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import java.io.IOException;

import static com.brandon3055.draconicevolution.client.gui.modwiki.WikiConfig.NAV_WINDOW;

/**
 * Created by brandon3055 on 8/09/2016.
 */
public class WikiTreeButton extends MGuiVerticalButton {

    private final TreeBranchRoot branch;

    public WikiTreeButton(IModularGui gui, int xPos, int yPos, int xSize, int ySize, String buttonText, TreeBranchRoot branch) {
        super(gui, "", xPos, yPos, xSize, ySize, buttonText);
        this.branch = branch;
        setShadow(false);
        setColours(0, 0, 0);
    }

    @Override
    public int getTextColour(boolean hovered, boolean disabled) {
        return mixColours(WikiConfig.TEXT_COLOUR, (hovered ? 0x202020 : 0));
    }

    @Override
    public void renderBackgroundLayer(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        int fill = isMouseOver(mouseX, mouseY) ? mixColours(NAV_WINDOW, 0x00202020, true) : 0;
        int border = isMouseOver(mouseX, mouseY) ? mixColours(NAV_WINDOW, 0x00404040, false) : mixColours(NAV_WINDOW, 0x00202020, true);
        drawBorderedRect(xPos + 0.5, yPos + 0.5, xSize - 1.5, ySize - 1, 0.5, fill, border);
        super.renderBackgroundLayer(mc, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (isMouseOver(mouseX, mouseY)) {
            modularGui.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            branch.guiWiki.wikiDataTree.setActiveBranch(branch);
            return true;
        }
        return false;
    }

}
