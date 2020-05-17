package com.brandon3055.draconicevolution.items.modules;

import codechicken.lib.colour.EnumColour;
import com.brandon3055.brandonscore.inventory.PlayerSlot;
import com.brandon3055.draconicevolution.api.TechLevel;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.config.*;
import com.brandon3055.draconicevolution.api.config.ConfigProperty.BooleanFormatter;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleHostImpl;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.capability.ItemCapabilityProvider;
import com.brandon3055.draconicevolution.init.DEModules;
import com.brandon3055.draconicevolution.inventory.ContainerConfigurableItem;
import com.brandon3055.draconicevolution.inventory.ContainerModularItem;
import com.brandon3055.draconicevolution.inventory.GuiLayoutFactories;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static codechicken.lib.colour.EnumColour.*;
import static com.brandon3055.draconicevolution.api.capability.DECapabilities.MODULE_HOST_CAPABILITY;
import static com.brandon3055.draconicevolution.api.capability.DECapabilities.PROPERTY_PROVIDER_CAPABILITY;
import static net.minecraft.util.Direction.*;

/**
 * Created by brandon3055 on 17/4/20.
 */
public class TestModuleHost extends Item {

    private final int width;
    private final int height;

    public TestModuleHost(Properties properties, int width, int height) {
        super(properties);
        this.width = width;
        this.height = height;
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
//        if (!stack.getOrCreateTag().hasUniqueId("host_id")) { //TODO check if that capability overwrite issue is fixed in 1.15
//            stack.getOrCreateTag().putUniqueId("host_id", UUID.randomUUID());
//            stack.getOrCreateTag().putUniqueId("host_id", UUID.fromString("5b5689b9-e43d-4282-a42a-dc916f3616b7"));
//        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
//        ItemCapabilityProvider capabilityProvider = new ItemCapabilityProvider();
//        capabilityProvider.addCapability("module_host", DECapabilities.MODULE_HOST_CAPABILITY, new ModuleHostImpl(TechLevel.DRACONIUM, width, height, ModuleTypes.ENERGY_STORAGE));
//
//        PropertyProviderImpl propertyProvider = new PropertyProviderImpl("test_configurable_item");
//        capabilityProvider.addCapability("property_provider", DECapabilities.PROPERTY_PROVIDER_CAPABILITY, propertyProvider);
//
//        //Add config properties to propertyProvider

        ModuleHostImpl moduleHost = new ModuleHostImpl(TechLevel.DRACONIUM, width, height, "test_configurable_item");
        moduleHost.setPropertyBuilder(properties -> {
            properties.add(new BooleanProperty("test_boolean1", false).setFormatter(BooleanFormatter.ACTIVE_INACTIVE));
            properties.add(new IntegerProperty("test_integer1", 0).range(-3, 3));
            properties.add(new DecimalProperty("test_decimal1", 0).range(-1, 10));
            properties.add(new EnumProperty<>("test_enum", NORTH));
            properties.add(new EnumProperty<>("test_enum2", RED).setAllowedValues(RED, GREEN, BLUE));
            if (this.getRegistryName().getPath().equals("test_module_host_10x10")) {
                properties.add(new BooleanProperty("test_boolean2", false).setFormatter(BooleanFormatter.ENABLED_DISABLED));
                properties.add(new BooleanProperty("test_boolean3", false).setFormatter(BooleanFormatter.YES_NO));
                properties.add(new IntegerProperty("test_integer2", 0).range(-100, 100));
                properties.add(new IntegerProperty("test_integer3", 0).range(100, 200));
                properties.add(new DecimalProperty("test_decimal2", 0).range(0, 10));
                properties.add(new DecimalProperty("test_decimal3", 0).range(-1, 10));
            }
        });

        return new ItemCapabilityProvider<>(moduleHost);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
//        if (stack.hasTag()) {
//            tooltip.add(new StringTextComponent("Itm: " + stack.getTag().getUniqueId("host_id").toString()));
//        }
//        if (stack.capNBT != null) {
//            tooltip.add(new StringTextComponent("Cap: "+stack.capNBT.toString()));
//        }
//        stack.getCapability(ModuleCapability.MODULE_HOST_CAPABILITY).ifPresent(moduleHost -> {
//            tooltip.add(new StringTextComponent("Host: " + Integer.toHexString(moduleHost.hashCode())));
//            tooltip.add(new StringTextComponent("Host: " + moduleHost.hashCode()));
//            tooltip.add(new StringTextComponent("Modules: " + moduleHost.getModuleEntities()));
//        });
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity player, Hand handIn) {
        if (player instanceof ServerPlayerEntity) {
            if (player.isShiftKeyDown()) {
                NetworkHooks.openGui((ServerPlayerEntity) player, new ContainerConfigurableItem.Provider());
            } else {
                PlayerSlot slot = new PlayerSlot(player, handIn);
                NetworkHooks.openGui((ServerPlayerEntity) player, new ContainerModularItem.Provider(slot.getStackInSlot(player), slot), slot::toBuff);
            }
        }

        return super.onItemRightClick(worldIn, player, handIn);
    }

    @Nullable
    @Override
    public CompoundNBT getShareTag(ItemStack stack) {
        return DECapabilities.writeToShareTag(stack, stack.getTag());
    }

    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt) {
        stack.setTag(nbt);
        DECapabilities.readFromShareTag(stack, nbt);
    }
}
