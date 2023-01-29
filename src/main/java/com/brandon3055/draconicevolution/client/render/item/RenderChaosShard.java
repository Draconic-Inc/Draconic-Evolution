package com.brandon3055.draconicevolution.client.render.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.client.handler.ResourceHandler;

/**
 * Created by brandon3055 on 1/10/2015.
 */
public class RenderChaosShard implements IItemRenderer {

    private static IModelCustom model = AdvancedModelLoader
            .loadModel(ResourceHandler.getResource("models/chaosCrystalShard.obj"));

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

        if (type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON)
            GL11.glTranslated(0.5, 0.5, 0.5);
        if (type != ItemRenderType.ENTITY) GL11.glRotatef(45, -1, 0, -1);
        else GL11.glRotatef(45, -1, 0, 0);

        GL11.glScalef(0.4F, 0.4F, 0.4F);

        ResourceHandler.bindResource("textures/models/chaosCrystal.png");
        model.renderAll();

        GL11.glPopMatrix();
    }
}
