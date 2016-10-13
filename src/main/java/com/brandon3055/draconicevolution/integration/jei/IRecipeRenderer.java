package com.brandon3055.draconicevolution.integration.jei;

import net.minecraft.client.Minecraft;

/**
 * Created by brandon3055 on 21/09/2016.
 */
public interface IRecipeRenderer {

    int getWidth();

    int getHeight();

    String getTitle();

    void render(Minecraft mc, int xPos, int yPos, int mouseX, int mouseY);

    boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton);
}