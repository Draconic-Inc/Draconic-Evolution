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

        if (te instanceof TileReactorStabilizer) {
            float coreRotation = 0;
            float ringRotation = 0;
            float brightness = 1;

            renderStabilizer(coreRotation, ringRotation, brightness, partialTicks);
        }
        else if (te instanceof TileReactorEnergyInjector) {
            renderInjector((TileReactorEnergyInjector) te, partialTicks);
        }


        GlStateManager.popMatrix();
    }

    private void renderStabilizer(float coreRotation, float ringRotation, float brightness, float partialTicks) {
        bindTexture(ResourceHelperDE.getResource(DETextures.REACTOR_STABILIZER));
        stabilizerModel.render(null, 19, 1, 1, 1, 1, 1F / 16F);
    }

    private void renderInjector(TileReactorEnergyInjector te, float partialTicks) {
        bindTexture(ResourceHelperDE.getResource("textures/models/model_reactor_power_injector.png"));
        injectorModel.render(null, 0, 0, 0, 0, 0, 1F / 16F);

    }

}
