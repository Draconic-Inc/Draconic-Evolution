package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.inventory.TileItemStackHandler;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.brandonscore.lib.PairKV;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedByte;
import com.brandon3055.brandonscore.lib.datamanager.ManagedEnum;
import com.brandon3055.brandonscore.lib.datamanager.ManagedVec3I;
import com.brandon3055.brandonscore.utils.FacingUtils;
import com.brandon3055.brandonscore.utils.InventoryUtils;
import com.brandon3055.brandonscore.utils.Teleporter;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEContent;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.ICrystalLink;
import com.brandon3055.draconicevolution.api.IENetEffectTile;
import com.brandon3055.draconicevolution.api.ITeleportEndPoint;
import com.brandon3055.draconicevolution.blocks.DislocatorReceptacle;
import com.brandon3055.draconicevolution.blocks.Portal;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandler;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandlerClient;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandlerServer;
import com.brandon3055.draconicevolution.client.render.effect.CrystalGLFXBase;
import com.brandon3055.draconicevolution.handlers.DEEventHandler;
import com.brandon3055.draconicevolution.handlers.DislocatorLinkHandler;
import com.brandon3055.draconicevolution.items.tools.Dislocator;
import com.brandon3055.draconicevolution.lib.DESoundHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;

import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.*;
import static com.brandon3055.draconicevolution.DEContent.dislocator_p2p;

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

        if (world.isRemote && !active.get()) {
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
                finishMove(pos, new HashSet<>());
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

            Teleporter.TeleportLocation location = ((Dislocator) stack.getItem()).getLocation(stack, world);
            if (dislocator_p2p.isValid(stack) && location != null) {
                location.setYaw(entity.rotationYaw);
                location.setPitch(entity.rotationPitch);
            }

            if (location == null) {
                if (!dislocator_p2p.isValid(stack)) {
                    deactivate();
                }
                else {
                    if (entity instanceof PlayerEntity) {
                        if (dislocator_p2p.isPlayer(stack)) {
                            ChatHelper.translate((PlayerEntity) entity, "info.de.bound_dislocator.cant_find_player", TextFormatting.RED);
                        }
                        else {
                            ChatHelper.translate((PlayerEntity) entity, "info.de.bound_dislocator.cant_find_target", TextFormatting.RED);
                        }
                    }
                }

                teleportQ.clear();
                return;
            }

            dislocator_p2p.notifyArriving(stack, world, entity);
            DESoundHandler.playSoundFromServer(entity.world, entity.posX, entity.posY, entity.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, entity.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
            location.teleport(entity);
            DESoundHandler.playSoundFromServer(entity.world, entity.posX, entity.posY, entity.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, entity.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
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
        if (world.isRemote && boundCrystals && remoteCrystalTier.isDirty(true)) {
            fxHandler.reloadConnections();
        }

        if (!world.isRemote && boundCrystals) {
            if (DEEventHandler.serverTicks % 10 == 0) {
                TileEntity remoteTile = getRemoteReceptacle();
                ICrystalLink remote = getRemoteCrystal();
                if (remoteTile != null && remote instanceof IENetEffectTile) {
                    int i = remote.getLinks().indexOf(remoteTile.getPos());
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
        else if (!world.isRemote) {
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
        if (world.isRemote && hiddenTime > 0) {
            hiddenTime--;
            if (hiddenTime == 0 || setHidden) {
                long time = System.nanoTime();
                for (BlockPos checkPos : BlockPos.getAllInBoxMutable(pos.add(-1, -1, -1), pos.add(1, 1, 1))) {
                    TileEntity tile = world.getTileEntity(checkPos);
                    if (tile instanceof TilePortal) {
                        BlockPos spawn = spawnPos.get().y == -999 ? pos : getSpawnPos();
                        TilePortal tPortal = (TilePortal) tile;
                        if (tPortal.getMasterPos().equals(pos) && tPortal.updateTime != time) {
                            if (!setHidden) {
                                world.playSound(spawn.getX() + 0.5, spawn.getY() + 0.5, spawn.getZ() + 0.5, SoundEvents.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, SoundCategory.BLOCKS, 2, 0.5F + (world.rand.nextFloat() * 0.1F), false);
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
        if (world.isRemote) {
            return !ltRedstone.get();
        }

        InventoryUtils.handleHeldStackTransfer(0, itemHandler, player);
        return true;
    }

    public void onInventoryChange(int index) {
        ItemStack prev = itemHandler.getListenerPrevStack();

        if (dislocator_p2p.isValid(prev) && !dislocator_p2p.isPlayer(prev)) {
            DislocatorLinkHandler.removeLink(world, prev);
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
            DislocatorLinkHandler.updateLink(world, stack, pos, world.getDimension().getType());
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
        if (world.isRemote || teleportQ.contains(entity) || coolDownMap.containsKey(entity.getEntityId())) {
            return;
        }

        //TODO in 1.13 use entity.portalCooldown
        if (arrivalsMap.containsKey(entity.getEntityId())) {
            if (entity instanceof PlayerEntity && arrivalsMap.get(entity.getEntityId()) < 10) {
                //TODO Packet Stuff
//                new PacketCustom("DEPCChannel", 1).writePos(pos).sendToPlayer((PlayerEntity) entity);
            }
            arrivalsMap.put(entity.getEntityId(), 10);
            return;
        }

        coolDownMap.put(entity.getEntityId(), 10);
        teleportQ.add(entity);
    }

    //endregion

    //region MultiBlock

    public void deactivate() {
        if (!world.isRemote) {
            active.set(false);
        }

        BlockState state = world.getBlockState(pos);
        if (state.getBlock() == DEContent.dislocator_receptacle) {
            world.setBlockState(pos, state.with(DislocatorReceptacle.ACTIVE, false));
        }

        for (BlockPos checkPos : BlockPos.getAllInBoxMutable(pos.add(-1, -1, -1), pos.add(1, 1, 1))) {
            TileEntity tile = world.getTileEntity(checkPos);
            if (tile instanceof TilePortal && ((TilePortal) tile).getMasterPos().equals(pos)) {
                world.removeBlock(tile.getPos(), false);
            }
        }
        updateBlock();
    }

    public boolean attemptIgnition() {
//        newOffsets.set(true);
        ItemStack stack = itemHandler.getStackInSlot(0);

        if (!(stack.getItem() instanceof Dislocator) || ((Dislocator) stack.getItem()).getLocation(stack, world) == null) {
            if (!dislocator_p2p.isValid(stack)) {
                return false;
            }
        }

        PairKV<Axis, List<BlockPos>> portalConfiguration = scanConfigurations();
        if (portalConfiguration != null) {
            igniting = true;

            for (BlockPos portalBlock : portalConfiguration.getValue()) {
                world.setBlockState(portalBlock, DEContent.portal.getDefaultState().with(Portal.AXIS, portalConfiguration.getKey()));
                TileEntity tile = world.getTileEntity(portalBlock);
                if (tile instanceof TilePortal) {
                    ((TilePortal) tile).setMasterPos(pos);
                }
            }

            active.set(true);
            activeAxis.set(portalConfiguration.getKey());

            BlockState state = world.getBlockState(pos);
            if (state.getBlock() == DEContent.dislocator_receptacle) {
                world.setBlockState(pos, state.with(DislocatorReceptacle.ACTIVE, true));
            }

            updateBlock();
            igniting = false;

            if (dislocator_p2p.isValid(stack) && !dislocator_p2p.isPlayer(stack)) {
                updateSpawnBlock(portalConfiguration.getValue());
                updateLinkBlock(portalConfiguration.getValue());
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
                if (world.isAirBlock(pos.up()) || world.getBlockState(pos.up()).getBlock() == DEContent.portal) {
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

    private PairKV<Axis, List<BlockPos>> scanConfigurations() {
        List<BlockPos> scanned = new ArrayList<BlockPos>();
        for (BlockPos offset : FacingUtils.AROUND_X) {
            List<BlockPos> portalBlocks = scanFromOrigin(pos.add(offset), Axis.X, scanned);
            if (portalBlocks != null) {
                return new PairKV<Axis, List<BlockPos>>(Axis.X, portalBlocks);
            }
        }

        scanned = new ArrayList<BlockPos>();
        for (BlockPos offset : FacingUtils.AROUND_Y) {
            List<BlockPos> portalBlocks = scanFromOrigin(pos.add(offset), Axis.Y, scanned);
            if (portalBlocks != null) {
                return new PairKV<Axis, List<BlockPos>>(Axis.Y, portalBlocks);
            }
        }

        scanned = new ArrayList<BlockPos>();
        for (BlockPos offset : FacingUtils.AROUND_Z) {
            List<BlockPos> portalBlocks = scanFromOrigin(pos.add(offset), Axis.Z, scanned);
            if (portalBlocks != null) {
                return new PairKV<Axis, List<BlockPos>>(Axis.Z, portalBlocks);
            }
        }

        return null;
    }

    private List<BlockPos> scanFromOrigin(BlockPos scanOrigin, Axis scanAxis, List<BlockPos> alreadyScanned) {
        if (!world.isAirBlock(scanOrigin) || alreadyScanned.contains(scanOrigin)) {
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
            BlockPos nextPos = scanPos.offset(facing);
            if (scanList.contains(nextPos) || isFrame(nextPos)) {
                continue;
            }
            else if (world.isAirBlock(nextPos)) {
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
        BlockState state = world.getBlockState(pos);
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
        Entity rootEntity = entity.getLowestRidingEntity();
        PassengerHelper passengerHelper = new PassengerHelper(rootEntity);
        passengerHelper.forEach(e -> arrivalsMap.put(e.getEntityId(), 10));
    }

    public void setSpawnPos(BlockPos spawnPos) {
        this.spawnPos.get().set(pos.subtract(spawnPos));
    }

    protected BlockPos getSpawnPos() {
        return pos.subtract(spawnPos.get().getPos());
    }

    public void setLinkPos(BlockPos spawnPos) {
        crystalLinkPos.get().set(pos.subtract(spawnPos));
    }

    protected BlockPos getLinkPos() {
        return pos.subtract(crystalLinkPos.get().getPos());
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
            BlockPos np = pos.offset(facing);
            if (blocks.contains(np)) continue;
            BlockState state = world.getBlockState(np);

            if (state.getBlock() == DEContent.portal) {
                TileEntity tile = world.getTileEntity(np);
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
        linkedCrystal.get().set(pos.subtract(crystalPos));
    }

    protected BlockPos getCrystalPos() {
        return pos.subtract(linkedCrystal.get().getPos());
    }

    private BlockPos remotePosCache = null;
    private DimensionType remoteDimCache = DimensionType.OVERWORLD;
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
                TileEntity tile = DislocatorLinkHandler.getTargetTile(world, stack);
                if (tile instanceof TileDislocatorReceptacle) {
                    remotePosCache = tile.getPos();
                    remoteDimCache = tile.getWorld().getDimension().getType();
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

        MinecraftServer server = world.getServer();
        if (server != null) {
            TileEntity tile = server.getWorld(remoteDimCache).getTileEntity(remotePosCache);
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
            MinecraftServer server = world.getServer();
            if (server != null && tile.linkedCrystal.get().y != -999) {
                TileEntity crystal = server.getWorld(remoteDimCache).getTileEntity(tile.getCrystalPos());
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
        setCrystalPos(((TileEntity) otherCrystal).getPos());
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
        double dist = FacingUtils.destanceInDirection(pos, linkTo, FacingUtils.getAxisFaces(activeAxis.get())[0]);
        Vec3D vec = Vec3D.getCenter(getLinkPos());

        Direction facing;
        if (dist > 0) {
            facing = FacingUtils.getAxisFaces(activeAxis.get())[0];
        }
        else {
            facing = FacingUtils.getAxisFaces(activeAxis.get())[1];
        }

        vec.add(facing.getXOffset() * 0.35, facing.getYOffset() * 0.35, facing.getZOffset() * 0.35);

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
    public CrystalGLFXBase createStaticFX() {
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
            hashID = pos.hashCode();
            hashCached = true;
        }
        return hashID;
    }

    //endregion
}
