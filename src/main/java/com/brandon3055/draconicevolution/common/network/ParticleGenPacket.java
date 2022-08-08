package com.brandon3055.draconicevolution.common.network;

import com.brandon3055.draconicevolution.common.tileentities.TileParticleGenerator;
import com.brandon3055.draconicevolution.common.utills.LogHelper;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;

public class ParticleGenPacket implements IMessage {
    byte buttonId = 0;
    short value = 0;
    int tileX = 0;
    int tileY = 0;
    int tileZ = 0;

    public ParticleGenPacket() {}

    public ParticleGenPacket(byte buttonId, short value, int x, int y, int z) {
        this.buttonId = buttonId;
        this.value = value;
        this.tileX = x;
        this.tileY = y;
        this.tileZ = z;
    }

    @Override
    public void toBytes(ByteBuf bytes) {
        bytes.writeByte(buttonId);
        bytes.writeShort(value);
        bytes.writeInt(tileX);
        bytes.writeInt(tileY);
        bytes.writeInt(tileZ);
    }

    @Override
    public void fromBytes(ByteBuf bytes) {
        this.buttonId = bytes.readByte();
        this.value = bytes.readShort();
        this.tileX = bytes.readInt();
        this.tileY = bytes.readInt();
        this.tileZ = bytes.readInt();
    }

    public static class Handler implements IMessageHandler<ParticleGenPacket, IMessage> {

        @Override
        public IMessage onMessage(ParticleGenPacket message, MessageContext ctx) {
            TileEntity tile = ctx.getServerHandler()
                    .playerEntity
                    .worldObj
                    .getTileEntity(message.tileX, message.tileY, message.tileZ);
            TileParticleGenerator gen =
                    (tile != null && tile instanceof TileParticleGenerator) ? (TileParticleGenerator) tile : null;
            if (gen != null) {
                // System.out.println(buttonId + " " + value);
                switch (message.buttonId) {
                    case 0: // red
                        gen.red = (int) message.value;
                        break;
                    case 1: // green
                        gen.green = (int) message.value;
                        break;
                    case 2: // blue
                        gen.blue = (int) message.value;
                        break;
                    case 3: // mx
                        gen.motion_x = (float) message.value / 1000F;
                        break;
                    case 4: // my
                        gen.motion_y = (float) message.value / 1000F;
                        break;
                    case 5: // mz
                        gen.motion_z = (float) message.value / 1000F;
                        break;
                    case 6: // ged
                        gen.red = (int) message.value;
                        break;
                    case 7: // green
                        gen.green = (int) message.value;
                        break;
                    case 8: // blue
                        gen.blue = (int) message.value;
                        break;
                    case 9: // mx
                        gen.motion_x = (float) message.value / 1000F;
                        break;
                    case 10: // my
                        gen.motion_y = (float) message.value / 1000F;
                        break;
                    case 11: // mz
                        gen.motion_z = (float) message.value / 1000F;
                        break;
                    case 12: // red
                        gen.random_red = (int) message.value;
                        break;
                    case 13: // green
                        gen.random_green = (int) message.value;
                        break;
                    case 14: // blue
                        gen.random_blue = (int) message.value;
                        break;
                    case 15: // mx
                        gen.random_motion_x = (float) message.value / 1000F;
                        break;
                    case 16: // my
                        gen.random_motion_y = (float) message.value / 1000F;
                        break;
                    case 17: // mz
                        gen.random_motion_z = (float) message.value / 1000F;
                        break;
                    case 18: // ged
                        gen.random_red = (int) message.value;
                        break;
                    case 19: // green
                        gen.random_green = (int) message.value;
                        break;
                    case 20: // blue
                        gen.random_blue = (int) message.value;
                        break;
                    case 21: // mx
                        gen.random_motion_x = (float) message.value / 1000F;
                        break;
                    case 22: // my
                        gen.random_motion_y = (float) message.value / 1000F;
                        break;
                    case 23: // mz
                        gen.random_motion_z = (float) message.value / 1000F;
                        break;
                    case 24: // Life +
                        gen.life = (int) message.value;
                        break;
                    case 25: // Life -
                        gen.life = (int) message.value;
                        break;
                    case 26: // RLife +
                        gen.random_life = (int) message.value;
                        break;
                    case 27: // RLife -
                        gen.random_life = (int) message.value;
                        break;
                    case 28: // Size +
                        gen.scale = (float) message.value / 100F;
                        break;
                    case 29: // Size -
                        gen.scale = (float) message.value / 100F;
                        break;
                    case 30: // RSize +
                        gen.random_scale = (float) message.value / 100F;
                        break;
                    case 31: // RSize -
                        gen.random_scale = (float) message.value / 100F;
                        break;
                    case 32: // SX +
                        gen.page = (int) message.value;
                        break;
                    case 33: // SX -
                        gen.page = (int) message.value;
                        break;
                    case 34: // SX +
                        gen.spawn_x = (float) message.value / 100F;
                        break;
                    case 35: // SX -
                        gen.spawn_x = (float) message.value / 100F;
                        break;
                    case 36: // RSX +
                        gen.random_spawn_x = (float) message.value / 100F;
                        break;
                    case 37: // RSX -
                        gen.random_spawn_x = (float) message.value / 100F;
                        break;
                    case 38: // SY +
                        gen.spawn_y = (float) message.value / 100F;
                        break;
                    case 39: // SY -
                        gen.spawn_y = (float) message.value / 100F;
                        break;
                    case 40: // RSY +
                        gen.random_spawn_y = (float) message.value / 100F;
                        break;
                    case 41: // RSY -
                        gen.random_spawn_y = (float) message.value / 100F;
                        break;
                    case 42: // SZ +
                        gen.spawn_z = (float) message.value / 100F;
                        break;
                    case 43: // SZ -
                        gen.spawn_z = (float) message.value / 100F;
                        break;
                    case 44: // RSZ +
                        gen.random_spawn_z = (float) message.value / 100F;
                        break;
                    case 45: // RSZ -
                        gen.random_spawn_z = (float) message.value / 100F;
                        break;
                    case 46: // Delay -
                        gen.spawn_rate = (int) message.value;
                        break;
                    case 47: // Delay -
                        gen.spawn_rate = (int) message.value;
                        break;
                    case 48: // Fade -
                        gen.fade = (int) message.value;
                        break;
                    case 49: // Fade -
                        gen.fade = (int) message.value;
                        break;
                    case 50: // Collision -
                        gen.collide = message.value == 0 ? false : true;
                        break;
                    case 51: // Particle Selected -
                        gen.selected_particle = (int) message.value;
                        break;
                    case 52: // Gravity +
                        gen.gravity = (float) message.value / 1000F;
                        break;
                    case 53: // Gravity -
                        gen.gravity = (float) message.value / 1000F;
                        break;
                    case 54: // Info Page (3)
                        gen.page = (int) message.value;
                        break;
                    case 55: // Back
                        gen.page = (int) message.value;
                        break;
                    case 58: // Back
                        LogHelper.info(message.value);
                        gen.particles_enabled = message.value == 1;
                        break;

                    case 100: // beam red +
                        gen.beam_red = message.value;
                        break;
                    case 101: // beam green +
                        gen.beam_green = message.value;
                        break;
                    case 102: // beam blue +
                        gen.beam_blue = message.value;
                        break;
                    case 103: // beam pitch +
                        gen.beam_pitch = (float) message.value / 100F;
                        break;
                    case 104: // beam yaw +
                        gen.beam_yaw = (float) message.value / 100F;
                        break;
                    case 105: // beam length +
                        gen.beam_length = (float) message.value / 100F;
                        break;
                    case 106: // beam rotation +
                        gen.beam_rotation = (float) message.value / 100F;
                        break;
                    case 107: // beam scale +
                        gen.beam_scale = (float) message.value / 100F;
                        break;
                    case 108: // beam red -
                        gen.beam_red = message.value;
                        break;
                    case 109: // beam green -
                        gen.beam_green = message.value;
                        break;
                    case 110: // beam blue -
                        gen.beam_blue = message.value;
                        break;
                    case 111: // beam pitch -
                        gen.beam_pitch = (float) message.value / 100F;
                        break;
                    case 112: // beam yaw -
                        gen.beam_yaw = (float) message.value / 100F;
                        break;
                    case 113: // beam length -
                        gen.beam_length = (float) message.value / 100F;
                        break;
                    case 114: // beam rotation -
                        gen.beam_rotation = (float) message.value / 100F;
                        break;
                    case 115: // beam scale -
                        gen.beam_scale = (float) message.value / 100F;
                        break;
                    case 116: // beam enabled
                        gen.beam_enabled = message.value == 1;
                        break;
                    case 117: // beam enabled
                        gen.render_core = message.value == 1;
                        break;
                }

                if (message.buttonId == 127) {
                    if (ctx.getServerHandler().playerEntity.capabilities.isCreativeMode
                            || ctx.getServerHandler().playerEntity.inventory.hasItem(Items.paper)) {
                        giveNote(message, ctx);
                    } else
                        ctx.getServerHandler()
                                .playerEntity
                                .addChatComponentMessage(
                                        new ChatComponentText("You need paper in your inventory to do that"));
                }

                ctx.getServerHandler()
                        .playerEntity
                        .worldObj
                        .markBlockForUpdate(message.tileX, message.tileY, message.tileZ);
            }
            return null;
        }

        private void giveNote(ParticleGenPacket message, MessageContext ctx) {
            EntityPlayer player = ctx.getServerHandler().playerEntity;
            if (!player.capabilities.isCreativeMode) player.inventory.consumeInventoryItem(Items.paper);
            ItemStack stack = new ItemStack(Items.paper);
            stack.setTagCompound(new NBTTagCompound());
            TileEntity tile = ctx.getServerHandler()
                    .playerEntity
                    .worldObj
                    .getTileEntity(message.tileX, message.tileY, message.tileZ);
            TileParticleGenerator gen =
                    (tile != null && tile instanceof TileParticleGenerator) ? (TileParticleGenerator) tile : null;
            if (gen != null) {
                gen.getBlockNBT(stack.getTagCompound());
                stack.setStackDisplayName("Saved Particle Gen Settings");
                player.worldObj.spawnEntityInWorld(
                        new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, stack));
            }
        }
    }
}
