package com.brandon3055.draconicevolution.datagen;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by brandon3055 on 10/3/20.
 */
public class BlockLootProvider extends BlockLootSubProvider {

    protected BlockLootProvider(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {
        dropSelf(DEContent.GENERATOR);
        dropSelf(DEContent.GRINDER);
        dropSelf(DEContent.DISENCHANTER);
        dropSelf(DEContent.ENERGY_TRANSFUSER);
        dropSelf(DEContent.DISLOCATOR_PEDESTAL);
        dropSelf(DEContent.DISLOCATOR_RECEPTACLE);
        dropSelf(DEContent.CREATIVE_OP_CAPACITOR);
        dropSelf(DEContent.ENTITY_DETECTOR);
        dropSelf(DEContent.ENTITY_DETECTOR_ADVANCED);
        dropSelf(DEContent.STABILIZED_SPAWNER);
        dropSelf(DEContent.POTENTIOMETER);
        dropSelf(DEContent.CELESTIAL_MANIPULATOR);
        dropSelf(DEContent.DRACONIUM_CHEST);
        dropSelf(DEContent.PARTICLE_GENERATOR);
        dropSelf(DEContent.BASIC_CRAFTING_INJECTOR);
        dropSelf(DEContent.WYVERN_CRAFTING_INJECTOR);
        dropSelf(DEContent.AWAKENED_CRAFTING_INJECTOR);
        dropSelf(DEContent.CHAOTIC_CRAFTING_INJECTOR);
        dropSelf(DEContent.CRAFTING_CORE);
        dropSelf(DEContent.ENERGY_CORE);
        dropSelf(DEContent.ENERGY_CORE_STABILIZER);
        dropSelf(DEContent.ENERGY_PYLON);
        dropSelf(DEContent.REACTOR_CORE);
        dropSelf(DEContent.REACTOR_STABILIZER);
        dropSelf(DEContent.REACTOR_INJECTOR);
        dropSelf(DEContent.RAIN_SENSOR);
        dropSelf(DEContent.DISLOCATION_INHIBITOR);
        dropSelf(DEContent.DRACONIUM_BLOCK);
        dropSelf(DEContent.AWAKENED_DRACONIUM_BLOCK);
        dropSelf(DEContent.FLUID_GATE);
        dropSelf(DEContent.FLUX_GATE);
        dropSelf(DEContent.INFUSED_OBSIDIAN);

        dropSelf(DEContent.BASIC_IO_CRYSTAL);
        dropSelf(DEContent.WYVERN_IO_CRYSTAL);
        dropSelf(DEContent.DRACONIC_IO_CRYSTAL);
        dropSelf(DEContent.BASIC_RELAY_CRYSTAL);
        dropSelf(DEContent.WYVERN_RELAY_CRYSTAL);
        dropSelf(DEContent.DRACONIC_RELAY_CRYSTAL);
        dropSelf(DEContent.BASIC_WIRELESS_CRYSTAL);
        dropSelf(DEContent.WYVERN_WIRELESS_CRYSTAL);
        dropSelf(DEContent.DRACONIC_WIRELESS_CRYSTAL);

        //Special Stuff
//            dropSelf(DEContent.energy_core_structure);
//            dropSelf(DEContent.placed_item);
//            add(DEContent.chaos_crystal, block -> createSingleItemTable(DEContent.chaos_shard).apply(SetCount.setCount(ConstantRange.exactly(5))));
//            add(DEContent.chaos_crystal_part, noDrop());
//            add(DEContent.portal, noDrop());
        noDrop(DEContent.STRUCTURE_BLOCK);
        noDrop(DEContent.PORTAL);
        noDrop(DEContent.CHAOS_CRYSTAL);
        noDrop(DEContent.CHAOS_CRYSTAL_PART);
        noDrop(DEContent.PLACED_ITEM);
        noDrop(DEContent.COMET_SPAWNER);

        var enchants = registries.lookupOrThrow(Registries.ENCHANTMENT);
        var fortune = enchants.getOrThrow(Enchantments.FORTUNE);

        //Fortune
        add(DEContent.OVERWORLD_DRACONIUM_ORE.get(), (block) -> createSilkTouchDispatchTable(block, applyExplosionDecay(block, LootItem.lootTableItem(DEContent.DUST_DRACONIUM.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 4.0F))).apply(ApplyBonusCount.addUniformBonusCount(fortune)))));
        add(DEContent.DEEPSLATE_DRACONIUM_ORE.get(), (block) -> createSilkTouchDispatchTable(block, applyExplosionDecay(block, LootItem.lootTableItem(DEContent.DUST_DRACONIUM.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 4.0F))).apply(ApplyBonusCount.addUniformBonusCount(fortune)))));
        add(DEContent.NETHER_DRACONIUM_ORE.get(), (block) -> createSilkTouchDispatchTable(block, applyExplosionDecay(block, LootItem.lootTableItem(DEContent.DUST_DRACONIUM.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 4.0F))).apply(ApplyBonusCount.addUniformBonusCount(fortune)))));
        add(DEContent.END_DRACONIUM_ORE.get(), (block) -> createSilkTouchDispatchTable(block, applyExplosionDecay(block, LootItem.lootTableItem(DEContent.DUST_DRACONIUM.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 4.0F))).apply(ApplyBonusCount.addUniformBonusCount(fortune)))));

    }

    protected void dropSelf(Supplier<? extends Block> pBlock) {
        super.dropSelf(pBlock.get());
    }

    protected void noDrop(Supplier<? extends Block> pBlock) {
        add(pBlock.get(), noDrop());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BuiltInRegistries.BLOCK.entrySet().stream().filter(e -> e.getKey().location().getNamespace().equals(DraconicEvolution.MODID)).map(Map.Entry::getValue).collect(Collectors.toList());
    }
}
