package com.brandon3055.draconicevolution.client.gui.manual;

import com.brandon3055.draconicevolution.common.utills.LogHelper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brandon on 18/09/2014.
 */
public class TutorialPage extends TitledPage {
	private float descriptionScale = 0.66f;
	public String[] rawDescription;
	public List<String>[] formattedDescription;
	public int page = 0;
	public int lastPage = 0;

	public TutorialPage(String name, PageCollection collection, String title) {
		super(name, true, collection, title, 0x00ff00);
	}

	@Override
	public void renderBackgroundLayer(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.renderBackgroundLayer(minecraft, offsetX, offsetY, mouseX, mouseY);
		if (lastPage == 0) return;
		String s = "page " + page + "/" + lastPage;
		fontRendererObj.drawString(s, offsetX+195, offsetY+185, 0x000000);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		super.initGui();
		if (lastPage == 0) return;
		buttonList.add(new GuiButtonAHeight(1, getXMin()+62, getYMin()+181, 25, 16, ttl("button.de.previous.txt")));
		buttonList.add(new GuiButtonAHeight(2, getXMin()+170, getYMin()+181, 25, 16, ttl("button.de.next.txt")));
		if (buttonList.get(1) instanceof GuiButton) ((GuiButton)buttonList.get(1)).enabled = !(page == 0);
		if (buttonList.get(2) instanceof GuiButton) ((GuiButton)buttonList.get(2)).enabled = !(page == lastPage);
	}

	public void drawScreen(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.drawScreen(minecraft, offsetX, offsetY, mouseX, mouseY);

	}

	public void addDescription(Minecraft minecraft, int offsetX, int offsetY, List<String> description){
		GL11.glPushMatrix();
		GL11.glTranslated(offsetX+5, offsetY+75, 1);
		GL11.glScalef(descriptionScale, descriptionScale, descriptionScale);
		int offset = 0;
		for (String s : description) {
			if (s == null) break;
			fontRendererObj.drawString(s, 0, offset, 0x000000);
			offset += fontRendererObj.FONT_HEIGHT;
		}
		GL11.glPopMatrix();
	}

	@SuppressWarnings("unchecked")
	public List<String> getFormattedText(FontRenderer fr, String rawDescription, int descriptionIndex) {
		if (formattedDescription[descriptionIndex] == null) {

			formattedDescription[descriptionIndex] = new ArrayList<String>();

			if (Strings.isNullOrEmpty(rawDescription)) {
				formattedDescription[descriptionIndex] = ImmutableList.of();
				return formattedDescription[descriptionIndex];
			}
			if (!rawDescription.contains("\\n")) {
				formattedDescription[descriptionIndex] = ImmutableList.copyOf(fr.listFormattedStringToWidth(rawDescription, 370));
				return formattedDescription[descriptionIndex];
			}

			List<String> segments = new ArrayList(); //Each separate string that is separated by a \n
			String raw = rawDescription;


			int escape = 0;
			while (raw.contains("\\n")) {
				segments.add(raw.substring(0, raw.indexOf("\\n")));
				raw = raw.substring(raw.indexOf("\\n") + 2);
				if (!raw.contains("\\n")) segments.add(raw);

				escape++;
				if (escape > 100) {
					LogHelper.error("Bailing Out!");
					break;
				}
			}

			for (String s : segments)
				formattedDescription[descriptionIndex].addAll(ImmutableList.copyOf(fr.listFormattedStringToWidth(s, 370)));
		}
		return formattedDescription[descriptionIndex];
	}

	@Override
	public void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		if (lastPage == 0) return;
		if (button.id == 1) page = Math.max(0, page-1);
		if (button.id == 2) page = Math.min(lastPage, page+1);
		if (buttonList.get(1) instanceof GuiButton) ((GuiButton)buttonList.get(1)).enabled = !(page == 0);
		if (buttonList.get(2) instanceof GuiButton) ((GuiButton)buttonList.get(2)).enabled = !(page == lastPage);
	}
}
