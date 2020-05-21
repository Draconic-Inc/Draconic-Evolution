package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.lib.TechItemProps;
import com.brandon3055.draconicevolution.DraconicEvolution;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.IArmorMaterial;

/**
 * Created by brandon3055 on 21/5/20.
 */
public class ModularArmor extends ArmorItem {
    public ModularArmor(TechItemProps builder) {
        super(ArmorMaterial.DIAMOND, EquipmentSlotType.CHEST, builder);
        DraconicEvolution.proxy.registerToolRenderer(this);
    }
}
