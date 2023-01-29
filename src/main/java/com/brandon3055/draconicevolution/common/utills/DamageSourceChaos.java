package com.brandon3055.draconicevolution.common.utills;

import net.minecraft.entity.Entity;
import net.minecraft.util.EntityDamageSource;

/**
 * Created by Brandon on 1/09/2014.
 */
public class DamageSourceChaos extends EntityDamageSource {

    public DamageSourceChaos(Entity entity) {
        super("Chaos", entity);
        this.setDamageBypassesArmor();
    }

    @Override
    public boolean isUnblockable() {
        return true;
    }
}
