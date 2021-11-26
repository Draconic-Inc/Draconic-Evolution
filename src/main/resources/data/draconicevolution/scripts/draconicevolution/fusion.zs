import mods.draconicevolution.TechLevel;
import mods.draconicevolution.FusionIngredient;
import crafttweaker.api.recipe.Replacer;

/*
    The following lines will do the following:
    1) Add a WYVERN Fusion recipe that will use 50000 energy to output Dirt when Glass is used as a catalyst and a Diamond and any item from the Wool tag is used as an input. Both inputs are consumed when crafting.
    1) Add a DRACONIUM Fusion recipe that will use 50000 energy to output a Diamond when White Wool is used as a catalyst and a Stick and a piece of Dirt is used as an input. The Stick is not consumed when crafting, but the Dirt is consumed.
*/

<recipetype:draconicevolution:fusion_crafting>.addRecipe("consuming_wyvern", <item:minecraft:dirt>, <item:minecraft:glass>, 50000, TechLevel.WYVERN, [<item:minecraft:diamond>, <tag:items:minecraft:wool>]);
<recipetype:draconicevolution:fusion_crafting>.addRecipe("nonconsuming_draconium", <item:minecraft:diamond>, <item:minecraft:white_wool>, 50000, TechLevel.DRACONIUM, [FusionIngredient.of(<item:minecraft:stick>, false), FusionIngredient.of(<item:minecraft:dirt>, true)]);

// Removes the recipe for a Chaotic Capacitor.
<recipetype:draconicevolution:fusion_crafting>.removeRecipe(<item:draconicevolution:chaotic_capacitor>);

/*
    The following lines will do the following:
    1) Replaces all Fusion Recipes that use Draconium Ingots, making the Ingot not get consumed by the recipe.
    1) Replaces all Fusion Recipes that use a Netherite Ingot, replacing the Ingot with a Nether Star that will be consumed.
*/

Replacer.forEverything()
    .replaceFusion(FusionIngredient.of(<item:draconicevolution:draconium_ingot>, true), FusionIngredient.of(<item:draconicevolution:draconium_ingot>, false))
    .replaceFusion(<item:minecraft:netherite_ingot>, <item:minecraft:nether_star>)
    .execute();
