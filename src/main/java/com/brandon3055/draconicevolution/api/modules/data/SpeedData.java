package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Map;

/**
 * Created by brandon3055 on 3/5/20.
 */
public class SpeedData implements ModuleData<SpeedData> {
    private final double speedMultiplier;

    public SpeedData(double speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    @Override
    public SpeedData combine(SpeedData other) {
        return new SpeedData(speedMultiplier + other.speedMultiplier);
    }

    @Override
    public void addInformation(Map<ITextComponent, ITextComponent> map, ModuleContext context) {
        map.put(new TranslationTextComponent("module.draconicevolution.speed.name"), new TranslationTextComponent("module.draconicevolution.speed.value", (int)(speedMultiplier * 100D)));
    }
}
