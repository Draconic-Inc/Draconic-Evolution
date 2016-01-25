package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.brandonscore.client.utills.GuiHelper;
import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.container.ContainerUpgradeModifier;
import com.brandon3055.draconicevolution.common.tileentities.TileUpgradeModifier;
import com.brandon3055.draconicevolution.common.utills.IUpgradableItem;
import com.brandon3055.draconicevolution.common.utills.IUpgradableItem.EnumUpgrade;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GUIUpgradeModifier extends GuiContainer {

	public EntityPlayer player;
	private TileUpgradeModifier tile;
	private float rotation = 0;
	private float rotationSpeed = 0;
	private float targetSpeed = 0;

	private boolean inUse = false;
	private IUpgradableItem upgradableItem = null;
	private ItemStack stack = null;
	private List<EnumUpgrade> itemUpgrades = new ArrayList<EnumUpgrade>();
	private ContainerUpgradeModifier containerEM;

	public GUIUpgradeModifier(InventoryPlayer invPlayer, TileUpgradeModifier tile, ContainerUpgradeModifier containerEM) {
		super(containerEM);
		this.containerEM = containerEM;


		xSize = 176;
		ySize = 190;

		this.tile = tile;
		this.player = invPlayer.player;
	}


	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
		GL11.glColor4f(1F, 1F, 1F, 1F);
		ResourceHandler.bindResource("textures/gui/UpgradeModifier.png");

		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		drawTexturedModalRect(guiLeft + 38, guiTop + 6, 3, 106, 100, 50);
		drawTexturedModalRect(guiLeft + 38, guiTop + 56, 3, 106, 100, 50);

		GL11.glPushMatrix();
		GL11.glTranslated(guiLeft + 38, guiTop + 6, 0);
		GL11.glTranslatef(50, 50, 0);
		GL11.glRotatef(rotation + (f * rotationSpeed), 0, 0, 1);
		GL11.glTranslatef(-50, -50, 0);
		drawTexturedModalRect(0, 0, 38, 6, 100, 100);
		GL11.glPopMatrix();

		if (!inUse)drawSlots();
		else renderUpgrades(x, y);

	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		drawCenteredString(fontRendererObj, tile.getBlockType().getLocalizedName(), xSize/2, -9, 0x00FFFF);

	}

	@Override
	public void initGui() {
		super.initGui();

	}

	@Override
	protected void actionPerformed(GuiButton button) {


	}

	private int coreSlots = 0;
	private int coreTier = 0;
	private int usedSlots = 0;
	private boolean[] coreInInventory = new boolean[4];

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (tile.getStackInSlot(0) != null && tile.getStackInSlot(0).getItem() instanceof IUpgradableItem)
		{
			stack = tile.getStackInSlot(0);
			upgradableItem = (IUpgradableItem) stack.getItem();
			itemUpgrades = upgradableItem.getUpgrades();
			inUse = true;
			coreSlots = upgradableItem.getUpgradeCap();
			coreTier = upgradableItem.getMaxTier();
			usedSlots = 0;
			coreInInventory[0] = player.inventory.hasItem(ModItems.draconicCore);
			coreInInventory[1] = player.inventory.hasItem(ModItems.wyvernCore);
			coreInInventory[2] = player.inventory.hasItem(ModItems.awakenedCore);
			coreInInventory[3] = player.inventory.hasItem(ModItems.chaoticCore);

			for (EnumUpgrade upgrade : upgradableItem.getUpgrades()) {
				for (Integer i : upgrade.getCoresApplied(stack)) usedSlots += i;
			}

		}
		else inUse = false;

		if (inUse) targetSpeed = 5F;
		else targetSpeed = 0F;

		if (rotationSpeed < targetSpeed) rotationSpeed += 0.05F;
		else if (rotationSpeed > targetSpeed) rotationSpeed -= 0.05F;
		if (targetSpeed == 0 && rotationSpeed < 0) rotationSpeed = 0;
		rotation += rotationSpeed;
	}

	private void drawSlots() {
		ResourceHandler.bindResource("textures/gui/Widgets.png");

		int xPos = guiLeft + ((xSize - 162) / 2);
		int yPos = guiTop + 110;

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				drawTexturedModalRect(xPos + x*18, yPos + y*18, 138, 0, 18, 18);
			}
		}

		for (int x = 0; x < 9; x++) {
			drawTexturedModalRect(xPos + x*18, yPos + 56, 138, 0, 18, 18);
		}

		drawTexturedModalRect(guiLeft + 79, guiTop + 47, 138, 0, 18, 18);
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);

		for (EnumUpgrade upgrade : itemUpgrades)
		{
			int xIndex = itemUpgrades.indexOf(upgrade);
			int spacing = (xSize-6) / itemUpgrades.size();
			int xPos = guiLeft + (xIndex * spacing) + ((spacing - 23) / 2) + 4;
			int yPos = guiTop + 90;


			int[] appliedCores = upgrade.getCoresApplied(tile.getStackInSlot(0));

			for (int i = 0; i <= coreTier; i++)
			{
				//Check + buttons
				if (/*coreInInventory[i] && */coreSlots > usedSlots && GuiHelper.isInRect(xPos, yPos+33 + i*18, 8, 8, x, y)){
					containerEM.sendObjectToServer(null, upgrade.index, i*2);
					Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(ResourceHandler.getResourceWOP("gui.button.press"), 1.0F));
				}

				//Check - buttons
				if (appliedCores[i] > -1 && GuiHelper.isInRect(xPos + 16, yPos+33 + i*18, 8, 8, x, y)){
					containerEM.sendObjectToServer(null, upgrade.index, 1 + i*2);
					Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(ResourceHandler.getResourceWOP("gui.button.press"), 1.0F));
				}
			}
		}

	}

	private void renderUpgrades(int x, int y){
		//First Draw
		for (EnumUpgrade upgrade : itemUpgrades)
		{
			int xIndex = itemUpgrades.indexOf(upgrade);
			int spacing = (xSize-6) / itemUpgrades.size();
			int xPos = guiLeft + (xIndex * spacing) + ((spacing - 23) / 2) + 4;
			int yPos = guiTop + 90;

			ResourceHandler.bindResource("textures/gui/UpgradeModifier.png");

			drawTexturedModalRect(xPos, yPos, 0, 190, 24, 24);
			drawTexturedModalRect(xPos+3, yPos+3, upgrade.index * 18, 220, 18, 18);

			int[] appliedCores = upgrade.getCoresApplied(tile.getStackInSlot(0));

			for (int i = 0; i <= coreTier; i++) {
				drawTexturedModalRect(xPos+3, yPos+24 + i*18, 24 + i*18, 190, 18, 18);
				drawTexturedModalRect(xPos+3, yPos+24 + i*18, 24 + i*18, 190, 18, 18);

				//Draw + buttons
				if (coreSlots > usedSlots){
					boolean hovering = GuiHelper.isInRect(xPos, yPos+33 + i*18, 8, 8, x, y);
					if (!coreInInventory[i]) drawTexturedModalRect(xPos, yPos+33 + i*18, 24, 208, 8, 8);
					else drawTexturedModalRect(xPos, yPos+33 + i*18, 32 + (hovering ? 8 : 0), 208, 8, 8);
				}

				//Draw - buttons
				if (appliedCores[i] > 0){
					boolean hovering = GuiHelper.isInRect(xPos + 16, yPos+33 + i*18, 8, 8, x, y);
					drawTexturedModalRect(xPos + 16, yPos+33 + i*18, 56 + (hovering ? 8 : 0), 208, 8, 8);
				}


			}
			for (int i = 0; i <= coreTier; i++) drawCenteredString(fontRendererObj, String.valueOf(appliedCores[i]), xPos + 12, yPos + 29 + i*18, 0xFFFFFF);
		}

		//Second Draw
		for (EnumUpgrade upgrade : itemUpgrades)
		{
			int xIndex = itemUpgrades.indexOf(upgrade);
			int spacing = (xSize-6) / itemUpgrades.size();
			int xPos = guiLeft + (xIndex * spacing) + ((spacing - 23) / 2) + 4;
			int yPos = guiTop + 90;
			int[] appliedCores = upgrade.getCoresApplied(tile.getStackInSlot(0));

			if (GuiHelper.isInRect(xPos, yPos, 24, 24, x, y)){
				List list = new ArrayList();
				list.add(upgrade.getLocalizedName());
				drawHoveringText(list, x, y, fontRendererObj);
			}

			for (int i = 0; i <= coreTier; i++) {
				//Draw Button Text (add)
				if (coreSlots > usedSlots && GuiHelper.isInRect(xPos, yPos+33 + i*18, 8, 8, x, y)) {
					List list = new ArrayList<String>();
					if (coreInInventory[i]) list.add(StatCollector.translateToLocal("gui.de.addCore.txt"));
					else list.add(StatCollector.translateToLocal("gui.de.noCoresInInventory" + i + ".txt"));
					drawHoveringText(list, x, y, fontRendererObj);
				}

				//Draw Button Text (remove)
				if (appliedCores[i] > 1 && GuiHelper.isInRect(xPos + 16, yPos+33 + i*18, 8, 8, x, y)) {
					List list = new ArrayList<String>();
					if (coreInInventory[i]) list.add(StatCollector.translateToLocal("gui.de.removeCore.txt"));
					drawHoveringText(list, x, y, fontRendererObj);
				}
			}
		}
	}
}
