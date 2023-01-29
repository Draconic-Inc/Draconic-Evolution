package com.brandon3055.draconicevolution.integration.computers.cc;

import com.brandon3055.draconicevolution.integration.computers.IDEPeripheral;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

/**
 * Created by brandon3055 on 21/9/2015.
 */
public class CCAdapter implements IPeripheral {

    private IDEPeripheral peripheral;

    public CCAdapter(IDEPeripheral peripheral) {
        this.peripheral = peripheral;
    }

    @Override
    public String getType() {
        return peripheral.getName();
    }

    @Override
    public String[] getMethodNames() {
        return peripheral.getMethodNames();
    }

    @Override
    public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments)
            throws LuaException, InterruptedException {
        try {
            return peripheral.callMethod(peripheral.getMethodNames()[method], arguments);
        } catch (Exception e) {
            throw new LuaException(e.getMessage());
        }
    }

    @Override
    public void attach(IComputerAccess iComputerAccess) {}

    @Override
    public void detach(IComputerAccess iComputerAccess) {}

    @Override
    public boolean equals(IPeripheral iPeripheral) {
        return false;
    }
}
