package com.brandon3055.draconicevolution.items.equipment;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.lib.TechItemProps;
import com.brandon3055.draconicevolution.api.IReaperItem;
import com.brandon3055.draconicevolution.api.modules.ModuleCategory;
import com.brandon3055.draconicevolution.api.modules.lib.ModularOPStorage;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleHostImpl;
import com.brandon3055.draconicevolution.init.EquipCfg;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static com.brandon3055.draconicevolution.init.ModuleCfg.*;

/**
 * Created by brandon3055 on 21/5/20.
 */
public class ModularStaff extends ToolItem implements IReaperItem, IModularMiningTool, IModularMelee {
    private final TechLevel techLevel;
    private final DEItemTier itemTier;

    public ModularStaff(TechItemProps props) {
        //noinspection unchecked
        super(0, 0, new DEItemTier(props, EquipCfg::getStaffDmgMult, EquipCfg::getStaffSpeedMult, EquipCfg::getStaffEffMult), Collections.EMPTY_SET, props.staffProps());
        this.techLevel = props.techLevel;
        this.itemTier = (DEItemTier) getTier();
    }

    @Override
    public TechLevel getTechLevel() {
        return techLevel;
    }

    @Override
    public DEItemTier getItemTier() {
        return itemTier;
    }

    @Override
    public ModuleHostImpl createHost(ItemStack stack) {
        ModuleHostImpl host = new ModuleHostImpl(techLevel, staffWidth(techLevel), staffHeight(techLevel), "staff", removeInvalidModules);
        host.addCategories(ModuleCategory.RANGED_WEAPON);
        return host;
    }

    @Nullable
    @Override
    public ModularOPStorage createOPStorage(ItemStack stack, ModuleHostImpl host) {
        return new ModularOPStorage(host, EquipCfg.getBaseStaffEnergy(techLevel), EquipCfg.getBaseStaffTransfer(techLevel));
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return IModularMiningTool.super.getDestroySpeed(stack, state);
    }

    @Override
    public float getBaseEfficiency() {
        return getTier().getEfficiency();
    }

    @Override
    public boolean overrideEffectivity(Material material) {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        addModularItemInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public int getReaperLevel(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        if (entity instanceof PlayerEntity) {
            ((PlayerEntity) entity).ticksSinceLastSwing = (int) Math.ceil(((PlayerEntity) entity).getCooledAttackStrength(0));
        }
        return true;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

}
