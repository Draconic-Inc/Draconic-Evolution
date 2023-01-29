package com.brandon3055.draconicevolution.common.tileentities;

import net.minecraft.tileentity.TileEntity;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.network.TileObjectPacket;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;

/**
 * Created by Brandon on 14/11/2014.
 */
public abstract class TileObjectSync extends TileEntity {

    /**
     * Sends a primitive to the client in the form of an object
     */
    public Object sendObjectToClient(byte dataType, int index, Object object) {
        return sendObjectToClient(
                dataType,
                index,
                object,
                new TargetPoint(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 64));
    }

    /**
     * Sends a primitive to the client in the form of an object
     */
    public Object sendObjectToClient(byte dataType, int index, Object object, TargetPoint point) {
        DraconicEvolution.network.sendToAllAround(new TileObjectPacket(this, dataType, index, object), point);
        return object;
    }

    public Object sendObjectToServer(byte dataType, int index, Object object) {
        DraconicEvolution.network.sendToServer(new TileObjectPacket(this, dataType, index, object));
        return object;
    }

    /**
     * Receives an object from the server
     */
    public void receiveObjectFromClient(int index, Object object) {}

    /**
     * Receives an object from the server
     */
    public void receiveObjectFromServer(int index, Object object) {}
}
