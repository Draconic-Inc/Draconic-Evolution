package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.brandonscore.client.gui.modulargui.MGuiElementBase;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.IMGuiListener;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.MGuiEntityFilter;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.ModuleBuilder;
import com.brandon3055.brandonscore.client.gui.modulargui.modularelements.*;
import com.brandon3055.brandonscore.client.utils.GuiHelper;
import com.brandon3055.brandonscore.utils.InfoHelper;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEntityDetector;
import com.brandon3055.draconicevolution.inventory.ContainerDummy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by brandon3055 on 23/10/2016.
 */
public class GuiEntityDetector extends ModularGuiContainer<ContainerDummy> implements IMGuiListener {

    private EntityPlayer player;
    private TileEntityDetector tile;
    private MGuiEntityFilter filter;
    private MGuiEnergyBar energyBar;
    private MGuiButton incrementRange;
    private MGuiButton decrementRange;
    private MGuiButton incrementPulse;
    private MGuiButton decrementPulse;
    private MGuiButton incrementRSMin;
    private MGuiButton decrementRSMin;
    private MGuiButton incrementRSMax;
    private MGuiButton decrementRSMax;
    private MGuiButtonToggle outputMode;

    public GuiEntityDetector(EntityPlayer player, TileEntityDetector tile) {
        super(new ContainerDummy(tile, player, 19, 124));
        this.player = player;
        this.tile = tile;
        this.xSize = 198;
        this.ySize = 204;
    }

    @Override
    public void initGui() {
        super.initGui();
        manager.clear();
        manager.add(new MGuiLabel(this, guiLeft(), guiTop() + 3, xSize, 12, DEFeatures.entityDetector.getLocalizedName()).setTextColour(InfoHelper.GUI_TITLE));

        //region Entity Filter

        filter = new MGuiEntityFilter(this, tile.entityFilter, guiLeft() + 4, guiTop() + 3, xSize - 8, ySize - 6) {
            @Override
            public void addExtraElements(MGuiEntityFilter filter) {
                addChild(new MGuiButtonSolid(GuiEntityDetector.this, "CLOSE_FILTER", GuiEntityDetector.this.guiLeft() + GuiEntityDetector.this.xSize - 44, GuiEntityDetector.this.guiTop() + GuiEntityDetector.this.ySize - 15, 40, 12, I18n.format("gui.de.button.close")));
                filter.childElements.addFirst(MGuiBackground.newGenericBackground(GuiEntityDetector.this, GuiEntityDetector.this.guiLeft(), GuiEntityDetector.this.guiTop(), GuiEntityDetector.this.xSize, GuiEntityDetector.this.ySize));
            }
        };

        filter.playerNames = tile.playerNames;
        filter.setEnabled(false);
        manager.add(filter, 2);
        hideInventorySlots(false);

        //endregion

        //region Main GUI

        int guiLeft = guiLeft() + 9;
        int xSize = this.xSize - 18;
        manager.add(MGuiBackground.newGenericBackground(this, guiLeft, guiTop(), xSize, ySize));


        ModuleBuilder.RawColumns builder = new ModuleBuilder.RawColumns(guiLeft + 9, guiTop() + 14, 3, 12, 1);

        builder.add(decrementRange = new MGuiButton(this, 0, 0, 12, 12, "-"));
        MGuiElementBase element;
        builder.add(element = new MGuiLabel(this, 0, 0, xSize - 45, 12, ""){
            @Override
            public String getDisplayString() {
                return I18n.format("gui.entityDetector.range") + ": " + tile.RANGE.value + " blocks.";
            }
        });
        element.addChild(new MGuiHoverPopup(this, new String[] {I18n.format("gui.entityDetector.range.info")}, element).setHoverDelay(10));
        builder.add(incrementRange = new MGuiButton(this, 0, 0, 12, 12, "+"));

        builder.add(decrementPulse = new MGuiButton(this, 0, 0, 12, 12, "-"));
        builder.add(element = new MGuiLabel(this, 0, 0, xSize - 45, 12, ""){
            @Override
            public String getDisplayString() {
                return I18n.format("gui.entityDetector.pulseDelay") + ": " + tile.PULSE_RATE.value / 20D + " sec.";
            }
        });
        element.addChild(new MGuiHoverPopup(this, new String[] {I18n.format("gui.entityDetector.pulseDelay.info")}, element).setHoverDelay(10));
        builder.add(incrementPulse = new MGuiButton(this, 0, 0, 12, 12, "+"));

        builder.add(decrementRSMin = new MGuiButton(this, 0, 0, 12, 12, "-"));
        builder.add(element = new MGuiLabel(this, 0, 0, xSize - 45, 12, ""){
            @Override
            public String getDisplayString() {
                return I18n.format("gui.entityDetector.rsMin") + ": " + tile.RS_MIN_DETECTION.value;
            }
        });
        element.addChild(new MGuiHoverPopup(this, new String[] {I18n.format("gui.entityDetector.rsMin.info")}, element).setHoverDelay(10));
        builder.add(incrementRSMin = new MGuiButton(this, 0, 0, 12, 12, "+"));

        builder.add(decrementRSMax = new MGuiButton(this, 0, 0, 12, 12, "-"));
        builder.add(element = new MGuiLabel(this, 0, 0, xSize - 45, 12, ""){
            @Override
            public String getDisplayString() {
                return I18n.format("gui.entityDetector.rsMax") + ": " + tile.RS_MAX_DETECTION.value;
            }
        });
        element.addChild(new MGuiHoverPopup(this, new String[] {I18n.format("gui.entityDetector.rsMax.info")}, element).setHoverDelay(10));
        builder.add(incrementRSMax = new MGuiButton(this, 0, 0, 12, 12, "+"));

        builder.finish(manager, 1);

        manager.add(outputMode = new MGuiButtonToggle(this, guiLeft + 9, builder.builderEndY + 1, xSize - 18, 12, "") {
            @Override
            public String getDisplayString() {
                return tile.PULSE_RS_MODE.value ? I18n.format("gui.entityDetector.outputPulse") : I18n.format("gui.entityDetector.outputContin");
            }

            @Override
            public boolean isPressed() {
                return tile.PULSE_RS_MODE.value;
            }
        }, 1);
        outputMode.setToolTip(new String[] {I18n.format("gui.entityDetector.output.info")}).setToolTipDelay(10);

        manager.add(element = new MGuiButton(this, guiLeft + 9, outputMode.yPos + 13, xSize - 18, 12, I18n.format("gui.entityDetector.filter")).setButtonName("OPEN_FILTER"));

        manager.add(element = new MGuiLabel(this, guiLeft + 9, element.yPos + 13, xSize - 18, 12, ""){
            @Override
            public String getDisplayString() {
                return I18n.format("gui.entityDetector.energyCost") + ": " + Utils.formatNumber(tile.getPulseCost()) + " RF";
            }
        }, 1);
        element.addChild(new MGuiHoverPopup(this, new String[] {I18n.format("gui.entityDetector.energyCost.info")}, element).setHoverDelay(10));

        manager.add(energyBar = new MGuiEnergyBar(this, guiLeft + 9, guiTop() + 106, xSize - 18, 14).setEnergyHandler(tile).setHorizontal(true), 1);
        manager.add(new MGuiElementBase(this) {
            @Override
            public void renderBackgroundLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
                GlStateManager.color(1, 1, 1);
                GuiHelper.drawPlayerSlots(GuiEntityDetector.this, guiLeft() + (GuiEntityDetector.this.xSize / 2), guiTop() + 123, true);
            }
        });

        //endregion

        manager.initElements();
    }

    @Override
    public void onMGuiEvent(String eventString, MGuiElementBase eventElement) {
        if (eventElement instanceof MGuiButton && ((MGuiButton) eventElement).buttonName.equals("CLOSE_FILTER")) {
            hideInventorySlots(false);
            filter.setEnabled(false);
            energyBar.setEnabled(true);
            filter.onClose();
        }
        else if (eventElement instanceof MGuiButton && ((MGuiButton) eventElement).buttonName.equals("OPEN_FILTER")) {
            hideInventorySlots(true);
            filter.setEnabled(true);
            energyBar.setEnabled(false);
        }
        else if (eventElement == decrementRange || eventElement == incrementRange) {
            tile.adjustRange(eventElement == decrementRange, isShiftKeyDown());
        }
        else if (eventElement == decrementPulse || eventElement == incrementPulse) {
            tile.adjustPulseRate(eventElement == decrementPulse, isShiftKeyDown());
        }
        else if (eventElement == decrementRSMin|| eventElement == incrementRSMin) {
            tile.adjustRSMin(eventElement == decrementRSMin, isShiftKeyDown());
        }
        else if (eventElement == decrementRSMax || eventElement == incrementRSMax) {
            tile.adjustRSMax(eventElement == decrementRSMax, isShiftKeyDown());
        }
        else if (eventElement == outputMode) {
            tile.togglePulsemode();
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }
}
