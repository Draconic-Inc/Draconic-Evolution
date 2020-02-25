package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.vec.*;
import com.brandon3055.draconicevolution.DEContent;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.machines.Grinder;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGrinder;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Map;

/**
 * Created by brandon3055 on 3/11/19.
 */
public class RenderTileGrinder extends TileEntityRenderer<TileGrinder> {

    private static ResourceLocation MODEL_TEXTURE = new ResourceLocation(DraconicEvolution.MODID, "textures/models/blocks/grinder.png");
    private static CCModel storageModel;
    private static final double[] ROTATION_MAP = new double[]{0, 180, 90, -90};

    public RenderTileGrinder() {
        if (storageModel == null) {
            Map<String, CCModel> map = OBJParser.parseModels(ResourceHelperDE.getResource("models/block/grinder/grinder_sword.obj"));
            storageModel = CCModel.combine(map.values());
            storageModel.computeNormals();
            storageModel.apply(new Scale(-1 / 16F));
            storageModel.apply(new Rotation(180 * MathHelper.torad, 0, 0, 1));
            storageModel.apply(new Rotation(90 * MathHelper.torad, 0, 1, 0));
        }
    }

    @Override
    public void render(TileGrinder te, double x, double y, double z, float partialTicks, int destroyStage) {
        BlockState state = te.getWorld().getBlockState(te.getPos());
        if (state.getBlock() == DEContent.grinder) {
            GlStateManager.translated(x, y, z);
            bindTexture(MODEL_TEXTURE);
            Direction facing = state.get(Grinder.FACING);
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_NORMAL);


            Vector3 tilePos = Vector3.fromTileCenter(te);
            Vector3 vecA = te.targetA == null ? null : getEntityMovingVec(te.targetA, partialTicks);
            Vector3 vecB = te.targetB == null ? null : getEntityMovingVec(te.targetB, partialTicks);
            renderSword(ccrs, facing, 0.34, tilePos, vecA, Math.min(te.animA + (partialTicks * te.getAnimSpeed()), 1), partialTicks);
            renderSword(ccrs, facing, -0.34, tilePos, vecB, Math.min(te.animB + (partialTicks * te.getAnimSpeed()), 1), partialTicks);

            ccrs.draw();
            GlStateManager.translated(-x, -y, -z);
        }
    }

    private void renderSword(CCRenderState ccrs, Direction tileFacing, double sideOffset, Vector3 tilePos, Vector3 targetPos, double attackAnimTime, float partialTicks) {
        attackAnimTime *= 2;
        double yoyo = attackAnimTime > 1D ? 1D - (attackAnimTime - 1D) : attackAnimTime;
        sideOffset *= 1D - yoyo;
        double sideAngle = ROTATION_MAP[tileFacing.getIndex() - 2] * MathHelper.torad;

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

        storageModel.render(ccrs, transforms);
    }

    private Vector3 getEntityMovingVec(Entity entity, float partialTicks) {
        Vector3 vec = new Vector3(entity.lastTickPosX, entity.lastTickPosY - entity.getYOffset() + (double) (entity.getHeight() / 2.0F), entity.lastTickPosZ);
        vec.add(Vector3.fromEntityCenter(entity).subtract(entity.lastTickPosX, entity.lastTickPosY - entity.getYOffset() + (double) (entity.getHeight() / 2.0F), entity.lastTickPosZ).multiply(partialTicks));
        return vec;
    }
}
