package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorComponent;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorInjector;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorStabilizer;
import com.brandon3055.draconicevolution.client.DETextures;
import com.brandon3055.draconicevolution.client.model.ModelReactorEnergyInjector;
import com.brandon3055.draconicevolution.client.model.ModelReactorStabilizerCore;
import com.brandon3055.draconicevolution.client.model.ModelReactorStabilizerRing;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;

/**
 * Created by brandon3055 on 20/01/2017.
 */
public class RenderTileReactorComponent extends TileEntityRenderer<TileReactorComponent> {
    public RenderTileReactorComponent(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    public static final ResourceLocation REACTOR_STABILIZER = new ResourceLocation(DraconicEvolution.MODID, "textures/block/reactor/reactor_stabilizer_core.png");
    public static final ResourceLocation REACTOR_STABILIZER_RING = new ResourceLocation(DraconicEvolution.MODID, "textures/block/reactor/reactor_stabilizer_ring.png");
    public static final ResourceLocation REACTOR_INJECTOR = new ResourceLocation(DraconicEvolution.MODID, "textures/block/reactor/model_reactor_power_injector.png");

    public static ModelReactorStabilizerCore stabilizerModel = new ModelReactorStabilizerCore(RenderType::entitySolid);
    public static ModelReactorStabilizerRing stabilizerRingModel = new ModelReactorStabilizerRing(RenderType::entitySolid);
    public static ModelReactorEnergyInjector injectorModel = new ModelReactorEnergyInjector(RenderType::entitySolid);

    @Override
    public void render(TileReactorComponent te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer getter, int packedLight, int packedOverlay) {
        matrix.translate(0.5, 0.5, 0.5);

        if (te.facing.get() == Direction.SOUTH) {
            matrix.mulPose(new Quaternion(0, 180, 0, true));
        } else if (te.facing.get() == Direction.EAST) {
            matrix.mulPose(new Quaternion(0, -90, 0, true));
        } else if (te.facing.get() == Direction.WEST) {
            matrix.mulPose(new Quaternion(0, 90, 0, true));
        } else if (te.facing.get() == Direction.UP) {
            matrix.mulPose(new Quaternion(90, 0, 0, true));
        } else if (te.facing.get() == Direction.DOWN) {
            matrix.mulPose(new Quaternion(-90, 0, 0, true));
        }

        if (te instanceof TileReactorStabilizer) {
            float coreRotation = te.animRotation + (partialTicks * te.animRotationSpeed);//Remember Partial Ticks here
            renderStabilizer(matrix, getter, coreRotation, te.animRotationSpeed / 15F, packedLight, packedOverlay);
        } else if (te instanceof TileReactorInjector) {
            renderInjector(matrix, getter, te.animRotationSpeed / 15F, packedLight, packedOverlay);
        }
    }


    public static void renderStabilizer(MatrixStack matrix, IRenderTypeBuffer getter, float coreRotation, float brightness, int packedLight, int packedOverlay) {
        float ringRotation = coreRotation * -0.5F;//Remember Partial Ticks here
        stabilizerModel.brightness = brightness;
        stabilizerModel.rotation = coreRotation;
        stabilizerModel.renderToBuffer(matrix, getter.getBuffer(stabilizerModel.renderType(REACTOR_STABILIZER)), packedLight, packedOverlay, 1F, 1F, 1F, 1F);
        matrix.mulPose(new Quaternion(90, 0, 0, true));
        matrix.translate(0, -0.58, 0);
//        matrix.scale(0.95F, 0.95F, 0.95F);
        matrix.mulPose(new Quaternion(0, ringRotation, 0, true));
        stabilizerRingModel.brightness = brightness;
        stabilizerRingModel.embitterRotation = 70F;
        stabilizerRingModel.renderToBuffer(matrix, getter.getBuffer(stabilizerModel.renderType(REACTOR_STABILIZER_RING)), packedLight, packedOverlay, 1F, 1F, 1F, 1F);
    }

    public static void renderInjector(MatrixStack matrix, IRenderTypeBuffer getter, float brightness, int packedLight, int packedOverlay) {
        injectorModel.brightness = brightness;
        injectorModel.renderToBuffer(matrix, getter.getBuffer(injectorModel.renderType(REACTOR_INJECTOR)), packedLight, packedOverlay, 1F, 1F, 1F, 1F);
    }

}
