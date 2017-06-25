package com.brandon3055.draconicevolution.network;

import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.raytracer.RayTracer;
import com.brandon3055.brandonscore.handlers.HandHelper;
import com.brandon3055.brandonscore.network.MessageHandlerWrapper;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.PlacedItem;
import com.brandon3055.draconicevolution.blocks.tileentity.TilePlacedItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by brandon3055 on 25/07/2016.
 */
public class PacketPlaceItem implements IMessage {

    public PacketPlaceItem() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class Handler extends MessageHandlerWrapper<PacketPlaceItem, IMessage> {

        @Override
        public IMessage handleMessage(PacketPlaceItem message, MessageContext ctx) {
            EntityPlayer player = ctx.getServerHandler().playerEntity;
            RayTraceResult traceResult = RayTracer.retrace(player);
            World world = player.world;

            if (traceResult != null && traceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos posHit = traceResult.getBlockPos();
                TileEntity tileHit = world.getTileEntity(posHit);
                TileEntity tileOnSide = world.getTileEntity(posHit.offset(traceResult.sideHit));
                ItemStack stack = HandHelper.getMainFirst(player);

                if (stack.isEmpty()) {
                    return null;
                }

                if (tileHit instanceof TilePlacedItem && InventoryUtils.insertItem((TilePlacedItem) tileHit, stack, true) == 0) {
                    PlayerInteractEvent event = new PlayerInteractEvent.RightClickBlock(player, EnumHand.MAIN_HAND, posHit, traceResult.sideHit, traceResult.hitVec);
                    MinecraftForge.EVENT_BUS.post(event);

                    if (event.isCanceled()) {
                        return null;
                    }

                    InventoryUtils.insertItem((TilePlacedItem) tileHit, stack, false);
                    player.inventory.deleteStack(stack);
                }
                else if (tileOnSide instanceof TilePlacedItem && InventoryUtils.insertItem((TilePlacedItem) tileOnSide, stack, true) == 0) {
                    PlayerInteractEvent event = new PlayerInteractEvent.RightClickBlock(player, EnumHand.MAIN_HAND, posHit, traceResult.sideHit, traceResult.hitVec);
                    MinecraftForge.EVENT_BUS.post(event);

                    if (event.isCanceled()) {
                        return null;
                    }

                    InventoryUtils.insertItem((TilePlacedItem) tileOnSide, stack, false);
                    player.inventory.deleteStack(stack);
                }
                else if (world.isAirBlock(posHit.offset(traceResult.sideHit))) {
                    BlockPos pos = posHit.offset(traceResult.sideHit);
                    BlockEvent.PlaceEvent event = ForgeEventFactory.onPlayerBlockPlace(player, new BlockSnapshot(world, pos, DEFeatures.placedItem.getDefaultState()), traceResult.sideHit);

                    if (event.isCanceled()) {
                        return null;
                    }

                    world.setBlockState(traceResult.getBlockPos().offset(traceResult.sideHit), DEFeatures.placedItem.getDefaultState().withProperty(PlacedItem.FACING, traceResult.sideHit.getOpposite()));
                    TileEntity tile = world.getTileEntity(traceResult.getBlockPos().offset(traceResult.sideHit));
                    if (tile instanceof TilePlacedItem) {
                        ((TilePlacedItem) tile).facing = traceResult.sideHit.getOpposite();
                        ((TilePlacedItem) tile).setInventorySlotContents(0, stack);
                        player.inventory.deleteStack(stack);
                    }
                }
            }

            return null;
        }
    }
}
