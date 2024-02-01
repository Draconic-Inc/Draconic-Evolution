package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiButton;
import com.brandon3055.brandonscore.inventory.ContainerBCore;
import com.brandon3055.brandonscore.inventory.ContainerSlotLayout;
import com.brandon3055.brandonscore.inventory.PlayerSlot;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleGrid;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleHostContainer;
import com.brandon3055.draconicevolution.api.modules.lib.StackModuleContext;
import com.brandon3055.draconicevolution.init.DEContent;
import com.google.common.collect.Streams;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by brandon3055 on 19/4/20.
 */
public class ContainerModularItem extends ContainerBCore<TileBCore> implements ModuleHostContainer {

    private PlayerSlot slot;
    public ItemStack hostStack;
    private ModuleGrid moduleGrid;
    private ModuleHost moduleHost;

    public ContainerModularItem(int windowId, Inventory player, FriendlyByteBuf extraData) {
        super(DEContent.container_modular_item, windowId, player, extraData);
        this.slot = PlayerSlot.fromBuff(extraData);
        this.onContainerOpen();
        this.moduleGrid = new ModuleGrid(this, player);
    }

    public ContainerModularItem(int windowId, Inventory player, PlayerSlot itemSlot, ContainerSlotLayout.LayoutFactory<TileBCore> factory) {
        super(DEContent.container_modular_item, windowId, player, factory);
        this.slot = itemSlot;
        this.onContainerOpen();
        this.moduleGrid = new ModuleGrid(this, player);
    }

    private static Stream<ItemStack> getPlayerInventory(Inventory player) {
        return Streams.concat(player.items.stream(), player.armor.stream(), player.offhand.stream()).filter(e -> !e.isEmpty());
    }

    public static void tryOpenGui(ServerPlayer sender) {
        ItemStack stack = sender.getMainHandItem();
        if (!stack.isEmpty() && stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).isPresent()) {
            PlayerSlot slot = new PlayerSlot(sender, InteractionHand.MAIN_HAND);
            NetworkHooks.openGui(sender, new ContainerModularItem.Provider(stack, slot), slot::toBuff);
            return;
        } else {
            PlayerSlot slot = PlayerSlot.findStackActiveFirst(sender.getInventory(), e -> e.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).isPresent());
            if (slot != null) {
                NetworkHooks.openGui(sender, new ContainerModularItem.Provider(slot.getStackInSlot(sender), slot), slot::toBuff);
                return;
            }
        }

        sender.sendMessage(Component.translatable("modular_item.draconicevolution.error.no_modular_items").withStyle(ChatFormatting.RED), Util.NIL_UUID);
    }

    @Override
    public ModuleHost getModuleHost() {
        if (moduleHost == null || EffectiveSide.get().isClient()) {
            LazyOptional<ModuleHost> optional = hostStack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY);
            if (optional.isPresent()) {
                moduleHost = optional.orElseThrow(RuntimeException::new);
            }
        }
        return moduleHost;
    }

    @Override
    public ModuleContext getModuleContext() {
        return new StackModuleContext(hostStack, player, slot.getEquipmentSlot());
    }

    @Override
    public void onGridChange() {
        if (EffectiveSide.get().isServer()) {
            for (int i = 0; i < this.slots.size(); ++i) {
                ItemStack itemstack = this.slots.get(i).getItem();
                ItemStack itemstack1 = this.lastSlots.get(i);
                if (!ItemStack.matches(itemstack1, itemstack)) {
                    itemstack1 = itemstack.copy();
                    this.lastSlots.set(i, itemstack1);
                    for (ContainerListener icontainerlistener : this.containerListeners) {
                        icontainerlistener.slotChanged(this, i, itemstack1);
                    }
                }
            }
        }
    }

    private void onContainerOpen() {
        hostStack = slot.getStackInSlot(player);
        getModuleHost();
    }

    @Override
    public boolean stillValid(Player playerIn) {
        if (moduleHost == null || hostStack != slot.getStackInSlot(player)) {
            return false;
        }
        if (moduleHost != hostStack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).orElse(null)) {
            return false; //I dont think this is actually possible... But just in case.
        }
        return true;//moduleHost != null && hostStack == slot.getStackInSlot(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        if (quickMoveModule(player, getSlot(i))) {
            return ItemStack.EMPTY;
        }
        return super.quickMoveStack(player, i);
    }

    @Override
    public ModuleGrid getGrid() {
        return moduleGrid;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientTick() {
        ItemStack stack = slot.getStackInSlot(player);
        if (stack != hostStack && !stack.isEmpty() && stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).isPresent()) {
            hostStack = stack; //Because the client side stack is invalidated every time the server sends an update.
        }
    }

    @Override
    public void initializeContents(int stateId, List<ItemStack> stacks, ItemStack carried) {
        super.initializeContents(stateId, stacks, carried);
        ItemStack stack = slot.getStackInSlot(player);
        if (stack != hostStack && !stack.isEmpty() && stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).isPresent()) {
            hostStack = stack; //Because the client side stack is invalidated every time the server sends an update.
        }
    }

    @Override
    public void setItem(int slotID, int stateId, ItemStack stack) {
        super.setItem(slotID, stateId, stack);
        stack = slot.getStackInSlot(player);
        if (stack != hostStack && !stack.isEmpty() && stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).isPresent()) {
            hostStack = stack; //Because the client side stack is invalidated every time the server sends an update.
        }
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickTypeIn, Player player) {
        if (slotId >= 0 && slotId < slots.size()) {
            Slot slot = this.slots.get(slotId);
            if (slot != null && !slot.getItem().isEmpty()) {
                if (slot.getItem() == hostStack) {
                    return;
                } else if (clickTypeIn == ClickType.PICKUP && button == 0 && player.containerMenu.getCarried().isEmpty()) {
                    if (slot.getItem().getCapability(DECapabilities.MODULE_HOST_CAPABILITY).isPresent()) {
                        if (player instanceof ServerPlayer) {
                            PlayerSlot playerSlot;
                            if (slotId >= 41) playerSlot = new PlayerSlot(slotId - 41, PlayerSlot.EnumInvCategory.EQUIPMENT);
                            else if (slotId >= 40) playerSlot = new PlayerSlot(slotId - 40, PlayerSlot.EnumInvCategory.OFF_HAND);
                            else if (slotId >= 36) playerSlot = new PlayerSlot(slotId - 36, PlayerSlot.EnumInvCategory.ARMOR);
                            else playerSlot = new PlayerSlot(slotId, PlayerSlot.EnumInvCategory.MAIN);
                            NetworkHooks.openGui((ServerPlayer) player, new Provider(slot.getItem(), playerSlot), playerSlot::toBuff);
                        } else {
                            GuiButton.playGenericClick();
                        }
                        return;
                    }
                }
            }
        }
        if (slotId > 40) {
            return;
        }
        super.clicked(slotId, button, clickTypeIn, player);
    }

    public static class Provider implements MenuProvider {
        private ItemStack stack;
        private PlayerSlot slot;

        public Provider(ItemStack stack, PlayerSlot slot) {
            this.stack = stack;
            this.slot = slot;
        }

        @Override
        public Component getDisplayName() {
            return stack.getHoverName().plainCopy().append(" ").append(Component.translatable("gui.draconicevolution.modular_item.modules"));
        }

        @Nullable
        @Override
        public AbstractContainerMenu createMenu(int menuID, Inventory playerInventory, Player playerEntity) {
            return new ContainerModularItem(menuID, playerInventory, slot, GuiLayoutFactories.MODULAR_ITEM_LAYOUT);
        }
    }
}
