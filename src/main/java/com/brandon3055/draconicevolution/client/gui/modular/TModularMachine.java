package com.brandon3055.draconicevolution.client.gui.modular;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.api.render.GuiHelper;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElement;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiButton;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.TBasicMachine;
import com.brandon3055.brandonscore.client.render.RenderUtils;
import com.brandon3055.brandonscore.inventory.ContainerSlotLayout;
import com.brandon3055.brandonscore.utils.MathUtils;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleGrid;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleHostContainer;
import com.brandon3055.draconicevolution.client.gui.ModuleGridRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.language.I18n;

import java.awt.*;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by brandon3055 on 18/11/2022
 * <p>
 * This should be used for pretty much all DE machine guis regardless of weather or not the machine is actually modular.
 * This template is the same as TBasicMachine except that if the machine happens to have the ModuleHost capability
 * this will automatically add the module config panel.
 */
public class TModularMachine extends TBasicMachine {

    private final ModularGuiContainer<?> gui;
    public GuiButton moduleButton;
    public ModulePanel modulePanel;

    public TModularMachine(ModularGuiContainer<?> gui, TileBCore tile) {
        super(gui, tile);
        this.gui = gui;
    }

    public TModularMachine(ModularGuiContainer<?> gui, TileBCore tile, ContainerSlotLayout slotLayout) {
        super(gui, tile, slotLayout);
        this.gui = gui;
    }

    public TModularMachine(ModularGuiContainer<?> gui, TileBCore tile, boolean addPlayerSlots) {
        super(gui, tile, addPlayerSlots);
        this.gui = gui;
    }

    public TModularMachine(ModularGuiContainer<?> gui, TileBCore tile, ContainerSlotLayout slotLayout, boolean addPlayerSlots) {
        super(gui, tile, slotLayout, addPlayerSlots);
        this.gui = gui;
    }

    @Override
    public void addDynamicButtons(List<GuiElement<?>> dynamicButtons) {
        super.addDynamicButtons(dynamicButtons);


        if (gui.getContainer() instanceof ModuleHostContainer container && container.getModuleHost() != null) {
            moduleButton = toolkit.createThemedIconButton(background, "grid_small")
                    .onPressed(() -> modulePanel.toggleExpanded());

            modulePanel = new ModulePanel(background, container)
                    .setOrigin(() -> new Point(moduleButton.xPos(), moduleButton.yPos()));

            background.addChild(modulePanel);
            toolkit.jeiExclude(modulePanel);
            dynamicButtons.add(moduleButton);

            TechLevel techLevel = container.getModuleHost().getHostTechLevel();
            StringBuilder gridName = new StringBuilder();
            gridName.append(container.getGrid().getWidth()).append("x").append(container.getGrid().getHeight());
            gridName.append(" ");
            gridName.append(techLevel.getTextColour());
            gridName.append(techLevel.getDisplayName().plainCopy().withStyle(techLevel.getTextColour()).getString());
            gridName.append(ChatFormatting.RESET);
            gridName.append(" ");
            gridName.append(I18n.get("gui.draconicevolution.modular_item.module_grid"));
            moduleButton.setHoverText(gridName.toString());
        }
    }

    public class ModulePanel extends GuiElement<ModulePanel> {
        private GuiElement<?> parent;
        private ModuleGrid grid;
        private ModuleHostContainer container;
        private boolean expanded = false;
        private double animState = 0;
        private Supplier<Point> origin;
        private int extendedWidth;
        private int extendedHeight;
        private ModuleGridRenderer gridRenderer;
        private int colour;

        public ModulePanel(GuiElement<?> parent, ModuleHostContainer container) {
            this.parent = parent;
            this.grid = container.getGrid();
            this.container = container;
            TechLevel techLevel = container.getModuleHost().getHostTechLevel();
            //noinspection ConstantConditions
            this.colour = techLevel.getTextColour().isColor() ? techLevel.getTextColour().getColor() : 0x0080ff;
        }

        public ModulePanel setOrigin(Supplier<Point> origin) {
            this.origin = origin;
            return this;
        }

        public void toggleExpanded() {
            expanded = !expanded;
            if (expanded) {
                int maxWidth = Math.min(grid.getWidth() * 16, screenWidth - parent.maxXPos() - 8);
                int gridSize = (int) (maxWidth / (double) grid.getWidth());
                grid.setCellSize(gridSize);
                extendedWidth = (gridSize * grid.getWidth()) + 6;
                extendedHeight = (gridSize * grid.getHeight()) + 6;
            }
        }

        @Override
        public void addChildElements() {
            super.addChildElements();
            gridRenderer = addChild(new ModuleGridRenderer(grid, gui.playerInv));
            gridRenderer.renderBorder = false;
            gridRenderer.setEnabledCallback(() -> expanded && animState >= 1);
            gui.setFloatingItemOverride(gridRenderer::renderStackOverride);
        }

        private void updatePosSize() {
            int xPos = parent.maxXPos() + 2;
            int yPos = parent.yPos();
            if (infoPanel.isEnabled() && infoPanel.animState > 0) {
                yPos = infoPanel.maxYPos() + 2;
            }

            Rectangle expanded = new Rectangle(xPos, yPos, extendedWidth, extendedHeight);
            Point origin = this.origin.get();
            Rectangle collapsed = new Rectangle(origin.x, origin.y, 12, 12);
            double animState = Math.max(0, this.animState);
            int sx = (int) MathUtils.map(animState, 0, 1, collapsed.x, expanded.x);
            int sy = (int) MathUtils.map(animState, 0, 1, collapsed.y, expanded.y);
            int sw = (int) MathUtils.map(animState, 0, 1, collapsed.width, expanded.width);
            int sh = (int) MathUtils.map(animState, 0, 1, collapsed.height, expanded.height);

            setPosAndSize(sx, sy, sw, sh);
            gridRenderer.setPos(xPos() + 3, yPos() + 3);
            gridRenderer.setSize(xSize() - 6, ySize() - 6);
        }

        @Override
        public void renderElement(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
            double fadeAlpha = Math.min(1, ((animState + 0.5) * 2));
            int backgroundCol = 0x100010 | (int) (0xf0 * fadeAlpha) << 24;
            int borderCol = colour | (int) (0xB0 * fadeAlpha) << 24;
            MultiBufferSource.BufferSource getter = RenderUtils.getGuiBuffers();
            PoseStack poseStack = new PoseStack();
            GuiHelper.drawHoverRect(getter, poseStack, xPos(), yPos(), xSize(), ySize(), backgroundCol, borderCol, false);
            getter.endBatch();

            super.renderElement(minecraft, mouseX, mouseY, partialTicks);
        }

        @Override
        public boolean onUpdate() {
            if (expanded && animState < 1) {
                animState = Math.min(1, animState + 0.2);
            } else if (!expanded && animState > -0.5) {
                animState = Math.max(-0.5, animState - 0.2);
            }
            updatePosSize();
            return super.onUpdate();
        }
    }
}
