package com.brandon3055.draconicevolution.client.gui.modular;

import com.brandon3055.brandonscore.client.BCSprites;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElement;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElementManager;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiButton;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiTexture;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.TGuiBase;
import com.brandon3055.brandonscore.client.utils.GuiHelper;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleGrid;
import com.brandon3055.draconicevolution.client.gui.ModuleGridRenderer;
import com.brandon3055.draconicevolution.inventory.ContainerModularItem;
import com.brandon3055.draconicevolution.client.DETextures;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.brandon3055.brandonscore.BCConfig.darkMode;


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
//        this.toolkit = new GuiToolkit<>(this, 300, 300);
    }

    @Override
    public void addElements(GuiElementManager manager) {
        TGuiBase template = new TGuiBase(this);
        //Custom background must be set before template is loaded.
        template.background = GuiTexture.newDynamicTexture(xSize(), ySize(), () -> BCSprites.getThemed("background_dynamic"));
        template.background.onReload(guiTex -> guiTex.setPos(guiLeft(), guiTop()));
        toolkit.loadTemplate(template);
        template.title.setInsets(0, 14, 0, 12);
        template.addPlayerSlots();
        infoPanel = template.infoPanel;

        gridRenderer = new ModuleGridRenderer(container.getGrid(), playerInventory);
        gridRenderer.setYPos(template.title.maxYPos() + 3);
        toolkit.centerX(gridRenderer, template.background, 0);
        template.background.addChild(gridRenderer);
        grid.setPosition(gridRenderer.xPos() - guiLeft(), gridRenderer.yPos() - guiTop());
        grid.setOnGridChange(this::updateInfoPanel);

        GuiButton itemConfig = toolkit.createThemedIconButton(template.background, "item_config");
        itemConfig.onReload(e -> e.setRelPos(template.background, 3, 3));
        itemConfig.setHoverText(I18n.format("gui.draconicevolution.modular_item.open_item_config.info"));
        itemConfig.onPressed(DraconicNetwork::sendOpenItemConfig);

        updateInfoPanel();
    }

    private void updateInfoPanel() {
        infoPanel.clear();
        Map<ITextComponent, ITextComponent> nameStatMap = new HashMap<>();
        grid.getModuleHost().addInformation(nameStatMap);
        for (ITextComponent name : nameStatMap.keySet()) {
            infoPanel.addLabeledValue(TextFormatting.GOLD + name.getFormattedText(), 6, 10, () -> TextFormatting.GRAY + nameStatMap.get(name).getFormattedText(), true);
        }
        infoPanel.setEnabled(!nameStatMap.isEmpty());
        reloadGui();
    }

    @Override
    public void drawItemStack(ItemStack stack, int x, int y, String altText) {
        if (gridRenderer.renderStackOverride(stack, x, y, altText)) {
            return;
        }
        super.drawItemStack(stack, x, y, altText);
    }

    @Override
    protected void drawSlotOverlay(Slot slot, boolean occluded) {
        if (slot.getHasStack() && slot.getStack().getCapability(DECapabilities.MODULE_HOST_CAPABILITY).isPresent()) {
            int y = slot.yPos;
            int x = slot.xPos;
            int light = 0xFFfbe555;
            int dark = 0xFFf45905;

            IRenderTypeBuffer.Impl getter = minecraft.getRenderTypeBuffers().getBufferSource();
            GuiHelper.drawShadedRect(getter.getBuffer(GuiHelper.TRANS_TYPE), x - 1, y - 1, 18, 18, 1, 0, dark, light, GuiElement.midColour(light, dark), 0);
            getter.finish();

            if (slot.getStack() == container.hostStack) {
                RenderSystem.disableLighting();
                RenderSystem.disableDepthTest();
                fill(x, y, x + 16, y + 16, 0x80FF0000);
                RenderSystem.enableLighting();
                RenderSystem.enableDepthTest();
            }
        }
    }
}
