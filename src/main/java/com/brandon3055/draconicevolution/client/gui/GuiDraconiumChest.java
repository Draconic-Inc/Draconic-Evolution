package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.brandonscore.BCConfig;
import com.brandon3055.brandonscore.client.BCSprites;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElement;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElementManager;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiButton;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiPickColourDialog;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiSlotRender;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiTexture;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.TGuiBase;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDraconiumChest;
import com.brandon3055.draconicevolution.client.DESprites;
import com.brandon3055.draconicevolution.client.DETextures;
import com.brandon3055.draconicevolution.client.model.ModelDraconiumChest;
import com.brandon3055.draconicevolution.inventory.ContainerDraconiumChest;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import sun.rmi.log.LogHandler;

import java.awt.*;
import java.util.Objects;

import static net.minecraft.util.text.TextFormatting.GOLD;
import static net.minecraft.util.text.TextFormatting.GRAY;

/**
 * Created by Werechang on 27/6/21
 */
public class GuiDraconiumChest extends ModularGuiContainer<ContainerDraconiumChest> {

    protected GuiToolkit<GuiDraconiumChest> toolkit = new GuiToolkit<>(this, 500, 288).setTranslationPrefix("gui.draconicevolution.draconium_chest");
    private final TileDraconiumChest tile;

    public static final ResourceLocation DRACONIUM_CHEST;
    public static ModelDraconiumChest chest;
    static {
        DRACONIUM_CHEST = new ResourceLocation(DraconicEvolution.MODID, DETextures.DRACONIUM_CHEST);
        chest = new ModelDraconiumChest(RenderType::entitySolid);
    }

    public GuiDraconiumChest(ContainerDraconiumChest container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
        tile = container.tile;
    }

    @Override
    public void addElements(GuiElementManager manager) {
        // Create and load dynamic background
        TGuiBase template = new TGuiBase(this);
        template.background = GuiTexture.newDynamicTexture(xSize(), ySize(), () -> BCSprites.getThemed("background_dynamic"));
        template.background.onReload(guiTex -> guiTex.setPos(guiLeft(), guiTop()));
        toolkit.loadTemplate(template);

        // Add player inventory to gui
        template.addPlayerSlots(true, false, false);

        // Main storage
        toolkit.createSlots(template.background, 26, 10, 0).setPos(guiLeft() + 16, guiTop() + 14);

        // Furnace
        toolkit.createSlots(template.background, 5, 1, 0).setPos(guiLeft() + 50, guiTop() + 206);
        GuiFurnaceFlames flames = new GuiFurnaceFlames(template.background, guiLeft() + 51, guiTop() + 226, tile);

        // EnergyBar
        template.energyBar = toolkit.createEnergyBar(template.background, tile.opStorage)
                .setHorizontal(true)
                .setPos(guiLeft() + 37, guiTop() + 247)
                .setYSize(14)
                .setMaxXPos(toolkit.guiLeft() + 100, true)
                .setXSize(120);

        // Capacitor slot
        new GuiChestEnergy(template.background, template.energyBar.xPos() - 21, template.energyBar.yPos() + 17);

        // Core field
        new GuiCoreSlot(template.background, guiLeft()+22, guiTop()+206);

        // Crafting
        toolkit.createSlots(template.background, 3, 3, 0)
                .setPos(guiLeft() + 339, guiTop() + 206);
        new GuiCraftingResultSlot(template.background, guiLeft() + 429, guiTop() + 220);
        new GuiCraftingArrow(template.background, guiLeft() + 400, guiTop() + 225);

        //Redstone button
        toolkit.createRSSwitch(template.background, tile)
                .setPos(template.themeButton.xPos(), template.themeButton.yPos() + 24);

        // AutoFill Button
        new GuiAutofillButton(template.background, template.themeButton.xPos(), template.themeButton.yPos() + 36, tile);


        new GuiColorPicker(template, template.themeButton.xPos(), template.themeButton.yPos() + 48, tile);

        // Info Panel
        template.infoPanel = toolkit.createInfoPanel(template.background, false);
        template.infoPanel.setOrigin(() -> new Point(template.themeButton.xPos(), template.themeButton.maxYPos()));
        template.infoPanel.setEnabled(false);

        template.infoPanel.addLabeledValue(GOLD + toolkit.i18n("smelt_energy"), 6, 11, () -> GRAY + (tile.smeltEnergyPerTick.get() + " OP/t"), true);
        template.infoPanel.addLabeledValue(GOLD + toolkit.i18n("smelt_speed"), 6, 11, () -> GRAY + (10000/tile.smeltTime.get() + " %"), true);
    }

    public static class GuiChestEnergy extends GuiSlotRender {
        public GuiChestEnergy(GuiElement parent, int xPos, int yPos) {
            super(xPos, yPos);
            parent.addChild(this);
            // Capacitor
            GuiTexture capacitor = new GuiTexture(16, 16, BCSprites.get("slots/energy")).setPos(xPos() + 1, yPos() + 1);
            this.addChild(capacitor);
            // Arrow; points to the right because x-size is negative
            GuiTexture arrow = new GuiTexture(-14, 14, BCSprites.get("item_charge/right_discharge"));
            arrow.setPos(xPos() + 18, yPos() - arrow.ySize() - 1);
            this.addChild(arrow);
        }
    }

    public static class GuiAutofillButton extends GuiButton {
        public GuiAutofillButton(GuiElement parent, int xPos, int yPos, TileDraconiumChest tile) {
            super();
            this.setPos(xPos, yPos);
            GuiToolkit.addHoverHighlight(this);
            this.setHoverTextDelay(10);
            this.setSize(12, 12);

            GuiTexture icon = new GuiTexture(12, 12, () -> DESprites.get("draconium_chest/autofill_" + tile.autoSmeltMode.get().toString().toLowerCase())).setPos(xPos, yPos);
            icon.setYPosMod(this::yPos);

            this.setHoverText(element -> TextFormatting.WHITE + I18n.get(tile.autoSmeltMode.get().unlocalizedName() + ".info"));
            this.onButtonPressed((pressed) -> tile.setAutoSmeltMode(tile.autoSmeltMode.get().next(hasShiftDown() || pressed == 1)));

            this.addChild(icon);
            parent.addChild(this);
        }
    }

    public static class GuiCoreSlot extends GuiSlotRender {
        public GuiCoreSlot(GuiElement parent, int xPos, int yPos) {
            super(xPos, yPos);
            parent.addChild(this);
            GuiTexture core = new GuiTexture(16, 16, DESprites.get("draconium_chest/core")).setPos(xPos+1, yPos+1);
            this.addChild(core);
        }
    }

    public static class GuiCraftingResultSlot extends GuiTexture {
        public GuiCraftingResultSlot(GuiElement parent, int xPos, int yPos) {
            super(26, 26, () -> DESprites.get("draconium_chest/crafting_field_" + (BCConfig.darkMode ? "dark" : "light")));
            this.setPos(xPos, yPos);
            parent.addChild(this);
        }
    }

    public static class GuiCraftingArrow extends GuiTexture {
        public GuiCraftingArrow(GuiElement parent, int xPos, int yPos) {
            super(22, 15, () -> DESprites.get("draconium_chest/crafting_arrow_" + (BCConfig.darkMode ? "dark" : "light")));
            this.setPos(xPos, yPos);
            parent.addChild(this);
        }
    }

    public static class GuiFurnaceFlames extends GuiTexture {

        public GuiFurnaceFlames(GuiElement parent, int xPos, int yPos, TileDraconiumChest tile) {
            super(88, 15, () -> DESprites.get("draconium_chest/furnace_" + (tile.isSmelting.get() ? "on" : "off")));
            this.setPos(xPos, yPos);
            parent.addChild(this);
        }

        @Override
        public void renderElement(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
            super.renderElement(minecraft, mouseX, mouseY, partialTicks);
        }
    }

    // Currently unused, may be implemented later
    /*private static class ChestRenderer extends GuiElement<ChestRenderer> {

        public float r, g, b;

        public ChestRenderer(GuiPickColourDialog parent) {
            parent.addChild(this);
        }

        @Override
        public void renderElement(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
            IRenderTypeBuffer.Impl getter = Minecraft.getInstance().renderBuffers().bufferSource();
            MatrixStack matrixStack = new MatrixStack();
            matrixStack.scale(1, 1, 1);
            matrixStack.mulPose(new Quaternion(0, 0, 0, true));
            matrixStack.translate(Objects.requireNonNull(this.getParent()).xPos() + 500, this.getParent().yPos(), 0);
            chest.renderToBuffer(matrixStack, getter.getBuffer(chest.renderType(DRACONIUM_CHEST)), 15728640, 655360, r, g, b, 1);
            getter.endBatch();
            super.renderElement(minecraft, mouseX, mouseY, partialTicks);
        }

        public void setColor(int color) {
            r = (float) ((color >> 16) & 0xFF) / 255f;
            g = (float) ((color >> 8) & 0xFF) / 255f;
            b = (float) (color & 0xFF) / 255f;
        }
    }*/

    public class GuiColorPicker extends GuiButton {
        public GuiColorPicker(TGuiBase template, int xPos, int yPos, TileDraconiumChest tile) {
            super(xPos, yPos);
            this.setPos(xPos, yPos);
            GuiToolkit.addHoverHighlight(this);
            this.setHoverTextDelay(10);
            this.setSize(12, 12);

            GuiTexture icon = new GuiTexture(12, 12, DESprites.get("draconium_chest/color_picker")).setPos(xPos, yPos);
            icon.setYPosMod(this::yPos);

            this.setHoverText(element -> TextFormatting.WHITE + I18n.get("gui.draconicevolution.draconium_chest.color_picker.info"));
            // Color picker
            GuiPickColourDialog colorPicker = new GuiPickColourDialog(template.themeButton.xPos(), template.themeButton.yPos() + 48, template.background);
            colorPicker.setIncludeAlpha(false);
            colorPicker.setColour(tile.colour.get());

            this.addChild(icon);

            colorPicker.setColourSelectListener(color -> {
                tile.setColour(color);
                colorPicker.setColour(color);
            });
            this.onButtonPressed(pressed -> {
                if (colorPicker.isVisible()) {
                    colorPicker.close();
                }
                else colorPicker.show();
            });
            this.addChild(colorPicker);
            template.background.addChild(this);
            colorPicker.close();
        }
    }
}
