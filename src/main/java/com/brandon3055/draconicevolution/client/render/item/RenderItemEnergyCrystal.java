package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.render.*;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.render.shader.ShaderProgram;
import codechicken.lib.render.state.GlStateTracker;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import com.brandon3055.draconicevolution.blocks.energynet.EnergyCrystal.CrystalType;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.render.shaders.DEShaders;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.brandon3055.draconicevolution.utils.DETextures;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel.MapWrapper;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Matrix4f;
import java.util.Map;

/**
 * Created by brandon3055 on 21/11/2016.
 */
public class RenderItemEnergyCrystal implements IItemRenderer {
    private CCModel crystalFull;
    private CCModel crystalHalf;
    private CCModel crystalBase;

    private static ShaderProgram shaderProgram;

    public RenderItemEnergyCrystal() {
        Map<String, CCModel> map = OBJParser.parseModels(ResourceHelperDE.getResource("models/crystal.obj"));
        crystalFull = CCModel.combine(map.values());
        map = OBJParser.parseModels(ResourceHelperDE.getResource("models/crystal_half.obj"));
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
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemStack stack, ItemCameraTransforms.TransformType cameraTransformType) {
        return MapWrapper.handlePerspective(this, TransformUtils.DEFAULT_BLOCK.getTransforms(), cameraTransformType);
    }

    @Override
    public void renderItem(ItemStack item, ItemCameraTransforms.TransformType transformType) {
        CrystalType type = CrystalType.fromMeta(item.getItemDamage());
        int tier = CrystalType.getTier(item.getItemDamage());

        GlStateManager.pushMatrix();
        GlStateTracker.pushState();
        GlStateManager.disableLighting();
        CCRenderState ccrs = CCRenderState.instance();
        Matrix4 mat = RenderUtils.getMatrix(new Vector3(0.5, type == CrystalType.CRYSTAL_IO ? 0 : 0.5, 0.5), new Rotation(0, 0, 0, 0), 1);
        ResourceHelperDE.bindTexture(DETextures.ENERGY_CRYSTAL_BASE);

        if (type == CrystalType.CRYSTAL_IO) {
            //Render Base
            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
            crystalBase.render(ccrs, mat);
            ccrs.draw();

            //Apply Crystal Rotation
            mat.apply(new Rotation((ClientEventHandler.elapsedTicks) / 400F, 0, 1, 0));

            //Render Crystal
            ResourceHelperDE.bindTexture(DETextures.REACTOR_CORE);
            bindShader(0, tier);
            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
            crystalHalf.render(ccrs, mat);
            ccrs.draw();
            releaseShader();
        }
        else {
            //Render Crystal
            bindShader(0, tier);
            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
            crystalFull.render(ccrs, mat);
            ccrs.draw();
            releaseShader();
        }


        GlStateTracker.popState();
        GlStateManager.popMatrix();
    }

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
            ResourceHelperDE.bindTexture(DETextures.ENERGY_CRYSTAL_NO_SHADER);
            GlStateManager.color(r[tier], g[tier], b[tier]);
        }
    }

    private void releaseShader() {
        if (DEShaders.useShaders()) {
            shaderProgram.releaseShader();
        }
    }

}
