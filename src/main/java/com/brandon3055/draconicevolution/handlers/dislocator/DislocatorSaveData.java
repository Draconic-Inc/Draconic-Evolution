package com.brandon3055.draconicevolution.handlers.dislocator;

import com.brandon3055.draconicevolution.items.tools.BoundDislocator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by brandon3055 on 28/8/21
 */
public class DislocatorSaveData extends SavedData {
    private static final String FILE_NAME = "draconic_dislocator_data";

    private Map<UUID, Map<UUID, DislocatorTarget>> linkTargetMap = new HashMap<>();

    public DislocatorSaveData() {}

    /**
     * @param world A server world required to access the save data manager.
     * @param stack The bound dislocator stack in our possession. We are looking for the location of the dislocator bound to this one.
     * @return The target associated with the other end of this link if it can be found.
     */
    @Nullable
    public static DislocatorTarget getLinkTarget(Level world, ItemStack stack) {
        if (BoundDislocator.isValid(stack)) {
            DislocatorSaveData saveData = getInstance(world);
            if (saveData != null) {
                UUID linkID = BoundDislocator.getLinkId(stack);
                if (saveData.linkTargetMap.containsKey(linkID)) {
                    Map<UUID, DislocatorTarget> targetMap = saveData.linkTargetMap.get(linkID);
                    UUID targetID = BoundDislocator.getDislocatorId(stack);
                    for (Map.Entry<UUID, DislocatorTarget> entry : targetMap.entrySet()) {
                        UUID entryID = entry.getKey();
                        if (!entryID.equals(targetID)) {
                            return entry.getValue();
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * @param world A server world required to access the save data manager.
     * @param stack The dislocator stack whose position is being updated.
     * @param target The new location of this dislocator.
     */
    public static void updateLinkTarget(Level world, ItemStack stack, DislocatorTarget target) {
        if (BoundDislocator.isValid(stack)) {
            DislocatorSaveData saveData = getInstance(world);
            if (saveData != null) {
                UUID linkID = BoundDislocator.getLinkId(stack);
                UUID targetID = BoundDislocator.getDislocatorId(stack);
                saveData.linkTargetMap.computeIfAbsent(linkID, uuid -> new HashMap<>()).put(targetID, target);
                saveData.setDirty();
            }
        }
    }

    @Nullable
    public static DislocatorSaveData getInstance(Level world) {
        if (world instanceof ServerLevel && world.getServer() != null) {
            ServerLevel level = world.getServer().getLevel(Level.OVERWORLD);
            if (level != null) {
                return level.getDataStorage().computeIfAbsent(new Factory<>(DislocatorSaveData::new, DislocatorSaveData::load), FILE_NAME);
            }
        }
        return null;
    }

    public static DislocatorSaveData load(CompoundTag nbt) {
        DislocatorSaveData data = new DislocatorSaveData();
        ListTag linkList = nbt.getList("link_map", 10);
        for (Tag lnbt : linkList) {
            CompoundTag linkNBT = (CompoundTag) lnbt;
            UUID linkID = linkNBT.getUUID("link_id");
            ListTag targetList = linkNBT.getList("targets", 10);
            Map<UUID, DislocatorTarget> targetMap = data.linkTargetMap.computeIfAbsent(linkID, uuid -> new HashMap<>());
            for (Tag tnbt : targetList) {
                CompoundTag targetNBT = (CompoundTag) tnbt;
                UUID targetID = targetNBT.getUUID("target_id");
                DislocatorTarget target = DislocatorTarget.load(targetNBT);
                targetMap.put(targetID, target);
            }
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        ListTag linkList = new ListTag();
        for (UUID linkID : linkTargetMap.keySet()) {
            Map<UUID, DislocatorTarget> targetMap = linkTargetMap.get(linkID);
            CompoundTag linkNBT = new CompoundTag();
            linkNBT.putUUID("link_id", linkID);

            ListTag targetList = new ListTag();
            for (UUID targetID : targetMap.keySet()) {
                DislocatorTarget target = targetMap.get(targetID);
                CompoundTag targetNBT = new CompoundTag();
                targetNBT.putUUID("target_id", targetID);
                target.save(targetNBT);
                targetList.add(targetNBT);
            }

            linkNBT.put("targets", targetList);
            linkList.add(linkNBT);
        }
        nbt.put("link_map", linkList);
        return nbt;
    }
}
