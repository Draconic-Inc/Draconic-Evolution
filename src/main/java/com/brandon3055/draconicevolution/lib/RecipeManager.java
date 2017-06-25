package com.brandon3055.draconicevolution.lib;

import com.brandon3055.brandonscore.handlers.FileHandler;
import com.brandon3055.brandonscore.lib.StackReference;
import com.brandon3055.brandonscore.registry.ModFeatureParser;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.api.OreDictHelper;
import com.brandon3055.draconicevolution.api.fusioncrafting.FusionRecipeAPI;
import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionRecipe;
import com.brandon3055.draconicevolution.api.fusioncrafting.SimpleFusionRecipe;
import com.brandon3055.draconicevolution.items.ToolUpgrade;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.brandon3055.draconicevolution.lib.RecipeManager.RecipeDifficulty.ALL;

/**
 * Created by brandon3055 on 20/07/2016.
 */
public class RecipeManager {

    public static final FusionRecipeRegistry FUSION_REGISTRY = new FusionRecipeRegistry();

    private static List<IRecipe> activeCrafting = new ArrayList<IRecipe>();
    private static List<IFusionRecipe> activeFusion = new ArrayList<IFusionRecipe>();

    //region Initialization

    /**
     * Creates the FusionRecipeRegistry and Initializes the FusionRecipeAPI
     */
    public static void initialize() {
        FusionRecipeAPI.registry = FUSION_REGISTRY;
    }

    /**
     * Loads all recipes from {@link DERecipes} If recipes have already been loaded
     * it will first remove all currently loaded recipes before reloading.
     */
    public static void loadRecipes() {
        if (DEConfig.hardMode) {
            LogHelper.info("Loading Hard Mode Recipes...");
        }
        else {
            LogHelper.info("Loading Default Recipes...");
        }

        if (!activeCrafting.isEmpty()) {
            for (IRecipe recipe : activeCrafting) {
                CraftingManager.getInstance().getRecipeList().remove(recipe);
            }
        }

        activeCrafting.clear();

        if (!activeFusion.isEmpty()) {
            for (IFusionRecipe recipe : activeFusion) {
                FUSION_REGISTRY.remove(recipe);
            }
        }

        activeFusion.clear();
        activeFusion.addAll(ToolUpgrade.createUpgradeRecipes());
        DERecipes.addRecipes();
        try {
            loadRecipesFromConfig();
        }
        catch (Exception e) {
            LogHelper.error("Something when wrong while attempting to load recipes from CustomFusionRecipes.json");
            e.printStackTrace();
        }
    }

    //endregion

    //region Config Recipes

    public static void loadRecipesFromConfig() throws Exception {
        File jsonFile = new File(FileHandler.brandon3055Folder, "CustomFusionRecipes.json");

        if (!jsonFile.exists()) {
            genInfoFile();
            return;
        }

        LogHelper.info("Loading custom fusion recipes...");

        List<IFusionRecipe> toAdd = new ArrayList<>();
        List<IFusionRecipe> toRemove = new ArrayList<>();
        int failedToAdd = 0;
        int failedToRemove = 0;

        JsonReader reader = new JsonReader(new FileReader(jsonFile));
        JsonArray jsonArray = new JsonParser().parse(reader).getAsJsonArray();

        for (JsonElement e : jsonArray) {
            try {
                JsonObject entry = e.getAsJsonObject();

                if (!validateEntry(entry)) {
                    LogHelper.error("Failed to load recipe entry!\n" + entry);
                    continue;
                }

                boolean add = entry.get("mode").getAsString().toUpperCase().equals("ADD");
                Object result = readStackEntry(entry.get("result").getAsString());
                Object catalyst = readStackEntry(entry.get("catalyst").getAsString());
                int energy = entry.has("energy") ? entry.get("energy").getAsInt() : 0;
                int tier = entry.has("tier") ? entry.get("tier").getAsInt() : 0;
                JsonArray ingredients = entry.has("ingredients") ? entry.get("ingredients").getAsJsonArray() : null;

                if (!validateRecipe(add, result, catalyst, energy, tier)) {
                    LogHelper.error("Failed to load recipe entry!\n" + entry);
                    if (add) failedToAdd++;
                    else failedToRemove++;
                    continue;
                }

                if (add) {
                    if (ingredients == null) {
                        LogHelper.error("No ingredients found for recipe! Catalyst:" + catalyst + " = Result:" + result);
                        failedToAdd++;
                        continue;
                    }

                    List<Object> ingredientObjs = new LinkedList<>();

                    for (JsonElement ing : ingredients) {
                        ingredientObjs.add(readStackEntry(ing.getAsString()));
                    }

                    toAdd.add(new SimpleFusionRecipe((ItemStack) result, (ItemStack) catalyst, energy, tier, ingredientObjs.toArray()));
                }
                else {
                    //region Remove
                    boolean found1 = false;
                    for (IFusionRecipe recipe : FUSION_REGISTRY.getRecipes()) {
                        if (recipe.getRecipeCatalyst() == null || !recipe.getRecipeCatalyst().isItemEqual((ItemStack) catalyst)) {
                            continue;
                        }
                        if (!recipe.getRecipeOutput((ItemStack) catalyst).isItemEqual((ItemStack) result)) {
                            continue;
                        }
                        if (ingredients != null) {
                            boolean isRecipeValid = true;
                            for (Object rIng : recipe.getRecipeIngredients()) {
                                boolean found = false;
                                for (Object tIng : recipe.getRecipeIngredients()) {
                                    ItemStack stack1 = OreDictHelper.resolveObject(rIng);
                                    ItemStack stack2 = OreDictHelper.resolveObject(tIng);
                                    if (stack1 != null && stack2 != null && stack1.isItemEqual(stack2)) {
                                        found = true;
                                        break;
                                    }
                                }
                                if (!found) {
                                    isRecipeValid = false;
                                    break;
                                }
                            }
                            if (!isRecipeValid) {
                                continue;
                            }
                        }
                        found1 = true;
                        toRemove.add(recipe);
                    }
                    if (!found1) {
                        LogHelper.warn("Did not find a recipe matching \"Catalyst:" + catalyst + ", Result:" + result + ", Ingredients:" + ingredients + "\" to remove.");
                        failedToRemove++;
                    }
                    //endregion
                }
            }
            catch (Exception exception) {
                LogHelper.error("An exception was thrown while reading a custom recipe!");
                exception.printStackTrace();
            }
        }

        reader.close();

//
//
//        reader.beginArray();
//        while (reader.hasNext()) {
//            reader.beginObject();
//            boolean failed = false;
//            boolean remove = false;
//            ItemStack result = null;
//            ItemStack catalyst = null;
//            int energy = 0;
//            int tier = 0;
//            List<Object> ingredients = null;
//
//            while (reader.hasNext()) {
//                String name = reader.nextName();
//                switch (name) {
//                    case "mode":
//                        remove = reader.nextString().toLowerCase().equals("remove");
//                        break;
//                    case "result":
//                        result = getStackOld(reader.nextString());
//                        break;
//                    case "catalyst":
//                        catalyst = getStackOld(reader.nextString());
//                        break;
//                    case "energy":
//                        energy = reader.nextInt();
//                        break;
//                    case "tier":
//                        tier = reader.nextInt();
//                        break;
//                    case "ingredients":
//                        reader.beginArray();
//                        ingredients = new ArrayList<>();
//                        while (reader.hasNext()) {
//                            String next = reader.nextString();
//                            Object o = getIngredientOld(next);
//                            if (o == null) {
//                                LogHelper.error("Failed to find ingredient! - " + next);
//                                failed = true;
//                            }
//                            else {
//                                ingredients.add(o);
//                            }
//                        }
//                        reader.endArray();
//                        break;
//                }
//            }
//
//            if (failed || catalyst == null || result == null) {
//                if (remove) {
//                    failedToRemove++;
//                }
//                else {
//                    failedToAdd++;
//                }
//                reader.endObject();
//                continue;
//            }
//
//            if (remove) {
//                boolean found1 = false;
//                for (IFusionRecipe recipe : FUSION_REGISTRY.getRecipes()) {
//                    if (recipe.getRecipeCatalyst() == null || !recipe.getRecipeCatalyst().isItemEqual(catalyst)) {
//                        continue;
//                    }
//                    if (!recipe.getRecipeOutput(catalyst).isItemEqual(result)) {
//                        continue;
//                    }
//                    if (ingredients != null) {
//                        boolean isRecipeValid = true;
//                        for (Object rIng : recipe.getRecipeIngredients()) {
//                            boolean found = false;
//                            for (Object tIng : recipe.getRecipeIngredients()) {
//                                ItemStack stack1 = OreDictHelper.resolveObject(rIng);
//                                ItemStack stack2 = OreDictHelper.resolveObject(tIng);
//                                if (stack1 != null && stack2 != null && stack1.isItemEqual(stack2)) {
//                                    found = true;
//                                    break;
//                                }
//                            }
//                            if (!found) {
//                                isRecipeValid = false;
//                                break;
//                            }
//                        }
//                        if (!isRecipeValid) {
//                            continue;
//                        }
//                    }
//                    found1 = true;
//                    toRemove.add(recipe);
//                }
//                if (!found1) {
//                    LogHelper.warn("Did not find a recipe matching \"Catalyst:" + catalyst + ", Result:" + result + ", Ingredients:" + ingredients + "\" to remove.");
//                    failedToRemove++;
//                }
//            }
//            else {
//                if (ingredients == null) {
//                    LogHelper.error("No ingredients found for recipe! Catalyst:" + catalyst + " = Result:" + result);
//                    failedToAdd++;
//                }
//
//                toAdd.add(new SimpleFusionRecipe(result, catalyst, energy, tier, ingredients.toArray()));
//            }
//
//            reader.endObject();
//        }
//        reader.endArray();
//        reader.close();

        for (IFusionRecipe recipe : toRemove) {
            activeFusion.remove(recipe);
            FUSION_REGISTRY.remove(recipe);
        }
        LogHelper.info("Successfully removed " + toRemove.size() + " Fusion Recipe(s)");

        for (IFusionRecipe recipe : toAdd) {
            activeFusion.add(recipe);
            FUSION_REGISTRY.add(recipe);
        }
        LogHelper.info("Successfully added " + toAdd.size() + " Fusion Recipe(s)");

        if (failedToRemove > 0) {
            LogHelper.warn("Failed to remove " + failedToRemove + " Fusion Recipe(s)");
        }
        if (failedToAdd > 0) {
            LogHelper.warn("Failed to add " + failedToAdd + " Fusion Recipe(s)");
        }
    }

    private static Object readStackEntry(String entry) {
        if (entry.startsWith("[") || entry.contains("x[")) {
            return getStackOld(entry);
        }

        if (entry.contains("ore:")) {
            return entry.substring(entry.indexOf(":") + 1);
        }

        StackReference stackReference = StackReference.fromString(entry);
        ItemStack stack;

        if (stackReference == null || (stack = stackReference.createStack()) == null) {
            LogHelper.dev("Could not find item stack! - " + entry);
            return null;
        }

        return stack;
    }

    private static boolean validateEntry(JsonObject entry) {
        if (!entry.has("mode")) {
            LogHelper.error("Detected invalid recipe entry! Entry is missing \"mode\"");
            LogHelper.error(entry);
            return false;
        }
        boolean add = entry.get("mode").getAsString().toUpperCase().equals("ADD");

        if (!entry.has("result")) {
            LogHelper.error("Detected invalid recipe entry! Entry is missing \"result\"");
            LogHelper.error(entry);
            return false;
        }

        if (!entry.has("catalyst")) {
            LogHelper.error("Detected invalid recipe entry! Entry is missing \"catalyst\"");
            LogHelper.error(entry);
            return false;
        }

        if (!entry.has("energy") && add) {
            LogHelper.error("Detected invalid recipe entry! Entry is missing \"energy\"");
            LogHelper.error(entry);
            return false;
        }

        if (!entry.has("tier") && add) {
            LogHelper.error("Detected invalid recipe entry! Entry is missing \"tier\"");
            LogHelper.error(entry);
            return false;
        }

        if (!entry.has("ingredients") && add) {
            LogHelper.error("Detected invalid recipe entry! Entry is missing \"ingredients\"");
            LogHelper.error(entry);
            return false;
        }

        return true;
    }

    private static boolean validateRecipe(boolean add, Object result, Object catalyst, int energy, int tier) {
        if (result == null) {
            LogHelper.error("Recipe result could not be found! Recipe will not be added/removed");
            return false;
        }
        else if (catalyst == null) {
            LogHelper.error("Recipe catalyst could not be found! Recipe will not be added/removed");
            return false;
        }
        else if (result instanceof String) {
            LogHelper.error("Recipe result does not support ore dictionary! Recipe will not be added/removed");
            return false;
        }
        else if (catalyst instanceof String) {
            LogHelper.error("Recipe catalyst does not support ore dictionary! Recipe will not be added/removed");
            return false;
        }
        else if (energy <= 0 && add) {
            LogHelper.error("Invalid energy value specified for recipe!");
            return false;
        }
        else if (tier < 0 && add) {
            LogHelper.error("Invalid tier specified for recipe! Tier must be >= 0");
            return false;
        }

        return true;
    }

    private static ItemStack getStackOld(String stackString) {
        ItemStack stack = OreDictHelper.resolveObject(getIngredientOld(stackString));
        if (stack.isEmpty()) {
            LogHelper.error("Could not find items stack: " + stackString);
        }

        return stack;
    }

    private static Object getIngredientOld(String stackString) {
        String rawString = stackString;
        int count = 1;
        int meta = 0;
        if (!rawString.contains("[") || !rawString.contains("]")) {
            LogHelper.error("Detected invalid formatting for stack string \"" + rawString + "\" (Missing \"[\" and or \"]\")");
            return null;
        }

        String itemString = rawString.substring(rawString.indexOf("[") + 1, rawString.lastIndexOf("]"));
        rawString = rawString.replace("[" + itemString + "]", "");

        if (rawString.contains(":")) {
            try {
                meta = Integer.parseInt(rawString.substring(rawString.indexOf(":") + 1));
            }
            catch (Exception e) {
                LogHelper.error("Could not read meta value for stack: " + stackString + " Error: " + e.getMessage());
                return null;
            }
        }

        if (rawString.toLowerCase().contains("x")) {
            try {
                count = Integer.parseInt(rawString.substring(0, rawString.indexOf("x")));
            }
            catch (Exception e) {
                LogHelper.error("Could not read meta value for stack: " + stackString + " Error: " + e.getMessage());
                return null;
            }
        }

        if (itemString.contains(":")) {
            if (Item.REGISTRY.containsKey(new ResourceLocation(itemString))) {
                return new ItemStack(Item.REGISTRY.getObject(new ResourceLocation(itemString)), count, meta);
            }
            else if (Block.REGISTRY.containsKey(new ResourceLocation(itemString))) {
                return new ItemStack(Block.REGISTRY.getObject(new ResourceLocation(itemString)), count, meta);
            }
            else {
                LogHelper.error("Could not find item or block - " + itemString);
                return null;
            }
        }
        else {
            return itemString;
        }
    }


    //Warning! This method will break your eyes if you look at it!
    private static void genInfoFile() {
        File file = new File(FileHandler.brandon3055Folder, "Custom Fusion Recipe Info.txt");
        if (file.exists()) {
            return;
        }

        LogHelper.info("Generating custom fusion recipe Documentation.");

        String infoText = "//The recipe format has recently been updated! Old recipes will still work but may be disabled in a future MC version.\n" + "\n" + "It is now possible to add custom fusion recipes and/or remove existing ones.\n" + "This feature is intended for Mod pack creators.\n" + "\n" + "Recipes are specified using json file which you will need to place in config/brandon3055 (The same folder you should have found this text document in)\n" + "The json MUST be named \"CustomFusionRecipes.json\" (Without the quotes)\n" + "\n" + "The following is an example of what the json file should look like (See further down for an explanation of what everything means)\n" + "\n" + "[\n" + "    {\n" + "        \"mode\": \"ADD\",\n" + "        \"result\": \"minecraft:beacon\",\n" + "        \"catalyst\": \"minecraft:nether_star\",\n" + "        \"energy\": 1000,\n" + "        \"tier\": 1,\n" + "        \"ingredients\": [\n" + "            \"minecraft:glass\",\n" + "            \"minecraft:glass\",\n" + "            \"minecraft:glass\",\n" + "            \"minecraft:glass\",\n" + "            \"minecraft:glass\",\n" + "            \"minecraft:obsidian\"\n" + "        ]\n" + "    },\n" + "    {\n" + "        \"mode\": \"ADD\",\n" + "        \"result\": \"minecraft:diamond_sword,1,0,{ench:[0:{lvl:5s,id:21s},1:{lvl:5s,id:20s},2:{lvl:5s,id:19s}]}\",\n" + "        \"catalyst\": \"minecraft:golden_apple,1,1\",\n" + "        \"energy\": 1000,\n" + "        \"tier\": 1,\n" + "        \"ingredients\": [\n" + "            \"ore:oreGold\",\n" + "            \"ore:blockGold\",\n" + "            \"ore:ingotGold\",\n" + "            \"minecraft:stone,1,0,{ench:[0:{lvl:5s,id:21s},1:{lvl:5s,id:20s},2:{lvl:5s,id:19s}]}\"\n" + "        ]\n" + "    },\n" + "    {\n" + "        \"mode\": \"REMOVE\",\n" + "        \"result\": \"draconicevolution:draconic_staff_of_power\",\n" + "        \"catalyst\": \"draconicevolution:draconic_pick\",\n" + "        \"ingredients\": [\n" + "            \"draconicevolution:draconic_ingot\",\n" + "            \"draconicevolution:draconic_ingot\",\n" + "            \"draconicevolution:draconic_ingot\",\n" + "            \"draconicevolution:draconic_ingot\",\n" + "            \"draconicevolution:draconic_ingot\",\n" + "            \"draconicevolution:draconic_shovel\",\n" + "            \"draconicevolution:draconic_sword\",\n" + "            \"draconicevolution:awakened_core\"\n" + "        ]\n" + "    },\n" + "    {\n" + "        \"mode\": \"REMOVE\",\n" + "        \"result\": \"draconicevolution:draconic_shovel\",\n" + "        \"catalyst\": \"draconicevolution:wyvern_shovel\"\n" + "    }    \n" + "]\n" + "\n" + "\n" + "The above json adds 2 recipes for vanilla items and removes 2 recipes from Draconic Evolution. It should be fairly clear how those examples work\n" + "The following explains the recipes in more detail\n" + "\n" + "# Adding Recipes #\n" + "\n" + "\"mode\":\n" + "- For adding recipes mode can be set to \"ADD\" or it can be left out and the recipe will default to the ADD function\n" + "\n" + "\"result\":\n" + "- This is the item stack that the recipe will create.\n" + "- This should be a specific item. It can not be an ore dictionary item.\n" + "\n" + "\"catalyst\":\n" + "- The catalyst is the item that gets combined with the ingredients to create the result. \n" + "- This should be a specific item. It can not be an ore dictionary item.\n" + "\n" + "\"energy\":\n" + "- This id the energy required for the crafting. Note that this number is multiplied by the number of ingredients.\n" + "Meaning if you specifies 1000 for a recipe that has 8 ingredients the total energy cost would be 8000RF.\n" + "\n" + "\"tier\":\n" + "- This is the crafting tier of this recipe 0 = basic, 1 = wyvern, 2 = awakened, 3 = chaotic\n" + "\n" + "\"ingredients\":\n" + "- This is a list of ingredients required for the recipe.\n" + "- These can be ore dictionary items.\n" + "\n" + "\n" + "# Removing Recipes #\n" + "\n" + "\"mode\":\n" + "- Mode must be set to \"REMOVE\" when removing a recipe\n" + "\n" + "When removing recipes you to not need to specify the tier or energy cost of the target recipe.\n" + "You can instead just specify the result, catalyst and ingredients of the target recipe.\n" + "Optionally if you leave out the ingredients it will remove any recipe matching the result and catalyst items.\n" + "\n" + "\n" + "## Item Stack Strings ##\n" + "\n" + "Example:      minecraft:wool,16,14     - This is 16 red wool\n" + "Ore Example:  ore:ingotCopper          - This is 1 copper ingots using the ore dictionary\n" + "Note: Ore entries do not support more that 1 stack size, Item damage or nbt\n" + "More on the ore dictionary http://www.minecraftuniversity.com/forge/ore_dictionary_list/\n" + "\n" + "The name used in the item/block registry name or an ore dictionary name. Both the number of items and the damage value are optional\n" + "\n" + "Examples:\n" + "name, stack size, meta, NBT\n" + "ore:stone\n" + "minecraft:stone         \n" + "minecraft:stone,64       \n" + "minecraft:stone,64,3 \n" + "minecraft:stone,64,3,{NBT}\n" + "\n" + "\n" + "### Notes ###\n" + "The order in which you add/remove recipes dose not matter when replacing recipes. \n" + "Meaning for example you can add a recipe for a DE item then remove the original recipe and it will not remove the one you jsut added.\n" + "\n" + "You can use ore dictionary items for the catalyst and result when adding recipes but the default fusion implementation dose not support that. \n" + "So they will be replaced with the first item stack from the ore dictionary that matches that name.";
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(infoText);
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //endregion

    //region Registration

    public static void addShaped(RecipeDifficulty difficulty, Block result, Object... recipe) {
        addShaped(difficulty, new ItemStack(result), recipe);
    }

    public static void addShaped(RecipeDifficulty difficulty, Item result, Object... recipe) {
        addShaped(difficulty, new ItemStack(result), recipe);
    }

    public static void addShaped(RecipeDifficulty difficulty, ItemStack result, Object... recipe) {
        if (difficulty != ALL && RecipeDifficulty.getDifficulty() != difficulty) {
            return;
        }

        boolean isOre = false;

        if (!isEnabled(result)) {
            return;
        }

        for (int i = 3; i < recipe.length; i++) {
            if (recipe[i] instanceof String) {
                isOre = true;
            }

            if (!isEnabled(recipe[i])) {
                return;
            }
        }

        if (isOre) {
            IRecipe iRecipe = new ShapedOreRecipe(result, recipe);
            activeCrafting.add(iRecipe);
            GameRegistry.addRecipe(iRecipe);
        }
        else {
            activeCrafting.add(GameRegistry.addShapedRecipe(result, recipe));
        }
    }

    public static void addShapeless(RecipeDifficulty difficulty, Block result, Object... recipe) {
        addShapeless(difficulty, new ItemStack(result), recipe);
    }

    public static void addShapeless(RecipeDifficulty difficulty, Item result, Object... recipe) {
        addShapeless(difficulty, new ItemStack(result), recipe);
    }

    public static void addShapeless(RecipeDifficulty difficulty, ItemStack result, Object... recipe) {
        if (difficulty != ALL && RecipeDifficulty.getDifficulty() != difficulty) {
            return;
        }

        boolean isOre = false;

        if (!isEnabled(result)) {
            return;
        }

        for (int i = 0; i < recipe.length; i++) {
            if (recipe[i] instanceof String) {
                isOre = true;
            }

            if (!isEnabled(recipe[i])) {
                return;
            }
        }

        if (isOre) {
            IRecipe iRecipe = new ShapelessOreRecipe(result, recipe);
            activeCrafting.add(iRecipe);
            GameRegistry.addRecipe(iRecipe);
        }
        else {
            List<ItemStack> list = new ArrayList<ItemStack>();

            for (Object object : recipe) {
                if (object instanceof ItemStack) {
                    list.add(((ItemStack) object).copy());
                }
                else if (object instanceof Item) {
                    list.add(new ItemStack((Item) object));
                }
                else {
                    if (!(object instanceof Block)) {
                        throw new IllegalArgumentException("Invalid shapeless recipe: unknown type " + object.getClass().getName() + "!");
                    }

                    list.add(new ItemStack((Block) object));
                }
            }

            IRecipe iRecipe = new ShapelessRecipes(result, list);
            activeCrafting.add(iRecipe);

            GameRegistry.addRecipe(iRecipe);
        }
    }

    public static void addFusion(RecipeDifficulty difficulty, ItemStack result, ItemStack catalyst, int energyCost, int craftingTier, Object... ingredients) {
        if (difficulty != ALL && RecipeDifficulty.getDifficulty() != difficulty) {
            return;
        }

        if (!isEnabled(result) || !isEnabled(catalyst)) {
            return;
        }

        for (Object ingredient : ingredients) {
            if (!isEnabled(OreDictHelper.resolveObject(ingredient))) {
                return;
            }
        }

        IFusionRecipe recipe = new SimpleFusionRecipe(result, catalyst, energyCost, craftingTier, ingredients);
        activeFusion.add(recipe);
        FUSION_REGISTRY.add(recipe);
    }

    public static void addFusionTool(RecipeDifficulty difficulty, ItemStack result, ItemStack catalyst, int energyCost, int craftingTier, Object... ingredients) {
        if (difficulty != ALL && RecipeDifficulty.getDifficulty() != difficulty) {
            return;
        }

        if (!isEnabled(result) || !isEnabled(catalyst)) {
            return;
        }

        for (Object ingredient : ingredients) {
            if (!isEnabled(OreDictHelper.resolveObject(ingredient))) {
                return;
            }
        }

        IFusionRecipe recipe = new ToolUpgradeRecipe(result, catalyst, energyCost, craftingTier, ingredients);
        activeFusion.add(recipe);
        FUSION_REGISTRY.add(recipe);
    }

    public static void addRecipe(IRecipe recipe) {
        activeCrafting.add(recipe);
        GameRegistry.addRecipe(recipe);
    }

    public static boolean isEnabled(Object stack) {
        if (stack instanceof ItemStack) {
            Item item = ((ItemStack) stack).getItem();
            Object o = item instanceof ItemBlock ? ((ItemBlock) item).getBlock() : item;
            return !ModFeatureParser.isFeature(o) || ModFeatureParser.isEnabled(o);
        }
        else if (stack instanceof Item || stack instanceof Block) {
            return !ModFeatureParser.isFeature(stack) || ModFeatureParser.isEnabled(stack);
        }
        return true;
    }

    public static enum RecipeDifficulty {
        ALL, NORMAL, HARD;

        public static RecipeDifficulty getDifficulty() {
            return DEConfig.hardMode ? HARD : NORMAL;
        }
    }

//endregion
}
