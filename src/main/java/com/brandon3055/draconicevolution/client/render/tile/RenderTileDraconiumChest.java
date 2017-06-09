package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDraconiumChest;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.brandon3055.draconicevolution.utils.DETextures;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumFacing;

public class RenderTileDraconiumChest extends TESRBase<TileDraconiumChest> {

    public static ModelChest modelChest = new ModelChest();

    @Override
    public void renderTileEntityAt(TileDraconiumChest te, double x, double y, double z, float partialTicks, int destroyStage) {
        float lidAngle = te.prevLidAngle + (te.lidAngle - te.prevLidAngle) * partialTicks;
        render(te.facing.value, te.colour.value, x, y, z, partialTicks, lidAngle, destroyStage);
    }

    public static void render(EnumFacing facing, int colour, double x, double y, double z, float partialTicks, float lidAngle, int destroyStage) {
        float red = (float) (50 + ((colour >> 16) & 0xFF)) / 255f;
        float green = (float) (50 + ((colour >> 8) & 0xFF)) / 255f;
        float blue = (float) (50 + (colour & 0xFF)) / 255f;

        GlStateManager.enableDepth();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        int i = facing.ordinal();

        if (destroyStage >= 0) {
            ResourceHelperDE.bindTexture(DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0F, 4.0F, 1.0F);
            GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);
        }
        else {
            ResourceHelperDE.bindTexture(DETextures.DRACONIUM_CHEST);
        }


        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();

        if (destroyStage < 0) {
            GlStateManager.color(red, green, blue, 1.0F);
        }

        GlStateManager.translate((float) x, (float) y + 1.0F, (float) z + 1.0F);
        GlStateManager.scale(1.0F, -1.0F, -1.0F);
        GlStateManager.translate(0.5F, 0.5F, 0.5F);
        int j = 0;

        if (i == 2) {
            j = 180;
        }
        else if (i == 3) {
            j = 0;
        }
        else if (i == 4) {
            j = 90;
        }
        else if (i == 5) {
            j = -90;
        }

        GlStateManager.rotate((float) j, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);

        lidAngle = 1.0F - lidAngle;
        lidAngle = 1.0F - lidAngle * lidAngle * lidAngle;
        modelChest.chestLid.rotateAngleX = -(lidAngle * ((float) Math.PI / 2F));
        modelChest.renderAll();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        if (destroyStage >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }
}
