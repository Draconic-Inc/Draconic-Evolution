package com.brandon3055.draconicevolution.client.gui.guicomponents;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import com.brandon3055.brandonscore.client.gui.guicomponents.ComponentBase;
import com.brandon3055.brandonscore.client.utills.GuiHelper;
import com.brandon3055.brandonscore.common.utills.DataUtills;
import com.brandon3055.draconicevolution.client.gui.componentguis.GUIToolConfig;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.utills.IConfigurableItem;
import com.brandon3055.draconicevolution.common.utills.ItemConfigField;

/**
 * Created by Brandon on 1/01/2015.
 */
public class ComponentFieldAdjuster extends ComponentBase {

    private static final ResourceLocation widgets = new ResourceLocation(
            References.RESOURCESPREFIX + "textures/gui/Widgets.png");
    protected static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");

    public ItemConfigField field;
    public GUIToolConfig gui;

    public ComponentFieldAdjuster(int x, int y, ItemConfigField field, GUIToolConfig gui) {
        super(x, y);
        this.field = field;
        this.gui = gui;
    }

    @Override
    public int getWidth() {
        return 190;
    }

    @Override
    public int getHeight() {
        return 20;
    }

    @Override
    public void renderBackground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
        if (field == null) return;
        minecraft.getTextureManager().bindTexture(buttonTextures);

        if (isBoolean()) {
            renderButton(
                    (getWidth() / 2) - 30,
                    0,
                    60,
                    20,
                    GuiHelper.isInRect(x + (getWidth() / 2) - 30, y, 60, 20, mouseX - offsetX, mouseY - offsetY));
        } else if (isDecimal() || isNonDecimal()) {
            renderButton(
                    (getWidth() / 2) - 43,
                    3,
                    24,
                    14,
                    GuiHelper.isInRect(x + (getWidth() / 2) - 43, y + 3, 24, 14, mouseX - offsetX, mouseY - offsetY));
            renderButton(
                    (getWidth() / 2) - 69,
                    3,
                    24,
                    14,
                    GuiHelper.isInRect(x + (getWidth() / 2) - 69, y + 3, 24, 14, mouseX - offsetX, mouseY - offsetY));
            renderButton(
                    (getWidth() / 2) - 95,
                    3,
                    24,
                    14,
                    GuiHelper.isInRect(x + (getWidth() / 2) - 95, y + 3, 24, 14, mouseX - offsetX, mouseY - offsetY));

            renderButton(
                    (getWidth() / 2) + 19,
                    3,
                    24,
                    14,
                    GuiHelper.isInRect(x + (getWidth() / 2) + 19, y + 3, 24, 14, mouseX - offsetX, mouseY - offsetY));
            renderButton(
                    (getWidth() / 2) + 45,
                    3,
                    24,
                    14,
                    GuiHelper.isInRect(x + (getWidth() / 2) + 45, y + 3, 24, 14, mouseX - offsetX, mouseY - offsetY));
            renderButton(
                    (getWidth() / 2) + 71,
                    3,
                    24,
                    14,
                    GuiHelper.isInRect(x + (getWidth() / 2) + 71, y + 3, 24, 14, mouseX - offsetX, mouseY - offsetY));
        }
    }

    @Override
    public void renderForground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
        if (field == null) return;
        String fieldName = field.getLocalizedName();
        String fieldValue = field.getFormattedValue();
        if (field.datatype == References.DOUBLE_ID) {
            double d = (Double) field.value;
            fieldValue = String.valueOf((double) Math.round(d * 100f) / 100D);
        }
        // if (field.datatype == References.FLOAT_ID) {
        // float d = (Float)field.value;
        // fieldValue = String.valueOf((double)Math.round((double)(d*100f)) / 100D);
        // }

        int centre = fontRendererObj.getStringWidth(fieldName) / 2;
        fontRendererObj.drawString(fieldName, x + getWidth() / 2 - centre, y - 12, 0x00000);
        drawCenteredString(fontRendererObj, fieldValue, x + getWidth() / 2, y + 6, 0xffffff);
        if (field.modifier == null || field.modifier.equals("AOE")) drawCenteredString(
                fontRendererObj,
                StatCollector.translateToLocal("gui.de.max.txt") + " " + field.getMaxFormattedValue(),
                x + getWidth() / 2,
                y + 20,
                0xFFFFFF);

        if (isDecimal() || isNonDecimal()) {
            fontRendererObj.drawString("---", 7, y + 6, 0x000000);
            fontRendererObj.drawString("--", 37, y + 6, 0x000000);
            fontRendererObj.drawString("-", 66, y + 6, 0x000000);
            fontRendererObj.drawString("+", 127, y + 6, 0x000000);
            fontRendererObj.drawString("++", 151, y + 6, 0x000000);
            fontRendererObj.drawString("+++", 174, y + 6, 0x000000);
        }
    }

    @Override
    public void renderFinal(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
        if (field == null) return;
    }

    @Override
    public void mouseClicked(int x, int y, int button) {
        if (field == null) return;

        if (!isBoolean()) {
            if (GuiHelper.isInRect(this.x + (getWidth() / 2) - 43, this.y + 3, 26, 14, x, y)) { // -
                incroment(-1);
            } else if (GuiHelper.isInRect(this.x + (getWidth() / 2) - 69, this.y + 3, 26, 14, x, y)) { // --
                incroment(-10);
            } else if (GuiHelper.isInRect(this.x + (getWidth() / 2) - 95, this.y + 3, 26, 14, x, y)) { // ---
                incroment(-100);
            } else if (GuiHelper.isInRect(this.x + (getWidth() / 2) + 19, this.y + 3, 26, 14, x, y)) { // +
                incroment(1);
            } else if (GuiHelper.isInRect(this.x + (getWidth() / 2) + 45, this.y + 3, 26, 14, x, y)) { // ++
                incroment(10);
            } else if (GuiHelper.isInRect(this.x + (getWidth() / 2) + 71, this.y + 3, 26, 14, x, y)) { // +++
                incroment(100);
            }
        } else if (GuiHelper.isInRect(this.x + (getWidth() / 2) - 30, this.y, 60, 20, x, y)) {
            incroment(1);
        }
    }

    private void incroment(int multiplyer) {
        switch (field.datatype) {
            case References.BYTE_ID:
                byte b = (Byte) field.value;
                b += (Byte) field.incroment * (byte) multiplyer;
                if (b > (Byte) field.max) b = (Byte) field.max;
                if (b < (Byte) field.min) b = (Byte) field.min;
                field.value = b;
                break;
            case References.SHORT_ID:
                short s = (Short) field.value;
                s += (Short) field.incroment * (short) multiplyer;
                if (s > (Short) field.max) s = (Short) field.max;
                if (s < (Short) field.min) s = (Short) field.min;
                field.value = s;
                break;
            case References.INT_ID:
                int i = (Integer) field.value;
                i += (Integer) field.incroment * multiplyer;
                if (i > (Integer) field.max) i = (Integer) field.max;
                if (i < (Integer) field.min) i = (Integer) field.min;
                field.value = i;
                break;
            case References.LONG_ID:
                long l = (Long) field.value;
                l += (Long) field.incroment * (long) multiplyer;
                if (l > (Long) field.max) l = (Long) field.max;
                if (l < (Long) field.min) l = (Long) field.min;
                field.value = l;
                break;
            case References.FLOAT_ID:
                float f = (Float) field.value;
                f += (Float) field.incroment * (float) multiplyer;
                f *= 100F;
                f = Math.round(f);
                f /= 100F;
                if (f > (Float) field.max) f = (Float) field.max;
                if (f < (Float) field.min) f = (Float) field.min;
                field.value = f;
                break;
            case References.DOUBLE_ID:
                double d = (Double) field.value;
                d += (Double) field.incroment * (double) multiplyer;
                if (d > (Double) field.max) d = (Double) field.max;
                if (d < (Double) field.min) d = (Double) field.min;
                field.value = d;
                break;
            case References.BOOLEAN_ID:
                field.value = !(Boolean) field.value;
                break;
        }
        Minecraft.getMinecraft().getSoundHandler()
                .playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
        ItemStack stack = gui.player.inventory.getStackInSlot(field.slot);
        if (stack != null && stack.getItem() instanceof IConfigurableItem) {
            DataUtills.writeObjectToCompound(
                    IConfigurableItem.ProfileHelper.getProfileCompound(stack),
                    field.value,
                    field.datatype,
                    field.name);
        }
        field.sendChanges();
    }

    public void renderButton(int offsetX, int offsetY, int xSize, int ySize, boolean highlighted) {
        int k = highlighted ? 2 : 1;
        int x = this.x + offsetX;
        int y = this.y + offsetY;

        this.drawTexturedModalRect(x, y, 0, 46 + k * 20, xSize / 2, 20 - Math.max(0, 20 - ySize));
        this.drawTexturedModalRect(
                x + xSize / 2,
                y,
                200 - xSize / 2,
                46 + k * 20,
                xSize / 2,
                20 - Math.max(0, 20 - ySize));

        if (ySize < 20) {
            this.drawTexturedModalRect(x, y + 3, 0, (46 + k * 20) + 20 - ySize + 3, xSize - 1, ySize - 3);
            this.drawTexturedModalRect(
                    x + xSize / 2,
                    y + 3,
                    200 - xSize / 2,
                    (46 + k * 20) + 20 - ySize + 3,
                    xSize / 2,
                    ySize - 3);
        }
    }

    private boolean isBoolean() {
        return field.datatype == References.BOOLEAN_ID;
    }

    private boolean isNonDecimal() {
        return field.datatype == References.INT_ID || field.datatype == References.SHORT_ID
                || field.datatype == References.BYTE_ID;
    }

    private boolean isDecimal() {
        return field.datatype == References.FLOAT_ID || field.datatype == References.DOUBLE_ID;
    }
}
