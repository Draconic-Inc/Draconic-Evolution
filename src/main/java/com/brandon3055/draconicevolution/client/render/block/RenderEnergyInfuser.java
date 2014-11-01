package com.brandon3055.draconicevolution.client.render.block;

import com.brandon3055.draconicevolution.common.tileentities.TileEnergyInfuser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class RenderEnergyInfuser implements IItemRenderer
{
	TileEntitySpecialRenderer render;
	private TileEnergyInfuser dummytile;

	public RenderEnergyInfuser(TileEntitySpecialRenderer render, TileEnergyInfuser dummy) {
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
		if (type == ItemRenderType.ENTITY)
			GL11.glTranslatef(-0.5F, 0.0F, -0.5F);
		dummytile.xCoord = 0;
		dummytile.yCoord = 0;
		dummytile.zCoord = 0;
		dummytile.setWorldObj(Minecraft.getMinecraft().theWorld);
		dummytile.rotation = 0F;
		dummytile.running = false;
		render.renderTileEntityAt(this.dummytile, 0.0D, 0.0D, 0.0D, 0.0F);
	}

	

}
