package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.api.hud.IHudItem;
import com.brandon3055.brandonscore.network.BCoreNetwork;
import com.brandon3055.brandonscore.utils.TargetPos;
import com.brandon3055.brandonscore.api.hud.IHudDisplay;
import com.brandon3055.draconicevolution.entity.PersistentItemEntity;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.handlers.DESounds;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.AnvilUpdateEvent;

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
        if (target.level.isClientSide) {
            return target;
        }

        if (targetPos == null) {
            messageUser(user, new TranslationTextComponent("dislocate.draconicevolution.not_set").withStyle(TextFormatting.RED));
            return target;
        }

        BCoreNetwork.sendSound(target.level, target.blockPosition(), DESounds.portal, SoundCategory.PLAYERS, 0.1F, target.level.random.nextFloat() * 0.1F + 0.9F, false);
        target = targetPos.teleport(target);
        BCoreNetwork.sendSound(target.level, target.blockPosition(), DESounds.portal, SoundCategory.PLAYERS, 0.1F, target.level.random.nextFloat() * 0.1F + 0.9F, false);
        return target;
    }

    public void messageUser(Entity user, ITextComponent message) {
        if (user instanceof PlayerEntity) {
//            ChatHelper.sendIndexed((PlayerEntity) user, message, 576);
            ((PlayerEntity) user).displayClientMessage(message, true);
        }
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        if (entity instanceof PlayerEntity && !(this instanceof DislocatorAdvanced)) {
            messageUser(player, new TranslationTextComponent("dislocate.draconicevolution.player_need_advanced").withStyle(TextFormatting.RED));
            return true;
        }

        if (player.level.isClientSide || !entity.canChangeDimensions() || !(entity instanceof LivingEntity) || player.getCooldowns().getCooldownPercent(this, 0) > 0) {
            return true;
        }

        TargetPos location = getTargetPos(stack, player.level);
        player.getCooldowns().addCooldown(this, 20);
        dislocateEntity(stack, player, entity, location);
        stack.hurtAndBreak(1, player, e -> {});
        if (location != null) {
            messageUser(player, new StringTextComponent(I18n.get("dislocate.draconicevolution.entity_sent_to") + " " + location.getReadableName(false)).withStyle(TextFormatting.GREEN));
        }

        return true;
    }


    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (world.isClientSide) {
            return new ActionResult<>(ActionResultType.PASS, stack);
        }

        TargetPos targetPos = getTargetPos(stack, world);

        if (player.isShiftKeyDown()) {
            if (targetPos == null) {
                setLocation(stack, targetPos = new TargetPos(player));
                messageUser(player, new TranslationTextComponent("dislocate.draconicevolution.bound_to").append("{" + targetPos.getReadableName(false) + "}").withStyle(TextFormatting.GREEN));
            } else {
                messageUser(player, new TranslationTextComponent("dislocate.draconicevolution.already_bound").withStyle(TextFormatting.RED));
            }
            return new ActionResult<>(ActionResultType.PASS, stack);
        } else {
            if (targetPos == null) {
                messageUser(player, new TranslationTextComponent("dislocate.draconicevolution.not_set").withStyle(TextFormatting.RED));
                return new ActionResult<>(ActionResultType.PASS, stack);
            }
            if (player.getHealth() > 2 || player.abilities.instabuild) {
                player.getCooldowns().addCooldown(this, 20);
                dislocateEntity(stack, player, player, targetPos);
                stack.hurtAndBreak(1, player, e -> {});

                if (!player.abilities.instabuild) {
                    player.setHealth(player.getHealth() - 2);
                }
            } else {
                messageUser(player, new TranslationTextComponent("dislocate.draconicevolution.low_health").withStyle(TextFormatting.RED));
            }
            return new ActionResult<>(ActionResultType.PASS, stack);
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return getTargetPos(stack, null) != null;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        TargetPos targetPos = getTargetPos(stack, world);
        if (targetPos == null) {
            tooltip.add(new TranslationTextComponent("dislocate.draconicevolution.un_set_info1").withStyle(TextFormatting.RED));
            tooltip.add(new TranslationTextComponent("dislocate.draconicevolution.un_set_info2").withStyle(TextFormatting.WHITE));
            tooltip.add(new TranslationTextComponent("dislocate.draconicevolution.un_set_info3").withStyle(TextFormatting.WHITE));
            tooltip.add(new TranslationTextComponent("dislocate.draconicevolution.un_set_info4").withStyle(TextFormatting.WHITE));
            tooltip.add(new TranslationTextComponent("dislocate.draconicevolution.un_set_info5").withStyle(TextFormatting.WHITE));
        } else {
            tooltip.add(new TranslationTextComponent("dislocate.draconicevolution.bound_to").withStyle(TextFormatting.GREEN));
            tooltip.add(new StringTextComponent(TextFormatting.WHITE + "{" + targetPos.getReadableName(flagIn.isAdvanced()) + "}"));
            tooltip.add(new TranslationTextComponent("dislocate.draconicevolution.uses_remain", stack.getMaxDamage() - stack.getDamageValue() + 1).withStyle(TextFormatting.BLUE));
        }
    }

    public TargetPos getTargetPos(ItemStack stack, @Nullable World world) {
        CompoundNBT targetTag = stack.getTagElement("target");
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

    @Nullable
    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return new PersistentItemEntity(world, location, itemstack);
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return repair.getItem() == DEContent.ingot_draconium;
    }

    @Override
    public float getXpRepairRatio(ItemStack stack) {
        return 1F;
    }

    @Override
    public void generateHudText(ItemStack stack, PlayerEntity player, List<ITextComponent> displayList) {
        TargetPos location = getTargetPos(stack, player.level);
        if (location != null) {
            displayList.add(stack.getHoverName());
            displayList.add(new StringTextComponent("{" + location.getReadableName(false) + ")"));
        }
    }

    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        if (event.getLeft().getItem() == DEContent.dislocator && event.getRight().getItem() == DEContent.ingot_draconium && event.getLeft().getDamageValue() > 0) {
            event.setOutput(event.getLeft().copy());
            event.getOutput().setDamageValue(0);
            event.setCost(1);
            event.setMaterialCost(1);
        }
    }
}
