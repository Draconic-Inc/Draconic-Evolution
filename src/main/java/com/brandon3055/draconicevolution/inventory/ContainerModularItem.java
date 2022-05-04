package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiButton;
import com.brandon3055.brandonscore.inventory.ContainerSlotLayout;
import com.brandon3055.brandonscore.inventory.PlayerSlot;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleGrid;
import com.brandon3055.draconicevolution.api.modules.lib.StackModuleContext;
import com.brandon3055.draconicevolution.init.DEContent;
import com.google.common.collect.Streams;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.*;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by brandon3055 on 19/4/20.
 */
public class ContainerModularItem extends ContainerModuleHost<TileBCore> {

    private PlayerSlot slot;
    public ItemStack hostStack;
    private ModuleGrid moduleGrid;
    private ModuleHost moduleHost;

    public ContainerModularItem(int windowId, PlayerInventory player, PacketBuffer extraData, ContainerSlotLayout.LayoutFactory<TileBCore> factory) {
        super(DEContent.container_modular_item, windowId, player, extraData, factory);
        this.slot = PlayerSlot.fromBuff(extraData);
        this.onContainerOpen();
        this.moduleGrid = new ModuleGrid(this, player);
    }

    public ContainerModularItem(int windowId, PlayerInventory player, PlayerSlot itemSlot, ContainerSlotLayout.LayoutFactory<TileBCore> factory) {
        super(DEContent.container_modular_item, windowId, player, factory);
        this.slot = itemSlot;
        this.onContainerOpen();
        this.moduleGrid = new ModuleGrid(this, player);
    }

    private static Stream<ItemStack> getPlayerInventory(PlayerInventory player) {
        return Streams.concat(player.items.stream(), player.armor.stream(), player.offhand.stream()).filter(e -> !e.isEmpty());
    }

    public static void tryOpenGui(ServerPlayerEntity sender) {
        ItemStack stack = sender.getMainHandItem();
        if (!stack.isEmpty() && stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).isPresent()) {
            PlayerSlot slot = new PlayerSlot(sender, Hand.MAIN_HAND);
            NetworkHooks.openGui(sender, new ContainerModularItem.Provider(stack, slot), slot::toBuff);
            return;
        } else {
            PlayerSlot slot = PlayerSlot.findStackActiveFirst(sender.inventory, e -> e.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).isPresent());
            if (slot != null) {
                NetworkHooks.openGui(sender, new ContainerModularItem.Provider(slot.getStackInSlot(sender), slot), slot::toBuff);
                return;
            }
        }

        sender.sendMessage(new TranslationTextComponent("modular_item.draconicevolution.error.no_modular_items").withStyle(TextFormatting.RED), Util.NIL_UUID);
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
                    for (IContainerListener icontainerlistener : this.containerListeners) {
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
    public boolean stillValid(PlayerEntity playerIn) {
        if (moduleHost == null || hostStack != slot.getStackInSlot(player)) {
            return false;
        }
        if (moduleHost != hostStack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).orElse(null)) {
            return false; //I dont think this is actually possible... But just in case.
        }
        return true;//moduleHost != null && hostStack == slot.getStackInSlot(player);
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
    public void setAll(List<ItemStack> stacks) {
        super.setAll(stacks);
        ItemStack stack = slot.getStackInSlot(player);
        if (stack != hostStack && !stack.isEmpty() && stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).isPresent()) {
            hostStack = stack; //Because the client side stack is invalidated every time the server sends an update.
        }
    }

    @Override
    public void setItem(int slotID, ItemStack stack) {
        super.setItem(slotID, stack);
        stack = slot.getStackInSlot(player);
        if (stack != hostStack && !stack.isEmpty() && stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).isPresent()) {
            hostStack = stack; //Because the client side stack is invalidated every time the server sends an update.
        }
    }

    @Override
    public ItemStack clicked(int slotId, int button, ClickType clickTypeIn, PlayerEntity player) {
        if (slotId >= 0 && slotId < slots.size()) {
            Slot slot = this.slots.get(slotId);
            if (slot != null && !slot.getItem().isEmpty()) {
                if (slot.getItem() == hostStack) {
                    return ItemStack.EMPTY;
                } else if (clickTypeIn == ClickType.PICKUP && button == 0 && player.inventory.getCarried().isEmpty()) {
                    if (slot.getItem().getCapability(DECapabilities.MODULE_HOST_CAPABILITY).isPresent()) {
                        if (player instanceof ServerPlayerEntity) {
                            PlayerSlot playerSlot;
                            if (slotId >= 41) playerSlot = new PlayerSlot(slotId - 41, PlayerSlot.EnumInvCategory.EQUIPMENT);
                            else if (slotId >= 40) playerSlot = new PlayerSlot(slotId - 40, PlayerSlot.EnumInvCategory.OFF_HAND);
                            else if (slotId >= 36) playerSlot = new PlayerSlot(slotId - 36, PlayerSlot.EnumInvCategory.ARMOR);
                            else playerSlot = new PlayerSlot(slotId, PlayerSlot.EnumInvCategory.MAIN);
                            NetworkHooks.openGui((ServerPlayerEntity) player, new Provider(slot.getItem(), playerSlot), playerSlot::toBuff);
                        } else {
                            GuiButton.playGenericClick();
                        }
                        return ItemStack.EMPTY;
                    }
                }
            }
        }
        if (slotId > 40) {
            return ItemStack.EMPTY;
        }
        return super.clicked(slotId, button, clickTypeIn, player);
    }

    public static class Provider implements INamedContainerProvider {
        private ItemStack stack;
        private PlayerSlot slot;

        public Provider(ItemStack stack, PlayerSlot slot) {
            this.stack = stack;
            this.slot = slot;
        }

        @Override
        public ITextComponent getDisplayName() {
            return stack.getHoverName().plainCopy().append(" ").append(new TranslationTextComponent("gui.draconicevolution.modular_item.modules"));
        }

        @Nullable
        @Override
        public Container createMenu(int menuID, PlayerInventory playerInventory, PlayerEntity playerEntity) {
            return new ContainerModularItem(menuID, playerInventory, slot, GuiLayoutFactories.MODULAR_ITEM_LAYOUT);
        }
    }
}
