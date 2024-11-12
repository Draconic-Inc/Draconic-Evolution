package com.brandon3055.draconicevolution.entity.guardian;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.worldentity.ITickableWorldEntity;
import com.brandon3055.brandonscore.worldentity.WorldEntity;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.IDraconicMelee;
import com.brandon3055.draconicevolution.blocks.tileentity.TileChaosCrystal;
import com.brandon3055.draconicevolution.entity.GuardianCrystalEntity;
import com.brandon3055.draconicevolution.entity.guardian.control.PhaseType;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.init.DEDamage;
import com.brandon3055.draconicevolution.world.ShieldedServerBossInfo;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Unit;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Predicate;

/**
 * Created by brandon3055 on 16/12/20
 */
public class GuardianFightManager extends WorldEntity implements ITickableWorldEntity {
    private static final Logger LOGGER = DraconicEvolution.LOGGER;
    public static final int CRYSTAL_DIST_FROM_CENTER = 90;
    public static final int CRYSTAL_HEIGHT_FROM_ORIGIN = 40;

    public static final float PROJECTILE_POWER = 15;
    public static final float CHARGE_DAMAGE = 150;
    public static final float COVER_FIRE_POWER = 15;

    private Predicate<Entity> validPlayer;
    private final ShieldedServerBossInfo bossInfo = (ShieldedServerBossInfo) (new ShieldedServerBossInfo(Component.translatable("entity.draconicevolution.draconic_guardian"), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS))
            .setPlayBossMusic(true)
            .setCreateWorldFog(true);
    private int ticksSinceGuardianSeen;
    private int aliveCrystals;
    private int ticksSinceCrystalsScanned;
    private int ticksSinceLastPlayerScan;
    private boolean guardianKilled;
    private UUID guardianUniqueId;
    private BlockPos arenaOrigin; //For now this can be the chaos crystal position
    public GuardianSpawnState respawnState;
    private int respawnStateTicks;
    private List<BlockPos> crystalsPosCache;

    public GuardianFightManager() {
        super(DEContent.WE_GUARDIAN_MANAGER.get());
    }

    public GuardianFightManager(BlockPos origin) {
        super(DEContent.WE_GUARDIAN_MANAGER.get());
        this.arenaOrigin = origin;
        this.validPlayer = EntitySelector.ENTITY_STILL_ALIVE.and(EntitySelector.withinDistance(origin.getX(), origin.getY(), origin.getZ(), 250.0D)); //Had to increase this range because it was still possible to cheese the guardian
        this.respawnState = GuardianSpawnState.START_WAIT_FOR_PLAYER;
        this.bossInfo.setProgress(0);
        this.bossInfo.setShieldPower(0);
    }

    @Override
    public void tick() {
        ServerLevel world = (ServerLevel) level;
        //Update Boss Info
        this.bossInfo.setVisible(!this.guardianKilled && (respawnState != GuardianSpawnState.START_WAIT_FOR_PLAYER));
        if (++this.ticksSinceLastPlayerScan >= 20) {
            this.updatePlayers();
            this.ticksSinceLastPlayerScan = 0;
        }

        //This is just using the player list in boss info to check if there are any players in the area.
        if (!this.bossInfo.getPlayers().isEmpty()) {
            world.getChunkSource().addRegionTicket(TicketType.DRAGON, new ChunkPos(arenaOrigin), 12, Unit.INSTANCE); //Is this chunk loading?
            boolean areaLoaded = this.isFightAreaLoaded();

            if (this.respawnState != null) {
                this.respawnState.process(world, this, null, this.respawnStateTicks++, this.arenaOrigin);
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
            world.getChunkSource().removeRegionTicket(TicketType.DRAGON, new ChunkPos(arenaOrigin), 12, Unit.INSTANCE);
        }
    }

    private void findOrCreateGuardian() {
        ServerLevel world = (ServerLevel) level;
        List<DraconicGuardianEntity> list = Streams.stream(world.getEntities().getAll()).filter(e -> e instanceof DraconicGuardianEntity).map(e -> (DraconicGuardianEntity) e).toList();
        if (list.isEmpty()) {
            LOGGER.debug("Haven't seen the guardian, respawning it");
            this.createNewGuardian();
        } else {
            LOGGER.debug("Haven't seen our guardian, but found another one to use.");
            this.guardianUniqueId = list.get(0).getUUID();
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
                findAliveCrystals();
                DraconicGuardianEntity guardian = this.createNewGuardian();
                for (ServerPlayer serverplayerentity : this.bossInfo.getPlayers()) {
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
                ChunkAccess ichunk = level.getChunk(pos.x + x, pos.z + z, ChunkStatus.FULL, false);
                if (!(ichunk instanceof LevelChunk)) {
                    return false;
                }

                if (!level.getChunkSource().hasChunk(pos.x + x, pos.z + z)) {
                    return false;
                }
            }
        }

        return true;
    }

    private void updatePlayers() {
        ServerLevel world = (ServerLevel) level;
        Set<ServerPlayer> validPlayers = Sets.newHashSet();
        for (ServerPlayer player : world.getPlayers(validPlayer)) {
            this.bossInfo.addPlayer(player);
            validPlayers.add(player);
        }

        Set<ServerPlayer> invalidPlayers = Sets.newHashSet(this.bossInfo.getPlayers());
        invalidPlayers.removeAll(validPlayers);
        for (ServerPlayer player : invalidPlayers) {
            this.bossInfo.removePlayer(player);
        }
    }

    public void processDragonDeath(DraconicGuardianEntity guardian) {
        this.bossInfo.setProgress(0.0F);
        this.bossInfo.setShieldPower(0);
        this.bossInfo.setVisible(false);
        this.guardianKilled = true;
        BlockEntity tile = level.getBlockEntity(arenaOrigin);
        if (tile instanceof TileChaosCrystal) {
            ((TileChaosCrystal) tile).setDefeated();
        }

        GuardianFightManager manager = guardian.getFightManager();
        ItemEntity item = EntityType.ITEM.create(guardian.level());
        if (manager != null && item != null) {
            item.setItem(new ItemStack(DEContent.DRAGON_HEART.get()));
            BlockPos podiumPos = manager.getArenaOrigin().above(20);
            item.moveTo(podiumPos.getX() + 0.5, podiumPos.getY(), podiumPos.getZ() + 0.5, 0, 0);
            item.setDeltaMovement(0, 0, 0);
            item.age = -32767;
            item.setInvulnerable(true);
            item.setNoGravity(true);
            item.getPersistentData().putBoolean("guardian_heart", true); //This is here to help fox out
            guardian.level().addFreshEntity(item);
        }

        cleanUpAndDispose();
    }

    private DraconicGuardianEntity createNewGuardian() {
        ServerLevel world = (ServerLevel) this.level;
        world.getChunkAt(guardianSpawnPos());
        DraconicGuardianEntity guardian = DEContent.ENTITY_DRACONIC_GUARDIAN.get().create(world);
        assert guardian != null;
        guardian.getPhaseManager().setPhase(PhaseType.START);
        guardian.moveTo(guardianSpawnPos().getX(), guardianSpawnPos().getY(), guardianSpawnPos().getZ(), this.level.random.nextFloat() * 360.0F, 0.0F);
        guardian.setFightManager(this);
        guardian.setArenaOrigin(arenaOrigin);
        world.addFreshEntity(guardian);
        this.guardianUniqueId = guardian.getUUID();
        return guardian;
    }

    public void guardianUpdate(DraconicGuardianEntity guardian) {
        if (guardian.getUUID().equals(this.guardianUniqueId)) {
            this.bossInfo.setProgress(guardian.getHealth() / guardian.getMaxHealth());
            this.bossInfo.setShieldPower(guardian.getShieldPower() / (float) DEConfig.guardianShield);
            this.bossInfo.setImmune(guardian.getPhaseManager().getCurrentPhase().isInvulnerable());
            bossInfo.setColor(guardian.getShieldPower() > 0 ? BossEvent.BossBarColor.PURPLE : BossEvent.BossBarColor.RED);
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
        ((ServerLevel) level).getChunkSource().removeRegionTicket(TicketType.DRAGON, new ChunkPos(arenaOrigin), 12, Unit.INSTANCE);
        bossInfo.removeAllPlayers();
        removeEntity();
    }

    public UUID getGuardianUniqueId() {
        return guardianUniqueId;
    }

    public Collection<ServerPlayer> getTrackedPlayers() {
        return bossInfo.getPlayers();
    }

    private void findAliveCrystals() {
        this.ticksSinceCrystalsScanned = 0;
        this.aliveCrystals = 0;

        for (BlockPos pos : getCrystalPositions()) {
            List<GuardianCrystalEntity> list = this.level.getEntitiesOfClass(GuardianCrystalEntity.class, new AABB(pos).inflate(3));
            for (GuardianCrystalEntity crystal : list) {
                if (!crystal.isAlive()) continue;
                if (crystal.getManagerId() == null || !crystal.getManagerId().equals(getUniqueID())) {
                    crystal.setManagerId(getUniqueID());
                }
                this.aliveCrystals++;
            }
        }
        this.bossInfo.setCrystals(getNumAliveCrystals());

        LOGGER.debug("Found {} end crystals still alive", (int) this.aliveCrystals);
    }

    public void crystalSpawned() {
        aliveCrystals++;
        this.bossInfo.setCrystals(getNumAliveCrystals());
    }

    public int getNumAliveCrystals() {
        return this.aliveCrystals;
    }

    public void onCrystalAttacked(GuardianCrystalEntity crystal, DamageSource dmgSrc, float damage, boolean destroyed) {
        ServerLevel world = (ServerLevel) this.level;
        if (destroyed) {
            this.findAliveCrystals();
        }
        Entity entity = world.getEntity(this.guardianUniqueId);
        if (entity instanceof DraconicGuardianEntity) {
            ((DraconicGuardianEntity) entity).onCrystalAttacked(crystal, crystal.blockPosition(), dmgSrc, damage, destroyed);
        }
    }

    public float getCrystalDamageModifier(GuardianCrystalEntity crystal, DamageSource dmgSrc) {
        if (dmgSrc.getEntity() instanceof DraconicGuardianEntity) {
            crystal.destabilize();
            return 0.1F; //Still want the player to have to do some work here
        } else if (DEDamage.getDamageLevel(dmgSrc) == TechLevel.CHAOTIC && DEConfig.chaoticBypassCrystalShield) {
            crystal.destabilize();
            return 1F;
        }
        return crystal.getUnstableTime() > 0 ? 1F : 0F;
    }

    /**
     * @return false to block the damage (There are still crystals alive)
     */
    public boolean onGuardianAttacked(DraconicGuardianEntity guardian, DamageSource source, float damage) {
        return getNumAliveCrystals() == 0;
    }

    public void resetCrystals() {
        for (BlockPos pos : getCrystalPositions()) {
            for (GuardianCrystalEntity endercrystalentity : this.level.getEntitiesOfClass(GuardianCrystalEntity.class, new AABB(pos).inflate(3))) {
                endercrystalentity.setInvulnerable(false);
                endercrystalentity.setBeamTarget(null);
            }
        }
    }

    public List<GuardianCrystalEntity> getCrystals() {
        List<GuardianCrystalEntity> list = new ArrayList<>();
        for (BlockPos pos : getCrystalPositions()) {
            list.addAll(this.level.getEntitiesOfClass(GuardianCrystalEntity.class, new AABB(pos).inflate(3)));
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
        return arenaOrigin.offset(0, 80, 0);
    }

    public BlockPos getArenaOrigin() {
        return arenaOrigin;
    }

    @Override
    public void write(CompoundTag nbt) {
        super.write(nbt);
        if (guardianUniqueId != null) {
            nbt.putUUID("guardian", guardianUniqueId);
        }

        nbt.putBoolean("guardian_killed", guardianKilled);
        nbt.put("arena_origin", NbtUtils.writeBlockPos(arenaOrigin));
        if (respawnState != null) {
            nbt.putBoolean("respawning", true);
        }
    }

    @Override
    public void read(CompoundTag nbt) {
        super.read(nbt);
        if (nbt.hasUUID("guardian")) {
            guardianUniqueId = nbt.getUUID("guardian");
        }
        guardianKilled = nbt.getBoolean("guardian_killed");
        arenaOrigin = NbtUtils.readBlockPos(nbt.getCompound("arena_origin"));
        validPlayer = EntitySelector.ENTITY_STILL_ALIVE.and(EntitySelector.withinDistance(arenaOrigin.getX(), arenaOrigin.getY(), arenaOrigin.getZ(), 192.0D));
        if (nbt.getBoolean("respawning")) {
            respawnState = GuardianSpawnState.START_WAIT_FOR_PLAYER;
        }
    }
}