package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.Map;

/**
 * Created by brandon3055 on 3/5/20.
 */
public class DamageData implements ModuleData<DamageData> {
    private final double damagePoints;

    public DamageData(double damagePoints) {
        this.damagePoints = damagePoints;
    }

    public double getDamagePoints() {
        return damagePoints;
    }

    @Override
    public DamageData combine(DamageData other) {
        return new DamageData(damagePoints + other.damagePoints);
    }

    @Override
    public void addInformation(Map<ITextComponent, ITextComponent> map, ModuleContext context) {
        map.put(new StringTextComponent("DamageModule"), new StringTextComponent("TODO"));
    }
}
