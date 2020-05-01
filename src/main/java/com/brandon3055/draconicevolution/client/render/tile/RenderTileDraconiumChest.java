package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDraconiumChest;
import com.brandon3055.draconicevolution.utils.ResourceHelperDE;
import com.brandon3055.draconicevolution.client.DETextures;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;

public class RenderTileDraconiumChest extends TESRBase<TileDraconiumChest> {
    public RenderTileDraconiumChest(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }



    //    public static ChestModel modelChest = new ChestModel();

//    @Override
    public void render(TileDraconiumChest te, double x, double y, double z, float partialTicks, int destroyStage) {
        float lidAngle = te.prevLidAngle + (te.lidAngle - te.prevLidAngle) * partialTicks;
        render(te.facing.get(), te.colour.get(), x, y, z, partialTicks, lidAngle, destroyStage);
    }

    public static void render(Direction facing, int colour, double x, double y, double z, float partialTicks, float lidAngle, int destroyStage) {
        float red = (float) (50 + ((colour >> 16) & 0xFF)) / 255f;
        float green = (float) (50 + ((colour >> 8) & 0xFF)) / 255f;
        float blue = (float) (50 + (colour & 0xFF)) / 255f;

        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(515);
        RenderSystem.depthMask(true);
        int i = facing.ordinal();

        if (destroyStage >= 0) {
//            ResourceHelperDE.bindTexture(DESTROY_STAGES[destroyStage]);
//            RenderSystem.matrixMode(5890);
//            RenderSystem.pushMatrix();
//            RenderSystem.scalef(4.0F, 4.0F, 1.0F);
//            RenderSystem.translatef(0.0625F, 0.0625F, 0.0625F);
//            RenderSystem.matrixMode(5888);
        }
        else {
            ResourceHelperDE.bindTexture(DETextures.DRACONIUM_CHEST);
        }


        RenderSystem.pushMatrix();
        RenderSystem.enableRescaleNormal();

        if (destroyStage < 0) {
            RenderSystem.color4f(red, green, blue, 1.0F);
        }

        RenderSystem.translatef((float) x, (float) y + 1.0F, (float) z + 1.0F);
        RenderSystem.scalef(1.0F, -1.0F, -1.0F);
        RenderSystem.translatef(0.5F, 0.5F, 0.5F);
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

        RenderSystem.rotatef((float) j, 0.0F, 1.0F, 0.0F);
        RenderSystem.translatef(-0.5F, -0.5F, -0.5F);

        lidAngle = 1.0F - lidAngle;
        lidAngle = 1.0F - lidAngle * lidAngle * lidAngle;
//        modelChest.getLid().rotateAngleX = -(lidAngle * ((float) Math.PI / 2F));
//        modelChest.renderAll();
        RenderSystem.disableRescaleNormal();
        RenderSystem.popMatrix();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        if (destroyStage >= 0) {
            RenderSystem.matrixMode(5890);
            RenderSystem.popMatrix();
            RenderSystem.matrixMode(5888);
        }
    }
}
