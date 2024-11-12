package com.brandon3055.draconicevolution.client.gui;

import codechicken.lib.gui.modular.ModularGui;
import codechicken.lib.gui.modular.ModularGuiContainer;
import codechicken.lib.gui.modular.elements.*;
import codechicken.lib.gui.modular.lib.Constraints;
import codechicken.lib.gui.modular.lib.container.ContainerGuiProvider;
import codechicken.lib.gui.modular.lib.container.ContainerScreenAccess;
import codechicken.lib.gui.modular.lib.geometry.Borders;
import codechicken.lib.gui.modular.lib.geometry.ConstraintImpl.BetweenOffsetDynamic;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.ShaderEnergyBar.EnergyBar;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.tileentity.TileCelestialManipulator;
import com.brandon3055.draconicevolution.client.DEGuiTextures;
import com.brandon3055.draconicevolution.inventory.CelestialManipulatorMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static codechicken.lib.gui.modular.lib.geometry.Constraint.*;
import static codechicken.lib.gui.modular.lib.geometry.GeoParam.*;

/**
 * Created by FoxMcloud5655 on 28/03/2024.
 */
public class CelestialManipulatorGui extends ContainerGuiProvider<CelestialManipulatorMenu> {
    public static final int GUI_WIDTH = 200;
    public static final int GUI_HEIGHT = 132;
    private static GuiToolkit toolkit = new GuiToolkit("gui." + DraconicEvolution.MODID + ".celestial_manipulator");
    private boolean isRSActive = false;

    @Override
    public GuiElement<?> createRootElement(ModularGui gui) {
        GuiManipulable root = new GuiManipulable(gui).addMoveHandle(3).enableCursors(true);
        GuiTexture bg = new GuiTexture(root.getContentElement(), DEGuiTextures.themedGetter("celestial_manipulator"));
        Constraints.bind(bg, root.getContentElement());
        return root;
    }

    @Override
    public void buildGui(ModularGui gui, ContainerScreenAccess<CelestialManipulatorMenu> screenAccess) {
        gui.initStandardGui(GUI_WIDTH, GUI_HEIGHT);
        GuiElement<?> root = gui.getRoot();
        TileCelestialManipulator tile = screenAccess.getMenu().tile;
        toolkit.createHeading(root, gui.getGuiTitle(), true);

        //Energy Bar
        EnergyBar energyBar = toolkit.createEnergyBar(root, tile.opStorage);
        energyBar.container()
                .constrain(TOP, relative(root.get(BOTTOM), -18))
                .constrain(BOTTOM, relative(root.get(BOTTOM), -5))
                .constrain(LEFT, relative(root.get(LEFT), 4))
                .constrain(RIGHT, relative(root.get(RIGHT), -4));

        GuiButton weatherMode = toolkit.createFlat3DButton(root, () -> toolkit.translate("weather"))
                .onPress(() -> tile.weatherMode.set(true))
                .setToggleMode(() -> tile.weatherMode.get())
                .constrain(TOP, relative(root.get(TOP), 17))
                .constrain(HEIGHT, literal(14))
                .constrain(LEFT, relative(root.get(LEFT), 4))
                .constrain(RIGHT, midPoint(root.get(LEFT), root.get(RIGHT), -2));

        GuiButton sunMode = toolkit.createFlat3DButton(root, () -> toolkit.translate("time"))
                .onPress(() -> tile.weatherMode.set(false))
                .setToggleMode(() -> !tile.weatherMode.get())
                .constrain(TOP, relative(root.get(TOP), 17))
                .constrain(HEIGHT, literal(14))
                .constrain(LEFT, midPoint(root.get(LEFT), root.get(RIGHT), 2))
                .constrain(RIGHT, relative(root.get(RIGHT), -4));

        // Weather Controls
        GuiText setWeatherText = new GuiText(root, () -> toolkit.translate("setWeather"))
                .constrain(TOP, relative(weatherMode.get(BOTTOM), 8))
                .constrain(HEIGHT, literal(14))
                .constrain(LEFT, match(weatherMode.get(LEFT)))
                .constrain(RIGHT, match(sunMode.get(RIGHT)))
                .setEnabled(weatherMode::toggleState);

        GuiButton stopRain = toolkit.createFlat3DButton(root, () -> toolkit.translate("clear"))
                .onPress(() -> tile.sendPacketToServer((output) -> output.writeString("STOP_RAIN"), 0))
                .constrain(TOP, relative(setWeatherText.get(BOTTOM), 4))
                .constrain(HEIGHT, match(setWeatherText.get(HEIGHT)))
                .constrain(LEFT, match(setWeatherText.get(LEFT)))
                .constrain(RIGHT, match(setWeatherText.get(RIGHT)))
                .setEnabled(weatherMode::toggleState);

        GuiButton startRain = toolkit.createFlat3DButton(root, () -> toolkit.translate("rain"))
                .onPress(() -> tile.sendPacketToServer((output) -> output.writeString("START_RAIN"), 0))
                .constrain(TOP, relative(stopRain.get(BOTTOM), 4))
                .constrain(HEIGHT, match(stopRain.get(HEIGHT)))
                .constrain(LEFT, match(stopRain.get(LEFT)))
                .constrain(RIGHT, match(stopRain.get(RIGHT)))
                .setEnabled(weatherMode::toggleState);

        GuiButton startStorm = toolkit.createFlat3DButton(root, () -> toolkit.translate("storm"))
                .onPress(() -> tile.sendPacketToServer((output) -> output.writeString("START_STORM"), 0))
                .constrain(TOP, relative(startRain.get(BOTTOM), 4))
                .constrain(HEIGHT, match(startRain.get(HEIGHT)))
                .constrain(LEFT, match(startRain.get(LEFT)))
                .constrain(RIGHT, match(startRain.get(RIGHT)))
                .setEnabled(weatherMode::toggleState);

        // Sun Controls
        GuiText skipText = new GuiText(root, () -> toolkit.translate("skipTo"))
                .constrain(TOP, relative(sunMode.get(BOTTOM), 8))
                .constrain(HEIGHT, literal(14))
                .constrain(LEFT, match(weatherMode.get(LEFT)))
                .constrain(RIGHT, match(sunMode.get(RIGHT)))
                .setEnabled(() -> !weatherMode.toggleState());

        GuiButton sunRise = toolkit.createFlat3DButton(root, () -> toolkit.translate("sunrise"))
                .onPress(() -> tile.sendPacketToServer((output) -> output.writeString("SUN_RISE"), 0))
                .constrain(TOP, relative(skipText.get(BOTTOM), 4))
                .constrain(HEIGHT, literal(14))
                .constrain(LEFT, match(skipText.get(LEFT)))
                .constrain(RIGHT, new BetweenOffsetDynamic(skipText.get(LEFT), skipText.get(RIGHT), () -> 1 / 3D, () -> -2D))
                .setEnabled(() -> !weatherMode.toggleState());

        GuiButton midDay = toolkit.createFlat3DButton(root, () -> toolkit.translate("noon"))
                .onPress(() -> tile.sendPacketToServer((output) -> output.writeString("MID_DAY"), 0))
                .constrain(TOP, match(sunRise.get(TOP)))
                .constrain(BOTTOM, match(sunRise.get(BOTTOM)))
                .constrain(LEFT, new BetweenOffsetDynamic(skipText.get(LEFT), skipText.get(RIGHT), () -> 1 / 3D, () -> 2D))
                .constrain(RIGHT, new BetweenOffsetDynamic(skipText.get(LEFT), skipText.get(RIGHT), () -> 2 / 3D, () -> -2D))
                .setEnabled(() -> !weatherMode.toggleState());

        GuiButton sunSet = toolkit.createFlat3DButton(root, () -> toolkit.translate("sunset"))
                .onPress(() -> tile.sendPacketToServer((output) -> output.writeString("SUN_SET"), 0))
                .constrain(TOP, match(midDay.get(TOP)))
                .constrain(BOTTOM, match(midDay.get(BOTTOM)))
                .constrain(LEFT, new BetweenOffsetDynamic(skipText.get(LEFT), skipText.get(RIGHT), () -> 2 / 3D, () -> 2D))
                .constrain(RIGHT, match(sunMode.get(RIGHT)))
                .setEnabled(() -> !weatherMode.toggleState());

        GuiButton moonRise = toolkit.createFlat3DButton(root, () -> toolkit.translate("moonrise"))
                .onPress(() -> tile.sendPacketToServer((output) -> output.writeString("MOON_RISE"), 0))
                .constrain(TOP, relative(sunRise.get(BOTTOM), 4))
                .constrain(HEIGHT, match(sunRise.get(HEIGHT)))
                .constrain(LEFT, match(sunRise.get(LEFT)))
                .constrain(RIGHT, match(sunRise.get(RIGHT)))
                .setEnabled(() -> !weatherMode.toggleState());

        GuiButton midnight = toolkit.createFlat3DButton(root, () -> toolkit.translate("midnight"))
                .onPress(() -> tile.sendPacketToServer((output) -> output.writeString("MIDNIGHT"), 0))
                .constrain(TOP, relative(midDay.get(BOTTOM), 4))
                .constrain(HEIGHT, match(midDay.get(HEIGHT)))
                .constrain(LEFT, match(midDay.get(LEFT)))
                .constrain(RIGHT, match(midDay.get(RIGHT)))
                .setEnabled(() -> !weatherMode.toggleState());

        GuiButton moonSet = toolkit.createFlat3DButton(root, () -> toolkit.translate("moonset"))
                .onPress(() -> tile.sendPacketToServer((output) -> output.writeString("MOON_SET"), 0))
                .constrain(TOP, relative(sunSet.get(BOTTOM), 4))
                .constrain(HEIGHT, match(sunSet.get(HEIGHT)))
                .constrain(LEFT, match(sunSet.get(LEFT)))
                .constrain(RIGHT, match(sunSet.get(RIGHT)))
                .setEnabled(() -> !weatherMode.toggleState());

        GuiButton skip24 = toolkit.createFlat3DButton(root, () -> toolkit.translate("skip24"))
                .onPress(() -> tile.sendPacketToServer((output) -> output.writeString("SKIP_24"), 0))
                .constrain(TOP, relative(moonRise.get(BOTTOM), 4))
                .constrain(HEIGHT, match(moonRise.get(HEIGHT)))
                .constrain(LEFT, match(skipText.get(LEFT)))
                .constrain(RIGHT, midPoint(skipText.get(LEFT), skipText.get(RIGHT), -2))
                .setEnabled(() -> !weatherMode.toggleState());

        GuiButton stop = toolkit.createFlat3DButton(root, () -> toolkit.translate("stop"))
                .onPress(() -> tile.sendPacketToServer((output) -> output.writeString("STOP"), 0))
                .constrain(TOP, relative(moonSet.get(BOTTOM), 4))
                .constrain(HEIGHT, match(moonSet.get(HEIGHT)))
                .constrain(LEFT, midPoint(skipText.get(LEFT), skipText.get(RIGHT), 2))
                .constrain(RIGHT, match(skipText.get(RIGHT)))
                .setEnabled(() -> !weatherMode.toggleState());

        String[] rsButtonNames = {"clear", "rain", "storm", "sunrise", "noon", "sunset", "moonrise", "midnight", "moonset"};
        GuiRectangle rsBackground = new GuiRectangle(root).setSize(18, 18);
        GuiItemStack rsItem = new GuiItemStack(rsBackground, BuiltInRegistries.ITEM.get(new ResourceLocation("minecraft:redstone")).getDefaultInstance())
                .enableStackToolTip(false);
        GuiButton rsButton = new GuiButton(rsBackground)
                .onPress(() -> isRSActive = !isRSActive)
                .setToggleMode(() -> isRSActive)
                .setTooltipSingle(() -> Component.translatable("generic.configureRedstone"))
                .setTooltipDelay(0);
        Constraints.placeOutside(rsBackground, root, Constraints.LayoutPos.TOP_RIGHT, 0, rsBackground.ySize());
        Constraints.bind(rsItem, rsBackground);
        Constraints.bind(rsButton, rsBackground);
        GuiToolkit.addHoverHighlight(rsButton, Borders.create(0), true);
        rsBackground.jeiExclude();

        GuiButton[] rsControlButtons = new GuiButton[rsButtonNames.length];
        for (int ii = 0; ii < rsButtonNames.length; ii++) {
            final int num = ii;
            rsControlButtons[num] = toolkit.createIconButton(rsBackground, 18, DEGuiTextures.getter("celestial_manipulator/" + rsButtonNames[num]))
                    .onPress(() -> tile.sendPacketToServer((output) -> output.writeInt(num), 1))
                    .setToggleMode(() -> tile.rsMode.get() == num)
                    .setTooltipSingle(() -> toolkit.translate(rsButtonNames[num]))
                    .setEnabled(() -> isRSActive)
                    .setTooltipDelay(0);
            rsControlButtons[num].jeiExclude();
            if (num == 0) {
                Constraints.placeOutside(rsControlButtons[num], rsBackground, Constraints.LayoutPos.BOTTOM_CENTER, 0, 2);
            } else {
                if (num % 3 != 0) {
                    Constraints.placeOutside(rsControlButtons[num], rsControlButtons[num - 1], Constraints.LayoutPos.MIDDLE_RIGHT, 2, 0);
                } else {
                    Constraints.placeOutside(rsControlButtons[num], rsControlButtons[num - 3], Constraints.LayoutPos.BOTTOM_CENTER, 0, 2);
                }
            }
        }

    }

    public static class Screen extends ModularGuiContainer<CelestialManipulatorMenu> {
        public Screen(CelestialManipulatorMenu menu, Inventory inv, Component title) {
            super(menu, inv, new CelestialManipulatorGui());
            getModularGui().setGuiTitle(title);
        }
    }
}
