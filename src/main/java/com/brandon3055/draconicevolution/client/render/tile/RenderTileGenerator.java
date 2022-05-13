package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.lighting.LightModel;
import codechicken.lib.render.model.OBJParser;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Vector3;
import codechicken.lib.vec.uv.IconTransformation;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.machines.Generator;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGenerator;
import com.brandon3055.draconicevolution.client.DETextures;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/**
 * Created by brandon3055 on 1/3/20.
 */
public class RenderTileGenerator implements BlockEntityRenderer<TileGenerator> {

    private static RenderType modelType = RenderType.solid();
    private final CCModel fanModel;

    public RenderTileGenerator(BlockEntityRendererProvider.Context context) {
        Map<String, CCModel> map = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/block/generator/generator_fan.obj")).quads().ignoreMtl().parse();
        fanModel = CCModel.combine(map.values()).backfacedCopy();
    }

    @Override
    public void render(TileGenerator tile, float partialTicks, PoseStack mStack, MultiBufferSource getter, int packedLight, int packedOverlay) {
        IconTransformation icon = new IconTransformation(DETextures.GENERATOR);
        if (icon.icon == null) return;
        Matrix4 mat = new Matrix4(mStack);
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;

        ccrs.bind(modelType, getter);
        mat.translate(Vector3.CENTER);
        mat.apply(new Rotation(tile.getBlockState().getValue(Generator.FACING).getOpposite().toYRot() * -MathHelper.torad, 0, 1, 0));
        mat.apply(new Scale(0.0625));
        mat.apply(new Rotation((tile.rotation + (tile.rotationSpeed * partialTicks)), 1, 0, 0).at(new Vector3(0, -1.5, -4.5)));

        fanModel.render(ccrs, LightModel.standardLightModel, icon, mat);
    }
}
