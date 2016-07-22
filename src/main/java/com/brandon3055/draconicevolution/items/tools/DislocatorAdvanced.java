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
            if (player.worldObj.isRemote) {
                player.addChatMessage(new TextComponentTranslation("msg.teleporterUnSet.txt"));
            }
            return true;
        }

        if (entity instanceof EntityPlayer) {
            if (player.worldObj.isRemote) {
                player.addChatMessage(new TextComponentTranslation("msg.teleporterPlayerT1.txt"));
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

            if (!entity.worldObj.isRemote) {
                DESoundHandler.playSoundFromServer(player.worldObj, player.posX, player.posY, player.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.worldObj.rand.nextFloat() * 0.1F + 0.9F, false, 32);
            }

            getLocation(stack).teleport(entity);

            if (!entity.worldObj.isRemote) {
                DESoundHandler.playSoundFromServer(player.worldObj, player.posX, player.posY, player.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.worldObj.rand.nextFloat() * 0.1F + 0.9F, false, 32);
            }

            if (player.worldObj.isRemote) {
                player.addChatMessage(new TextComponentString(new TextComponentTranslation("msg.teleporterSentMob.txt").getFormattedText() + " x:" + (int) getLocation(stack).getXCoord() + " y:" + (int) getLocation(stack).getYCoord() + " z:" + (int) getLocation(stack).getZCoord() + " Dimension: " + getLocation(stack).getDimensionName()));
            }
        }
        else if (player.worldObj.isRemote){
            player.addChatMessage(new TextComponentTranslation("msg.teleporterLowHealth.txt"));
        }

        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
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
                    player.addChatMessage(new TextComponentString("msg.teleporterOutOfFuel.txt"));
                }
                return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
            }

            if (!player.capabilities.isCreativeMode && fuel > 0) {
                ItemNBTHelper.setInteger(stack, "Fuel", fuel - 1);
            }

            if (!world.isRemote) {
                DESoundHandler.playSoundFromServer(player.worldObj, player.posX, player.posY, player.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.worldObj.rand.nextFloat() * 0.1F + 0.9F, false, 32);
            }
            getLocation(stack).teleport(player);
            if (!world.isRemote) {
                DESoundHandler.playSoundFromServer(player.worldObj, player.posX, player.posY, player.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.worldObj.rand.nextFloat() * 0.1F + 0.9F, false, 32);
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
        NBTTagList list = (NBTTagList) compound.getTag("Locations");
        if (list == null) {
            return null;
        }

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
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        short selected = ItemNBTHelper.getShort(stack, "Selection", (short) 0);
        int selrctionOffset = ItemNBTHelper.getInteger(stack, "SelectionOffset", 0);
        NBTTagCompound compound = ItemNBTHelper.getCompound(stack);

        NBTTagList list = (NBTTagList) compound.getTag("Locations");
        if (list == null) {
            list = new NBTTagList();
        }
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
