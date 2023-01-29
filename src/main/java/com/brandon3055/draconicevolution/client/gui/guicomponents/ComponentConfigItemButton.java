package com.brandon3055.draconicevolution.client.gui.guicomponents;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import com.brandon3055.brandonscore.client.gui.guicomponents.ComponentBase;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.utills.IConfigurableItem;
import com.brandon3055.draconicevolution.common.utills.IInventoryTool;
import com.brandon3055.draconicevolution.common.utills.ItemConfigField;

/**
 * Created by Brandon on 29/12/2014.
 */
public class ComponentConfigItemButton extends ComponentBase {

    private static final ResourceLocation texture = new ResourceLocation(
            References.RESOURCESPREFIX + "textures/gui/Widgets.png");
    public int slot;
    private InventoryPlayer inventory;
    public boolean hasValidItem = false;

    public ComponentConfigItemButton(int x, int y, int slot, EntityPlayer player) {
        super(x, y);
        this.slot = slot;
        this.inventory = player.inventory;
        refreshState();
    }

    public void refreshState() {
        hasValidItem = (inventory.getStackInSlot(slot) != null
                && ((inventory.getStackInSlot(slot).getItem() instanceof IConfigurableItem
                        && !((IConfigurableItem) inventory.getStackInSlot(slot).getItem())
                                .getFields(inventory.getStackInSlot(slot), slot).isEmpty())
                        || inventory.getStackInSlot(slot).getItem() instanceof IInventoryTool));
    }

    @Override
    public int getWidth() {
        return 18;
    }

    @Override
    public int getHeight() {
        return 18;
    }

    @Override
    public void renderBackground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        int heightOffset = isMouseOver(mouseX, mouseY) ? 36 : 18;
        if (!hasValidItem) drawTexturedModalRect(x, y, 0, 0, getWidth(), getHeight());
        else drawTexturedModalRect(x, y, 0, heightOffset, getWidth(), getHeight());

        if (inventory.getStackInSlot(slot) != null) {
            drawItemStack(inventory.getStackInSlot(slot), x + 1, y + 1, "null");
        }
    }

    @Override
    public void renderForground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {}

    @Override
    public void renderFinal(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
        if (isMouseOver(mouseX, mouseY) && hasValidItem) {
            List<String> list = new ArrayList<String>();
            List<ItemConfigField> fields = ((IConfigurableItem) inventory.getStackInSlot(slot).getItem())
                    .getFields(inventory.getStackInSlot(slot), slot);
            for (ItemConfigField field : fields) {
                list.add(field.getLocalizedName() + ": " + field.getFormattedValue());
            }
            drawHoveringText(list, mouseX + offsetX, mouseY + offsetY + 10, fontRendererObj);
        }
    }

    @Override
    public void mouseClicked(int x, int y, int button) {
        if (hasValidItem) {
            Minecraft.getMinecraft().getSoundHandler()
                    .playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
        }
    }
}
