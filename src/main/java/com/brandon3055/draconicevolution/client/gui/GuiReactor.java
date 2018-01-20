package com.brandon3055.draconicevolution.client.gui;

import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.client.ResourceHelperBC;
import com.brandon3055.brandonscore.client.gui.modulargui_old.MGuiElementBase;
import com.brandon3055.brandonscore.client.gui.modulargui_old.ModularGuiContainer;
import com.brandon3055.brandonscore.client.gui.modulargui_old.lib.EnumAlignment;
import com.brandon3055.brandonscore.client.gui.modulargui_old.lib.IMGuiListener;
import com.brandon3055.brandonscore.client.gui.modulargui_old.modularelements.*;
import com.brandon3055.brandonscore.client.utils.GuiHelper;
import com.brandon3055.brandonscore.utils.InfoHelper;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.api.IJEIClearence;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorComponent;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorComponent.RSMode;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileReactorCore;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.brandon3055.draconicevolution.inventory.ContainerReactor;
import com.brandon3055.draconicevolution.utils.DETextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by brandon3055 on 10/02/2017.
 */
public class GuiReactor extends ModularGuiContainer<ContainerReactor> implements IMGuiListener, IJEIClearence {

    private EntityPlayer player;
    private final TileReactorCore tile;
    public TileReactorComponent component = null;
    private static double compPanelAnim = 0;
    private static boolean compPanelExtended = false;
    private MGuiElementBase compPanel;

    public GuiReactor(EntityPlayer player, TileReactorCore tile) {
        super(new ContainerReactor(player, tile));
        this.player = player;
        this.tile = tile;
        this.xSize = 248;
        this.ySize = 222;
    }

    @Override
    public void initGui() {
        super.initGui();
        manager.clear();

        //region Background Elements

        manager.add(compPanel = new MGuiBorderedRect(this, guiLeft + xSize, guiTop + 125, 0, 91));
        manager.add(new MGuiBackground(this, guiLeft, guiTop, 0, 0, xSize, ySize, "draconicevolution:" + DETextures.GUI_REACTOR) {
            @Override
            public void renderForegroundLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
                super.renderForegroundLayer(minecraft, mouseX, mouseY, partialTicks);
                RenderTileReactorCore.renderGUI(tile, guiLeft + xSize / 2, guiTop + 70);
            }
        });
        manager.add(new MGuiBorderedRect(this, guiLeft + 12, guiTop + 138, 162, 77) {
            @Override
            public boolean isEnabled() {
                return tile.reactorState.value != TileReactorCore.ReactorState.COLD;
            }
        });

        //endregion

        //region Status Labels

        int y = guiTop + 140;
        manager.add(new MGuiLabel(this, guiLeft + 10, y, 162, 8, I18n.format("gui.reactor.coreVolume.info")) {
            @Override
            public boolean isEnabled() {
                return tile.reactorState.value != TileReactorCore.ReactorState.COLD && tile.reactorState.value != TileReactorCore.ReactorState.BEYOND_HOPE;
            }
        }.setAlignment(EnumAlignment.LEFT).setShadow(false).setTextColour(0x00C0FF).addChild(new MGuiHoverPopup(this, new String[]{I18n.format("gui.reactor.coreVolume.txt")}).setHoverDelay(2)));
        manager.add(new MGuiLabel(this, guiLeft + 13, y += 8, 162, 8, "") {
            @Override
            public boolean isEnabled() {
                return tile.reactorState.value != TileReactorCore.ReactorState.COLD && tile.reactorState.value != TileReactorCore.ReactorState.BEYOND_HOPE;
            }

            @Override
            public String getDisplayString() {
                return Utils.round((tile.reactableFuel.value + tile.convertedFuel.value) / 1296D, 100) + "m^3";
            }
        }.setAlignment(EnumAlignment.LEFT).setShadow(false).setTextColour(0xB0B0B0));
        manager.add(new MGuiLabel(this, guiLeft + 10, y += 11, 162, 8, I18n.format("gui.reactor.genRate.info")) {
            @Override
            public boolean isEnabled() {
                return tile.reactorState.value != TileReactorCore.ReactorState.COLD && tile.reactorState.value != TileReactorCore.ReactorState.BEYOND_HOPE;
            }
        }.setAlignment(EnumAlignment.LEFT).setShadow(false).setTextColour(0x00C0FF).addChild(new MGuiHoverPopup(this, new String[]{I18n.format("gui.reactor.genRate.txt")}).setHoverDelay(2)));
        manager.add(new MGuiLabel(this, guiLeft + 13, y += 8, 162, 8, "") {
            @Override
            public boolean isEnabled() {
                return tile.reactorState.value != TileReactorCore.ReactorState.COLD && tile.reactorState.value != TileReactorCore.ReactorState.BEYOND_HOPE;
            }

            @Override
            public String getDisplayString() {
                return Utils.addCommas((int) tile.generationRate.value) + " RF/t";
            }
        }.setAlignment(EnumAlignment.LEFT).setShadow(false).setTextColour(0xB0B0B0));
        manager.add(new MGuiLabel(this, guiLeft + 10, y += 11, 162, 8, I18n.format("gui.reactor.fieldInputRate.info")) {
            @Override
            public boolean isEnabled() {
                return tile.reactorState.value != TileReactorCore.ReactorState.COLD && tile.reactorState.value != TileReactorCore.ReactorState.BEYOND_HOPE;
            }
        }.setAlignment(EnumAlignment.LEFT).setShadow(false).setTextColour(0x00C0FF).addChild(new MGuiHoverPopup(this, new String[]{I18n.format("gui.reactor.inputRate.txt")}).setHoverDelay(2)));
        manager.add(new MGuiLabel(this, guiLeft + 13, y += 8, 162, 8, "") {
            @Override
            public boolean isEnabled() {
                return tile.reactorState.value != TileReactorCore.ReactorState.COLD && tile.reactorState.value != TileReactorCore.ReactorState.BEYOND_HOPE;
            }

            @Override
            public String getDisplayString() {
                double inputRate = tile.fieldDrain.value / (1D - (tile.shieldCharge.value / tile.maxShieldCharge.value));
                return Utils.addCommas((int) Math.min(inputRate, Integer.MAX_VALUE)) + "RF/t";
            }
        }.setAlignment(EnumAlignment.LEFT).setShadow(false).setTextColour(0xB0B0B0));
        manager.add(new MGuiLabel(this, guiLeft + 10, y += 11, 162, 8, I18n.format("gui.reactor.fuelConversionRate.info")) {
            @Override
            public boolean isEnabled() {
                return tile.reactorState.value != TileReactorCore.ReactorState.COLD && tile.reactorState.value != TileReactorCore.ReactorState.BEYOND_HOPE;
            }
        }.setAlignment(EnumAlignment.LEFT).setShadow(false).setTextColour(0x00C0FF).addChild(new MGuiHoverPopup(this, new String[]{I18n.format("gui.reactor.conversionRate.txt")}).setHoverDelay(2)));
        manager.add(new MGuiLabel(this, guiLeft + 13, y += 8, 162, 8, "") {
            @Override
            public boolean isEnabled() {
                return tile.reactorState.value != TileReactorCore.ReactorState.COLD && tile.reactorState.value != TileReactorCore.ReactorState.BEYOND_HOPE;
            }

            @Override
            public String getDisplayString() {
                return Utils.addCommas((int) Math.round(tile.fuelUseRate.value * 1000000D)) + "nb/t";
            }
        }.setAlignment(EnumAlignment.LEFT).setShadow(false).setTextColour(0xB0B0B0));

        manager.add(new MGuiLabel(this, guiLeft + 13, guiTop + 139, 161, 77, "Emergency shield reserve is now active but it wont last long! There is no way to stop the overload the stabilizers are fried. I suggest you run!") {
            @Override
            public boolean isEnabled() {
                return tile.reactorState.value == TileReactorCore.ReactorState.BEYOND_HOPE;
            }
        }.setWrap(true).setAlignment(EnumAlignment.LEFT).setShadow(false).setTextColour(0xB0B0B0));

        //endregion

        //region Slots, Misc labels and gauges

        manager.add(new MGuiElementBase(this) {
            @Override
            public void renderBackgroundLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
                if (tile.reactorState.value == TileReactorCore.ReactorState.COLD) {
//                    drawTexturedModalRect(guiLeft + 12, guiTop + 193, 10, 193, 162, 4);
                    GuiHelper.drawPlayerSlots(GuiReactor.this, guiLeft + 43 - 31, guiTop + 139, false);
                    ResourceHelperBC.bindTexture("textures/gui/bc_widgets.png");

                    for (int x = 0; x < 3; x++) {
                        drawTexturedModalRect(guiLeft + 182 + (x * 18), guiTop + 148, 138, 0, 18, 18);
                    }

                    for (int x = 0; x < 3; x++) {
                        drawTexturedModalRect(guiLeft + 182 + (x * 18), guiTop + 179, 138, 0, 18, 18);
                    }
                }
            }
        });

        manager.add(new MGuiLabel(this, guiLeft, guiTop + 2, xSize, 12, I18n.format("gui.reactor.draconicReactor.txt")).setAlignment(EnumAlignment.CENTER).setTextColour(InfoHelper.GUI_TITLE));
        manager.add(new MGuiLabel(this, guiLeft + 182, guiTop + 139, 54, 8, I18n.format("gui.reactor.fuelIn.txt")) {
            @Override
            public boolean isEnabled() {
                return tile.reactorState.value == TileReactorCore.ReactorState.COLD;
            }
        }.setAlignment(EnumAlignment.CENTER));
        manager.add(new MGuiLabel(this, guiLeft + 182, guiTop + 170, 54, 8, I18n.format("gui.reactor.chaosOut.txt")) {
            @Override
            public boolean isEnabled() {
                return tile.reactorState.value == TileReactorCore.ReactorState.COLD;
            }
        }.setAlignment(EnumAlignment.CENTER));
        manager.add(new MGuiLabel(this, guiLeft + 7, guiTop + 127, xSize, 12, "") {
            @Override
            public String getDisplayString() {
                String s = tile.reactorState.value.localize();
                if (tile.reactorState.value == TileReactorCore.ReactorState.BEYOND_HOPE && ClientEventHandler.elapsedTicks % 10 > 5) {
                    s = TextFormatting.DARK_RED + "**" + s + "**";
                }
                else if (tile.reactorState.value == TileReactorCore.ReactorState.BEYOND_HOPE) {
                    s = TextFormatting.DARK_RED + "--" + s + "--";
                }

                return TextFormatting.GOLD + I18n.format("gui.reactor.status.info") + ": " + s;
            }

            @Override
            public boolean isEnabled() {
                return super.isEnabled();
            }

            @Override
            public boolean getDropShadow() {
                return tile.reactorState.value != TileReactorCore.ReactorState.BEYOND_HOPE;
            }

        }.setAlignment(EnumAlignment.LEFT));

        manager.add(new MGuiTexturedPointer(this, guiLeft + 11, guiTop + 5, 14, 112, 0, 222, 5, ResourceHelperDE.getResource(DETextures.GUI_REACTOR)) {
            @Override
            public double getPos() {
                return MathHelper.clip(tile.temperature.value / TileReactorCore.MAX_TEMPERATURE, 0, 1);
            }
        }.addChild(new MGuiHoverPopup(this) {
            @Override
            public List<String> getToolTip() {
                return getTempStats();
            }
        }.setHoverDelay(5)));
        manager.add(new MGuiTexturedPointer(this, guiLeft + 35, guiTop + 5, 14, 112, 0, 222, 5, ResourceHelperDE.getResource(DETextures.GUI_REACTOR)) {
            @Override
            public double getPos() {
                return MathHelper.clip(tile.shieldCharge.value / Math.max(tile.maxShieldCharge.value, 1), 0, 1);
            }
        }.addChild(new MGuiHoverPopup(this) {
            @Override
            public List<String> getToolTip() {
                return getShieldStats();
            }
        }.setHoverDelay(5)));

        manager.add(new MGuiTexturedPointer(this, guiLeft + 199, guiTop + 5, 14, 112, 0, 222, 5, ResourceHelperDE.getResource(DETextures.GUI_REACTOR)) {
            @Override
            public double getPos() {
                return MathHelper.clip(tile.saturation.value / (double) Math.max(tile.maxSaturation.value, 1), 0, 1);
            }
        }.addChild(new MGuiHoverPopup(this) {
            @Override
            public List<String> getToolTip() {
                return getSaturationStats();
            }
        }.setHoverDelay(5)));


        manager.add(new MGuiTexturedPointer(this, guiLeft + 223, guiTop + 5, 14, 112, 0, 222, 5, ResourceHelperDE.getResource(DETextures.GUI_REACTOR)) {
            @Override
            public double getPos() {
                return MathHelper.clip(tile.convertedFuel.value / Math.max(tile.reactableFuel.value + tile.convertedFuel.value, 1), 0, 1);
            }
        }.addChild(new MGuiHoverPopup(this) {
            @Override
            public List<String> getToolTip() {
                return getFuelStats();
            }
        }.setHoverDelay(5)));

        //endregion

        //region Buttons

        manager.add(new MGuiButtonSolid(this, "CHARGE", guiLeft + 182, guiTop + 199, 54, 14, I18n.format("gui.reactor.charge.btn")) {
            @Override
            public boolean isEnabled() {
                return tile.canCharge();
            }
        });
        manager.add(new MGuiButtonSolid(this, "ACTIVATE", guiLeft + 182, guiTop + 182, 54, 14, I18n.format("gui.reactor.activate.btn")) {
            @Override
            public boolean isEnabled() {
                return tile.canActivate();
            }
        });
        manager.add(new MGuiButtonSolid(this, "SHUTDOWN", guiLeft + 182, guiTop + 199, 54, 14, I18n.format("gui.reactor.shutdown.btn")) {
            @Override
            public boolean isEnabled() {
                return tile.canStop();
            }
        });
        manager.add(new MGuiButtonSolid(this, "FAIL_SAFE", guiLeft + 182, guiTop + 165, 54, 14, I18n.format("gui.reactor.failSafe.btn")) {
            @Override
            public boolean isEnabled() {
                return tile.reactorState.value != TileReactorCore.ReactorState.COLD && tile.reactorState.value != TileReactorCore.ReactorState.BEYOND_HOPE;
            }

            @Override
            public void onPressed(int mouseX, int mouseY, int mouseButton) {
                tile.toggleFailSafe();
            }

            @Override
            public int getFillColour(boolean hovering, boolean disabled) {
                if (tile.failSafeMode.value) {
                    return 0xFF4040FF;
                }
                return super.getFillColour(hovering, disabled);
            }

        }.addChild(new MGuiHoverPopup(this, new String[]{I18n.format("gui.reactor.failSafe.txt")})));

        manager.add(new MGuiLabel(this, guiLeft + 180, guiTop + 138, 58, 24, I18n.format("gui.reactor.rsMode.btn").replaceAll("\\\\n", "\n")) {
            @Override
            public boolean isEnabled() {
                return tile.reactorState.value != TileReactorCore.ReactorState.COLD && component != null && tile.reactorState.value != TileReactorCore.ReactorState.BEYOND_HOPE;
            }
        }.setWrap(true).addChild(new MGuiButtonSolid(this, "TOGGLE_COMP_P", guiLeft + 182, guiTop + 138, 54, 24, "") {
            @Override
            public String getDisplayString() {
                return super.getDisplayString();
            }
        }).addChild(new MGuiHoverPopup(this, new String[]{I18n.format("gui.reactor.redstoneMode.txt")})));

        manager.add(new MGuiLabel(this, guiLeft + 175, guiTop + 138, 68, 80, "ETE") {
            @Override
            public String getDisplayString() {
                return "Estimated\nTime\nUntil\nDetonation\n\n" + TextFormatting.UNDERLINE + (tile.explosionCountdown.value >= 0 ? (tile.explosionCountdown.value / 20) + "s" : "Calculating..");
            }

            @Override
            public boolean isEnabled() {
                return tile.reactorState.value == TileReactorCore.ReactorState.BEYOND_HOPE;
            }
        }.setWrap(true).setTextColour(0xFF0000).setShadow(false));

        y = 0;
        for (final RSMode mode : RSMode.values()) {
            manager.add(new MGuiButtonSolid(this, "RS_T", guiLeft + xSize + 2, guiTop + 127 + y, 66, 10, I18n.format("gui.reactor.rsMode_" + mode.name().toLowerCase() + ".btn")) {
                @Override
                public boolean isEnabled() {
                    return compPanelAnim == 1 && component != null;
                }

                @Override
                public int getFillColour(boolean hovering, boolean disabled) {
                    if (component != null && component.rsMode.value == mode) {
                        return 0xFFAA0000;
                    }
                    else if (hovering) {
                        return 0xFF656565;
                    }
                    return super.getFillColour(hovering, disabled);
                }

                @Override
                public int getBorderColour(boolean hovering, boolean disabled) {
                    return getFillColour(hovering, disabled);
                }

                @Override
                public void onPressed(int mouseX, int mouseY, int mouseButton) {
                    if (component != null) {
                        component.setRSMode(player, mode);
                    }
                }

            }.setColours(0xFF454545, 0xFF454545, 0xFF454545).addChild(new MGuiHoverPopup(this, new String[]{I18n.format("gui.reactor.rsMode_" + mode.name().toLowerCase() + ".txt")})));
            y += 11;
        }

        //endregion

        manager.initElements();
    }

    @Override
    public void onMGuiEvent(String eventString, MGuiElementBase eventElement) {
        if (eventElement instanceof MGuiButton) {
            if (((MGuiButton) eventElement).buttonName.equals("CHARGE")) {
                tile.chargeReactor();
            }
            else if (((MGuiButton) eventElement).buttonName.equals("ACTIVATE")) {
                tile.activateReactor();
            }
            else if (((MGuiButton) eventElement).buttonName.equals("SHUTDOWN")) {
                tile.shutdownReactor();
            }
            else if (((MGuiButton) eventElement).buttonName.equals("TOGGLE_COMP_P")) {
                compPanelExtended = !compPanelExtended;
            }
        }
    }

    public List<String> getTempStats() {
        List<String> list = new ArrayList<>();
        list.add(I18n.format("gui.reactor.reactionTemp.info"));
        list.add(Utils.round(tile.temperature.value, 10) + "C");
        return list;
    }

    public List<String> getShieldStats() {
        List<String> list = new ArrayList<>();
        list.add(I18n.format("gui.reactor.fieldStrength.info"));
        if (tile.maxShieldCharge.value > 0) {
            list.add(Utils.round(tile.shieldCharge.value / tile.maxShieldCharge.value * 100D, 100D) + "%");
        }
        list.add(Utils.addCommas((int) tile.shieldCharge.value) + " / " + Utils.addCommas((int) tile.maxShieldCharge.value));
        return list;
    }

    public List<String> getSaturationStats() {
        List<String> list = new ArrayList<>();
        list.add(I18n.format("gui.reactor.energySaturation.info"));
        if (tile.maxSaturation.value > 0) {
            list.add(Utils.round((double) tile.saturation.value / (double) tile.maxSaturation.value * 100D, 100D) + "%");
        }
        list.add(Utils.addCommas(tile.saturation.value) + " / " + Utils.addCommas(tile.maxSaturation.value));
        return list;
    }

    public List<String> getFuelStats() {
        List<String> list = new ArrayList<>();
        list.add(I18n.format("gui.reactor.fuelConversion.info"));
        if (tile.reactableFuel.value + tile.convertedFuel.value > 0) {
            list.add(Utils.round(tile.convertedFuel.value / (tile.reactableFuel.value + tile.convertedFuel.value) * 100D, 100D) + "%");
        }
        list.add(Utils.round(tile.convertedFuel.value, 100) + " / " + Utils.round(tile.convertedFuel.value + tile.reactableFuel.value, 100));
        return list;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if (tile.reactorState.value == TileReactorCore.ReactorState.COLD != container.fuelSlots) {
            container.setSlotState();
        }

        if (compPanelExtended && (compPanelAnim < 1 || compPanel.xSize != 70)) {
            compPanelAnim += 0.1;
            if (compPanelAnim > 1) {
                compPanelAnim = 1;
            }
            compPanel.xSize = (int) (compPanelAnim * 70);
        }
        else if (!compPanelExtended && compPanelAnim > 0) {
            compPanelAnim -= 0.1;
            if (compPanelAnim < 0) {
                compPanelAnim = 0;
            }
            compPanel.xSize = (int) (compPanelAnim * 70);
        }

        if (compPanel.isEnabled() && compPanelAnim == 0) {
            compPanel.setEnabled(false);
        }
        else if (!compPanel.isEnabled() && compPanelAnim > 0) {
            compPanel.setEnabled(true);
        }
    }

    @Override
    public List<Rectangle> getGuiExtraAreas() {
        if (compPanel == null) return Collections.emptyList();
        return Collections.singletonList(new Rectangle(compPanel.xPos, compPanel.yPos, compPanel.xSize, compPanel.ySize));
    }
}
