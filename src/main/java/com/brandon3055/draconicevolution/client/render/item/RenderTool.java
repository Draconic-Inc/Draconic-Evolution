package com.brandon3055.draconicevolution.client.render.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.brandon3055.draconicevolution.client.render.IRenderTweak;

/**
 * Created by brandon3055 on 29/10/2015.
 */
public class RenderTool implements IItemRenderer {

    private IModelCustom toolModel;
    private String toolTexture;
    private IRenderTweak tool;

    // Draconic Sword

    public RenderTool(String model, String texture, IRenderTweak tool) {
        this.tool = tool;
        this.toolModel = AdvancedModelLoader.loadModel(ResourceHandler.getResource(model));
        this.toolTexture = texture;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return type == ItemRenderType.ENTITY && helper == ItemRendererHelper.ENTITY_ROTATION;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        ResourceHandler.bindResource(toolTexture);

        tool.tweakRender(type);
        toolModel.renderAll();

        GL11.glPopMatrix();
    }
}
