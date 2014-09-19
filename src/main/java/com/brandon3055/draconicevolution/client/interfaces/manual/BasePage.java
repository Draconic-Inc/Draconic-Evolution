package com.brandon3055.draconicevolution.client.interfaces.manual;

import com.brandon3055.draconicevolution.common.lib.References;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

/**
 * Created by Brandon on 16/09/2014.
 */
public class BasePage extends GuiScreen {
	/**Name used to reference the page*/
	private String REFERENCE_NAME;
	/**Name Displayed in the index page*/
	public String INDEX_NAME;
	public boolean hasIndexButton = false;
	public static final ResourceLocation PAGE_TEXTURE = new ResourceLocation(References.RESOURCESPREFIX + "textures/gui/InfoGui.png");
	private final int xSize = 256;
	private final int ySize = 202;
	protected PageCollection collection;

	public BasePage(String referenceName, PageCollection collection) {
		this.REFERENCE_NAME = referenceName;
		this.mc = Minecraft.getMinecraft();
		this.collection = collection;
		initGui();
	}

	public BasePage(String referenceName, boolean showInMenue, PageCollection collection) {
		this(referenceName, collection);
		this.hasIndexButton = showInMenue;
	}

	public int getXMin(){
		return (this.width - xSize) / 2;
	}

	public int getYMin(){
		return (this.height - ySize) / 2;
	}

	public void setXMin(int x){
		this.width = x;
	}

	public void setYMin(int y){
		this.height = y;
	}

	/**Unlocalized Index Page Name*/
	public BasePage setIndexName(String unlocalizedName){
		this.INDEX_NAME = ttl(unlocalizedName);
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		buttonList.clear();
		buttonList.add(new GuiButtonAHeight(0, getXMin()+88, getYMin()+181, 80, 16, ttl("button.de.backToIndex.txt")));
	}

	public void setReferenceName(String name){
		REFERENCE_NAME = name;}

	public String getReferenceName(){ return REFERENCE_NAME;}

	public void renderBackgroundLayer(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY){
		minecraft.renderEngine.bindTexture(PAGE_TEXTURE);
		drawTexturedModalRect(offsetX, offsetY, 0, 0, xSize, ySize);
	}

	public void renderOverlayComponents(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY){

	}

	public void drawScreen(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.drawScreen(mouseX+offsetX, mouseY+offsetY, 0);
		renderOverlayComponents(minecraft, offsetX, offsetY, mouseX, mouseY);
	}

	@Override
	 public void actionPerformed(GuiButton button) {
		collection.changeActivePage("INDEX");
	}
	@Override
	public void mouseMovedOrUp(int par1, int par2, int par3){super.mouseMovedOrUp(par1, par2, par3);}
	@Override
	public void mouseClicked(int par1, int par2, int par3){super.mouseClicked(par1, par2, par3);}

	/**Translate To Local*/
	public String ttl(String unlocalizedName){return StatCollector.translateToLocal(unlocalizedName);}
}
