package com.brandon3055.draconicevolution.client.gui.guicomponents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringUtils;

import org.lwjgl.opengl.GL11;

import com.brandon3055.brandonscore.client.gui.guicomponents.ComponentScrollingBase;
import com.brandon3055.brandonscore.client.gui.guicomponents.GUIScrollingBase;
import com.brandon3055.brandonscore.client.utills.ClientUtills;
import com.brandon3055.brandonscore.common.utills.InfoHelper;
import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.brandon3055.draconicevolution.common.handler.ContributorHandler;
import com.brandon3055.draconicevolution.common.handler.ContributorHandler.Contributor;

/**
 * Created by brandon3055 on 5/11/2015.
 */
public class ComponentContributorsPage extends ComponentScrollingBase implements GuiYesNoCallback {

    public int scrollOffset = 0;
    public int pageLength = 0;
    public int scrollLimit = 0;

    public ComponentContributorsPage(int x, int y, GUIScrollingBase gui) {
        super(x, y, gui);
        pageLength = 55;
        pageLength += ContributorHandler.contributors.size() * 30;
        scrollLimit = pageLength - 325;
    }

    @Override
    public void handleScrollInput(int direction) {
        // page.scrollOffset += direction * 10;
        scrollOffset += direction * (InfoHelper.isShiftKeyDown() ? 30 : 10);
        if (scrollOffset < 0) scrollOffset = 0;
        if (scrollOffset > pageLength - getHeight()) scrollOffset = pageLength - getHeight();
        if (pageLength <= getHeight()) scrollOffset = 0;
    }

    @Override
    public int getWidth() {
        return gui.getXSize();
    }

    @Override
    public int getHeight() {
        return gui.getYSize();
    }

    @Override
    public void renderBackground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
        int yOffset = -scrollOffset;

        yOffset += 20;

        if (yOffset > 0) {
            drawCenteredString(
                    fontRendererObj,
                    StatCollector.translateToLocal("info.de.manual.contributors.txt"),
                    getWidth() / 2,
                    yOffset,
                    0x00FFFF);
        }

        yOffset += 10;

        boolean patreonButtonHighlighted = mouseX >= 140 && mouseY >= 290;

        for (Contributor contributor : ContributorHandler.contributors.values()) {
            if (!StringUtils.isNullOrEmpty(contributor.details) && contributor.details.contains("[UNLISTED]")) continue;
            yOffset += 30;

            if (!(yOffset > 25 && yOffset < 324)) continue;

            if (mouseX > 15 && mouseX < 245
                    && mouseY < yOffset + 5
                    && mouseY > yOffset - 25
                    && !patreonButtonHighlighted) {
                ResourceHandler.bindResource("textures/gui/manualTop.png");
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glColor4f(0F, 0F, 0F, 0.4F);
                drawTexturedModalRect(15, yOffset - 26, 20, 20, 226, 30);
                GL11.glDisable(GL11.GL_BLEND);
            }

            GL11.glPushMatrix();
            GL11.glColor4f(1F, 1F, 1F, 1F);
            GL11.glTranslated(30, yOffset, 0);
            GL11.glScaled(100, 100, 100);
            renderPlayer(contributor.ign);
            GL11.glPopMatrix();

            fontRendererObj
                    .drawStringWithShadow(EnumChatFormatting.GOLD + contributor.name, 50, yOffset - 20, 0x000000);
            fontRendererObj
                    .drawString(EnumChatFormatting.DARK_BLUE + contributor.contribution, 50, yOffset - 10, 0x000000);
        }
    }

    @Override
    public void renderFinal(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
        int yOffset = -scrollOffset + 30;

        boolean patreonButtonHighlighted = mouseX >= 140 && mouseY >= 290;

        for (Contributor contributor : ContributorHandler.contributors.values()) {
            if (!StringUtils.isNullOrEmpty(contributor.details) && contributor.details.contains("[UNLISTED]")) continue;
            yOffset += 30;

            if (!(yOffset > 20 && yOffset < 330)) continue;

            if (mouseX > 15 && mouseX < 245
                    && mouseY < yOffset + 5
                    && mouseY > yOffset - 25
                    && !patreonButtonHighlighted) {
                List<String> list = new ArrayList<String>();
                list.add(EnumChatFormatting.GOLD + contributor.contribution);

                if (!StringUtils.isNullOrEmpty(contributor.details)) {
                    list.add("");
                    List l = fontRendererObj.listFormattedStringToWidth(contributor.details, 247);
                    for (Object o : l) list.add(EnumChatFormatting.DARK_PURPLE + (String) o);
                }

                if (!contributor.name.equals(contributor.ign)) {
                    list.add("");
                    list.add(EnumChatFormatting.BLUE + "IGN: " + EnumChatFormatting.GOLD + contributor.ign);
                }

                if (!StringUtils.isNullOrEmpty(contributor.website)) {
                    list.add("");
                    list.add("Website: " + EnumChatFormatting.BLUE + contributor.website);
                    list.add(
                            EnumChatFormatting.GRAY
                                    + StatCollector.translateToLocal("info.de.manual.clickToGoToSite.txt"));
                }

                drawHoveringText(list, offsetX - 7, offsetY + yOffset + 20, fontRendererObj);
            }
        }

        if (!ContributorHandler.successfulLoad) fontRendererObj
                .drawString("[Error] Failed to download contributors list", offsetX + 20, offsetY + 40, 0xFF0000);
    }

    @Override
    public void renderForground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {}

    private String lastClickURL = "";

    @Override
    public void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);

        int yOffset = -scrollOffset + 30;

        boolean patreonButtonHighlighted = x >= 140 && y >= 290;

        for (Contributor contributor : ContributorHandler.contributors.values()) {
            if (!StringUtils.isNullOrEmpty(contributor.details) && contributor.details.contains("[UNLISTED]")) continue;
            yOffset += 30;

            if (!(yOffset > 20 && yOffset < 330) || patreonButtonHighlighted) continue;

            if (x > 15 && x < 245
                    && y < yOffset + 5
                    && y > yOffset - 25
                    && !StringUtils.isNullOrEmpty(contributor.website)) {
                lastClickURL = contributor.website;
                mc.displayGuiScreen(new GuiConfirmOpenLink(this, contributor.website, 0, true));
            }
        }
    }

    @Override
    public void confirmClicked(boolean confirmed, int button) {
        mc.displayGuiScreen(this.gui);
        if (confirmed) ClientUtills.openLink(lastClickURL);
    }

    private static ModelBiped modelBiped = new ModelBiped(0.0265F);

    private static void renderPlayer(String username) {
        ResourceHandler.bindTexture(getSkin(username));
        modelBiped.bipedHead.render(0.0265F);
        modelBiped.bipedHeadwear.render(0.0265F);
    }

    private static Map<String, ResourceLocation> skinCache = new HashMap<String, ResourceLocation>();
    private static boolean skinCashInitialized = false;

    private static ResourceLocation getSkin(String username) {
        if (!skinCashInitialized) {
            for (Contributor contributor : ContributorHandler.contributors.values()) {
                if (!StringUtils.isNullOrEmpty(contributor.details) && contributor.details.contains("[UNLISTED]"))
                    continue;
                ResourceLocation skin = AbstractClientPlayer.getLocationSkin(contributor.ign);
                AbstractClientPlayer.getDownloadImageSkin(skin, contributor.ign);
                skinCache.put(contributor.ign, skin);
            }
            skinCashInitialized = true;
        }

        if (!skinCache.containsKey(username)) return AbstractClientPlayer.locationStevePng;
        return skinCache.get(username);
    }
}
