package com.brandon3055.draconicevolution.lib;

/**
 * Created by brandon3055 on 25/06/2025
 */
public class DumData<T> {
    public T value;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DumData<?>;
    }

    @Override
    public int hashCode() {
        return 123456789;
    }
}
