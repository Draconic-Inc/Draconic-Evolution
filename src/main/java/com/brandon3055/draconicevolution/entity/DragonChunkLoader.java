package com.brandon3055.draconicevolution.entity;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.MinecraftForge;

import java.util.*;

/**
 * Created by brandon3055 on 21/10/2015.
 */
public class DragonChunkLoader implements LoadingCallback {
    public static DragonChunkLoader instance;
    public static Map<EntityChaosGuardian, Ticket> ticketList = new HashMap<EntityChaosGuardian, Ticket>();
    public static boolean hasReportedIssue = false;

    public static void init() {
        instance = new DragonChunkLoader();
        MinecraftForge.EVENT_BUS.register(instance);
        ForgeChunkManager.setForcedChunkLoadingCallback(DraconicEvolution.instance, instance);
    }

    public static void updateLoaded(EntityChaosGuardian guardian) {
        Ticket ticket;

        if (ticketList.containsKey(guardian)) {
            ticket = ticketList.get(guardian);
        } else {
            ticket = ForgeChunkManager.requestTicket(DraconicEvolution.instance, guardian.worldObj, ForgeChunkManager.Type.ENTITY);

            if (ticket != null) {
                ticket.bindEntity(guardian);
                ticket.setChunkListDepth(9);
                ticketList.put(guardian, ticket);

            } else {
                if (!hasReportedIssue) {
                    LogHelper.error("##########################################################################################");
                    LogHelper.error("Could not get ticket for dragon");
                    LogHelper.error("Fore some reason forge has denied DE's request for a loader ticket for the chaos guardian");
                    LogHelper.error("This means the chaos guardian may not behave as indented");
                    LogHelper.error("This error will not show again.");
                    LogHelper.error("##########################################################################################");
                    hasReportedIssue = true;
                }
                return;
            }
        }

        if (ticket == null || guardian.ticksExisted % 4 != 0) return;

        Set<ChunkPos> dragonChunks = new HashSet<ChunkPos>();

        for (int xx = guardian.chunkCoordX - 1; xx <= guardian.chunkCoordX + 1; xx++) {
            for (int zz = guardian.chunkCoordZ - 1; zz <= guardian.chunkCoordZ + 1; zz++) {
                dragonChunks.add(new ChunkPos(xx, zz));
            }
        }

        Set<ChunkPos> toLoad = new HashSet<ChunkPos>();
        Set<ChunkPos> toUnload = new HashSet<ChunkPos>();

        for (ChunkPos pair : ticket.getChunkList()) {
            if (!contains(dragonChunks, pair)) toUnload.add(pair);
        }

        for (ChunkPos pair : dragonChunks) {
            if (!contains(ticket.getChunkList(), pair)) toLoad.add(pair);
        }

        for (ChunkPos unload : toUnload) ForgeChunkManager.unforceChunk(ticket, unload);
        for (ChunkPos load : toLoad) ForgeChunkManager.forceChunk(ticket, load);
    }

    private static boolean contains(Set<ChunkPos> set, ChunkPos pair) {
        for (ChunkPos pair1 : set) if (pair1.equals(pair)) return true;
        return false;
    }

    public static void stopLoading(EntityChaosGuardian guardian) {
        if (!ticketList.containsKey(guardian)) return;
        ForgeChunkManager.releaseTicket(ticketList.get(guardian));
        ticketList.remove(guardian);
    }

    @Override
    public void ticketsLoaded(List<Ticket> tickets, World world) {
        if (!tickets.isEmpty()) {
            for (Ticket ticket : tickets) {
                if (ticket.getType() == ForgeChunkManager.Type.ENTITY && ticket.getEntity() instanceof EntityChaosGuardian) {
                    updateLoaded((EntityChaosGuardian) ticket.getEntity());
                }
            }
        }
    }
}
