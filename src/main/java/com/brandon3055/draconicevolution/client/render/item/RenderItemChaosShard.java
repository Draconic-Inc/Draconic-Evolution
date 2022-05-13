package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.render.model.OBJParser;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Vector3;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileChaosCrystal;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.utils.ResourceHelperDE;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.Map;

/**
 * Created by brandon3055 on 27/2/20.
 */
public class RenderItemChaosShard implements IItemRenderer {

    private CCModel shard;
    private Item item;

//    private static RenderType crystalType = RenderType.create("crystal_type", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, GL11.GL_QUADS, 256, RenderType.CompositeState.builder()
//            .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(DraconicEvolution.MODID, "textures/item/chaos_crystal.png"), false, false))
//            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
//            .setTexturingState(new RenderStateShard.TexturingStateShard("lighting", RenderSystem::disableLighting, SneakyUtils.none()))
//            .createCompositeState(false));

    public RenderItemChaosShard(Item item) {
        this.item = item;
        Map<String, CCModel> map = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/item/chaos_shard.obj")).quads().ignoreMtl().parse();
        shard = CCModel.combine(map.values()).backfacedCopy();
    }

    //region Unused

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }

    //endregion

    @Override
    public void renderItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack mStack, MultiBufferSource getter, int packedLight, int packedOverlay) {
//        Matrix4 mat = new Matrix4(mStack);
//        CCRenderState ccrs = CCRenderState.instance();
//        ccrs.reset();
//        ccrs.brightness = packedLight;
//        ccrs.overlay = packedOverlay;
//        mat.apply(new Scale(item == DEContent.chaos_shard ? 1 : item == DEContent.chaos_frag_large ? 0.75 : item == DEContent.chaos_frag_medium ? 0.5 : 0.25).at(new Vector3(0.5, 0.5, 0.5)));
//
//        ccrs.bind(new ShaderRenderType(RenderTileChaosCrystal.chaosType, RenderTileChaosCrystal.chaosShader, RenderTileChaosCrystal.chaosShader.pushCache()), getter);
//        shard.render(ccrs, mat);
//        ccrs.baseColour = 0xFFFFFFF0;
//        ccrs.bind(crystalType, getter);
//        shard.render(ccrs, mat);
    }

    @Override
    public ModelState getModelTransform() {
        return TransformUtils.DEFAULT_ITEM;
    }
}
