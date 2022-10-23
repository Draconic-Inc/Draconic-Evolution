package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.api.power.IOPStorageModifiable;
import com.brandon3055.brandonscore.capability.MultiCapabilityProvider;
import com.brandon3055.brandonscore.utils.DataUtils;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.draconicevolution.api.IInvCharge;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
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
import com.brandon3055.draconicevolution.items.equipment.IModularItem;
import com.brandon3055.draconicevolution.lib.WTFException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 31/05/2016.
 */
public class DraconiumCapacitor extends Item implements IInvCharge, IModularItem {
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
    public ModuleHostImpl createHost(ItemStack stack) {
        ModuleHostImpl host;
        if (this == DEContent.capacitor_creative) {
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
    public MultiCapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        MultiCapabilityProvider prov = IModularItem.super.initCapabilities(stack, nbt);
        if (this == DEContent.capacitor_creative && prov != null) {
            ModuleHost host = prov.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).orElseThrow(WTFException::new);
            host.getModuleCategories().remove(ModuleCategory.ENERGY);
        }
        return prov;
    }

    @Nullable
    @Override
    public ModularOPStorage createOPStorage(ItemStack stack, ModuleHostImpl host) {
        if (this == DEContent.capacitor_creative) {
            return new ModularOPStorage(host, Long.MAX_VALUE, Long.MAX_VALUE, true) {
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
            };
        }
        return new ModularOPStorage(host, EquipCfg.getBaseCapEnergy(techLevel), EquipCfg.getBaseCapTransfer(techLevel), true);
    }

    @Override
    public void handleTick(ItemStack stack, LivingEntity entity, @Nullable EquipmentSlot slot, boolean inEquipModSlot) {
        ArrayList<ItemStack> stacks = new ArrayList<>();

        stack.getCapability(DECapabilities.PROPERTY_PROVIDER_CAPABILITY).ifPresent(props -> {
            boolean held = props.getBool("charge_held_item").getValue();
            boolean armor = props.getBool("charge_armor").getValue();
            boolean hot_bar = props.getBool("charge_hot_bar").getValue();
            boolean main = props.getBool("charge_main").getValue();

            if (EquipmentManager.equipModLoaded() && props.getBool("charge_" + EquipmentManager.equipModID()).getValue()) {
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
        });

        stacks.remove(stack);

        if (!stacks.isEmpty()) {
            updateEnergy(stack, entity, stacks);
        }
    }

    public void updateEnergy(ItemStack capacitor, LivingEntity player, List<ItemStack> stacks) {
        capacitor.getCapability(DECapabilities.OP_STORAGE).ifPresent(e -> {
            IOPStorageModifiable storage = (IOPStorageModifiable) e;
            for (ItemStack stack : stacks) {
                if (EnergyUtils.canReceiveEnergy(stack)) {
                    Item item = stack.getItem();
                    if (item instanceof IInvCharge && !((IInvCharge) item).canCharge(stack, player, DataUtils.contains(player.getHandSlots(), stack))) {
                        continue;
                    }
                    EnergyUtils.insertEnergy(stack, EnergyUtils.extractEnergy(capacitor, EnergyUtils.insertEnergy(stack, storage.getOPStored(), true), false), false);
                }
            }
        });
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
        return source == DamageSource.OUT_OF_WORLD;
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (entity.getAge() >= 0) {
            entity.setExtendedLifetime();
        }
        return super.onEntityItemUpdate(stack, entity);
    }
}
