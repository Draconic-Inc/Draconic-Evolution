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
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.IProperty;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.*;
import net.minecraft.world.storage.loot.functions.*;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
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
//        for(ResourceLocation resourcelocation : Sets.difference(LootTables.func_215796_a(), map.keySet())) {
//            validationresults.addProblem("Missing built-in table: " + resourcelocation);
//        }
        map.forEach((p_218436_2_, p_218436_3_) -> {
            LootTableManager.func_227508_a_(validationtracker, p_218436_2_, p_218436_3_);
        });
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
//            registerDropSelfLootTable(DEContent.crafting_injector_basic);
//            registerDropSelfLootTable(DEContent.crafting_injector_wyvern);
//            registerDropSelfLootTable(DEContent.crafting_injector_awakened);
//            registerDropSelfLootTable(DEContent.crafting_injector_chaotic);
//            registerDropSelfLootTable(DEContent.crafting_core);
            registerDropSelfLootTable(DEContent.energy_core);
            registerDropSelfLootTable(DEContent.energy_core_stabilizer);
            registerDropSelfLootTable(DEContent.energy_pylon);
//            registerDropSelfLootTable(DEContent.reactor_core);
//            registerDropSelfLootTable(DEContent.reactor_stabilizer);
//            registerDropSelfLootTable(DEContent.reactor_injector);
//            registerDropSelfLootTable(DEContent.rain_sensor);
//            registerDropSelfLootTable(DEContent.dislocation_inhibitor);
            registerDropSelfLootTable(DEContent.block_draconium);
            registerDropSelfLootTable(DEContent.block_draconium_awakened);
//            registerDropSelfLootTable(DEContent.infused_obsidian);

            //Special Stuff
//            registerDropSelfLootTable(DEContent.energy_core_structure);
//            registerDropSelfLootTable(DEContent.placed_item);
//            registerLootTable(DEContent.chaos_crystal, p_218546_0_ -> dropping(DEContent.chaos_shard, 5));

            //Fortune
            registerLootTable(DEContent.ore_draconium_overworld, (block) -> droppingWithSilkTouch(block, withExplosionDecay(block, ItemLootEntry.builder(DEContent.dust_draconium).acceptFunction(SetCount.builder(RandomValueRange.of(2.0F, 4.0F))).acceptFunction(ApplyBonus.uniformBonusCount(Enchantments.FORTUNE)))));
            registerLootTable(DEContent.ore_draconium_nether, (block) -> droppingWithSilkTouch(block, withExplosionDecay(block, ItemLootEntry.builder(DEContent.dust_draconium).acceptFunction(SetCount.builder(RandomValueRange.of(2.0F, 4.0F))).acceptFunction(ApplyBonus.uniformBonusCount(Enchantments.FORTUNE)))));
            registerLootTable(DEContent.ore_draconium_end, (block) -> droppingWithSilkTouch(block, withExplosionDecay(block, ItemLootEntry.builder(DEContent.dust_draconium).acceptFunction(SetCount.builder(RandomValueRange.of(2.0F, 4.0F))).acceptFunction(ApplyBonus.uniformBonusCount(Enchantments.FORTUNE)))));



//            this.registerDropSelfLootTable(Blocks.GRANITE);
//            this.registerDropping(Blocks.FARMLAND, Blocks.DIRT);
//            this.registerLootTable(Blocks.STONE, (p_218490_0_) -> droppingWithSilkTouch(p_218490_0_, Blocks.COBBLESTONE));
//            this.registerLootTable(Blocks.ACACIA_SLAB, BlockLootTables::droppingSlab);
//            this.registerLootTable(Blocks.ACACIA_DOOR, (p_218483_0_) -> droppingWhen(p_218483_0_, DoorBlock.HALF, DoubleBlockHalf.LOWER));
        }


//        protected static <T> T withExplosionDecay(IItemProvider p_218552_0_, ILootFunctionConsumer<T> p_218552_1_) {
//            return (T)(!IMMUNE_TO_EXPLOSIONS.contains(p_218552_0_.asItem()) ? p_218552_1_.acceptFunction(ExplosionDecay.builder()) : p_218552_1_.cast());
//        }
//
//        protected static <T> T withSurvivesExplosion(IItemProvider p_218560_0_, ILootConditionConsumer<T> p_218560_1_) {
//            return (T)(!IMMUNE_TO_EXPLOSIONS.contains(p_218560_0_.asItem()) ? p_218560_1_.acceptCondition(SurvivesExplosion.builder()) : p_218560_1_.cast());
//        }
//
//        protected static LootTable.Builder dropping(IItemProvider itemProvider) {
//            return LootTable.builder()
//                    .addLootPool(withSurvivesExplosion(itemProvider, LootPool.builder()
//                            .rolls(ConstantRange.of(1))
//                            .addEntry(ItemLootEntry.builder(itemProvider))));
//        }
//
//        protected static LootTable.Builder dropping(IItemProvider itemProvider, int count) {
//            return LootTable.builder()
//                    .addLootPool(withSurvivesExplosion(itemProvider, LootPool.builder()
//                            .acceptFunction(SetCount.builder(ConstantRange.of(count)))
//                            .addEntry(ItemLootEntry.builder(itemProvider))));
//        }
//
//        protected static LootTable.Builder dropping(Block p_218494_0_, ILootCondition.IBuilder p_218494_1_, LootEntry.Builder<?> p_218494_2_) {
//            return LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(((StandaloneLootEntry.Builder)ItemLootEntry.builder(p_218494_0_).acceptCondition(p_218494_1_)).alternatively(p_218494_2_)));
//        }
//
//        protected static LootTable.Builder droppingWithSilkTouch(Block p_218519_0_, LootEntry.Builder<?> p_218519_1_) {
//            return dropping(p_218519_0_, SILK_TOUCH, p_218519_1_);
//        }
//
//        protected static LootTable.Builder droppingWithShears(Block p_218511_0_, LootEntry.Builder<?> p_218511_1_) {
//            return dropping(p_218511_0_, SHEARS, p_218511_1_);
//        }
//
//        protected static LootTable.Builder droppingWithSilkTouchOrShears(Block p_218535_0_, LootEntry.Builder<?> p_218535_1_) {
//            return dropping(p_218535_0_, SILK_TOUCH_OR_SHEARS, p_218535_1_);
//        }
//
//        protected static LootTable.Builder droppingWithSilkTouch(Block p_218515_0_, IItemProvider p_218515_1_) {
//            return droppingWithSilkTouch(p_218515_0_, withSurvivesExplosion(p_218515_0_, ItemLootEntry.builder(p_218515_1_)));
//        }
//
//        protected static LootTable.Builder droppingRandomly(IItemProvider p_218463_0_, IRandomRange p_218463_1_) {
//            return LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(withExplosionDecay(p_218463_0_, ItemLootEntry.builder(p_218463_0_).acceptFunction(SetCount.builder(p_218463_1_)))));
//        }
//
//        protected static LootTable.Builder droppingWithSilkTouchOrRandomly(Block p_218530_0_, IItemProvider p_218530_1_, IRandomRange p_218530_2_) {
//            return droppingWithSilkTouch(p_218530_0_, withExplosionDecay(p_218530_0_, ItemLootEntry.builder(p_218530_1_).acceptFunction(SetCount.builder(p_218530_2_))));
//        }
//
//        protected static LootTable.Builder onlyWithSilkTouch(IItemProvider p_218561_0_) {
//            return LootTable.builder().addLootPool(LootPool.builder().acceptCondition(SILK_TOUCH).rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(p_218561_0_)));
//        }
//
//        protected static LootTable.Builder droppingAndFlowerPot(IItemProvider p_218523_0_) {
//            return LootTable.builder().addLootPool(withSurvivesExplosion(Blocks.FLOWER_POT, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Blocks.FLOWER_POT)))).addLootPool(withSurvivesExplosion(p_218523_0_, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(p_218523_0_))));
//        }
//
//        protected static LootTable.Builder droppingSlab(Block p_218513_0_) {
//            return LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(withExplosionDecay(p_218513_0_, ItemLootEntry.builder(p_218513_0_).acceptFunction(SetCount.builder(ConstantRange.of(2)).acceptCondition(BlockStateProperty.builder(p_218513_0_).with(SlabBlock.TYPE, SlabType.DOUBLE))))));
//        }
//
//        protected static <T extends Comparable<T>> LootTable.Builder droppingWhen(Block p_218562_0_, IProperty<T> p_218562_1_, T p_218562_2_) {
//            return LootTable.builder().addLootPool(withSurvivesExplosion(p_218562_0_, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(p_218562_0_).acceptCondition(BlockStateProperty.builder(p_218562_0_).with(p_218562_1_, p_218562_2_)))));
//        }
//
//        protected static LootTable.Builder droppingWithName(Block p_218481_0_) {
//            return LootTable.builder().addLootPool(withSurvivesExplosion(p_218481_0_, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(p_218481_0_).acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY)))));
//        }
//
//        protected static LootTable.Builder droppingWithContents(Block p_218544_0_) {
//            return LootTable.builder().addLootPool(withSurvivesExplosion(p_218544_0_, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(p_218544_0_).acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY)).acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY).replaceOperation("Lock", "BlockEntityTag.Lock").func_216056_a("LootTable", "BlockEntityTag.LootTable").func_216056_a("LootTableSeed", "BlockEntityTag.LootTableSeed")).acceptFunction(SetContents.func_215920_b().func_216075_a(DynamicLootEntry.func_216162_a(ShulkerBoxBlock.field_220169_b))))));
//        }
//
//        protected static LootTable.Builder droppingWithPatterns(Block p_218559_0_) {
//            return LootTable.builder().addLootPool(withSurvivesExplosion(p_218559_0_, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(p_218559_0_).acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY)).acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY).replaceOperation("Patterns", "BlockEntityTag.Patterns")))));
//        }
//
//        protected static LootTable.Builder droppingItemWithFortune(Block p_218476_0_, Item p_218476_1_) {
//            return droppingWithSilkTouch(p_218476_0_, withExplosionDecay(p_218476_0_, ItemLootEntry.builder(p_218476_1_).acceptFunction(ApplyBonus.func_215869_a(Enchantments.FORTUNE))));
//        }
//
//        protected static LootTable.Builder droppingItemRarely(Block p_218491_0_, IItemProvider p_218491_1_) {
//            return droppingWithSilkTouch(p_218491_0_, withExplosionDecay(p_218491_0_, ItemLootEntry.builder(p_218491_1_).acceptFunction(SetCount.func_215932_a(RandomValueRange.func_215837_a(-6.0F, 2.0F))).acceptFunction(LimitCount.func_215911_a(IntClamper.func_215848_a(0)))));
//        }
//
//        protected static LootTable.Builder droppingSeeds(Block p_218570_0_) {
//            return droppingWithShears(p_218570_0_, withExplosionDecay(p_218570_0_, (ItemLootEntry.builder(Items.WHEAT_SEEDS).acceptCondition(RandomChance.builder(0.125F))).acceptFunction(ApplyBonus.func_215865_a(Enchantments.FORTUNE, 2))));
//        }
//
//        protected static LootTable.Builder droppingByAge(Block p_218475_0_, Item p_218475_1_) {
//            return LootTable.builder().addLootPool(withExplosionDecay(p_218475_0_, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(p_218475_1_).acceptFunction(SetCount.func_215932_a(BinomialRange.func_215838_a(3, 0.06666667F)).acceptCondition(BlockStateProperty.builder(p_218475_0_).with(StemBlock.AGE, 0))).acceptFunction(SetCount.func_215932_a(BinomialRange.func_215838_a(3, 0.13333334F)).acceptCondition(BlockStateProperty.builder(p_218475_0_).with(StemBlock.AGE, 1))).acceptFunction(SetCount.func_215932_a(BinomialRange.func_215838_a(3, 0.2F)).acceptCondition(BlockStateProperty.builder(p_218475_0_).with(StemBlock.AGE, 2))).acceptFunction(SetCount.func_215932_a(BinomialRange.func_215838_a(3, 0.26666668F)).acceptCondition(BlockStateProperty.builder(p_218475_0_).with(StemBlock.AGE, 3))).acceptFunction(SetCount.func_215932_a(BinomialRange.func_215838_a(3, 0.33333334F)).acceptCondition(BlockStateProperty.builder(p_218475_0_).with(StemBlock.AGE, 4))).acceptFunction(SetCount.func_215932_a(BinomialRange.func_215838_a(3, 0.4F)).acceptCondition(BlockStateProperty.builder(p_218475_0_).with(StemBlock.AGE, 5))).acceptFunction(SetCount.func_215932_a(BinomialRange.func_215838_a(3, 0.46666667F)).acceptCondition(BlockStateProperty.builder(p_218475_0_).with(StemBlock.AGE, 6))).acceptFunction(SetCount.func_215932_a(BinomialRange.func_215838_a(3, 0.53333336F)).acceptCondition(BlockStateProperty.builder(p_218475_0_).with(StemBlock.AGE, 7))))));
//        }
//
//        protected static LootTable.Builder onlyWithShears(IItemProvider p_218486_0_) {
//            return LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).acceptCondition(SHEARS).addEntry(ItemLootEntry.builder(p_218486_0_)));
//        }
//
//        protected static LootTable.Builder droppingWithChancesAndSticks(Block p_218540_0_, Block p_218540_1_, float... p_218540_2_) {
//            return droppingWithSilkTouchOrShears(p_218540_0_, withSurvivesExplosion(p_218540_0_, ItemLootEntry.builder(p_218540_1_)).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, p_218540_2_))).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).acceptCondition(NOT_SILK_TOUCH_OR_SHEARS).addEntry(withExplosionDecay(p_218540_0_, ItemLootEntry.builder(Items.STICK).acceptFunction(SetCount.func_215932_a(RandomValueRange.func_215837_a(1.0F, 2.0F)))).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, 0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F))));
//        }
//
//        protected static LootTable.Builder droppingWithChancesSticksAndApples(Block p_218526_0_, Block p_218526_1_, float... p_218526_2_) {
//            return droppingWithChancesAndSticks(p_218526_0_, p_218526_1_, p_218526_2_).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).acceptCondition(NOT_SILK_TOUCH_OR_SHEARS).addEntry(withSurvivesExplosion(p_218526_0_, ItemLootEntry.builder(Items.APPLE)).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F))));
//        }
//
//        protected static LootTable.Builder droppingAndBonusWhen(Block p_218541_0_, Item p_218541_1_, Item p_218541_2_, ILootCondition.IBuilder p_218541_3_) {
//            return withExplosionDecay(p_218541_0_, LootTable.builder().addLootPool(LootPool.builder().addEntry(((StandaloneLootEntry.Builder)ItemLootEntry.builder(p_218541_1_).acceptCondition(p_218541_3_)).func_216080_a(ItemLootEntry.builder(p_218541_2_)))).addLootPool(LootPool.builder().acceptCondition(p_218541_3_).addEntry(ItemLootEntry.builder(p_218541_2_).acceptFunction(ApplyBonus.func_215870_a(Enchantments.FORTUNE, 0.5714286F, 3)))));
//        }
//
//        public static LootTable.Builder func_218482_a() {
//            return LootTable.builder();
//        }


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

//        public void registerFlowerPot(Block flowerPot) {
//            this.registerLootTable(flowerPot, (p_218524_0_) -> {
//                return droppingAndFlowerPot(((FlowerPotBlock)p_218524_0_).func_220276_d());
//            });
//        }
//
//        public void registerSilkTouch(Block blockIn, Block silkTouchDrop) {
//            this.registerLootTable(blockIn, onlyWithSilkTouch(silkTouchDrop));
//        }
//
//        public void registerDropping(Block blockIn, IItemProvider drop) {
//            this.registerLootTable(blockIn, dropping(drop));
//        }
//
//        public void registerSilkTouch(Block blockIn) {
//            this.registerSilkTouch(blockIn, blockIn);
//        }
//
//        public void registerDropSelfLootTable(Block p_218492_1_) {
//            this.registerDropping(p_218492_1_, p_218492_1_);
//        }
//
//        protected void registerLootTable(Block blockIn, Function<Block, LootTable.Builder> factory) {
//            this.registerLootTable(blockIn, factory.apply(blockIn));
//        }
//
//        protected void registerLootTable(Block blockIn, LootTable.Builder table) {
//            this.lootTables.put(blockIn.getLootTable(), table);
//        }
    }
}
