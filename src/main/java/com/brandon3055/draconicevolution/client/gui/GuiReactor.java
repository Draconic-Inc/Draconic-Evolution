package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElement;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElementManager;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiButton;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiBorderedRect;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiLabel;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiSlideIndicator;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiTexture;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.GuiAlign;
import com.brandon3055.brandonscore.utils.InfoHelper;
import com.brandon3055.brandonscore.utils.MathUtils;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorComponent;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorComponent.RSMode;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.client.DEGuiTextures;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileReactorCore;
import com.brandon3055.draconicevolution.inventory.ContainerReactor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by brandon3055 on 10/02/2017.
 */
//public class GuiReactor extends ModularGuiContainer<ContainerReactor> {
public class GuiReactor extends AbstractContainerScreen<ContainerReactor> { //Temp fix for tile

    private Player player;
    private final TileReactorCore tile;
    public TileReactorComponent component = null;
    private static double compPanelAnim = 0;
    private static boolean compPanelExtended = false;
    private GuiElement<?> compPanel;

    protected GuiToolkit<GuiReactor> toolkit = new GuiToolkit<>(this, 248, 222);

    public GuiReactor(ContainerReactor container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
        this.player = inv.player;
        this.tile = container.tile;
    }

    @Override
    public void addElements(GuiElementManager manager) {
        List<GuiElement<?>> exclusions = new ArrayList<>();

        //region Background Elements
        manager.addChild(compPanel = new GuiBorderedRect(leftPos + imageWidth, topPos + 125, 0, 91));
        exclusions.add(compPanel);
        manager.setJeiExclusions(() -> exclusions);
        GuiTexture background = manager.addChild(new GuiTexture(leftPos, topPos, imageWidth, imageHeight, DEGuiTextures.get("reactor/background")) {
            @Override
            public void renderElement(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
                super.renderElement(minecraft, mouseX, mouseY, partialTicks);
                RenderTileReactorCore.renderGUI(tile, leftPos + imageWidth / 2, topPos + 70);
            }

        }.onReload(e -> e.setPosAndSize(leftPos, topPos, imageWidth, imageHeight)));

        background.addChild(new GuiBorderedRect(leftPos + 12, topPos + 138, 162, 77)
                .setEnabledCallback(() -> tile.reactorState.get() != TileReactorCore.ReactorState.COLD)
                .setColours(0xFF000000, 0xFFFFFFFF));
        //endregion

        //region Status Labels

        int y = topPos + 140;
        background.addChild(new GuiLabel(leftPos + 10 + 5, y, 162, 8, I18n.get("gui.draconicevolution.reactor.core_volume"))
                .setEnabledCallback(() -> tile.reactorState.get() != TileReactorCore.ReactorState.COLD && tile.reactorState.get() != TileReactorCore.ReactorState.BEYOND_HOPE)
                .setAlignment(GuiAlign.LEFT)
                .setShadow(false)
                .setTextColour(0x00C0FF)
                .setHoverText(I18n.get("gui.draconicevolution.reactor.core_volume.info"))
                .setHoverTextDelay(2));

        background.addChild(new GuiLabel(leftPos + 13 + 5, y += 8, 162, 8, "")
                .setEnabledCallback(() -> tile.reactorState.get() != TileReactorCore.ReactorState.COLD && tile.reactorState.get() != TileReactorCore.ReactorState.BEYOND_HOPE)
                .setDisplaySupplier(() -> MathUtils.round((tile.reactableFuel.get() + tile.convertedFuel.get()) / 1296D, 100) + " m^3")
                .setAlignment(GuiAlign.LEFT)
                .setShadow(false)
                .setTextColour(0xB0B0B0));

        background.addChild(new GuiLabel(leftPos + 10 + 5, y += 11, 162, 8, I18n.get("gui.draconicevolution.reactor.gen_rate"))
                .setEnabledCallback(() -> tile.reactorState.get() != TileReactorCore.ReactorState.COLD && tile.reactorState.get() != TileReactorCore.ReactorState.BEYOND_HOPE)
                .setAlignment(GuiAlign.LEFT)
                .setShadow(false)
                .setTextColour(0x00C0FF)
                .setHoverText(I18n.get("gui.draconicevolution.reactor.gen_rate.info"))
                .setHoverTextDelay(2));

        background.addChild(new GuiLabel(leftPos + 13 + 5, y += 8, 162, 8, "")
                .setEnabledCallback(() -> tile.reactorState.get() != TileReactorCore.ReactorState.COLD && tile.reactorState.get() != TileReactorCore.ReactorState.BEYOND_HOPE)
                .setDisplaySupplier(() -> Utils.addCommas((int) tile.generationRate.get()) + " OP/t")
                .setAlignment(GuiAlign.LEFT)
                .setShadow(false)
                .setTextColour(0xB0B0B0));

        background.addChild(new GuiLabel(leftPos + 10 + 5, y += 11, 162, 8, I18n.get("gui.draconicevolution.reactor.field_rate"))
                .setEnabledCallback(() -> tile.reactorState.get() != TileReactorCore.ReactorState.COLD && tile.reactorState.get() != TileReactorCore.ReactorState.BEYOND_HOPE)
                .setAlignment(GuiAlign.LEFT)
                .setShadow(false)
                .setTextColour(0x00C0FF)
                .setHoverText(I18n.get("gui.draconicevolution.reactor.field_rate.info"))
                .setHoverTextDelay(2));

        background.addChild(new GuiLabel(leftPos + 13 + 5, y += 8, 162, 8, "")
                .setEnabledCallback(() -> tile.reactorState.get() != TileReactorCore.ReactorState.COLD && tile.reactorState.get() != TileReactorCore.ReactorState.BEYOND_HOPE)
                .setDisplaySupplier(() -> {
                    double inputRate = tile.fieldDrain.get() / (1D - (tile.shieldCharge.get() / tile.maxShieldCharge.get()));
                    return Utils.addCommas((int) Math.min(inputRate, Integer.MAX_VALUE)) + " OP/t";
                })
                .setAlignment(GuiAlign.LEFT)
                .setShadow(false)
                .setTextColour(0xB0B0B0));

        background.addChild(new GuiLabel(leftPos + 10 + 5, y += 11, 162, 8, I18n.get("gui.draconicevolution.reactor.convert_rate"))
                .setEnabledCallback(() -> tile.reactorState.get() != TileReactorCore.ReactorState.COLD && tile.reactorState.get() != TileReactorCore.ReactorState.BEYOND_HOPE)
                .setAlignment(GuiAlign.LEFT)
                .setShadow(false)
                .setTextColour(0x00C0FF)
                .setHoverText(I18n.get("gui.draconicevolution.reactor.convert_rate.info"))
                .setHoverTextDelay(2));

        background.addChild(new GuiLabel(leftPos + 13 + 5, y += 8, 162, 8, "")
                .setEnabledCallback(() -> tile.reactorState.get() != TileReactorCore.ReactorState.COLD && tile.reactorState.get() != TileReactorCore.ReactorState.BEYOND_HOPE)
                .setDisplaySupplier(() -> Utils.addCommas((int) Math.round(tile.fuelUseRate.get() * 1000000D)) + " nb/t")
                .setAlignment(GuiAlign.LEFT)
                .setShadow(false)
                .setTextColour(0xB0B0B0));

        background.addChild(new GuiLabel(leftPos + 13 + 5, topPos + 139, 161, 77, I18n.get("gui.draconicevolution.reactor.go_boom_now"))
                .setEnabledCallback(() -> tile.reactorState.get() == TileReactorCore.ReactorState.BEYOND_HOPE)
                .setAlignment(GuiAlign.LEFT)
                .setWrap(true)
                .setShadow(false)
                .setTextColour(0xB0B0B0));
        //endregion

        //region Slots, Misc labels and gauges

        toolkit.createPlayerSlots(background, false)
                .setXPos(background.xPos() + 12)
                .setMaxYPos(background.maxYPos() - 8, false)
                .setEnabledCallback(() -> tile.reactorState.get() == TileReactorCore.ReactorState.COLD);

        toolkit.createSlots(background, 3, 1, 0)
                .setPos(leftPos + 182, topPos + 148)
                .setEnabledCallback(() -> tile.reactorState.get() == TileReactorCore.ReactorState.COLD);

        toolkit.createSlots(background, 3, 1, 0)
                .setPos(leftPos + 182, topPos + 179)
                .setEnabledCallback(() -> tile.reactorState.get() == TileReactorCore.ReactorState.COLD);


        background.addChild(new GuiLabel(leftPos, topPos + 2, imageWidth, 12, I18n.get("gui.draconicevolution.reactor.title"))
                .setAlignment(GuiAlign.CENTER)
                .setTextColour(InfoHelper.GUI_TITLE));

        background.addChild(new GuiLabel(leftPos + 182, topPos + 139, 54, 8, I18n.get("gui.draconicevolution.reactor.fuel_in"))
                .setEnabledCallback(() -> tile.reactorState.get() == TileReactorCore.ReactorState.COLD)
                .setAlignment(GuiAlign.CENTER)
                .setTrim(false));

        background.addChild(new GuiLabel(leftPos + 182, topPos + 170, 54, 8, I18n.get("gui.draconicevolution.reactor.chaos_out"))
                .setEnabledCallback(() -> tile.reactorState.get() == TileReactorCore.ReactorState.COLD)
                .setAlignment(GuiAlign.CENTER)
                .setTrim(false));

        background.addChild(new GuiLabel(leftPos + 7, topPos + 127, imageWidth, 12, "")
                .setShadowStateSupplier(() -> tile.reactorState.get() != TileReactorCore.ReactorState.BEYOND_HOPE)
                .setDisplaySupplier(() -> {
                    String s = tile.reactorState.get().localize();
                    if (tile.reactorState.get() == TileReactorCore.ReactorState.BEYOND_HOPE && ClientEventHandler.elapsedTicks % 10 > 5) {
                        s = ChatFormatting.DARK_RED + "**" + s + "**";
                    } else if (tile.reactorState.get() == TileReactorCore.ReactorState.BEYOND_HOPE) {
                        s = ChatFormatting.DARK_RED + "--" + s + "--";
                    }
                    return ChatFormatting.GOLD + I18n.get("gui.draconicevolution.reactor.status") + ": " + s;
                }).setAlignment(GuiAlign.LEFT));

        background.addChild(new GuiSlideIndicator(() -> tile.temperature.get() / TileReactorCore.MAX_TEMPERATURE)
                .setPos(leftPos + 10, topPos + 5)
                .setSize(16, 112)
                .setSlideElement(new GuiTexture(16, 8, DEGuiTextures.get("reactor/pointer")))
                .setOffsets(-2, -1)
                .setHoverText(element -> getTempStats())
                .setHoverTextDelay(5)
        );

        background.addChild(new GuiSlideIndicator(() -> tile.shieldCharge.get() / Math.max(tile.maxShieldCharge.get(), 1))
                .setPos(leftPos + 34, topPos + 5)
                .setSize(16, 112)
                .setSlideElement(new GuiTexture(16, 8, DEGuiTextures.get("reactor/pointer")))
                .setOffsets(-2, -1)
                .setHoverText(element -> getShieldStats())
                .setHoverTextDelay(5)
        );

        background.addChild(new GuiSlideIndicator(() -> tile.saturation.get() / (double) Math.max(tile.maxSaturation.get(), 1))
                .setPos(leftPos + 198, topPos + 5)
                .setSize(16, 112)
                .setSlideElement(new GuiTexture(16, 8, DEGuiTextures.get("reactor/pointer")))
                .setOffsets(-2, -1)
                .setHoverText(element -> getSaturationStats())
                .setHoverTextDelay(5)
        );

        background.addChild(new GuiSlideIndicator(() -> tile.convertedFuel.get() / Math.max(tile.reactableFuel.get() + tile.convertedFuel.get(), 1))
                .setPos(leftPos + 222, topPos + 5)
                .setSize(16, 112)
                .setSlideElement(new GuiTexture(16, 8, DEGuiTextures.get("reactor/pointer")))
                .setOffsets(-2, -1)
                .setHoverText(element -> getFuelStats())
                .setHoverTextDelay(5)
        );

        //endregion

        //region Buttons

        background.addChild(new GuiButton(leftPos + 177, topPos + 199, 64, 14, I18n.get("gui.draconicevolution.reactor.charge"))
                .setEnabledCallback(tile::canCharge)
                .setBorderColours(0xFF555555, 0xFF777777)
                .setFillColour(0xFF000000)
                .setTrim(false)
                .onPressed(tile::chargeReactor));

        background.addChild(new GuiButton(leftPos + 177, topPos + 182, 64, 14, I18n.get("gui.draconicevolution.reactor.activate"))
                .setEnabledCallback(tile::canActivate)
                .setBorderColours(0xFF555555, 0xFF777777)
                .setFillColour(0xFF000000)
                .setTrim(false)
                .onPressed(tile::activateReactor));

        background.addChild(new GuiButton(leftPos + 177, topPos + 199, 64, 14, I18n.get("gui.draconicevolution.reactor.shutdown"))
                .setEnabledCallback(tile::canStop)
                .setBorderColours(0xFF555555, 0xFF777777)
                .setFillColour(0xFF000000)
                .setTrim(false)
                .onPressed(tile::shutdownReactor));

        background.addChild(new GuiButton(leftPos + 177, topPos + 165, 64, 14, I18n.get("gui.draconicevolution.reactor.sas"))
                .setEnabledCallback(() -> tile.reactorState.get() != TileReactorCore.ReactorState.COLD && tile.reactorState.get() != TileReactorCore.ReactorState.BEYOND_HOPE)
                .setBorderColours(0xFF555555, 0xFF777777)
                .setRectFillColourGetter((hovering, disabled) -> tile.failSafeMode.get() ? 0xFF4040FF : 0xFF000000)
                .setTrim(false)
                .onPressed(tile::toggleFailSafe)
                .setHoverText(I18n.get("gui.draconicevolution.reactor.sas.info")));

        background.addChild(new GuiButton(leftPos + 177, topPos + 138, 64, 24, I18n.get("gui.draconicevolution.reactor.rs_mode").replaceAll("\\\\n", "\n"))
                .setEnabledCallback(() -> tile.reactorState.get() != TileReactorCore.ReactorState.COLD && component != null && tile.reactorState.get() != TileReactorCore.ReactorState.BEYOND_HOPE)
                .setWrap(true)
                .setBorderColours(0xFF555555, 0xFF777777)
                .setFillColour(0xFF000000)
                .onPressed(() -> compPanelExtended = !compPanelExtended)
                .setInsets(5, 0, 5, 0)
                .setHoverText(I18n.get("gui.draconicevolution.reactor.rs_mode.info")));

        background.addChild(new GuiLabel(leftPos + 175, topPos + 138, 68, 80, "ETE")
                .setEnabledCallback(() -> tile.reactorState.get() == TileReactorCore.ReactorState.BEYOND_HOPE)
                .setDisplaySupplier(() -> "Estimated\nTime\nUntil\nDetonation\n\n" + ChatFormatting.UNDERLINE + (tile.explosionCountdown.get() >= 0 ? (tile.explosionCountdown.get() / 20) + "s" : "Calculating.."))
                .setWrap(true)
                .setShadow(false)
                .setTextColour(0xFF0000));

        y = 0;
        for (RSMode mode : RSMode.values()) {
            background.addChild(new GuiButton(leftPos + imageWidth + 2, topPos + 127 + y, 76, 10, I18n.get("gui.draconicevolution.reactor.rs_mode_" + mode.name().toLowerCase(Locale.ENGLISH)))
                    .setEnabledCallback(() -> compPanelAnim == 1 && component != null)
                    .setRectFillColourGetter((hovering, disabled) -> {
                        if (component != null && component.rsMode.get() == mode) {
                            return 0xFFAA0000;
                        } else if (hovering) {
                            return 0xFF656565;
                        }
                        return 0xFF000000;
                    })
                    .setRectBorderColourGetter((hovering, disabled) -> {
                        if (component != null && component.rsMode.get() == mode) {
                            return 0xFFAA0000;
                        } else if (hovering) {
                            return 0xFF656565;
                        }
                        return 0xFF000000;
                    })
                    .onPressed(() -> {
                        if (component != null) {
                            component.setRSMode(player, mode);
                        }
                    })
                    .setHoverText(I18n.get("gui.draconicevolution.reactor.rs_mode_" + mode.name().toLowerCase(Locale.ENGLISH) + ".info"))
                    .setTrim(false));
            y += 11;
        }

        //endregion
    }

    public List<String> getTempStats() {
        List<String> list = new ArrayList<>();
        list.add(I18n.get("gui.draconicevolution.reactor.reaction_temp"));
        list.add(MathUtils.round(tile.temperature.get(), 10) + "C");
        return list;
    }

    public List<String> getShieldStats() {
        List<String> list = new ArrayList<>();
        list.add(I18n.get("gui.draconicevolution.reactor.field_strength"));
        if (tile.maxShieldCharge.get() > 0) {
            list.add(MathUtils.round(tile.shieldCharge.get() / tile.maxShieldCharge.get() * 100D, 100D) + "%");
        }
        list.add(Utils.addCommas((int) tile.shieldCharge.get()) + " / " + Utils.addCommas((int) tile.maxShieldCharge.get()));
        return list;
    }

    public List<String> getSaturationStats() {
        List<String> list = new ArrayList<>();
        list.add(I18n.get("gui.draconicevolution.reactor.energy_saturation"));
        if (tile.maxSaturation.get() > 0) {
            list.add(MathUtils.round((double) tile.saturation.get() / (double) tile.maxSaturation.get() * 100D, 100D) + "%");
        }
        list.add(Utils.addCommas(tile.saturation.get()) + " / " + Utils.addCommas(tile.maxSaturation.get()));
        return list;
    }

    public List<String> getFuelStats() {
        List<String> list = new ArrayList<>();
        list.add(I18n.get("gui.draconicevolution.reactor.fuel_conversion"));
        if (tile.reactableFuel.get() + tile.convertedFuel.get() > 0) {
            list.add(MathUtils.round(tile.convertedFuel.get() / (tile.reactableFuel.get() + tile.convertedFuel.get()) * 100D, 100D) + "%");
        }
        list.add(MathUtils.round(tile.convertedFuel.get(), 100) + " / " + MathUtils.round(tile.convertedFuel.get() + tile.reactableFuel.get(), 100));
        return list;
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        if (tile.reactorState.get() == TileReactorCore.ReactorState.COLD != container.fuelSlots) {
            container.setSlotState();
        }

        if (compPanelExtended && (compPanelAnim < 1 || compPanel.xSize() != 80)) {
            compPanelAnim += 0.1;
            if (compPanelAnim > 1) {
                compPanelAnim = 1;
            }
            compPanel.setXSize((int) (compPanelAnim * 80));
        } else if (!compPanelExtended && compPanelAnim > 0) {
            compPanelAnim -= 0.1;
            if (compPanelAnim < 0) {
                compPanelAnim = 0;
            }
            compPanel.setXSize((int) (compPanelAnim * 80));
        }

        if (compPanel.isEnabled() && compPanelAnim == 0) {
            compPanel.setEnabled(false);
        } else if (!compPanel.isEnabled() && compPanelAnim > 0) {
            compPanel.setEnabled(true);
        }
    }
}
