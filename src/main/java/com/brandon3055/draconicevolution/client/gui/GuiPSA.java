package com.brandon3055.draconicevolution.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

/**
 * Created by brandon3055 on 29/07/2017.
 */
public class GuiPSA extends GuiScreen {
    private static boolean messageDisplayed = false;
    private GuiMainMenu parent;
    private static File configDir;

    public GuiPSA(GuiMainMenu parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        super.initGui();
        int center = width / 2;
        int y = (height / 2);

        buttonList.add(new GuiButton(0, center - 190, y + 20, 132, 20, "Visit stopmodreposts.org"));
        buttonList.add(new GuiButton(1, center + 190 - 132, y + 20, 132, 20, "Continue") {
            @Override
            public void drawButton(Minecraft mc, int mouseX, int mouseY) {
                super.drawButton(mc, mouseX, mouseY);
                if (isMouseOver()) {
                    drawHoveringText(Collections.singletonList("This message will not show again."), mouseX, mouseY);
                }
            }
        });
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        int center = width / 2;
        int y = (height / 2) - 100;

        drawCenteredSplitString(fontRendererObj, TextFormatting.GOLD + "PSA from CodeChickenLib", center, y, 255, 0xFF0000, false);

        drawCenteredSplitString(fontRendererObj, "Websites are §creposting unsafe versions of mods§r.\n" +
                "Some popular examples are §c9minecraft.net§r and §cminecraftdl.com§r but there are hundreds more.\n" +
                "The mods they host can be outdated, buggy, and in some cases contain malware.\n" +
                "Visit §3stopmodreposts.org§r to learn where you can safely download mods.", center, y + 25, 350, 0xFFFFFF, false);

        fontRendererObj.drawSplitString("Stay Safe\n" + TextFormatting.GOLD + "-brandon3055, covers1624 and the modding community.", center - 190, y + 95, 380, 0xFFFFFF);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public static void drawCenteredString(FontRenderer fontRenderer, String text, int x, int y, int color, boolean dropShadow) {
        fontRenderer.drawString(text, (float) (x - fontRenderer.getStringWidth(text) / 2), (float) y, color, dropShadow);
    }

    public static void drawCenteredSplitString(FontRenderer fontRenderer, String str, int x, int y, int wrapWidth, int color, boolean dropShadow) {
        for (String s : fontRenderer.listFormattedStringToWidth(str, wrapWidth)) {
            drawCenteredString(fontRenderer, s, x, y, color, dropShadow);
            y += fontRenderer.FONT_HEIGHT;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            try {
                ReflectionHelper.setPrivateValue(GuiScreen.class, this, new URI("http://stopmodreposts.org/"), "clickedLinkURI", "field_175286_t");
                this.mc.displayGuiScreen(new GuiConfirmOpenLink(this, "http://stopmodreposts.org/", 31102009, false));
            }
            catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        else if (button.id == 1) {
            mc.displayGuiScreen(parent);
        }
        else if (button.id == 2) {
            mc.displayGuiScreen(parent);
            File marker = new File(configDir, "cclPSAMarker.txt");
            FileWriter writer = new FileWriter(marker);
            writer.append("This is a marker for CCL's Stop Mod Reposts public service announcement. Deleting this file will re enable the announcement.");
            writer.close();
        }
        super.actionPerformed(button);
    }

    public static void init(FMLPreInitializationEvent event) {
        configDir = event.getModConfigurationDirectory();
        File marker = new File(configDir, "cclPSAMarker.txt");
        if (!marker.exists()) {
            MinecraftForge.EVENT_BUS.register(new GuiPSA(null));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void guiOpenEvent(GuiOpenEvent event) {
//        messageDisplayed = false;
        if (event.getGui() instanceof GuiMainMenu && !messageDisplayed) {
            event.setGui(new GuiPSA((GuiMainMenu) event.getGui()));
            messageDisplayed = true;
        }
    }
}

//        drawCenteredSplitString(fontRendererObj, "This is a Public Service Announcement from\n" + TextFormatting.GOLD + "Code Chicken Lib", center, y, 255, 0xFF0000, false);
//
//        drawCenteredSplitString(fontRendererObj, "For a while now the modding community has been plagued by websites that §cillegally re-post minecraft mods§r.\n\n " + //
//                "These sites cause all sorts of issues for both mod developers and users. For example there mods can be miss labeled, outdated and even contain malware in some cases.\n\n" + //
//                "To a user these sites may look completely legit but they are not. Some popular examples of these sites are §c9minecraft.net§r and §cminecraftdl.com§r but there are hundreds more.\n\n" + //
//                "To learn more about this issue and find out how you can identify these sites visit §2stopmodreposts.org§r Check out their browser plugin that can help you identify these sites.", center, y + 25, 350, 0xFFFFFF, false);