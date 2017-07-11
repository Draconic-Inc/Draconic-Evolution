package com.brandon3055.draconicevolution.network;

import com.brandon3055.brandonscore.handlers.HandHelper;
import com.brandon3055.brandonscore.network.MessageHandlerWrapper;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.GuiHandler;
import com.brandon3055.draconicevolution.api.itemconfig.IConfigurableItem;
import com.brandon3055.draconicevolution.api.itemconfig.ToolConfigHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSimpleBoolean implements IMessage {
    public static final byte ID_TOOL_CONFIG = 0;
    public static final byte ID_TOOL_PROFILE_CHANGE = 1;

    //	public static final byte ID_WEATHERCONTROLLER =
//	public static final byte ID_DISSENCHANTER =
//	public static final byte ID_DRACONIUMCHEST0 =
//	public static final byte ID_DRACONIUMCHEST1 =
//	public static final byte ID_DRACONIUMCHEST2 =
//	public static final byte ID_DRACONIUMCHEST3 =
//	public static final byte ID_DRACONIUMCHEST4 =
//	public static final byte ID_TOOL_PROFILE_CHANGE =
    byte id = 0;
    boolean value = false;

    public PacketSimpleBoolean() {
    }

    public PacketSimpleBoolean(byte id, boolean state) {
        this.id = id;
        this.value = state;
    }

    @Override
    public void fromBytes(ByteBuf bytes) {
        this.id = bytes.readByte();
        this.value = bytes.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf bytes) {
        bytes.writeByte(id);
        bytes.writeBoolean(value);
    }

    public static class Handler extends MessageHandlerWrapper<PacketSimpleBoolean, IMessage> {

        @Override
        public IMessage handleMessage(PacketSimpleBoolean message, MessageContext ctx) {
            switch (message.id) {

                case ID_TOOL_CONFIG: {
                    ctx.getServerHandler().player.openGui(DraconicEvolution.instance, GuiHandler.GUIID_TOOL_CONFIG, ctx.getServerHandler().player.world, (int) ctx.getServerHandler().player.posX, (int) ctx.getServerHandler().player.posY, (int) ctx.getServerHandler().player.posZ);
                    break;
                }
                case ID_TOOL_PROFILE_CHANGE: {
                    ItemStack stack = HandHelper.getMainFirst(ctx.getServerHandler().player);
                    if (!stack.isEmpty() && stack.getItem() instanceof IConfigurableItem) {
                        ToolConfigHelper.incrementProfile(stack);
                    }

                    break;
                }

//				case ID_WEATHERCONTROLLER:
//				{
//					Container container = ctx.getServerHandler().playerEntity.openContainer;
//					if (container != null && container instanceof ContainerWeatherController){
//						TileWeatherController tileWC = ((ContainerWeatherController) container).getTileWC();
//						tileWC.reciveButtonEvent(message.id);
//					}
//					break;
//				}
//				case ID_DISSENCHANTER:
//				{
//					Container container = ctx.getServerHandler().playerEntity.openContainer;
//					if (container != null && container instanceof ContainerDissEnchanter){
//						TileDissEnchanter tile = ((ContainerDissEnchanter) container).getTile();
//						tile.buttonClick(ctx.getServerHandler().playerEntity);
//					}
//					break;
//				}
//				case ID_DRACONIUMCHEST0:
//				{
//					Container container = ctx.getServerHandler().playerEntity.openContainer;
//					if (container != null && container instanceof ContainerDraconiumChest){
//						TileDraconiumChest tile = ((ContainerDraconiumChest) container).getTile();
//						tile.setAutoFeed(0);
//					}
//					break;
//				}
//				case ID_DRACONIUMCHEST1:
//				{
//					Container container = ctx.getServerHandler().playerEntity.openContainer;
//					if (container != null && container instanceof ContainerDraconiumChest){
//						TileDraconiumChest tile = ((ContainerDraconiumChest) container).getTile();
//						tile.setAutoFeed(1);
//					}
//					break;
//				}
//				case ID_DRACONIUMCHEST2:
//				{
//					Container container = ctx.getServerHandler().playerEntity.openContainer;
//					if (container != null && container instanceof ContainerDraconiumChest){
//						TileDraconiumChest tile = ((ContainerDraconiumChest) container).getTile();
//						tile.setAutoFeed(2);
//					}
//					break;
//				}
//				case ID_DRACONIUMCHEST3:
//				{
//					Container container = ctx.getServerHandler().playerEntity.openContainer;
//					if (container != null && container instanceof ContainerDraconiumChest){
//						TileDraconiumChest tile = ((ContainerDraconiumChest) container).getTile();
//						tile.setAutoFeed(3);
//					}
//					break;
//				}
//				case ID_DRACONIUMCHEST4:
//				{
//					Container container = ctx.getServerHandler().playerEntity.openContainer;
//					if (container != null && container instanceof ContainerDraconiumChest){
//						TileDraconiumChest tile = ((ContainerDraconiumChest) container).getTile();
//						tile.lockOutputSlots = ! tile.lockOutputSlots;
//					}
//					break;
//				}

//				case ID_TOOL_PROFILE_CHANGE:
//				{
//					ItemStack stack = ctx.getServerHandler().playerEntity.getHeldItem();
//					if (stack != null && stack.getItem() instanceof IConfigurableItem && ((IConfigurableItem)stack.getItem()).hasProfiles()){
//						int preset = ItemNBTHelper.getInteger(stack, "ConfigProfile", 0);
//						if (++preset >= 5) preset = 0;
//						ItemNBTHelper.setInteger(stack, "ConfigProfile", preset);
//					}
//					break;
//				}

                default:
                    break;
            }
            return null;
        }

    }
}