package com.brandon3055.draconicevolution.common.blocks.multiblock;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Created by Brandon on 25/07/2014.
 */
public class MultiblockHelper {



	public static class TileLocation {
		protected int xCoord;
		protected int yCoord;
		protected int zCoord;

		public TileLocation(){

		}

		public TileLocation(int x, int y, int z){
			this.xCoord = x;
			this.yCoord = y;
			this.zCoord = z;
		}

		public int getXCoord(){
			return xCoord;
		}

		public int getYCoord(){
			return yCoord;
		}

		public int getZCoord(){
			return zCoord;
		}

		public void setXCoord(int x){
			xCoord = x;
		}

		public void setYCoord(int y){
			yCoord = y;
		}

		public void setZCoord(int z){
			zCoord = z;
		}

		public void writeToNBT(NBTTagCompound compound, String key) {
			compound.setInteger("X_"+key, xCoord);
			compound.setInteger("Y_"+key, yCoord);
			compound.setInteger("Z_"+key, zCoord);
		}

		public void readFromNBT(NBTTagCompound compound, String key) {
			xCoord = compound.getInteger("X_"+key);
			yCoord = compound.getInteger("Y_"+key);
			zCoord = compound.getInteger("Z_"+key);
		}
	}

}
