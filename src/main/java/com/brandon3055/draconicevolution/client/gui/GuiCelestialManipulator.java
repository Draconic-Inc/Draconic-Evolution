package com.brandon3055.draconicevolution.client.gui;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import com.brandon3055.brandonscore.client.BCGuiSprites;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.GuiToolkit.LayoutPos;
import com.brandon3055.brandonscore.client.gui.modulargui.*;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiButton;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiBorderedRect;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiEnergyBar;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiLabel;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiStackIcon;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiTexture;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.ModuleBuilder.RawColumns;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.TBasicMachine;
import com.brandon3055.brandonscore.inventory.ContainerBCTile;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.tileentity.TileCelestialManipulator;
import com.brandon3055.draconicevolution.client.DEGuiSprites;

import com.brandon3055.draconicevolution.client.gui.modular.TModularMachine;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

/**
 * Created by brandon3055 on 17/10/2016.
 */
public class GuiCelestialManipulator extends ModularGuiContainer<ContainerBCTile<TileCelestialManipulator>> {

	private Player player;
	private TileCelestialManipulator tile;
	private TBasicMachine temp;
    //private MGuiEffectRenderer effectRenderer;
	private GuiButton weatherMode;
	private GuiButton sunMode;
	private List<GuiElement> weatherControls = new ArrayList<>();
	private List<GuiElement> sunControls = new ArrayList<>();
	private GuiEnergyBar energyBar;
	private GuiBorderedRect rsBackground;
	private GuiElement[] rsControlButtons = new GuiElement[9];
	private double rsTabAnim = 0;
	private boolean rsTabEnabled = false;

	protected GuiToolkit<GuiCelestialManipulator> toolkit = new GuiToolkit<>(this, 180, 214).setTranslationPrefix("gui." + DraconicEvolution.MODID + ".celestial_manipulator");

	public GuiCelestialManipulator(ContainerBCTile<TileCelestialManipulator> container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);

		this.imageWidth = 180;
		this.imageHeight = 200;

		this.tile = container.tile;
		this.player = playerInventory.player;
	}

	@Override
	public void addElements(GuiElementManager manager) {
		manager.clear();
		weatherControls.clear();
		sunControls.clear();
		temp = new TModularMachine(this, tile, false);
		temp.background = GuiTexture.newDynamicTexture(xSize(), ySize(), () -> BCGuiSprites.getThemed("background_dynamic"));
		temp.background.onReload(guiTex -> guiTex.setPos(guiLeft(), guiTop()));
		toolkit.loadTemplate(temp);
		temp.playerSlots = toolkit.createPlayerSlots(temp.background, false);
		toolkit.placeInside(temp.playerSlots, temp.background, GuiToolkit.LayoutPos.BOTTOM_CENTER, 0, -7);
		//temp.background.addChild(effectRenderer = new MGuiEffectRenderer(this).setParticleTexture(DEParticles.DE_SHEET.toString()));
		temp.background.addChild(weatherMode = new GuiButton(guiLeft() + 5, guiTop() + 17, 50, 12, toolkit.i18n("weather"))
			.setFillColours(0x90000000, 0x90111111, 0x90222222)
			.setTextColour(0x9060FF60, 0x9080FFFF, 0x90600000)
			.setBorderColours(0x9000FFFF, 0x9000FF00, 0x9000FF00)
			.onPressed(() -> {
				tile.weatherMode.set(true);
				updateControls();
			}));
		temp.background.addChild(sunMode = new GuiButton(guiLeft() + xSize() - 55, guiTop() + 17, 50, 12, toolkit.i18n("time"))
			.setFillColours(0x90000000, 0x90111111, 0x90222222)
			.setTextColour(0x9060FF60, 0x9080FFFF, 0x90600000)
			.setBorderColours(0x9000FFFF, 0x9000FF00, 0x9000FF00)
			.onPressed(() -> {
				tile.weatherMode.set(false);
				updateControls();
			}));
		temp.energyBar = toolkit.createEnergyBar(temp.background, tile.opStorage);
		temp.energyBar.setPos(guiLeft() + 9, guiTop() + 98).setSize(temp.playerSlots.xSize(), 14);
		temp.background.addChild(new GuiBorderedRect(guiLeft() + 4, guiTop() + 16, xSize() - 8, ySize() - 100).setFillColour(0x40000000).setBorderColour(0x90000000));

		int i = 32;
		weatherControls.add(new GuiButton(guiLeft() + 4, guiTop() + i, xSize() - 8, 14, toolkit.i18n("stopRain"))
			.onPressed(() -> tile.sendPacketToServer((output) -> output.writeString("STOP_RAIN"), 0)));
		weatherControls.add(new GuiButton(guiLeft() + 4, guiTop() + (i += 22), xSize() - 8, 14, toolkit.i18n("startRain"))
			.onPressed(() -> tile.sendPacketToServer((output) -> output.writeString("START_RAIN"), 0)));
		weatherControls.add(new GuiButton(guiLeft() + 4, guiTop() + (i += 22), xSize() - 8, 14, toolkit.i18n("startStorm"))
			.onPressed(() -> tile.sendPacketToServer((output) -> output.writeString("START_STORM"), 0)));

		i = 22;
		sunControls.add(new GuiLabel(guiLeft(), guiTop() + i, xSize(), 12, toolkit.i18n("skipTo")));
		i += 14;
		sunControls.add(new GuiButton(guiLeft() + 4, guiTop() + i, xSize() / 3 - 4, 14, toolkit.i18n("sunRise"))
			.onPressed(() -> tile.sendPacketToServer((output) -> output.writeString("SUN_RISE"), 0)));
		sunControls.add(new GuiButton(guiLeft() + 4 + xSize() / 3 - 2, guiTop() + i, xSize() / 3 - 4, 14, toolkit.i18n("midDay"))
			.onPressed(() -> tile.sendPacketToServer((output) -> output.writeString("MID_DAY"), 0)));
		sunControls.add(new GuiButton(guiLeft() + 4 + (xSize() / 3) * 2 - 4, guiTop() + i, xSize() / 3 - 4, 14, toolkit.i18n("sunSet"))
			.onPressed(() -> tile.sendPacketToServer((output) -> output.writeString("SUN_SET"), 0)));
		i += 20;
		sunControls.add(new GuiButton(guiLeft() + 4, guiTop() + i, xSize() / 3 - 4, 14, toolkit.i18n("moonRise"))
			.onPressed(() -> tile.sendPacketToServer((output) -> output.writeString("MOON_RISE"), 0)));
		sunControls.add(new GuiButton(guiLeft() + 4 + xSize() / 3 - 2, guiTop() + i, xSize() / 3 - 4, 14, toolkit.i18n("midnight"))
			.onPressed(() -> tile.sendPacketToServer((output) -> output.writeString("MIDNIGHT"), 0)));
		sunControls.add(new GuiButton(guiLeft() + 4 + (xSize() / 3) * 2 - 4, guiTop() + i, xSize() / 3 - 4, 14, toolkit.i18n("moonSet"))
			.onPressed(() -> tile.sendPacketToServer((output) -> output.writeString("MOON_SET"), 0)));
		i += 20;
		sunControls.add(new GuiButton(guiLeft() + 4, guiTop() + i, xSize() / 2 - 5, 14, toolkit.i18n("skip24"))
			.onPressed(() -> tile.sendPacketToServer((output) -> output.writeString("SKIP_24"), 0)));
		sunControls.add(new GuiButton(guiLeft() + 1 + xSize() / 2, guiTop() + i, xSize() / 2 - 5, 14, toolkit.i18n("stop"))
			.onPressed(() -> tile.sendPacketToServer((output) -> output.writeString("STOP"), 0)));

		updateControls();

		temp.background.addChild(rsBackground = new GuiBorderedRect(guiLeft() + xSize(), guiTop() + 97, 18, 18).setBorderColour(0xFF505050));
		rsBackground.addChild(new GuiStackIcon(rsBackground.xPos(), rsBackground.yPos(), rsBackground.xSize(), rsBackground.ySize(), "minecraft:redstone")
			.setToolTip(false));
		temp.background.addChild(new GuiButton(rsBackground.xPos(), rsBackground.yPos(), rsBackground.xSize(), rsBackground.ySize(), "")
			.setFillColours(0, 0, 0)
			.setHoverText(new String[]{I18n.get("generic.configureRedstone")})
			.setHoverTextDelay(2)
			.onPressed(() -> rsBackground.setChildGroupEnabled("RS_BUTTON", !rsControlButtons[0].isEnabled())));
		toolkit.jeiExclude(rsBackground);

		String[] rsButtonNames = {"clear", "rain", "storm", "sunrise", "noon", "sunset", "moonrise", "midnight", "moonset"};
		for (int ii = 0; ii < rsControlButtons.length; ii++) {
			final int num = ii;
			rsControlButtons[num] = toolkit.createIconButton(rsBackground, 18, DEGuiSprites.getter("celestial_manipulator/" + rsButtonNames[num]))
				.setFillColours(0, 0xFF505050, 0xFF707070)
				.setBorderColours(0xFF505050, 0xFF707070, 0xFFF00000)
				.onPressed(() -> tile.sendPacketToServer((output) -> {output.writeInt(num);}, 1))
				.setHoverText(new String[]{toolkit.i18n("rs." + num)})
				.setEnabled(false)
				.addToGroup("RS_BUTTON");
			toolkit.jeiExclude(rsControlButtons[num]);
			if (num == 0) {
				toolkit.placeOutside(rsControlButtons[num], rsBackground, LayoutPos.BOTTOM_CENTER, 0, 2);
			}
			else {
				if (num % 3 != 0) {
					toolkit.placeOutside(rsControlButtons[num], rsControlButtons[num-1], LayoutPos.MIDDLE_RIGHT, 2, 0);
				}
				else {
					toolkit.placeOutside(rsControlButtons[num], rsControlButtons[num-3], LayoutPos.BOTTOM_CENTER, 0, 2);
				}
			}
		}
	}

	private void updateControls() {
		if (tile.weatherMode.get()) {
			for (GuiElement elementBase : sunControls) {
				temp.background.removeChild(elementBase);
			}
			for (GuiElement elementBase : weatherControls) {
				if (!temp.background.getChildElements().contains(elementBase)) {
					temp.background.addChild(elementBase);
				}
			}
			sunMode.setDisabled(false);
			weatherMode.setDisabled(true);
		}
		else {
			for (GuiElement elementBase : sunControls) {
				if (!temp.background.getChildElements().contains(elementBase)) {
					temp.background.addChild(elementBase);
				}
			}
			for (GuiElement elementBase : weatherControls) {
				temp.background.removeChild(elementBase);
			}
			sunMode.setDisabled(true);
			weatherMode.setDisabled(false);
		}
	}

//	@Override
//	public void updateScreen() {
//		int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
//		int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
//
//		for (GuiElement element : manager.getElements()) {
//			if (element instanceof GuiButton && (element.isMouseOver(mouseX, mouseY) || ((element == sunMode || element == weatherMode) && ((GuiButton) element).disabled))) {
//				GuiParticle particle = new GuiParticle(mc.world, element.xPos + 4 + mc.world.rand.nextInt(element.xSize() - 8), element.yPos + (element.ySize() / 2));
//				particle.setScale(0.1F);
//				particle.setRBGColorF(1, 1, 1);
//				effectRenderer.addEffect(particle);
//			}
//		}
//
//		updateControls();
//
//		effectRenderer.addEffect(new GuiParticle(mc.world, guiLeft() + xSize() / 2, guiTop() + 6));
//
//		if (rsTabEnabled && rsTabAnim < 1) {
//			rsTabAnim += 0.2;
//		}
//		else if (!rsTabEnabled && rsTabAnim > 0) {
//			rsTabAnim -= 0.2;
//		}
//		if (rsTabAnim > 1) rsTabAnim = 1;
//		else if (rsTabAnim < 0) rsTabAnim = 0;
//
//		rsBackground.xSize() = 18 + (int) (rsTabAnim * 48);
//		rsBackground.ySize() = 18 + (int) (rsTabAnim * 84);
//
//		animRect = new Rectangle(rsBackground.xPos(), rsBackground.yPos(), rsBackground.xSize(), rsBackground.ySize());
//
//		for (GuiElement elementBase : rsControlButtons) {
//			if (elementBase != null) {
//				elementBase.setEnabled(elementBase.xPos < animRect.x + animRect.width - 18 && elementBase.yPos < animRect.y + animRect.height - 18);
//				GuiElement e;
//				if (elementBase.childElements.size() > 0 && (e = elementBase.childElements.get(0)) instanceof GuiButton) {
//					((GuiButton) e).disabled = tile.rsMode.get() == ((GuiButton) e).buttonId;
//				}
//			}
//		}
//
//		super.updateScreen();
//	}

//    private class GuiParticle extends GuiEffect {
//
//        protected GuiParticle(World world, double posX, double posY) {
//            super(world, posX, posY);
//
//            float speed = 5F;
//            this.motionX = (-0.5F + rand.nextFloat()) * speed;
//            this.motionY = (-0.5F + rand.nextFloat()) * speed / 4F;
//            this.particleMaxAge = 10 + rand.nextInt(10);
//            this.particleScale = 0.5F;
//            this.particleTextureIndexX = 0;
//            this.particleTextureIndexY = 1;
//            this.particleRed = 0;
//        }
//
//        @Override
//        public GuiEffect setScale(float scale) {
//            float speed = 5F * scale;
//            this.motionX = (-0.5F + rand.nextFloat()) * speed;
//            this.motionY = (-0.5F + rand.nextFloat()) * speed;
//            return super.setScale(scale);
//        }
//
//        @Override
//        public void onUpdate() {
//            super.onUpdate();
//
//            particleTextureIndexX = rand.nextInt(5);
//            int ttd = particleMaxAge - particleAge;
//            if (ttd < 10) {
//                particleScale = ttd / 10F;
//            }
//            if (ttd == 1) {
//                particleScale = 0.5F;
//                setExpired();
//            }
//
//        }
//
//        @Override
//        public void renderParticle(float partialTicks) {
//            if (particleAge == 0) {
//                return;
//            }
//            super.renderParticle(partialTicks);
//        }
//    }
}
