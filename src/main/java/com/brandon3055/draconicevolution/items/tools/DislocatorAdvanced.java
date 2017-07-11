package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.utils.InfoHelper;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.brandonscore.utils.Teleporter;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.GuiHandler;
import com.brandon3055.draconicevolution.api.IHudDisplay;
import com.brandon3055.draconicevolution.entity.EntityPersistentItem;
import com.brandon3055.draconicevolution.lib.DESoundHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by brandon3055 on 16/07/2016.
 */
public class DislocatorAdvanced extends Dislocator implements IHudDisplay {

    public DislocatorAdvanced() {
        this.setMaxStackSize(1);
        this.setNoRepair();
    }

    //region Item Interact

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (getLocation(stack) == null) {
            if (player.world.isRemote) {
                player.sendMessage(new TextComponentTranslation("msg.teleporterUnSet.txt"));
            }
            return true;
        }

        int fuel = ItemNBTHelper.getInteger(stack, "Fuel", 0);
        World world = player.world;

        if (!player.capabilities.isCreativeMode && fuel <= 0) {
            if (world.isRemote) player.sendMessage(new TextComponentTranslation("msg.teleporterOutOfFuel.txt"));
            return true;
        }

        if (entity instanceof EntityPlayer) {
            if (entity.isSneaking()) {
                getLocation(stack).teleport(entity);
                if (!player.capabilities.isCreativeMode && fuel > 0) {
                    ItemNBTHelper.setInteger(stack, "Fuel", fuel - 1);
                }
            }
            else {
                if (world.isRemote) {
                    player.sendMessage(new TextComponentTranslation("msg.teleporterPlayerConsent.txt"));
                }
            }
            return true;
        }

        if (!entity.isNonBoss() || !(entity instanceof EntityLiving)) {
            return true;
        }

        if (!entity.world.isRemote) {
            DESoundHandler.playSoundFromServer(player.world, player.posX, player.posY, player.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
        }

        getLocation(stack).teleport(entity);

        if (!entity.world.isRemote) {
            DESoundHandler.playSoundFromServer(player.world, player.posX, player.posY, player.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
        }

        if (player.world.isRemote) {
            player.sendMessage(new TextComponentString(new TextComponentTranslation("msg.teleporterSentMob.txt").getFormattedText() + " x:" + (int) getLocation(stack).getXCoord() + " y:" + (int) getLocation(stack).getYCoord() + " z:" + (int) getLocation(stack).getZCoord() + " Dimension: " + getLocation(stack).getDimensionName()));
        }

        if (!player.capabilities.isCreativeMode && fuel > 0) {
            ItemNBTHelper.setInteger(stack, "Fuel", fuel - 1);
        }

        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        int fuel = ItemNBTHelper.getInteger(stack, "Fuel", 0);

        if (player.isSneaking()) {
            if (world.isRemote) {
                FMLNetworkHandler.openGui(player, DraconicEvolution.instance, GuiHandler.GUIID_TELEPORTER, world, (int) player.posX, (int) player.posY, (int) player.posZ);
            }
        }
        else {

            if (getLocation(stack) == null) {
                if (world.isRemote) {
                    FMLNetworkHandler.openGui(player, DraconicEvolution.instance, GuiHandler.GUIID_TELEPORTER, world, (int) player.posX, (int) player.posY, (int) player.posZ);
                }
                return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
            }

            if (!player.capabilities.isCreativeMode && fuel <= 0) {
                if (world.isRemote) {
                    player.sendMessage(new TextComponentTranslation("msg.teleporterOutOfFuel.txt"));
                }
                return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
            }

            if (!player.capabilities.isCreativeMode && fuel > 0) {
                ItemNBTHelper.setInteger(stack, "Fuel", fuel - 1);
            }

            if (!world.isRemote) {
                DESoundHandler.playSoundFromServer(player.world, player.posX, player.posY, player.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
            }
            getLocation(stack).teleport(player);
            if (!world.isRemote) {
                DESoundHandler.playSoundFromServer(player.world, player.posX, player.posY, player.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
            }
        }
        return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
    }

    //endregion

    //region Teleporter

    public Teleporter.TeleportLocation getLocation(ItemStack stack) {
        short selected = ItemNBTHelper.getShort(stack, "Selection", (short) 0);
        int selrctionOffset = ItemNBTHelper.getInteger(stack, "SelectionOffset", 0);
        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null) {
            return null;
        }
        NBTTagList list = compound.getTagList("Locations", 10);
        Teleporter.TeleportLocation destination = new Teleporter.TeleportLocation();
        destination.readFromNBT(list.getCompoundTagAt(selected + selrctionOffset));
        if (destination.getName().isEmpty()) {
            return null;
        }

        return destination;
    }

    //endregion

    //region Misc

    @Override
    public void addInformation(ItemStack stack, World playerIn, List<String> tooltip, ITooltipFlag advanced) {
        short selected = ItemNBTHelper.getShort(stack, "Selection", (short) 0);
        int selrctionOffset = ItemNBTHelper.getInteger(stack, "SelectionOffset", 0);
        NBTTagCompound compound = ItemNBTHelper.getCompound(stack);

        NBTTagList list = compound.getTagList("Locations", 10);
        String selectedDest = list.getCompoundTagAt(selected + selrctionOffset).getString("Name");

        tooltip.add(TextFormatting.GOLD + "" + selectedDest);
        if (InfoHelper.holdShiftForDetails(tooltip)) {
            tooltip.add(TextFormatting.WHITE + I18n.format("info.teleporterInfFuel.txt") + " " + ItemNBTHelper.getInteger(stack, "Fuel", 0));
            tooltip.add(TextFormatting.DARK_PURPLE + "" + TextFormatting.ITALIC + I18n.format("info.teleporterInfGUI.txt"));
            tooltip.add(TextFormatting.DARK_PURPLE + "" + TextFormatting.ITALIC + I18n.format("info.teleporterInfScroll.txt"));
        }
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
    public void addDisplayData(@Nullable ItemStack stack, World world, @Nullable BlockPos pos, List<String> displayData) {
        Teleporter.TeleportLocation location = getLocation(stack);
        if (location != null) {
            displayData.add(location.getName());
        }
        displayData.add(I18n.format("info.teleporterInfFuel.txt") + " " + ItemNBTHelper.getInteger(stack, "Fuel", 0));
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false;
    }

    //endregion
}
