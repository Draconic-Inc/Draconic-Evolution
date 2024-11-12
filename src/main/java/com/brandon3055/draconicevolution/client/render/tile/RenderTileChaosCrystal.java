package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.model.OBJParser;
import codechicken.lib.util.ClientUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Vector3;
import com.brandon3055.draconicevolution.blocks.tileentity.TileChaosCrystal;
import com.brandon3055.draconicevolution.client.DEShaders;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.Map;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by brandon3055 on 24/9/2015.
 */
public class RenderTileChaosCrystal implements BlockEntityRenderer<TileChaosCrystal> {

    private static final RenderType CHAOS_CRYSTAL_INNER = RenderType.create(MODID + ":chaos_crystal_inner", DefaultVertexFormat.BLOCK, Mode.TRIANGLES, 256, RenderType.CompositeState.builder()
            .setShaderState(new RenderStateShard.ShaderStateShard(() -> DEShaders.chaosBlockShader))
            .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(MODID, "textures/item/equipment/chaos_shader.png"), true, false))
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setCullState(RenderStateShard.NO_CULL)
            .setLightmapState(RenderStateShard.LIGHTMAP)
            .createCompositeState(false));

    private static final RenderType CHAOS_CRYSTAL = RenderType.create(MODID + ":chaos_crystal", DefaultVertexFormat.BLOCK, Mode.TRIANGLES, 256, RenderType.CompositeState.builder()
            .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeCutoutShader)) //TODO Figure out shader
            .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(MODID, "textures/block/chaos_crystal.png"), false, false))
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setLightmapState(RenderStateShard.LIGHTMAP)
            .createCompositeState(false));

    private static final RenderType CHAOS_CRYSTAL_SHIELD = RenderType.create(MODID + ":chaos_shield_type", DefaultVertexFormat.POSITION_TEX, Mode.TRIANGLES, 256, RenderType.CompositeState.builder()
            .setShaderState(new RenderStateShard.ShaderStateShard(() -> DEShaders.shieldShader))
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setLightmapState(RenderStateShard.LIGHTMAP)
            .setCullState(RenderStateShard.NO_CULL)
            .createCompositeState(false));

    private final CCModel model;

    public RenderTileChaosCrystal(BlockEntityRendererProvider.Context context) {
        Map<String, CCModel> map = new OBJParser(new ResourceLocation(MODID, "models/block/chaos_crystal.obj"))
                .ignoreMtl()
                .parse();
        model = CCModel.combine(map.values())
                .backfacedCopy()
                .computeNormals();
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public void render(TileChaosCrystal te, float partialTicks, PoseStack mStack, MultiBufferSource getter, int packedLight, int packedOverlay) {
        if (te.parentPos.notNull()) return;
        Matrix4 mat = new Matrix4(mStack);
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;

        mat.translate(0.5, 0.5, 0.5);
        mat.rotate((ClientEventHandler.elapsedTicks + partialTicks) / 180F, Vector3.Y_POS);
        mat.scale(0.75F);

        Player player = Minecraft.getInstance().player;
        DEShaders.chaosBlockTime.glUniform1f((float) ClientUtils.getRenderTime());
        DEShaders.chaosBlockYaw.glUniform1f((float) (player.getYRot() * MathHelper.torad));
        DEShaders.chaosBlockPitch.glUniform1f((float) -(player.getXRot() * MathHelper.torad));
        ccrs.bind(CHAOS_CRYSTAL_INNER, getter);
        model.render(ccrs, mat);

        ccrs.baseColour = 0xFFFFFFF0;
        ccrs.bind(CHAOS_CRYSTAL, getter);
        model.render(ccrs, mat);

        if (!te.guardianDefeated.get()) {
            DEShaders.shieldBarMode.glUniform1i(0);
            DEShaders.shieldActivation.glUniform1f(1F);
            DEShaders.shieldColour.glUniform4f(1F, 0F, 0F, 1F);
            ccrs.bind(CHAOS_CRYSTAL_SHIELD, getter);
            model.render(ccrs, mat);
        }
    }

    @Override
    public AABB getRenderBoundingBox(TileChaosCrystal blockEntity) {
        return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity).inflate(3);
    }
}
