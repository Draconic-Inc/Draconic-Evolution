package com.brandon3055.draconicevolution.items.equipment;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.modules.ModuleCategory;
import com.brandon3055.draconicevolution.api.modules.lib.ModularOPStorage;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleHostImpl;
import com.brandon3055.draconicevolution.init.EquipCfg;
import com.brandon3055.draconicevolution.init.ModuleCfg;
import com.brandon3055.draconicevolution.init.TechProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by brandon3055 on 21/5/20.
 */
public class ModularHoe extends HoeItem implements IModularTieredItem {
    private final TechLevel techLevel;
    private final DETier itemTier;

    public ModularHoe(DETier tier, TechProperties props) {
        super(tier, 0, 0, props);
        this.techLevel = props.getTechLevel();
        this.itemTier = (DETier) getTier();
    }

    @Override
    public TechLevel getTechLevel() {
        return techLevel;
    }

    @Override
    public DETier getItemTier() {
        return itemTier;
    }

    @Override
    public double getSwingSpeedMultiplier() {
        return EquipCfg.hoeSwingSpeedMultiplier;
    }

    @Override
    public double getDamageMultiplier() {
        return EquipCfg.hoeDamageMultiplier;
    }

    @Override
    public ModuleHostImpl createHost(ItemStack stack) {
        ModuleHostImpl host = new ModuleHostImpl(techLevel, ModuleCfg.toolWidth(techLevel), ModuleCfg.toolHeight(techLevel), "hoe", ModuleCfg.removeInvalidModules);
        host.addCategories(ModuleCategory.TOOL_HOE);
        return host;
    }

    @Nullable
    @Override
    public ModularOPStorage createOPStorage(ItemStack stack, ModuleHostImpl host) {
        return new ModularOPStorage(host, EquipCfg.getBaseToolEnergy(techLevel), EquipCfg.getBaseToolTransfer(techLevel));
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return IModularTieredItem.super.getDestroySpeed(stack, state);
    }

    @Override
    public float getBaseEfficiency() {
        return getTier().getSpeed();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        addModularItemInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return damageBarVisible(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return damageBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return damageBarColour(stack);
    }

    @Override
    public boolean canBeHurtBy(DamageSource source) {
        return source == DamageSource.OUT_OF_WORLD;
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (entity.getAge() >= 0) {
            entity.setExtendedLifetime();
        }
        return super.onEntityItemUpdate(stack, entity);
    }

    @Override
    public boolean isEnchantable(ItemStack p_41456_) {
        return true;
    }
}
