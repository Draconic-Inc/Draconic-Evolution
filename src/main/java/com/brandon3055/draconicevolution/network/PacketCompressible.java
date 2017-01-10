package com.brandon3055.draconicevolution.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.EncoderException;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Created by brandon3055 on 29/11/2016.
 */
public abstract class PacketCompressible implements IMessage {

    public abstract void writeBytes(ByteBuf buf);

    public abstract void readBytes(ByteBuf buf);

    @Override
    public void fromBytes(ByteBuf buf) {
        boolean isCompressed = buf.readBoolean();

        if (!isCompressed) {
            readBytes(buf);
            return;
        }

        Inflater inflater = new Inflater();
        try {
            int rawSize = buf.readInt();
            byte[] compressedBytes = new byte[buf.readableBytes()];
            buf.readBytes(compressedBytes);
            buf = Unpooled.buffer();
            inflater.setInput(compressedBytes);

            byte[] rawBytes = new byte[rawSize];
            inflater.inflate(rawBytes);

            buf.clear();
//            buf.capacity(rawBytes.length);
//            buf.ensureWritable(rawBytes.length, true); //Just to make absolutely sure the buffer is gig enough
//            LogHelper.dev("Reading Compressed " + rawSize +" "+compressedBytes.length+" "+buf.capacity()+" "+buf.maxCapacity());
//            LogHelper.dev(new String(rawBytes));
            buf.writeBytes(rawBytes);
            buf.readerIndex(0);
            readBytes(buf);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new EncoderException(e);
        }
        finally {
            inflater.end();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
//        LogHelper.info(buf.unwrap().getClass());
        Deflater deflater = new Deflater();
        try {
            buf.writeBoolean(false);
            writeBytes(buf);
            buf.readerIndex(2);
//            LogHelper.dev("Raw Size " + buf.readableBytes() +" Array Size " + buf.array().length);
            int rawSize = buf.readableBytes();
            deflater.setInput(buf.array(), buf.readerIndex(), rawSize);
            deflater.finish();

            byte[] cBytes = new byte[rawSize];
            int cSize = deflater.deflate(cBytes);

//            LogHelper.dev("Compression: " + rawSize + " to " + (cSize + 6-6) + " [Compressed to " + (Utils.round((cSize + 6D) / rawSize, 100) * 100) + "% original size]");

            if (cSize >= rawSize - 6 || !deflater.finished()) {
//                LogHelper.dev("Compression No Good! I Ain't Doing It!");
                return;
            }

            buf.readerIndex(0);
            byte packetId = buf.readByte();

            byte[] out = new byte[cSize];
            System.arraycopy(cBytes, 0, out, 0, out.length);

            buf.clear();
            buf.writeByte(packetId);
            buf.writeBoolean(true);
            buf.writeInt(rawSize);
            buf.writeBytes(out);
        }
        catch (Exception e) {
            throw new EncoderException(e);
        }
        finally {
            buf.readerIndex(0);
            deflater.end();
        }
    }
}
