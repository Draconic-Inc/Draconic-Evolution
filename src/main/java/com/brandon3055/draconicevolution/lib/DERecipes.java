package com.brandon3055.draconicevolution.lib;

import com.brandon3055.draconicevolution.DEFeatures;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import static com.brandon3055.draconicevolution.DEFeatures.*;
import static com.brandon3055.draconicevolution.lib.RecipeManager.RecipeDifficulty.*;
import static com.brandon3055.draconicevolution.lib.RecipeManager.*;
import static net.minecraft.init.Blocks.EMERALD_BLOCK;
import static net.minecraft.init.Items.NETHER_STAR;

/**
 * Created by brandon3055 on 23/07/2016.
 */
public class DERecipes {

    //region Crafting

    public static void addCrafting() {
        /* ------------------ Blocks ------------------ */

        //region Simple Blocks

        //endregion

        //region Machines

        //endregion

        //region Advanced Machines

        //endregion

        //region Exotic Blocks

        //endregion

	    /* ------------------ Items ------------------ */

        //region Crafting Components / Base items
        if (RecipeManager.isEnabled(DEFeatures.draconiumDust) && RecipeManager.isEnabled(DEFeatures.draconiumIngot)){
            GameRegistry.addSmelting(DEFeatures.draconiumDust, new ItemStack(DEFeatures.draconicIngot), 0);
        }

        //Nuggets, Ingots, Blocks and Shards
        addShapeless(ALL, new ItemStack(nugget, 9), "ingotDraconium");                          //Ingots to Nuggets
        addShapeless(ALL, new ItemStack(nugget, 9, 1), "ingotDraconiumAwakened");
        addShaped(ALL, draconiumIngot, "AAA", "AAA", "AAA", 'A', "nuggetDraconium");            //Nuggets to Ingots
        addShaped(ALL, draconicIngot, "AAA", "AAA", "AAA", 'A', new ItemStack(nugget, 1, 1));
        addShaped(ALL, draconiumBlock, "AAA", "AAA", "AAA", 'A', "ingotDraconium");             //Ingots to Blocks
        addShaped(ALL, draconicBlock, "AAA", "AAA", "AAA", 'A', "ingotDraconiumAwakened");
        addShapeless(ALL, new ItemStack(draconiumIngot, 9), "blockDraconium");                  //Blocks to Ingots
        addShapeless(ALL, new ItemStack(draconicIngot, 9), "blockDraconiumAwakened");
        addShaped(ALL, new ItemStack(chaosShard, 1, 2), "AAA", "AAA", "AAA", 'A', new ItemStack(chaosShard, 1, 3));
        addShaped(ALL, new ItemStack(chaosShard, 1, 1), "AAA", "AAA", "AAA", 'A', new ItemStack(chaosShard, 1, 2));
        addShaped(ALL, chaosShard, "AAA", "AAA", "AAA", 'A', new ItemStack(chaosShard, 1, 1));
        //Cores
        addShaped(NORMAL, draconicCore, "ABA", "BCB", "ABA", 'A', "ingotDraconium", 'B', "ingotGold", 'C', "gemDiamond");
        addShaped(HARD, draconicCore, "ABA", "BCB", "ABA", 'A', "ingotDraconium", 'B', "gemDiamond", 'C', "netherStar");
        addShaped(NORMAL, wyvernCore, "ABA", "BCB", "ABA", 'A', "ingotDraconium", 'B', draconicCore, 'C', "netherStar");
        addFusion(HARD, new ItemStack(wyvernCore), new ItemStack(EMERALD_BLOCK), 1000000, 0, draconicCore, draconicCore, "blockDraconium", "netherStar", draconicCore, "netherStar", "blockDraconium", draconicCore, draconicCore);
        addFusion(NORMAL, new ItemStack(awakenedCore), new ItemStack(NETHER_STAR), 1000000, 1, wyvernCore, wyvernCore, "ingotDraconiumAwakened", "ingotDraconiumAwakened", "ingotDraconiumAwakened", "ingotDraconiumAwakened", "ingotDraconiumAwakened", wyvernCore, wyvernCore);
        addFusion(HARD, new ItemStack(awakenedCore), new ItemStack(NETHER_STAR), 10000000, 1, wyvernCore, wyvernCore, "blockDraconiumAwakened", "blockDraconiumAwakened", wyvernCore, "blockDraconiumAwakened", "blockDraconiumAwakened", wyvernCore, wyvernCore);
        addFusion(NORMAL, new ItemStack(chaoticCore), new ItemStack(chaosShard), 100000000, 2, "ingotDraconiumAwakened", "ingotDraconiumAwakened", awakenedCore, awakenedCore, "ingotDraconiumAwakened", awakenedCore, awakenedCore, "ingotDraconiumAwakened");
        addFusion(HARD, new ItemStack(chaoticCore), new ItemStack(chaosShard), 100000000, 2, chaosShard, awakenedCore, "blockDraconiumAwakened", "blockDraconiumAwakened", "blockDraconiumAwakened", awakenedCore, chaosShard, chaosShard, awakenedCore, "blockDraconiumAwakened", "blockDraconiumAwakened", "blockDraconiumAwakened", awakenedCore, chaosShard);
        //energy Cores
        addShaped(NORMAL, wyvernEnergyCore, "ABA", "BCB", "ABA", 'A', "ingotDraconium", 'B', "blockRedstone", 'C', draconicCore);
        addShaped(HARD, wyvernEnergyCore, "ABA", "BCB", "ABA", 'A', "blockDraconium", 'B', "blockRedstone", 'C', draconicCore);
        addShaped(NORMAL, draconicEnergyCore, "ABA", "BCB", "ABA", 'A', "ingotDraconiumAwakened", 'B', wyvernEnergyCore, 'C', wyvernCore);
        addFusion(HARD, new ItemStack(draconicEnergyCore), new ItemStack(wyvernEnergyCore), 10000000, 2, "ingotDraconiumAwakened", "ingotDraconiumAwakened", awakenedCore, "ingotDraconiumAwakened", "ingotDraconiumAwakened", "blockRedstone", "blockRedstone", "blockRedstone", "blockRedstone", "blockRedstone");
        //endregion

        //region Tools

        //endregion

        //region Armor

        //endregion

        //region Exotic Items

        //endregion

//        Pump out recipes as quick as possible
//                Than JEI Handler
//                Than maby the chest or the reactor
//                Maby try to get as many litle features in as possible
//                Or.... Finish the tools Atleast the weapons

    }

    //endregion

    //region Fusion

    public static void addFusiond() {

        /* ------------------ Blocks ------------------ */

        //region Simple Blocks

        //endregion

        //region Machines

        //endregion

        //region Advanced Machines

        //endregion

        //region Exotic Blocks

        //endregion

	    /* ------------------ Items ------------------ */

        //region Crafting Components / Base items

        //endregion

        //region Tools

        //endregion

        //region Armor

        //endregion

        //region Exotic Items

        //endregion

    }

    //endregion
}
