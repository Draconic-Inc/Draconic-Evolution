package com.brandon3055.draconicevolution.datagen;

import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.block.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import net.minecraft.loot.*;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.MatchTool;
import net.minecraft.loot.functions.ApplyBonus;
import net.minecraft.loot.functions.LimitCount;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;


import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by brandon3055 on 10/3/20.
 */
public class LootTableGenerator extends LootTableProvider {

    private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> lootTables = ImmutableList.of(Pair.of(BlockLootTables::new, LootParameterSets.BLOCK));

    public LootTableGenerator(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }
    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationtracker) {
        map.forEach((p_218436_2_, p_218436_3_) -> LootTableManager.validate(validationtracker, p_218436_2_, p_218436_3_));
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
        return lootTables;
    }

    public static class BlockLootTables extends net.minecraft.data.loot.BlockLootTables {

        protected void addTables() {
            dropSelf(DEContent.generator);
            dropSelf(DEContent.grinder);
//            registerDropSelfLootTable(DEContent.disenchanter);
            dropSelf(DEContent.energy_transfuser);
            dropSelf(DEContent.dislocator_pedestal);
            dropSelf(DEContent.dislocator_receptacle);
            dropSelf(DEContent.creative_op_capacitor);
//            registerDropSelfLootTable(DEContent.entity_detector);
//            registerDropSelfLootTable(DEContent.entity_detector_advanced);
            dropSelf(DEContent.stabilized_spawner);
            dropSelf(DEContent.potentiometer);
//            registerDropSelfLootTable(DEContent.celestial_manipulator);
            dropSelf(DEContent.draconium_chest);
            dropSelf(DEContent.particle_generator);
            dropSelf(DEContent.crafting_injector_basic);
            dropSelf(DEContent.crafting_injector_wyvern);
            dropSelf(DEContent.crafting_injector_awakened);
            dropSelf(DEContent.crafting_injector_chaotic);
            dropSelf(DEContent.crafting_core);
            dropSelf(DEContent.energy_core);
            dropSelf(DEContent.energy_core_stabilizer);
            dropSelf(DEContent.energy_pylon);
            dropSelf(DEContent.reactor_core);
            dropSelf(DEContent.reactor_stabilizer);
            dropSelf(DEContent.reactor_injector);
//            registerDropSelfLootTable(DEContent.rain_sensor);
            dropSelf(DEContent.dislocation_inhibitor);
            dropSelf(DEContent.block_draconium);
            dropSelf(DEContent.block_draconium_awakened);
            dropSelf(DEContent.fluid_gate);
            dropSelf(DEContent.flux_gate);
            dropSelf(DEContent.infused_obsidian);

            dropSelf(DEContent.crystal_io_basic);
            dropSelf(DEContent.crystal_io_wyvern);
            dropSelf(DEContent.crystal_io_draconic);
            dropSelf(DEContent.crystal_relay_basic);
            dropSelf(DEContent.crystal_relay_wyvern);
            dropSelf(DEContent.crystal_relay_draconic);
            dropSelf(DEContent.crystal_wireless_basic);
            dropSelf(DEContent.crystal_wireless_wyvern);
            dropSelf(DEContent.crystal_wireless_draconic);



            //Special Stuff
//            registerDropSelfLootTable(DEContent.energy_core_structure);
//            registerDropSelfLootTable(DEContent.placed_item);
            add(DEContent.chaos_crystal, block -> createSingleItemTable(DEContent.chaos_shard).apply(SetCount.setCount(ConstantRange.exactly(5))));
            add(DEContent.chaos_crystal_part, noDrop());
//            add(DEContent.portal, noDrop());

            //Fortune
            add(DEContent.ore_draconium_overworld, (block) -> createSilkTouchDispatchTable(block, applyExplosionDecay(block, ItemLootEntry.lootTableItem(DEContent.dust_draconium).apply(SetCount.setCount(RandomValueRange.between(2.0F, 4.0F))).apply(ApplyBonus.addUniformBonusCount(Enchantments.BLOCK_FORTUNE)))));
            add(DEContent.ore_draconium_nether, (block) -> createSilkTouchDispatchTable(block, applyExplosionDecay(block, ItemLootEntry.lootTableItem(DEContent.dust_draconium).apply(SetCount.setCount(RandomValueRange.between(2.0F, 4.0F))).apply(ApplyBonus.addUniformBonusCount(Enchantments.BLOCK_FORTUNE)))));
            add(DEContent.ore_draconium_end, (block) -> createSilkTouchDispatchTable(block, applyExplosionDecay(block, ItemLootEntry.lootTableItem(DEContent.dust_draconium).apply(SetCount.setCount(RandomValueRange.between(2.0F, 4.0F))).apply(ApplyBonus.addUniformBonusCount(Enchantments.BLOCK_FORTUNE)))));


        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return Registry.BLOCK.stream().filter(block -> Objects.requireNonNull(block.getRegistryName()).getNamespace().equals(DraconicEvolution.MODID)).collect(Collectors.toList());
        }

    }
}
