package com.brandon3055.draconicevolution.client.render.tile;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.brandon3055.draconicevolution.client.model.ModelReactorEnergyInjector;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor.TileReactorEnergyInjector;

/**
 * Created by Brandon on 6/7/2015.
 */
public class RenderTileReactorEnergyInjector extends TileEntitySpecialRenderer {

    public static ModelReactorEnergyInjector modelReactorEnergyInjector = new ModelReactorEnergyInjector();

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTick) {
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);

        renderCore((TileReactorEnergyInjector) tileEntity, partialTick);

        GL11.glPopMatrix();
    }

    public static void renderCore(TileReactorEnergyInjector tile, float partialTick) {
        GL11.glPushMatrix();
        float scale = (1F / 16F);

        switch (tile.facingDirection) {
            case 1:
                GL11.glRotated(180, -1, 0, 0);
                break;
            case 2:
                GL11.glRotated(90, 1, 0, 0);
                break;
            case 3:
                GL11.glRotated(90, -1, 0, 0);
                break;
            case 4:
                GL11.glRotated(90, 0, 0, -1);
                break;
            case 5:
                GL11.glRotated(90, 0, 0, 1);
        }

        ResourceHandler.bindResource("textures/models/ModelReactorPowerInjector.png");
        modelReactorEnergyInjector.render(null, tile.modelIllumination, 0F, 0F, 0F, 0F, scale);

        GL11.glPopMatrix();
    }
}
