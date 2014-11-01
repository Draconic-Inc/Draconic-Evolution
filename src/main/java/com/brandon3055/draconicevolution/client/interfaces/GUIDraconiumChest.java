package com.brandon3055.draconicevolution.client.interfaces;

import codechicken.nei.VisiblityData;
import codechicken.nei.api.INEIGuiHandler;
import codechicken.nei.api.TaggedInventoryArea;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.interfaces.manual.GuiButtonAHeight;
import com.brandon3055.draconicevolution.common.container.ContainerDraconiumChest;
import com.brandon3055.draconicevolution.common.core.network.ButtonPacket;
import com.brandon3055.draconicevolution.common.core.utills.GuiHelper;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.tileentities.TileDraconiumChest;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SideOnly(Side.CLIENT)
@Optional.Interface(iface = "codechicken.nei.api.INEIGuiHandler", modid = "NotEnoughItems")
public class GUIDraconiumChest extends GuiContainer implements INEIGuiHandler {
//todo unify gui headings
	public EntityPlayer player;
	private TileDraconiumChest tile;
	private static final ResourceLocation textureLeft = new ResourceLocation(References.MODID.toLowerCase(), "textures/gui/DraconicChestLeft.png");
	private static final ResourceLocation textureRight = new ResourceLocation(References.MODID.toLowerCase(), "textures/gui/DraconicChestRight.png");
	private boolean lastAutoFeed;

	public GUIDraconiumChest(InventoryPlayer invPlayer, TileDraconiumChest tile) {
		super(new ContainerDraconiumChest(invPlayer, tile));

		xSize = 481;
		ySize = 256;

		this.tile = tile;
		this.player = invPlayer.player;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
		GL11.glColor4f(1F, 1F, 1F, 1F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(textureRight);
		drawTexturedModalRect(guiLeft + 256, guiTop, 0, 0, 225, ySize);
		Minecraft.getMinecraft().getTextureManager().bindTexture(textureLeft);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, 256, ySize);
		drawTexturedModalRect(guiLeft+140, guiTop+216, 3, 176, 16, 23);
		drawTexturedModalRect(guiLeft+387, guiTop+236, 44, 177, 90, 16);
		drawTexturedModalRect(guiLeft+387, guiTop+180, 44, 177, 90, 16);

		int arrowHight = (int)(((float)tile.smeltingProgressTime / (float)tile.smeltingCompleateTime) * 22f);//todo maby need adjustment
		drawTexturedModalRect(guiLeft+140, guiTop+192+22-arrowHight, 140, 216+22-arrowHight, 16, arrowHight);

		Minecraft.getMinecraft().getTextureManager().bindTexture(textureRight);
		int energyWidth = (int)(((float)tile.getEnergyStored(ForgeDirection.DOWN) / (float)tile.getMaxEnergyStored(ForgeDirection.DOWN)) * 90f);
		drawTexturedModalRect(guiLeft+44, guiTop+235, 131, 236, energyWidth, 16);

		int flameHight = (int)(((float)tile.smeltingBurnSpeed / (float)tile.smeltingMaxBurnSpeed) * 13f);
		//flameHight = tile.smeltingProgressTime <= 0 ? 0 : Math.min(flameHight, 13);
		drawTexturedModalRect(guiLeft + 45, guiTop + 217 + 13 - flameHight, 132, 180 + 13 - flameHight, 88, flameHight);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		fontRendererObj.drawStringWithShadow(StatCollector.translateToLocal("tile.draconicevolution:draconiumChest.name"), 4, 4, 0x00FFFF);

		ArrayList<String> list = new ArrayList<String>();
		list.add(String.valueOf(tile.getEnergyStored(ForgeDirection.DOWN)) + "/" + String.valueOf(tile.getMaxEnergyStored(ForgeDirection.DOWN)) + "RF");
		if (GuiHelper.isInRect(44, 235, 90, 16, x-guiLeft, y-guiTop))drawHoveringText(list, x-guiLeft, y-guiTop, fontRendererObj);
		RenderHelper.enableGUIStandardItemLighting();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		super.initGui();
		int posX = (this.width - xSize) / 2;
		int posY = (this.height - ySize) / 2;
		buttonList.clear();
		buttonList.add(new GuiButtonAHeight(0, posX+47, posY+180, 85, 12, "AutoFeed "+(tile.smeltingAutoFeed ? "on" : "off")));
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		DraconicEvolution.network.sendToServer(new ButtonPacket(ButtonPacket.ID_DRACONIUMCHEST, false));
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (lastAutoFeed != tile.smeltingAutoFeed){
			lastAutoFeed = tile.smeltingAutoFeed;
			((GuiButton)buttonList.get(0)).displayString = "AutoFeed "+(tile.smeltingAutoFeed ? "on" : "off");
		}
	}

	@Override
	public VisiblityData modifyVisiblity(GuiContainer guiContainer, VisiblityData visiblityData) {
		return null;
	}

	@Override
	public Iterable<Integer> getItemSpawnSlots(GuiContainer guiContainer, ItemStack stack) {
		return null;
	}

	@Override
	public List<TaggedInventoryArea> getInventoryAreas(GuiContainer guiContainer) {
		return Collections.emptyList();
	}

	@Override
	public boolean handleDragNDrop(GuiContainer guiContainer, int i, int i2, ItemStack stack, int i3) {
		return false;
	}

	@Override
	public boolean hideItemPanelSlot(GuiContainer guiContainer, int i, int i2, int i3, int i4) {
		return false;
	}
}
