package com.brandon3055.draconicevolution.network;

import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.handlers.HandHelper;
import com.brandon3055.brandonscore.network.MessageHandlerWrapper;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.brandonscore.utils.Teleporter.TeleportLocation;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.lib.DESoundHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketDislocator implements IMessage { //TODO Re Write this mess!!!
    public static final int ADDDESTINATION = 0;
    public static final int REMOVEDESTINATION = 1;
    public static final int UPDATENAME = 2;
    public static final int UPDATELOCK = 3;
    public static final int CHANGESELECTION = 4;
    public static final int UPDATEOFFSET = 5;
    public static final int ADDFUEL = 6;
    public static final int UPDATEDESTINATION = 7;
    public static final int TELEPORT = 8;
    public static final int SCROLL = 9;
    public static final int MOVELOCATION = 10;

    private int data = 0;
    private boolean dataB;
    private byte function = -1;
    private TeleportLocation location;

    public PacketDislocator() {
    }

    public PacketDislocator(int function, int data, boolean b) {
        this.data = data;
        this.function = (byte) function;
        this.dataB = b;
    }

    public PacketDislocator(TeleportLocation location, int function) {
        this.location = location;
        this.function = (byte) function;
    }

    public PacketDislocator(TeleportLocation location, int function, int data) {
        this.data = data;
        this.location = location;
        this.function = (byte) function;
    }

    @Override
    public void toBytes(ByteBuf bytes) {
        bytes.writeByte(function);
        if (function == ADDDESTINATION || function == UPDATEDESTINATION) {
            bytes.writeDouble(location.getXCoord());
            bytes.writeDouble(location.getYCoord());
            bytes.writeDouble(location.getZCoord());
            bytes.writeInt(location.getDimension());
            bytes.writeFloat(location.getPitch());
            bytes.writeFloat(location.getYaw());
            ByteBufUtils.writeUTF8String(bytes, location.getName());
            //ByteBufUtils.writeUTF8String(bytes, location.getDimensionName());
            if (function == UPDATEDESTINATION) bytes.writeInt(data);
        }

        if (function == UPDATELOCK || function == MOVELOCATION) {
            bytes.writeInt(data);
            bytes.writeBoolean(dataB);
        }

        if (function == UPDATEOFFSET || function == CHANGESELECTION || function == REMOVEDESTINATION || function == ADDFUEL || function == SCROLL) {
            bytes.writeInt(data);
        }

        if (function == UPDATENAME) {
            ByteBufUtils.writeUTF8String(bytes, location.getName());
            bytes.writeInt(data);
        }

        if (function == TELEPORT) {
            bytes.writeInt(data);
        }
    }

    @Override
    public void fromBytes(ByteBuf bytes) {
        function = bytes.readByte();
        if (function == ADDDESTINATION || function == UPDATEDESTINATION) {
            location = new TeleportLocation();
            location.setXCoord(bytes.readDouble());
            location.setYCoord(bytes.readDouble());
            location.setZCoord(bytes.readDouble());
            location.setDimension(bytes.readInt());
            location.setPitch(bytes.readFloat());
            location.setYaw(bytes.readFloat());
            location.setName(ByteBufUtils.readUTF8String(bytes));
            //location.setDimentionName(ByteBufUtils.readUTF8String(bytes));
            if (function == UPDATEDESTINATION) data = bytes.readInt();
        }

        if (function == UPDATELOCK || function == MOVELOCATION) {
            data = bytes.readInt();
            dataB = bytes.readBoolean();
        }

        if (function == UPDATEOFFSET || function == CHANGESELECTION || function == REMOVEDESTINATION || function == ADDFUEL || function == SCROLL) {
            data = bytes.readInt();
        }

        if (function == UPDATENAME) {
            location = new TeleportLocation();
            location.setName(ByteBufUtils.readUTF8String(bytes));
            data = bytes.readInt();
        }

        if (function == TELEPORT) {
            data = bytes.readInt();
        }
    }

    public static class Handler extends MessageHandlerWrapper<PacketDislocator, IMessage> {

        @Override
        public IMessage handleMessage(PacketDislocator message, MessageContext ctx) {
            ItemStack teleporter = HandHelper.getItem(ctx.getServerHandler().playerEntity, DEFeatures.dislocatorAdvanced);
            if (teleporter.isEmpty()) {
                return null;
            }

            NBTTagCompound compound = ItemNBTHelper.getCompound(teleporter);
            NBTTagList list = compound.getTagList("Locations", 10);

            if (message.function == ADDDESTINATION) {
                NBTTagCompound tag = new NBTTagCompound();
                message.location.setDimensionName(BrandonsCore.proxy.getMCServer().worldServerForDimension(message.location.getDimension()).provider.getDimensionType().getName());

                message.location.setXCoord(ctx.getServerHandler().playerEntity.posX);
                message.location.setYCoord(ctx.getServerHandler().playerEntity.posY);
                message.location.setZCoord(ctx.getServerHandler().playerEntity.posZ);

                message.location.writeToNBT(tag);
                list.appendTag(tag);
                compound.setTag("Locations", list);
                teleporter.setTagCompound(compound);
            }

            if (message.function == SCROLL) {
                int selected = ItemNBTHelper.getShort(teleporter, "Selection", (short) 0);
                int selectionOffset = ItemNBTHelper.getInteger(teleporter, "SelectionOffset", 0);
                int maxSelect = Math.min(list.tagCount() - 1, 11);
                int maxOffset = Math.max(list.tagCount() - 12, 0);

                if (message.data > 0 && selected < maxSelect) {
                    ItemNBTHelper.setShort(teleporter, "Selection", (short) (selected + 1));
                    return null;
                }
                if (message.data > 0 && selectionOffset < maxOffset) {
                    ItemNBTHelper.setInteger(teleporter, "SelectionOffset", selectionOffset + 1);
                    return null;
                }
                if (message.data < 0 && selected > 0) {
                    ItemNBTHelper.setShort(teleporter, "Selection", (short) (selected - 1));
                    return null;
                }
                if (message.data < 0 && selectionOffset > 0) {
                    ItemNBTHelper.setInteger(teleporter, "SelectionOffset", selectionOffset - 1);
                    return null;
                }

            }

            if (message.function == UPDATEDESTINATION) {
                NBTTagCompound tag = list.getCompoundTagAt(message.data);
                message.location.setDimensionName(BrandonsCore.proxy.getMCServer().worldServerForDimension(message.location.getDimension()).provider.getDimensionType().getName());

                message.location.setXCoord(ctx.getServerHandler().playerEntity.posX);
                message.location.setYCoord(ctx.getServerHandler().playerEntity.posY);
                message.location.setZCoord(ctx.getServerHandler().playerEntity.posZ);

                message.location.writeToNBT(tag);
                list.set(message.data, tag);
                compound.setTag("Locations", list);
                teleporter.setTagCompound(compound);
            }

            if (message.function == UPDATELOCK) {
                list.getCompoundTagAt(message.data).setBoolean("WP", message.dataB);
                compound.setTag("Locations", list);
                teleporter.setTagCompound(compound);
            }

            if (message.function == REMOVEDESTINATION) {
                list.removeTag(message.data);
                compound.setTag("Locations", list);
                teleporter.setTagCompound(compound);
            }

            if (message.function == UPDATENAME) {
                list.getCompoundTagAt(message.data).setString("Name", message.location.getName());
                compound.setTag("Locations", list);
                teleporter.setTagCompound(compound);
            }

            if (message.function == TELEPORT) {
                int fuel = ItemNBTHelper.getInteger(teleporter, "Fuel", 0);
                if (!ctx.getServerHandler().playerEntity.capabilities.isCreativeMode) ItemNBTHelper.setInteger(teleporter, "Fuel", fuel - 1);
                TeleportLocation destination = new TeleportLocation();
                destination.readFromNBT(list.getCompoundTagAt(message.data));

                if (!ctx.getServerHandler().playerEntity.world.isRemote) {
                    DESoundHandler.playSoundFromServer(ctx.getServerHandler().playerEntity.world, ctx.getServerHandler().playerEntity.posX, ctx.getServerHandler().playerEntity.posY, ctx.getServerHandler().playerEntity.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, ctx.getServerHandler().playerEntity.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
                }
                destination.teleport(ctx.getServerHandler().playerEntity);
                if (!ctx.getServerHandler().playerEntity.world.isRemote) {
                    DESoundHandler.playSoundFromServer(ctx.getServerHandler().playerEntity.world, ctx.getServerHandler().playerEntity.posX, ctx.getServerHandler().playerEntity.posY, ctx.getServerHandler().playerEntity.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, ctx.getServerHandler().playerEntity.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
                }
            }

            if (message.function == MOVELOCATION) {
                int selected = ItemNBTHelper.getShort(teleporter, "Selection", (short) 0);
                int selectionOffset = ItemNBTHelper.getInteger(teleporter, "SelectionOffset", 0);
                int maxSelect = Math.min(list.tagCount() - 1, 11);
                int maxOffset = Math.max(list.tagCount() - 12, 0);

                if (message.dataB) //up
                {
                    if (selected > 0) {
                        NBTTagCompound temp = list.getCompoundTagAt(selected + selectionOffset);
                        list.set(selected + selectionOffset, list.getCompoundTagAt(selected + selectionOffset - 1));
                        list.set(selected + selectionOffset - 1, temp);
                        compound.setTag("Locations", list);
                        teleporter.setTagCompound(compound);
                        ItemNBTHelper.setShort(teleporter, "Selection", (short) (ItemNBTHelper.getShort(teleporter, "Selection", (short) 0) - 1));
                    }
                }
                else //down
                {
                    if (selected < maxSelect) {
                        NBTTagCompound temp = list.getCompoundTagAt(selected + selectionOffset);
                        list.set(selected + selectionOffset, list.getCompoundTagAt(selected + selectionOffset + 1));
                        list.set(selected + selectionOffset + 1, temp);
                        compound.setTag("Locations", list);
                        teleporter.setTagCompound(compound);
                        ItemNBTHelper.setShort(teleporter, "Selection", (short) (ItemNBTHelper.getShort(teleporter, "Selection", (short) 0) + 1));
                    }
                }
            }

            if (message.function == ADDFUEL) {
                int fuel = ItemNBTHelper.getInteger(teleporter, "Fuel", 0);
                int count = 0;
                for (int i = 0; i < message.data; i++) {
                    if (ctx.getServerHandler().playerEntity.inventory.hasItemStack(new ItemStack(Items.ENDER_PEARL))) {
                        ctx.getServerHandler().playerEntity.inventory.clearMatchingItems(Items.ENDER_PEARL, 0, 1, null);
                        count++;
                    }
                    else break;
                }
                ItemNBTHelper.setInteger(teleporter, "Fuel", fuel + (DEConfig.dislocatorUsesPerPearl * count));
            }

            if (message.function == CHANGESELECTION) {
                ItemNBTHelper.setShort(teleporter, "Selection", (short) message.data);
            }

            if (message.function == UPDATEOFFSET) {
                ItemNBTHelper.setInteger(teleporter, "SelectionOffset", message.data);
            }
            return null;
        }
    }
}