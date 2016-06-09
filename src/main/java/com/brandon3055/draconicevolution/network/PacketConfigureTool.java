package com.brandon3055.draconicevolution.network;

import com.brandon3055.brandonscore.inventory.PlayerSlot;
import com.brandon3055.brandonscore.network.MessageHandlerWrapper;
import com.brandon3055.draconicevolution.api.itemconfig.IConfigurableItem;
import com.brandon3055.draconicevolution.api.itemconfig.IItemConfigField;
import com.brandon3055.draconicevolution.api.itemconfig.ItemConfigFieldRegistry;
import com.brandon3055.draconicevolution.api.itemconfig.ToolConfigHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by brandon3055 on 8/06/2016.
 */
public class PacketConfigureTool implements IMessage {

    public PlayerSlot slot;
    public int fieldIndex;
    public int button;
    public int data;

    public PacketConfigureTool(){}

    public PacketConfigureTool(PlayerSlot slot, int fieldIndex, int button, int data) {

        this.slot = slot;
        this.fieldIndex = fieldIndex;
        this.button = button;
        this.data = data;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        slot.toBuff(buf);
        buf.writeByte(fieldIndex);
        buf.writeByte(button);
        buf.writeShort(data);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        slot = PlayerSlot.fromBuff(buf);
        fieldIndex = buf.readByte();
        button = buf.readByte();
        data = buf.readShort();
    }

    public static class Handler extends MessageHandlerWrapper<PacketConfigureTool, IMessage> {

        @Override
        public IMessage handleMessage(PacketConfigureTool message, MessageContext ctx) {
            ItemStack stack = message.slot.getStackInSlot(ctx.getServerHandler().playerEntity);
            if (stack == null || !(stack.getItem() instanceof IConfigurableItem)){
                return null;
            }

            IConfigurableItem item = (IConfigurableItem) stack.getItem();
            IItemConfigField field = item.getFields(stack, new ItemConfigFieldRegistry()).getField(message.fieldIndex);

            if (field != null){
                field.handleButton(IItemConfigField.EnumButton.getButton(message.button), message.data);
                field.writeToNBT(ToolConfigHelper.getFieldStorage(stack));
            }

            return null;
        }
    }
}
