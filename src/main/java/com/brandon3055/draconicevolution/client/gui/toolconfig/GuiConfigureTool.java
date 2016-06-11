package com.brandon3055.draconicevolution.client.gui.toolconfig;

import com.brandon3055.brandonscore.client.utills.GuiHelper;
import com.brandon3055.brandonscore.inventory.PlayerSlot;
import com.brandon3055.brandonscore.utils.InfoHelper;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.itemconfig.IConfigurableItem;
import com.brandon3055.draconicevolution.api.itemconfig.IItemConfigField;
import com.brandon3055.draconicevolution.api.itemconfig.IItemConfigField.EnumButton;
import com.brandon3055.draconicevolution.api.itemconfig.ItemConfigFieldRegistry;
import com.brandon3055.draconicevolution.api.itemconfig.ToolConfigHelper;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.network.PacketConfigureTool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.brandon3055.draconicevolution.api.itemconfig.IItemConfigField.EnumButton.*;

/**
 * Created by brandon3055 on 7/06/2016.
 */
public class GuiConfigureTool extends GuiScreen {

    private GuiToolConfig parent;
    private final EntityPlayer player;
    private final ItemStack stack;
    private final PlayerSlot slot;
    private ItemConfigFieldRegistry fieldRegistry = new ItemConfigFieldRegistry();
    public FieldButton[] fieldButtons;
    public int selected = -1;
    public float partialTick;

    public GuiConfigureTool(GuiToolConfig parent, EntityPlayer player, ItemStack stack, PlayerSlot slot) {
        this.parent = parent;
        this.player = player;
        this.stack = stack;
        this.slot = slot;
    }

    //region Logic

    @Override
    public void initGui() {
        super.initGui();
        if (!confirmValidation()) {
            return;
        }

        int centerX = width / 2;
        int centerY = height / 2;

        fieldRegistry.clear();
        IConfigurableItem item = (IConfigurableItem) stack.getItem();
        item.getFields(stack, fieldRegistry);
        fieldButtons = new FieldButton[fieldRegistry.size()];

        int i = 0;
        for (IItemConfigField field : fieldRegistry.getFields()) {
            int bHeight = 180 / fieldRegistry.size();
            if (bHeight > 20) {
                bHeight = 20;
            }

            int x = -159 + centerX + i % 2 * 180;
            int y = -103 + centerY + i / 2 * bHeight;

            buttonList.add(fieldButtons[i] = new FieldButton(i, x, y, 138, bHeight - 1, field, this));

            i++;
        }

        buttonList.add(new AdjusterButton(i, centerX - 160, centerY - 6, 320, 18, this));
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (confirmValidation() && fieldButtons != null && selected >= 0 && selected < fieldButtons.length) {
            fieldButtons[selected].field.readFromNBT(ToolConfigHelper.getFieldStorage(slot.getStackInSlot(player)));
        }

        for (GuiButton button : buttonList){
            if (button instanceof AdjusterButton){
                ((AdjusterButton)button).onUpdate();
            }
        }
    }

    private boolean confirmValidation() {
        if (!parent.isItemValid(stack) || !stack.isItemEqual(slot.getStackInSlot(player))) {
            this.mc.displayGuiScreen(null);

            if (this.mc.currentScreen == null) {
                this.mc.setIngameFocus();
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    //endregion

    //region Render

    @SuppressWarnings("unchecked")
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        partialTick = partialTicks;
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

        if (selected != -1 && fieldButtons != null && selected >= 0 && selected < fieldButtons.length) {
            List<String> lines = fontRendererObj.listFormattedStringToWidth(I18n.format(fieldButtons[selected].field.getDescription()), 310);
            for (String line : lines) {
                fontRendererObj.drawString(line, centerX - 159, centerY + 16 + (lines.indexOf(line) * 12), 0xFFFFFF);
            }
        }

        //endregion

        super.drawScreen(mouseX, mouseY, partialTicks);

        for (GuiButton button : buttonList) {
            if (button instanceof FieldButton) {
                ((FieldButton) button).drawToolTips(mc, mouseX, mouseY);
            }
            if (button instanceof AdjusterButton) {
                ((AdjusterButton) button).drawToolTips(mc, mouseX, mouseY);
            }
        }
    }

    private void renderItem(int x, int y, float partialTicks) {
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
        RenderHelper.disableStandardItemLighting();

        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    //endregion

    //region Interaction

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button instanceof FieldButton) {
            selected = button.id;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        if (mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode)) {
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

        public void drawToolTips(Minecraft mc, int mouseX, int mouseY) {
//            if (hovered && stack != null && stack.getItem() instanceof IConfigurableItem) {
//                renderToolTip(stack, mouseX, mouseY);
//            }
        }

        @Override
        public void playPressSound(SoundHandler soundHandlerIn) {
            if (gui.selected != id) {
                super.playPressSound(soundHandlerIn);
            }
        }
    }

    public class AdjusterButton extends GuiButton {
        private final GuiConfigureTool gui;
        private FieldButton activeButton = null;
        private List<Button> buttonList = new ArrayList<Button>();
        private boolean dropDownActive = false;
        private float dropDownPos = 0f;
        private boolean isDragging = false;
        private double dragPos = 0;
        private double releasePos = 0;
        private int dragTick = 0;

        public AdjusterButton(int buttonId, int x, int y, int width, int height, GuiConfigureTool gui) {
            super(buttonId, x, y, width, height, "");
            this.gui = gui;
        }

        //region Init

        private void initSubButtons() {
            buttonList.clear();
            if (activeButton != null && activeButton.field != null) {
                switch (activeButton.field.getType()) {
                    case PLUS3_MINUS3:
                        buttonList.add(new Button(0, xPosition + 31, yPosition + 1, 18, 16, "<<<", MINUS3));
                        buttonList.add(new Button(1, xPosition + width - 49, yPosition + 1, 18, 16, ">>>", PLUS3));
                    case PLUS2_MINUS2:
                        buttonList.add(new Button(2, xPosition + 50, yPosition + 1, 14, 16, "<<", MINUS2));
                        buttonList.add(new Button(3, xPosition + width - 64, yPosition + 1, 14, 16, ">>", PLUS2));
                    case PLUS1_MINUS1:
                        buttonList.add(new Button(4, xPosition + 65, yPosition + 1, 10, 16, "<", MINUS1));
                        buttonList.add(new Button(5, xPosition + width - 75, yPosition + 1, 10, 16, ">", PLUS1));
                        buttonList.add(new Button(6, xPosition + 1, yPosition + 1, 16, 16, "<*", MIN));
                        buttonList.add(new Button(7, xPosition + width - 17, yPosition + 1, 16, 16, "*>", MAX));
                        break;
                    case SLIDER:
                        break;
                    case SELECTIONS:
                        if (dropDownPos == 1){
                            Map<Integer, String> values = activeButton.field.getValues();

                            int i = 0;
                            for (Integer index : values.keySet()) {
                                int cols = (values.size() / 8) + (values.size() % 8 > 0 ? 1 : 0);

                                if (width - ((width / cols) * cols) >= width / cols){
                                    cols++;
                                }

                                int col = i % cols;
                                int xPos = xPosition + ((width / cols) * col);
                                int xSize = width / cols + 1;

                                int y = 5 + yPosition + 18 + i / cols * 12;

                                buttonList.add(new Button(9 + i, xPos + 1, y, xSize - 2, 11, values.get(index), SELECTION, index));
                                i++;
                            }
                        }
                        break;
                    case TOGGLE:
                        buttonList.add(new Button(8, xPosition + width / 2 - 159, yPosition + 1, 318, 16, "", TOGGLE));
                        break;
                }
            }
        }

        //endregion

        //region Draw & Refresh

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            updateActiveButton();

            if (activeButton == null) {
                return;
            }

            IItemConfigField field = activeButton.field;

            switch (activeButton.field.getType()) {
                case PLUS3_MINUS3:
                case PLUS2_MINUS2:
                case PLUS1_MINUS1:
                    GuiHelper.drawCenteredString(mc.fontRendererObj, field.getReadableValue(), xPosition + width / 2, yPosition + 1, 0xFFFFFF, false);
                    GuiHelper.drawCenteredString(mc.fontRendererObj, I18n.format("gui.de.min.txt") + ": " + field.getMin() + " - " + I18n.format("gui.de.max.txt") + ": " + field.getMax(), xPosition + width / 2, yPosition + 10, 0xFFFFFF, false);
                    break;
                case SLIDER:
                    double sliderPos = field.getFractionalValue();

                    if (isDragging){
                        sliderPos = (mouseX - 3 - (xPosition + 4D)) / (width - 14D);
                        if (sliderPos > 1){
                            sliderPos = 1;
                        }
                        else if (sliderPos < 0){
                            sliderPos = 0;
                        }
                        dragPos = releasePos = sliderPos;
                        dragTick = 20;
                    }

                    if (dragTick > 0){
                        if (dragTick < 10) {
                            double trans = 1D - (dragTick / 10D);
                            sliderPos = dragPos + (trans * (sliderPos - dragPos));
                        }
                        else {
                            sliderPos = dragPos;
                        }
                        dragTick--;
                    }

                    int pos = (int)((width - 14) * sliderPos);
                    boolean mouseOver = GuiHelper.isInRect(xPosition + 3 + pos, yPosition + 3, 6, 12, mouseX, mouseY) || isDragging;

                    GuiHelper.drawColouredRect(xPosition + 4, yPosition + 8, width - 8, 2, 0xFF222222);
                    GuiHelper.drawColouredRect(xPosition + 1, yPosition + 2, 3, 14, 0xFF770000);
                    GuiHelper.drawColouredRect(xPosition + width - 4, yPosition + 2, 3, 14, 0xFF770000);
                    GuiHelper.drawColouredRect(xPosition + 4 + pos, yPosition + 3, 6, 12, mouseOver ? 0xFF00FF00 : 0xFFFF0000);
                    GuiHelper.drawColouredRect(xPosition + 5 + pos, yPosition + 4, 4, 10, 0xFF000000);


                    int txtWidth = mc.fontRendererObj.getStringWidth(isDragging ? field.getValueFraction(sliderPos) : field.getReadableValue());
                    GuiHelper.drawColouredRect(xPosition + width / 2 - txtWidth / 2, yPosition , txtWidth, 8, 0x88000000);
                    GuiHelper.drawCenteredString(mc.fontRendererObj, isDragging ? field.getValueFraction(sliderPos) : field.getReadableValue(), xPosition + width / 2, yPosition + 1, 0xFFFFFF, false);
                    break;
                case SELECTIONS:
                    mouseOver = GuiHelper.isInRect(xPosition + 1, yPosition + 1, width - 2, 16, mouseX, mouseY);

                    int border = mouseOver ? 0xFF009999 : 0xFF220033;
                    GuiHelper.drawColouredRect(xPosition + 1, yPosition + 1, width - 2, 16, border);
                    GuiHelper.drawColouredRect(xPosition + 2, yPosition + 2, width - 4, 14, 0xFF000000);

                    GuiHelper.drawCenteredString(mc.fontRendererObj, field.getReadableValue(), xPosition + width / 2, yPosition + 5, 0xFFFFFF, false);
                    break;
                case TOGGLE:
                    break;
            }

            if (dropDownPos > 0) {
                int h = (dropDownPos < 1F ? (int) ((float) height + partialTick) : height) - 20;
                GuiHelper.drawColouredRect(xPosition, yPosition + 20, width, h, 0xFF000000);
                //GuiHelper.drawColouredRect(xPosition, yPosition + 18, width, 1, 0xFFFF0000);
                //GuiHelper.drawColouredRect(xPosition, yPosition + height - (int)(1 * dropDownPos), width, 1, 0xFFFF0000);
                //GuiHelper.drawColouredRect(xPosition, yPosition + 18, 1, height - 18, 0xFFFF0000);
                //GuiHelper.drawColouredRect(xPosition + width - 1, yPosition + 18, 1, height - 18, 0xFFFF0000);
            }

            for (Button button : buttonList) {
                button.drawButton(mc, mouseX, mouseY);
            }
        }

        public void drawToolTips(Minecraft mc, int mouseX, int mouseY) {
            if (activeButton == null) {
                return;
            }

            IItemConfigField field = activeButton.field;

            switch (activeButton.field.getType()) {
                case TOGGLE:
                    GuiHelper.drawCenteredString(mc.fontRendererObj, field.getReadableValue(), xPosition + width / 2, yPosition + 5, 0xFFFFFF, false);
                    break;
            }
        }

        private void updateActiveButton() {
            int currentButton = activeButton == null ? -1 : activeButton.id;
            if (gui.selected >= 0 && gui.selected < gui.fieldButtons.length && gui.fieldButtons[gui.selected].field != null) {
                activeButton = gui.fieldButtons[gui.selected];
                if (currentButton != activeButton.id) {
                    dropDownActive = false;
                    initSubButtons();
                }
            } else {
                activeButton = null;
                buttonList.clear();
            }
        }

        public void onUpdate(){
            if (dropDownActive && dropDownPos < 1){
                dropDownPos += 0.2F;
                if (dropDownPos > 1F){
                    dropDownPos = 1F;
                }

                height = 18 + (int) (dropDownPos * 103D);

                initSubButtons();
            }
            else if (!dropDownActive && dropDownPos > 0){
                dropDownPos -= 0.2F;
                if (dropDownPos < 0){
                    dropDownPos = 0;
                }

                height = 18 + (int) (dropDownPos * 100D);

                initSubButtons();
            }
        }

        //endregion

        //region Button Interact

        @Override
        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
            if (activeButton == null || activeButton.field == null) {
                return super.mousePressed(mc, mouseX, mouseY);
            }

            for (Button button : buttonList) {
                if (button.enabled && button.buttonPressed(mc, mouseX, mouseY)) {
                    buttonClicked(button);
                }
            }

            switch (activeButton.field.getType()) {

                case PLUS3_MINUS3:
                case PLUS2_MINUS2:
                case PLUS1_MINUS1:
                    break;
                case SLIDER:
                    int pos = (int)((width - 14) * activeButton.field.getFractionalValue());
                    isDragging = GuiHelper.isInRect(xPosition + 3 + pos, yPosition + 3, 6, 12, mouseX, mouseY);
                    if (isDragging){
                        mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    }
                    break;
                case SELECTIONS:
                    if (GuiHelper.isInRect(xPosition, yPosition, width, 18, mouseX, mouseY)) {
                        dropDownActive = !dropDownActive;
                        super.playPressSound(mc.getSoundHandler());
                    }
                    break;
            }

            return super.mousePressed(mc, mouseX, mouseY);
        }

        @Override
        protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
            if (activeButton == null) {
                return;
            }

            switch (activeButton.field.getType()) {

                case PLUS3_MINUS3:
                case PLUS2_MINUS2:
                case PLUS1_MINUS1:
                break;
                case SLIDER:
                break;
                case SELECTIONS:
                break;
            }
        }

        @Override
        public void mouseReleased(int mouseX, int mouseY) {
            if (activeButton != null && activeButton.field != null) {
                switch (activeButton.field.getType()) {
                    case PLUS3_MINUS3:
                    case PLUS2_MINUS2:
                    case PLUS1_MINUS1:
                    break;
                    case SLIDER:
                        if (isDragging){
                            double pos = (mouseX - 3 - (xPosition + 4D)) / (width - 14D);
                            int fieldIndex = gui.fieldRegistry.getIndexFromName(activeButton.field.getName());
                            DraconicEvolution.network.sendToServer(new PacketConfigureTool(slot, fieldIndex, EnumButton.SLIDER.index, (int)(pos * 10000D)));
                            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 0.9F));
                        }
                    break;
                    case SELECTIONS:
                    break;
                }
            }
            isDragging = false;
        }

        @Override
        public void playPressSound(SoundHandler soundHandlerIn) {
//            if (gui.selected != id){
//                super.playPressSound(soundHandlerIn);
//            }
        }

        //endregion

        //region Sub-Button Interact

        private void buttonClicked(Button button) {
            updateActiveButton();
            if (activeButton == null) {
                return;
            }

            int fieldIndex = gui.fieldRegistry.getIndexFromName(activeButton.field.getName());

            DraconicEvolution.network.sendToServer(new PacketConfigureTool(slot, fieldIndex, button.enumButton.index, button.selectionIndex));

            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }

        //endregion

        public class Button {
            public final int id;
            public final int xPosition;
            public final int yPosition;
            public final int width;
            public final int height;
            public final String text;
            public boolean enabled = true;
            public final EnumButton enumButton;
            public int selectionIndex;

            public Button(int id, int xPosition, int yPosition, int width, int height, String text, EnumButton enumButton) {
                this.id = id;
                this.xPosition = xPosition;
                this.yPosition = yPosition;
                this.width = width;
                this.height = height;
                this.text = text;
                this.enumButton = enumButton;
                this.selectionIndex = 0;
            }

            public Button(int id, int xPosition, int yPosition, int width, int height, String text, EnumButton enumButton, int selectionIndex) {
                this(id, xPosition, yPosition, width, height, text, enumButton);
                this.selectionIndex = selectionIndex;
            }

            public void drawButton(Minecraft mc, int mouseX, int mouseY) {
                if (enabled) {
                    boolean mouseOver = GuiHelper.isInRect(xPosition, yPosition, width, height, mouseX, mouseY);

                    int border = mouseOver ? 0xFF00A000 : 0xFFA00000;
                    GuiHelper.drawColouredRect(xPosition, yPosition, width, height, border);
                    GuiHelper.drawColouredRect(xPosition + 1, yPosition + 1, width - 2, height - 2, 0xFF000000);
                    GuiHelper.drawCenteredString(mc.fontRendererObj, text, xPosition + width / 2, yPosition + (height / 2) - (mc.fontRendererObj.FONT_HEIGHT / 2) + 1, 0xFFFFFF, false);
                }
            }

            public boolean buttonPressed(Minecraft mc, int mouseX, int mouseY) {
                return GuiHelper.isInRect(xPosition, yPosition, width, height, mouseX, mouseY);
            }
        }
    }

    //endregion
}
