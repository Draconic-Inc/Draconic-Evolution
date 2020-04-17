package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.api.power.OPStorage;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.brandonscore.inventory.TileItemStackHandler;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.CapabilityItemHandler;

import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.SYNC_TILE;

/**
 * Created by brandon3055 on 30/05/2016.
 */
public class TileEnergyInfuser extends TileBCore implements ITickableTileEntity {

    public final ManagedBool running = register(new ManagedBool("running", SYNC_TILE));
    public final ManagedBool charging = register(new ManagedBool("charging", SYNC_TILE));

    public float rotation = 0;
    public TileItemStackHandler itemHandler = new TileItemStackHandler(1);
    public OPStorage opStorage = new OPStorage(10000000);

    public TileEnergyInfuser() {
        super(DEContent.tile_energy_infuser);

        capManager.setManaged("inventory", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, itemHandler).syncTile().saveBoth();
        itemHandler.setStackValidator((integer, stack) -> EnergyUtils.getStorage(stack) != null);

        capManager.setManaged("energy", CapabilityOP.OP, opStorage).saveBoth().syncTile();
    }

    //region Function

    @Override
    public void tick() {
        if (world.isRemote) {
            spawnParticles();
        }
        else {
            super.tick();
            ItemStack stack = itemHandler.getStackInSlot(0);
            if (EnergyUtils.getStorage(stack) != null) {
                long maxAccept = EnergyUtils.insertEnergy(stack, opStorage.getMaxExtract(), true);
                running.set(maxAccept > 0);

                long transferred = opStorage.extractOP(EnergyUtils.insertEnergy(stack, Math.min(opStorage.getOPStored(), opStorage.getMaxExtract()), false), false);
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

    //endregion

    //region Render

    @OnlyIn(Dist.CLIENT)
    private void spawnParticles() {
        if (world.isRemote && running.get() && charging.get()) {
            for (int i = 0; i < 4; i++) {
                double rotation = (this.rotation / 180 * Math.PI) + (i * (Math.PI / 2));

                Vec3D spawn = new Vec3D(pos).add(0.5, 0.5, 0.5).add(Math.sin(rotation) * 0.33, 0.30, Math.cos(rotation) * 0.33);
                Vec3D target = new Vec3D(pos).add(0.5, 0.7, 0.5);
                //TODO particles
                //                BCEffectHandler.spawnFX(DEParticles.INFUSER, world, spawn, target, 0);

                double xRand = 0.4 + (world.rand.nextDouble() * 0.7);
                double yRand = world.rand.nextDouble() * 0.45;

                spawn = new Vec3D(pos).add(0.5, 0.45, 0.5).add(Math.sin(rotation) * xRand, yRand, Math.cos(rotation) * xRand);
//                BCEffectHandler.spawnFX(DEParticles.INFUSER, world, spawn, target, 1);
            }
        }
    }

    @Override
    public boolean hasFastRenderer() {
        return false;//ToDo Re write fancy renderer
    }

    //endregion
}
