package com.brandon3055.draconicevolution.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static com.brandon3055.draconicevolution.init.DEDamage.*;

/**
 * Created by brandon3055 on 13/01/2024
 */
public class DamageTypeGenerator extends DamageTypeTagsProvider {

    public DamageTypeGenerator(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, modId, existingFileHelper);
    }


    @Override
    protected void addTags(HolderLookup.Provider provider) {


        tag(DamageTypeTags.IS_EXPLOSION).add(GUARDIAN_LASER, FUSION_EXPLOSION, /*ADMIN_KILL, */CHAOS_IMPLOSION, GUARDIAN_PROJECTILE);
        tag(DamageTypeTags.BYPASSES_ARMOR).add(GUARDIAN_LASER, FUSION_EXPLOSION, /*ADMIN_KILL, */CHAOS_IMPLOSION, GUARDIAN_PROJECTILE, GUARDIAN, CRYSTAL_MOVE);
        tag(DamageTypeTags.BYPASSES_EFFECTS).add(GUARDIAN_LASER, FUSION_EXPLOSION, /*ADMIN_KILL, */CHAOS_IMPLOSION, GUARDIAN_PROJECTILE, GUARDIAN, CRYSTAL_MOVE);
        tag(DamageTypeTags.BYPASSES_COOLDOWN).add(GUARDIAN_LASER, FUSION_EXPLOSION, /*ADMIN_KILL, */CHAOS_IMPLOSION, GUARDIAN_PROJECTILE, GUARDIAN, CRYSTAL_MOVE);
        tag(DamageTypeTags.BYPASSES_SHIELD).add(GUARDIAN_LASER, FUSION_EXPLOSION, /*ADMIN_KILL, */CHAOS_IMPLOSION, GUARDIAN_PROJECTILE, GUARDIAN, CRYSTAL_MOVE);
        tag(DamageTypeTags.BYPASSES_RESISTANCE).add(GUARDIAN_LASER, FUSION_EXPLOSION, /*ADMIN_KILL, */CHAOS_IMPLOSION, GUARDIAN_PROJECTILE, GUARDIAN, CRYSTAL_MOVE);

        tag(DamageTypeTags.IS_PROJECTILE).add(DRACONIUM_ARROW, WYVERN_ARROW, DRACONIC_ARROW, CHAOTIC_ARROW, GUARDIAN_PROJECTILE);

        tag(Tags.DRACONIUM).add(DRACONIUM_ARROW, DRACONIUM_ARROW_SPOOF);
        tag(Tags.WYVERN).add(WYVERN_ARROW, WYVERN_ARROW_SPOOF);
        tag(Tags.DRACONIC).add(DRACONIC_ARROW, DRACONIC_ARROW_SPOOF);
        tag(Tags.CHAOTIC).add(CHAOTIC_ARROW, CHAOTIC_ARROW_SPOOF, FUSION_EXPLOSION, CHAOS_IMPLOSION, GUARDIAN_PROJECTILE, GUARDIAN, GUARDIAN_LASER);
        tag(Tags.PROJECTILE_ANTI_DODGE).add(DRACONIUM_ARROW_SPOOF, WYVERN_ARROW_SPOOF, DRACONIC_ARROW_SPOOF, CHAOTIC_ARROW_SPOOF);



        tag(DamageTypeTags.BYPASSES_INVULNERABILITY).add(/*ADMIN_KILL, */CRYSTAL_MOVE);
    }
}
