package com.brandon3055.draconicevolution.blocks.tileentity;

import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.utils.FacingUtils;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.blocks.Portal;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by brandon3055 on 23/8/21
 */
public class PortalHelper {

    private TileDislocatorReceptacle tile;
    private boolean scanning = false;
    private boolean building = false;

    /**
     * List of air blocks that are part of a previous failed scan pass on the current axis.
     * If we hit one of these then we know this pass will fail.
     */
    private Map<Axis, Set<BlockPos>> invalidBlocks = null;
    /**
     * List of valid portal positions found in the current scan pass.
     */
    private Map<Axis, Set<BlockPos>> scanResult = null;

    /**
     * This method starts at a known valid portal position (air block) and proceeds to scan all of the blocks around that position.
     * If it finds more air blocks those are added to the que to be scanned next.
     * If it finds an already scanned block it is skipped.
     * If it finds anything that isn't an air block or a valid part of the portal frame the scan is aborted because this isn't a valid portal.
     */
    private Map<Axis, List<BlockPos>> scanQue = null;

    /**
     * List of possible scan start positions for the current axis
     */
    private Map<Axis, LinkedList<BlockPos>> originQue = null;


    public PortalHelper(TileDislocatorReceptacle tile) {
        this.tile = tile;
    }

    public int scanIndex = -1;

    public void startScan() {
        if (scanning) return;
        scanQue = new HashMap<>();
        originQue = new HashMap<>();
        invalidBlocks = new HashMap<>();
        scanResult = new HashMap<>();
        scanning = true;
        scanIndex++;
        initScan();
    }

    public void updateTick() {
        if (scanning) {
            boolean hasWork = false;
            for (Axis axis : Axis.values()) {
                if (scanning) {
                    updateAxis(axis);
                    if ((scanResult != null && scanResult.get(axis) != null) || (scanQue != null && !scanQue.isEmpty())) {
                        hasWork = true;
                    }
                }
            }

            if (scanning && !hasWork) {
                endScan(false, null, null);
            }
        } else if (building) {
            updateBuildTick();
        }
    }

    private void updateAxis(Axis axis) {
        if (!preScanCheck(axis)) return;

        Set<BlockPos> invalidBlocks = this.invalidBlocks.get(axis);
        Set<BlockPos> scanResult = this.scanResult.get(axis);
        List<BlockPos> scanQue = this.scanQue.get(axis);

        if (scanQue.isEmpty()) {
            endScan(true, scanResult, axis);
            return;
        }
        BlockPos pos = scanQue.remove(tile.getLevel().random.nextInt(scanQue.size()));

        tile.onScanBlock(pos);

        for (Direction dir : FacingUtils.getFacingsAroundAxis(axis)) {
            BlockPos newPos = pos.relative(dir);

            //If we have already scanned this position then skip it.
            if (scanResult.contains(newPos) || tile.getLevel().isOutsideBuildHeight(newPos)) {
                continue;
            }

            //If this is part of a previous failed scan then we know this scan will also fail.
            if (invalidBlocks.contains(newPos) || newPos.distSqr(tile.getBlockPos()) > DEConfig.portalMaxDistanceSq) {
                endPass(axis);
                return;
            }

            if (!tile.getLevel().isLoaded(newPos)) {
                endPass(axis);
                return;
            }

            if (isAir(newPos)) {
                scanQue.add(newPos);
                scanResult.add(newPos);
                if (scanResult.size() > DEConfig.portalMaxArea) {
                    endPass(axis);
                    return;
                }
            } else if (!isFrame(newPos)) {
                endPass(axis);
                return;
            }
        }
    }

    private boolean preScanCheck(Axis axis) {
        Set<BlockPos> invalidBlocks = this.invalidBlocks.get(axis);
        Set<BlockPos> scanResult = this.scanResult.get(axis);
        List<BlockPos> scanQue = this.scanQue.get(axis);
        LinkedList<BlockPos> originQue = this.originQue.get(axis);

        //The previous scan on this axis has failed
        if (scanResult == null) {
            //We have scanned all possible start positions on this axis
            if (originQue.isEmpty()) {
                return false;
            }

            BlockPos scanOrigin = originQue.pollFirst();
            if (invalidBlocks.contains(scanOrigin)) {
                return false;
            }
            scanResult = new HashSet<>();
            scanQue.clear();
            scanQue.add(scanOrigin);
            scanResult.add(scanOrigin);
            this.scanResult.put(axis, scanResult);
        }
        return true;
    }

    /**
     * Called to start scanning on the next scan axis
     * Updates the list of possible scan origins for the current axis and clears junk from previous axis.
     */
    private void initScan() {
        for (Axis axis : Axis.values()) {
            invalidBlocks.put(axis, new HashSet<>());
            scanQue.put(axis, new ArrayList<>());
            originQue.put(axis, new LinkedList<>());
            LinkedList<BlockPos> originQue = this.originQue.get(axis);

            BlockPos[] offsets = FacingUtils.getAroundAxis(axis);
            for (BlockPos offset : offsets) {
                BlockPos pos = tile.getBlockPos().offset(offset);
                if (isAir(pos)) {
                    originQue.add(pos);
                }
            }
        }
    }

    private void endPass(Axis axis) {
        invalidBlocks.get(axis).addAll(scanResult.get(axis));
        scanResult.put(axis, null);
    }

    private void endScan(boolean successful, Set<BlockPos> result, Axis axis) {
        scanning = false;
        scanQue = null;
        originQue = null;
        invalidBlocks = null;
        tile.onScanComplete(successful ? result : null, axis);
        scanResult = null;
    }

    public boolean isRunning() {
        return scanning || building;
    }

    private boolean isAir(BlockPos pos) {
        if (tile.getLevel().isEmptyBlock(pos)) {
            return true;
        }
        BlockEntity te = tile.getLevel().getBlockEntity(pos);
        return te instanceof TilePortal && ((TilePortal) te).getControllerPos().equals(tile.getBlockPos());
    }

    private boolean isFrame(BlockPos pos) {
        return pos.equals(tile.getBlockPos()) || tile.getLevel().getBlockState(pos).is(DEContent.INFUSED_OBSIDIAN.get());
    }

    public void abort() {
        if (scanning) endScan(false, null, null);
        if (building) endBuild(false);
    }

    private LinkedList<BlockPos> buildList = null;
    private Set<BlockPos> builtList = null;
    private Axis buildAxis = null;

    public void buildPortal(Set<BlockPos> portalShape, Axis axis) {
        building = true;
        buildList = new LinkedList<>();
        builtList = new HashSet<>();
        buildAxis = axis;
        Vector3 min = new Vector3().set(60000000);
        Vector3 max = new Vector3().set(-60000000);
        for (BlockPos pos : portalShape) {
            if (pos.getX() < min.x) min.x = pos.getX();
            if (pos.getY() < min.y) min.y = pos.getY();
            if (pos.getZ() < min.z) min.z = pos.getZ();
            if (pos.getX() > max.x) max.x = pos.getX();
            if (pos.getY() > max.y) max.y = pos.getY();
            if (pos.getZ() > max.z) max.z = pos.getZ();
        }
        BlockPos mid = min.copy().add(max.subtract(min).divide(2)).pos();
        buildList.addAll(portalShape.stream().parallel().sorted(Comparator.comparing(pos -> pos.distSqr(mid))).collect(Collectors.toList()));
    }

    private void updateBuildTick() {
        if (buildList.isEmpty()) {
            endBuild(true);
            return;
        }

        BlockPos nextPos = buildList.pollLast();
        if (!isAir(nextPos)) {
            endBuild(false);
            return;
        }

        BlockState portalState = DEContent.PORTAL.get().defaultBlockState().setValue(Portal.AXIS, buildAxis);
        tile.getLevel().setBlockAndUpdate(nextPos, Portal.getPlacementState(portalState, tile.getLevel(), nextPos));
        builtList.add(nextPos);
        BlockEntity placedTile = tile.getLevel().getBlockEntity(nextPos);
        if (placedTile instanceof TilePortal) {
            ((TilePortal) placedTile).setControllerPos(tile.getBlockPos());
        } else {
            endBuild(false);
            return;
        }
    }

    private void endBuild(boolean successful) {
        building = false;
        buildList = null;
        if (successful) {
            tile.onBuildSuccess(new ArrayList<>(builtList));
        } else {
            tile.onBuildFail();
            for (BlockPos pos : builtList) {
                BlockState state = tile.getLevel().getBlockState(pos);
                if (state.is(DEContent.PORTAL.get())) {
                    tile.getLevel().removeBlock(pos, false);
                }
            }
        }
        builtList = null;
    }

    public boolean isBuilding() {
        return building;
    }
}
