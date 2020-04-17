package com.brandon3055.draconicevolution.datagen;

import com.brandon3055.draconicevolution.init.DEContent;
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
//            simpleItem(DEContent.chaos_shard);
//            simpleItem(DEContent.chaos_frag_small);
//            simpleItem(DEContent.chaos_frag_medium);
//            simpleItem(DEContent.chaos_frag_large);
        simpleItem(DEContent.magnet);
        simpleItem(DEContent.magnet_advanced);
        simpleItem(DEContent.dislocator);
        simpleItem(DEContent.dislocator_advanced);
        simpleItem(DEContent.dislocator_p2p, modLoc("item/dislocator_bound"));
        simpleItem(DEContent.dislocator_player, modLoc("item/dislocator_bound"));
        simpleItem(DEContent.crystal_binder);
        simpleItem(DEContent.info_tablet);

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

    @Override
    public String getName() {
        return "Draconic Evolution Item Models";
    }
}
