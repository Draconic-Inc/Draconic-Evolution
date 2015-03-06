package com.brandon3055.draconicevolution.client.gui.manual;

import net.minecraft.client.Minecraft;

import java.util.List;

/**
 * Created by Brandon on 20/09/2014.
 */
public class DescriptionPage extends  TutorialPage {
	private String rawDescription;
	public DescriptionPage(String name, PageCollection collection, String unlocalizedDescription) {
		super(name, collection, null);
		this.rawDescription = unlocalizedDescription;
		this.formattedDescription = new List[1];
	}

	@Override
	public void renderBackgroundLayer(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.renderBackgroundLayer(minecraft, offsetX, offsetY, mouseX, mouseY);
	}

	@Override
	public void drawScreen(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.drawScreen(minecraft, offsetX, offsetY, mouseX, mouseY);
		addDescription(minecraft, offsetX, offsetY-50, getFormattedText(fontRendererObj, ttl(rawDescription), 0));
	}
}
