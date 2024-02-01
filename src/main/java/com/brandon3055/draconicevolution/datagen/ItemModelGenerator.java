package com.brandon3055.draconicevolution.datagen;

import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.init.DEModules;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Locale;
import java.util.Objects;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by brandon3055 on 28/2/20.
 */
public class ItemModelGenerator extends ItemModelProvider {

    public ItemModelGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator.getPackOutput(), MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        //region Block Items
        blockItem(DEContent.GENERATOR, modLoc("block/generator/generator"));
        blockItem(DEContent.GRINDER, modLoc("block/grinder/grinder"));
        blockItem(DEContent.ENERGY_PYLON, modLoc("block/energy_pylon_input"));
//        blockItem(DEContent.BASIC_CRAFTING_INJECTOR); //TODO Why did i have these???
//        blockItem(DEContent.WYVERN_CRAFTING_INJECTOR);
//        blockItem(DEContent.AWAKENED_CRAFTING_INJECTOR);
//        blockItem(DEContent.CHAOTIC_CRAFTING_INJECTOR);
        blockItem(DEContent.CRAFTING_CORE, modLoc("block/crafting/fusion_crafting_core"));
        blockItem(DEContent.BASIC_CRAFTING_INJECTOR, modLoc("block/crafting/crafting_injector_draconium"));
        blockItem(DEContent.WYVERN_CRAFTING_INJECTOR, modLoc("block/crafting/crafting_injector_wyvern"));
        blockItem(DEContent.AWAKENED_CRAFTING_INJECTOR, modLoc("block/crafting/crafting_injector_draconic"));
        blockItem(DEContent.CHAOTIC_CRAFTING_INJECTOR, modLoc("block/crafting/crafting_injector_chaotic"));

        blockItem(DEContent.FLUID_GATE);
        blockItem(DEContent.FLUX_GATE);
        blockItem(DEContent.POTENTIOMETER);

        blockItem(DEContent.DISENCHANTER);
        blockItem(DEContent.ENERGY_TRANSFUSER, modLoc("block/energy_transfuser"));
        blockItem(DEContent.DISLOCATOR_PEDESTAL);
        blockItem(DEContent.DISLOCATOR_RECEPTACLE, modLoc("block/dislocator_receptacle_inactive"));
        blockItem(DEContent.CREATIVE_OP_CAPACITOR);
        blockItem(DEContent.ENTITY_DETECTOR);
        blockItem(DEContent.ENTITY_DETECTOR_ADVANCED);
        blockItem(DEContent.STABILIZED_SPAWNER);
        blockItem(DEContent.CELESTIAL_MANIPULATOR);
        blockItem(DEContent.DRACONIUM_CHEST);
        blockItem(DEContent.PARTICLE_GENERATOR);
        dummyBlock(DEContent.PLACED_ITEM);
        blockItem(DEContent.PORTAL);
        blockItem(DEContent.CHAOS_CRYSTAL);
        blockItem(DEContent.ENERGY_CORE);
        blockItem(DEContent.ENERGY_CORE_STABILIZER);
        blockItem(DEContent.STRUCTURE_BLOCK);
        blockItem(DEContent.REACTOR_CORE);
        blockItem(DEContent.REACTOR_STABILIZER);
        blockItem(DEContent.REACTOR_INJECTOR);
        blockItem(DEContent.RAIN_SENSOR);
        blockItem(DEContent.DISLOCATION_INHIBITOR);
        blockItem(DEContent.OVERWORLD_DRACONIUM_ORE);
        blockItem(DEContent.NETHER_DRACONIUM_ORE);
        blockItem(DEContent.END_DRACONIUM_ORE);
        blockItem(DEContent.DEEPSLATE_DRACONIUM_ORE);
        blockItem(DEContent.DRACONIUM_BLOCK);
        blockItem(DEContent.AWAKENED_DRACONIUM_BLOCK);
        blockItem(DEContent.INFUSED_OBSIDIAN);
        dummyBlock(DEContent.BASIC_IO_CRYSTAL);
        dummyBlock(DEContent.WYVERN_IO_CRYSTAL);
        dummyBlock(DEContent.DRACONIC_IO_CRYSTAL);
//      dummyModel(DEContent.CRYSTAL_IO_CHAOTIC);
        dummyBlock(DEContent.BASIC_RELAY_CRYSTAL);
        dummyBlock(DEContent.WYVERN_RELAY_CRYSTAL);
        dummyBlock(DEContent.DRACONIC_RELAY_CRYSTAL);
//      dummyModel(DEContent.CRYSTAL_RELAY_CHAOTIC);
        dummyBlock(DEContent.BASIC_WIRELESS_CRYSTAL);
        dummyBlock(DEContent.WYVERN_WIRELESS_CRYSTAL);
        dummyBlock(DEContent.DRACONIC_WIRELESS_CRYSTAL);
//      dummyModel(DEContent.CRYSTAL_WIRELESS_CHAOTIC);
        //endregion

        //region Components
        simpleItem(DEContent.DUST_DRACONIUM, "item/components");
        simpleItem(DEContent.DUST_DRACONIUM_AWAKENED, "item/components");
        simpleItem(DEContent.INGOT_DRACONIUM, "item/components");
        simpleItem(DEContent.INGOT_DRACONIUM_AWAKENED, "item/components");
        simpleItem(DEContent.NUGGET_DRACONIUM, "item/components");
        simpleItem(DEContent.NUGGET_DRACONIUM_AWAKENED, "item/components");
        simpleItem(DEContent.CORE_DRACONIUM, "item/components");
        simpleItem(DEContent.CORE_WYVERN, "item/components");
        simpleItem(DEContent.CORE_AWAKENED, "item/components");
        simpleItem(DEContent.CORE_CHAOTIC, "item/components");
        simpleItem(DEContent.ENERGY_CORE_WYVERN, "item/components");
        simpleItem(DEContent.ENERGY_CORE_DRACONIC, "item/components");
        simpleItem(DEContent.ENERGY_CORE_CHAOTIC, "item/components");
        simpleItem(DEContent.DRAGON_HEART, "item/components");
        simpleItem(DEContent.MODULE_CORE, "item/components");
        dummyItem(DEContent.CHAOS_SHARD);
        dummyItem(DEContent.CHAOS_FRAG_SMALL);
        dummyItem(DEContent.CHAOS_FRAG_MEDIUM);
        dummyItem(DEContent.CHAOS_FRAG_LARGE);
        dummyItem(DEContent.REACTOR_PRT_STAB_FRAME);
        dummyItem(DEContent.REACTOR_PRT_IN_ROTOR);
        dummyItem(DEContent.REACTOR_PRT_OUT_ROTOR);
        dummyItem(DEContent.REACTOR_PRT_ROTOR_FULL);
        dummyItem(DEContent.REACTOR_PRT_FOCUS_RING);
        dummyBlock(DEContent.DRACONIUM_CHEST);
        dummyBlock(DEContent.REACTOR_CORE);
        dummyBlock(DEContent.REACTOR_STABILIZER);
        dummyBlock(DEContent.REACTOR_INJECTOR);
        //endregion

        //region Misc
        dummyItem(DEContent.MOB_SOUL);
        simpleItem(DEContent.MAGNET);
        simpleItem(DEContent.MAGNET_ADVANCED);
        simpleItem(DEContent.DISLOCATOR);
        simpleItem(DEContent.DISLOCATOR_ADVANCED);
        simpleItem(DEContent.DISLOCATOR_P2P, modLoc("item/bound_dislocator"));
        simpleItem(DEContent.DISLOCATOR_P2P_UNBOUND, modLoc("item/un_bound_dislocator"));
        simpleItem(DEContent.DISLOCATOR_PLAYER, modLoc("item/bound_dislocator"));
        simpleItem(DEContent.DISLOCATOR_PLAYER_UNBOUND, modLoc("item/bound_dislocator"));
        simpleItem(DEContent.CRYSTAL_BINDER);
        simpleItem(DEContent.INFO_TABLET);
        //endregion

//        File textures = new File("../BrandonsMods/Draconic-Evolution/src/main/resources/assets/draconicevolution/textures");
//        DEModules.moduleItemMap.forEach((module, item) -> {
//            String name = Objects.requireNonNull(module.getRegistryName()).getPath();
//            File moduleTexture = new File(textures, "module/" + name + ".png");
//            if (!moduleTexture.exists()) SneakyUtils.sneaky(() -> FileUtils.copyFile(new File(textures, "item/module/" + module.getModuleTechLevel().name().toLowerCase(Locale.ENGLISH) + ".png"), moduleTexture));
//        });

        DEModules.MODULES.getEntries().stream().filter(e->e.getId().getNamespace().equals(MODID)).forEach((module) -> {
            String name = Objects.requireNonNull(module.getId()).getPath();
            ResourceLocation baseTexture = new ResourceLocation(MODID, "item/module/" + module.get().getModuleTechLevel().name().toLowerCase(Locale.ENGLISH));
            ResourceLocation overlay = new ResourceLocation(MODID, "module/" + name);
            multiLayerItem(module.get().getItem(), baseTexture, overlay);
        });

        //region Modular Tools
        simpleItem(DEContent.CAPACITOR_WYVERN, "item/tools");
        simpleItem(DEContent.CAPACITOR_DRACONIC, "item/tools");
        simpleItem(DEContent.CAPACITOR_CHAOTIC, "item/tools");
        simpleItem(DEContent.CAPACITOR_CREATIVE, "item/tools");
        simpleItem(DEContent.SHOVEL_WYVERN, "item/tools");
        simpleItem(DEContent.SHOVEL_DRACONIC, "item/tools");
        simpleItem(DEContent.SHOVEL_CHAOTIC, "item/tools");
        simpleItem(DEContent.PICKAXE_WYVERN, "item/tools");
        simpleItem(DEContent.PICKAXE_DRACONIC, "item/tools");
        simpleItem(DEContent.PICKAXE_CHAOTIC, "item/tools");
        simpleItem(DEContent.HOE_WYVERN, "item/tools");
        simpleItem(DEContent.HOE_DRACONIC, "item/tools");
        simpleItem(DEContent.HOE_CHAOTIC, "item/tools");
        simpleItem(DEContent.AXE_WYVERN, "item/tools");
        simpleItem(DEContent.AXE_DRACONIC, "item/tools");
        simpleItem(DEContent.AXE_CHAOTIC, "item/tools");
        simpleItem(DEContent.BOW_WYVERN, "item/tools");
        simpleItem(DEContent.BOW_DRACONIC, "item/tools");
        simpleItem(DEContent.BOW_CHAOTIC, "item/tools");
        simpleItem(DEContent.SWORD_WYVERN, "item/tools");
        simpleItem(DEContent.SWORD_DRACONIC, "item/tools");
        simpleItem(DEContent.SWORD_CHAOTIC, "item/tools");
        simpleItem(DEContent.STAFF_DRACONIC, "item/tools");
        simpleItem(DEContent.STAFF_CHAOTIC, "item/tools");
        simpleItem(DEContent.CHESTPIECE_WYVERN, "item/tools");
        simpleItem(DEContent.CHESTPIECE_DRACONIC, "item/tools");
        simpleItem(DEContent.CHESTPIECE_CHAOTIC, "item/tools");
        //endregion
    }

    private void simpleItem(RegistryObject<? extends Item> item) {
        simpleItem(item, "item");
    }

    @SuppressWarnings("ConstantConditions")
    private void simpleItem(RegistryObject<? extends Item> item, String textureFolder) {
        ResourceLocation reg = item.getId();
        simpleItem(item, new ResourceLocation(reg.getNamespace(), textureFolder + "/" + reg.getPath()));
    }

    @SuppressWarnings("ConstantConditions")
    private void simpleItem(RegistryObject<? extends Item> item, ResourceLocation texture) {
        ResourceLocation reg = item.getId();
        getBuilder(reg.getPath())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", texture);
    }

    @SuppressWarnings("ConstantConditions")
    private void multiLayerItem(RegistryObject<? extends Item> item, ResourceLocation texture, ResourceLocation overlay) {
        ResourceLocation reg = item.getId();
        getBuilder(reg.getPath())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", texture)
                .texture("layer1", overlay);
    }

    private void multiLayerItem(Item item, ResourceLocation texture, ResourceLocation overlay) {
        ResourceLocation reg = ForgeRegistries.ITEMS.getKey(item);
        getBuilder(reg.getPath())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", texture)
                .texture("layer1", overlay);
    }

    private void blockItem(RegistryObject<? extends Block> block) {
        if (block == null) return;
        ResourceLocation reg = block.getId();
        blockItem(block, new ResourceLocation(reg.getNamespace(), "block/" + reg.getPath()));
    }

    private void blockItem(RegistryObject<? extends Block> block, ResourceLocation blockModel) {
        if (block == null) return;
        ResourceLocation reg = block.getId();
        getBuilder(reg.getPath()).parent(new ModelFile.UncheckedModelFile(blockModel));
    }

    private void dummyBlock(RegistryObject<? extends Block> block) {
        getBuilder(block.getId().getPath())//
                .parent(new ModelFile.UncheckedModelFile("builtin/generated"));
    }

    private void dummyItem(RegistryObject<? extends Item> item) {
        getBuilder(item.getId().getPath())//
                .parent(new ModelFile.UncheckedModelFile("builtin/generated"));
    }

    @Override
    public String getName() {
        return "Draconic Evolution Item Models";
    }
}
