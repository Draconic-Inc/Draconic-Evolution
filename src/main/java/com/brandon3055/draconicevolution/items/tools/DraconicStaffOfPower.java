package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.lib.PairKV;
import com.brandon3055.draconicevolution.api.IReaperItem;
import com.brandon3055.draconicevolution.api.itemconfig_dep.DoubleConfigField;
import com.brandon3055.draconicevolution.api.itemconfig_dep.IItemConfigField;
import com.brandon3055.draconicevolution.api.itemconfig_dep.ItemConfigFieldRegistry;
import com.brandon3055.draconicevolution.api.itemconfig_dep.ToolConfigHelper;
import com.brandon3055.draconicevolution.api.itemupgrade_dep.UpgradeHelper;
import com.brandon3055.draconicevolution.items.ToolUpgrade;
import com.brandon3055.draconicevolution.client.DETextures;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


import java.util.List;


/**
 * Created by brandon3055 on 5/06/2016.
 */
public class DraconicStaffOfPower extends MiningToolBase implements IAOEWeapon, IReaperItem {
    public DraconicStaffOfPower(Properties properties) {
        super(properties, MiningToolBase.PICKAXE_OVERRIDES);
    }

    //    public DraconicStaffOfPower() {
//        super(/*ToolStats.DRA_STAFF_ATTACK_DAMAGE, ToolStats.DRA_STAFF_ATTACK_SPEED, */PICKAXE_OVERRIDES);
////        this.baseMiningSpeed = (float) ToolStats.DRA_STAFF_MINING_SPEED;
////        this.baseAOE = ToolStats.BASE_DRACONIC_MINING_AOE + 1;
////        setEnergyStats(ToolStats.DRACONIC_BASE_CAPACITY * 3, 16000000, 0);
////        this.setHarvestLevel("all", 10);
//        this.setHarvestLevel("pickaxe", 10);
//        this.setHarvestLevel("axe", 10);
//        this.setHarvestLevel("shovel", 10);
//    }

    @Override
    public double getBaseMinSpeedConfig() {
        return ToolStats.DRA_STAFF_MINING_SPEED;
    }

    @Override
    public double getBaseAttackSpeedConfig() {
        return ToolStats.DRA_STAFF_ATTACK_SPEED;
    }

    @Override
    public double getBaseAttackDamageConfig() {
        return ToolStats.DRA_STAFF_ATTACK_DAMAGE;
    }

    @Override
    public int getBaseMinAOEConfig() {
        return ToolStats.BASE_DRACONIC_MINING_AOE + 1;
    }

    @Override
    public void loadEnergyStats() {
        setEnergyStats(ToolStats.DRACONIC_BASE_CAPACITY * 3, 16000000, 0);
    }

    @Override
    public List<String> getValidUpgrades(ItemStack stack) {
        List<String> list = super.getValidUpgrades(stack);
        list.add(ToolUpgrade.ATTACK_DAMAGE);
        list.add(ToolUpgrade.ATTACK_AOE);
        return list;
    }

    @Override
    public int getMaxUpgradeLevel(ItemStack stack, String upgrade) {
        return 3;
    }

    @Override
    public int getToolTier(ItemStack stack) {
        return 2;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (getDisabledEnchants(stack).containsKey(enchantment)) {
            return false;
        }
        return super.canApplyAtEnchantingTable(stack, enchantment) || enchantment.type == EnchantmentType.WEAPON;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
        return super.onBlockStartBreak(itemstack, pos, player);
    }

    //region Attack Stats

    @Override
    public double getMaxWeaponAOE(ItemStack stack) {
        int level = UpgradeHelper.getUpgradeLevel(stack, ToolUpgrade.ATTACK_AOE);
        if (level == 0) return 2;
        else if (level == 1) return 3;
        else if (level == 2) return 5;
        else if (level == 3) return 8;
        else if (level == 4) return 15;
        else return 0;
    }

    @Override
    public ItemConfigFieldRegistry getFields(ItemStack stack, ItemConfigFieldRegistry registry) {
        registry.register(stack, new DoubleConfigField("attackAOE", getMaxWeaponAOE(stack), 0, getMaxWeaponAOE(stack), "config.field.attackAOE.description", IItemConfigField.EnumControlType.SLIDER));
        return super.getFields(stack, registry);
    }

    @Override
    public double getWeaponAOE(ItemStack stack) {
        return ToolConfigHelper.getDoubleField("attackAOE", stack);
    }

    @Override
    public void setWeaponAOE(ItemStack stack, double value) {
        ToolConfigHelper.setDoubleField("attackAOE", stack, value);
    }

    //endregion

    @Override
    public int getReaperLevel(ItemStack stack) {
        return 3;
    }

    //region Rendering

//    @Override
//    public void registerRenderer(Feature feature) {
//        super.registerRenderer(feature);
//        ToolOverrideList.putOverride(this, DraconicStaffOfPower::handleTransforms);
//    }

//    @OnlyIn(Dist.CLIENT)//Avoids synthetic lambda creation booping the classloader on the server.
//    private static IModelState handleTransforms(TransformType transformType, IModelState state) {
//        return transformType == TransformType.FIXED || transformType == TransformType.GROUND ? STAFF_STATE : state;
//    }

    @Override
    public PairKV<TextureAtlasSprite, ResourceLocation> getModels(ItemStack stack) {
        return new PairKV<>(DETextures.DRACONIC_STAFF_OF_POWER, new ResourceLocation("draconicevolution", "models/item/tools/draconic_staff_of_power.obj"));
    }

    //endregion
}
