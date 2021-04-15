package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.lighting.LightModel;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.vec.*;
import codechicken.lib.vec.uv.IconTransformation;
import codechicken.lib.vec.uv.UV;
import codechicken.lib.vec.uv.UVTransformation;
import com.brandon3055.draconicevolution.blocks.Potentiometer;
import com.brandon3055.draconicevolution.blocks.tileentity.TilePotentiometer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;

public class RenderTilePotentiometer extends TileEntityRenderer<TilePotentiometer> {

    private static CCModel model;


    public RenderTilePotentiometer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
        double pxl = 1D / 16D;
        double ls = pxl * 2.5;      //Left Side
        double rs = pxl * 1.5;      //Right Side
        double fe = pxl * 7;        //Front Edge
        double be = pxl * 0;        //Back Edge
        double bh = pxl * 3;        //Back Height
        double fh = pxl * 0.5;      //Front Height
        int i = 24;

        model = CCModel.quadModel(40);
        model.generateBlock(0, new Cuboid6(0, 0, 0, pxl * 4, pxl, pxl * 4));

        //Top
        model.verts[i++] = new Vertex5(new Vector3(ls, bh, be), new UV(ls, be));
        model.verts[i++] = new Vertex5(new Vector3(ls, fh, fe), new UV(ls, fe));
        model.verts[i++] = new Vertex5(new Vector3(rs, fh, fe), new UV(rs, fe));
        model.verts[i++] = new Vertex5(new Vector3(rs, bh, be), new UV(rs, be));

        //Left
        model.verts[i++] = new Vertex5(new Vector3(ls, 0, fe), new UV(0, fe));
        model.verts[i++] = new Vertex5(new Vector3(ls, 0, be), new UV(0, be));
        model.verts[i++] = new Vertex5(new Vector3(ls, bh, be), new UV(bh, be));
        model.verts[i++] = new Vertex5(new Vector3(ls, fh, fe), new UV(fh, fe));

        //Right
        model.verts[i++] = new Vertex5(new Vector3(rs, 0, fe), new UV(0, fe));
        model.verts[i++] = new Vertex5(new Vector3(rs, 0, be), new UV(0, be));
        model.verts[i++] = new Vertex5(new Vector3(rs, bh, be), new UV(bh, be));
        model.verts[i++] = new Vertex5(new Vector3(rs, fh, fe), new UV(fh, fe));

        //Back
        model.verts[i++] = new Vertex5(new Vector3(ls, pxl, be), new UV(ls, 0));
        model.verts[i++] = new Vertex5(new Vector3(rs, pxl, be), new UV(rs, 0));
        model.verts[i++] = new Vertex5(new Vector3(rs, bh, be), new UV(rs, bh));
        model.verts[i++] = new Vertex5(new Vector3(ls, bh, be), new UV(ls, bh));

        model.computeNormals();
        model.computeLightCoords();
        model = model.twoFacedCopy();
    }

    @Override
    public void render(TilePotentiometer te, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        TextureAtlasSprite stoneTex = TextureUtils.getBlockTexture("oak_planks");
        UVTransformation iconTransform = new IconTransformation(stoneTex);
        CCRenderState state = CCRenderState.instance();
        state.reset();
        state.bind(RenderType.solid(), buffer);
        state.brightness = combinedLightIn;
        state.overlay = combinedOverlayIn;
        double pxl = 1D / 16D;

        Matrix4 mat = new Matrix4(matrixStackIn);
        mat.apply(Rotation.sideOrientation(te.getBlockState().getValue(Potentiometer.FACING).getOpposite().get3DDataValue(), 0).at(Vector3.CENTER));
        mat.apply(new Translation(6 * pxl, pxl, 6 * pxl));
        mat.apply(new Rotation(te.power.get() * 22.5D * -MathHelper.torad, 0, 1, 0).at(new Vector3(pxl * 2, 0, pxl * 2)));

        model.render(state, LightModel.standardLightModel, iconTransform, mat);
    }
}
