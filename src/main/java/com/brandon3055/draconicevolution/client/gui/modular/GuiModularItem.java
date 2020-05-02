package com.brandon3055.draconicevolution.client.gui.modular;

import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElementManager;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiTexture;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.TGuiBase;
import com.brandon3055.brandonscore.client.utils.GuiHelper;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleGrid;
import com.brandon3055.draconicevolution.client.gui.ModuleGridRenderer;
import com.brandon3055.draconicevolution.init.ModCapabilities;
import com.brandon3055.draconicevolution.inventory.ContainerModularItem;
import com.brandon3055.draconicevolution.client.DETextures;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Created by brandon3055 on 19/4/20.
 */
public class GuiModularItem extends ModularGuiContainer<ContainerModularItem> {

    private ModuleGrid grid;
    private GuiToolkit<GuiModularItem> toolkit;
    private ModuleGridRenderer gridRenderer;
    private GuiToolkit.InfoPanel infoPanel;

    public GuiModularItem(ContainerModularItem container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
        this.grid = container.getGrid();
        int maxGridWidth = 226;
        int maxGridHeight = 145;
        int minXPadding = 30;
        int yPadding = 112;
        int cellSize = Math.min(Math.min(maxGridWidth / grid.getWidth(), maxGridHeight / grid.getHeight()), 16);
        int width = Math.max(GuiToolkit.DEFAULT_WIDTH, (cellSize * grid.getWidth()) + minXPadding);
        int height = yPadding + (cellSize * grid.getHeight());
        grid.setCellSize(cellSize);
        this.toolkit = new GuiToolkit<>(this, width, height);
    }

    @Override
    public void addElements(GuiElementManager manager) {
        TGuiBase template = new TGuiBase(this);
        //Custom background must be set before template is loaded.
        template.background = GuiTexture.newDynamicTexture(xSize(), ySize(), DETextures::getBGDynamic);
        template.background.onReload(guiTex -> guiTex.setPos(guiLeft(), guiTop()));
        toolkit.loadTemplate(template);
        template.addPlayerSlots();
        infoPanel = template.infoPanel;

        gridRenderer = new ModuleGridRenderer(container.getGrid(), playerInventory);
        gridRenderer.setYPos(template.title.maxYPos() + 3);
        toolkit.centerX(gridRenderer, template.background, 0);
        template.background.addChild(gridRenderer);
        grid.setPosition(gridRenderer.xPos() - guiLeft(), gridRenderer.yPos() - guiTop());
        grid.setOnGridChange(this::updateInfoPanel);

        updateInfoPanel();
    }

    private void updateInfoPanel() {
        infoPanel.clear();

        Map<ITextComponent, ITextComponent> nameStatMap = new HashMap<>();
        grid.getModuleHost().getModules().map(Module::getModuleType).distinct().forEach(type -> {
            List<Module<?>> list = grid.getModuleHost().getModules().filter(module -> module.getModuleType() == type).collect(Collectors.toList());
            if (!list.isEmpty()) {
                type.getProperties(list.get(0)).addCombinedStats(list.stream().map(type::getProperties).collect(Collectors.toList()), nameStatMap, grid.getModuleHost());
            }
        });

        for (ITextComponent name : nameStatMap.keySet()) {
            infoPanel.addLabeledValue(TextFormatting.GOLD + name.getFormattedText(), 6, 10, () -> TextFormatting.GRAY + nameStatMap.get(name).getFormattedText(), true);
        }

        infoPanel.setEnabled(!nameStatMap.isEmpty());
    }

    @Override
    public void drawItemStack(ItemStack stack, int x, int y, String altText) {
        if (gridRenderer.renderStackOverride(stack, x, y, altText)) {
            return;
        }
        super.drawItemStack(stack, x, y, altText);
    }

    @Override
    protected void drawSlotOverlay(Slot slot) {
        if (slot.getHasStack() && slot.getStack().getCapability(DECapabilities.MODULE_HOST_CAPABILITY).isPresent()) {
            int x = slot.xPos;
            int y = slot.yPos;
            RenderSystem.disableLighting();
            RenderSystem.disableDepthTest();
            if (slot.getStack() == container.hostStack){
                fill(x - 1, y -1, x + 17, y + 17, 0x80FF0000);
            }
            else {
                GuiHelper.drawBorderedRect(x - 1, y - 1, 18, 18, 1, 0, 0x8000FFFF);
            }
            RenderSystem.enableLighting();
            RenderSystem.enableDepthTest();
        }
    }
}
