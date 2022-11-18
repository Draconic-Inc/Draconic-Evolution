package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.brandonscore.BCConfig;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.api.render.GuiHelper;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.GuiToolkit.Palette;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElement;
import com.brandon3055.brandonscore.client.gui.modulargui.ThemedElements;
import com.brandon3055.brandonscore.client.render.RenderUtils;
import com.brandon3055.brandonscore.client.utils.GuiHelperOld;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.lib.InstallResult;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleGrid;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleItem;
import com.brandon3055.draconicevolution.client.ClientProxy;
import com.brandon3055.draconicevolution.client.ModuleSpriteUploader;
import com.brandon3055.draconicevolution.client.render.item.ToolRenderBase;
import com.brandon3055.draconicevolution.init.ClientInit;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.Collections;
import java.util.List;

/**
 * Created by brandon3055 on 26/4/20.
 */
public class ModuleGridRenderer extends GuiElement<ModuleGridRenderer> {
    //Does this work?
    private static final RenderType moduleType = RenderType.create("module_type", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, RenderType.CompositeState.builder()
            .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionTexShader))
            .setTextureState(new RenderStateShard.TextureStateShard(ModuleSpriteUploader.LOCATION_MODULE_TEXTURE, false, false))
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .createCompositeState(false)
    );

    private ModuleGrid grid;
    private Inventory player;
    private boolean doubleClick;
    private long lastClickTime;
    private int lastClickButton;
    private boolean canDrop = false;
    private ModuleGrid.GridPos lastClickPos;
    private Component lastError = null;
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
        MultiBufferSource.BufferSource getter = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
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
                renderCell(getter, xPos, yPos, s, x, y, mouseX, mouseY, GuiHelperOld.isInRect(xPos, yPos, s, s, mouseX, mouseY), partialTicks);
            }
        }
        getter.endBatch();
    }

    @Override
    public boolean renderOverlayLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        if (isMouseOver(mouseX, mouseY)) {
            if (player.player.containerMenu.getCarried().isEmpty()) {
                renderCellOverlay(mouseX, mouseY);
            } else if (lastError != null) {
                PoseStack poseStack = new PoseStack();
                poseStack.translate(0, 0, getRenderZLevel());
                renderToolTipStrings(poseStack, Collections.singletonList(lastError.getString()), mouseX, mouseY);
            }
        }
        return super.renderOverlayLayer(minecraft, mouseX, mouseY, partialTicks);
    }

    public void renderCell(MultiBufferSource getter, int x, int y, int size, int cellX, int cellY, double mouseX, double mouseY, boolean mouseOver, float partialTicks) {
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
            entity.renderSlotOverlay(getter, mc, xPos() + (entity.getGridX() * cs), yPos() + (entity.getGridY() * cs), cell.getEntity().getWidth() * cs, cell.getEntity().getHeight() * cs, mouseX, mouseY, mouseOver, partialTicks);
        } else {
            drawColouredRect(getter, x + 1, y + 1, size - 2, size - 2, BCConfig.darkMode ? 0xFF808080 : 0xFF505050);
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
            List<Component> list = getTooltipFromItem(stack);
            PoseStack poseStack = new PoseStack();
            poseStack.translate(0, 0, getRenderZLevel());
            renderTooltip(poseStack, list, mouseX, mouseY);
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
                MultiBufferSource.BufferSource getter = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                drawModule(getter, x - (mw / 2), y - (mh / 2), module);
                getter.endBatch();
                if (stack.getCount() > 1 || altText != null) {
                    zOffset += 250;
                    Font font = fontRenderer;
                    String s = altText == null ? String.valueOf(stack.getCount()) : altText;
                    font.drawShadow(new PoseStack(), s, (float) (x - font.width(s)) + (mw / 2F) - 1, (float) (y - font.lineHeight) + (mh / 2F), 0xffffff);
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
            InputConstants.Key mouseKey = InputConstants.Type.MOUSE.getOrCreate(button);
            boolean pickBlock = mc.options.keyPickItem.isActiveAndMatches(mouseKey);
            ModuleGrid.GridPos cell = getCellAtPos(mouseX, mouseY, true);
            long i = Util.getMillis();
            //Double click pickup wont track the cell you click
            doubleClick = i - lastClickTime < 250L && lastClickButton == button && getCellAtPos(mouseX, mouseY, false).equals(lastClickPos);

            if ((button == 0 || pickBlock) && cell.isValidCell()) {
                if (player.player.containerMenu.getCarried().isEmpty()) {
                    if (pickBlock) {
                        handleGridClick(cell, button, ClickType.CLONE); //Creative Clone
                    } else {
                        boolean shiftClick = (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344));
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

    private void drawModule(MultiBufferSource getter, int x, int y, Module<?> module) {
        int cs = grid.getCellSize();
        int mw = module.getProperties().getWidth() * cs;
        int mh = module.getProperties().getHeight() * cs;

        int colour = getModuleColour(module);
        drawColouredRect(getter, x, y, mw, mh, colour);
        drawBorderedRect(getter, x, y, mw, mh, 1, 0, mixColours(colour, 0x20202000, true));

        if (module.getProperties().getTechLevel() == TechLevel.CHAOTIC) {
            VertexConsumer builder = getter.getBuffer(RenderType.glint());
            float zLevel = getRenderZLevel();
            builder.vertex(x, y + mh, zLevel).uv(0, ((float) mh / mw) / 64F).endVertex();
            builder.vertex(x + mw, y + mh, zLevel).uv(((float) mw / mh) / 64F, ((float) mh / mw) / 64F).endVertex();
            builder.vertex(x + mw, y, zLevel).uv(((float) mw / mh) / 64F, 0).endVertex();
            builder.vertex(x, y, zLevel).uv(0, 0).endVertex();
            RenderUtils.endBatch(getter);
        }

        TextureAtlasSprite sprite = ClientInit.moduleSpriteUploader.getSprite(module);
        float ar = (float) sprite.getWidth() / (float) sprite.getHeight();
        float iar = (float) sprite.getHeight() / (float) sprite.getWidth();

        VertexConsumer builder = getter.getBuffer(moduleType);
        if (iar * mw <= mh) { //Fit Width
            double height = mw * iar;
            bufferSprite(builder, sprite, x, y + (mh / 2D) - (height / 2D), mw, height);
        } else { //Fit height
            double width = mh * ar;
            bufferSprite(builder, sprite, x + (mw / 2D) - (width / 2D), y, width, mh);
        }
    }

    private void bufferSprite(VertexConsumer builder, TextureAtlasSprite sprite, double x, double y, double width, double height) {
        //@formatter:off
        builder.vertex(x,         y + height, zOffset).uv(sprite.getU0(), sprite.getV1()).endVertex();
        builder.vertex(x + width, y + height, zOffset).uv(sprite.getU1(), sprite.getV1()).endVertex();
        builder.vertex(x + width, y,          zOffset).uv(sprite.getU1(), sprite.getV0()).endVertex();
        builder.vertex(x,         y,          zOffset).uv(sprite.getU0(), sprite.getV0()).endVertex();
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
