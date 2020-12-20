package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.render.shader.ShaderRenderType;
import codechicken.lib.util.SneakyUtils;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.Vector3;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileChaosCrystal;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.utils.ResourceHelperDE;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.TransformationMatrix;
import org.lwjgl.opengl.GL11;

import java.util.Map;

import static net.minecraft.client.renderer.RenderState.TRANSLUCENT_TRANSPARENCY;

/**
 * Created by brandon3055 on 27/2/20.
 */
public class RenderItemChaosShard implements IItemRenderer {

    private CCModel shard;
    private Item item;

    private static RenderType crystalType = RenderType.makeType("crystal_type", DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP, GL11.GL_QUADS, 256, RenderType.State.getBuilder()
            .texture(new RenderState.TextureState(new ResourceLocation(DraconicEvolution.MODID, "textures/item/chaos_crystal.png"), false, false))
            .transparency(TRANSLUCENT_TRANSPARENCY)
            .texturing(new RenderState.TexturingState("lighting", RenderSystem::disableLighting, SneakyUtils.none()))
            .build(false));

    public RenderItemChaosShard(Item item) {
        this.item = item;
        Map<String, CCModel> map = OBJParser.parseModels(ResourceHelperDE.getResource("models/item/chaos_shard.obj"), GL11.GL_QUADS, null);
        shard = CCModel.combine(map.values()).backfacedCopy();
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

    @Override
    public boolean isSideLit() {
        return false;
    }

    //endregion

    @Override
    public void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack mStack, IRenderTypeBuffer getter, int packedLight, int packedOverlay) {
        Matrix4 mat = new Matrix4(mStack);
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;
        mat.apply(new Scale(item == DEContent.chaos_shard ? 1 : item == DEContent.chaos_frag_large ? 0.75 : item == DEContent.chaos_frag_medium ? 0.5 : 0.25).at(new Vector3(0.5, 0.5, 0.5)));

        ccrs.bind(new ShaderRenderType(RenderTileChaosCrystal.chaosType, RenderTileChaosCrystal.chaosShader, RenderTileChaosCrystal.chaosShader.pushCache()), getter);
        shard.render(ccrs, mat);
        ccrs.baseColour = 0xFFFFFFF0;
        ccrs.bind(crystalType, getter);
        shard.render(ccrs, mat);
    }

    @Override
    public ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> getTransforms() {
        return TransformUtils.DEFAULT_ITEM;
    }
}
