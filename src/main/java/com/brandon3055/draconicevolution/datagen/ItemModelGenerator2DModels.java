package com.brandon3055.draconicevolution.datagen;

import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.nio.file.Path;

/**
 * Created by brandon3055 on 13/11/2024.
 */
public class ItemModelGenerator2DModels extends ItemModelGenerator {

    public ItemModelGenerator2DModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, existingFileHelper);
    }

    @Override
    protected void registerModels() {
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

    }

    @Override
    protected Path getPath(ItemModelBuilder model) {
        ResourceLocation loc = model.getLocation();
        return this.output.getOutputFolder().resolve("2d_item_models/assets").resolve(loc.getNamespace()).resolve("models").resolve(loc.getPath() + ".json");
    }

    @Override
    public String getName() {
        return "Draconic Evolution 2D Item Models";
    }
}
