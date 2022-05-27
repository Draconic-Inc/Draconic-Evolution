package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.colour.Colour;
import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.model.OBJParser;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.client.render.RenderUtils;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalDirectIO;
import com.brandon3055.draconicevolution.client.DEShaders;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

/**
 * Created by brandon3055 on 6/11/2016.
 */
public class RenderTileEnergyCrystal implements BlockEntityRenderer<TileCrystalBase> {

    public static float[][] COLOURS = {{0.0F, 0.2F, 0.3F}, {0.47F, 0.0F, 0.58F}, {1.0F, 0.4F, 0.1F}};

    private static final RenderType fallBackType = RenderType.entityTranslucent(new ResourceLocation(DraconicEvolution.MODID, "textures/models/crystal_no_shader.png"));
    private static final RenderType fallBackOverlayType = RenderType.entityTranslucent(new ResourceLocation(DraconicEvolution.MODID, "textures/models/crystal_base.png"));
    private static final RenderType crystalBaseType = RenderType.entitySolid(new ResourceLocation(DraconicEvolution.MODID, "textures/models/crystal_base.png"));

    public static RenderType crystalType = RenderType.create("crystal_type", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, RenderType.CompositeState.builder()
            .setShaderState(new RenderStateShard.ShaderStateShard(() -> DEShaders.energyCrystalShader))
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .createCompositeState(false));

    private final CCModel crystalFull;
    private final CCModel crystalHalf;
    private final CCModel crystalBase;

    public RenderTileEnergyCrystal(BlockEntityRendererProvider.Context context) {
        Map<String, CCModel> map = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/block/crystal.obj")).quads().ignoreMtl().parse();
        crystalFull = CCModel.combine(map.values());
        map = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/block/crystal_half.obj")).quads().ignoreMtl().parse();
        crystalHalf = map.get("Crystal");
        crystalBase = map.get("Base");
    }

    @Override
    public void render(TileCrystalBase te, float partialTicks, PoseStack mStack, MultiBufferSource getter, int packedLight, int packedOverlay) {
        int tier = te.getTier();

        Matrix4 mat = new Matrix4(mStack);
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = 240;//packedLight;
        ccrs.overlay = packedOverlay;

        Player player = Minecraft.getInstance().player;
        BlockPos pos = te.getBlockPos();
        double x = player.getX() - (pos.getX() + 0.5);
        double y = player.getY() - (pos.getY() + 0.5);
        double z = player.getZ() - (pos.getZ() + 0.5);
        double mm = MathHelper.clip((((x * x) + (y * y) + (z * z) - 5) / 4096), 0, 1);
//        float xrot = (float) Math.atan2(x + 0.5, z + 0.5);
//        float dist = (float) Utils.getDistanceAtoB(Vec3D.getCenter(pos).x, Vec3D.getCenter(pos).z, Minecraft.getInstance().player.getX(), Minecraft.getInstance().player.getZ());
//        float yrot = (float) net.minecraft.util.Mth.atan2(dist, y + 0.5);
        ccrs.baseColour = 0xFFFFFFFF;
        DEShaders.energyCrystalMipmap.glUniform1f((float) mm);
        DEShaders.energyCrystalColour.glUniform3f(COLOURS[tier][0], COLOURS[tier][1], COLOURS[tier][2]);
//        DEShaders.energyCrystalAngle.glUniform2f(xrot / -3.125F, yrot / 3.125F);

        if (te instanceof TileCrystalDirectIO) {
            ccrs.bind(crystalBaseType, getter);
            mat.translate(0.5, 1, 0.5);
            mat.scale(-0.5);
            mat.apply(Rotation.sideOrientation(((TileCrystalDirectIO) te).facing.get().getOpposite().get3DDataValue(), 0).at(new Vector3(0, 1, 0)));
            crystalBase.render(ccrs, mat);
            mat.rotate((ClientEventHandler.elapsedTicks + partialTicks) / 400F, new Vector3(0, 1, 0));
            if (mm < 1) {
                ccrs.bind(crystalType, getter);
                crystalHalf.render(ccrs, mat);
            } else {
                ccrs.baseColour = Colour.packRGBA(r[tier], g[tier], b[tier], 1F);
                ccrs.bind(fallBackType, getter);
                crystalHalf.render(ccrs, mat);
                ccrs.baseColour = -1;
            }
        } else {
            mat.translate(Vector3.CENTER);
            mat.rotate(180 * MathHelper.torad, new Vector3(1, 0, 0));
            mat.scale(-0.5);
            mat.rotate((ClientEventHandler.elapsedTicks + partialTicks) / 400F, new Vector3(0, 1, 0));
            if (mm < 1) {
                ccrs.bind(crystalType, getter);
                crystalFull.render(ccrs, mat);
            } else {
                ccrs.baseColour = Colour.packRGBA(r[tier], g[tier], b[tier], 1F);
                ccrs.bind(fallBackType, getter);
                crystalFull.render(ccrs, mat);
                ccrs.baseColour = -1;
            }
        }

        RenderUtils.endBatch(getter);
    }

    private static float[] r = {0.0F, 0.55F, 1.0F};
    private static float[] g = {0.35F, 0.3F, 0.572F};
    private static float[] b = {0.65F, 0.9F, 0.172F};
}
