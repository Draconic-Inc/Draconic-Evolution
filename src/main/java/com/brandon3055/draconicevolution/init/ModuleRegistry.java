//package com.brandon3055.draconicevolution.init;
//
//import com.brandon3055.draconicevolution.api.modules_old.IModule;
//import com.brandon3055.draconicevolution.api.modules_old.IModuleRegistry;
//import com.brandon3055.draconicevolution.api.modules_old.ModuleType;
//import net.minecraft.item.Item;
//import net.minecraft.util.ResourceLocation;
//
//import java.util.HashMap;
//
///**
// * Created by brandon3055 on 8/4/20.
// */
//public class ModuleRegistry implements IModuleRegistry {
//
//    public static final ModuleRegistry INSTANCE = new ModuleRegistry();
//
//    private HashMap<IModule, ResourceLocation> moduleNameMap = new HashMap<>();
//    private HashMap<ResourceLocation, IModule> nameModuleMap = new HashMap<>();
//
//
//    @Override
//    public <T extends IModule> void registerModule(ModuleType<T> type, T module) {
//        ResourceLocation name = ((Item) module).getRegistryName();
//        if (name == null) {
//            throw new IllegalStateException("Module item must be registered before it can be added to the module registry: " + module);
//        }
//        if (nameModuleMap.containsKey(name)) {
//            throw new IllegalStateException("Module has already been registered: " + name);
//        }
//        moduleNameMap.put(module, name);
//        nameModuleMap.put(name, module);
//    }
//
//    @Override
//    public ResourceLocation getModuleName(IModule module) {
//        return moduleNameMap.get(module);
//    }
//
//    @Override
//    public IModule getModuleByName(ResourceLocation name) {
//        return nameModuleMap.get(name);
//    }
//
//
//}
