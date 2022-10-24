package com.brandon3055.draconicevolution.client.gui;

import java.awt.Rectangle;
import java.util.ArrayList;

import com.brandon3055.brandonscore.client.BCGuiSprites;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.GuiToolkit.LayoutPos;
import com.brandon3055.brandonscore.client.gui.modulargui.*;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.*;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.*;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.TBasicMachine;
import com.brandon3055.brandonscore.inventory.ContainerBCTile;
import com.brandon3055.brandonscore.inventory.ContainerSlotLayout;
import com.brandon3055.brandonscore.inventory.ContainerSlotLayout.SlotType;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDisenchanter;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGrinder;
import com.brandon3055.draconicevolution.inventory.ContainerDisenchanter;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Created by FoxMcloud5655 on 22/10/2022.
 */
public class GuiDisenchanter extends ModularGuiContainer<ContainerBCTile<TileDisenchanter>> {
	public Player player;
	private TileDisenchanter tile;
	private TBasicMachine temp;
	private GuiSlideControl scrollBar;
	private GuiElement<?> listBG;
	private GuiScrollElement listElement;
	private ItemStack prevStack;

	protected GuiToolkit<GuiDisenchanter> toolkit = new GuiToolkit<>(this, 210, 170);

	public GuiDisenchanter(ContainerBCTile<TileDisenchanter> container, Inventory inv, Component title) {
		super(container, inv, title);
		this.tile = container.tile;
		this.player = inv.player;
	}

	@Override
	public void addElements(GuiElementManager manager) {
		int padding = 3;
		temp = new TBasicMachine(this, tile, false);
		temp.background = GuiTexture.newDynamicTexture(xSize(), ySize(), () -> BCGuiSprites.getThemed("background_dynamic"));
		temp.background.onReload(guiTex -> guiTex.setPos(guiLeft(), guiTop()));
		toolkit.loadTemplate(temp);
		temp.addPlayerSlots(false, true, false);
		GuiElement input = toolkit.createSlot(temp.background, container.getSlotLayout().getSlotData(SlotType.TILE_INV, 0), BCGuiSprites.getter("slots/sword"), false);
		GuiElement books = toolkit.createSlot(temp.background, container.getSlotLayout().getSlotData(SlotType.TILE_INV, 1), null, false);
		GuiElement output = toolkit.createSlot(temp.background, container.getSlotLayout().getSlotData(SlotType.TILE_INV, 2), null, false);
		toolkit.placeInside(input, temp.background, LayoutPos.TOP_LEFT, 8, 24);
		toolkit.placeOutside(books, input, LayoutPos.MIDDLE_RIGHT, 20, 0);
		toolkit.placeOutside(output, input, LayoutPos.BOTTOM_CENTER, (books.xPos() - input.xPos()) / 2, 20);
		//GuiTexture add = new GuiTexture(BCGuiSprites.getter("add"));
		//toolkit.placeOutside(add, input, LayoutPos.MIDDLE_RIGHT, (books.xPos() - input.maxXPos()) / 2 - add.xSize() / 2, 0);
		scrollBar = toolkit.createVanillaScrollBar()
			.setPos(temp.background.maxXPos() - 12, input.yPos())
			.setXSize(10)
			.setMaxYPos(temp.playerSlots.yPos() + 2, true);
		toolkit.placeInside(scrollBar, temp.background, LayoutPos.TOP_RIGHT, -4, 16);
		listBG = temp.background.addChild(new GuiBorderedRect())
			.setPos(books.maxXPos() + padding * 2, scrollBar.yPos())
			.setYSize(scrollBar.ySize())
			.setMaxXPos(scrollBar.xPos() - 1, true)
			.set3DGetters(GuiToolkit.Palette.Slot::fill, GuiToolkit.Palette.Slot::accentDark, GuiToolkit.Palette.Slot::accentLight)
			.setBorderColourL(GuiToolkit.Palette.Slot::border3D);
		prevStack = tile.itemHandler.getStackInSlot(0);
		if (!prevStack.isEmpty()) {
			addList();
		}
	}

	@Override
	public void containerTick() {
		super.containerTick();
		ItemStack stack = tile.itemHandler.getStackInSlot(0);
		if (!stack.equals(prevStack, false)) {
			removeList();
			if (!stack.isEmpty()) {
				addList();
			}
			prevStack = stack.copy();
		}
	}

	private void addList() {
		ItemStack slot0 = tile.itemHandler.getStackInSlot(0);
		if (!slot0.isEmpty()) {
			listElement = new GuiScrollElement().setListMode(GuiScrollElement.ListMode.VERT_LOCK_POS_WIDTH)
				.setListSpacing(1)
				.setInsets(1, 1, 2, 1);
			listBG.addChild(listElement)
				.setPos(listBG)
				.setSize(listBG.getInsetRect()).bindSize(listBG, true)
				.setVerticalScrollBar(scrollBar)
				.setStandardScrollBehavior();
			for (Tag tag : slot0.getEnchantmentTags()) {
				CompoundTag cTag = (CompoundTag)tag;
				int lvl = cTag.getShort("lvl");
				ArrayList<Component> hoverText = new ArrayList<Component>();
				hoverText.add(new TranslatableComponent("gui." + DraconicEvolution.MODID + ".disenchanter.level", lvl));
				hoverText.add(new TranslatableComponent("gui." + DraconicEvolution.MODID + ".disenchanter.cost", tile.getCostInLevels(tile.getEnchantmentFromTag(cTag), lvl)));
				GuiButton button = toolkit.createButton(() -> ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(cTag.getString("id"))).getDescriptionId(), listElement)
					.setYSize(14)
					.setXSize(listElement.getInsetRect().width)
					.setComponentHoverText(hoverText)
					.onPressed(() -> tile.sendPacketToServer(output -> output.writeString(cTag.getString("id")), 1));
				listElement.addElement(button);
			}
		}
	}

	private void removeList() {
		listBG.removeChild(listElement);
		listElement = null;
	}
}
