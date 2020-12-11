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
        map.forEach((p_218436_2_, p_218436_3_) -> LootTableManager.validateLootTable(validationtracker, p_218436_2_, p_218436_3_));
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
        return lootTables;
    }

    public static class BlockLootTables extends net.minecraft.data.loot.BlockLootTables {

        private static final ILootCondition.IBuilder SILK_TOUCH = MatchTool.builder(ItemPredicate.Builder.create().enchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.IntBound.atLeast(1))));
        private static final ILootCondition.IBuilder NO_SILK_TOUCH = SILK_TOUCH.inverted();
        private static final ILootCondition.IBuilder SHEARS = MatchTool.builder(ItemPredicate.Builder.create().item(Items.SHEARS));
        private static final ILootCondition.IBuilder SILK_TOUCH_OR_SHEARS = SHEARS.alternative(SILK_TOUCH);
        private static final ILootCondition.IBuilder NOT_SILK_TOUCH_OR_SHEARS = SILK_TOUCH_OR_SHEARS.inverted();
        private static final Set<Item> IMMUNE_TO_EXPLOSIONS = Collections.EMPTY_SET;//Stream.of(Blocks.DRAGON_EGG).map(IItemProvider::asItem).collect(ImmutableSet.toImmutableSet());

        private final Map<ResourceLocation, LootTable.Builder> lootTables = Maps.newHashMap();

        protected void addTables() {
            registerDropSelfLootTable(DEContent.generator);
            registerDropSelfLootTable(DEContent.grinder);
//            registerDropSelfLootTable(DEContent.disenchanter);
//            registerDropSelfLootTable(DEContent.energy_infuser);
//            registerDropSelfLootTable(DEContent.dislocator_pedestal);
//            registerDropSelfLootTable(DEContent.dislocator_receptacle);
            registerDropSelfLootTable(DEContent.creative_op_capacitor);
//            registerDropSelfLootTable(DEContent.entity_detector);
//            registerDropSelfLootTable(DEContent.entity_detector_advanced);
            registerDropSelfLootTable(DEContent.stabilized_spawner);
//            registerDropSelfLootTable(DEContent.potentiometer);
//            registerDropSelfLootTable(DEContent.celestial_manipulator);
//            registerDropSelfLootTable(DEContent.draconium_chest);
//            registerDropSelfLootTable(DEContent.particle_generator);
            registerDropSelfLootTable(DEContent.crafting_injector_basic);
            registerDropSelfLootTable(DEContent.crafting_injector_wyvern);
            registerDropSelfLootTable(DEContent.crafting_injector_awakened);
            registerDropSelfLootTable(DEContent.crafting_injector_chaotic);
            registerDropSelfLootTable(DEContent.crafting_core);
            registerDropSelfLootTable(DEContent.energy_core);
            registerDropSelfLootTable(DEContent.energy_core_stabilizer);
            registerDropSelfLootTable(DEContent.energy_pylon);
            registerDropSelfLootTable(DEContent.reactor_core);
            registerDropSelfLootTable(DEContent.reactor_stabilizer);
            registerDropSelfLootTable(DEContent.reactor_injector);
//            registerDropSelfLootTable(DEContent.rain_sensor);
//            registerDropSelfLootTable(DEContent.dislocation_inhibitor);
            registerDropSelfLootTable(DEContent.block_draconium);
            registerDropSelfLootTable(DEContent.block_draconium_awakened);
            registerDropSelfLootTable(DEContent.fluid_gate);
            registerDropSelfLootTable(DEContent.flux_gate);
//            registerDropSelfLootTable(DEContent.infused_obsidian);

            //Special Stuff
//            registerDropSelfLootTable(DEContent.energy_core_structure);
//            registerDropSelfLootTable(DEContent.placed_item);
//            registerLootTable(DEContent.chaos_crystal, p_218546_0_ -> dropping(DEContent.chaos_shard, 5));

            //Fortune
            registerLootTable(DEContent.ore_draconium_overworld, (block) -> droppingWithSilkTouch(block, withExplosionDecay(block, ItemLootEntry.builder(DEContent.dust_draconium).acceptFunction(SetCount.builder(RandomValueRange.of(2.0F, 4.0F))).acceptFunction(ApplyBonus.uniformBonusCount(Enchantments.FORTUNE)))));
            registerLootTable(DEContent.ore_draconium_nether, (block) -> droppingWithSilkTouch(block, withExplosionDecay(block, ItemLootEntry.builder(DEContent.dust_draconium).acceptFunction(SetCount.builder(RandomValueRange.of(2.0F, 4.0F))).acceptFunction(ApplyBonus.uniformBonusCount(Enchantments.FORTUNE)))));
            registerLootTable(DEContent.ore_draconium_end, (block) -> droppingWithSilkTouch(block, withExplosionDecay(block, ItemLootEntry.builder(DEContent.dust_draconium).acceptFunction(SetCount.builder(RandomValueRange.of(2.0F, 4.0F))).acceptFunction(ApplyBonus.uniformBonusCount(Enchantments.FORTUNE)))));
        }





        @Override
        public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
            addTables();
            Set<ResourceLocation> set = Sets.newHashSet();

            for (Block block : getKnownDEBlocks()) {
                ResourceLocation resourcelocation = block.getLootTable();
                if (resourcelocation != LootTables.EMPTY && set.add(resourcelocation)) {

                    LootTable.Builder loottable$builder = this.lootTables.remove(resourcelocation);
                    if (loottable$builder == null) {
                        throw new IllegalStateException(String.format("Missing loottable '%s' for '%s'", resourcelocation, Registry.BLOCK.getKey(block)));
                    }

                    consumer.accept(resourcelocation, loottable$builder);
                }
            }

            if (!this.lootTables.isEmpty()) {
                throw new IllegalStateException("Created block loot tables for non-blocks: " + this.lootTables.keySet());
            }
        }

        protected Iterable<Block> getKnownDEBlocks() {
            return Registry.BLOCK.stream().filter(block -> Objects.requireNonNull(block.getRegistryName()).getNamespace().equals(DraconicEvolution.MODID)).collect(Collectors.toList());
        }
    }
}
