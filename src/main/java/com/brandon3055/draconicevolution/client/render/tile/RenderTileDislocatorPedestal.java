package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.render.TextureUtils;
import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.brandonscore.utils.ModelUtils;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDislocatorPedestal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;

import java.util.List;

/**
 * Created by brandon3055 on 27/09/2016.
 */
public class RenderTileDislocatorPedestal extends TESRBase<TileDislocatorPedestal> {

    public static List<BakedQuad> modelQuads = null;

    @Override
    public void renderTileEntityAt(TileDislocatorPedestal te, double x, double y, double z, float partialTicks, int destroyStage) {
        if (modelQuads == null) {
            modelQuads = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(DEFeatures.dislocatorPedestal.getDefaultState()).getQuads(DEFeatures.dislocatorPedestal.getDefaultState(), null, 0);
        }


        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y, z + 0.5);
        GlStateManager.rotate(-te.rotation.value * 22.5F, 0, 1, 0);
        GlStateManager.translate(-0.5, 0, -0.5);

        TextureUtils.bindBlockTexture();
        ModelUtils.renderQuads(modelQuads);


        if (te.getStackInSlot(0) != null) {
            GlStateManager.translate(0.5, 0.79, 0.52);
            GlStateManager.rotate(-67.5F, 1, 0, 0);
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            renderItem(te.getStackInSlot(0));
        }

        GlStateManager.popMatrix();

        //TODO render destination name. After i fix the advanced dislocator mess...
    }
}
