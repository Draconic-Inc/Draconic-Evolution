package com.brandon3055.draconicevolution.client.gui;

import codechicken.lib.gui.modular.ModularGui;
import codechicken.lib.gui.modular.ModularGuiContainer;
import codechicken.lib.gui.modular.elements.*;
import codechicken.lib.gui.modular.lib.Constraints;
import codechicken.lib.gui.modular.lib.container.ContainerGuiProvider;
import codechicken.lib.gui.modular.lib.container.ContainerScreenAccess;
import codechicken.lib.gui.modular.lib.geometry.Axis;
import codechicken.lib.gui.modular.lib.geometry.Direction;
import com.brandon3055.brandonscore.client.BCGuiTextures;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.ButtonRow;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDisenchanter;
import com.brandon3055.draconicevolution.client.DEGuiTextures;
import com.brandon3055.draconicevolution.inventory.DisenchanterMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

import static codechicken.lib.gui.modular.lib.geometry.Constraint.*;
import static codechicken.lib.gui.modular.lib.geometry.GeoParam.*;

/**
 * Created by FoxMcloud5655 on 22/10/2022.
 */
public class DisenchanterGui extends ContainerGuiProvider<DisenchanterMenu> {
    private static final GuiToolkit TOOLKIT = new GuiToolkit("gui.draconicevolution.disenchanter");
    public static final int GUI_WIDTH = 198;
    public static final int GUI_HEIGHT = 170;

    //	public Player player;
//	private TileDisenchanter tile;
//	private TBasicMachine temp;
//	private GuiSlideControl scrollBar;
//	private GuiElement<?> listBG;
//	private GuiScrollElement listElement;

    private GuiScrolling scroll;
    private List<GuiElement<?>> listButtons = new ArrayList<>();
    private ItemStack prevStack = ItemStack.EMPTY;

    @Override
    public GuiElement<?> createRootElement(ModularGui gui) {
        GuiManipulable root = new GuiManipulable(gui).addMoveHandle(3).enableCursors(true);
        GuiTexture bg = new GuiTexture(root.getContentElement(), DEGuiTextures.themedGetter("disenchanter"));
        Constraints.bind(bg, root.getContentElement());
        return root;
    }

    @Override
    public void buildGui(ModularGui gui, ContainerScreenAccess<DisenchanterMenu> screenAccess) {
        gui.initStandardGui(GUI_WIDTH, GUI_HEIGHT);
        DisenchanterMenu menu = screenAccess.getMenu();
        TileDisenchanter tile = menu.tile;
        GuiElement<?> root = gui.getRoot();
        TOOLKIT.createHeading(root, gui.getGuiTitle(), true);

        ButtonRow buttonRow = ButtonRow.topRightInside(root, Direction.DOWN, 3, 3).setSpacing(1);
        buttonRow.addButton(TOOLKIT::createThemeButton);

        var playInv = GuiSlots.playerWithArmor(root, screenAccess, menu.main, menu.hotBar, menu.armor);
        playInv.stream().forEach(e -> e.setSlotTexture(slot -> BCGuiTextures.getThemed("slot")));
        Constraints.placeInside(playInv.container(), root, Constraints.LayoutPos.BOTTOM_CENTER, 0, -7);

        GuiRectangle listBg = TOOLKIT.shadedBorder(root)
                .fill(GuiToolkit.Palette.Slot::fill)
                .constrain(RIGHT, relative(playInv.container().get(RIGHT), -6))
                .constrain(BOTTOM, relative(playInv.container().get(TOP), -4))
                .constrain(TOP, relative(root.get(TOP), 16))
                .constrain(WIDTH, literal(120));

        scroll = new GuiScrolling(listBg);
        Constraints.bind(scroll, listBg, 1);
        scroll.installContainerElement(new GuiElement<>(scroll));
        scroll.getContentElement()
                .constrain(WIDTH, null)
                .constrain(LEFT, match(scroll.get(LEFT)))
                .constrain(RIGHT, match(scroll.get(RIGHT)));

        var vanillaBar = TOOLKIT.vanillaScrollBar(root, Axis.Y);
        vanillaBar.container()
                .constrain(TOP, match(listBg.get(TOP)))
                .constrain(BOTTOM, match(listBg.get(BOTTOM)))
                .constrain(LEFT, relative(listBg.get(RIGHT), 1))
                .constrain(WIDTH, literal(8));
        vanillaBar.slider()
                .setSliderState(scroll.scrollState(Axis.Y))
                .setScrollableElement(scroll);

        GuiSlots toolSlot = GuiSlots.singleSlot(root, screenAccess, menu.input, 0)
                .setSlotTexture(slot -> BCGuiTextures.getThemed("slot"))
                .setEmptyIcon(BCGuiTextures.get("slots/sword"))
                .constrain(LEFT, match(playInv.container().get(LEFT)))
                .constrain(TOP, relative(listBg.get(TOP), 7));

        GuiSlots bookSlot = GuiSlots.singleSlot(root, screenAccess, menu.books, 0)
                .setSlotTexture(slot -> BCGuiTextures.getThemed("slot"))
                .setEmptyIcon(BCGuiTextures.get("slots/book"))
                .constrain(RIGHT, relative(listBg.get(LEFT), -4))
                .constrain(TOP, relative(listBg.get(TOP), 7));

        GuiSlots outputSlot = GuiSlots.singleSlot(root, screenAccess, menu.output, 0)
                .setSlotTexture(slot -> BCGuiTextures.getThemed("slot"))
                .constrain(LEFT, midPoint(toolSlot.get(LEFT), bookSlot.get(RIGHT), -8))
                .constrain(TOP, relative(toolSlot.get(BOTTOM), 18));

        gui.onTick(() -> tick(tile));

        //		GuiElement input = toolkit.createSlot(temp.background, container.getSlotLayout().getSlotData(SlotType.TILE_INV, 0), BCGuiTextures.getter("slots/sword"), false);
//		GuiElement books = toolkit.createSlot(temp.background, container.getSlotLayout().getSlotData(SlotType.TILE_INV, 1), null, false);
//		GuiElement output = toolkit.createSlot(temp.background, container.getSlotLayout().getSlotData(SlotType.TILE_INV, 2), null, false);
//		toolkit.placeInside(input, temp.background, LayoutPos.TOP_LEFT, 8, 24);
//		toolkit.placeOutside(books, input, LayoutPos.MIDDLE_RIGHT, 20, 0);
//		toolkit.placeOutside(output, input, LayoutPos.BOTTOM_CENTER, (books.xPos() - input.xPos()) / 2, 20);
//		//GuiTexture add = new GuiTexture(BCGuiSprites.getter("add"));
//		//toolkit.placeOutside(add, input, LayoutPos.MIDDLE_RIGHT, (books.xPos() - input.maxXPos()) / 2 - add.xSize() / 2, 0);
//		scrollBar = toolkit.createVanillaScrollBar()
//			.setPos(temp.background.maxXPos() - 12, input.yPos())
//			.setXSize(10)
//			.setMaxYPos(temp.playerSlots.yPos() + 2, true);
//		toolkit.placeInside(scrollBar, temp.background, LayoutPos.TOP_RIGHT, -4, 16);
//		listBG = temp.background.addChild(new GuiBorderedRect())
//			.setPos(books.maxXPos() + padding * 2, scrollBar.yPos())
//			.setYSize(scrollBar.ySize())
//			.setMaxXPos(scrollBar.xPos() - 1, true)
//			.set3DGetters(GuiToolkit.Palette.Slot::fill, GuiToolkit.Palette.Slot::accentDark, GuiToolkit.Palette.Slot::accentLight)
//			.setBorderColourL(GuiToolkit.Palette.Slot::border3D);
//		prevStack = tile.itemHandler.getStackInSlot(0);
//		if (!prevStack.isEmpty()) {
//			addList();
//		}

    }

    public void tick(TileDisenchanter tile) {
        ItemStack stack = tile.itemHandler.getStackInSlot(0);
        if (!stack.equals(prevStack)) {
            clearList();
            if (!stack.isEmpty()) {
                populateList(stack, tile);
            }
            prevStack = stack.copy();
        }
    }

    private void populateList(ItemStack stack, TileDisenchanter tile) {
        if (stack.isEmpty()) return;
        int index = 0;
        for (Tag tag : stack.getEnchantmentTags()) {
            CompoundTag cTag = (CompoundTag) tag;
            int lvl = cTag.getShort("lvl");
            Enchantment enchantment = BuiltInRegistries.ENCHANTMENT.get(new ResourceLocation(cTag.getString("id")));

            Enchantment enchForLevel = tile.getEnchantmentFromTag(cTag);
            GuiButton button = TOOLKIT.createFlat3DButton(scroll.getContentElement(), () -> Component.translatable(enchantment.getDescriptionId()))
                    .setTooltip(Component.translatable("gui." + DraconicEvolution.MODID + ".disenchanter.level", lvl), Component.translatable("gui." + DraconicEvolution.MODID + ".disenchanter.cost", enchForLevel == null ? 0 : tile.getCostInLevels(enchForLevel, lvl)))
                    .onPress(() -> tile.sendPacketToServer(output -> output.writeString(cTag.getString("id")), 1))
                    .constrain(HEIGHT, literal(14))
                    .constrain(LEFT, match(scroll.getContentElement().get(LEFT)))
                    .constrain(RIGHT, match(scroll.getContentElement().get(RIGHT)))
                    .constrain(TOP, relative(scroll.getContentElement().get(TOP), index * 15));
            listButtons.add(button);
            index++;
        }
    }

    private void clearList() {
        listButtons.forEach(scroll.getContentElement()::removeChild);
        listButtons.clear();
    }

    public static class Screen extends ModularGuiContainer<DisenchanterMenu> {
        public Screen(DisenchanterMenu menu, Inventory inv, Component title) {
            super(menu, inv, new DisenchanterGui());
            getModularGui().setGuiTitle(title);
        }
    }
}
