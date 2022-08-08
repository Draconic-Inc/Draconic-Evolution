package com.brandon3055.draconicevolution.common.tileentities.multiblocktiles;

import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.blocks.multiblock.MultiblockHelper.TileLocation;
import com.brandon3055.draconicevolution.common.handler.BalanceConfigHandler;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.tileentities.TileObjectSync;
import com.brandon3055.draconicevolution.common.tileentities.TileParticleGenerator;
import com.brandon3055.draconicevolution.common.utills.LogHelper;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;

/**
 * Created by Brandon on 25/07/2014.
 */
public class TileEnergyStorageCore extends TileObjectSync {

    protected TileLocation[] stabilizers = new TileLocation[4];
    protected int tier = 0;
    protected boolean online = false;
    public float modelRotation = 0;
    private long energy = 0;
    private long capacity = 0;
    private long lastTickCapacity = 0;
    private int tick = 0;

    public TileEnergyStorageCore() {
        for (int i = 0; i < stabilizers.length; i++) {
            stabilizers[i] = new TileLocation();
        }
    }

    @Override
    public void updateEntity() {
        // energy = 200000000;
        if (!online) return;
        if (worldObj.isRemote) modelRotation += 0.5;
        if (!worldObj.isRemote) detectAndRendChanges();
        tick++;
    }

    /**
     * ######################MultiBlock Methods#######################
     */
    public boolean tryActivate() {
        if (!findStabalyzers()) return false;
        if (!setTier(false)) return false;
        if (!testOrActivateStructureIfValid(false, false)) return false;
        online = true;
        if (!testOrActivateStructureIfValid(false, true)) {
            online = false;
            deactivateStabilizers();
            return false;
        }
        activateStabilizers();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        return true;
    }

    public boolean creativeActivate() {
        if (!findStabalyzers()) return false;
        if (!setTier(false)) return false;
        if (!testOrActivateStructureIfValid(true, false)) return false;
        online = true;
        if (!testOrActivateStructureIfValid(false, true)) {
            online = false;
            deactivateStabilizers();
            return false;
        }
        activateStabilizers();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        return true;
    }

    public boolean isStructureStillValid(boolean update) {
        if (!checkStabilizers()) online = false;
        if (!testOrActivateStructureIfValid(false, false)) online = false;
        if (!areStabilizersActive()) online = false;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        if (!online) deactivateStabilizers();
        if (update && !online) reIntegrate();
        // if (update && !online && worldObj.getTileEntity(xCoord, yCoord + 1, zCoord) != null &&
        // worldObj.getTileEntity(xCoord, yCoord + 1, zCoord) instanceof TileInvisibleMultiblock)
        // ((TileInvisibleMultiblock)worldObj.getTileEntity(xCoord, yCoord + 1, zCoord)).isStructureStillValid();
        return online;
    }

    private void reIntegrate() {
        for (int x = xCoord - 1; x <= xCoord + 1; x++) {
            for (int y = yCoord - 1; y <= yCoord + 1; y++) {
                for (int z = zCoord - 1; z <= zCoord + 1; z++) {
                    if (worldObj.getBlock(x, y, z) == ModBlocks.invisibleMultiblock) {
                        if (worldObj.getBlockMetadata(x, y, z) == 0) {
                            worldObj.setBlock(
                                    x,
                                    y,
                                    z,
                                    BalanceConfigHandler.energyStorageStructureOuterBlock,
                                    BalanceConfigHandler.energyStorageStructureOuterBlockMetadata,
                                    3);
                        } else if (worldObj.getBlockMetadata(x, y, z) == 1) {
                            worldObj.setBlock(
                                    x,
                                    y,
                                    z,
                                    BalanceConfigHandler.energyStorageStructureBlock,
                                    BalanceConfigHandler.energyStorageStructureBlockMetadata,
                                    3);
                        }
                    }
                }
            }
        }
    }

    private boolean findStabalyzers() {
        boolean flag = true;
        for (int x = xCoord; x <= xCoord + 11; x++) {
            if (worldObj.getBlock(x, yCoord, zCoord) == ModBlocks.particleGenerator) {
                if (worldObj.getBlockMetadata(x, yCoord, zCoord) == 1) {
                    flag = false;
                    break;
                }
                stabilizers[0] = new TileLocation(x, yCoord, zCoord);
                break;
            } else if (x == xCoord + 11) {
                flag = false;
            }
        }
        for (int x = xCoord; x >= xCoord - 11; x--) {
            if (worldObj.getBlock(x, yCoord, zCoord) == ModBlocks.particleGenerator) {
                if (worldObj.getBlockMetadata(x, yCoord, zCoord) == 1) {
                    flag = false;
                    break;
                }
                stabilizers[1] = new TileLocation(x, yCoord, zCoord);
                break;
            } else if (x == xCoord - 11) {
                flag = false;
            }
        }
        for (int z = zCoord; z <= zCoord + 11; z++) {
            if (worldObj.getBlock(xCoord, yCoord, z) == ModBlocks.particleGenerator) {
                if (worldObj.getBlockMetadata(xCoord, yCoord, z) == 1) {
                    flag = false;
                    break;
                }
                stabilizers[2] = new TileLocation(xCoord, yCoord, z);
                break;
            } else if (z == zCoord + 11) {
                flag = false;
            }
        }
        for (int z = zCoord; z >= zCoord - 11; z--) {
            if (worldObj.getBlock(xCoord, yCoord, z) == ModBlocks.particleGenerator) {
                if (worldObj.getBlockMetadata(xCoord, yCoord, z) == 1) {
                    flag = false;
                    break;
                }
                stabilizers[3] = new TileLocation(xCoord, yCoord, z);
                break;
            } else if (z == zCoord - 11) {
                flag = false;
            }
        }
        return flag;
    }

    private boolean setTier(boolean force) {
        if (force) return true;
        int xPos = 0;
        int xNeg = 0;
        int yPos = 0;
        int yNeg = 0;
        int zPos = 0;
        int zNeg = 0;
        int range = 5;

        for (int x = 0; x <= range; x++) {
            if (testForOrActivateDraconium(xCoord + x, yCoord, zCoord, false, false)) {
                xPos = x;
                break;
            }
        }

        for (int x = 0; x <= range; x++) {
            if (testForOrActivateDraconium(xCoord - x, yCoord, zCoord, false, false)) {
                xNeg = x;
                break;
            }
        }

        for (int y = 0; y <= range; y++) {
            if (testForOrActivateDraconium(xCoord, yCoord + y, zCoord, false, false)) {
                yPos = y;
                break;
            }
        }

        for (int y = 0; y <= range; y++) {
            if (testForOrActivateDraconium(xCoord, yCoord - y, zCoord, false, false)) {
                yNeg = y;
                break;
            }
        }

        for (int z = 0; z <= range; z++) {
            if (testForOrActivateDraconium(xCoord, yCoord, zCoord + z, false, false)) {
                zPos = z;
                break;
            }
        }

        for (int z = 0; z <= range; z++) {
            if (testForOrActivateDraconium(xCoord, yCoord, zCoord - z, false, false)) {
                zNeg = z;
                break;
            }
        }

        if (zNeg != zPos || zNeg != yNeg || zNeg != yPos || zNeg != xNeg || zNeg != xPos) return false;

        tier = xPos;
        if (tier > 1) tier++;
        if (tier == 1) {
            if (testForOrActivateDraconium(xCoord + 1, yCoord + 1, zCoord, false, false)) tier = 2;
        }
        return true;
    }

    private boolean testOrActivateStructureIfValid(boolean setBlocks, boolean activate) {
        switch (tier) {
            case 0:
                if (!testOrActivateRect(1, 1, 1, "air", setBlocks, activate)) return false;
                break;
            case 1:
                if (!testForOrActivateDraconium(xCoord + 1, yCoord, zCoord, setBlocks, activate)
                        || !testForOrActivateDraconium(xCoord - 1, yCoord, zCoord, setBlocks, activate)
                        || !testForOrActivateDraconium(xCoord, yCoord + 1, zCoord, setBlocks, activate)
                        || !testForOrActivateDraconium(xCoord, yCoord - 1, zCoord, setBlocks, activate)
                        || !testForOrActivateDraconium(xCoord, yCoord, zCoord + 1, setBlocks, activate)
                        || !testForOrActivateDraconium(xCoord, yCoord, zCoord - 1, setBlocks, activate)) return false;
                if (!isReplacable(xCoord + 1, yCoord + 1, zCoord, setBlocks)
                        || !isReplacable(xCoord, yCoord + 1, zCoord + 1, setBlocks)
                        || !isReplacable(xCoord - 1, yCoord + 1, zCoord, setBlocks)
                        || !isReplacable(xCoord, yCoord + 1, zCoord - 1, setBlocks)
                        || !isReplacable(xCoord + 1, yCoord - 1, zCoord, setBlocks)
                        || !isReplacable(xCoord, yCoord - 1, zCoord + 1, setBlocks)
                        || !isReplacable(xCoord - 1, yCoord - 1, zCoord, setBlocks)
                        || !isReplacable(xCoord, yCoord - 1, zCoord - 1, setBlocks)
                        || !isReplacable(xCoord + 1, yCoord, zCoord + 1, setBlocks)
                        || !isReplacable(xCoord - 1, yCoord, zCoord - 1, setBlocks)
                        || !isReplacable(xCoord + 1, yCoord, zCoord - 1, setBlocks)
                        || !isReplacable(xCoord - 1, yCoord, zCoord + 1, setBlocks)) return false;
                if (!isReplacable(xCoord + 1, yCoord + 1, zCoord + 1, setBlocks)
                        || !isReplacable(xCoord - 1, yCoord + 1, zCoord - 1, setBlocks)
                        || !isReplacable(xCoord + 1, yCoord + 1, zCoord - 1, setBlocks)
                        || !isReplacable(xCoord - 1, yCoord + 1, zCoord + 1, setBlocks)
                        || !isReplacable(xCoord + 1, yCoord - 1, zCoord + 1, setBlocks)
                        || !isReplacable(xCoord - 1, yCoord - 1, zCoord - 1, setBlocks)
                        || !isReplacable(xCoord + 1, yCoord - 1, zCoord - 1, setBlocks)
                        || !isReplacable(xCoord - 1, yCoord - 1, zCoord + 1, setBlocks)) return false;
                break;
            case 2:
                if (!testOrActivateRect(1, 1, 1, "draconiumBlock", setBlocks, activate)) return false;
                break;
            case 3:
                if (!testOrActivateSides(1, "draconiumBlock", setBlocks, activate)) return false;
                if (!testOrActivateRect(1, 1, 1, "redstone", setBlocks, activate)) return false;
                break;
            case 4:
                if (!testOrActivateSides(2, "draconiumBlock", setBlocks, activate)) return false;
                if (!testOrActivateRect(2, 1, 1, "redstone", setBlocks, activate)) return false;
                if (!testOrActivateRect(1, 2, 1, "redstone", setBlocks, activate)) return false;
                if (!testOrActivateRect(1, 1, 2, "redstone", setBlocks, activate)) return false;
                if (!testOrActivateRings(2, 2, "draconiumBlock", setBlocks, activate)) return false;
                break;
            case 5:
                if (!testOrActivateSides(3, "draconiumBlock", setBlocks, activate)) return false;
                if (!testOrActivateSides(2, "redstone", setBlocks, activate)) return false;
                if (!testOrActivateRect(2, 2, 2, "redstone", setBlocks, activate)) return false;
                if (!testOrActivateRings(2, 3, "draconiumBlock", setBlocks, activate)) return false;
                break;
            case 6:
                if (!testOrActivateSides(4, "draconiumBlock", setBlocks, activate)) return false;
                if (!testOrActivateSides(3, "redstone", setBlocks, activate)) return false;
                if (!testOrActivateRect(3, 2, 2, "redstone", setBlocks, activate)) return false;
                if (!testOrActivateRect(2, 3, 2, "redstone", setBlocks, activate)) return false;
                if (!testOrActivateRect(2, 2, 3, "redstone", setBlocks, activate)) return false;
                if (!testOrActivateRings(2, 4, "draconiumBlock", setBlocks, activate)) return false;
                if (!testOrActivateRings(3, 3, "draconiumBlock", setBlocks, activate)) return false;
                break;
        }
        return true;
    }

    private boolean testOrActivateRect(int xDim, int yDim, int zDim, String block, boolean set, boolean activate) {
        for (int x = xCoord - xDim; x <= xCoord + xDim; x++) {
            for (int y = yCoord - yDim; y <= yCoord + yDim; y++) {
                for (int z = zCoord - zDim; z <= zCoord + zDim; z++) {

                    if (block.equals("air")) {
                        if (!(x == xCoord && y == yCoord && z == zCoord) && !isReplacable(x, y, z, set)) return false;
                    } else if (block.equals("redstone")) {
                        if (!(x == xCoord && y == yCoord && z == zCoord)
                                && !testForOrActivateRedstone(x, y, z, set, activate)) return false;
                    } else if (block.equals("draconiumBlock")) {
                        if (!(x == xCoord && y == yCoord && z == zCoord)
                                && !testForOrActivateDraconium(x, y, z, set, activate)) return false;
                    } else if (!block.equals("draconiumBlock") && !block.equals("redstone") && !block.equals("air")) {
                        LogHelper.error("Invalid String In Multiblock Structure Code!!!");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean testOrActivateRings(int size, int dist, String block, boolean set, boolean activate) {
        for (int y = yCoord - size; y <= yCoord + size; y++) {
            for (int z = zCoord - size; z <= zCoord + size; z++) {

                if (y == yCoord - size || y == yCoord + size || z == zCoord - size || z == zCoord + size) {
                    if (block.equals("air")) {
                        if (!(xCoord + dist == xCoord && y == yCoord && z == zCoord)
                                && !isReplacable(xCoord + dist, y, z, set)) return false;
                    } else if (block.equals("redstone")) {
                        if (!(xCoord + dist == xCoord && y == yCoord && z == zCoord)
                                && !testForOrActivateRedstone(xCoord + dist, y, z, set, activate)) return false;
                    } else if (block.equals("draconiumBlock")) {
                        if (!(xCoord + dist == xCoord && y == yCoord && z == zCoord)
                                && !testForOrActivateDraconium(xCoord + dist, y, z, set, activate)) return false;
                    } else if (!block.equals("draconiumBlock") && !block.equals("redstone") && !block.equals("air")) {
                        LogHelper.error("Invalid String In Multiblock Structure Code!!!");
                        return false;
                    }
                }
            }
        }
        for (int y = yCoord - size; y <= yCoord + size; y++) {
            for (int z = zCoord - size; z <= zCoord + size; z++) {

                if (y == yCoord - size || y == yCoord + size || z == zCoord - size || z == zCoord + size) {
                    if (block.equals("air")) {
                        if (!(xCoord - dist == xCoord && y == yCoord && z == zCoord)
                                && !isReplacable(xCoord - dist, y, z, set)) return false;
                    } else if (block.equals("redstone")) {
                        if (!(xCoord - dist == xCoord && y == yCoord && z == zCoord)
                                && !testForOrActivateRedstone(xCoord - dist, y, z, set, activate)) return false;
                    } else if (block.equals("draconiumBlock")) {
                        if (!(xCoord - dist == xCoord && y == yCoord && z == zCoord)
                                && !testForOrActivateDraconium(xCoord - dist, y, z, set, activate)) return false;
                    } else if (!block.equals("draconiumBlock") && !block.equals("redstone") && !block.equals("air")) {
                        LogHelper.error("Invalid String In Multiblock Structure Code!!!");
                        return false;
                    }
                }
            }
        }

        for (int x = xCoord - size; x <= xCoord + size; x++) {
            for (int z = zCoord - size; z <= zCoord + size; z++) {

                if (x == xCoord - size || x == xCoord + size || z == zCoord - size || z == zCoord + size) {
                    if (block.equals("air")) {
                        if (!(x == xCoord && yCoord + dist == yCoord && z == zCoord)
                                && !isReplacable(x, yCoord + dist, z, set)) return false;
                    } else if (block.equals("redstone")) {
                        if (!(x == xCoord && yCoord + dist == yCoord && z == zCoord)
                                && !testForOrActivateRedstone(x, yCoord + dist, z, set, activate)) return false;
                    } else if (block.equals("draconiumBlock")) {
                        if (!(x == xCoord && yCoord + dist == yCoord && z == zCoord)
                                && !testForOrActivateDraconium(x, yCoord + dist, z, set, activate)) return false;
                    } else if (!block.equals("draconiumBlock") && !block.equals("redstone") && !block.equals("air")) {
                        LogHelper.error("Invalid String In Multiblock Structure Code!!!");
                        return false;
                    }
                }
            }
        }
        for (int x = xCoord - size; x <= xCoord + size; x++) {
            for (int z = zCoord - size; z <= zCoord + size; z++) {

                if (x == xCoord - size || x == xCoord + size || z == zCoord - size || z == zCoord + size) {
                    if (block.equals("air")) {
                        if (!(x == xCoord && yCoord - dist == yCoord && z == zCoord)
                                && !isReplacable(x, yCoord - dist, z, set)) return false;
                    } else if (block.equals("redstone")) {
                        if (!(x == xCoord && yCoord - dist == yCoord && z == zCoord)
                                && !testForOrActivateRedstone(x, yCoord - dist, z, set, activate)) return false;
                    } else if (block.equals("draconiumBlock")) {
                        if (!(x == xCoord && yCoord - dist == yCoord && z == zCoord)
                                && !testForOrActivateDraconium(x, yCoord - dist, z, set, activate)) return false;
                    } else if (!block.equals("draconiumBlock") && !block.equals("redstone") && !block.equals("air")) {
                        LogHelper.error("Invalid String In Multiblock Structure Code!!!");
                        return false;
                    }
                }
            }
        }

        for (int y = yCoord - size; y <= yCoord + size; y++) {
            for (int x = xCoord - size; x <= xCoord + size; x++) {

                if (y == yCoord - size || y == yCoord + size || x == xCoord - size || x == xCoord + size) {
                    if (block.equals("air")) {
                        if (!(x == xCoord && y == yCoord && zCoord + dist == zCoord)
                                && !isReplacable(x, y, zCoord + dist, set)) return false;
                    } else if (block.equals("redstone")) {
                        if (!(x == xCoord && y == yCoord && zCoord + dist == zCoord)
                                && !testForOrActivateRedstone(x, y, zCoord + dist, set, activate)) return false;
                    } else if (block.equals("draconiumBlock")) {
                        if (!(x == xCoord && y == yCoord && zCoord + dist == zCoord)
                                && !testForOrActivateDraconium(x, y, zCoord + dist, set, activate)) return false;
                    } else if (!block.equals("draconiumBlock") && !block.equals("redstone") && !block.equals("air")) {
                        LogHelper.error("Invalid String In Multiblock Structure Code!!!");
                        return false;
                    }
                }
            }
        }
        for (int y = yCoord - size; y <= yCoord + size; y++) {
            for (int x = xCoord - size; x <= xCoord + size; x++) {

                if (y == yCoord - size || y == yCoord + size || x == xCoord - size || x == xCoord + size) {
                    if (block.equals("air")) {
                        if (!(x == xCoord && y == yCoord && zCoord - dist == zCoord)
                                && !isReplacable(x, y, zCoord - dist, set)) return false;
                    } else if (block.equals("redstone")) {
                        if (!(x == xCoord && y == yCoord && zCoord - dist == zCoord)
                                && !testForOrActivateRedstone(x, y, zCoord - dist, set, activate)) return false;
                    } else if (block.equals("draconiumBlock")) {
                        if (!(x == xCoord && y == yCoord && zCoord - dist == zCoord)
                                && !testForOrActivateDraconium(x, y, zCoord - dist, set, activate)) return false;
                    } else if (!block.equals("draconiumBlock") && !block.equals("redstone") && !block.equals("air")) {
                        LogHelper.error("Invalid String In Multiblock Structure Code!!!");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean testOrActivateSides(int dist, String block, boolean set, boolean activate) {
        dist++;
        for (int y = yCoord - 1; y <= yCoord + 1; y++) {
            for (int z = zCoord - 1; z <= zCoord + 1; z++) {

                if (block.equals("air")) {
                    if (!(xCoord + dist == xCoord && y == yCoord && z == zCoord)
                            && !isReplacable(xCoord + dist, y, z, set)) return false;
                } else if (block.equals("redstone")) {
                    if (!(xCoord + dist == xCoord && y == yCoord && z == zCoord)
                            && !testForOrActivateRedstone(xCoord + dist, y, z, set, activate)) return false;
                } else if (block.equals("draconiumBlock")) {
                    if (!(xCoord + dist == xCoord && y == yCoord && z == zCoord)
                            && !testForOrActivateDraconium(xCoord + dist, y, z, set, activate)) return false;
                } else if (!block.equals("draconiumBlock") && !block.equals("redstone") && !block.equals("air")) {
                    LogHelper.error("Invalid String In Multiblock Structure Code!!!");
                    return false;
                }
            }
        }
        for (int y = yCoord - 1; y <= yCoord + 1; y++) {
            for (int z = zCoord - 1; z <= zCoord + 1; z++) {

                if (block.equals("air")) {
                    if (!(xCoord - dist == xCoord && y == yCoord && z == zCoord)
                            && !isReplacable(xCoord - dist, y, z, set)) return false;
                } else if (block.equals("redstone")) {
                    if (!(xCoord - dist == xCoord && y == yCoord && z == zCoord)
                            && !testForOrActivateRedstone(xCoord - dist, y, z, set, activate)) return false;
                } else if (block.equals("draconiumBlock")) {
                    if (!(xCoord - dist == xCoord && y == yCoord && z == zCoord)
                            && !testForOrActivateDraconium(xCoord - dist, y, z, set, activate)) return false;
                } else if (!block.equals("draconiumBlock") && !block.equals("redstone") && !block.equals("air")) {
                    LogHelper.error("Invalid String In Multiblock Structure Code!!!");
                    return false;
                }
            }
        }

        for (int x = xCoord - 1; x <= xCoord + 1; x++) {
            for (int z = zCoord - 1; z <= zCoord + 1; z++) {

                if (block.equals("air")) {
                    if (!(x == xCoord && yCoord + dist == yCoord && z == zCoord)
                            && !isReplacable(x, yCoord + dist, z, set)) return false;
                } else if (block.equals("redstone")) {
                    if (!(x == xCoord && yCoord + dist == yCoord && z == zCoord)
                            && !testForOrActivateRedstone(x, yCoord + dist, z, set, activate)) return false;
                } else if (block.equals("draconiumBlock")) {
                    if (!(x == xCoord && yCoord + dist == yCoord && z == zCoord)
                            && !testForOrActivateDraconium(x, yCoord + dist, z, set, activate)) return false;
                } else if (!block.equals("draconiumBlock") && !block.equals("redstone") && !block.equals("air")) {
                    LogHelper.error("Invalid String In Multiblock Structure Code!!!");
                    return false;
                }
            }
        }
        for (int x = xCoord - 1; x <= xCoord + 1; x++) {
            for (int z = zCoord - 1; z <= zCoord + 1; z++) {

                if (block.equals("air")) {
                    if (!(x == xCoord && yCoord - dist == yCoord && z == zCoord)
                            && !isReplacable(x, yCoord - dist, z, set)) return false;
                } else if (block.equals("redstone")) {
                    if (!(x == xCoord && yCoord - dist == yCoord && z == zCoord)
                            && !testForOrActivateRedstone(x, yCoord - dist, z, set, activate)) return false;
                } else if (block.equals("draconiumBlock")) {
                    if (!(x == xCoord && yCoord - dist == yCoord && z == zCoord)
                            && !testForOrActivateDraconium(x, yCoord - dist, z, set, activate)) return false;
                } else if (!block.equals("draconiumBlock") && !block.equals("redstone") && !block.equals("air")) {
                    LogHelper.error("Invalid String In Multiblock Structure Code!!!");
                    return false;
                }
            }
        }

        for (int y = yCoord - 1; y <= yCoord + 1; y++) {
            for (int x = xCoord - 1; x <= xCoord + 1; x++) {

                if (block.equals("air")) {
                    if (!(x == xCoord && y == yCoord && zCoord + dist == zCoord)
                            && !isReplacable(x, y, zCoord + dist, set)) return false;
                } else if (block.equals("redstone")) {
                    if (!(x == xCoord && y == yCoord && zCoord + dist == zCoord)
                            && !testForOrActivateRedstone(x, y, zCoord + dist, set, activate)) return false;
                } else if (block.equals("draconiumBlock")) {
                    if (!(x == xCoord && y == yCoord && zCoord + dist == zCoord)
                            && !testForOrActivateDraconium(x, y, zCoord + dist, set, activate)) return false;
                } else if (!block.equals("draconiumBlock") && !block.equals("redstone") && !block.equals("air")) {
                    LogHelper.error("Invalid String In Multiblock Structure Code!!!");
                    return false;
                }
            }
        }
        for (int y = yCoord - 1; y <= yCoord + 1; y++) {
            for (int x = xCoord - 1; x <= xCoord + 1; x++) {

                if (block.equals("air")) {
                    if (!(x == xCoord && y == yCoord && zCoord - dist == zCoord)
                            && !isReplacable(x, y, zCoord - dist, set)) return false;
                } else if (block.equals("redstone")) {
                    if (!(x == xCoord && y == yCoord && zCoord - dist == zCoord)
                            && !testForOrActivateRedstone(x, y, zCoord - dist, set, activate)) return false;
                } else if (block.equals("draconiumBlock")) {
                    if (!(x == xCoord && y == yCoord && zCoord - dist == zCoord)
                            && !testForOrActivateDraconium(x, y, zCoord - dist, set, activate)) return false;
                } else if (!block.equals("draconiumBlock") && !block.equals("redstone") && !block.equals("air")) {
                    LogHelper.error("Invalid String In Multiblock Structure Code!!!");
                    return false;
                }
            }
        }

        return true;
    }

    private boolean testForOrActivateDraconium(int x, int y, int z, boolean set, boolean activate) {
        if (!activate) {
            if (set) {
                worldObj.setBlock(
                        x,
                        y,
                        z,
                        BalanceConfigHandler.energyStorageStructureOuterBlock,
                        BalanceConfigHandler.energyStorageStructureOuterBlockMetadata,
                        3);
                return true;
            } else
                return (worldObj.getBlock(x, y, z) == BalanceConfigHandler.energyStorageStructureOuterBlock
                                && worldObj.getBlockMetadata(x, y, z)
                                        == BalanceConfigHandler.energyStorageStructureOuterBlockMetadata)
                        || (worldObj.getBlock(x, y, z) == ModBlocks.invisibleMultiblock
                                && worldObj.getBlockMetadata(x, y, z) == 0);
        } else {
            return activateDraconium(x, y, z);
        }
    }

    private boolean testForOrActivateRedstone(int x, int y, int z, boolean set, boolean activate) {
        if (!activate) {
            if (set) {
                worldObj.setBlock(
                        x,
                        y,
                        z,
                        BalanceConfigHandler.energyStorageStructureBlock,
                        BalanceConfigHandler.energyStorageStructureBlockMetadata,
                        3);
                return true;
            } else {
                return (worldObj.getBlock(x, y, z) == BalanceConfigHandler.energyStorageStructureBlock
                                && worldObj.getBlockMetadata(x, y, z)
                                        == BalanceConfigHandler.energyStorageStructureBlockMetadata)
                        || (worldObj.getBlock(x, y, z) == ModBlocks.invisibleMultiblock
                                && worldObj.getBlockMetadata(x, y, z) == 1);
            }
        } else {
            return activateRedstone(x, y, z);
        }
    }

    private boolean activateDraconium(int x, int y, int z) {
        if (testForOrActivateDraconium(x, y, z, false, false)) {
            worldObj.setBlock(x, y, z, ModBlocks.invisibleMultiblock, 0, 2);
            TileInvisibleMultiblock tile = (worldObj.getTileEntity(x, y, z) != null
                            && worldObj.getTileEntity(x, y, z) instanceof TileInvisibleMultiblock)
                    ? (TileInvisibleMultiblock) worldObj.getTileEntity(x, y, z)
                    : null;
            if (tile != null) {
                tile.master = new TileLocation(xCoord, yCoord, zCoord);
            }
            return true;
        }
        LogHelper.error("Failed to activate structure (activateDraconium)");
        return false;
    }

    private boolean activateRedstone(int x, int y, int z) {
        if (testForOrActivateRedstone(x, y, z, false, false)) {
            worldObj.setBlock(x, y, z, ModBlocks.invisibleMultiblock, 1, 2);
            TileInvisibleMultiblock tile = (worldObj.getTileEntity(x, y, z) != null
                            && worldObj.getTileEntity(x, y, z) instanceof TileInvisibleMultiblock)
                    ? (TileInvisibleMultiblock) worldObj.getTileEntity(x, y, z)
                    : null;
            if (tile != null) {
                tile.master = new TileLocation(xCoord, yCoord, zCoord);
            }
            return true;
        }
        LogHelper.error("Failed to activate structure (activateRedstone)");
        return false;
    }

    private boolean isReplacable(int x, int y, int z, boolean set) {
        if (set) {
            worldObj.setBlock(x, y, z, Blocks.air);
            return true;
        } else return worldObj.getBlock(x, y, z).isReplaceable(worldObj, x, y, z) || worldObj.isAirBlock(x, y, z);
    }

    public boolean isOnline() {
        return online;
    }

    private void activateStabilizers() {
        for (int i = 0; i < stabilizers.length; i++) {
            if (stabilizers[i] == null) {
                LogHelper.error("activateStabilizers stabalizers[" + i + "] == null!!!");
                return;
            }
            TileParticleGenerator tile = (worldObj.getTileEntity(
                                            stabilizers[i].getXCoord(),
                                            stabilizers[i].getYCoord(),
                                            stabilizers[i].getZCoord())
                                    != null
                            && worldObj.getTileEntity(
                                            stabilizers[i].getXCoord(),
                                            stabilizers[i].getYCoord(),
                                            stabilizers[i].getZCoord())
                                    instanceof TileParticleGenerator)
                    ? (TileParticleGenerator) worldObj.getTileEntity(
                            stabilizers[i].getXCoord(), stabilizers[i].getYCoord(), stabilizers[i].getZCoord())
                    : null;
            if (tile == null) {
                LogHelper.error("Missing Tile Entity (Particle Generator)");
                return;
            }
            tile.stabalizerMode = true;
            tile.setMaster(new TileLocation(xCoord, yCoord, zCoord));
            worldObj.setBlockMetadataWithNotify(
                    stabilizers[i].getXCoord(), stabilizers[i].getYCoord(), stabilizers[i].getZCoord(), 1, 2);
        }
        initializeCapacity();
    }

    private void initializeCapacity() {
        long capacity = 0;
        switch (tier) {
            case 0:
                capacity = BalanceConfigHandler.energyStorageTier1Storage;
                break;
            case 1:
                capacity = BalanceConfigHandler.energyStorageTier2Storage;
                break;
            case 2:
                capacity = BalanceConfigHandler.energyStorageTier3Storage;
                break;
            case 3:
                capacity = BalanceConfigHandler.energyStorageTier4Storage;
                break;
            case 4:
                capacity = BalanceConfigHandler.energyStorageTier5Storage;
                break;
            case 5:
                capacity = BalanceConfigHandler.energyStorageTier6Storage;
                break;
            case 6:
                capacity = BalanceConfigHandler.energyStorageTier7Storage;
                break;
        }
        this.capacity = capacity;
        if (energy > capacity) energy = capacity;
    }

    public void deactivateStabilizers() {
        for (int i = 0; i < stabilizers.length; i++) {
            if (stabilizers[i] == null) {
                LogHelper.error("activateStabilizers stabalizers[" + i + "] == null!!!");
            } else {
                TileParticleGenerator tile = (worldObj.getTileEntity(
                                                stabilizers[i].getXCoord(),
                                                stabilizers[i].getYCoord(),
                                                stabilizers[i].getZCoord())
                                        != null
                                && worldObj.getTileEntity(
                                                stabilizers[i].getXCoord(),
                                                stabilizers[i].getYCoord(),
                                                stabilizers[i].getZCoord())
                                        instanceof TileParticleGenerator)
                        ? (TileParticleGenerator) worldObj.getTileEntity(
                                stabilizers[i].getXCoord(), stabilizers[i].getYCoord(), stabilizers[i].getZCoord())
                        : null;
                if (tile == null) {
                    // LogHelper.error("Missing Tile Entity (Particle Generator)");
                } else {
                    tile.stabalizerMode = false;
                    worldObj.setBlockMetadataWithNotify(
                            stabilizers[i].getXCoord(), stabilizers[i].getYCoord(), stabilizers[i].getZCoord(), 0, 2);
                }
            }
        }
    }

    private boolean areStabilizersActive() {
        for (int i = 0; i < stabilizers.length; i++) {
            if (stabilizers[i] == null) {
                LogHelper.error("activateStabilizers stabalizers[" + i + "] == null!!!");
                return false;
            }
            TileParticleGenerator tile = (worldObj.getTileEntity(
                                            stabilizers[i].getXCoord(),
                                            stabilizers[i].getYCoord(),
                                            stabilizers[i].getZCoord())
                                    != null
                            && worldObj.getTileEntity(
                                            stabilizers[i].getXCoord(),
                                            stabilizers[i].getYCoord(),
                                            stabilizers[i].getZCoord())
                                    instanceof TileParticleGenerator)
                    ? (TileParticleGenerator) worldObj.getTileEntity(
                            stabilizers[i].getXCoord(), stabilizers[i].getYCoord(), stabilizers[i].getZCoord())
                    : null;
            if (tile == null) {
                // LogHelper.error("Missing Tile Entity (Particle Generator)");
                return false;
            }
            if (!tile.stabalizerMode
                    || worldObj.getBlockMetadata(
                                    stabilizers[i].getXCoord(), stabilizers[i].getYCoord(), stabilizers[i].getZCoord())
                            != 1) return false;
        }
        return true;
    }

    private boolean checkStabilizers() {
        for (int i = 0; i < stabilizers.length; i++) {
            if (stabilizers[i] == null) return false;
            TileParticleGenerator gen = (worldObj.getTileEntity(
                                            stabilizers[i].getXCoord(),
                                            stabilizers[i].getYCoord(),
                                            stabilizers[i].getZCoord())
                                    != null
                            && worldObj.getTileEntity(
                                            stabilizers[i].getXCoord(),
                                            stabilizers[i].getYCoord(),
                                            stabilizers[i].getZCoord())
                                    instanceof TileParticleGenerator)
                    ? (TileParticleGenerator) worldObj.getTileEntity(
                            stabilizers[i].getXCoord(), stabilizers[i].getYCoord(), stabilizers[i].getZCoord())
                    : null;
            if (gen == null || !gen.stabalizerMode) return false;
            if (gen.getMaster().xCoord != xCoord
                    || gen.getMaster().yCoord != yCoord
                    || gen.getMaster().zCoord != zCoord) return false;
        }
        return true;
    }

    public int getTier() {
        return tier;
    }

    /**
     * ###############################################################
     */
    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setBoolean("Online", online);
        compound.setShort("Tier", (short) tier);
        compound.setLong("EnergyL", energy);
        for (int i = 0; i < stabilizers.length; i++) {
            if (stabilizers[i] != null) stabilizers[i].writeToNBT(compound, String.valueOf(i));
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        online = compound.getBoolean("Online");
        tier = (int) compound.getShort("Tier");
        energy = compound.getLong("EnergyL");
        if (compound.hasKey("Energy")) energy = (long) compound.getDouble("Energy");
        for (int i = 0; i < stabilizers.length; i++) {
            if (stabilizers[i] != null) stabilizers[i].readFromNBT(compound, String.valueOf(i));
        }
        initializeCapacity();
        super.readFromNBT(compound);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        writeToNBT(nbttagcompound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbttagcompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
    }

    /* EnergyHandler */

    public int receiveEnergy(int maxReceive, boolean simulate) {
        long energyReceived = Math.min(capacity - energy, maxReceive);

        if (!simulate) {
            energy += energyReceived;
        }
        return (int) energyReceived;
    }

    public int extractEnergy(int maxExtract, boolean simulate) {
        long energyExtracted = Math.min(energy, maxExtract);

        if (!simulate) {
            energy -= energyExtracted;
        }
        return (int) energyExtracted;
    }

    public long getEnergyStored() {
        return energy;
    }

    public long getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 40960.0D;
    }

    private void detectAndRendChanges() {
        if (lastTickCapacity != energy)
            lastTickCapacity = (Long) sendObjectToClient(
                    References.LONG_ID,
                    0,
                    energy,
                    new NetworkRegistry.TargetPoint(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 20));
    }

    @Override
    public void receiveObjectFromServer(int index, Object object) {
        energy = (Long) object;
    }
}
