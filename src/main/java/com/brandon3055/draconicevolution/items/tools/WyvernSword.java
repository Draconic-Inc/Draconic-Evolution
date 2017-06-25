package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.lib.Set3;
import com.brandon3055.draconicevolution.api.IReaperItem;
import com.brandon3055.draconicevolution.api.itemconfig.DoubleConfigField;
import com.brandon3055.draconicevolution.api.itemconfig.IItemConfigField;
import com.brandon3055.draconicevolution.api.itemconfig.ItemConfigFieldRegistry;
import com.brandon3055.draconicevolution.api.itemconfig.ToolConfigHelper;
import com.brandon3055.draconicevolution.api.itemupgrade.UpgradeHelper;
import com.brandon3055.draconicevolution.items.ToolUpgrade;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.math.BlockPos;

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
    protected Set3<String, String, String> getTextureLocations() {
        return Set3.of("items/tools/wyvern_sword", "items/tools/obj/wyvern_sword", "models/item/tools/wyvern_sword.obj");
    }

    //endregion
}
