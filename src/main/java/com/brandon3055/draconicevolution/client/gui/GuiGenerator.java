package com.brandon3055.draconicevolution.client.gui;

import codechicken.lib.gui.modular.ModularGui;
import codechicken.lib.gui.modular.ModularGuiContainer;
import codechicken.lib.gui.modular.ModularGuiScreen;
import codechicken.lib.gui.modular.lib.container.ContainerGuiProvider;
import codechicken.lib.gui.modular.lib.container.ContainerScreenAccess;
import com.brandon3055.brandonscore.inventory.ContainerBCTile;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class GuiGenerator extends ContainerGuiProvider<ContainerBCTile<TileGenerator>> {

    @Override
    public void buildGui(ModularGui gui, ContainerScreenAccess<ContainerBCTile<TileGenerator>> screenAccess) {

    }

//    private static final RenderType modelType = RenderType.entitySolid(new ResourceLocation(DraconicEvolution.MODID, "textures/block/generator/generator_2.png"));
//    private static final CCModel storageModel;
//
//    static {
//        Map<String, CCModel> map = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/block/generator/generator_storage.obj")).quads().ignoreMtl().parse();
//        storageModel = CCModel.combine(map.values());
//        storageModel.computeNormals();
//    }
//
//    public Player player;
//    private TileGenerator tile;
//
//    protected GuiToolkit<GuiGenerator> toolkit = new GuiToolkit<>(this, GuiToolkit.GuiLayout.DEFAULT).setTranslationPrefix("gui.draconicevolution.generator");
//
//    public GuiGenerator(ContainerBCTile<TileGenerator> container, Inventory playerInventory, Component title) {
//        super(container, playerInventory, title);
//        this.tile = container.tile;
//        this.player = playerInventory.player;
//    }
//
//    @Override
//    public void addElements(GuiElementManager manager) {
//        TBasicMachine template = toolkit.loadTemplate(new TModularMachine(this, tile));
//
//        //Storage Renderer
//        template.background.addChild(new StorageRenderer().setPos(guiLeft(), guiTop()));
//        GuiElement fuelSlots = toolkit.createSlots(template.background, 3, 1, 0, (x, y) -> container.getSlotLayout().getSlotData(ContainerSlotLayout.SlotType.TILE_INV, x), BCGuiTextures.get("slots/fuel"));
//        fuelSlots.zOffset += 100; //Offset so that this element renders above the chest model in the background
//        fuelSlots.setPos(guiLeft() + 64, guiTop() + 28);
//
//        //Power
//        template.addEnergyBar(tile.opStorage);
//        template.addEnergyItemSlot(true, container.getSlotLayout().getSlotData(ContainerSlotLayout.SlotType.TILE_INV, 3));
//
//        //Mode Button
//        GuiButton modeButton = toolkit.createButton_old("", template.background);
//        modeButton.setDisplaySupplier(() -> I18n.get(tile.mode.get().unlocalizedName()));
//        modeButton.setHoverText(element -> ChatFormatting.BLUE + I18n.get(tile.mode.get().unlocalizedName() + ".info"));
//        modeButton.onButtonPressed((pressed) -> tile.mode.set(tile.mode.get().next(hasShiftDown() || pressed == 1)));
//        modeButton.setSize(100, 14);
//        modeButton.zOffset += 100; //Offset so that this element renders above the chest model in the background
//        modeButton.getChildElements().forEach(e -> e.zOffset += 100);
//        modeButton.setPos(template.playerSlots.maxXPos() - modeButton.xSize(), template.playerSlots.yPos() - modeButton.ySize() + 8);
//        modeButton.setResetHoverOnClick(true);
//
//        //Info Panel
//        template.infoPanel.addLabeledValue(ChatFormatting.GOLD + toolkit.i18n("fuel_efficiency"), 6, 11, () -> ChatFormatting.GRAY + (tile.mode.get().getEfficiency() + "%"), true);
//        template.infoPanel.addLabeledValue(ChatFormatting.GOLD + toolkit.i18n("output_power"), 6, 11, () -> ChatFormatting.GRAY + (tile.productionRate.get() + " / " + tile.mode.get().powerOutput + " OP/t"), true);
//        template.infoPanel.addLabeledValue(ChatFormatting.GOLD + toolkit.i18n("current_fuel_value"), 6, 11, () -> ChatFormatting.GRAY + (tile.fuelRemaining.get() == 0 ? "n/a" : tile.fuelRemaining.get() + " / " + tile.fuelValue.get()), true);
//    }
//
//
//    private class StorageRenderer extends GuiElement<StorageRenderer> {
//
//        @Override
//        public void renderElement(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
//            CCRenderState ccrs = CCRenderState.instance();
//            ccrs.reset();
//
//            PoseStack mStack = new PoseStack();
//            MultiBufferSource.BufferSource getter = RenderUtils.getGuiBuffers();
//            ccrs.bind(modelType, getter);
//
//            Matrix4 mat = new Matrix4(mStack);
//            mat.translate(xPos() + 90, yPos() + 45, 50);
//            float mx = (((mouseX - guiLeft()) / (float) GuiGenerator.this.xSize()) - 0.5F) * .1F;
//            float my = (((mouseY - guiTop()) / (float) GuiGenerator.this.ySize()) - 0.5F) * .1F;
//            mat.apply(new Rotation(150 * MathHelper.torad, 1, 0, 0).with(new Rotation(10 * MathHelper.torad, -my, 1 + mx, 0)));
//            mat.scale(7.5);
//            storageModel.render(ccrs, mat);
//            getter.endBatch();
//        }
//    }

    public static class Screen extends ModularGuiContainer<ContainerBCTile<TileGenerator>> {
        public Screen(ContainerBCTile<TileGenerator> menu, Inventory inv, Component title) {
            super(menu, inv, new GuiGenerator());
            getModularGui().setGuiTitle(title);
        }
    }
}
