package com.brandon3055.draconicevolution.client.render.tile;

import com.brandon3055.draconicevolution.client.model.ModelTeleporterStand;
import com.brandon3055.draconicevolution.common.items.tools.TeleporterMKII;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.tileentities.TileTeleporterStand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 * Created by Brandon on 25/10/2014.
 */
public class RenderTileTeleporterStand extends TileEntitySpecialRenderer{


	ModelTeleporterStand model = new ModelTeleporterStand();

	private final ResourceLocation texture = new ResourceLocation(References.MODID.toLowerCase(), "textures/models/TeleporterStand.png");

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {
		ItemStack item = null;
		int rotation = 0;
		if (tileentity instanceof TileTeleporterStand){
			item = ((TileTeleporterStand) tileentity).getStackInSlot(0);
			rotation = ((TileTeleporterStand) tileentity).rotation;
		}

		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		GL11.glTranslatef((float) x, (float) y, (float) z);

		Minecraft.getMinecraft().renderEngine.bindTexture(texture);

		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		GL11.glScalef(1F, -1F, -1F);
		GL11.glRotated(rotation, 0, 1, 0);

		model.render();

		GL11.glRotatef(90F, 1F, 0F, 0F);
		GL11.glTranslatef(0F, 0F, -0.6F);

		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		if (item != null) renderItem(tileentity, item, f);
		GL11.glPopMatrix();
	}

	public void renderItem(TileEntity tile, ItemStack item, float f){
		GL11.glPushMatrix();

		EntityItem itemEntity = new EntityItem(tile.getWorldObj(), 0, 0, 0, item);
		itemEntity.hoverStart = 0.0F;

		if (item.getItem() instanceof TeleporterMKII) {
			GL11.glTranslatef(0F, 0.18F, 0.864F);
			GL11.glRotated(180, 0, 0, 1);
			GL11.glRotated(30, 1, 0, 0);
			GL11.glScalef(1F, 1F, 1F);
		}else{
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
}
