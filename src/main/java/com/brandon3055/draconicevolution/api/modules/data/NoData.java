package com.brandon3055.draconicevolution.api.modules.data;

/**
 * Created by brandon3055 on 18/4/20.
 * This is a 'blank' implementation of {@link ModuleData} Use this for basic modules that dont require any additional properties.
 */
public record NoData() implements ModuleData<NoData> {

    @Override
    public NoData combine(NoData other) {
        return new NoData();
    }
}
