package com.brandon3055.draconicevolution.init;

import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.capability.ModuleProvider;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import com.brandon3055.draconicevolution.api.modules.lib.ModularOPStorage;
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
import net.covers1624.quack.util.CrashLock;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

/**
 * Created by brandon3055 and covers1624 on 4/16/20.
 */
public class CapabilityData {

    private static final CrashLock LOCK = new CrashLock("Already Initialized");

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, DraconicEvolution.MODID);

    public static final Supplier<AttachmentType<ModuleHost>> MODULAR_ITEM_HOST = ATTACHMENT_TYPES.register("module_item_host", () -> AttachmentType.serializable(CapabilityData::createHostFor).build());
    public static final Supplier<AttachmentType<ModularOPStorage>> MODULAR_ITEM_ENERGY = ATTACHMENT_TYPES.register("module_item_energy", () -> AttachmentType.serializable(CapabilityData::createEnergyFor).build());

    private static ModuleHost createHostFor(IAttachmentHolder holder) {
        if (!(holder instanceof ItemStack stack)) throw new IllegalStateException("ITEM_HOST_DATA can only be used on an ItemStack who's item implements IModularItem!");
        if (!(stack.getItem() instanceof IModularItem item)) throw new IllegalStateException("ITEM_HOST_DATA can only be used on an ItemStack who's item implements IModularItem!");
        return item.createHostCapForRegistration(stack);
    }

    private static ModularOPStorage createEnergyFor(IAttachmentHolder holder) {
        if (!(holder instanceof ItemStack stack)) throw new IllegalStateException("ITEM_HOST_DATA can only be used on an ItemStack who's item implements IModularItem!");
        if (!(stack.getItem() instanceof IModularEnergyItem item)) throw new IllegalStateException("ITEM_HOST_DATA can only be used on an ItemStack who's item implements IModularEnergyItem!");
        return item.createOPCapForRegistration(stack);
    }

    public static void init(IEventBus modBus) {
        LOCK.lock();
        ATTACHMENT_TYPES.register(modBus);
        modBus.addListener(CapabilityData::register);
    }

    private static int i = 0;

    public static void register(RegisterCapabilitiesEvent event) {

        DEContent.ITEMS.getEntries().forEach(holder -> {
            Item item = holder.get();
            if (item instanceof IModularItem modularItem) {
                event.registerItem(DECapabilities.Host.ITEM, (stack, context) -> stack.getData(MODULAR_ITEM_HOST), item);
                event.registerItem(DECapabilities.Properties.ITEM, (stack, context) -> stack.getData(MODULAR_ITEM_HOST) instanceof PropertyProvider provider ? provider : null, item);

                if (item instanceof IModularEnergyItem modularEnergyItem) {
                    event.registerItem(CapabilityOP.ITEM, (stack, context) -> stack.getData(MODULAR_ITEM_ENERGY), item);
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
