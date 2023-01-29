package com.brandon3055.draconicevolution.common.items.tools;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import com.brandon3055.brandonscore.common.utills.Teleporter.TeleportLocation;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.entity.EntityPersistentItem;
import com.brandon3055.draconicevolution.common.items.ItemDE;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TeleporterMKI extends ItemDE {

    public TeleporterMKI(boolean MKII) {}

    public TeleporterMKI() {
        this.setUnlocalizedName(Strings.teleporterMKIName);
        this.setCreativeTab(DraconicEvolution.tabToolsWeapons);
        this.setMaxDamage(19);
        this.setMaxStackSize(1);
        ModItems.register(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + Strings.teleporterMKIName);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (getLocation(stack) == null) {
            if (player.worldObj.isRemote)
                player.addChatMessage(new ChatComponentTranslation("msg.teleporterUnSet.txt"));
            return true;
        }

        if (entity instanceof EntityPlayer) {
            if (player.worldObj.isRemote)
                player.addChatMessage(new ChatComponentTranslation("msg.teleporterPlayerT1.txt"));
            return true;
        }

        if (entity instanceof IBossDisplayData || !(entity instanceof EntityLiving)) return true;

        if (player.getHealth() > 2 || player.capabilities.isCreativeMode) {
            stack.damageItem(1, player);
            if (!player.capabilities.isCreativeMode) player.setHealth(player.getHealth() - 2);
            getLocation(stack).sendEntityToCoords(entity);
            if (player.worldObj.isRemote) player.addChatMessage(
                    new ChatComponentText(
                            new ChatComponentTranslation("msg.teleporterSentMob.txt").getFormattedText() + " x:"
                                    + (int) getLocation(stack).getXCoord()
                                    + " y:"
                                    + (int) getLocation(stack).getYCoord()
                                    + " z:"
                                    + (int) getLocation(stack).getZCoord()
                                    + " Dimension: "
                                    + getLocation(stack).getDimensionName()));
        } else if (player.worldObj.isRemote)
            player.addChatMessage(new ChatComponentTranslation("msg.teleporterLowHealth.txt"));

        return true;
    }

    @Override
    public ItemStack onItemRightClick(final ItemStack stack, final World world, final EntityPlayer player) {
        if (player.isSneaking()) {
            if (getLocation(stack) == null) {
                if (world.isRemote) {
                    player.addChatMessage(
                            new ChatComponentText(
                                    new ChatComponentTranslation("msg.teleporterBound.txt").getFormattedText() + "{X:"
                                            + (int) player.posX
                                            + " Y:"
                                            + (int) player.posY
                                            + " Z:"
                                            + (int) player.posZ
                                            + " Dim:"
                                            + player.worldObj.provider.getDimensionName()
                                            + "}"));
                } else {
                    ItemNBTHelper.setDouble(stack, "X", player.posX);
                    ItemNBTHelper.setDouble(stack, "Y", player.posY);
                    ItemNBTHelper.setDouble(stack, "Z", player.posZ);
                    ItemNBTHelper.setFloat(stack, "Yaw", player.rotationYaw);
                    ItemNBTHelper.setFloat(stack, "Pitch", player.rotationPitch);
                    ItemNBTHelper.setInteger(stack, "Dimension", player.dimension);
                    ItemNBTHelper.setBoolean(stack, "IsSet", true);
                    ItemNBTHelper.setString(
                            stack,
                            "DimentionName",
                            BrandonsCore.proxy.getMCServer().worldServerForDimension(player.dimension).provider
                                    .getDimensionName());
                }
                return stack;
            } else
                if (world.isRemote) player.addChatMessage(new ChatComponentTranslation("msg.teleporterAlreadySet.txt"));

            return stack;
        } else {
            if (getLocation(stack) == null) {
                if (world.isRemote) player.addChatMessage(new ChatComponentTranslation("msg.teleporterUnSet.txt"));
                return stack;
            }

            if (player.getHealth() > 2 || player.capabilities.isCreativeMode) {
                getLocation(stack).sendEntityToCoords(player);
                stack.damageItem(1, player);
                if (!player.capabilities.isCreativeMode) player.setHealth(player.getHealth() - 2);
            } else
                if (world.isRemote) player.addChatMessage(new ChatComponentTranslation("msg.teleporterLowHealth.txt"));
            return stack;
        }
    }

    @Override
    public void addInformation(final ItemStack stack, final EntityPlayer player, final List list,
            final boolean extraInformation) {
        if (!ItemNBTHelper.getBoolean(stack, "IsSet", false)) {
            list.add(EnumChatFormatting.RED + StatCollector.translateToLocal("info.teleporterInfUnset1.txt"));
            list.add(EnumChatFormatting.WHITE + StatCollector.translateToLocal("info.teleporterInfUnset2.txt"));
            list.add(EnumChatFormatting.WHITE + StatCollector.translateToLocal("info.teleporterInfUnset3.txt"));
            list.add(EnumChatFormatting.WHITE + StatCollector.translateToLocal("info.teleporterInfUnset4.txt"));
            list.add(EnumChatFormatting.WHITE + StatCollector.translateToLocal("info.teleporterInfUnset5.txt"));
        } else {
            list.add(EnumChatFormatting.GREEN + StatCollector.translateToLocal("info.teleporterInfSet1.txt"));
            list.add(
                    EnumChatFormatting.WHITE + "{x:"
                            + (int) ItemNBTHelper.getDouble(stack, "X", 0)
                            + " y:"
                            + (int) ItemNBTHelper.getDouble(stack, "Y", 0)
                            + " z:"
                            + (int) ItemNBTHelper.getDouble(stack, "Z", 0)
                            + " Dim:"
                            + getLocation(stack).getDimensionName()
                            + "}");
            list.add(
                    EnumChatFormatting.BLUE + String.valueOf(stack.getMaxDamage() - stack.getItemDamage() + 1)
                            + " "
                            + StatCollector.translateToLocal("info.teleporterInfSet2.txt"));
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.uncommon;
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return new EntityPersistentItem(world, location, itemstack);
    }

    public TeleportLocation getLocation(ItemStack stack) {
        if (!ItemNBTHelper.getBoolean(stack, "IsSet", false)) return null;

        TeleportLocation location = new TeleportLocation();
        location.readFromNBT(stack.getTagCompound());

        return location;
    }
}
