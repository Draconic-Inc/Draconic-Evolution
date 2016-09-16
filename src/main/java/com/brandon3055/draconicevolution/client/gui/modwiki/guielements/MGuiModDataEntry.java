package com.brandon3055.draconicevolution.client.gui.modwiki.guielements;

import com.brandon3055.brandonscore.client.gui.modulargui.IModularGui;
import com.brandon3055.brandonscore.client.gui.modulargui.MGuiElementBase;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.EnumAlignment;
import com.brandon3055.brandonscore.client.gui.modulargui.modularelements.MGuiLabel;
import com.brandon3055.brandonscore.client.gui.modulargui.modularelements.MGuiList;
import com.brandon3055.brandonscore.client.gui.modulargui.modularelements.MGuiListEntry;
import com.brandon3055.draconicevolution.client.gui.modwiki.WikiStyle;
import com.brandon3055.draconicevolution.client.gui.modwiki.moddata.ModContentPage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import java.io.IOException;

import static com.brandon3055.draconicevolution.client.gui.modwiki.WikiStyle.NAV_WINDOW;

/**
 * Created by brandon3055 on 4/09/2016.
 */
@Deprecated //TODO Delete. I am just leaving this here incase i find a use for it
public class MGuiModDataEntry extends MGuiListEntry {
    public final ModContentPage dataEntry;
    private MGuiLabel label;

    public MGuiModDataEntry(IModularGui modularGui, ModContentPage dataEntry) {
        super(modularGui);
        this.dataEntry = dataEntry;
    }

    @Override
    public void setList(MGuiList list) {
        super.setList(list);
    }

    @Override
    public void initElement() {
        childElements.clear();
        ySize = (modularGui.getMinecraft().fontRendererObj.listFormattedStringToWidth(dataEntry.readableName, xSize - 10).size() * 8) + 10;
        addChild(label = new MGuiLabel(modularGui, xPos, yPos, xSize, getEntryHeight(), dataEntry.readableName) {
            @Override
            public int getTextColour() {
                return WikiStyle.TEXT_COLOUR;
            }
        }.setAlignment(EnumAlignment.LEFT).setWrap(true));
        super.initElement();
    }

    @Override
    public int getEntryHeight() {
        if (ySize == 0) {
            ySize = (modularGui.getMinecraft().fontRendererObj.listFormattedStringToWidth(dataEntry.readableName, xSize - 10).size() * 8) + 10;
        }

        return ySize;
    }

    @Override
    public void moveEntry(int newXPos, int newYPos) {
        xPos = newXPos;
        yPos = newYPos;
        for (MGuiElementBase element : childElements) {
            element.xPos = xPos;
            element.yPos = yPos;
        }
    }

    @Override
    public void renderBackgroundLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        int navWindowColour = NAV_WINDOW;
        boolean hovering = isMouseOver(mouseX, mouseY);

        int back = mixColours(navWindowColour, hovering ? 0x00404040 : 0x00303030);
        int pos = mixColours(navWindowColour, 0x00505050);
        int neg = mixColours(navWindowColour, 0x00202020, true);

        drawColouredRect(xPos + 1, yPos + 1, xSize - 2, ySize - 2, back);
        drawColouredRect(xPos + 1, yPos + 1, xSize - 2, 0.5, pos);
        drawColouredRect(xPos + 1, yPos + 1, 0.5, ySize - 2, pos);
        drawColouredRect(xPos + 1, yPos + ySize - 1, xSize - 2, 0.5, neg);
        drawColouredRect(xPos + xSize - 1, yPos + 1, 0.5, ySize - 2, neg);

        super.renderBackgroundLayer(minecraft, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (isMouseOver(mouseX, mouseY)) {
            modularGui.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
//            GuiModWiki.activeModData = dataEntry;
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
