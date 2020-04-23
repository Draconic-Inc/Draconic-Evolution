package com.brandon3055.draconicevolution.api.modules.properties;

import com.brandon3055.draconicevolution.api.TechLevel;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

/**
 * Created by covers1624 on 4/16/20.
 */
public class DamageModuleProperties extends ModuleProperties<DamageModuleProperties> {
    private final float damagePoints;

    public DamageModuleProperties(TechLevel techLevel, int damagePoints, int width, int height) {
        super(techLevel, width, height);
        this.damagePoints = damagePoints;
    }

    public DamageModuleProperties(TechLevel techLevel, int damagePoints) {
        this(techLevel, damagePoints, 2, 2);
    }

    public float getDamagePoints() {
        return damagePoints;
    }

    @Override
    public void addCombinedStats(List<DamageModuleProperties> propertiesList, List<ITextComponent> combinedStats) {
        combinedStats.add(new StringTextComponent("//TODO Combined Damage stats"));
    }
}
