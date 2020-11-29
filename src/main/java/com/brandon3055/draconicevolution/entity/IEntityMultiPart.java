package com.brandon3055.draconicevolution.entity;

import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

@Deprecated
public interface IEntityMultiPart {
    World getWorld();

    boolean attackEntityFromPart(EntityDragonOld.MultiPartEntityPart dragonPart, DamageSource source, float damage);
}