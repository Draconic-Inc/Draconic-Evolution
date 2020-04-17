package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorComponent;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorInjector;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorStabilizer;
import com.brandon3055.draconicevolution.client.model.ModelReactorEnergyInjector;
import com.brandon3055.draconicevolution.client.model.ModelReactorStabilizerCore;
import com.brandon3055.draconicevolution.client.model.ModelReactorStabilizerRing;
import com.brandon3055.draconicevolution.utils.ResourceHelperDE;
import com.brandon3055.draconicevolution.utils.DETextures;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Direction;

/**
 * Created by brandon3055 on 20/01/2017.
 */
public class RenderTileReactorComponent extends TESRBase<TileReactorComponent> {

    public static ModelReactorStabilizerCore stabilizerModel = new ModelReactorStabilizerCore();
    public static ModelReactorStabilizerRing stabilizerRingModel = new ModelReactorStabilizerRing();
    public static ModelReactorEnergyInjector injectorModel = new ModelReactorEnergyInjector();

    @Override
    public void render(TileReactorComponent te, double x, double y, double z, float partialTicks, int destroyStage) {
        GlStateManager.pushMatrix();
        GlStateManager.translated(x + 0.5, y + 0.5, z + 0.5);

        if (te.facing.get() == Direction.SOUTH) {
            GlStateManager.rotated(180, 0, 1, 0);
        }
        else if (te.facing.get() == Direction.EAST) {
            GlStateManager.rotated(-90, 0, 1, 0);
        }
        else if (te.facing.get() == Direction.WEST) {
            GlStateManager.rotated(90, 0, 1, 0);
        }
        else if (te.facing.get() == Direction.UP) {
            GlStateManager.rotated(90, 1, 0, 0);
        }
        else if (te.facing.get() == Direction.DOWN) {
            GlStateManager.rotated(-90, 1, 0, 0);
        }

        if (te instanceof TileReactorStabilizer) {
            float coreRotation = te.animRotation + (partialTicks * te.animRotationSpeed);//Remember Partial Ticks here
            float ringRotation = coreRotation * -0.5F;//Remember Partial Ticks here
            renderStabilizer(coreRotation, ringRotation, te.animRotationSpeed / 15F, partialTicks, false, destroyStage);
        }
        else if (te instanceof TileReactorInjector) {
            renderInjector(te.animRotationSpeed / 15F, partialTicks, false, destroyStage);
        }

        GlStateManager.popMatrix();
    }

    public static void renderStabilizer(float coreRotation, float ringRotation, float brightness, float partialTicks, boolean invRender, int destroyStage) {
        if (destroyStage >= 0) {
            ResourceHelperDE.bindTexture(DESTROY_STAGES[destroyStage]);
        }
        else {
            ResourceHelperDE.bindTexture(DETextures.REACTOR_STABILIZER);
        }
        stabilizerModel.render(coreRotation, brightness, invRender ? 1 : 0, 1F / 16F);
        ResourceHelperDE.bindTexture(DETextures.REACTOR_STABILIZER_RING);
        GlStateManager.rotated(90, 1, 0, 0);
        GlStateManager.translated(0, -0.58, 0);
        GlStateManager.scaled(0.95, 0.95, 0.95);
        GlStateManager.rotated(ringRotation, 0, 1, 0);
        stabilizerRingModel.render(-70F, brightness, invRender ? 1 : 0, 1F / 16F);
    }

    public static void renderInjector(float brightness, float partialTicks, boolean invRender, int destroyStage) {
        if (destroyStage >= 0) {
            ResourceHelperDE.bindTexture(DESTROY_STAGES[destroyStage]);
        }
        else {
            ResourceHelperDE.bindTexture(DETextures.REACTOR_INJECTOR);
        }
        injectorModel.render(brightness, invRender ? 1 : 0, 1F / 16F);
    }

}


//        if (destroyStage >= 0) {
//                bindTexture(DESTROY_STAGES[destroyStage]);
//                ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
//                crystalFull.render(ccrs, mat);
//                ccrs.draw();
//                return;
//                }