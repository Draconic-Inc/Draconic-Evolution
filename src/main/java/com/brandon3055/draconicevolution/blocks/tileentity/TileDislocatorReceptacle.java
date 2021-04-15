package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.inventory.TileItemStackHandler;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.brandonscore.lib.Pair;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedByte;
import com.brandon3055.brandonscore.lib.datamanager.ManagedEnum;
import com.brandon3055.brandonscore.lib.datamanager.ManagedVec3I;
import com.brandon3055.brandonscore.network.BCoreNetwork;
import com.brandon3055.brandonscore.utils.FacingUtils;
import com.brandon3055.brandonscore.utils.InventoryUtils;
import com.brandon3055.brandonscore.utils.TargetPos;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.energy.ICrystalLink;
import com.brandon3055.draconicevolution.api.energy.IENetEffectTile;
import com.brandon3055.draconicevolution.api.ITeleportEndPoint;
import com.brandon3055.draconicevolution.blocks.DislocatorReceptacle;
import com.brandon3055.draconicevolution.blocks.Portal;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandler;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandlerClient;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandlerServer;
import com.brandon3055.draconicevolution.client.render.effect.CrystalFXBase;
import com.brandon3055.draconicevolution.handlers.DEEventHandler;
import com.brandon3055.draconicevolution.handlers.DislocatorLinkHandler;
import com.brandon3055.draconicevolution.items.tools.Dislocator;
import com.brandon3055.draconicevolution.handlers.DESounds;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;

import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.*;
import static com.brandon3055.draconicevolution.init.DEContent.dislocator_p2p;

/**
 * Created by brandon3055 on 16/07/2016.
 */
public class TileDislocatorReceptacle extends TileBCore implements ITickableTileEntity, ITeleportEndPoint, ICrystalLink, IENetEffectTile {
//    used to update existing portals to the new offset based portal positions
//    public final ManagedBool newOffsets = register(new ManagedBool("new_offsets", SAVE_NBT)); //This was to "reboot" existing portals after an update that broke them (Can be removed at some point)
    public final ManagedBool active = register(new ManagedBool("active", SAVE_NBT_SYNC_TILE, TRIGGER_UPDATE));
    public final ManagedBool camo = register(new ManagedBool("camo", SAVE_NBT_SYNC_TILE, TRIGGER_UPDATE));
    public final ManagedBool ltRedstone = register(new ManagedBool("lt_redstone", SAVE_NBT_SYNC_TILE, TRIGGER_UPDATE));
    public final ManagedVec3I spawnPos = register(new ManagedVec3I("spawn_pos", new Vec3I(0, -999, 0), SAVE_NBT_SYNC_TILE));
    public final ManagedEnum<Axis> activeAxis = register(new ManagedEnum<>("active_axis", Axis.X, SAVE_NBT_SYNC_TILE));
    public final ManagedBool isBound = register(new ManagedBool("is_bound", SAVE_NBT_SYNC_TILE));
    public final ManagedVec3I linkedCrystal = register(new ManagedVec3I("linked_crystal", new Vec3I(0, -999, 0), SAVE_NBT_SYNC_TILE));
    public final ManagedByte remoteCrystalTier = register(new ManagedByte("remote_crystal_tier", SAVE_NBT_SYNC_TILE));
    public final ManagedByte linkedFlowRate = register(new ManagedByte("linked_flow_rate", SAVE_NBT));
    public final ManagedVec3I crystalLinkPos = register(new ManagedVec3I("crystal_link+pos", new Vec3I(0, -999, 0), SAVE_NBT_SYNC_TILE));

    public int hiddenTime = 0;
    public boolean igniting = false;
    public boolean frameMoving = false;
    private List<Entity> teleportQ = new ArrayList<Entity>();
    private Map<Integer, Integer> coolDownMap = new HashMap<>();
    private Map<Integer, Integer> arrivalsMap = new HashMap<>();

    public TileItemStackHandler itemHandler = new TileItemStackHandler(1);

    public TileDislocatorReceptacle() {
        super(DEContent.tile_dislocator_receptacle);
        capManager.setManaged("inventory", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, itemHandler).saveBoth();
        itemHandler.setContentsChangeListener(this::onInventoryChange);
        itemHandler.setSlotValidator(0, (stack) -> stack.getItem() instanceof Dislocator);

        fxHandler = DraconicEvolution.proxy.createENetFXHandler(this);
    }

    @Override
    public void tick() {
        super.tick();

        updateHidden(false);
        updateCrystalLogic();

        if (level.isClientSide && !active.get()) {
            hiddenTime = 5;
        }

        //Offset fix implemented in 1.12 to avoid a breaking change
//        if (!world.isRemote && !newOffsets.get() && active.get()) {
//            deactivate();
//            attemptIgnition();
//            newOffsets.set(true);
//        }

        if (frameMoving) {
            if (active.get()) {
                finishMove(worldPosition, new HashSet<>());
            }
            frameMoving = false;
            checkIn();
        }

        for (Entity entity : teleportQ) {
            ItemStack stack = itemHandler.getStackInSlot(0);

            if (!(stack.getItem() instanceof Dislocator)) {
                deactivate();
                return;
            }

            TargetPos location = ((Dislocator) stack.getItem()).getTargetPos(stack, level);
            if (dislocator_p2p.isValid(stack) && location != null) {
                location.setYaw(entity.yRot);
                location.setPitch(entity.xRot);
            }

            if (location == null) {
                if (!dislocator_p2p.isValid(stack)) {
                    deactivate();
                }
                else {
                    if (entity instanceof PlayerEntity) {
                        if (dislocator_p2p.isPlayer(stack)) {
                            ChatHelper.sendMessage((PlayerEntity) entity, new TranslationTextComponent("info.de.bound_dislocator.cant_find_player").withStyle(TextFormatting.RED));
                        }
                        else {
                            ChatHelper.sendMessage((PlayerEntity) entity, new TranslationTextComponent("info.de.bound_dislocator.cant_find_target").withStyle(TextFormatting.RED));
                        }
                    }
                }

                teleportQ.clear();
                return;
            }

            dislocator_p2p.notifyArriving(stack, level, entity);
            BCoreNetwork.sendSound(entity.level, entity.blockPosition(), DESounds.portal, SoundCategory.PLAYERS, 0.1F, entity.level.random.nextFloat() * 0.1F + 0.9F, false);
            location.teleport(entity);
            BCoreNetwork.sendSound(entity.level, entity.blockPosition(), DESounds.portal, SoundCategory.PLAYERS, 0.1F, entity.level.random.nextFloat() * 0.1F + 0.9F, false);
        }

        try {
            List<Integer> toRemove = new ArrayList<>();

            for (Integer key : coolDownMap.keySet()) {
                if (coolDownMap.get(key) > 0) {
                    coolDownMap.put(key, coolDownMap.get(key) - 1);
                }
                else {
                    toRemove.add(key);
                }
            }

            for (Integer i : toRemove) {
                coolDownMap.remove(i);
            }
            toRemove.clear();

            toRemove = new ArrayList<>();

            for (Integer key : arrivalsMap.keySet()) {
                if (arrivalsMap.get(key) > 0) {
                    arrivalsMap.put(key, arrivalsMap.get(key) - 1);
                }
                else {
                    toRemove.add(key);
                }
            }

            for (Integer i : toRemove) {
                arrivalsMap.remove(i);
            }
            toRemove.clear();

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        teleportQ.clear();
    }

    private void updateCrystalLogic() {
        fxHandler.update();

        boolean boundCrystals = active.get() && isBound.get() && linkedCrystal.get().y != -999;
        if (level.isClientSide && boundCrystals && remoteCrystalTier.isDirty(true)) {
            fxHandler.reloadConnections();
        }

        if (!level.isClientSide && boundCrystals) {
            if (DEEventHandler.serverTicks % 10 == 0) {
                TileEntity remoteTile = getRemoteReceptacle();
                ICrystalLink remote = getRemoteCrystal();
                if (remoteTile != null && remote instanceof IENetEffectTile) {
                    int i = remote.getLinks().indexOf(remoteTile.getBlockPos());
                    List<Byte> rates = ((IENetEffectTile) remote).getFlowRates();
                    if (i >= 0 && i < rates.size()) {
                        linkedFlowRate.set(rates.get(i));
                    }
                    else {
                        linkedFlowRate.zero();
                    }
                }
                else {
                    linkedFlowRate.zero();
                }
            }
            if (linkedFlowRate.get() != 0 && DEEventHandler.serverTicks % 100 == 0) {
                dataManager.forceSync(linkedFlowRate);
            }
        }
        else if (!level.isClientSide) {
            linkedFlowRate.zero();
        }
    }

    public void setHidden() {
        boolean firstSet = hiddenTime == 0;
        hiddenTime = 30;
        if (firstSet) {
            updateHidden(true);
        }
    }

    private void updateHidden(boolean setHidden) {
        if (level.isClientSide && hiddenTime > 0) {
            hiddenTime--;
            if (hiddenTime == 0 || setHidden) {
                long time = System.nanoTime();
                for (BlockPos checkPos : BlockPos.betweenClosed(worldPosition.offset(-1, -1, -1), worldPosition.offset(1, 1, 1))) {
                    TileEntity tile = level.getBlockEntity(checkPos);
                    if (tile instanceof TilePortal) {
                        BlockPos spawn = spawnPos.get().y == -999 ? worldPosition : getSpawnPos();
                        TilePortal tPortal = (TilePortal) tile;
                        if (tPortal.getMasterPos().equals(worldPosition) && tPortal.updateTime != time) {
                            if (!setHidden) {
                                level.playLocalSound(spawn.getX() + 0.5, spawn.getY() + 0.5, spawn.getZ() + 0.5, SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, SoundCategory.BLOCKS, 2, 0.5F + (level.random.nextFloat() * 0.1F), false);
                            }
                            ((TilePortal) tile).propRenderUpdate(time, !setHidden);
                        }
                    }
                }
            }
        }
    }

    //region Activation & Inventory

    public boolean onBlockActivated(PlayerEntity player) {
        if (level.isClientSide) {
            return !ltRedstone.get();
        }

        InventoryUtils.handleHeldStackTransfer(0, itemHandler, player);
        return true;
    }

    public void onInventoryChange(int index) {
        ItemStack prev = itemHandler.getListenerPrevStack();

        if (dislocator_p2p.isValid(prev) && !dislocator_p2p.isPlayer(prev)) {
            DislocatorLinkHandler.removeLink(level, prev);
        }

        isBound.set(false);
        if (itemHandler.getStackInSlot(0).isEmpty() && active.get()) {
            deactivate();
        }
        else if (!itemHandler.getStackInSlot(0).isEmpty()) {
            attemptIgnition();
            checkIn();
        }
    }

//    @Override
//    public ItemStack decrStackSize(int index, int count) {
//        ItemStack prev = getStackInSlot(0);
//        ItemStack ret = super.decrStackSize(index, count);
//
//        if (dislocator_p2p.isValid(prev) && !dislocator_p2p.isPlayer(prev)) {
//            DislocatorLinkHandler.removeLink(world, prev);
//        }
//
//        isBound.set(false);
//        if (getStackInSlot(0).isEmpty() && active.get()) {
//            deactivate();
//        }
//        else if (!getStackInSlot(0).isEmpty()) {
//            attemptIgnition();
//            checkIn();
//        }
//
//        return ret;
//    }

    private void checkIn() {
        ItemStack stack = itemHandler.getStackInSlot(0);
        if (dislocator_p2p.isValid(stack) && !dislocator_p2p.isPlayer(stack)) {
            DislocatorLinkHandler.updateLink(level, stack, worldPosition, level.dimension());
            isBound.set(true);
        }
        else {
            isBound.set(false);
        }
    }

//    @Override
//    public boolean isItemValidForSlot(int index, ItemStack stack) {
//        return stack.getItem() instanceof Dislocator;
//    }

    //endregion

    //region Teleport Handling

    public void handleEntityTeleport(Entity entity) {
        if (level.isClientSide || teleportQ.contains(entity) || coolDownMap.containsKey(entity.getId())) {
            return;
        }

        //TODO in 1.13 use entity.portalCooldown
        if (arrivalsMap.containsKey(entity.getId())) {
            if (entity instanceof PlayerEntity && arrivalsMap.get(entity.getId()) < 10) {
                //TODO Packet Stuff
//                new PacketCustom("DEPCChannel", 1).writePos(pos).sendToPlayer((PlayerEntity) entity);
            }
            arrivalsMap.put(entity.getId(), 10);
            return;
        }

        coolDownMap.put(entity.getId(), 10);
        teleportQ.add(entity);
    }

    //endregion

    //region MultiBlock

    public void deactivate() {
        if (!level.isClientSide) {
            active.set(false);
        }

        BlockState state = level.getBlockState(worldPosition);
        if (state.getBlock() == DEContent.dislocator_receptacle) {
            level.setBlockAndUpdate(worldPosition, state.setValue(DislocatorReceptacle.ACTIVE, false));
        }

        for (BlockPos checkPos : BlockPos.betweenClosed(worldPosition.offset(-1, -1, -1), worldPosition.offset(1, 1, 1))) {
            TileEntity tile = level.getBlockEntity(checkPos);
            if (tile instanceof TilePortal && ((TilePortal) tile).getMasterPos().equals(worldPosition)) {
                level.removeBlock(tile.getBlockPos(), false);
            }
        }
        updateBlock();
    }

    public boolean attemptIgnition() {
//        newOffsets.set(true);
        ItemStack stack = itemHandler.getStackInSlot(0);

        if (!(stack.getItem() instanceof Dislocator) || ((Dislocator) stack.getItem()).getTargetPos(stack, level) == null) {
            if (!dislocator_p2p.isValid(stack)) {
                return false;
            }
        }

        Pair<Axis, List<BlockPos>> portalConfiguration = scanConfigurations();
        if (portalConfiguration != null) {
            igniting = true;

            for (BlockPos portalBlock : portalConfiguration.value()) {
                level.setBlockAndUpdate(portalBlock, DEContent.portal.defaultBlockState().setValue(Portal.AXIS, portalConfiguration.key()));
                TileEntity tile = level.getBlockEntity(portalBlock);
                if (tile instanceof TilePortal) {
                    ((TilePortal) tile).setMasterPos(worldPosition);
                }
            }

            active.set(true);
            activeAxis.set(portalConfiguration.key());

            BlockState state = level.getBlockState(worldPosition);
            if (state.getBlock() == DEContent.dislocator_receptacle) {
                level.setBlockAndUpdate(worldPosition, state.setValue(DislocatorReceptacle.ACTIVE, true));
            }

            updateBlock();
            igniting = false;

            if (dislocator_p2p.isValid(stack) && !dislocator_p2p.isPlayer(stack)) {
                updateSpawnBlock(portalConfiguration.value());
                updateLinkBlock(portalConfiguration.value());
            }

            return true;
        }

        return false;
    }

    private void updateSpawnBlock(List<BlockPos> rawBlocks) {
        Map<Integer, List<BlockPos>> levelMap = new HashMap<>();
        rawBlocks.forEach(block -> levelMap.computeIfAbsent(block.getY(), integer -> new ArrayList<>()).add(block));
        LinkedList<Integer> levels = new LinkedList<>(levelMap.keySet());
        levels.sort(Comparator.naturalOrder());

        List<BlockPos> foundValid = new ArrayList<>();
        for (int level : levels) {
            List<BlockPos> blocks = levelMap.get(level);
            for (BlockPos pos : blocks) {
                if (this.level.isEmptyBlock(pos.above()) || this.level.getBlockState(pos.above()).getBlock() == DEContent.portal) {
                    foundValid.add(pos);
                }
            }
            if (!foundValid.isEmpty()) {
                break;
            }
        }

        if (foundValid.isEmpty()) {
            spawnPos.set(new Vec3I(0, -999, 0));
            return;
        }

        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;

        for (BlockPos pos : foundValid) {
            if (pos.getX() > maxX) maxX = pos.getX();
            if (pos.getY() > maxY) maxY = pos.getY();
            if (pos.getZ() > maxZ) maxZ = pos.getZ();
            if (pos.getX() < minX) minX = pos.getX();
            if (pos.getY() < minY) minY = pos.getY();
            if (pos.getZ() < minZ) minZ = pos.getZ();
        }

        Vec3D center = new Vec3D((minX + ((maxX - minX) / 2)) + 0.5, (minY + ((maxY - minY) / 2)) + 0.5, (minZ + ((maxZ - minZ) / 2)) + 0.5);
//        BCEffectHandler.spawnFX(DEParticles.LINE_INDICATOR, world, center, new Vec3D(), 255, 0, 0, 10000);
        BlockPos closestPos = foundValid.get(0);
        double closest = 10000000;

        for (BlockPos pos : foundValid) {
            double dist = Utils.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, center.x, center.y, center.z);
            if (dist < closest) {
                closest = dist;
                closestPos = pos;
            }
        }

        setSpawnPos(closestPos);
    }

    private void updateLinkBlock(List<BlockPos> rawBlocks) {
        if (rawBlocks.isEmpty()) {
            crystalLinkPos.set(new Vec3I(0, -999, 0));
            return;
        }

        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;

        for (BlockPos pos : rawBlocks) {
            if (pos.getX() > maxX) maxX = pos.getX();
            if (pos.getY() > maxY) maxY = pos.getY();
            if (pos.getZ() > maxZ) maxZ = pos.getZ();
            if (pos.getX() < minX) minX = pos.getX();
            if (pos.getY() < minY) minY = pos.getY();
            if (pos.getZ() < minZ) minZ = pos.getZ();
        }

        Vec3D center = new Vec3D((minX + ((maxX - minX) / 2)) + 0.5, (minY + ((maxY - minY) / 2)) + 0.5, (minZ + ((maxZ - minZ) / 2)) + 0.5);
//        for (int i = 0; i < 1000; i++)
//        BCEffectHandler.spawnFX(DEParticles.LINE_INDICATOR, world, center, new Vec3D(), 0, 255, 255, 10000);

        BlockPos closestPos = rawBlocks.get(0);
        double closest = 10000000;

        for (BlockPos pos : rawBlocks) {
            double dist = Utils.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, center.x, center.y, center.z);
            if (dist < closest) {
                closest = dist;
                closestPos = pos;
            }
        }

        setLinkPos(closestPos);
    }

    private Pair<Axis, List<BlockPos>> scanConfigurations() {
        List<BlockPos> scanned = new ArrayList<BlockPos>();
        for (BlockPos offset : FacingUtils.AROUND_X) {
            List<BlockPos> portalBlocks = scanFromOrigin(worldPosition.offset(offset), Axis.X, scanned);
            if (portalBlocks != null) {
                return new Pair<Axis, List<BlockPos>>(Axis.X, portalBlocks);
            }
        }

        scanned = new ArrayList<BlockPos>();
        for (BlockPos offset : FacingUtils.AROUND_Y) {
            List<BlockPos> portalBlocks = scanFromOrigin(worldPosition.offset(offset), Axis.Y, scanned);
            if (portalBlocks != null) {
                return new Pair<Axis, List<BlockPos>>(Axis.Y, portalBlocks);
            }
        }

        scanned = new ArrayList<BlockPos>();
        for (BlockPos offset : FacingUtils.AROUND_Z) {
            List<BlockPos> portalBlocks = scanFromOrigin(worldPosition.offset(offset), Axis.Z, scanned);
            if (portalBlocks != null) {
                return new Pair<Axis, List<BlockPos>>(Axis.Z, portalBlocks);
            }
        }

        return null;
    }

    private List<BlockPos> scanFromOrigin(BlockPos scanOrigin, Axis scanAxis, List<BlockPos> alreadyScanned) {
        if (!level.isEmptyBlock(scanOrigin) || alreadyScanned.contains(scanOrigin)) {
            return null;
        }

        List<BlockPos> scannedBlocks = new ArrayList<BlockPos>();
        if (scanPortal(scanOrigin, scanOrigin, scanAxis, scannedBlocks, alreadyScanned)) {
            return scannedBlocks;
        }

        return null;
    }

    private boolean scanPortal(BlockPos scanPos, BlockPos origin, Axis scanAxis, List<BlockPos> scanList, List<BlockPos> blackList) {
        if (Utils.getDistanceAtoB(new Vec3D(scanPos), new Vec3D(origin)) > 100) {
            return false;
        }

        scanList.add(scanPos);

        for (Direction facing : FacingUtils.getFacingsAroundAxis(scanAxis)) {
            BlockPos nextPos = scanPos.relative(facing);
            if (scanList.contains(nextPos) || isFrame(nextPos)) {
                continue;
            }
            else if (level.isEmptyBlock(nextPos)) {
                if (!scanPortal(nextPos, origin, scanAxis, scanList, blackList)) {
                    return false;
                }
            }
            else {
                return false;
            }
        }

        return true;
    }

    private boolean isFrame(BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.getBlock() == DEContent.infused_obsidian || state.getBlock() == DEContent.dislocator_receptacle;
    }

    //endregion

    //region Bound portal code

    @Override
    public BlockPos getArrivalPos(String linkID) {
        if (!active.get() || !dislocator_p2p.isValid(itemHandler.getStackInSlot(0)) || !dislocator_p2p.getLinkID(itemHandler.getStackInSlot(0)).equals(linkID)) {
            return null;
        }

        return spawnPos.get().y == -999 ? null : getSpawnPos();
    }

    @Override
    public void entityArriving(Entity entity) {
        Entity rootEntity = entity.getRootVehicle();
        PassengerHelper passengerHelper = new PassengerHelper(rootEntity);
        passengerHelper.forEach(e -> arrivalsMap.put(e.getId(), 10));
    }

    public void setSpawnPos(BlockPos spawnPos) {
        this.spawnPos.get().set(worldPosition.subtract(spawnPos));
    }

    protected BlockPos getSpawnPos() {
        return worldPosition.subtract(spawnPos.get().getPos());
    }

    public void setLinkPos(BlockPos spawnPos) {
        crystalLinkPos.get().set(worldPosition.subtract(spawnPos));
    }

    protected BlockPos getLinkPos() {
        return worldPosition.subtract(crystalLinkPos.get().getPos());
    }

//    @Override
//    @Optional.Method(modid = "appliedenergistics2")
//    public boolean prepareToMove() {
//        return true;
//    }

//    @Override
//    @Optional.Method(modid = "appliedenergistics2")
//    public void doneMoving() {
//        checkIn();
//    }

    private static class PassengerHelper {
        public Entity entity;
        public LinkedList<PassengerHelper> passengers = new LinkedList<>();

        public PassengerHelper(Entity entity) {
            this.entity = entity;
            for (Entity passenger : entity.getPassengers()) {
                passengers.add(new PassengerHelper(passenger));
            }
        }

        public void forEach(Consumer<Entity> action) {
            action.accept(entity);
            passengers.forEach(helper -> helper.forEach(action));
        }
    }

    //endregion

    //region Frame Movement

//    @Override
//    public Iterable<BlockPos> getBlocksForFrameMove() {
//        if (active.get()) {
//            for (BlockPos offset : FacingUtils.getAroundAxis(activeAxis.get())) {
//                BlockPos next = pos.add(offset);
//                if (world.getBlockState(next).getBlock() == DEFeatures.portal) {
//                    HashSet<BlockPos> blocks = new HashSet<>();
//                    findActiveBlocksOnAxis(activeAxis.get(), next, blocks);
//                    return blocks;
//                }
//            }
//        }
//        else {
//            PairKV<Axis, List<BlockPos>> config = scanConfigurations();
//            if (config != null) {
//                HashSet<BlockPos> blocks = new HashSet<>();
//                for (BlockPos ppos : config.getValue()) {
//                    for (BlockPos offset : FacingUtils.getAroundAxis(config.getKey())) {
//                        BlockPos next = ppos.add(offset);
//                        if (blocks.contains(next)) continue;
//                        if (world.getBlockState(next).getBlock() == DEFeatures.infusedObsidian) {
//                            blocks.add(next);
//                        }
//                    }
//                }
//                return blocks;
//            }
//        }
//        return Collections.emptyList();
//    }

//    public void findActiveBlocksOnAxis(Axis axis, BlockPos pos, HashSet<BlockPos> blocks) {
//        blocks.add(pos);
//        for (BlockPos offset : FacingUtils.getAroundAxis(axis)) {
//            BlockPos next = pos.add(offset);
//            if (blocks.contains(next)) continue;
//
//            BlockState state = world.getBlockState(next);
//            if (state.getBlock() == DEFeatures.portal) {
//                findActiveBlocksOnAxis(axis, next, blocks);
//            }
//            else if (state.getBlock() == DEFeatures.infusedObsidian) {
//                blocks.add(next);
//            }
//        }
//    }

    public void finishMove(BlockPos pos, HashSet<BlockPos> blocks) {
        for (Direction facing : FacingUtils.getFacingsAroundAxis(activeAxis.get())) {
            BlockPos np = pos.relative(facing);
            if (blocks.contains(np)) continue;
            BlockState state = level.getBlockState(np);

            if (state.getBlock() == DEContent.portal) {
                TileEntity tile = level.getBlockEntity(np);
                if (tile instanceof TilePortal) {
                    ((TilePortal) tile).frameMoving = false;
                }
                blocks.add(np);
                finishMove(np, blocks);
            }
        }
    }

    //endregion

    //region F!@#$%^ Lasers through portals code for Morph

    protected void setCrystalPos(BlockPos crystalPos) {
        linkedCrystal.get().set(worldPosition.subtract(crystalPos));
    }

    protected BlockPos getCrystalPos() {
        return worldPosition.subtract(linkedCrystal.get().getPos());
    }

    private BlockPos remotePosCache = null;
    private RegistryKey<World> remoteDimCache = World.OVERWORLD;
    private int invalidLinkTime = 0;
    protected ENetFXHandler fxHandler;

    private TileDislocatorReceptacle getRemoteReceptacle() {
        return getRemoteReceptacle(false);
    }

    private TileDislocatorReceptacle getRemoteReceptacle(boolean skipRemoteCheck) {
        if (!isBound.get() || !active.get()) return null;

        if (invalidLinkTime > 0) {
            invalidLinkTime--;
            return null;
        }

        if (remotePosCache == null) {
            ItemStack stack = itemHandler.getStackInSlot(0);
            if (dislocator_p2p.isValid(stack) && !dislocator_p2p.isPlayer(stack)) {
                TileEntity tile = DislocatorLinkHandler.getTargetTile(level, stack);
                if (tile instanceof TileDislocatorReceptacle) {
                    remotePosCache = tile.getBlockPos();
                    remoteDimCache = tile.getLevel().dimension();
                }
                else {
                    invalidLinkTime = 100;
                    return null;
                }
            }
            else {
                return null;
            }
        }

        MinecraftServer server = level.getServer();
        if (server != null) {
            TileEntity tile = server.getLevel(remoteDimCache).getBlockEntity(remotePosCache);
            if (tile instanceof TileDislocatorReceptacle) {
                if (skipRemoteCheck) {
                    return (TileDislocatorReceptacle) tile;
                }
                if (((TileDislocatorReceptacle) tile).active.get() && ((TileDislocatorReceptacle) tile).getRemoteReceptacle(true) == this) {
                    return (TileDislocatorReceptacle) tile;
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
            if (server != null && tile.linkedCrystal.get().y != -999) {
                TileEntity crystal = server.getLevel(remoteDimCache).getBlockEntity(tile.getCrystalPos());
                if (crystal instanceof IENetEffectTile) {
                    remoteCrystalTier.set((byte) ((IENetEffectTile) crystal).getTier());
                    return (ICrystalLink) crystal;
                }
                return null;
            }
        }
        return null;
    }

    @Nonnull
    @Override
    public List<BlockPos> getLinks() {
        if (linkedCrystal.get().y != -999) {
            return Collections.singletonList(getCrystalPos());
        }
        return Collections.emptyList();
    }

    @Override
    public boolean binderUsed(PlayerEntity player, BlockPos linkTarget, Direction sideClicked) {
        return false;
    }

    @Override
    public boolean createLink(ICrystalLink otherCrystal) {
        setCrystalPos(((TileEntity) otherCrystal).getBlockPos());
        return true;
    }

    @Override
    public void breakLink(BlockPos otherCrystal) {
        linkedCrystal.set(new Vec3I(0, -999, 0));
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
        double dist = FacingUtils.distanceInDirection(worldPosition, linkTo, FacingUtils.getAxisFaces(activeAxis.get())[0]);
        Vec3D vec = Vec3D.getCenter(getLinkPos());

        Direction facing;
        if (dist > 0) {
            facing = FacingUtils.getAxisFaces(activeAxis.get())[0];
        }
        else {
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
    public CrystalFXBase createStaticFX() {
        return null;
    }

    @Override
    public LinkedList<Byte> getFlowRates() {
        return new LinkedList<>(Collections.singletonList(linkedFlowRate.get()));
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
            hashID = worldPosition.hashCode();
            hashCached = true;
        }
        return hashID;
    }

    //endregion
}
