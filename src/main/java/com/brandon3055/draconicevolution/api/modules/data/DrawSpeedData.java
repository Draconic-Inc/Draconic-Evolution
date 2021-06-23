package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by Werechang on 15/6/21
 */

public class DrawSpeedData implements ModuleData<DrawSpeedData> {
    public final float drawTimeReduction;

    public DrawSpeedData(float drawSpeed) {
        this.drawTimeReduction = drawSpeed;
    }

    public float getDrawTimeReduction() {
        return drawTimeReduction;
    }

    @Override
    public DrawSpeedData combine(DrawSpeedData other) {
        return new DrawSpeedData(drawTimeReduction + other.drawTimeReduction);
    }

    @Override
    public void addInformation(Map<ITextComponent, ITextComponent> map, @Nullable ModuleContext context, boolean stack) {
        map.put(new TranslationTextComponent("module.draconicevolution.draw_speed.name"), new StringTextComponent("+" + (int)(drawTimeReduction*100) + "%"));
    }
}
