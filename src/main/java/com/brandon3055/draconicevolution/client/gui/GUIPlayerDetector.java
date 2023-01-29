package com.brandon3055.draconicevolution.client.gui;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.container.ContainerPlayerDetector;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.network.PlayerDetectorButtonPacket;
import com.brandon3055.draconicevolution.common.network.PlayerDetectorStringPacket;
import com.brandon3055.draconicevolution.common.tileentities.TilePlayerDetectorAdvanced;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GUIPlayerDetector extends GuiContainer {

    public EntityPlayer player;
    private TilePlayerDetectorAdvanced detector;
    public boolean showInvSlots = true;
    private boolean editMode = false;
    private int range = 0;
    private int maxRange = 20;
    private boolean whitelist = false;
    private boolean initScedualed = false;
    private int initTick = 0;
    private String[] names = new String[42];
    private GuiTextField selectedNameText;
    private int selected = -1;
    private boolean outputInverted = false;

    public GUIPlayerDetector(InventoryPlayer invPlayer, TilePlayerDetectorAdvanced detector) {
        super(detector.getGuiContainer(invPlayer));
        this.inventorySlots = new ContainerPlayerDetector(invPlayer, detector, this);

        for (int i = 0; i < names.length; i++) names[i] = "";

        xSize = 176;
        ySize = 198;

        this.detector = detector;
        this.player = invPlayer.player;
        syncWithServer();
    }

    private static final ResourceLocation texture = new ResourceLocation(
            References.MODID.toLowerCase(),
            "textures/gui/PlayerDetector.png");

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GL11.glColor4f(1, 1, 1, 1);

        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        if (editMode) {
            drawTexturedModalRect(guiLeft + 3, guiTop + ySize / 2, 3, 3, xSize - 6, (ySize / 2) - 3);
            drawNameChart(x, y);
        }

        if (showInvSlots) drawTexturedModalRect(guiLeft + 142, guiTop + 19, xSize, 0, 23, 41);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        drawGuiText(x, y);
    }

    @Override
    public void drawScreen(int x, int y, float p_73863_3_) {
        super.drawScreen(x, y, p_73863_3_);
        ArrayList<String> lines = new ArrayList<String>();
        lines.add("Camouflage");
        if ((x - guiLeft > 142 && x - guiLeft < 160) && (y - guiTop > 19 && y - guiTop < 37) && showInvSlots)
            drawHoveringText(lines, x, y, fontRendererObj);
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        if (!editMode) {
            String wt = whitelist ? "White List" : "Black List";
            int centre = width / 2;
            buttonList.add(new GuiButton(0, centre - 20 - 20, guiTop + 20, 20, 20, "+"));
            buttonList.add(new GuiButton(1, centre + 20, guiTop + 20, 20, 20, "-"));
            buttonList.add(new GuiButton(3, centre - 40, guiTop + 45, 60, 20, wt));
            buttonList.add(new GuiButton(4, centre + 20, guiTop + 45, 20, 20, "!"));
            buttonList.add(new GuiButton(6, centre - 40, guiTop + 70, 80, 20, "Invert Output"));
        } else {
            buttonList.add(new GuiButton(5, guiLeft - 40, guiTop + ySize - 20, 40, 20, "Back"));
        }

        selectedNameText = new GuiTextField(fontRendererObj, 4, -12, 168, 12);
        selectedNameText.setTextColor(-1);
        selectedNameText.setDisabledTextColour(-1);
        selectedNameText.setEnableBackgroundDrawing(true);
        selectedNameText.setMaxStringLength(40);
        selectedNameText.setVisible(editMode);

        // ID
        // buttonList.add(new GuiButton(0, guiLeft + 85, guiTop , 85, 20, "sgASGgs"));

        // syncWithServer();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0: // Range +
                range = (range < maxRange) ? range + 1 : maxRange;
                DraconicEvolution.network.sendToServer(new PlayerDetectorButtonPacket((byte) 0, (byte) range));
                break;
            case 1: // Range -
                range = (range > 1) ? range - 1 : 1;
                DraconicEvolution.network.sendToServer(new PlayerDetectorButtonPacket((byte) 0, (byte) range));
                break;
            case 3: // White List -
                initScedualed = true;
                editMode = true;
                showInvSlots = false;
                ((ContainerPlayerDetector) this.inventorySlots).updateContainerSlots();
                break;
            case 4: // Toggle White List -
                whitelist = !whitelist;
                initScedualed = true;
                byte val = (byte) (whitelist ? 1 : 0);
                DraconicEvolution.network.sendToServer(new PlayerDetectorButtonPacket((byte) 1, val));
                break;
            case 5: // Back -
                editMode = false;
                showInvSlots = true;
                ((ContainerPlayerDetector) this.inventorySlots).updateContainerSlots();
                initScedualed = true;
                break;
            case 6: // Back -
                outputInverted = !outputInverted;
                initScedualed = true;
                byte val2 = (byte) (outputInverted ? 1 : 0);
                DraconicEvolution.network.sendToServer(new PlayerDetectorButtonPacket((byte) 2, val2));
                break;
        }

        // DraconicEvolution.channelHandler.sendToServer(new ButtonPacket((byte) 0, true));

    }

    @Override
    protected void keyTyped(char par1, int par2) {
        if (this.selectedNameText.textboxKeyTyped(par1, par2)) return;
        else if (par2 == 28) {
            if (selectedNameText.isFocused()) {
                names[selected] = selectedNameText.getText();
                selectedNameText.setText("");
                selectedNameText.setFocused(false);
                DraconicEvolution.network
                        .sendToServer(new PlayerDetectorStringPacket((byte) selected, names[selected]));
                selected = -1;
            }
        } else super.keyTyped(par1, par2);
    }

    @Override
    public void updateScreen() {
        if (initScedualed) initTick++;
        if (initTick > 1) {
            initTick = 0;
            initScedualed = false;
            initGui();
        }
        super.updateScreen();
    }

    @Override
    protected void mouseClicked(int x, int y, int par3) {
        super.mouseClicked(x, y, par3);

        if (editMode) selectName(x - guiLeft, y - guiTop);

        // this.selectedNameText.mouseClicked(x - guiLeft, y - guiTop, par3);
    }

    private void drawGuiText(int rawX, int rawY) {
        if (!editMode) {
            drawCenteredString(fontRendererObj, "Advanced Player Detector", xSize / 2, 5, 0x00FFFF);

            fontRendererObj.drawString("Range:", 73, 21, 0x000000, false);
            fontRendererObj.drawString("Output Inverted: " + String.valueOf(outputInverted), 33, 97, 0x000000, false);
            if (range < 10) fontRendererObj.drawString("" + range, 85, 31, 0x000000, false);
            else fontRendererObj.drawString("" + range, 82, 31, 0x000000, false);
        } else {
            if (selected != -1) drawCenteredString(fontRendererObj, "Press Enter to save", xSize / 2, -22, 0xFF0000);

            for (int i = 0; i < 21; i++) {
                for (int j = 0; j < 2; j++) {
                    if (i + j * 21 != selected) {
                        String s = names[i + j * 21];
                        if (s.length() > 13) s = s.substring(0, 13) + "...";
                        fontRendererObj.drawString(s, 5 + j * 84, 6 + i * 9, 0x980000);
                    }
                }
            }
        }

        selectedNameText.drawTextBox();
    }

    private void drawNameChart(int rawX, int rawY) {
        int x = rawX - guiLeft;
        int y = rawY - guiTop;

        for (int i = 0; i < 21; i++) {
            drawTexturedModalRect(guiLeft + 4, guiTop + 4 + i * 9, 0, ySize, 186, 10);
        }

        for (int i = 0; i < 21; i++) {
            for (int j = 0; j < 2; j++) {
                if ((x > 4 + j * 84 && x < (xSize / 2) - 1 + j * 82) && (y > 4 + i * 9 && y < 13 + i * 9)
                        || i + j * 21 == selected)
                    drawTexturedModalRect(guiLeft + 5 + j * 84, guiTop + 5 + i * 9, 0, ySize + 10, 82, 8);
            }
        }
    }

    public void selectName(int x, int y) {
        if (initScedualed) return;

        for (int i = 0; i < 21; i++) {
            for (int j = 0; j < 2; j++) {
                if ((x > 4 + j * 84 && x < (xSize / 2) - 1 + j * 82) && (y > 4 + i * 9 && y < 13 + i * 9)) {
                    selected = i + j * 21;
                    selectedNameText.setText(names[i + j * 21]);
                    selectedNameText.setFocused(true);
                }
            }
        }
    }

    private void syncWithServer() {
        this.whitelist = detector.whiteList;
        for (int i = 0; i < detector.names.length; i++) {
            if (detector.names[i] != null) names[i] = detector.names[i];
        }
        range = detector.range;
        outputInverted = detector.outputInverted;
    }
}
