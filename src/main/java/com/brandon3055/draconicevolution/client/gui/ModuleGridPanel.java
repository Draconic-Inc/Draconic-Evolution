package com.brandon3055.draconicevolution.client.gui;

import codechicken.lib.gui.modular.elements.GuiElement;
import codechicken.lib.gui.modular.lib.BackgroundRender;
import codechicken.lib.gui.modular.lib.Constraints;
import codechicken.lib.gui.modular.lib.GuiRender;
import codechicken.lib.gui.modular.lib.geometry.GuiParent;
import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleGrid;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleHostContainer;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

import static codechicken.lib.gui.modular.lib.geometry.Constraint.*;
import static codechicken.lib.gui.modular.lib.geometry.GeoParam.*;

/**
 * Created by brandon3055 on 13/06/2024
 */
public class ModuleGridPanel extends GuiElement<ModuleGridPanel> implements BackgroundRender {
    private static AtomicBoolean globalExpanded = new AtomicBoolean(false);

    private final int colour;
    private double animState = 0;
    public int fixedGridSize = -1;

    private ModuleGrid grid;
    private AtomicBoolean expanded = globalExpanded;
    public ModuleGridRenderer gridRenderer;

    public ModuleGridPanel(@NotNull GuiParent<?> parent, ModuleHostContainer container) {
        super(parent);
        this.jeiExclude();
        this.grid = container.getGrid();
        TechLevel techLevel = container.getModuleHost().getHostTechLevel();
        //noinspection DataFlowIssue
        this.colour = techLevel.getTextColour().isColor() ? techLevel.getTextColour().getColor() : 0x0080ff;

        gridRenderer = new ModuleGridRenderer(this, grid, mc().player.getInventory())
                .setEnabled(() -> expanded.get() && animState >= 1);
        gridRenderer.renderBorder = false;
        Constraints.center(gridRenderer, this);

        this.constrain(WIDTH, dynamic(() -> ((computeGridSize() * grid.getWidth()) + 6D) * Math.max(0, animState)));
        this.constrain(HEIGHT, dynamic(() -> ((computeGridSize() * grid.getHeight()) + 6D) * Math.max(0, animState)));
    }

    public ModuleGridPanel setGridPos(GridPos pos, int offset) {
        constrain(TOP, null).constrain(LEFT, null).constrain(BOTTOM, null).constrain(RIGHT, null);
        GuiElement<?> root = getModularGui().getRoot();
        return switch (pos) {
            case TOP_RIGHT -> constrain(TOP, match(root.get(TOP))).constrain(LEFT, relative(root.get(RIGHT), offset));
            case BOTTOM_RIGHT -> constrain(BOTTOM, match(root.get(BOTTOM))).constrain(LEFT, relative(root.get(RIGHT), offset));
            case TOP_LEFT -> constrain(TOP, match(root.get(TOP))).constrain(RIGHT, relative(root.get(LEFT), offset));
            case BOTTOM_LEFT -> constrain(BOTTOM, match(root.get(BOTTOM))).constrain(RIGHT, relative(root.get(LEFT), offset));
        };
    }

    private int computeGridSize() {
        if (fixedGridSize > 0) return fixedGridSize;
        GuiElement<?> root = getModularGui().getRoot();
        int swidth = scaledScreenWidth();
        double space = root.xMin() < swidth / 2D && root.xMax() < swidth / 2D ? root.xMin() : scaledScreenWidth() - root.xMax();
        double maxWidth = Math.min(grid.getWidth() * 16, space - 8);
        return (int) (maxWidth / (double) grid.getWidth());
    }

    public void toggleExpanded() {
        expanded.set(!expanded.get());
    }

    public ModuleGridPanel setExpandedStateHolder(AtomicBoolean expanded) {
        this.expanded = expanded;
        return this;
    }

    @Override
    public void tick(double mouseX, double mouseY) {
        super.tick(mouseX, mouseY);

        if (grid.getCellSize() != computeGridSize()) {
            grid.setCellSize(computeGridSize());
        }

        if (expanded.get() && animState < 1) {
            animState = Math.min(1, animState + 0.2);
        } else if (!expanded.get() && animState > 0) {
            animState = Math.max(0, animState - 0.2);
        }
    }

    @Override
    public void renderBackground(GuiRender render, double mouseX, double mouseY, float partialTicks) {
        double fadeAlpha = MathHelper.clip((animState - 0.1) * 1.1, 0, 1);
        int backgroundCol = 0x100010 | (int) (0xf0 * fadeAlpha) << 24;
        int borderCol = colour | (int) (0xB0 * fadeAlpha) << 24;
        int borderColorEnd = (borderCol & 0xFEFEFE) >> 1 | borderCol & 0xFF000000;
        render.toolTipBackground(xMin(), yMin(), xSize(), ySize(), backgroundCol, borderCol, borderColorEnd);
    }

    public enum GridPos {
        TOP_RIGHT,
        BOTTOM_RIGHT,
        TOP_LEFT,
        BOTTOM_LEFT
    }
}
