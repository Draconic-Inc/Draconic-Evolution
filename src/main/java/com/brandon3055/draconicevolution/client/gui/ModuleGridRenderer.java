package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.brandonscore.client.gui.modulargui.GuiElement;
import com.brandon3055.brandonscore.client.utils.GuiHelper;
import com.brandon3055.draconicevolution.api.modules.IModule;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleGrid;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleItem;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;

import static com.brandon3055.brandonscore.BCConfig.darkMode;

/**
 * Created by brandon3055 on 26/4/20.
 */
public class ModuleGridRenderer extends GuiElement<ModuleGridRenderer> {
    private ModuleGrid grid;
    private PlayerInventory player;
    private boolean doubleClick;
    private long lastClickTime;
    private int lastClickButton;
    private boolean canDrop = false;
    private ModuleGrid.GridPos lastClickPos;

    public ModuleGridRenderer(ModuleGrid grid, PlayerInventory player) {
        this.grid = grid;
        this.player = player;
        this.setSize(grid.getWidth() * grid.getCellSize(), grid.getHeight() * grid.getCellSize());
    }

    @Override
    public void renderElement(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        super.renderElement(minecraft, mouseX, mouseY, partialTicks);
        int light = darkMode ? 0xFFFFFFFF : 0xFFFFFFFF;
        int dark = darkMode ? 0xFF808080 : 0xFF505050;

        drawShadedRect(xPos() - 2, yPos() - 2, xSize() + 4, ySize() + 4, 1, 0, light, dark, midColour(light, dark));
        drawShadedRect(xPos() - 1, yPos() - 1, xSize() + 2, ySize() + 2, 1, 0, dark, light, midColour(light, dark));
        drawColouredRect(xPos(), yPos(), xSize(), ySize(), midColour(light, dark));

        int s = grid.getCellSize();
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                int xPos = xPos() + (x * s);
                int yPos = yPos() + (y * s);
                renderCell(xPos, yPos, s, x, y, GuiHelper.isInRect(xPos, yPos, s, s, mouseX, mouseY));
            }
        }
    }

    @Override
    public boolean renderOverlayLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        if (isMouseOver(mouseX, mouseY)) {
            int x = (mouseX - xPos()) / grid.getWidth();
            int y = (mouseY - yPos()) / grid.getHeight();
            int cs = grid.getCellSize();
            renderCellOverlay(xPos() + (x * cs), yPos() + (y * cs), cs, x, y);
        }
        return super.renderOverlayLayer(minecraft, mouseX, mouseY, partialTicks);
    }

    public void renderCell(int x, int y, int size, int cellX, int cellY, boolean mouseOver) {
        ModuleGrid.GridPos cell = grid.getCell(cellX, cellY);
        if (cell.hasEntity()) {
            ModuleEntity entity = cell.getEntity();
            int cs = grid.getCellSize();
            if (cell.isActualEntityPos()) {
                drawModule(x, y, entity.getModule(), true);
            }
            if (mouseOver) {
                drawColouredRect(xPos() + (entity.getGridX() * cs), yPos() + (entity.getGridY() * cs), cell.getEntity().getWidth() * cs, cell.getEntity().getHeight() * cs, 0x5000FFFF);
            }
        } else {
            drawColouredRect(x + 1, y + 1, size - 2, size - 2, 0xFF505050);
            if (mouseOver) {
                drawColouredRect(x, y, size, size, 0x5000FFFF);
            }
        }
    }

    public void renderCellOverlay(int x, int y, int size, int cellX, int cellY) {
        ModuleGrid.GridPos cell = grid.getCell(cellX, cellY);
    }

    public boolean renderStackOverride(ItemStack stack, int x, int y, String altText) {
        x += 8;
        y += 8;
        if (isMouseOver(x + modularGui.guiLeft(), y + modularGui.guiTop())) {
            IModule<?> module = ModuleItem.getModule(stack);
            if (module != null) {
                zOffset += 250;
                int cs = grid.getCellSize();
                int mw = module.getProperties().getWidth() * cs;
                int mh = module.getProperties().getHeight() * cs;
                drawModule(x - (mw / 2), y - (mh / 2), module, false);
                //Draw Module
                FontRenderer font = stack.getItem().getFontRenderer(stack);
                if (font == null) font = fontRenderer;
                String s = altText == null ? String.valueOf(stack.getCount()) : altText;
                GlStateManager.disableLighting();
                GlStateManager.disableDepthTest();
                GlStateManager.disableBlend();
                font.drawStringWithShadow(s, (float) (x - font.getStringWidth(s)) + (mw / 2F) - 1, (float) (y - font.FONT_HEIGHT) + (mh / 2F), 0xffffff);
                GlStateManager.enableBlend();
                GlStateManager.enableLighting();
                GlStateManager.enableDepthTest();
                GlStateManager.enableBlend();
                zOffset -= 250;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        canDrop = false;
        if (isMouseOver(mouseX, mouseY)) {
            InputMappings.Input mouseKey = InputMappings.Type.MOUSE.getOrMakeInput(button);
            boolean pickBlock = mc.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseKey);
            ModuleGrid.GridPos cell = getCellAtPos(mouseX, mouseY, true);
            long i = Util.milliTime();
            //Double click pickup wont track the cell you click
            doubleClick = i - lastClickTime < 250L && lastClickButton == button && getCellAtPos(mouseX, mouseY, false).equals(lastClickPos);

            if ((button == 0 || pickBlock) && cell.isValidCell()) {
                if (player.getItemStack().isEmpty()) {
                    if (pickBlock) {
                        handleGridClick(cell, button, ClickType.CLONE); //Creative Clone
                    } else {
                        boolean shiftClick = (InputMappings.isKeyDown(Minecraft.getInstance().mainWindow.getHandle(), 340) || InputMappings.isKeyDown(Minecraft.getInstance().mainWindow.getHandle(), 344));
                        ClickType clicktype = ClickType.PICKUP;
                        if (shiftClick) {
                            clicktype = ClickType.QUICK_MOVE;
                        }
                        handleGridClick(cell, button, clicktype);
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
            this.handleGridClick(cell, button, ClickType.PICKUP_ALL);
            this.doubleClick = false;
            this.lastClickTime = 0L;
        } else if (canDrop && button == 0) {
            handleGridClick(cell, button, ClickType.PICKUP);
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    protected void handleGridClick(ModuleGrid.GridPos cell, int mouseButton, ClickType type) {
        DraconicNetwork.sendModuleContainerClick(cell, mouseButton, type);
        grid.cellClicked(cell, mouseButton, type);
    }

    /**
     * @param withPlaceOffset if true and the player clicks the grid while holding a module
     *                        with a size larger than 1x1 this will return the cell position
     *                        at the top left of the module. Basically the cell the module is
     *                        going to be in stalled in.
     */
    private ModuleGrid.GridPos getCellAtPos(double xPos, double yPos, boolean withPlaceOffset) {
        int cs = grid.getCellSize();
        int x = (int) ((xPos - xPos()) / cs);
        int y = (int) ((yPos - yPos()) / cs);
        IModule<?> module = ModuleItem.getModule(player.getItemStack());
        if (module != null && withPlaceOffset) {
            int mw = module.getProperties().getWidth() * cs;
            int mh = module.getProperties().getHeight() * cs;
            x = (int) ((xPos - xPos() - (mw / 2D) + (cs / 2D)) / cs);
            y = (int) ((yPos - yPos() - (mh / 2D) + (cs / 2D)) / cs);
        }
        return grid.getCell(x, y);
    }

    private void drawModule(int x, int y, IModule<?> module, boolean installed) {
        int cs = grid.getCellSize();
        int mw = module.getProperties().getWidth() * cs;
        int mh = module.getProperties().getHeight() * cs;
        drawColouredRect(x, y, mw, mh, installed ? 0xFF008080 : 0xFF00FFFF);
        drawBorderedRect(x, y, mw, mh, 1, 0, 0xFF000080);
    }

    @Override
    public boolean onUpdate() {
        grid.container.clientTick();
        return super.onUpdate();
    }
}
