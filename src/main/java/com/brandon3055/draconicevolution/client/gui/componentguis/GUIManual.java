package com.brandon3055.draconicevolution.client.gui.componentguis;

import com.brandon3055.brandonscore.client.gui.guicomponents.*;
import com.brandon3055.brandonscore.client.utills.ClientUtills;
import com.brandon3055.brandonscore.common.utills.InfoHelper;
import com.brandon3055.draconicevolution.client.gui.guicomponents.ComponentContributorsPage;
import com.brandon3055.draconicevolution.client.gui.guicomponents.ComponentIndexButton;
import com.brandon3055.draconicevolution.client.gui.guicomponents.ComponentManualPage;
import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.brandon3055.draconicevolution.common.container.DummyContainer;
import com.brandon3055.draconicevolution.common.handler.ConfigHandler;
import com.brandon3055.draconicevolution.common.handler.ContributorHandler;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.utills.LogHelper;
import com.google.gson.stream.JsonReader;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.apache.commons.io.IOUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

/**
 * Created by Brandon on 6/03/2015.
 */
public class GUIManual extends GUIScrollingBase implements GuiYesNoCallback {

    private static List<ManualPage> pageList = new ArrayList<ManualPage>();
    public static List<String> imageURLs = new ArrayList<String>();
    private static ManualPage currentPage = null;
    private int previousScale = -1;
    private int worldUpdateIn = -1;
    private static String lang;

    public GUIManual() {
        super(new DummyContainer(), 255, 325);
        if (currentPage != null) {
            collection
                    .addComponent(new ComponentManualPage(0, 0, this, currentPage))
                    .setGroup(GR_PAGE);
            collection
                    .addComponent(new ComponentButton(
                            102, 314, 50, 12, 1, this, StatCollector.translateToLocal("button.de.back.txt")))
                    .setGroup(GR_PAGE);
            collection.setOnlyGroupEnabled(GR_BACKGROUND);
            collection.setGroupEnabled(GR_PAGE, true);
        }

        // mc.gameSettings.guiScale
    }

    @Override
    public void initGui() {
        super.initGui();
        loadPages();
        if (previousScale == -1) adjustGuiScale();

        if (!Minecraft.getMinecraft()
                .getLanguageManager()
                .getCurrentLanguage()
                .getLanguageCode()
                .equals(lang)) loadPages();

        LogHelper.info(ContributorHandler.contributors);
    }

    private void adjustGuiScale() {
        previousScale = mc.gameSettings.guiScale;

        if (height < ySize) {
            int x = width;
            int y = height;

            if (mc.gameSettings.guiScale == 0) {
                mc.gameSettings.guiScale = 3;
                ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
                x = scaledresolution.getScaledWidth();
                y = scaledresolution.getScaledHeight();
            }

            if (y < ySize && mc.gameSettings.guiScale == 3) {
                mc.gameSettings.guiScale = 2;
                ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
                x = scaledresolution.getScaledWidth();
                y = scaledresolution.getScaledHeight();
            }

            if (y < ySize && mc.gameSettings.guiScale == 2) {
                mc.gameSettings.guiScale = 1;
                ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
                x = scaledresolution.getScaledWidth();
                y = scaledresolution.getScaledHeight();
            }

            height = y;
            width = x;
            this.guiLeft = (this.width - this.xSize) / 2;
            this.guiTop = (this.height - this.ySize) / 2;
            worldUpdateIn = 5;
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        mc.gameSettings.guiScale = previousScale;
    }

    public static final String GR_BACKGROUND = "BACKGROUND";
    public static final String GR_INTRO = "INTRO";
    public static final String GR_CONTRIBUTORS = "CONTRIBUTORS";
    public static final String GR_INDEX = "INDEX";
    public static final String GR_PAGE = "PAGE";

    @Override
    protected ComponentCollection assembleComponents() {
        collection = new ComponentCollection(0, 0, xSize, ySize, this);

        collection
                .addComponent(new ComponentTexturedRect(
                        0, 0, 255, 255, ResourceHandler.getResource("textures/gui/manualTop.png")))
                .setGroup(GR_BACKGROUND);
        collection
                .addComponent(new ComponentTexturedRect(
                        0, 255, 255, 69, ResourceHandler.getResource("textures/gui/manualBottom.png")))
                .setGroup(GR_BACKGROUND);

        collection
                .addComponent(new ComponentTexturedRect(
                        7, 100, 0, 0, 255, 255, ResourceHandler.getResource("textures/gui/images/debanner.png"), true))
                .setGroup(GR_INTRO)
                .setName("BANNER");

        collection
                .addComponent(new ComponentButton(
                        75,
                        260,
                        100,
                        20,
                        0,
                        this,
                        StatCollector.translateToLocal("info.de.manual.indexButton.txt"),
                        StatCollector.translateToLocal("info.de.manual.indexButtonTip.txt")))
                .setGroup(GR_INTRO);
        collection
                .addComponent(new ComponentButton(
                        75,
                        285,
                        100,
                        20,
                        2,
                        this,
                        StatCollector.translateToLocal("info.de.manual.contributorsButton.txt"),
                        StatCollector.translateToLocal("info.de.manual.contributorsButtonInfo.txt")))
                .setGroup(GR_INTRO);
        collection
                .addComponent(new ComponentContributorsPage(0, 0, this))
                .setGroup(GR_CONTRIBUTORS)
                .setName("CONTRIBUTORS");

        collection
                .addComponent(new ComponentTextureButton(
                                140,
                                290,
                                0,
                                0,
                                100,
                                30,
                                10,
                                this,
                                "",
                                "",
                                ResourceHandler.getResource("textures/gui/patreon.png"))
                        .forceFullRender())
                .setGroup(GR_CONTRIBUTORS);

        for (int i = 0; i < pageList.size(); i++) {
            collection
                    .addComponent(new ComponentIndexButton(20, 20 + i * 20, this, pageList.get(i)))
                    .setGroup(GR_INDEX)
                    .setName("INDEX_BUTTON_" + i);
            pageLength += 20;
        }

        scrollLimit = pageLength - 285;

        collection.setOnlyGroupEnabled(GR_BACKGROUND);
        collection.setGroupEnabled(GR_INTRO, true);
        return collection;
    }

    @Override
    public void handleScrollInput(int direction) {
        if (collection.getComponent("INDEX_BUTTON_0") != null
                && collection.getComponent("INDEX_BUTTON_0").isEnabled()) {

            if (currentPage != null) return;
            scrollOffset += direction * (InfoHelper.isShiftKeyDown() ? 30 : 10);
            if (scrollOffset < 0) scrollOffset = 0;
            if (scrollOffset > pageLength - ySize + 40) scrollOffset = pageLength - ySize + 40;
            if (pageLength + 40 <= ySize) scrollOffset = 0;

            barPosition = (int) (((double) scrollOffset / scrollLimit) * 247D);
        } else if (collection.getComponent("CONTRIBUTORS") != null
                && collection.getComponent("CONTRIBUTORS").isEnabled()) {
            ComponentContributorsPage page = (ComponentContributorsPage) collection.getComponent("CONTRIBUTORS");
            barPosition = (int) (((double) page.scrollOffset / page.scrollLimit) * 247D);
        } else if (collection.getComponent("OPEN_PAGE") != null
                && collection.getComponent("OPEN_PAGE").isEnabled()) {
            ComponentManualPage page = (ComponentManualPage) collection.getComponent("OPEN_PAGE");
            barPosition = (int) (((double) page.scrollOffset / page.scrollLimit) * 247D);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float par3) {
        super.drawScreen(mouseX, mouseY, par3);
        int posX = (this.width - xSize) / 2;
        int posY = (this.height - ySize) / 2;
        if (collection.getComponent("BANNER") != null
                && collection.getComponent("BANNER").isEnabled())
            fontRendererObj.drawSplitString(
                    StatCollector.translateToLocal("info.de.manual.introTxt.txt"),
                    posX + 20,
                    posY + 190,
                    150,
                    0x000000);

        if (collection.getComponent("BANNER") != null
                && collection.getComponent("BANNER").isEnabled()) return;
        ResourceHandler.bindResource("textures/gui/Widgets.png");
        GL11.glColor4f(1F, 1F, 1F, 1F);
        drawTexturedModalRect(guiLeft, guiTop, 118, 20, 17, 17);

        if (mouseX - posX >= 0 && mouseX - posX <= 17 && mouseY - posY >= 0 && mouseY - posY <= 17) {
            List<String> list = new ArrayList<String>();
            list.add(EnumChatFormatting.GREEN + StatCollector.translateToLocal("info.de.manual.nav1.txt"));
            list.add("");
            list.add("-" + StatCollector.translateToLocal("info.de.manual.nav2.txt"));
            list.add("-" + StatCollector.translateToLocal("info.de.manual.nav3.txt"));
            drawHoveringText(list, posX + 8, posY + 32, fontRendererObj);
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        disableScrollBar = collection.getComponent("BANNER") != null
                && collection.getComponent("BANNER").isEnabled();

        if (!scrollPressed
                && collection.getComponent("INDEX_BUTTON_0") != null
                && collection.getComponent("INDEX_BUTTON_0").isEnabled()) {
            barPosition = (int) (((double) scrollOffset / scrollLimit) * 247D);
        } else if (!scrollPressed
                && collection.getComponent("CONTRIBUTORS") != null
                && collection.getComponent("CONTRIBUTORS").isEnabled()) {
            ComponentContributorsPage page = (ComponentContributorsPage) collection.getComponent("CONTRIBUTORS");
            barPosition = (int) (((double) page.scrollOffset / page.scrollLimit) * 247D);
        } else if (!scrollPressed
                && collection.getComponent("OPEN_PAGE") != null
                && collection.getComponent("OPEN_PAGE").isEnabled()) {
            ComponentManualPage page = (ComponentManualPage) collection.getComponent("OPEN_PAGE");
            barPosition = (int) (((double) page.scrollOffset / page.scrollLimit) * 247D);
        }

        if (worldUpdateIn > -1) worldUpdateIn--;
        if (worldUpdateIn == 0) collection.setWorldAndResolution(mc, width, height);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);

        ResourceHandler.bindResource("textures/gui/manualBottom.png");
        GL11.glColor4f(1f, 1f, 1f, 1f);
        drawTexturedModalRect(0, ySize - 34, 0, 69, 256, 34);
        drawTexturedModalRect(0, 0, 0, 103, 256, 34);

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();

        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);
        if (disableScrollBar) return;

        ResourceHandler.bindResource("textures/gui/manualBottom.png");
        GL11.glColor4f(1F, 1F, 1F, 1F);
        drawTexturedModalRect(guiLeft - 16, guiTop + 10, 0, 146, 15, 100);
        drawTexturedModalRect(guiLeft - 16, guiTop + ySize - 110, 0, 156, 15, 100);
        drawTexturedModalRect(guiLeft - 16, guiTop + 110, 0, 156, 15, 90);
        drawTexturedModalRect(guiLeft - 16, guiTop + 140, 0, 156, 15, 90);

        drawTexturedModalRect(guiLeft - 15, guiTop + 20 + barPosition, 15, 218, 13, 38);
    }

    @Override
    public void buttonClicked(int id, int button) {
        super.buttonClicked(id, button);
        if (id == 0) {
            collection.setOnlyGroupEnabled(GR_BACKGROUND);
            collection.setGroupEnabled(GR_INDEX, true);
        } else if (id == 1) {
            currentPage = null;
            collection.setOnlyGroupEnabled(GR_BACKGROUND);
            collection.setGroupEnabled(GR_INDEX, true);
            collection.schedulRemoval(GR_PAGE);
        } else if (id == 2) {
            currentPage = null;
            collection.setOnlyGroupEnabled(GR_BACKGROUND);
            collection.setGroupEnabled(GR_CONTRIBUTORS, true);
            collection.schedulRemoval(GR_PAGE);
        } else if (id == 10) {
            mc.displayGuiScreen(new GuiConfirmOpenLink(this, "https://www.patreon.com/brandon3055", 0, true));
        }
    }

    @Override
    protected void keyTyped(char par1, int par2) {
        super.keyTyped(par1, par2);
        if (Keyboard.KEY_BACK == par2) {
            if (currentPage != null) {
                currentPage = null;
                collection.setOnlyGroupEnabled(GR_BACKGROUND);
                collection.setGroupEnabled(GR_INDEX, true);
                collection.schedulRemoval(GR_PAGE);
            } else {
                collection.setOnlyGroupEnabled(GR_BACKGROUND);
                collection.setGroupEnabled(GR_INTRO, true);
            }
        }
    }

    public static void loadPages() {
        lang = Minecraft.getMinecraft()
                .getLanguageManager()
                .getCurrentLanguage()
                .getLanguageCode();

        ResourceLocation rsLocation =
                new ResourceLocation(References.RESOURCESPREFIX + "manual/manual-" + lang + ".json");
        IResource resource = null;

        try {
            resource = Minecraft.getMinecraft().getResourceManager().getResource(rsLocation);
        } catch (IOException e) {
            LogHelper.warn("##################################################################################");
            LogHelper.warn("");
            LogHelper.warn("Info Tablet language localisation is not available for the selected language: " + lang);
            LogHelper.warn("The default language (en_US) will be loaded instead");
            LogHelper.warn("");
            LogHelper.warn("##################################################################################");

            rsLocation = new ResourceLocation(References.RESOURCESPREFIX + "manual/manual-en_US.json");
            try {
                resource = Minecraft.getMinecraft().getResourceManager().getResource(rsLocation);
            } catch (IOException e1) {
                LogHelper.error("Well that didn't work... ");
                e1.printStackTrace();
            }
        }

        if (resource == null) {
            LogHelper.error("Something went wrong while loading the Info Tablet json file");
            return;
        }

        try {
            File manualJSON = new File(ResourceHandler.getConfigFolder(), "manual.json");

            InputStream is = resource.getInputStream();
            OutputStream os = new FileOutputStream(manualJSON);
            IOUtils.copy(is, os);
            is.close();
            os.close();

            JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(manualJSON), "utf-8"));

            List<String> images;
            List<String> content;

            reader.beginArray();

            // Read next page
            while (reader.hasNext()) {
                String name;
                String nameL = null;
                int meta = 0;
                images = new ArrayList<String>();
                content = new ArrayList<String>();

                reader.beginObject();

                // Read page name
                String s = reader.nextName();
                if (s.equals("name")) {
                    name = reader.nextString();
                } else {
                    reader.close();
                    throw new IOException(
                            "Error reading manual.json (invalid name in place of \"name\" [Found:\"" + s + "\"])");
                }

                // Read page images
                s = reader.nextName();
                if (s.equals("nameL")) {
                    nameL = reader.nextString();
                    s = reader.nextName();
                }
                if (s.equals("meta")) {
                    meta = reader.nextInt();
                    s = reader.nextName();
                }

                if (s.equals("images")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        String url = reader.nextString();
                        images.add(url);
                        if (!imageURLs.contains(url)) imageURLs.add(url);
                    }
                    reader.endArray();
                } else {
                    reader.close();
                    throw new IOException(
                            "Error reading manual.json (invalid name in place of \"name\" [Found:\"" + s + "\"])");
                }
                // Read page content
                s = reader.nextName();
                if (s.equals("content")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        content.add(reader.nextString());
                    }
                    reader.endArray();
                } else {
                    reader.close();
                    throw new IOException(
                            "Error reading manual.json (invalid name in place of \"name\" [Found:\"" + s + "\"])");
                }
                reader.endObject();

                if (isValidPage(name))
                    pageList.add(new ManualPage(
                            name,
                            images.toArray(new String[images.size()]),
                            content.toArray(new String[content.size()]),
                            nameL,
                            meta));
            }
            reader.endArray();
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);
        if (buttonPressed) return;

        for (ComponentBase c : collection.getComponents()) {
            if (c instanceof ComponentIndexButton
                    && c.isEnabled()
                    && ((ComponentIndexButton) c).isOnScreen()
                    && c.isMouseOver(x - guiLeft, y - guiTop)) {
                Minecraft.getMinecraft()
                        .getSoundHandler()
                        .playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
                currentPage = ((ComponentIndexButton) c).getPage();
                collection
                        .addComponent(new ComponentManualPage(0, 0, this, currentPage))
                        .setGroup(GR_PAGE)
                        .setName("OPEN_PAGE");
                collection
                        .addComponent(new ComponentButton(
                                102, 314, 50, 12, 1, this, StatCollector.translateToLocal("button.de.back.txt")))
                        .setGroup(GR_PAGE);
                collection.setOnlyGroupEnabled(GR_BACKGROUND);
                collection.setGroupEnabled(GR_PAGE, true);
                break;
            }
        }
    }

    @Override
    public void barMoved(double position) {
        if (collection.getComponent("INDEX_BUTTON_0") != null
                && collection.getComponent("INDEX_BUTTON_0").isEnabled()) scrollOffset = (int) (position * scrollLimit);
        else if (collection.getComponent("CONTRIBUTORS") != null
                && collection.getComponent("CONTRIBUTORS").isEnabled()) {
            ComponentContributorsPage page = (ComponentContributorsPage) collection.getComponent("CONTRIBUTORS");
            if (page.scrollLimit < 0) return;
            page.scrollOffset = (int) (position * page.scrollLimit);
        } else if (collection.getComponent("OPEN_PAGE") != null
                && collection.getComponent("OPEN_PAGE").isEnabled()) {
            ComponentManualPage page = (ComponentManualPage) collection.getComponent("OPEN_PAGE");
            if (page.scrollLimit < 0) return;
            page.scrollOffset = (int) (position * page.scrollLimit);
        }
    }

    private static boolean isValidPage(String name) {
        if (name.contains("info.")) return true;
        else if (!ConfigHandler.disabledNamesList.contains(name)) return true;
        return false;
    }

    @Override
    public void confirmClicked(boolean confirmed, int id) {
        if (confirmed) ClientUtills.openLink("https://www.patreon.com/brandon3055");
        mc.displayGuiScreen(this);
    }
}
