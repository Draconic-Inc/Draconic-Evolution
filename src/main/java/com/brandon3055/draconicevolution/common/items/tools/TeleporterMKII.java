package com.brandon3055.draconicevolution.common.items.tools;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.brandon3055.brandonscore.common.utills.InfoHelper;
import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import com.brandon3055.brandonscore.common.utills.Teleporter.TeleportLocation;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.gui.GuiHandler;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.entity.EntityPersistentItem;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.utills.IHudDisplayItem;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TeleporterMKII extends TeleporterMKI implements IHudDisplayItem {

    public TeleporterMKII() {
        super(true);
        this.setUnlocalizedName(Strings.teleporterMKIIName);
        this.setCreativeTab(DraconicEvolution.tabToolsWeapons);
        this.setMaxStackSize(1);
        ModItems.register(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + Strings.teleporterMKIIName);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        World world = player.worldObj;
        int fuel = ItemNBTHelper.getInteger(stack, "Fuel", 0);

        if (getLocation(stack) == null) {
            if (world.isRemote) FMLNetworkHandler.openGui(
                    player,
                    DraconicEvolution.instance,
                    GuiHandler.GUIID_TELEPORTER,
                    world,
                    (int) player.posX,
                    (int) player.posY,
                    (int) player.posZ);
            return true;
        }

        if (!player.capabilities.isCreativeMode && fuel <= 0) {
            if (world.isRemote) player.addChatMessage(new ChatComponentTranslation("msg.teleporterOutOfFuel.txt"));
            return true;
        }

        if (entity instanceof EntityPlayer) {
            if (entity.isSneaking()) {
                getLocation(stack).sendEntityToCoords(entity);
                if (!player.capabilities.isCreativeMode && fuel > 0) ItemNBTHelper.setInteger(stack, "Fuel", fuel - 1);
            } else {
                if (world.isRemote)
                    player.addChatMessage(new ChatComponentTranslation("msg.teleporterPlayerConsent.txt"));
            }
            return true;
        } else if (entity instanceof EntityLiving) {
            getLocation(stack).sendEntityToCoords(entity);
            if (!player.capabilities.isCreativeMode && fuel > 0) ItemNBTHelper.setInteger(stack, "Fuel", fuel - 1);
        }

        return true;
    }

    @Override
    public ItemStack onItemRightClick(final ItemStack stack, final World world, final EntityPlayer player) {
        int fuel = ItemNBTHelper.getInteger(stack, "Fuel", 0);

        if (player.isSneaking()) {
            if (world.isRemote) {
                FMLNetworkHandler.openGui(
                        player,
                        DraconicEvolution.instance,
                        GuiHandler.GUIID_TELEPORTER,
                        world,
                        (int) player.posX,
                        (int) player.posY,
                        (int) player.posZ);
            }
        } else {

            if (getLocation(stack) == null) {
                if (world.isRemote) FMLNetworkHandler.openGui(
                        player,
                        DraconicEvolution.instance,
                        GuiHandler.GUIID_TELEPORTER,
                        world,
                        (int) player.posX,
                        (int) player.posY,
                        (int) player.posZ);
                return stack;
            }

            if (!player.capabilities.isCreativeMode && fuel <= 0) {
                if (world.isRemote) player.addChatMessage(new ChatComponentTranslation("msg.teleporterOutOfFuel.txt"));
                return stack;
            }

            if (!player.capabilities.isCreativeMode && fuel > 0) ItemNBTHelper.setInteger(stack, "Fuel", fuel - 1);

            getLocation(stack).sendEntityToCoords(player);
        }

        return stack;
    }

    @Override
    public void addInformation(final ItemStack teleporter, final EntityPlayer player, final List list2,
            final boolean extraInformation) {
        short selected = ItemNBTHelper.getShort(teleporter, "Selection", (short) 0);
        int selrctionOffset = ItemNBTHelper.getInteger(teleporter, "SelectionOffset", 0);
        NBTTagCompound compound = teleporter.getTagCompound();
        if (compound == null) compound = new NBTTagCompound();
        NBTTagList list = (NBTTagList) compound.getTag("Locations");
        if (list == null) list = new NBTTagList();
        String selectedDest = list.getCompoundTagAt(selected + selrctionOffset).getString("Name");

        list2.add(EnumChatFormatting.GOLD + "" + selectedDest);
        if (InfoHelper.holdShiftForDetails(list2)) {
            list2.add(
                    EnumChatFormatting.WHITE + StatCollector.translateToLocal("info.teleporterInfFuel.txt")
                            + " "
                            + ItemNBTHelper.getInteger(teleporter, "Fuel", 0));
            list2.add(
                    EnumChatFormatting.DARK_PURPLE + ""
                            + EnumChatFormatting.ITALIC
                            + StatCollector.translateToLocal("info.teleporterInfGUI.txt"));
            list2.add(
                    EnumChatFormatting.DARK_PURPLE + ""
                            + EnumChatFormatting.ITALIC
                            + StatCollector.translateToLocal("info.teleporterInfScroll.txt"));
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.rare;
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return new EntityPersistentItem(world, location, itemstack);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getItemStackDisplayName(ItemStack teleporter) {
        return super.getItemStackDisplayName(teleporter);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int p_77663_4_, boolean p_77663_5_) {
        // if (world.isRemote && entity instanceof EntityPlayer && ((EntityPlayer) entity).getHeldItem() != null &&
        // ((EntityPlayer) entity).getHeldItem().getItem() instanceof TeleporterMKII){
        // if (getLocation(((EntityPlayer) entity).getHeldItem()) != null)
        // HudHandler.setTooltip(getLocation(((EntityPlayer) entity).getHeldItem()).getName());
        // }
    }

    @Override
    public TeleportLocation getLocation(ItemStack stack) {
        short selected = ItemNBTHelper.getShort(stack, "Selection", (short) 0);
        int selrctionOffset = ItemNBTHelper.getInteger(stack, "SelectionOffset", 0);
        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null) return null;
        NBTTagList list = (NBTTagList) compound.getTag("Locations");
        if (list == null) return null;

        TeleportLocation destination = new TeleportLocation();
        destination.readFromNBT(list.getCompoundTagAt(selected + selrctionOffset));
        if (destination.getName().isEmpty()) return null;

        return destination;
    }

    @Override
    public List<String> getDisplayData(ItemStack stack) {
        List<String> list = new ArrayList<String>();
        TeleportLocation location = getLocation(stack);
        if (location != null) {
            list.add(location.getName());
        }
        list.add(
                StatCollector.translateToLocal("info.teleporterInfFuel.txt") + " "
                        + ItemNBTHelper.getInteger(stack, "Fuel", 0));

        return list;
    }
}
