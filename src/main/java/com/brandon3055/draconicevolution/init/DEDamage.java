package com.brandon3055.draconicevolution.init;

import com.brandon3055.brandonscore.api.TechLevel;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by brandon3055 on 13/01/2024
 */
public class DEDamage {
    private static Map<ResourceKey<DamageType>, DamageSource> SOURCES = new HashMap<>();

    public static ResourceKey<DamageType> FUSION_EXPLOSION = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MODID, "fusion_explosion"));
//    public static ResourceKey<DamageType> ADMIN_KILL = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MODID, "admin_kill"));
    public static ResourceKey<DamageType> KILL = DamageTypes.GENERIC_KILL;
    public static ResourceKey<DamageType> CRYSTAL_MOVE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MODID, "crystal_move"));
    public static ResourceKey<DamageType> CHAOS_IMPLOSION = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MODID, "chaos_implosion"));
    public static ResourceKey<DamageType> GUARDIAN = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MODID, "guardian"));
    public static ResourceKey<DamageType> GUARDIAN_PROJECTILE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MODID, "guardian_projectile"));
    public static ResourceKey<DamageType> GUARDIAN_LASER = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MODID, "guardian_laser"));

    public static ResourceKey<DamageType> DRACONIUM_ARROW = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MODID, "draconium_arrow"));
    public static ResourceKey<DamageType> DRACONIUM_ARROW_SPOOF = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MODID, "draconium_arrow_spoof"));
    public static ResourceKey<DamageType> WYVERN_ARROW = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MODID, "wyvern_arrow"));
    public static ResourceKey<DamageType> WYVERN_ARROW_SPOOF = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MODID, "wyvern_arrow_spoof"));
    public static ResourceKey<DamageType> DRACONIC_ARROW = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MODID, "draconic_arrow"));
    public static ResourceKey<DamageType> DRACONIC_ARROW_SPOOF = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MODID, "draconic_arrow_spoof"));
    public static ResourceKey<DamageType> CHAOTIC_ARROW = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MODID, "chaotic_arrow"));
    public static ResourceKey<DamageType> CHAOTIC_ARROW_SPOOF = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MODID, "chaotic_arrow_spoof"));


    public static DamageSource fusionDamage(Level level) {
        return getSource(level, FUSION_EXPLOSION);
    }

    public static DamageSource killDamage(Level level) {
        return getSource(level, DamageTypes.GENERIC_KILL);
    }

    public static DamageSource crystalMove(Level level) {
        return getSource(level, CRYSTAL_MOVE);
    }

    public static DamageSource chaosImplosion(Level level) {
        return getSource(level, CHAOS_IMPLOSION);
    }

    public static DamageSource guardianProjectile(Level level, @Nullable Entity projectile, @Nullable Entity owner) {
        return getSource(level, GUARDIAN_PROJECTILE, projectile, owner);
    }

    public static DamageSource guardianLaser(Level level, @Nullable Entity attacker) {
        return getSource(level, GUARDIAN_LASER, attacker);
    }

    public static DamageSource guardian(Level level, @Nullable Entity attacker) {
        return getSource(level, GUARDIAN, attacker);
    }

    public static DamageSource draconicArrow(Level level, @Nullable Entity projectile, @Nullable Entity owner, TechLevel techLevel, boolean bypassImmune) {
        ResourceKey<DamageType> key = switch (techLevel) {
            case DRACONIUM -> bypassImmune ? DRACONIUM_ARROW_SPOOF : DRACONIUM_ARROW;
            case WYVERN -> bypassImmune ? WYVERN_ARROW_SPOOF : WYVERN_ARROW;
            case DRACONIC -> bypassImmune ? DRACONIC_ARROW_SPOOF : DRACONIC_ARROW;
            case CHAOTIC -> bypassImmune ? CHAOTIC_ARROW_SPOOF : CHAOTIC_ARROW;
        };
        return getSource(level, key, projectile, owner);
    }

    private static DamageSource getSource(Level level, ResourceKey<DamageType> type, @Nullable Entity attacker) {
        return SOURCES.computeIfAbsent(type, e -> new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(e), attacker));
    }

    private static DamageSource getSource(Level level, ResourceKey<DamageType> type, @Nullable Entity projectile, @Nullable Entity owner) {
        return SOURCES.computeIfAbsent(type, e -> new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(e), projectile, owner));
    }

    private static DamageSource getSource(Level level, ResourceKey<DamageType> type) {
        return SOURCES.computeIfAbsent(type, e -> new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(e)));
    }

    public static class Tags {
        public static final TagKey<DamageType> CHAOTIC = TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MODID, "chaotic"));
        public static final TagKey<DamageType> DRACONIC = TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MODID, "draconic"));
        public static final TagKey<DamageType> WYVERN = TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MODID, "wyvern"));
        public static final TagKey<DamageType> DRACONIUM = TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MODID, "draconium"));

        public static final TagKey<DamageType> PROJECTILE_ANTI_DODGE = TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MODID, "proj_anti_dodge"));
    }
}
