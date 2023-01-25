package com.brandon3055.draconicevolution.client.gui.modular;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.api.render.GuiHelper;
import com.brandon3055.brandonscore.client.BCGuiSprites;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.HudConfigGui;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElement;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElementManager;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiButton;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiTexture;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.TGuiBase;
import com.brandon3055.brandonscore.client.render.RenderUtils;
import com.brandon3055.brandonscore.client.utils.GuiHelperOld;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleGrid;
import com.brandon3055.draconicevolution.client.gui.ModuleGridRenderer;
import com.brandon3055.draconicevolution.inventory.ContainerModularItem;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by brandon3055 on 19/4/20.
 */
public class GuiModularItem extends ModularGuiContainer<ContainerModularItem> {

    private static AtomicBoolean infoExpanded = new AtomicBoolean(true);
    private ModuleGrid grid;
    private Inventory playerInv;
    private GuiToolkit<GuiModularItem> toolkit;
    private ModuleGridRenderer gridRenderer;
    private GuiToolkit.InfoPanel infoPanel;

    public GuiModularItem(ContainerModularItem container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
        this.grid = container.getGrid();
        this.playerInv = inv;
        int maxGridWidth = 226;
        int maxGridHeight = 145;
        int minXPadding = 30;
        int yPadding = 112;
        int cellSize = Math.min(Math.min(maxGridWidth / grid.getWidth(), maxGridHeight / grid.getHeight()), 16);
        int width = Math.max((11 * 18) + 6 + 14, (cellSize * grid.getWidth()) + minXPadding);
        int height = yPadding + (cellSize * grid.getHeight());
        grid.setCellSize(cellSize);
        this.toolkit = new GuiToolkit<>(this, width, height);
    }

    @Override
    public void addElements(GuiElementManager manager) {
        TGuiBase template = new TGuiBase(this);
        //Custom background must be set before template is loaded.
        template.background = GuiTexture.newDynamicTexture(xSize(), ySize(), () -> BCGuiSprites.getThemed("background_dynamic"));
        template.background.onReload(guiTex -> guiTex.setPos(guiLeft(), guiTop()));

        SupportedModulesIcon supportedModules = new SupportedModulesIcon(container.getModuleHost())
                .setSize(12, 12);
        template.dynamicButtonPrePosition(e -> e.addLast(supportedModules));
        toolkit.loadTemplate(template);

        template.title.setInsets(0, 14, 0, 12);
        template.addPlayerSlots(true, true, true);
        infoPanel = template.infoPanel;
        infoPanel.setExpandedHolder(infoExpanded);

        gridRenderer = new ModuleGridRenderer(container.getGrid(), playerInv);
        gridRenderer.setYPos(template.title.maxYPos() + 3);
        toolkit.centerX(gridRenderer, template.background, 0);
        template.background.addChild(gridRenderer);
        grid.setPosition(gridRenderer.xPos() - guiLeft(), gridRenderer.yPos() - guiTop());
        grid.setOnGridChange(this::updateInfoPanel);
        setFloatingItemOverride(gridRenderer::renderStackOverride);

        GuiElement<?> equipModSlots = toolkit.createEquipModSlots(template.background, playerInv.player, true, e -> e.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).isPresent());
        equipModSlots.setPos(template.background.xPos() - 28, template.background.yPos());

        GuiButton itemConfig = toolkit.createThemedIconButton(template.background, "item_config");
        itemConfig.onReload(e -> e.setRelPos(template.background, 3, 3));
        itemConfig.setHoverText(I18n.get("gui.draconicevolution.modular_item.open_item_config.info"));
        itemConfig.onPressed(() -> DraconicNetwork.sendOpenItemConfig(false));

        GuiButton hudConfig = toolkit.createIconButton(template.background, 16, 9, 16, 8, BCGuiSprites.themedGetter("hud_button"));
        hudConfig.onReload(e -> e.setPos(itemConfig.maxXPos() + 1, itemConfig.yPos()));
        hudConfig.setHoverText(I18n.get("hud.draconicevolution.open_hud_config"));
        hudConfig.onPressed(() -> minecraft.setScreen(new HudConfigGui()));

        //Need to add this last so it renders on top of everything else.
        template.background.addChild(supportedModules);

        updateInfoPanel();
    }

    private void updateInfoPanel() {
        infoPanel.clear();

        TechLevel techLevel = container.getModuleHost().getHostTechLevel();
        StringBuilder gridName = new StringBuilder();
        gridName.append(grid.getWidth()).append("x").append(grid.getHeight());
        gridName.append(" ");
        gridName.append(techLevel.getDisplayName().plainCopy().withStyle(techLevel.getTextColour()).getString());
        gridName.append(" ");
        gridName.append(I18n.get("gui.draconicevolution.modular_item.module_grid"));
        infoPanel.addDynamicLabel(gridName::toString, 12);

        Map<Component, Component> nameStatMap = new LinkedHashMap<>();
        grid.getModuleHost().addInformation(nameStatMap, container.getModuleContext());
        for (Component name : nameStatMap.keySet()) {
            infoPanel.addLabeledValue(ChatFormatting.GOLD + name.getString(), 6, 10, () -> ChatFormatting.GRAY + nameStatMap.get(name).getString(), true);
        }

        reloadGui();
    }

    @Override
    protected void drawSlotOverlay(Slot slot, boolean occluded) {
        if (slot.hasItem() && slot.getItem().getCapability(DECapabilities.MODULE_HOST_CAPABILITY).isPresent()) {
            int y = slot.y;
            int x = slot.x;
            int light = 0xFFfbe555;
            int dark = 0xFFf45905;

            MultiBufferSource.BufferSource getter = RenderUtils.getGuiBuffers();
            GuiHelperOld.drawShadedRect(getter.getBuffer(GuiHelper.transColourType), x - 1, y - 1, 18, 18, 1, 0, dark, light, GuiElement.midColour(light, dark), 0);

            if (slot.getItem() == container.hostStack) {
                GuiHelperOld.drawBorderedRect(getter.getBuffer(GuiHelper.transColourType), x, y, 16, 16, 1, 0x50FF0000, 0xFFFF0000, 0);
            }
            getter.endBatch();
        }
    }
}
