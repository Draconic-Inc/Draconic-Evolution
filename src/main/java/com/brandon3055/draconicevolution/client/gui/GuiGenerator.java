package com.brandon3055.draconicevolution.client.gui;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import com.brandon3055.brandonscore.client.BCSprites;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElement;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElementManager;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiButton;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.TBasicMachine;
import com.brandon3055.brandonscore.inventory.ContainerBCTile;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGenerator;
import com.brandon3055.draconicevolution.utils.ResourceHelperDE;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.util.Map;

import static codechicken.lib.math.MathHelper.torad;
import static com.brandon3055.brandonscore.client.gui.GuiToolkit.GuiLayout.DEFAULT;
import static com.brandon3055.brandonscore.inventory.ContainerSlotLayout.SlotType.TILE_INV;
import static net.minecraft.util.text.TextFormatting.GOLD;
import static net.minecraft.util.text.TextFormatting.GRAY;

public class GuiGenerator extends ModularGuiContainer<ContainerBCTile<TileGenerator>> {

    private static final RenderType modelType = RenderType.getEntitySolid(new ResourceLocation(DraconicEvolution.MODID, "textures/block/generator/generator_2.png"));
    private static final CCModel storageModel;

    static {
        Map<String, CCModel> map = OBJParser.parseModels(new ResourceLocation(DraconicEvolution.MODID, "models/block/generator/generator_storage.obj"), GL11.GL_QUADS, null);
        storageModel = CCModel.combine(map.values());
        storageModel.computeNormals();
    }

    public PlayerEntity player;
    private TileGenerator tile;

    protected GuiToolkit<GuiGenerator> toolkit = new GuiToolkit<>(this, DEFAULT).setTranslationPrefix("gui.draconicevolution.generator");

    public GuiGenerator(ContainerBCTile<TileGenerator> container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.tile = container.tile;
        this.player = playerInventory.player;
    }

    @Override
    public void addElements(GuiElementManager manager) {
        TBasicMachine template = toolkit.loadTemplate(new TBasicMachine(this, tile));

        //Storage Renderer
        template.background.addChild(new StorageRenderer());
        GuiElement fuelSlots = toolkit.createSlots(template.background, 3, 1, 0, (x, y) -> container.getSlotLayout().getSlotData(TILE_INV, x), BCSprites.get("slots/fuel"));
        fuelSlots.zOffset += 100;
        fuelSlots.setPos(guiLeft() + 64, guiTop() + 28);

        //Power
        template.addEnergyBar(tile.opStorage);
        template.addEnergyItemSlot(true, container.getSlotLayout().getSlotData(TILE_INV, 3));

        //Mode Button
        GuiButton modeButton = toolkit.createButton("", template.background);
        modeButton.setDisplaySupplier(() -> I18n.format(tile.mode.get().unlocalizedName()));
        modeButton.setHoverText(element -> TextFormatting.BLUE + I18n.format(tile.mode.get().unlocalizedName() + ".info"));
        modeButton.onButtonPressed((pressed) -> tile.mode.set(tile.mode.get().next(hasShiftDown() || pressed == 1)));
        modeButton.setSize(80, 14);
        modeButton.zOffset += 100;
        modeButton.getChildElements().forEach(e -> e.zOffset += 100);
        modeButton.setPos(template.playerSlots.maxXPos() - modeButton.xSize(), template.playerSlots.yPos() - modeButton.ySize() + 8);

        //Info Panel
        template.infoPanel.addLabeledValue(GOLD + toolkit.i18n("fuel_efficiency"), 6, 11, () -> GRAY + (tile.mode.get().getEfficiency() + "%"), true);
        template.infoPanel.addLabeledValue(GOLD + toolkit.i18n("output_power"), 6, 11, () -> GRAY + (tile.productionRate.get() + " / " + tile.mode.get().powerOutput + " OP/t"), true);
        template.infoPanel.addLabeledValue(GOLD + toolkit.i18n("current_fuel_value"), 6, 11, () -> GRAY + (tile.fuelRemaining.get() == 0 ? "n/a" : tile.fuelRemaining.get() + " / " + tile.fuelValue.get()), true);
    }


    private class StorageRenderer extends GuiElement<StorageRenderer> {

        @Override
        public void renderElement(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();

            MatrixStack mStack = new MatrixStack();
            IRenderTypeBuffer.Impl getter = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
            ccrs.bind(modelType, getter);

            Matrix4 mat = new Matrix4(mStack);
            mat.translate(guiLeft() + 90, guiTop() + 45, 50);
            float mx = (((mouseX - guiLeft()) / (float) GuiGenerator.this.xSize()) - 0.5F) * .1F;
            float my = (((mouseY - guiTop()) / (float) GuiGenerator.this.ySize()) - 0.5F) * .1F;
            mat.apply(new Rotation(150 * torad, 1, 0, 0).with(new Rotation(10 * torad, -my, 1 + mx, 0)));
            mat.scale(7.5);
            storageModel.render(ccrs, mat);
            getter.finish();
        }
    }
}
