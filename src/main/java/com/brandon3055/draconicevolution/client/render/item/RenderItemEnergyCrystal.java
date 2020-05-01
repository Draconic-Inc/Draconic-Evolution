package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.colour.Colour;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.render.shader.ShaderProgram;
import codechicken.lib.util.SneakyUtils;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.TechLevel;
import com.brandon3055.draconicevolution.blocks.energynet.EnergyCrystal.CrystalType;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.render.shaders.DEShaders;
import com.brandon3055.draconicevolution.utils.ResourceHelperDE;
import com.brandon3055.draconicevolution.client.DETextures;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Map;

/**
 * Created by brandon3055 on 21/11/2016.
 */
public class RenderItemEnergyCrystal implements IItemRenderer {
    public static final RenderType fallBackType = RenderType.makeType("fall_back_type", DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP, GL11.GL_QUADS, 256, RenderType.State.getBuilder()
            .texture(new RenderState.TextureState(new ResourceLocation(DraconicEvolution.MODID, "textures/models/crystal_no_shader.png"), false, false))
            .texturing(new RenderState.TexturingState("lighting", RenderSystem::disableLighting, SneakyUtils.none()))
            .cull(RenderState.CULL_DISABLED)
            .build(false)
    );
    public static final RenderType crystalBaseType = RenderType.getEntitySolid(new ResourceLocation(DraconicEvolution.MODID, "textures/models/crystal_base.png"));
    public static final RenderType fallBackOverlayType = RenderType.getEntityTranslucent(new ResourceLocation(DraconicEvolution.MODID, "textures/models/crystal_base.png"));

    private final CrystalType type;
    private final TechLevel techLevel;
    private final CCModel crystalFull;
    private final CCModel crystalHalf;
    private final CCModel crystalBase;

    private static ShaderProgram shaderProgram;

    public RenderItemEnergyCrystal(CrystalType type, TechLevel techLevel) {
        this.type = type;
        this.techLevel = techLevel;
        Map<String, CCModel> map = OBJParser.parseModels(new ResourceLocation(DraconicEvolution.MODID, "models/block/crystal.obj"), GL11.GL_QUADS, null);
        crystalFull = CCModel.combine(map.values());
        map = OBJParser.parseModels(new ResourceLocation(DraconicEvolution.MODID, "models/block/crystal_half.obj"), GL11.GL_QUADS, null);
        crystalHalf = map.get("Crystal");
        crystalBase = map.get("Base");
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
        int tier = techLevel.index;

        Matrix4 mat = new Matrix4(mStack);
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;

        mat.translate(0.5, type == CrystalType.CRYSTAL_IO ? 0 : 0.5, 0.5);

        if (type == CrystalType.CRYSTAL_IO) {
            ccrs.bind(crystalBaseType, getter);
            crystalBase.render(ccrs, mat);

            //Apply Crystal Rotation
            mat.apply(new Rotation((ClientEventHandler.elapsedTicks) / 400F, 0, 1, 0));

            //Render Crystal

//            bindShader(0, techLevel.index);
            ccrs.baseColour = Colour.packRGBA(r[tier], g[tier], b[tier], 1F);
            ccrs.bind(fallBackType, getter);
            crystalHalf.render(ccrs, mat);

            ccrs.bind(fallBackOverlayType, getter);
            crystalHalf.render(ccrs, mat);

//            releaseShader();
        }
        else {
            //Render Crystal
//            bindShader(0, techLevel.index);
            ccrs.baseColour = Colour.packRGBA(r[tier], g[tier], b[tier], 1F);
            ccrs.bind(fallBackType, getter);
            crystalFull.render(ccrs, mat);

            ccrs.bind(fallBackOverlayType, getter);
            crystalFull.render(ccrs, mat);

//            releaseShader();
        }

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
    public void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType) {
        RenderSystem.pushMatrix();
//        GlStateTracker.pushState();
//        RenderSystem.disableLighting();
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
//        ResourceHelperDE.bindTexture(DETextures.ENERGY_CRYSTAL_BASE);

//        if (type == CrystalType.CRYSTAL_IO) {
//            //Render Base
//            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
//            crystalBase.render(ccrs, mat);
//            ccrs.draw();
//
//            //Apply Crystal Rotation
//            mat.apply(new Rotation((ClientEventHandler.elapsedTicks) / 400F, 0, 1, 0));
//
//            //Render Crystal
//            ResourceHelperDE.bindTexture(DETextures.REACTOR_CORE);
//            bindShader(0, techLevel.index);
//            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
//            crystalHalf.render(ccrs, mat);
//            ccrs.draw();
//            releaseShader();
//        }
//        else {
//            //Render Crystal
//            bindShader(0, techLevel.index);
//            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
//            crystalFull.render(ccrs, mat);
//            ccrs.draw();
//            releaseShader();
//        }


//        GlStateTracker.popState();
        RenderSystem.popMatrix();
    }

//    @Override
//    public IModelState getTransforms() {
//        return TransformUtils.DEFAULT_BLOCK;
//    }

    private static float[] r = {0.0F, 0.47F, 1.0F};
    private static float[] g = {0.2F, 0.0F, 0.4F};
    private static float[] b = {0.3F, 0.58F, 0.1F};

    public void bindShader(float partialTicks, int tier) {
        if (DEShaders.useShaders()) {
            if (shaderProgram == null) {
                shaderProgram = new ShaderProgram();
                shaderProgram.attachShader(DEShaders.energyCrystal_V);
                shaderProgram.attachShader(DEShaders.energyCrystal_F);
            }

            shaderProgram.useShader(cache -> {
                cache.glUniform1F("time", (ClientEventHandler.elapsedTicks + partialTicks) / 50);
                cache.glUniform1F("mipmap", (float) 0);
                cache.glUniform1I("type", tier);
                cache.glUniform2F("angle", 0, 0);
            });
        }
        else {
//            ResourceHelperDE.bindTexture(DETextures.ENERGY_CRYSTAL_NO_SHADER);
//            RenderSystem.color3f(r[tier], g[tier], b[tier]);
        }
    }

    private void releaseShader() {
        if (DEShaders.useShaders()) {
            shaderProgram.releaseShader();
        }
    }

}
