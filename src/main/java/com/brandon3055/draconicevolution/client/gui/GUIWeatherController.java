package com.brandon3055.draconicevolution.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.container.ContainerWeatherController;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.network.ButtonPacket;
import com.brandon3055.draconicevolution.common.tileentities.TileWeatherController;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GUIWeatherController extends GuiContainer {

    public EntityPlayer player;
    private TileWeatherController tileWC;
    private int charges;
    private int guiUpdateTick;

    public GUIWeatherController(InventoryPlayer invPlayer, TileWeatherController tileWC) {
        super(new ContainerWeatherController(invPlayer, tileWC));

        xSize = 176;
        ySize = 143;

        this.tileWC = tileWC;
        this.charges = tileWC.charges;
        this.player = invPlayer.player;
    }

    private static final ResourceLocation texture = new ResourceLocation(
            References.MODID.toLowerCase(),
            "textures/gui/WeatherController.png");

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GL11.glColor4f(1, 1, 1, 1);

        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        if (charges > 0) drawTexturedModalRect(guiLeft + 37, guiTop + 4, 0, 143, 37, 38);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        fontRendererObj.drawString("Charges: " + charges, 90, 25, 0x000000);
        // drawCenteredString(fontRendererObj, "Charges: " + charges, 117, 25, 0x000000);
        drawCenteredString(fontRendererObj, "Weather Controller", xSize / 2, -15, 0x2a4ed0);
    }

    public static final String RAIN_ON_TEXT = "Turn rain on";
    public static final String RAIN_OFF_TEXT = "Turn rain off";
    public static final String STORM_TEXT = "Thunder Storm";
    public static String text;

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        if (tileWC.mode == 0) text = RAIN_OFF_TEXT;
        else if (tileWC.mode == 1) text = RAIN_ON_TEXT;
        else text = STORM_TEXT;
        // ID
        buttonList.add(new GuiButton(0, guiLeft + 85, guiTop, 85, 20, text));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            DraconicEvolution.network.sendToServer(new ButtonPacket((byte) 0, true));
            if (button.displayString.equals(STORM_TEXT)) button.displayString = RAIN_OFF_TEXT;
            else if (button.displayString.equals(RAIN_OFF_TEXT)) button.displayString = RAIN_ON_TEXT;
            else button.displayString = STORM_TEXT;
        }
    }

    @Override
    public void updateScreen() {
        guiUpdateTick++;
        if (guiUpdateTick >= 10) {
            initGui();
            guiUpdateTick = 0;
        }
        super.updateScreen();
        this.charges = tileWC.charges;
    }
}
