package com.brandon3055.draconicevolution.handlers;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.api.power.IOPStorageModifiable;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.JumpData;
import com.brandon3055.draconicevolution.api.modules.data.SpeedData;
import com.brandon3055.draconicevolution.api.modules.entities.FlightEntity;
import com.brandon3055.draconicevolution.api.modules.entities.LastStandEntity;
import com.brandon3055.draconicevolution.api.modules.entities.ShieldControlEntity;
import com.brandon3055.draconicevolution.init.EquipCfg;
import com.brandon3055.draconicevolution.items.equipment.IModularItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.thread.EffectiveSide;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Brandon on 13/11/2014.
 */
@Mod.EventBusSubscriber(modid = DraconicEvolution.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModularArmorEventHandler {
    private static final EquipmentSlotType[] ARMOR_SLOTS = new EquipmentSlotType[]{EquipmentSlotType.FEET, EquipmentSlotType.LEGS, EquipmentSlotType.CHEST, EquipmentSlotType.HEAD};

    public static final UUID WALK_SPEED_UUID = UUID.fromString("0ea6ce8e-d2e8-11e5-ab30-625662870761");
    private static final DamageSource KILL_COMMAND = new DamageSource("administrative.kill").setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute();
    public static Map<PlayerEntity, Boolean> playersWithFlight = new WeakHashMap<>();
    public static List<UUID> playersWithUphillStep = new ArrayList<>();

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onEntityAttacked(LivingAttackEvent event) {
        if (event.isCanceled() || event.getAmount() <= 0 || event.getEntityLiving().world.isRemote || event.getSource() == KILL_COMMAND) {
            return;
        }

        //Allows /kill to completely bypass all protections
        if (event.getAmount() == Float.MAX_VALUE && event.getSource() == DamageSource.OUT_OF_WORLD) {
            event.setCanceled(true);
            event.getEntityLiving().attackEntityFrom(KILL_COMMAND, Float.MAX_VALUE);
            return;
        }

        LivingEntity entity = event.getEntityLiving();
        ItemStack chestStack = entity.getItemStackFromSlot(EquipmentSlotType.CHEST);
        LazyOptional<ModuleHost> optionalHost = chestStack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY);

        if (chestStack.isEmpty() || !optionalHost.isPresent()) {
            return;
        }

        ModuleHost host = optionalHost.orElseThrow(IllegalStateException::new);
        ShieldControlEntity shieldControl = host.getEntitiesByType(ModuleTypes.SHIELD_CONTROLLER).map(e -> (ShieldControlEntity) e).findAny().orElse(null);
        if (shieldControl == null) {
            return;
        }

        shieldControl.tryBlockDamage(event);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onEntityDamaged(LivingDamageEvent event) {
        if (event.isCanceled() || event.getAmount() <= 0 || event.getEntityLiving().world.isRemote || event.getSource() == KILL_COMMAND) {
            return;
        }

        LivingEntity entity = event.getEntityLiving();
        ItemStack chestStack = entity.getItemStackFromSlot(EquipmentSlotType.CHEST);
        LazyOptional<ModuleHost> optionalHost = chestStack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY);

        if (chestStack.isEmpty() || !optionalHost.isPresent()) {
            return;
        }

        ModuleHost host = optionalHost.orElseThrow(IllegalStateException::new);
        ShieldControlEntity shieldControl = host.getEntitiesByType(ModuleTypes.SHIELD_CONTROLLER).map(e -> (ShieldControlEntity) e).findAny().orElse(null);
        if (shieldControl == null) {
            return;
        }

        shieldControl.tryBlockDamage(event);
    }

    @SubscribeEvent
    public static void onEntityFall(LivingFallEvent event) {
        LivingEntity entity = event.getEntityLiving();
        float jumpBoost = getJumpBoost(entity, true);
        if (jumpBoost > 0) {
            jumpBoost *= 2;
            event.setDistance(Math.max(0, event.getDistance() - jumpBoost));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityDeath(LivingDeathEvent event) {
        if (event.isCanceled() || event.getEntityLiving().world.isRemote) {
            return;
        }

        LivingEntity entity = event.getEntityLiving();
        List<LastStandEntity> lastStandModules = new ArrayList<>();

        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            NonNullList<ItemStack> stacks = player.inventory.mainInventory;
            for (int i = 0; i < stacks.size(); ++i) {
                getLastStandEntities(stacks.get(i), lastStandModules, player.inventory.currentItem == i ? EquipmentSlotType.MAINHAND : null);
            }
            for (EquipmentSlotType slot : ARMOR_SLOTS) {
                getLastStandEntities(player.inventory.armorInventory.get(slot.getIndex()), lastStandModules, slot);
            }
            for (ItemStack stack : player.inventory.offHandInventory) {
                getLastStandEntities(stack, lastStandModules, EquipmentSlotType.OFFHAND);
            }
        } else {
            for (EquipmentSlotType slot : EquipmentSlotType.values()) {
                getLastStandEntities(entity.getItemStackFromSlot(slot), lastStandModules, slot);
            }
        }

        if (lastStandModules.isEmpty() || event.getSource() == KILL_COMMAND) {
            return;
        }

        boolean blocked = lastStandModules.stream()
                .sorted(Comparator.comparing(e -> e.getModule().getModuleTechLevel().index))
                .anyMatch(e -> e.tryBlockDeath(event));

        if (blocked) {
            event.setCanceled(true);
        }
    }

    private static void getLastStandEntities(ItemStack stack, List<LastStandEntity> entities, EquipmentSlotType slot) {
        LazyOptional<ModuleHost> optional = stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY);
        if (!stack.isEmpty() && stack.getItem() instanceof IModularItem && ((IModularItem) stack.getItem()).isEquipped(stack, slot, false)) {
            optional.ifPresent(host -> {
                entities.addAll(host.getModuleEntities()
                        .stream()
                        .filter(e -> e instanceof LastStandEntity)
                        .map(e -> (LastStandEntity) e)
                        .collect(Collectors.toList())
                );
            });
        }
    }

    @SubscribeEvent
    public static void livingTick(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        ArmorAbilities armorAbilities = new ArmorAbilities();
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            NonNullList<ItemStack> stacks = player.inventory.mainInventory;
            for (int i = 0; i < stacks.size(); ++i) {
                tryTickStack(stacks.get(i), player, player.inventory.currentItem == i ? EquipmentSlotType.MAINHAND : null, armorAbilities);
            }
            for (EquipmentSlotType slot : ARMOR_SLOTS) {
                tryTickStack(player.inventory.armorInventory.get(slot.getIndex()), player, slot, armorAbilities);
            }
            for (ItemStack stack : player.inventory.offHandInventory) {
                tryTickStack(stack, player, EquipmentSlotType.OFFHAND, armorAbilities);
            }
        } else {
            for (EquipmentSlotType slot : EquipmentSlotType.values()) {
                tryTickStack(entity.getItemStackFromSlot(slot), entity, slot, armorAbilities);
            }
        }

        //region/*---------------- HillStep -----------------*/

        if (entity.world.isRemote) {
            ItemStack chestStack = entity.getItemStackFromSlot(EquipmentSlotType.CHEST);
            LazyOptional<ModuleHost> optional = chestStack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY);
            boolean hasHost = !chestStack.isEmpty() && optional.isPresent();
            boolean highStepListed = playersWithUphillStep.contains(entity.getUniqueID()) && entity.stepHeight >= 1f;
            boolean hasHighStep = hasHost && optional.orElseThrow(IllegalStateException::new).getEntitiesByType(ModuleTypes.HILL_STEP).findAny().isPresent();

            if (hasHighStep && !highStepListed) {
                playersWithUphillStep.add(entity.getUniqueID());
                entity.stepHeight = 1.0625f;
            }

            if (!hasHighStep && highStepListed) {
                playersWithUphillStep.remove(entity.getUniqueID());
                entity.stepHeight = 0.6F;
            }
        }

        //endregion

        //region/*---------------- Movement Speed ----------------*/

        IAttribute speedAttr = SharedMonsterAttributes.MOVEMENT_SPEED;
        double speedModifier = 0;
        if (armorAbilities.data != null) {
            speedModifier = armorAbilities.data.getSpeedMultiplier();
            if (entity.isSprinting() && armorAbilities.speedSettingRun != -1) {
                speedModifier = Math.min(speedModifier, armorAbilities.speedSettingRun);
            } else if (armorAbilities.speedSetting != -1) {
                speedModifier = Math.min(speedModifier, armorAbilities.speedSetting);
            }
        }

        AttributeModifier currentModifier = entity.getAttribute(speedAttr).getModifier(WALK_SPEED_UUID);
        if (speedModifier > 0) {
            if (currentModifier == null) {
                entity.getAttribute(speedAttr).applyModifier(new AttributeModifier(WALK_SPEED_UUID, speedAttr.getName(), speedModifier, AttributeModifier.Operation.MULTIPLY_BASE));
            } else if (currentModifier.getAmount() != speedModifier) {
                entity.getAttribute(speedAttr).removeModifier(currentModifier);
                entity.getAttribute(speedAttr).applyModifier(new AttributeModifier(WALK_SPEED_UUID, speedAttr.getName(), speedModifier, AttributeModifier.Operation.MULTIPLY_BASE));
            }

            if (!entity.onGround && entity.getRidingEntity() == null) {
                entity.jumpMovementFactor = 0.02F + (0.02F * (float) speedModifier);
            }
        } else {
            if (currentModifier != null) {
                entity.getAttribute(speedAttr).removeModifier(currentModifier);
            }
        }

        //endregion

        //region/*----------------- Flight ------------------*/

        if (entity instanceof PlayerEntity && !entity.world.isRemote) {
            PlayerEntity player = (PlayerEntity) entity;
            boolean canFly = true;
            boolean noPower = false;
            if (armorAbilities.creativeFlight && armorAbilities.flightPower != null && !player.abilities.isCreativeMode) {
                canFly = armorAbilities.flightPower.getOPStored() >= EquipCfg.creativeFlightEnergy;
                noPower = !canFly;
                if (canFly && player.abilities.isFlying) {
                    if (armorAbilities.flightPower instanceof IOPStorageModifiable) {
                        ((IOPStorageModifiable) armorAbilities.flightPower).modifyEnergyStored(-EquipCfg.creativeFlightEnergy);
                    } else {
                        armorAbilities.flightPower.extractOP(EquipCfg.creativeFlightEnergy, false);
                    }
                }
            }
            if (armorAbilities.creativeFlight && canFly) {
                if (!player.abilities.allowFlying) {
                    player.abilities.allowFlying = true;
                    player.sendPlayerAbilities();
                }
                playersWithFlight.put(player, true);
                //TODO do i want to do creative flight speed boost? I mean i kinda like the idea of creative as "precise" flight and elytra a "fast" flight.
            } else {
                if (!playersWithFlight.containsKey(player)) {
                    playersWithFlight.put(player, false);
                }

                if (playersWithFlight.get(player)) {
                    playersWithFlight.put(player, false);

                    if (!player.abilities.isCreativeMode) {
                        boolean wasFlying = player.abilities.isFlying;
                        player.abilities.allowFlying = false;
                        player.abilities.isFlying = false;
                        player.sendPlayerAbilities();
                        if (wasFlying && noPower) {
                            player.tryToStartFallFlying();
                        }
                    }
                }
            }
        }
        //endregion
    }

    private static float getJumpBoost(LivingEntity entity, boolean max) {
        ItemStack stack = entity.getItemStackFromSlot(EquipmentSlotType.CHEST);
        LazyOptional<ModuleHost> optional = stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY);
        if (optional.isPresent()) {
            ModuleHost host = optional.orElseThrow(IllegalStateException::new);
            JumpData jumpData = host.getModuleData(ModuleTypes.JUMP_BOOST);
            if (jumpData != null) {
                double jump = jumpData.getMultiplier();
                if (max) return (float) jump;
                if (entity.isSprinting()) {
                    if (host instanceof PropertyProvider && ((PropertyProvider) host).hasDecimal("de.module.jump_boost_run.prop")) {
                        jump = Math.min(jump, ((PropertyProvider) host).getDecimal("de.module.jump_boost_run.prop").getValue());
                    }
                } else {
                    if (host instanceof PropertyProvider && ((PropertyProvider) host).hasDecimal("de.module.jump_boost.prop")) {
                        jump = Math.min(jump, ((PropertyProvider) host).getDecimal("de.module.jump_boost.prop").getValue());
                    }
                }
                return (float) jump;
            }
        }
        return 0;
    }

    private static void tryTickStack(ItemStack stack, LivingEntity entity, EquipmentSlotType slot, ArmorAbilities abilities) {
        if (!stack.isEmpty() && stack.getItem() instanceof IModularItem) {
            ((IModularItem) stack.getItem()).handleTick(stack, entity, slot);

            if (slot != null && slot.getSlotType() == EquipmentSlotType.Group.ARMOR) {
                LazyOptional<ModuleHost> optional = stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY);
                optional.ifPresent(host -> {
                    gatherArmorProps(stack, host, entity, abilities);
                });
            }
        }
    }

    @SubscribeEvent
    public static void onLivingJumpEvent(LivingEvent.LivingJumpEvent event) {
        LivingEntity entity = event.getEntityLiving();
        float jumpBoost = getJumpBoost(entity, false);
        if (jumpBoost > 0) {
            entity.addVelocity(0, 0.1F * (jumpBoost + 1), 0);
        }
    }

    public static void gatherArmorProps(ItemStack stack, ModuleHost host, LivingEntity entity, ArmorAbilities abilities) {
        SpeedData speed = host.getModuleData(ModuleTypes.SPEED);
        if (speed != null) {
            abilities.addSpeedData(speed, host);
        }

        FlightEntity flight = host.getEntitiesByType(ModuleTypes.FLIGHT).map(e -> (FlightEntity) e).findAny().orElse(null);
        if (flight != null) {
            LazyOptional<IOPStorage> optional = stack.getCapability(DECapabilities.OP_STORAGE);
            abilities.addFlightData(flight, optional.isPresent() ? optional.orElseThrow(IllegalStateException::new) : null);
        }
    }

    private static class ArmorAbilities {
        private float speedSetting = -1;
        private float speedSettingRun = -1;
        private SpeedData data;
        private boolean elytraFlight = false;
        private boolean creativeFlight = false;
        private IOPStorage flightPower = null;

        private void addSpeedData(SpeedData data, ModuleHost host) {
            this.data = this.data == null ? data : this.data.combine(data);
            if (host instanceof PropertyProvider) {
                if (((PropertyProvider) host).hasDecimal("run_speed")) {
                    if (speedSettingRun == -1) speedSettingRun = 0;
                    speedSettingRun += ((PropertyProvider) host).getDecimal("run_speed").getValue();
                }
                if (((PropertyProvider) host).hasDecimal("walk_speed")) {
                    if (speedSetting == -1) speedSetting = 0;
                    speedSetting += ((PropertyProvider) host).getDecimal("walk_speed").getValue();
                }
            }
        }

        private void addFlightData(FlightEntity entity, IOPStorage flightPower) {
            elytraFlight = elytraFlight || entity.getElytraEnabled();
            creativeFlight = creativeFlight || entity.getCreativeEnabled();
            if (flightPower != null && (this.flightPower == null || flightPower.getOPStored() > this.flightPower.getOPStored())) {
                this.flightPower = flightPower;
            }
        }
    }
}
