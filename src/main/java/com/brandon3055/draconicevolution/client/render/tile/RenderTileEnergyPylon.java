package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.colour.Colour;
import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.util.SneakyUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Vector3;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyPylon;

import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Map;

/**
 * Created by brandon3055 on 20/05/2016.
 */
public class RenderTileEnergyPylon extends TileEntityRenderer<TileEnergyPylon> {

    private static RenderType modelType = RenderType.getEntitySolid(new ResourceLocation(DraconicEvolution.MODID, "textures/models/block/pylon_sphere_texture.png"));
    private static RenderType shelType = RenderType.makeType("pylon_sphere22", DefaultVertexFormats.POSITION_COLOR_TEX, GL11.GL_QUADS, 256, RenderType.State.getBuilder()
            .texture(new RenderState.TextureState(new ResourceLocation(DraconicEvolution.MODID, "textures/models/block/pylon_sphere_texture.png"), false, false))
            .transparency(RenderState.LIGHTNING_TRANSPARENCY)
            .writeMask(RenderState.COLOR_WRITE)
            .texturing(new RenderState.TexturingState("lighting", RenderSystem::disableLighting, SneakyUtils.none()))
            .build(false)
    );
    private final CCModel model;


    public RenderTileEnergyPylon(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
        Map<String, CCModel> map = OBJParser.parseModels(new ResourceLocation(DraconicEvolution.MODID, "models/pylon_sphere.obj"), GL11.GL_QUADS, null); //Note dont generate the model evey render frame move this to constructor
        model = CCModel.combine(map.values());
        model.apply(new Scale(-0.35, -0.35, -0.35));
        model.computeNormals();
    }

    @Override
    public void render(TileEnergyPylon te, float partialTicks, MatrixStack mStack, IRenderTypeBuffer getter, int packedLight, int packedOverlay) {
        if (!te.structureValid.get()) return;

        Matrix4 mat = new Matrix4(mStack);
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = 240;
        ccrs.overlay = packedOverlay;

        ccrs.bind(modelType, getter);
        mat.translate(0.5, (te.sphereOnTop.get() ? 1.5 : -0.5), 0.5);
        mat.rotate(((ClientEventHandler.elapsedTicks + partialTicks) * 2F) * MathHelper.torad, new Vector3(0, 1, 0.5).normalize());
        model.render(ccrs, mat);

        float f = ((ClientEventHandler.elapsedTicks + partialTicks) % 30F) / 30F;
        if (te.isOutputMode.get()) {
            f = 1F - f;
        }

        ccrs.baseColour = Colour.packRGBA(1F - f, 1F, 1F, 1F - f);
        ccrs.bind(shelType, getter);
        mat.scale(1 + f);
        model.render(ccrs, mat);
    }
}
