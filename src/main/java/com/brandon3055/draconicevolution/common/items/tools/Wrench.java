package com.brandon3055.draconicevolution.common.items.tools;

import cofh.api.energy.IEnergyReceiver;
import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.items.ItemDE;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.tileentities.energynet.TileRemoteEnergyBase;
import com.brandon3055.draconicevolution.common.tileentities.energynet.TileWirelessEnergyTransceiver;
import com.brandon3055.draconicevolution.common.utills.IHudDisplayItem;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Brandon on 23/08/2014.
 */
public class Wrench extends ItemDE implements IHudDisplayItem {

    public static final String BIND_MODE = "bind";
    public static final String UNBIND_MODE = "unBind";
    public static final String CLEAR_BINDINGS = "unBindAll";
    public static final String MODE_SWITCH = "modeSwitch";

    public Wrench() {
        this.setUnlocalizedName(Strings.wrenchName);
        this.setCreativeTab(DraconicEvolution.tabToolsWeapons);
        this.setMaxStackSize(1);

        ModItems.register(this);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {

        // if (world.isRemote)FMLCommonHandler.instance().bus().register(new UpdateChecker());
        // player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.BLUE + "[Draconic Evolution]" +
        // EnumChatFormatting.RESET + " New version available:"));

        if (player.isSneaking()) cycleMode(stack, world, player);
        else if (ItemNBTHelper.getCompound(stack).hasKey("LinkData")
                && ItemNBTHelper.getCompound(stack).getCompoundTag("LinkData").getBoolean("Bound"))
            ItemNBTHelper.getCompound(stack).getCompoundTag("LinkData").setBoolean("Bound", false);
        return super.onItemRightClick(stack, world, player);
    }

    static final String[] modes = new String[] {BIND_MODE, UNBIND_MODE, CLEAR_BINDINGS, MODE_SWITCH};

    private static void cycleMode(ItemStack stack, World world, EntityPlayer player) {
        String currentMode = ItemNBTHelper.getString(stack, "Mode", "bind");
        int mode = 0;
        for (String s : modes) {
            if (s.equals(currentMode)) {
                if (mode + 1 >= modes.length) currentMode = modes[0];
                else currentMode = modes[mode + 1];
                break;
            }
            mode++;
        }
        ItemNBTHelper.setString(stack, "Mode", currentMode);
        if (world.isRemote)
            player.addChatComponentMessage(new ChatComponentTranslation("msg.de.wrenchMode." + currentMode + ".txt"));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer p_77624_2_, List list, boolean bool) {
        list.add(StatCollector.translateToLocal(
                "msg.de.wrenchMode." + ItemNBTHelper.getString(stack, "Mode", "bind") + ".txt"));
        NBTTagCompound linkDat = null;
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("LinkData"))
            linkDat = stack.getTagCompound().getCompoundTag("LinkData");
        if (linkDat != null && linkDat.getBoolean("Bound")) {
            list.add(StatCollector.translateToLocal("msg.de.boundTo.txt") + ": [X:" + linkDat.getInteger("XCoord")
                    + ", Y:" + linkDat.getInteger("YCoord") + ", Z:" + linkDat.getInteger("ZCoord") + "]");
            list.add(StatCollector.translateToLocal("msg.de.rightClickUnbind.txt"));
        }
    }

    @Override
    public List<String> getDisplayData(ItemStack stack) {
        List<String> list = new ArrayList<String>();
        list.add(StatCollector.translateToLocal(
                "msg.de.wrenchMode." + ItemNBTHelper.getString(stack, "Mode", "bind") + ".txt"));
        NBTTagCompound linkDat = null;
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("LinkData"))
            linkDat = stack.getTagCompound().getCompoundTag("LinkData");
        if (linkDat != null && linkDat.getBoolean("Bound")) {
            list.add(StatCollector.translateToLocal("msg.de.boundTo.txt") + ": [X:" + linkDat.getInteger("XCoord")
                    + ", Y:" + linkDat.getInteger("YCoord") + ", Z:" + linkDat.getInteger("ZCoord") + "]");
            list.add(StatCollector.translateToLocal("msg.de.rightClickUnbind.txt"));
        }
        return list;
    }

    public static String getMode(ItemStack stack) {
        return ItemNBTHelper.getString(stack, "Mode", "bind");
    }

    @Override
    public boolean onItemUseFirst(
            ItemStack stack,
            EntityPlayer player,
            World world,
            int x,
            int y,
            int z,
            int side,
            float hitX,
            float hitY,
            float hitZ) {
        Block clicked = world.getBlock(x, y, z);
        if (getMode(stack).equals(MODE_SWITCH)
                && clicked.rotateBlock(world, x, y, z, ForgeDirection.getOrientation(side))
                && !world.isRemote) return true;

        if (world.isRemote) return false;

        TileEntity tileClicked = world.getTileEntity(x, y, z);
        if (tileClicked instanceof TileRemoteEnergyBase) return false;
        else if (!(tileClicked instanceof IEnergyReceiver)) return false;
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("LinkData")) {
            NBTTagCompound linkData = stack.getTagCompound().getCompoundTag("LinkData");
            if (!linkData.getBoolean("Bound")) return false;
            int xCoord = linkData.getInteger("XCoord");
            int yCoord = linkData.getInteger("YCoord");
            int zCoord = linkData.getInteger("ZCoord");

            if (world.getTileEntity(xCoord, yCoord, zCoord) instanceof TileWirelessEnergyTransceiver) {
                ((TileWirelessEnergyTransceiver) world.getTileEntity(xCoord, yCoord, zCoord))
                        .linkDevice(x, y, z, side, player, ItemNBTHelper.getString(stack, "Mode", "bind"));

                // linkData.setBoolean("Bound", false);
                return true;
            } else return false;
        }

        return false;
    }
}
