package com.brandon3055.draconicevolution.datagen;

import codechicken.lib.util.SneakyUtils;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.init.DEModules;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Objects;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by brandon3055 on 28/2/20.
 */
public class ItemModelGenerator extends ItemModelProvider {

    public ItemModelGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        //region Block Items
        blockItem(DEContent.generator, modLoc("block/generator/generator"));
        blockItem(DEContent.grinder, modLoc("block/grinder/grinder"));
        blockItem(DEContent.energy_pylon, modLoc("block/energy_pylon_input"));
        blockItem(DEContent.crafting_injector_basic);
        blockItem(DEContent.crafting_injector_wyvern);
        blockItem(DEContent.crafting_injector_awakened);
        blockItem(DEContent.crafting_injector_chaotic);
        blockItem(DEContent.crafting_core, modLoc("block/crafting/fusion_crafting_core"));
        blockItem(DEContent.crafting_injector_basic, modLoc("block/crafting/crafting_injector_draconium"));
        blockItem(DEContent.crafting_injector_wyvern, modLoc("block/crafting/crafting_injector_wyvern"));
        blockItem(DEContent.crafting_injector_awakened, modLoc("block/crafting/crafting_injector_draconic"));
        blockItem(DEContent.crafting_injector_chaotic, modLoc("block/crafting/crafting_injector_chaotic"));

        blockItem(DEContent.fluid_gate);
        blockItem(DEContent.flux_gate);
        blockItem(DEContent.potentiometer);


        blockItem(DEContent.disenchanter);
        blockItem(DEContent.energy_transfuser, modLoc("block/energy_transfuser"));
        blockItem(DEContent.dislocator_pedestal);
        blockItem(DEContent.dislocator_receptacle);
        blockItem(DEContent.creative_op_capacitor);
        blockItem(DEContent.entity_detector);
        blockItem(DEContent.entity_detector_advanced);
        blockItem(DEContent.stabilized_spawner);
        blockItem(DEContent.celestial_manipulator);
        blockItem(DEContent.draconium_chest);
        blockItem(DEContent.particle_generator);
        blockItem(DEContent.placed_item);
        blockItem(DEContent.portal);
        blockItem(DEContent.chaos_crystal);
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
        //endregion

        //region Components
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
        simpleItem(DEContent.energy_core_chaotic, "item/components");
        simpleItem(DEContent.dragon_heart, "item/components");
        simpleItem(DEContent.module_core, "item/components");
        dummyModel(DEContent.chaos_shard);
        dummyModel(DEContent.chaos_frag_small);
        dummyModel(DEContent.chaos_frag_medium);
        dummyModel(DEContent.chaos_frag_large);
        dummyModel(DEContent.reactor_prt_stab_frame);
        dummyModel(DEContent.reactor_prt_in_rotor);
        dummyModel(DEContent.reactor_prt_out_rotor);
        dummyModel(DEContent.reactor_prt_rotor_full);
        dummyModel(DEContent.reactor_prt_focus_ring);
        //endregion

        //region Misc
        dummyModel(DEContent.mob_soul);
        simpleItem(DEContent.magnet);
        simpleItem(DEContent.magnet_advanced);
        simpleItem(DEContent.dislocator);
        simpleItem(DEContent.dislocator_advanced);
        simpleItem(DEContent.dislocator_p2p, modLoc("item/bound_dislocator"));
        simpleItem(DEContent.dislocator_player, modLoc("item/bound_dislocator"));
        simpleItem(DEContent.crystal_binder);
        simpleItem(DEContent.info_tablet);
        //endregion

        //region Modules (Dont mind this. This is just me being to lazy to go copy paste some images)
        File textures = new File("C:\\Users\\tom\\Desktop\\Java Applications\\Draconic-Evolution\\src\\main\\resources\\assets\\draconicevolution\\textures");
        DEModules.moduleItemMap.forEach((module, item) -> {
            String name = Objects.requireNonNull(module.getRegistryName()).getPath();
            File itemTexture = new File(textures, "item/modules/" + name + ".png");
            File moduleTexture = new File(textures, "module/" + name + ".png");
            if (!itemTexture.exists()) SneakyUtils.sneaky(() -> FileUtils.copyFile(new File(textures, "item/modules/blank_" + name.substring(0, name.indexOf("_")) + ".png"), itemTexture));
            if (!moduleTexture.exists()) SneakyUtils.sneaky(() -> FileUtils.copyFile(itemTexture, moduleTexture));
            simpleItem(item, new ResourceLocation(MODID, "item/modules/" + name));
        });
        //endregion

        //region Modular Tools
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
        //endregion
    }

    private void simpleItem(Item item) {
        simpleItem(item, "item");
    }

    @SuppressWarnings("ConstantConditions")
    private void simpleItem(Item item, String textureFolder) {
        ResourceLocation reg = item.getRegistryName();
        simpleItem(item, new ResourceLocation(reg.getNamespace(), textureFolder + "/" + reg.getPath()));
    }

    @SuppressWarnings("ConstantConditions")
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
