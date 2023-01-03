package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.brandonscore.client.BCGuiSprites;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElement;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElementManager;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiButton;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.*;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.GuiAlign;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.TGuiBase;
import com.brandon3055.brandonscore.inventory.SlotMover;
import com.brandon3055.brandonscore.lib.datamanager.ManagedEnum;
import com.brandon3055.draconicevolution.blocks.tileentity.chest.SmeltingLogic.FeedMode;
import com.brandon3055.draconicevolution.blocks.tileentity.chest.TileDraconiumChest;
import com.brandon3055.draconicevolution.client.DEGuiSprites;
import com.brandon3055.draconicevolution.inventory.ContainerDraconiumChest;
import com.google.common.collect.Lists;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * Created by Werechang on 27/6/21
 */
public class GuiDraconiumChest extends ModularGuiContainer<ContainerDraconiumChest> {
    protected GuiToolkit<GuiDraconiumChest> toolkit = new GuiToolkit<>(this, 478, 268).setTranslationPrefix("gui.draconicevolution.draconium_chest");
    private final TileDraconiumChest tile;

    public GuiTexture craftIcon;
    public GuiProgressIcon furnaceProgress;
    public GuiPickColourDialog colourDialog;

    public GuiDraconiumChest(ContainerDraconiumChest container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
        tile = container.tile;
    }

    @Override
    public void addElements(GuiElementManager manager) {
        // Create and load dynamic background
        TGuiBase template = new TGuiBase(this);
        template.setButtonPlacer(null);
        template.background = GuiTexture.newDynamicTexture(xSize(), ySize(), () -> BCGuiSprites.getThemed("background_dynamic"));
        template.background.onReload(guiTex -> guiTex.setPos(guiLeft(), guiTop()));
        template.dragZoneHeight = 5;
        toolkit.loadTemplate(template);
        template.background.removeChild(template.title);

        // Add player inventory to gui
        template.playerSlots = toolkit.createPlayerSlotsManualMovers(template.background, false, index -> new SlotMover(container.slots.get(index)));
        toolkit.placeInside(template.playerSlots, template.background, GuiToolkit.LayoutPos.BOTTOM_CENTER, 0, -5);

        // Main storage
        GuiElement<?> mainSlots = toolkit.createSlots(template.background, 26, 10, 0, (x, y) -> new SlotMover(container.mainSlots.get(x + (y * 26))), null).setPos(guiLeft() + 5, guiTop() + 5);
        template.themeButton.setPos(template.background.maxXPos() - 15, mainSlots.maxYPos() + 1);

        GuiButton colourPicker = toolkit.createIconButton(template.background, 12, 12, BCGuiSprites.getter("color_picker"))
                .setHoverText(toolkit.i18n("color_picker"));
        toolkit.placeOutside(colourPicker, template.themeButton, GuiToolkit.LayoutPos.BOTTOM_CENTER, 0, 0);

        colourDialog = new GuiPickColourDialog(template.background);
        colourDialog.setBackgroundElement(new GuiTooltipBackground());
        colourDialog.setColourChangeListener(tile.colour::set);
        colourDialog.setIncludeAlpha(false);
        colourDialog.setDragZone(null);
        colourPicker.onPressed(() -> {
            colourDialog.setColour(tile.colour.get());
            toolkit.placeOutside(colourDialog, colourPicker, GuiToolkit.LayoutPos.BOTTOM_LEFT, -3, -20);
            colourDialog.show(200);
        });

        GuiButton redstoneMode = toolkit.createRSSwitch(template.background, tile);
        toolkit.placeOutside(redstoneMode, colourPicker, GuiToolkit.LayoutPos.BOTTOM_CENTER, 0, 0);


        // Crafting Slots
        GuiElement<?> craftInputSlots = toolkit.createSlots(template.background, 3, 3, 0, (x, y) -> new SlotMover(container.craftInputSlots.get(x + (y * 3))), null);
        toolkit.placeOutside(craftInputSlots, template.playerSlots, GuiToolkit.LayoutPos.MIDDLE_RIGHT, 12, 0);
        craftIcon = template.background.addChild(new GuiTexture(22, 15, BCGuiSprites.themedGetter("prog_arrow_right")));
        toolkit.placeOutside(craftIcon, craftInputSlots, GuiToolkit.LayoutPos.MIDDLE_RIGHT, 7, 0);
        GuiElement<?> craftOutputSlot = toolkit.createSlot(template.background, new SlotMover(container.craftResultSlot), null, true)
                .setPos(craftInputSlots.maxXPos() + 10, craftInputSlots.yPos());
        toolkit.placeOutside(craftOutputSlot, craftIcon, GuiToolkit.LayoutPos.MIDDLE_RIGHT, 7, 0);
        GuiLabel craftingLabel = toolkit.createHeading("container.crafting", craftInputSlots)
                .setYSize(8)
                .setTrim(false)
                .setWidthFromText()
                .setAlignment(GuiAlign.LEFT);
        toolkit.placeOutside(craftingLabel, craftOutputSlot, GuiToolkit.LayoutPos.TOP_CENTER, 0, -6);

        // Furnace Container Element
//        GuiElement<?> furnaceContainer = template.background.addChild(GuiTexture.newDynamicTexture(BCSprites.themedGetter("slot")));
//        GuiElement<?> furnaceContainer = template.background.addChild(new GuiBorderedRect().set3DGetters(GuiToolkit.Palette.SubItem::fill, GuiToolkit.Palette.SubItem::accentDark, GuiToolkit.Palette.SubItem::accentLight));
//        GuiElement<?> furnaceContainer = template.background.addChild(new GuiBorderedRect().set3DGetters(GuiToolkit.Palette.BG::fill, GuiToolkit.Palette.BG::accentDark, GuiToolkit.Palette.BG::accentLight));
//        GuiElement<?> furnaceContainer = template.background.addChild(new GuiBorderedRect().set3DGetters(GuiToolkit.Palette.Slot::fill, GuiToolkit.Palette.BG::accentDark, GuiToolkit.Palette.BG::accentLight));
        GuiElement<?> furnaceContainer = template.background.addChild(new GuiBorderedRect().set3DGetters(GuiToolkit.Palette.Slot::fill, GuiToolkit.Palette.Slot::accentLight, GuiToolkit.Palette.Slot::accentDark));
//        GuiElement<?> furnaceContainer = template.background.addChild(new GuiElement<>());
        GuiElement<?> furnaceSlots = toolkit.createSlots(furnaceContainer, 5, 1, 0, (x, y) -> new SlotMover(container.furnaceInputSlots.get(x)), null);

        // Furnace Flames
        GuiProgressIcon furnaceFlame = furnaceContainer.addChild(new GuiProgressIcon(DEGuiSprites.getter("chest/fire_base"), DEGuiSprites.getter("chest/fire_over"), GuiProgressIcon.Direction.UP).setSize(88, 15));
        furnaceFlame.setProgressSupplier(() -> (double) tile.smeltingLogic.smeltingPower.get());
        toolkit.placeOutside(furnaceFlame, furnaceSlots, GuiToolkit.LayoutPos.BOTTOM_CENTER, 0, 4);

        // Furnace Progress
        furnaceProgress = furnaceContainer.addChild(new GuiProgressIcon(BCGuiSprites.themedGetter("prog_arrow_up_tall"), BCGuiSprites.themedGetter("prog_arrow_up_tall_over"), GuiProgressIcon.Direction.UP).setSize(16, 32));
        furnaceProgress.setProgressSupplier(() -> (double) tile.smeltingLogic.smeltProgress.get());
        toolkit.placeOutside(furnaceProgress, furnaceSlots, GuiToolkit.LayoutPos.BOTTOM_RIGHT, 6, -16);

        // Energy Bar and
        GuiEnergyBar energyBar = toolkit.createEnergyBar(furnaceContainer, tile.opStorage)
                .setSize(furnaceSlots.xSize(), 14);
        toolkit.placeOutside(energyBar, furnaceSlots, GuiToolkit.LayoutPos.BOTTOM_CENTER, 0, 5 + 19);

        // Capacitor Slot
        GuiElement<?> capSlot = toolkit.createSlot(furnaceContainer, new SlotMover(container.capacitorSlot), BCGuiSprites.getter("slots/energy"), false);
        toolkit.placeOutside(capSlot, furnaceFlame, GuiToolkit.LayoutPos.MIDDLE_LEFT, -4, 0);
        GuiTexture chargeArrow = furnaceContainer.addChild(new GuiTexture(16, 16, BCGuiSprites.get("item_charge/right_discharge")));
        chargeArrow.flipX().flipY();
        toolkit.placeOutside(chargeArrow, capSlot, GuiToolkit.LayoutPos.BOTTOM_CENTER, 2, 0);

        ManagedEnum<FeedMode> feedMode = tile.smeltingLogic.feedMode;

        // Feed Mode Button
        GuiButton feedButton = toolkit.createBorderlessButton(furnaceContainer, "")
                .setSize(18, 18)
                .setHoverTextDelay(5)
                .setHoverText(e -> toolkit.i18n("feed." + feedMode.get().localKey() + ".info"));
        feedButton.addChild(new GuiTexture(1, 1, 16, 16, () -> DEGuiSprites.get(feedMode.get().getSprite())));
        toolkit.placeOutside(feedButton, furnaceSlots, GuiToolkit.LayoutPos.MIDDLE_LEFT, -3, 0);

        GuiSelectDialog<FeedMode> dialog = new GuiSelectDialog<FeedMode>(furnaceContainer)
                .setSize(18+6, 72+6)
                .setInsets(3, 3, 3, 3)
                .setCloseOnSelection(true)
                .setPlayClickSound(true)
                .addItems(Lists.newArrayList(FeedMode.values()))
                .setSelectionListener(feedMode::set)
                .setRendererBuilder(mode -> {
                    GuiButton button = toolkit.createBorderlessButton("")
                            .setSize(18, 18)
                            .setHoverTextDelay(5)
                            .setHoverText(toolkit.i18n("feed." + mode.localKey() + ".info"));
                    button.addChild(new GuiTexture(16, 16, DEGuiSprites.getter(mode.getSprite()))
                            .bindPosition(button, 1, 1));
                    return button;
                });
        dialog.addBackGroundChild(new GuiTooltipBackground().setSize(dialog));
        feedButton.onPressed(() -> {
            dialog.setPos(feedButton);
            dialog.translate(-3, -10);
            dialog.show(800);
        });

        // Furnace container bounds and position
        furnaceContainer.setBoundsToChildren(4, 4, 4, 4);
        toolkit.placeOutside(furnaceContainer, template.playerSlots, GuiToolkit.LayoutPos.MIDDLE_LEFT, -7, 1);
    }
}
