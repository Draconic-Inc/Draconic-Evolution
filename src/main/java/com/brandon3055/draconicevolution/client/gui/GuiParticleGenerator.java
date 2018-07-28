package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.tileentity.TileParticleGenerator;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.brandon3055.draconicevolution.network.PacketParticleGenerator;
import com.brandon3055.draconicevolution.utils.DETextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiSlider;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

public class GuiParticleGenerator extends GuiScreen {
    private static final String I18N_PREFIX = "gui.particleGenerator.";

    private final int xSize = 210;
    private final int ySize = 145;
    private static int page = 1;
    private boolean requiresInit = false;

    private TileParticleGenerator tile;

    public GuiParticleGenerator(TileParticleGenerator tile) {
        this.tile = tile;
    }

    @Override
    public void drawScreen(int x, int y, float f) {
        drawDefaultBackground();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        ResourceHelperDE.bindTexture(DETextures.GUI_PARTICLE_GENERATOR);
        int posX = (this.width - xSize) / 2;
        int posY = (this.height - ySize) / 2;
        drawTexturedModalRect(posX, posY, 0, 0, xSize, ySize);
        drawCenteredString(fontRenderer, I18n.format(I18N_PREFIX + "name"), width/2, posY+6, 0x00FFFF);
        drawCenteredString(fontRenderer, page + "/4", width/2, posY+ySize-19, 0xFFFFFF);

        super.drawScreen(x, y, f);
    }

    @Override
    public void initGui() {
        int posX = (this.width - xSize) / 2;
        int posY = (this.height - ySize) / 2;
        buttonList.clear();

        if (page > 1) buttonList.add(new GuiButtonExt(100, posX + 5, posY + ySize - 25, 20, 20, "<"));
        if (page < 4) buttonList.add(new GuiButtonExt(101, posX + xSize - 25, posY + ySize - 25, 20, 20, ">"));

        switch (page) {
            default:
            case 1:
                buttonList.add(new GuiRangeSliderI(0, posX + 5, posY + 20, I18n.format(I18N_PREFIX + "red"), 0, 255, tile.RED.value, tile.RANDOM_RED.value));
                buttonList.add(new GuiRangeSliderI(1, posX + 5, posY + 45, I18n.format(I18N_PREFIX + "green"), 0, 255, tile.GREEN.value, tile.RANDOM_GREEN.value));
                buttonList.add(new GuiRangeSliderI(2, posX + 5, posY + 70, I18n.format(I18N_PREFIX + "blue"), 0, 255, tile.BLUE.value, tile.RANDOM_BLUE.value));
                buttonList.add(new GuiRangeSliderI(3, posX + 5, posY + 95, I18n.format(I18N_PREFIX + "alpha"), 0, 255, tile.ALPHA.value, tile.RANDOM_ALPHA.value));
                break;
            case 2:
                buttonList.add(new GuiRangeSlider(10, posX + 5, posY + 20, I18n.format(I18N_PREFIX + "motionX"), -2, 2, tile.MOTION_X.value, tile.RANDOM_MOTION_X.value));
                buttonList.add(new GuiRangeSlider(11, posX + 5, posY + 45, I18n.format(I18N_PREFIX + "motionY"), -2, 2, tile.MOTION_Y.value, tile.RANDOM_MOTION_Y.value));
                buttonList.add(new GuiRangeSlider(12, posX + 5, posY + 70, I18n.format(I18N_PREFIX + "motionZ"), -2, 2, tile.MOTION_Z.value, tile.RANDOM_MOTION_Z.value));
                buttonList.add(new GuiRangeSlider(6, posX + 5, posY + 95, I18n.format(I18N_PREFIX + "gravity"), 0, 1, tile.GRAVITY.value, tile.RANDOM_GRAVITY.value));
                break;
            case 3:
                buttonList.add(new GuiRangeSlider(13, posX + 5, posY + 20, I18n.format(I18N_PREFIX + "spawnX"), -10, 10, tile.SPAWN_X.value, tile.RANDOM_SPAWN_X.value));
                buttonList.add(new GuiRangeSlider(14, posX + 5, posY + 45, I18n.format(I18N_PREFIX + "spawnY"), -10, 10, tile.SPAWN_Y.value, tile.RANDOM_SPAWN_Y.value));
                buttonList.add(new GuiRangeSlider(15, posX + 5, posY + 70, I18n.format(I18N_PREFIX + "spawnZ"), -10, 10, tile.SPAWN_Z.value, tile.RANDOM_SPAWN_Z.value));
                buttonList.add(new GuiRangeSlider(4, posX + 5, posY + 95, I18n.format(I18N_PREFIX + "scale"), 0.01, 50, tile.SCALE.value, tile.RANDOM_SCALE.value));
                break;
            case 4:
                buttonList.add(new GuiSliderI(16, posX + 5, posY + 20, I18n.format(I18N_PREFIX + "delay"), 1, 200, tile.DELAY.value));
                buttonList.add(new GuiRangeSliderI(5, posX + 5, posY + 45, I18n.format(I18N_PREFIX + "life"), 0, 200, tile.LIFE.value, tile.RANDOM_LIFE.value));
                buttonList.add(new GuiRangeSliderI(7, posX + 5, posY + 70, I18n.format(I18N_PREFIX + "fade"), 0, 200, tile.FADE.value, tile.RANDOM_FADE.value));
                buttonList.add(new GuiButtonToggle(8, posX + 5, posY + 95, 98, 20, I18n.format(I18N_PREFIX + "type"), tile.TYPE.value, I18n.format(I18N_PREFIX + "type0"), I18n.format(I18N_PREFIX + "type1"), I18n.format(I18N_PREFIX + "type2"), I18n.format(I18N_PREFIX + "type3"), I18n.format(I18N_PREFIX + "type4")));
                buttonList.add(new GuiButtonToggle(9, posX + 107, posY + 95, 98, 20, I18n.format(I18N_PREFIX + "collision"), tile.COLLISION.value ? 1 : 0, I18n.format("options.off"), I18n.format("options.on")));
                break;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 8:
                DraconicEvolution.network.sendToServer(new PacketParticleGenerator(tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), (byte) button.id, ((GuiButtonToggle) button).getIndex(),0));
                break;
            case 9:
                DraconicEvolution.network.sendToServer(new PacketParticleGenerator(tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), (byte) button.id, ((GuiButtonToggle) button).getIndex(),0));
                break;

            case 100:
                page--;
                requiresInit = true;
                break;
            case 101:
                page++;
                requiresInit = true;
                break;
        }
    }

    @Override
    public void keyTyped(char key, int keyN) {
        if ((key == 'e') || key == '') {
            this.mc.displayGuiScreen(null);
            this.mc.setIngameFocus();
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 1)
        {
            for (int i = 0; i < this.buttonList.size(); ++i)
            {
                GuiButton guibutton = this.buttonList.get(i);

                if (guibutton instanceof GuiRangeSlider && ((GuiRangeSlider) guibutton).rightClick(this.mc, mouseX, mouseY))
                {
                    net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre event = new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre(this, guibutton, this.buttonList);
                    if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
                        break;
                    guibutton = event.getButton();
                    this.selectedButton = guibutton;
                    guibutton.playPressSound(this.mc.getSoundHandler());
                    this.actionPerformed(guibutton);
                    if (this.equals(this.mc.currentScreen))
                        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post(this, event.getButton(), this.buttonList));
                }
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);

        if (selectedButton instanceof GuiRangeSlider && state == 1) {
            ((GuiRangeSlider) selectedButton).rightReleased(mouseX, mouseY);
            selectedButton = null;
        }
        else if (requiresInit) {
            requiresInit = false;
            initGui();
        }
    }

    class GuiSliderI extends GuiSlider {

        public GuiSliderI(int id, int xPos, int yPos, String displayStr, int minVal, int maxVal, int currentVal) {
            super(id, xPos, yPos, 200, 20, displayStr + ": ", "", minVal, maxVal, currentVal, false, true, slider -> {});
        }

        @Override
        public void mouseReleased(int par1, int par2) {
            super.mouseReleased(par1, par2);
            DraconicEvolution.network.sendToServer(new PacketParticleGenerator(tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), (byte) id, getValueInt(),0));
        }
    }

    class GuiRangeSlider extends GuiSlider {

        public double rangeValue = 1.0;
        private boolean rightDragging = false;

        public GuiRangeSlider(int id, int xPos, int yPos, String displayStr, double minVal, double maxVal, double currentVal, double currentRandom) {
            super(id, xPos, yPos, 200, 20, displayStr + ": ", "", minVal, maxVal, currentVal, true, true, slider -> {});
            setRandomValue(currentRandom);
            precision = 2;
            updateSlider();
        }

        @Override
        public void updateSlider() {
            if (this.sliderValue < 0.0F) {
                this.sliderValue = 0.0F;
            }

            if (this.sliderValue > 1.0F) {
                this.sliderValue = 1.0F;
            }

            if (this.rangeValue < 0.0F) {
                this.rangeValue = 0.0F;
            }

            if (this.rangeValue > 1.0F) {
                this.rangeValue = 1.0F;
            }

            if (rangeValue < sliderValue) {
                rangeValue = sliderValue;
            }

            String val;
            String valR;

            if (showDecimal) {
                val = Double.toString(getValue());
                valR = Double.toString(getRangeValue());

                if (val.substring(val.indexOf(".") + 1).length() > precision) {
                    val = val.substring(0, val.indexOf(".") + precision + 1);

                    if (val.endsWith(".")) {
                        val = val.substring(0, val.indexOf(".") + precision);
                    }
                } else {
                    while (val.substring(val.indexOf(".") + 1).length() < precision) {
                        val += "0";
                    }
                }

                if (valR.substring(valR.indexOf(".") + 1).length() > precision) {
                    valR = valR.substring(0, valR.indexOf(".") + precision + 1);

                    if (valR.endsWith(".")) {
                        valR = valR.substring(0, valR.indexOf(".") + precision);
                    }
                } else {
                    while (valR.substring(valR.indexOf(".") + 1).length() < precision) {
                        valR += "0";
                    }
                }
            } else {
                val = Integer.toString(getValueInt());
                valR = Integer.toString(getRangeValueInt());
            }

            if(drawString) {
                displayString = dispString + val + " - " + valR + suffix;
            }

            if (parent != null) {
                parent.onChangeSliderValue(this);
            }
        }

        public boolean rightClick(Minecraft mc, int mouseX, int mouseY) {
            if (this.enabled && this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height) {
                rangeValue = (float)(mouseX - (this.x + 4)) / (float)(this.width - 8);
                updateSlider();
                rightDragging = true;
                return true;
            } else return false;
        }

        @Override
        protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
            super.mouseDragged(mc, mouseX, mouseY);

            if (visible) {
                if (rightDragging) {
                    rangeValue = (mouseX - (this.x + 4)) / (float)(this.width - 8);
                    updateSlider();
                }

                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.drawTexturedModalRect(this.x + (int)(rangeValue * (float)(this.width - 8)), this.y, 0, 66, 4, 20);
                this.drawTexturedModalRect(this.x + (int)(rangeValue * (float)(this.width - 8)) + 4, this.y, 196, 66, 4, 20);
            }
        }

        public void rightReleased(int mouseX, int mouseY) {
            mouseReleased(mouseX, mouseY);
        }

        @Override
        public void mouseReleased(int par1, int par2) {
            super.mouseReleased(par1, par2);
            rightDragging = false;
            sendPacket();
        }

        void sendPacket() {
            DraconicEvolution.network.sendToServer(new PacketParticleGenerator(tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), (byte) id, (int) Math.round(getValue()*10000), (int) Math.round(getRandomValue()*10000)));
        }

        public int getRangeValueInt() {
            return (int)Math.round(rangeValue * (maxValue - minValue) + minValue);
        }

        public double getRangeValue() {
            return rangeValue * (maxValue - minValue) + minValue;
        }

        public void setRangeValue(double d) {
            this.rangeValue = (d - minValue) / (maxValue - minValue);
        }

        public int getRandomValueInt() {
            return getRangeValueInt() - getValueInt();
        }

        public double getRandomValue() {
            return getRangeValue() - getValue();
        }

        public void setRandomValue(double d) {
            setRangeValue(getValue() + d);
        }

    }

    class GuiRangeSliderI extends GuiRangeSlider {

        public GuiRangeSliderI(int id, int xPos, int yPos, String displayStr, int minVal, int maxVal, int currentVal, int currentRandom) {
            super(id, xPos, yPos, displayStr, minVal, maxVal, currentVal, currentRandom);
            showDecimal = false;
            updateSlider();
        }

        @Override
        void sendPacket() {
            DraconicEvolution.network.sendToServer(new PacketParticleGenerator(tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), (byte) id, getValueInt(), getRandomValueInt()));
        }
    }

    class GuiButtonToggle extends GuiButtonExt {

        String dispString;
        String[] options;
        int index;

        public GuiButtonToggle(int id, int xPos, int yPos, int width, int height, String displayString, int index, String... options) {
            super(id, xPos, yPos, width, height, displayString + ": " + options[index]);
            dispString = displayString + ": ";
            this.index = index;
            this.options = options;
        }

        @Override
        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
            if (super.mousePressed(mc, mouseX, mouseY)) {
                index++;
                if (index >= options.length) index = 0;
                update();
                return true;
            } else return false;
        }

        public void update() {
            displayString = dispString + getValue();
        }

        public void setIndex(int index) {
            this.index = index;
            update();
        }

        public int getIndex() {
            return index;
        }

        public String getValue() {
            return options[index];
        }

    }

}
