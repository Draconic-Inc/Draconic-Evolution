package com.brandon3055.draconicevolution;

import com.brandon3055.brandonscore.config.ModFeatureParser;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by brandon3055 on 22/3/2016.
 * This class will handle all ore dictionary entries for DE along with some other stuff
 */
public class OreHandler {

	public static void initialize(){
		registerOres();
	}

	private static void registerOres(){
		ModFeatureParser parser = DraconicEvolution.featureParser;

		if (parser.isEnabled(DEFeatures.draconiumOre)) {
			OreDictionary.registerOre("oreDraconium", DEFeatures.draconiumOre);
		}

		if (parser.isEnabled(DEFeatures.draconiumBlock)) {
			OreDictionary.registerOre("blockDraconium", new ItemStack(DEFeatures.draconiumBlock));
		}

		if (parser.isEnabled(DEFeatures.draconicBlock)) {
			OreDictionary.registerOre("blockDraconiumAwakened", new ItemStack(DEFeatures.draconicBlock));
		}

		if (parser.isEnabled(DEFeatures.draconiumIngot)) {
			OreDictionary.registerOre("ingotDraconium", DEFeatures.draconiumIngot);
		}

		if (parser.isEnabled(DEFeatures.draconiumDust)) {
			OreDictionary.registerOre("dustDraconium", DEFeatures.draconiumDust);
		}

		if (parser.isEnabled(DEFeatures.draconicIngot)) {
			OreDictionary.registerOre("ingotDraconiumAwakened", DEFeatures.draconicIngot);
		}

		if (parser.isEnabled(DEFeatures.nugget)) {
			OreDictionary.registerOre("nuggetDraconium", new ItemStack(DEFeatures.nugget, 1, 0));
			OreDictionary.registerOre("nuggetDraconiumAwakened", new ItemStack(DEFeatures.nugget, 1, 1));
		}
	}
}
