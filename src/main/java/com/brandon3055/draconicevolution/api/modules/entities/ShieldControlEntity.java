package com.brandon3055.draconicevolution.api.modules.entities;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.api.power.IOPStorageModifiable;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.ShieldData;
import com.brandon3055.draconicevolution.api.modules.lib.*;
import com.brandon3055.draconicevolution.init.EquipCfg;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.thread.EffectiveSide;

/**
 * Created by brandon3055 on 7/7/20
 */
public class ShieldControlEntity extends ModuleEntity {

    private ShieldData shieldCache;
    private double shieldPoints;
    private int shieldCapacity;
    private int shieldCoolDown;

    public ShieldControlEntity(Module<?> module) {
        super(module);
    }

    @Override
    public void tick(ModuleContext context) {
        IOPStorageModifiable storage = context.getOpStorage();
        ShieldData data = getShieldData();

        if (context instanceof StackTickContext && EffectiveSide.get().isServer()) {
            StackTickContext tickContext = (StackTickContext) context;
            if (tickContext.getSlot() == EquipmentSlotType.CHEST) {
                shieldCapacity = data.getShieldCapacity();
                if (shieldPoints > shieldCapacity) {
                    shieldPoints = shieldCapacity;
                }
                if (shieldCoolDown > 0) {
                    shieldCoolDown--;
                    return;
                }
                if (shieldPoints < shieldCapacity && storage.getOPStored() > 0 && data.getShieldRecharge() > 0) {
                    double energyPerPoint = Math.max(data.getShieldRecharge() * EquipCfg.energyShieldChg, EquipCfg.energyShieldChg);
                    long extracted = storage.modifyEnergyStored(-(int) (Math.min(data.getShieldRecharge(), shieldCapacity - shieldPoints) * energyPerPoint));
                    shieldPoints += extracted / energyPerPoint;
                }
            }
        }
    }

    public double getShieldPoints() {
        return shieldPoints;
    }

    public int getShieldCapacity() {
        return shieldCapacity;
    }

    public int getShieldCoolDown() {
        return shieldCoolDown;
    }

    /**
     * Will check if this shield is able to completely absorb this damage event.
     * If so the event will be canceled and shield points will be consumed.
     * Note: partial damage blocking is handled by {@link #tryBlockDamage(LivingDamageEvent)}
     *
     * @param event The damage event.
     */
    public void tryBlockDamage(LivingAttackEvent event) {


    }

    /**
     * Called when the shield in unable to completely block a damage source.
     * This will calculate how much damage the shield can absorb and subtract that
     * amount from the damage event.
     *
     * @param event The damage event.
     */
    public void tryBlockDamage(LivingDamageEvent event) {


    }

    private ShieldData getShieldData() {
        if (shieldCache == null) {
            shieldCache = host.getModuleData(ModuleTypes.SHIELD_BOOST, new ShieldData(0, 0));
        }
        return shieldCache;
    }

    @Override
    public void clearCaches() {
        shieldCache = null;
    }

    @Override
    public void writeToItemStack(ItemStack stack, ModuleContext context) {
        super.writeToItemStack(stack, context);
    }

    @Override
    public void readFromItemStack(ItemStack stack, ModuleContext context) {
        super.readFromItemStack(stack, context);
    }

    @Override
    public void writeToNBT(CompoundNBT compound) {
        super.writeToNBT(compound);
        compound.putInt("cap", shieldCapacity);
        compound.putDouble("points", shieldPoints);
        compound.putShort("cooldwn", (short) shieldCoolDown);
    }

    @Override
    public void readFromNBT(CompoundNBT compound) {
        super.readFromNBT(compound);
        shieldCapacity = compound.getInt("cap");
        shieldPoints = compound.getDouble("points");
        shieldCoolDown = compound.getShort("cooldwn");
    }
}
