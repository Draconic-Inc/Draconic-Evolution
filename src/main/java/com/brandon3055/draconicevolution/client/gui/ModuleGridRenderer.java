package com.brandon3055.draconicevolution.client.gui;

import codechicken.lib.gui.modular.elements.GuiElement;
import codechicken.lib.gui.modular.lib.BackgroundRender;
import codechicken.lib.gui.modular.lib.GuiRender;
import codechicken.lib.gui.modular.lib.geometry.Constraint;
import codechicken.lib.gui.modular.lib.geometry.GuiParent;
import com.brandon3055.brandonscore.BCConfig;
import com.brandon3055.brandonscore.client.gui.GuiToolkit.Palette;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.items.ModuleItem;
import com.brandon3055.draconicevolution.api.modules.lib.InstallResult;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleGrid;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import static codechicken.lib.gui.modular.lib.geometry.GeoParam.HEIGHT;
import static codechicken.lib.gui.modular.lib.geometry.GeoParam.WIDTH;

/**
 * Created by brandon3055 on 26/4/20.
 */
public class ModuleGridRenderer extends GuiElement<ModuleGridRenderer> implements BackgroundRender {

    private ModuleGrid grid;
    private Inventory player;
    private boolean doubleClick;
    private long lastClickTime;
    private int lastClickButton;
    private boolean canDrop = false;
    private ModuleGrid.GridPos lastClickPos;
    private List<Component> lastError = null;
    private int lastErrorTime = 0;
    public boolean renderBorder = true;


    public ModuleGridRenderer(@NotNull GuiParent<?> parent, ModuleGrid grid, Inventory player) {
        super(parent);
        this.grid = grid;
        this.player = player;
        this.constrain(WIDTH, Constraint.literal(grid.getWidth() * grid.getCellSize()));
        this.constrain(HEIGHT, Constraint.literal(grid.getHeight() * grid.getCellSize()));
        this.setTooltip(() -> lastError == null ? Collections.emptyList() : lastError);
        this.setTooltipDelay(0);
        this.getModularGui().setFloatingItemDisablesToolTips(false);
    }

    @Override
    public void renderBackground(GuiRender render, double mouseX, double mouseY, float partialTicks) {
        int light = Palette.BG.accentLight();
        int dark = Palette.BG.accentDark();
        int fill = Palette.BG.fill();
        if (renderBorder) {
            render.shadedRect(xMin() - 2, yMin() - 2, xSize() + 4, ySize() + 4, 1, light, dark, fill);
            render.shadedRect(xMin() - 1, yMin() - 1, xSize() + 2, ySize() + 2, 1, dark, light, fill);
        }
        render.rect(xMin(), yMin(), xSize(), ySize(), GuiRender.midColour(light, dark));

        int s = grid.getCellSize();
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                int xPos = (int) xMin() + (x * s);
                int yPos = (int) yMin() + (y * s);
                renderCell(render, xPos, yPos, s, x, y, mouseX, mouseY, GuiRender.isInRect(xPos, yPos, s, s, mouseX, mouseY), partialTicks);
            }
        }
    }

    @Override
    public boolean renderOverlay(GuiRender render, double mouseX, double mouseY, float partialTicks, boolean consumed) {
        if (consumed) return super.renderOverlay(render, mouseX, mouseY, partialTicks, consumed);

        for (ModuleEntity<?> entity : grid.getModuleHost().getModuleEntities()) {
            int cs = grid.getCellSize();
            int mw = entity.getWidth() * cs;
            int mh = entity.getHeight() * cs;
            int x = (int) xMin() + (entity.getGridX() * cs);
            int y = (int) yMin() + (entity.getGridY() * cs);
            boolean mouseOver = GuiRender.isInRect(x, y, mw, mh, mouseX, mouseY) && isMouseOver();
            if (entity.renderModuleOverlay(this, grid.container.getModuleContext(), render, x, y, mw, mh, mouseX, mouseY, partialTicks, mouseOver ? hoverTime : 0)) {
                return true;
            }
        }

        return super.renderOverlay(render, mouseX, mouseY, partialTicks, consumed);
    }

    public void renderCell(GuiRender render, int x, int y, int size, int cellX, int cellY, double mouseX, double mouseY, boolean mouseOver, float partialTicks) {
        ModuleGrid.GridPos cell = grid.getCell(cellX, cellY);
        if (cell.hasEntity()) {
            ModuleEntity<?> entity = cell.getEntity();
            int cs = grid.getCellSize();
            int mw = entity.getWidth() * cs;                                //Module Render Width
            int mh = entity.getHeight() * cs;                               //Module Render Height
            if (cell.isActualEntityPos()) {
                entity.renderModule(this, render, x, y, mw, mh, mouseX, mouseY, false, partialTicks);
            }
        } else {
            render.rect(x + 1, y + 1, size - 2, size - 2, BCConfig.darkMode ? 0xFF808080 : 0xFF505050);
            if (mouseOver) {
                render.rect(x, y, size, size, 0x50FFFFFF);
            }
        }
    }

    public boolean renderStackOverride(GuiRender render, ItemStack stack, int x, int y, String altText) {
        x += 8;
        y += 8;
        if (isMouseOver()) {//GuiRender.isInRect(getModularGui().xMin(), getModularGui().yMin(), getModularGui().xSize(), getModularGui().ySize(), x + getModularGui().xMin(), y + getModularGui().yMin())) {
            Module<?> module = ModuleItem.getModule(stack);
            if (module != null) {
                ModuleEntity<?> entity = module.createEntity();
                int cs = grid.getCellSize();
                int mw = module.getProperties().getWidth() * cs;
                int mh = module.getProperties().getHeight() * cs;


                render.pose().pushPose();
//                render.pose().translate(0.0F, 0.0F, 50F);
                entity.renderModule(this, render, x - (mw / 2), y - (mh / 2), mw, mh, x, y, true, mc().getDeltaFrameTime());
                if (stack.getCount() > 1 || altText != null) {
                    String s = altText == null ? String.valueOf(stack.getCount()) : altText;
//                    render.pose().translate(0, 0, 250);
                    render.drawString(s, (float) (x - font().width(s)) + (mw / 2F) + 1, (float) (y - font().lineHeight) + (mh / 2F) + 2, 0xffffff, true);
                }
                render.pose().popPose();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        canDrop = false;
        lastError = null;

        if (isMouseOver()) {
            InputConstants.Key mouseKey = InputConstants.Type.MOUSE.getOrCreate(button);
            boolean pickBlock = mc().options.keyPickItem.isActiveAndMatches(mouseKey);
            ModuleGrid.GridPos cell = getCellAtPos(mouseX, mouseY, true);
            long i = Util.getMillis();
            //Double click pickup wont track the cell you click
            doubleClick = i - lastClickTime < 250L && lastClickButton == button && getCellAtPos(mouseX, mouseY, false).equals(lastClickPos);

            if (cell.isValidCell()) {
                ModuleEntity<?> entity = cell.getEntity();
                if (entity != null) {
                    int cs = grid.getCellSize();
                    int mw = entity.getWidth() * cs;
                    int mh = entity.getHeight() * cs;
                    int xPos = (int) xMin() + (entity.getGridX() * cs);
                    int yPos = (int) yMin() + (entity.getGridY() * cs);
                    if (entity.clientModuleClicked(this, player.player, xPos, yPos, mw, mh, mouseX, mouseY, button)) {
                        return true;
                    }
                }

                if (player.player.containerMenu.getCarried().isEmpty()) {
                    if (pickBlock) {
                        handleGridClick(cell, mouseX, mouseY, button, ClickType.CLONE); //Creative Clone
                    } else {
                        boolean shiftClick = (InputConstants.isKeyDown(mc().getWindow().getWindow(), 340) || InputConstants.isKeyDown(mc().getWindow().getWindow(), 344));
                        ClickType clicktype = ClickType.PICKUP;
                        if (shiftClick) {
                            clicktype = ClickType.QUICK_MOVE;
                        }
                        handleGridClick(cell, mouseX, mouseY, button, clicktype);
                    }
                } else {
                    canDrop = true;
                }
            }

            lastClickTime = i;
            lastClickPos = getCellAtPos(mouseX, mouseY, false);
            lastClickButton = button;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        ModuleGrid.GridPos cell = getCellAtPos(mouseX, mouseY, true);
        if (this.doubleClick && button == 0) {
            this.handleGridClick(cell, mouseX, mouseY, button, ClickType.PICKUP_ALL);
            this.doubleClick = false;
            this.lastClickTime = 0L;
        } else if (canDrop) {
            handleGridClick(cell, mouseX, mouseY, button, ClickType.PICKUP);
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    protected void handleGridClick(ModuleGrid.GridPos cell, double mouseX, double mouseY, int mouseButton, ClickType type) {
        float x = 0.5F;
        float y = 0.5F;
        ModuleEntity<?> entity = cell.getEntity();
        if (entity != null) {
            int cs = grid.getCellSize();
            int mw = entity.getWidth() * cs;
            int mh = entity.getHeight() * cs;
            int mx = (int) xMin() + (entity.getGridX() * cs);
            int my = (int) xMin() + (entity.getGridY() * cs);
            x = (float) (mouseX - mx) / mw;
            y = (float) (mouseY - my) / mh;
        }
        DraconicNetwork.sendModuleContainerClick(cell, x, y, mouseButton, type);
        InstallResult result = grid.cellClicked(cell, x, y, mouseButton, type);
        if (result != null && result.resultType != InstallResult.InstallResultType.YES && result.resultType != InstallResult.InstallResultType.OVERRIDE) {
            lastError = result.reason;
            lastErrorTime = 0;
        }
    }

    /**
     * @param withPlaceOffset if true and the player clicks the grid while holding a module
     *                        with a size larger than 1x1 this will return the cell position
     *                        at the top left of the module. Basically the cell the module is
     *                        going to be in stalled in.
     */
    private ModuleGrid.GridPos getCellAtPos(double xPos, double yPos, boolean withPlaceOffset) {
        int cs = grid.getCellSize();
        int x = (int) ((xPos - xMin()) / cs);
        int y = (int) ((yPos - yMin()) / cs);
        Module<?> module = ModuleItem.getModule(player.player.containerMenu.getCarried());
        if (module != null && withPlaceOffset) {
            int mw = module.getProperties().getWidth() * cs;
            int mh = module.getProperties().getHeight() * cs;
            x = (int) ((xPos - xMin() - (mw / 2D) + (cs / 2D)) / cs);
            y = (int) ((yPos - yMin() - (mh / 2D) + (cs / 2D)) / cs);
        }
        return grid.getCell(x, y);
    }

    private ModuleGrid.GridPos hoverCell = null;
    private int hoverTime = 0;

    @Override
    public void tick(double mouseX, double mouseY) {
        ModuleGrid.GridPos cell = getCellAtPos(mouseX, mouseY, false);
        if (cell.hasEntity()) {
            ModuleEntity<?> entity = cell.getEntity();
            cell = grid.getCell(entity.getGridX(), entity.getGridY());
            if (cell.equals(hoverCell)) {
                hoverTime++;
            } else {
                hoverTime = 0;
                hoverCell = cell;
            }
        } else {
            hoverTime = 0;
        }

        if (lastError != null && lastErrorTime++ > 100) {
            lastError = null;
        }
        super.tick(mouseX, mouseY);
    }
}
