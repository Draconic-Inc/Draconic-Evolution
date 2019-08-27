package com.brandon3055.draconicevolution.api;

import com.brandon3055.brandonscore.api.power.PowerTier;

/**
 * Created by brandon3055 on 8/02/19.
 */
//TODO think of a better name for this
public enum TechLevel {
    DRACONIUM(0),
    WYVERN(1),
    DRACONIC(2),
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

    public PowerTier powerTier() {
        return PowerTier.VALUES[index];
    }

    /**
     * @return the name used in localization / resource names
     */
    public String localName() {
        return name().toLowerCase();
    }
}
