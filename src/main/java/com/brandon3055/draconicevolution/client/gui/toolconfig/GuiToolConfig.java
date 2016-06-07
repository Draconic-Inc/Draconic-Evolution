package com.brandon3055.draconicevolution.client.gui.toolconfig;

import com.brandon3055.brandonscore.inventory.PlayerSlot;
import com.brandon3055.draconicevolution.api.itemconfig.IConfigurableItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.io.IOException;

import static com.brandon3055.brandonscore.inventory.PlayerSlot.EnumInvCategory.ARMOR;
import static com.brandon3055.brandonscore.inventory.PlayerSlot.EnumInvCategory.MAIN;
import static com.brandon3055.brandonscore.inventory.PlayerSlot.EnumInvCategory.OFF_HAND;

/**
 * Created by brandon3055 on 2/06/2016.
 */
public class GuiToolConfig extends GuiScreen {
    private EntityPlayer player;
    private ToolButton[] armor;
    private ToolButton[] inventory;
    private ToolButton[] offHand;

    public GuiToolConfig(EntityPlayer player){
        this.player = player;
        this.armor = new ToolButton[player.inventory.armorInventory.length];
        this.inventory = new ToolButton[player.inventory.mainInventory.length];
        this.offHand = new ToolButton[player.inventory.offHandInventory.length];
    }

    @Override
    public void initGui() {
        super.initGui();
        int centerX = width / 2;
        int centerY = height / 2;
        buttonList.clear();
        int id = 0;

        for (int i = 0; i < player.inventory.armorInventory.length; i++){
            ItemStack stack = player.inventory.armorInventory[i];
            int x = - 81 + centerX;// + i % 2 * 80;
            int y = - 52 + centerY - i * 18;
            buttonList.add(armor[i] = new ToolButton(id, x, y, 18, 18, stack, new PlayerSlot(i, ARMOR)));
            id++;
        }

        for (int i = 0; i < player.inventory.mainInventory.length; i++){
            ItemStack stack = player.inventory.mainInventory[i];
            int x = - 81 + centerX + i % 9 * 18;
            int y = + 30 + centerY - i / 9 * 18;
            if (i < 9) {
                y += 1;
            }
            buttonList.add(inventory[i] = new ToolButton(id, x, y, 18, 18, stack, new PlayerSlot(i, MAIN)));
            id++;
        }

        for (int i = 0; i < player.inventory.offHandInventory.length; i++){
            ItemStack stack = player.inventory.offHandInventory[i];
            int x = - 100 + centerX;
            int y = + 31 + centerY + i * 18;
            buttonList.add(offHand[i] = new ToolButton(id, x, y, 18, 18, stack, new PlayerSlot(i, OFF_HAND)));
            id++;
        }

    }

    //region Draw

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int centerX = width / 2;
        int centerY = height / 2;
        drawDefaultBackground();

        //region Draw Inventory Back

        //Draw Background
        int posX = centerX - 90;
        int posY = centerY - 115;
        this.drawGradientRect(posX, posY, posX + 172 + 8, posY + 173, 0xaF000000, 0xaF000000);
        posX = centerX - 100 - 8;
        posY = centerY + 31 - 8;
        this.drawGradientRect(posX, posY, posX + 18, posY + 35, 0xaF000000, 0xaF000000);
        //Draw Main Inventory
        posX = centerX - 81;
        posY = centerY - 24;
        this.drawGradientRect(posX - 1, posY - 1, posX + 163, posY + 74, 0xFF00FFFF, 0xFF00FFFF);
        this.drawGradientRect(posX, posY, posX + 162, posY + 73, 0xFF000000, 0xFF000000);
        posY = centerY + 30;
        this.drawGradientRect(posX - 1, posY, posX + 163, posY + 1, 0xFF00FFFF, 0xFF00FFFF);
        //Draw Off Hand
        posX = centerX - 100;
        posY = centerY + 31;
        this.drawGradientRect(posX - 1, posY - 1, posX + 18, posY + 19, 0xFF00FFFF, 0xFF00FFFF);
        this.drawGradientRect(posX, posY, posX + 18, posY + 18, 0xFF000000, 0xFF000000);
        //Draw Armor
        posX = centerX - 81;
        posY = centerY - 106;
        this.drawGradientRect(posX - 1, posY - 1, posX + 19, posY + 73, 0xFF00FFFF, 0xFF00FFFF);
        this.drawGradientRect(posX, posY, posX + 18, posY + 72, 0xFF000000, 0xFF000000);

        //endregion

        //region Draw Player

        int playerX = centerX;
        int playerY = centerY - 42;

        this.drawGradientRect(playerX - 21, playerY - 65, playerX + 21, playerY + 9, 0xFF00FFFF, 0xFF00FFFF);
        this.drawGradientRect(playerX - 20, playerY - 64, playerX + 20, playerY + 8, 0xFF000000, 0xFF000000);

        GuiInventory.drawEntityOnScreen(playerX, playerY, 30, playerX - mouseX, playerY - 50 - mouseY, player);
        //endregion

        super.drawScreen(mouseX, mouseY, partialTicks);

        for (GuiButton button : buttonList){
            if (button instanceof ToolButton){
                ((ToolButton)button).drawToolTips(mc, mouseX, mouseY);
            }
        }
    }




    //endregion

    //region Interaction

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button instanceof ToolButton){
            ToolButton toolButton = (ToolButton) button;

            if (toolButton.stack != null && toolButton.stack.getItem() instanceof IConfigurableItem){
                mc.displayGuiScreen(new GuiConfigureTool(this, player, ((ToolButton) button).stack, ((ToolButton) button).slot));
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1 || mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode))
        {
            this.mc.displayGuiScreen((GuiScreen)null);

            if (this.mc.currentScreen == null)
            {
                this.mc.setIngameFocus();
            }
        }
    }

    //endregion

    //region Helpers

    public boolean isItemValid(ItemStack stack){
        return stack != null && stack.getItem() instanceof IConfigurableItem;
    }

    //endregion

    public class ToolButton extends GuiButton {
        public ItemStack stack = null;
        public final PlayerSlot slot;

        public ToolButton(int buttonId, int x, int y, int widthIn, int heightIn, ItemStack stack, PlayerSlot slot) {
            super(buttonId, x, y, widthIn, heightIn, "");
            this.stack = stack;
            this.slot = slot;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
           // super.drawButton(mc, mouseX, mouseY);

            if (stack != null){
                if (hovered){
                    int hoverColour = stack.getItem() instanceof IConfigurableItem ? 0x3000ffff : 0xFFFF0000;

                    GlStateManager.disableLighting();
                    GlStateManager.disableDepth();
                    int j1 = xPosition;
                    int k1 = yPosition;
                    GlStateManager.colorMask(true, true, true, false);
                    this.drawGradientRect(j1, k1, j1 + 18, k1 + 18, hoverColour, hoverColour);
                    GlStateManager.colorMask(true, true, true, true);
                    GlStateManager.enableLighting();
                    GlStateManager.enableDepth();
                }

                mc.getRenderItem().renderItemAndEffectIntoGUI(mc.thePlayer, stack, xPosition + 1, yPosition + 1);
                mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj, stack, xPosition + 1, yPosition + 1, stack.stackSize > 1 ? String.valueOf(stack.stackSize) : "");
            }

            if (stack == null || !(stack.getItem() instanceof IConfigurableItem)){
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.colorMask(true, true, true, false);
                int j1 = xPosition;
                int k1 = yPosition;
                this.drawGradientRect(j1, k1, j1 + 18, k1 + 18, 0xA0000000, 0xA0000000);
                GlStateManager.colorMask(true, true, true, true);
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }

        }

        public void drawToolTips(Minecraft mc, int mouseX, int mouseY){
            if (hovered && stack != null && stack.getItem() instanceof IConfigurableItem) {
                renderToolTip(stack, mouseX, mouseY);
            }
        }

        @Override
        public void playPressSound(SoundHandler soundHandlerIn) {
            if (stack != null && stack.getItem() instanceof IConfigurableItem) {
                super.playPressSound(soundHandlerIn);
            }
        }
    }
}
