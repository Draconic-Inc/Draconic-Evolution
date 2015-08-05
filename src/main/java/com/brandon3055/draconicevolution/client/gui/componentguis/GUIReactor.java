package com.brandon3055.draconicevolution.client.gui.componentguis;

import com.brandon3055.brandonscore.client.utills.GuiHelper;
import com.brandon3055.draconicevolution.client.gui.guicomponents.*;
import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.brandon3055.draconicevolution.common.container.ContainerReactor;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor.TileReactorCore;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 30/7/2015.
 */
public class GUIReactor extends GUIBase {
	private TileReactorCore reactor;
	private ContainerReactor container;

	public GUIReactor(EntityPlayer player, TileReactorCore reactor, ContainerReactor container) {
		super(container, 248, 222);
		this.reactor = reactor;
		this.container = container;
	}

	@Override
	protected ComponentCollection assembleComponents() {
		collection = new ComponentCollection(0, 0, 248, 222, this);
		collection.addComponent(new ComponentTexturedRect(0, 0, xSize, ySize, ResourceHandler.getResource("textures/gui/Reactor.png")));
		collection.addComponent(new ComponentTextureButton(14, 190, 18, 54, 18, 18, 0, this, "", StatCollector.translateToLocal("button.de.reactorStart.txt"), ResourceHandler.getResource("textures/gui/Widgets.png"))).setName("ACTIVATE");
		collection.addComponent(new ComponentTextureButton(216, 190, 18, 108, 18, 18, 1, this, "", StatCollector.translateToLocal("button.de.reactorStop.txt"), ResourceHandler.getResource("textures/gui/Widgets.png"))).setName("DEACTIVATE");
		return collection;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {//todo tool tips
		super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);
		//Draw I/O Slots
		if (reactor.reactorState == TileReactorCore.STATE_OFFLINE){
			RenderHelper.enableGUIStandardItemLighting();
			drawTexturedModalRect(guiLeft + 14, guiTop + 139, 14, ySize, 18, 18);
			drawTexturedModalRect(guiLeft + 216, guiTop + 139, 32, ySize, 18, 18);

			fontRendererObj.drawString(StatCollector.translateToLocal("gui.de.insert.txt"), guiLeft + 8, guiTop + 159, 0);
			fontRendererObj.drawString(StatCollector.translateToLocal("gui.de.fuel.txt"), guiLeft + 13, guiTop + 168, 0);

			fontRendererObj.drawString(StatCollector.translateToLocal("gui.de.extract.txt"), guiLeft + 206, guiTop + 159, 0);
			fontRendererObj.drawString(StatCollector.translateToLocal("gui.de.fuel.txt"), guiLeft + 215, guiTop + 168, 0);
		}
		drawCenteredString(fontRendererObj, "Draconic Reactor", guiLeft + xSize / 2, guiTop + 4, 0x00FFFF);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		ResourceHandler.bindResource("textures/gui/Reactor.png");

		//Draw Indicators
		double value = reactor.reactionTemperature / reactor.maxReactTemperature;
		int pixOffset = (int)(value * 108);
		drawTexturedModalRect(11, 112 - pixOffset, 0, 222, 14, 5);

		value = reactor.fieldCharge / reactor.maxFieldCharge;
		pixOffset = (int)(value * 108);
		drawTexturedModalRect(35, 112 - pixOffset, 0, 222, 14, 5);

		value = (double)reactor.energySaturation / (double)reactor.maxEnergySaturation;
		pixOffset = (int)(value * 108);
		drawTexturedModalRect(199, 112 - pixOffset, 0, 222, 14, 5);

		value = (double)reactor.convertedFuel / (double)reactor.reactorFuel;
		pixOffset = (int)(value * 108);
		drawTexturedModalRect(223, 112 - pixOffset, 0, 222, 14, 5);

		double fuelM2 = Math.round((double)reactor.reactorFuel / 1296D * 1000D) / 1000D;
		fontRendererObj.drawString(StatCollector.translateToLocal("gui.de.coreMass.txt") + ": " + fuelM2 + "m³", 5, 130, 0);
		String status = StatCollector.translateToLocal("gui.de.status.txt")+": " + (reactor.reactorState == 0 ? EnumChatFormatting.DARK_GRAY : reactor.reactorState == 1 ? EnumChatFormatting.RED : reactor.reactorState == 2 ? EnumChatFormatting.GREEN : EnumChatFormatting.RED) + StatCollector.translateToLocal("gui.de.status"+reactor.reactorState+".txt");
		fontRendererObj.drawString(status, xSize - 5 - fontRendererObj.getStringWidth(status), 130, 0);

	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float par3) {
		super.drawScreen(mouseX, mouseY, par3);
		List<String> text = new ArrayList<String>();
		if (GuiHelper.isInRect(9, 4, 18, 144, mouseX - guiLeft, mouseY - guiTop)){
			text.add(StatCollector.translateToLocal("gui.de.reactionTemp.txt"));
			text.add((int)reactor.reactionTemperature + "°C");
			drawHoveringText(text, mouseX, mouseY, fontRendererObj);
		}
		else if (GuiHelper.isInRect(33, 4, 18, 144, mouseX-guiLeft, mouseY-guiTop)){
			text.add(StatCollector.translateToLocal("gui.de.fieldStrength.txt"));
			if (reactor.maxFieldCharge > 0) text.add(reactor.fieldCharge / reactor.maxFieldCharge * 100D + "%");
			drawHoveringText(text, mouseX, mouseY, fontRendererObj);
		}
		else if (GuiHelper.isInRect(197, 4, 18, 144, mouseX-guiLeft, mouseY-guiTop)){
			text.add(StatCollector.translateToLocal("gui.de.energySaturation.txt"));
			if (reactor.maxEnergySaturation > 0) text.add((double)reactor.energySaturation / (double)reactor.maxEnergySaturation * 100D + "%");
			drawHoveringText(text, mouseX, mouseY, fontRendererObj);
		}
		else if (GuiHelper.isInRect(221, 4, 18, 144, mouseX-guiLeft, mouseY-guiTop)){
			text.add(StatCollector.translateToLocal("gui.de.fuelConversion.txt"));
			if (reactor.reactorFuel + reactor.convertedFuel > 0) text.add((double)reactor.convertedFuel / ((double)reactor.convertedFuel + (double)reactor.reactorFuel) * 100D + "%");
			drawHoveringText(text, mouseX, mouseY, fontRendererObj);
		}
	}

	@Override
	public void updateScreen() {
		if (reactor.reactorState == TileReactorCore.STATE_OFFLINE || reactor.reactorState == TileReactorCore.STATE_STOP) collection.getComponent("DEACTIVATE").setEnabled(false);
		else collection.getComponent("DEACTIVATE").setEnabled(true);
		if ((reactor.reactorState == TileReactorCore.STATE_OFFLINE || reactor.reactorState == TileReactorCore.STATE_STOP) && reactor.reactorFuel > 0) collection.getComponent("ACTIVATE").setEnabled(true);
		else collection.getComponent("ACTIVATE").setEnabled(false);

		super.updateScreen();
	}

	@Override
	public void buttonClicked(int id, int button) {
		container.sendObjectToServer(null, 20, id);
		super.buttonClicked(id, button);
	}
}
