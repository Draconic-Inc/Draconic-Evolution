package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorComponent;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorEnergyInjector;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorStabilizer;
import com.brandon3055.draconicevolution.client.model.ModelReactorEnergyInjector;
import com.brandon3055.draconicevolution.client.model.ModelReactorStabilizerCore;
import com.brandon3055.draconicevolution.client.model.ModelReactorStabilizerRing;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.brandon3055.draconicevolution.utils.DETextures;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumFacing;

/**
 * Created by brandon3055 on 20/01/2017.
 */
public class RenderTileReactorComponent extends TESRBase<TileReactorComponent> {

    private static ModelBase stabilizerModel = new ModelReactorStabilizerCore();
    private static ModelBase stabilizerRingModel = new ModelReactorStabilizerRing();
    private static ModelBase injectorModel = new ModelReactorEnergyInjector();

    @Override
    public void renderTileEntityAt(TileReactorComponent te, double x, double y, double z, float partialTicks, int destroyStage) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);

        if (te.facing.value == EnumFacing.SOUTH) {
            GlStateManager.rotate(180, 0, 1, 0);
        }
        else if (te.facing.value == EnumFacing.EAST){
            GlStateManager.rotate(-90, 0, 1, 0);
        }
        else if (te.facing.value == EnumFacing.WEST){
            GlStateManager.rotate(90, 0, 1, 0);
        }
        else if (te.facing.value == EnumFacing.UP){
            GlStateManager.rotate(90, 1, 0, 0);
        }
        else if (te.facing.value == EnumFacing.DOWN){
            GlStateManager.rotate(-90, 1, 0, 0);
        }

        float brightness = 1;
        if (te instanceof TileReactorStabilizer) {
            float coreRotation = 0;//Remember Partial Ticks here
            float ringRotation = 0;//Remember Partial Ticks here

            renderStabilizer(coreRotation, ringRotation, brightness, partialTicks, false);
        }
        else if (te instanceof TileReactorEnergyInjector) {
            renderInjector(brightness, partialTicks, false);
        }


        GlStateManager.popMatrix();
    }

    public static void renderStabilizer(float coreRotation, float ringRotation, float brightness, float partialTicks, boolean invRender) {
        ResourceHelperDE.bindTexture(DETextures.REACTOR_STABILIZER);
        stabilizerModel.render(null, coreRotation, brightness, invRender ? 1 : 0, 0, 0, 1F / 16F);
        ResourceHelperDE.bindTexture(DETextures.REACTOR_STABILIZER_RING);
        GlStateManager.rotate(90, 1, 0, 0);
        GlStateManager.translate(0, -0.58, 0);
        GlStateManager.scale(0.95, 0.95, 0.95);
        GlStateManager.rotate(ringRotation, 0, 1, 0);
        stabilizerRingModel.render(null, -70F, brightness, invRender ? 1 : 0, 0, 0, 1F / 16F);
    }

    public static void renderInjector(float brightness, float partialTicks, boolean invRender) {
        ResourceHelperDE.bindTexture("textures/models/model_reactor_power_injector.png");
        injectorModel.render(null, brightness, invRender ? 1 : 0, 0, 0, 0, 1F / 16F);
    }

}
