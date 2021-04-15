package com.brandon3055.draconicevolution.handlers;

import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.utils.DataUtils;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.draconicevolution.api.ITeleportEndPoint;

import com.brandon3055.draconicevolution.entity.PersistentItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.brandon3055.draconicevolution.init.DEContent.dislocator_p2p;

/**
 * Created by brandon3055 on 27/2/2018.
 */
public class DislocatorLinkHandler extends WorldSavedData {

    private static final String SAVE_DATA_NAME = "DECustomData";
    public Map<String, LinkData> linkDataMap = new HashMap<>();

    public DislocatorLinkHandler(String name) {
        super(name);
    }


    @Nullable
    public static DislocatorLinkHandler getDataInstance(World world) {
//        WorldSavedData storage = world.getServer().getWorld(DimensionType.OVERWORLD).getSavedData().getOrCreate(() -> new DislocatorLinkHandler(SAVE_DATA_NAME) , SAVE_DATA_NAME);
//        if (storage == null) {
//            LogHelper.bigError("Detected null MapStorage! This may cause issues!");
//            return null;
//        }
        if (world instanceof ServerWorld) {
            return world.getServer().getLevel(World.OVERWORLD).getDataStorage().computeIfAbsent(() -> new DislocatorLinkHandler(SAVE_DATA_NAME), SAVE_DATA_NAME);
        }
        return null;
//        if (data != null && data instanceof DislocatorLinkHandler) {
//            return (DislocatorLinkHandler) data;
//        }

//        data = new DislocatorLinkHandler(SAVE_DATA_NAME);
//        storage.setData(SAVE_DATA_NAME, data);
//        data.markDirty();
//        storage.saveAllData();
//        return (DislocatorLinkHandler) data;
    }


    public static void updateLink(World world, ItemStack stack, BlockPos pos, RegistryKey<World> dimension) {
        DislocatorLinkHandler data = getDataInstance(world);
        if (data == null || world.isClientSide || !dislocator_p2p.isValid(stack)) {
            return;
        }
        String linkID = dislocator_p2p.getLinkID(stack);
        LinkData link = data.linkDataMap.computeIfAbsent(linkID, s -> new LinkData(linkID, data));
        link.setTarget(pos, dimension);
    }

    public static void updateLink(World world, ItemStack stack, PlayerEntity player) {
        DislocatorLinkHandler data = getDataInstance(world);
        if (data == null || world.isClientSide || !dislocator_p2p.isValid(stack)) {
            return;
        }
        String linkID = dislocator_p2p.getLinkID(stack);

        LinkData link = data.linkDataMap.computeIfAbsent(linkID, s -> new LinkData(linkID, data));
        link.setTarget(player.getGameProfile().getId().toString());
    }

    public static void removeLink(World world, ItemStack stack) {
        DislocatorLinkHandler data = getDataInstance(world);
        if (data == null || world.isClientSide || !dislocator_p2p.isValid(stack)) {
            return;
        }
        String linkID = dislocator_p2p.getLinkID(stack);
        data.linkDataMap.remove(linkID);
        data.setDirty();
    }

    /**
     * Returns the location of the dislocator this is bound to
     */
    public static Vec3D getLinkPos(World world, ItemStack stack) {
        DislocatorLinkHandler data = getDataInstance(world);
        if (data == null || !dislocator_p2p.isValid(stack)) {
            return null;
        }

        if (dislocator_p2p.isPlayer(stack)) {
            return getPlayerPos(ItemNBTHelper.getString(stack, "PlayerLink", "null"), world);
        } else {
            String linkID = dislocator_p2p.getLinkToID(stack);
            LinkData link = data.linkDataMap.get(linkID);
            if (link != null) {
                if (link.isPlayer) {
                    return getAndValidatePlayerPos(world, data, link, linkID);
                } else {
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
            } else {
                return getTileOrEntityPos(link, linkID);
            }
        }

        return null;
    }

    public static TileEntity getTargetTile(World world, ItemStack stack) {
        DislocatorLinkHandler data = getDataInstance(world);
        if (data == null || !dislocator_p2p.isValid(stack)) {
            return null;
        }

        if (!dislocator_p2p.isPlayer(stack)) {
            String linkID = dislocator_p2p.getLinkToID(stack);
            LinkData link = data.linkDataMap.get(linkID);
            if (link != null && !link.isPlayer) {
                MinecraftServer server = BrandonsCore.proxy.getMCServer();
                if (server == null) {
                    return null;
                }
                World targetWorld = server.getLevel(link.dimension);

                TileEntity tile = targetWorld.getBlockEntity(link.pos);
                if (tile instanceof ITeleportEndPoint) {
                    return tile;
                }
            }
        }

        return null;
    }

    private static Vec3D getAndValidatePlayerPos(World world, DislocatorLinkHandler data, LinkData link, String linkID) {
        if (world.getServer() == null) {
            return null;
        }
        PlayerList players = world.getServer().getPlayerList();
        PlayerEntity player = players.getPlayer(UUID.fromString(link.playerUUID));
        boolean flag = DataUtils.firstMatch(player.inventory.items, stack -> dislocator_p2p.isValid(stack) && dislocator_p2p.getLinkID(stack).equals(linkID)) != null;
        flag = flag || DataUtils.firstMatch(player.inventory.offhand, stack -> dislocator_p2p.isValid(stack) && dislocator_p2p.getLinkID(stack).equals(linkID)) != null;
        if (!flag) {
            data.linkDataMap.remove(linkID);
            data.setDirty();
            return null;
        }
        return new Vec3D(player);
    }

    private static Vec3D getTileOrEntityPos(LinkData link, String linkID) {
        MinecraftServer server = BrandonsCore.proxy.getMCServer();
        if (server == null) {
            return null;
        }
        World targetWorld = server.getLevel(link.dimension);

        TileEntity tile = targetWorld.getBlockEntity(link.pos);
        if (tile instanceof ITeleportEndPoint) {
            BlockPos tilePos = ((ITeleportEndPoint) tile).getArrivalPos(linkID);
            if (tilePos == null) {
                return null;
            }
            return new Vec3D(tilePos.getX() + 0.5, tilePos.getY() + 0.2, tilePos.getZ() + 0.5);
        } else {
            AxisAlignedBB bb = new AxisAlignedBB(link.pos, link.pos.offset(1, 1, 1));
            bb.inflate(5);
            List<PersistentItemEntity> items = targetWorld.getEntitiesOfClass(PersistentItemEntity.class, bb);
            for (PersistentItemEntity item : items) {
                ItemStack i = item.getItem();
                if (dislocator_p2p.isValid(i)) {
                    String l = dislocator_p2p.getLinkID(i);
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
    public static LinkData getLink(ItemStack stack, ServerWorld world) {
        DislocatorLinkHandler data = getDataInstance(world);
        if (data == null || !dislocator_p2p.isValid(stack)) {
            return null;
        }
        return data.linkDataMap.get(dislocator_p2p.getLinkToID(stack));
    }

    public static Vec3D getPlayerPos(String playerID, World world) {
        if (world.getServer() == null) {
            return null;
        }
        PlayerList players = world.getServer().getPlayerList();
        PlayerEntity player = players.getPlayer(UUID.fromString(playerID));
        return player == null ? null : new Vec3D(player);
    }

//    public static boolean validateStack(ItemStack stack) {
//        return !stack.isEmpty() && stack.getItem() == dislocator_p2p && stack.hasTag();
//    }

    @Override
    public void load(CompoundNBT nbt) {
        linkDataMap.clear();
        ListNBT dataList = nbt.getList("LinkList", 10);
        dataList.forEach(nbtBase -> {
            LinkData data = new LinkData(this).fromNBT((CompoundNBT) nbtBase);
            linkDataMap.put(data.linkID, data);
        });
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        ListNBT dataList = new ListNBT();
        linkDataMap.forEach((id, linkData) -> dataList.add(linkData.toNBT(new CompoundNBT())));
        compound.put("LinkList", dataList);
        return compound;
    }

    //Link data is only required when linking to a block because it its in a players inventory we can just look up the player.
    public static class LinkData {
        private String linkID;
        public DislocatorLinkHandler handler;
        public BlockPos pos = new BlockPos(0, 128, 0);
        public RegistryKey<World> dimension;
        private String playerUUID;
        public boolean isPlayer = false;

        public LinkData(DislocatorLinkHandler handler) {this.handler = handler;}

        public LinkData(String linkID, DislocatorLinkHandler handler) {
            this.linkID = linkID;
            this.handler = handler;
        }

        public void setTarget(BlockPos pos, RegistryKey<World> dimension) {
            this.pos = pos;
            this.dimension = dimension;
            isPlayer = false;
            handler.setDirty();
        }

        public void setTarget(String playerUUID) {
            this.playerUUID = playerUUID;
            isPlayer = true;
            handler.setDirty();
        }

        public CompoundNBT toNBT(CompoundNBT compound) {
            compound.putString("LinkID", linkID);
            compound.putBoolean("IsPlayer", isPlayer);
            compound.putString("Dim", dimension.location().toString());

            if (isPlayer) {
                compound.putString("PlayerID", playerUUID);
            } else {
                compound.putLong("Pos", pos.asLong());
            }
            return compound;
        }

        public LinkData fromNBT(CompoundNBT compound) {
            linkID = compound.getString("LinkID");
            isPlayer = compound.getBoolean("IsPlayer");
            dimension = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(compound.getString("Dim")));

            if (isPlayer) {
                playerUUID = compound.getString("PlayerID");
            } else {
                pos = BlockPos.of(compound.getLong("Pos"));
            }
            return this;
        }
    }
}
