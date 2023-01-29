package com.brandon3055.draconicevolution.common.utills;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.ForgeDirection;

import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.TileDislocatorReceptacle;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.TilePortalBlock;

/**
 * Created by Brandon on 23/5/2015.
 */
public class PortalHelper {

    static int iterationNow = 0;

    public static boolean isFrame(Block block) {
        return block == ModBlocks.infusedObsidian;
    }

    public static boolean isReceptacle(Block block) {
        return block == ModBlocks.dislocatorReceptacle;
    }

    public static boolean isPortal(Block block) {
        return block == ModBlocks.portal;
    }

    public static PortalStructure getValidStructure(World world, int x, int y, int z) {
        if (world.isRemote) return null;

        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            for (ForgeDirection plane : ForgeDirection.VALID_DIRECTIONS) {
                if (plane != direction && plane != direction.getOpposite()) {
                    PortalStructure structure = traceFrame(world, x, y, z, direction, plane);
                    if (structure != null && structure.scanPortal(world, x, y, z, false, false)) return structure;
                }
            }
        }

        return null;
    }

    public static PortalStructure traceFrame(World world, int x, int y, int z, ForgeDirection startDir,
            ForgeDirection plane) {
        int MAX_SIZE = 150;
        int startX = x + startDir.offsetX;
        int startY = y + startDir.offsetY;
        int startZ = z + startDir.offsetZ;

        // Check that the trace is starting from a receptacle
        if (!world.isAirBlock(startX, startY, startZ)) return null;

        int xSize = 0;
        int ySize = 0;
        int yOffset = 0;

        // Get X size
        for (int i = 0; i <= MAX_SIZE; i++) {
            Block block = world.getBlock(
                    startX + i * startDir.offsetX,
                    startY + i * startDir.offsetY,
                    startZ + i * startDir.offsetZ);
            if (isFrame(block)) {
                xSize = i;
                break;
            } else if (!world.isAirBlock(
                    startX + i * startDir.offsetX,
                    startY + i * startDir.offsetY,
                    startZ + i * startDir.offsetZ))
                return null;
        }

        // Get Y size above receptacle
        for (int i = 0; i <= MAX_SIZE; i++) {
            Block block = world
                    .getBlock(startX + i * plane.offsetX, startY + i * plane.offsetY, startZ + i * plane.offsetZ);
            if (isFrame(block)) {
                ySize = i;
                break;
            } else if (!world
                    .isAirBlock(startX + i * plane.offsetX, startY + i * plane.offsetY, startZ + i * plane.offsetZ))
                return null;
        }

        // Get Y size below receptacle and get y offset
        for (int i = 0; i <= MAX_SIZE; i++) {
            Block block = world
                    .getBlock(startX - i * plane.offsetX, startY - i * plane.offsetY, startZ - i * plane.offsetZ);
            if (isFrame(block)) {
                ySize += i - 1;
                yOffset = i;
                break;
            } else if (!world
                    .isAirBlock(startX - i * plane.offsetX, startY - i * plane.offsetY, startZ - i * plane.offsetZ))
                return null;
        }

        if (xSize == 0 || ySize == 0 || ySize > MAX_SIZE) return null;

        PortalStructure structure = new PortalStructure(xSize, ySize, yOffset, startDir, plane);

        if (!structure.checkFrameIsValid(world, x, y, z) || !structure.scanPortal(world, x, y, z, false, false))
            return null;

        return structure;
    }

    public static class PortalStructure {

        public int xSize;
        public int ySize;
        public int yOffset;
        public ForgeDirection startDir;
        public ForgeDirection plane;

        public PortalStructure() {}

        public PortalStructure(int xSize, int ySize, int yOffset, ForgeDirection startDir, ForgeDirection plane) {
            this.xSize = xSize;
            this.ySize = ySize;
            this.yOffset = yOffset;
            this.startDir = startDir;
            this.plane = plane;
        }

        public boolean checkFrameIsValid(World world, int x, int y, int z) {

            int startX = x + startDir.offsetX;
            int startY = y + startDir.offsetY;
            int startZ = z + startDir.offsetZ;

            // Check structure for y size
            for (int y1 = 1; y1 <= ySize; y1++) {
                int y2 = y1 - yOffset;

                int inX = startX + y2 * plane.offsetX - startDir.offsetX;
                int inY = startY + y2 * plane.offsetY - startDir.offsetY;
                int inZ = startZ + y2 * plane.offsetZ - startDir.offsetZ;

                int outX = startX + y2 * plane.offsetX + (xSize) * startDir.offsetX;
                int outY = startY + y2 * plane.offsetY + (xSize) * startDir.offsetY;
                int outZ = startZ + y2 * plane.offsetZ + (xSize) * startDir.offsetZ;

                if (!isFrame(world.getBlock(inX, inY, inZ)) && !(inX == x && inY == y && inZ == z)) return false;
                if (!isFrame(world.getBlock(outX, outY, outZ))) return false;
            }

            // Check structure for x size
            for (int x1 = 0; x1 < xSize; x1++) {
                int upX = startX + x1 * startDir.offsetX - yOffset * plane.offsetX;
                int upY = startY + x1 * startDir.offsetY - yOffset * plane.offsetY;
                int upZ = startZ + x1 * startDir.offsetZ - yOffset * plane.offsetZ;

                int downX = startX + x1 * startDir.offsetX + (ySize - yOffset + 1) * plane.offsetX;
                int downY = startY + x1 * startDir.offsetY + (ySize - yOffset + 1) * plane.offsetY;
                int downZ = startZ + x1 * startDir.offsetZ + (ySize - yOffset + 1) * plane.offsetZ;

                if (!isFrame(world.getBlock(upX, upY, upZ))) return false;
                if (!isFrame(world.getBlock(downX, downY, downZ))) return false;
            }

            return true;
        }

        public boolean scanPortal(World world, int x, int y, int z, boolean setPortalBlocks,
                boolean checkPortalBlocks) {
            int startX = x + startDir.offsetX;
            int startY = y + startDir.offsetY;
            int startZ = z + startDir.offsetZ;

            TileDislocatorReceptacle receptacle = (TileDislocatorReceptacle) world.getTileEntity(x, y, z);
            if (receptacle == null) return false;
            if (setPortalBlocks) receptacle.updating = true;

            for (int x1 = 0; x1 < xSize; x1++) {
                for (int y1 = 1; y1 <= ySize; y1++) {
                    int y2 = y1 - yOffset;
                    int X = (startX + x1 * startDir.offsetX) + y2 * plane.offsetX;
                    int Y = (startY + x1 * startDir.offsetY) + y2 * plane.offsetY;
                    int Z = (startZ + x1 * startDir.offsetZ) + y2 * plane.offsetZ;

                    Block block = world.getBlock(X, Y, Z);

                    if (checkPortalBlocks) {
                        if (!isPortal(block)) return false;
                    } else if (setPortalBlocks) {
                        world.setBlock(X, Y, Z, ModBlocks.portal);
                        Chunk chunk = world.getChunkFromBlockCoords(X, Z);
                        TilePortalBlock tile = (TilePortalBlock) chunk.func_150806_e(X & 15, Y, Z & 15);
                        tile.masterX = x;
                        tile.masterY = y;
                        tile.masterZ = z;
                    } else if (!world.isAirBlock(X, Y, Z)) return false;
                }
            }

            if (setPortalBlocks) receptacle.updating = false;

            return true;
        }

        public void writeToNBT(NBTTagCompound compound) {
            compound.setInteger("XSize", xSize);
            compound.setInteger("YSize", ySize);
            compound.setInteger("YOffset", yOffset);
            compound.setString("StartDir", startDir.name());
            compound.setString("Plane", plane.name());
        }

        public void readFromNBT(NBTTagCompound compound) {
            xSize = compound.getInteger("XSize");
            ySize = compound.getInteger("YSize");
            yOffset = compound.getInteger("YOffset");
            startDir = ForgeDirection.valueOf(compound.getString("StartDir"));
            plane = ForgeDirection.valueOf(compound.getString("Plane"));
        }
    }
}
