package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.items.ItemBCore;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.brandonscore.utils.Teleporter.TeleportLocation;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.entity.EntityPersistentItem;
import com.brandon3055.draconicevolution.lib.DESoundHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Created by brandon3055 on 16/07/2016.
 */
public class Dislocator extends ItemBCore {

    public Dislocator() {
        this.setMaxStackSize(1);
        this.setMaxDamage(19);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (getLocation(stack, player.world) == null) {
            if (player.world.isRemote) {
                player.sendMessage(new TextComponentTranslation("msg.teleporterUnSet.txt"));
            }
            return true;
        }

        if (entity instanceof EntityPlayer && !(this instanceof DislocatorAdvanced)) {
            if (player.world.isRemote) {
                player.sendMessage(new TextComponentTranslation("msg.teleporterPlayerT1.txt"));
            }
            return true;
        }

        if (!entity.isNonBoss() || !(entity instanceof EntityLiving)) {
            return true;
        }

        if (player.getHealth() > 2 || player.capabilities.isCreativeMode) {
            if (!player.capabilities.isCreativeMode) {
                player.setHealth(player.getHealth() - 2);
            }

            if (!entity.world.isRemote) {
                DESoundHandler.playSoundFromServer(player.world, player.posX, player.posY, player.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
            }

            getLocation(stack, player.world).teleport(entity);
            stack.damageItem(1, player);
            if (stack.getCount() <= 0) {
                player.inventory.deleteStack(stack);
            }


            if (!entity.world.isRemote) {
                DESoundHandler.playSoundFromServer(player.world, player.posX, player.posY, player.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
            }

            if (player.world.isRemote) {
                TeleportLocation location = getLocation(stack, player.world);
                player.sendMessage(new TextComponentString(new TextComponentTranslation("msg.teleporterSentMob.txt").getFormattedText() + " x:" + (int) location.getXCoord() + " y:" + (int) location.getYCoord() + " z:" + (int) location.getZCoord() + " Dimension: " + location.getDimensionName()));
            }
        }
        else if (player.world.isRemote) {
            player.sendMessage(new TextComponentTranslation("msg.teleporterLowHealth.txt"));
        }

        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            if (getLocation(stack, world) == null) {
                if (world.isRemote) {
                    player.sendMessage(new TextComponentString(new TextComponentTranslation("msg.teleporterBound.txt").getFormattedText() + "{X:" + (int) player.posX + " Y:" + (int) player.posY + " Z:" + (int) player.posZ + " Dim:" + player.world.provider.getDimensionType().getName() + "}"));
                }
                else {
                    ItemNBTHelper.setDouble(stack, "X", player.posX);
                    ItemNBTHelper.setDouble(stack, "Y", player.posY);
                    ItemNBTHelper.setDouble(stack, "Z", player.posZ);
                    ItemNBTHelper.setFloat(stack, "Yaw", player.rotationYaw);
                    ItemNBTHelper.setFloat(stack, "Pitch", player.rotationPitch);
                    ItemNBTHelper.setInteger(stack, "Dimension", player.dimension);
                    ItemNBTHelper.setBoolean(stack, "IsSet", true);
                    ItemNBTHelper.setString(stack, "DimentionName", BrandonsCore.proxy.getMCServer().getWorld(player.dimension).provider.getDimensionType().getName());//TODO Is this really needed?
                }
                return new ActionResult<>(EnumActionResult.PASS, stack);
            }
            else if (world.isRemote) {
                player.sendMessage(new TextComponentTranslation("msg.teleporterAlreadySet.txt"));
            }

            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        else {
            if (getLocation(stack, world) == null) {
                if (world.isRemote) {
                    player.sendMessage(new TextComponentTranslation("msg.teleporterUnSet.txt"));
                }
                return new ActionResult<>(EnumActionResult.PASS, stack);
            }

            if (player.getHealth() > 2 || player.capabilities.isCreativeMode) {

                if (!world.isRemote) {
                    DESoundHandler.playSoundFromServer(player.world, player.posX, player.posY, player.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
                }

                getLocation(stack, world).teleport(player);

                if (!world.isRemote) {
                    DESoundHandler.playSoundFromServer(player.world, player.posX, player.posY, player.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
                }

                stack.damageItem(1, player);

                if (!player.capabilities.isCreativeMode) {
                    player.setHealth(player.getHealth() - 2);
                }
            }
            else if (world.isRemote) {
                player.sendMessage(new TextComponentTranslation("msg.teleporterLowHealth.txt"));
            }
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }

    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced) {
        if (!ItemNBTHelper.getBoolean(stack, "IsSet", false)) {
            tooltip.add(TextFormatting.RED + I18n.format("info.teleporterInfUnset1.txt"));
            tooltip.add(TextFormatting.WHITE + I18n.format("info.teleporterInfUnset2.txt"));
            tooltip.add(TextFormatting.WHITE + I18n.format("info.teleporterInfUnset3.txt"));
            tooltip.add(TextFormatting.WHITE + I18n.format("info.teleporterInfUnset4.txt"));
            tooltip.add(TextFormatting.WHITE + I18n.format("info.teleporterInfUnset5.txt"));
        }
        else {
            tooltip.add(TextFormatting.GREEN + I18n.format("info.teleporterInfSet1.txt"));
            tooltip.add(TextFormatting.WHITE + "{x:" + (int) ItemNBTHelper.getDouble(stack, "X", 0) + " y:" + (int) ItemNBTHelper.getDouble(stack, "Y", 0) + " z:" + (int) ItemNBTHelper.getDouble(stack, "Z", 0) + " Dim:" + getLocation(stack, world).getDimensionName() + "}");
            tooltip.add(TextFormatting.BLUE + String.valueOf(stack.getMaxDamage() - stack.getItemDamage() + 1) + " " + I18n.format("info.teleporterInfSet2.txt"));
        }
    }

    public TeleportLocation getLocation(ItemStack stack, World world) {
        if (!ItemNBTHelper.getBoolean(stack, "IsSet", false)) {
            return null;
        }

        TeleportLocation location = new TeleportLocation();
        location.readFromNBT(stack.getTagCompound());

        return location;
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return new EntityPersistentItem(world, location, itemstack);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return repair != null && repair.getItem() == DEFeatures.draconiumIngot;
    }
}
