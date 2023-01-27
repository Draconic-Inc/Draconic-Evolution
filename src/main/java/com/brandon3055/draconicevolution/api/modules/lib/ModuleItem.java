package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleProvider;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.ModuleData;
import com.brandon3055.draconicevolution.integration.equipment.EquipmentManager;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
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

    @Deprecated //Do not access this directly!
    private Module<P> module = null;
    private Supplier<Module<P>> moduleSupplier;

    //This needs to be a supplier so i can lazy load the module. The reason being items are registered before modules
    //so when this item is created the module does not exist yet.
    public ModuleItem(Properties properties, Supplier<Module<P>> moduleSupplier) {
        super(properties);
        this.moduleSupplier = moduleSupplier;
    }

    public ModuleItem(Properties properties, Module<P> module) {
        super(properties);
        this.module = module;
    }

    //Only use if you intend to call setModule immediately after construction.
    public ModuleItem(Properties properties) {
        super(properties);
    }

    public void setModule(Module<P> module) {
        this.module = module;
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
        if (module == null) {
            module = moduleSupplier.get();
        }
        return module;
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
