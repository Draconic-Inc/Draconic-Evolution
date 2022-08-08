package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.network.ParticleGenPacket;
import com.brandon3055.draconicevolution.common.tileentities.TileParticleGenerator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class GUIParticleGenerator extends GuiScreen {
    private final int xSize = 212;
    private final int ySize = 198;
    private ResourceLocation guiTexture =
            new ResourceLocation(References.MODID.toLowerCase(), "textures/gui/ParticleGenerator.png");
    private int page = 1;
    private int infoPage = 0;
    private boolean hasInitialized = false;

    // Particle variables
    private boolean particles_enabled = true;

    private int red = 0;
    private int green = 0;
    private int blue = 0;
    private int random_red = 0;
    private int random_green = 0;
    private int random_blue = 0;
    private float motion_x = 0F;
    private float motion_y = 0F;
    private float motion_z = 0F;
    private float random_motion_x = 0F;
    private float random_motion_y = 0F;
    private float random_motion_z = 0F;
    private float scale = 0F;
    private float random_scale = 0F;
    private int life = 0;
    private int random_life = 0;
    private float spawn_x = 0F;
    private float spawn_y = 0F;
    private float spawn_z = 0F;
    private float random_spawn_x = 0F;
    private float random_spawn_y = 0F;
    private float random_spawn_z = 0F;
    private int fade = 0;
    private int spawn_rate = 0;
    private boolean collide = false;
    private int selected_particle = 1;
    private int selected_max = 3;
    private float gravity = 0F;

    // Beam variables
    private boolean beam_enabled = false;
    private boolean render_core = false;

    private int beam_red = 0;
    private int beam_green = 0;
    private int beam_blue = 0;
    private float beam_scale = 0F;
    private float beam_pitch = 0F;
    private float beam_yaw = 0F;
    private float beam_length = 0F;
    private float beam_rotation = 0F;

    // Buttons

    // Info Page
    // particle selection

    private TileParticleGenerator tile;

    public GUIParticleGenerator(TileParticleGenerator tile, EntityPlayer player) {
        super();
        this.tile = tile;
        syncWithServer();
    }

    String[] InfoText = {
        ""
                + "The Particle Generator is a decorative device that allows you to create your own custom particle effects.                                                                 "
                + "It is fairly easy you use this device you simply adjust the fields (variables) in the interface to change how the generated particles look and behave.                                                                                            "
                + "This block is a work in progress and new features and particles are likely to be added in future versions.                                                         "
                + "The following is a list of all of the fields in the interface and what they do.",
        "The first thing to note is that most fields have a random modifier which will add a random number between 0 and whatever max (or min) value you give it to the field.                                                                                      "
                + "-The first 3 fields (Red, Green & Blue) control the colour of the particle. Most people should be familiar with this colour system if not google RGB colours. Note: the max value for each colour can not go higher then 255 so the colour field limits the random modifier e.g. if the colour field is set to 255 and the random modifier is set to 20 the result will always be 255",
        "-The next 3 fields (Motion X, Y & Z) control the direction and speed of the particle                                                                                            "
                + "-The \"Life\" field sets how long (in ticks) before the particle despawns.                                                       "
                + "-The \"Size\" field sets the size of the particle.                                                                                           "
                + "-The next 3 fields (Spawn X, Y & Z) Sets the spawn location of the particle (relative to the location of the particle generator)",
        "-The \"Delay\" field sets the delay (in ticks) between each particle spawn e.g. 1=20/s, 20=1/s, 100=1/5s                                                                    "
                + "-The \"Fade\" field sets how long (in ticks) it takes the partile to fade out of existance. Note: This adds to the life of the particle                                                                                  "
                + "-The \"Gravity\" field sets how the particle is affected by gravity.                                                              "
                + "-\"Block Collision\" Toggles weather or not the particle will collide with blocks                                                     "
                + "-\"Particle Selected\" Switches between the different particles available.",
        EnumChatFormatting.DARK_RED + "              Redstone Control" + EnumChatFormatting.BLACK
                + "\nBy default a redstone signal is required for the generator to run."
                + "\n\nHowever if you shift right click the generator with an empty hand it will switch to inverted mode."
                + "\nThe redstone mode is indicated by the 8 cubes at the corners of the block.",
        EnumChatFormatting.DARK_RED + "              Computer Control" + EnumChatFormatting.BLACK
                + "\nThe Generator can be controlled via a computer" + "\nIt exposes a relatively straight forward API:"
                + "\n\n  setGeneratorProperty(property, value)\n  getGeneratorState()\n  resetGeneratorState()"
                + "\n\nGenerator state is obtained as a whole from getGeneratorState, whereas properties are modified one at a time using setGeneratorProperty. Property names are strings and mostly correspond to button labels in the GUI."
    };

    // @formatter:off
    @Override
    public void drawScreen(int x, int y, float f) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().renderEngine.bindTexture(guiTexture);
        int posX = (this.width - xSize) / 2;
        int posY = (this.height - ySize) / 2;
        drawTexturedModalRect(posX, posY, 0, 0, xSize, ySize);
        if (page < 3) fontRendererObj.drawStringWithShadow("Particle Generator", posX + 60, posY + 5, 0x00FFFF);
        else if (page < 10) fontRendererObj.drawStringWithShadow("Beam Generator", posX + 65, posY + 5, 0x00FFFF);
        else fontRendererObj.drawStringWithShadow("Information", posX + 75, posY + 5, 0x00FFFF);

        if (page == 1) page1Txt();
        else if (page == 2) page2Txt();
        else if (page == 3) page3Txt();
        else if (page == 10) {
            fontRendererObj.drawSplitString(InfoText[infoPage], posX + 5, posY + 20, 200, 0x000000);
            fontRendererObj.drawSplitString("Page: " + (infoPage + 1), posX + 88, posY + 180, 200, 0xFF0000);
        }

        fontRendererObj.drawStringWithShadow("Hold:", posX + 215, posY + 11, 0xFFFFFF);
        fontRendererObj.drawStringWithShadow("Shift +- 10", posX + 215, posY + 21, 0xFFFFFF);
        fontRendererObj.drawStringWithShadow("Ctrl +- 50", posX + 215, posY + 31, 0xFFFFFF);
        fontRendererObj.drawStringWithShadow("Shift+Ctrl +- 100", posX + 215, posY + 41, 0xFFFFFF);

        super.drawScreen(x, y, f);
    }
    // @formatter:on

    @Override
    protected void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);
    }

    @Override
    public void initGui() {
        int posX = (this.width - xSize) / 2;
        int posY = (this.height - ySize) / 2;
        buttonList.clear();

        if (page < 10) {
            buttonList.add(new GuiButton(33, posX + 213, posY + 177, 47, 20, "===>"));
            buttonList.add(new GuiButton(32, posX - 47, posY + 177, 47, 20, "<==="));
        }

        if (page == 1) page1Buttons();
        else if (page == 2) page2Buttons();
        else if (page == 3) page3Buttons();
        else if (page == 10) {
            buttonList.add(new GuiButton(57, posX + 4, posY + 174, 80, 20, "Previous page"));
            buttonList.add(new GuiButton(56, posX + 128, posY + 174, 80, 20, "Next page"));
        }

        if (page < 10) buttonList.add(new GuiButton(54, posX - 21, posY + 3, 20, 20, "i"));
        else buttonList.add(new GuiButton(55, posX - 31, posY + 23, 30, 20, "Back"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id < 100) particleActions(button);
        else beamActions(button);
    }

    private void particleActions(GuiButton button) {
        int value = 1;
        float value_F;
        short packetValue = 0;
        if (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54)) value = 10;
        if (Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157)) value = 50;
        if ((Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157)) && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54)))
            value = 100;

        value_F = (value) / 1000F;

        switch (button.id) {
            case 0: // Red +
                red = (red + value) > 255 ? 255 : red + value;
                packetValue = (short) red;
                break;
            case 1: // Green +
                green = (green + value) > 255 ? 255 : green + value;
                packetValue = (short) green;
                break;
            case 2: // Blue +
                blue = (blue + value) > 255 ? 255 : blue + value;
                packetValue = (short) blue;
                break;
            case 3: // MX +
                motion_x = (motion_x + value_F) > 5F ? 5F : motion_x + value_F;
                packetValue = (short) (motion_x * 1000F);
                break;
            case 4: // MY +
                motion_y = (motion_y + value_F) > 5F ? 5F : motion_y + value_F;
                packetValue = (short) (motion_y * 1000F);
                break;
            case 5: // MZ +
                motion_z = (motion_z + value_F) > 5F ? 5F : motion_z + value_F;
                packetValue = (short) (motion_z * 1000F);
                break;
            case 6: // Red -
                red = (red - value) < 0 ? 0 : red - value;
                packetValue = (short) red;
                break;
            case 7: // Green -
                green = (green - value) < 0 ? 0 : green - value;
                packetValue = (short) green;
                break;
            case 8: // Blue -
                blue = (blue - value) < 0 ? 0 : blue - value;
                packetValue = (short) blue;
                break;
            case 9: // MX -
                motion_x = (motion_x - value_F) < -5F ? -5F : motion_x - value_F;
                packetValue = (short) (motion_x * 1000F);
                break;
            case 10: // MY -
                motion_y = (motion_y - value_F) < -5F ? -5F : motion_y - value_F;
                packetValue = (short) (motion_y * 1000F);
                break;
            case 11: // MZ -
                motion_z = (motion_z - value_F) < -5F ? -5F : motion_z - value_F;
                packetValue = (short) (motion_z * 1000F);
                break;
            case 12: // RRed +
                random_red = (random_red + value) > 255 ? 255 : random_red + value;
                packetValue = (short) random_red;
                break;
            case 13: // RGreen +
                random_green = (random_green + value) > 255 ? 255 : random_green + value;
                packetValue = (short) random_green;
                break;
            case 14: // RBlue +
                random_blue = (random_blue + value) > 255 ? 255 : random_blue + value;
                packetValue = (short) random_blue;
                break;
            case 15: // RMX +
                random_motion_x = (random_motion_x + value_F) > 5F ? 5F : random_motion_x + value_F;
                packetValue = (short) (random_motion_x * 1000F);
                break;
            case 16: // RMY +
                random_motion_y = (random_motion_y + value_F) > 5F ? 5F : random_motion_y + value_F;
                packetValue = (short) (random_motion_y * 1000F);
                break;
            case 17: // RMZ +
                random_motion_z = (random_motion_z + value_F) > 5F ? 5F : random_motion_z + value_F;
                packetValue = (short) (random_motion_z * 1000F);
                break;
            case 18: // RRed -
                random_red = (random_red - value) < 0 ? 0 : random_red - value;
                packetValue = (short) random_red;
                break;
            case 19: // RGreen -
                random_green = (random_green - value) < 0 ? 0 : random_green - value;
                packetValue = (short) random_green;
                break;
            case 20: // RBlue -
                random_blue = (random_blue - value) < 0 ? 0 : random_blue - value;
                packetValue = (short) random_blue;
                break;
            case 21: // RMX -
                random_motion_x = (random_motion_x - value_F) < -5F ? -5F : random_motion_x - value_F;
                packetValue = (short) (random_motion_x * 1000F);
                break;
            case 22: // RMY -
                random_motion_y = (random_motion_y - value_F) < -5F ? -5F : random_motion_y - value_F;
                packetValue = (short) (random_motion_y * 1000F);
                break;
            case 23: // RMZ -
                random_motion_z = (random_motion_z - value_F) < -5F ? -5F : random_motion_z - value_F;
                packetValue = (short) (random_motion_z * 1000F);
                break;
            case 24: // Life +
                life = (life + value) > 1000 ? 1000 : life + value;
                packetValue = (short) life;
                break;
            case 25: // Life -
                life = (life - value) < 0 ? 0 : life - value;
                packetValue = (short) life;
                break;
            case 26: // RLife +
                random_life = (random_life + value) > 1000 ? 1000 : random_life + value;
                packetValue = (short) random_life;
                break;
            case 27: // RLife -
                random_life = (random_life - value) < 0 ? 0 : random_life - value;
                packetValue = (short) random_life;
                break;
            case 28: // Size +
                scale = (scale + value_F * 10F) > 50F ? 50F : scale + value_F * 10F;
                packetValue = (short) (scale * 100F);
                break;
            case 29: // Size -
                scale = (scale - value_F * 10F) < 0.01F ? 0.01F : scale - value_F * 10F;
                packetValue = (short) (scale * 100F);
                break;
            case 30: // RSize +
                random_scale = (random_scale + value_F * 10F) > 50F ? 50F : random_scale + value_F * 10F;
                packetValue = (short) (random_scale * 100F);
                break;
            case 31: // RSize -
                random_scale = (random_scale - value_F * 10F) < 0.0F ? 0.0F : random_scale - value_F * 10F;
                packetValue = (short) (random_scale * 100F);
                break;
            case 32: // Page 2
                if (page > 1) page--;
                packetValue = (short) page;
                initGui();
                break;
            case 33: // Page 1
                if (page < 3) page++;
                initGui();
                packetValue = (short) page;
                break;
            case 34: // SX +
                spawn_x = (spawn_x + value_F * 100F) > 50F ? 50F : spawn_x + value_F * 100F;
                packetValue = (short) (spawn_x * 100F);
                break;
            case 35: // SX -
                spawn_x = (spawn_x - value_F * 100F) < -50F ? -50F : spawn_x - value_F * 100F;
                packetValue = (short) (spawn_x * 100F);
                break;
            case 36: // RSX +
                random_spawn_x = (random_spawn_x + value_F * 100F) > 50F ? 50F : random_spawn_x + value_F * 100F;
                packetValue = (short) (random_spawn_x * 100F);
                break;
            case 37: // RSX -
                random_spawn_x = (random_spawn_x - value_F * 100F) < -50F ? -50F : random_spawn_x - value_F * 100F;
                packetValue = (short) (random_spawn_x * 100F);
                break;
            case 38: // SY +
                spawn_y = (spawn_y + value_F * 100F) > 50F ? 50F : spawn_y + value_F * 100F;
                packetValue = (short) (spawn_y * 100F);
                break;
            case 39: // SY -
                spawn_y = (spawn_y - value_F * 100F) < -50F ? -50F : spawn_y - value_F * 100F;
                packetValue = (short) (spawn_y * 100F);
                break;
            case 40: // RSY +
                random_spawn_y = (random_spawn_y + value_F * 100F) > 50F ? 50F : random_spawn_y + value_F * 100F;
                packetValue = (short) (random_spawn_y * 100F);
                break;
            case 41: // RSY -
                random_spawn_y = (random_spawn_y - value_F * 100F) < -50F ? -50F : random_spawn_y - value_F * 100F;
                packetValue = (short) (random_spawn_y * 100F);
                break;
            case 42: // SZ +
                spawn_z = (spawn_z + value_F * 100F) > 50F ? 50F : spawn_z + value_F * 100F;
                packetValue = (short) (spawn_z * 100F);
                break;
            case 43: // SZ -
                spawn_z = (spawn_z - value_F * 100F) < -50F ? -50F : spawn_z - value_F * 100F;
                packetValue = (short) (spawn_z * 100F);
                break;
            case 44: // RSZ +
                random_spawn_z = (random_spawn_z + value_F * 100F) > 50F ? 50F : random_spawn_z + value_F * 100F;
                packetValue = (short) (random_spawn_z * 100F);
                break;
            case 45: // RSZ -
                random_spawn_z = (random_spawn_z - value_F * 100F) < -50F ? -50F : random_spawn_z - value_F * 100F;
                packetValue = (short) (random_spawn_z * 100F);
                break;
            case 46: // Delay +
                spawn_rate = (spawn_rate + value) > 200 ? 200 : spawn_rate + value;
                packetValue = (short) spawn_rate;
                break;
            case 47: // Delay -
                spawn_rate = (spawn_rate - value) < 1 ? 1 : spawn_rate - value;
                packetValue = (short) spawn_rate;
                break;
            case 48: // Fade +
                fade = (fade + value) > 100 ? 100 : fade + value;
                packetValue = (short) fade;
                break;
            case 49: // Fade -
                fade = (fade - value) < 0 ? 0 : fade - value;
                packetValue = (short) fade;
                break;
            case 50: // Toggle Collision
                collide = !collide;
                packetValue = (short) (collide ? 1 : 0);
                initGui();
                break;
            case 51: // cycle particle selection
                selected_particle = selected_particle < selected_max ? selected_particle + 1 : 1;
                packetValue = (short) selected_particle;
                initGui();
                break;
            case 52: // RSZ +
                gravity = (gravity + value_F) > 5F ? 5F : gravity + value_F;
                packetValue = (short) (gravity * 1000F);
                break;
            case 53: // RSZ -
                gravity = (gravity - value_F) < -5F ? -5F : gravity - value_F;
                packetValue = (short) (gravity * 1000F);
                break;
            case 54: // Info Page
                page = 10;
                initGui();
                packetValue = (short) page;
                break;
            case 55: // Back Page
                page = 1;
                initGui();
                packetValue = (short) page;
                break;
            case 56: // Info Next Page
                if (infoPage < 5) infoPage++;
                else initGui();
                break;
            case 57: // Info Previous Page
                if (infoPage > 0) infoPage--;
                else initGui();
                break;
            case 58: // particles Enabled
                particles_enabled = !particles_enabled;
                packetValue = particles_enabled ? (byte) 1 : (byte) 0;
                initGui();
                break;
        }
        DraconicEvolution.network.sendToServer(
                new ParticleGenPacket((byte) button.id, packetValue, tile.xCoord, tile.yCoord, tile.zCoord));
    }

    @Override
    public void keyTyped(char key, int keyN) {
        if ((key == 'e') || key == '') {
            this.mc.displayGuiScreen(null);
            this.mc.setIngameFocus();
        }
    }

    @Override
    public void updateScreen() {}

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private void syncWithServer() {
        red = tile.red;
        green = tile.green;
        blue = tile.blue;
        random_red = tile.random_red;
        random_green = tile.random_green;
        random_blue = tile.random_blue;
        motion_x = tile.motion_x;
        motion_y = tile.motion_y;
        motion_z = tile.motion_z;
        random_motion_x = tile.random_motion_x;
        random_motion_y = tile.random_motion_y;
        random_motion_z = tile.random_motion_z;
        scale = tile.scale;
        random_scale = tile.random_scale;
        life = tile.life;
        random_life = tile.random_life;
        spawn_x = tile.spawn_x;
        spawn_y = tile.spawn_y;
        spawn_z = tile.spawn_z;
        random_spawn_x = tile.random_spawn_x;
        random_spawn_y = tile.random_spawn_y;
        random_spawn_z = tile.random_spawn_z;
        page = tile.page;
        spawn_rate = tile.spawn_rate;
        collide = tile.collide;
        fade = tile.fade;
        selected_particle = tile.selected_particle;
        gravity = tile.gravity;
        particles_enabled = tile.particles_enabled;

        render_core = tile.render_core;
        beam_enabled = tile.beam_enabled;
        beam_red = tile.beam_red;
        beam_green = tile.beam_green;
        beam_blue = tile.beam_blue;
        beam_scale = tile.beam_scale;
        beam_pitch = tile.beam_pitch;
        beam_yaw = tile.beam_yaw;
        beam_length = tile.beam_length;
        beam_rotation = tile.beam_rotation;
    }

    // @formatter:off
    private void page1Txt() {
        int posX = (this.width - xSize) / 2;
        int posY = (this.height - ySize) / 2;

        String motionX = String.valueOf(Math.round(motion_x * 1000F) / 1000F);
        String motionY = String.valueOf(Math.round(motion_y * 1000F) / 1000F);
        String motionZ = String.valueOf(Math.round(motion_z * 1000F) / 1000F);
        String random_motionX = String.valueOf(Math.round(random_motion_x * 1000F) / 1000F);
        String random_motionY = String.valueOf(Math.round(random_motion_y * 1000F) / 1000F);
        String random_motionZ = String.valueOf(Math.round(random_motion_z * 1000F) / 1000F);
        String scale1 = String.valueOf(Math.round(scale * 100F) / 100F);
        String random_scale1 = String.valueOf(Math.round(random_scale * 100F) / 100F);

        int col1 = posX + 30;
        int col2 = posX + 141;
        int ln1 = 20;
        int ln2 = 30;

        fontRendererObj.drawString("Red:", col1, posY + ln1 + 0 * 22, 0x000000, false);
        fontRendererObj.drawString(String.valueOf(red), col1, posY + ln2 + 0 * 22, 0x000000, false);
        fontRendererObj.drawString("Green:", col1, posY + ln1 + 1 * 22, 0x000000, false);
        fontRendererObj.drawString(String.valueOf(green), col1, posY + ln2 + 1 * 22, 0x000000, false);
        fontRendererObj.drawString("Blue:", col1, posY + ln1 + 2 * 22, 0x000000, false);
        fontRendererObj.drawString(String.valueOf(blue), col1, posY + ln2 + 2 * 22, 0x000000, false);
        fontRendererObj.drawString("Motion X:", col1, posY + ln1 + 3 * 22, 0x000000, false);
        fontRendererObj.drawString(motionX, col1, posY + ln2 + 3 * 22, 0x000000, false);
        fontRendererObj.drawString("Motion Y:", col1, posY + ln1 + 4 * 22, 0x000000, false);
        fontRendererObj.drawString(motionY, col1, posY + ln2 + 4 * 22, 0x000000, false);
        fontRendererObj.drawString("Motion Z:", col1, posY + ln1 + 5 * 22, 0x000000, false);
        fontRendererObj.drawString(motionZ, col1, posY + ln2 + 5 * 22, 0x000000, false);
        fontRendererObj.drawString("Life:", col1, posY + ln1 + 6 * 22, 0x000000, false);
        fontRendererObj.drawString("" + life + " T", col1, posY + ln2 + 6 * 22, 0x000000, false);
        fontRendererObj.drawString("Size:", col1, posY + ln1 + 7 * 22, 0x000000, false);
        fontRendererObj.drawString("" + scale1, col1, posY + ln2 + 7 * 22, 0x000000, false);

        for (int i = 0; i < 8; i++)
            fontRendererObj.drawStringWithShadow("(+)", posX + 98, posY + 25 + i * 22, 0xFFFFFF);

        fontRendererObj.drawString("Random:0", col2, posY + ln1 + 0 * 22, 0x000000, false);
        fontRendererObj.drawString("> " + random_red, col2, posY + ln2 + 0 * 22, 0x000000, false);
        fontRendererObj.drawString("Random:0", col2, posY + ln1 + 1 * 22, 0x000000, false);
        fontRendererObj.drawString("> " + random_green, col2, posY + ln2 + 1 * 22, 0x000000, false);
        fontRendererObj.drawString("Random:0", col2, posY + ln1 + 2 * 22, 0x000000, false);
        fontRendererObj.drawString("> " + random_blue, col2, posY + ln2 + 2 * 22, 0x000000, false);
        fontRendererObj.drawString("Random:0", col2, posY + ln1 + 3 * 22, 0x000000, false);
        fontRendererObj.drawString("> " + random_motionX, col2, posY + ln2 + 3 * 22, 0x000000, false);
        fontRendererObj.drawString("Random:0", col2, posY + ln1 + 4 * 22, 0x000000, false);
        fontRendererObj.drawString("> " + random_motionY, col2, posY + ln2 + 4 * 22, 0x000000, false);
        fontRendererObj.drawString("Random:0", col2, posY + ln1 + 5 * 22, 0x000000, false);
        fontRendererObj.drawString("> " + random_motionZ, col2, posY + ln2 + 5 * 22, 0x000000, false);
        fontRendererObj.drawString("Random:0", col2, posY + ln1 + 6 * 22, 0x000000, false);
        fontRendererObj.drawString("> " + random_life, col2, posY + ln2 + 6 * 22, 0x000000, false);
        fontRendererObj.drawString("Random:0", col2, posY + ln1 + 7 * 22, 0x000000, false);
        fontRendererObj.drawString("> " + random_scale1, col2, posY + ln2 + 7 * 22, 0x000000, false);
    }

    private void page1Buttons() {
        int posX = (this.width - xSize) / 2;
        int posY = (this.height - ySize) / 2;
        int DX1 = 5;
        int DX2 = DX1 + 71;
        int DX3 = DX2 + 40;
        int DX4 = DX3 + 71;
        int y1 = 19;

        buttonList.add(new GuiButton(0, posX + DX1, posY + y1 + 0 * 22, 20, 20, "+"));
        buttonList.add(new GuiButton(1, posX + DX1, posY + y1 + 1 * 22, 20, 20, "+"));
        buttonList.add(new GuiButton(2, posX + DX1, posY + y1 + 2 * 22, 20, 20, "+"));
        buttonList.add(new GuiButton(3, posX + DX1, posY + y1 + 3 * 22, 20, 20, "+"));
        buttonList.add(new GuiButton(4, posX + DX1, posY + y1 + 4 * 22, 20, 20, "+"));
        buttonList.add(new GuiButton(5, posX + DX1, posY + y1 + 5 * 22, 20, 20, "+"));
        buttonList.add(new GuiButton(24, posX + DX1, posY + y1 + 6 * 22, 20, 20, "+"));
        buttonList.add(new GuiButton(28, posX + DX1, posY + y1 + 7 * 22, 20, 20, "+"));

        buttonList.add(new GuiButton(6, posX + DX2, posY + y1 + 0 * 22, 20, 20, "-"));
        buttonList.add(new GuiButton(7, posX + DX2, posY + y1 + 1 * 22, 20, 20, "-"));
        buttonList.add(new GuiButton(8, posX + DX2, posY + y1 + 2 * 22, 20, 20, "-"));
        buttonList.add(new GuiButton(9, posX + DX2, posY + y1 + 3 * 22, 20, 20, "-"));
        buttonList.add(new GuiButton(10, posX + DX2, posY + y1 + 4 * 22, 20, 20, "-"));
        buttonList.add(new GuiButton(11, posX + DX2, posY + y1 + 5 * 22, 20, 20, "-"));
        buttonList.add(new GuiButton(25, posX + DX2, posY + y1 + 6 * 22, 20, 20, "-"));
        buttonList.add(new GuiButton(29, posX + DX2, posY + y1 + 7 * 22, 20, 20, "-"));

        buttonList.add(new GuiButton(12, posX + DX3, posY + y1 + 0 * 22, 20, 20, "+"));
        buttonList.add(new GuiButton(13, posX + DX3, posY + y1 + 1 * 22, 20, 20, "+"));
        buttonList.add(new GuiButton(14, posX + DX3, posY + y1 + 2 * 22, 20, 20, "+"));
        buttonList.add(new GuiButton(15, posX + DX3, posY + y1 + 3 * 22, 20, 20, "+"));
        buttonList.add(new GuiButton(16, posX + DX3, posY + y1 + 4 * 22, 20, 20, "+"));
        buttonList.add(new GuiButton(17, posX + DX3, posY + y1 + 5 * 22, 20, 20, "+"));
        buttonList.add(new GuiButton(26, posX + DX3, posY + y1 + 6 * 22, 20, 20, "+"));
        buttonList.add(new GuiButton(30, posX + DX3, posY + y1 + 7 * 22, 20, 20, "+"));

        buttonList.add(new GuiButton(18, posX + DX4, posY + y1 + 0 * 22, 20, 20, "-"));
        buttonList.add(new GuiButton(19, posX + DX4, posY + y1 + 1 * 22, 20, 20, "-"));
        buttonList.add(new GuiButton(20, posX + DX4, posY + y1 + 2 * 22, 20, 20, "-"));
        buttonList.add(new GuiButton(21, posX + DX4, posY + y1 + 3 * 22, 20, 20, "-"));
        buttonList.add(new GuiButton(22, posX + DX4, posY + y1 + 4 * 22, 20, 20, "-"));
        buttonList.add(new GuiButton(23, posX + DX4, posY + y1 + 5 * 22, 20, 20, "-"));
        buttonList.add(new GuiButton(27, posX + DX4, posY + y1 + 6 * 22, 20, 20, "-"));
        buttonList.add(new GuiButton(31, posX + DX4, posY + y1 + 7 * 22, 20, 20, "-"));
    }

    private void page2Txt() {
        int posX = (this.width - xSize) / 2;
        int posY = (this.height - ySize) / 2;

        int col1 = posX + 30;
        int col2 = posX + 141;
        int ln1 = 20;
        int ln2 = 30;

        String spawn_X = String.valueOf(Math.round(spawn_x * 10F) / 10F);
        String spawn_Y = String.valueOf(Math.round(spawn_y * 10F) / 10F);
        String spawn_Z = String.valueOf(Math.round(spawn_z * 10F) / 10F);
        String random_spawn_X = String.valueOf(Math.round(random_spawn_x * 10F) / 10F);
        String random_spawn_Y = String.valueOf(Math.round(random_spawn_y * 10F) / 10F);
        String random_spawn_Z = String.valueOf(Math.round(random_spawn_z * 10F) / 10F);
        String Gravity = String.valueOf(Math.round(gravity * 1000F) / 1000F);

        fontRendererObj.drawString("Spawn X:", col1, posY + ln1 + 0 * 22, 0x000000, false);
        fontRendererObj.drawString(spawn_X, col1, posY + ln2 + 0 * 22, 0x000000, false);
        fontRendererObj.drawString("Spawn Y:", col1, posY + ln1 + 1 * 22, 0x000000, false);
        fontRendererObj.drawString(spawn_Y, col1, posY + ln2 + 1 * 22, 0x000000, false);
        fontRendererObj.drawString("Spawn Z:", col1, posY + ln1 + 2 * 22, 0x000000, false);
        fontRendererObj.drawString(spawn_Z, col1, posY + ln2 + 2 * 22, 0x000000, false);

        for (int i = 0; i < 3; i++)
            fontRendererObj.drawStringWithShadow("(+)", posX + 98, posY + 25 + i * 22, 0xFFFFFF);

        fontRendererObj.drawString("Random:0", col2, posY + ln1 + 0 * 22, 0x000000, false);
        fontRendererObj.drawString("> " + random_spawn_X, col2, posY + ln2 + 0 * 22, 0x000000, false);
        fontRendererObj.drawString("Random:0", col2, posY + ln1 + 1 * 22, 0x000000, false);
        fontRendererObj.drawString("> " + random_spawn_Y, col2, posY + ln2 + 1 * 22, 0x000000, false);
        fontRendererObj.drawString("Random:0", col2, posY + ln1 + 2 * 22, 0x000000, false);
        fontRendererObj.drawString("> " + random_spawn_Z, col2, posY + ln2 + 2 * 22, 0x000000, false);

        fontRendererObj.drawString("Delay:", col1, posY + ln1 + 3 * 22, 0x000000, false);
        fontRendererObj.drawString("" + spawn_rate, col1, posY + ln2 + 3 * 22, 0x000000, false);
        fontRendererObj.drawString("Fade:", col1, posY + ln1 + 4 * 22, 0x000000, false);
        fontRendererObj.drawString("" + fade, col1, posY + ln2 + 4 * 22, 0x000000, false);
        fontRendererObj.drawString("Gravity:", col1, posY + ln1 + 5 * 22, 0x000000, false);
        fontRendererObj.drawString(Gravity, col1, posY + ln2 + 5 * 22, 0x000000, false);
    }

    private void page2Buttons() {
        int posX = (this.width - xSize) / 2;
        int posY = (this.height - ySize) / 2;
        int DX1 = 5;
        int DX2 = DX1 + 71;
        int DX3 = DX2 + 40;
        int DX4 = DX3 + 71;
        int y1 = 19;

        buttonList.add(new GuiButton(34, posX + DX1, posY + y1 + 0 * 22, 20, 20, "+"));
        buttonList.add(new GuiButton(38, posX + DX1, posY + y1 + 1 * 22, 20, 20, "+"));
        buttonList.add(new GuiButton(42, posX + DX1, posY + y1 + 2 * 22, 20, 20, "+"));
        buttonList.add(new GuiButton(46, posX + DX1, posY + y1 + 3 * 22, 20, 20, "+"));
        buttonList.add(new GuiButton(48, posX + DX1, posY + y1 + 4 * 22, 20, 20, "+"));
        buttonList.add(new GuiButton(52, posX + DX1, posY + y1 + 5 * 22, 20, 20, "+"));

        buttonList.add(new GuiButton(35, posX + DX2, posY + y1 + 0 * 22, 20, 20, "-"));
        buttonList.add(new GuiButton(39, posX + DX2, posY + y1 + 1 * 22, 20, 20, "-"));
        buttonList.add(new GuiButton(43, posX + DX2, posY + y1 + 2 * 22, 20, 20, "-"));
        buttonList.add(new GuiButton(47, posX + DX2, posY + y1 + 3 * 22, 20, 20, "-"));
        buttonList.add(new GuiButton(49, posX + DX2, posY + y1 + 4 * 22, 20, 20, "-"));
        buttonList.add(new GuiButton(53, posX + DX2, posY + y1 + 5 * 22, 20, 20, "-"));

        buttonList.add(new GuiButton(36, posX + DX3, posY + y1 + 0 * 22, 20, 20, "+"));
        buttonList.add(new GuiButton(40, posX + DX3, posY + y1 + 1 * 22, 20, 20, "+"));
        buttonList.add(new GuiButton(44, posX + DX3, posY + y1 + 2 * 22, 20, 20, "+"));

        buttonList.add(new GuiButton(37, posX + DX4, posY + y1 + 0 * 22, 20, 20, "-"));
        buttonList.add(new GuiButton(41, posX + DX4, posY + y1 + 1 * 22, 20, 20, "-"));
        buttonList.add(new GuiButton(45, posX + DX4, posY + y1 + 2 * 22, 20, 20, "-"));

        buttonList.add(new GuiButton(
                50, posX + DX3 - 11, posY + y1 + 3 * 22, 102, 20, "Block Collision: " + (collide ? "on" : "off")));
        buttonList.add(new GuiButton(
                51, posX + DX3 - 11, posY + y1 + 4 * 22, 102, 20, "Particle Selected: " + selected_particle));

        buttonList.add(new GuiButton(
                58, posX + DX3 - 11, posY + y1 + 5 * 22, 102, 20, "Enabled: " + (particles_enabled ? "on" : "off")));
    }

    private void page3Txt() {
        int posX = (this.width - xSize) / 2;
        int posY = (this.height - ySize) / 2;

        int col1 = posX + 30;
        int col2 = posX + 141;
        int ln1 = 20;
        int ln2 = 30;

        String pitch = String.valueOf(Math.round(beam_pitch * 100F) / 100F);
        String yaw = String.valueOf(Math.round(beam_yaw * 100F) / 100F);
        String length = String.valueOf(Math.round(beam_length * 100F) / 100F);
        String rotation = String.valueOf(Math.round(beam_rotation * 100F) / 100F);
        String scale = String.valueOf(Math.round(beam_scale * 100F) / 100F);

        fontRendererObj.drawString("Red:", col1, posY + ln1 + 0 * 22, 0x000000, false);
        fontRendererObj.drawString(String.valueOf(beam_red), col1, posY + ln2 + 0 * 22, 0x000000, false);
        fontRendererObj.drawString("Green:", col1, posY + ln1 + 1 * 22, 0x000000, false);
        fontRendererObj.drawString(String.valueOf(beam_green), col1, posY + ln2 + 1 * 22, 0x000000, false);
        fontRendererObj.drawString("Blue:", col1, posY + ln1 + 2 * 22, 0x000000, false);
        fontRendererObj.drawString(String.valueOf(beam_blue), col1, posY + ln2 + 2 * 22, 0x000000, false);

        fontRendererObj.drawString("Y Rot:", col1, posY + ln1 + 3 * 22, 0x000000, false);
        fontRendererObj.drawString(pitch, col1, posY + ln2 + 3 * 22, 0x000000, false);
        fontRendererObj.drawString("Z,X Rot:", col1, posY + ln1 + 4 * 22, 0x000000, false);
        fontRendererObj.drawString(yaw, col1, posY + ln2 + 4 * 22, 0x000000, false);
        fontRendererObj.drawString("Length:", col1, posY + ln1 + 5 * 22, 0x000000, false);
        fontRendererObj.drawString(length, col1, posY + ln2 + 5 * 22, 0x000000, false);
        fontRendererObj.drawString("Rotation:", col1, posY + ln1 + 6 * 22, 0x000000, false);
        fontRendererObj.drawString(rotation, col1, posY + ln2 + 6 * 22, 0x000000, false);
        fontRendererObj.drawString("Scale:", col1, posY + ln1 + 7 * 22, 0x000000, false);
        fontRendererObj.drawString(scale, col1, posY + ln2 + 7 * 22, 0x000000, false);
    }

    private void page3Buttons() {
        int posX = (this.width - xSize) / 2;
        int posY = (this.height - ySize) / 2;
        int DX1 = 5; // x pos for row 1,2,3,4
        int DX2 = DX1 + 71;
        int DX3 = DX2 + 40;
        int DX4 = DX3 + 71;
        int y1 = 19;

        buttonList.add(new GuiButton(100, posX + DX1, posY + y1 + 0 * 22, 20, 20, "+"));
        buttonList.add(new GuiButton(101, posX + DX1, posY + y1 + 1 * 22, 20, 20, "+"));
        buttonList.add(new GuiButton(102, posX + DX1, posY + y1 + 2 * 22, 20, 20, "+"));
        buttonList.add(new GuiButton(103, posX + DX1, posY + y1 + 3 * 22, 20, 20, "+"));
        buttonList.add(new GuiButton(104, posX + DX1, posY + y1 + 4 * 22, 20, 20, "+"));
        buttonList.add(new GuiButton(105, posX + DX1, posY + y1 + 5 * 22, 20, 20, "+"));
        buttonList.add(new GuiButton(106, posX + DX1, posY + y1 + 6 * 22, 20, 20, "+"));
        buttonList.add(new GuiButton(107, posX + DX1, posY + y1 + 7 * 22, 20, 20, "+"));

        buttonList.add(new GuiButton(108, posX + DX2, posY + y1 + 0 * 22, 20, 20, "-"));
        buttonList.add(new GuiButton(109, posX + DX2, posY + y1 + 1 * 22, 20, 20, "-"));
        buttonList.add(new GuiButton(110, posX + DX2, posY + y1 + 2 * 22, 20, 20, "-"));
        buttonList.add(new GuiButton(111, posX + DX2, posY + y1 + 3 * 22, 20, 20, "-"));
        buttonList.add(new GuiButton(112, posX + DX2, posY + y1 + 4 * 22, 20, 20, "-"));
        buttonList.add(new GuiButton(113, posX + DX2, posY + y1 + 5 * 22, 20, 20, "-"));
        buttonList.add(new GuiButton(114, posX + DX2, posY + y1 + 6 * 22, 20, 20, "-"));
        buttonList.add(new GuiButton(115, posX + DX2, posY + y1 + 7 * 22, 20, 20, "-"));

        buttonList.add(new GuiButton(
                116, posX + DX3 - 11, posY + y1 + 0 * 22, 102, 20, "Enabled: " + (beam_enabled ? "on" : "off")));
        buttonList.add(new GuiButton(
                117, posX + DX3 - 11, posY + y1 + 1 * 22, 102, 20, "Render Core: " + (render_core ? "on" : "off")));

        buttonList.add(new GuiButton(127, posX + DX3 - 11, posY + y1 + 7 * 22, 102, 20, "Take note of values"));
    }

    private void beamActions(GuiButton button) {
        int value = 1;
        float value_F;
        short packetValue = 0;
        if (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54)) value = 10;
        if (Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157)) value = 100;
        if ((Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157)) && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54)))
            value = 1000;

        value_F = (value) / 100F;

        switch (button.id) {
            case 100: // beam red +
                beam_red = (beam_red + value) > 255 ? 255 : beam_red + value;
                packetValue = (short) beam_red;
                break;
            case 101: // beam green +
                beam_green = (beam_green + value) > 255 ? 255 : beam_green + value;
                packetValue = (short) beam_green;
                break;
            case 102: // beam blue +
                beam_blue = (beam_blue + value) > 255 ? 255 : beam_blue + value;
                packetValue = (short) beam_blue;
                break;
            case 103: // beam pitch +
                beam_pitch = (beam_pitch + value_F) > 180F ? 180F : beam_pitch + value_F;
                packetValue = (short) (beam_pitch * 100F);
                break;
            case 104: // beam yaw +
                beam_yaw = (beam_yaw + value_F) > 180F ? 180F : beam_yaw + value_F;
                packetValue = (short) (beam_yaw * 100F);
                break;
            case 105: // beam length +
                beam_length = (beam_length + value_F) > 320F ? 320F : beam_length + value_F;
                packetValue = (short) (beam_length * 100F);
                break;
            case 106: // beam rotation +
                beam_rotation = (beam_rotation + value_F) > 1F ? 1F : beam_rotation + value_F;
                packetValue = (short) (beam_rotation * 100F);
                break;
            case 107: // beam scale +
                beam_scale = (beam_scale + value_F) > 5F ? 5F : beam_scale + value_F;
                packetValue = (short) (beam_scale * 100F);
                break;
            case 108: // beam red -
                beam_red = (beam_red - value) < 0 ? 0 : beam_red - value;
                packetValue = (short) beam_red;
                break;
            case 109: // beam green -
                beam_green = (beam_green - value) < 0 ? 0 : beam_green - value;
                packetValue = (short) beam_green;
                break;
            case 110: // beam blue -
                beam_blue = (beam_blue - value) < 0 ? 0 : beam_blue - value;
                packetValue = (short) beam_blue;
                break;
            case 111: // beam pitch -
                beam_pitch = (beam_pitch - value_F) < -180F ? -180F : beam_pitch - value_F;
                packetValue = (short) (beam_pitch * 100F);
                break;
            case 112: // beam yaw -
                beam_yaw = (beam_yaw - value_F) < -180F ? -180F : beam_yaw - value_F;
                packetValue = (short) (beam_yaw * 100F);
                break;
            case 113: // beam length -
                beam_length = (beam_length - value_F) < -0F ? -0F : beam_length - value_F;
                packetValue = (short) (beam_length * 100F);
                break;
            case 114: // beam rotation -
                beam_rotation = (beam_rotation - value_F) < -1F ? -1F : beam_rotation - value_F;
                packetValue = (short) (beam_rotation * 100F);
                break;
            case 115: // beam scale -
                beam_scale = (beam_scale - value_F) < -0F ? -0F : beam_scale - value_F;
                packetValue = (short) (beam_scale * 100F);
                break;
            case 116: // beam enabled
                beam_enabled = !beam_enabled;
                packetValue = beam_enabled ? (byte) 1 : (byte) 0;
                initGui();
                break;
            case 117: // beam enabled
                render_core = !render_core;
                packetValue = render_core ? (byte) 1 : (byte) 0;
                initGui();
                break;
        }
        DraconicEvolution.network.sendToServer(
                new ParticleGenPacket((byte) button.id, packetValue, tile.xCoord, tile.yCoord, tile.zCoord));
    }
    // @formatter:on

}
