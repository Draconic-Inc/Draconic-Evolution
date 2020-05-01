package com.brandon3055.draconicevolution.api;

import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import static net.minecraft.item.Rarity.*;
import static net.minecraft.util.text.TextFormatting.*;

/**
 * Created by brandon3055 on 8/02/19.
 * <p>
 * These are the definitions for the different tech levels in Draconic Evolution.
 * Also to make this a little less confusing i will be switching up the core names a little in 1.14+
 * They are now
 * Tier 0: Draconium Core
 * Tier 1: Wyvern Core
 * Tier 2: Draconic Core
 * Tier 3: Chaotic Core
 */
public enum TechLevel {
    /**
     * Basic / Draconium level.
     */
    DRACONIUM(0, WHITE, COMMON),
    /**
     * Wyvern can be thought of as "Nether Star tier"
     * Though that does not necessarily mean all wyvern tier items
     * require nether stars. Take wyvern energy crystals for example.
     */
    WYVERN(1, BLUE, UNCOMMON),
    /**
     * AKA Awakened. Pretty self explanatory. Draconic is the tier above wyvern and in most cases
     * draconic tier items should require awakened draconium to craft.
     */
    DRACONIC(2, GOLD, RARE),
    /**
     * Chaotic is the "silly tier" Basically the ultimate end game tier.
     * Obviously all chaotic tier items require chaos shards or fragments to craft.
     */
    CHAOTIC(3, DARK_PURPLE, EPIC);

    public final int index;
    private final TextFormatting textColour;
    private Rarity rarity;
    public static final TechLevel[] VALUES = new TechLevel[4];
    public static final TechLevel[] TOOL_LEVELS = new TechLevel[3];

    static {
        for (TechLevel tier : values()) {
            VALUES[tier.index] = tier;
            if (tier != DRACONIUM) {
                TOOL_LEVELS[tier.index - 1] = tier;
            }
        }
    }

    TechLevel(int index, TextFormatting colour, Rarity rarity) {
        this.index = index;
        this.textColour = colour;
        this.rarity = rarity;
    }

    public TextFormatting getTextColour() {
        return textColour;
    }

    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("tech_level.de." + name().toLowerCase() + ".name");
    }

    public Rarity getRarity() {
        return rarity;
    }
}
