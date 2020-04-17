package com.brandon3055.draconicevolution.modules_temp;

import com.brandon3055.draconicevolution.modules_temp.capability.IModuleHost;
import com.brandon3055.draconicevolution.modules_temp.internal.EnergyModuleType;
import com.brandon3055.draconicevolution.modules_temp.internal.ShieldModuleType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static codechicken.lib.util.SneakyUtils.unsafeCast;
import static com.brandon3055.draconicevolution.modules_temp.InstallResult.InstallResultType.NO;
import static com.brandon3055.draconicevolution.modules_temp.InstallResult.InstallResultType.ONLY_WHEN_OVERRIDEN;

/**
 * Created by brandon3055 and covers1624 on 4/16/20.
 */
public class ModuleHostImpl implements IModuleHost, INBTSerializable<CompoundNBT> {

//    private EnergyModuleProperties.Impl energyProperties = new EnergyModuleProperties.Impl();
//    private ShieldModuleProperties.Impl shieldProperties = new ShieldModuleProperties.Impl();

    private Map<IModule<?>, IModuleProperties> modulePropertyMap = new HashMap<>();

    @Override
    public List<IModule<?>> getModules() {
        return null;
    }

    public void loadProperties() {
        List<IModule<?>> modules = getModules();

        modulePropertyMap = modules.stream()//
//                .map(e -> {
////                    IModuleProperties props = e.getModuleType().createProperties();
////                    e.applyProperties(unsafeCast(props));
//                    return Pair.of(e, e.getProperties());
//                })//
                .collect(Collectors.toMap(e -> e, IModule::getProperties));

//        modulePropertyMap.entrySet().stream()//
//                .filter(e -> e.getKey().getModuleType() == ModuleTypes.ENERGY_STORAGE)//
//                .map(e -> ((EnergyModuleProperties) e.getValue()))//
//                .forEach(e -> energyProperties.merge(unsafeCast(e)));

    }


    @Override
    public CompoundNBT serializeNBT() {
        return new CompoundNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {

    }
}
