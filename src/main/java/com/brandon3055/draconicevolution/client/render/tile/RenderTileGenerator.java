package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.render.lighting.LightModel;
import codechicken.lib.vec.*;
import codechicken.lib.vec.uv.IconTransformation;
import com.brandon3055.draconicevolution.blocks.machines.Generator;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGenerator;
import com.brandon3055.draconicevolution.utils.ResourceHelperDE;
import com.brandon3055.draconicevolution.client.DETextures;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import org.lwjgl.opengl.GL11;

import java.util.Map;

/**
 * Created by brandon3055 on 1/3/20.
 */
public class RenderTileGenerator extends TileEntityRenderer<TileGenerator> {

    private static RenderType modelType = RenderType.getSolid();
    private final CCModel fanModel;

    public RenderTileGenerator(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
        Map<String, CCModel> map = OBJParser.parseModels(ResourceHelperDE.getResource("models/block/generator/generator_fan.obj"), GL11.GL_QUADS, null);
        fanModel = CCModel.combine(map.values()).backfacedCopy();
    }

    @Override
    public void render(TileGenerator tile, float partialTicks, MatrixStack mStack, IRenderTypeBuffer getter, int packedLight, int packedOverlay) {
        IconTransformation icon = new IconTransformation(DETextures.GENERATOR);
        if (icon.icon == null) return;
        Matrix4 mat = new Matrix4(mStack);
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;

        ccrs.bind(modelType, getter);
        mat.translate(Vector3.CENTER);
        mat.apply(new Rotation(tile.getBlockState().get(Generator.FACING).getOpposite().getHorizontalAngle() * -MathHelper.torad, 0, 1, 0));
        mat.apply(new Scale(0.0625));
        mat.apply(new Rotation((tile.rotation + (tile.rotationSpeed * partialTicks)), 1, 0, 0).at(new Vector3(0, -1.5, -4.5)));

        fanModel.render(ccrs, LightModel.standardLightModel, icon, mat);
    }
}
