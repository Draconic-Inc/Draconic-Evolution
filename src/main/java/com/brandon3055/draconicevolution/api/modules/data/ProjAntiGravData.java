package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Map;

/**
 * Created by brandon3055 on 3/5/20.
 */
@Deprecated
public class ProjAntiGravData implements ModuleData<ProjAntiGravData> {
    private final float antiGrav;

    public ProjAntiGravData(float antiGrav) {
        this.antiGrav = antiGrav;
    }

    public float getAntiGrav() {
        return antiGrav > 1 ? 1 : antiGrav;
    }

    @Override
    public ProjAntiGravData combine(ProjAntiGravData other) {
        return new ProjAntiGravData(antiGrav + other.antiGrav);
    }

    @Override
    public void addInformation(Map<Component, Component> map, ModuleContext context, boolean stack) {
        map.put(new TranslatableComponent("module.draconicevolution.proj_grav_comp.name"), new TranslatableComponent("module.draconicevolution.proj_grav_comp.value", (int)(getAntiGrav() * 100)));
    }
}
