package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.api.TimeKeeper;
import com.brandon3055.brandonscore.network.BCoreNetwork;
import com.brandon3055.brandonscore.utils.InventoryUtils;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.brandonscore.utils.TargetPos;
import com.brandon3055.draconicevolution.handlers.DESounds;
import com.brandon3055.draconicevolution.handlers.dislocator.DislocatorSaveData;
import com.brandon3055.draconicevolution.handlers.dislocator.DislocatorTarget;
import com.brandon3055.draconicevolution.handlers.dislocator.GroundTarget;
import com.brandon3055.draconicevolution.handlers.dislocator.PlayerTarget;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Created by brandon3055 on 16/07/2016.
 */
public class BoundDislocator extends Dislocator {
    public BoundDislocator(Properties properties) {
        super(properties);
    }

//    @Override
//    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> list) {
//        if (this.allowdedIn(group) && (this == DEContent.DISLOCATOR_PLAYER_UNBOUND.get() || this == DEContent.DISLOCATOR_P2P_UNBOUND.get())) {
//            list.add(new ItemStack(this));
//        }
//    }

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
        if (entity.getAge() >= 0) {
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

        if (!entity.canChangeDimensions() || !(entity instanceof LivingEntity)) {
            return true;
        }

        BCoreNetwork.sendSound(player.level(), player.blockPosition(), DESounds.PORTAL.get(), SoundSource.PLAYERS, 0.1F, player.level().random.nextFloat() * 0.1F + 0.9F, false);

        location.setPitch(player.getXRot());
        location.setYaw(player.getYRot());
        notifyArriving(stack, player.level(), entity);
        location.teleport(entity);

        BCoreNetwork.sendSound(player.level(), player.blockPosition(), DESounds.PORTAL.get(), SoundSource.PLAYERS, 0.1F, player.level().random.nextFloat() * 0.1F + 0.9F, false);
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
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
            ItemNBTHelper.setString(bound, "player_name", player.getName().getString());
            player.setItemInHand(hand, ItemStack.EMPTY);
            InventoryUtils.givePlayerStack(player, bound);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        } else {
            TargetPos location = getTargetPos(stack, world);
            if (location == null) {
                if (isPlayer(stack)) {
                    player.sendSystemMessage(Component.translatable("dislocate.draconicevolution.bound.cant_find_player").withStyle(ChatFormatting.RED));
                } else {
                    player.sendSystemMessage(Component.translatable("dislocate.draconicevolution.bound.cant_find_target").withStyle(ChatFormatting.RED));
                }
                return new InteractionResultHolder<>(InteractionResult.PASS, stack);
            }

            BCoreNetwork.sendSound(player.level(), player.blockPosition(), DESounds.PORTAL.get(), SoundSource.PLAYERS, 0.1F, player.level().random.nextFloat() * 0.1F + 0.9F, false);
            location.setPitch(player.getXRot());
            location.setYaw(player.getYRot());
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

    public static UUID getPlayerID(ItemStack stack) {
        return ItemNBTHelper.getUUID(stack, "player_id", null);
    }

    public static void setPlayerID(ItemStack stack, UUID playerID) {
        stack.getOrCreateTag().putUUID("player_id", playerID);
    }

    public static boolean isValid(ItemStack stack) {
        CompoundTag compound = stack.getTag();
        if (stack.getItem() instanceof BoundDislocator) {
            return compound != null && getLinkId(stack) != null && getDislocatorId(stack) != null;
        }
        return false;
    }

    @Nullable
    public static UUID getDislocatorId(ItemStack stack) {
        return ItemNBTHelper.getUUID(stack, "stack_id", null);
    }

    @Nullable
    public static UUID getLinkId(ItemStack stack) {
        return ItemNBTHelper.getUUID(stack, "link_id", null);
    }

    private static void setDislocatorId(ItemStack stack, UUID dislocatorID) {
        ItemNBTHelper.setUUID(stack, "stack_id", dislocatorID);
    }

    private static void setLinkId(ItemStack stack, UUID linkID) {
        ItemNBTHelper.setUUID(stack, "link_id", linkID);
    }

    private static ItemStack createP2PDislocator(UUID linkID) {
        ItemStack stack = new ItemStack(DEContent.DISLOCATOR_P2P.get());
        setLinkId(stack, linkID);
        setDislocatorId(stack, UUID.randomUUID());
        return stack;
    }

    @Override
    public TargetPos getTargetPos(ItemStack stack, @Nullable Level world) {
        if (world instanceof ServerLevel) {
            if (isPlayer(stack)) {
                UUID playerID = getPlayerID(stack);
                MinecraftServer server = world.getServer();
                if (playerID != null && server != null) {
                    Player player = server.getPlayerList().getPlayer(playerID);
                    if (player != null) {
                        return new TargetPos(player);
                    }
                }
            } else {
                DislocatorTarget target = DislocatorSaveData.getLinkTarget(world, stack);
                UUID linkID = getLinkId(stack);
                if (target != null && linkID != null) {
                    return target.getTargetPos(world.getServer(), linkID, getDislocatorId(stack));
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
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (stack.getItem() == DEContent.DISLOCATOR_P2P_UNBOUND.get()) {
            tooltip.add(Component.translatable("dislocate.draconicevolution.bound.click_to_link").withStyle(ChatFormatting.GREEN));
        } else if (stack.getItem() == DEContent.DISLOCATOR_PLAYER_UNBOUND.get()) {
            tooltip.add(Component.translatable("dislocate.draconicevolution.bound.click_to_link_self").withStyle(ChatFormatting.GREEN));
        } else {
            if (isPlayer(stack)) {
                tooltip.add(Component.translatable("dislocate.draconicevolution.bound.player_link").append(": ").append(ItemNBTHelper.getString(stack, "player_name", "Unknown Player")).withStyle(ChatFormatting.BLUE));
            } else {
                tooltip.add(Component.translatable("dislocate.draconicevolution.bound.link_id").append(": ").append(String.valueOf(getLinkId(stack))).withStyle(ChatFormatting.BLUE));
            }
        }
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.RARE;
    }

    @Override
    public boolean canBeHurtBy(DamageSource source) {
        return source.is(DamageTypes.FELL_OUT_OF_WORLD);
    }
}
