package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.util.text.ITextComponent;

import java.util.Map;

/**
 * Created by brandon3055 on 18/4/20.
 * This is a 'blank' implementation of {@link ModuleData} Use this for basic modules that dont require any additional properties.
 */
public class NoData implements ModuleData<NoData> {

    public NoData() {}

    @Override
    public NoData combine(NoData other) {
        return new NoData(); //Technically this could return itself but all other combine functions return a new instance.
    }

    @Override
    public void addInformation(Map<ITextComponent, ITextComponent> map, ModuleContext context) {

    }
}
