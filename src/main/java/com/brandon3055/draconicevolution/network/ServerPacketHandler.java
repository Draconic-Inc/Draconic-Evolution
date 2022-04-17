package com.brandon3055.draconicevolution.network;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.packet.ICustomPacketHandler;
import codechicken.lib.packet.PacketCustom;
import com.brandon3055.brandonscore.handlers.HandHelper;
import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleGrid;
import com.brandon3055.draconicevolution.blocks.PlacedItem;
import com.brandon3055.draconicevolution.blocks.tileentity.TilePlacedItem;
import com.brandon3055.draconicevolution.client.gui.modular.itemconfig.PropertyData;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.integration.equipment.EquipmentManager;
import com.brandon3055.draconicevolution.integration.jei.FusionRecipeTransferHelper;
import com.brandon3055.draconicevolution.inventory.ContainerConfigurableItem;
import com.brandon3055.draconicevolution.inventory.ContainerFusionCraftingCore;
import com.brandon3055.draconicevolution.inventory.ContainerModularItem;
import com.brandon3055.draconicevolution.inventory.ContainerModuleHost;
import com.brandon3055.draconicevolution.items.tools.DislocatorAdvanced;
import com.brandon3055.draconicevolution.items.tools.Magnet;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.BlockEvent;

import java.util.ArrayList;
import java.util.List;

public class ServerPacketHandler implements ICustomPacketHandler.IServerPacketHandler {


    @Override
    public void handlePacket(PacketCustom packet, ServerPlayerEntity sender, IServerPlayNetHandler handler) {
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

    private void toggleDislocators(PlayerEntity player) {
        List<ItemStack> dislocators = new ArrayList<>();

        for (int i = 0; i < player.inventory.getContainerSize(); i++) {
            ItemStack stack = player.inventory.getItem(i);
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
            player.displayClientMessage(new TranslationTextComponent("item_dislocate.draconicevolution." + (enabled ? "activate" : "deactivate")).withStyle(enabled ? TextFormatting.GREEN : TextFormatting.RED), true);
        }
    }

//    private void changeToolProfile(PlayerEntity player, boolean armor) {
//        if (armor) {
//            int i = 0;
//            NonNullList<ItemStack> armorInventory = player.inventory.armorInventory;
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

    private void moduleSlotClick(PlayerEntity player, MCDataInput input) {
        if (player.containerMenu instanceof ContainerModuleHost) {
            ModuleGrid grid = ((ContainerModuleHost<?>) player.containerMenu).getGrid();
            if (grid != null) {
                ModuleGrid.GridPos pos = grid.getCell(input.readByte(), input.readByte());
                grid.cellClicked(pos, input.readByte(), input.readEnum(ClickType.class));
            }
        }
    }

    private void propertyData(ServerPlayerEntity sender, PacketCustom packet) {
        PropertyData data = PropertyData.read(packet);
        ContainerConfigurableItem.handlePropertyData(sender, data);
    }

    private void dislocatorMessage(ServerPlayerEntity sender, PacketCustom packet) {
        ItemStack stack = DislocatorAdvanced.findDislocator(sender);
        if (!stack.isEmpty()) {
            DEContent.dislocator_advanced.handleClientAction(sender, stack, packet);
        }
    }

    private void jeiFusionTransfer(ServerPlayerEntity sender, PacketCustom packet) {
        ResourceLocation id = packet.readResourceLocation();
        boolean maxTransfer = packet.readBoolean();
        IRecipe<?> recipe = sender.level.getRecipeManager().byKey(id).orElse(null);
        if (recipe instanceof IFusionRecipe && sender.containerMenu instanceof ContainerFusionCraftingCore) {
            FusionRecipeTransferHelper.doServerSideTransfer(sender, (ContainerFusionCraftingCore) sender.containerMenu, (IFusionRecipe) recipe, maxTransfer);
        }
    }

    private void placeItem(ServerPlayerEntity player, PacketCustom packet) {
        ItemStack stack = HandHelper.getMainFirst(player);
        if (stack.isEmpty()) {
            return;
        }

        RayTraceResult traceResult = player.pick(4, 0, false);
        if (traceResult instanceof BlockRayTraceResult) {
            World world = player.level;
            BlockRayTraceResult blockTrace = (BlockRayTraceResult) traceResult;
            BlockPos posHit = blockTrace.getBlockPos();
            if (!com.brandon3055.brandonscore.network.ServerPacketHandler.verifyPlayerPermission(player, posHit)) {
                return;
            }

            TileEntity tileHit = world.getBlockEntity(posHit);
            BlockPos posOnSide = posHit.relative(blockTrace.getDirection());
            TileEntity tileOnSide = world.getBlockEntity(posOnSide);

            if (tileHit instanceof TilePlacedItem && InventoryUtils.insertItem(((TilePlacedItem) tileHit).itemHandler, stack, true).isEmpty()) {
                InventoryUtils.insertItem(((TilePlacedItem) tileHit).itemHandler, stack, false);
                player.inventory.removeItem(stack);

            } else if (tileOnSide instanceof TilePlacedItem && InventoryUtils.insertItem(((TilePlacedItem) tileOnSide).itemHandler, stack, true).isEmpty()) {
                InventoryUtils.insertItem(((TilePlacedItem) tileOnSide).itemHandler, stack, false);
                player.inventory.removeItem(stack);

            } else if (world.isEmptyBlock(posOnSide)) {
                if (!ForgeEventFactory.onBlockPlace(player, BlockSnapshot.create(player.level.dimension(), world, posHit), blockTrace.getDirection())) {
                    world.setBlockAndUpdate(posOnSide, DEContent.placed_item.defaultBlockState().setValue(PlacedItem.FACING, blockTrace.getDirection()));
                    TileEntity tile = world.getBlockEntity(posOnSide);

                    if (tile instanceof TilePlacedItem) {
                        ((TilePlacedItem) tile).itemHandler.setStackInSlot(0, stack);
                        player.inventory.removeItem(stack);
                    }
                }
            }
        }
    }
}