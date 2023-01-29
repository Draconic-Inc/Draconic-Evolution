package com.brandon3055.draconicevolution.common.lib;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.OreDictionary;

import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.items.DraconiumBlend;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Created by Brandon on 2/11/2014.
 */
public class OreDoublingRegistry {

    private static Map<String, ItemStack> oreResults = new HashMap<String, ItemStack>();
    private static String[] names = { "oreGold", "oreIron", "oreAluminum", "oreCopper", "oreLead", "oreSilver",
            "oreTin", "oreUranium", "orePlatinum", "oreNickel", "oreMithril", "oreCobalt", "oreArdite" };
    public static Map<String, ItemStack> resultOverrides = new HashMap<String, ItemStack>();

    public static void init() {

        for (String oreName : names) {
            String ingotName = "ingot" + oreName.substring(oreName.indexOf("ore") + 3);

            ItemStack resultIngot = GameRegistry.findItemStack("ThermalFoundation", ingotName, 1);

            if (resultIngot == null && OreDictionary.getOres(ingotName).size() > 0)
                resultIngot = OreDictionary.getOres(ingotName).get(0);

            oreResults.put(oreName, resultIngot);
        }
    }

    public static ItemStack getOreResult(ItemStack stack) {
        if (stack == null) return null;
        if (resultOverrides.containsKey(stack.getItem().getUnlocalizedName(stack)))
            return resultOverrides.get(stack.getItem().getUnlocalizedName(stack)).copy();
        if (FurnaceRecipes.smelting().getSmeltingResult(stack) == null) return null;
        if (stack.getItem() instanceof DraconiumBlend) return new ItemStack(ModItems.draconiumIngot, 4);
        else if (stack.getItem() == Item.getItemFromBlock(Blocks.cobblestone)) return new ItemStack(Blocks.stone, 2);
        else if (stack.getItem() == Item.getItemFromBlock(Blocks.sand)) return new ItemStack(Blocks.glass, 2);
        else if (stack.getItem() == Item.getItemFromBlock(Blocks.cactus)) return new ItemStack(Items.dye, 2, 2);
        else if (stack.getItem() == Items.clay_ball) return new ItemStack(Items.brick, 2);

        int[] ids = OreDictionary.getOreIDs(stack);
        String name;
        ItemStack resultStack = null;

        if (ids.length > 0) {
            name = OreDictionary.getOreName(ids[0]);
            resultStack = oreResults.get(name);
        }

        if (resultStack != null) {
            ItemStack doubledStack = resultStack.copy();
            doubledStack.stackSize = FurnaceRecipes.smelting().getSmeltingResult(stack).stackSize * 2;
            return doubledStack;
        }

        return null;
    }
}
