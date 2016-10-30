package com.brandon3055.draconicevolution.integration.computers.oc;


import com.brandon3055.draconicevolution.api.IExtendedRFStorage;
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
public class IExtendedRFStoragePeripheral extends ManagedEnvironment implements ManagedPeripheral, NamedBlock {

	private IExtendedRFStorage peripheral;
	private final String NAME = "extended_rf_storage";

	public IExtendedRFStoragePeripheral(IExtendedRFStorage peripheral){
		this.peripheral = peripheral;
		this.setNode(Network.newNode(this, Visibility.Network).withComponent(NAME, Visibility.Network).create());
	}

	@Override
	public String[] methods() {
		return new String[] {"getEnergyStored", "getMaxEnergyStored"};
	}

	@Override
	public Object[] invoke(String method, Context context, Arguments args) throws Exception {
		if (method.equals("getEnergyStored")) {
			return new Object[] {peripheral.getExtendedStorage()};
		}
		else if (method.equals("getMaxEnergyStored")) {
			return new Object[] {peripheral.getExtendedCapacity()};
		}

		return new Object[0];
	}

	@Override
	public String preferredName() {
		return NAME;
	}

	@Override
	public int priority() {
		return 5;
	}
}

