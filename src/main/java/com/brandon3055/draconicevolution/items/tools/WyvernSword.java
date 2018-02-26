package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.lib.PairKV;
import com.brandon3055.brandonscore.registry.Feature;
import com.brandon3055.draconicevolution.api.IReaperItem;
import com.brandon3055.draconicevolution.api.itemconfig.DoubleConfigField;
import com.brandon3055.draconicevolution.api.itemconfig.IItemConfigField;
import com.brandon3055.draconicevolution.api.itemconfig.ItemConfigFieldRegistry;
import com.brandon3055.draconicevolution.api.itemconfig.ToolConfigHelper;
import com.brandon3055.draconicevolution.api.itemupgrade.UpgradeHelper;
import com.brandon3055.draconicevolution.client.model.tool.ToolOverrideList;
import com.brandon3055.draconicevolution.client.model.tool.ToolTransforms;
import com.brandon3055.draconicevolution.items.ToolUpgrade;
import com.brandon3055.draconicevolution.utils.DETextures;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Created by brandon3055 on 2/06/2016.
 */
public class WyvernSword extends ToolBase implements IAOEWeapon, IReaperItem {

    public WyvernSword(double attackDamage, double attackSpeed) {
        super(attackDamage, attackSpeed);
    }

    public WyvernSword() {
        super(ToolStats.WYV_SWORD_ATTACK_DAMAGE, ToolStats.WYV_SWORD_ATTACK_SPEED);
        setEnergyStats(ToolStats.WYVERN_BASE_CAPACITY, 512000, 0);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        return 25F;
    }

    @Override
    public boolean canHarvestBlock(IBlockState state, ItemStack stack) {
        Material mat = state.getMaterial();
        return mat.isReplaceable() || mat == Material.WEB || mat == Material.CLOTH || mat == Material.CARPET || mat == Material.LEAVES || mat == Material.PLANTS;
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
        return 2;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment.type == EnumEnchantmentType.WEAPON || enchantment.type == EnumEnchantmentType.ALL;
    }

    @Override
    public int getToolTier(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
        boolean cancel = super.onBlockStartBreak(itemstack, pos, player) || player.capabilities.isCreativeMode;

        if (cancel && player instanceof EntityPlayerMP) {
            ((EntityPlayerMP) player).connection.sendPacket(new SPacketBlockChange(player.world, pos));
        }

        return cancel;
    }

    //region Attack Stats

    protected double getMaxAttackAOE(ItemStack stack) {
        int level = UpgradeHelper.getUpgradeLevel(stack, ToolUpgrade.ATTACK_AOE);
        if (level == 0) return 0;
        else if (level == 1) return 1;
        else if (level == 2) return 2;
        else if (level == 3) return 5;
        else if (level == 4) return 10;
        else return 0;
    }

    @Override
    public ItemConfigFieldRegistry getFields(ItemStack stack, ItemConfigFieldRegistry registry) {
        registry.register(stack, new DoubleConfigField("attackAOE", getMaxAttackAOE(stack), 0, getMaxAttackAOE(stack), "config.field.attackAOE.description", IItemConfigField.EnumControlType.SLIDER));
        return registry;
    }

    @Override
    public double getWeaponAOE(ItemStack stack) {
        return ToolConfigHelper.getDoubleField("attackAOE", stack);
    }

    @Override
    public int getReaperLevel(ItemStack stack) {
        return 1;
    }

    //endregion

    //region Rendering

    @Override
    public void registerRenderer(Feature feature) {
        super.registerRenderer(feature);
        ToolOverrideList.putOverride(this, WyvernSword::handleTransforms);
    }

    @SideOnly(Side.CLIENT)//Avoids synthetic lambda creation booping the classloader on the server.
    private static IModelState handleTransforms(TransformType transformType, IModelState state) {
        return transformType == TransformType.FIXED || transformType == TransformType.GROUND ? ToolTransforms.WY_SWORD_STATE : state;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public PairKV<TextureAtlasSprite, ResourceLocation> getModels(ItemStack stack) {
        return new PairKV<>(DETextures.WYVERN_SWORD, new ResourceLocation("draconicevolution", "models/item/tools/wyvern_sword.obj"));
    }

    //endregion
}
