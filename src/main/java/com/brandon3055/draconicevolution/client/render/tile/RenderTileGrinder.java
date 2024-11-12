package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.render.buffer.TransformingVertexConsumer;
import codechicken.lib.render.model.OBJParser;
import codechicken.lib.vec.*;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.machines.Grinder;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGrinder;
import com.brandon3055.draconicevolution.init.DEContent;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.OptionalDouble;

/**
 * Created by brandon3055 on 3/11/19.
 */
public class RenderTileGrinder implements BlockEntityRenderer<TileGrinder> {
    private static final double[] ROTATION_MAP = new double[]{0, 180, 90, -90};
    private static final RenderType swordType = RenderType.entitySolid(new ResourceLocation(DraconicEvolution.MODID, "textures/block/grinder.png"));
    private static final RenderType fanType = RenderType.entitySolid(new ResourceLocation(DraconicEvolution.MODID, "textures/block/parts/machine_fan.png"));
    private static final RenderType aoeOutlineType = RenderType.create("aoe", DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, 256, RenderType.CompositeState.builder()
            .setShaderState(RenderStateShard.RENDERTYPE_LINES_SHADER)
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setCullState(RenderStateShard.NO_CULL)
            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
            .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(4.0)))
            .createCompositeState(false)
    );
    private static RenderType aoeSolidType = RenderType.create("aoe_solid", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, RenderType.CompositeState.builder()
            .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorShader))
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setCullState(RenderStateShard.NO_CULL)
            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
            .createCompositeState(false)
    );


    private final CCModel swordModel;
    private final CCModel fanModel;


    public RenderTileGrinder(BlockEntityRendererProvider.Context context) {
        Map<String, CCModel> map = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/block/grinder/grinder_fan.obj")).quads().ignoreMtl().parse();
        fanModel = CCModel.combine(map.values());

        map = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/block/grinder/grinder_sword.obj")).quads().ignoreMtl().parse();
        swordModel = CCModel.combine(map.values());
        swordModel.computeNormals();
        swordModel.apply(new Scale(-1 / 16F));
        swordModel.apply(new Rotation(180 * MathHelper.torad, 0, 0, 1));
        swordModel.apply(new Rotation(90 * MathHelper.torad, 0, 1, 0));
    }

    @Override
    public void render(TileGrinder tile, float partialTicks, PoseStack mStack, MultiBufferSource getter, int packedLight, int packedOverlay) {
        BlockState state = tile.getLevel().getBlockState(tile.getBlockPos());
        if (!state.is(DEContent.GRINDER.get())) return;
        Direction facing = state.getValue(Grinder.FACING);

        Matrix4 mat = new Matrix4(mStack);
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;

        //Note to self: this is the hacky approach. Ideally this should be converted to matrix operations.
        //But it is performant so i am leaving it like this to remind myself that this is possible.
        ccrs.bind(swordType, getter);
        ccrs.r = new TransformingVertexConsumer(ccrs.r, mat);
        Vector3 tilePos = Vector3.fromTileCenter(tile);
        Vector3 vecA = tile.targetA == null ? null : getEntityMovingVec(tile.targetA, partialTicks);
        Vector3 vecB = tile.targetB == null ? null : getEntityMovingVec(tile.targetB, partialTicks);
        renderSword(ccrs, facing, 0.34, tilePos, vecA, Math.min(tile.animA + (partialTicks * tile.getAnimSpeed()), 1), partialTicks);
        renderSword(ccrs, facing, -0.34, tilePos, vecB, Math.min(tile.animB + (partialTicks * tile.getAnimSpeed()), 1), partialTicks);

        ccrs.bind(fanType, getter);
        Matrix4 fanMat = mat.copy();
        fanMat.translate(Vector3.CENTER);
        fanMat.apply(new Rotation(facing.toYRot() * -MathHelper.torad, 0, 1, 0));
        fanMat.apply(new Scale(-0.0625));
        fanMat.apply(new Rotation((tile.fanRotation + (tile.fanSpeed * partialTicks)), 0, 0, 1));
        fanModel.render(ccrs, fanMat);

        if (tile.aoeDisplay > 0.51) {
            tile.validateKillZone(true);
            VertexConsumer builder = new TransformingVertexConsumer(getter.getBuffer(aoeOutlineType), mat);
            Cuboid6 box = new Cuboid6(tile.killZone.move(Vector3.fromTile(tile).multiply(-1).pos()).deflate(0.01).deflate(tile.aoe.get() - tile.aoeDisplay));
            RenderUtils.bufferCuboidOutline(builder, box, 0F, 0F, 0F, 1F);
            builder = new TransformingVertexConsumer(getter.getBuffer(aoeSolidType), mat);
            RenderUtils.bufferCuboidSolid(builder, box, 0F, 1F, 1F, 0.2F);
            com.brandon3055.brandonscore.client.render.RenderUtils.endBatch(getter);
        }
    }

    private void renderSword(CCRenderState ccrs, Direction tileFacing, double sideOffset, Vector3 tilePos, Vector3 targetPos, double attackAnimTime, float partialTicks) {
        attackAnimTime *= 2;
        double yoyo = attackAnimTime > 1D ? 1D - (attackAnimTime - 1D) : attackAnimTime;
        sideOffset *= 1D - yoyo;
        double sideAngle = ROTATION_MAP[tileFacing.get3DDataValue() - 2] * MathHelper.torad;

        Transformation rotation = new Rotation(yoyo * Math.PI * -2.8, 1, 0, 0).at(new Vector3(0, -0.2, 0));
        Vector3 handPos = new Vector3(0.4975 + sideOffset, 0.5, 0.125 + (yoyo * (0.375)));
        handPos.apply(new Rotation(sideAngle, 0, 1, 0).at(new Vector3(0.5, 0, 0.5)));

        Transformation translation = new Translation(handPos);
        TransformationList transforms = rotation.with(translation);
        if (targetPos == null) {
            transforms = transforms.with(new Rotation(sideAngle, 0, 1, 0).at(handPos));
        } else {
            Vector3 targetVec = targetPos.copy().subtract(tilePos);
            double angle = Math.atan2(targetVec.x, targetVec.z) + Math.PI;
            transforms = transforms.with(new Rotation(angle, 0, 1, 0).at(handPos));
            transforms = transforms.with(new Translation(targetVec.multiply(yoyo)));
        }

        swordModel.render(ccrs, transforms);
    }

    private Vector3 getEntityMovingVec(Entity entity, float partialTicks) {
        return new Vector3(entity.getPosition(partialTicks)).add(0, entity.getBbHeight() / 2D, 0);
    }

    @Override
    public AABB getRenderBoundingBox(TileGrinder blockEntity) {
        return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity).inflate(8);
    }
}
