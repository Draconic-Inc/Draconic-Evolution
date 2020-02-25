package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.items.ItemBCore;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.brandonscore.utils.Teleporter.TeleportLocation;
import com.brandon3055.draconicevolution.DEContent;
import com.brandon3055.draconicevolution.entity.EntityPersistentItem;
import com.brandon3055.draconicevolution.lib.DESoundHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
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
                player.sendMessage(new TranslationTextComponent("msg.teleporterUnSet.txt"));
            }
            return true;
        }

        if (entity instanceof PlayerEntity && !(this instanceof DislocatorAdvanced)) {
            if (player.world.isRemote) {
                player.sendMessage(new TranslationTextComponent("msg.teleporterPlayerT1.txt"));
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
                DESoundHandler.playSoundFromServer(player.world, player.posX, player.posY, player.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
            }

            getLocation(stack, player.world).teleport(entity);
            stack.damageItem(1, player, e -> {});
            if (stack.getCount() <= 0) {
                player.inventory.deleteStack(stack);
            }


            if (!entity.world.isRemote) {
                DESoundHandler.playSoundFromServer(player.world, player.posX, player.posY, player.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
            }

            if (player.world.isRemote) {
                TeleportLocation location = getLocation(stack, player.world);
                player.sendMessage(new StringTextComponent(I18n.format("msg.teleporterSentMob.txt") + " x:" + (int) location.getXCoord() + " y:" + (int) location.getYCoord() + " z:" + (int) location.getZCoord() + " Dimension: " + location.getDimensionName()));
            }
        }
        else if (player.world.isRemote) {
            player.sendMessage(new TranslationTextComponent("msg.teleporterLowHealth.txt"));
        }

        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            if (getLocation(stack, world) == null) {
                if (world.isRemote) {
//                    player.sendMessage(new StringTextComponent(new TranslationTextComponent("msg.teleporterBound.txt").getFormattedText() + "{X:" + (int) player.posX + " Y:" + (int) player.posY + " Z:" + (int) player.posZ + " Dim:" + player.world.getDimension().getType().getName() + "}"));
                }
                else {
                    ItemNBTHelper.setDouble(stack, "X", player.posX);
                    ItemNBTHelper.setDouble(stack, "Y", player.posY);
                    ItemNBTHelper.setDouble(stack, "Z", player.posZ);
                    ItemNBTHelper.setFloat(stack, "Yaw", player.rotationYaw);
                    ItemNBTHelper.setFloat(stack, "Pitch", player.rotationPitch);
                    ItemNBTHelper.setString(stack, "Dimension", player.dimension.getRegistryName().toString());
                    ItemNBTHelper.setBoolean(stack, "IsSet", true);
//                    ItemNBTHelper.setString(stack, "DimentionName", BrandonsCore.proxy.getMCServer().getWorld(player.dimension).getDimension().tra);//TODO Is this really needed?
                }
                return new ActionResult<>(ActionResultType.PASS, stack);
            }
            else if (world.isRemote) {
                player.sendMessage(new TranslationTextComponent("msg.teleporterAlreadySet.txt"));
            }

            return new ActionResult<>(ActionResultType.PASS, stack);
        }
        else {
            if (getLocation(stack, world) == null) {
                if (world.isRemote) {
                    player.sendMessage(new TranslationTextComponent("msg.teleporterUnSet.txt"));
                }
                return new ActionResult<>(ActionResultType.PASS, stack);
            }

            if (player.getHealth() > 2 || player.abilities.isCreativeMode) {

                if (!world.isRemote) {
                    DESoundHandler.playSoundFromServer(player.world, player.posX, player.posY, player.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
                }

                getLocation(stack, world).teleport(player);

                if (!world.isRemote) {
                    DESoundHandler.playSoundFromServer(player.world, player.posX, player.posY, player.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
                }

                stack.damageItem(1, player, e -> {});

                if (!player.abilities.isCreativeMode) {
                    player.setHealth(player.getHealth() - 2);
                }
            }
            else if (world.isRemote) {
                player.sendMessage(new TranslationTextComponent("msg.teleporterLowHealth.txt"));
            }
            return new ActionResult<>(ActionResultType.PASS, stack);
        }

    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (!ItemNBTHelper.getBoolean(stack, "IsSet", false)) {
            tooltip.add(new TranslationTextComponent("info.teleporterInfUnset1.txt").setStyle(new Style().setColor(TextFormatting.RED)));
            tooltip.add(new TranslationTextComponent("info.teleporterInfUnset2.txt").setStyle(new Style().setColor(TextFormatting.WHITE)));
            tooltip.add(new TranslationTextComponent("info.teleporterInfUnset3.txt").setStyle(new Style().setColor(TextFormatting.WHITE)));
            tooltip.add(new TranslationTextComponent("info.teleporterInfUnset4.txt").setStyle(new Style().setColor(TextFormatting.WHITE)));
            tooltip.add(new TranslationTextComponent("info.teleporterInfUnset5.txt").setStyle(new Style().setColor(TextFormatting.WHITE)));
        }
        else {
            tooltip.add(new TranslationTextComponent("info.teleporterInfSet1.txt").setStyle(new Style().setColor(TextFormatting.GREEN)));
//            tooltip.add(TextFormatting.WHITE + "{x:" + (int) ItemNBTHelper.getDouble(stack, "X", 0) + " y:" + (int) ItemNBTHelper.getDouble(stack, "Y", 0) + " z:" + (int) ItemNBTHelper.getDouble(stack, "Z", 0) + " Dim:" + getLocation(stack, world).getDimensionName() + "}");
//            tooltip.add(TextFormatting.BLUE + String.valueOf(stack.getMaxDamage() - stack.getItemDamage() + 1) + " " + I18n.format("info.teleporterInfSet2.txt"));
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
