package com.brandon3055.draconicevolution.client.gui.guicomponents;

import com.brandon3055.brandonscore.client.gui.guicomponents.ComponentBase;
import com.brandon3055.draconicevolution.client.gui.componentguis.GUIToolConfig;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.brandonscore.common.utills.DataUtills;
import com.brandon3055.brandonscore.client.utills.GuiHelper;
import com.brandon3055.draconicevolution.common.utills.IConfigurableItem;
import com.brandon3055.draconicevolution.common.utills.ItemConfigField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Brandon on 1/01/2015.
 */
public class ComponentFieldAdjuster extends ComponentBase {
	private static final ResourceLocation widgets = new ResourceLocation(References.RESOURCESPREFIX + "textures/gui/Widgets.png");
	protected static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");

	public ItemConfigField field;
	public GUIToolConfig gui;

	public ComponentFieldAdjuster(int x, int y, ItemConfigField field, GUIToolConfig gui) {
		super(x, y);
		this.field = field;
		this.gui = gui;
	}

	@Override
	public int getWidth() {
		return 190;
	}

	@Override
	public int getHeight() {
		return 20;
	}

	@Override
	public void renderBackground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		if (field == null) return;
		minecraft.getTextureManager().bindTexture(buttonTextures);

		if (isBoolean())
		{
			renderButton((getWidth() / 2) - 30, 0, 60, 20, GuiHelper.isInRect(x + (getWidth() / 2) - 30, y, 60, 20, mouseX - offsetX, mouseY - offsetY));
		}else if (isDecimal() || isNonDecimal())
		{
			renderButton((getWidth() / 2) - 39, 3, 26, 14, GuiHelper.isInRect(x + (getWidth() / 2) - 39, y + 3, 26, 14, mouseX - offsetX, mouseY - offsetY));
			renderButton((getWidth() / 2) - 67, 3, 26, 14, GuiHelper.isInRect(x + (getWidth() / 2) - 67, y + 3, 26, 14, mouseX - offsetX, mouseY - offsetY));
			renderButton((getWidth() / 2) - 95, 3, 26, 14, GuiHelper.isInRect(x + (getWidth() / 2) - 95, y + 3, 26, 14, mouseX - offsetX, mouseY - offsetY));

			renderButton((getWidth() / 2) + 12, 3, 26, 14, GuiHelper.isInRect(x + (getWidth() / 2) + 12, y + 3, 26, 14, mouseX - offsetX, mouseY - offsetY));
			renderButton((getWidth() / 2) + 40, 3, 26, 14, GuiHelper.isInRect(x + (getWidth() / 2) + 40, y + 3, 26, 14, mouseX - offsetX, mouseY - offsetY));
			renderButton((getWidth() / 2) + 68, 3, 26, 14, GuiHelper.isInRect(x + (getWidth() / 2) + 68, y + 3, 26, 14, mouseX - offsetX, mouseY - offsetY));
		}
	}

	@Override
	public void renderForground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		if (field == null) return;
		String fieldName = field.getLocalizedName();
		String fieldValue = field.getFormatedValue();
		if (field.datatype == References.DOUBLE_ID) {
			double d = (Double)field.value;
			fieldValue = String.valueOf((double)Math.round(d*100f) / 100D);
		}
		if (field.datatype == References.FLOAT_ID) {
			float d = (Float)field.value;
			fieldValue = String.valueOf((double)Math.round((double)(d*100f)) / 100D);
		}

		int centre = fontRendererObj.getStringWidth(fieldName) / 2;
		int centre2 = fontRendererObj.getStringWidth(fieldValue) / 2;
		fontRendererObj.drawString(fieldName, x + getWidth() / 2 - centre, y - 12, 0x00000);
		fontRendererObj.drawStringWithShadow(fieldValue, x + getWidth() / 2 - centre2, y + 6, 0xffffff);

		if (isDecimal() || isNonDecimal())
		{
			fontRendererObj.drawString("---", 8, y + 6, 0x000000);
			fontRendererObj.drawString("--", 40, y + 6, 0x000000);
			fontRendererObj.drawString("-", 70, y + 6, 0x000000);
			fontRendererObj.drawString("+", 121, y + 6, 0x000000);
			fontRendererObj.drawString("++", 147, y + 6, 0x000000);
			fontRendererObj.drawString("+++", 172, y + 6, 0x000000);

		}
	}

	@Override
	public void renderFinal(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		if (field == null) return;
	}

	@Override
	public void mouseClicked(int x, int y, int button) {
		if (field == null) return;

		if (!isBoolean())
		{
			if (GuiHelper.isInRect(this.x + (getWidth() / 2) - 39, this.y + 3, 26, 14, x, y))
			{//-
				incroment(-1);
			} else if (GuiHelper.isInRect(this.x + (getWidth() / 2) - 67, this.y + 3, 26, 14, x, y))
			{//--
				incroment(-10);
			} else if (GuiHelper.isInRect(this.x + (getWidth() / 2) - 95, this.y + 3, 26, 14, x, y))
			{//---
				incroment(-100);
			} else if (GuiHelper.isInRect(this.x + (getWidth() / 2) + 12, this.y + 3, 26, 14, x, y))
			{//+
				incroment(1);
			} else if (GuiHelper.isInRect(this.x + (getWidth() / 2) + 40, this.y + 3, 26, 14, x, y))
			{//++
				incroment(10);
			} else if (GuiHelper.isInRect(this.x + (getWidth() / 2) + 68, this.y + 3, 26, 14, x, y))
			{//+++
				incroment(100);
			}
		}
		else if (GuiHelper.isInRect(this.x + (getWidth() / 2) - 30, this.y, 60, 20, x, y)){
			incroment(1);
		}
	}

	private void incroment(int multiplyer){
		switch (field.datatype){
			case References.BYTE_ID:
				byte b = (Byte)field.value;
				b += (Byte)field.incroment * (byte)multiplyer;
				if (b > (Byte)field.max) b = (Byte) field.max;
				if (b < (Byte)field.min) b = (Byte) field.min;
				field.value = b;
				break;
			case References.SHORT_ID:
				short s = (Short)field.value;
				s += (Short)field.incroment * (short)multiplyer;
				if (s > (Short)field.max) s = (Short) field.max;
				if (s < (Short)field.min) s = (Short) field.min;
				field.value = s;
				break;
			case References.INT_ID:
				int i = (Integer)field.value;
				i += (Integer)field.incroment * multiplyer;
				if (i > (Integer)field.max) i = (Integer) field.max;
				if (i < (Integer)field.min) i = (Integer) field.min;
				field.value = i;
				break;
			case References.LONG_ID:
				long l = (Long)field.value;
				l += (Long)field.incroment * (long)multiplyer;
				if (l > (Long)field.max) l = (Long) field.max;
				if (l < (Long)field.min) l = (Long) field.min;
				field.value = l;
				break;
			case References.FLOAT_ID:
				float f = (Float)field.value;
				f += (Float)field.incroment * (float)multiplyer;
				f *= 100F;
				f = Math.round(f);
				f /= 100F;
				if (f > (Float)field.max) f = (Float) field.max;
				if (f < (Float)field.min) f = (Float) field.min;
				field.value = f;
				break;
			case References.DOUBLE_ID:
				double d = (Double)field.value;
				d += (Double)field.incroment * (double)multiplyer;
				if (d > (Double)field.max) d = (Double) field.max;
				if (d < (Double)field.min) d = (Double) field.min;
				field.value = d;
				break;
			case References.BOOLEAN_ID:
				field.value = !(Boolean)field.value;
				break;
		}
		Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
		ItemStack stack = gui.player.inventory.getStackInSlot(field.slot);
		if (stack != null && stack.getItem() instanceof IConfigurableItem){
			DataUtills.writeObjectToItem(stack, field.value, field.datatype, field.name);
		}
		field.sendChanges();
	}

	public void renderButton(int offsetX, int offsetY, int xSize, int ySize, boolean highlighted){
		int k = highlighted ? 2 : 1;
		int x = this.x + offsetX;
		int y = this.y + offsetY;

		this.drawTexturedModalRect(x, y, 0, 46 + k * 20, xSize /2, 20 - Math.max(0, 20 - ySize));
		this.drawTexturedModalRect(x + xSize / 2, y, 200 - xSize / 2, 46 + k * 20, xSize / 2, 20 - Math.max(0, 20-ySize));

		if (ySize < 20){
			this.drawTexturedModalRect(x, y+3, 0, (46 + k * 20)+20-ySize+3, xSize -1, ySize-3);
			this.drawTexturedModalRect(x + xSize / 2, y+3, 200 - xSize / 2, (46 + k * 20)+20-ySize+3, xSize / 2, ySize-3);
		}
	}

	private boolean isBoolean(){ return field.datatype == References.BOOLEAN_ID; }

	private boolean isNonDecimal(){ return field.datatype == References.INT_ID || field.datatype == References.SHORT_ID || field.datatype == References.BYTE_ID; }

	private boolean isDecimal(){ return field.datatype == References.FLOAT_ID || field.datatype == References.DOUBLE_ID; }
}
