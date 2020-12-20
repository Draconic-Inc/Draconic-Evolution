package com.brandon3055.draconicevolution.items.equipment;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.api.power.IOPStorageModifiable;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.FlightData;
import com.brandon3055.draconicevolution.api.modules.data.SpeedData;
import com.brandon3055.draconicevolution.api.modules.entities.FlightEntity;
import com.brandon3055.draconicevolution.init.EquipCfg;
import com.google.common.collect.Multimap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.List;

import static com.brandon3055.draconicevolution.api.capability.DECapabilities.MODULE_HOST_CAPABILITY;
import static com.brandon3055.draconicevolution.api.capability.DECapabilities.OP_STORAGE;

/**
 * Created by brandon3055 on 16/6/20
 */
public interface IModularArmor extends IModularItem {

    @Override
    default Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = IModularItem.super.getAttributeModifiers(slot, stack);
        return map;
    }

    @Override
    default void addModularItemInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        IModularItem.super.addModularItemInformation(stack, worldIn, tooltip, flagIn);
        if (DEConfig.armorSpeedLimit != -1 && MODULE_HOST_CAPABILITY != null && stack.getCapability(MODULE_HOST_CAPABILITY).isPresent()) {
            ModuleHost host = stack.getCapability(MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);
            SpeedData speed = host.getModuleData(ModuleTypes.SPEED);
            if (speed != null && speed.getSpeedMultiplier() > DEConfig.armorSpeedLimit) {
                tooltip.add(new StringTextComponent("Speed limit on this server is +" + (int) (DEConfig.armorSpeedLimit * 100) + "%").mergeStyle(TextFormatting.RED));
            }
        }
    }

    @Override
    default boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        LazyOptional<IOPStorage> power = stack.getCapability(OP_STORAGE);
        power.ifPresent(storage -> {
            if (storage.getOPStored() < 512) {
                Vector3d motion = entity.getMotion();
                entity.setMotion(motion.x * 0.95, motion.y > 0 ? motion.y * 0.95 : motion.y, motion.z * 0.95);

            } else if (storage instanceof IOPStorageModifiable) {
                int energy = EquipCfg.elytraFlightEnergy;
                if (entity.isSprinting()) {
                    ModuleHost host = stack.getCapability(MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);
                    FlightEntity module = (FlightEntity)host.getEntitiesByType(ModuleTypes.FLIGHT).findAny().orElse(null);
                    double flightSpeed = module == null ? 0 : module.getElytraBoost();
                    if (flightSpeed > 0) {
                        double speed = 1.5D * flightSpeed;
                        double accel = 0.01 * flightSpeed;
                        Vector3d look = entity.getLookVec();
                        Vector3d motion = entity.getMotion();
                        entity.setMotion(motion.add(
                                look.x * accel + (look.x * speed - motion.x) * accel,
                                look.y * accel + (look.y * speed - motion.y) * accel,
                                look.z * accel + (look.z * speed - motion.z) * accel
                        ));
                        energy += EquipCfg.getElytraEnergy(module.getModule().getModuleTechLevel()) * flightSpeed;
                    }
                }

                if (!entity.world.isRemote) {
                    ((IOPStorageModifiable) storage).modifyEnergyStored(-energy);
                }
            }
        });

        return true;
    }

    @Override
    default boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        ModuleHost host = stack.getCapability(MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);
        FlightEntity flight = host.getEntitiesByType(ModuleTypes.FLIGHT).map(e -> (FlightEntity) e).findAny().orElse(null);
        return flight != null && flight.getElytraEnabled();
    }
}
