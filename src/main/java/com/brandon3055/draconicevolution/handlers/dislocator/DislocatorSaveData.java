package com.brandon3055.draconicevolution.handlers.dislocator;

import com.brandon3055.draconicevolution.items.tools.BoundDislocator;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by brandon3055 on 28/8/21
 */
public class DislocatorSaveData extends WorldSavedData {
    private static final String SAVE_DATA_NAME = "draconic_dislocator_data";

    private Map<UUID, Map<UUID, DislocatorTarget>> linkTargetMap = new HashMap<>();

    public DislocatorSaveData(String name) {
        super(name);
    }

    /**
     * @param world A server world required to access the save data manager.
     * @param stack The bound dislocator stack in our possession. We are looking for the location of the dislocator bound to this one.
     * @return The target associated with the other end of this link if it can be found.
     */
    @Nullable
    public static DislocatorTarget getLinkTarget(World world, ItemStack stack) {
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
    public static void updateLinkTarget(World world, ItemStack stack, DislocatorTarget target) {
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
    public static DislocatorSaveData getInstance(World world) {
        if (world instanceof ServerWorld && world.getServer() != null) {
            ServerWorld level = world.getServer().getLevel(World.OVERWORLD);
            if (level != null) {
                return level.getDataStorage().computeIfAbsent(() -> new DislocatorSaveData(SAVE_DATA_NAME), SAVE_DATA_NAME);
            }
        }
        return null;
    }

    @Override
    public void load(CompoundNBT nbt) {
        linkTargetMap.clear();
        ListNBT linkList = nbt.getList("link_map", 10);
        for (INBT lnbt : linkList) {
            CompoundNBT linkNBT = (CompoundNBT) lnbt;
            UUID linkID = linkNBT.getUUID("link_id");
            ListNBT targetList = linkNBT.getList("targets", 10);
            Map<UUID, DislocatorTarget> targetMap = linkTargetMap.computeIfAbsent(linkID, uuid -> new HashMap<>());
            for (INBT tnbt : targetList) {
                CompoundNBT targetNBT = (CompoundNBT) tnbt;
                UUID targetID = targetNBT.getUUID("target_id");
                DislocatorTarget target = DislocatorTarget.load(targetNBT);
                targetMap.put(targetID, target);
            }
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        ListNBT linkList = new ListNBT();
        for (UUID linkID : linkTargetMap.keySet()) {
            Map<UUID, DislocatorTarget> targetMap = linkTargetMap.get(linkID);
            CompoundNBT linkNBT = new CompoundNBT();
            linkNBT.putUUID("link_id", linkID);

            ListNBT targetList = new ListNBT();
            for (UUID targetID : targetMap.keySet()) {
                DislocatorTarget target = targetMap.get(targetID);
                CompoundNBT targetNBT = new CompoundNBT();
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
