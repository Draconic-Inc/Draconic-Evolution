package com.brandon3055.draconicevolution.client.gui;

import codechicken.lib.gui.modular.ModularGui;
import codechicken.lib.gui.modular.ModularGuiScreen;
import codechicken.lib.gui.modular.lib.container.ContainerGuiProvider;
import codechicken.lib.gui.modular.lib.container.ContainerScreenAccess;
import com.brandon3055.brandonscore.inventory.ContainerBCTile;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyTransfuser;

/**
 * Created by brandon3055 on 12/12/2020.
 */
public class GuiEnergyTransfuser extends ContainerGuiProvider<ContainerBCTile<TileEnergyTransfuser>> {

    @Override
    public void buildGui(ModularGui gui, ContainerScreenAccess<ContainerBCTile<TileEnergyTransfuser>> screenAccess) {

    }

//    public Player player;
//    private TileEnergyTransfuser tile;
//
//    protected GuiToolkit<GuiEnergyTransfuser> toolkit = new GuiToolkit<>(this, 218, 215).setTranslationPrefix("gui.draconicevolution.transfuser");
//
//    public GuiEnergyTransfuser(ContainerBCTile<TileEnergyTransfuser> container, Inventory inv, Component title) {
//        super(container, inv, title);
//        this.tile = container.tile;
//        this.player = inv.player;
//    }
//
//    @Override
//    public void addElements(GuiElementManager manager) {
//        TBasicMachine temp = new TModularMachine(this, tile, false);
//        temp.background = GuiTexture.newDynamicTexture(xSize(), ySize(), () -> BCGuiTextures.getThemed("background_dynamic"));
//        temp.background.onReload(guiTex -> guiTex.setPos(guiLeft(), guiTop()));
//        toolkit.loadTemplate(temp);
//
//        temp.addPlayerSlots(true, true, true);
//
//        int padding = 3;
//        int ySpace = (temp.playerSlots.yPos() - temp.title.maxYPos()) - (padding * 2);
//        int slotWidth = 24;
//        int barWidth = 18;
//        int totalWidth = 18 * 9;
//
//        GuiElement<?> holder = new GuiElement<>();
//        int xOffset = 0;
//        for (int i = 0; i < 4; i++) {
//            int fi = i;
//            GuiBorderedRect column = new GuiBorderedRect()
//                    .set3DGetters(() -> Ctrl.fill(false), () -> Ctrl.accentLight(false), () -> Ctrl.accentDark(false))
//                    .setSize(slotWidth, ySpace)
//                    .setXPos(xOffset);
//
//            GuiElement<?> slot = toolkit.createSlots(column, 1, 1, 10, (col, row) -> container.getSlotLayout().getSlotData(ContainerSlotLayout.SlotType.TILE_INV, fi), null)
//                    .setXPos(xOffset + (slotWidth - 18) / 2)
//                    .setMaxYPos(column.maxYPos() - (slotWidth - 18) / 2, false);
//
//            GuiButton button = toolkit.createButton_old("", column, true, 0)
//                    .setSize(16, 16)
//                    .setXPos(xOffset + (slotWidth - 16) / 2)
//                    .setMaxYPos(slot.yPos() - 1, false)
//                    .setHoverText(e -> toolkit.i18n(tile.ioModes[fi].get().getName()))
//                    .onButtonPressed(btn -> tile.ioModes[fi].set(tile.ioModes[fi].get().nextMode(Screen.hasShiftDown() == (btn == 0))));
//
//            button.addChild(new GuiTexture(16, 16, () -> DEGuiTextures.get(tile.ioModes[fi].get().getSpriteName()))
//                    .setRelPos(0, 0));
//
//            toolkit.createEnergyBar(column)
//                    .setItemSupplier(() -> tile.itemsCombined.getStackInSlot(fi))
//                    .setDisabled(() -> !EnergyUtils.isEnergyItem(tile.itemsCombined.getStackInSlot(fi)))
//                    .setShaderEnabled(() -> tile.itemsCombined.getStackInSlot(fi).getCapability(CapabilityOP.OP).isPresent())
//                    .setDrawHoveringText(() -> EnergyUtils.isEnergyItem(tile.itemsCombined.getStackInSlot(fi)))
//                    .setXPos(xOffset + (slotWidth - barWidth) / 2)
//                    .setYPos(column.yPos() + (slotWidth - barWidth) / 2)
//                    .setXSize(barWidth)
//                    .setMaxYPos(button.yPos() - 1, true);
//
//            column.addChild(new GuiElement(){
//                @Override
//                public void renderElement(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
//                    PoseStack mStack = new PoseStack();
//                    mStack.translate(xPos() + (fi == 0 ? 5 : fi == 2 ? 7 : 4)  , yPos() + 2, 0);
//                    mStack.scale(2, 2, 2);
//                    fontRenderer.draw(mStack, RenderTileEnergyTransfuser.TEXT[fi].getVisualOrderText(), 0, 0, tile.ioModes[fi].get().getColour());
//                }
//            }.setPosAndSize(slot));
//
//            holder.addChild(column);
//            xOffset += slotWidth + (totalWidth - (slotWidth * 4)) / 3;
//        }
//        Rectangle rect = holder.getEnclosingRect();
//        holder.setRawPos(rect.x, rect.y);
//        holder.setSize(rect);
//        temp.background.addChild(holder);
//        toolkit.placeInside(holder, temp.background, GuiToolkit.LayoutPos.TOP_CENTER, 0, 0);
//        holder.setYPos(temp.title.maxYPos() + padding);
//
//        GuiButton button = toolkit.createButton_old("", temp.background, true, 0)
//                .setSize(20, 20)
//                .setMaxXPos(holder.xPos() - 2, false)
//                .setMaxYPos(holder.maxYPos(), false)
//                .setHoverText(e -> toolkit.i18n(tile.balancedMode.get() ? "balanced_charge" : "sequential_charge"))
//                .onPressed(() -> tile.balancedMode.invert());
//
//        button.addChild(new GuiTexture(18, 18, () -> DEGuiTextures.get("transfuser/" + (tile.balancedMode.get() ? "balanced_charge" : "sequential_charge")))
//                .setRelPos(1, 1));
//    }

    public static class Screen extends ModularGuiScreen {
        public Screen() {
            super(new GuiEnergyTransfuser());
        }
    }
}


/* I/O modes.
Input       Charge Item
Output      Discharge Item
Buffer      Item is a buffer
*/