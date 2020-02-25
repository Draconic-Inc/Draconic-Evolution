//package com.brandon3055.draconicevolution.client.gui;
//
//import com.brandon3055.brandonscore.client.gui.modulargui.GuiElementManager;
//import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
//import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFlowGate;
//import com.brandon3055.draconicevolution.inventory.ContainerDummy;
//import net.minecraft.entity.player.PlayerEntity;
//
///**
// * Created by brandon3055 on 15/11/2016.
// */
//public class GuiFlowGate extends ModularGuiContainer {
//    public TileFlowGate tile;
////    private MGuiTextField minField;
////    private MGuiTextField maxField;
////    private long ltMin = -1;
////    private long ltMax = -1;
////    //region Validator
////    private Predicate<String> fieldValidator = new Predicate<String>() {
////        @Override
////        public boolean apply(@Nullable String input) {
////            try {
////                if (input.equals("")) {
////                    return true;
////                }
////                Long.parseLong(input);
////            }
////            catch (Exception e) {
////                return false;
////            }
////
////            return true;
////        }
////    };
////    //endregion
//
//    public GuiFlowGate(TileFlowGate tile, PlayerEntity player) {
//        super(new ContainerDummy(tile, player, -1, -1));
////        this.tile = tile;
////        this.xSize = 197;
////        this.ySize = 88;
//    }
//
//    @Override
//    public void addElements(GuiElementManager manager) {
//
//    }
//
////    @Override
////    public void initGui() {
////        super.initGui();
////        manager.clear();
////
////        manager.add(MGuiBackground.newGenericBackground(this, guiLeft(), guiTop(), xSize, ySize));
////        manager.add(new MGuiLabel(this, guiLeft(), guiTop() + 4, xSize, 8, I18n.format(tile.getName())).setTextColour(InfoHelper.GUI_TITLE));
////
////        if (tile.flowOverridden.get()) {
////            manager.add(new MGuiLabel(this, guiLeft(), guiTop(), xSize, ySize, I18n.format("gui.de.flowGateOverridden.txt")).setTextColour(0x00FF00));
////        }
////        else {
////            manager.add(new MGuiLabel(this, guiLeft(), guiTop() + 16, xSize, 8, I18n.format("gui.de.flowGateRSHigh.name")).setTextColour(0xff0000).setShadow(false).setAlignment(EnumAlignment.LEFT));
////            manager.add(maxField = new MGuiTextField(this, guiLeft() + 4, guiTop() + 26, xSize - 44, 16, fontRenderer));
////            manager.add(new MGuiButton(this, "SAVE_HIGH", guiLeft() + xSize - 39, guiTop() + 26, 35, 16, I18n.format("gui.button.save")));
////
////            manager.add(new MGuiLabel(this, guiLeft(), guiTop() + 44, xSize, 8, I18n.format("gui.de.flowGateRSLow.name")).setTextColour(0x660000).setShadow(false).setAlignment(EnumAlignment.LEFT));
////            manager.add(minField = new MGuiTextField(this, guiLeft() + 4, guiTop() + 54, xSize - 44, 16, fontRenderer));
////            manager.add(new MGuiButton(this, "SAVE_LOW", guiLeft() + xSize - 39, guiTop() + 54, 35, 16, I18n.format("gui.button.save")));
////
////            minField.setValidator(fieldValidator);
////            maxField.setValidator(fieldValidator);
////        }
////
////        manager.add(new MGuiLabel(this, guiLeft(), guiTop() + ySize - 12, xSize, 8, "") {
////            @Override
////            public String getDisplayString() {
////                return I18n.format("gui.de.flowGateCurrentFlow.name") + ": " + Utils.addCommas(tile.getFlow()) + tile.getUnits();
////            }
////        }.setTextColour(0x2c2c2c).setShadow(false));
////        manager.initElements();
////    }
////
////    @Override
////    public void onMGuiEvent(String eventString, MGuiElementBase eventElement) {
////        if (eventElement instanceof MGuiButton && ((MGuiButton) eventElement).buttonName.equals("SAVE_HIGH")) {
////            tile.setMax(maxField.getText());
////        }
////        else if (eventElement instanceof MGuiButton && ((MGuiButton) eventElement).buttonName.equals("SAVE_LOW")) {
////            tile.setMin(minField.getText());
////        }
////    }
////
////    @Override
////    public void updateScreen() {
////        super.updateScreen();
////
////        if (tile.minFlow.get() != ltMin && minField != null) {
////            ltMin = tile.minFlow.get();
////            minField.setText(String.valueOf(ltMin));
////        }
////
////        if (tile.maxFlow.get() != ltMax && maxField != null) {
////            ltMax = tile.maxFlow.get();
////            maxField.setText(String.valueOf(ltMax));
////        }
////
////        if (maxField != null && !maxField.isFocused() && maxField.getText().equals("")) {
////            maxField.setText("0");
////        }
////        if (minField != null && !minField.isFocused() && minField.getText().equals("")) {
////            minField.setText("0");
////        }
////    }
//}
