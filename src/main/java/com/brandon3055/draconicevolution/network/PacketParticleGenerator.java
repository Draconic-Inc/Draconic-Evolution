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
                        gen.red.set(message.value);
                        gen.randomRed.set(message.randomValue);
                        break;
                    case 1:
                        gen.green.set(message.value);
                        gen.randomGreen.set(message.randomValue);
                        break;
                    case 2:
                        gen.blue.set(message.value);
                        gen.randomBlue.set(message.randomValue);
                        break;
                    case 3:
                        gen.alpha.set(message.value);
                        gen.randomAlpha.set(message.randomValue);
                        break;
                    case 4:
                        gen.scale.set(message.value / 10000D);
                        gen.randomScale.set(message.randomValue / 10000D);
                        break;
                    case 5:
                        gen.life.set(message.value);
                        gen.randomLife.set(message.randomValue);
                        break;
                    case 6:
                        gen.gravity.set(message.value / 10000D);
                        gen.randomGravity.set(message.randomValue / 10000D);
                        break;
                    case 7:
                        gen.fade.set(message.value);
                        gen.randomFade.set(message.randomValue);
                        break;
                    case 8:
                        gen.type.set(message.value);
                        break;
                    case 9:
                        gen.collision.set(message.value == 1);
                        break;
                    case 10:
                        gen.motionX.set(message.value / 10000D);
                        gen.randomMotionX.set(message.randomValue / 10000D);
                        break;
                    case 11:
                        gen.motionY.set(message.value / 10000D);
                        gen.randomMotionY.set(message.randomValue / 10000D);
                        break;
                    case 12:
                        gen.motionZ.set(message.value / 10000D);
                        gen.randomMotionZ.set(message.randomValue / 10000D);
                        break;
                    case 13:
                        gen.spawnX.set(message.value / 10000D);
                        gen.randomSpawnX.set(message.randomValue / 10000D);
                        break;
                    case 14:
                        gen.spawnY.set(message.value / 10000D);
                        gen.randomSpawnY.set(message.randomValue / 10000D);
                        break;
                    case 15:
                        gen.spawnZ.set(message.value / 10000D);
                        gen.randomSpawnZ.set(message.randomValue / 10000D);
                        break;
                    case 16:
                        gen.delay.set(message.value);
                        break;
                }
            }
            return null;
        }
    }

}
