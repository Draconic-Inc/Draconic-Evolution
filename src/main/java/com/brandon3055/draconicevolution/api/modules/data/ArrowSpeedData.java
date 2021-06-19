package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.Map;

public class ArrowSpeedData implements ModuleData<ArrowSpeedData> {
    public final float arrowSpeed;

    public ArrowSpeedData(float arrowSpeed) {
        this.arrowSpeed = arrowSpeed;
    }

    public float getArrowSpeed() {
        return arrowSpeed;
    }

    @Override
    public ArrowSpeedData combine(ArrowSpeedData other) {
        return new ArrowSpeedData(arrowSpeed + other.arrowSpeed);
    }

    @Override
    public void addInformation(Map<ITextComponent, ITextComponent> map, @Nullable ModuleContext context, boolean stack) {
        map.put(new TranslationTextComponent("module.draconicevolution.arrow_speed.name"), new StringTextComponent("+" + (int)(arrowSpeed*100) + "%"));
    }
}
