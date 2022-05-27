package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.model.OBJParser;
import codechicken.lib.render.shader.ShaderObject;
import codechicken.lib.render.shader.ShaderProgram;
import codechicken.lib.render.shader.ShaderProgramBuilder;
import codechicken.lib.render.shader.UniformType;
import codechicken.lib.util.ClientUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.client.render.RenderUtils;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.tileentity.TileChaosCrystal;
import com.brandon3055.draconicevolution.client.DEShaders;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.render.item.ToolRenderBase;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

/**
 * Created by brandon3055 on 24/9/2015.
 */
public class RenderTileChaosCrystal implements BlockEntityRenderer<TileChaosCrystal> {
    private CCModel model;

    public static RenderType chaosType = RenderType.create("chaos_type", DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, VertexFormat.Mode.QUADS, 256, RenderType.CompositeState.builder()
            .setShaderState(new RenderStateShard.ShaderStateShard(() -> DEShaders.chaosShader))
            .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(DraconicEvolution.MODID, "textures/item/equipment/chaos_shader.png"), true, false))
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setCullState(RenderStateShard.NO_CULL)
            .createCompositeState(false));

    public static RenderType crystalType = RenderType.create("chaos_crystal", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, RenderType.CompositeState.builder()
            .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorTexLightmapShader))
            .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(DraconicEvolution.MODID, "textures/block/chaos_crystal.png"), false, false))
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .createCompositeState(false));


    private static RenderType shieldType = RenderType.create("chaos_shield_type", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, RenderType.CompositeState.builder()
            .setShaderState(new RenderStateShard.ShaderStateShard(() -> DEShaders.armorShieldShader))
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setLightmapState(RenderStateShard.LIGHTMAP)
            .setCullState(RenderStateShard.NO_CULL)
            .createCompositeState(false));



    public RenderTileChaosCrystal(BlockEntityRendererProvider.Context context) {
        Map<String, CCModel> map = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/block/chaos_crystal.obj")).quads().ignoreMtl().parse();
        model = CCModel.combine(map.values()).backfacedCopy();
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public void render(TileChaosCrystal te, float partialTicks, PoseStack mStack, MultiBufferSource getter, int packedLight, int packedOverlay) {
        if (te.parentPos.get().getY() != -1) return;
        Matrix4 mat = new Matrix4(mStack);
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;

        mat.translate(0.5, 0.5, 0.5);
        mat.rotate((ClientEventHandler.elapsedTicks + partialTicks) / 180F, Vector3.Y_POS);
        mat.scale(0.75F);

        Player player = Minecraft.getInstance().player;
        DEShaders.chaosTime.glUniform1f((float) ClientUtils.getRenderTime());
        DEShaders.chaosYaw.glUniform1f((float) (player.getYRot() * MathHelper.torad));
        DEShaders.chaosPitch.glUniform1f((float) -(player.getXRot() * MathHelper.torad));
        ccrs.bind(RenderTileChaosCrystal.chaosType, getter);
        model.render(ccrs, mat);

        ccrs.baseColour = 0xFFFFFFF0;
        ccrs.bind(crystalType, getter);
        model.render(ccrs, mat);

        if (!te.guardianDefeated.get()) {
            DEShaders.armorShieldActivation.glUniform1f(1F);
            DEShaders.armorShieldColour.glUniform4f(1F, 0F, 0F, 1F);
            ccrs.bind(RenderTileChaosCrystal.shieldType, getter);
            model.render(ccrs, mat);
        }
    }
}
