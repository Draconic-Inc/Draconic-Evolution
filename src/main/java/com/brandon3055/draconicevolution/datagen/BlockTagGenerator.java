package com.brandon3055.draconicevolution.datagen;

import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.init.DETags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 02/09/2022
 */
public class BlockTagGenerator extends BlockTagsProvider {
    public BlockTagGenerator(DataGenerator generatorIn, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(generatorIn, modId, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(DETags.Blocks.STORAGE_BLOCKS_DRACONIUM).add(DEContent.block_draconium);
        tag(DETags.Blocks.STORAGE_BLOCKS_DRACONIUM_AWAKENED).add(DEContent.block_draconium_awakened);
        tag(Tags.Blocks.STORAGE_BLOCKS).add(DEContent.block_draconium, DEContent.block_draconium_awakened);

        tag(DETags.Blocks.ORES_DRACONIUM).add(DEContent.ore_draconium_end, DEContent.ore_draconium_nether, DEContent.ore_draconium_overworld, DEContent.ore_draconium_deepslate);
        tag(Tags.Blocks.ORES).add(DEContent.ore_draconium_end, DEContent.ore_draconium_nether, DEContent.ore_draconium_overworld, DEContent.ore_draconium_deepslate);
        tag(BlockTags.SOUL_FIRE_BASE_BLOCKS).add(DEContent.infused_obsidian);

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(DEContent.generator)
                .add(DEContent.grinder)
                .add(DEContent.disenchanter)
                .add(DEContent.energy_transfuser)
                .add(DEContent.dislocator_pedestal)
                .add(DEContent.dislocator_receptacle)
                .add(DEContent.creative_op_capacitor)
                .add(DEContent.entity_detector)
                .add(DEContent.entity_detector_advanced)
                .add(DEContent.stabilized_spawner)
                .add(DEContent.potentiometer)
                .add(DEContent.celestial_manipulator)
                .add(DEContent.draconium_chest)
                .add(DEContent.particle_generator)
                .add(DEContent.chaos_crystal)
                .add(DEContent.chaos_crystal_part)
                .add(DEContent.crafting_injector_basic)
                .add(DEContent.crafting_injector_wyvern)
                .add(DEContent.crafting_injector_awakened)
                .add(DEContent.crafting_injector_chaotic)
                .add(DEContent.crafting_core)
                .add(DEContent.energy_core)
                .add(DEContent.energy_core_stabilizer)
                .add(DEContent.energy_pylon)
                .add(DEContent.structure_block)
                .add(DEContent.reactor_core)
                .add(DEContent.reactor_stabilizer)
                .add(DEContent.reactor_injector)
                .add(DEContent.rain_sensor)
                .add(DEContent.dislocation_inhibitor)
                .add(DEContent.ore_draconium_overworld)
                .add(DEContent.ore_draconium_deepslate)
                .add(DEContent.ore_draconium_nether)
                .add(DEContent.ore_draconium_end)
                .add(DEContent.block_draconium)
                .add(DEContent.block_draconium_awakened)
                .add(DEContent.infused_obsidian)
                .add(DEContent.crystal_io_basic)
                .add(DEContent.crystal_io_wyvern)
                .add(DEContent.crystal_io_draconic)
//                .add(DEContent.crystal_io_chaotic)
                .add(DEContent.crystal_relay_basic)
                .add(DEContent.crystal_relay_wyvern)
                .add(DEContent.crystal_relay_draconic)
//                .add(DEContent.crystal_relay_chaotic)
                .add(DEContent.crystal_wireless_basic)
                .add(DEContent.crystal_wireless_wyvern)
                .add(DEContent.crystal_wireless_draconic)
//                .add(DEContent.crystal_wireless_chaotic)
                .add(DEContent.flux_gate)
                .add(DEContent.fluid_gate);


        tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(DEContent.infused_obsidian)
                .add(DEContent.chaos_crystal)
                .add(DEContent.chaos_crystal_part);


    }
}
