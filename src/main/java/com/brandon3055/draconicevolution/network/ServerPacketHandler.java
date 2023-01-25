package com.brandon3055.draconicevolution.network;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.packet.ICustomPacketHandler;
import codechicken.lib.packet.PacketCustom;
import com.brandon3055.brandonscore.handlers.HandHelper;
import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleGrid;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleHostContainer;
import com.brandon3055.draconicevolution.blocks.PlacedItem;
import com.brandon3055.draconicevolution.blocks.tileentity.TilePlacedItem;
import com.brandon3055.draconicevolution.client.gui.modular.itemconfig.PropertyData;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.integration.equipment.EquipmentManager;
import com.brandon3055.draconicevolution.integration.jei.FusionRecipeTransferHelper;
import com.brandon3055.draconicevolution.inventory.ContainerConfigurableItem;
import com.brandon3055.draconicevolution.inventory.ContainerFusionCraftingCore;
import com.brandon3055.draconicevolution.inventory.ContainerModularItem;
import com.brandon3055.draconicevolution.items.tools.DislocatorAdvanced;
import com.brandon3055.draconicevolution.items.tools.Magnet;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.ArrayList;
import java.util.List;

public class ServerPacketHandler implements ICustomPacketHandler.IServerPacketHandler {

    @Override
    public void handlePacket(PacketCustom packet, ServerPlayer sender, ServerGamePacketListenerImpl handler) {
        switch (packet.getType()) {
            case DraconicNetwork.S_TOGGLE_DISLOCATORS:
                toggleDislocators(sender);
                break;
            case DraconicNetwork.S_TOOL_PROFILE:
//                changeToolProfile(sender, packet.readBoolean());
                break;
            case DraconicNetwork.S_CYCLE_DIG_AOE:
//                cycleToolAOE(sender, packet.readBoolean());
                break;
            case DraconicNetwork.S_CYCLE_ATTACK_AOE:
//                cycleAttackAOE(sender, packet.readBoolean());
                break;
            case DraconicNetwork.S_MODULE_CONTAINER_CLICK:
                moduleSlotClick(sender, packet);
                break;
            case DraconicNetwork.S_MODULE_ENTITY_MESSAGE:
                moduleEntityMessage(sender, packet);
                break;
            case DraconicNetwork.S_PROPERTY_DATA:
                propertyData(sender, packet);
                break;
            case DraconicNetwork.S_ITEM_CONFIG_GUI:
                if (packet.readBoolean())
                    ContainerModularItem.tryOpenGui(sender);
                else
                    ContainerConfigurableItem.tryOpenGui(sender);
                break;
            case DraconicNetwork.S_MODULE_CONFIG_GUI:
                ContainerModularItem.tryOpenGui(sender);
                break;
            case DraconicNetwork.S_DISLOCATOR_MESSAGE:
                dislocatorMessage(sender, packet);
                break;
            case DraconicNetwork.S_JEI_FUSION_TRANSFER:
                jeiFusionTransfer(sender, packet);
                break;
            case DraconicNetwork.S_PLACE_ITEM:
                placeItem(sender, packet);
                break;
        }
    }

    private void toggleDislocators(Player player) {
        List<ItemStack> dislocators = new ArrayList<>();

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof Magnet) {
                dislocators.add(stack);
            }
        }

        for (ItemStack stack : EquipmentManager.getAllItems(player)) {
            if (stack.getItem() instanceof Magnet) {
                dislocators.add(stack);
            }
        }

        for (ItemStack stack : dislocators) {
            Magnet.toggleEnabled(stack, player);
            boolean enabled = Magnet.isEnabled(stack);
//            ChatHelper.sendIndexed(player, new TranslationTextComponent("item_dislocate.draconicevolution." + (enabled ? "activate" : "deactivate")), 567);
            player.displayClientMessage(new TranslatableComponent("item_dislocate.draconicevolution." + (enabled ? "activate" : "deactivate")).withStyle(enabled ? ChatFormatting.GREEN : ChatFormatting.RED), true);
        }
    }

//    private void changeToolProfile(PlayerEntity player, boolean armor) {
//        if (armor) {
//            int i = 0;
//            NonNullList<ItemStack> armorInventory = player.getInventory().armorInventory;
//            for (int i1 = armorInventory.size() - 1; i1 >= 0; i1--) {
//                ItemStack stack = armorInventory.get(i1);
//                if (!stack.isEmpty() && stack.getItem() instanceof IConfigurableItem) {
//                    ToolConfigHelper.incrementProfile(stack);
//                    int newProfile = ToolConfigHelper.getProfile(stack);
//                    String name = ToolConfigHelper.getProfileName(stack, newProfile);
////                    ChatHelper.indexedTrans(player, new TranslationTextComponent("config.de.armor_profile_" + i + ".msg").toString() + " " + name, -30553045 + i);
//                }
//                i++;
//            }
//        } else {
//            ItemStack stack = HandHelper.getMainFirst(player);
//            if (!stack.isEmpty() && stack.getItem() instanceof IConfigurableItem) {
//                ToolConfigHelper.incrementProfile(stack);
//            }
//        }
//    }
//
//    private void cycleToolAOE(PlayerEntity player, boolean depth) {
//        ItemStack stack = player.getHeldItemMainhand();
//
//        if (stack.getItem() instanceof MiningToolBase) {
//            MiningToolBase tool = (MiningToolBase) stack.getItem();
//            int value = depth ? tool.getDigDepth(stack) : tool.getDigAOE(stack);
//            int maxValue = depth ? tool.getMaxDigDepth(stack) : tool.getMaxDigAOE(stack);
//
//            value++;
//            if (value > maxValue) {
//                value = 0;
//            }
//
//            if (depth) {
//                tool.setMiningDepth(stack, value);
//            } else {
//                tool.setMiningAOE(stack, value);
//            }
//        }
//    }
//
//    private void cycleAttackAOE(PlayerEntity player, boolean reverse) {
//        ItemStack stack = player.getHeldItemMainhand();
//
//        if (stack.getItem() instanceof IAOEWeapon) {
//            IAOEWeapon weapon = (IAOEWeapon) stack.getItem();
//            double value = weapon.getWeaponAOE(stack);
//            double maxValue = weapon.getMaxWeaponAOE(stack);
//
//            if (reverse) {
//                value -= 0.5;
//                if (value < 0) {
//                    value = maxValue;
//                }
//            } else {
//                value += 0.5;
//                if (value > maxValue) {
//                    value = 0;
//                }
//            }
//
//            weapon.setWeaponAOE(stack, value);
//        }
//
//    }

    private void moduleSlotClick(Player player, MCDataInput input) {
        if (player.containerMenu instanceof ModuleHostContainer container) {
            ModuleGrid grid = container.getGrid();
            if (grid != null) {
                ModuleGrid.GridPos pos = grid.getCell(input.readByte(), input.readByte());
                grid.cellClicked(pos, input.readFloat(), input.readFloat(), input.readByte(), input.readEnum(ClickType.class));
            }
        }
    }

    private void moduleEntityMessage(Player player, MCDataInput input) {
        if (player.containerMenu instanceof ModuleHostContainer container) {
            ModuleGrid grid = container.getGrid();
            if (grid != null) {
                ModuleGrid.GridPos pos = grid.getCell(input.readByte(), input.readByte());
                if (pos.hasEntity()) {
                    pos.getEntity().handleClientMessage(input);
                }
            }
        }
    }

    private void propertyData(ServerPlayer sender, PacketCustom packet) {
        PropertyData data = PropertyData.read(packet);
        ContainerConfigurableItem.handlePropertyData(sender, data);
    }

    private void dislocatorMessage(ServerPlayer sender, PacketCustom packet) {
        ItemStack stack = DislocatorAdvanced.findDislocator(sender);
        if (!stack.isEmpty()) {
            DEContent.dislocator_advanced.handleClientAction(sender, stack, packet);
        }
    }

    private void jeiFusionTransfer(ServerPlayer sender, PacketCustom packet) {
        ResourceLocation id = packet.readResourceLocation();
        boolean maxTransfer = packet.readBoolean();
        Recipe<?> recipe = sender.level.getRecipeManager().byKey(id).orElse(null);
        if (recipe instanceof IFusionRecipe && sender.containerMenu instanceof ContainerFusionCraftingCore) {
            FusionRecipeTransferHelper.doServerSideTransfer(sender, (ContainerFusionCraftingCore) sender.containerMenu, (IFusionRecipe) recipe, maxTransfer);
        }
    }

    private void placeItem(ServerPlayer player, PacketCustom packet) {
        ItemStack stack = HandHelper.getMainFirst(player);
        if (stack.isEmpty()) {
            return;
        }

        HitResult traceResult = player.pick(5, 0, false);
        if (traceResult.getType() == HitResult.Type.BLOCK) {
            Level world = player.level;
            BlockHitResult blockTrace = (BlockHitResult) traceResult;
            BlockPos posHit = blockTrace.getBlockPos();
            if (!com.brandon3055.brandonscore.network.ServerPacketHandler.verifyPlayerPermission(player, posHit)) {
                return;
            }

            BlockEntity tileHit = world.getBlockEntity(posHit);
            BlockPos posOnSide = posHit.relative(blockTrace.getDirection());
            BlockEntity tileOnSide = world.getBlockEntity(posOnSide);

            if (tileHit instanceof TilePlacedItem && InventoryUtils.insertItem(((TilePlacedItem) tileHit).itemHandler, stack, true).isEmpty()) {
                InventoryUtils.insertItem(((TilePlacedItem) tileHit).itemHandler, stack, false);
                player.getInventory().removeItem(stack);

            } else if (tileOnSide instanceof TilePlacedItem && InventoryUtils.insertItem(((TilePlacedItem) tileOnSide).itemHandler, stack, true).isEmpty()) {
                InventoryUtils.insertItem(((TilePlacedItem) tileOnSide).itemHandler, stack, false);
                player.getInventory().removeItem(stack);

            } else if (world.isEmptyBlock(posOnSide)) {
                if (!ForgeEventFactory.onBlockPlace(player, BlockSnapshot.create(player.level.dimension(), world, posHit), blockTrace.getDirection())) {
                    world.setBlockAndUpdate(posOnSide, DEContent.placed_item.defaultBlockState().setValue(PlacedItem.FACING, blockTrace.getDirection()));
                    BlockEntity tile = world.getBlockEntity(posOnSide);

                    if (tile instanceof TilePlacedItem) {
                        ((TilePlacedItem) tile).itemHandler.setStackInSlot(0, stack);
                        player.getInventory().removeItem(stack);
                    }
                }
            }
        }
    }
}