package com.brandon3055.draconicevolution.client.interfaces.manual;

import com.brandon3055.draconicevolution.common.blocks.ModBlocks;
import com.brandon3055.draconicevolution.common.core.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.common.items.ModItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 * Created by Brandon on 16/09/2014.
 */
@SideOnly(Side.CLIENT)
public class GuiManual extends GuiContainer {

	protected final PageCollection root;
	protected int pageIndex = 0;

	public GuiManual() {
		super(new DummyContainer());
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

		pageCollection.addPage(new CraftingPage("CRAFTING_PAGE_"+getNextPageIndex(), pageCollection, new ItemStack(ModBlocks.draconiumOre), ""));
		pageCollection.addPage(new CraftingPage("CRAFTING_PAGE_"+getNextPageIndex(), pageCollection, new ItemStack(ModItems.draconiumDust), ""));
		pageCollection.addPage(new CraftingPage("CRAFTING_PAGE_"+getNextPageIndex(), pageCollection, new ItemStack(ModItems.draconiumBlend), ""));
		pageCollection.addPage(new CraftingPage("CRAFTING_PAGE_"+getNextPageIndex(), pageCollection, new ItemStack(ModItems.draconiumIngot, 2), ""));
		pageCollection.addPage(new CraftingPage("CRAFTING_PAGE_"+getNextPageIndex(), pageCollection, new ItemStack(ModItems.draconicCore), ""));
		pageCollection.addPage(new CraftingPage("CRAFTING_PAGE_"+getNextPageIndex(), pageCollection, new ItemStack(ModItems.infusedCompound), ""));
		pageCollection.addPage(new CraftingPage("CRAFTING_PAGE_"+getNextPageIndex(), pageCollection, new ItemStack(ModItems.dragonHeart), ""));
		pageCollection.addPage(new CraftingPage("CRAFTING_PAGE_"+getNextPageIndex(), pageCollection, new ItemStack(ModItems.draconicCompound, 2), ""));
		pageCollection.addPage(new CraftingPage("CRAFTING_PAGE_"+getNextPageIndex(), pageCollection, new ItemStack(ModItems.sunFocus), ""));

		pageCollection.addPage(new CraftingPage("CRAFTING_PAGE_"+getNextPageIndex(), pageCollection, new ItemStack(ModItems.wyvernPickaxe), ""));
		pageCollection.addPage(new CraftingPage("CRAFTING_PAGE_"+getNextPageIndex(), pageCollection, new ItemStack(ModItems.wyvernShovel), ""));
		pageCollection.addPage(new CraftingPage("CRAFTING_PAGE_"+getNextPageIndex(), pageCollection, new ItemStack(ModItems.wyvernSword), ""));
		pageCollection.addPage(new CraftingPage("CRAFTING_PAGE_"+getNextPageIndex(), pageCollection, new ItemStack(ModItems.wyvernBow), ""));

		pageCollection.addPage(new CraftingPage("CRAFTING_PAGE_"+getNextPageIndex(), pageCollection, new ItemStack(ModItems.draconicPickaxe), ""));
		pageCollection.addPage(new CraftingPage("CRAFTING_PAGE_"+getNextPageIndex(), pageCollection, new ItemStack(ModItems.draconicShovel), ""));
		pageCollection.addPage(new CraftingPage("CRAFTING_PAGE_"+getNextPageIndex(), pageCollection, new ItemStack(ModItems.draconicHoe), ""));
		pageCollection.addPage(new CraftingPage("CRAFTING_PAGE_"+getNextPageIndex(), pageCollection, new ItemStack(ModItems.draconicAxe), ""));
		pageCollection.addPage(new CraftingPage("CRAFTING_PAGE_"+getNextPageIndex(), pageCollection, new ItemStack(ModItems.draconicSword), ""));
		pageCollection.addPage(new CraftingPage("CRAFTING_PAGE_"+getNextPageIndex(), pageCollection, new ItemStack(ModItems.draconicBow), ""));
		pageCollection.addPage(new CraftingPage("CRAFTING_PAGE_"+getNextPageIndex(), pageCollection, new ItemStack(ModItems.draconicDistructionStaff), ""));

		pageCollection.addPage(new CraftingPage("CRAFTING_PAGE_"+getNextPageIndex(), pageCollection, new ItemStack(ModItems.wyvernHelm), ""));
		pageCollection.addPage(new CraftingPage("CRAFTING_PAGE_"+getNextPageIndex(), pageCollection, new ItemStack(ModItems.wyvernChest), ""));
		pageCollection.addPage(new CraftingPage("CRAFTING_PAGE_"+getNextPageIndex(), pageCollection, new ItemStack(ModItems.wyvernLeggs), ""));
		pageCollection.addPage(new CraftingPage("CRAFTING_PAGE_"+getNextPageIndex(), pageCollection, new ItemStack(ModItems.wyvernBoots), ""));
		pageCollection.addPage(new CraftingPage("CRAFTING_PAGE_"+getNextPageIndex(), pageCollection, new ItemStack(ModItems.draconicHelm), ""));
		pageCollection.addPage(new CraftingPage("CRAFTING_PAGE_"+getNextPageIndex(), pageCollection, new ItemStack(ModItems.draconicChest), ""));
		pageCollection.addPage(new CraftingPage("CRAFTING_PAGE_"+getNextPageIndex(), pageCollection, new ItemStack(ModItems.draconicLeggs), ""));
		pageCollection.addPage(new CraftingPage("CRAFTING_PAGE_"+getNextPageIndex(), pageCollection, new ItemStack(ModItems.draconicBoots), ""));

		pageCollection.addPage(new CraftingPage("CRAFTING_PAGE_"+getNextPageIndex(), pageCollection, new ItemStack(ModItems.teleporterMKI), ""));
		pageCollection.addPage(new CraftingPage("CRAFTING_PAGE_"+getNextPageIndex(), pageCollection, new ItemStack(ModItems.teleporterMKII), ""));
		pageCollection.addPage(new CraftingPage("CRAFTING_PAGE_"+getNextPageIndex(), pageCollection, new ItemStack(ModItems.enderArrow), ""));
		pageCollection.addPage(new CraftingPage("CRAFTING_PAGE_"+getNextPageIndex(), pageCollection, new ItemStack(ModItems.safetyMatch), ""));
		pageCollection.addPage(new CraftingPage("CRAFTING_PAGE_"+getNextPageIndex(), pageCollection, mobSoul, ""));


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

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int mouseX, int mouseY) {
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
	protected void actionPerformed(GuiButton button) {
		root.actionPerformed(button);
	}

	@Override
	protected void mouseMovedOrUp(int par1, int par2, int par3){
		root.mouseMovedOrUp(par1, par2, par3);
	}

	@Override
	protected void mouseClicked(int par1, int par2, int par3){
		root.mouseClicked(par1, par2, par3);
	}

	@Override
	public void handleInput() {
		super.handleInput();
	}
}

