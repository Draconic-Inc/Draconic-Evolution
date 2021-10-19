package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.api.TimeKeeper;
import com.brandon3055.brandonscore.network.BCoreNetwork;
import com.brandon3055.brandonscore.utils.InventoryUtils;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.brandonscore.utils.TargetPos;
import com.brandon3055.draconicevolution.entity.PersistentItemEntity;
import com.brandon3055.draconicevolution.handlers.DESounds;
import com.brandon3055.draconicevolution.handlers.dislocator.DislocatorSaveData;
import com.brandon3055.draconicevolution.handlers.dislocator.DislocatorTarget;
import com.brandon3055.draconicevolution.handlers.dislocator.GroundTarget;
import com.brandon3055.draconicevolution.handlers.dislocator.PlayerTarget;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> list) {
        if (this.allowdedIn(group) && (this == DEContent.dislocator_player_unbound || this == DEContent.dislocator_p2p_unbound)) {
            list.add(new ItemStack(this));
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (world instanceof ServerWorld && TimeKeeper.getServerTick() % 20 == 0) {
            if (isValid(stack) && !isPlayer(stack) && entity instanceof PlayerEntity) {
                DislocatorSaveData.updateLinkTarget(world, stack, new PlayerTarget((PlayerEntity) entity));
            }
        }
        super.inventoryTick(stack, world, entity, itemSlot, isSelected);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (entity.level instanceof ServerWorld && TimeKeeper.getServerTick() % 20 == 0) {
            if (isValid(stack) && !isPlayer(stack)) {
                DislocatorSaveData.updateLinkTarget(entity.level, stack, new GroundTarget(entity));
            }
        }
        return false;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        if (player.level.isClientSide) {
            return true;
        }
        TargetPos location = getTargetPos(stack, player.level);
        if (location == null) {
            if (isPlayer(stack)) {
                player.sendMessage(new TranslationTextComponent("dislocate.draconicevolution.bound.cant_find_player").withStyle(TextFormatting.RED), Util.NIL_UUID);
            } else {
                player.sendMessage(new TranslationTextComponent("dislocate.draconicevolution.bound.cant_find_target").withStyle(TextFormatting.RED), Util.NIL_UUID);
            }
            return true;
        }

        if (!entity.canChangeDimensions() || !(entity instanceof LivingEntity)) {
            return true;
        }

        BCoreNetwork.sendSound(player.level, player.blockPosition(), DESounds.portal, SoundCategory.PLAYERS, 0.1F, player.level.random.nextFloat() * 0.1F + 0.9F, false);

        location.setPitch(player.xRot);
        location.setYaw(player.yRot);
        notifyArriving(stack, player.level, entity);
        location.teleport(entity);

        BCoreNetwork.sendSound(player.level, player.blockPosition(), DESounds.portal, SoundCategory.PLAYERS, 0.1F, player.level.random.nextFloat() * 0.1F + 0.9F, false);
        return true;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.level.isClientSide) {
            return new ActionResult<>(ActionResultType.PASS, stack);
        }

        if (stack.getItem() == DEContent.dislocator_p2p_unbound) {
            UUID linkID = UUID.randomUUID();
            ItemStack boundA = createP2PDislocator(linkID);
            ItemStack boundB = createP2PDislocator(linkID);
            player.setItemInHand(hand, ItemStack.EMPTY);
            InventoryUtils.givePlayerStack(player, boundA);
            InventoryUtils.givePlayerStack(player, boundB);
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        } else if (stack.getItem() == DEContent.dislocator_player_unbound) {
            ItemStack bound = new ItemStack(DEContent.dislocator_player);
            setPlayerID(bound, player.getUUID());
            setDislocatorId(stack, UUID.randomUUID());
            ItemNBTHelper.setString(bound, "player_name", player.getName().getString());
            player.setItemInHand(hand, ItemStack.EMPTY);
            InventoryUtils.givePlayerStack(player, bound);
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        } else {
            TargetPos location = getTargetPos(stack, world);
            if (location == null) {
                if (isPlayer(stack)) {
                    player.sendMessage(new TranslationTextComponent("dislocate.draconicevolution.bound.cant_find_player").withStyle(TextFormatting.RED), Util.NIL_UUID);
                } else {
                    player.sendMessage(new TranslationTextComponent("dislocate.draconicevolution.bound.cant_find_target").withStyle(TextFormatting.RED), Util.NIL_UUID);
                }
                return new ActionResult<>(ActionResultType.PASS, stack);
            }

            BCoreNetwork.sendSound(player.level, player.blockPosition(), DESounds.portal, SoundCategory.PLAYERS, 0.1F, player.level.random.nextFloat() * 0.1F + 0.9F, false);
            location.setPitch(player.xRot);
            location.setYaw(player.yRot);
            notifyArriving(stack, player.level, player);
            location.teleport(player);
            BCoreNetwork.sendSound(player.level, player.blockPosition(), DESounds.portal, SoundCategory.PLAYERS, 0.1F, player.level.random.nextFloat() * 0.1F + 0.9F, false);

            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        }
    }

    public static boolean isPlayer(ItemStack stack) {
        return stack.getItem() == DEContent.dislocator_player;
    }

    public static boolean isP2P(ItemStack stack) {
        return stack.getItem() == DEContent.dislocator_p2p;
    }

    public static UUID getPlayerID(ItemStack stack) {
        return ItemNBTHelper.getUUID(stack, "player_id", null);
    }

    public static void setPlayerID(ItemStack stack, UUID playerID) {
        stack.getOrCreateTag().putUUID("player_id", playerID);
    }

    public static boolean isValid(ItemStack stack) {
        CompoundNBT compound = stack.getTag();
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
        ItemStack stack = new ItemStack(DEContent.dislocator_p2p);
        setLinkId(stack, linkID);
        setDislocatorId(stack, UUID.randomUUID());
        return stack;
    }


    @Override
    public TargetPos getTargetPos(ItemStack stack, @Nullable World world) {
        if (world instanceof ServerWorld) {
            if (isPlayer(stack)) {
                UUID playerID = getPlayerID(stack);
                MinecraftServer server = world.getServer();
                if (playerID != null && server != null) {
                    PlayerEntity player = server.getPlayerList().getPlayer(playerID);
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

    public static void notifyArriving(ItemStack stack, World world, Entity entity) {
        if (world instanceof ServerWorld) {
            DislocatorTarget target = DislocatorSaveData.getLinkTarget(world, stack);
            if (target != null) {
                target.preTeleport(world.getServer(), entity);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (stack.getItem() == DEContent.dislocator_p2p_unbound) {
            tooltip.add(new TranslationTextComponent("dislocate.draconicevolution.bound.click_to_link").withStyle(TextFormatting.GREEN));
        } else if (stack.getItem() == DEContent.dislocator_player_unbound) {
            tooltip.add(new TranslationTextComponent("dislocate.draconicevolution.bound.click_to_link_self").withStyle(TextFormatting.GREEN));
        } else {
            if (isPlayer(stack)) {
                tooltip.add(new TranslationTextComponent("dislocate.draconicevolution.bound.player_link").append(": ").append(ItemNBTHelper.getString(stack, "player_name", "Unknown Player")).withStyle(TextFormatting.BLUE));
            } else {
                tooltip.add(new TranslationTextComponent("dislocate.draconicevolution.bound.link_id").append(": ").append(String.valueOf(getLinkId(stack))).withStyle(TextFormatting.BLUE));
            }
        }
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return new PersistentItemEntity(world, location, itemstack);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.RARE;
    }
}
