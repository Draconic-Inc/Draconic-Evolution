package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.api.hud.IHudItem;
import com.brandon3055.brandonscore.lib.DelayedTask;
import com.brandon3055.brandonscore.network.BCoreNetwork;
import com.brandon3055.brandonscore.utils.TargetPos;
import com.brandon3055.draconicevolution.handlers.DESounds;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.AnvilUpdateEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by brandon3055 on 16/07/2016.
 */
public class Dislocator extends Item implements IHudItem {

    public Dislocator(Properties properties) {
        super(properties.fireResistant());
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    /**
     * @param stack  The dislocator item, stack
     * @param user   The entity using the dislocator / portal.
     * @param target The entity being teleported if not the user.
     */
    public Entity dislocateEntity(ItemStack stack, @Nonnull Entity user, @Nonnull Entity target, TargetPos targetPos) {
        if (target.level().isClientSide) {
            return target;
        }

        if (targetPos == null) {
            messageUser(user, Component.translatable("dislocate.draconicevolution.not_set").withStyle(ChatFormatting.RED));
            return target;
        }

        BCoreNetwork.sendSound(target.level(), target.blockPosition(), DESounds.PORTAL.get(), SoundSource.PLAYERS, 0.1F, target.level().random.nextFloat() * 0.1F + 0.9F, false);
        target = targetPos.teleport(target);
        Entity finalTarget = target;
        DelayedTask.run(1, () -> BCoreNetwork.sendSound(finalTarget.level(), finalTarget.blockPosition(), DESounds.PORTAL.get(), SoundSource.PLAYERS, 0.1F, finalTarget.level().random.nextFloat() * 0.1F + 0.9F, false));
        return target;
    }

    public void messageUser(Entity user, Component message) {
        if (user instanceof Player) {
//            ChatHelper.sendIndexed((PlayerEntity) user, message, 576);
            ((Player) user).displayClientMessage(message, true);
        }
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (entity instanceof Player && !(this instanceof DislocatorAdvanced)) {
            messageUser(player, Component.translatable("dislocate.draconicevolution.player_need_advanced").withStyle(ChatFormatting.RED));
            return true;
        }

        if (player.level().isClientSide || !entity.canChangeDimensions() || !(entity instanceof LivingEntity) || player.getCooldowns().getCooldownPercent(this, 0) > 0) {
            return true;
        }

        TargetPos location = getTargetPos(stack, player.level());
        player.getCooldowns().addCooldown(this, 20);
        dislocateEntity(stack, player, entity, location);
        stack.hurtAndBreak(1, player, e -> {});
        if (location != null) {
            messageUser(player, Component.literal(I18n.get("dislocate.draconicevolution.entity_sent_to") + " " + location.getReadableName(false)).withStyle(ChatFormatting.GREEN));
        }

        return true;
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (world.isClientSide) {
            return new InteractionResultHolder<>(InteractionResult.PASS, stack);
        }

        TargetPos targetPos = getTargetPos(stack, world);

        if (player.isShiftKeyDown()) {
            if (targetPos == null) {
                setLocation(stack, targetPos = new TargetPos(player));
                messageUser(player, Component.translatable("dislocate.draconicevolution.bound_to").append("{" + targetPos.getReadableName(false) + "}").withStyle(ChatFormatting.GREEN));
            } else {
                messageUser(player, Component.translatable("dislocate.draconicevolution.already_bound").withStyle(ChatFormatting.RED));
            }
            return new InteractionResultHolder<>(InteractionResult.PASS, stack);
        } else {
            if (targetPos == null) {
                messageUser(player, Component.translatable("dislocate.draconicevolution.not_set").withStyle(ChatFormatting.RED));
                return new InteractionResultHolder<>(InteractionResult.PASS, stack);
            }
            if (player.getHealth() > 2 || player.getAbilities().instabuild) {
                player.getCooldowns().addCooldown(this, 20);
                dislocateEntity(stack, player, player, targetPos);
                stack.hurtAndBreak(1, player, e -> {});

                if (!player.getAbilities().instabuild) {
                    player.setHealth(player.getHealth() - 2);
                }
            } else {
                messageUser(player, Component.translatable("dislocate.draconicevolution.low_health").withStyle(ChatFormatting.RED));
            }
            return new InteractionResultHolder<>(InteractionResult.PASS, stack);
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return getTargetPos(stack, null) != null;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flagIn) {
        TargetPos targetPos = getTargetPos(stack, world);
        if (targetPos == null) {
            tooltip.add(Component.translatable("dislocate.draconicevolution.un_set_info1").withStyle(ChatFormatting.RED));
            tooltip.add(Component.translatable("dislocate.draconicevolution.un_set_info2").withStyle(ChatFormatting.WHITE));
            tooltip.add(Component.translatable("dislocate.draconicevolution.un_set_info3").withStyle(ChatFormatting.WHITE));
            tooltip.add(Component.translatable("dislocate.draconicevolution.un_set_info4").withStyle(ChatFormatting.WHITE));
            tooltip.add(Component.translatable("dislocate.draconicevolution.un_set_info5").withStyle(ChatFormatting.WHITE));
        } else {
            tooltip.add(Component.translatable("dislocate.draconicevolution.bound_to").withStyle(ChatFormatting.GREEN));
            tooltip.add(Component.literal(ChatFormatting.WHITE + "{" + targetPos.getReadableName(flagIn.isAdvanced()) + "}"));
            tooltip.add(Component.translatable("dislocate.draconicevolution.uses_remain", stack.getMaxDamage() - stack.getDamageValue() + 1).withStyle(ChatFormatting.BLUE));
        }
    }

    public TargetPos getTargetPos(ItemStack stack, @Nullable Level world) {
        CompoundTag targetTag = stack.getTagElement("target");
        if (targetTag != null) {
            return new TargetPos(targetTag);
        }
        return null;
    }

    public void setLocation(ItemStack stack, TargetPos pos) {
        stack.addTagElement("target", pos.writeToNBT());
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return repair.getItem() == DEContent.INGOT_DRACONIUM.get();
    }

    @Override
    public float getXpRepairRatio(ItemStack stack) {
        return 1F;
    }

    @Override
    public void generateHudText(ItemStack stack, Player player, List<Component> displayList) {
        TargetPos location = getTargetPos(stack, player.level());
        if (location != null) {
            displayList.add(stack.getHoverName());
            displayList.add(Component.literal("{" + location.getReadableName(false) + ")"));
        }
    }

    @Override
    public boolean canBeHurtBy(DamageSource source) {
        return source.is(DamageTypes.FELL_OUT_OF_WORLD);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (entity.getAge() >= 0) {
            entity.setExtendedLifetime();
        }
        return super.onEntityItemUpdate(stack, entity);
    }
}
