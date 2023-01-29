package com.brandon3055.draconicevolution.client.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.common.items.MobSoul;
import com.brandon3055.draconicevolution.common.tileentities.TilePlacedItem;

/**
 * Created by Brandon on 27/07/2014.
 */
public class RenderTilePlacedItem extends TileEntitySpecialRenderer {

    public RenderTilePlacedItem() {}

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float timeSinceLastTick) {
        if (te == null || !(te instanceof TilePlacedItem)) return;
        TilePlacedItem tile = (TilePlacedItem) te;
        if (tile.getStack() == null) return;
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glTranslated(x, y, z);
        renderItem(tile);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    public void renderItem(TilePlacedItem tile) {
        ItemStack stack = tile.getStack();
        World world = Minecraft.getMinecraft().theWorld;
        EntityItem itemEntity = new EntityItem(tile.getWorldObj(), 0, 0, 0, stack);
        int meta = world.getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord);
        // itemEntity.getEntityItem().stackSize = 1;
        itemEntity.hoverStart = 0.0F;
        boolean is3D = stack.getItem().isFull3D();
        boolean isBlock = stack.getItem() instanceof ItemBlock;

        if (isBlock) {
            GL11.glTranslatef(0.5F, 0.25F, 0.5F);
            GL11.glScalef(1.5F, 1.5F, 1.5F);
            metaAdjustBlock(meta);
            GL11.glRotatef(tile.rotation, 0F, 1F, 0F);
        } else if (is3D || stack.getItem() instanceof ItemArmor || stack.getItem() instanceof ItemBow) {
            GL11.glScalef(2F, 2F, 2F);
            GL11.glRotatef(90F, -1F, 0F, 0F);
            GL11.glTranslatef(0.25F, -0.45F, 0.02F);
            metaAdjustItemTool(meta);
            GL11.glTranslatef(0.0F, 0.21F, 0.0F);
            GL11.glRotatef(tile.rotation, 0F, 0F, 1F);
            GL11.glTranslatef(0.0F, -0.21F, 0.0F);
        } else if (stack.getItem() instanceof MobSoul) {

            GL11.glTranslatef(0.5F, 0.3F, 0.5F);
        } else {
            GL11.glRotatef(90F, -1F, 0F, 0F);
            GL11.glTranslatef(0.5F, -0.65F, 0.02F);
            metaAdjustItem(meta);
            GL11.glTranslatef(0.0F, 0.18F, 0.0F);
            GL11.glRotatef(tile.rotation, 0F, 0F, 1F);
            GL11.glTranslatef(0.0F, -0.18F, 0.0F);
        }

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderItem.renderInFrame = true;
        RenderManager.instance.renderEntityWithPosYaw(itemEntity, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
        RenderItem.renderInFrame = false;
        GL11.glDisable(GL11.GL_BLEND);
    }

    private void metaAdjustItemTool(int meta) {
        switch (meta) {
            case 0:
                GL11.glRotatef(180F, 0F, 1F, 0F);
                GL11.glTranslatef(0.0F, 0.0F, -0.46F);
                break;
            case 1:
                break;
            case 2:
                GL11.glRotatef(90F, 1F, 0F, 0F);
                GL11.glRotatef(180F, 0F, 1F, 0F);
                GL11.glTranslatef(0.0F, 0.02F, -0.03F);
                break;
            case 3:
                GL11.glRotatef(90F, 1F, 0F, 0F);
                GL11.glTranslatef(0.0F, 0.02F, -0.43F);
                break;
            case 4:
                GL11.glRotatef(90F, 0F, -1F, 0F);
                GL11.glRotatef(90F, 0F, 0F, -1F);
                GL11.glTranslatef(-0.205F, 0.02F, -0.23F);
                break;
            case 5:
                GL11.glRotatef(90F, 0F, -1F, 0F);
                GL11.glRotatef(90F, 0F, 0F, -1F);
                GL11.glRotatef(180F, 0F, 1F, 0F);
                GL11.glTranslatef(0.223F, 0.0F, -0.23F);
                break;
        }
    }

    private void metaAdjustItem(int meta) {
        switch (meta) {
            case 0:
                GL11.glRotatef(180F, 0F, 1F, 0F);
                GL11.glTranslatef(0.0F, 0.0F, -0.96F);
                break;
            case 1:
                break;
            case 2:
                GL11.glRotatef(90F, 1F, 0F, 0F);
                GL11.glRotatef(180F, 0F, 1F, 0F);
                GL11.glTranslatef(0.0F, 0.32F, -0.33F);
                break;
            case 3:
                GL11.glRotatef(90F, 1F, 0F, 0F);
                GL11.glTranslatef(0.0F, 0.3F, -0.63F);
                break;
            case 4:
                GL11.glRotatef(90F, 0F, -1F, 0F);
                GL11.glRotatef(90F, 0F, 0F, -1F);
                GL11.glTranslatef(-0.17F, 0.302F, -0.475F);
                break;
            case 5:
                GL11.glRotatef(90F, 0F, -1F, 0F);
                GL11.glRotatef(90F, 0F, 0F, -1F);
                GL11.glRotatef(180F, 0F, 1F, 0F);
                GL11.glTranslatef(0.15F, 0.3F, -0.475F);
                break;
        }
    }

    private void metaAdjustBlock(int meta) {
        switch (meta) {
            case 0:
                GL11.glTranslatef(0.0F, 0.51F, 0.0F);
                GL11.glRotatef(180F, 0F, 0F, 1F);
                break;
            case 1:
                GL11.glTranslatef(0.0F, -0.17F, 0.0F);
                break;
            case 2:
                GL11.glTranslatef(-0.0F, 0.17F, 0.34F);
                GL11.glRotatef(90F, -1F, 0F, 0F);
                break;
            case 3:
                GL11.glTranslatef(0.0F, 0.17F, -0.34F);
                GL11.glRotatef(90F, 1F, 0F, 0F);
                break;
            case 4:
                GL11.glTranslatef(0.34F, 0.17F, 0.0F);
                GL11.glRotatef(90F, 0F, 0F, 1F);
                break;
            case 5:
                GL11.glTranslatef(-0.34F, 0.17F, 0.0F);
                GL11.glRotatef(90F, 0F, 0F, -1F);
                break;
        }
    }
}
