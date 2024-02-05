package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.api.modules.lib.IDamageModifier;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by brandon3055 on 8/4/21
 */
public record DamageModData(IDamageModifier modifier) implements ModuleData<DamageModData> {

    @Override
    public DamageModData combine(DamageModData other) {
        return this;
    }

    @Override
    public void addInformation(Map<Component, Component> map, @Nullable ModuleContext context, boolean stack) {
        modifier.addInformation(map, context, stack);
    }
}
