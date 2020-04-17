package com.brandon3055.draconicevolution.api;

/**
 * Created by brandon3055 on 8/02/19.
 *
 * These are the definitions for the different tech levels in Draconic Evolution.
 */
public enum TechLevel {
    /**
     * Think of draconium as a level above diamond tier.
     */
    DRACONIUM(0),
    /**
     * Wyvern can be thought of as "Nether Star tier"
     * Though that does not necessarily mean all wyvern tier items
     * require nether stars. Take wyvern energy crystals for example.
     */
    WYVERN(1),
    /**
     * AKA Awakened. Pretty self explanatory. Draconic is the tier above wyvern and in most cases
     * draconic tier items should require awakened draconium to craft.
     */
    DRACONIC(2),
    /**
     * Chaotic is the "silly tier" Basically the ultimate end game tier.
     * Obviously all chaotic tier items require chaos shards or fragments to craft.
     */
    CHAOTIC(3);

    public final int index;
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

    TechLevel(int index) {
        this.index = index;
    }

    /**
     * @return the name used in localization / resource names
     */
    public String localName() {
        return name().toLowerCase();
    }
}
