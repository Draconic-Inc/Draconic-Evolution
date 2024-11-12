package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.math.MathHelper;
import codechicken.lib.model.PerspectiveModelState;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.TransformingVertexConsumer;
import codechicken.lib.render.model.OBJParser;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.client.DEShaders;
import com.brandon3055.draconicevolution.client.shader.ToolShader;
import com.brandon3055.draconicevolution.items.equipment.ModularBow;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.entity.TippableArrowRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Created by brandon3055 on 22/5/20.
 */
public class RenderModularBow extends ToolRenderBase {

    private static final float[][] STRING_BASE_COLORS = {
            { 0.1F, 0.5F, 0.8F, 1F },
            { 0.55F, 0.25F, 0.65F, 1F },
            { 0.7F, 0.4F, 0.2F, 1F },
            { 0.55F, 0.2F, 0.1F, 0.2F },
    };
    private static final RenderType bowStringType = RenderType.create("shaderStringType", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, RenderType.CompositeState.builder()
            .setShaderState(new RenderStateShard.ShaderStateShard(DEShaders.BOW_STRING_SHADER::getShaderInstance))
            .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(DraconicEvolution.MODID, "textures/item/equipment/bow_string.png"), true, false))
            .setTransparencyState(RenderStateShard.LIGHTNING_TRANSPARENCY)
            .setCullState(RenderStateShard.NO_CULL)
            .setWriteMaskState(RenderStateShard.WriteMaskStateShard.COLOR_WRITE)
            .createCompositeState(false)
    );

    private final ToolPart basePart;
    private final ToolPart materialPart;
    private final ToolPart gemPart;

    @Nullable
    protected LivingEntity entity;
    @Nullable
    protected ClientLevel world;

    public RenderModularBow(TechLevel techLevel) {
        super(techLevel, "bow");
        Map<String, CCModel> model = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/item/equipment/bow.obj")).ignoreMtl().parse();
        basePart = basePart(model.get("bow_handle").backfacedCopy());
        materialPart = materialPart(model.get("bow_arm").backfacedCopy());
        gemPart = gemPart(model.get("bow_gem").backfacedCopy());
    }

    private final ItemOverrides overrideList = new ItemOverrides() {
        @Override
        public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int i) {
            RenderModularBow.this.entity = entity;
            RenderModularBow.this.world = world == null ? entity == null ? null : (ClientLevel) entity.level() : null;
            return originalModel;
        }
    };

    @Override
    public ItemOverrides getOverrides() {
        return overrideList;
    }

    @Override
    public void renderTool(CCRenderState ccrs, ItemStack stack, ItemDisplayContext context, Matrix4 mat, MultiBufferSource buffers, boolean gui) {
        transform(mat, 0.46, 0.54, 0.5, gui ? 0.9 : 1.125);
        double drawAngle = getDrawAngle(stack, Minecraft.getInstance().getDeltaFrameTime());

        basePart.render(context, buffers, mat);

        Matrix4 bottomMat = mat.copy();
        bottomMat.rotate(MathHelper.torad * 180, Vector3.Y_POS);

        materialPart.render(context, buffers, mat);
        materialPart.render(context, buffers, bottomMat);

        boolean hasPower = isCreative(entity) || (stack.getCapability(DECapabilities.Host.ITEM) != null && ModularBow.calculateShotEnergy(stack) <= EnergyUtils.getEnergyStored(stack));
        drawStrings(ccrs, context, mat, bottomMat, buffers, drawAngle, hasPower);
    }

    private void drawStrings(CCRenderState ccrs, ItemDisplayContext context, Matrix4 topMat, Matrix4 bottomMat, MultiBufferSource buffers, double drawAngle, boolean isCharged) {
        glUniformStringBaseColor(DEShaders.BOW_STRING_SHADER);
        VertexConsumer builder = new TransformingVertexConsumer(buffers.getBuffer(bowStringType), topMat);

        double crystalX = 12.508D * 0.01D;
        double crystalY = 67.844D * 0.01D;
        double A = 180 - 90 - drawAngle;
        double c = crystalY * (Math.sin(drawAngle * MathHelper.torad) / Math.sin(A * MathHelper.torad));
        if (isCharged) {
            renderBeam(builder, new Vector3(0, -crystalX, crystalY), new Vector3(0, -(crystalX + c), 0), ccrs.brightness);
            renderBeam(builder, new Vector3(0, -crystalX, -crystalY), new Vector3(0, -(crystalX + c), 0), ccrs.brightness);
        }

        if (drawAngle > 0) {
            Matrix4 arrowMat = topMat.copy();
            arrowMat.translate(0.055, 0.325 - c, 0);
            arrowMat.rotate(90 * MathHelper.torad, Vector3.Z_POS);
            renderArrow(arrowMat, buffers, ccrs.brightness);
        }

        topMat = topMat.copy().apply(new Rotation(drawAngle * MathHelper.torad, 1, 0, 0).at(new Vector3(0, -crystalX, -crystalY)));
        bottomMat = bottomMat.copy().apply(new Rotation(drawAngle * MathHelper.torad, 1, 0, 0).at(new Vector3(0, -crystalX, -crystalY)));
        gemPart.render(context, buffers, topMat);
        gemPart.render(context, buffers, bottomMat);
    }

    private void renderBeam(VertexConsumer buffer, Vector3 source, Vector3 target, int packedLight) {
        double scale = 0.03;

        Vector3 dirVec = source.copy().subtract(target).normalize();
        Vector3 planeA = dirVec.copy().perpendicular().normalize();
        Vector3 planeB = dirVec.copy().crossProduct(planeA);
        Vector3 planeC = planeB.copy().rotate(45 * MathHelper.torad, dirVec).normalize();
        Vector3 planeD = planeB.copy().rotate(-45 * MathHelper.torad, dirVec).normalize();
        planeA.multiply(scale);
        planeB.multiply(scale);
        planeC.multiply(scale);
        planeD.multiply(scale);

        Vector3 p1 = source.copy().add(planeA);
        Vector3 p2 = target.copy().add(planeA);
        Vector3 p3 = source.copy().subtract(planeA);
        Vector3 p4 = target.copy().subtract(planeA);
        bufferShaderQuad(buffer, p1, p2, p3, p4, packedLight);

        p1 = source.copy().add(planeB);
        p2 = target.copy().add(planeB);
        p3 = source.copy().subtract(planeB);
        p4 = target.copy().subtract(planeB);
        bufferShaderQuad(buffer, p1, p2, p3, p4, packedLight);

        p1 = source.copy().add(planeC);
        p2 = target.copy().add(planeC);
        p3 = source.copy().subtract(planeC);
        p4 = target.copy().subtract(planeC);
        bufferShaderQuad(buffer, p1, p2, p3, p4, packedLight);

        p1 = source.copy().add(planeD);
        p2 = target.copy().add(planeD);
        p3 = source.copy().subtract(planeD);
        p4 = target.copy().subtract(planeD);
        bufferShaderQuad(buffer, p1, p2, p3, p4, packedLight);
    }

    private void bufferShaderQuad(VertexConsumer buffer, Vector3 p1, Vector3 p2, Vector3 p3, Vector3 p4, int packedLight) {
        Vector3 diff1 = p2.copy().subtract(p1);
        Vector3 diff2 = p4.copy().subtract(p1);
        Vector3 norm = diff1.crossProduct(diff2).normalize();
        bufferVertex(buffer, p1.x, p1.y, p1.z, 0.0F, 0F, (float) norm.x, (float) norm.y, (float) norm.z, packedLight);
        bufferVertex(buffer, p2.x, p2.y, p2.z, 0.0F, 1F, (float) norm.x, (float) norm.y, (float) norm.z, packedLight);
        bufferVertex(buffer, p4.x, p4.y, p4.z, 1F, 1F, (float) norm.x, (float) norm.y, (float) norm.z, packedLight);
        bufferVertex(buffer, p3.x, p3.y, p3.z, 1F, 0F, (float) norm.x, (float) norm.y, (float) norm.z, packedLight);
    }

    private void renderArrow(Matrix4 mat, MultiBufferSource getter, int packedLight) {
        mat.scale(0.05625F, 0.05625F, 0.05625F);
        VertexConsumer builder = new TransformingVertexConsumer(getter.getBuffer(RenderType.entityCutout(TippableArrowRenderer.NORMAL_ARROW_LOCATION)), mat);
        bufferVertex(builder, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, packedLight);
        bufferVertex(builder, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, packedLight);
        bufferVertex(builder, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, packedLight);
        bufferVertex(builder, -7, 2, -2, 0.0F, 0.3125F, -1, 0, 0, packedLight);
        bufferVertex(builder, -7, 2, -2, 0.0F, 0.15625F, 1, 0, 0, packedLight);
        bufferVertex(builder, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, packedLight);
        bufferVertex(builder, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, packedLight);
        bufferVertex(builder, -7, -2, -2, 0.0F, 0.3125F, 1, 0, 0, packedLight);

        for (int j = 0; j < 4; ++j) {
            mat.rotate(90 * MathHelper.torad, Vector3.X_POS);
            bufferVertex(builder, -8.5F, -2, 0, 0.0F, 0.0F, 0, 1, 0, packedLight);
            bufferVertex(builder, 8.5F, -2, 0, 0.5F, 0.0F, 0, 1, 0, packedLight);
            bufferVertex(builder, 8.5F, 2, 0, 0.5F, 0.15625F, 0, 1, 0, packedLight);
            bufferVertex(builder, -8.5F, 2, 0, 0.0F, 0.15625F, 0, 1, 0, packedLight);
        }
    }

    public void bufferVertex(VertexConsumer builder, double x, double y, double z, float u, float v, float normX, float normZ, float normY, int light) {
        builder.vertex(x, y, z).color(255, 255, 255, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normX, normY, normZ).endVertex();
    }

    @Override
    public @Nullable PerspectiveModelState getModelState() {
        return TransformUtils.DEFAULT_BOW;
    }

    private void glUniformStringBaseColor(ToolShader shader) {
        float[] color = STRING_BASE_COLORS[techLevel.index];
        shader.getBaseColorUniform().glUniform4f(color[0], color[1], color[2], color[3]);
    }

    private double getDrawAngle(ItemStack stack, float partialTicks) {
        if (entity != null && entity.getUseItem() == stack) {
            float maxCount = entity.getTicksUsingItem() - partialTicks;
            return Math.max(0, ModularBow.getPowerForTime((int) (maxCount), stack) * 45F);
        }
        return 0;
    }

    private boolean isCreative(LivingEntity entity) {
        return entity instanceof Player && ((Player) entity).getAbilities().instabuild;
    }

    public static float torad(double degrees) {
        return (float) (degrees * MathHelper.torad);
    }

    //@formatter:off //This is not cursed at all! idk what your talking about!
    public static class BOW_WYVERN extends RenderModularBow { public BOW_WYVERN() {super(TechLevel.WYVERN);}}
    public static class BOW_DRACONIC extends RenderModularBow { public BOW_DRACONIC() {super(TechLevel.DRACONIC);}}
    public static class BOW_CHAOTIC extends RenderModularBow { public BOW_CHAOTIC() {super(TechLevel.CHAOTIC);}}
    //@formatter::on
}
