package com.brandon3055.draconicevolution.common.tileentities.multiblocktiles;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import com.brandon3055.draconicevolution.common.blocks.ModBlocks;
import com.brandon3055.draconicevolution.common.blocks.multiblock.MultiblockHelper.TileLocation;
import com.brandon3055.draconicevolution.common.core.utills.LogHelper;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Brandon on 25/07/2014.
 */
public class TileEnergyStorageCore extends TileEntity implements IEnergyHandler {

	protected EnergyStorage storage = new EnergyStorage(1000000, 1000000, 1000000);
	protected TileLocation[] stabilizers = new TileLocation[4];
	protected int tier = 6;
	protected boolean online = false;


	@Override
	public void updateEntity() {
		if (tier != 6)
			LogHelper.info(tier);
	}


	/**
	 * ######################MultiBlock Methods#######################
	 */

	public boolean tryActivate() {
		if (!findStabalyzers()) return false;
		if (!setTier(false)) return false;
		if (!testOrActivateStructureIfValid(false, false)) return false;
		if (!testOrActivateStructureIfValid(false, true)) return false;
		//LogHelper.info(testOrActivateStructureIfValid(false, true));

		return true;
	}

	public boolean isStructureStillValid(){
		return true;
	}

	private boolean findStabalyzers() {
		for (int x = xCoord; x <= xCoord + 11; x++) {
			if (worldObj.getBlock(x, yCoord, zCoord) == ModBlocks.particleGenerator) {
				stabilizers[0] = new TileLocation(x, yCoord, zCoord);
				break;
			} else if (x == xCoord + 11) {
				return false;
			}

		}
		for (int x = xCoord; x >= xCoord - 11; x--) {
			if (worldObj.getBlock(x, yCoord, zCoord) == ModBlocks.particleGenerator) {
				stabilizers[1] = new TileLocation(x, yCoord, zCoord);
				break;
			} else if (x == xCoord - 11) {
				return false;
			}
		}
		for (int z = zCoord; z <= zCoord + 11; z++) {
			if (worldObj.getBlock(xCoord, yCoord, z) == ModBlocks.particleGenerator) {
				stabilizers[2] = new TileLocation(xCoord, yCoord, z);
				break;
			} else if (z == zCoord + 11) {
				return false;
			}
		}
		for (int z = zCoord; z >= zCoord - 11; z--) {
			if (worldObj.getBlock(xCoord, yCoord, z) == ModBlocks.particleGenerator) {
				stabilizers[3] = new TileLocation(xCoord, yCoord, z);
				break;
			} else if (z == zCoord - 11) {
				return false;
			}
		}
		return true;
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
				if (xNeg != xPos) return false;
				break;
			}
		}

		for (int y = 0; y <= range; y++) {
			if (testForOrActivateDraconium(xCoord, yCoord + y, zCoord, false, false)) {
				yPos = y;
				if (yPos != xNeg) return false;
				break;
			}
		}

		for (int y = 0; y <= range; y++) {
			if (testForOrActivateDraconium(xCoord, yCoord - y, zCoord, false, false)) {
				yNeg = y;
				if (yNeg != yPos) return false;
				break;
			}
		}

		for (int z = 0; z <= range; z++) {
			if (testForOrActivateDraconium(xCoord, yCoord, zCoord + z, false, false)) {
				zPos = z;
				if (zPos != yNeg) return false;
				break;
			}
		}

		for (int z = 0; z <= range; z++) {
			if (testForOrActivateDraconium(xCoord, yCoord, zCoord - z, false, false)) {
				zNeg = z;
				if (zNeg != zPos) return false;
				break;
			}
		}

		tier = xPos;
		if (tier > 1) tier++;
		if (tier == 1){
			if (testForOrActivateDraconium(xCoord + 1, yCoord + 1, zCoord, false, false)) tier = 2;
		}
		//LogHelper.info(""+tier);
		return true;
	}

	private boolean testOrActivateStructureIfValid(boolean setBlocks, boolean activate){
		switch (tier){
			case 0:
				if (!testOrActivateRect(1, 1, 1, "air", setBlocks, activate)) return false;
				break;
			case 1:
				if (!testForOrActivateDraconium(xCoord + 1, yCoord, zCoord, setBlocks, activate) || !testForOrActivateDraconium(xCoord - 1, yCoord, zCoord, setBlocks, activate) || !testForOrActivateDraconium(xCoord, yCoord + 1, zCoord, setBlocks, activate) || !testForOrActivateDraconium(xCoord, yCoord - 1, zCoord, setBlocks, activate) || !testForOrActivateDraconium(xCoord, yCoord, zCoord + 1, setBlocks, activate) || !testForOrActivateDraconium(xCoord, yCoord, zCoord - 1, setBlocks, activate)) return false;
				if (!isAir(xCoord+1, yCoord+1, zCoord, setBlocks) || !isAir(xCoord, yCoord+1, zCoord+1, setBlocks) || !isAir(xCoord-1, yCoord+1, zCoord, setBlocks) || !isAir(xCoord, yCoord+1, zCoord-1, setBlocks) || !isAir(xCoord+1, yCoord-1, zCoord, setBlocks) || !isAir(xCoord, yCoord-1, zCoord+1, setBlocks) || !isAir(xCoord-1, yCoord-1, zCoord, setBlocks) || !isAir(xCoord, yCoord-1, zCoord-1, setBlocks) || !isAir(xCoord+1, yCoord, zCoord+1, setBlocks) || !isAir(xCoord-1, yCoord, zCoord-1, setBlocks) || !isAir(xCoord+1, yCoord, zCoord-1, setBlocks) || !isAir(xCoord-1, yCoord, zCoord+1, setBlocks)) return false;
				if (!isAir(xCoord+1, yCoord+1, zCoord+1, setBlocks) || !isAir(xCoord-1, yCoord+1, zCoord-1, setBlocks) || !isAir(xCoord+1, yCoord+1, zCoord-1, setBlocks) || !isAir(xCoord-1, yCoord+1, zCoord+1, setBlocks) || !isAir(xCoord+1, yCoord-1, zCoord+1, setBlocks) || !isAir(xCoord-1, yCoord-1, zCoord-1, setBlocks) || !isAir(xCoord+1, yCoord-1, zCoord-1, setBlocks) || !isAir(xCoord-1, yCoord-1, zCoord+1, setBlocks)) return false;
				break;
			case 2:
				if (!testOrActivateRect(1, 1, 1, "draconium", setBlocks, activate)) return false;
				break;
			case 3:
				if (!testOrActivateSides(1, "draconium", setBlocks, activate)) return false;
				if (!testOrActivateRect(1, 1, 1, "redstone", setBlocks, activate)) return false;
				break;
			case 4:
				if (!testOrActivateSides(2, "draconium", setBlocks, activate)) return false;
				if (!testOrActivateRect(2, 1, 1, "redstone", setBlocks, activate)) return false;
				if (!testOrActivateRect(1, 2, 1, "redstone", setBlocks, activate)) return false;
				if (!testOrActivateRect(1, 1, 2, "redstone", setBlocks, activate)) return false;
				if (!testOrActivateRings(2, 2, "draconium", setBlocks, activate)) return false;
				break;
			case 5:
				if (!testOrActivateSides(3, "draconium", setBlocks, activate)) return false;
				if (!testOrActivateSides(2, "redstone", setBlocks, activate)) return false;
				if (!testOrActivateRect(2, 2, 2, "redstone", setBlocks, activate)) return false;
				if (!testOrActivateRings(2, 3, "draconium", setBlocks, activate)) return false;
				break;
			case 6:
				if (!testOrActivateSides(4, "draconium", setBlocks, activate)) return false;
				if (!testOrActivateSides(3, "redstone", setBlocks, activate)) return false;
				if (!testOrActivateRect(3, 2, 2, "redstone", setBlocks, activate)) return false;
				if (!testOrActivateRect(2, 3, 2, "redstone", setBlocks, activate)) return false;
				if (!testOrActivateRect(2, 2, 3, "redstone", setBlocks, activate)) return false;
				if (!testOrActivateRings(2, 4, "draconium", setBlocks, activate)) return false;
				if (!testOrActivateRings(3, 3, "draconium", setBlocks, activate)) return false;
				break;
		}
		return true;
	}

	private boolean testOrActivateRect(int xDim, int yDim, int zDim, String block, boolean set, boolean activate){
		for (int x = xCoord - xDim; x <= xCoord + xDim; x++){
			for (int y = yCoord - yDim; y <= yCoord + yDim; y++){
				for (int z = zCoord - zDim; z <= zCoord + zDim; z++){

					if (block.equals("air")){
						if (!(x == xCoord && y == yCoord && z == zCoord) && !isAir(x, y, z, set)) return false;
					}
					else if (block.equals("redstone")){
						if (!(x == xCoord && y == yCoord && z == zCoord) && !testForOrActivateRedstone(x, y, z, set, activate)) return false;
					}
					else if (block.equals("draconium")){
						if (!(x == xCoord && y == yCoord && z == zCoord) && !testForOrActivateDraconium(x, y, z, set, activate)) return false;
					}
					else if (!block.equals("draconium") && !block.equals("redstone") && !block.equals("air")) {
						LogHelper.error("Invalid String In Multiblock Structure Code!!!");
						return false;
					}
				}
			}
		}
		return true;
	}

	private boolean testOrActivateRings(int size, int dist, String block, boolean set, boolean activate){
		for (int y=yCoord-size; y<=yCoord+size;y++){
			for (int z=zCoord-size; z<=zCoord+size;z++){

				if (y == yCoord-size || y == yCoord+size || z == zCoord-size || z==zCoord+size) {
					if (block.equals("air")) {
						if (!(xCoord + dist == xCoord && y == yCoord && z == zCoord) && !isAir(xCoord + dist, y, z, set))
							return false;
					} else if (block.equals("redstone")) {
						if (!(xCoord + dist == xCoord && y == yCoord && z == zCoord) && !testForOrActivateRedstone(xCoord + dist, y, z, set, activate))
							return false;
					} else if (block.equals("draconium")) {
						if (!(xCoord + dist == xCoord && y == yCoord && z == zCoord) && !testForOrActivateDraconium(xCoord + dist, y, z, set, activate))
							return false;
					} else if (!block.equals("draconium") && !block.equals("redstone") && !block.equals("air")) {
						LogHelper.error("Invalid String In Multiblock Structure Code!!!");
						return false;
					}
				}
			}
		}
		for (int y=yCoord-size; y<=yCoord+size;y++){
			for (int z=zCoord-size; z<=zCoord+size;z++){

				if (y == yCoord-size || y == yCoord+size || z == zCoord-size || z==zCoord+size) {
					if (block.equals("air")) {
						if (!(xCoord - dist == xCoord && y == yCoord && z == zCoord) && !isAir(xCoord - dist, y, z, set))
							return false;
					} else if (block.equals("redstone")) {
						if (!(xCoord - dist == xCoord && y == yCoord && z == zCoord) && !testForOrActivateRedstone(xCoord - dist, y, z, set, activate))
							return false;
					} else if (block.equals("draconium")) {
						if (!(xCoord - dist == xCoord && y == yCoord && z == zCoord) && !testForOrActivateDraconium(xCoord - dist, y, z, set, activate))
							return false;
					} else if (!block.equals("draconium") && !block.equals("redstone") && !block.equals("air")) {
						LogHelper.error("Invalid String In Multiblock Structure Code!!!");
						return false;
					}
				}
			}
		}

		for (int x=xCoord-size; x<=xCoord+size;x++){
			for (int z=zCoord-size; z<=zCoord+size;z++){

				if (x == xCoord-size || x == xCoord+size || z == zCoord-size || z == zCoord+size) {
					if (block.equals("air")){
						if (!(x == xCoord && yCoord + dist == yCoord && z == zCoord) && !isAir(x, yCoord + dist, z, set)) return false;
					}
					else if (block.equals("redstone")){
						if (!(x == xCoord && yCoord + dist == yCoord && z == zCoord) && !testForOrActivateRedstone(x, yCoord + dist, z, set, activate)) return false;
					}
					else if (block.equals("draconium")){
						if (!(x == xCoord && yCoord + dist == yCoord && z == zCoord) && !testForOrActivateDraconium(x, yCoord + dist, z, set, activate)) return false;
					}
					else if (!block.equals("draconium") && !block.equals("redstone") && !block.equals("air")) {
						LogHelper.error("Invalid String In Multiblock Structure Code!!!");
						return false;
					}
				}
			}
		}
		for (int x=xCoord-size; x<=xCoord+size;x++){
			for (int z=zCoord-size; z<=zCoord+size;z++){

				if (x == xCoord-size || x == xCoord+size || z == zCoord-size || z==zCoord+size) {
					if (block.equals("air")){
						if (!(x == xCoord && yCoord - dist == yCoord && z == zCoord) && !isAir(x, yCoord - dist, z, set)) return false;
					}
					else if (block.equals("redstone")){
						if (!(x == xCoord && yCoord - dist == yCoord && z == zCoord) && !testForOrActivateRedstone(x, yCoord - dist, z, set, activate)) return false;
					}
					else if (block.equals("draconium")){
						if (!(x == xCoord && yCoord - dist == yCoord && z == zCoord) && !testForOrActivateDraconium(x, yCoord - dist, z, set, activate)) return false;
					}
					else if (!block.equals("draconium") && !block.equals("redstone") && !block.equals("air")) {
						LogHelper.error("Invalid String In Multiblock Structure Code!!!");
						return false;
					}
				}
			}
		}

		for (int y=yCoord-size; y<=yCoord+size;y++){
			for (int x=xCoord-size; x<=xCoord+size;x++){

				if (y == yCoord-size || y == yCoord+size || x == xCoord-size || x == xCoord+size) {
					if (block.equals("air")){
						if (!(x == xCoord && y == yCoord && zCoord + dist == zCoord) && !isAir(x, y, zCoord + dist, set)) return false;
					}
					else if (block.equals("redstone")){
						if (!(x == xCoord && y == yCoord && zCoord + dist == zCoord) && !testForOrActivateRedstone(x, y, zCoord + dist, set, activate)) return false;
					}
					else if (block.equals("draconium")){
						if (!(x == xCoord && y == yCoord && zCoord + dist == zCoord) && !testForOrActivateDraconium(x, y, zCoord + dist, set, activate)) return false;
					}
					else if (!block.equals("draconium") && !block.equals("redstone") && !block.equals("air")) {
						LogHelper.error("Invalid String In Multiblock Structure Code!!!");
						return false;
					}
				}
			}
		}
		for (int y=yCoord-size; y<=yCoord+size;y++){
			for (int x=xCoord-size; x<=xCoord+size;x++){

				if (y == yCoord-size || y == yCoord+size || x == xCoord-size || x==xCoord+size) {
					if (block.equals("air")){
						if (!(x == xCoord && y == yCoord && zCoord - dist == zCoord) && !isAir(x, y, zCoord - dist, set)) return false;
					}
					else if (block.equals("redstone")){
						if (!(x == xCoord && y == yCoord && zCoord - dist == zCoord) && !testForOrActivateRedstone(x, y, zCoord - dist, set, activate)) return false;
					}
					else if (block.equals("draconium")){
						if (!(x == xCoord && y == yCoord && zCoord - dist == zCoord) && !testForOrActivateDraconium(x, y, zCoord - dist, set, activate)) return false;
					}
					else if (!block.equals("draconium") && !block.equals("redstone") && !block.equals("air")) {
						LogHelper.error("Invalid String In Multiblock Structure Code!!!");
						return false;
					}
				}
			}
		}
		return true;
	}

	private boolean testOrActivateSides(int dist, String block, boolean set, boolean activate){
		dist++;
		for (int y=yCoord-1; y<=yCoord+1;y++){
			for (int z=zCoord-1; z<=zCoord+1;z++){

				if (block.equals("air")){
					if (!(xCoord + dist == xCoord && y == yCoord && z == zCoord) && !isAir(xCoord + dist, y, z, set)) return false;
				}
				else if (block.equals("redstone")){
					if (!(xCoord + dist == xCoord && y == yCoord && z == zCoord) && !testForOrActivateRedstone(xCoord + dist, y, z, set, activate)) return false;
				}
				else if (block.equals("draconium")){
					if (!(xCoord + dist == xCoord && y == yCoord && z == zCoord) && !testForOrActivateDraconium(xCoord + dist, y, z, set, activate)) return false;
				}
				else if (!block.equals("draconium") && !block.equals("redstone") && !block.equals("air")) {
					LogHelper.error("Invalid String In Multiblock Structure Code!!!");
					return false;
				}
			}
		}
		for (int y=yCoord-1; y<=yCoord+1;y++){
			for (int z=zCoord-1; z<=zCoord+1;z++){

				if (block.equals("air")){
					if (!(xCoord - dist == xCoord && y == yCoord && z == zCoord) && !isAir(xCoord - dist, y, z, set)) return false;
				}
				else if (block.equals("redstone")){
					if (!(xCoord - dist == xCoord && y == yCoord && z == zCoord) && !testForOrActivateRedstone(xCoord - dist, y, z, set, activate)) return false;
				}
				else if (block.equals("draconium")){
					if (!(xCoord - dist == xCoord && y == yCoord && z == zCoord) && !testForOrActivateDraconium(xCoord - dist, y, z, set, activate)) return false;
				}
				else if (!block.equals("draconium") && !block.equals("redstone") && !block.equals("air")) {
					LogHelper.error("Invalid String In Multiblock Structure Code!!!");
					return false;
				}
			}
		}

		for (int x=xCoord-1; x<=xCoord+1;x++){
			for (int z=zCoord-1; z<=zCoord+1;z++){

				if (block.equals("air")){
					if (!(x == xCoord && yCoord + dist == yCoord && z == zCoord) && !isAir(x, yCoord + dist, z, set)) return false;
				}
				else if (block.equals("redstone")){
					if (!(x == xCoord && yCoord + dist == yCoord && z == zCoord) && !testForOrActivateRedstone(x, yCoord + dist, z, set, activate)) return false;
				}
				else if (block.equals("draconium")){
					if (!(x == xCoord && yCoord + dist == yCoord && z == zCoord) && !testForOrActivateDraconium(x, yCoord + dist, z, set, activate)) return false;
				}
				else if (!block.equals("draconium") && !block.equals("redstone") && !block.equals("air")) {
					LogHelper.error("Invalid String In Multiblock Structure Code!!!");
					return false;
				}
			}
		}
		for (int x=xCoord-1; x<=xCoord+1;x++){
			for (int z=zCoord-1; z<=zCoord+1;z++){

				if (block.equals("air")){
					if (!(x == xCoord && yCoord - dist == yCoord && z == zCoord) && !isAir(x, yCoord - dist, z, set)) return false;
				}
				else if (block.equals("redstone")){
					if (!(x == xCoord && yCoord - dist == yCoord && z == zCoord) && !testForOrActivateRedstone(x, yCoord - dist, z, set, activate)) return false;
				}
				else if (block.equals("draconium")){
					if (!(x == xCoord && yCoord - dist == yCoord && z == zCoord) && !testForOrActivateDraconium(x, yCoord - dist, z, set, activate)) return false;
				}
				else if (!block.equals("draconium") && !block.equals("redstone") && !block.equals("air")) {
					LogHelper.error("Invalid String In Multiblock Structure Code!!!");
					return false;
				}
			}
		}

		for (int y=yCoord-1; y<=yCoord+1;y++){
			for (int x=xCoord-1; x<=xCoord+1;x++){

				if (block.equals("air")){
					if (!(x == xCoord && y == yCoord && zCoord + dist == zCoord) && !isAir(x, y, zCoord + dist, set)) return false;
				}
				else if (block.equals("redstone")){
					if (!(x == xCoord && y == yCoord && zCoord + dist == zCoord) && !testForOrActivateRedstone(x, y, zCoord + dist, set, activate)) return false;
				}
				else if (block.equals("draconium")){
					if (!(x == xCoord && y == yCoord && zCoord + dist == zCoord) && !testForOrActivateDraconium(x, y, zCoord + dist, set, activate)) return false;
				}
				else if (!block.equals("draconium") && !block.equals("redstone") && !block.equals("air")) {
					LogHelper.error("Invalid String In Multiblock Structure Code!!!");
					return false;
				}
			}
		}
		for (int y=yCoord-1; y<=yCoord+1;y++){
			for (int x=xCoord-1; x<=xCoord+1;x++){

				if (block.equals("air")){
					if (!(x == xCoord && y == yCoord && zCoord - dist == zCoord) && !isAir(x, y, zCoord - dist, set)) return false;
				}
				else if (block.equals("redstone")){
					if (!(x == xCoord && y == yCoord && zCoord - dist == zCoord) && !testForOrActivateRedstone(x, y, zCoord - dist, set, activate)) return false;
				}
				else if (block.equals("draconium")){
					if (!(x == xCoord && y == yCoord && zCoord - dist == zCoord) && !testForOrActivateDraconium(x, y, zCoord - dist, set, activate)) return false;
				}
				else if (!block.equals("draconium") && !block.equals("redstone") && !block.equals("air")) {
					LogHelper.error("Invalid String In Multiblock Structure Code!!!");
					return false;
				}
			}
		}

		return true;
	}

	private boolean testForOrActivateDraconium(int x, int y, int z, boolean set, boolean activate){
		if (!activate) {
			if (set) {
				worldObj.setBlock(x, y, z, ModBlocks.draconium);
				return true;
			} else return worldObj.getBlock(x, y, z) == ModBlocks.draconium || (worldObj.getBlock(x, y, z) == ModBlocks.invisibleMultiblock && worldObj.getBlockMetadata(x, y, z) == 0);
		}else{
			return activateDraconium(x, y, z);
		}
	}

	private boolean testForOrActivateRedstone(int x, int y, int z, boolean set, boolean activate){
		if (!activate) {
			if (set) {
				worldObj.setBlock(x, y, z, Blocks.redstone_block);
				return true;
			} else return worldObj.getBlock(x, y, z) == Blocks.redstone_block || (worldObj.getBlock(x, y, z) == ModBlocks.invisibleMultiblock && worldObj.getBlockMetadata(x, y, z) == 1);
		}else{
			return activateRedstone(x, y, z);
		}
	}

	private boolean activateDraconium(int x, int y, int z){
		if (testForOrActivateDraconium(x, y, z, false, false)){
			worldObj.setBlock(x, y, z, ModBlocks.invisibleMultiblock, 0, 2);
			TileInvisibleMultiblock tile = (worldObj.getTileEntity(x, y, z) != null && worldObj.getTileEntity(x, y, z) instanceof TileInvisibleMultiblock) ? (TileInvisibleMultiblock) worldObj.getTileEntity(x, y, z) : null;
			if (tile != null) {
				tile.master = new TileLocation(xCoord, yCoord, zCoord);
			}
			return true;
		}
		LogHelper.error("Failed to activate structure (activateDraconium)");
		return false;
	}

	private boolean activateRedstone(int x, int y, int z){
		if (testForOrActivateRedstone(x, y, z, false, false)){
			worldObj.setBlock(x, y, z, ModBlocks.invisibleMultiblock, 1, 2);
			TileInvisibleMultiblock tile = (worldObj.getTileEntity(x, y, z) != null && worldObj.getTileEntity(x, y, z) instanceof TileInvisibleMultiblock) ? (TileInvisibleMultiblock) worldObj.getTileEntity(x, y, z) : null;
			if (tile != null) {
				tile.master = new TileLocation(xCoord, yCoord, zCoord);
			}
			return true;
		}
		LogHelper.error("Failed to activate structure (activateRedstone)");
		return false;
	}

	private boolean isAir(int x, int y, int z, boolean set){
		if (set){
			worldObj.setBlock(x, y, z, Blocks.air);
			return true;
		}else
			return worldObj.getBlock(x, y, z) == Blocks.air;
	}

	public boolean isOnline(){
		return online;
	}

	/**
	 * ###############################################################
	 */


	@Override
	public void writeToNBT(NBTTagCompound compound) {
		storage.writeToNBT(compound);
		super.writeToNBT(compound);
		for (int i = 0; i < stabilizers.length; i++){
			if (stabilizers[i] != null)
				stabilizers[i].writeToNBT(compound, String.valueOf(i));
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		storage.readFromNBT(compound);
		super.readFromNBT(compound);
		for (int i = 0; i < stabilizers.length; i++){
			if (stabilizers[i] != null)
				stabilizers[i].readFromNBT(compound, String.valueOf(i));
		}
	}

	@Override
	public Packet getDescriptionPacket(){
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		writeToNBT(nbttagcompound);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbttagcompound);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT(pkt.func_148857_g());
	}

	/* IEnergyHandler */
	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return true;
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {

		return storage.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		return storage.extractEnergy(maxExtract, simulate);
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return storage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return storage.getMaxEnergyStored();
	}
}
