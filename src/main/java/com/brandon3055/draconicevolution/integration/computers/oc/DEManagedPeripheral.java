package com.brandon3055.draconicevolution.integration.computers.oc;


import com.brandon3055.draconicevolution.integration.computers.IDEPeripheral;
import li.cil.oc.api.Network;
import li.cil.oc.api.driver.NamedBlock;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedPeripheral;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.ManagedEnvironment;

/**
 * Created by brandon3055 on 22/9/2015.
 */
public class DEManagedPeripheral extends ManagedEnvironment implements ManagedPeripheral, NamedBlock {
	private IDEPeripheral peripheral;

	public DEManagedPeripheral(IDEPeripheral peripheral){
		this.peripheral = peripheral;
        this.setNode(Network.newNode(this, Visibility.Network).withComponent(peripheral.getPeripheralName(), Visibility.Network).create());
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
		return peripheral.getPeripheralName();
	}

	@Override
	public int priority() {
		return 10;
	}
}

