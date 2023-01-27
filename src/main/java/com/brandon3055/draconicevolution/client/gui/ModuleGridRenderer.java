package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.brandonscore.BCConfig;
import com.brandon3055.brandonscore.api.render.GuiHelper;
import com.brandon3055.brandonscore.client.gui.GuiToolkit.Palette;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElement;
import com.brandon3055.brandonscore.client.render.RenderUtils;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.lib.InstallResult;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleGrid;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleItem;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Created by brandon3055 on 26/4/20.
 */
public class ModuleGridRenderer extends GuiElement<ModuleGridRenderer> {

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


    public ModuleGridRenderer(ModuleGrid grid, Inventory player) {
        this.grid = grid;
        this.player = player;
        this.setSize(grid.getWidth() * grid.getCellSize(), grid.getHeight() * grid.getCellSize());
    }

    @Override
    public void renderElement(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        super.renderElement(minecraft, mouseX, mouseY, partialTicks);
        MultiBufferSource.BufferSource getter = RenderUtils.getGuiBuffers();
        PoseStack poseStack = new PoseStack();

        int light = Palette.BG.accentLight();
        int dark = Palette.BG.accentDark();
        int fill = Palette.BG.fill();
        if (renderBorder) {
            GuiHelper.drawShadedRect(getter, poseStack, xPos() - 2, yPos() - 2, xSize() + 4, ySize() + 4, 1, 0, light, dark, fill);
            GuiHelper.drawShadedRect(getter, poseStack, xPos() - 1, yPos() - 1, xSize() + 2, ySize() + 2, 1, 0, dark, light, fill);
        }
        GuiHelper.drawRect(getter, poseStack, xPos(), yPos(), xSize(), ySize(), midColour(light, dark));

        int s = grid.getCellSize();
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                int xPos = xPos() + (x * s);
                int yPos = yPos() + (y * s);
                renderCell(getter, xPos, yPos, s, x, y, mouseX, mouseY, GuiHelper.isInRect(xPos, yPos, s, s, mouseX, mouseY), partialTicks);
            }
        }
        getter.endBatch();
    }

    @Override
    public boolean renderOverlayLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        boolean carrying = !player.player.containerMenu.getCarried().isEmpty();
        MultiBufferSource.BufferSource getter = RenderUtils.getGuiBuffers();
        PoseStack poseStack = new PoseStack();
        poseStack.translate(0, 0, getRenderZLevel());

        if (isMouseOver(mouseX, mouseY) && lastError != null) {
            renderTooltip(poseStack, lastError, mouseX, mouseY);
            return true;
        }

        for (ModuleEntity<?> entity : grid.getModuleHost().getModuleEntities()) {
            int cs = grid.getCellSize();
            int mw = entity.getWidth() * cs;
            int mh = entity.getHeight() * cs;
            int x = xPos() + (entity.getGridX() * cs);
            int y = yPos() + (entity.getGridY() * cs);
            boolean mouseOver = GuiHelper.isInRect(x, y, mw, mh, mouseX, mouseY);
            if (entity.renderModuleOverlay(this, grid.container.getModuleContext(), getter, poseStack, x, y, mw, mh, mouseX, mouseY, partialTicks, mouseOver ? hoverTime : 0)) {
                return true;
            }
        }
        getter.endBatch();

        return super.renderOverlayLayer(minecraft, mouseX, mouseY, partialTicks);
    }

    public void renderCell(MultiBufferSource getter, int x, int y, int size, int cellX, int cellY, double mouseX, double mouseY, boolean mouseOver, float partialTicks) {
        PoseStack poseStack = new PoseStack();
        poseStack.translate(0, 0, getRenderZLevel());
        ModuleGrid.GridPos cell = grid.getCell(cellX, cellY);
        if (cell.hasEntity()) {
            ModuleEntity<?> entity = cell.getEntity();
            int cs = grid.getCellSize();
            int mw = entity.getWidth() * cs;                                //Module Render Width
            int mh = entity.getHeight() * cs;                               //Module Render Height
            if (cell.isActualEntityPos()) {
                entity.renderModule(this, getter, poseStack, x, y, mw, mh, mouseX, mouseY, false, partialTicks);
            }
        } else {
            GuiHelper.drawRect(getter, poseStack, x + 1, y + 1, size - 2, size - 2, BCConfig.darkMode ? 0xFF808080 : 0xFF505050);
            if (mouseOver) {
                GuiHelper.drawRect(getter, poseStack, x, y, size, size, 0x50FFFFFF);
            }
        }
    }

    public boolean renderStackOverride(ItemStack stack, int x, int y, String altText) {
        x += 8;
        y += 8;
        if (isMouseOver(x + modularGui.guiLeft(), y + modularGui.guiTop())) {
            Module<?> module = ModuleItem.getModule(stack);
            if (module != null) {
                ModuleEntity<?> entity = module.createEntity();
                int cs = grid.getCellSize();
                int mw = module.getProperties().getWidth() * cs;
                int mh = module.getProperties().getHeight() * cs;
                PoseStack poseStack = new PoseStack();
                poseStack.translate(0, 0, getRenderZLevel());
                MultiBufferSource.BufferSource getter = RenderUtils.getGuiBuffers();
                entity.renderModule(this, getter, poseStack, x - (mw / 2), y - (mh / 2), mw, mh, x, y, true, mc.getDeltaFrameTime());
                getter.endBatch();
                if (stack.getCount() > 1 || altText != null) {
                    Font font = fontRenderer;
                    String s = altText == null ? String.valueOf(stack.getCount()) : altText;
                    poseStack.translate(0, 0, 250);
                    font.drawShadow(poseStack, s, (float) (x - font.width(s)) + (mw / 2F) + 1, (float) (y - font.lineHeight) + (mh / 2F) + 2, 0xffffff);
                    poseStack.translate(0, 0, -250);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        canDrop = false;
        lastError = null;
        if (isMouseOver(mouseX, mouseY)) {
            InputConstants.Key mouseKey = InputConstants.Type.MOUSE.getOrCreate(button);
            boolean pickBlock = mc.options.keyPickItem.isActiveAndMatches(mouseKey);
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
                    int xPos = xPos() + (entity.getGridX() * cs);
                    int yPos = yPos() + (entity.getGridY() * cs);
                    if (entity.clientModuleClicked(this, player.player, xPos, yPos, mw, mh, mouseX, mouseY, button)) {
                        return true;
                    }
                }

                if (player.player.containerMenu.getCarried().isEmpty()) {
                    if (pickBlock) {
                        handleGridClick(cell, mouseX, mouseY, button, ClickType.CLONE); //Creative Clone
                    } else {
                        boolean shiftClick = (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344));
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
            int mx = xPos() + (entity.getGridX() * cs);
            int my = yPos() + (entity.getGridY() * cs);
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
        int x = (int) ((xPos - xPos()) / cs);
        int y = (int) ((yPos - yPos()) / cs);
        Module<?> module = ModuleItem.getModule(player.player.containerMenu.getCarried());
        if (module != null && withPlaceOffset) {
            int mw = module.getProperties().getWidth() * cs;
            int mh = module.getProperties().getHeight() * cs;
            x = (int) ((xPos - xPos() - (mw / 2D) + (cs / 2D)) / cs);
            y = (int) ((yPos - yPos() - (mh / 2D) + (cs / 2D)) / cs);
        }
        return grid.getCell(x, y);
    }

    private ModuleGrid.GridPos hoverCell = null;
    private int hoverTime = 0;

    @Override
    public boolean onUpdate() {
        ModuleGrid.GridPos cell = getCellAtPos(getMouseX(), getMouseY(), false);
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
        return super.onUpdate();
    }
}
