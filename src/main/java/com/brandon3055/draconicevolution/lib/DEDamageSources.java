package com.brandon3055.draconicevolution.lib;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;

/**
 * Created by brandon3055 on 5/07/2016.
 */
@Deprecated
public class DEDamageSources {

    public static final DamageSource CHAOS_ISLAND_IMPLOSION = new DamageSource("de.islandImplode").bypassArmor().bypassMagic();

    public static class DamageSourceChaos extends EntityDamageSource {
        public DamageSourceChaos(Entity entity) {
            super("chaos", entity);
            this.bypassArmor();
        }

        @Override
        public boolean isBypassArmor() {
            return true;
        }
    }
}
