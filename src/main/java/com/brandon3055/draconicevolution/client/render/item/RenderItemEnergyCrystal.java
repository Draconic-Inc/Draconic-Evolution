package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCOBJParser;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.render.shader.ShaderProgram;
import codechicken.lib.render.state.GlStateManagerHelper;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import com.brandon3055.draconicevolution.blocks.energynet.EnergyCrystal.CrystalType;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.render.shaders.DEShaders;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.brandon3055.draconicevolution.utils.DETextures;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by brandon3055 on 21/11/2016.
 */
public class RenderItemEnergyCrystal implements IItemRenderer, IPerspectiveAwareModel {
    private CCModel crystalFull;
    private CCModel crystalHalf;
    private CCModel crystalBase;

    public RenderItemEnergyCrystal() {
        Map<String, CCModel> map = CCOBJParser.parseObjModels(ResourceHelperDE.getResource("models/crystal.obj"));
        crystalFull = CCModel.combine(map.values());
        map = CCOBJParser.parseObjModels(ResourceHelperDE.getResource("models/crystal_half.obj"));
        crystalHalf = map.get("Crystal");
        crystalBase = map.get("Base");
    }

    //region Unused
    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return new ArrayList<>();
    }

    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return true;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return null;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }

    //endregion

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        return MapWrapper.handlePerspective(this, TransformUtils.DEFAULT_BLOCK.getTransforms(), cameraTransformType);
    }

    @Override
    public void renderItem(ItemStack item) {
        CrystalType type = CrystalType.fromMeta(item.getItemDamage());
        int tier = CrystalType.getTier(item.getItemDamage());

        GlStateManager.pushMatrix();
        GlStateManagerHelper.pushState();
        GlStateManager.disableLighting();
        CCRenderState ccrs = CCRenderState.instance();
        Matrix4 mat = RenderUtils.getMatrix(new Vector3(0.5, type == CrystalType.CRYSTAL_IO ? 0 : 0.5, 0.5), new Rotation(0, 0, 0, 0), 1);
        ResourceHelperDE.bindTexture(DETextures.ENERGY_CRYSTAL_BASE);
//        mat.apply(Rotation.sideOrientation(EnumFacing.NORTH.getOpposite().getIndex(), 0).at(new Vector3(0, 1, 0)));

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


        GlStateManagerHelper.popState();
        GlStateManager.popMatrix();
    }

    private static float[] r = {0.0F, 0.47F, 1.0F};
    private static float[] g = {0.2F, 0.0F, 0.4F};
    private static float[] b = {0.3F, 0.58F, 0.1F};

    public void bindShader(float partialTicks, int tier) {
        if (DEShaders.useShaders()) {
            DEShaders.eCrystalOp.setType(tier);
            DEShaders.eCrystalOp.setAnimation((ClientEventHandler.elapsedTicks + partialTicks) / 50);
            DEShaders.eCrystalOp.setMipmap(0);
            DEShaders.energyCrystal.freeBindShader();
        }
        else {
            ResourceHelperDE.bindTexture(DETextures.ENERGY_CRYSTAL_NO_SHADER);
            GlStateManager.color(r[tier], g[tier], b[tier]);
        }
    }

    private void releaseShader() {
        if (DEShaders.useShaders()) {
            ShaderProgram.unbindShader();
        }
    }

}
