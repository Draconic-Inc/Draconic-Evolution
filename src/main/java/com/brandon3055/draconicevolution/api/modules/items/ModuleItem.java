package com.brandon3055.draconicevolution.api.modules.items;

import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleProvider;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.ModuleData;
import com.brandon3055.draconicevolution.api.modules.lib.LimitedModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.api.modules.lib.StackModuleContext;
import net.covers1624.quack.util.SneakyUtils;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by covers1624 on 4/16/20.
 */
public class ModuleItem<P extends ModuleData<P>> extends Item implements ModuleProvider<P> {

    private Module<P> moduleCache = null;
    private Supplier<Module<P>> moduleSupplier;

    public ModuleItem(Properties properties, Supplier<Module<?>> moduleSupplier) {
        super(properties);
        this.moduleSupplier = SneakyUtils.unsafeCast(moduleSupplier);
    }

    public ModuleItem(Supplier<Module<?>> moduleSupplier) {
        super(new Properties());
        this.moduleSupplier = SneakyUtils.unsafeCast(moduleSupplier);
    }

    public static Module<?> getModule(ItemStack stack) {
        LazyOptional<ModuleProvider<?>> cap = stack.getCapability(DECapabilities.MODULE_CAPABILITY);
        if (!stack.isEmpty() && cap.isPresent()) {
            return cap.orElseThrow(RuntimeException::new).getModule();
        }
        return null;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilityProvider() {
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                if (cap == DECapabilities.MODULE_CAPABILITY){
                    return LazyOptional.of(() -> ModuleItem.this).cast();
                }
                return LazyOptional.empty();
            }
        };
    }

    @Override
    public Module<P> getModule() {
        if (moduleCache == null) {
            moduleCache = moduleSupplier.get();
        }
        return moduleCache;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        getModule().addInformation(tooltip, new LimitedModuleContext(stack, null, worldIn, null));
        ModuleEntity<?> entity = getModule().createEntity();
        entity.readFromItemStack(stack, new StackModuleContext(stack, null, null));
        entity.addToolTip(tooltip);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return getModule().getProperties().getTechLevel().getRarity();
    }
}
