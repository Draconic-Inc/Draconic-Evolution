package com.brandon3055.draconicevolution.handlers;

import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.utils.DataUtils;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.draconicevolution.api.ITeleportEndPoint;
import com.brandon3055.draconicevolution.entity.EntityPersistentItem;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.brandon3055.draconicevolution.DEFeatures.dislocatorBound;

/**
 * Created by brandon3055 on 27/2/2018.
 *
 */
public class DislocatorLinkHandler extends WorldSavedData {

    private static final String SAVE_DATA_NAME = "DECustomData";
    public Map<String, LinkData> linkDataMap = new HashMap<>();

    public DislocatorLinkHandler(String name) {
        super(name);
    }


    @Nullable
    public static DislocatorLinkHandler getDataInstance(World world) {
        MapStorage storage = world.getMapStorage();
        if (storage == null) {
            LogHelper.bigError("Detected null MapStorage! This may cause issues!");
            return null;
        }
        WorldSavedData data = storage.getOrLoadData(DislocatorLinkHandler.class, SAVE_DATA_NAME);
        if (data != null && data instanceof DislocatorLinkHandler) {
            return (DislocatorLinkHandler) data;
        }

        data = new DislocatorLinkHandler(SAVE_DATA_NAME);
        storage.setData(SAVE_DATA_NAME, data);
        data.markDirty();
        storage.saveAllData();
        return (DislocatorLinkHandler) data;
    }

    public static void updateLink(World world, ItemStack stack, BlockPos pos, int dimension) {
        DislocatorLinkHandler data = getDataInstance(world);
        if (data == null || world.isRemote || !dislocatorBound.isValid(stack)) {
            return;
        }
        String linkID = dislocatorBound.getLinkID(stack);
        LinkData link = data.linkDataMap.computeIfAbsent(linkID, s -> new LinkData(linkID, data));
        link.setTarget(pos, dimension);
    }

    public static void updateLink(World world, ItemStack stack, EntityPlayer player) {
        DislocatorLinkHandler data = getDataInstance(world);
        if (data == null || world.isRemote || !dislocatorBound.isValid(stack)) {
            return;
        }
        String linkID = dislocatorBound.getLinkID(stack);

        LinkData link = data.linkDataMap.computeIfAbsent(linkID, s -> new LinkData(linkID, data));
        link.setTarget(player.getGameProfile().getId().toString());
    }

    public static void removeLink(World world, ItemStack stack) {
        DislocatorLinkHandler data = getDataInstance(world);
        if (data == null || world.isRemote || !dislocatorBound.isValid(stack)) {
            return;
        }
        String linkID = dislocatorBound.getLinkID(stack);
        data.linkDataMap.remove(linkID);
        data.markDirty();
    }

    /**
     * Returns the location of the dislocator this is bound to
     */
    public static Vec3D getLinkPos(World world, ItemStack stack) {
        DislocatorLinkHandler data = getDataInstance(world);
        if (data == null || !dislocatorBound.isValid(stack)) {
            return null;
        }

        if (dislocatorBound.isPlayer(stack)) {
            return getPlayerPos(ItemNBTHelper.getString(stack, "PlayerLink", "null"), world);
        }
        else {
            String linkID = dislocatorBound.getLinkToID(stack);
            LinkData link = data.linkDataMap.get(linkID);
            if (link != null) {
                if (link.isPlayer) {
                    return getAndValidatePlayerPos(world, data, link, linkID);
                }
                else {
                   return getTileOrEntityPos(link, linkID);
                }
            }
        }

        return null;
    }

    /**
     * Returns the location of the dislocator with this link id
     */
    public static Vec3D getLinkPos(World world, String linkID) {
        DislocatorLinkHandler data = getDataInstance(world);
        if (data == null) {
            return null;
        }
        LinkData link = data.linkDataMap.get(linkID);
        if (link != null) {
            if (link.isPlayer) {
                return getAndValidatePlayerPos(world, data, link, linkID);
            }
            else {
                return getTileOrEntityPos(link, linkID);
            }
        }

        return null;
    }

    public static TileEntity getTargetTile(World world, ItemStack stack) {
        DislocatorLinkHandler data = getDataInstance(world);
        if (data == null || !dislocatorBound.isValid(stack)) {
            return null;
        }

        if (!dislocatorBound.isPlayer(stack)) {
            String linkID = dislocatorBound.getLinkToID(stack);
            LinkData link = data.linkDataMap.get(linkID);
            if (link != null && !link.isPlayer) {
                MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
                if (server == null || !DimensionManager.isDimensionRegistered(link.dimension)) {
                    return null;
                }
                World targetWorld = server.getWorld(link.dimension);

                TileEntity tile = targetWorld.getTileEntity(link.pos);
                if (tile instanceof ITeleportEndPoint) {
                    return tile;
                }
            }
        }

        return null;
    }

    private static Vec3D getAndValidatePlayerPos(World world, DislocatorLinkHandler data, LinkData link, String linkID) {
        if (world.getMinecraftServer() == null) {
            return null;
        }
        PlayerList players = world.getMinecraftServer().getPlayerList();
        EntityPlayer player = players.getPlayerByUUID(UUID.fromString(link.playerUUID));
        boolean flag = DataUtils.firstMatch(player.inventory.mainInventory, stack -> dislocatorBound.isValid(stack) && dislocatorBound.getLinkID(stack).equals(linkID)) != null;
        flag = flag || DataUtils.firstMatch(player.inventory.offHandInventory, stack -> dislocatorBound.isValid(stack) && dislocatorBound.getLinkID(stack).equals(linkID)) != null;
        if (!flag) {
            data.linkDataMap.remove(linkID);
            data.markDirty();
            return null;
        }
        return new Vec3D(player);
    }

    private static Vec3D getTileOrEntityPos(LinkData link, String linkID) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null || !DimensionManager.isDimensionRegistered(link.dimension)) {
            return null;
        }
        World targetWorld = server.getWorld(link.dimension);

        TileEntity tile = targetWorld.getTileEntity(link.pos);
        if (tile instanceof ITeleportEndPoint) {
            BlockPos tilePos = ((ITeleportEndPoint) tile).getArrivalPos(linkID);
            if (tilePos == null) {
                return null;
            }
            return new Vec3D(tilePos.getX() + 0.5, tilePos.getY() + 0.2, tilePos.getZ() + 0.5);
        }
        else {
            AxisAlignedBB bb = new AxisAlignedBB(link.pos, link.pos.add(1, 1, 1));
            bb.grow(5);
            List<EntityPersistentItem> items = targetWorld.getEntitiesWithinAABB(EntityPersistentItem.class, bb);
            for (EntityPersistentItem item : items) {
                ItemStack i = item.getItem();
                if (dislocatorBound.isValid(i)) {
                    String l = dislocatorBound.getLinkID(i);
                    if (l.equals(linkID)) {
                        return new Vec3D(item);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns the link data for the dislocator this dislocator is bound to
     */
    public static LinkData getLink(ItemStack stack, World world) {
        DislocatorLinkHandler data = getDataInstance(world);
        if (data == null || !dislocatorBound.isValid(stack)) {
            return null;
        }
        return data.linkDataMap.get(dislocatorBound.getLinkToID(stack));
    }

    public static Vec3D getPlayerPos(String playerID, World world) {
        if (world.getMinecraftServer() == null) {
            return null;
        }
        PlayerList players = world.getMinecraftServer().getPlayerList();
        EntityPlayer player = players.getPlayerByUUID(UUID.fromString(playerID));
        return player == null ? null : new Vec3D(player);
    }

    public static boolean validateStack(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == dislocatorBound && stack.hasTagCompound() && stack.getMetadata() == 0;
    }


    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        linkDataMap.clear();
        NBTTagList dataList = nbt.getTagList("LinkList", 10);
        dataList.forEach(nbtBase -> {
            LinkData data = new LinkData(this).fromNBT((NBTTagCompound) nbtBase);
            linkDataMap.put(data.linkID, data);
        });
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagList dataList = new NBTTagList();
        linkDataMap.forEach((id, linkData) -> dataList.appendTag(linkData.toNBT(new NBTTagCompound())));
        compound.setTag("LinkList", dataList);
        return compound;
    }

    //Link data is only required when linking to a block because it its in a players inventory we can just look up the player.
    public static class LinkData {
        private String linkID;
        public DislocatorLinkHandler handler;
        public BlockPos pos = new BlockPos(0, 128, 0);
        public int dimension = 0;
        private String playerUUID;
        public boolean isPlayer = false;

        public LinkData(DislocatorLinkHandler handler) {this.handler = handler;}

        public LinkData(String linkID, DislocatorLinkHandler handler) {
            this.linkID = linkID;
            this.handler = handler;
        }

        public void setTarget(BlockPos pos, int dimension) {
            this.pos = pos;
            this.dimension = dimension;
            isPlayer = false;
            handler.markDirty();
        }

        public void setTarget(String playerUUID) {
            this.playerUUID = playerUUID;
            isPlayer = true;
            handler.markDirty();
        }

        public NBTTagCompound toNBT(NBTTagCompound compound) {
            compound.setString("LinkID", linkID);
            compound.setBoolean("IsPlayer", isPlayer);
            compound.setInteger("Dim", dimension);

            if (isPlayer) {
                compound.setString("PlayerID", playerUUID);
            }
            else {
                compound.setLong("Pos", pos.toLong());
            }
            return compound;
        }

        public LinkData fromNBT(NBTTagCompound compound) {
            linkID = compound.getString("LinkID");
            isPlayer = compound.getBoolean("IsPlayer");
            dimension = compound.getInteger("Dim");

            if (isPlayer) {
                playerUUID = compound.getString("PlayerID");
            }
            else {
                pos = BlockPos.fromLong(compound.getLong("Pos"));
            }
            return this;
        }
    }
}
