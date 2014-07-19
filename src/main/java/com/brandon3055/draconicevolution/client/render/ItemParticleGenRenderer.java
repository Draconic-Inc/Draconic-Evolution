package com.brandon3055.draconicevolution.client.render;

import com.brandon3055.draconicevolution.common.tileentities.TileEnergyInfuser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

public class ItemParticleGenRenderer implements IItemRenderer
{
	TileEntitySpecialRenderer render;
	private TileEntity dummytile;
	
	public ItemParticleGenRenderer(TileEntitySpecialRenderer render, TileEntity dummy) {
	    this.render = render;
	    this.dummytile = dummy;
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		if (type == IItemRenderer.ItemRenderType.ENTITY)
			GL11.glTranslatef(-0.5F, 0.0F, -0.5F);
		this.dummytile.xCoord = 0;
		this.dummytile.yCoord = 0;
		this.dummytile.zCoord = 0;
		this.dummytile.setWorldObj(Minecraft.getMinecraft().theWorld);
		this.render.renderTileEntityAt(this.dummytile, 0.0D, 0.0D, 0.0D, 0.0F);
	}

	

}
