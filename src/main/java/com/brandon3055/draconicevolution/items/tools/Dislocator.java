package com.brandon3055.draconicevolution.items.tools;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.brandonscore.network.BCoreNetwork;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.brandonscore.utils.TargetPos;
import com.brandon3055.draconicevolution.api.IHudDisplay;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.handlers.DESounds;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
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
import java.util.Comparator;
import java.util.List;

/**
 * Created by brandon3055 on 16/07/2016.
 */
public class Dislocator extends Item implements IHudDisplay {

    public Dislocator(Properties properties) {
        super(properties.isImmuneToFire());
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
        if (target.world.isRemote) {
            return target;
        }

        if (targetPos == null) {
            messageUser(user, new TranslationTextComponent("dislocate.draconicevolution.not_set").mergeStyle(TextFormatting.RED));
            return target;
        }

        BCoreNetwork.sendSound(target.world, target.getPosition(), DESounds.portal, SoundCategory.PLAYERS, 0.1F, target.world.rand.nextFloat() * 0.1F + 0.9F, false);
        target = targetPos.teleport(target);
        BCoreNetwork.sendSound(target.world, target.getPosition(), DESounds.portal, SoundCategory.PLAYERS, 0.1F, target.world.rand.nextFloat() * 0.1F + 0.9F, false);
        return target;
    }

    public void messageUser(Entity user, ITextComponent message) {
        if (user instanceof PlayerEntity) {
//            ChatHelper.sendIndexed((PlayerEntity) user, message, 576);
            ((PlayerEntity) user).sendStatusMessage(message, true);
        }
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        if (entity instanceof PlayerEntity && !(this instanceof DislocatorAdvanced)) {
            messageUser(player, new TranslationTextComponent("dislocate.draconicevolution.player_need_advanced").mergeStyle(TextFormatting.RED));
            return true;
        }

        if (player.world.isRemote || !entity.isNonBoss() || !(entity instanceof LivingEntity) || player.getCooldownTracker().getCooldown(this, 0) > 0) {
            return true;
        }

        if (player.getHealth() > 2 || player.abilities.isCreativeMode) {
            if (!player.abilities.isCreativeMode) {
                player.setHealth(player.getHealth() - 2);
            }

            TargetPos location = getTargetPos(stack, player.world);
            player.getCooldownTracker().setCooldown(this, 20);
            dislocateEntity(stack, player, entity, location);
            stack.damageItem(1, player, e -> {});
            if (location != null){
                messageUser(player, new StringTextComponent(I18n.format("dislocate.draconicevolution.entity_sent_to") + " "+ location.getReadableName(false)).mergeStyle(TextFormatting.GREEN));
            }

        } else if (player.world.isRemote) {
            messageUser(player, new TranslationTextComponent("dislocate.draconicevolution.low_health").mergeStyle(TextFormatting.RED));
        }

        return true;
    }


    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.isRemote) {
            return new ActionResult<>(ActionResultType.PASS, stack);
        }

        TargetPos targetPos = getTargetPos(stack, world);

        if (player.isSneaking()) {
            if (targetPos == null) {
                setLocation(stack, targetPos = new TargetPos(player));
                messageUser(player, new TranslationTextComponent("dislocate.draconicevolution.bound_to").appendString("{" + targetPos.getReadableName(false) + "}").mergeStyle(TextFormatting.GREEN));
            } else {
                messageUser(player, new TranslationTextComponent("dislocate.draconicevolution.already_bound").mergeStyle(TextFormatting.RED));
            }
            return new ActionResult<>(ActionResultType.PASS, stack);
        } else {
            if (targetPos == null) {
                messageUser(player, new TranslationTextComponent("dislocate.draconicevolution.not_set").mergeStyle(TextFormatting.RED));
                return new ActionResult<>(ActionResultType.PASS, stack);
            }
            if (player.getHealth() > 2 || player.abilities.isCreativeMode) {
                player.getCooldownTracker().setCooldown(this, 20);
                dislocateEntity(stack, player, player, targetPos);
                stack.damageItem(1, player, e -> {});

                if (!player.abilities.isCreativeMode) {
                    player.setHealth(player.getHealth() - 2);
                }
            } else {
                messageUser(player, new TranslationTextComponent("dislocate.draconicevolution.low_health").mergeStyle(TextFormatting.RED));
            }
            return new ActionResult<>(ActionResultType.PASS, stack);
        }
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return getTargetPos(stack, null) != null;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        TargetPos targetPos = getTargetPos(stack, world);
        if (targetPos == null) {
            tooltip.add(new TranslationTextComponent("dislocate.draconicevolution.un_set_info1").mergeStyle(TextFormatting.RED));
            tooltip.add(new TranslationTextComponent("dislocate.draconicevolution.un_set_info2").mergeStyle(TextFormatting.WHITE));
            tooltip.add(new TranslationTextComponent("dislocate.draconicevolution.un_set_info3").mergeStyle(TextFormatting.WHITE));
            tooltip.add(new TranslationTextComponent("dislocate.draconicevolution.un_set_info4").mergeStyle(TextFormatting.WHITE));
            tooltip.add(new TranslationTextComponent("dislocate.draconicevolution.un_set_info5").mergeStyle(TextFormatting.WHITE));
        } else {
            tooltip.add(new TranslationTextComponent("dislocate.draconicevolution.bound_to").mergeStyle(TextFormatting.GREEN));
            tooltip.add(new StringTextComponent(TextFormatting.WHITE + "{" + targetPos.getReadableName(flagIn.isAdvanced()) + "}"));
            tooltip.add(new TranslationTextComponent("dislocate.draconicevolution.uses_remain", stack.getMaxDamage() - stack.getDamage() + 1).mergeStyle(TextFormatting.BLUE));
        }
    }

    public TargetPos getTargetPos(ItemStack stack, @Nullable World world) {
        CompoundNBT targetTag = stack.getChildTag("target");
        if (targetTag != null) {
            return new TargetPos(targetTag);
        }
        return null;
    }

    public void setLocation(ItemStack stack, TargetPos pos) {
        stack.setTagInfo("target", pos.writeToNBT());
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Nullable
    @Override //Todo waiting on forge to fix shit
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
//        return new PersistentItemEntity(world, location, itemstack);
        if (location instanceof ItemEntity) {
            ((ItemEntity) location).age = -32767; //extra 27 minute despawn delay
        }
        return null;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return repair.getItem() == DEContent.ingot_draconium;
    }

    @Override
    public float getXpRepairRatio(ItemStack stack) {
        return 1F;
    }

    @Override
    public void addDisplayData(ItemStack stack, World world, @Nullable BlockPos pos, List<String> displayData) {
        TargetPos location = getTargetPos(stack, world);
        if (location != null) {
            displayData.add(stack.getDisplayName().getString());
            displayData.add("{" + location.getReadableName(false) + ")");
        }
    }

    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        if (event.getLeft().getItem() == DEContent.dislocator && event.getRight().getItem() == DEContent.ingot_draconium && event.getLeft().getDamage() > 0) {
            event.setOutput(event.getLeft().copy());
            event.getOutput().setDamage(0);
            event.setCost(1);
            event.setMaterialCost(1);
        }
    }
}
