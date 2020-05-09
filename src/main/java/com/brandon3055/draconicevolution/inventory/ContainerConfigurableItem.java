package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiButton;
import com.brandon3055.brandonscore.inventory.ContainerBCore;
import com.brandon3055.brandonscore.inventory.ContainerSlotLayout;
import com.brandon3055.brandonscore.inventory.PlayerSlot;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.lib.WTFException;
import com.google.common.collect.Streams;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.*;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static com.brandon3055.draconicevolution.api.capability.DECapabilities.PROPERTY_PROVIDER_CAPABILITY;

/**
 * Created by brandon3055 on 19/4/20.
 */
public class ContainerConfigurableItem extends ContainerBCore<Object> {
    private static final UUID DEFAULT_UUID = UUID.fromString("d12b41e3-16ce-4653-ab36-1cd913719af8"); //This is just a completely random UUID

    public UUID selectedId; //Default is irrelevant as long as its not null.
    private Runnable onInventoryChange;

    public ContainerConfigurableItem(int windowId, PlayerInventory player, PacketBuffer extraData, ContainerSlotLayout.LayoutFactory<Object> factory) {
        super(DEContent.container_configurable_item, windowId, player, extraData, factory);
        UUID held = getProviderID(player.player.getHeldItemMainhand());
        this.selectedId = held == null ? DEFAULT_UUID : held;
    }

    public ContainerConfigurableItem(int windowId, PlayerInventory player, ContainerSlotLayout.LayoutFactory<Object> factory) {
        super(DEContent.container_configurable_item, windowId, player, factory);
        sanitizeProviders();
        UUID held = getProviderID(player.player.getHeldItemMainhand());
        this.selectedId = held == null ? DEFAULT_UUID : held;
    }

    private Stream<ItemStack> getInventoryStacks(PlayerInventory player) {
//        Stream<ItemStack> stackStream = Streams.concat(player.mainInventory.stream(), player.armorInventory.stream(), player.offHandInventory.stream()).filter(e -> !e.isEmpty());
//        //TODO add support for things like baubles
//        return stackStream;
        return inventorySlots.stream()
                .map(Slot::getStack)
                .filter(stack -> !stack.isEmpty());
    }

    public void setOnInventoryChange(Runnable onInventoryChange) {
        this.onInventoryChange = onInventoryChange;
    }

    public Stream<PropertyProvider> getProviders() {
        return getInventoryStacks(player.inventory)
                .map(e -> e.getCapability(PROPERTY_PROVIDER_CAPABILITY))
                .filter(LazyOptional::isPresent)
                .map(e -> e.orElseThrow(WTFException::new));
    }

    public PropertyProvider findProvider(UUID providerID) {
        return getProviders()
                .filter(provider -> provider.getProviderID().equals(providerID))
                .findFirst()
                .orElse(null);
    }

    private void sanitizeProviders() {
        HashSet<UUID> uuids = new HashSet<>();
        getProviders()
                .filter(provider -> !uuids.add(provider.getProviderID()))
                .forEach(PropertyProvider::regenProviderID);
    }

    @Override
    public ItemStack slotClick(int slotId, int button, ClickType clickTypeIn, PlayerEntity player) {
        if (slotId >= 0 && slotId < inventorySlots.size()) {
            Slot slot = this.inventorySlots.get(slotId);
            if (slot != null && !slot.getStack().isEmpty()) {
                LazyOptional<PropertyProvider> optionalCap = slot.getStack().getCapability(PROPERTY_PROVIDER_CAPABILITY);
                if (optionalCap.isPresent()) {
                    PropertyProvider provider = optionalCap.orElseThrow(WTFException::new);
                    if (provider.getProviderID().equals(selectedId)) {
                        return ItemStack.EMPTY;
                    } else if (clickTypeIn == ClickType.PICKUP && button == 0 && player.inventory.getItemStack().isEmpty()) {
                        selectedId = provider.getProviderID();
                        GuiButton.playGenericClick();
                        return ItemStack.EMPTY;
                    }
                }
            }
        }
        return super.slotClick(slotId, button, clickTypeIn, player);
    }

    private UUID getProviderID(ItemStack stack) {
        LazyOptional<PropertyProvider> optionalCap = stack.getCapability(PROPERTY_PROVIDER_CAPABILITY);
        if (!stack.isEmpty() && optionalCap.isPresent()) {
            return optionalCap.orElseThrow(WTFException::new).getProviderID();
        }
        return null;
    }

    public void receivePropertyData() {
        //TODO handle property update from the client then force resend all stack data same way vanilla does
    }

    @Override
    public void setAll(List<ItemStack> stacks) {
        super.setAll(stacks);
        if (onInventoryChange != null) {
            onInventoryChange.run();
        }
        //TODO Let the gui know it needs to reload ALL property elements!
    }

    public static class Provider implements INamedContainerProvider {
        @Override
        public ITextComponent getDisplayName() {
            return new TranslationTextComponent("gui.draconicevolution.configure_item.name");
        }

        @Nullable
        @Override
        public Container createMenu(int menuID, PlayerInventory playerInventory, PlayerEntity playerEntity) {
            return new ContainerConfigurableItem(menuID, playerInventory, GuiLayoutFactories.CONFIGURABLE_ITEM_LAYOUT);
        }
    }
}
