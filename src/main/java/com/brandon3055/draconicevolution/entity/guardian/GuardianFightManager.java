package com.brandon3055.draconicevolution.entity.guardian;

import com.brandon3055.brandonscore.worldentity.ITickableWorldEntity;
import com.brandon3055.brandonscore.worldentity.WorldEntity;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.tileentity.TileChaosCrystal;
import com.brandon3055.draconicevolution.entity.GuardianCrystalEntity;
import com.brandon3055.draconicevolution.entity.guardian.control.PhaseType;
import com.brandon3055.draconicevolution.init.DEContent;
import com.google.common.collect.Sets;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Unit;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by brandon3055 on 16/12/20
 */
public class GuardianFightManager extends WorldEntity implements ITickableWorldEntity {
    private static final Logger LOGGER = DraconicEvolution.LOGGER;
    public static final int CRYSTAL_DIST_FROM_CENTER = 90;
    public static final int CRYSTAL_HEIGHT_FROM_ORIGIN = 40;
    private Predicate<Entity> validPlayer;
    private final ServerBossInfo bossInfo = (ServerBossInfo) (new ServerBossInfo(new TranslationTextComponent("entity.draconicevolution.draconic_guardian"), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS)).setPlayEndBossMusic(true).setCreateFog(true);
    private int ticksSinceGuardianSeen;
    private int aliveCrystals;
    private int ticksSinceCrystalsScanned;
    private int ticksSinceLastPlayerScan;
    private boolean guardianKilled;
    private UUID guardianUniqueId;
    private BlockPos arenaOrigin; //For now this can be the chaos crystal position
    private GuardianSpawnState respawnState;
    private int respawnStateTicks;
    private List<EnderCrystalEntity> crystals;
    private List<BlockPos> crystalsPosCache;

    public GuardianFightManager() {
        super(DEContent.guardianManagerType);
    }

    public GuardianFightManager(BlockPos origin) {
        super(DEContent.guardianManagerType);
        this.arenaOrigin = origin;
        this.validPlayer = EntityPredicates.IS_ALIVE.and(EntityPredicates.withinRange(origin.getX(), origin.getY(), origin.getZ(), 192.0D));
        this.respawnState = GuardianSpawnState.START_WAIT_FOR_PLAYER;
        this.bossInfo.setPercent(0);
    }

    @Override
    public void tick() {
//        bossInfo.setCreateFog(false);
        ServerWorld world = (ServerWorld) this.world;

        //Update Boss Info
        this.bossInfo.setVisible(!this.guardianKilled && (respawnState != GuardianSpawnState.START_WAIT_FOR_PLAYER));
        if (++this.ticksSinceLastPlayerScan >= 20) {
            this.updatePlayers();
            this.ticksSinceLastPlayerScan = 0;
        }

        //This is just using the player list in boss info to check if there are any players in the area.
        if (!this.bossInfo.getPlayers().isEmpty()) {
//            testGenPath();
            world.getChunkProvider().registerTicket(TicketType.DRAGON, new ChunkPos(arenaOrigin), 12, Unit.INSTANCE); //Is this chunk loading?
            boolean areaLoaded = this.isFightAreaLoaded();

            if (this.respawnState != null) {
                this.respawnState.process(world, this, this.crystals, this.respawnStateTicks++, this.arenaOrigin);
            }

            if (!this.guardianKilled && respawnState == null) {
                if ((this.guardianUniqueId == null || ++this.ticksSinceGuardianSeen >= 1200) && areaLoaded) {
                    this.findOrCreateGuardian();
                    this.ticksSinceGuardianSeen = 0;
                }

                if (++this.ticksSinceCrystalsScanned >= 100 && areaLoaded) {
                    this.findAliveCrystals();
                    this.ticksSinceCrystalsScanned = 0;
                }
            }
        } else {
            world.getChunkProvider().releaseTicket(TicketType.DRAGON, new ChunkPos(arenaOrigin), 12, Unit.INSTANCE);
        }
    }


    private void findOrCreateGuardian() {
        ServerWorld world = (ServerWorld) this.world;
        List<DraconicGuardianEntity> list = world.getEntities().filter(e -> e instanceof DraconicGuardianEntity).map(e -> (DraconicGuardianEntity) e).collect(Collectors.toList());
        if (list.isEmpty()) {
            LOGGER.debug("Haven't seen the guardian, respawning it");
            this.createNewGuardian();
        } else {
            LOGGER.debug("Haven't seen our guardian, but found another one to use.");
            this.guardianUniqueId = list.get(0).getUniqueID();
        }

    }

    protected void setRespawnState(GuardianSpawnState state) {
        if (this.respawnState == null) {
            throw new IllegalStateException("Guardian respawn isn't in progress, can't skip ahead in the animation.");
        } else {
            this.respawnStateTicks = 0;
            if (state == GuardianSpawnState.END) {
                this.respawnState = null;
                this.guardianKilled = false;
                DraconicGuardianEntity guardian = this.createNewGuardian();
                for (ServerPlayerEntity serverplayerentity : this.bossInfo.getPlayers()) {
                    CriteriaTriggers.SUMMONED_ENTITY.trigger(serverplayerentity, guardian);
                }
            } else {
                this.respawnState = state;
            }
        }
    }

    private boolean isFightAreaLoaded() {
        for (int x = -8; x <= 8; ++x) {
            for (int z = 8; z <= 8; ++z) {
                ChunkPos pos = new ChunkPos(arenaOrigin);
                IChunk ichunk = this.world.getChunk(pos.x + x, pos.z + z, ChunkStatus.FULL, false);
                if (!(ichunk instanceof Chunk)) {
                    return false;
                }

                ChunkHolder.LocationType locationType = ((Chunk) ichunk).getLocationType();
                if (!locationType.isAtLeast(ChunkHolder.LocationType.TICKING)) {
                    return false;
                }
            }
        }

        return true;
    }

    private void updatePlayers() {
        ServerWorld world = (ServerWorld) this.world;
        Set<ServerPlayerEntity> validPlayers = Sets.newHashSet();
        for (ServerPlayerEntity player : world.getPlayers(validPlayer)) {
            this.bossInfo.addPlayer(player);
            validPlayers.add(player);
//            ChatHelper.sendMessage(player, new StringTextComponent("You are now being tracked"));
        }

        Set<ServerPlayerEntity> invalidPlayers = Sets.newHashSet(this.bossInfo.getPlayers());
        invalidPlayers.removeAll(validPlayers);
        for (ServerPlayerEntity player : invalidPlayers) {
            this.bossInfo.removePlayer(player);
//            ChatHelper.sendMessage(player, new StringTextComponent("You are no longer tracked"));
        }
    }

    public void processDragonDeath(DraconicGuardianEntity guardian) {
        this.bossInfo.setPercent(0.0F);
        this.bossInfo.setVisible(false);
//            this.generatePortal(true); //Unlock Crystal?
        this.guardianKilled = true;
        TileEntity tile = world.getTileEntity(arenaOrigin);
        if (tile instanceof TileChaosCrystal) {
            ((TileChaosCrystal) tile).setDefeated();
        }
        cleanUpAndDispose();
    }

    private DraconicGuardianEntity createNewGuardian() {
        ServerWorld world = (ServerWorld) this.world;
        world.getChunkAt(guardianSpawnPos());
        DraconicGuardianEntity guardian = DEContent.draconicGuardian.create(world);
        guardian.getPhaseManager().setPhase(PhaseType.START);
//        guardian.getPhaseManager().setPhase(PhaseType.HOVER);
        guardian.setLocationAndAngles(guardianSpawnPos().getX(), guardianSpawnPos().getY(), guardianSpawnPos().getZ(), this.world.rand.nextFloat() * 360.0F, 0.0F);
        guardian.setFightManager(this);
        guardian.setArenaOrigin(arenaOrigin);
        world.addEntity(guardian);
        this.guardianUniqueId = guardian.getUniqueID();
        return guardian;
    }

    public void guardianUpdate(DraconicGuardianEntity guardian) {
        if (guardian.getUniqueID().equals(this.guardianUniqueId)) {
            this.bossInfo.setPercent(guardian.getHealth() / guardian.getMaxHealth());
            this.ticksSinceGuardianSeen = 0;
            if (!arenaOrigin.equals(guardian.getArenaOrigin())) {
                guardian.setArenaOrigin(arenaOrigin);
                guardian.initPathPoints(true);
            }
            if (guardian.hasCustomName()) {
                this.bossInfo.setName(guardian.getDisplayName());
            }
        }
    }

    private void cleanUpAndDispose() {
        ((ServerWorld) world).getChunkProvider().releaseTicket(TicketType.DRAGON, new ChunkPos(arenaOrigin), 12, Unit.INSTANCE);
        bossInfo.removeAllPlayers();
        removeEntity();
    }

    public UUID getGuardianUniqueId() {
        return guardianUniqueId;
    }

    public Collection<ServerPlayerEntity> getTrackedPlayers() {
        return bossInfo.getPlayers();
    }

    private void findAliveCrystals() {
        this.ticksSinceCrystalsScanned = 0;
        this.aliveCrystals = 0;

        for (BlockPos pos : getCrystalPositions()) {
            List<GuardianCrystalEntity> list = this.world.getEntitiesWithinAABB(GuardianCrystalEntity.class, new AxisAlignedBB(pos.add(-3, -3, -3), pos.add(4, 4, 4)));
            for (GuardianCrystalEntity crystal : list) {
                if (!crystal.getManagerId().equals(getUniqueID())) {
                    crystal.setManagerId(getUniqueID());
                }
            }
            this.aliveCrystals += list.size();
        }

        LOGGER.debug("Found {} end crystals still alive", (int) this.aliveCrystals);
    }

    public int getNumAliveCrystals() {
        return this.aliveCrystals;
    }

    public void onCrystalDestroyed(GuardianCrystalEntity crystal, DamageSource dmgSrc) {
        ServerWorld world = (ServerWorld) this.world;
        this.findAliveCrystals();
        Entity entity = world.getEntityByUuid(this.guardianUniqueId);
        if (entity instanceof DraconicGuardianEntity) {
            ((DraconicGuardianEntity) entity).onCrystalDestroyed(crystal, crystal.getPosition(), dmgSrc);
        }
    }

    public void resetCrystals() {
        for (BlockPos pos : getCrystalPositions()) {
            for (GuardianCrystalEntity endercrystalentity : this.world.getEntitiesWithinAABB(GuardianCrystalEntity.class, new AxisAlignedBB(pos.add(-3, -3, -3), pos.add(4, 4, 4)))) {
                endercrystalentity.setInvulnerable(false);
                endercrystalentity.setBeamTarget(null);
            }
        }
    }

    public List<GuardianCrystalEntity> getCrystals() {
        List<GuardianCrystalEntity> list = new ArrayList<>();
        for (BlockPos pos : getCrystalPositions()) {
            list.addAll(this.world.getEntitiesWithinAABB(GuardianCrystalEntity.class, new AxisAlignedBB(pos.add(-3, -3, -3), pos.add(4, 4, 4))));
        }
        return list;
    }

    public List<BlockPos> getCrystalPositions() {
        if (crystalsPosCache == null) {
            crystalsPosCache = new ArrayList<>();
            for (int i = 0; i < 14; i++) {
                double rotation = i * 0.45D;
                int sX = arenaOrigin.getX() + (int) (Math.sin(rotation) * CRYSTAL_DIST_FROM_CENTER);
                int sZ = arenaOrigin.getZ() + (int) (Math.cos(rotation) * CRYSTAL_DIST_FROM_CENTER);
                crystalsPosCache.add(new BlockPos(sX, arenaOrigin.getY() + CRYSTAL_HEIGHT_FROM_ORIGIN, sZ));
            }
        }
        return crystalsPosCache;
    }

    private List<BlockPos> crystalSpawnList;

    public BlockPos getNextCrystalPos(boolean initial) {
        if (initial) {
            crystalSpawnList = new ArrayList<>(getCrystalPositions());
        }
        if (crystalSpawnList.isEmpty()) {
            return null;
        }
        return crystalSpawnList.remove(0);
    }

    public BlockPos guardianSpawnPos() {
        return arenaOrigin.add(0, 80, 0);
    }


    public BlockPos getArenaOrigin() {
        return arenaOrigin;
    }

    @Override
    public void write(CompoundNBT nbt) {
        super.write(nbt);
        if (guardianUniqueId != null) {
            nbt.putUniqueId("guardian", guardianUniqueId);
        }

        nbt.putBoolean("guardian_killed", guardianKilled);
        nbt.put("arena_origin", NBTUtil.writeBlockPos(arenaOrigin));
        if (respawnState != null) {
            nbt.putBoolean("respawning", true);
        }
    }

    @Override
    public void read(CompoundNBT nbt) {
        super.read(nbt);
        if (nbt.hasUniqueId("guardian")) {
            guardianUniqueId = nbt.getUniqueId("guardian");
        }
        guardianKilled = nbt.getBoolean("guardian_killed");
        arenaOrigin = NBTUtil.readBlockPos(nbt.getCompound("arena_origin"));
        validPlayer = EntityPredicates.IS_ALIVE.and(EntityPredicates.withinRange(arenaOrigin.getX(), arenaOrigin.getY(), arenaOrigin.getZ(), 192.0D));
        if (nbt.getBoolean("respawning")) {
            respawnState = GuardianSpawnState.START_WAIT_FOR_PLAYER;
        }
    }
}





//    int tick = 0;
//        tick++;
//        int i = tick % 360;
//
////        for (int i = 0; i < 28; i++) {
//            float loopPos = i / 360F;
//            float angle = loopPos * 360;
//            int pointX = (20 * MathHelper.cos(angle * MathHelper.torad));
//            int pointZ = (20 * MathHelper.sin(angle * MathHelper.torad));
//            world.playEvent(4000, new BlockPos(arenaOrigin.getX() + pointX, arenaOrigin.getY() + 30, arenaOrigin.getZ() + pointZ), 0);
////        }
//    private void testGenPath() {
////        tick = 0;
////        if (true) return;
//        List<PathPoint> points = new ArrayList<>();
//
//        //Outer Circle
//        PathPoint lastPoint = null;
//        for (int i = 0; i < 28; i++) {
//            float loopPos = i / 28F;
//            float angle = loopPos * 360;
//            int pointX = MathHelper.floor((CRYSTAL_DIST_FROM_CENTER - 20) * Math.cos(angle * MathHelper.torad));
//            int pointZ = MathHelper.floor((CRYSTAL_DIST_FROM_CENTER - 20) * Math.sin(angle * MathHelper.torad));
//            int pointY = Math.max(arenaOrigin.getY() + CRYSTAL_HEIGHT_FROM_ORIGIN, this.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(pointX, 0, pointZ)).getY());
//            points.add(lastPoint = new PathPoint(arenaOrigin.getX() + pointX, pointY, arenaOrigin.getZ() + pointZ));
//        }
//
//        for (int i = 0; i < 5; i++) {
//            double angle = (i + 1) * 144;
//            int pointX = MathHelper.floor((CRYSTAL_DIST_FROM_CENTER - 20) * Math.cos(angle * MathHelper.torad));
//            int pointZ = MathHelper.floor((CRYSTAL_DIST_FROM_CENTER - 20) * Math.sin(angle * MathHelper.torad));
//            int pointY = Math.max(arenaOrigin.getY() + CRYSTAL_HEIGHT_FROM_ORIGIN, this.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(pointX, 0, pointZ)).getY());
//            PathPoint next = new PathPoint(arenaOrigin.getX() + pointX, pointY, arenaOrigin.getZ() + pointZ);
//
//            for (int j = 0; j < 7; j++) {
//                float loopPos = j / 7F;
//                double x = lastPoint.x + ((next.x - lastPoint.x) * loopPos);
//                double y = lastPoint.y + ((next.y - lastPoint.y) * loopPos);
//                double z = lastPoint.z + ((next.z - lastPoint.z) * loopPos);
//                points.add(lastPoint = new PathPoint(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z)));
//            }
//        }
//
//
//        int shiftTime = 10;
//        for (int i = 0; i < shiftTime / 2; i++) {
//            tick++;
//            int pointIdx = (tick / shiftTime) % points.size();
//            int nextPointIdx = (pointIdx + 1) % points.size();
//            double pos = (tick % shiftTime) / (double) shiftTime;
//            PathPoint point = points.get(pointIdx);
//            PathPoint nextPoint = points.get(nextPointIdx);
//
//            double x = point.x + ((nextPoint.x - point.x) * pos);
//            double y = point.y + ((nextPoint.y - point.y) * pos);
//            double z = point.z + ((nextPoint.z - point.z) * pos);
//
//            world.playEvent(4000, new BlockPos(x, y, z), 0);
//        }
//
//
//    }
