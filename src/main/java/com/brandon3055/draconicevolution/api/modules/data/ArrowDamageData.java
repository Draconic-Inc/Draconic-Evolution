package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by Werechang on 13/6/21
 */

public class ArrowDamageData implements ModuleData<ArrowDamageData>{
    public final float arrowDamage;

    public ArrowDamageData(float arrowDamage) {
        this.arrowDamage = arrowDamage;
    }

    public double getArrowDamage() {
        return arrowDamage;
    }

    @Override
    public ArrowDamageData combine(ArrowDamageData other) {
        return new ArrowDamageData(arrowDamage + other.arrowDamage);
    }

    @Override
    public void addInformation(Map<ITextComponent, ITextComponent> map, @Nullable ModuleContext context, boolean stack) {
        map.put(new TranslationTextComponent("module.draconicevolution.arrow_damage.name"), new TranslationTextComponent("module.draconicevolution.arrow_damage.value", ModuleData.round(arrowDamage, 10)));
    }
}
