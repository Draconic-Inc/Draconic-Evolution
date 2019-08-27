package com.brandon3055.draconicevolution.blocks.tileentity;

import cofh.redstoneflux.api.IEnergyContainerItem;
import cofh.redstoneflux.api.IEnergyReceiver;
import com.brandon3055.brandonscore.blocks.TileEnergyInventoryBase;
import com.brandon3055.brandonscore.client.particle.BCEffectHandler;
import com.brandon3055.brandonscore.lib.EnergyHelper;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.draconicevolution.client.DEParticles;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.SYNC_TILE;

/**
 * Created by brandon3055 on 30/05/2016.
 */
public class TileEnergyInfuser extends TileEnergyInventoryBase implements IEnergyReceiver, ITickable {

    public final ManagedBool running = register(new ManagedBool("running", SYNC_TILE));
    public final ManagedBool charging = register(new ManagedBool("charging", SYNC_TILE));

    public float rotation = 0;

    public TileEnergyInfuser() {
        this.setCapacityAndTransfer(10000000, 10000000, 10000000);
        this.setInventorySize(1);
        setEnergySyncMode().addFlags(SYNC_TILE);
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
            if (EnergyHelper.canReceiveEnergy(stack)) {

                int maxAccept = EnergyHelper.insertEnergy(stack, energyStorage.getMaxExtract(), true);
                running.set(maxAccept > 0);

                int transferred = energyStorage.extractEnergy(EnergyHelper.insertEnergy(stack, Math.min(energyStorage.getEnergyStored(), energyStorage.getMaxExtract()), false), false);
                charging.set(transferred > 0);
            }
            else {
                running.set(charging.set(false));
            }
        }

        if (running.get()) {
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
        if (world.isRemote && running.get() && charging.get()) {
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
