package com.brandon3055.draconicevolution.api.modules.types;

import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleCategory;
import com.brandon3055.draconicevolution.api.modules.data.DamageData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;

import java.util.function.Function;

/**
 * Created by brandon3055 on 19/6/20
 */
public class DamageType extends ModuleTypeImpl<DamageData> {
    public DamageType(String name, int defaultWidth, int defaultHeight, Function<Module<DamageData>, ModuleEntity> entityFactory, ModuleCategory... categories) {
        super(name, defaultWidth, defaultHeight, entityFactory, categories);
    }

    public DamageType(String name, int defaultWidth, int defaultHeight, ModuleCategory... categories) {
        super(name, defaultWidth, defaultHeight, categories);
    }

//I would have like to do this this way but i dont want this to show as an "additional damage" modifier. I want it to modify the base damage.
//    private static UUID damageUUID = UUID.fromString("ada0a316-051d-47da-acc6-662b7f9b6b19");
//
//    @Override
//    public void getAttributeModifiers(@Nullable DamageData moduleData, EquipmentSlotType slot, ItemStack stack, Multimap<String, AttributeModifier> map) {
//        if (moduleData != null && slot == EquipmentSlotType.MAINHAND) {
//            map.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(damageUUID, "Tool modifier", moduleData.getDamagePoints(), AttributeModifier.Operation.ADDITION));
//        }
//    }
}
