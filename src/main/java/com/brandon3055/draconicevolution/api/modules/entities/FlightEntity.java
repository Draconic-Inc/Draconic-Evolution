package com.brandon3055.draconicevolution.api.modules.entities;

import com.brandon3055.draconicevolution.api.config.BooleanProperty;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.config.ConfigProperty.DecimalFormatter;
import com.brandon3055.draconicevolution.api.config.DecimalProperty;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.FlightData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public class FlightEntity extends ModuleEntity {

    private BooleanProperty elytraEnabled = null;
    private BooleanProperty creativeEnabled = null;
    private DecimalProperty elytraBoost = null;

    public FlightEntity(Module<FlightData> module) {
        super(module);
        if (module.getData().elytra()) {
            addProperty(elytraEnabled = new BooleanProperty("flight_mod.elytra", true).setFormatter(ConfigProperty.BooleanFormatter.ENABLED_DISABLED));
            addProperty(elytraBoost = new DecimalProperty("flight_mod.elytra_boost", module.getData().getElytraSpeed()).setFormatter(DecimalFormatter.PERCENT_0).range(0, module.getData().getElytraSpeed()));
        }
        if (module.getData().creative()) {
            addProperty(creativeEnabled = new BooleanProperty("flight_mod.creative", true).setFormatter(ConfigProperty.BooleanFormatter.ENABLED_DISABLED));
        }
        this.savePropertiesToItem = true;
    }

    @Override
    public void onInstalled(ModuleContext context) {}

    public boolean getElytraEnabled() {
        return elytraEnabled != null && elytraEnabled.getValue();
    }

    public boolean getCreativeEnabled() {
        return creativeEnabled != null && creativeEnabled.getValue();
    }

    public double getElytraBoost() {
        return elytraBoost != null ? elytraBoost.getValue() : 0;
    }

    public static UUID FLIGHT_UUID = UUID.fromString("415af318-505f-4fa2-a2d8-c432373dc600");
    private static AttributeModifier FLIGHT_MODIFIER = new AttributeModifier(FLIGHT_UUID, "Flight Module", 1, AttributeModifier.Operation.ADDITION);
    public static UUID FLIGHT_SPEED_UUID = UUID.fromString("d5775a32-4480-4f1f-b247-3942be930464");
    private static AttributeModifier SPEED_MODIFIER = new AttributeModifier(FLIGHT_SPEED_UUID, "Flight Speed Enchantment", 1, AttributeModifier.Operation.ADDITION);

    //PR Testing
    //Will need to clear the flight attribute in livingTick event then optionally re apply it here and along with my acceleration and power consumption code.
//    @Override
//    public void tick(ModuleContext context) {
//        if (context.getType() != ModuleContext.Type.ITEM_STACK || EffectiveSide.get().isClient()) {
//            return;
//        }
//        StackModuleContext ctx = (StackModuleContext) context;
//        LivingEntity entity = ctx.getEntity();
//
//        int flightEnchantmentLevel = 1;
//
//        ModifiableAttributeInstance fallFlyingAttribute = entity.getAttribute(ForgeMod.FALL_FLIGHT.get());
//        if (fallFlyingAttribute != null)
//        {
//            if (fallFlyingAttribute.getModifier(FLIGHT_UUID) != null)
//            {
//                fallFlyingAttribute.removeModifier(FLIGHT_UUID);
//            }
//            if(flightEnchantmentLevel > 0)
//            {
//                fallFlyingAttribute.addTransientModifier(FLIGHT_MODIFIER);
//            }
//        }
//
////        int flightSpeedEnchantmentLevel = EnchantmentHelper.getEnchantmentLevel(FLIGHT_SPEED_ENCHANTMENT.get(), entity);
//        ModifiableAttributeInstance fallFlyingSpeedAttribute = entity.getAttribute(ForgeMod.FALL_FLYING_SPEED.get());
//        if (fallFlyingSpeedAttribute != null)
//        {
////            if (fallFlyingSpeedAttribute.getModifier(FLIGHT_SPEED_UUID) != null)
////            {
////                fallFlyingSpeedAttribute.removeModifier(FLIGHT_SPEED_UUID);
////            }
////            if(flightSpeedEnchantmentLevel > 0)
////            {
////                fallFlyingSpeedAttribute.addTransientModifier(FlightEnchantment.ELYTRA_SPEED_ENCHANTMENT_MODIFIER);
////            }
//        }
//
//
//        //                double flightSpeed = 1; //flightModule == null ? 0 : flightModule.getElytraBoost();
////                if (flightSpeed > 0 && player.isFallFlying() && entity.isSprinting()) {
////                    double speed = 1.5D * flightSpeed;
////                    double accel = 0.01 * flightSpeed;
////                    Vector3d look = entity.getLookAngle();
////                    Vector3d motion = entity.getDeltaMovement();
////                    entity.setDeltaMovement(motion.add(
////                            look.x * accel + (look.x * speed - motion.x) * accel,
////                            look.y * accel + (look.y * speed - motion.y) * accel,
////                            look.z * accel + (look.z * speed - motion.z) * accel
////                    ));
////                }
//    }
}
