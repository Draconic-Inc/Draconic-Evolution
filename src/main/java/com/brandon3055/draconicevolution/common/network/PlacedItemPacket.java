package com.brandon3055.draconicevolution.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.BlockEvent;

import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.tileentities.TilePlacedItem;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

/**
 * Created by Brandon on 14/08/2014.
 */
public class PlacedItemPacket implements IMessage {

    byte side = 0;
    int blockX = 0;
    int blockY = 0;
    int blockZ = 0;

    public PlacedItemPacket() {}

    public PlacedItemPacket(byte side, int x, int y, int z) {
        this.side = side;
        this.blockX = x;
        this.blockY = y;
        this.blockZ = z;
    }

    @Override
    public void toBytes(ByteBuf bytes) {
        bytes.writeByte(side);
        bytes.writeInt(blockX);
        bytes.writeInt(blockY);
        bytes.writeInt(blockZ);
    }

    @Override
    public void fromBytes(ByteBuf bytes) {
        this.side = bytes.readByte();
        this.blockX = bytes.readInt();
        this.blockY = bytes.readInt();
        this.blockZ = bytes.readInt();
    }

    public static class Handler implements IMessageHandler<PlacedItemPacket, IMessage> {

        @Override
        public IMessage onMessage(PlacedItemPacket message, MessageContext ctx) {
            ForgeDirection dir = ForgeDirection.getOrientation(message.side);
            int x = message.blockX + dir.offsetX;
            int y = message.blockY + dir.offsetY;
            int z = message.blockZ + dir.offsetZ;
            World world = ctx.getServerHandler().playerEntity.worldObj;
            EntityPlayer player = ctx.getServerHandler().playerEntity;

            if (!world.isAirBlock(x, y, z) || player.getHeldItem() == null
                    || !ModBlocks.isEnabled(ModBlocks.placedItem))
                return null;

            BlockEvent.PlaceEvent event = new BlockEvent.PlaceEvent(
                    new BlockSnapshot(world, x, y, z, ModBlocks.placedItem, 0),
                    world.getBlock(message.blockX, message.blockY, message.blockZ),
                    player);
            MinecraftForge.EVENT_BUS.post(event);

            if (event.isCanceled()) {
                return null;
            }

            ItemStack stack = player.getHeldItem();

            world.setBlock(x, y, z, ModBlocks.placedItem, message.side, 2);
            TilePlacedItem tile = (world.getTileEntity(x, y, z) != null
                    && world.getTileEntity(x, y, z) instanceof TilePlacedItem)
                            ? (TilePlacedItem) world.getTileEntity(x, y, z)
                            : null;

            if (tile == null) {
                world.setBlockToAir(x, y, z);
                return null;
            }

            tile.setStack(stack.copy());
            player.destroyCurrentEquippedItem();
            return null;
        }
    }
}
