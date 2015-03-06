package com.brandon3055.draconicevolution.client.gui.manual;

import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.handler.ConfigHandler;
import com.brandon3055.draconicevolution.common.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.common.ModItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 * Created by Brandon on 16/09/2014.
 */
@SideOnly(Side.CLIENT)
public class GuiManual extends GuiScreen {

	protected final PageCollection root;
	protected int pageIndex = 0;
	protected int xSize = 0;
	protected int ySize = 0;
	public Container inventorySlots;
	protected int guiLeft;
	protected int guiTop;

	public GuiManual() {
		this.xSize = 256;
		this.ySize = 202;
		root = createRoot();
	}

	protected PageCollection createRoot() {
		ItemStack mobSoul = new ItemStack(ModItems.mobSoul);
		ItemNBTHelper.setString(mobSoul, "Name", "Any");
		pageIndex = 0;
		final PageCollection pageCollection = new PageCollection();
		pageCollection.addPage(new IntroPage("INTRO", pageCollection));
		//pageCollection.addPage(new TitledPage("TITLED_PAGE_1", true, pageCollection, "manual.de.Page Title.txt", 0xff0000).setIndexName("manual.de.TitledPage.txt"));

		pageCollection.addPage(new CraftingInfoPage("BLOCK_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModBlocks.draconiumOre), ""));
		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModItems.draconiumDust), ""));
		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModItems.draconiumBlend), ""));
		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModItems.draconiumIngot, 2), ""));
		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModItems.draconicCore), ""));
		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModItems.wyvernCore), ""));
		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModItems.dragonHeart), ""));
		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModItems.awakenedCore), ""));
		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, ModItems.wyvernEnergyCore, ""));
		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, ModItems.draconicEnergyCore, ""));
//		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModItems.sunFocus), ""));

		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModItems.wyvernPickaxe), ""));
		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModItems.wyvernShovel), ""));
		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModItems.wyvernSword), ""));
		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModItems.wyvernBow), ""));
		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, ModItems.wyvernFluxCapacitor, ""));

		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModItems.draconicPickaxe), ""));
		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModItems.draconicShovel), ""));
		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModItems.draconicHoe), ""));
		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModItems.draconicAxe), ""));
		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModItems.draconicSword), ""));
		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModItems.draconicBow), ""));
		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModItems.draconicDestructionStaff), ""));
		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, ModItems.draconicFluxCapacitor, ""));

		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModItems.wyvernHelm), ""));
		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModItems.wyvernChest), ""));
		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModItems.wyvernLeggs), ""));
		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModItems.wyvernBoots), ""));
		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModItems.draconicHelm), ""));
		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModItems.draconicChest), ""));
		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModItems.draconicLeggs), ""));
		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModItems.draconicBoots), ""));

		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModItems.teleporterMKI), ""));
		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModItems.teleporterMKII), ""));
		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModItems.enderArrow), ""));
		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModItems.safetyMatch), ""));
		pageCollection.addPage(new CraftingInfoPage("ITEM_PAGE."+getNextPageIndex(), pageCollection, mobSoul, ""));

		pageCollection.addPage(new CraftingInfoPage("BLOCK_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModBlocks.weatherController), ""));
		if (ConfigHandler.disableSunDial != 2) pageCollection.addPage(new CraftingInfoPage("BLOCK_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModBlocks.sunDial), ""));
		pageCollection.addPage(new CraftingInfoPage("BLOCK_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModBlocks.playerDetector), ""));
		pageCollection.addPage(new CraftingInfoPage("BLOCK_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModBlocks.playerDetectorAdvanced), ""));
		pageCollection.addPage(new CraftingInfoPage("BLOCK_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModBlocks.grinder, 1, 3), ""));
		pageCollection.addPage(new CraftingInfoPage("BLOCK_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModBlocks.energyInfuser), ""));
		pageCollection.addPage(new CraftingInfoPage("BLOCK_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModBlocks.customSpawner), ""));
		pageCollection.addPage(new CraftingInfoPage("BLOCK_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModBlocks.generator, 1, 3), ""));
		pageCollection.addPage(new CraftingInfoPage("BLOCK_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModBlocks.dissEnchanter), ""));
		pageCollection.addPage(new CraftingInfoPage("BLOCK_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModBlocks.teleporterStand), ""));
		pageCollection.addPage(new CraftingInfoPage("BLOCK_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModBlocks.draconiumChest), ""));

		pageCollection.addPage(new CraftingInfoPage("BLOCK_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModBlocks.particleGenerator), ""));
		if (ConfigHandler.disableXrayBlock != 2) pageCollection.addPage(new CraftingInfoPage("BLOCK_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModBlocks.xRayBlock), ""));
		pageCollection.addPage(new CraftingInfoPage("BLOCK_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModBlocks.potentiometer), ""));
		pageCollection.addPage(new CraftingInfoPage("BLOCK_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModBlocks.rainSensor), ""));
		pageCollection.addPage(new CraftingInfoPage("BLOCK_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModBlocks.draconiumBlock), ""));
		pageCollection.addPage(new CraftingInfoPage("BLOCK_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModBlocks.draconicBlock), ""));
		pageCollection.addPage(new CraftingInfoPage("BLOCK_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModBlocks.energyStorageCore), ""));
		pageCollection.addPage(new CraftingInfoPage("BLOCK_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModBlocks.energyPylon), ""));
		pageCollection.addPage(new CraftingInfoPage("BLOCK_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModBlocks.draconiumBlock, 1, 1), ""));
		pageCollection.addPage(new CraftingInfoPage("BLOCK_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModBlocks.draconiumBlock, 1, 2), ""));
		pageCollection.addPage(new CraftingInfoPage("BLOCK_PAGE."+getNextPageIndex(), pageCollection, new ItemStack(ModBlocks.longRangeDislocator, 1, 1), ""));

		pageCollection.addPage(new EnergyCoreTutorialPage("TUTORIAL."+getNextPageIndex(), pageCollection).setIndexName(StatCollector.translateToLocal("manual.de.ecth.txt")));
		pageCollection.addPage(new EnderResurrectionTutorialPage("TUTORIAL."+getNextPageIndex(), pageCollection).setIndexName("manual.de.roerth.txt"));
		pageCollection.addPage(new DescriptionPage("INFO."+getNextPageIndex(), pageCollection, "manual.de.endercomet.description").setIndexName("manual.de.endercomet.txt"));
		pageCollection.addPage(new DescriptionPage("INFO."+getNextPageIndex(), pageCollection, "manual.de.chaosisland.description").setIndexName("manual.de.enderisland.txt"));
		pageCollection.addPage(new DescriptionPage("INFO."+getNextPageIndex(), pageCollection, "manual.de.placeditems.description").setIndexName("manual.de.placeditems.txt"));
		pageCollection.addPage(new DescriptionPage("INFO."+getNextPageIndex(), pageCollection, "manual.de.pigmenBloodRage.description").setIndexName("manual.de.pigmenBloodRage.txt"));

		pageCollection.addPage(new IndexPage("INDEX", pageCollection));
		return pageCollection;
	}

	private int getNextPageIndex(){
		int i = pageIndex;
		pageIndex++;
		return i;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float par3) {
		drawGuiBackgroundLayer(par3, mouseX, mouseY);
		super.drawScreen(mouseX, mouseY, par3);

		prepareRenderState();
		GL11.glPushMatrix();

		root.drawScreen(this.mc, this.guiLeft, this.guiTop, mouseX - this.guiLeft, mouseY - this.guiTop);

		GL11.glPopMatrix();
		restoreRenderState();
	}

	protected void prepareRenderState() {
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}

	protected void restoreRenderState() {
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		RenderHelper.enableStandardItemLighting();
	}

	protected void drawGuiBackgroundLayer(float p_146976_1_, int mouseX, int mouseY) {
		GL11.glPushMatrix();
		GL11.glTranslated(this.guiLeft, this.guiTop, 0);
		root.renderBackgroundLayer(this.mc, 0, 0, mouseX - this.guiLeft, mouseY - this.guiTop);
		GL11.glPopMatrix();
	}

	@Override
	public void setWorldAndResolution(Minecraft minecraft, int x, int y) {
		super.setWorldAndResolution(minecraft, x, y);
		root.setWorldAndResolution(minecraft, x, y);
	}

	@Override
	public void actionPerformed(GuiButton button) {
		root.actionPerformed(button);
	}

	@Override
	public void mouseMovedOrUp(int par1, int par2, int par3){
		root.mouseMovedOrUp(par1, par2, par3);
	}

	@Override
	public void mouseClicked(int par1, int par2, int par3){
		root.mouseClicked(par1, par2, par3);
	}

	@Override
	public void handleInput() {
		super.handleInput();
	}

	@Override
	public void initGui() {
		super.initGui();
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
	}
}

