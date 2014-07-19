package com.brandon3055.draconicevolution.client.render;

import com.brandon3055.draconicevolution.common.core.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.common.core.utills.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class SoulItemRenderer implements IItemRenderer {
	private Minecraft mc;

	public SoulItemRenderer() {
		this.mc = Minecraft.getMinecraft();
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON  || type == ItemRenderType.INVENTORY  || type == ItemRenderType.ENTITY;
	}

	@Override

	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		Entity mob = EntityList.createEntityByName(ItemNBTHelper.getString(item, "Name", "Pig"), mc.theWorld);

		GL11.glPushMatrix();
		//GL11.glEnable(GL11.GL_BLEND);
		GL11.glScalef(0.5F, 0.5F, 0.5F);
		//GL11.glColor4f(1F, 1F, 1F, 0.5F);

		if (type == ItemRenderType.INVENTORY)
		{
			GL11.glPushMatrix();
			//RenderHelper.enableStandardItemLighting();
			GL11.glScalef(13F, 13F, 13F);
			GL11.glTranslated(1.2, 2.2, 0);
			GL11.glRotatef(180F, 1F, 0F, 0F);
			GL11.glRotatef(mc.getSystemTime() / -10, 0F, 1F, 0F);
			GL11.glRotatef(-20F, 1F, 0F, 0F);
			RenderManager.instance.renderEntityWithPosYaw(mob, 0, 0, 0, 0F, 1F);
			//RenderHelper.disableStandardItemLighting();
			GL11.glPopMatrix();
		} else if (type == ItemRenderType.EQUIPPED_FIRST_PERSON)
		{
			GL11.glPushMatrix();
			GL11.glScalef(0.8F, 0.8F, 0.8F);
			GL11.glTranslated(2, 0.5, 0);
			GL11.glRotatef(20F, 0F, 0F, 1F);
			GL11.glRotatef(mc.getSystemTime() / -10, 0F, 1F, 0F);
			GL11.glRotatef(-20F, 1F, 0F, 0F);
			RenderManager.instance.renderEntityWithPosYaw(mob, 0, 0, 0, 0F, 1F);
			GL11.glPopMatrix();
		} else if (type == ItemRenderType.EQUIPPED)
		{
			GL11.glPushMatrix();
			GL11.glScalef(0.8F, 0.8F, 0.8F);
			GL11.glTranslated(1, 0.5, 0);
			GL11.glRotatef(20F, 0F, 0F, 1F);
			GL11.glRotatef(mc.getSystemTime() / -10, 0F, 1F, 0F);
			GL11.glRotatef(-20F, 1F, 0F, 0F);
			RenderManager.instance.renderEntityWithPosYaw(mob, 0, 0, 0, 0F, 1F);
			GL11.glPopMatrix();

		} else
		{
			GL11.glPushMatrix();
			GL11.glScalef(1.5F, 1.5F, 1.5F);
			GL11.glRotatef(mc.getSystemTime() / -10, 0F, 1F, 0F);
			GL11.glRotatef(-20F, 1F, 0F, 0F);
			RenderManager.instance.renderEntityWithPosYaw(mob, 0, 0, 0, 0F, 0F);
			GL11.glPopMatrix();

		}

		//GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();

	}
}
