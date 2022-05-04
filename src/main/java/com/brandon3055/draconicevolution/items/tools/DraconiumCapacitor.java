package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.api.power.IOPStorageModifiable;
import com.brandon3055.brandonscore.capability.MultiCapabilityProvider;
import com.brandon3055.brandonscore.lib.TechPropBuilder;
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
import com.brandon3055.draconicevolution.integration.equipment.EquipmentManager;
import com.brandon3055.draconicevolution.items.equipment.DEItemTier;
import com.brandon3055.draconicevolution.items.equipment.IModularItem;
import com.brandon3055.draconicevolution.lib.WTFException;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 31/05/2016.
 */
//@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class DraconiumCapacitor extends Item implements IInvCharge, IModularItem {
    private TechLevel techLevel;

    public DraconiumCapacitor(TechPropBuilder properties) {
        super(properties.build());
        techLevel = properties.techLevel;
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
        }else {
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
    public MultiCapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
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
    public void handleTick(ItemStack stack, LivingEntity entity, @Nullable EquipmentSlotType slot, boolean inEquipModSlot) {
        ArrayList<ItemStack> stacks = new ArrayList<>();

        stack.getCapability(DECapabilities.PROPERTY_PROVIDER_CAPABILITY).ifPresent(props -> {
            boolean held = props.getBool("charge_held_item").getValue();
            boolean armor = props.getBool("charge_armor").getValue();
            boolean hot_bar = props.getBool("charge_hot_bar").getValue();
            boolean main = props.getBool("charge_main").getValue();

            if (EquipmentManager.equipModLoaded() &&  props.getBool("charge_" + EquipmentManager.equipModID()).getValue()) {
                stacks.addAll(EquipmentManager.getAllItems(entity));
            }

            if (entity instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) entity;
                if (hot_bar && main) {
                    stacks.addAll(player.inventory.items);
                } else if (hot_bar) {
                    stacks.addAll(player.inventory.items.subList(0, 9));
                } else if (main) {
                    stacks.addAll(player.inventory.items.subList(9, 36));
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
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        addModularItemInformation(stack, worldIn, tooltip, flagIn);
    }

//    @Override
//    public boolean hasCustomEntity(ItemStack stack) {
//        return true;
//    }
//
//    @Override
//    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
//        return new EntityPersistentItem(world, location, itemstack);
//    }

    @Override
    public int getEnchantmentValue() {
        return DEItemTier.getEnchantability(techLevel);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }
}
