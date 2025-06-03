package com.brandon3055.draconicevolution.init;

import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.capability.*;
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
import com.brandon3055.draconicevolution.lib.ComponentDataAccess;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

/**
 * Created by brandon3055 and covers1624 on 4/16/20.
 */
public class CapabilityData {

    private static final CrashLock LOCK = new CrashLock("Already Initialized");

    private static ModuleHostImpl getItemHostCap(ItemStack stack, HolderLookup.Provider provider) {
        if (!stack.has(ItemData.MODULE_HOST_CAP_INSTANCE)) {
            if (!(stack.getItem() instanceof IModularItem item)) throw new IllegalStateException("ITEM_HOST_DATA can only be used on an ItemStack who's item implements IModularItem!");
            stack.set(ItemData.MODULE_HOST_CAP_INSTANCE, item.createHostCapForRegistration(stack));
        }

        ModuleHostImpl host = (ModuleHostImpl) stack.get(ItemData.MODULE_HOST_CAP_INSTANCE);;
        assert host != null;
        host.updateDataAccess(new ComponentDataAccess(stack, provider, ItemData.MODULE_HOST_STORAGE.get()));
        return host;
    }

    private static ModularOPStorage getEnergyCap(ItemStack stack, HolderLookup.Provider provider) {
        if (!stack.has(ItemData.ENERGY_CAP_INSTANCE)) {
            if (!(stack.getItem() instanceof IModularEnergyItem item)) throw new IllegalStateException("ITEM_HOST_DATA can only be used on an ItemStack who's item implements IModularEnergyItem!");
//            stack.set(ItemData.ENERGY_CAP_INSTANCE, item.createOPCapForRegistration(stack));
        }

        ModularOPStorage storage = stack.get(ItemData.ENERGY_CAP_INSTANCE);
        assert storage != null;
        storage.updateDataAccess(new ComponentDataAccess(stack, provider, ItemData.MODULAR_ENERGY_STORAGE.get()));
        return storage;
    }

    public static void init(IEventBus modBus) {
        LOCK.lock();
//        ATTACHMENT_TYPES.register(modBus);
        modBus.addListener(CapabilityData::register);
    }

    public static void register(RegisterCapabilitiesEvent event) {

        DEContent.ITEMS.getEntries().forEach(holder -> {
            Item item = holder.get();
            if (item instanceof IModularItem modularItem) {
                //Ok, so just realized i dont currently support Property provider without module host so that makes things simpler.
                //I'm thinking I use a non serializing compoennt to store the host isntance, that then has a seperate data accessor for reading and writing data
//                event.registerItem(DECapabilities.Host.ITEM, (stack, context) -> stack.getData(MODULAR_ITEM_HOST), item);
//                event.registerItem(DECapabilities.Properties.ITEM, (stack, context) -> stack.getData(MODULAR_ITEM_HOST) instanceof PropertyProvider provider ? provider : null, item);
                event.registerItem(DECapabilities.Host.ITEM, (stack, v) -> new RegistryCap<>(access -> getItemHostCap(stack, access)), item);
                event.registerItem(DECapabilities.Properties.ITEM, (stack, v) -> new RegistryCap<>(access -> getItemHostCap(stack, access)), item);

//                Removing RegistryAccess isnt going to work because we need registry access for item serialisation....
//                Going to have to bite the fucking tank shell....
//                Ok... So... unless covers finds a better solution,
//                        will need to convert the entierty of module host and all modules to components?
//                wait no... Module host would bneed to be a component, modules and stuff would just need to...
//                No. ModuleHost would need a codec, and so would everything that gets saved on module host. yea... this is fucked but maybe doable? idk...
//
//                Or, maybe instead of the capability returning a "ModuleHost" it could return something like "ItemModuleHost" with a get(RegistyAccess) method to do the actual get, that would load and stuff?
//                And this could just be for the item version of the capability?.

//                Actually, thinking about it some more, I dont think much will nbeed to change.
//                        I will still need to figure out a way to handle saving on data change, but that was goign to need to be a thing regardless,
//                        Serialization should be pretty much the same, just with codecs.... I hope...

                if (item instanceof IModularEnergyItem modularEnergyItem) {
//                    event.registerItem(CapabilityOP.ITEM, (stack, v) -> getEnergyCap(stack), item);
//                    event.registerItem(CapabilityOP.ITEM, (stack, v) -> new RegistryCap<>(access -> getItemHostCap(stack, access)), item);
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
