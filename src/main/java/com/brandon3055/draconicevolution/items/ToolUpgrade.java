package com.brandon3055.draconicevolution.items;

import com.brandon3055.brandonscore.config.Feature;
import com.brandon3055.brandonscore.config.ICustomRender;
import com.brandon3055.brandonscore.items.ItemBCore;
import com.brandon3055.brandonscore.utils.InfoHelper;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.OreDictHelper;
import com.brandon3055.draconicevolution.api.fusioncrafting.FusionRecipeAPI;
import com.brandon3055.draconicevolution.api.itemupgrade.FusionUpgradeRecipe;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by brandon3055 on 26/06/2016.
 */
public class ToolUpgrade extends ItemBCore implements ICustomRender {//TODO Make better textures for the upgrades! (Maby just a single texture with slightly different variants based on what it applies to)

    public static final Map<Integer, String> ID_TO_NAME = new HashMap<Integer, String>();
    public static final Map<String, Integer> NAME_TO_ID = new HashMap<String, Integer>();

    public static final String RF_CAPACITY = "rfCap";
    public static final String DIG_SPEED = "digSpeed";
    public static final String DIG_AOE = "digAOE";
    //    public static final String DIG_DEPTH       = "digDepth";
    public static final String ATTACK_DAMAGE = "attackDmg";
    public static final String ATTACK_AOE = "attackAOE";
    //    public static final String ATTACK_SPEED    = "attackSpeed";
    public static final String ARROW_DAMAGE = "arrowDmg";
    public static final String DRAW_SPEED = "drawSpeed";
    public static final String ARROW_SPEED = "arrowSpeed";
    public static final String SHIELD_CAPACITY = "shieldCap";
    public static final String SHIELD_RECOVERY = "shieldRec";
    public static final String MOVE_SPEED = "moveSpeed";
    public static final String JUMP_BOOST = "jumpBoost";

    public static final Map<String, LinkedList<FusionUpgradeRecipe>> RECIPE_MAP = new HashMap<String, LinkedList<FusionUpgradeRecipe>>();

    private static final int[] UPGRADE_COST = new int[]{32000, 512000, 32000000, 512000000};

    public ToolUpgrade() {
        setHasSubtypes(true);
    }

    private static void registerUpgrade(int id, String upgrade, int maxLevel) {
        ID_TO_NAME.put(id, upgrade);
        NAME_TO_ID.put(upgrade, id);

        for (int i = 0; i < maxLevel; i++) {
            registerRecipe(upgrade, i);
        }
    }

    private static void registerRecipe(String name, int level) {
        FusionUpgradeRecipe recipe = null;
        switch (level) {
            case 0:
                recipe = new FusionUpgradeRecipe(name, new ItemStack(DEFeatures.toolUpgrade, 1, NAME_TO_ID.get(name)), UPGRADE_COST[level], level, level + 1, Items.GOLDEN_APPLE, Items.GOLDEN_APPLE, "gemDiamond", "gemDiamond", Items.ENDER_EYE, Items.ENDER_EYE, DEFeatures.draconicCore);
                break;
            case 1:
                recipe = new FusionUpgradeRecipe(name, new ItemStack(DEFeatures.toolUpgrade, 1, NAME_TO_ID.get(name)), UPGRADE_COST[level], level, level + 1, Items.NETHER_STAR, Items.NETHER_STAR, DEFeatures.draconicCore, DEFeatures.draconicCore, Items.EMERALD, Items.EMERALD, DEFeatures.wyvernCore);
                break;
            case 2:
                recipe = new FusionUpgradeRecipe(name, new ItemStack(DEFeatures.toolUpgrade, 1, NAME_TO_ID.get(name)), UPGRADE_COST[level], level, level + 1, Items.NETHER_STAR, Items.NETHER_STAR, DEFeatures.wyvernCore, DEFeatures.wyvernCore, Blocks.EMERALD_BLOCK, Blocks.EMERALD_BLOCK, DEFeatures.awakenedCore);
                break;
            case 3:
                recipe = new FusionUpgradeRecipe(name, new ItemStack(DEFeatures.toolUpgrade, 1, NAME_TO_ID.get(name)), UPGRADE_COST[level], level, level + 1, DEFeatures.wyvernCore, DEFeatures.wyvernCore, DEFeatures.awakenedCore, DEFeatures.awakenedCore, Blocks.DRAGON_EGG, Blocks.DRAGON_EGG, DEFeatures.chaoticCore);
                break;
        }

        if (recipe != null) {
            if (!RECIPE_MAP.containsKey(name)) {
                RECIPE_MAP.put(name, new LinkedList<FusionUpgradeRecipe>());
            }

            RECIPE_MAP.get(name).add(recipe);
            FusionRecipeAPI.addRecipe(recipe);
        }
    }

    public static void addUpgrades() {
        registerUpgrade(0, RF_CAPACITY, 4);
        registerUpgrade(1, DIG_SPEED, 4);
        registerUpgrade(2, DIG_AOE, 4);
        //registerUpgrade(DIG_DEPTH);
        registerUpgrade(3, ATTACK_DAMAGE, 4);
        registerUpgrade(4, ATTACK_AOE, 4);
        //registerUpgrade(ATTACK_SPEED);
        registerUpgrade(5, ARROW_DAMAGE, 4);
        registerUpgrade(6, DRAW_SPEED, 3);
        registerUpgrade(7, ARROW_SPEED, 4);
        registerUpgrade(8, SHIELD_CAPACITY, 4);
        registerUpgrade(9, SHIELD_RECOVERY, 4);
        registerUpgrade(10, MOVE_SPEED, 4);
        registerUpgrade(11, JUMP_BOOST, 4);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems) {
        for (Integer meta : ID_TO_NAME.keySet()) {
            subItems.add(new ItemStack(item, 1, meta));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + "." + ID_TO_NAME.get(stack.getItemDamage());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        if (!InfoHelper.isShiftKeyDown()) {
            tooltip.add(I18n.format("upgrade.de.holdShiftForRecipes.info", TextFormatting.AQUA + "" + TextFormatting.ITALIC, TextFormatting.RESET + "" + TextFormatting.GRAY));
        }
        else if (ID_TO_NAME.containsKey(stack.getItemDamage()) && RECIPE_MAP.containsKey(ID_TO_NAME.get(stack.getItemDamage()))) {
            LinkedList<FusionUpgradeRecipe> recipes = RECIPE_MAP.get(ID_TO_NAME.get(stack.getItemDamage()));
            FusionUpgradeRecipe recipe = recipes.get(ClientEventHandler.elapsedTicks / 100 % recipes.size());
            tooltip.add(InfoHelper.ITC() + I18n.format("upgrade.de.level.info") + ": " + InfoHelper.HITC() + I18n.format("upgrade.level." + (recipe.getRecipeTier() + 1)) + TextFormatting.DARK_GRAY + " " + (5 - ClientEventHandler.elapsedTicks % 100 / 20));
            for (Object o : recipe.getRecipeIngredients()) {
                ItemStack ingredient = OreDictHelper.resolveObject(o);
                tooltip.add("-" + ingredient.getDisplayName());
            }
        }
    }

    @Override
    public void registerRenderer(Feature feature) {
        for (Integer meta : ID_TO_NAME.keySet()) {
            String fullName = DraconicEvolution.MODID.toLowerCase() + ":" + feature.registryName();
            String variant = "type=" + ID_TO_NAME.get(meta).toLowerCase();
            ModelLoader.setCustomModelResourceLocation(this, meta, new ModelResourceLocation(fullName, variant));
        }
    }

    @Override
    public boolean registerNormal(Feature feature) {
        return false;
    }

}
