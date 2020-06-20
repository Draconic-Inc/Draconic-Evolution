package com.brandon3055.draconicevolution.datagen;

import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by brandon3055 on 28/2/20.
 */
public class ItemModelGenerator extends ItemModelProvider {
    private static final Logger LOGGER = LogManager.getLogger();

    public ItemModelGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

        blockItem(DEContent.generator, modLoc("block/generator/generator"));
        blockItem(DEContent.grinder, modLoc("block/grinder/grinder"));
        blockItem(DEContent.energy_pylon, modLoc("block/energy_pylon_input"));


        blockItem(DEContent.disenchanter);
        blockItem(DEContent.energy_infuser);
        blockItem(DEContent.dislocator_pedestal);
        blockItem(DEContent.dislocator_receptacle);
        blockItem(DEContent.creative_op_capacitor);
        blockItem(DEContent.entity_detector);
        blockItem(DEContent.entity_detector_advanced);
        blockItem(DEContent.stabilized_spawner);
        blockItem(DEContent.potentiometer);
        blockItem(DEContent.celestial_manipulator);
        blockItem(DEContent.draconium_chest);
        blockItem(DEContent.particle_generator);
        blockItem(DEContent.placed_item);
        blockItem(DEContent.portal);
        blockItem(DEContent.chaos_crystal);
        blockItem(DEContent.crafting_injector_basic);
        blockItem(DEContent.crafting_injector_wyvern);
        blockItem(DEContent.crafting_injector_awakened);
        blockItem(DEContent.crafting_injector_chaotic);
        blockItem(DEContent.crafting_core);
        blockItem(DEContent.energy_core);
        blockItem(DEContent.energy_core_stabilizer);
        blockItem(DEContent.energy_core_structure);
        blockItem(DEContent.reactor_core);
        blockItem(DEContent.reactor_stabilizer);
        blockItem(DEContent.reactor_injector);
        blockItem(DEContent.rain_sensor);
        blockItem(DEContent.dislocation_inhibitor);
        blockItem(DEContent.ore_draconium_overworld);
        blockItem(DEContent.ore_draconium_nether);
        blockItem(DEContent.ore_draconium_end);
        blockItem(DEContent.block_draconium);
        blockItem(DEContent.block_draconium_awakened);
        blockItem(DEContent.infused_obsidian);
        dummyModel(DEContent.crystal_io_basic);
        dummyModel(DEContent.crystal_io_wyvern);
        dummyModel(DEContent.crystal_io_draconic);
//      dummyModel(DEContent.  crystal_io_chaotic);
        dummyModel(DEContent.crystal_relay_basic);
        dummyModel(DEContent.crystal_relay_wyvern);
        dummyModel(DEContent.crystal_relay_draconic);
//      dummyModel(DEContent.  crystal_relay_chaotic);
        dummyModel(DEContent.crystal_wireless_basic);
        dummyModel(DEContent.crystal_wireless_wyvern);
        dummyModel(DEContent.crystal_wireless_draconic);
//      dummyModel(DEContent.  crystal_wireless_chaotic);

        simpleItem(DEContent.dust_draconium, "item/components");
        simpleItem(DEContent.dust_draconium_awakened, "item/components");
        simpleItem(DEContent.ingot_draconium, "item/components");
        simpleItem(DEContent.ingot_draconium_awakened, "item/components");
        simpleItem(DEContent.nugget_draconium, "item/components");
        simpleItem(DEContent.nugget_draconium_awakened, "item/components");
        simpleItem(DEContent.core_draconium, "item/components");
        simpleItem(DEContent.core_wyvern, "item/components");
        simpleItem(DEContent.core_awakened, "item/components");
        simpleItem(DEContent.core_chaotic, "item/components");
        simpleItem(DEContent.energy_core_wyvern, "item/components");
        simpleItem(DEContent.energy_core_draconic, "item/components");
        simpleItem(DEContent.dragon_heart, "item/components");
        dummyModel(DEContent.chaos_shard);
        dummyModel(DEContent.chaos_frag_small);
        dummyModel(DEContent.chaos_frag_medium);
        dummyModel(DEContent.chaos_frag_large);
        dummyModel(DEContent.mob_soul);
        simpleItem(DEContent.magnet);
        simpleItem(DEContent.magnet_advanced);
        simpleItem(DEContent.dislocator);
        simpleItem(DEContent.dislocator_advanced);
        simpleItem(DEContent.dislocator_p2p, modLoc("item/bound_dislocator"));
        simpleItem(DEContent.dislocator_player, modLoc("item/bound_dislocator"));
        simpleItem(DEContent.crystal_binder);
        simpleItem(DEContent.info_tablet);


        //Modular Tools
        simpleItem(DEContent.capacitor_wyvern, "item/tools");
        simpleItem(DEContent.capacitor_draconic, "item/tools");
        simpleItem(DEContent.capacitor_chaotic, "item/tools");
        simpleItem(DEContent.capacitor_creative, "item/tools");
        simpleItem(DEContent.shovel_wyvern, "item/tools");
        simpleItem(DEContent.shovel_draconic, "item/tools");
        simpleItem(DEContent.shovel_chaotic, "item/tools");
        simpleItem(DEContent.pickaxe_wyvern, "item/tools");
        simpleItem(DEContent.pickaxe_draconic, "item/tools");
        simpleItem(DEContent.pickaxe_chaotic, "item/tools");
        simpleItem(DEContent.axe_wyvern, "item/tools");
        simpleItem(DEContent.axe_draconic, "item/tools");
        simpleItem(DEContent.axe_chaotic, "item/tools");
        simpleItem(DEContent.bow_wyvern, "item/tools");
        simpleItem(DEContent.bow_draconic, "item/tools");
        simpleItem(DEContent.bow_chaotic, "item/tools");
        simpleItem(DEContent.sword_wyvern, "item/tools");
        simpleItem(DEContent.sword_draconic, "item/tools");
        simpleItem(DEContent.sword_chaotic, "item/tools");
        simpleItem(DEContent.staff_draconic, "item/tools");
        simpleItem(DEContent.staff_chaotic, "item/tools");
        simpleItem(DEContent.chestpiece_wyvern, "item/tools");
        simpleItem(DEContent.chestpiece_draconic, "item/tools");
        simpleItem(DEContent.chestpiece_chaotic, "item/tools");


//            getBuilder("test_generated_model")
//                    .parent(new ModelFile.UncheckedModelFile("item/generated"))
//                    .texture("layer0", mcLoc("block/stone"));
//
//            getBuilder("test_block_model")
//                    .parent(getExistingFile(mcLoc("block/block")))
//                    .texture("all", mcLoc("block/dirt"))
//                    .texture("top", mcLoc("block/stone"))
//                    .element()
//                    .cube("#all")
//                    .face(Direction.UP)
//                    .texture("#top")
//                    .tintindex(0)
//                    .end()
//                    .end();
//
//            // Testing consistency
//
//            // Test overrides
//            ModelFile fishingRod = withExistingParent("fishing_rod", "handheld_rod")
//                    .texture("layer0", mcLoc("item/fishing_rod"))
//                    .override()
//                    .predicate(mcLoc("cast"), 1)
//                    .model(getExistingFile(mcLoc("item/fishing_rod_cast"))) // Use the vanilla model for validation
//                    .end();
//
//            withExistingParent("fishing_rod_cast", modLoc("fishing_rod"))
//                    .parent(fishingRod)
//                    .texture("layer0", mcLoc("item/fishing_rod_cast"));
    }

    private void simpleItem(Item item) {
        simpleItem(item, "item");
    }

    private void simpleItem(Item item, String textureFolder) {
        ResourceLocation reg = item.getRegistryName();
        simpleItem(item, new ResourceLocation(reg.getNamespace(), textureFolder + "/" + reg.getPath()));
    }

    private void simpleItem(Item item, ResourceLocation texture) {
        ResourceLocation reg = item.getRegistryName();
        getBuilder(reg.getPath())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", texture);
    }

    private void blockItem(Block block) {
        if (block == null) return;
        ResourceLocation reg = block.getRegistryName();
        blockItem(block, new ResourceLocation(reg.getNamespace(), "block/" + reg.getPath()));
    }

    private void blockItem(Block block, ResourceLocation blockModel) {
        if (block == null) return;
        ResourceLocation reg = block.getRegistryName();
        getBuilder(reg.getPath())
                .parent(new ModelFile.UncheckedModelFile(blockModel));
    }

    private void dummyModel(Block block) {
        dummyModel(block.asItem());
    }

    private void dummyModel(Item item) {
        getBuilder(item.getRegistryName().getPath())//
                .parent(new ModelFile.UncheckedModelFile("builtin/generated"));
    }

    @Override
    public String getName() {
        return "Draconic Evolution Item Models";
    }
}
