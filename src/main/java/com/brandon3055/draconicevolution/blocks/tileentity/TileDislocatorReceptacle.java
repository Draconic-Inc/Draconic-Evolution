package com.brandon3055.draconicevolution.blocks.tileentity;

import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.api.hud.IHudBlock;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.brandonscore.inventory.TileItemStackHandler;
import com.brandon3055.brandonscore.lib.DelayedTask;
import com.brandon3055.brandonscore.lib.IInteractTile;
import com.brandon3055.brandonscore.lib.IRSSwitchable;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.lib.datamanager.DataFlags;
import com.brandon3055.brandonscore.lib.datamanager.ManagedByte;
import com.brandon3055.brandonscore.lib.datamanager.ManagedEnum;
import com.brandon3055.brandonscore.lib.datamanager.ManagedPos;
import com.brandon3055.brandonscore.network.BCoreNetwork;
import com.brandon3055.brandonscore.utils.*;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.DislocatorEndPoint;
import com.brandon3055.draconicevolution.api.energy.ICrystalLink;
import com.brandon3055.draconicevolution.api.energy.IENetEffectTile;
import com.brandon3055.draconicevolution.blocks.DislocatorReceptacle;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandler;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandlerClient;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandlerServer;
import com.brandon3055.draconicevolution.client.render.effect.CrystalFXBase;
import com.brandon3055.draconicevolution.handlers.DEEventHandler;
import com.brandon3055.draconicevolution.handlers.DESounds;
import com.brandon3055.draconicevolution.handlers.dislocator.DislocatorSaveData;
import com.brandon3055.draconicevolution.handlers.dislocator.DislocatorTarget;
import com.brandon3055.draconicevolution.handlers.dislocator.PlayerTarget;
import com.brandon3055.draconicevolution.handlers.dislocator.TileTarget;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.items.tools.BoundDislocator;
import com.brandon3055.draconicevolution.items.tools.Dislocator;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by brandon3055 on 16/07/2016.
 */
public class TileDislocatorReceptacle extends TileBCore implements IInteractTile, IHudBlock, IRSSwitchable, DislocatorEndPoint, ICrystalLink, IENetEffectTile {
    private static final Random rand = new Random();

    public final ManagedPos arrivalPos = register(new ManagedPos("arrival_pos", (BlockPos) null, DataFlags.SAVE_NBT_SYNC_TILE));
    public final ManagedByte ignitionStage = register(new ManagedByte("ignition_stage", (byte) 0, DataFlags.SYNC_TILE));
    public final ManagedEnum<Axis> activeAxis = register(new ManagedEnum<>("active_axis", Axis.X, DataFlags.SAVE_NBT_SYNC_TILE));
    public final ManagedPos linkedCrystal = register(new ManagedPos("crystal_pos", (BlockPos) null, DataFlags.SAVE_NBT_SYNC_TILE));
    public final ManagedByte remoteCrystalTier = register(new ManagedByte("crystal_pos_tier", 0, DataFlags.SAVE_NBT_SYNC_TILE));
    public final ManagedByte linkedFlowRate = register(new ManagedByte("linked_flow_rate", 0, DataFlags.SYNC_TILE));
    public final ManagedPos crystalLinkPos = register(new ManagedPos("crystal_link_pos", (BlockPos) null, DataFlags.SAVE_NBT_SYNC_TILE));

    public TileItemStackHandler itemHandler = new TileItemStackHandler(this, 1);
    private PortalHelper portalHelper = new PortalHelper(this);
    //A que is used to get around the issue of an entity touching multiple portal blocks simultaneously and getting teleported more than once.
    private List<Entity> teleportQ = new ArrayList<>();

    public TileDislocatorReceptacle(BlockPos pos, BlockState state) {
        super(DEContent.TILE_DISLOCATOR_RECEPTACLE.get(), pos, state);
        capManager.setManaged("inventory", Capabilities.ItemHandler.BLOCK, itemHandler).saveBoth().syncTile();
        itemHandler.setContentsChangeListener(e -> onInventoryChange());
        itemHandler.setSlotValidator(0, (stack) -> stack.getItem() instanceof Dislocator);
        fxHandler = DraconicEvolution.proxy.createENetFXHandler(this);
    }

    public static void register(RegisterCapabilitiesEvent event) {
        capability(event, DEContent.TILE_DISLOCATOR_RECEPTACLE, CapabilityOP.BLOCK);
        capability(event, DEContent.TILE_DISLOCATOR_RECEPTACLE, Capabilities.ItemHandler.BLOCK);
    }

    @Override
    public void onSignalChange(boolean newSignal) {
        if (newSignal) {
            attemptActivation();
        } else {
            deactivate();
        }
    }

    @Override
    public InteractionResult onBlockUse(BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (hasRSSignal()) {
            return InteractionResult.PASS;
        }

        if (stack.getItem() == DEContent.INFUSED_OBSIDIAN.get().asItem()) {
            level.setBlockAndUpdate(worldPosition, state.setValue(DislocatorReceptacle.CAMO, !state.getValue(DislocatorReceptacle.CAMO)));
            return InteractionResult.SUCCESS;
        }

        if (!level.isClientSide) {
            ItemStack previousInstalled = itemHandler.getStackInSlot(0);
            InventoryUtils.handleHeldStackTransfer(0, itemHandler, player);
            //Transfer the dislocator that was in the pedestal to the players inventory
            if (BoundDislocator.isValid(previousInstalled) && BoundDislocator.isP2P(previousInstalled) && itemHandler.getStackInSlot(0).isEmpty()) {
                DislocatorSaveData.updateLinkTarget(level, previousInstalled, new PlayerTarget(player));
            }

            checkIn();
        }

        return InteractionResult.SUCCESS;
    }

    private void onInventoryChange() {
        if (level.isClientSide) return;

        if (portalHelper.isRunning()) {
            portalHelper.abort();
        }

        ItemStack stack = itemHandler.getStackInSlot(0);
        if (stack.isEmpty() && isActive()) {
            deactivate();
        } else if (!stack.isEmpty()) {
            attemptActivation();
        }
    }

    public void attemptActivation() {
        if (level.isClientSide || isActive() || portalHelper.isRunning()) return;
        TargetPos target = getTargetPos();
        if (target != null) {
            portalHelper.startScan();
            ignitionStage.set(1);
        }
    }

    public void deactivate() {
        setActive(false);
        for (BlockPos pos : BlockPos.betweenClosed(getBlockPos().offset(-1, -1, -1), getBlockPos().offset(1, 1, 1))) {
            BlockEntity tile = level.getBlockEntity(pos);
            if (tile instanceof TilePortal && ((TilePortal) tile).getControllerPos().equals(getBlockPos())) {
                level.removeBlock(pos, false);
            }
        }
    }

    public void handleEntityTeleport(Entity entity) {
        if (level.isClientSide || teleportQ.contains(entity)) {
            return;
        }

        if (entity.isOnPortalCooldown()) {
            entity.setPortalCooldown();
            return;
        }

        teleportQ.add(entity);
    }

    @Override
    public void tick() {
        updateCrystalLogic();
        super.tick();
        if (level.isClientSide) return;

        if (portalHelper.isRunning()) {
            int maxSpeed = portalHelper.isBuilding() ? 125 : 384; //ignitionStage.get() == 1 ? 128 : 256;
            int cycles = Utils.scaleToTPS(level, maxSpeed / 8, maxSpeed);
            for (int i = 0; i < cycles && portalHelper.isRunning(); i++) {
                portalHelper.updateTick();
            }
        } else if (!teleportQ.isEmpty()) {
            for (Entity entity : teleportQ) {
                TargetPos target = getTargetPos();
                if (target == null) {
                    deactivate();
                    teleportQ.clear();
                    return;
                }

                BCoreNetwork.sendSound(entity.level(), entity.blockPosition(), DESounds.PORTAL.get(), SoundSource.PLAYERS, 0.1F, rand.nextFloat() * 0.1F + 0.9F, false);
                entity.setPortalCooldown();
                target.teleport(entity);
                if (entity instanceof ServerPlayer) {
                    //This is a hack. I need to find a better solution.
                    DelayedTask.run(10, () -> DraconicNetwork.sendDislocatorTeleported((ServerPlayer) entity));
                    DelayedTask.run(20, () -> DraconicNetwork.sendDislocatorTeleported((ServerPlayer) entity));
                    DelayedTask.run(60, () -> DraconicNetwork.sendDislocatorTeleported((ServerPlayer) entity));
                }
                entity.setPortalCooldown();
                DelayedTask.run(1, () -> BCoreNetwork.sendSound(entity.level(), entity.blockPosition(), DESounds.PORTAL.get(), SoundSource.PLAYERS, 0.1F, rand.nextFloat() * 0.1F + 0.9F, false));
            }
            teleportQ.clear();
        }
    }

    //#################################################################################
    // Portal Helper Call Backs
    //#################################################################################

    public void onScanBlock(BlockPos pos) {
//        BCoreNetwork.sendParticle(getLevel(), ParticleTypes.CLOUD, Vector3.fromBlockPosCenter(pos), Vector3.ZERO, true);
    }

    public void onScanComplete(@Nullable Set<BlockPos> result, @Nullable Axis resultAxis) {
        if (result == null || resultAxis == null) {
            ignitionStage.set(0);
        } else {
            ignitionStage.set(2);
            portalHelper.buildPortal(result, resultAxis);
            activeAxis.set(resultAxis);
        }
    }

    public void onBuildSuccess(List<BlockPos> builtList) {
        setActive(true);
        ignitionStage.set(0);
        Map<Integer, List<BlockPos>> levelMap = new HashMap<>();
        builtList.forEach(block -> levelMap.computeIfAbsent(block.getY(), integer -> new ArrayList<>()).add(block));
        LinkedList<Integer> levels = new LinkedList<>(levelMap.keySet());
        levels.sort(Comparator.naturalOrder());

        List<BlockPos> foundValid = new ArrayList<>();
        for (int level : levels) {
            List<BlockPos> blocks = levelMap.get(level);
            for (BlockPos pos : blocks) {
                if (this.level.isEmptyBlock(pos.above()) || this.level.getBlockState(pos.above()).is(DEContent.PORTAL.get())) {
                    foundValid.add(pos);
                }
            }
            if (!foundValid.isEmpty()) {
                break;
            }
        }

        if (foundValid.isEmpty()) {
            arrivalPos.set(null);
            return;
        }

        Vector3 min = new Vector3().set(60000000);
        Vector3 max = new Vector3().set(-60000000);
        for (BlockPos pos : foundValid) {
            if (pos.getX() < min.x) min.x = pos.getX();
            if (pos.getY() < min.y) min.y = pos.getY();
            if (pos.getZ() < min.z) min.z = pos.getZ();
            if (pos.getX() > max.x) max.x = pos.getX();
            if (pos.getY() > max.y) max.y = pos.getY();
            if (pos.getZ() > max.z) max.z = pos.getZ();
        }
        Vector3 mid = min.copy().add(max.subtract(min).divide(2));
        BlockPos closestPos = foundValid.get(0);
        double closest = Integer.MAX_VALUE;
        for (BlockPos pos : foundValid) {
            double dist = Utils.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, mid.x, mid.y, mid.z);
            if (dist < closest) {
                closest = dist;
                closestPos = pos;
            }
        }
        arrivalPos.set(closestPos);
//        setLinkPos(builtList.stream().max(Comparator.comparing(pos -> pos.distSqr(mid.pos()))).orElse(null));
        setLinkPos(getMidPos(builtList));
    }

    private BlockPos getMidPos(List<BlockPos> blocks) {
        Vector3 min = new Vector3(60000000, 60000000, 60000000);
        Vector3 max = new Vector3(-60000000, -60000000, -60000000);
        for (BlockPos pos : blocks) {
            if (pos.getX() + 0.5 < min.x) min.x = pos.getX() + 0.5;
            if (pos.getY() + 0.5 < min.y) min.y = pos.getY() + 0.5;
            if (pos.getZ() + 0.5 < min.z) min.z = pos.getZ() + 0.5;
            if (pos.getX() + 0.5 > max.x) max.x = pos.getX() + 0.5;
            if (pos.getY() + 0.5 > max.y) max.y = pos.getY() + 0.5;
            if (pos.getZ() + 0.5 > max.z) max.z = pos.getZ() + 0.5;
        }
        Vector3 mid = min.copy().add(max.subtract(min).divide(2));
        return blocks.stream().min(Comparator.comparingDouble(pos -> MathUtils.distanceSq(mid, Vector3.fromBlockPosCenter(pos)))).orElse(null);
    }

    public void onBuildFail() {
        setActive(false);
        ignitionStage.set(0);
    }

    //#################################################################################

    public boolean isActive() {
        return getBlockState().getValue(DislocatorReceptacle.ACTIVE);
    }

    public void setActive(boolean active) {
        if (level.getBlockState(getBlockPos()).is(DEContent.DISLOCATOR_RECEPTACLE.get())) {
            level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(DislocatorReceptacle.ACTIVE, active));
        }
    }

    private TargetPos getTargetPos() {
        ItemStack stack = itemHandler.getStackInSlot(0);
        if (!(stack.getItem() instanceof Dislocator)) return null;
        return ((Dislocator) stack.getItem()).getTargetPos(stack, level);
    }

    @Override
    public void generateHudText(Level world, BlockPos pos, Player player, List<Component> displayList) {
        displayList.add(Component.literal(ignitionStage.get() == 1 ? "Scanning..." : "Activating..."));
    }

    @Override
    public boolean shouldDisplayHudText(Level world, BlockPos pos, Player player) {
        return ignitionStage.get() > 0;
    }

    public void checkIn() {
        ItemStack stack = itemHandler.getStackInSlot(0);
        if (BoundDislocator.isValid(stack) && BoundDislocator.isP2P(stack)) {
            DislocatorSaveData.updateLinkTarget(level, stack, new TileTarget(this));
        }
    }

    private boolean isBound() {
        ItemStack stack = itemHandler.getStackInSlot(0);
        return BoundDislocator.isValid(stack) && BoundDislocator.isP2P(stack);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level instanceof ServerLevel) {
            checkIn();
        }
    }

    @Nullable
    @Override
    public Vec3 getArrivalPos(UUID linkID) {
        BlockPos ap = arrivalPos.get();
        return isActive() && ap != null ? new Vec3(ap.getX() + 0.5, ap.getY() + 0.25, ap.getZ()) : null;
    }

    @Override
    public void entityArriving(Entity entity) {
        entity.setPortalCooldown();
        if (entity instanceof ServerPlayer) {
            //This is a hack. I need to find a better solution.
            DelayedTask.run(10, () -> DraconicNetwork.sendDislocatorTeleported((ServerPlayer) entity));
            DelayedTask.run(20, () -> DraconicNetwork.sendDislocatorTeleported((ServerPlayer) entity));
            DelayedTask.run(60, () -> DraconicNetwork.sendDislocatorTeleported((ServerPlayer) entity));
        }
    }

    //#################################################################################
    // Crystal Network Code
    //#################################################################################

    private BlockPos remotePosCache = null;
    private ResourceKey<Level> remoteWorldCache = null;
    private int invalidLinkTime = 0;
    protected ENetFXHandler fxHandler;

    private void updateCrystalLogic() {
        fxHandler.update();

        boolean boundCrystals = isActive() && isBound() && linkedCrystal.get() != null;
        if (level.isClientSide && boundCrystals && remoteCrystalTier.isDirty(false)) {
            fxHandler.reloadConnections();
        }

        if (!level.isClientSide && boundCrystals) {
            if (DEEventHandler.serverTicks % 10 == 0) {
                BlockEntity remoteTile = getRemoteReceptacle();
                ICrystalLink remote = getRemoteCrystal();
                if (remoteTile != null && remote instanceof IENetEffectTile) {
                    int i = remote.getLinks().indexOf(remoteTile.getBlockPos());
                    List<Byte> rates = ((IENetEffectTile) remote).getFlowRates();
                    if (i >= 0 && i < rates.size()) {
                        linkedFlowRate.set(rates.get(i));
                    } else {
                        linkedFlowRate.set(0);
                    }
                } else {
                    linkedFlowRate.set(0);
                }
            }
            if (linkedFlowRate.get() != 0 && DEEventHandler.serverTicks % 100 == 0) {
                dataManager.forceSync(linkedFlowRate);
            }
        } else if (!level.isClientSide) {
            linkedFlowRate.set(0);
        }
    }

    public void setLinkPos(BlockPos spawnPos) {
        crystalLinkPos.set(getBlockPos().subtract(spawnPos));
    }

    protected BlockPos getLinkPos() {
        if (crystalLinkPos.get() != null) {
            return getBlockPos().subtract(Objects.requireNonNull(crystalLinkPos.get()));
        }
        return BlockPos.ZERO;
    }

    protected void setCrystalPos(BlockPos crystalPos) {
        linkedCrystal.set(getBlockPos().subtract(crystalPos));
    }

    protected BlockPos getCrystalPos() {
        if (linkedCrystal.get() != null) {
            return getBlockPos().subtract(Objects.requireNonNull(linkedCrystal.get()));
        }
        return BlockPos.ZERO;
    }

    private TileDislocatorReceptacle getRemoteReceptacle() {
        return getRemoteReceptacle(false);
    }

    private TileDislocatorReceptacle getRemoteReceptacle(boolean skipRemoteCheck) {
        if (!isActive() || !isBound()) return null;

        if (invalidLinkTime > 0) {
            invalidLinkTime--;
            return null;
        }

        if (remotePosCache == null) {
            ItemStack stack = itemHandler.getStackInSlot(0);
            DislocatorTarget target = DislocatorSaveData.getLinkTarget(level, stack);
            if (target instanceof TileTarget) {
                TileTarget tileTarget = (TileTarget) target;
                remotePosCache = tileTarget.getTilePos();
                remoteWorldCache = tileTarget.getWorldKey();
            } else {
                invalidLinkTime = 100;
                return null;
            }
        }

        MinecraftServer server = level.getServer();
        if (server != null) {
            Level remoteWorld = server.getLevel(remoteWorldCache);
            if (remoteWorld != null) {
                BlockEntity tile = remoteWorld.getBlockEntity(remotePosCache);
                if (tile instanceof TileDislocatorReceptacle) {
                    if (skipRemoteCheck) {
                        return (TileDislocatorReceptacle) tile;
                    }
                    if (((TileDislocatorReceptacle) tile).isActive() && ((TileDislocatorReceptacle) tile).getRemoteReceptacle(true) == this) {
                        return (TileDislocatorReceptacle) tile;
                    }
                }
            }

            remotePosCache = null;
            return null;
        }
        return null;
    }

    private ICrystalLink getRemoteCrystal() {
        TileDislocatorReceptacle tile = getRemoteReceptacle();
        if (tile != null) {
            MinecraftServer server = level.getServer();
            if (server != null && tile.linkedCrystal.get() != null) {
                Level remoteWorld = server.getLevel(remoteWorldCache);
                if (remoteWorld != null) {
                    BlockEntity crystal = remoteWorld.getBlockEntity(tile.getCrystalPos());
                    if (crystal instanceof IENetEffectTile) {
                        remoteCrystalTier.set(((IENetEffectTile) crystal).getTier());
                        return (ICrystalLink) crystal;
                    }
                }
                return null;
            }
        }
        return null;
    }

    //Interfaces

    @Nonnull
    @Override
    public List<BlockPos> getLinks() {
        if (linkedCrystal.get() != null) {
            return Collections.singletonList(getCrystalPos());
        }
        return Collections.emptyList();
    }

    @Override
    public boolean binderUsed(Player player, BlockPos linkTarget, Direction sideClicked) {
        return false;
    }

    @Override
    public boolean createLink(ICrystalLink otherCrystal) {
        setCrystalPos(((BlockEntity) otherCrystal).getBlockPos());
        return true;
    }

    @Override
    public void breakLink(BlockPos otherCrystal) {
        linkedCrystal.set(null);
    }

    @Override
    public int balanceMode() {
        ICrystalLink remote = getRemoteCrystal();
        return remote != null ? remote.balanceMode() : 1;
    }

    @Override
    public int maxLinks() {
        return 1;
    }

    @Override
    public int maxLinkRange() {
        return 32;
    }

    @Override
    public long getEnergyStored() {
        ICrystalLink remote = getRemoteCrystal();
        return remote != null ? remote.getEnergyStored() : 0;
    }

    @Override
    public long getMaxEnergyStored() {
        ICrystalLink remote = getRemoteCrystal();
        return remote != null ? remote.getMaxEnergyStored() : 0;
    }

    @Override
    public void modifyEnergyStored(long energy) {
        ICrystalLink remote = getRemoteCrystal();
        if (remote != null) {
            remote.modifyEnergyStored(energy);
        }
    }

    @Override
    public Vec3D getBeamLinkPos(BlockPos linkTo) {
        double dist = FacingUtils.distanceInDirection(getBlockPos(), linkTo, FacingUtils.getAxisFaces(activeAxis.get())[0]);
        Vec3D vec = Vec3D.getCenter(getLinkPos());

        Direction facing;
        if (dist > 0) {
            facing = FacingUtils.getAxisFaces(activeAxis.get())[0];
        } else {
            facing = FacingUtils.getAxisFaces(activeAxis.get())[1];
        }

        vec.add(facing.getStepX() * 0.35, facing.getStepY() * 0.35, facing.getStepZ() * 0.35);

        return vec;
    }

    //INetEffectTile

    @Override
    public boolean renderBeamTermination() {
        return true;
    }

    @Override
    public ENetFXHandler createServerFXHandler() {
        return new ENetFXHandlerServer(this);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ENetFXHandler createClientFXHandler() {
        return new ENetFXHandlerClient(this);
    }

    @Override
    public boolean hasStaticFX() {
        return false;
    }

    @Override
    public CrystalFXBase<?> createStaticFX() {
        return null;
    }

    @Override
    public LinkedList<Byte> getFlowRates() {
        return new LinkedList<>(Collections.singletonList((byte) linkedFlowRate.get()));
    }

    @Override
    public int getTier() {
        return remoteCrystalTier.get();
    }

    boolean hashCached = false;
    int hashID = 0;

    @Override
    public int getIDHash() {
        if (!hashCached) {
            hashID = getBlockPos().hashCode();
            hashCached = true;
        }
        return hashID;
    }

}
