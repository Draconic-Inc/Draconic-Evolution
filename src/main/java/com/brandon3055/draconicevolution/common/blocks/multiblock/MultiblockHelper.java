package com.brandon3055.draconicevolution.common.blocks.multiblock;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

/**
 * Created by Brandon on 25/07/2014.
 */
public class MultiblockHelper {



	public static class TileLocation extends ChunkCoordinates {
		public TileLocation(){

		}

		public TileLocation(int x, int y, int z){
			this.posX = x;
			this.posY = y;
			this.posZ = z;
		}

		public int getXCoord(){
			return posX;
		}

		public int getYCoord(){
			return posY;
		}

		public int getZCoord(){
			return posY;
		}

		public void setXCoord(int x){
			posX = x;
		}

		public void setYCoord(int y){
			posY = y;
		}

		public void setZCoord(int z){
			posZ = z;
		}

		public TileEntity getTileEntity(World world) { return world.getTileEntity(posX, posY, posZ); }

		public void writeToNBT(NBTTagCompound compound, String key) {
			compound.setInteger("X_"+key, posX);
			compound.setInteger("Y_"+key, posY);
			compound.setInteger("Z_"+key, posZ);
		}

		public void readFromNBT(NBTTagCompound compound, String key) {
			posX = compound.getInteger("X_"+key);
			posY = compound.getInteger("Y_"+key);
			posZ = compound.getInteger("Z_"+key);
		}
	}

}
