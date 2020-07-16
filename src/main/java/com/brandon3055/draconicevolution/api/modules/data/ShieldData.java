package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.init.EquipCfg;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Map;

/**
 * Created by brandon3055 on 3/5/20.
 */
public class ShieldData implements ModuleData<ShieldData> {
    private final int shieldCapacity;
    private final double shieldRecharge; //Shield points per tick

    public ShieldData(int shieldCapacity, double shieldRecharge) {
        this.shieldCapacity = shieldCapacity;
        this.shieldRecharge = shieldRecharge;
    }

    public int getShieldCapacity() {
        return shieldCapacity;
    }

    public double getShieldRecharge() {
        return shieldRecharge;
    }

    @Override
    public ShieldData combine(ShieldData other) {
        return new ShieldData(shieldCapacity + other.shieldCapacity, shieldRecharge + other.shieldRecharge);
    }

    @Override
    public void addInformation(Map<ITextComponent, ITextComponent> map, ModuleContext context) {
        if (shieldCapacity > 0){
            map.put(new TranslationTextComponent("module.draconicevolution.shield_capacity.name"),
                    new TranslationTextComponent("module.draconicevolution.shield_capacity.value", shieldCapacity));
        }
        if (shieldRecharge > 0){
            map.put(new TranslationTextComponent("module.draconicevolution.shield_recharge.name"),
                    new TranslationTextComponent("module.draconicevolution.shield_recharge.value", ModuleData.round(shieldRecharge * 20, 10), ModuleData.round((shieldCapacity / shieldRecharge) / 20, 10), (int) (Math.max(shieldRecharge * EquipCfg.energyShieldChg, EquipCfg.energyShieldChg))));
        }
    }
}
