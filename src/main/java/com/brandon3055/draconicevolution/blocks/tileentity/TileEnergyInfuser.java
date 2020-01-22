package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.api.power.OPStorage;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.client.particle.BCEffectHandler;
import com.brandon3055.brandonscore.inventory.TileItemStackHandler;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.draconicevolution.client.DEParticles;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by brandon3055 on 30/05/2016.
 */
public class TileEnergyInfuser extends TileBCore implements ITickable {

    public final ManagedBool running = register("running", new ManagedBool(false)).syncViaTile().finish();
    public final ManagedBool charging = register("charging", new ManagedBool(false)).syncViaTile().finish();

    public float rotation = 0;
    public TileItemStackHandler itemHandler;
    public OPStorage opStorage;

    public TileEnergyInfuser() {
<<<<<<< HEAD
        itemHandler = addItemHandlerCap(new TileItemStackHandler(1)).syncTile(true).getData();
        itemHandler.setStackValidator((integer, stack) -> EnergyUtils.getStorage(stack) != null);
        opStorage = addEnergyCap(new OPStorage(10000000)).syncTile(true).getData();
=======
        this.setCapacityAndTransfer(10000000, 10000000, 10000000);
        this.setInventorySize(1);
        setEnergySyncMode().syncViaContainer();
>>>>>>> parent of 9cd2c6a8... Implement Tile Data system changes.
    }

    //region Function

    @Override
    public void update() {
        if (world.isRemote) {
            spawnParticles();
        }
        else {
            super.update();
<<<<<<< HEAD
            ItemStack stack = itemHandler.getStackInSlot(0);
            if (EnergyUtils.getStorage(stack) != null) {
                long maxAccept = EnergyUtils.insertEnergy(stack, opStorage.getMaxExtract(), true);
                running.set(maxAccept > 0);

                long transferred = opStorage.extractOP(EnergyUtils.insertEnergy(stack, Math.min(opStorage.getOPStored(), opStorage.getMaxExtract()), false), false);
                charging.set(transferred > 0);
=======
            ItemStack stack = getStackInSlot(0);
            if (EnergyHelper.canReceiveEnergy(stack)) {

                int maxAccept = EnergyHelper.insertEnergy(stack, energyStorage.getMaxExtract(), true);
                running.value = maxAccept > 0;

                int transferred = energyStorage.extractEnergy(EnergyHelper.insertEnergy(stack, Math.min(energyStorage.getEnergyStored(), energyStorage.getMaxExtract()), false), false);
                charging.value = transferred > 0;
>>>>>>> parent of 9cd2c6a8... Implement Tile Data system changes.
            }
            else {
                running.value = charging.value = false;
            }
        }

        if (running.value) {
            rotation++;
        }
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
