package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.render.buffer.TransformingVertexBuilder;
import codechicken.lib.util.SneakyUtils;
import codechicken.lib.vec.*;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.machines.Grinder;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGrinder;
import com.brandon3055.draconicevolution.init.DEContent;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Map;
import java.util.OptionalDouble;

/**
 * Created by brandon3055 on 3/11/19.
 */
public class RenderTileGrinder extends TileEntityRenderer<TileGrinder> {
    private static final double[] ROTATION_MAP = new double[]{0, 180, 90, -90};
    private static final RenderType swordType = RenderType.entitySolid(new ResourceLocation(DraconicEvolution.MODID, "textures/block/grinder.png"));
    private static final RenderType fanType = RenderType.entitySolid(new ResourceLocation(DraconicEvolution.MODID, "textures/block/parts/machine_fan.png"));
    private static final RenderType aoeOutlineType = RenderType.create("aoe", DefaultVertexFormats.POSITION_COLOR, GL11.GL_LINES, 256, RenderType.State.builder()
            .setTransparencyState(RenderState.TRANSLUCENT_TRANSPARENCY)
            .setCullState(RenderState.NO_CULL)
            .setWriteMaskState(RenderState.COLOR_WRITE)
            .setLineState(new RenderState.LineState(OptionalDouble.of(4.0)))
            .setTexturingState(new RenderState.TexturingState("lighting", RenderSystem::disableLighting, SneakyUtils.none()))
            .createCompositeState(false)
    );
    private static final RenderType aoeSolidType = RenderType.create("aoe_solid", DefaultVertexFormats.POSITION_COLOR, GL11.GL_QUADS, 256, RenderType.State.builder()
            .setTransparencyState(RenderState.TRANSLUCENT_TRANSPARENCY)
            .setCullState(RenderState.NO_CULL)
            .setWriteMaskState(RenderState.COLOR_WRITE)
            .setTexturingState(new RenderState.TexturingState("lighting", RenderSystem::disableLighting, SneakyUtils.none()))
            .createCompositeState(false)
    );


    private final CCModel swordModel;
    private final CCModel fanModel;


    public RenderTileGrinder(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
        Map<String, CCModel> map = OBJParser.parseModels(new ResourceLocation(DraconicEvolution.MODID, "models/block/grinder/grinder_fan.obj"), GL11.GL_QUADS, null);
        fanModel = CCModel.combine(map.values());

        map = OBJParser.parseModels(new ResourceLocation(DraconicEvolution.MODID, "models/block/grinder/grinder_sword.obj"), GL11.GL_QUADS, null);
        swordModel = CCModel.combine(map.values());
        swordModel.computeNormals();
        swordModel.apply(new Scale(-1 / 16F));
        swordModel.apply(new Rotation(180 * MathHelper.torad, 0, 0, 1));
        swordModel.apply(new Rotation(90 * MathHelper.torad, 0, 1, 0));
    }

    @Override
    public void render(TileGrinder tile, float partialTicks, MatrixStack mStack, IRenderTypeBuffer getter, int packedLight, int packedOverlay) {
        BlockState state = tile.getLevel().getBlockState(tile.getBlockPos());
        if (state.getBlock() != DEContent.grinder) return;
        Direction facing = state.getValue(Grinder.FACING);

        Matrix4 mat = new Matrix4(mStack);
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;

        //Note to self: this is the hacky approach. Ideally this should be converted to matrix operations.
        //But it is performant so i am leaving it like this to remind myself that this is possible.
        ccrs.bind(swordType, getter);
        ccrs.r = new TransformingVertexBuilder(ccrs.r, mat);
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
            IVertexBuilder builder = new TransformingVertexBuilder(getter.getBuffer(aoeOutlineType), mat);
            Cuboid6 box = new Cuboid6(tile.killZone.move(Vector3.fromTile(tile).multiply(-1).pos()).deflate(0.01).deflate(tile.aoe.get() - tile.aoeDisplay));
            RenderUtils.bufferCuboidOutline(builder, box, 0F, 0F, 0F, 1F);
            builder = new TransformingVertexBuilder(getter.getBuffer(aoeSolidType), mat);
            RenderUtils.bufferCuboidSolid(builder, box, 0F, 1F, 1F, 0.2F);
        }
    }

////    @Override
//    public void render(TileGrinder te, double x, double y, double z, float partialTicks, int destroyStage) {
//        BlockState state = te.getWorld().getBlockState(te.getPos());
//        if (state.getBlock() == DEContent.grinder) {
//
//            if (te.aoeDisplay > 0.51) {
//                te.validateKillZone(true);
//                RenderSystem.enableBlend(); //RenderState.TRANSLUCENT_TRANSPARENCY
//                RenderSystem.color4f(0F, 1F, 1F, 0.2F); //On the VF
//                RenderSystem.disableTexture();
//                RenderSystem.disableCull(); //RenderState.CULL_DISABLED
//                RenderSystem.depthMask(false); //RenderState.COLOR_WRITE //writeState
//                RenderSystem.lineWidth(4); //
//                RenderSystem.disableLighting();
//                Cuboid6 box = new Cuboid6(te.killZone.offset(Vector3.fromTile(te).multiply(-1).pos()).shrink(0.01).shrink(te.aoe.get() - te.aoeDisplay));
//                RenderUtils.drawCuboidSolid(box);
//                RenderSystem.color4f(0F, 0F, 0F, 1F);
//                RenderUtils.drawCuboidOutline(box);
//                RenderSystem.enableLighting();
//                RenderSystem.depthMask(true);
//                RenderSystem.enableCull();
//                RenderSystem.enableTexture();
//                RenderSystem.disableBlend();
//            }
//
//            RenderSystem.translated(-x, -y, -z);
//        }
//    }

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
        Vector3 vec = new Vector3(entity.xOld, entity.yOld - entity.getMyRidingOffset() + (double) (entity.getBbHeight() / 2.0F), entity.zOld);
        vec.add(Vector3.fromEntityCenter(entity).subtract(entity.xOld, entity.yOld - entity.getMyRidingOffset() + (double) (entity.getBbHeight() / 2.0F), entity.zOld).multiply(partialTicks));
        return vec;
    }
}
