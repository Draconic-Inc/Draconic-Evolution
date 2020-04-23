package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.GuiToolkit.GuiLayout;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElement;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElementManager;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiTexture;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.MGuiEffectRenderer;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.TBasicMachine;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.TGuiBase;
import com.brandon3055.draconicevolution.api.modules.IModule;
import com.brandon3055.draconicevolution.api.modules.capability.IModuleProvider;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleGrid;
import com.brandon3055.draconicevolution.init.ModuleCapability;
import com.brandon3055.draconicevolution.inventory.ContainerModularItem;
import com.brandon3055.draconicevolution.utils.DETextures;
import com.brandon3055.draconicevolution.utils.LogHelper;
import jdk.nashorn.internal.objects.NativeJava;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.LazyOptional;

import static com.brandon3055.brandonscore.BCConfig.darkMode;


/**
 * Created by brandon3055 on 19/4/20.
 */
public class GuiModularItem extends ModularGuiContainer<ContainerModularItem> {

    private ModuleGrid grid;
    private GuiToolkit<GuiModularItem> toolkit;
    private ModuleGridRenderer gridRenderer;

//    DEFAULT_WIDTH = 176;
//    WIDE_WIDTH = 200;
//    EXTRA_WIDE_WIDTH = 250;

//    DEFAULT_HEIGHT = 166;
//    TALL_HEIGHT = 200;
//    EXTRA_TALL_HEIGHT = 250;

    public GuiModularItem(ContainerModularItem container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
        this.grid = container.moduleGrid;
        int maxGridWidth = 226;
        int maxGridHeight = 145;
        int minXPadding = 24;
        int yPadding = 110;
        int cellSize = Math.min(Math.min(maxGridWidth / grid.getWidth(), maxGridHeight / grid.getHeight()), 16);
        int width = Math.max(GuiToolkit.DEFAULT_WIDTH, (cellSize * grid.getWidth()) + minXPadding);
        int height = yPadding + (cellSize * grid.getHeight());

//        int xBuffer = 16;
//        int gw = grid.getWidth();
//        int width = 176;
//        if ((width - xBuffer) / gw < 12) {
//            width = 200;
//        }
//        if ((width - xBuffer) / gw < 12) {
//            width = 250;
//        }
//
//        int yBuffer = 107;
//        int gh = grid.getHeight();
//        int height = 166;
//        if ((height - yBuffer) / gh < 12) {
//            height = 200;
//        }
//        if ((height - yBuffer) / gh < 12) {
//            height = 250;
//        }
//
//        int cellSize = Math.min(Math.min((width - xBuffer) / gw, (height - yBuffer) / gh), 16);
//        height = yBuffer + (cellSize * gh);
//        width = xBuffer + (cellSize * gw);

//        grid.setCellSize(cellSize);
        this.toolkit = new GuiToolkit<>(this, width, height);
//        this.toolkit = new GuiToolkit<>(this, GuiLayout.EXTRA_TALL);
//        this.toolkit = new GuiToolkit<>(this, GuiLayout.getBestFit(width, height));
    }

    @Override
    public void addElements(GuiElementManager manager) {
        TGuiBase template = new TGuiBase(this);
        //Custom background must be set before template is loaded.
        template.background = GuiTexture.newDynamicTexture(xSize(), ySize(), DETextures::getBGDynamic);
        template.background.onReload(guiTex -> guiTex.setPos(guiLeft(), guiTop()));
        toolkit.loadTemplate(template);

        template.addPlayerSlots();

        gridRenderer = new ModuleGridRenderer(container.moduleGrid);
        gridRenderer.setYPos(template.title.maxYPos() + ((template.playerSlots.yPos() - template.title.maxYPos()) / 2) - (gridRenderer.ySize() / 2));
//        gridRenderer.setXPos(template.background.xPos() + 8);
        toolkit.centerX(gridRenderer, template.background, 0);
        template.background.addChild(gridRenderer);
        grid.setPosition(gridRenderer.xPos() - guiLeft(), gridRenderer.yPos() - guiTop());

//        template.playerSlots.setXPos(gridRenderer.xPos());
//        container.moduleGrid


//        LogHelper.dev((template.background.maxYPos() - template.playerSlots.yPos()) + (template.title.maxYPos() - template.background.yPos()));
        LogHelper.dev((template.themeButton.xPos() - (guiLeft() + 8)) + ", " + (template.playerSlots.yPos() - template.title.maxYPos()) + " " + (xSize() - (template.themeButton.xPos() - (guiLeft() + 8))));
    }

    @Override
    public void drawItemStack(ItemStack stack, int x, int y, String altText) {
        if (gridRenderer.isMouseOver(x + guiLeft() + 8, y + guiTop() + 8)) {
            LazyOptional<IModuleProvider<?>> cap = stack.getCapability(ModuleCapability.MODULE_CAPABILITY);
            if (cap.isPresent()) {
                IModule<?> module = cap.orElseThrow(RuntimeException::new).getModule();
                int mw = module.getProperties().getWidth() * grid.getCellSize();
                int mh = module.getProperties().getHeight() * grid.getCellSize();
                gridRenderer.drawColouredRect(x + 8 - (mw / 2D), y + 8 - (mh / 2D), mw, mh, 0xFF00FFFF);
                return;
            }
        }


        super.drawItemStack(stack, x, y, altText);


//        drawCenteredString(font, "'This is a stack'", x, y, 0xFFFFFF);
    }

    public static class ModuleGridRenderer extends GuiElement<ModuleGridRenderer> {
        private ModuleGrid grid;

        public ModuleGridRenderer(ModuleGrid grid) {
            this.grid = grid;
            this.setSize(grid.getWidth() * grid.getCellSize(), grid.getHeight() * grid.getCellSize());

        }

        @Override
        public void renderElement(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
            super.renderElement(minecraft, mouseX, mouseY, partialTicks);
            drawBorderedRect(xPos(), yPos(), xSize(), ySize(), 1, 1, 0xFFFF0000);

            int s = grid.getCellSize();
            for (int x = 0; x < grid.getWidth(); x++) {
                for (int y = 0; y < grid.getHeight(); y++) {
                    drawColouredRect(xPos() + (x * s) + 1, yPos() + (y * s) + 1, s - 2, s - 2, 0xFF00FF00);
                }
            }
        }
    }
}
