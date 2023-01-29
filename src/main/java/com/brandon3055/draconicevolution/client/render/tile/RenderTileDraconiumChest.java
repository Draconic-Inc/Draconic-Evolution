package com.brandon3055.draconicevolution.client.render.tile;

import static org.lwjgl.opengl.GL11.*;

import java.util.Random;

import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.tileentities.TileDraconiumChest;

/**
 * Created by Brandon on 25/10/2014.
 */
public class RenderTileDraconiumChest extends TileEntitySpecialRenderer {

    ModelChest model;
    private final ResourceLocation texture = new ResourceLocation(
            References.MODID.toLowerCase(),
            "textures/models/DraconiumChest.png");
    private Random random;

    public RenderTileDraconiumChest() {
        model = new ModelChest();
        random = new Random();
    }

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {
        render((TileDraconiumChest) tileentity, x, y, z, f);
    }

    public void render(TileDraconiumChest tile, double x, double y, double z, float partialTick) {
        if (tile == null) {
            return;
        }
        int facing = 3;
        if (tile.hasWorldObj()) {
            facing = tile.getFacing();
        }
        bindTexture(texture);
        glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        glEnable(32826 /* GL_RESCALE_NORMAL_EXT */);
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float red = (float) (tile.red + 50) / 255f;
        float green = (float) (tile.green + 50) / 255f;
        float blue = (float) (tile.blue + 50) / 255f;
        glColor4f(red, green, blue, 1.0F);
        glTranslatef((float) x, (float) y + 1.0F, (float) z + 1.0F);
        glScalef(1.0F, -1F, -1F);
        glTranslatef(0.5F, 0.5F, 0.5F);
        int k = 0;
        if (facing == 2) {
            k = 180;
        }
        if (facing == 3) {
            k = 0;
        }
        if (facing == 4) {
            k = 90;
        }
        if (facing == 5) {
            k = -90;
        }
        glRotatef(k, 0.0F, 1.0F, 0.0F);
        glTranslatef(-0.5F, -0.5F, -0.5F);
        float lidangle = tile.prevLidAngle + (tile.lidAngle - tile.prevLidAngle) * partialTick;
        lidangle = 1.0F - lidangle;
        lidangle = 1.0F - lidangle * lidangle * lidangle;
        model.chestLid.rotateAngleX = -((lidangle * 3.141593F) / 2.0F);
        model.renderAll();
        glDisable(32826 /* GL_RESCALE_NORMAL_EXT */);
        GL11.glPopAttrib();
        glPopMatrix();
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
