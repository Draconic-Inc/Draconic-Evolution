package com.brandon3055.draconicevolution.integration.computers;

/**
 * Created by brandon3055 on 21/9/2015.
 */
public interface IDEPeripheral {

    /**
     * Get the unique name for this peripheral type
     */
    public String getName();

    /**
     * Get a list of methods for this peripheral
     */
    public String[] getMethodNames();

    /**
     * Call a method
     */
    public Object[] callMethod(String method, Object... args);
}
