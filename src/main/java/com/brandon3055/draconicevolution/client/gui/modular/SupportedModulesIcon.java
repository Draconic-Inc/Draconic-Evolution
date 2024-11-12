package com.brandon3055.draconicevolution.client.gui.modular;

import codechicken.lib.gui.modular.elements.*;
import codechicken.lib.gui.modular.lib.Constraints;
import codechicken.lib.gui.modular.lib.geometry.Align;
import codechicken.lib.gui.modular.lib.geometry.ConstrainedGeometry;
import codechicken.lib.gui.modular.lib.geometry.GuiParent;
import com.brandon3055.brandonscore.api.TimeKeeper;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleRegistry;
import com.brandon3055.draconicevolution.api.modules.ModuleType;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static codechicken.lib.gui.modular.lib.geometry.Constraint.*;
import static codechicken.lib.gui.modular.lib.geometry.GeoParam.*;
import static net.minecraft.ChatFormatting.GOLD;
import static net.minecraft.ChatFormatting.UNDERLINE;

/**
 * Created by brandon3055 on 31/12/2022
 */
public class SupportedModulesIcon extends GuiElement<SupportedModulesIcon> {

    private Map<ModuleType<?>, List<Module<?>>> supported = new HashMap<>();
    private Map<Module<?>, ItemStack> moduleStacks = new HashMap<>();

    private GuiItemStack cyclingIcon;
    private GuiElement<?> hoverElement;
    private boolean locked = false;
    private int page = Integer.MIN_VALUE;
    private int pages = 0;
    private double xPos = 0;
    private double yPos = 0;

    public SupportedModulesIcon(GuiParent<?> parent, ModuleHost host) {
        super(parent);
        for (Module<?> module : ModuleRegistry.getRegistry()) {
            if (host.getHostTechLevel().index >= module.getModuleTechLevel().index && host.isModuleSupported(module.createEntity())) {
                supported.computeIfAbsent(module.getType(), e -> new ArrayList<>()).add(module);
                moduleStacks.put(module, new ItemStack(module.getItem()));
            }
        }
        addElements();
    }

    public void addElements() {
        cyclingIcon = new GuiItemStack(this)
                .enableStackToolTip(false)
                .setStack(() -> supported.isEmpty() ? ItemStack.EMPTY : ImmutableList.copyOf(moduleStacks.values()).get((TimeKeeper.interval(20, moduleStacks.values().size()))));
        Constraints.bind(cyclingIcon, this);

        GuiRectangle hoverBackground = GuiRectangle.toolTipBackground(this)
                .setOpaque(true)
                .jeiExclude()
                .setEnabled(() -> cyclingIcon.isMouseOver() || locked);

        GuiText title = new GuiText(hoverBackground, Component.translatable("gui.draconicevolution.modular_item.supported_modules").withStyle(GOLD).withStyle(UNDERLINE))
                .setAlignment(Align.LEFT)
                .setScroll(false)
                .constrain(WIDTH, literal(12))
                .constrain(HEIGHT, literal(8));
        Constraints.placeInside(title, hoverBackground, Constraints.LayoutPos.TOP_LEFT, 4, 3);

        GuiButton right = GuiButton.flatColourButton(hoverBackground, () -> Component.literal(">"), e -> e ? 0x60909090 : 0)
                .onPress(() -> cyclePage(1));
        right.getLabel().setScroll(false);
        Constraints.size(right, 8, 8);
        Constraints.placeInside(right, hoverBackground, Constraints.LayoutPos.TOP_RIGHT, -3, 3);

        GuiButton left = GuiButton.flatColourButton(hoverBackground, () -> Component.literal("<"), e -> e ? 0x60909090 : 0)
                .onPress(() -> cyclePage(-1));
        left.getLabel().setScroll(false);
        Constraints.size(left, 8, 8);
        Constraints.placeInside(left, hoverBackground, Constraints.LayoutPos.TOP_RIGHT, -13, 3);

        int itemOffset = 14;

        int iconSize = 18;
        List<GuiElement<?>> displayElements = new ArrayList<>();
        int index = 0;
        int buildPage = 0;

        for (ModuleType<?> type : supported.keySet()) {
            List<Module<?>> modules = supported.get(type);
            for (Module<?> module : modules) {
                if (index == 0) pages++;
                ItemStack stack = moduleStacks.get(module);
                int containerWidth = Math.max(minWidth(stack.getDisplayName()), 120) + iconSize + 2;

                int finalBuildPage = buildPage;
                GuiElement<?> container = new GuiElement<>(hoverBackground)
                        .setEnabled(() -> getPage() == finalBuildPage)
                        .constrain(TOP, relative(hoverBackground.get(TOP), itemOffset + (index * (iconSize + 1D))))
                        .constrain(LEFT, relative(hoverBackground.get(LEFT), 3))
                        .constrain(WIDTH, literal(containerWidth))
                        .constrain(HEIGHT, literal(iconSize));

                GuiItemStack icon = new GuiItemStack(container)
                        .setStack(() -> stack);
                Constraints.size(icon, iconSize, iconSize);
                Constraints.placeInside(icon, container, Constraints.LayoutPos.TOP_LEFT);

                GuiText label = new GuiText(container)
                        .constrain(LEFT, relative(icon.get(RIGHT), 2))
                        .constrain(TOP, match(icon.get(TOP)))
                        .constrain(RIGHT, match(container.get(RIGHT)))
                        .constrain(HEIGHT, literal(iconSize))

                        .setWrap(true)
                        .setAlignment(Align.LEFT)
                        .setTextSupplier(() -> stack.getHoverName().copy().withStyle(stack.getRarity().getStyleModifier()));
                displayElements.add(container);

                index++;
                if (index == 8) {
                    index = 0;
                    buildPage++;
                }
            }
        }

        hoverBackground.constrain(TOP, dynamic(() -> yPos));
        hoverBackground.constrain(RIGHT, dynamic(() -> xPos));

        hoverBackground.constrain(WIDTH, dynamic(() -> displayElements.stream()
                .filter(GuiElement::isEnabled)
                .mapToDouble(ConstrainedGeometry::xSize)
                .max()
                .orElse(10) + 6));

        hoverBackground.constrain(HEIGHT, dynamic(() -> displayElements.stream()
                .filter(GuiElement::isEnabled)
                .mapToDouble(ConstrainedGeometry::ySize)
                .sum() + itemOffset + 10));
    }

    private int minWidth(Component component) {
        int w = font().width(component) / 2;
        while (font().getSplitter().splitLines(component, w, component.getStyle()).size() > 2) {
            w++;
        }
        return w;
    }

    private int getPage() {
        return page == Integer.MIN_VALUE ? (TimeKeeper.getClientTick() / 60) % pages : page;
    }

    private void cyclePage(int dir) {
        page += dir;
        if (page < 0) page = pages - 1;
        if (page == pages) page = 0;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver() && !locked) {
            locked = true;
            mc().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1F));
        } else if (locked) {
            locked = false;
            page = Integer.MIN_VALUE;
            mc().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1F));
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void tick(double mouseX, double mouseY) {
        if (!locked) {
            xPos = mouseX;
            yPos = mouseY;
        }
        super.tick(mouseX, mouseY);
    }
}
