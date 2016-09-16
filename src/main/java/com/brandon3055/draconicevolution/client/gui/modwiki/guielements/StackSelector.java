package com.brandon3055.draconicevolution.client.gui.modwiki.guielements;

import com.brandon3055.brandonscore.client.gui.modulargui.IModularGui;
import com.brandon3055.brandonscore.client.gui.modulargui.MGuiElementBase;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.IMGuiListener;
import com.brandon3055.brandonscore.client.gui.modulargui.modularelements.MGuiStackIcon;
import com.brandon3055.brandonscore.lib.StackReference;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 13/09/2016.
 */
public class StackSelector extends MGuiElementBase {

    public IMGuiListener listener;
    private List<MGuiStackIcon> selection = new ArrayList<MGuiStackIcon>();

    public StackSelector(IModularGui modularGui, int xPos, int yPos, int xSize, int ySize) {
        super(modularGui, xPos, yPos, xSize, ySize);
    }

    @Override
    public void renderBackgroundLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        drawBorderedRect(xPos, yPos, xSize, ySize, 1, 0xFF707070, 0xFF000000);
        super.renderBackgroundLayer(minecraft, mouseX, mouseY, partialTicks);
    }

    public void setListener(IMGuiListener listener) {
        this.listener = listener;
    }

    public void setStacks(List<ItemStack> stacks) {
        toRemove.addAll(selection);
        int cols = (int)Math.floor(xSize / 19D);
//        LogHelper.dev("Cols "+cols);
        int index = 0;
        for (ItemStack stack : stacks) {
            if (stack != null) {
                int x = index % cols;
//                LogHelper.dev(index+" Col "+x+" "+cols);
                int y = index / cols;

                MGuiStackIcon stackIcon = new MGuiStackIcon(modularGui, xPos + 2 + (x * 19), yPos + 2 + (y * 19), 18, 18, new StackReference(stack));
                addChild(stackIcon);
                selection.add(stackIcon);
                index++;
            }
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (MGuiElementBase element : childElements) {
            if (element.isMouseOver(mouseX, mouseY)) {
                if (selection.contains(element)) {
                    if (listener != null) {
                        listener.onMGuiEvent("SELECTOR_PICK", element);
                    }
                    return true;
                }
                else if (element.isEnabled() && element.mouseClicked(mouseX, mouseY, mouseButton)) {
                    return true;
                }
            }
        }
        return isMouseOver(mouseX, mouseY);
    }
}
