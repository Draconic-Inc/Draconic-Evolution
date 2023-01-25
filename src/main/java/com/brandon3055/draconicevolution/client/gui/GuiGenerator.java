package com.brandon3055.draconicevolution.client.gui;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.model.OBJParser;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import com.brandon3055.brandonscore.client.BCGuiSprites;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElement;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElementManager;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiButton;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.TBasicMachine;
import com.brandon3055.brandonscore.client.render.RenderUtils;
import com.brandon3055.brandonscore.inventory.ContainerBCTile;
import com.brandon3055.brandonscore.inventory.ContainerSlotLayout;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleGrid;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGenerator;
import com.brandon3055.draconicevolution.client.gui.modular.TModularMachine;
import com.brandon3055.draconicevolution.inventory.ContainerDETile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public class GuiGenerator extends ModularGuiContainer<ContainerBCTile<TileGenerator>> {

    private static final RenderType modelType = RenderType.entitySolid(new ResourceLocation(DraconicEvolution.MODID, "textures/block/generator/generator_2.png"));
    private static final CCModel storageModel;

    static {
        Map<String, CCModel> map = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/block/generator/generator_storage.obj")).quads().ignoreMtl().parse();
        storageModel = CCModel.combine(map.values());
        storageModel.computeNormals();
    }

    public Player player;
    private TileGenerator tile;

    protected GuiToolkit<GuiGenerator> toolkit = new GuiToolkit<>(this, GuiToolkit.GuiLayout.DEFAULT).setTranslationPrefix("gui.draconicevolution.generator");

    public GuiGenerator(ContainerBCTile<TileGenerator> container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        this.tile = container.tile;
        this.player = playerInventory.player;
    }

    @Override
    public void addElements(GuiElementManager manager) {
        TBasicMachine template = toolkit.loadTemplate(new TModularMachine(this, tile));

        //Storage Renderer
        template.background.addChild(new StorageRenderer().setPos(guiLeft(), guiTop()));
        GuiElement fuelSlots = toolkit.createSlots(template.background, 3, 1, 0, (x, y) -> container.getSlotLayout().getSlotData(ContainerSlotLayout.SlotType.TILE_INV, x), BCGuiSprites.get("slots/fuel"));
        fuelSlots.zOffset += 100; //Offset so that this element renders above the chest model in the background
        fuelSlots.setPos(guiLeft() + 64, guiTop() + 28);

        //Power
        template.addEnergyBar(tile.opStorage);
        template.addEnergyItemSlot(true, container.getSlotLayout().getSlotData(ContainerSlotLayout.SlotType.TILE_INV, 3));

        //Mode Button
        GuiButton modeButton = toolkit.createButton_old("", template.background);
        modeButton.setDisplaySupplier(() -> I18n.get(tile.mode.get().unlocalizedName()));
        modeButton.setHoverText(element -> ChatFormatting.BLUE + I18n.get(tile.mode.get().unlocalizedName() + ".info"));
        modeButton.onButtonPressed((pressed) -> tile.mode.set(tile.mode.get().next(hasShiftDown() || pressed == 1)));
        modeButton.setSize(100, 14);
        modeButton.zOffset += 100; //Offset so that this element renders above the chest model in the background
        modeButton.getChildElements().forEach(e -> e.zOffset += 100);
        modeButton.setPos(template.playerSlots.maxXPos() - modeButton.xSize(), template.playerSlots.yPos() - modeButton.ySize() + 8);
        modeButton.setResetHoverOnClick(true);

        //Info Panel
        template.infoPanel.addLabeledValue(ChatFormatting.GOLD + toolkit.i18n("fuel_efficiency"), 6, 11, () -> ChatFormatting.GRAY + (tile.mode.get().getEfficiency() + "%"), true);
        template.infoPanel.addLabeledValue(ChatFormatting.GOLD + toolkit.i18n("output_power"), 6, 11, () -> ChatFormatting.GRAY + (tile.productionRate.get() + " / " + tile.mode.get().powerOutput + " OP/t"), true);
        template.infoPanel.addLabeledValue(ChatFormatting.GOLD + toolkit.i18n("current_fuel_value"), 6, 11, () -> ChatFormatting.GRAY + (tile.fuelRemaining.get() == 0 ? "n/a" : tile.fuelRemaining.get() + " / " + tile.fuelValue.get()), true);
    }


    private class StorageRenderer extends GuiElement<StorageRenderer> {

        @Override
        public void renderElement(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();

            PoseStack mStack = new PoseStack();
            MultiBufferSource.BufferSource getter = RenderUtils.getGuiBuffers();
            ccrs.bind(modelType, getter);

            Matrix4 mat = new Matrix4(mStack);
            mat.translate(xPos() + 90, yPos() + 45, 50);
            float mx = (((mouseX - guiLeft()) / (float) GuiGenerator.this.xSize()) - 0.5F) * .1F;
            float my = (((mouseY - guiTop()) / (float) GuiGenerator.this.ySize()) - 0.5F) * .1F;
            mat.apply(new Rotation(150 * MathHelper.torad, 1, 0, 0).with(new Rotation(10 * MathHelper.torad, -my, 1 + mx, 0)));
            mat.scale(7.5);
            storageModel.render(ccrs, mat);
            getter.endBatch();
        }
    }
}
