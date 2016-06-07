package com.brandon3055.draconicevolution.client.gui.toolconfig;

import com.brandon3055.brandonscore.client.utills.GuiHelper;
import com.brandon3055.brandonscore.inventory.PlayerSlot;
import com.brandon3055.brandonscore.utils.InfoHelper;
import com.brandon3055.draconicevolution.api.itemconfig.IConfigurableItem;
import com.brandon3055.draconicevolution.api.itemconfig.IItemConfigField;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by brandon3055 on 7/06/2016.
 */
public class GuiConfigureTool extends GuiScreen {

    private GuiToolConfig parent;
    private final EntityPlayer player;
    private final ItemStack stack;
    private final PlayerSlot slot;
    private LinkedHashMap<String, IItemConfigField> fields = new LinkedHashMap<String, IItemConfigField>();
    private FieldButton[] fieldButtons;
    public int selected = -1;

    public GuiConfigureTool(GuiToolConfig parent, EntityPlayer player, ItemStack stack, PlayerSlot slot){
        this.parent = parent;
        this.player = player;
        this.stack = stack;
        this.slot = slot;
    }

    //region Logic

    @Override
    public void initGui() {
        super.initGui();
        if (!confirmValidation()){
            return;
        }

        int centerX = width / 2;
        int centerY = height / 2;

        fields.clear();
        IConfigurableItem item = (IConfigurableItem)stack.getItem();
        item.getFields(stack, fields);
        fieldButtons = new FieldButton[fields.size()];

        int i = 0;
        for (String name : fields.keySet()){
            int bHeight = 180 / fields.size();
            if (bHeight > 20){
                bHeight = 20;
            }

            int x = -159 + centerX + i % 2 * 180;
            int y = -103 + centerY + i / 2 * bHeight;

            buttonList.add(fieldButtons[i] = new FieldButton(i, x, y, 138, bHeight - 1, fields.get(name), this));

            i++;
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        confirmValidation();
    }

    private boolean confirmValidation(){
        if (!parent.isItemValid(stack) || !stack.isItemEqual(slot.getStackInSlot(player))){
            this.mc.displayGuiScreen(null);

            if (this.mc.currentScreen == null)
            {
                this.mc.setIngameFocus();
            }
            return false;
        }
        return true;
    }

    //endregion

    //region Render

    @SuppressWarnings("unchecked")
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        int centerX = width / 2;
        int centerY = height / 2;

        //ToDO Drop down for selectables


        //region Draw Background & Item

        GuiHelper.drawColouredRect(centerX - 160, centerY - 120, 320, 235, 0xAF000000);
        int border = 0xFF004444;
        GuiHelper.drawColouredRect(centerX - 160, centerY - 122, 320, 2, border);
        GuiHelper.drawColouredRect(centerX - 160, centerY + 115, 320, 2, border);
        GuiHelper.drawColouredRect(centerX - 162, centerY - 122, 2, 239, border);
        GuiHelper.drawColouredRect(centerX + 160, centerY - 122, 2, 239, border);

        GuiHelper.drawColouredRect(centerX - 160, centerY - 8, 320, 2, border);

        GuiHelper.drawColouredRect(centerX - 160, centerY + 12, 320, 2, border);

        //Draw item Background
        GuiHelper.drawColouredRect(centerX - 20, centerY - 103, 40, 92, 0xFFFF0000);
        GuiHelper.drawColouredRect(centerX - 19, centerY - 102, 38, 90, 0xFF000000);

        renderItem(centerX, centerY - 60, partialTicks);

        drawCenteredString(fontRendererObj, I18n.format("gui.de.configureItem.txt"), centerX, centerY - 116, InfoHelper.GUI_TITLE);

        //endregion

        //region Draw Description

        if (selected != -1 && fieldButtons != null && selected >= 0 && selected < fieldButtons.length){
            List<String> lines = fontRendererObj.listFormattedStringToWidth(I18n.format(fieldButtons[selected].field.getDescription()), 310);
            for (String line : lines){
                fontRendererObj.drawString(line, centerX - 159, centerY + 16 + (lines.indexOf(line) * 12), 0xFFFFFF);
            }
        }

        //endregion

        super.drawScreen(mouseX, mouseY, partialTicks);

        for (GuiButton button : buttonList){
            if (button instanceof FieldButton){
                ((FieldButton)button).drawToolTips(mc, mouseX, mouseY);
            }
        }
    }

    private void renderItem(int x, int y, float partialTicks){
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 500);
        GlStateManager.disableCull();

        GlStateManager.scale(24, 24, 24);
        GlStateManager.rotate((ClientEventHandler.elapsedTicks + partialTicks) * 2F, 0, 1, 0);
        GlStateManager.rotate(180, 1, 0, 0);
        GlStateManager.rotate(45, 0, 0, 1);

        //GlStateManager.
        RenderHelper.enableStandardItemLighting();
        mc.getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.NONE);


        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    //endregion

    //region Interaction

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        selected = button.id;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        if (mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode)){
            mc.displayGuiScreen(parent);
        }
    }

    //endregion

    //region Classes

    public class FieldButton extends GuiButton {
        public final IItemConfigField field;
        private final GuiConfigureTool gui;

        public FieldButton(int buttonId, int x, int y, int width, int height, IItemConfigField field, GuiConfigureTool gui) {
            super(buttonId, x, y, width, height, "");
            this.field = field;
            this.gui = gui;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;


            int back = 0xFF000000;
            GuiHelper.drawColouredRect(xPosition + 1, yPosition + 1, width - 2, height - 2, back);
            int border = hovered || gui.selected == id ? 0xFF009999 : 0xFF220033;
            GuiHelper.drawColouredRect(xPosition, yPosition, width, 1, border);
            GuiHelper.drawColouredRect(xPosition, yPosition + height - 1, width, 1, border);
            GuiHelper.drawColouredRect(xPosition, yPosition, 1, height, border);
            GuiHelper.drawColouredRect(xPosition + width - 1, yPosition, 1, height, border);

            GuiHelper.drawCenteredString(mc.fontRendererObj, I18n.format(field.getUnlocalizedName()), xPosition + width / 2, yPosition + (height / 2) - (mc.fontRendererObj.FONT_HEIGHT / 2), 0xFFFFFF, false);
        }

        public void drawToolTips(Minecraft mc, int mouseX, int mouseY){
//            if (hovered && stack != null && stack.getItem() instanceof IConfigurableItem) {
//                renderToolTip(stack, mouseX, mouseY);
//            }
        }

        @Override
        public void playPressSound(SoundHandler soundHandlerIn) {
            if (gui.selected != id){
                super.playPressSound(soundHandlerIn);
            }
        }
    }

    //endregion
}
