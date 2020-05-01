package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorComponent;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorInjector;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorStabilizer;
import com.brandon3055.draconicevolution.utils.ResourceHelperDE;
import com.brandon3055.draconicevolution.client.DETextures;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;

/**
 * Created by brandon3055 on 20/01/2017.
 */
public class RenderTileReactorComponent extends TESRBase<TileReactorComponent> {
    public RenderTileReactorComponent(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

//    public static ModelReactorStabilizerCore stabilizerModel = new ModelReactorStabilizerCore();
//    public static ModelReactorStabilizerRing stabilizerRingModel = new ModelReactorStabilizerRing();
//    public static ModelReactorEnergyInjector injectorModel = new ModelReactorEnergyInjector();

//    @Override
    public void render(TileReactorComponent te, double x, double y, double z, float partialTicks, int destroyStage) {
        RenderSystem.pushMatrix();
        RenderSystem.translated(x + 0.5, y + 0.5, z + 0.5);

        if (te.facing.get() == Direction.SOUTH) {
            RenderSystem.rotatef(180, 0, 1, 0);
        }
        else if (te.facing.get() == Direction.EAST) {
            RenderSystem.rotatef(-90, 0, 1, 0);
        }
        else if (te.facing.get() == Direction.WEST) {
            RenderSystem.rotatef(90, 0, 1, 0);
        }
        else if (te.facing.get() == Direction.UP) {
            RenderSystem.rotatef(90, 1, 0, 0);
        }
        else if (te.facing.get() == Direction.DOWN) {
            RenderSystem.rotatef(-90, 1, 0, 0);
        }

        if (te instanceof TileReactorStabilizer) {
            float coreRotation = te.animRotation + (partialTicks * te.animRotationSpeed);//Remember Partial Ticks here
            float ringRotation = coreRotation * -0.5F;//Remember Partial Ticks here
            renderStabilizer(coreRotation, ringRotation, te.animRotationSpeed / 15F, partialTicks, false, destroyStage);
        }
        else if (te instanceof TileReactorInjector) {
            renderInjector(te.animRotationSpeed / 15F, partialTicks, false, destroyStage);
        }

        RenderSystem.popMatrix();
    }

    public static void renderStabilizer(float coreRotation, float ringRotation, float brightness, float partialTicks, boolean invRender, int destroyStage) {
        if (destroyStage >= 0) {
//            ResourceHelperDE.bindTexture(DESTROY_STAGES[destroyStage]);
        }
        else {
            ResourceHelperDE.bindTexture(DETextures.REACTOR_STABILIZER);
        }
//        stabilizerModel.render(coreRotation, brightness, invRender ? 1 : 0, 1F / 16F);
        ResourceHelperDE.bindTexture(DETextures.REACTOR_STABILIZER_RING);
        RenderSystem.rotatef(90, 1, 0, 0);
        RenderSystem.translated(0, -0.58, 0);
        RenderSystem.scaled(0.95, 0.95, 0.95);
        RenderSystem.rotatef(ringRotation, 0, 1, 0);
//        stabilizerRingModel.render(-70F, brightness, invRender ? 1 : 0, 1F / 16F);
    }

    public static void renderInjector(float brightness, float partialTicks, boolean invRender, int destroyStage) {
        if (destroyStage >= 0) {
//            ResourceHelperDE.bindTexture(DESTROY_STAGES[destroyStage]);
        }
        else {
            ResourceHelperDE.bindTexture(DETextures.REACTOR_INJECTOR);
        }
//        injectorModel.render(brightness, invRender ? 1 : 0, 1F / 16F);
    }

}
