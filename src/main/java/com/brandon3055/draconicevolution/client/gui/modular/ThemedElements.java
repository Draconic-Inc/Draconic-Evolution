package com.brandon3055.draconicevolution.client.gui.modular;

import com.brandon3055.brandonscore.client.gui.modulargui.GuiElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;

import static com.brandon3055.brandonscore.BCConfig.darkMode;

/**
 * Created by brandon3055 on 8/5/20.
 */
public class ThemedElements {
    public static int getLight() {
        return darkMode ? 0xFFFFFFFF : 0xFFFFFFFF;
    }

    public static int getDark() {
        return darkMode ? 0xFF808080 : 0xFF505050;
    }

    public static int getFill() {
        return darkMode ? 0xFF707070 : GuiElement.midColour(getLight(), getDark());
    }

    public static int getLightScroll() {
        return darkMode ? 0xFF5d5e68 : 0xFFFFFFFF;
    }

    public static int getDarkScroll() {
        return darkMode ? 0xFF353535 : 0xFF505050;
    }

    public static class BorderedContainer extends GuiElement<BorderedContainer> {
        @Override
        public void renderElement(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
            IRenderTypeBuffer.Impl getter = minecraft.getRenderTypeBuffers().getBufferSource();
            int light = getLight();
            int dark = getDark();
            drawShadedRect(getter, xPos(), yPos(), xSize(), ySize(), 1, 0, light, dark, midColour(light, dark));
            drawShadedRect(getter, xPos() + 1, yPos() + 1, xSize() - 2, ySize() - 2, 1, getFill(), dark, light, midColour(light, dark));
            getter.finish();
            super.renderElement(minecraft, mouseX, mouseY, partialTicks);
        }
    }

    public static class ScrollBar extends GuiElement<ScrollBar> {
        private boolean background;

        public ScrollBar(boolean background) {
            this.background = background;
        }

        @Override
        public void renderElement(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
            IRenderTypeBuffer.Impl getter = minecraft.getRenderTypeBuffers().getBufferSource();
            if (background) {
                drawBorderedRect(getter, xPos(), yPos(), xSize(), ySize(), 1, GuiElement.mixColours(ThemedElements.getFill(), 0x00101010, true), 0);
            } else {
                int light = getLightScroll();//mixColours(getLight(), 0x00101010, true);
                int dark = getDarkScroll();//mixColours(getDark(), 0x00101010, true);
                drawShadedRect(getter, xPos() + 1, yPos() + 1, xSize() - 2, ySize() - 2, 1, midColour(light, dark), light, dark, midColour(light, dark));
            }
            getter.finish();
            super.renderElement(minecraft, mouseX, mouseY, partialTicks);
        }
    }
}
