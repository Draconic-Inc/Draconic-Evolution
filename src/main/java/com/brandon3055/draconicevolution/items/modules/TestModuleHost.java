package com.brandon3055.draconicevolution.items.modules;

import com.brandon3055.brandonscore.inventory.PlayerSlot;
import com.brandon3055.draconicevolution.api.TechLevel;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleHostImpl;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.capability.ModuleHostCapabilityProvider;
import com.brandon3055.draconicevolution.init.ModuleCapability;
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
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

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
        if (!stack.getOrCreateTag().hasUniqueId("host_id")) { //TODO check if that capability overwrite issue is fixed in 1.15
            stack.getOrCreateTag().putUniqueId("host_id", UUID.randomUUID());
//            stack.getOrCreateTag().putUniqueId("host_id", UUID.fromString("5b5689b9-e43d-4282-a42a-dc916f3616b7"));
        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new ModuleHostCapabilityProvider(new ModuleHostImpl(TechLevel.DRACONIUM, width, height, ModuleTypes.ENERGY_STORAGE));
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
            PlayerSlot slot = new PlayerSlot(player, handIn);
            NetworkHooks.openGui((ServerPlayerEntity) player, new INamedContainerProvider() {
                @Override
                public ITextComponent getDisplayName() {
                    return player.getHeldItem(handIn).getDisplayName();
                }

                @Nullable
                @Override
                public Container createMenu(int menuID, PlayerInventory playerInventory, PlayerEntity player) {
                    return new ContainerModularItem(menuID, playerInventory, slot, GuiLayoutFactories.MODULAR_ITEM_LAYOUT);
                }
            }, slot::toBuff);
        }

        return super.onItemRightClick(worldIn, player, handIn);
    }
}
