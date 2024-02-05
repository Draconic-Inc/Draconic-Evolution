package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.colour.Colour;
import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.model.OBJParser;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.client.shader.BCShaders;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyPylon;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/**
 * Created by brandon3055 on 20/05/2016.
 */
public class RenderTileEnergyPylon implements BlockEntityRenderer<TileEnergyPylon> {

    private static RenderType modelType = RenderType.entitySolid(new ResourceLocation(DraconicEvolution.MODID, "textures/block/pylon_sphere_texture.png"));

    private static RenderType shellType = RenderType.create("pylon_sphere", DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256, RenderType.CompositeState.builder()
            .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(DraconicEvolution.MODID, "textures/block/pylon_sphere_texture.png"), false, false))
            .setShaderState(new RenderStateShard.ShaderStateShard(() -> BCShaders.posColourTexAlpha0))
            .setTransparencyState(RenderStateShard.LIGHTNING_TRANSPARENCY)
            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
            .createCompositeState(false)
    );
    private final CCModel model;

    public RenderTileEnergyPylon(BlockEntityRendererProvider.Context context) {
        Map<String, CCModel> map = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/pylon_sphere.obj")).quads().ignoreMtl().parse();
        model = CCModel.combine(map.values());
        model.apply(new Scale(-0.35, -0.35, -0.35));
        model.computeNormals();
    }

    @Override
    public void render(TileEnergyPylon te, float partialTicks, PoseStack mStack, MultiBufferSource getter, int packedLight, int packedOverlay) {
        if (!te.structureValid.get()) return;

        Matrix4 mat = new Matrix4(mStack);
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = 240;
        ccrs.overlay = packedOverlay;

        ccrs.baseColour = 0x005efaFF;
        if (te.colour.notNull()) {
            ccrs.baseColour = te.colour.get().rgba();
        }

        shellType = RenderType.create("pylon_sphere", DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256, RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(DraconicEvolution.MODID, "textures/block/pylon_sphere_texture.png"), false, false))
                .setShaderState(new RenderStateShard.ShaderStateShard(() -> BCShaders.posColourTexAlpha0))
                .setTransparencyState(RenderStateShard.LIGHTNING_TRANSPARENCY)
                .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                .createCompositeState(false)
        );

        ccrs.bind(modelType, getter);
        mat.translate(te.direction.get().getNormal());
        mat.translate(0.5, 0.5, 0.5);
        mat.rotate(((ClientEventHandler.elapsedTicks + partialTicks) * 2F) * MathHelper.torad, new Vector3(0, 1, 0.5).normalize());
        model.render(ccrs, mat);

        float f = MathHelper.clip(((ClientEventHandler.elapsedTicks + partialTicks) % 35F) / 30F, 0, 1);
        if (te.ioMode.get().canExtract()) {
            f = 1F - f;
        }

        ccrs.baseColour = Colour.packRGBA(1F - f, 0xF5 / 255F, 0xfa / 255F, 1F - f);
        if (te.colour.notNull()) {
            ccrs.baseColour = Colour.packRGBA(te.colour.get().rF() * (1F - f), te.colour.get().gF(), te.colour.get().bF(), 1F - (f * f));
        }

        ccrs.bind(shellType, getter);
        mat.scale(1 + f);
        model.render(ccrs, mat);
    }
}
