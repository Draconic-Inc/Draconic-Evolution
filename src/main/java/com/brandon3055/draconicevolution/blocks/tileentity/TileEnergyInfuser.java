package com.brandon3055.draconicevolution.blocks.tileentity;

import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyReceiver;
import com.brandon3055.brandonscore.blocks.TileEnergyInventoryBase;
import com.brandon3055.brandonscore.client.particle.BCEffectHandler;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.draconicevolution.client.DEParticles;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by brandon3055 on 30/05/2016.
 */
public class TileEnergyInfuser extends TileEnergyInventoryBase implements IEnergyReceiver, ITickable {

    public final ManagedBool running = register("running", new ManagedBool(false)).syncViaTile().finish();
    public final ManagedBool charging = register("charging", new ManagedBool(false)).syncViaTile().finish();

    public float rotation = 0;

    public TileEnergyInfuser() {
        this.setCapacityAndTransfer(10000000, 10000000, 10000000);
        this.setInventorySize(1);
        setEnergySyncMode().syncViaContainer();
    }

    //region Function

    @Override
    public void update() {
        if (world.isRemote) {
            spawnParticles();
        }
        else {
            super.update();
            ItemStack stack = getStackInSlot(0);
            if (stack.getItem() instanceof IEnergyContainerItem) {
                IEnergyContainerItem item = (IEnergyContainerItem) stack.getItem();

                int maxAccept = item.receiveEnergy(stack, energyStorage.getMaxExtract(), true);
                running.value = maxAccept > 0;

                int transfer = energyStorage.extractEnergy(item.receiveEnergy(stack, Math.min(energyStorage.getEnergyStored(), energyStorage.getMaxExtract()), false), false);
                charging.value = transfer > 0;
            }
            else {
                running.value = charging.value = false;
            }
        }

        if (running.value) {
            rotation++;
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return stack.getItem() instanceof IEnergyContainerItem;
    }

    //endregion

    //region Render

    @SideOnly(Side.CLIENT)
    private void spawnParticles() {
        if (world.isRemote && running.value && charging.value) {
            for (int i = 0; i < 4; i++) {
                double rotation = (this.rotation / 180 * Math.PI) + (i * (Math.PI / 2));

                Vec3D spawn = new Vec3D(pos).add(0.5, 0.5, 0.5).add(Math.sin(rotation) * 0.33, 0.30, Math.cos(rotation) * 0.33);
                Vec3D target = new Vec3D(pos).add(0.5, 0.7, 0.5);
                BCEffectHandler.spawnFX(DEParticles.INFUSER, world, spawn, target, 0);

                double xRand = 0.4 + (world.rand.nextDouble() * 0.7);
                double yRand = world.rand.nextDouble() * 0.45;

                spawn = new Vec3D(pos).add(0.5, 0.45, 0.5).add(Math.sin(rotation) * xRand, yRand, Math.cos(rotation) * xRand);
                BCEffectHandler.spawnFX(DEParticles.INFUSER, world, spawn, target, 1);
            }
        }
    }

    @Override
    public boolean hasFastRenderer() {
        return false;//ToDo Re write fancy renderer
    }

    //endregion
}
