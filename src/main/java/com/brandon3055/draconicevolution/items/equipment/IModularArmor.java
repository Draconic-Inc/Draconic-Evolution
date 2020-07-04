package com.brandon3055.draconicevolution.items.equipment;

import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import com.brandon3055.draconicevolution.api.modules.ModuleCategory;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.DamageData;
import com.brandon3055.draconicevolution.api.modules.data.SpeedData;
import com.google.common.collect.Multimap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

import static com.brandon3055.draconicevolution.api.capability.DECapabilities.MODULE_HOST_CAPABILITY;

/**
 * Created by brandon3055 on 16/6/20
 */
public interface IModularArmor extends IModularItem {

    UUID speedUUID = UUID.fromString("deff7521-d4f1-4def-af63-03b88505555f");

    @Override
    default Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        Multimap<String, AttributeModifier> map = IModularItem.super.getAttributeModifiers(slot, stack);

        if (MODULE_HOST_CAPABILITY != null && stack.getCapability(MODULE_HOST_CAPABILITY).isPresent()) {
            ModuleHost host = stack.getCapability(MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);
            if ((host.getModuleCategories().contains(ModuleCategory.CHESTPIECE) && slot == EquipmentSlotType.CHEST) || (host.getModuleCategories().contains(ModuleCategory.ARMOR_LEGS) && slot == EquipmentSlotType.LEGS)) {
                SpeedData speed = host.getModuleData(ModuleTypes.SPEED);
                if (speed != null) {
                    double speedBoost = speed.getSpeedMultiplier();
                    if (host instanceof PropertyProvider && ((PropertyProvider) host).hasDecimal("move_speed")) {
                        speedBoost = Math.min(speedBoost, ((PropertyProvider) host).getDecimal("move_speed").getValue());
                    }
                    if (DEConfig.armorSpeedLimit != -1) {
                        speedBoost = Math.min(speedBoost, DEConfig.armorSpeedLimit);
                    }
                    map.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), new AttributeModifier(speedUUID, "Armor modifier", speedBoost, AttributeModifier.Operation.MULTIPLY_BASE));
                }
            }
        }

        return map;
    }

    @Override
    default void addModularItemInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        IModularItem.super.addModularItemInformation(stack, worldIn, tooltip, flagIn);
        if (DEConfig.armorSpeedLimit != -1 && MODULE_HOST_CAPABILITY != null && stack.getCapability(MODULE_HOST_CAPABILITY).isPresent()) {
            ModuleHost host = stack.getCapability(MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);
            SpeedData speed = host.getModuleData(ModuleTypes.SPEED);
            if (speed != null && speed.getSpeedMultiplier() > DEConfig.armorSpeedLimit) {
                tooltip.add(new StringTextComponent("Speed limit on this server is +" + (int) (DEConfig.armorSpeedLimit * 100) + "%").applyTextStyle(TextFormatting.RED));
            }
        }
    }
}
