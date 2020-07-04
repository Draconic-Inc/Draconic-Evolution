package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.brandonscore.client.gui.modulargui.GuiElement;
import com.brandon3055.brandonscore.client.gui.modulargui.ThemedElements;
import com.brandon3055.brandonscore.client.utils.GuiHelper;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.lib.InstallResult;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleGrid;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleItem;
import com.brandon3055.draconicevolution.client.ClientProxy;
import com.brandon3055.draconicevolution.client.ModuleSpriteUploader;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.opengl.GL11;

import java.util.Collections;

import static com.brandon3055.brandonscore.BCConfig.darkMode;
import static com.brandon3055.draconicevolution.api.modules.lib.InstallResult.InstallResultType.*;
import static com.brandon3055.draconicevolution.api.modules.lib.InstallResult.InstallResultType.YES;

/**
 * Created by brandon3055 on 26/4/20.
 */
public class ModuleGridRenderer extends GuiElement<ModuleGridRenderer> {
    private static final RenderType moduleType = RenderType.makeType("module_type", DefaultVertexFormats.POSITION_TEX, GL11.GL_QUADS, 256, RenderType.State.getBuilder()
            .texture(new RenderState.TextureState(ModuleSpriteUploader.LOCATION_MODULE_TEXTURE, false, false))
            .transparency(RenderState.TRANSLUCENT_TRANSPARENCY)
            .build(false)
    );

    private ModuleGrid grid;
    private PlayerInventory player;
    private boolean doubleClick;
    private long lastClickTime;
    private int lastClickButton;
    private boolean canDrop = false;
    private ModuleGrid.GridPos lastClickPos;
    private ITextComponent lastError = null;
    private int lastErrorTime = 0;


    public ModuleGridRenderer(ModuleGrid grid, PlayerInventory player) {
        this.grid = grid;
        this.player = player;
        this.setSize(grid.getWidth() * grid.getCellSize(), grid.getHeight() * grid.getCellSize());
    }

    @Override
    public void renderElement(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        super.renderElement(minecraft, mouseX, mouseY, partialTicks);
        IRenderTypeBuffer.Impl getter = minecraft.getRenderTypeBuffers().getBufferSource();

        int light = ThemedElements.getBgLight();
        int dark = ThemedElements.getBgDark();
        int fill = ThemedElements.getBgFill();
        drawShadedRect(getter, xPos() - 2, yPos() - 2, xSize() + 4, ySize() + 4, 1, 0, light, dark, fill);
        drawShadedRect(getter, xPos() - 1, yPos() - 1, xSize() + 2, ySize() + 2, 1, 0, dark, light, fill);
        drawColouredRect(getter, xPos(), yPos(), xSize(), ySize(), midColour(light, dark));

        int s = grid.getCellSize();
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                int xPos = xPos() + (x * s);
                int yPos = yPos() + (y * s);
                renderCell(getter, xPos, yPos, s, x, y, GuiHelper.isInRect(xPos, yPos, s, s, mouseX, mouseY));
            }
        }
        getter.finish();
    }

    @Override
    public boolean renderOverlayLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        if (isMouseOver(mouseX, mouseY)) {
            if (player.getItemStack().isEmpty()) {
                renderCellOverlay(mouseX, mouseY);
            } else if (lastError != null) {
                drawHoveringText(Collections.singletonList(lastError.getFormattedText()), mouseX, mouseY, fontRenderer);
            }
        }
        return super.renderOverlayLayer(minecraft, mouseX, mouseY, partialTicks);
    }

    public void renderCell(IRenderTypeBuffer getter, int x, int y, int size, int cellX, int cellY, boolean mouseOver) {
        ModuleGrid.GridPos cell = grid.getCell(cellX, cellY);
        if (cell.hasEntity()) {
            ModuleEntity entity = cell.getEntity();
            int cs = grid.getCellSize();
            if (cell.isActualEntityPos()) {
                drawModule(getter, x, y, entity.getModule());
            }
            if (mouseOver) {
                drawColouredRect(getter, xPos() + (entity.getGridX() * cs), yPos() + (entity.getGridY() * cs), cell.getEntity().getWidth() * cs, cell.getEntity().getHeight() * cs, 0x50FFFFFF);
            }
        } else {
            drawColouredRect(getter, x + 1, y + 1, size - 2, size - 2, darkMode ? 0xFF808080 : 0xFF505050);
            if (mouseOver) {
                drawColouredRect(getter, x, y, size, size, 0x50FFFFFF);
            }
        }
    }

    public void renderCellOverlay(int mouseX, int mouseY) {
        ModuleGrid.GridPos cell = getCellAtPos(mouseX, mouseY, false);
        if (cell.hasEntity() && hoverTime > 10) {
            Item item = cell.getEntity().getModule().getItem();
            ItemStack stack = new ItemStack(item);
            cell.getEntity().writeToItemStack(stack, grid.container.getModuleContext());
            FontRenderer font = stack.getItem().getFontRenderer(stack);
            if (font == null) font = fontRenderer;
            drawHoveringText(getTooltipFromItem(stack), mouseX, mouseY, font);
        }
    }

    public boolean renderStackOverride(ItemStack stack, int x, int y, String altText) {
        x += 8;
        y += 8;
        if (isMouseOver(x + modularGui.guiLeft(), y + modularGui.guiTop())) {
            Module<?> module = ModuleItem.getModule(stack);
            if (module != null) {
                int cs = grid.getCellSize();
                int mw = module.getProperties().getWidth() * cs;
                int mh = module.getProperties().getHeight() * cs;
                IRenderTypeBuffer.Impl getter = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
                drawModule(getter, x - (mw / 2), y - (mh / 2), module);
                getter.finish();
                if (stack.getCount() > 1 || altText != null) {
                    zOffset += 250;
                    FontRenderer font = stack.getItem().getFontRenderer(stack);
                    if (font == null) font = fontRenderer;
                    String s = altText == null ? String.valueOf(stack.getCount()) : altText;
                    font.drawStringWithShadow(s, (float) (x - font.getStringWidth(s)) + (mw / 2F) - 1, (float) (y - font.FONT_HEIGHT) + (mh / 2F), 0xffffff);
                    zOffset -= 250;
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
                        boolean shiftClick = (InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 340) || InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 344));
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
        InstallResult result = grid.cellClicked(cell, mouseButton, type);
        if (result != null && result.resultType != YES && result.resultType != OVERRIDE) {
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
        Module<?> module = ModuleItem.getModule(player.getItemStack());
        if (module != null && withPlaceOffset) {
            int mw = module.getProperties().getWidth() * cs;
            int mh = module.getProperties().getHeight() * cs;
            x = (int) ((xPos - xPos() - (mw / 2D) + (cs / 2D)) / cs);
            y = (int) ((yPos - yPos() - (mh / 2D) + (cs / 2D)) / cs);
        }
        return grid.getCell(x, y);
    }

    private void drawModule(IRenderTypeBuffer getter, int x, int y, Module<?> module) {
        int cs = grid.getCellSize();
        int mw = module.getProperties().getWidth() * cs;
        int mh = module.getProperties().getHeight() * cs;

        int colour = getModuleColour(module);
        drawColouredRect(getter, x, y, mw, mh, colour);
        drawBorderedRect(getter, x, y, mw, mh, 1, 0, mixColours(colour, 0x20202000, true));

        if (module.getProperties().getTechLevel() == TechLevel.CHAOTIC) {
            IVertexBuilder builder = getter.getBuffer(RenderType.getGlint());
            float zLevel = getRenderZLevel();
            builder.pos(x, y + mh, zLevel).tex(0, ((float) mh / mw) / 64F).endVertex();
            builder.pos(x + mw, y + mh, zLevel).tex(((float) mw / mh) / 64F, ((float) mh / mw) / 64F).endVertex();
            builder.pos(x + mw, y, zLevel).tex(((float) mw / mh) / 64F, 0).endVertex();
            builder.pos(x, y, zLevel).tex(0, 0).endVertex();
            ((IRenderTypeBuffer.Impl) getter).finish(); //Because i want the glint to render under the sprite.
        }

        TextureAtlasSprite sprite = ClientProxy.moduleSpriteUploader.getSprite(module);
        float ar = (float) sprite.getWidth() / (float) sprite.getHeight();
        float iar = (float) sprite.getHeight() / (float) sprite.getWidth();

        IVertexBuilder builder = getter.getBuffer(moduleType);
        if (iar * mw <= mh) { //Fit Width
            double height = mw * iar;
            bufferSprite(builder, sprite, x, y + (mh / 2D) - (height / 2D), mw, height);
        } else { //Fit height
            double width = mh * ar;
            bufferSprite(builder, sprite, x + (mw / 2D) - (width / 2D), y, width, mh);
        }
    }

    private void bufferSprite(IVertexBuilder builder, TextureAtlasSprite sprite, double x, double y, double width, double height) {
        //@formatter:off
        builder.pos(x,         y + height, zOffset).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();
        builder.pos(x + width, y + height, zOffset).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
        builder.pos(x + width, y,          zOffset).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();
        builder.pos(x,         y,          zOffset).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
        //@formatter:on
    }


    private ModuleGrid.GridPos hoverCell = null;
    private int hoverTime = 0;

    @Override
    public boolean onUpdate() {
        ModuleGrid.GridPos cell = getCellAtPos(getMouseX(), getMouseY(), false);
        if (cell.hasEntity()) {
            if (cell.equals(hoverCell)) {
                hoverTime++;
            } else {
                hoverTime = 0;
                hoverCell = cell;
            }
        } else {
            hoverTime = 0;
        }

        grid.container.clientTick();
        if (lastError != null && lastErrorTime++ > 100) {
            lastError = null;
        }
        return super.onUpdate();
    }

    private int getModuleColour(Module<?> module) {
        switch (module.getProperties().getTechLevel()) {
            case DRACONIUM:
                return 0xff1e4596;
            case WYVERN:
                return 0xFF3c1551;
            case DRACONIC:
                return 0xFFcb2a00;
            case CHAOTIC:
                return 0xFF111111;
        }
        return 0;
    }
}
