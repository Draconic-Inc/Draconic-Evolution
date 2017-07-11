package com.brandon3055.draconicevolution.items;

import com.brandon3055.brandonscore.items.ItemBCore;
import com.brandon3055.brandonscore.registry.Feature;
import com.brandon3055.brandonscore.registry.IRenderOverride;
import com.brandon3055.brandonscore.utils.InfoHelper;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.OreDictHelper;
import com.brandon3055.draconicevolution.api.fusioncrafting.FusionRecipeAPI;
import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionRecipe;
import com.brandon3055.draconicevolution.api.itemupgrade.FusionUpgradeRecipe;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by brandon3055 on 26/06/2016.
 */
public class ToolUpgrade extends ItemBCore implements IRenderOverride {

    public static final Map<Integer, String> ID_TO_NAME = new HashMap<Integer, String>();
    public static final Map<String, Integer> NAME_TO_ID = new HashMap<String, Integer>();
    public static final Map<String, Integer> NAME_MAX_LEVEL = new HashMap<String, Integer>();

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
    public static final List<IFusionRecipe> upgradeRecipes = new ArrayList<>();

    private static final int[] UPGRADE_COST = new int[]{32000, 512000, 32000000, 256000000};

    static {
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

    public ToolUpgrade() {
        setHasSubtypes(true);
    }

    private static void registerUpgrade(int id, String upgrade, int maxLevel) {
        ID_TO_NAME.put(id, upgrade);
        NAME_TO_ID.put(upgrade, id);
        NAME_MAX_LEVEL.put(upgrade, maxLevel);
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
            upgradeRecipes.add(recipe);
        }
    }

    public static Collection<IFusionRecipe> createUpgradeRecipes() {
        upgradeRecipes.clear();

        for (String upgrade : NAME_TO_ID.keySet()) {
            for (int i = 0; i < NAME_MAX_LEVEL.get(upgrade); i++) {
                registerRecipe(upgrade, i);
            }
        }

        return upgradeRecipes;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (isInCreativeTab(tab)) {
            for (Integer meta : ID_TO_NAME.keySet()) {
                subItems.add(new ItemStack(this, 1, meta));
            }
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + "." + ID_TO_NAME.get(stack.getItemDamage());
    }

    private int tick;

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced) {
        if (!InfoHelper.isShiftKeyDown()) {
            tooltip.add(I18n.format("upgrade.de.holdShiftForRecipes.info", TextFormatting.AQUA + "" + TextFormatting.ITALIC, TextFormatting.RESET + "" + TextFormatting.GRAY));
        }
        else if (ID_TO_NAME.containsKey(stack.getItemDamage()) && RECIPE_MAP.containsKey(ID_TO_NAME.get(stack.getItemDamage()))) {
            if (!InfoHelper.isCtrlKeyDown()) {
                tick = ClientEventHandler.elapsedTicks;
            }

            LinkedList<FusionUpgradeRecipe> recipes = RECIPE_MAP.get(ID_TO_NAME.get(stack.getItemDamage()));
            FusionUpgradeRecipe recipe = recipes.get(tick / 100 % recipes.size());
            tooltip.add(InfoHelper.ITC() + I18n.format("upgrade.de.level.info") + ": " + InfoHelper.HITC() + I18n.format("upgrade.level." + (recipe.getRecipeTier() + 1)) + TextFormatting.DARK_GRAY + " " + (5 - tick % 100 / 20));
            for (Object o : recipe.getRecipeIngredients()) {
                ItemStack ingredient = OreDictHelper.resolveObject(o);
                if (!ingredient.isEmpty()) {
                    tooltip.add("-" + ingredient.getDisplayName());
                }
            }

            tooltip.add(TextFormatting.BLUE + I18n.format("upgrade.de.holdCTRLToPause.info"));
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerRenderer(Feature feature) {
        for (Integer meta : ID_TO_NAME.keySet()) {
            String fullName = DraconicEvolution.MODID + ":" + feature.getName();
            String variant = "type=" + ID_TO_NAME.get(meta).toLowerCase();
            ModelLoader.setCustomModelResourceLocation(this, meta, new ModelResourceLocation(fullName, variant));
        }
    }

    @Override
    public boolean registerNormal(Feature feature) {
        return false;
    }

}
