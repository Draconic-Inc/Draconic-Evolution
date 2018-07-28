package com.brandon3055.draconicevolution.network;

import com.brandon3055.brandonscore.network.MessageHandlerWrapper;
import com.brandon3055.draconicevolution.blocks.tileentity.TileParticleGenerator;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketParticleGenerator implements IMessage {

    private int tileX;
    private int tileY;
    private int tileZ;
    private byte key;
    private int value;
    private int randomValue;

    public PacketParticleGenerator() {
    }

    public PacketParticleGenerator(int tileX, int tileY, int tileZ, byte key, int value, int rValue) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.tileZ = tileZ;
        this.key = key;
        this.value = value;
        this.randomValue = rValue;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        tileX = buf.readInt();
        tileY = buf.readInt();
        tileZ = buf.readInt();
        key = buf.readByte();
        value = buf.readInt();
        randomValue = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(tileX);
        buf.writeInt(tileY);
        buf.writeInt(tileZ);
        buf.writeByte(key);
        buf.writeInt(value);
        buf.writeInt(randomValue);
    }

    public static class Handler extends MessageHandlerWrapper<PacketParticleGenerator, IMessage> {

        @Override
        public IMessage handleMessage(PacketParticleGenerator message, MessageContext ctx) {
            TileEntity tile = ctx.getServerHandler().player.world.getTileEntity(new BlockPos(message.tileX, message.tileY, message.tileZ));
            TileParticleGenerator gen = (tile instanceof TileParticleGenerator) ? ((TileParticleGenerator) tile) : null;

            if (gen != null) {
                switch (message.key) {
                    case 0:
                        gen.RED.value = message.value;
                        gen.RANDOM_RED.value = message.randomValue;
                        break;
                    case 1:
                        gen.GREEN.value = message.value;
                        gen.RANDOM_GREEN.value = message.randomValue;
                        break;
                    case 2:
                        gen.BLUE.value = message.value;
                        gen.RANDOM_BLUE.value = message.randomValue;
                        break;
                    case 3:
                        gen.ALPHA.value = message.value;
                        gen.RANDOM_ALPHA.value = message.randomValue;
                        break;
                    case 4:
                        gen.SCALE.value = message.value/10000D;
                        gen.RANDOM_SCALE.value = message.randomValue/10000D;
                        break;
                    case 5:
                        gen.LIFE.value = message.value;
                        gen.RANDOM_LIFE.value = message.randomValue;
                        break;
                    case 6:
                        gen.GRAVITY.value = message.value/10000D;
                        gen.RANDOM_GRAVITY.value = message.randomValue/10000D;
                        break;
                    case 7:
                        gen.FADE.value = message.value;
                        gen.RANDOM_FADE.value = message.randomValue;
                        break;
                    case 8:
                        gen.TYPE.value = message.value;
                        break;
                    case 9:
                        gen.COLLISION.value = message.value == 1;
                        break;
                    case 10:
                        gen.MOTION_X.value = message.value/10000D;
                        gen.RANDOM_MOTION_X.value = message.randomValue/10000D;
                        break;
                    case 11:
                        gen.MOTION_Y.value = message.value/10000D;
                        gen.RANDOM_MOTION_Y.value = message.randomValue/10000D;
                        break;
                    case 12:
                        gen.MOTION_Z.value = message.value/10000D;
                        gen.RANDOM_MOTION_Z.value = message.randomValue/10000D;
                        break;
                    case 13:
                        gen.SPAWN_X.value = message.value/10000D;
                        gen.RANDOM_SPAWN_X.value = message.randomValue/10000D;
                        break;
                    case 14:
                        gen.SPAWN_Y.value = message.value/10000D;
                        gen.RANDOM_SPAWN_Y.value = message.randomValue/10000D;
                        break;
                    case 15:
                        gen.SPAWN_Z.value = message.value/10000D;
                        gen.RANDOM_SPAWN_Z.value = message.randomValue/10000D;
                        break;
                    case 16:
                        gen.DELAY.value = message.value;
                        break;
                }
            }
            return null;
        }
    }

}
