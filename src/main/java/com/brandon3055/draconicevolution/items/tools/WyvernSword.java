package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.draconicevolution.api.itemconfig.*;
import com.brandon3055.draconicevolution.api.itemupgrade.UpgradeHelper;
import com.brandon3055.draconicevolution.items.ToolUpgrade;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by brandon3055 on 2/06/2016.
 */
public class WyvernSword extends ToolBase implements IAOEWeapon {

    public WyvernSword(double attackDamage, double attackSpeed) {
        super(attackDamage, attackSpeed);
    }

    public WyvernSword() {
        super(ToolStats.WYV_SWORD_ATTACK_DAMAGE, ToolStats.WYV_SWORD_ATTACK_SPEED);//TODO Attack Damage and speed
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
        return 1;
    }

    @Override
    public boolean checkEnchantTypeValid(EnumEnchantmentType type) {
        return type == EnumEnchantmentType.WEAPON || type == EnumEnchantmentType.ALL;
    }

    @Override
    public int getToolTier(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
        boolean cancel = super.onBlockStartBreak(itemstack, pos, player) || player.capabilities.isCreativeMode;

        if (cancel && player instanceof EntityPlayerMP) {
            ((EntityPlayerMP)player).connection.sendPacket(new SPacketBlockChange(player.worldObj, pos));
        }

        return cancel;
    }

    @Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        return super.onItemUseFirst(stack, player, world, pos, side, hitX, hitY, hitZ, hand);
    }

    //region Attack Stats

    protected double getMaxAttackAOE(ItemStack stack) {
        int level = UpgradeHelper.getUpgradeLevel(stack, ToolUpgrade.ATTACK_AOE);
        if (level == 0) return 1;
        else if (level == 1) return 2;
        else if (level == 2) return 3;
        else if (level == 3) return 5;
        else if (level == 4) return 10;
        else return 0;
    }

    @Override
    public ItemConfigFieldRegistry getFields(ItemStack stack, ItemConfigFieldRegistry registry) {
        registry.register(stack, new DoubleConfigField("attackAOE", getMaxAttackAOE(stack), 0, getMaxAttackAOE(stack), "config.field.attackAOE.description", IItemConfigField.EnumControlType.SLIDER));
        return super.getFields(stack, registry);
    }

    @Override
    public double getWeaponAOE(ItemStack stack) {
        return ToolConfigHelper.getDoubleField("attackAOE", stack);
    }

    //endregion
}
