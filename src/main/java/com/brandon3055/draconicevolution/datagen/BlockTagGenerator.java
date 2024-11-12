package com.brandon3055.draconicevolution.datagen;

import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.init.DETags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * Created by brandon3055 on 02/09/2022
 */
public class BlockTagGenerator extends BlockTagsProvider {

    public BlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(DETags.Blocks.STORAGE_BLOCKS_DRACONIUM).add(DEContent.DRACONIUM_BLOCK.get());
        tag(DETags.Blocks.STORAGE_BLOCKS_DRACONIUM_AWAKENED).add(DEContent.AWAKENED_DRACONIUM_BLOCK.get());
        tag(Tags.Blocks.STORAGE_BLOCKS).add(DEContent.DRACONIUM_BLOCK.get(), DEContent.AWAKENED_DRACONIUM_BLOCK.get());

        tag(DETags.Blocks.ORES_DRACONIUM).add(DEContent.END_DRACONIUM_ORE.get(), DEContent.NETHER_DRACONIUM_ORE.get(), DEContent.OVERWORLD_DRACONIUM_ORE.get(), DEContent.DEEPSLATE_DRACONIUM_ORE.get());
        tag(Tags.Blocks.ORES).add(DEContent.END_DRACONIUM_ORE.get(), DEContent.NETHER_DRACONIUM_ORE.get(), DEContent.OVERWORLD_DRACONIUM_ORE.get(), DEContent.DEEPSLATE_DRACONIUM_ORE.get());
        tag(BlockTags.SOUL_FIRE_BASE_BLOCKS).add(DEContent.INFUSED_OBSIDIAN.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(DEContent.GENERATOR.get())
                .add(DEContent.GRINDER.get())
                .add(DEContent.DISENCHANTER.get())
                .add(DEContent.ENERGY_TRANSFUSER.get())
                .add(DEContent.DISLOCATOR_PEDESTAL.get())
                .add(DEContent.DISLOCATOR_RECEPTACLE.get())
                .add(DEContent.CREATIVE_OP_CAPACITOR.get())
                .add(DEContent.ENTITY_DETECTOR.get())
                .add(DEContent.ENTITY_DETECTOR_ADVANCED.get())
                .add(DEContent.STABILIZED_SPAWNER.get())
                .add(DEContent.POTENTIOMETER.get())
                .add(DEContent.CELESTIAL_MANIPULATOR.get())
                .add(DEContent.DRACONIUM_CHEST.get())
                .add(DEContent.PARTICLE_GENERATOR.get())
                .add(DEContent.CHAOS_CRYSTAL.get())
                .add(DEContent.CHAOS_CRYSTAL_PART.get())
                .add(DEContent.BASIC_CRAFTING_INJECTOR.get())
                .add(DEContent.WYVERN_CRAFTING_INJECTOR.get())
                .add(DEContent.AWAKENED_CRAFTING_INJECTOR.get())
                .add(DEContent.CHAOTIC_CRAFTING_INJECTOR.get())
                .add(DEContent.CRAFTING_CORE.get())
                .add(DEContent.ENERGY_CORE.get())
                .add(DEContent.ENERGY_CORE_STABILIZER.get())
                .add(DEContent.ENERGY_PYLON.get())
                .add(DEContent.STRUCTURE_BLOCK.get())
                .add(DEContent.REACTOR_CORE.get())
                .add(DEContent.REACTOR_STABILIZER.get())
                .add(DEContent.REACTOR_INJECTOR.get())
                .add(DEContent.RAIN_SENSOR.get())
                .add(DEContent.DISLOCATION_INHIBITOR.get())
                .add(DEContent.OVERWORLD_DRACONIUM_ORE.get())
                .add(DEContent.DEEPSLATE_DRACONIUM_ORE.get())
                .add(DEContent.NETHER_DRACONIUM_ORE.get())
                .add(DEContent.END_DRACONIUM_ORE.get())
                .add(DEContent.DRACONIUM_BLOCK.get())
                .add(DEContent.AWAKENED_DRACONIUM_BLOCK.get())
                .add(DEContent.INFUSED_OBSIDIAN.get())
                .add(DEContent.BASIC_IO_CRYSTAL.get())
                .add(DEContent.WYVERN_IO_CRYSTAL.get())
                .add(DEContent.DRACONIC_IO_CRYSTAL.get())
//                .add(DEContent.CRYSTAL_IO_CHAOTIC.get())
                .add(DEContent.BASIC_RELAY_CRYSTAL.get())
                .add(DEContent.WYVERN_RELAY_CRYSTAL.get())
                .add(DEContent.DRACONIC_RELAY_CRYSTAL.get())
//                .add(DEContent.CRYSTAL_RELAY_CHAOTIC.get())
                .add(DEContent.BASIC_WIRELESS_CRYSTAL.get())
                .add(DEContent.WYVERN_WIRELESS_CRYSTAL.get())
                .add(DEContent.DRACONIC_WIRELESS_CRYSTAL.get())
//                .add(DEContent.CRYSTAL_WIRELESS_CHAOTIC.get())
                .add(DEContent.FLUX_GATE.get())
                .add(DEContent.FLUID_GATE.get());

        tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(DEContent.INFUSED_OBSIDIAN.get())
                .add(DEContent.CHAOS_CRYSTAL.get())
                .add(DEContent.CHAOS_CRYSTAL_PART.get());
    }
}
