package com.brandon3055.draconicevolution.client.gui;

import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.client.BCSprites;
import com.brandon3055.brandonscore.client.ResourceHelperBC;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElementManager;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElement;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiButton;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.*;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.GuiAlign;
import com.brandon3055.brandonscore.client.utils.GuiHelper;
import com.brandon3055.brandonscore.utils.InfoHelper;
import com.brandon3055.brandonscore.utils.MathUtils;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorComponent;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorComponent.RSMode;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.client.DETextures;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileReactorCore;
import com.brandon3055.draconicevolution.inventory.ContainerReactor;
import com.brandon3055.draconicevolution.utils.ResourceHelperDE;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 10/02/2017.
 */
public class GuiReactor extends ModularGuiContainer<ContainerReactor> {

    private PlayerEntity player;
    private final TileReactorCore tile;
    public TileReactorComponent component = null;
    private static double compPanelAnim = 0;
    private static boolean compPanelExtended = false;
    private GuiElement<?> compPanel;

    public GuiReactor(ContainerReactor container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
        this.player = player;
        this.tile = container.tile;
        this.xSize = 248;
        this.ySize = 222;
    }

    @Override
    public void addElements(GuiElementManager manager) {
        List<GuiElement<?>> exclusions = new ArrayList<>();

        //region Background Elements
        manager.addChild(compPanel = new GuiBorderedRect(guiLeft + xSize, guiTop + 125, 0, 91));
        exclusions.add(compPanel);
        manager.setJeiExclusions(() -> exclusions);
        manager.addChild(new GuiTexture(guiLeft, guiTop, 0, 0, xSize, ySize, new ResourceLocation("draconicevolution:" + DETextures.GUI_REACTOR)) {
            @Override
            public void renderElement(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
                super.renderElement(minecraft, mouseX, mouseY, partialTicks);
                RenderTileReactorCore.renderGUI(tile, guiLeft + xSize / 2, guiTop + 70);
            }

        });
        manager.addChild(new GuiBorderedRect(guiLeft + 12, guiTop + 138, 162, 77)
                .setEnabledCallback(() -> tile.reactorState.get() != TileReactorCore.ReactorState.COLD)
                .setShadeColours(0xFF000000, 0xFFFFFFFF));
        //endregion

        //region Status Labels

        int y = guiTop + 140;
        manager.addChild(new GuiLabel(guiLeft + 10 + 5, y, 162, 8, I18n.format("gui.draconicevolution.reactor.core_volume"))
                .setEnabledCallback(() -> tile.reactorState.get() != TileReactorCore.ReactorState.COLD && tile.reactorState.get() != TileReactorCore.ReactorState.BEYOND_HOPE)
                .setAlignment(GuiAlign.LEFT)
                .setShadow(false)
                .setTextColour(0x00C0FF)
                .setHoverText(I18n.format("gui.draconicevolution.reactor.core_volume.info"))
                .setHoverTextDelay(2));

        manager.addChild(new GuiLabel(guiLeft + 13 + 5, y += 8, 162, 8, "")
                .setEnabledCallback(() -> tile.reactorState.get() != TileReactorCore.ReactorState.COLD && tile.reactorState.get() != TileReactorCore.ReactorState.BEYOND_HOPE)
                .setDisplaySupplier(() -> MathUtils.round((tile.reactableFuel.get() + tile.convertedFuel.get()) / 1296D, 100) + "m^3")
                .setAlignment(GuiAlign.LEFT)
                .setShadow(false)
                .setTextColour(0xB0B0B0));

        manager.addChild(new GuiLabel(guiLeft + 10 + 5, y += 11, 162, 8, I18n.format("gui.draconicevolution.reactor.gen_rate"))
                .setEnabledCallback(() -> tile.reactorState.get() != TileReactorCore.ReactorState.COLD && tile.reactorState.get() != TileReactorCore.ReactorState.BEYOND_HOPE)
                .setAlignment(GuiAlign.LEFT)
                .setShadow(false)
                .setTextColour(0x00C0FF)
                .setHoverText(I18n.format("gui.draconicevolution.reactor.gen_rate.info"))
                .setHoverTextDelay(2));

        manager.addChild(new GuiLabel(guiLeft + 13 + 5, y += 8, 162, 8, "")
                .setEnabledCallback(() -> tile.reactorState.get() != TileReactorCore.ReactorState.COLD && tile.reactorState.get() != TileReactorCore.ReactorState.BEYOND_HOPE)
                .setDisplaySupplier(() -> Utils.addCommas((int) tile.generationRate.get()) + " OP/t")
                .setAlignment(GuiAlign.LEFT)
                .setShadow(false)
                .setTextColour(0xB0B0B0));

        manager.addChild(new GuiLabel(guiLeft + 10 + 5, y += 11, 162, 8, I18n.format("gui.draconicevolution.reactor.field_rate"))
                .setEnabledCallback(() -> tile.reactorState.get() != TileReactorCore.ReactorState.COLD && tile.reactorState.get() != TileReactorCore.ReactorState.BEYOND_HOPE)
                .setAlignment(GuiAlign.LEFT)
                .setShadow(false)
                .setTextColour(0x00C0FF)
                .setHoverText(I18n.format("gui.draconicevolution.reactor.field_rate.info"))
                .setHoverTextDelay(2));

        manager.addChild(new GuiLabel(guiLeft + 13 + 5, y += 8, 162, 8, "")
                .setEnabledCallback(() -> tile.reactorState.get() != TileReactorCore.ReactorState.COLD && tile.reactorState.get() != TileReactorCore.ReactorState.BEYOND_HOPE)
                .setDisplaySupplier(() -> {
                    double inputRate = tile.fieldDrain.get() / (1D - (tile.shieldCharge.get() / tile.maxShieldCharge.get()));
                    return Utils.addCommas((int) Math.min(inputRate, Integer.MAX_VALUE)) + "OP/t";
                })
                .setAlignment(GuiAlign.LEFT)
                .setShadow(false)
                .setTextColour(0xB0B0B0));

        manager.addChild(new GuiLabel(guiLeft + 10 + 5, y += 11, 162, 8, I18n.format("gui.draconicevolution.reactor.convert_rate"))
                .setEnabledCallback(() -> tile.reactorState.get() != TileReactorCore.ReactorState.COLD && tile.reactorState.get() != TileReactorCore.ReactorState.BEYOND_HOPE)
                .setAlignment(GuiAlign.LEFT)
                .setShadow(false)
                .setTextColour(0x00C0FF)
                .setHoverText(I18n.format("gui.draconicevolution.reactor.convert_rate.info"))
                .setHoverTextDelay(2));

        manager.addChild(new GuiLabel(guiLeft + 13 + 5, y += 8, 162, 8, "")
                .setEnabledCallback(() -> tile.reactorState.get() != TileReactorCore.ReactorState.COLD && tile.reactorState.get() != TileReactorCore.ReactorState.BEYOND_HOPE)
                .setDisplaySupplier(() -> Utils.addCommas((int) Math.round(tile.fuelUseRate.get() * 1000000D)) + "nb/t")
                .setAlignment(GuiAlign.LEFT)
                .setShadow(false)
                .setTextColour(0xB0B0B0));

        manager.addChild(new GuiLabel(guiLeft + 13 + 5, guiTop + 139, 161, 77, I18n.format("gui.draconicevolution.reactor.go_boom_now"))
                .setEnabledCallback(() -> tile.reactorState.get() == TileReactorCore.ReactorState.BEYOND_HOPE)
                .setAlignment(GuiAlign.LEFT)
                .setWrap(true)
                .setShadow(false)
                .setTextColour(0xB0B0B0));
        //endregion

        //region Slots, Misc labels and gauges

        manager.addChild(new GuiSlotRender());

        manager.addChild(new GuiElement() {
            @Override
            public void renderElement(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
                if (tile.reactorState.get() == TileReactorCore.ReactorState.COLD) {
                    RenderSystem.color4f(1F, 1F, 1F, 1F);
                    RenderMaterial mat = BCSprites.getThemed("slot");
                    bindTexture(mat.getAtlasLocation());
                    IRenderTypeBuffer.Impl getter = minecraft.getRenderTypeBuffers().getBufferSource();
                    GuiHelper.drawPlayerSlots(GuiReactor.this, guiLeft + 43 - 31, guiTop + 139, false);
                    for (int x = 0; x < 3; x++) {
                        drawSprite(mat.getBuffer(getter, BCSprites::makeType), guiLeft + 182 + (x * 18), guiTop + 148, 18, 18, mat.getSprite());
                    }
                    for (int x = 0; x < 3; x++) {
                        drawSprite(mat.getBuffer(getter, BCSprites::makeType), guiLeft + 182 + (x * 18), guiTop + 179, 18, 18, mat.getSprite());
                    }
                    getter.finish();
                }
            }
        });

        manager.addChild(new GuiLabel(guiLeft, guiTop + 2, xSize, 12, I18n.format("gui.draconicevolution.reactor.title"))
                .setAlignment(GuiAlign.CENTER)
                .setTextColour(InfoHelper.GUI_TITLE));

        manager.addChild(new GuiLabel(guiLeft + 182, guiTop + 139, 54, 8, I18n.format("gui.draconicevolution.reactor.fuel_in"))
                .setEnabledCallback(() -> tile.reactorState.get() == TileReactorCore.ReactorState.COLD)
                .setAlignment(GuiAlign.CENTER)
                .setTrim(false));

        manager.addChild(new GuiLabel(guiLeft + 182, guiTop + 170, 54, 8, I18n.format("gui.draconicevolution.reactor.chaos_out"))
                .setEnabledCallback(() -> tile.reactorState.get() == TileReactorCore.ReactorState.COLD)
                .setAlignment(GuiAlign.CENTER)
                .setTrim(false));

        manager.addChild(new GuiLabel(guiLeft + 7, guiTop + 127, xSize, 12, "")
                .setShadowStateSupplier(() -> tile.reactorState.get() != TileReactorCore.ReactorState.BEYOND_HOPE)
                .setDisplaySupplier(() -> {
                    String s = tile.reactorState.get().localize();
                    if (tile.reactorState.get() == TileReactorCore.ReactorState.BEYOND_HOPE && ClientEventHandler.elapsedTicks % 10 > 5) {
                        s = TextFormatting.DARK_RED + "**" + s + "**";
                    } else if (tile.reactorState.get() == TileReactorCore.ReactorState.BEYOND_HOPE) {
                        s = TextFormatting.DARK_RED + "--" + s + "--";
                    }
                    return TextFormatting.GOLD + I18n.format("gui.draconicevolution.reactor.status") + ": " + s;
                }).setAlignment(GuiAlign.LEFT));


        manager.addChild(new GuiTexturedPointer(guiLeft + 11, guiTop + 5, 14, 112, 0, 222, 5, ResourceHelperDE.getResource(DETextures.GUI_REACTOR)) {
            @Override
            public double getPos() {
                return MathHelper.clip(tile.temperature.get() / TileReactorCore.MAX_TEMPERATURE, 0, 1);
            }
        }.setHoverText(element -> getTempStats()).setHoverTextDelay(5));

        manager.addChild(new GuiTexturedPointer(guiLeft + 35, guiTop + 5, 14, 112, 0, 222, 5, ResourceHelperDE.getResource(DETextures.GUI_REACTOR)) {
            @Override
            public double getPos() {
                return MathHelper.clip(tile.shieldCharge.get() / Math.max(tile.maxShieldCharge.get(), 1), 0, 1);
            }
        }.setHoverText(element -> getShieldStats()).setHoverTextDelay(5));

        manager.addChild(new GuiTexturedPointer(guiLeft + 199, guiTop + 5, 14, 112, 0, 222, 5, ResourceHelperDE.getResource(DETextures.GUI_REACTOR)) {
            @Override
            public double getPos() {
                return MathHelper.clip(tile.saturation.get() / (double) Math.max(tile.maxSaturation.get(), 1), 0, 1);
            }
        }.setHoverText(element -> getSaturationStats()).setHoverTextDelay(5));

        manager.addChild(new GuiTexturedPointer(guiLeft + 223, guiTop + 5, 14, 112, 0, 222, 5, ResourceHelperDE.getResource(DETextures.GUI_REACTOR)) {
            @Override
            public double getPos() {
                return MathHelper.clip(tile.convertedFuel.get() / Math.max(tile.reactableFuel.get() + tile.convertedFuel.get(), 1), 0, 1);
            }
        }.setHoverText(element -> getFuelStats()).setHoverTextDelay(5));

        //endregion

        //region Buttons

        manager.addChild(new GuiButton(guiLeft + 182, guiTop + 199, 54, 14, I18n.format("gui.draconicevolution.reactor.charge"))
                .setEnabledCallback(tile::canCharge)
                .setBorderColours(0xFF555555, 0xFF777777)
                .setFillColour(0xFF000000)
                .setTrim(false)
                .onPressed(tile::chargeReactor));

        manager.addChild(new GuiButton(guiLeft + 182, guiTop + 182, 54, 14, I18n.format("gui.draconicevolution.reactor.activate"))
                .setEnabledCallback(tile::canActivate)
                .setBorderColours(0xFF555555, 0xFF777777)
                .setFillColour(0xFF000000)
                .setTrim(false)
                .onPressed(tile::activateReactor));

        manager.addChild(new GuiButton(guiLeft + 182, guiTop + 199, 54, 14, I18n.format("gui.draconicevolution.reactor.shutdown"))
                .setEnabledCallback(tile::canStop)
                .setBorderColours(0xFF555555, 0xFF777777)
                .setFillColour(0xFF000000)
                .setTrim(false)
                .onPressed(tile::shutdownReactor));

        manager.addChild(new GuiButton(guiLeft + 182, guiTop + 165, 54, 14, I18n.format("gui.draconicevolution.reactor.sas"))
                .setEnabledCallback(() -> tile.reactorState.get() != TileReactorCore.ReactorState.COLD && tile.reactorState.get() != TileReactorCore.ReactorState.BEYOND_HOPE)
                .setBorderColours(0xFF555555, 0xFF777777)
                .setRectFillColourGetter((hovering, disabled) -> tile.failSafeMode.get() ? 0xFF4040FF : 0xFF000000)
                .setTrim(false)
                .onPressed(tile::toggleFailSafe)
                .setHoverText(I18n.format("gui.draconicevolution.reactor.sas.info")));

        manager.addChild(new GuiButton(guiLeft + 182, guiTop + 138, 54, 24, I18n.format("gui.draconicevolution.reactor.rs_mode").replaceAll("\\\\n", "\n"))
                .setEnabledCallback(() -> tile.reactorState.get() != TileReactorCore.ReactorState.COLD && component != null && tile.reactorState.get() != TileReactorCore.ReactorState.BEYOND_HOPE)
                .setWrap(true)
                .setBorderColours(0xFF555555, 0xFF777777)
                .setFillColour(0xFF000000)
                .onPressed(() -> compPanelExtended = !compPanelExtended)
                .setInsets(5, 0, 5, 0)
                .setHoverText(I18n.format("gui.draconicevolution.reactor.rs_mode.info")));

        manager.addChild(new GuiLabel(guiLeft + 175, guiTop + 138, 68, 80, "ETE")
                .setEnabledCallback(() -> tile.reactorState.get() == TileReactorCore.ReactorState.BEYOND_HOPE)
                .setDisplaySupplier(() -> "Estimated\nTime\nUntil\nDetonation\n\n" + TextFormatting.UNDERLINE + (tile.explosionCountdown.get() >= 0 ? (tile.explosionCountdown.get() / 20) + "s" : "Calculating.."))
                .setWrap(true)
                .setShadow(false)
                .setTextColour(0xFF0000));


        y = 0;
        for (RSMode mode : RSMode.values()) {
            manager.addChild(new GuiButton(guiLeft + xSize + 2, guiTop + 127 + y, 66, 10, I18n.format("gui.draconicevolution.reactor.rs_mode_" + mode.name().toLowerCase()))
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
                    .setHoverText("gui.draconicevolution.reactor.rs_mode_" + mode.name().toLowerCase() + ".info")
                    .setTrim(false));
            y += 11;
        }

        //endregion
    }

    public List<String> getTempStats() {
        List<String> list = new ArrayList<>();
        list.add(I18n.format("gui.draconicevolution.reactor.reaction_temp"));
        list.add(MathUtils.round(tile.temperature.get(), 10) + "C");
        return list;
    }

    public List<String> getShieldStats() {
        List<String> list = new ArrayList<>();
        list.add(I18n.format("gui.draconicevolution.reactor.field_strength"));
        if (tile.maxShieldCharge.get() > 0) {
            list.add(MathUtils.round(tile.shieldCharge.get() / tile.maxShieldCharge.get() * 100D, 100D) + "%");
        }
        list.add(Utils.addCommas((int) tile.shieldCharge.get()) + " / " + Utils.addCommas((int) tile.maxShieldCharge.get()));
        return list;
    }

    public List<String> getSaturationStats() {
        List<String> list = new ArrayList<>();
        list.add(I18n.format("gui.draconicevolution.reactor.energy_saturation"));
        if (tile.maxSaturation.get() > 0) {
            list.add(MathUtils.round((double) tile.saturation.get() / (double) tile.maxSaturation.get() * 100D, 100D) + "%");
        }
        list.add(Utils.addCommas(tile.saturation.get()) + " / " + Utils.addCommas(tile.maxSaturation.get()));
        return list;
    }

    public List<String> getFuelStats() {
        List<String> list = new ArrayList<>();
        list.add(I18n.format("gui.draconicevolution.reactor.fuel_conversion"));
        if (tile.reactableFuel.get() + tile.convertedFuel.get() > 0) {
            list.add(MathUtils.round(tile.convertedFuel.get() / (tile.reactableFuel.get() + tile.convertedFuel.get()) * 100D, 100D) + "%");
        }
        list.add(MathUtils.round(tile.convertedFuel.get(), 100) + " / " + MathUtils.round(tile.convertedFuel.get() + tile.reactableFuel.get(), 100));
        return list;
    }

    @Override
    public void tick() {
        super.tick();

        if (tile.reactorState.get() == TileReactorCore.ReactorState.COLD != container.fuelSlots) {
            container.setSlotState();
        }

        if (compPanelExtended && (compPanelAnim < 1 || compPanel.xSize() != 70)) {
            compPanelAnim += 0.1;
            if (compPanelAnim > 1) {
                compPanelAnim = 1;
            }
            compPanel.setXSize((int) (compPanelAnim * 70));
        } else if (!compPanelExtended && compPanelAnim > 0) {
            compPanelAnim -= 0.1;
            if (compPanelAnim < 0) {
                compPanelAnim = 0;
            }
            compPanel.setXSize((int) (compPanelAnim * 70));
        }

        if (compPanel.isEnabled() && compPanelAnim == 0) {
            compPanel.setEnabled(false);
        } else if (!compPanel.isEnabled() && compPanelAnim > 0) {
            compPanel.setEnabled(true);
        }
    }
}
