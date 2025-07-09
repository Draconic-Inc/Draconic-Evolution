package com.brandon3055.draconicevolution.init;

import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.DataComponentAccessor;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.capability.ModuleProvider;
import com.brandon3055.draconicevolution.api.modules.lib.ModularOPStorage;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleHostImpl;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalDirectIO;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalRelay;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalWirelessIO;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorInjector;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorStabilizer;
import com.brandon3055.draconicevolution.blocks.tileentity.*;
import com.brandon3055.draconicevolution.blocks.tileentity.chest.TileDraconiumChest;
import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFluidGate;
import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFluxGate;
import com.brandon3055.draconicevolution.integration.equipment.EquipmentManager;
import com.brandon3055.draconicevolution.integration.equipment.IDEEquipment;
import com.brandon3055.draconicevolution.items.equipment.IModularEnergyItem;
import com.brandon3055.draconicevolution.items.equipment.IModularItem;
import com.brandon3055.draconicevolution.lib.DumData;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/**
 * Created by brandon3055 and covers1624 on 4/16/20.
 */
public class CapabilityData {

    private static final CrashLock LOCK = new CrashLock("Already Initialized");

    private static ModuleHostImpl getItemHostCap(ItemStack stack) {
        if (!(stack.getItem() instanceof IModularItem item)) {
            throw new IllegalStateException("ITEM_HOST_DATA can only be used on an ItemStack who's item implements IModularItem!");
        }

        ModuleHostImpl host = item.createHostCapForRegistration(stack);
        assert host != null;
        host.updateDataAccess(DataComponentAccessor.itemStack(stack));
        return host;
    }

    private static ModularOPStorage getEnergyCap(ItemStack stack) {
        if (!(stack.getItem() instanceof IModularEnergyItem item)) {
            throw new IllegalStateException("ITEM_HOST_DATA can only be used on an ItemStack who's item implements IModularEnergyItem!");
        }

        ModularOPStorage storage = item.createOPCapForRegistration(stack);
        assert storage != null;
        storage.updateDataAccess(DataComponentAccessor.itemStack(stack));
        return storage;
    }

    public static void init(IEventBus modBus) {
        LOCK.lock();
        modBus.addListener(CapabilityData::register);
    }

    public static void register(RegisterCapabilitiesEvent event) {

        DEContent.ITEMS.getEntries().forEach(holder -> {
            Item item = holder.get();
            if (item instanceof IModularItem modularItem) {
                event.registerItem(DECapabilities.Host.ITEM, (stack, v) -> getItemHostCap(stack), item);
                if (item instanceof IModularEnergyItem modularEnergyItem) {
                    event.registerItem(CapabilityOP.ITEM, (stack, v) -> getEnergyCap(stack), item);
                    event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, v) -> getEnergyCap(stack), item);
                }
            }
            if (item instanceof IDEEquipment) {
                EquipmentManager.registerCapability(event, item);
            }
            if (item instanceof ModuleProvider<?> provider) {
                event.registerItem(DECapabilities.Module.ITEM, (stack, context) -> provider, item);
            }
        });

        DEModules.ITEMS.getEntries().forEach(holder -> {
            Item item = holder.get();
            if (item instanceof ModuleProvider<?> provider) {
                event.registerItem(DECapabilities.Module.ITEM, (stack, context) -> provider, item);
            }
        });


        TileGenerator.register(event);
        TileGrinder.register(event);
        TileDisenchanter.register(event);
        TileEnergyTransfuser.register(event);
        TileDislocatorPedestal.register(event);
        TileDislocatorReceptacle.register(event);
        TileCreativeOPCapacitor.register(event);
        TileEntityDetector.register(event);
        TileCelestialManipulator.register(event);
        TileDraconiumChest.register(event);
        TilePlacedItem.register(event);
        TileFusionCraftingInjector.register(event);
        TileFusionCraftingCore.register(event);
        TileEnergyCore.register(event);
        TileEnergyPylon.register(event);
        TileReactorStabilizer.register(event);
        TileReactorInjector.register(event);
        TileFluxGate.register(event);
        TileFluidGate.register(event);
        TileCrystalDirectIO.register(event);
        TileCrystalRelay.register(event);
        TileCrystalWirelessIO.register(event);
    }
}
