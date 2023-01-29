package com.brandon3055.draconicevolution.integration.computers.oc;

import li.cil.oc.api.driver.NamedBlock;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedPeripheral;
import li.cil.oc.api.prefab.ManagedEnvironment;

import com.brandon3055.draconicevolution.integration.computers.IDEPeripheral;

/**
 * Created by brandon3055 on 22/9/2015.
 */
public class DEManagedPeripheral extends ManagedEnvironment implements ManagedPeripheral, NamedBlock {

    private IDEPeripheral peripheral;

    public DEManagedPeripheral(IDEPeripheral peripheral) {
        this.peripheral = peripheral;
    }

    @Override
    public String[] methods() {
        return peripheral.getMethodNames();
    }

    @Override
    public Object[] invoke(String method, Context context, Arguments args) throws Exception {
        return peripheral.callMethod(method, args.toArray());
    }

    @Override
    public String preferredName() {
        return peripheral.getName();
    }

    @Override
    public int priority() {
        return 10;
    }
}
