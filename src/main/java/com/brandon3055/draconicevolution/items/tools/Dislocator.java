package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.items.ItemBCore;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.brandonscore.utils.Teleporter.TeleportLocation;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.handlers.DESoundHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

/**
 * Created by brandon3055 on 16/07/2016.
 */
public class Dislocator extends ItemBCore {

    public Dislocator(Properties properties) {
        super(properties);
    }

//    public Dislocator() {
//        this.setMaxStackSize(1);
//        this.setMaxDamage(19);
//    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        if (getLocation(stack, player.world) == null) {
            if (player.world.isRemote) {
                player.sendMessage(new TranslationTextComponent("msg.teleporterUnSet.txt"), Util.DUMMY_UUID);
            }
            return true;
        }

        if (entity instanceof PlayerEntity && !(this instanceof DislocatorAdvanced)) {
            if (player.world.isRemote) {
                player.sendMessage(new TranslationTextComponent("msg.teleporterPlayerT1.txt"), Util.DUMMY_UUID);
            }
            return true;
        }

        if (!entity.isNonBoss() || !(entity instanceof LivingEntity)) {
            return true;
        }

        if (player.getHealth() > 2 || player.abilities.isCreativeMode) {
            if (!player.abilities.isCreativeMode) {
                player.setHealth(player.getHealth() - 2);
            }

            if (!entity.world.isRemote) {
                DESoundHandler.playSoundFromServer(player.world, player.getPosX(), player.getPosY(), player.getPosZ(), DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
            }

            getLocation(stack, player.world).teleport(entity);
            stack.damageItem(1, player, e -> {});
            if (stack.getCount() <= 0) {
                player.inventory.deleteStack(stack);
            }


            if (!entity.world.isRemote) {
                DESoundHandler.playSoundFromServer(player.world, player.getPosX(), player.getPosY(), player.getPosZ(), DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
            }

            if (player.world.isRemote) {
                TeleportLocation location = getLocation(stack, player.world);
                player.sendMessage(new StringTextComponent(I18n.format("msg.teleporterSentMob.txt") + " x:" + (int) location.getXCoord() + " y:" + (int) location.getYCoord() + " z:" + (int) location.getZCoord() + " Dimension: " + location.getDimensionName()), Util.DUMMY_UUID);
            }
        }
        else if (player.world.isRemote) {
            player.sendMessage(new TranslationTextComponent("msg.teleporterLowHealth.txt"), Util.DUMMY_UUID);
        }

        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            if (getLocation(stack, world) == null) {
                if (world.isRemote) {
//                    player.sendMessage(new StringTextComponent(new TranslationTextComponent("msg.teleporterBound.txt").getFormattedText() + "{X:" + (int) player.getPosX() + " Y:" + (int) player.getPosY() + " Z:" + (int) player.getPosZ() + " Dim:" + player.world.getDimension().getType().getName() + "}"));
                }
                else {
                    ItemNBTHelper.setDouble(stack, "X", player.getPosX());
                    ItemNBTHelper.setDouble(stack, "Y", player.getPosY());
                    ItemNBTHelper.setDouble(stack, "Z", player.getPosZ());
                    ItemNBTHelper.setFloat(stack, "Yaw", player.rotationYaw);
                    ItemNBTHelper.setFloat(stack, "Pitch", player.rotationPitch);
                    ItemNBTHelper.setString(stack, "Dimension", player.world.getDimensionKey().getLocation().toString());
                    ItemNBTHelper.setBoolean(stack, "IsSet", true);
//                    ItemNBTHelper.setString(stack, "DimentionName", BrandonsCore.proxy.getMCServer().getWorld(player.dimension).getDimension().tra);//TODO Is this really needed?
                }
                return new ActionResult<>(ActionResultType.PASS, stack);
            }
            else if (world.isRemote) {
                player.sendMessage(new TranslationTextComponent("msg.teleporterAlreadySet.txt"), Util.DUMMY_UUID);
            }

            return new ActionResult<>(ActionResultType.PASS, stack);
        }
        else {
            if (getLocation(stack, world) == null) {
                if (world.isRemote) {
                    player.sendMessage(new TranslationTextComponent("msg.teleporterUnSet.txt"), Util.DUMMY_UUID);
                }
                return new ActionResult<>(ActionResultType.PASS, stack);
            }

            if (player.getHealth() > 2 || player.abilities.isCreativeMode) {

                if (!world.isRemote) {
                    DESoundHandler.playSoundFromServer(player.world, player.getPosX(), player.getPosY(), player.getPosZ(), DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
                }

                getLocation(stack, world).teleport(player);

                if (!world.isRemote) {
                    DESoundHandler.playSoundFromServer(player.world, player.getPosX(), player.getPosY(), player.getPosZ(), DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
                }

                stack.damageItem(1, player, e -> {});

                if (!player.abilities.isCreativeMode) {
                    player.setHealth(player.getHealth() - 2);
                }
            }
            else if (world.isRemote) {
                player.sendMessage(new TranslationTextComponent("msg.teleporterLowHealth.txt"), Util.DUMMY_UUID);
            }
            return new ActionResult<>(ActionResultType.PASS, stack);
        }

    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (!ItemNBTHelper.getBoolean(stack, "IsSet", false)) {
            tooltip.add(new TranslationTextComponent("info.teleporterInfUnset1.txt").mergeStyle(TextFormatting.RED));
            tooltip.add(new TranslationTextComponent("info.teleporterInfUnset2.txt").mergeStyle(TextFormatting.WHITE));
            tooltip.add(new TranslationTextComponent("info.teleporterInfUnset3.txt").mergeStyle(TextFormatting.WHITE));
            tooltip.add(new TranslationTextComponent("info.teleporterInfUnset4.txt").mergeStyle(TextFormatting.WHITE));
            tooltip.add(new TranslationTextComponent("info.teleporterInfUnset5.txt").mergeStyle(TextFormatting.WHITE));
        }
        else {
            tooltip.add(new TranslationTextComponent("info.teleporterInfSet1.txt").mergeStyle(TextFormatting.GREEN));
            tooltip.add(new StringTextComponent(TextFormatting.WHITE + "{x:" + (int) ItemNBTHelper.getDouble(stack, "X", 0) + " y:" + (int) ItemNBTHelper.getDouble(stack, "Y", 0) + " z:" + (int) ItemNBTHelper.getDouble(stack, "Z", 0) + " Dim:" + getLocation(stack, world).getDimensionName() + "}"));
            tooltip.add(new StringTextComponent(TextFormatting.BLUE + String.valueOf(stack.getMaxDamage() - stack.getDamage() + 1) + " " + I18n.format("info.teleporterInfSet2.txt")));
        }
    }

    public TeleportLocation getLocation(ItemStack stack, World world) {
        if (!ItemNBTHelper.getBoolean(stack, "IsSet", false)) {
            return null;
        }

        TeleportLocation location = new TeleportLocation();
        location.readFromNBT(stack.getTag());

        return location;
    }

//    @Override
//    public boolean hasCustomEntity(ItemStack stack) {
//        return true;
//    }

//    @Override
//    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
//        return new EntityPersistentItem(world, location, itemstack);
//    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return repair != null && repair.getItem() == DEContent.ingot_draconium;
    }
}
