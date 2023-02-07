package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.brandonscore.client.BCGuiSprites;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElement;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElementManager;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiStackIcon;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiTextCompLabel;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiTexture;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.GuiAlign;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.TBasicMachine;
import com.brandon3055.brandonscore.inventory.ContainerSlotLayout;
import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.brandon3055.draconicevolution.api.crafting.IFusionInjector;
import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;
import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore;
import com.brandon3055.draconicevolution.client.gui.modular.TModularMachine;
import com.brandon3055.draconicevolution.inventory.ContainerFusionCraftingCore;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GuiFusionCraftingCore extends ModularGuiContainer<ContainerFusionCraftingCore> {

    private final TileFusionCraftingCore tile;
    private IFusionRecipe currentRecipe = null;

    protected GuiToolkit<GuiFusionCraftingCore> toolkit = new GuiToolkit<>(this, 218/*238*/, /*245*/220).setTranslationPrefix("gui.draconicevolution.fusion_craft");
    public GuiStackIcon stackIcon;

    public GuiFusionCraftingCore(ContainerFusionCraftingCore container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
        this.tile = container.tile;
    }

    @Override
    public void addElements(GuiElementManager manager) {
        TBasicMachine template = new TModularMachine(this, tile, false);
        template.background = GuiTexture.newDynamicTexture(xSize(), ySize(), () -> BCGuiSprites.getThemed("background_dynamic"));
        template.background.onReload((guiTex) -> guiTex.setPos(guiLeft(), guiTop()));
        toolkit.loadTemplate(template);
        template.addPlayerSlots(true, true, true);

        //Setup Craft display area
        GuiElement<?> craftArea = template.background.addChild(new GuiElement<>());
        craftArea.setPos(template.background.xPos() + 17, template.title.maxYPos() + 3);
        craftArea.setMaxPos(template.background.maxXPos() - 17, template.playerSlots.yPos() - 3, true);

        //Input/OOutput Slots
        GuiElement<?> slots = toolkit.createSlots(template.background, 1, 2, 26, (x, y) -> container.getSlotLayout().getSlotData(ContainerSlotLayout.SlotType.TILE_INV, y), null);
        toolkit.center(slots, craftArea, 0, 3);

        //Status Label
        template.background.addChild(new GuiTextCompLabel()
                .setPosAndSize(template.background.xPos() + (template.background.xSize() / 2) - 40, template.title.maxYPos() + 4, 80, 20)
                .setAlignment(GuiAlign.CENTER).setTrim(false).setWrap(true).setShadow(true)
                .setEnabledCallback(() -> tile.userStatus.get() != null)
                .setTextSupplier(tile.userStatus::get));

        //Craft Button
        toolkit.createButton_old("gui.draconicevolution.fusion_craft.craft", template.background)
                .setPosAndSize(width / 2 - 40, template.playerSlots.yPos() - 17, 80, 14)
                .setEnabledCallback(() -> tile.getActiveRecipe() != null && !tile.isCrafting())
                .onButtonReleased((b) -> tile.sendPacketToServer(output -> {}, 0));

        //Result Display
        template.background.addChild(stackIcon = new GuiStackIcon(ItemStack.EMPTY));
        toolkit.center(stackIcon, craftArea, 0, 3);
        template.background.addChild(new IngredRenderer(tile).setPosAndSize(craftArea));
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        currentRecipe = tile.getLevel().getRecipeManager().getRecipeFor(DraconicAPI.FUSION_RECIPE_TYPE, tile, tile.getLevel()).orElse(null);
        if (currentRecipe == null) {
            stackIcon.setStack(ItemStack.EMPTY);
        } else {
            stackIcon.setStack(currentRecipe.getResultItem());
        }
    }

    private static class IngredRenderer extends GuiElement<IngredRenderer> {
        private TileFusionCraftingCore core;
        private List<ItemStack> lastStacks = new ArrayList<>();

        public IngredRenderer(TileFusionCraftingCore core) {
            this.core = core;
        }

        private void setInjectors() {
            int centerX = xPos() + (xSize() / 2);
            ArrayList<ArrayList<ItemStack>> columns = new ArrayList<>();
            int colCount = Math.min((int)Math.ceil(lastStacks.size() / 12D), 3) * 2;
            for (int i = 0; i < colCount; i++) {
                columns.add(new ArrayList<>());
            }
            for (int i = 0; i < lastStacks.size(); i++) {
                columns.get(i % colCount).add(lastStacks.get(i));
            }

            int innerOffset = colCount == 6 ? 44 : colCount == 4 ? 53 : 65;

            for (int column = 0; column < columns.size(); column++) {
                List<ItemStack> stacks = columns.get(column);
                int offset = (column / 2) * 20;
                int xPos = (column % 2 == 0) ? (centerX + innerOffset + offset) : (centerX - innerOffset - offset - 18);
                int yHeight = Math.min(stacks.size() * 20, ySize());
                for (int i = 0; i < stacks.size(); i++) {
                    int yPos = ((yPos() + (ySize() / 2)) - (yHeight / 2)) + (i * (yHeight / stacks.size()));
                    addChild(new GuiStackIcon().setStack(stacks.get(i)).setPos(xPos, yPos));
                }
            }
        }

        @Override
        public boolean onUpdate() {
            List<ItemStack> stacks = core.getInjectors()
                    .stream()
                    .map(IFusionInjector::getInjectorStack)
                    .filter(injectorStack -> !injectorStack.isEmpty())
                    .collect(Collectors.toList());
            if (!stacks.equals(lastStacks)) {
                childElements.clear();
                lastStacks = stacks;
                if (!lastStacks.isEmpty()) {
                    setInjectors();
                }
                return true;
            }
            return super.onUpdate();
        }
    }
}
