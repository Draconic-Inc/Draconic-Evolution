package com.brandon3055.draconicevolution.client.gui.toolconfig;

import com.brandon3055.brandonscore.client.gui.modulargui_old.ModularGuiContainer;
import com.brandon3055.brandonscore.client.gui.modulargui_old.modularelements.MGuiBorderedRect;
import com.brandon3055.brandonscore.client.gui.modulargui_old.modularelements.MGuiLabel;
import com.brandon3055.brandonscore.inventory.PlayerSlot;
import com.brandon3055.brandonscore.utils.InfoHelper;
import com.brandon3055.draconicevolution.inventory.ContainerJunkFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraftforge.items.IItemHandler;

import java.io.IOException;

/**
 * Created by brandon3055 on 21/12/2017.
 */
public class GuiJunkFilter extends ModularGuiContainer<ContainerJunkFilter> {

    private GuiScreen parent;
    private final EntityPlayer player;
    private final PlayerSlot slot;
    private IItemHandler itemHandler;

    public GuiJunkFilter(EntityPlayer player, PlayerSlot slot, IItemHandler itemHandler) {
        super(new ContainerJunkFilter(player, slot, itemHandler));
        this.itemHandler = itemHandler;
        this.parent = Minecraft.getMinecraft().currentScreen;
        this.player = player;
        this.slot = slot;
        xSize = 176;
        ySize = 112 + (18 * (itemHandler.getSlots() / 9));
    }


    @Override
    public void initGui() {
        super.initGui();
        manager.clear();

        manager.add(new MGuiBorderedRect(this, getGuiLeft(), getGuiTop(), xSize(), ySize()).setBorderColour(0xFF005555).setFillColour(0xAF000000).setBorderWidth(2));
        manager.add(new MGuiLabel(this, getGuiLeft(), getGuiTop() + 3, xSize(), 12, I18n.format("gui.de.junkFilter.name")).setTextColour(InfoHelper.GUI_TITLE));

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            int x = i % 9 * 18;
            int y = i / 9 * 18;
            if (i / 9 == 3) y += 4;
            manager.add(new MGuiBorderedRect(this, guiLeft() + x + 7, guiTop() + y + 20, 18, 18).setFillColour(0xFF8b8b8b).setBorderColour(0xFFFF0000));
        }

        for (int i = 0; i < 36; i++) {
            int x = i % 9 * 18;
            int y = i / 9 * 18;
            if (i / 9 == 3) y += 4;
            manager.add(new MGuiBorderedRect(this, guiLeft() + x + 7, (guiTop() + ySize() - 84) + y, 18, 18).setFillColour(0xFF8b8b8b));
        }

        manager.initElements();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (manager.keyTyped(typedChar, keyCode)) {
            return;
        }

        if (keyCode == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode)) {
            this.mc.player.closeScreen();
            if (parent != null) {
                Minecraft.getMinecraft().displayGuiScreen(parent);
            }
        }

        this.checkHotbarKeys(keyCode);

        if (this.hoveredSlot != null && this.hoveredSlot.getHasStack()) {
            if (this.mc.gameSettings.keyBindPickBlock.isActiveAndMatches(keyCode)) {
                this.handleMouseClick(this.hoveredSlot, this.hoveredSlot.slotNumber, 0, ClickType.CLONE);
            }
            else if (this.mc.gameSettings.keyBindDrop.isActiveAndMatches(keyCode)) {
                this.handleMouseClick(this.hoveredSlot, this.hoveredSlot.slotNumber, isCtrlKeyDown() ? 1 : 0, ClickType.THROW);
            }
        }
    }
}
