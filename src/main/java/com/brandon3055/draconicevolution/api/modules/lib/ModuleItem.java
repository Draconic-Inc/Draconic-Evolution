package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.draconicevolution.api.modules.IModule;
import com.brandon3055.draconicevolution.api.modules.properties.ModuleProperties;
import com.brandon3055.draconicevolution.api.modules.capability.IModuleProvider;
import com.brandon3055.draconicevolution.init.ModuleCapability;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
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
public class ModuleItem<P extends ModuleProperties<P>> extends Item implements IModuleProvider<P> {

    @Deprecated //Do not access this directly!
    private IModule<P> module = null;
    private final Supplier<IModule<P>> moduleSupplier;

    //This needs to be a supplier so i can lazy load the module. The reason being items are registered before modules
    //so when this item is created the module does not exist yet.
    public ModuleItem(Properties properties, Supplier<IModule<P>> moduleSupplier) {
        super(properties);
        this.moduleSupplier = moduleSupplier;
    }

    public static IModule<?> getModule(ItemStack stack) {
        LazyOptional<IModuleProvider<?>> cap = stack.getCapability(ModuleCapability.MODULE_CAPABILITY);
        if (!stack.isEmpty() && cap.isPresent()) {
            return cap.orElseThrow(RuntimeException::new).getModule();
        }
        return null;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new ICapabilityProvider() {
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                if (cap == ModuleCapability.MODULE_CAPABILITY){
                    return LazyOptional.of(() -> ModuleItem.this).cast();
                }
                return LazyOptional.empty();
            }
        };
    }

    @Override
    public IModule<P> getModule() {
        if (module == null) {
            module = moduleSupplier.get();
        }
        return module;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        getModule().addInformation(tooltip);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return getModule().getProperties().getTechLevel().getRarity();
    }
}
