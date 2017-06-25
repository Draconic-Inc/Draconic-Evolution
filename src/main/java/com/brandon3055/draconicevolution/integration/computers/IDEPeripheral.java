package com.brandon3055.draconicevolution.integration.computers;

/**
 * Created by brandon3055 on 21/9/2015.
 */
public interface IDEPeripheral {

    /**
     * Get the unique name for this peripheral type
     */
    String getPeripheralName();

    /**
     * Get a list of methods for this peripheral
     */
    String[] getMethodNames();

    /**
     * Call a method
     */
    Object[] callMethod(String method, ArgHelper args);
}
