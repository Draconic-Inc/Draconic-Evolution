package com.brandon3055.draconicevolution.client.gui;

import codechicken.nei.VisiblityData;
import codechicken.nei.api.INEIGuiHandler;
import codechicken.nei.api.TaggedInventoryArea;
import com.brandon3055.brandonscore.client.utills.GuiHelper;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.brandon3055.draconicevolution.common.container.ContainerDraconiumChest;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.network.ButtonPacket;
import com.brandon3055.draconicevolution.common.tileentities.TileDraconiumChest;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
@Optional.Interface(iface = "codechicken.nei.api.INEIGuiHandler", modid = "NotEnoughItems")
public class GUIDraconiumChest extends GuiContainer implements INEIGuiHandler {
    public EntityPlayer player;
    private TileDraconiumChest tile;
    private static final ResourceLocation textureLeft =
            new ResourceLocation(References.MODID.toLowerCase(), "textures/gui/DraconicChestLeft.png");
    private static final ResourceLocation textureRight =
            new ResourceLocation(References.MODID.toLowerCase(), "textures/gui/DraconicChestRight.png");
    private int lastAutoFeed = -1;

    public GUIDraconiumChest(InventoryPlayer invPlayer, TileDraconiumChest tile) {
        super(new ContainerDraconiumChest(invPlayer, tile));

        this.xSize = 481;
        this.ySize = 256;

        this.tile = tile;
        this.player = invPlayer.player;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(textureRight);
        drawTexturedModalRect(guiLeft + 256, guiTop, 0, 0, 225, ySize);
        Minecraft.getMinecraft().getTextureManager().bindTexture(textureLeft);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, 256, ySize);
        drawTexturedModalRect(guiLeft + 140, guiTop + 216, 3, 176, 16, 23);
        drawTexturedModalRect(guiLeft + 387, guiTop + 236, 44, 177, 90, 16);
        drawTexturedModalRect(guiLeft + 387, guiTop + 180, 44, 177, 90, 16);

        if (tile.lockOutputSlots) {
            ResourceHandler.bindResource("textures/gui/Widgets.png");
            GL11.glColor4f(1f, 1f, 1f, 1f);
            for (int i = 0; i < 5; i++) drawTexturedModalRect(guiLeft + 385 + i * 18, guiTop + 158, 138, 18, 18, 18);
            Minecraft.getMinecraft().getTextureManager().bindTexture(textureLeft);
        }

        int arrowHight = (int) (((float) tile.smeltingProgressTime / (float) tile.smeltingCompleateTime) * 22f);
        drawTexturedModalRect(
                guiLeft + 140, guiTop + 192 + 22 - arrowHight, 140, 216 + 22 - arrowHight, 16, arrowHight);

        Minecraft.getMinecraft().getTextureManager().bindTexture(textureRight);
        int energyWidth = (int) (((float) tile.getEnergyStored(ForgeDirection.DOWN)
                        / (float) tile.getMaxEnergyStored(ForgeDirection.DOWN))
                * 90f);
        drawTexturedModalRect(guiLeft + 44, guiTop + 235, 131, 236, energyWidth, 16);

        int flameHight = (int) (((float) tile.smeltingBurnSpeed / (float) tile.smeltingMaxBurnSpeed) * 13f);
        // flameHight = tile.smeltingProgressTime <= 0 ? 0 : Math.min(flameHight, 13);
        drawTexturedModalRect(guiLeft + 45, guiTop + 217 + 13 - flameHight, 132, 180 + 13 - flameHight, 88, flameHight);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        fontRendererObj.drawString(tile.getInventoryName(), 4, 4, 0x222222);
        // fontRendererObj.drawStringWithShadow(StatCollector.translateToLocal("button.de.chestAutoFeed.txt"), 4, 180,
        // 0x00FFFF);

        ArrayList<String> list = new ArrayList<String>();
        list.add(String.valueOf(tile.getEnergyStored(ForgeDirection.DOWN)) + "/"
                + String.valueOf(tile.getMaxEnergyStored(ForgeDirection.DOWN)) + "RF");
        if (GuiHelper.isInRect(44, 235, 90, 16, x - guiLeft, y - guiTop))
            drawHoveringText(list, x - guiLeft, y - guiTop, fontRendererObj);
        list.clear();
        String s = StatCollector.translateToLocal("button.de.chestAutoFeed0.txt");
        if (GuiHelper.isInRect(4, 180, 38, 12, x - guiLeft, y - guiTop)) {
            list.add(s.substring(s.indexOf(".") + 1));
            drawHoveringText(list, x - guiLeft, y - guiTop, fontRendererObj);
        }
        if (GuiHelper.isInRect(4, 193, 38, 12, x - guiLeft, y - guiTop)) {
            s = StatCollector.translateToLocal("button.de.chestAutoFeed1.txt");
            list.add(s.substring(s.indexOf(".") + 1));
            drawHoveringText(list, x - guiLeft, y - guiTop, fontRendererObj);
        }
        if (GuiHelper.isInRect(4, 206, 38, 12, x - guiLeft, y - guiTop)) {
            s = StatCollector.translateToLocal("button.de.chestAutoFeed2.txt");
            list.add(s.substring(s.indexOf(".") + 1));
            drawHoveringText(list, x - guiLeft, y - guiTop, fontRendererObj);
        }
        if (GuiHelper.isInRect(4, 219, 38, 12, x - guiLeft, y - guiTop)) {
            s = StatCollector.translateToLocal("button.de.chestAutoFeed3.txt");
            list.add(s.substring(s.indexOf(".") + 1));
            drawHoveringText(list, x - guiLeft, y - guiTop, fontRendererObj);
        }
        if (GuiHelper.isInRect(398, 180, 70, 12, x - guiLeft, y - guiTop)) {
            s = StatCollector.translateToLocal("button.de.chestLockOutput.txt");
            list.add(s.substring(s.indexOf(".") + 1));
            drawHoveringText(list, x - guiLeft, y - guiTop, fontRendererObj);
        }

        RenderHelper.enableGUIStandardItemLighting();
    }

    @Override
    public void initGui() {
        super.initGui();
        int posX = (this.width - xSize) / 2;
        int posY = (this.height - ySize) / 2;
        buttonList.clear();
        String s = StatCollector.translateToLocal("button.de.chestAutoFeed0.txt");
        buttonList.add(new GuiButtonAHeight(0, posX + 4, posY + 180, 38, 12, s.substring(0, s.indexOf("."))));
        s = StatCollector.translateToLocal("button.de.chestAutoFeed1.txt");
        buttonList.add(new GuiButtonAHeight(1, posX + 4, posY + 193, 38, 12, s.substring(0, s.indexOf("."))));
        s = StatCollector.translateToLocal("button.de.chestAutoFeed2.txt");
        buttonList.add(new GuiButtonAHeight(2, posX + 4, posY + 206, 38, 12, s.substring(0, s.indexOf("."))));
        s = StatCollector.translateToLocal("button.de.chestAutoFeed3.txt");
        buttonList.add(new GuiButtonAHeight(3, posX + 4, posY + 219, 38, 12, s.substring(0, s.indexOf("."))));
        s = StatCollector.translateToLocal("button.de.chestLockOutput.txt");
        buttonList.add(new GuiButtonAHeight(4, posX + 398, posY + 180, 70, 12, s.substring(0, s.indexOf("."))));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0)
            DraconicEvolution.network.sendToServer(new ButtonPacket(ButtonPacket.ID_DRACONIUMCHEST0, false));
        else if (button.id == 1)
            DraconicEvolution.network.sendToServer(new ButtonPacket(ButtonPacket.ID_DRACONIUMCHEST1, false));
        else if (button.id == 2)
            DraconicEvolution.network.sendToServer(new ButtonPacket(ButtonPacket.ID_DRACONIUMCHEST2, false));
        else if (button.id == 3)
            DraconicEvolution.network.sendToServer(new ButtonPacket(ButtonPacket.ID_DRACONIUMCHEST3, false));
        else if (button.id == 4)
            DraconicEvolution.network.sendToServer(new ButtonPacket(ButtonPacket.ID_DRACONIUMCHEST4, false));
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (lastAutoFeed != tile.smeltingAutoFeed) {
            lastAutoFeed = tile.smeltingAutoFeed;
            ((GuiButton) buttonList.get(0)).enabled = lastAutoFeed != 0;
            ((GuiButton) buttonList.get(1)).enabled = lastAutoFeed != 1;
            ((GuiButton) buttonList.get(2)).enabled = lastAutoFeed != 2;
            ((GuiButton) buttonList.get(3)).enabled = lastAutoFeed != 3;
        }
    }

    @Override
    public VisiblityData modifyVisiblity(GuiContainer guiContainer, VisiblityData visiblityData) {
        return null;
    }

    @Override
    public Iterable<Integer> getItemSpawnSlots(GuiContainer guiContainer, ItemStack stack) {
        return null;
    }

    @Override
    public List<TaggedInventoryArea> getInventoryAreas(GuiContainer guiContainer) {
        return Collections.emptyList();
    }

    @Override
    public boolean handleDragNDrop(GuiContainer guiContainer, int i, int i2, ItemStack stack, int i3) {
        return false;
    }

    @Override
    public boolean hideItemPanelSlot(GuiContainer guiContainer, int i, int i2, int i3, int i4) {
        return false;
    }
}
