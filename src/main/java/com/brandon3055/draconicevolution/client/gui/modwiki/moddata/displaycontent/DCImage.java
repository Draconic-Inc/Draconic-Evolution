package com.brandon3055.draconicevolution.client.gui.modwiki.moddata.displaycontent;

import com.brandon3055.brandonscore.client.gui.modulargui.MGuiElementBase;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.EnumAlignment;
import com.brandon3055.brandonscore.client.gui.modulargui.modularelements.MGuiButtonSolid;
import com.brandon3055.brandonscore.client.gui.modulargui.modularelements.MGuiHoverPopup;
import com.brandon3055.brandonscore.client.gui.modulargui.modularelements.MGuiLabel;
import com.brandon3055.brandonscore.client.gui.modulargui.modularelements.MGuiTextField;
import com.brandon3055.brandonscore.lib.DLRSCache;
import com.brandon3055.brandonscore.lib.DLResourceLocation;
import com.brandon3055.draconicevolution.client.gui.modwiki.GuiModWiki;
import com.brandon3055.draconicevolution.client.gui.modwiki.moddata.guidoctree.TreeBranchRoot;
import com.google.common.base.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.w3c.dom.Element;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import static com.brandon3055.brandonscore.client.gui.modulargui.lib.EnumAlignment.CENTER;
import static com.brandon3055.brandonscore.client.gui.modulargui.lib.EnumAlignment.LEFT;

/**
 * Created by brandon3055 on 8/09/2016.
 */
public class DCImage extends DisplayComponentBase {

    public static final String ATTRIB_URL = "imgURL";
    public static final String ATTRIB_SCALE = "scale";

    public String url;
    public int scale;
    public DLResourceLocation resourceLocation;
    public boolean ltComplete = false;

    public DCImage(GuiModWiki modularGui, String componentType, TreeBranchRoot branch) {
        super(modularGui, componentType, branch);
        ySize = 20;
    }

    //region Render

    @Override
    public void renderBackgroundLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        super.renderBackgroundLayer(minecraft, mouseX, mouseY, partialTicks);

        if (resourceLocation == null) {
            return;
        }

        double setScale = this.scale / 100D;

        if (resourceLocation.dlFailed || !resourceLocation.dlFinished) {
            setScale = 2;
        }

        int texXSize = resourceLocation.width;
        int texYSize = resourceLocation.height;

        double scaledWidth = Math.min(xSize - 4, texXSize * setScale);
        double renderScale = scaledWidth / texXSize;
        double scaledHeight = texYSize * renderScale;


        bindTexture(resourceLocation);
        GlStateManager.color(1F, 1F, 1F, 1F);

        int texXPos = (int) (alignment == LEFT ? xPos + 2 : alignment == CENTER ? xPos + (xSize / 2) - (scaledWidth / 2) : xPos + xSize - scaledWidth - 2);

        drawScaledCustomSizeModalRect(texXPos, yPos, 0, 0, texXSize, texYSize, scaledWidth, scaledHeight, texXSize, texYSize);
    }

    @Override
    public void renderForegroundLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        super.renderForegroundLayer(minecraft, mouseX, mouseY, partialTicks);

        if (isMouseOver(mouseX, mouseY) && mouseY > list.yPos + list.topPadding) {
            List<String> toolTip = new LinkedList<String>();
            if (resourceLocation == null) {
                toolTip.add(TextFormatting.DARK_RED + "An unknown error occurred...");
            }
            else if (GuiScreen.isShiftKeyDown()) {
                toolTip.add(TextFormatting.DARK_RED + "Click to re-download image.");
            }
            else if (resourceLocation.dlFailed) {
                toolTip.add(TextFormatting.DARK_RED + "Image download failed! Click to retry.");
            }
            else if (!resourceLocation.dlFinished) {
                toolTip.add(TextFormatting.BLUE + "Downloading image...");
            }
            else {
                toolTip.add(TextFormatting.GREEN + "Right-Click to open image in browser.");
            }
            drawHoveringText(toolTip, mouseX, mouseY, fontRenderer, modularGui.screenWidth(), modularGui.screenHeight());
        }
    }


    //endregion

    //region Interact

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {

        if (isMouseOver(mouseX, mouseY)) {
            if (resourceLocation == null) {
                return false;
            }
            else if (GuiScreen.isShiftKeyDown()) {
                DLRSCache.clearFileCache(url);
                resourceLocation = DLRSCache.getResource(url);
                ltComplete = false;
                ySize = 32;
                list.schedualUpdate();
            }
            else if (resourceLocation.dlFailed) {
                DLRSCache.clearFileCache(url);
                resourceLocation = DLRSCache.getResource(url);
                ltComplete = false;
            }
            else if (resourceLocation.dlFinished && mouseButton == 1) {
                try {
                    ReflectionHelper.setPrivateValue(GuiScreen.class, branch.guiWiki, new URI(url), "clickedLinkURI", "field_175286_t");
                    this.mc.displayGuiScreen(new GuiConfirmOpenLink(branch.guiWiki, url, 31102009, false));
                }
                catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
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

        list.add(new MGuiLabel(modularGui, 0, 0, 22, 12, "URL:").setAlignment(EnumAlignment.CENTER));
        MGuiTextField urlField = new MGuiTextField(modularGui, 0, 0, 150, 12, fontRenderer).setListener(this).setMaxStringLength(2048).setText(url);
        urlField.addChild(new MGuiHoverPopup(modularGui, new String[] {"Set the image URL. Note some URL's may not be supported.", TextFormatting.GREEN + "Will Auto-Save 3 seconds after you stop typing."}, urlField));
        urlField.setId("URL");
        list.add(urlField);

        list.add(new MGuiLabel(modularGui, 0, 0, 30, 12, "Scale:").setAlignment(EnumAlignment.CENTER));
        MGuiTextField scaleField = new MGuiTextField(modularGui, 0, 0, 36, 12, fontRenderer).setListener(this).setMaxStringLength(2048).setText(String.valueOf(scale));
        scaleField.addChild(new MGuiHoverPopup(modularGui, new String[] {"Set the image scale (As a percentage of the actual size)", TextFormatting.GOLD + "The size of the image will be limited by both this and the width of the GUI.", TextFormatting.GOLD + "Whichever value is smaller will take priority.", TextFormatting.GREEN + "Will save as you type."}, scaleField));
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

            if (newScale < 1) {
                newScale = 1;
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
        else if (eventElement.id.equals("URL") && eventString.equals("TEXT_FIELD_CHANGED") && eventElement instanceof MGuiTextField) {
            element.setAttribute(ATTRIB_URL, ((MGuiTextField) eventElement).getText());
            url = ((MGuiTextField) eventElement).getText();
            requiresSave = true;
            saveTimer = 60;
//            int pos = ((MGuiTextField) eventElement).getCursorPosition();
//            save();
//
//            for (MGuiElementBase element : branch.guiWiki.contentWindow.editControls) {
//                if (element instanceof MGuiTextField && element.id.equals("URL")) {
//                    ((MGuiTextField) element).setFocused(true);
//                    ((MGuiTextField) element).setCursorPosition(pos);
//                    break;
//                }
//            }
        }

    }

    @Override
    public void onCreated() {
        element.setAttribute(ATTRIB_URL, "http://www.rd.com/wp-content/uploads/sites/2/2016/02/06-train-cat-shake-hands.jpg");
        element.setAttribute(ATTRIB_SCALE, "100");
    }

    @Override
    public void setXSize(int xSize) {
        super.setXSize(xSize);

        if (resourceLocation == null) {
            ltComplete = false;
            return;
        }

        double setScale = this.scale / 100D;
        int texXSize = resourceLocation.width;
        int texYSize = resourceLocation.height;

        double scaledWidth = Math.min(xSize - 4, texXSize * setScale);
        double renderScale = scaledWidth / texXSize;
        ySize = (int) (texYSize * renderScale);
    }

    @Override
    public boolean onUpdate() {
        if (!ltComplete && resourceLocation != null && resourceLocation.dlFinished) {
            ltComplete = true;

            double setScale = this.scale / 100D;
            int texXSize = resourceLocation.width;
            int texYSize = resourceLocation.height;

            double scaledWidth = Math.min(xSize - 4, texXSize * setScale);
            double renderScale = scaledWidth / texXSize;
            ySize = (int) (texYSize * renderScale);
            list.schedualUpdate();
        }

        return super.onUpdate();
    }

    //endregion

    //region XML & Factory

    @Override
    public void loadFromXML(Element element) {
        super.loadFromXML(element);
        url = element.getAttribute(ATTRIB_URL);
        try {
            scale = Integer.parseInt(element.getAttribute(ATTRIB_SCALE));
        }
        catch (Exception e) {}
        if (scale < 1) {
            scale = 1;
        }
        resourceLocation = DLRSCache.getResource(url);
    }

    public static class Factory implements IDisplayComponentFactory {
        @Override
        public DisplayComponentBase createNewInstance(GuiModWiki guiWiki, TreeBranchRoot branch) {
            DisplayComponentBase component = new DCImage(guiWiki, getID(), branch);
            component.setWorldAndResolution(guiWiki.mc, guiWiki.screenWidth(), guiWiki.screenHeight());
            return component;
        }

        @Override
        public String getID() {
            return "image";
        }
    }

    //endregion
}
