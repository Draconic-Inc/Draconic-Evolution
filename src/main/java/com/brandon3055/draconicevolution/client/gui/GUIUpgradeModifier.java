package com.brandon3055.draconicevolution.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import com.brandon3055.brandonscore.client.utills.GuiHelper;
import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.container.ContainerUpgradeModifier;
import com.brandon3055.draconicevolution.common.tileentities.TileUpgradeModifier;
import com.brandon3055.draconicevolution.common.utills.IUpgradableItem;
import com.brandon3055.draconicevolution.common.utills.IUpgradableItem.EnumUpgrade;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GUIUpgradeModifier extends GuiContainer {

    public EntityPlayer player;
    private TileUpgradeModifier tile;

    public boolean inUse = false;
    private IUpgradableItem upgradableItem = null;
    private ItemStack stack = null;
    private List<EnumUpgrade> itemUpgrades = new ArrayList<EnumUpgrade>();
    private ContainerUpgradeModifier containerEM;

    public GUIUpgradeModifier(InventoryPlayer invPlayer, TileUpgradeModifier tile,
            ContainerUpgradeModifier containerEM) {
        super(containerEM);
        this.containerEM = containerEM;

        xSize = 176;
        ySize = 190;

        this.tile = tile;
        this.player = invPlayer.player;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        ResourceHandler.bindResource("textures/gui/UpgradeModifier.png");

        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        drawTexturedModalRect(guiLeft + 70, guiTop + 6, 60, 106, 100, 50);
        drawTexturedModalRect(guiLeft + 70, guiTop + 56, 60, 106, 100, 50);

        GL11.glPushMatrix();
        GL11.glTranslated(guiLeft + 70, guiTop + 6, 0);
        GL11.glTranslatef(50, 50, 0);
        GL11.glRotatef(tile.rotation + (f * tile.rotationSpeed), 0, 0, 1);
        GL11.glTranslatef(-50, -50, 0);
        drawTexturedModalRect(0, 0, 70, 6, 100, 100);
        GL11.glPopMatrix();

        if (!inUse) {
            drawTexturedModalRect(guiLeft + 3, guiTop + 77, 60, 106, 56, 55);
            drawTexturedModalRect(guiLeft + 3, guiTop + 132, 60, 106, 56, 55);
        } else {
            drawFlippedTexturedModalRect(guiLeft + 59, guiTop + 77, 3, 77, 56, 110);
            drawTexturedModalRect(guiLeft + 115, guiTop + 77, 3, 77, 56, 110);
            drawFlippedTexturedModalRect(guiLeft + 171, guiTop + 77, 57, 77, 2, 110);
        }

        if (!inUse) drawSlots();
        else renderUpgrades(x, y);

        if (inUse)
            drawHoveringText(upgradableItem.getUpgradeStats(stack), guiLeft + xSize - 9, guiTop + 17, fontRendererObj);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        drawCenteredString(fontRendererObj, tile.getBlockType().getLocalizedName(), xSize / 2, -9, 0x00FFFF);
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) {}

    private int coreSlots = 0;
    private int coreTier = 0;
    private int usedSlots = 0;
    private boolean[] coreInInventory = new boolean[4];

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (tile.getStackInSlot(0) != null && tile.getStackInSlot(0).getItem() instanceof IUpgradableItem) {
            stack = tile.getStackInSlot(0);
            upgradableItem = (IUpgradableItem) stack.getItem();
            itemUpgrades = upgradableItem.getUpgrades(stack);
            inUse = true;
            coreSlots = upgradableItem.getUpgradeCap(stack);
            coreTier = upgradableItem.getMaxTier(stack);
            usedSlots = 0;
            coreInInventory[0] = player.inventory.hasItem(ModItems.draconicCore);
            coreInInventory[1] = player.inventory.hasItem(ModItems.wyvernCore);
            coreInInventory[2] = player.inventory.hasItem(ModItems.awakenedCore);
            coreInInventory[3] = player.inventory.hasItem(ModItems.chaoticCore);

            for (EnumUpgrade upgrade : upgradableItem.getUpgrades(stack)) {
                for (Integer i : upgrade.getCoresApplied(stack)) usedSlots += i;
            }

        } else inUse = false;
    }

    private void drawSlots() {
        ResourceHandler.bindResource("textures/gui/Widgets.png");

        int xPos = guiLeft + ((xSize - 162) / 2);
        int yPos = guiTop + 110;

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                drawTexturedModalRect(xPos + x * 18, yPos + y * 18, 138, 0, 18, 18);
            }
        }

        for (int x = 0; x < 9; x++) {
            drawTexturedModalRect(xPos + x * 18, yPos + 56, 138, 0, 18, 18);
        }

        drawTexturedModalRect(guiLeft + 111, guiTop + 47, 138, 0, 18, 18);
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);

        for (EnumUpgrade upgrade : itemUpgrades) {
            int xIndex = itemUpgrades.indexOf(upgrade);
            int spacing = (xSize - 6) / itemUpgrades.size();
            int xPos = guiLeft + (xIndex * spacing) + ((spacing - 23) / 2) + 4;
            int yPos = guiTop + 90;

            int[] appliedCores = upgrade.getCoresApplied(tile.getStackInSlot(0));

            for (int i = 0; i <= coreTier; i++) {
                // Check + buttons
                if (coreInInventory[i] && coreSlots > usedSlots
                        && GuiHelper.isInRect(xPos, yPos + 33 + i * 18, 8, 8, x, y)
                        && upgrade.getUpgradePoints(stack) < upgradableItem.getMaxUpgradePoints(upgrade.index, stack)) {
                    containerEM.sendObjectToServer(null, upgrade.index, i * 2);
                    Minecraft.getMinecraft().getSoundHandler().playSound(
                            PositionedSoundRecord
                                    .func_147674_a(ResourceHandler.getResourceWOP("gui.button.press"), 1.0F));
                }

                // Check - buttons
                if (appliedCores[i] > 0 && GuiHelper.isInRect(xPos + 16, yPos + 33 + i * 18, 8, 8, x, y)) {
                    containerEM.sendObjectToServer(null, upgrade.index, 1 + i * 2);
                    Minecraft.getMinecraft().getSoundHandler().playSound(
                            PositionedSoundRecord
                                    .func_147674_a(ResourceHandler.getResourceWOP("gui.button.press"), 1.0F));
                }
            }
        }
    }

    private void renderUpgrades(int x, int y) {
        // First Draw
        for (EnumUpgrade upgrade : itemUpgrades) {
            int xIndex = itemUpgrades.indexOf(upgrade);
            int spacing = (xSize - 6) / itemUpgrades.size();
            int xPos = guiLeft + (xIndex * spacing) + ((spacing - 23) / 2) + 4;
            int yPos = guiTop + 90;

            ResourceHandler.bindResource("textures/gui/UpgradeModifier.png");

            drawTexturedModalRect(xPos, yPos, 0, 190, 24, 24);
            drawTexturedModalRect(xPos + 3, yPos + 3, upgrade.index * 18, 220, 18, 18);
            drawTexturedModalRect(xPos + 2, yPos - 10, 126, 190, 20, 11);

            int[] appliedCores = upgrade.getCoresApplied(tile.getStackInSlot(0));

            for (int i = 0; i <= coreTier; i++) {
                drawTexturedModalRect(xPos + 3, yPos + 24 + i * 18, 24 + i * 18, 190, 18, 18);
                drawTexturedModalRect(xPos + 3, yPos + 24 + i * 18, 24 + i * 18, 190, 18, 18);

                GL11.glEnable(GL11.GL_BLEND);
                GL11.glColor4f(0F, 0F, 0F, 0.9F);
                if (appliedCores[i] < 10) drawTexturedModalRect(xPos + 8, yPos + 28 + i * 18, 3, 3, 7, 9);
                else drawTexturedModalRect(xPos + 5, yPos + 28 + i * 18, 3, 3, 13, 9);
                GL11.glColor4f(1F, 1F, 1F, 1F);
                GL11.glDisable(GL11.GL_BLEND);

                // Draw + buttons
                if (coreSlots > usedSlots
                        && upgrade.getUpgradePoints(stack) < upgradableItem.getMaxUpgradePoints(upgrade.index, stack)) {
                    boolean hovering = GuiHelper.isInRect(xPos, yPos + 33 + i * 18, 8, 8, x, y);
                    if (!coreInInventory[i]) drawTexturedModalRect(xPos, yPos + 33 + i * 18, 24, 208, 8, 8);
                    else drawTexturedModalRect(xPos, yPos + 33 + i * 18, 32 + (hovering ? 8 : 0), 208, 8, 8);
                }

                // Draw - buttons
                if (appliedCores[i] > 0) {
                    boolean hovering = GuiHelper.isInRect(xPos + 16, yPos + 33 + i * 18, 8, 8, x, y);
                    drawTexturedModalRect(xPos + 16, yPos + 33 + i * 18, 56 + (hovering ? 8 : 0), 208, 8, 8);
                }
            }
            for (int i = 0; i <= coreTier; i++) fontRendererObj.drawString(
                    String.valueOf(appliedCores[i]),
                    xPos + 12 - fontRendererObj.getStringWidth(String.valueOf(appliedCores[i])) / 2,
                    yPos + 29 + i * 18,
                    0xFFFFFF);
            fontRendererObj.drawString(
                    String.valueOf(upgrade.getUpgradePoints(stack)),
                    xPos + 12 - fontRendererObj.getStringWidth(String.valueOf(upgrade.getUpgradePoints(stack))) / 2,
                    yPos - 8,
                    0xFFFFFF);
        }

        fontRendererObj.drawStringWithShadow(
                StatCollector.translateToLocal("gui.de.cores.txt"),
                guiLeft + 4,
                guiTop + 4,
                0x00ff00);
        fontRendererObj
                .drawString(StatCollector.translateToLocal("gui.de.cap.txt"), guiLeft + 4, guiTop + 16, 0x000000);
        fontRendererObj.drawString(">" + coreSlots, guiLeft + 4, guiTop + 25, 0x000000);
        fontRendererObj
                .drawString(StatCollector.translateToLocal("gui.de.installed.txt"), guiLeft + 4, guiTop + 37, 0x000000);
        fontRendererObj.drawString(">" + usedSlots, guiLeft + 4, guiTop + 46, 0x000000);
        fontRendererObj
                .drawString(StatCollector.translateToLocal("gui.de.free.txt"), guiLeft + 4, guiTop + 58, 0x000000);
        fontRendererObj.drawString(">" + (coreSlots - usedSlots), guiLeft + 4, guiTop + 67, 0x000000);
    }

    @Override
    public void drawScreen(int x, int y, float f) {
        super.drawScreen(x, y, f);

        if (!inUse) return;
        // Second Draw
        for (EnumUpgrade upgrade : itemUpgrades) {
            int xIndex = itemUpgrades.indexOf(upgrade);
            int spacing = (xSize - 6) / itemUpgrades.size();
            int xPos = guiLeft + (xIndex * spacing) + ((spacing - 23) / 2) + 4;
            int yPos = guiTop + 90;
            int[] appliedCores = upgrade.getCoresApplied(tile.getStackInSlot(0));

            if (GuiHelper.isInRect(xPos, yPos, 24, 24, x, y)) {
                List list = new ArrayList();
                list.add(upgrade.getLocalizedName());
                drawHoveringText(list, x, y, fontRendererObj);
            }

            if (GuiHelper.isInRect(xPos + 3, yPos - 9, 18, 8, x, y)) {
                List list = new ArrayList();
                list.add(
                        StatCollector.translateToLocal("gui.de.basePoints.txt") + ": "
                                + upgradableItem.getBaseUpgradePoints(upgrade.index));
                list.add(
                        StatCollector.translateToLocal("gui.de.maxPoints.txt") + ": "
                                + upgradableItem.getMaxUpgradePoints(upgrade.index, stack));
                list.add(StatCollector.translateToLocal("gui.de.pointCost.txt") + ": " + upgrade.pointConversion);
                drawHoveringText(list, x, y, fontRendererObj);
            }

            for (int i = 0; i <= coreTier; i++) {
                if (GuiHelper.isInRect(xPos + 9, yPos + 25 + i * 18, 6, 15, x, y)) {
                    List list = new ArrayList();
                    double value = Math.pow(2, i) / upgrade.pointConversion;
                    String string = StatCollector.translateToLocal("gui.de.value.txt") + ": "
                            + value
                            + " "
                            + (value == 1 ? StatCollector.translateToLocal("gui.de.point.txt")
                                    : StatCollector.translateToLocal("gui.de.points.txt"));
                    list.add(string.replace(".0", ""));
                    drawHoveringText(list, x, y, fontRendererObj);
                }

                // Draw Button Text (add)
                if (coreSlots > usedSlots && GuiHelper.isInRect(xPos, yPos + 33 + i * 18, 8, 8, x, y)) {
                    List list = new ArrayList<String>();
                    if (coreInInventory[i]) list.add(StatCollector.translateToLocal("gui.de.addCore.txt"));
                    else list.add(StatCollector.translateToLocal("gui.de.noCoresInInventory" + i + ".txt"));
                    drawHoveringText(list, x, y, fontRendererObj);
                }

                // Draw Button Text (remove)
                if (appliedCores[i] > 0 && GuiHelper.isInRect(xPos + 16, yPos + 33 + i * 18, 8, 8, x, y)) {
                    List list = new ArrayList<String>();
                    if (coreInInventory[i]) list.add(StatCollector.translateToLocal("gui.de.removeCore.txt"));
                    drawHoveringText(list, x, y, fontRendererObj);
                }
            }
        }
    }

    public void drawFlippedTexturedModalRect(int xPos, int yPos, int texXPos, int texYPos, int xSize, int ySize) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(
                (double) (xPos),
                (double) (yPos + ySize),
                (double) this.zLevel,
                (double) ((float) (texXPos + xSize) * f),
                (double) ((float) (texYPos + ySize) * f1));
        tessellator.addVertexWithUV(
                (double) (xPos + xSize),
                (double) (yPos + ySize),
                (double) this.zLevel,
                (double) ((float) (texXPos) * f),
                (double) ((float) (texYPos + ySize) * f1));
        tessellator.addVertexWithUV(
                (double) (xPos + xSize),
                (double) (yPos),
                (double) this.zLevel,
                (double) ((float) (texXPos) * f),
                (double) ((float) (texYPos) * f1));
        tessellator.addVertexWithUV(
                (double) (xPos),
                (double) (yPos),
                (double) this.zLevel,
                (double) ((float) (texXPos + xSize) * f),
                (double) ((float) (texYPos) * f1));
        tessellator.draw();
    }
}
