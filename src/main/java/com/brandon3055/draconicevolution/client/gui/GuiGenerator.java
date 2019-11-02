package com.brandon3055.draconicevolution.client.gui;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.TransformationList;
import com.brandon3055.brandonscore.client.gui.BCGuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElementManager;
import com.brandon3055.brandonscore.client.gui.modulargui.MGuiElementBase;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiButton;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.TBasicMachine;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGenerator;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.brandon3055.draconicevolution.inventory.ContainerGenerator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.util.Map;

import static codechicken.lib.math.MathHelper.torad;
import static com.brandon3055.brandonscore.client.gui.BCGuiToolkit.GuiLayout.DEFAULT_CONTAINER;
import static net.minecraft.util.text.TextFormatting.GOLD;
import static net.minecraft.util.text.TextFormatting.GRAY;

public class GuiGenerator extends ModularGuiContainer<ContainerGenerator> {

    private static ResourceLocation MODEL_TEXTURE = new ResourceLocation(DraconicEvolution.MODID, "textures/models/blocks/generator/generator_2.png");
    private static CCModel storageModel;
    static {
        if (storageModel == null) {
            Map<String, CCModel> map = OBJParser.parseModels(ResourceHelperDE.getResource("models/block/generator/generator_storage.obj"));
            storageModel = CCModel.combine(map.values());
            storageModel.computeNormals();
        }
    }

    public EntityPlayer player;
    private TileGenerator tile;

    protected BCGuiToolkit<GuiGenerator> toolkit = new BCGuiToolkit<>(this, DEFAULT_CONTAINER);

    public GuiGenerator(EntityPlayer player, TileGenerator tile) {
        super(new ContainerGenerator(player, tile));
        this.tile = tile;        this.player = player;
    }

    @Override
    public void addElements(GuiElementManager manager) {
        TBasicMachine template = toolkit.loadTemplate(new TBasicMachine(tile));
//        template.setTitle("gui.de.title.generator");//TODO move to tile? Or make the tile name override this?

        //Storage Renderer
        template.background.addChild(new StorageRenderer());
        MGuiElementBase fuelSlots = toolkit.createSlots(template.background, 3, 1);
        fuelSlots.zOffset = 100;
        fuelSlots.setPos(guiLeft() + 64, guiTop() + 28);

        //Power
        template.addEnergyBar(tile.opStorage);
        template.addEnergyItemSlot(true);

        //Mode Button
        GuiButton modeButton = toolkit.createRectButton("", template.background);
        modeButton.setDisplaySupplier(() -> I18n.format(tile.mode.get().unlocalizedName()));
        modeButton.setHoverText(element -> TextFormatting.BLUE + I18n.format(tile.mode.get().unlocalizedName() + ".info"));
        modeButton.setListener((guiButton, pressed) -> tile.mode.set(tile.mode.get().next(isShiftKeyDown() || pressed == 1)));
        modeButton.setSize(80, 12);
        modeButton.zOffset = 100;
        modeButton.setPos(template.playerSlots.maxXPos() - modeButton.xSize(), template.playerSlots.yPos() - modeButton.ySize() + 8);

        //Info Panel
        template.infoPanel.addLabeledValue(GOLD + I18n.format("gui.de.generator.fuel_efficiency"), 6, 11, () -> GRAY + (tile.mode.get().getEfficiency() + "%"), true);
        template.infoPanel.addLabeledValue(GOLD + I18n.format("gui.de.generator.output_power"), 6, 11, () -> GRAY + (tile.productionRate.get() + " / " + tile.mode.get().powerOutput + " OP/t"), true);
        template.infoPanel.addLabeledValue(GOLD + I18n.format("gui.de.generator.current_fuel_value"), 6, 11, () -> GRAY + (tile.fuelRemaining.get() == 0 ? "n/a" : tile.fuelRemaining.get() + " / " + tile.fuelValue.get()), true);
    }

    private class StorageRenderer extends MGuiElementBase {

        @Override
        public void renderElement(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();

            GlStateManager.pushMatrix();
            GlStateManager.translate(guiLeft() + 90, guiTop() + 45, 50);

            float mx = (((mouseX - guiLeft()) / (float) GuiGenerator.this.xSize()) - 0.5F) * .1F;
            float my = (((mouseY - guiTop()) / (float) GuiGenerator.this.ySize()) - 0.5F) * .1F;
            TransformationList transforms = new Rotation(150 * torad, 1, 0, 0).with(new Rotation(10 * torad, -my, 1 + mx, 0)).with(new Scale(7.5));

            bindTexture(MODEL_TEXTURE);
            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
            storageModel.render(ccrs, transforms);
            ccrs.draw();

            GlStateManager.popMatrix();
        }
    }
}
