package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import com.brandon3055.brandonscore.common.utills.Utills;
import com.brandon3055.draconicevolution.client.model.ModelTeleporterStand;
import com.brandon3055.draconicevolution.common.handler.ConfigHandler;
import com.brandon3055.draconicevolution.common.items.tools.TeleporterMKII;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.tileentities.TileTeleporterStand;
import java.awt.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 * Created by Brandon on 25/10/2014.
 */
public class RenderTileTeleporterStand extends TileEntitySpecialRenderer {

    ModelTeleporterStand model = new ModelTeleporterStand();

    private final ResourceLocation texture =
            new ResourceLocation(References.MODID.toLowerCase(), "textures/models/TeleporterStand.png");

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {

        ItemStack item = null;
        int rotation = 0;
        if (tileentity instanceof TileTeleporterStand) {
            item = ((TileTeleporterStand) tileentity).getStackInSlot(0);
            rotation = ((TileTeleporterStand) tileentity).rotation;
        }

        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef((float) x, (float) y, (float) z);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);

        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        GL11.glScalef(1F, -1F, -1F);
        GL11.glRotated(rotation, 0, 1, 0);

        model.render();
        if (item != null) drawNameString(item, rotation, tileentity, f);

        GL11.glRotatef(90F, 1F, 0F, 0F);
        GL11.glTranslatef(0F, 0F, -0.6F);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        if (item != null) renderItem(tileentity, item, f);
        GL11.glPopMatrix();
    }

    public void renderItem(TileEntity tile, ItemStack item, float f) {
        if (item.getItem() == null) return;
        GL11.glPushMatrix();

        EntityItem itemEntity = new EntityItem(tile.getWorldObj(), 0, 0, 0, item);
        itemEntity.hoverStart = 0.0F;

        if (item.getItem() instanceof TeleporterMKII) {
            GL11.glTranslatef(0F, 0.18F, 0.864F);
            GL11.glRotated(180, 0, 0, 1);
            GL11.glRotated(30, 1, 0, 0);
            GL11.glScalef(1F, 1F, 1F);
        } else {
            GL11.glTranslatef(0F, 0.22F, 0.84F);
            GL11.glRotated(180, 0, 0, 1);
            GL11.glRotated(30, 1, 0, 0);
            GL11.glScalef(1F, 1F, 1F);
        }

        RenderItem.renderInFrame = true;
        RenderManager.instance.renderEntityWithPosYaw(itemEntity, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
        RenderItem.renderInFrame = false;

        GL11.glPopMatrix();
    }

    private void drawNameString(ItemStack item, float rotation, TileEntity tileentity, float f) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        MovingObjectPosition mop = player.rayTrace(10, f);
        boolean isCursorOver = mop != null
                && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
                && mop.blockX == tileentity.xCoord
                && mop.blockY == tileentity.yCoord
                && mop.blockZ == tileentity.zCoord;
        boolean isSneaking = player.isSneaking();

        if (!isCursorOver && (isSneaking != ConfigHandler.invertDPDSB)) return;

        String s = item.hasDisplayName() ? item.getDisplayName() : "";
        if (item.getItem() instanceof TeleporterMKII) {
            short selected = ItemNBTHelper.getShort(item, "Selection", (short) 0);
            int selrctionOffset = ItemNBTHelper.getInteger(item, "SelectionOffset", 0);
            NBTTagCompound compound = item.getTagCompound();
            if (compound == null) compound = new NBTTagCompound();
            NBTTagList list = (NBTTagList) compound.getTag("Locations");
            if (list == null) list = new NBTTagList();
            s = list.getCompoundTagAt(selected + selrctionOffset).getString("Name");
        }
        if (s.isEmpty()) return;

        FontRenderer fontRenderer = RenderManager.instance.getFontRenderer();
        Tessellator tess = Tessellator.instance;

        GL11.glPushMatrix();
        GL11.glScalef(0.02f, 0.02f, 0.02f);
        GL11.glRotated(180, 0, 1, 0);
        GL11.glTranslated(0, -40, 0);

        Point.Double p1 = new Point.Double(tileentity.xCoord + 0.5, tileentity.zCoord + 0.5);
        Point.Double p2 = new Point.Double(player.posX, player.posZ);

        double xDiff = player.posX - (tileentity.xCoord + 0.5);
        double yDiff = player.posY - (tileentity.yCoord + 0.5);
        double zDiff = player.posZ - (tileentity.zCoord + 0.5);
        double yawAngle = Math.toDegrees(Math.atan2(zDiff, xDiff));
        double pitchAngle = Math.toDegrees(Math.atan2(
                yDiff,
                Utills.getDistanceAtoB(
                        player.posX,
                        player.posY,
                        player.posZ,
                        tileentity.xCoord + 0.5,
                        tileentity.yCoord + 0.5,
                        tileentity.zCoord + 0.5)));

        GL11.glRotated(yawAngle + 90 - rotation, 0, 1, 0);
        GL11.glRotated(-pitchAngle, 1, 0, 0);

        int xmin = -1 - fontRenderer.getStringWidth(s) / 2;
        int xmax = 1 + fontRenderer.getStringWidth(s) / 2;
        int ymin = -1;
        int ymax = fontRenderer.FONT_HEIGHT;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(0f, 0f, 0f, 0.5f);

        tess.startDrawingQuads();
        tess.addVertexWithUV(xmin, ymax, 0, xmin / 64, 1);
        tess.addVertexWithUV(xmax, ymax, 0, xmax / 64, 1);
        tess.addVertexWithUV(xmax, ymin, 0, xmax / 64, 0.75);
        tess.addVertexWithUV(xmin, ymin, 0, xmin / 64, 0.75);
        tess.draw();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glTranslated(0, 0, -0.1);
        GL11.glColor4f(1f, 1f, 1f, 1f);
        GL11.glDisable(GL11.GL_LIGHTING);

        fontRenderer.drawString(s, -fontRenderer.getStringWidth(s) / 2, 0, 0xffffff);

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }
}
