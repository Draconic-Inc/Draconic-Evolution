package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCoreStabilizer;
import com.brandon3055.draconicevolution.client.model.ModelLargeECStabilizer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;

/**
 * Created by brandon3055 on 19/4/2016.
 */
public class RenderTileECStabilizer extends TileEntityRenderer<TileEnergyCoreStabilizer> {

    private static final RenderType modelType = RenderType.entitySolid(new ResourceLocation(DraconicEvolution.MODID, "textures/block/stabilizer_large.png"));
    private final ModelLargeECStabilizer model;

    public RenderTileECStabilizer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
        model = new ModelLargeECStabilizer();
    }

    @Override
    public void render(TileEnergyCoreStabilizer tile, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer getter, int packedLightIn, int packedOverlayIn) {
        if (!tile.isValidMultiBlock.get()) return;

        Direction facing;
        if (tile.isCoreActive.get()) {
            facing = tile.coreDirection.get();
        } else {
            facing = Direction.get(Direction.AxisDirection.POSITIVE, tile.multiBlockAxis.get());
        }

        matrixStack.pushPose();
        matrixStack.translate(0.5, 0.5, 0.5);
        if (facing.getAxis() == Direction.Axis.X || facing.getAxis() == Direction.Axis.Y) {
            matrixStack.mulPose(new Quaternion(facing.getStepY() * 90, facing.getStepX() * -90, 0, true));
        } else if (facing == Direction.SOUTH) {
            matrixStack.mulPose(new Quaternion(0, 180F, 0, true));
        }
        matrixStack.mulPose(new Quaternion(0, 0F, tile.rotation + (tile.isCoreActive.get() ? partialTicks : 0), true));
        model.renderToBuffer(matrixStack, getter.getBuffer(modelType), packedLightIn, packedOverlayIn, 1, 1, 1, 1);
        matrixStack.popPose();
    }
}
