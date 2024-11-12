package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.brandonscore.utils.DataUtils;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.draconicevolution.api.IInvCharge;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import com.brandon3055.draconicevolution.api.config.BooleanProperty;
import com.brandon3055.draconicevolution.api.modules.ModuleCategory;
import com.brandon3055.draconicevolution.api.modules.lib.ModularOPStorage;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleHostImpl;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.init.EquipCfg;
import com.brandon3055.draconicevolution.init.ModuleCfg;
import com.brandon3055.draconicevolution.init.TechProperties;
import com.brandon3055.draconicevolution.integration.equipment.EquipmentManager;
import com.brandon3055.draconicevolution.items.equipment.DETier;
import com.brandon3055.draconicevolution.items.equipment.IModularEnergyItem;
import com.brandon3055.draconicevolution.items.equipment.IModularItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by brandon3055 on 31/05/2016.
 */
public class DraconiumCapacitor extends Item implements IInvCharge, IModularEnergyItem {
    private TechLevel techLevel;

    public DraconiumCapacitor(TechProperties properties) {
        super(properties);
        techLevel = properties.getTechLevel();
    }

    @Override
    public boolean canCharge(ItemStack stack, LivingEntity player, boolean isHeld) {
        return isHeld;
    }

    @Override
    public TechLevel getTechLevel() {
        return techLevel;
    }

    @Override
    public ModuleHost createHostCapForRegistration(ItemStack stack) {
        ModuleHost host = IModularEnergyItem.super.createHostCapForRegistration(stack);
        if (this == DEContent.CAPACITOR_CREATIVE.get()) {
            host.getModuleCategories().remove(ModuleCategory.ENERGY);
        }
        return host;
    }

    @Override
    public @NotNull ModuleHostImpl instantiateHost(ItemStack stack) {
        ModuleHostImpl host;
        if (this == DEContent.CAPACITOR_CREATIVE.get()) {
            host = new ModuleHostImpl(techLevel, 1, 1, "capacitor", ModuleCfg.removeInvalidModules);
        } else {
            host = new ModuleHostImpl(techLevel, ModuleCfg.capacitorWidth(techLevel), ModuleCfg.capacitorHeight(techLevel), "capacitor", ModuleCfg.removeInvalidModules);
        }
        host.addPropertyBuilder(props -> {
            props.add(new BooleanProperty("charge_held_item", false));
            props.add(new BooleanProperty("charge_armor", false));
            props.add(new BooleanProperty("charge_hot_bar", false));
            props.add(new BooleanProperty("charge_main", false));
            if (EquipmentManager.equipModLoaded()) {
                props.add(new BooleanProperty("charge_" + EquipmentManager.equipModID(), false));
            }
        });

        return host;
    }

    @Override
    public @NotNull ModularOPStorage instantiateOPStorage(ItemStack stack, Supplier<ModuleHost> hostSupplier) {
        if (this == DEContent.CAPACITOR_CREATIVE.get()) {
            return new ModularOPStorage(hostSupplier, Long.MAX_VALUE, Long.MAX_VALUE) {
                @Override
                public long getOPStored() {
                    return Long.MAX_VALUE / 2;
                }

                @Override
                public long receiveOP(long maxReceive, boolean simulate) {
                    return maxReceive;
                }

                @Override
                public long extractOP(long maxExtract, boolean simulate) {
                    return maxExtract;
                }

                @Override
                public boolean canExtract() {
                    return true;
                }
            }.setIOMode(true, true);
        }
        return new ModularOPStorage(hostSupplier, EquipCfg.getBaseCapEnergy(techLevel), EquipCfg.getBaseCapTransfer(techLevel)).setIOMode(true, true);
    }

    @Override
    public void handleTick(ItemStack stack, LivingEntity entity, @Nullable EquipmentSlot slot, boolean inEquipModSlot) {
        IModularEnergyItem.super.handleTick(stack, entity, slot, inEquipModSlot);

        ArrayList<ItemStack> stacks = new ArrayList<>();

        PropertyProvider provider = stack.getCapability(DECapabilities.Properties.ITEM);
        if (provider != null){
            boolean held = provider.getBool("charge_held_item").getValue();
            boolean armor = provider.getBool("charge_armor").getValue();
            boolean hot_bar = provider.getBool("charge_hot_bar").getValue();
            boolean main = provider.getBool("charge_main").getValue();

            if (EquipmentManager.equipModLoaded() && provider.getBool("charge_" + EquipmentManager.equipModID()).getValue()) {
                stacks.addAll(EquipmentManager.getAllItems(entity));
            }

            if (entity instanceof Player) {
                Player player = (Player) entity;
                if (hot_bar && main) {
                    stacks.addAll(player.getInventory().items);
                } else if (hot_bar) {
                    stacks.addAll(player.getInventory().items.subList(0, 9));
                } else if (main) {
                    stacks.addAll(player.getInventory().items.subList(9, 36));
                }
                if (held) {
                    if (!hot_bar) {
                        stacks.add(entity.getMainHandItem());
                    }
                    stacks.add(entity.getOffhandItem());
                }
            } else {
                if (held) {
                    entity.getHandSlots().forEach(stacks::add);
                }
            }

            if (armor) {
                entity.getArmorSlots().forEach(stacks::add);
            }
        }

        stacks.remove(stack);

        if (!stacks.isEmpty()) {
            updateEnergy(stack, entity, stacks);
        }
    }

    public void updateEnergy(ItemStack capacitor, LivingEntity player, List<ItemStack> stacks) {
        IOPStorage storage = capacitor.getCapability(CapabilityOP.ITEM);
        if (storage != null) {
            for (ItemStack stack : stacks) {
                if (EnergyUtils.canReceiveEnergy(stack)) {
                    Item item = stack.getItem();
                    if (item instanceof IInvCharge && !((IInvCharge) item).canCharge(stack, player, DataUtils.contains(player.getHandSlots(), stack))) {
                        continue;
                    }
                    EnergyUtils.insertEnergy(stack, EnergyUtils.extractEnergy(capacitor, EnergyUtils.insertEnergy(stack, storage.getOPStored(), true), false), false);
                }
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        addModularItemInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public int getEnchantmentValue() {
        return DETier.getEnchantability(techLevel);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return damageBarVisible(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return damageBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return damageBarColour(stack);
    }

    @Override
    public boolean canBeHurtBy(DamageSource source) {
        return source.is(DamageTypes.FELL_OUT_OF_WORLD);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (entity.getAge() >= 0) {
            entity.setExtendedLifetime();
        }
        return super.onEntityItemUpdate(stack, entity);
    }
}
