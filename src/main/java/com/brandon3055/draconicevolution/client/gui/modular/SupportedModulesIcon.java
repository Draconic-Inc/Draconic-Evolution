package com.brandon3055.draconicevolution.client.gui.modular;

import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.api.TimeKeeper;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElement;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiButton;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiLabel;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiStackIcon;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiTooltipBackground;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.GuiAlign;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleRegistry;
import com.brandon3055.draconicevolution.api.modules.ModuleType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.covers1624.quack.collection.StreamableIterable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;
import static net.minecraft.ChatFormatting.GOLD;
import static net.minecraft.ChatFormatting.UNDERLINE;

/**
 * Created by brandon3055 on 31/12/2022
 */
public class SupportedModulesIcon extends GuiElement<SupportedModulesIcon> {

    private Map<ModuleType<?>, List<Module<?>>> supported = new HashMap<>();
    private Map<Module<?>, ItemStack> moduleStacks = new HashMap<>();

    private GuiStackIcon stackIcon;
    private GuiElement<?> hoverElement;
    private boolean locked = false;
    private int page = Integer.MIN_VALUE;

    public SupportedModulesIcon(ModuleHost host) {
        for (Module<?> module : ModuleRegistry.getRegistry().getValues()) {
            if (host.isModuleSupported(module.createEntity())) {
                supported.computeIfAbsent(module.getType(), e -> new ArrayList<>()).add(module);
                moduleStacks.put(module, new ItemStack(module.getItem()));
            }
        }
    }

    @Override
    public void addChildElements() {
        stackIcon = addChild(new GuiStackIcon())
                .bindPosition(this)
                .bindSize(this, false)
                .setDrawToolTip(false)
                .setStackSupplier(() -> supported.isEmpty() ? ItemStack.EMPTY : ImmutableList.copyOf(moduleStacks.values()).get((TimeKeeper.interval(20, moduleStacks.values().size()))));

        int iconSize = 18;
        List<GuiElement<?>> typeElements = new ArrayList<>();
        for (ModuleType<?> type : supported.keySet()) {
            List<Module<?>> modules = supported.get(type);
            int maxWidth = modules.stream()
                    .map(moduleStacks::get)
                    .map(ItemStack::getDisplayName)
                    .mapToInt(this::minWidth)
                    .max().orElse(10);
            maxWidth = Math.max(maxWidth, 100);

            GuiStackIcon icon = new GuiStackIcon()
                    .setInsets(0, 0, 0, 0)
                    .setSize(iconSize, iconSize)
                    .setStackSupplier(() -> moduleStacks.get(modules.get(Math.floorMod(getPage(), modules.size()))));

            GuiLabel label = new GuiLabel()
                    .setWrap(true)
                    .setSize(maxWidth + 4, iconSize)
                    .setPos(icon.maxXPos() + 2, icon.yPos())
                    .setAlignment(GuiAlign.LEFT)
                    .setComponentSupplier(() -> {
                        ItemStack stack = moduleStacks.get(modules.get(Math.floorMod(getPage(), modules.size())));
                        return stack.getHoverName().copy().withStyle(stack.getRarity().getStyleModifier());
                    });

            GuiElement<?> element = new GuiElement<>();
            element.addChild(icon);
            element.addChild(label);
            element.setBoundsToChildren(1, 0, 1, 1);
            typeElements.add(element);
        }

        int count = typeElements.size();
        int elementWidth = typeElements.stream().mapToInt(GuiElement::xSize).max().orElse(10);
        int cols = 1;
        while ((count / cols) * iconSize > (cols * elementWidth) && cols < count && (cols + 1) * elementWidth < screenWidth) {
            cols++;
        }

        hoverElement = modularGui.getManager().addChild(new GuiTooltipBackground(), 100, false);
        hoverElement.addChild(new GuiLabel(new TranslatableComponent("gui.draconicevolution.modular_item.supported_modules").withStyle(GOLD).withStyle(UNDERLINE)))
                .setAlignment(GuiAlign.LEFT)
                .setSize(12, 8)
                .setTrim(false)
                .setPos(hoverElement.xPos(), hoverElement.yPos() - 10);

        for (int i = 0; i < typeElements.size(); i++) {
            GuiElement<?> element = typeElements.get(i);
            element.setPos((i % cols) * elementWidth, (i / cols) * iconSize);
            hoverElement.addChild(element);
        }

        hoverElement.setBoundsToChildren(4, 4, 4, 4);
        hoverElement.setEnabledCallback(() -> SupportedModulesIcon.this.getHoverTime() > 0 || locked);
        hoverElement.normalizePosition();

        hoverElement.addChild(new GuiButton("<"))
                .setInsets(0, 1, 0, 0)
                .setSize(8, 8)
                .setFillColours(0, 0x60909090)
                .setAlignment(GuiAlign.LEFT)
                .setPos(hoverElement.maxXPos() - 26, hoverElement.yPos() + 4)
                .onPressed(() -> cyclePage(-1));

        hoverElement.addChild(new GuiButton(">"))
                .setInsets(0, 0, 0, 1)
                .setSize(8, 8)
                .setFillColours(0, 0x60909090)
                .setAlignment(GuiAlign.RIGHT)
                .setPos(hoverElement.maxXPos() - 12, hoverElement.yPos() + 4)
                .onPressed(() -> cyclePage(1));

        hoverElement.modifyZOffset(150);
    }

    private int minWidth(Component component) {
        int w = fontRenderer.width(component) / 2;
        while (fontRenderer.getSplitter().splitLines(component, w, component.getStyle()).size() > 2) {
            w++;
        }
        return w;
    }

    private int getPage() {
        return page == Integer.MIN_VALUE ? TimeKeeper.getClientTick() / 60 : page;
    }

    private void cyclePage(int dir) {
        page = page == Integer.MIN_VALUE ? 0 : page + dir;
    }

    @Override
    public void renderElement(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        for (GuiElement<?> element : childElements) {
            if (element.isEnabled()) {
                element.renderElement(minecraft, mouseX, mouseY, partialTicks);
            }
        }

        if (!hoverElement.isEnabled() || locked) return;
        hoverElement.setYPos(mouseY + 4);
        if (mouseX > screenWidth / 2) {
            hoverElement.setMaxXPos(mouseX, false);
        } else {
            hoverElement.setXPos(mouseX);
        }
        hoverElement.normalizePosition();
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY) && !locked) {
            locked = true;
            GuiButton.playGenericClick();
        } else if (locked) {
            locked = false;
            page = Integer.MIN_VALUE;
            GuiButton.playGenericClick();
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
}
