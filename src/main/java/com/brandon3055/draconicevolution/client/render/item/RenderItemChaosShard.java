package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.Vector3;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.utils.ResourceHelperDE;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.Map;

/**
 * Created by brandon3055 on 27/2/20.
 */
public class RenderItemChaosShard implements IItemRenderer {

    private CCModel shard;
    private Item item;

    public RenderItemChaosShard(Item item) {
        this.item = item;
        Map<String, CCModel> map = OBJParser.parseModels(ResourceHelperDE.getResource("models/item/chaos_shard.obj"));
        shard = CCModel.combine(map.values());
    }

    //region Unused

    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    //endregion


    @Override
    public void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack mStack, IRenderTypeBuffer getter, int packedLight, int packedOverlay) {
        RenderSystem.pushMatrix();
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ResourceHelperDE.bindTexture("textures/models/item/chaos_crystal.png");
        ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
        shard.render(ccrs, new Scale(item == DEContent.chaos_shard ? 1 : item == DEContent.chaos_frag_large ? 0.75 : item == DEContent.chaos_frag_medium ? 0.5 : 0.25).at(new Vector3(0.5, 0.5, 0)).with(new Translation(-0.5, -0.5, 0)));
        ccrs.draw();
        RenderSystem.popMatrix();
    }

    @Override
    public ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> getTransforms() {
        return TransformUtils.DEFAULT_BLOCK;
    }

    @Override
    public boolean func_230044_c_() {
        return false;
    }

//    @Override
//    public IModelState getTransforms() {
//        return TransformUtils.DEFAULT_ITEM;
//    }
}
