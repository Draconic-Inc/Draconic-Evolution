package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.model.OBJParser;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCoreStabilizer;
import com.brandon3055.draconicevolution.client.DEShaders;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Quaternion;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/**
 * Created by brandon3055 on 19/4/2016.
 */
public class RenderEnergyCoreStabilizer implements BlockEntityRenderer<TileEnergyCoreStabilizer> {

    private static final RenderType MODEL_TYPE = RenderType.entitySolid(new ResourceLocation(DraconicEvolution.MODID, "textures/block/energy_core/stabilizer_large.png"));

    private static final RenderType MODEL_TYPE_ACTIVE = RenderType.create("stab_type_a", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder()
            .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(DraconicEvolution.MODID, "textures/block/energy_core/stabilizer_large.png"), false, false))
            .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorTexLightmapShader))
            .setLightmapState(RenderStateShard.LIGHTMAP)
            .createCompositeState(false)
    );

    private CCModel model;

    public RenderEnergyCoreStabilizer(BlockEntityRendererProvider.Context context) {
        Map<String, CCModel> map = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/block/energy_core/stabilizer_large.obj")).quads().ignoreMtl().parse();
        model = CCModel.combine(map.values()).backfacedCopy();
    }

    @Override
    public void render(TileEnergyCoreStabilizer tile, float partialTicks, PoseStack poseStack, MultiBufferSource getter, int packedLight, int packedOverlay) {
        if (!tile.isValidMultiBlock.get()) return;

        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = 240;
        ccrs.overlay = packedOverlay;

        boolean coreActive = tile.isCoreActive.get();
        Direction facing;
        if (coreActive) {
            facing = tile.coreDirection.get();
        } else {
            facing = Direction.get(Direction.AxisDirection.POSITIVE, tile.multiBlockAxis.get());
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        if (facing.getAxis() == Direction.Axis.X || facing.getAxis() == Direction.Axis.Y) {
            poseStack.mulPose(new Quaternion(facing.getStepY() * 90, facing.getStepX() * -90, 0, true));
        } else if (facing == Direction.SOUTH) {
            poseStack.mulPose(new Quaternion(0, 180F, 0, true));
        }
        poseStack.mulPose(new Quaternion(0, 0F, tile.rotation + (coreActive ? partialTicks : 0), true));
        poseStack.translate(0, -1.5, 0);
        ccrs.bind(coreActive ? MODEL_TYPE_ACTIVE : MODEL_TYPE, getter, poseStack);
        model.render(ccrs);
        poseStack.popPose();
    }
}
