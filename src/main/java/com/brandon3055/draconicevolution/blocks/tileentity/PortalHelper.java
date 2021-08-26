package com.brandon3055.draconicevolution.blocks.tileentity;

import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.network.BCoreNetwork;
import com.brandon3055.brandonscore.utils.FacingUtils;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

/**
 * Created by brandon3055 on 23/8/21
 */
public class PortalHelper {

    private TileDislocatorReceptacle tile;
    private boolean running = false;
    /**
     * The current axis being scanned.
     */
    private Axis scanAxis = null;
    /**
     * List of air blocks that are part of a previous failed scan pass on the current axis.
     * If we hit one of these then we know this pass will fail.
     */
    private Set<BlockPos> invalidBlocks = null;
    /**
     * List of valid portal positions found in the current scan pass.
     */
    private Set<BlockPos> scanResult = null;

    /**
     * This method starts at a known valid portal position (air block) and proceeds to scan all of the blocks around that position.
     * If it finds more air blocks those are added to the que to be scanned next.
     * If it finds an already scanned block it is skipped.
     * If it finds anything that isn't an air block or a valid part of the portal frame the scan is aborted because this isn't a valid portal.
     */
    private List<BlockPos> scanQue = null;

    /**
     * List of possible scan start positions for the current axis
     */
    private LinkedList<BlockPos> originQue = null;


    public PortalHelper(TileDislocatorReceptacle tile) {
        this.tile = tile;
    }

    public int scanIndex = -1;

    public void startScan() {
        if (running) return;
        scanAxis = null;
        scanQue = new ArrayList<>();
        originQue = new LinkedList<>();
        invalidBlocks = new HashSet<>();
        running = true;
        scanIndex++;
    }

    public void updateTick() {
        if (!running || !preScanCheck()) return;

        if (scanQue.isEmpty()) {
            endScan(true);
            return;
        }
        BlockPos pos;
//        switch (scanIndex) {
//            case 0:
//                pos = scanQue.pollFirst(); //Plane spread
//                break;
//            case 1:
                pos = scanQue.remove(tile.getLevel().random.nextInt(scanQue.size())); //Random Spread
//                break;
//            case 2:
//                pos = scanQue.pollLast(); //Funky player spread
//                break;
//            case 3:
//                scanQue.sort(Comparator.comparing(e -> e.distSqr(tile.getBlockPos())));
//                pos = scanQue.pollFirst();
//                break;
//            case 4:
//                scanQue.sort(Comparator.comparing(e -> e.distSqr(tile.getBlockPos())));
//                pos = scanQue.pollLast();
//                break;
//            default:
//                scanIndex = 0;
//                return;
//        }

        tile.onScanBlock(pos);
        BCoreNetwork.sendParticle(tile.getLevel(), ParticleTypes.CLOUD, Vector3.fromBlockPosCenter(pos), Vector3.ZERO, true);

        for (Direction dir : FacingUtils.getFacingsAroundAxis(scanAxis)) {
            BlockPos newPos = pos.relative(dir);

            //If we have already scanned this position then skip it.
            if (scanResult.contains(newPos) || World.isOutsideBuildHeight(newPos)) {
                continue;
            }

            //If this is part of a previous failed scan then we know this scan will also fail.
            if (invalidBlocks.contains(newPos) || newPos.distSqr(tile.getBlockPos()) > DEConfig.portalMaxDistanceSq) {
                endPass();
                return;
            }

            if (!tile.getLevel().isLoaded(newPos)) {
                endPass();
                return;
            }

            if (isAir(newPos)) {
                scanQue.add(newPos);
                scanResult.add(newPos);
                if (scanResult.size() > DEConfig.portalMaxArea) {
                    endPass();
                    return;
                }
            } else if (!isFrame(newPos)) {
                endPass();
                return;
            }
        }
    }

    private boolean preScanCheck() {
        //The previous scan on this axis has failed
        if (scanResult == null) {
            //We have scanned all possible start positions on this axis
            if (originQue.isEmpty()) {
                startNextAxis();
                if (!running || originQue.isEmpty()) {
                    return false;
                }
            }

            BlockPos scanOrigin = originQue.pollFirst();
            if (invalidBlocks.contains(scanOrigin)) {
                return false;
            }
            scanResult = new HashSet<>();
            scanQue.clear();
            scanQue.add(scanOrigin);
            scanResult.add(scanOrigin);
        }
        return true;
    }

    /**
     * Called to start scanning on the next scan axis
     * Updates the list of possible scan origins for the current axis and clears junk from previous axis.
     */
    private void startNextAxis() {
        if (scanAxis == null) {
            scanAxis = Axis.X;
        } else if (scanAxis == Axis.Z) {
            endScan(false);
            return;
        } else {
            scanAxis = Axis.values()[scanAxis.ordinal() + 1];
        }

        originQue.clear();
        invalidBlocks.clear();

        BlockPos[] offsets = FacingUtils.getAroundAxis(scanAxis);
        for (BlockPos offset : offsets) {
            BlockPos pos = tile.getBlockPos().offset(offset);
            if (isAir(pos)) {
                originQue.add(pos);
            }
        }
    }

    private void endPass() {
        invalidBlocks.addAll(scanResult);
        scanResult = null;
    }

    public void endScan(boolean successful) {
        running = false;
        scanQue = null;
        originQue = null;
        invalidBlocks = null;
        tile.onScanComplete(successful ? scanResult : null);
        scanResult = null;
    }

    public boolean isRunning() {
        return running;
    }

    //    @Nullable
//    public Set<BlockPos> scanPortal() {
//
////        Do some profiling and attempt optimisation if needed
////                Also figure out max size constraints
//
//        Set<BlockPos> result = null;
//        for (Axis axis : Axis.values()) {
//            result = scanAxis(axis);
//            if (result != null) break;
//        }
//
//        result = scanAxis(Axis.X);
//
//        return result;
//    }
//
//    private Set<BlockPos> scanAxis(Axis axis) {
//        BlockPos[] startOffsets = FacingUtils.getAroundAxis(axis);
//        Set<BlockPos> result = null;
//        HashSet<BlockPos> invalidArea = new HashSet<>();
//
//        for (BlockPos offset : startOffsets) {
//            BlockPos pos = tile.getBlockPos().offset(offset);
//            if (isAir(pos) && !invalidArea.contains(pos)) {
//                result = scanFromOrigin(pos, axis, invalidArea);
//                if (result != null) break;
//            }
//        }
//
//        return result;
//    }
//
//    private Set<BlockPos> scanFromOrigin(BlockPos origin, Axis axis, HashSet<BlockPos> failedCache) {
//        Set<BlockPos> result = new HashSet<>();
//
//        /*
//        * This method starts at a known valid portal position (air block) and proceeds to scan all of the blocks around that position.
//        * If it finds more air blocks those are added to the que to be scanned next.
//        * If it finds an already scanned block it is skipped.
//        * If it finds anything that isn't an air block or a valid part of the portal frame the scan is aborted because this isn't a valid portal.
//        * */
//
//        LinkedList<BlockPos> scanQue = new LinkedList<>();
//        scanQue.add(origin);
//        result.add(origin);
//
//        int scanCount = 0;
//        while (!scanQue.isEmpty()) {
//            //This pos should always be a valid portal position (so air)
//            BlockPos pos = scanQue.pollFirst();
//
//            for (Direction dir : FacingUtils.getFacingsAroundAxis(axis)) {
//                BlockPos newPos = pos.relative(dir);
//
//                //If we have already scanned this position then skip it.
//                if (result.contains(newPos) || World.isOutsideBuildHeight(newPos)) {
//                    continue;
//                }
//
//                //If this is part of a previous failed scan then we know this scan will also fail.
//                if (failedCache.contains(newPos)) {
//                    failedCache.addAll(result);
//                    return null;
//                }
//
//                if (isAir(newPos)) {
//                    scanQue.add(newPos);
//                    result.add(newPos);
//                } else if (!isFrame(newPos)) {
//                    failedCache.addAll(result);
//                    return null;
//                }
//            }
//
//            if (++scanCount > 65536) return null;
//        }
//
//        return result;
//    }


    private boolean isAir(BlockPos pos) {
        //Is air or portal block that belongs to this and is decaying
        return tile.getLevel().isEmptyBlock(pos);
    }

    private boolean isFrame(BlockPos pos) {
        return pos.equals(tile.getBlockPos()) || tile.getLevel().getBlockState(pos).getBlock() == DEContent.infused_obsidian;
    }
}
