package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileInventoryBase;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.brandonscore.lib.PairKV;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedEnum;
import com.brandon3055.brandonscore.lib.datamanager.ManagedVec3I;
import com.brandon3055.brandonscore.utils.FacingUtils;
import com.brandon3055.brandonscore.utils.InventoryUtils;
import com.brandon3055.brandonscore.utils.Teleporter;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.api.ITeleportEndPoint;
import com.brandon3055.draconicevolution.blocks.DislocatorReceptacle;
import com.brandon3055.draconicevolution.blocks.Portal;
import com.brandon3055.draconicevolution.handlers.DislocatorLinkHandler;
import com.brandon3055.draconicevolution.integration.funkylocomotion.IMovableStructure;
import com.brandon3055.draconicevolution.items.tools.Dislocator;
import com.brandon3055.draconicevolution.lib.DESoundHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

import java.util.*;
import java.util.function.Consumer;

import static com.brandon3055.draconicevolution.DEFeatures.dislocatorBound;

/**
 * Created by brandon3055 on 16/07/2016.
 */
public class TileDislocatorReceptacle extends TileInventoryBase implements ITickable, ITeleportEndPoint, IMovableStructure {

    //used to update existing portals to the new offset based portal positions
    public final ManagedBool NEW_OFFSETS = register("NEW_OFFSETS", new ManagedBool(false)).saveToTile().finish();
    public final ManagedBool ACTIVE = register("ACTIVE", new ManagedBool(false)).saveToTile().syncViaTile().trigerUpdate().finish();
    public final ManagedBool CAMO = register("CAMO", new ManagedBool(false)).saveToTile().syncViaTile().trigerUpdate().finish();
    public final ManagedBool LT_REDSTONE = register("LT_REDSTONE", new ManagedBool(false)).saveToTile().syncViaTile().trigerUpdate().finish();
    public final ManagedVec3I SPAWN_POS = register("SPAWN_POS", new ManagedVec3I(new Vec3I(0, -999, 0))).saveToTile().finish();
    public final ManagedEnum<Axis> ACTIVE_AXIS = register("ACTIVE_AXIS", new ManagedEnum<>(Axis.X)).saveToTile().finish();
    public boolean igniting = false;
    public boolean frameMoving = false;
    private List<Entity> teleportQ = new ArrayList<Entity>();
    private Map<Integer, Integer> coolDownMap = new HashMap<>();
    private Map<Integer, Integer> arrivalsMap = new HashMap<>();

    public TileDislocatorReceptacle() {
        setInventorySize(1);
        setShouldRefreshOnBlockChange();
    }

    @Override
    public void update() {
        super.update();
        if (!NEW_OFFSETS.value && ACTIVE.value) {
            deactivate();
            attemptIgnition();
            NEW_OFFSETS.value = true;
        }

        if (frameMoving) {
            if (ACTIVE.value){
                finishMove(pos, new HashSet<>());
            }
            frameMoving = false;
            checkIn();
        }

        for (Entity entity : teleportQ) {
            ItemStack stack = getStackInSlot(0);

            if (!(stack.getItem() instanceof Dislocator)) {
                deactivate();
                return;
            }

            Teleporter.TeleportLocation location = ((Dislocator) stack.getItem()).getLocation(stack, world);
            if (dislocatorBound.isValid(stack) && location != null) {
                location.setYaw(entity.rotationYaw);
                location.setPitch(entity.rotationPitch);
            }


            if (location == null) {
                if (!dislocatorBound.isValid(stack)) {
                    deactivate();
                }
                else {
                    if (dislocatorBound.isPlayer(stack)) {
                        ChatHelper.translate(entity, "info.de.bound_dislocator.cant_find_player", TextFormatting.RED);
                    }
                    else {
                        ChatHelper.translate(entity, "info.de.bound_dislocator.cant_find_target", TextFormatting.RED);
                    }
                }
                return;
            }

            dislocatorBound.notifyArriving(stack, world, entity);
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

    //region Activation & Inventory

    public boolean onBlockActivated(EntityPlayer player) {
        if (world.isRemote) {
            return !LT_REDSTONE.value;
        }

        InventoryUtils.handleHeldStackTransfer(0, this, player);
        return true;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        super.setInventorySlotContents(index, stack);

        ItemStack prev = getStackInSlot(0);
        if (dislocatorBound.isValid(prev) && !dislocatorBound.isPlayer(prev)) {
            DislocatorLinkHandler.removeLink(world, stack);
        }

        if (getStackInSlot(0).isEmpty() && ACTIVE.value) {
            deactivate();
        }
        else if (!getStackInSlot(0).isEmpty()) {
            attemptIgnition();
            checkIn();
        }
    }

    private void checkIn() {
        ItemStack stack = getStackInSlot(0);
        if (dislocatorBound.isValid(stack) && !dislocatorBound.isPlayer(stack)) {
            DislocatorLinkHandler.updateLink(world, stack, pos, world.provider.getDimension());
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return stack.getItem() instanceof Dislocator;
    }

    //endregion

    //region Teleport Handling

    public void handleEntityTeleport(Entity entity) {
        if (world.isRemote || teleportQ.contains(entity) || coolDownMap.containsKey(entity.getEntityId())) {
            return;
        }

        if (arrivalsMap.containsKey(entity.getEntityId())) {
            arrivalsMap.put(entity.getEntityId(), 10);
            return;
        }

        coolDownMap.put(entity.getEntityId(), 10);
        teleportQ.add(entity);
    }

    //endregion

    //region MultiBlock

    public void deactivate() {
        ACTIVE.value = false;

        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == DEFeatures.dislocatorReceptacle) {
            world.setBlockState(pos, state.withProperty(DislocatorReceptacle.ACTIVE, false));
        }

        for (BlockPos checkPos : BlockPos.getAllInBox(pos.add(-1, -1, -1), pos.add(1, 1, 1))) {
            TileEntity tile = world.getTileEntity(checkPos);
            if (tile instanceof TilePortal && ((TilePortal) tile).getMasterPos().equals(pos)) {
                world.setBlockToAir(tile.getPos());
            }
        }
        updateBlock();
    }

    public boolean attemptIgnition() {
        NEW_OFFSETS.value = true;
        ItemStack stack = getStackInSlot(0);

        if (!(stack.getItem() instanceof Dislocator) || ((Dislocator) stack.getItem()).getLocation(stack, world) == null) {
            return false;
        }

        PairKV<Axis, List<BlockPos>> portalConfiguration = scanConfigurations();
        if (portalConfiguration != null) {
            igniting = true;

            for (BlockPos portalBlock : portalConfiguration.getValue()) {
                world.setBlockState(portalBlock, DEFeatures.portal.getDefaultState().withProperty(Portal.AXIS, portalConfiguration.getKey()));
                TileEntity tile = world.getTileEntity(portalBlock);
                if (tile instanceof TilePortal) {
                    ((TilePortal) tile).setMasterPos(pos);
                }
            }

            ACTIVE.value = true;
            ACTIVE_AXIS.value = portalConfiguration.getKey();

            IBlockState state = world.getBlockState(pos);
            if (state.getBlock() == DEFeatures.dislocatorReceptacle) {
                world.setBlockState(pos, state.withProperty(DislocatorReceptacle.ACTIVE, true));
            }

            updateBlock();
            igniting = false;

            if (dislocatorBound.isValid(stack) && !dislocatorBound.isPlayer(stack)) {
                updateSpawnBlock(portalConfiguration.getValue());
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
                if (world.isAirBlock(pos.up()) || world.getBlockState(pos.up()).getBlock() == DEFeatures.portal) {
                    foundValid.add(pos);
                }
            }
            if (!foundValid.isEmpty()) {
                break;
            }
        }

        if (foundValid.isEmpty()) {
            SPAWN_POS.vec = new Vec3I(0, -999, 0);
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

        Vec3D center = new Vec3D(minX + ((maxX - minX) / 2), minY + ((maxY - minY) / 2), minZ + ((maxZ - minZ) / 2));
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

        for (EnumFacing facing : FacingUtils.getFacingsAroundAxis(scanAxis)) {
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
        IBlockState state = world.getBlockState(pos);
        return state.getBlock() == DEFeatures.infusedObsidian || state.getBlock() == DEFeatures.dislocatorReceptacle;
    }

    //endregion

    @Override
    public BlockPos getArrivalPos(String linkID) {
        if (!dislocatorBound.isValid(getStackInSlot(0)) || !dislocatorBound.getLinkID(getStackInSlot(0)).equals(linkID)) {
            return null;
        }

        return SPAWN_POS.vec.y == -999 ? null : getSpawnPos();
    }

    @Override
    public void entityArriving(Entity entity) {
        Entity rootEntity = entity.getLowestRidingEntity();
        PassengerHelper passengerHelper = new PassengerHelper(rootEntity);
        passengerHelper.forEach(e -> arrivalsMap.put(e.getEntityId(), 10));
    }

    public void setSpawnPos(BlockPos masterPos) {
        SPAWN_POS.vec.set(pos.subtract(masterPos));
    }

    protected BlockPos getSpawnPos() {
        return pos.subtract(SPAWN_POS.vec.getPos());
    }

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

    //Frame Movement

    @Override
    public Iterable<BlockPos> getBlocksForFrameMove() {
        if (ACTIVE.value) {
            for (BlockPos offset : FacingUtils.getAroundAxis(ACTIVE_AXIS.value)) {
                BlockPos next = pos.add(offset);
                if (world.getBlockState(next).getBlock() == DEFeatures.portal) {
                    HashSet<BlockPos> blocks = new HashSet<>();
                    findActiveBlocksOnAxis(ACTIVE_AXIS.value, next, blocks);
                    return blocks;
                }
            }
        }
        else {
            PairKV<Axis, List<BlockPos>> config = scanConfigurations();
            if (config != null) {
                HashSet<BlockPos> blocks = new HashSet<>();
                for (BlockPos ppos : config.getValue()) {
                    for (BlockPos offset : FacingUtils.getAroundAxis(config.getKey())) {
                        BlockPos next = ppos.add(offset);
                        if (blocks.contains(next)) continue;
                        if (world.getBlockState(next).getBlock() == DEFeatures.infusedObsidian) {
                            blocks.add(next);
                        }
                    }
                }
                return blocks;
            }
        }
        return Collections.emptyList();
    }

    public void findActiveBlocksOnAxis(Axis axis, BlockPos pos, HashSet<BlockPos> blocks) {
        blocks.add(pos);
        for (BlockPos offset : FacingUtils.getAroundAxis(axis)) {
            BlockPos next = pos.add(offset);
            if (blocks.contains(next)) continue;

            IBlockState state = world.getBlockState(next);
            if (state.getBlock() == DEFeatures.portal) {
                findActiveBlocksOnAxis(axis, next, blocks);
            }
            else if (state.getBlock() == DEFeatures.infusedObsidian) {
                blocks.add(next);
            }
        }
    }

    public void finishMove(BlockPos pos, HashSet<BlockPos> blocks) {
        for (EnumFacing facing : FacingUtils.getFacingsAroundAxis(ACTIVE_AXIS.value)) {
            BlockPos np = pos.offset(facing);
            if (blocks.contains(np)) continue;
            IBlockState state = world.getBlockState(np);

            if (state.getBlock() == DEFeatures.portal) {
                TileEntity tile = world.getTileEntity(np);
                if (tile instanceof TilePortal) {
                    ((TilePortal) tile).frameMoving = false;
                }
                blocks.add(np);
                finishMove(np, blocks);
            }
        }
    }
}
