package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.api.TimeKeeper;
import com.brandon3055.brandonscore.network.BCoreNetwork;
import com.brandon3055.brandonscore.utils.InventoryUtils;
import com.brandon3055.brandonscore.utils.TargetPos;
import com.brandon3055.draconicevolution.handlers.DESounds;
import com.brandon3055.draconicevolution.handlers.dislocator.DislocatorSaveData;
import com.brandon3055.draconicevolution.handlers.dislocator.DislocatorTarget;
import com.brandon3055.draconicevolution.handlers.dislocator.GroundTarget;
import com.brandon3055.draconicevolution.handlers.dislocator.PlayerTarget;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.init.ItemData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Created by brandon3055 on 16/07/2016.
 */
public class BoundDislocator extends Dislocator {
    public BoundDislocator(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
        if (world instanceof ServerLevel && TimeKeeper.getServerTick() % 20 == 0) {
            if (isValid(stack) && !isPlayer(stack) && entity instanceof Player) {
                DislocatorSaveData.updateLinkTarget(world, stack, new PlayerTarget((Player) entity));
            }
        }
        super.inventoryTick(stack, world, entity, itemSlot, isSelected);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (entity.getAge() >= 0 && entity.pickupDelay != 32767) {
            entity.setExtendedLifetime();
        }
        if (entity.level() instanceof ServerLevel && TimeKeeper.getServerTick() % 20 == 0) {
            if (isValid(stack) && !isPlayer(stack)) {
                DislocatorSaveData.updateLinkTarget(entity.level(), stack, new GroundTarget(entity));
            }
        }
        return false;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (player.level().isClientSide) {
            return true;
        }
        TargetPos location = getTargetPos(stack, player.level());
        if (location == null) {
            if (isPlayer(stack)) {
                player.sendSystemMessage(Component.translatable("dislocate.draconicevolution.bound.cant_find_player").withStyle(ChatFormatting.RED));
            } else {
                player.sendSystemMessage(Component.translatable("dislocate.draconicevolution.bound.cant_find_target").withStyle(ChatFormatting.RED));
            }
            return true;
        }

        MinecraftServer server = player.getServer();
        if (server == null || !(server.getLevel(location.getDimension()) instanceof Level targetLevel)) {
            return true;
        }
        if (!entity.canChangeDimensions(entity.level(), targetLevel) || !(entity instanceof LivingEntity)) {
            return true;
        }

        BCoreNetwork.sendSound(player.level(), player.blockPosition(), DESounds.PORTAL.get(), SoundSource.PLAYERS, 0.1F, player.level().random.nextFloat() * 0.1F + 0.9F, false);
        notifyArriving(stack, player.level(), entity);
        location.teleport(entity);
        BCoreNetwork.sendSound(player.level(), player.blockPosition(), DESounds.PORTAL.get(), SoundSource.PLAYERS, 0.1F, player.level().random.nextFloat() * 0.1F + 0.9F, false);
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.level().isClientSide) {
            return new InteractionResultHolder<>(InteractionResult.PASS, stack);
        }

        if (stack.getItem() == DEContent.DISLOCATOR_P2P_UNBOUND.get()) {
            UUID linkID = UUID.randomUUID();
            ItemStack boundA = createP2PDislocator(linkID);
            ItemStack boundB = createP2PDislocator(linkID);
            player.setItemInHand(hand, ItemStack.EMPTY);
            InventoryUtils.givePlayerStack(player, boundA);
            InventoryUtils.givePlayerStack(player, boundB);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        } else if (stack.getItem() == DEContent.DISLOCATOR_PLAYER_UNBOUND.get()) {
            ItemStack bound = new ItemStack(DEContent.DISLOCATOR_PLAYER.get());
            setPlayerID(bound, player.getUUID());
            setDislocatorId(stack, UUID.randomUUID());
            bound.set(ItemData.DISLOCATOR_PLAYER_NAME, player.getName().getString());
            player.setItemInHand(hand, ItemStack.EMPTY);
            InventoryUtils.givePlayerStack(player, bound);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        } else {
            TargetPos location = getTargetPos(stack, level);
            if (location == null) {
                if (isPlayer(stack)) {
                    player.sendSystemMessage(Component.translatable("dislocate.draconicevolution.bound.cant_find_player").withStyle(ChatFormatting.RED));
                } else {
                    player.sendSystemMessage(Component.translatable("dislocate.draconicevolution.bound.cant_find_target").withStyle(ChatFormatting.RED));
                }
                return new InteractionResultHolder<>(InteractionResult.PASS, stack);
            }

            BCoreNetwork.sendSound(player.level(), player.blockPosition(), DESounds.PORTAL.get(), SoundSource.PLAYERS, 0.1F, player.level().random.nextFloat() * 0.1F + 0.9F, false);
            notifyArriving(stack, player.level(), player);
            location.teleport(player);
            BCoreNetwork.sendSound(player.level(), player.blockPosition(), DESounds.PORTAL.get(), SoundSource.PLAYERS, 0.1F, player.level().random.nextFloat() * 0.1F + 0.9F, false);

            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        }
    }

    public static boolean isPlayer(ItemStack stack) {
        return stack.getItem() == DEContent.DISLOCATOR_PLAYER.get();
    }

    public static boolean isP2P(ItemStack stack) {
        return stack.getItem() == DEContent.DISLOCATOR_P2P.get();
    }

    // === Data Access === //

    @Nullable
    public static UUID getPlayerID(ItemStack stack) {
        return stack.get(ItemData.DISLOCATOR_PLAYER_ID);
    }

    public static void setPlayerID(ItemStack stack, @Nullable UUID playerID) {
        stack.set(ItemData.DISLOCATOR_PLAYER_ID, playerID);
    }

    @Nullable
    public static UUID getDislocatorId(ItemStack stack) {
        return stack.get(ItemData.DISLOCATOR_STACK_ID);
    }

    private static void setDislocatorId(ItemStack stack, @Nullable UUID dislocatorID) {
        stack.set(ItemData.DISLOCATOR_STACK_ID, dislocatorID);
    }

    @Nullable
    public static UUID getLinkId(ItemStack stack) {
        return stack.get(ItemData.DISLOCATOR_LINK_ID);
    }

    private static void setLinkId(ItemStack stack, @Nullable UUID linkID) {
        stack.set(ItemData.DISLOCATOR_LINK_ID, linkID);
    }

    // =====================

    public static boolean isValid(ItemStack stack) {
        if (stack.getItem() instanceof BoundDislocator) {
            return getLinkId(stack) != null && getDislocatorId(stack) != null;
        }
        return false;
    }

    private static ItemStack createP2PDislocator(UUID linkID) {
        ItemStack stack = new ItemStack(DEContent.DISLOCATOR_P2P.get());
        setLinkId(stack, linkID);
        setDislocatorId(stack, UUID.randomUUID());
        return stack;
    }

    @Override
    public TargetPos getTargetPos(ItemStack stack, @Nullable Level level) {
        if (level instanceof ServerLevel serverLevel) {
            if (isPlayer(stack)) {
                UUID playerID = getPlayerID(stack);
                MinecraftServer server = serverLevel.getServer();
                if (playerID != null) {
                    Player player = server.getPlayerList().getPlayer(playerID);
                    if (player != null) {
                        return TargetPos.of(player);
                    }
                }
            } else {
                DislocatorTarget target = DislocatorSaveData.getLinkTarget(level, stack);
                UUID linkID = getLinkId(stack);
                if (target != null && linkID != null) {
                    return target.getTargetPos(level.getServer(), linkID, getDislocatorId(stack));
                }
            }
        }
        return null;
    }

    public static void notifyArriving(ItemStack stack, Level world, Entity entity) {
        if (world instanceof ServerLevel) {
            DislocatorTarget target = DislocatorSaveData.getLinkTarget(world, stack);
            if (target != null) {
                target.preTeleport(world.getServer(), entity);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        if (stack.getItem() == DEContent.DISLOCATOR_P2P_UNBOUND.get()) {
            tooltip.add(Component.translatable("dislocate.draconicevolution.bound.click_to_link").withStyle(ChatFormatting.GREEN));
        } else if (stack.getItem() == DEContent.DISLOCATOR_PLAYER_UNBOUND.get()) {
            tooltip.add(Component.translatable("dislocate.draconicevolution.bound.click_to_link_self").withStyle(ChatFormatting.GREEN));
        } else {
            if (isPlayer(stack)) {
                tooltip.add(Component.translatable("dislocate.draconicevolution.bound.player_link").append(": ").append(stack.getOrDefault(ItemData.DISLOCATOR_PLAYER_NAME, "Unknown Player")).withStyle(ChatFormatting.BLUE));
            } else {
                tooltip.add(Component.translatable("dislocate.draconicevolution.bound.link_id").append(": ").append(String.valueOf(getLinkId(stack))).withStyle(ChatFormatting.BLUE));
            }
        }
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

//    @Override
//    public Rarity getRarity(ItemStack stack) {
//        return Rarity.RARE;
//    }
}
