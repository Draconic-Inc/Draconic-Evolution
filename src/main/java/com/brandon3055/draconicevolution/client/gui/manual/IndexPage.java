package com.brandon3055.draconicevolution.client.gui.manual;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

/**
 * Created by Brandon on 17/09/2014.
 */
public class IndexPage extends TitledPage {
	private int itemPageColour = 0xab373f;
	private int blockPageColour = 0x7729a7;
	private int tutorialPageColour = 0x000080;
	private int otherPageColour = 0x228B22;
	public IndexPage(String name, PageCollection collection) {
		super(name, false, collection, "manual.de.indexTitle.txt", 0xffa600);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		buttonList.clear();
		buttonList.add(new GuiButtonAHeight(0, getXMin() + 88, getYMin() + 181, 80, 16, ttl("button.de.back.txt")));
		int row = 0;
		int collum = 0;
		for (BasePage page : collection.pages){
			if (page.hasIndexButton){
				String indexName = page.INDEX_NAME;
				if (indexName==null && page instanceof CraftingInfoPage) indexName = ttl(((CraftingInfoPage)page).result.getUnlocalizedName()+".name");
				else if (indexName==null) indexName = page.getReferenceName();
				int colour = otherPageColour;
				if (page.getReferenceName() != null && page.getReferenceName().contains("ITEM_PAGE")) colour = itemPageColour;
				if (page.getReferenceName() != null && page.getReferenceName().contains("BLOCK_PAGE")) colour = blockPageColour;
				if (page.getReferenceName() != null && page.getReferenceName().contains("TUTORIAL")) colour = tutorialPageColour;

				buttonList.add(new GuiButtonTextOnly(999, getXMin()+5+collum*81, getYMin()+20+(row*7), 82, 7, indexName, page.getReferenceName(), colour));
				row++;
				if (row > 21){
					row = 0;
					collum++;
				}
			}
		}
	}

	@Override
	public void actionPerformed(GuiButton button) {
		switch (button.id) {
			case 0:	collection.changeActivePage("INTRO"); break;
		}

		if (button instanceof GuiButtonTextOnly)
			collection.changeActivePage(((GuiButtonTextOnly)button).LINKED_PAGE);
	}

	@Override
	public void renderBackgroundLayer(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.renderBackgroundLayer(minecraft, offsetX, offsetY, mouseX, mouseY);
		fontRendererObj.drawString("Items", offsetX+10, offsetY+179, itemPageColour);
		fontRendererObj.drawString("Blocks", offsetX+10, offsetY+189, blockPageColour);
		fontRendererObj.drawString("Tutorials", offsetX+200, offsetY+179, tutorialPageColour);
		fontRendererObj.drawString("Other", offsetX+200, offsetY+189, otherPageColour);
	}

	@Override
	public void drawScreen(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.drawScreen(minecraft, offsetX, offsetY, mouseX, mouseY);
		for (int k = 0; k < this.buttonList.size(); ++k)
		{
			if (buttonList.get(k) instanceof GuiButtonTextOnly && ((GuiButtonTextOnly) buttonList.get(k)).getIsHovering()) {
				((GuiButtonTextOnly) this.buttonList.get(k)).drawButton(this.mc, mouseX + offsetX, mouseY + offsetY);
			}
		}
	}
}
