package com.brandon3055.draconicevolution.items.equipment;

import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.DamageData;
import com.brandon3055.draconicevolution.api.modules.data.SpeedData;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import static com.brandon3055.draconicevolution.api.capability.DECapabilities.MODULE_HOST_CAPABILITY;

/**
 * Created by brandon3055 on 16/6/20
 * <p>
 * This is for any modular items that extend vanilla's {@link net.minecraft.item.TieredItem}
 */
public interface IModularTieredItem extends IModularItem {

    DEItemTier getItemTier();

    @Override
    default Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        Multimap<String, AttributeModifier> map = IModularItem.super.getAttributeModifiers(slot, stack);

        if (MODULE_HOST_CAPABILITY != null && stack.getCapability(MODULE_HOST_CAPABILITY).isPresent()) {
            DEItemTier tier = getItemTier();
            if (slot == EquipmentSlotType.MAINHAND) {
                ModuleHost host = stack.getCapability(MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);
                DamageData damage = host.getModuleData(ModuleTypes.DAMAGE, new DamageData(0));
                SpeedData speed = host.getModuleData(ModuleTypes.SPEED, new SpeedData(0));
                map.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(Item.ATTACK_DAMAGE_MODIFIER, "Tool modifier", (tier.getAttackDamage() - 1) + damage.getDamagePoints(), AttributeModifier.Operation.ADDITION));
                map.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(Item.ATTACK_SPEED_MODIFIER, "Tool modifier", tier.getAttackSpeed() / (1 + speed.getSpeedMultiplier()), AttributeModifier.Operation.ADDITION));
            }
        }

        return map;
    }
}
