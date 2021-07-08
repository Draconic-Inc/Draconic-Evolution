package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Map;

/**
 * Created by brandon3055 on 3/5/20.
 */
@Deprecated
public class ProjVelocityData implements ModuleData<ProjVelocityData> {
    private final float velocity;

    public ProjVelocityData(float velocity) {
        this.velocity = velocity;
    }

    public float getVelocity() {
        return velocity;
    }

    @Override
    public ProjVelocityData combine(ProjVelocityData other) {
        return new ProjVelocityData(velocity + other.velocity);
    }

    @Override
    public void addInformation(Map<ITextComponent, ITextComponent> map, ModuleContext context, boolean stack) {
        map.put(new TranslationTextComponent("module.draconicevolution.proj_velocity.name"), new TranslationTextComponent("module.draconicevolution.proj_velocity.value", (int)(getVelocity() * 100)));
    }
}
