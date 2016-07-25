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
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.model.ModelLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by brandon3055 on 26/06/2016.
 */
public class ToolUpgrade extends ItemBCore implements ICustomRender {//TODO Make better textures for the upgrades! (Maby just a single texture with slightly different variants based on what it applies to)

    private static int highestMeta = 0;
    public static final Map<Integer, String> ID_TO_NAME = new HashMap<Integer, String>();
    public static final Map<String, Integer> NAME_TO_ID = new HashMap<String, Integer>();

    public static final String RF_CAPACITY     = "rfCap";
    public static final String DIG_SPEED       = "digSpeed";
    public static final String DIG_AOE         = "digAOE";
//    public static final String DIG_DEPTH       = "digDepth";
    public static final String ATTACK_DAMAGE   = "attackDmg";
    public static final String ATTACK_AOE      = "attackAOE";
//    public static final String ATTACK_SPEED    = "attackSpeed";
    public static final String ARROW_DAMAGE    = "arrowDmg";
    public static final String DRAW_SPEED      = "drawSpeed";
    public static final String ARROW_SPEED     = "arrowSpeed";
    public static final String SHIELD_CAPACITY = "shieldCap";
    public static final String SHIELD_RECOVERY = "shieldRec";
    public static final String MOVE_SPEED      = "moveSpeed";
    public static final String JUMP_BOOST      = "jumpBoost";

    private static final Object[][][] UPGRADE_RECIPES = new Object[][][] { //TODO Change the registry system to a map that is not based on the order in which things are added. So that if something is removed existing upgrades dont change.
            {{DEFeatures.draconicCore, Items.NETHER_STAR}, {DEFeatures.wyvernCore, DEFeatures.wyvernEnergyCore}, {DEFeatures.awakenedCore, DEFeatures.draconicEnergyCore, DEFeatures.wyvernCapacitor}, {DEFeatures.chaoticCore, DEFeatures.draconicEnergyCore, DEFeatures.draconicCapacitor}}, //RF_CAPACITY
            {{DEFeatures.draconicCore, Items.NETHER_STAR}, {DEFeatures.wyvernCore}, {DEFeatures.awakenedCore}, {DEFeatures.chaoticCore}},       //DIG_SPEED TODO Add more ingredients to recipes
            {{DEFeatures.draconicCore, Items.NETHER_STAR}, {DEFeatures.wyvernCore}, {DEFeatures.awakenedCore}, {DEFeatures.chaoticCore}},       //DIG_AOE
    //        {{DEFeatures.draconicCore, Items.NETHER_STAR}, {DEFeatures.wyvernCore}, {DEFeatures.draconicCore}, {DEFeatures.chaoticCore}},     //DIG_DEPTH
            {{DEFeatures.draconicCore, Items.NETHER_STAR}, {DEFeatures.wyvernCore}, {DEFeatures.awakenedCore}, {DEFeatures.chaoticCore}},       //ATTACK_DAMAGE
            {{DEFeatures.draconicCore, Items.NETHER_STAR}, {DEFeatures.wyvernCore}, {DEFeatures.awakenedCore}, {DEFeatures.chaoticCore}},       //ATTACK_AOE
    //        {{DEFeatures.draconicCore, Items.NETHER_STAR}, {DEFeatures.wyvernCore}, {DEFeatures.draconicCore}, {DEFeatures.chaoticCore}},     //ATTACK_SPEED
            {{DEFeatures.draconicCore, Items.NETHER_STAR}, {DEFeatures.wyvernCore}, {DEFeatures.awakenedCore}, {DEFeatures.chaoticCore}},       //ARROW_DAMAGE
            {{DEFeatures.draconicCore, Items.NETHER_STAR}, {DEFeatures.wyvernCore}, {DEFeatures.awakenedCore}},                                 //DRAW_SPEED
            {{DEFeatures.draconicCore, Items.NETHER_STAR}, {DEFeatures.wyvernCore}, {DEFeatures.awakenedCore}, {DEFeatures.chaoticCore}},       //ARROW_SPEED
            {{DEFeatures.draconicCore, Items.NETHER_STAR}, {DEFeatures.wyvernCore}, {DEFeatures.awakenedCore}, {DEFeatures.chaoticCore}},       //SHIELD_CAPACITY
            {{DEFeatures.draconicCore, Items.NETHER_STAR}, {DEFeatures.wyvernCore}, {DEFeatures.awakenedCore}, {DEFeatures.chaoticCore}},       //SHIELD_RECOVERY
            {{DEFeatures.draconicCore, Items.NETHER_STAR}, {DEFeatures.wyvernCore}, {DEFeatures.awakenedCore}, {DEFeatures.chaoticCore}},       //MOVE_SPEED
            {{DEFeatures.draconicCore, Items.NETHER_STAR}, {DEFeatures.wyvernCore}, {DEFeatures.awakenedCore}, {DEFeatures.chaoticCore}}        //JUMP_BOOST
    };

    private static final int[] UPGRADE_COST = new int[] {32000, 512000, 32000000, 512000000};

    static {
        registerUpgrade(RF_CAPACITY);
        registerUpgrade(DIG_SPEED);
        registerUpgrade(DIG_AOE);
        //registerUpgrade(DIG_DEPTH);
        registerUpgrade(ATTACK_DAMAGE);
        registerUpgrade(ATTACK_AOE);
        //registerUpgrade(ATTACK_SPEED);
        registerUpgrade(ARROW_DAMAGE);
        registerUpgrade(DRAW_SPEED);
        registerUpgrade(ARROW_SPEED);
        registerUpgrade(SHIELD_CAPACITY);
        registerUpgrade(SHIELD_RECOVERY);
        registerUpgrade(MOVE_SPEED);
        registerUpgrade(JUMP_BOOST);
    }

    public ToolUpgrade() {
        setHasSubtypes(true);
    }

    private static void registerUpgrade(String upgrade){
        ID_TO_NAME.put(highestMeta, upgrade);
        NAME_TO_ID.put(upgrade, highestMeta);
        highestMeta++;
    }

    public static void addUpgradeRecipes() {
        for (int id = 0; id < ID_TO_NAME.size(); id++){
            for (int level = 0; level < UPGRADE_RECIPES[id].length; level++){
                FusionRecipeAPI.addRecipe(new FusionUpgradeRecipe(ID_TO_NAME.get(id), new ItemStack(DEFeatures.toolUpgrade, 1, id), UPGRADE_COST[level], level, level + 1, UPGRADE_RECIPES[id][level]));
            }
        }
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems) {
        for (Integer meta : ID_TO_NAME.keySet()){
            subItems.add(new ItemStack(item, 1, meta));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + "." + ID_TO_NAME.get(stack.getItemDamage());
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        if (!InfoHelper.isShiftKeyDown()) {
            tooltip.add(I18n.format("upgrade.de.holdShiftForRecipes.info", TextFormatting.AQUA + "" + TextFormatting.ITALIC, TextFormatting.RESET + "" + TextFormatting.GRAY));
        }
        else if (stack.getItemDamage() >= 0 && stack.getItemDamage() < UPGRADE_RECIPES.length) {
            for (int level = 0; level < UPGRADE_RECIPES[stack.getItemDamage()].length; level++){
                tooltip.add(InfoHelper.ITC() + I18n.format("upgrade.de.level.info") + ": " + InfoHelper.HITC() + I18n.format("upgrade.level." + (level + 1)));
                for (int item = 0; item < UPGRADE_RECIPES[stack.getItemDamage()][level].length; item++){
                    ItemStack ingredient = OreDictHelper.resolveObject(UPGRADE_RECIPES[stack.getItemDamage()][level][item]);
                    tooltip.add("-" + ingredient.getDisplayName());
                }
            }
        }
    }

    @Override
    public void registerRenderer(Feature feature) {
        for (Integer meta : ID_TO_NAME.keySet()){
            String fullName = DraconicEvolution.MODID.toLowerCase() + ":" + feature.name();
            String variant = "type=" + ID_TO_NAME.get(meta).toLowerCase();
            ModelLoader.setCustomModelResourceLocation(this, meta, new ModelResourceLocation(fullName, variant));
        }
    }

    @Override
    public boolean registerNormal(Feature feature) {
        return false;
    }

}
