package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElementManager;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiScreen;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiLabel;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.GuiAlign;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.TGuiBase;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 24/12/20
 */
public class WhatsBrokenGui extends ModularGuiScreen {

    protected GuiToolkit<WhatsBrokenGui> toolkit = new GuiToolkit<>(this, GuiToolkit.GuiLayout.EXTRA_WIDE_EXTRA_TALL);

    public WhatsBrokenGui() {
        super(Component.literal("Whats Broken? (Besides this info tablet)"));
    }

    @Override
    public void addElements(GuiElementManager manager) {
        TGuiBase temp = toolkit.loadTemplate(new TGuiBase(this));

        List<String> brokenList = new ArrayList<>();

        brokenList.add(ChatFormatting.BLUE + "Not Yet Implemented");
        brokenList.add("- Dislocators & Portals");
        brokenList.add("- Particle Generator");
        brokenList.add("- Additional Modules:");
        brokenList.add("  - Night Vision");
        brokenList.add("  - Many more planned modules");
        brokenList.add("");
        brokenList.add(ChatFormatting.BLUE + "Work In Progress");
        brokenList.add("- Chaos Guardian - In the process of being re-written, but does work for now.");
        brokenList.add("- Fusion Crafting - Temporarily hacked in so you can craft stuff.  Will be re-written later.");
        brokenList.add("");
        brokenList.add(ChatFormatting.BLUE + "Known Issues");
        brokenList.add("- On a dedicated server, opening creative inventory nukes all data on DE tools and curios.");
        brokenList.add("  This is due to a Forge Capability bug and cannot be fixed, so all data will eventually be moved to NBT.");

        GuiLabel last = null;
        for (String line : brokenList) {
            GuiLabel label = new GuiLabel(line);
            label.setAlignment(GuiAlign.LEFT);
            label.setXSize(xSize() - 10);
            label.setWrap(true);
            label.setYSize(font.wordWrapHeight(line, xSize() - 12));
            label.setXPos(guiLeft() + 5);
            label.setYPos(last == null ? temp.title.maxYPos() + 5 : last.maxYPos() + 3);
            last = label;
            temp.background.addChild(label);
        }
    }
}
