package com.brandon3055.draconicevolution.network;

import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.network.MessageHandlerWrapper;
import com.brandon3055.draconicevolution.lib.DESoundHandler;
import com.brandon3055.draconicevolution.utils.LogHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketPlaySound implements IMessage
{
    public double x;
    public double y;
    public double z;
    public String sound;
    public String category;
    public float volume;
    public float pitch;
    public boolean distanceDelay;

    public PacketPlaySound() {}

	public PacketPlaySound(double x, double y, double z, String sound, String category, float volume, float pitch, boolean distanceDelay) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.sound = sound;
        this.category = category;
        this.volume = volume;
        this.pitch = pitch;
        this.distanceDelay = distanceDelay;
    }


    @Override
    public void toBytes(ByteBuf bytes){
        bytes.writeFloat((float)x);
        bytes.writeFloat((float)y);
        bytes.writeFloat((float)z);
        ByteBufUtils.writeUTF8String(bytes, sound);
        ByteBufUtils.writeUTF8String(bytes, category);
        bytes.writeFloat(volume);
        bytes.writeFloat(pitch);
        bytes.writeBoolean(distanceDelay);
    }

	@Override
	public void fromBytes(ByteBuf bytes){
		x = bytes.readFloat();
        y = bytes.readFloat();
        z = bytes.readFloat();
        sound = ByteBufUtils.readUTF8String(bytes);
        category = ByteBufUtils.readUTF8String(bytes);
        volume = bytes.readFloat();
        pitch = bytes.readFloat();
        distanceDelay = bytes.readBoolean();
	}

	public static class Handler extends MessageHandlerWrapper<PacketPlaySound, IMessage> {

        @Override
        public IMessage handleMessage(PacketPlaySound message, MessageContext ctx) {
            SoundEvent event = DESoundHandler.getSound(message.sound);
            SoundCategory category = SoundCategory.getByName(message.category);

            if (event != null){
                BrandonsCore.proxy.getClientWorld().playSound(message.x, message.y, message.z, event, category, message.volume, message.pitch, message.distanceDelay);
            }
            else {
                LogHelper.error("Unable to find sound in vanilla or DE's sound events [%s]", message.sound);
            }
            return null;
        }

	}
}