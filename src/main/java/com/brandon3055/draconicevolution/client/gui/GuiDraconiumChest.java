package com.brandon3055.draconicevolution.client.gui;

import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.client.gui.modulargui.IModularGui;
import com.brandon3055.brandonscore.client.gui.modulargui.MGuiElementBase;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.IMGuiListener;
import com.brandon3055.brandonscore.client.gui.modulargui.modularelements.*;
import com.brandon3055.brandonscore.client.utils.GuiHelper;
import com.brandon3055.brandonscore.lib.StackReference;
import com.brandon3055.brandonscore.utils.InfoHelper;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDraconiumChest;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileDraconiumChest;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.brandon3055.draconicevolution.inventory.ContainerDraconiumChest;
import com.brandon3055.draconicevolution.utils.DETextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.brandon3055.draconicevolution.blocks.tileentity.TileDraconiumChest.AutoSmeltMode;

/**
 * Created by brandon3055 on 4/06/2017.
 */
public class GuiDraconiumChest extends ModularGuiContainer<ContainerDraconiumChest> implements IMGuiListener {

    public final TileDraconiumChest tile;
    public MGuiButtonToggle regionEditButton;
    public RegionEditor regionEditor;

    public GuiDraconiumChest(TileDraconiumChest tile, ContainerDraconiumChest container) {
        super(container);
        this.tile = tile;
        this.xSize = 480;
        this.ySize = 266;
    }

    @Override
    public void initGui() {
        super.initGui();
        manager.clear();

        manager.add(new MGuiBackground(this, guiLeft(), guiTop(), 0, 0, xSize(), ySize(), "draconicevolution:" + DETextures.GUI_DRACONIUM_CHEST).setTextureSize(512, 512));

        //region Background and Displays
        //Burn Rate
        manager.add(new MGuiBackground(this, guiLeft() + 45, guiTop() + 227, 0, 282, 88, 0, "draconicevolution:" + DETextures.GUI_DRACONIUM_CHEST) {
            @Override
            public boolean onUpdate() {
                double progress = tile.burnRate.value;
                ySize = (int) (14 * progress);
                yPos = guiTop() + 227 + (int) (14 * (1 - progress));
                textureY = 282 + (14 - ySize);
                return super.onUpdate();
            }
        }.setTextureSize(512, 512));
        //Energy Bar
        manager.add(new MGuiBackground(this, guiLeft() + 44, guiTop() + 245, 0, 266, 0, 16, "draconicevolution:" + DETextures.GUI_DRACONIUM_CHEST) {
            @Override
            public boolean onUpdate() {
                double progress = tile.energySync.value / (double) tile.energyStorage.getMaxEnergyStored();
                xSize = (int) (90 * progress);
                return super.onUpdate();
            }
        }.setTextureSize(512, 512));
        manager.add(new MGuiElementBase(this, guiLeft() + 44, guiTop() + 245, 90, 16).addChild(new MGuiHoverPopup(this) {
            @Override
            public List<String> getToolTip() {
                List<String> list = new ArrayList<>();
                list.add(InfoHelper.ITC() + I18n.format("gui.de.energyStorage.txt"));
                list.add(InfoHelper.HITC() + Utils.formatNumber(tile.energySync.value) + " / " + Utils.formatNumber(tile.energyStorage.getMaxEnergyStored()));
                list.add(I18n.format("gui.draconiumChest.energyConsumption.info") + ": " + Utils.addCommas(tile.smeltEnergyPerTick.value) + " RF/t");
                return list;
            }
        }.setHoverDelay(0)));
        //Progress Bar
        manager.add(new MGuiBackground(this, guiLeft() + 139, guiTop() + 202, 0, 297, 16, 0, "draconicevolution:" + DETextures.GUI_DRACONIUM_CHEST) {
            @Override
            public boolean onUpdate() {
                double progress = Math.min(tile.smeltProgress.value / (double) tile.smeltTime.value, 1);
                ySize = (int) (22 * progress);
                yPos = guiTop() + 202 + (int) (22 * (1 - progress));
                textureY = 297 + (22 - ySize);
                return super.onUpdate();
            }
        }.setTextureSize(512, 512));
        manager.add(new MGuiElementBase(this, guiLeft() + 45, guiTop() + 227, 88, 13).addChild(new MGuiHoverPopup(this) {
            @Override
            public List<String> getToolTip() {
                List<String> list = new ArrayList<>();
                list.add(I18n.format("gui.draconiumChest.processTime.info") + ": " + Utils.round(tile.smeltTime.value / 20D, 100) + "s");
                return list;
            }
        }.setHoverDelay(0)));


        manager.add(new MGuiStackIcon(this, guiLeft() + 7, guiTop() + 197, 36, 36, new StackReference("draconicevolution:draconic_core")).setToolTip(false)).addChild(new MGuiHoverPopup(this, new String[]{I18n.format("gui.draconiumChest.speedBoost.info")}));
        manager.add(new MGuiSlotRender(this, guiLeft() + 16, guiTop() + 206, 18, 18) {
            @Override
            public void renderBackgroundLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
                zOffset += 100;
                super.renderBackgroundLayer(minecraft, mouseX, mouseY, partialTicks);
                zOffset -= 100;
            }
        });

        //endregion

        //region Furnace Mode

        manager.add(new MGuiButtonSolid(this, "SELECT_MODE", guiLeft() + 44, guiTop() + 190, 90, 12, "") {
            @Override
            public String getDisplayString() {
                return I18n.format("gui.draconiumChest.fMode.btn") + ": " + TextFormatting.GOLD + I18n.format("gui.draconiumChest.fMode." + tile.autoSmeltMode.toString().toLowerCase() + ".btn");
            }
        }.addChild(new MGuiHoverPopup(this, new String[]{I18n.format("gui.draconiumChest.fMode.info")}).setHoverDelay(10)), 1);
//        manager.add(new MGuiSelectDialog(this, guiLeft() + 44, guiTop() + 190, 90, 12));

        //endregion

        //region Region Editor

        manager.add(regionEditButton = (MGuiButtonToggle) new MGuiButtonToggle(this, "EDIT_REGIONS", guiLeft() + 390, guiTop() + 188, 84, 12, I18n.format("gui.draconiumChest.ioRegions.btn")).setToolTip(new String[]{I18n.format("gui.draconiumChest.ioRegions.info")}));
//        manager.add(regionEditor = (RegionEditor) new RegionEditor(this, guiLeft() + 6, guiTop() + 6, tile).setEnabled(false));

        //endregion

        //region colour picker

        manager.add(new MGuiButtonSolid(this, "PICK_COLOUR", guiLeft() + 390, guiTop() + 248, 84, 12, I18n.format("gui.draconiumChest.pickColour.btn")) {
            @Override
            public int getFillColour(boolean hovering, boolean disabled) {
                return 0xFF000000 | tile.colour.value;
            }
        }.setToolTip(new String[]{I18n.format("gui.draconiumChest.pickColour.info")}));

        //endregion

        manager.initElements();

    }

    @Override
    public void onMGuiEvent(String eventString, MGuiElementBase element) {
        if (element instanceof MGuiButton && ((MGuiButton) element).buttonName.equals("SELECT_MODE")) {
            MGuiPopUpDialog dialog = new MGuiPopUpDialog(this, element.xPos, element.yPos + element.ySize, element.xSize, 51, element);
            dialog.setCloseOnCapturedClick(true);
            MGuiSelectDialog selector = new MGuiSelectDialog(this, dialog.xPos, dialog.yPos, dialog.xSize, dialog.ySize);

            List<MGuiElementBase> modeButtons = new ArrayList<>();
            for (AutoSmeltMode mode : AutoSmeltMode.values()) {
                MGuiButtonSolid modeButton = new MGuiButtonSolid(this, mode.name(), 0, 0, dialog.xSize - 2, 12, mode.name().toLowerCase());
                modeButton.addChild(new MGuiHoverPopup(this, new String[]{I18n.format("gui.draconiumChest.fMode." + mode.name().toLowerCase() + ".info")}));
                modeButtons.add(modeButton);
            }
            selector.setOptions(modeButtons, false);
            selector.setListener(this);
            selector.xSize -= 10;

            dialog.addChild(selector);
            dialog.show();
        }
        else if (eventString.equals("SELECTOR_PICK") && element instanceof MGuiButton) {
            AutoSmeltMode newMode = AutoSmeltMode.valueOf(((MGuiButton) element).buttonName);
            tile.setAutoSmeltMode(newMode);
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1F));
        }
        else if (element instanceof MGuiButtonToggle && ((MGuiButtonToggle) element).buttonName.equals("EDIT_REGIONS")) {
            if (((MGuiButtonToggle) element).isPressed()) {
                regionEditor = new RegionEditor(this, guiLeft() + 160, guiTop() + 188, tile);
                manager.add(regionEditor, 2);
                regionEditor.initElement();
            }
            else {
                if (regionEditor != null) {
                    manager.remove(regionEditor);
                    regionEditor = null;
                }
            }
        }
        else if (element instanceof MGuiButton && ((MGuiButton) element).buttonName.equals("PICK_COLOUR")) {
            MGuiColourPicker picker = new MGuiColourPicker(this, guiLeft() + (xSize() / 2) - 40, guiTop() + (ySize() / 2) - 40, element) {
                @Override
                public void onMGuiEvent(String eventString, MGuiElementBase eventElement) {
                    super.onMGuiEvent(eventString, eventElement);

                    if (eventElement instanceof MGuiSlider || eventElement instanceof MGuiTextField) {
                        listener.onMGuiEvent("COLOUR_PICKED", this);
                    }
                }
            };

            picker.setColour(tile.colour.value);
            picker.setListener(this);
            picker.setIncludeAlpha(false);
            picker.initElement();
            picker.cancelButton.setEnabled(false);
            picker.selectButton.xSize = 72;
            picker.show();
        }
        else if (element instanceof MGuiColourPicker && eventString.equals("COLOUR_PICKED")) {
            tile.setColour(((MGuiColourPicker) element).getColourARGB());
        }
    }

    @Override
    public void renderBackgroundLayer(int mouseX, int mouseY, float partialTicks) {
        super.renderBackgroundLayer(mouseX, mouseY, partialTicks);

        for (TileDraconiumChest.SlotRegion region : tile.slotRegions) {
            if (region.enabled && !region.isDefault && (!region.invalid || regionEditor != null)) {
                int fill = 0;
                if (regionEditor != null && regionEditor.editingRegion == region.regionID) {
                    fill = region.colour | 0x33000000;
                    if (region.invalid) {
                        fill = ClientEventHandler.elapsedTicks % 20 > 10 ? 0 : 0xFFFF0000;
                    }
                }
                GuiHelper.drawBorderedRect(guiLeft() + 6 + (region.xPos * 18), guiTop() + 6 + (region.yPos * 18), region.xSize * 18, region.ySize * 18, 1, fill, region.colour | 0xFF000000);
            }
        }
    }

    public class RegionEditor extends MGuiElementBase implements IMGuiListener {

        private final TileDraconiumChest tile;
        private int editingRegion = -1;
        private boolean selectingRegion = false;
        private int selectStartX = 0;
        private int selectStartY = 0;
        private int selectEndX = 0;
        private int selectEndY = 0;

        public RegionEditor(IModularGui modularGui, int xPos, int yPos, TileDraconiumChest tile) {
            super(modularGui, xPos, yPos, 162, 74);
            this.tile = tile;
        }

        @Override
        public void initElement() {
            //region Add Region Selection Buttons
            for (int i = 0; i < 6; i++) {
                int colour = tile.slotRegions[i].colour | 0xA0000000;
                int hColour = tile.slotRegions[i].colour | 0xFF000000;
                int x = xPos + 1 + ((i % 2) * ((xSize / 2)));
                int y = yPos + 1 + ((i / 2) * 19);
                addChild(new MGuiButtonSolid(modularGui, i, x, y, (xSize - 4) / 2, 18, I18n.format("gui.draconiumChest.editRegion.btn") + " " + (i + 1)).setColours(0xFF000000, colour, hColour).setListener(this).setButtonName("SELECT_REGION").addToGroup("R_SELECTORS"));
            }
            addChild(new MGuiButtonSolid(modularGui, 6, xPos + 1, yPos + ySize - 16, (xSize - 2), 15, I18n.format("gui.draconiumChest.editDefaultRegion.btn")).setColours(0xFF000000, 0xFFA0A0A0, 0xFFFFFFFF).setListener(this).setButtonName("SELECT_REGION").addToGroup("R_SELECTORS"));
            //endregion

            addChild(new MGuiLabel(modularGui, xPos + xSize, yPos + 14, xSize - 5, 60, I18n.format("gui.draconiumChest.regionSelect.info")).setWrap(true).setEnabled(false).addToGroup("REGION_EDITOR").addToGroup("EXCLUDE_DEFAULT"));
            addChild(new MGuiBorderedRect(modularGui, xPos + xSize + 5, yPos + 14, xSize - 15, 60).setEnabled(false).addToGroup("REGION_EDITOR").addToGroup("EXCLUDE_DEFAULT"));
            addChild(new MGuiButtonSolid(modularGui, "TOGGLE_REGION", xPos + 45, yPos + 1, xSize - 46, 12, "") {
                @Override
                public String getDisplayString() {
                    if (editingRegion < 0 || editingRegion >= 6) {
                        return "Error-InvalidGroup";
                    }
                    return tile.slotRegions[editingRegion].enabled ? TextFormatting.RED + I18n.format("gui.draconiumChest.disableRegion.btn") : TextFormatting.GREEN + I18n.format("gui.draconiumChest.enableRegion.btn");
                }
            }.setColours(0xFF000000, 0xFFB0B0B0, 0xFFFFFFFF).setListener(this).setEnabled(false).addToGroup("REGION_EDITOR").addToGroup("EXCLUDE_DEFAULT"));
            addChild(new MGuiButtonSolid(modularGui, "BACK", xPos + 1, yPos + 1, 43, 12, "Back").setColours(0xFF000000, 0xFFB0B0B0, 0xFFFFFFFF).setListener(this).setEnabled(false).addToGroup("REGION_EDITOR"));

            //region IO Buttons

            int yAlign = yPos + (ySize / 2) - 3;
            //IO Button Renderer
            addChild(new MGuiElementBase(modularGui, xPos + (xSize / 4) - 18, yAlign, 18, 18) {
                @Override
                public void renderBackgroundLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
                    if (editingRegion >= 0 && editingRegion < tile.slotRegions.length) {
                        drawBorderedRect(parent.xPos, parent.yPos, parent.xSize, parent.ySize, 1, 0, 0xFF000000 | tile.slotRegions[editingRegion].colour);
                    }
                    //Draw IO Button Outlines
                    drawBorderedRect(xPos - 20, yPos - 1, 58, 20, 1, 0, 0xFFFFFFFF);
                    drawBorderedRect(xPos - 1, yPos - 20, 20, 58, 1, 0, 0xFFFFFFFF);
                    drawBorderedRect(xPos + 58, yPos - 20, 20, 58, 1, 0, 0xFFFFFFFF);
                    drawBorderedRect(xPos + 58, yPos - 1, 20, 20, 1, 0, 0xFFFFFFFF);
                    drawBorderedRect(xPos + 99, yPos - 20, 20, 39, 1, 0, 0xFFFFFFFF);
                    drawBorderedRect(xPos + 99, yPos - 1, 20, 20, 1, 0, 0xFFFFFFFF);
                }

                @Override
                public void renderForegroundLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
                    ResourceHelperDE.bindTexture(DETextures.GUI_DRACONIUM_CHEST);

                    if (editingRegion >= 0 && editingRegion < tile.slotRegions.length) {
                        TileDraconiumChest.SlotRegion region = tile.slotRegions[editingRegion];
                        //Draw IO Button IO Icons
                        drawModalRectWithCustomSizedTexture(xPos + 61, yPos + 21, 0, 319 + region.getFaceIO(EnumFacing.DOWN) * 14, 14, 14, 512, 512);
                        drawModalRectWithCustomSizedTexture(xPos + 61, yPos - 17, 14, 319 + region.getFaceIO(EnumFacing.UP) * 14, 14, 14, 512, 512);
                        drawModalRectWithCustomSizedTexture(xPos + 2, yPos - 17, 14, 319 + region.getFaceIO(EnumFacing.SOUTH) * 14, 14, 14, 512, 512);
                        drawModalRectWithCustomSizedTexture(xPos + 2, yPos + 21, 0, 319 + region.getFaceIO(EnumFacing.NORTH) * 14, 14, 14, 512, 512);
                        drawModalRectWithCustomSizedTexture(xPos + 21, yPos + 2, 28, 319 + region.getFaceIO(EnumFacing.WEST) * 14, 14, 14, 512, 512);
                        drawModalRectWithCustomSizedTexture(xPos - 17, yPos + 2, 42, 319 + region.getFaceIO(EnumFacing.EAST) * 14, 14, 14, 512, 512);

                        drawModalRectWithCustomSizedTexture(xPos + 102, yPos - 17, 14, 319 + region.getFurnaceIO() * 14, 14, 14, 512, 512);
                    }
                }
            }.addToGroup("REGION_EDITOR").setEnabled(false));

            addChild(new MGuiElementBase(modularGui, xPos + (xSize / 4) - 18, yAlign, 18, 18) {
                @Override
                public void renderBackgroundLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
                    super.renderBackgroundLayer(minecraft, mouseX, mouseY, partialTicks);

                    ResourceHelperDE.bindTexture(DETextures.DRACONIUM_CHEST);

                    float red = (float) (50 + ((tile.colour.value >> 16) & 0xFF)) / 255f;
                    float green = (float) (50 + ((tile.colour.value >> 8) & 0xFF)) / 255f;
                    float blue = (float) (50 + (tile.colour.value & 0xFF)) / 255f;
                    GlStateManager.color(red, green, blue);

                    float scale = 16;
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(xPos + 1, yPos + 16, 500);
                    GlStateManager.rotate(-90, 1, 0, 0);
                    GlStateManager.scale(scale, scale, -scale);
                    RenderTileDraconiumChest.modelChest.renderAll();
                    GlStateManager.popMatrix();

                    GlStateManager.pushMatrix();
                    GlStateManager.translate(xPos + 60, yPos, 500);
                    GlStateManager.scale(scale, scale, -scale);
                    RenderTileDraconiumChest.modelChest.chestLid.rotateAngleX = 0;
                    RenderTileDraconiumChest.modelChest.renderAll();
                    GlStateManager.popMatrix();
                }
            }.addToGroup("REGION_EDITOR").setEnabled(false));
            addChild(new MGuiStackIcon(modularGui, xPos + (xSize / 4) + 82, yAlign, 18, 18, new StackReference(new ItemStack(Blocks.FURNACE))).setToolTip(false).addToGroup("REGION_EDITOR").setEnabled(false));

            Function<EnumFacing, List<String>> ioTooltipGetter = facing -> {
                List<String> list = new ArrayList<>();
                int io = 0;
                if (editingRegion >= 0) {
                    io = tile.slotRegions[editingRegion].getFaceIO(facing);
                }
                list.add(InfoHelper.ITC() + I18n.format("gui.draconiumChest.toggleIOMode." + (facing == null ? "furnace" : facing.getName()) + ".btn"));
                list.add(InfoHelper.HITC() + I18n.format("gui.draconiumChest.ioState" + io + ".btn"));
                return list;
            };
            //D
            addChild(new MGuiButtonSolid(modularGui, "TOGGLE_FACE_D", xPos + 81, yAlign + 19, 18, 18, "").setButtonId(0).setListener(this).addToGroup("REGION_EDITOR").setEnabled(false).addChild(new MGuiHoverPopup(modularGui) {
                @Override
                public List<String> getToolTip() {
                    return ioTooltipGetter.apply(EnumFacing.DOWN);
                }
            }));
            //U
            addChild(new MGuiButtonSolid(modularGui, "TOGGLE_FACE_U", xPos + 81, yAlign - 19, 18, 18, "").setButtonId(1).setListener(this).addToGroup("REGION_EDITOR").setEnabled(false).addChild(new MGuiHoverPopup(modularGui) {
                @Override
                public List<String> getToolTip() {
                    return ioTooltipGetter.apply(EnumFacing.UP);
                }
            }));

            //N
            addChild(new MGuiButtonSolid(modularGui, "TOGGLE_FACE_N", xPos + (xSize / 4) - 18, yAlign + 19, 18, 18, "").setButtonId(2).setListener(this).addToGroup("REGION_EDITOR").setEnabled(false).addChild(new MGuiHoverPopup(modularGui) {
                @Override
                public List<String> getToolTip() {
                    return ioTooltipGetter.apply(EnumFacing.NORTH);
                }
            }));
            //S
            addChild(new MGuiButtonSolid(modularGui, "TOGGLE_FACE_S", xPos + (xSize / 4) - 18, yAlign - 19, 18, 18, "").setButtonId(3).setListener(this).addToGroup("REGION_EDITOR").setEnabled(false).addChild(new MGuiHoverPopup(modularGui) {
                @Override
                public List<String> getToolTip() {
                    return ioTooltipGetter.apply(EnumFacing.SOUTH);
                }
            }));
            //W
            addChild(new MGuiButtonSolid(modularGui, "TOGGLE_FACE_W", xPos + (xSize / 4) + 1, yAlign, 18, 18, "").setButtonId(4).setListener(this).addToGroup("REGION_EDITOR").setEnabled(false).addChild(new MGuiHoverPopup(modularGui) {
                @Override
                public List<String> getToolTip() {
                    return ioTooltipGetter.apply(EnumFacing.WEST);
                }
            }));
            //E
            addChild(new MGuiButtonSolid(modularGui, "TOGGLE_FACE_E", xPos + (xSize / 4) - 37, yAlign, 18, 18, "").setButtonId(5).setListener(this).addToGroup("REGION_EDITOR").setEnabled(false).addChild(new MGuiHoverPopup(modularGui) {
                @Override
                public List<String> getToolTip() {
                    return ioTooltipGetter.apply(EnumFacing.EAST);
                }
            }));

            //F
            addChild(new MGuiButtonSolid(modularGui, "TOGGLE_FACE_E", xPos + 122, yAlign - 19, 18, 18, "").setButtonId(6).setListener(this).addToGroup("REGION_EDITOR").setEnabled(false).addChild(new MGuiHoverPopup(modularGui) {
                @Override
                public List<String> getToolTip() {
                    return ioTooltipGetter.apply(null);
                }
            }));


            //endregion

            super.initElement();
        }

        @Override
        public void renderBackgroundLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
            drawBorderedRect(xPos, yPos, xSize, ySize, 1, 0xFF000000, 0xFF000000);

            int yAlign = yPos + (ySize / 2) - 3;

//            //N
//            addChild(new MGuiButtonSolid(modularGui, "TOGGLE_FACE_N", xPos + (xSize / 4) - 18, yAlign + 19, 18, 18, "").setButtonId(2).addToGroup("REGION_EDITOR").setEnabled(false));
//            //S
//            addChild(new MGuiButtonSolid(modularGui, "TOGGLE_FACE_S", xPos + (xSize / 4) - 18, yAlign - 19, 18, 18, "").setButtonId(3).addToGroup("REGION_EDITOR").setEnabled(false));
//            //W
//            addChild(new MGuiButtonSolid(modularGui, "TOGGLE_FACE_W", xPos + (xSize / 4) + 1, yAlign, 18, 18, "").setButtonId(4).addToGroup("REGION_EDITOR").setEnabled(false));
//            //E
//            addChild(new MGuiButtonSolid(modularGui, "TOGGLE_FACE_E", xPos + (xSize / 4) - 37, yAlign, 18, 18, "").setButtonId(5).addToGroup("REGION_EDITOR").setEnabled(false));


            super.renderBackgroundLayer(minecraft, mouseX, mouseY, partialTicks);

//            if (selectingRegion) {
//                int startX = selectStartX < selectEndX ? selectStartX : selectEndX;//xPos + (selectStartX * 18);
//                int startY = selectStartY < selectEndY ? selectStartY : selectEndY;//yPos + (selectStartY * 18);
//                int width = selectEndX > selectStartX ? selectEndX : selectStartX;//(selectEndX - selectStartX) * 18;
//                int height = selectEndY > selectStartY ? selectEndY : selectStartY; //(selectEndY - selectStartY) * 18;
//                startX = (startX * 18) + xPos;
//                startY = (startY * 18) + yPos;
//                width = (width + 1) * 18;
//                height = (height + 1) * 18;
//
////                    LogHelper.dev(startX+" "+width+" - "+startY+" "+height);
//                drawBorderedRect(startX, startY, width, height, 1, 0x70FF0000, 0xFFFF0000);
//            }


        }

        @Override
        public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
            selectingRegion = false;

            //Handles region switching via clicking on regions
            int changeRegion = -1;
            int x = ((mouseX - 6 - guiLeft()) / 18);
            int y = ((mouseY - 6 - guiTop()) / 18);
            for (TileDraconiumChest.SlotRegion region : tile.slotRegions) {
                if (region.enabled && region.getRectangle().contains(x, y) && region.regionID != editingRegion && !region.isDefault) {
                    changeRegion = region.regionID;
                    break;
                }
            }

//            LogHelper.dev("Change: " + changeRegion);

            if (changeRegion != -1) {
                setChildGroupEnabled("R_SELECTORS", false);
                setChildGroupEnabled("REGION_EDITOR", true);
                editingRegion = changeRegion;
                setChildGroupEnabled("EXCLUDE_DEFAULT", editingRegion != 6);
                return true;
            }

            //Handles the start of a region click+drag selection
            if (editingRegion >= 0 && editingRegion < tile.slotRegions.length && GuiHelper.isInRect(guiLeft() + 6, guiTop() + 6, 26 * 18, 10 * 18, mouseX, mouseY)) {
                if (!tile.slotRegions[editingRegion].enabled) {
                    tile.slotRegions[editingRegion].enabled = true;
                    return true;
                }
                selectStartX = selectEndX = ((mouseX - 6 - guiLeft()) / 18);
                selectStartY = selectEndY = ((mouseY - 6 - guiTop()) / 18);
                for (TileDraconiumChest.SlotRegion region : tile.slotRegions) {
                    if (region.enabled && region.getRectangle().contains(selectStartX, selectStartY) && region.regionID != editingRegion) {
                        return true;
                    }
                }
                tile.slotRegions[editingRegion].xPos = selectStartX;
                tile.slotRegions[editingRegion].yPos = selectStartY;
                tile.slotRegions[editingRegion].xSize = 1;
                tile.slotRegions[editingRegion].ySize = 1;
                for (TileDraconiumChest.SlotRegion r : tile.slotRegions) {
                    r.validate();
                }
                selectingRegion = true;
                return true;
            }

            //Block normal slot clicks while region editing is in progress.
            return super.mouseClicked(mouseX, mouseY, mouseButton) || (regionEditButton != null && !regionEditButton.isMouseOver(mouseX, mouseY));
        }

        @Override
        public boolean mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
            if (selectingRegion && editingRegion >= 0 && editingRegion < tile.slotRegions.length) {
                selectEndX = MathHelper.clip(((mouseX - 6 - guiLeft()) / 18), 0, 25);
                selectEndY = MathHelper.clip(((mouseY - 6 - guiTop()) / 18), 0, 9);

                int xSize = selectEndX - selectStartX;
                int ySize = selectEndY - selectStartY;

                tile.slotRegions[editingRegion].xPos = xSize < 0 ? selectEndX : selectStartX;
                tile.slotRegions[editingRegion].yPos = ySize < 0 ? selectEndY : selectStartY;
                tile.slotRegions[editingRegion].xSize = Math.abs(xSize) + 1;
                tile.slotRegions[editingRegion].ySize = Math.abs(ySize) + 1;
                for (TileDraconiumChest.SlotRegion r : tile.slotRegions) {
                    r.validate();
                }
            }

            return super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        }

        @Override
        public boolean mouseReleased(int mouseX, int mouseY, int state) {
            selectingRegion = false;
            if (editingRegion >= 0 || editingRegion < tile.slotRegions.length) {
                for (TileDraconiumChest.SlotRegion r : tile.slotRegions) {
                    r.validate();
                }
                tile.setRegionState(editingRegion);
            }
            return super.mouseReleased(mouseX, mouseY, state);
        }

        @Override
        public void onMGuiEvent(String eventString, MGuiElementBase eventElement) {
            if (eventElement instanceof MGuiButton && eventElement.isInGroup("R_SELECTORS") && ((MGuiButton) eventElement).buttonName.equals("SELECT_REGION")) {
                setChildGroupEnabled("R_SELECTORS", false);
                setChildGroupEnabled("REGION_EDITOR", true);
                editingRegion = ((MGuiButton) eventElement).buttonId;
                setChildGroupEnabled("EXCLUDE_DEFAULT", editingRegion != 6);
            }
            else if (eventElement instanceof MGuiButton && ((MGuiButton) eventElement).buttonName.equals("TOGGLE_REGION")) {
                if (editingRegion >= 0 || editingRegion < tile.slotRegions.length) {
                    tile.slotRegions[editingRegion].enabled = !tile.slotRegions[editingRegion].enabled;
                    for (TileDraconiumChest.SlotRegion r : tile.slotRegions) {
                        r.validate();
                    }
                    tile.setRegionState(editingRegion);
                }
            }
            else if (eventElement instanceof MGuiButton && ((MGuiButton) eventElement).buttonName.equals("BACK")) {
                editingRegion = -1;
                setChildGroupEnabled("R_SELECTORS", true);
                setChildGroupEnabled("REGION_EDITOR", false);
            }
            else if (eventElement instanceof MGuiButton && ((MGuiButton) eventElement).buttonName.startsWith("TOGGLE_FACE_")) {
                if (editingRegion >= 0 || editingRegion < tile.slotRegions.length) {
                    int bID = ((MGuiButton) eventElement).buttonId;
                    EnumFacing facing = bID == 6 ? null : EnumFacing.getFront(bID);
                    int io = tile.slotRegions[editingRegion].getFaceIO(facing);
                    tile.slotRegions[editingRegion].setFaceIO(facing, io == 3 ? 0 : io + 1);
                    tile.setRegionState(editingRegion);
                }
            }
        }
    }
}
