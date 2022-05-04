package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElementManager;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiScreen;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiLabel;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.GuiAlign;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.TGuiBase;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 24/12/20
 */
public class WhatsBrokenGui extends ModularGuiScreen {

    protected GuiToolkit<WhatsBrokenGui> toolkit = new GuiToolkit<>(this, GuiToolkit.GuiLayout.EXTRA_WIDE_EXTRA_TALL);

    public WhatsBrokenGui() {
        super(new StringTextComponent("Whats Broken? (Besides this info tablet)"));

    }



    @Override
    public void addElements(GuiElementManager manager) {
        TGuiBase temp = toolkit.loadTemplate(new TGuiBase(this));

        List<String> brokenList = new ArrayList<>();

        brokenList.add(TextFormatting.BLUE + "Not Implemented / WIP");
        brokenList.add("- Dislocators & Portals");
        brokenList.add("- Draconium Chest");
        brokenList.add("- Fusion crafting (Temporarily hacked in so you can craft stuff. Will be re written later)");
        brokenList.add("- Bows");
        brokenList.add("- Staff");
        brokenList.add("- Particle generator");
        brokenList.add("- Disenchanter");
        brokenList.add("- Entity Detectors");
        brokenList.add("- Chaos guardian is in the process of being re written but does work");
        brokenList.add("- Modules: Junk filter, Night vision, Mining stability, (bunch of other modules i have planned)");

        brokenList.add("");
        brokenList.add(TextFormatting.BLUE + "Know Issues");
        brokenList.add("- Opening creative inventory nukes all data on my tools and armor (forge capability bug)");
        brokenList.add("");
        brokenList.add(TextFormatting.BLUE + "See \"known-issues\" channel on discord for up to date list.");

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
