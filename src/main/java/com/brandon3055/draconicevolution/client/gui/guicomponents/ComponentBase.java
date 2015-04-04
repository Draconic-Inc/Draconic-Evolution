package com.brandon3055.draconicevolution.client.gui.guicomponents;

/**
 * Created by Brandon on 25/12/2014.
 * This gui system is based on open blocks gui system
 */

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.Iterator;
import java.util.List;

public abstract class ComponentBase extends Gui {

	protected static RenderItem itemRender = new RenderItem();
	protected int x;
	protected int y;
	protected boolean enabled = true;
	public static Minecraft mc;
	public FontRenderer fontRendererObj;
	private String group = "";
	/** The width of the screen object. */
	public int width;
	/** The height of the screen object. */
	public int height;
	public String name;

	public ComponentBase(int x, int y) {
		this.x = x;
		this.y = y;
		mc = Minecraft.getMinecraft();
		fontRendererObj = mc.fontRenderer;
	}

	public String getGroup() {
		return group;
	}

	public ComponentBase setGroup(String group) {
		this.group = group;
		return this;
	}

	public ComponentBase setName(String name) {
		this.name = name;
		return this;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public abstract int getWidth();

	public abstract int getHeight();

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isMouseOver(int mouseX, int mouseY) {
		return mouseX >= x && mouseX < x + getWidth() && mouseY >= y && mouseY < y + getHeight();
	}

	public abstract void renderBackground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY);

	public abstract void renderForground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY);

	public void renderFinal(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {}

	protected void drawHoveringText(List list, int x, int y, FontRenderer font)
	{
		if (!list.isEmpty())
		{
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
//			RenderHelper.disableStandardItemLighting();
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			int k = 0;
			Iterator iterator = list.iterator();

			while (iterator.hasNext())
			{
				String s = (String)iterator.next();
				int l = font.getStringWidth(s);

				if (l > k)
				{
					k = l;
				}
			}

			int adjX = x + 12;
			int adjY = y - 12;
			int i1 = 8;

			if (list.size() > 1)
			{
				i1 += 2 + (list.size() - 1) * 10;
			}

			if (adjX + k > width)
			{
				adjX -= 28 + k;
			}

			if (adjY + i1 + 6 > height)
			{
				adjY = height - i1 - 6;
			}

			this.zLevel = 300.0F;
			itemRender.zLevel = 300.0F;
			int j1 = -267386864;
			this.drawGradientRect(adjX - 3, adjY - 4, adjX + k + 3, adjY - 3, j1, j1);
			this.drawGradientRect(adjX - 3, adjY + i1 + 3, adjX + k + 3, adjY + i1 + 4, j1, j1);
			this.drawGradientRect(adjX - 3, adjY - 3, adjX + k + 3, adjY + i1 + 3, j1, j1);
			this.drawGradientRect(adjX - 4, adjY - 3, adjX - 3, adjY + i1 + 3, j1, j1);
			this.drawGradientRect(adjX + k + 3, adjY - 3, adjX + k + 4, adjY + i1 + 3, j1, j1);
			int k1 = 1347420415;
			int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
			this.drawGradientRect(adjX - 3, adjY - 3 + 1, adjX - 3 + 1, adjY + i1 + 3 - 1, k1, l1);
			this.drawGradientRect(adjX + k + 2, adjY - 3 + 1, adjX + k + 3, adjY + i1 + 3 - 1, k1, l1);
			this.drawGradientRect(adjX - 3, adjY - 3, adjX + k + 3, adjY - 3 + 1, k1, k1);
			this.drawGradientRect(adjX - 3, adjY + i1 + 2, adjX + k + 3, adjY + i1 + 3, l1, l1);

			for (int i2 = 0; i2 < list.size(); ++i2)
			{
				String s1 = (String)list.get(i2);
				font.drawStringWithShadow(s1, adjX, adjY, -1);

				if (i2 == 0)
				{
					adjY += 2;
				}

				adjY += 10;
			}

			this.zLevel = 0.0F;
			itemRender.zLevel = 0.0F;
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
//			RenderHelper.enableStandardItemLighting();
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		}
	}

	public void drawItemStack(ItemStack stack, int x, int y, String count)
	{
		if (stack == null) return;
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		GL11.glTranslatef(0.0F, 0.0F, 32.0F);
		this.zLevel = 200.0F;
		itemRender.zLevel = 200.0F;
		FontRenderer font;
		font = stack.getItem().getFontRenderer(stack);
		if (font == null) font = fontRendererObj;
		itemRender.renderItemAndEffectIntoGUI(font, mc.getTextureManager(), stack, x, y);
		//itemRender.renderEffect(mc.getTextureManager(), x, y);
		if (!count.equals("null"))itemRender.renderItemOverlayIntoGUI(font, mc.getTextureManager(), stack, x, y, count);
		this.zLevel = 0.0F;
		itemRender.zLevel = 0.0F;

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	protected void renderToolTip(ItemStack stack, int x, int y)
	{
		List list = stack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);

		for (int k = 0; k < list.size(); ++k)
		{
			if (k == 0)
			{
				list.set(k, stack.getRarity().rarityColor + (String)list.get(k));
			}
			else
			{
				list.set(k, EnumChatFormatting.GRAY + (String)list.get(k));
			}
		}

		FontRenderer font = stack.getItem().getFontRenderer(stack);
		drawHoveringText(list, x, y, (font == null ? fontRendererObj : font));
	}

	public void mouseClicked(int x, int y, int button){
	}

	public void setWorldAndResolution(Minecraft mc, int width, int height)
	{

		this.mc = mc;
		this.fontRendererObj = mc.fontRenderer;
		this.width = width;
		this.height = height;
	}

	/**Translate To Local*/
	public String ttl(String unlocalizedName){return StatCollector.translateToLocal(unlocalizedName);}

	public void updateScreen(){}
}
