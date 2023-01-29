package com.brandon3055.draconicevolution.common.network;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.brandon3055.brandonscore.common.lib.References;
import com.brandon3055.brandonscore.common.utills.DataUtills;
import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.common.utills.IConfigurableItem;
import com.brandon3055.draconicevolution.common.utills.ItemConfigField;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class ItemConfigPacket implements IMessage {

    public byte datatype;
    public int slot;
    public Object value;
    public String name;
    public boolean renameProfile = false;

    public ItemConfigPacket() {}

    public ItemConfigPacket(ItemConfigField field) {
        this.datatype = (byte) field.datatype;
        this.slot = field.slot;
        this.value = field.value;
        this.name = field.name;
    }

    public ItemConfigPacket(int slot, String name) {
        this.datatype = References.BOOLEAN_ID;
        this.slot = slot;
        this.value = false;
        this.name = name;
        this.renameProfile = true;
    }

    @Override
    public void fromBytes(ByteBuf bytes) {
        this.datatype = bytes.readByte();
        this.slot = bytes.readInt();
        this.name = ByteBufUtils.readUTF8String(bytes);
        this.value = DataUtills.instance.readObjectFromBytes(bytes, datatype);
        this.renameProfile = bytes.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf bytes) {
        bytes.writeByte(datatype);
        bytes.writeInt(slot);
        ByteBufUtils.writeUTF8String(bytes, name);
        DataUtills.instance.writeObjectToBytes(bytes, datatype, value);
        bytes.writeBoolean(renameProfile);
    }

    public static class Handler implements IMessageHandler<ItemConfigPacket, IMessage> {

        @Override
        public IMessage onMessage(ItemConfigPacket message, MessageContext ctx) {
            EntityPlayer player = ctx.getServerHandler().playerEntity;

            if (message.slot >= player.inventory.getSizeInventory() || message.slot < 0) return null;

            ItemStack stack = player.inventory.getStackInSlot(message.slot);

            if (stack != null && stack.getItem() instanceof IConfigurableItem) {

                if (message.renameProfile) {
                    ItemNBTHelper.setString(
                            stack,
                            "ProfileName" + ItemNBTHelper.getInteger(stack, "ConfigProfile", 0),
                            message.name);
                    return null;
                }

                IConfigurableItem item = (IConfigurableItem) stack.getItem();
                List<ItemConfigField> fields = item.getFields(stack, message.slot);

                for (ItemConfigField field : fields) {
                    if (field.name.equals(message.name) && message.datatype == field.datatype) {
                        ItemConfigField newValue = new ItemConfigField(
                                message.datatype,
                                message.value,
                                message.slot,
                                message.name);

                        if (newValue.castToDouble() <= field.castMaxToDouble()
                                && newValue.castToDouble() >= field.castMinToDouble()) {
                            DataUtills.writeObjectToCompound(
                                    IConfigurableItem.ProfileHelper.getProfileCompound(stack),
                                    message.value,
                                    message.datatype,
                                    message.name);
                        }
                    }
                }
            }
            return null;
        }
    }
}
