package com.brandon3055.draconicevolution.inventory;

import codechicken.lib.gui.modular.lib.container.SlotGroup;
import codechicken.lib.inventory.container.modular.ModularGuiContainerMenu;
import codechicken.lib.inventory.container.modular.ModularSlot;
import com.brandon3055.brandonscore.inventory.PlayerSlot;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleGrid;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleHostContainer;
import com.brandon3055.draconicevolution.api.modules.lib.StackModuleContext;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.integration.equipment.EquipmentManager;
import com.google.common.collect.Streams;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
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
public class ModularItemMenu extends ModularGuiContainerMenu implements ModuleHostContainer {

    private PlayerSlot slot;
    public ItemStack hostStack;
    private ModuleGrid moduleGrid;
    private ModuleHost moduleHost;
    private Player player;

    public final SlotGroup main = createSlotGroup(0);
    public final SlotGroup hotBar = createSlotGroup(0);
    public final SlotGroup armor = createSlotGroup(0);
    public final SlotGroup offhand = createSlotGroup(0);
    public final SlotGroup curios = createSlotGroup(0);

    public ModularItemMenu(int windowId, Inventory inv, FriendlyByteBuf extraData) {
        this(windowId, inv, PlayerSlot.fromBuff(extraData));
    }

    public ModularItemMenu(int windowId, Inventory inv, PlayerSlot itemSlot) {
        super(DEContent.MENU_MODULAR_ITEM.get(), windowId, inv);
        this.player = inv.player;
        this.slot = itemSlot;
        this.onContainerOpen();
        this.moduleGrid = new ModuleGrid(this, inv);

        hotBar.addPlayerBar(inv);
        main.addPlayerMain(inv);
        armor.addPlayerArmor(inv);
        offhand.addPlayerOffhand(inv);
        EquipmentManager.getEquipmentInventory(inv.player).ifPresent(handler -> curios.addSlots(handler.getSlots(), 0, i -> new ModularSlot(handler, i)));
    }

    private static Stream<ItemStack> getPlayerInventory(Inventory player) {
        return Streams.concat(player.items.stream(), player.armor.stream(), player.offhand.stream()).filter(e -> !e.isEmpty());
    }

    public static void tryOpenGui(ServerPlayer sender) {
        ItemStack stack = sender.getMainHandItem();
        if (!stack.isEmpty() && stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).isPresent()) {
            PlayerSlot slot = new PlayerSlot(sender, InteractionHand.MAIN_HAND);
            NetworkHooks.openScreen(sender, new ModularItemMenu.Provider(stack, slot), slot::toBuff);
            return;
        } else {
            PlayerSlot slot = PlayerSlot.findStackActiveFirst(sender.getInventory(), e -> e.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).isPresent());
            if (slot != null) {
                NetworkHooks.openScreen(sender, new ModularItemMenu.Provider(slot.getStackInSlot(sender), slot), slot::toBuff);
                return;
            }
        }

        sender.sendSystemMessage(Component.translatable("modular_item.draconicevolution.error.no_modular_items").withStyle(ChatFormatting.RED));
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
        if (hostStack.isEmpty() || moduleHost == null || hostStack != slot.getStackInSlot(player)) {
            return false;
        }
        return moduleHost == hostStack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).orElse(null);
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

    //    @Override
    @OnlyIn (Dist.CLIENT)
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
                            else if (slotId >= 36) playerSlot = new PlayerSlot(3 - (slotId - 36), PlayerSlot.EnumInvCategory.ARMOR);
                            else playerSlot = new PlayerSlot(slotId, PlayerSlot.EnumInvCategory.MAIN);
                            NetworkHooks.openScreen((ServerPlayer) player, new Provider(slot.getItem(), playerSlot), playerSlot::toBuff);
                        } else {
                            player.playSound(SoundEvents.UI_BUTTON_CLICK.get(), 0.25F, 1F);
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
            return new ModularItemMenu(menuID, playerInventory, slot);
        }
    }
}
