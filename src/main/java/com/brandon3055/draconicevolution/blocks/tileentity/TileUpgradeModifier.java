package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileInventoryBase;
import net.minecraft.util.ITickable;

/**
 * Created by brandon3055 on 9/06/2016.
 */
public class TileUpgradeModifier extends TileInventoryBase implements ITickable {
    public float rotation = 0;
    public float rotationSpeed = 0;
    public float targetSpeed = 0;

    public TileUpgradeModifier(){
        setInventorySize(1);
    }

    @Override
    public void update() {
        if (getStackInSlot(0) != null) targetSpeed = 5F;
        else targetSpeed = 0F;

        if (rotationSpeed < targetSpeed) rotationSpeed += 0.05F;
        else if (rotationSpeed > targetSpeed) rotationSpeed -= 0.05F;
        if (targetSpeed == 0 && rotationSpeed < 0) rotationSpeed = 0;
        rotation += rotationSpeed;
    }

    //region Inventory


    //endregion
}
