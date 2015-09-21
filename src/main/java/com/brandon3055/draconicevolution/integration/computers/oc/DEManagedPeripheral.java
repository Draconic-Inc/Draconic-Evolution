package com.brandon3055.draconicevolution.integration.computers.oc;

import com.brandon3055.draconicevolution.integration.computers.IDEPeripheral;
import li.cil.oc.api.driver.NamedBlock;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.ManagedPeripheral;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by brandon3055 on 22/9/2015.
 */
public class DEManagedPeripheral implements ManagedPeripheral, ManagedEnvironment, NamedBlock{
	private IDEPeripheral peripheral;

	public DEManagedPeripheral(IDEPeripheral peripheral){
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
		return "draconic_reactor";
	}

	@Override
	public int priority() {
		return 1;
	}

	//Unused Methods
	@Override public boolean canUpdate() { return false; }
	@Override public void update() {}
	@Override public Node node() {return null;}
	@Override public void onConnect(Node node) {}
	@Override public void onDisconnect(Node node) {}
	@Override public void onMessage(Message message) {}
	@Override public void load(NBTTagCompound nbtTagCompound) {}
	@Override public void save(NBTTagCompound nbtTagCompound) {}
}
