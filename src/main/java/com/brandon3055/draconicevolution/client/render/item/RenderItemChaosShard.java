package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.model.PerspectiveModelState;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.render.model.OBJParser;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.client.shader.BCShaders;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.init.DEContent;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by brandon3055 on 27/2/20.
 */
public class RenderItemChaosShard implements IItemRenderer {

    private static final RenderType CHAOS_CRYSTAL_INNER = RenderType.create(MODID + ":chaos_crystal_inner", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, RenderType.CompositeState.builder()
            .setShaderState(new RenderStateShard.ShaderStateShard(BCShaders.CHAOS_ENTITY_SHADER::getShaderInstance))
            .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(MODID, "textures/item/equipment/chaos_shader.png"), true, false))
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setCullState(RenderStateShard.NO_CULL)
            .setLightmapState(RenderStateShard.LIGHTMAP)
            .setOverlayState(RenderStateShard.OVERLAY)
            .createCompositeState(false));
    private static final RenderType CHAOS_CRYSTAL = RenderType.create(MODID + ":chaos_crystal", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, RenderType.CompositeState.builder()
            .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeCutoutShader)) //TODO figure out render type stuff.
            .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(MODID, "textures/block/chaos_crystal.png"), false, false))
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setLightmapState(RenderStateShard.LIGHTMAP)
            .setOverlayState(RenderStateShard.OVERLAY)
            .createCompositeState(false));

    private final CCModel shard;
    private final Item item;

    public RenderItemChaosShard(Item item) {
        this.item = item;
        Map<String, CCModel> map = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/item/chaos_shard.obj"))
                .ignoreMtl()
                .parse();
        shard = CCModel.combine(map.values())
                .backfacedCopy()
                .computeNormals();
    }

    @Override
    public void renderItem(ItemStack stack, ItemDisplayContext context, PoseStack mStack, MultiBufferSource getter, int packedLight, int packedOverlay) {
        Matrix4 mat = new Matrix4(mStack);
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;
        mat.apply(new Scale(item == DEContent.CHAOS_SHARD.get() ? 1 : item == DEContent.CHAOS_FRAG_LARGE.get() ? 0.75 : item == DEContent.CHAOS_FRAG_MEDIUM.get() ? 0.5 : 0.25).at(new Vector3(0.5, 0.5, 0.5)));

        BCShaders.CHAOS_ENTITY_SHADER.getModelMatUniform().glUniformMatrix4f(new Matrix4());
        BCShaders.CHAOS_ENTITY_SHADER.getSimpleLightUniform().glUniform1b(true);
        BCShaders.CHAOS_ENTITY_SHADER.getDisableLightUniform().glUniform1b(false);
        BCShaders.CHAOS_ENTITY_SHADER.getDisableOverlayUniform().glUniform1b(false);

        ccrs.bind(CHAOS_CRYSTAL_INNER, getter);
        shard.render(ccrs, mat);

        ccrs.baseColour = 0xFFFFFFF0;
        ccrs.bind(CHAOS_CRYSTAL, getter);
        shard.render(ccrs, mat);
    }

    // @formatter:off
    @Override
    public @Nullable PerspectiveModelState getModelState() { return TransformUtils.DEFAULT_ITEM; }
    @Override public boolean useAmbientOcclusion() { return false; }
    @Override public boolean isGui3d() { return false; }
    @Override public boolean usesBlockLight() { return false; }

    //This is not cursed at all! idk what your talking about!
    public static class CHAOS_SHARD extends RenderItemChaosShard { public CHAOS_SHARD() {super(DEContent.CHAOS_SHARD.get());}}
    public static class CHAOS_FRAG_LARGE extends RenderItemChaosShard { public CHAOS_FRAG_LARGE() {super(DEContent.CHAOS_FRAG_LARGE.get());}}
    public static class CHAOS_FRAG_MEDIUM extends RenderItemChaosShard { public CHAOS_FRAG_MEDIUM() {super(DEContent.CHAOS_FRAG_MEDIUM.get());}}
    public static class CHAOS_FRAG_SMALL extends RenderItemChaosShard { public CHAOS_FRAG_SMALL() {super(DEContent.CHAOS_FRAG_SMALL.get());}}
    // @formatter:on
}
