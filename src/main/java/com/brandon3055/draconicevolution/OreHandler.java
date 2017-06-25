package com.brandon3055.draconicevolution;

import com.brandon3055.brandonscore.registry.ModFeatureParser;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by brandon3055 on 22/3/2016.
 * This class will handle all ore dictionary entries for DE along with some other stuff
 */
public class OreHandler {

    public static void initialize() {
        registerOres();
    }

    private static void registerOres() {
        OreDictionary.registerOre("dragonEgg", Blocks.DRAGON_EGG);

        if (Loader.isModLoaded("DragonMounts")) {
            LogHelper.info("Adding ore entry for Dragon Mounts Dragon Eggs...");
            Item egg = Item.REGISTRY.getObject(new ResourceLocation("dragonmounts:dragon_egg"));
            if (egg != null) {
                for (int i = 0; i < 8; i++) {
                    OreDictionary.registerOre("dragonEgg", new ItemStack(egg, 1, i));
                }
            }
        }

        if (ModFeatureParser.isEnabled(DEFeatures.draconiumOre)) {
            OreDictionary.registerOre("oreDraconium", DEFeatures.draconiumOre);
        }

        if (ModFeatureParser.isEnabled(DEFeatures.draconiumBlock)) {
            OreDictionary.registerOre("blockDraconium", new ItemStack(DEFeatures.draconiumBlock));
        }

        if (ModFeatureParser.isEnabled(DEFeatures.draconicBlock)) {
            OreDictionary.registerOre("blockDraconiumAwakened", new ItemStack(DEFeatures.draconicBlock));
        }

        if (ModFeatureParser.isEnabled(DEFeatures.draconiumIngot)) {
            OreDictionary.registerOre("ingotDraconium", DEFeatures.draconiumIngot);
        }

        if (ModFeatureParser.isEnabled(DEFeatures.draconiumDust)) {
            OreDictionary.registerOre("dustDraconium", DEFeatures.draconiumDust);
        }

        if (ModFeatureParser.isEnabled(DEFeatures.draconicIngot)) {
            OreDictionary.registerOre("ingotDraconiumAwakened", DEFeatures.draconicIngot);
        }

        if (ModFeatureParser.isEnabled(DEFeatures.nugget)) {
            OreDictionary.registerOre("nuggetDraconium", new ItemStack(DEFeatures.nugget, 1, 0));
            OreDictionary.registerOre("nuggetDraconiumAwakened", new ItemStack(DEFeatures.nugget, 1, 1));
        }
    }
}
