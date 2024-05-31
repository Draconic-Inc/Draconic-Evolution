package com.brandon3055.draconicevolution.client.gui;

import codechicken.lib.gui.modular.ModularGui;
import codechicken.lib.gui.modular.ModularGuiContainer;
import codechicken.lib.gui.modular.elements.*;
import codechicken.lib.gui.modular.lib.Constraints;
import codechicken.lib.gui.modular.lib.GuiRender;
import codechicken.lib.gui.modular.lib.SliderState;
import codechicken.lib.gui.modular.lib.container.ContainerGuiProvider;
import codechicken.lib.gui.modular.lib.container.ContainerScreenAccess;
import codechicken.lib.gui.modular.lib.geometry.Align;
import codechicken.lib.gui.modular.lib.geometry.Axis;
import codechicken.lib.gui.modular.lib.geometry.Direction;
import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.api.TimeKeeper;
import com.brandon3055.brandonscore.client.BCGuiTextures;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.ButtonRow;
import com.brandon3055.brandonscore.utils.MathUtils;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorComponent;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorComponent.RSMode;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.client.DEGuiTextures;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileReactorCore;
import com.brandon3055.draconicevolution.inventory.ReactorMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

import static codechicken.lib.gui.modular.lib.geometry.Constraint.*;
import static codechicken.lib.gui.modular.lib.geometry.GeoParam.*;

/**
 * Created by brandon3055 on 10/02/2017.
 */
public class ReactorGui extends ContainerGuiProvider<ReactorMenu> {
    private static final GuiToolkit TOOLKIT = new GuiToolkit("gui.draconicevolution.reactor");
    public static final int GUI_WIDTH = 248;
    public static final int GUI_HEIGHT = 222;

    public TileReactorComponent component = null;
    private static boolean compPanelExtended = false;

    @Override
    public GuiElement<?> createRootElement(ModularGui gui) {
        GuiManipulable root = new GuiManipulable(gui).addMoveHandle(3).enableCursors(true);
        GuiTexture bg = new GuiTexture(root.getContentElement(), DEGuiTextures.themedGetter("reactor"));
        Constraints.bind(bg, root.getContentElement());
        return root;
    }

    @Override
    public void buildGui(ModularGui gui, ContainerScreenAccess<ReactorMenu> screenAccess) {
        gui.initStandardGui(GUI_WIDTH, GUI_HEIGHT);
        ReactorMenu menu = screenAccess.getMenu();
        TileReactorCore tile = menu.tile;
        GuiElement<?> root = gui.getRoot();
        TOOLKIT.createHeading(root, gui.getGuiTitle(), true);

        //Slots
        var playInv = GuiSlots.player(root, screenAccess, menu.main, menu.hotBar);
        playInv.stream().forEach(e -> e.setSlotTexture(slot -> BCGuiTextures.getThemed("slot")));
        playInv.container().setEnabled(() -> tile.reactorState.get() == TileReactorCore.ReactorState.COLD);
        Constraints.placeInside(playInv.container(), root, Constraints.LayoutPos.BOTTOM_LEFT, 12, -8);

        ButtonRow buttonRow = new ButtonRow(root, Direction.LEFT);
        buttonRow.addButton(TOOLKIT::createThemeButton);
        Constraints.placeInside(buttonRow, playInv.container(), Constraints.LayoutPos.TOP_RIGHT, 2, -12);

        GuiSlots input = new GuiSlots(root, screenAccess, menu.input, 3)
                .setEnabled(() -> tile.reactorState.get() == TileReactorCore.ReactorState.COLD)
                .constrain(TOP, relative(playInv.container().get(TOP), 9))
                .constrain(LEFT, midPoint(playInv.container().get(RIGHT), root.get(RIGHT), -29));
        Constraints.placeOutside(TOOLKIT.createHeading(input, TOOLKIT.translate("fuel_in")).setSize(32, 8).setScroll(false), input, Constraints.LayoutPos.TOP_CENTER, 0, -1);

        GuiSlots output = new GuiSlots(root, screenAccess, menu.output, 3)
                .setEnabled(() -> tile.reactorState.get() == TileReactorCore.ReactorState.COLD);
        Constraints.placeOutside(output, input, Constraints.LayoutPos.BOTTOM_CENTER, 0, 13);
        Constraints.placeOutside(TOOLKIT.createHeading(output, TOOLKIT.translate("chaos_out")).setSize(32, 8).setScroll(false), output, Constraints.LayoutPos.TOP_CENTER, 0, -1);

        //Buttons
        GuiButton chargeButton = TOOLKIT.createFlat3DButton(root, () -> TOOLKIT.translate("charge"))
                .setEnabled(tile::canCharge)
                .onPress(tile::chargeReactor);
        Constraints.size(chargeButton, 64, 14);
        Constraints.placeOutside(chargeButton, playInv.container(), Constraints.LayoutPos.BOTTOM_RIGHT, 3, -14);

        GuiButton activateButton = TOOLKIT.createFlat3DButton(root, () -> TOOLKIT.translate("activate"))
                .setEnabled(tile::canActivate)
                .onPress(tile::activateReactor);
        Constraints.size(activateButton, 64, 14);
        Constraints.placeOutside(activateButton, chargeButton, Constraints.LayoutPos.TOP_CENTER, 0, -3);

        GuiButton shutdownButton = TOOLKIT.createFlat3DButton(root, () -> TOOLKIT.translate("shutdown"))
                .setEnabled(tile::canStop)
                .onPress(tile::shutdownReactor);
        Constraints.bind(shutdownButton, chargeButton);

        GuiButton sasButton = TOOLKIT.createFlat3DButton(root, () -> TOOLKIT.translate("sas"))
                .setEnabled(() -> tile.reactorState.get() != TileReactorCore.ReactorState.COLD && tile.reactorState.get() != TileReactorCore.ReactorState.BEYOND_HOPE)
                .onPress(tile::toggleFailSafe)
                .setToggleMode(tile.failSafeMode::get)
                .setTooltip(TOOLKIT.translate("sas.info"));
        Constraints.size(sasButton, 64, 14);
        Constraints.placeOutside(sasButton, activateButton, Constraints.LayoutPos.TOP_CENTER, 0, -3);

        GuiButton rsButton = TOOLKIT.createFlat3DButton(root, () -> TOOLKIT.translate("rs_mode"))
                .setEnabled(() -> tile.reactorState.get() != TileReactorCore.ReactorState.COLD && component != null && tile.reactorState.get() != TileReactorCore.ReactorState.BEYOND_HOPE)
                .onPress(() -> compPanelExtended = !compPanelExtended)
                .setToggleMode(() -> compPanelExtended)
                .setTooltip(TOOLKIT.translate("rs_mode.info"))
                .constrain(TOP, match(playInv.container().get(TOP)))
                .constrain(LEFT, match(chargeButton.get(LEFT)));
        rsButton.getLabel().setWrap(true);
        Constraints.size(rsButton, 64, 24);

        //Status Panel
        GuiRectangle statusPanel = new GuiRectangle(root)
                .setEnabled(() -> tile.reactorState.get() != TileReactorCore.ReactorState.COLD)
                .fill(0xFF000000)
                .border(GuiToolkit.Palette.BG::border)
                .border(0xFFFFFFFF);
        Constraints.bind(statusPanel, playInv.container());

        //Volume
        GuiText last;
        last = new GuiText(statusPanel, TOOLKIT.translate("core_volume"))
                .setEnabled(() -> tile.reactorState.get() != TileReactorCore.ReactorState.COLD && tile.reactorState.get() != TileReactorCore.ReactorState.BEYOND_HOPE)
                .setAlignment(Align.LEFT)
                .setShadow(false)
                .setTextColour(0x00C0FF)
                .setTooltip(TOOLKIT.translate("core_volume.info"))
                .setTooltipDelay(2);
        Constraints.size(last, 160, 9);
        Constraints.placeInside(last, statusPanel, Constraints.LayoutPos.TOP_LEFT, 3, 1);

        last = new GuiText(statusPanel)
                .setEnabled(() -> tile.reactorState.get() != TileReactorCore.ReactorState.COLD && tile.reactorState.get() != TileReactorCore.ReactorState.BEYOND_HOPE)
                .setTextSupplier(() -> Component.literal(MathUtils.round((tile.reactableFuel.get() + tile.convertedFuel.get()) / 1296D, 100) + " m^3"))
                .setAlignment(Align.LEFT)
                .setShadow(false)
                .setTextColour(0xB0B0B0)
                .constrain(TOP, relative(last.get(BOTTOM), 0))
                .constrain(LEFT, relative(statusPanel.get(LEFT), 6));
        Constraints.size(last, 160, 9);

        //Rate
        last = new GuiText(statusPanel, TOOLKIT.translate("gen_rate"))
                .setEnabled(() -> tile.reactorState.get() != TileReactorCore.ReactorState.COLD && tile.reactorState.get() != TileReactorCore.ReactorState.BEYOND_HOPE)
                .setAlignment(Align.LEFT)
                .setShadow(false)
                .setTextColour(0x00C0FF)
                .setTooltip(TOOLKIT.translate("gen_rate.info"))
                .setTooltipDelay(2)
                .constrain(TOP, relative(last.get(BOTTOM), 0))
                .constrain(LEFT, relative(statusPanel.get(LEFT), 3));
        Constraints.size(last, 160, 9);

        last = new GuiText(statusPanel)
                .setEnabled(() -> tile.reactorState.get() != TileReactorCore.ReactorState.COLD && tile.reactorState.get() != TileReactorCore.ReactorState.BEYOND_HOPE)
                .setTextSupplier(() -> Component.literal(Utils.addCommas((int) tile.generationRate.get()) + " OP/t"))
                .setAlignment(Align.LEFT)
                .setShadow(false)
                .setTextColour(0xB0B0B0)
                .constrain(TOP, relative(last.get(BOTTOM), 0))
                .constrain(LEFT, relative(statusPanel.get(LEFT), 6));
        Constraints.size(last, 160, 9);

        //Field
        last = new GuiText(statusPanel, TOOLKIT.translate("field_rate"))
                .setEnabled(() -> tile.reactorState.get() != TileReactorCore.ReactorState.COLD && tile.reactorState.get() != TileReactorCore.ReactorState.BEYOND_HOPE)
                .setAlignment(Align.LEFT)
                .setShadow(false)
                .setTextColour(0x00C0FF)
                .setTooltip(TOOLKIT.translate("field_rate.info"))
                .setTooltipDelay(2)
                .constrain(TOP, relative(last.get(BOTTOM), 0))
                .constrain(LEFT, relative(statusPanel.get(LEFT), 3));
        Constraints.size(last, 160, 9);

        last = new GuiText(statusPanel)
                .setEnabled(() -> tile.reactorState.get() != TileReactorCore.ReactorState.COLD && tile.reactorState.get() != TileReactorCore.ReactorState.BEYOND_HOPE)
                .setTextSupplier(() -> {
                    double inputRate = tile.fieldDrain.get() / (1D - (tile.shieldCharge.get() / tile.maxShieldCharge.get()));
                    return Component.literal(Utils.addCommas((int) Math.min(inputRate, Integer.MAX_VALUE)) + " OP/t");
                })
                .setAlignment(Align.LEFT)
                .setShadow(false)
                .setTextColour(0xB0B0B0)
                .constrain(TOP, relative(last.get(BOTTOM), 0))
                .constrain(LEFT, relative(statusPanel.get(LEFT), 6));
        Constraints.size(last, 160, 9);

        //Conversion
        last = new GuiText(statusPanel, TOOLKIT.translate("convert_rate"))
                .setEnabled(() -> tile.reactorState.get() != TileReactorCore.ReactorState.COLD && tile.reactorState.get() != TileReactorCore.ReactorState.BEYOND_HOPE)
                .setAlignment(Align.LEFT)
                .setShadow(false)
                .setTextColour(0x00C0FF)
                .setTooltip(TOOLKIT.translate("convert_rate.info"))
                .setTooltipDelay(2)
                .constrain(TOP, relative(last.get(BOTTOM), 0))
                .constrain(LEFT, relative(statusPanel.get(LEFT), 3));
        Constraints.size(last, 160, 9);

        last = new GuiText(statusPanel)
                .setEnabled(() -> tile.reactorState.get() != TileReactorCore.ReactorState.COLD && tile.reactorState.get() != TileReactorCore.ReactorState.BEYOND_HOPE)
                .setTextSupplier(() -> Component.literal(Utils.addCommas((int) Math.round(tile.fuelUseRate.get() * 1000000D)) + " nb/t"))
                .setAlignment(Align.LEFT)
                .setShadow(false)
                .setTextColour(0xB0B0B0)
                .constrain(TOP, relative(last.get(BOTTOM), 0))
                .constrain(LEFT, relative(statusPanel.get(LEFT), 6));
        Constraints.size(last, 160, 9);

        //Go Boom
        GuiText goBoom = new GuiText(statusPanel, TOOLKIT.translate("go_boom_now"))
                .setEnabled(() -> tile.reactorState.get() == TileReactorCore.ReactorState.BEYOND_HOPE)
                .setAlignment(Align.LEFT)
                .setWrap(true)
                .setShadow(false)
                .setTextColour(0xB0B0B0);
        Constraints.size(goBoom, 155, 77);
        Constraints.center(goBoom, statusPanel);

        GuiText eta = new GuiText(statusPanel, Component.literal("ETA"))
                .setEnabled(() -> tile.reactorState.get() == TileReactorCore.ReactorState.BEYOND_HOPE)
                .setTextSupplier(() -> Component.literal("Estimated Time Until Detonation\n\n" + ChatFormatting.UNDERLINE + (tile.explosionCountdown.get() >= 0 ? (tile.explosionCountdown.get() / 20) + "s" : "Calculating..")))
                .setAlignment(Align.LEFT)
                .setWrap(true)
                .setShadow(false)
                .setTextColour(0xFF0000);
        Constraints.size(eta, 68, 80);
        Constraints.placeOutside(eta, statusPanel, Constraints.LayoutPos.MIDDLE_RIGHT, 5, 0);

        //Indicators
        GuiTexture tempBg = new GuiTexture(root, DEGuiTextures.get("reactor/temp_gauge"))
                .setTooltipDelay(5)
                .setTooltip(() -> getTempStats(tile));
        Constraints.size(tempBg, 18, 114);
        Constraints.placeInside(tempBg, root, Constraints.LayoutPos.TOP_LEFT, 8, 6);
        GuiSlider tempIndicator = new GuiSlider(root, Axis.Y)
                .setSliderState(new IndicatorState(() -> tile.temperature.get() / TileReactorCore.MAX_TEMPERATURE));
        Constraints.bind(tempIndicator, tempBg, 1, 1, -1, 1);
        Constraints.bind(new GuiTexture(tempIndicator.getSlider(), DEGuiTextures.get("reactor/pointer")), tempIndicator.getSlider());

        GuiTexture shieldBg = new GuiTexture(root, DEGuiTextures.get("reactor/shield_gauge"))
                .setTooltipDelay(5)
                .setTooltip(() -> getShieldStats(tile));
        Constraints.size(shieldBg, 18, 114);
        Constraints.placeOutside(shieldBg, tempBg, Constraints.LayoutPos.MIDDLE_RIGHT, 6, 0);
        GuiSlider shieldIndicator = new GuiSlider(root, Axis.Y)
                .setSliderState(new IndicatorState(() -> tile.shieldCharge.get() / Math.max(tile.maxShieldCharge.get(), 1)));
        Constraints.bind(shieldIndicator, shieldBg, 1, 1, -1, 1);
        Constraints.bind(new GuiTexture(shieldIndicator.getSlider(), DEGuiTextures.get("reactor/pointer")), shieldIndicator.getSlider());

        GuiTexture fuelBg = new GuiTexture(root, DEGuiTextures.get("reactor/fuel_gauge"))
                .setTooltipDelay(5)
                .setTooltip(() -> getFuelStats(tile));
        Constraints.size(fuelBg, 18, 114);
        Constraints.placeInside(fuelBg, root, Constraints.LayoutPos.TOP_RIGHT, -8, 6);
        GuiSlider fuelIndicator = new GuiSlider(root, Axis.Y)
                .setSliderState(new IndicatorState(() -> tile.convertedFuel.get() / Math.max(tile.reactableFuel.get() + tile.convertedFuel.get(), 1)));
        Constraints.bind(fuelIndicator, fuelBg, 1, 1, -1, 1);
        Constraints.bind(new GuiTexture(fuelIndicator.getSlider(), DEGuiTextures.get("reactor/pointer")), fuelIndicator.getSlider());

        GuiTexture satBg = new GuiTexture(root, DEGuiTextures.get("reactor/sat_gauge"))
                .setTooltipDelay(5)
                .setTooltip(() -> getSaturationStats(tile));
        Constraints.size(satBg, 18, 114);
        Constraints.placeOutside(satBg, fuelBg, Constraints.LayoutPos.MIDDLE_LEFT, -6, 0);
        GuiSlider satIndicator = new GuiSlider(root, Axis.Y)
                .setSliderState(new IndicatorState(() -> tile.saturation.get() / (double) Math.max(tile.maxSaturation.get(), 1)));
        Constraints.bind(satIndicator, satBg, 1, 1, -1, 1);
        Constraints.bind(new GuiTexture(satIndicator.getSlider(), DEGuiTextures.get("reactor/pointer")), satIndicator.getSlider());

        //Core Render
        GuiTexture coreBg = new GuiTexture(root, DEGuiTextures.get("reactor/core")) {
            @Override
            public double getBackgroundDepth() {
                return 100;
            }

            @Override
            public void renderBackground(GuiRender render, double mouseX, double mouseY, float partialTicks) {
                super.renderBackground(render, mouseX, mouseY, partialTicks);
                render.pose().pushPose();
                render.pose().translate(xCenter(), yCenter(), 100);
                RenderTileReactorCore.renderGUI(render, tile);
                render.pose().popPose();
            }
        };
        Constraints.size(coreBg, 128, 128);
        Constraints.placeInside(coreBg, root, Constraints.LayoutPos.TOP_CENTER, 0, 10);

        //Status Label
        GuiText statusText = new GuiText(root)
                .setShadow(() -> tile.reactorState.get() != TileReactorCore.ReactorState.BEYOND_HOPE)
                .setTextSupplier(() -> {
                    String s = tile.reactorState.get().localize();
                    if (tile.reactorState.get() == TileReactorCore.ReactorState.BEYOND_HOPE && TimeKeeper.getClientTick() % 10 > 5) {
                        s = ChatFormatting.DARK_RED + "**" + s + "**";
                    } else if (tile.reactorState.get() == TileReactorCore.ReactorState.BEYOND_HOPE) {
                        s = ChatFormatting.DARK_RED + "--" + s + "--";
                    }
                    return TOOLKIT.translate("status").withStyle(ChatFormatting.GOLD).append(": " + s);
                })
                .setAlignment(Align.LEFT);
        Constraints.size(statusText, 160, 12);
        Constraints.placeInside(statusText, statusPanel, Constraints.LayoutPos.TOP_LEFT, -5, -12);

        //RS Buttons
        GuiRectangle rsPanel = new GuiRectangle(root)
                .setEnabled(() -> compPanelExtended)
                .jeiExclude()
                .fill(0xFF808080)
                .border(0xFF000000);
        Constraints.size(rsPanel, 80, (RSMode.values().length * 13) + 3);
        Constraints.placeInside(rsPanel, root, Constraints.LayoutPos.BOTTOM_RIGHT, 80, -3);

        double y = 1;
        for (RSMode mode : RSMode.values()) {
            GuiButton btn = TOOLKIT.createFlat3DButton(rsPanel, () -> TOOLKIT.translate("rs_mode_" + mode.name().toLowerCase(Locale.ENGLISH)))
                    .onPress(() -> {
                        if (component != null) {
                            component.setRSMode(menu.player, mode);
                        }
                    })
                    .setToggleMode(() -> component != null && component.rsMode.get() == mode)
                    .setTooltip(TOOLKIT.translate("rs_mode_" + mode.name().toLowerCase(Locale.ENGLISH) + ".info"));
            Constraints.size(btn, 76, 12);
            Constraints.placeInside(btn, rsPanel, Constraints.LayoutPos.TOP_CENTER, 0, 1 + y);
            y += btn.ySize() + 1;
        }
    }

    public List<Component> getTempStats(TileReactorCore tile) {
        List<Component> list = new ArrayList<>();
        list.add(TOOLKIT.translate("reaction_temp"));
        list.add(Component.literal(MathUtils.round(tile.temperature.get(), 10) + "C"));
        return list;
    }

    public List<Component> getShieldStats(TileReactorCore tile) {
        List<Component> list = new ArrayList<>();
        list.add(TOOLKIT.translate("field_strength"));
        if (tile.maxShieldCharge.get() > 0) {
            list.add(Component.literal(MathUtils.round(tile.shieldCharge.get() / tile.maxShieldCharge.get() * 100D, 100D) + "%"));
        }
        list.add(Component.literal(Utils.addCommas((int) tile.shieldCharge.get()) + " / " + Utils.addCommas((int) tile.maxShieldCharge.get())));
        return list;
    }

    public List<Component> getSaturationStats(TileReactorCore tile) {
        List<Component> list = new ArrayList<>();
        list.add(TOOLKIT.translate("energy_saturation"));
        if (tile.maxSaturation.get() > 0) {
            list.add(Component.literal(MathUtils.round((double) tile.saturation.get() / (double) tile.maxSaturation.get() * 100D, 100D) + "%"));
        }
        list.add(Component.literal(Utils.addCommas(tile.saturation.get()) + " / " + Utils.addCommas(tile.maxSaturation.get())));
        return list;
    }

    public List<Component> getFuelStats(TileReactorCore tile) {
        List<Component> list = new ArrayList<>();
        list.add(TOOLKIT.translate("fuel_conversion"));
        if (tile.reactableFuel.get() + tile.convertedFuel.get() > 0) {
            list.add(Component.literal(MathUtils.round(tile.convertedFuel.get() / (tile.reactableFuel.get() + tile.convertedFuel.get()) * 100D, 100D) + "%"));
        }
        list.add(Component.literal(MathUtils.round(tile.convertedFuel.get(), 100) + " / " + MathUtils.round(tile.convertedFuel.get() + tile.reactableFuel.get(), 100)));
        return list;
    }

    public static class IndicatorState implements SliderState {
        private final Supplier<Double> pos;

        public IndicatorState(Supplier<Double> pos) {
            this.pos = pos;
        }

        @Override
        public double sliderRatio() {
            return 8D / 118D;
        }

        @Override
        public double getPos() {
            return MathHelper.clip(1 - pos.get(), 0, 1);
        }

        @Override
        public void setPos(double pos) {}
    }

    public static class Screen extends ModularGuiContainer<ReactorMenu> {
        public Screen(ReactorMenu menu, Inventory inv, Component title) {
            super(menu, inv, new ReactorGui());
            getModularGui().setGuiTitle(Component.translatable("gui.draconicevolution.reactor.title"));
        }
    }
}
