package com.brandon3055.draconicevolution.api;

import com.brandon3055.brandonscore.api.TechLevel;

/**
 * Created by brandon3055 on 18/7/21
 * Can be implemented on any Melee weapon.
 */
public interface IDraconicMelee {

    /**
     * Chaotic damage source can break though the shields on chaos guardian crystals.
     *
     * @return The tech level for this damage source.
     */
    TechLevel getTechLevel();
}
