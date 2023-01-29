package com.brandon3055.draconicevolution.client.render.block;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileReactorEnergyInjector;

/**
 * Created by brandon3055 on 30/7/2015.
 */
public class RenderReactorEnergyInjector implements IItemRenderer {

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        float scale = (1F / 16F);

        if (type == ItemRenderType.INVENTORY) GL11.glRotated(180, 0, 1, 0);
        else if (type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glTranslated(0.5, 0.5, 0.5);
            GL11.glRotated(90, 0, 1, 0);
        }
        GL11.glRotated(180, 0, 0, 1);

        ResourceHandler.bindResource("textures/models/ModelReactorPowerInjector.png");
        RenderTileReactorEnergyInjector.modelReactorEnergyInjector.render(null, 1F, 0F, 0F, 0F, 0F, scale);
        GL11.glPopMatrix();
    }
}
