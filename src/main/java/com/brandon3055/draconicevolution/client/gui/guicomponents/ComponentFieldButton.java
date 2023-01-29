package com.brandon3055.draconicevolution.client.gui.guicomponents;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.brandon3055.brandonscore.client.gui.guicomponents.ComponentBase;
import com.brandon3055.brandonscore.common.utills.DataUtills;
import com.brandon3055.draconicevolution.client.gui.componentguis.GUIToolConfig;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.utills.IConfigurableItem;
import com.brandon3055.draconicevolution.common.utills.ItemConfigField;

/**
 * Created by Brandon on 31/12/2014.
 */
public class ComponentFieldButton extends ComponentBase {

    private static final ResourceLocation widgets = new ResourceLocation(
            References.RESOURCESPREFIX + "textures/gui/Widgets.png");

    public EntityPlayer player;
    public int slot;
    public ItemStack stack;
    public ItemConfigField field;
    public GUIToolConfig gui;

    public ComponentFieldButton(int x, int y, EntityPlayer player, ItemConfigField field, GUIToolConfig gui) {
        super(x, y);
        this.player = player;
        this.slot = field.slot;
        this.stack = player.inventory.getStackInSlot(slot);
        this.field = field;
        this.gui = gui;
    }

    @Override
    public int getWidth() {
        return 150;
    }

    @Override
    public int getHeight() {
        return 12;
    }

    @Override
    public void renderBackground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
        Minecraft.getMinecraft().renderEngine.bindTexture(widgets);

        if (!isMouseOver(mouseX - offsetX, mouseY - offsetY)) {
            drawTexturedModalRect(x, y, 18, 0, getWidth() - 50, getHeight());
            drawTexturedModalRect(x, y + getHeight() - 1, 18, 19, getWidth() - 50, 1);

            drawTexturedModalRect(x + 50, y, 19, 0, getWidth() - 50 - 1, getHeight());
            drawTexturedModalRect(x + 50, y + getHeight() - 1, 19, 19, getWidth() - 50 - 1, 1);
        } else {
            drawTexturedModalRect(x, y, 18, 20, getWidth() - 50, getHeight());
            drawTexturedModalRect(x, y + getHeight() - 1, 18, 39, getWidth() - 50, 1);

            drawTexturedModalRect(x + 50, y, 19, 20, getWidth() - 50 - 1, getHeight());
            drawTexturedModalRect(x + 50, y + getHeight() - 1, 19, 39, getWidth() - 50 - 1, 1);
        }
    }

    @Override
    public void renderForground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
        drawString(
                fontRendererObj,
                field.getLocalizedName(),
                x + offsetX + 2,
                y + offsetY + (getHeight() / 2) - (fontRendererObj.FONT_HEIGHT / 2),
                0xffffff);
    }

    @Override
    public void renderFinal(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
        if (isMouseOver(mouseX, mouseY)) {
            List list = new ArrayList();
            list.add(field.getFormattedValue());
            drawHoveringText(list, mouseX + offsetX, mouseY + offsetY + 10, fontRendererObj);
        }
    }

    @Override
    public void mouseClicked(int x, int y, int button) {
        Minecraft.getMinecraft().getSoundHandler()
                .playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
        if (field.datatype == References.BOOLEAN_ID) {
            field.value = !(Boolean) field.value;
            field.sendChanges();
            ItemStack stack = gui.player.inventory.getStackInSlot(field.slot);
            if (stack != null && stack.getItem() instanceof IConfigurableItem) {
                DataUtills.writeObjectToCompound(
                        IConfigurableItem.ProfileHelper.getProfileCompound(stack),
                        field.value,
                        field.datatype,
                        field.name);
            }
            return;
        }
        gui.setFieldBeingEdited(field);
    }
}
