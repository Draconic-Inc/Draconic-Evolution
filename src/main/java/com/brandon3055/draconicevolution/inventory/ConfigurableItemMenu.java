package com.brandon3055.draconicevolution.inventory;

import codechicken.lib.gui.modular.lib.container.SlotGroup;
import codechicken.lib.inventory.container.modular.ModularGuiContainerMenu;
import codechicken.lib.inventory.container.modular.ModularSlot;
import com.brandon3055.brandonscore.inventory.PlayerSlot;
import com.brandon3055.brandonscore.lib.Pair;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.IdentityProvider;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import com.brandon3055.draconicevolution.client.gui.modular.itemconfig.PropertyData;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.integration.equipment.EquipmentManager;
import com.google.common.collect.Streams;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Created by brandon3055 on 19/4/20.
 */
public class ConfigurableItemMenu extends ModularGuiContainerMenu implements ModularMenuCommon{
    private static final UUID DEFAULT_UUID = UUID.fromString("d12b41e3-16ce-4653-ab36-1cd913719af8"); //This is just a completely random UUID

    private UUID selectedIdentity; //Default is irrelevant as long as its not null.
    private Runnable onInventoryChange;
    private Consumer<Boolean> onSelectionMade;
    private ItemStack stackCache = ItemStack.EMPTY;

    public final SlotGroup main = createSlotGroup(0);
    public final SlotGroup hotBar = createSlotGroup(0);
    public final SlotGroup armor = createSlotGroup(0);
    public final SlotGroup offhand = createSlotGroup(0);
    public final SlotGroup curios = createSlotGroup(0);

    public ConfigurableItemMenu(int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(windowId, playerInv, PlayerSlot.fromBuff(extraData));
    }

    public ConfigurableItemMenu(int windowId, Inventory playerInv, PlayerSlot slot) {
        super(DEContent.MENU_CONFIGURABLE_ITEM.get(), windowId, playerInv);
        main.addPlayerMain(playerInv);
        hotBar.addPlayerBar(playerInv);
        armor.addPlayerArmor(playerInv);
        offhand.addPlayerOffhand(playerInv);
        EquipmentManager.getEquipmentInventory(playerInv.player).ifPresent(handler -> curios.addSlots(handler.getSlots(), 0, i -> new ModularSlot(handler, i)));

        IdentityProvider.resolveDuplicateIdentities(getInventoryStacks());

        UUID found = getIdentity(slot.getStackInSlot(playerInv.player));
        if (found != null) {
            stackCache = slot.getStackInSlot(playerInv.player);
        }
        this.selectedIdentity = found == null ? DEFAULT_UUID : found;
    }

    @Override
    public List<Slot> getSlots() {
        return slots;
    }

    public void setOnInventoryChange(Runnable onInventoryChange) {
        this.onInventoryChange = onInventoryChange;
    }

    public void setSelectionListener(Consumer<Boolean> onSelectionMade) {
        this.onSelectionMade = onSelectionMade;
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickTypeIn, Player player) {
        if (slotId >= 0 && slotId < slots.size()) {
            Slot slot = this.slots.get(slotId);
            if (slot != null && !slot.getItem().isEmpty()) {
                PropertyProvider provider = slot.getItem().getCapability(DECapabilities.Properties.ITEM);
                if (provider != null) {
                    if (clickTypeIn == ClickType.PICKUP && button == 0 && player.containerMenu.getCarried().isEmpty()) {
                        selectedIdentity = provider.getIdentity();
                        if (onSelectionMade != null) {
                            onSelectionMade.accept(false);
                        }
                        stackCache = slot.getItem();
                        return;
                    }
                }
            }
        }
        if (onInventoryChange != null) {
            onInventoryChange.run();
        }
        super.clicked(slotId, button, clickTypeIn, player);
    }

    public UUID getSelectedIdentity() {
        return selectedIdentity;
    }

    public static Stream<ItemStack> getPlayerInventory(Inventory player) {
        return Streams.concat(player.items.stream(), player.armor.stream(), player.offhand.stream(), EquipmentManager.getAllItems(player.player).stream()).filter(e -> !e.isEmpty());
    }

    public static Stream<Pair<ItemStack, PropertyProvider>> getStackProviders(Stream<ItemStack> stacks) {
        return stacks
                .map(e -> Pair.of(e, e.getCapability(DECapabilities.Properties.ITEM)))
                .filter(e -> e.value() != null);
    }

    public static void handlePropertyData(Player player, PropertyData data) {
        if (data.isGlobal) {
            getStackProviders(getPlayerInventory(player.getInventory()))
                    .filter(e -> e.value().getProviderName().equals(data.providerName))
                    .map(e -> Pair.of(e.key(), e.value().getProperty(data.getPropertyName())))
                    .filter(e -> Objects.nonNull(e.value()))
                    .filter(e -> e.value().getType() == data.type)
                    .forEach(e -> e.value().loadData(data, e.key()));
        } else {
            getStackProviders(getPlayerInventory(player.getInventory()))
                    .filter(e -> e.value().getIdentity().equals(data.providerID))
                    .map(e -> Pair.of(e.key(), e.value().getProperty(data.getPropertyName())))
                    .filter(e -> Objects.nonNull(e.value()))
                    .filter(e -> e.value().getType() == data.type)
                    .findAny()
                    .ifPresent(e -> e.value().loadData(data, e.key()));
        }
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
    }

    @Override
    public void initializeContents(int stateId, List<ItemStack> stacks, ItemStack carried) {
        super.initializeContents(stateId, stacks, carried);
        onSyncDataReceived();
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    @Override
    public void setItem(int slotID, int stateId, ItemStack stack) {
        super.setItem(slotID, stateId, stack);
        onSyncDataReceived();
    }

    private boolean initialSync = false;

    private UUID lastSelected = null;

    private void onSyncDataReceived() {
        if (!initialSync) {
            initialSync = true;
            if (onSelectionMade != null) {
                onSelectionMade.accept(true);
            }
        }
        if (selectedIdentity != lastSelected || findProvider(selectedIdentity) == null) {
            lastSelected = selectedIdentity;
            if (onInventoryChange != null) {
                onInventoryChange.run();
            }
        }
    }

    /**
     * Do not use this for anything important!
     */
    public ItemStack getLastStack() {
        return stackCache;
    }

    public static void tryOpenGui(ServerPlayer sender) {
        ItemStack stack = sender.getMainHandItem();
        if (!stack.isEmpty() && stack.getCapability(DECapabilities.Properties.ITEM) != null) {
            PlayerSlot slot = new PlayerSlot(sender, InteractionHand.MAIN_HAND);
            sender.openMenu(new ConfigurableItemMenu.Provider(slot), slot::toBuff);
            return;
        } else {
            PlayerSlot slot = PlayerSlot.findStackActiveFirst(sender.getInventory(), e -> e.getCapability(DECapabilities.Properties.ITEM) != null);
            if (slot != null) {
                sender.openMenu(new ConfigurableItemMenu.Provider(slot), slot::toBuff);
                return;
            }
        }

        sender.sendSystemMessage(Component.translatable("gui.draconicevolution.item_config.no_configurable_items").withStyle(ChatFormatting.RED));
    }

    public static class Provider implements MenuProvider {
        private PlayerSlot slot;

        public Provider(PlayerSlot slot) {
            this.slot = slot;
        }

        @Override
        public Component getDisplayName() {
            return Component.translatable("gui.draconicevolution.item_config.name");
        }

        @Nullable
        @Override
        public AbstractContainerMenu createMenu(int menuID, Inventory playerInventory, Player playerEntity) {
            return new ConfigurableItemMenu(menuID, playerInventory, slot);
        }
    }
}
