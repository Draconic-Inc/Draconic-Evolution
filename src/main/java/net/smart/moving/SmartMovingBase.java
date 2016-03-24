// ==================================================================
// This file is part of Smart Moving.
//
// Smart Moving is free software: you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation, either version 3 of the
// License, or (at your option) any later version.
//
// Smart Moving is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Smart Moving. If not, see <http://www.gnu.org/licenses/>.
// ==================================================================

package net.smart.moving;

import java.util.*;

import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.client.*;
import net.minecraft.client.entity.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.smart.moving.config.*;

public abstract class SmartMovingBase extends SmartMovingContext
{
	public final EntityPlayer sp;
	public final EntityPlayerSP esp;
	public final IEntityPlayerSP isp;

	public SmartMovingBase(EntityPlayer sp, IEntityPlayerSP isp)
	{
		this.sp = sp;
		this.isp = isp;

		if (sp instanceof EntityPlayerSP)
		{
			esp = (EntityPlayerSP) sp;
			if(Minecraft.getMinecraft().thePlayer == null)
			{
				Options.resetForNewGame();
				Config = Options;
			}
		}
		else
			esp = null;
	}

	protected void moveFlying(float moveUpward, float moveStrafing, float moveForward, float speedFactor, boolean treeDimensional)
	{
		float diffMotionXStrafing = 0, diffMotionXForward = 0, diffMotionZStrafing = 0, diffMotionZForward = 0;
		{
			float total = MathHelper.sqrt_float(moveStrafing * moveStrafing + moveForward * moveForward);
			if(total >= 0.01F)
			{
				if(total < 1.0F)
					total = 1.0F;

				float moveStrafingFactor = moveStrafing / total;
				float moveForwardFactor = moveForward / total;
				float sin = MathHelper.sin((sp.rotationYaw * 3.141593F) / 180F);
				float cos = MathHelper.cos((sp.rotationYaw * 3.141593F) / 180F);
				diffMotionXStrafing = moveStrafingFactor * cos;
				diffMotionXForward = -moveForwardFactor * sin;
				diffMotionZStrafing = moveStrafingFactor * sin;
				diffMotionZForward = moveForwardFactor * cos;
			}
		}

		float rotation = treeDimensional ? sp.rotationPitch / RadiantToAngle : 0;
		float divingHorizontalFactor = MathHelper.cos(rotation);
		float divingVerticalFactor = -MathHelper.sin(rotation) * Math.signum(moveForward);

		float diffMotionX = diffMotionXForward * divingHorizontalFactor + diffMotionXStrafing;
		float diffMotionY = MathHelper.sqrt_float(diffMotionXForward * diffMotionXForward + diffMotionZForward * diffMotionZForward) * divingVerticalFactor + moveUpward;
		float diffMotionZ = diffMotionZForward * divingHorizontalFactor + diffMotionZStrafing;

		float total = MathHelper.sqrt_float(MathHelper.sqrt_float(diffMotionX * diffMotionX + diffMotionZ * diffMotionZ) + diffMotionY * diffMotionY);
		if(total > 0.01F)
		{
			float factor = speedFactor / total;
			sp.motionX += diffMotionX * factor;
			sp.motionY += diffMotionY * factor;
			sp.motionZ += diffMotionZ * factor;
		}
	}

	protected Block supportsCeilingClimbing(int i, int j, int k)
	{
		Block block = sp.worldObj.getBlock(i, j, k);
		if(block == null)
			return null;

		Dictionary<Object,Set<Integer>> configuration = Config._ceilingClimbConfigurationObject.value;
		Set<Integer> metaDatas = configuration.get(block);
		if(metaDatas == null)
		{
			String blockName = block.getUnlocalizedName();
			if (blockName != null && !blockName.isEmpty())
			{
				metaDatas = configuration.get(blockName);
				if(metaDatas == null && blockName.startsWith("tile.") && blockName.length() > 5)
					metaDatas = configuration.get(blockName.substring(5));
			}
		}

		if(metaDatas == null)
			return null;
		if(metaDatas.isEmpty())
			return block;
		if(metaDatas.contains(sp.worldObj.getBlockMetadata(i, j, k)))
			return block;
		return null;
	}

	@SuppressWarnings("static-method")
	protected boolean isLava(Block block)
	{
		if(block == Block.getBlockFromName("lava") || block == Block.getBlockFromName("flowing_lava"))
			return true;
		return block != null && block.getMaterial() == Material.lava;
	}

	protected float getLiquidBorder(int i, int j, int k)
	{
		float finiteLiquidBorder;
		Block block = sp.worldObj.getBlock(i, j, k);
		if(block == Block.getBlockFromName("water") || block == Block.getBlockFromName("flowing_water"))
			return getNormalWaterBorder(i, j, k);
		if(SmartMovingOptions.hasFiniteLiquid && (finiteLiquidBorder = getFiniteLiquidWaterBorder(i, j, k, block)) > 0)
			return finiteLiquidBorder;
		if(block == Block.getBlockFromName("lava") || block == Block.getBlockFromName("flowing_lava"))
			return Config._lavaLikeWater.value ? getNormalWaterBorder(i, j, k) : 0F;

		Material material = sp.worldObj.getBlock(i, j, k).getMaterial();
		if(material == null || material == Material.lava)
			return Config._lavaLikeWater.value ? 1F : 0F;
		if(material == Material.water)
			return getNormalWaterBorder(i, j, k);
		if(material.isLiquid())
			return 1F;

		return 0F;
	}

	protected float getNormalWaterBorder(int i, int j, int k)
	{
		int blockMetaData = sp.worldObj.getBlockMetadata(i, j, k);
		if(blockMetaData >= 8)
			return 1F;
		if(blockMetaData == 0)
			if(sp.worldObj.isAirBlock(i, j + 1, k))
				return 0.8875F;
			else
				return 1F;
		return (8 - blockMetaData) / 8F;
	}

	protected float getFiniteLiquidWaterBorder(int i, int j, int k, Block block)
	{
		int type;
		if((type = Orientation.getFiniteLiquidWater(block)) > 0)
		{
			if(type == 2)
				return 1F;
			if(type == 1)
			{
				Block aboveBlock = sp.worldObj.getBlock(i, j + 1, k);
				if(Orientation.getFiniteLiquidWater(aboveBlock) > 0)
					return 1F;
				return (sp.worldObj.getBlockMetadata(i, j, k) + 1) / 16F;
			}
		}
		return 0F;
	}

	public boolean isFacedToLadder(boolean isSmall)
	{
		return getOnLadder(1, true, isSmall) > 0;
	}

	public boolean isFacedToSolidVine(boolean isSmall)
	{
		return getOnVine(1, true, isSmall) > 0;
	}

	public boolean isOnLadderOrVine(boolean isSmall)
	{
		return getOnLadderOrVine(1, false, isSmall) > 0;
	}

	public boolean isOnVine(boolean isSmall)
	{
		return getOnLadderOrVine(1, false, false, true, isSmall) > 0;
	}

	public boolean isOnLadder(boolean isSmall)
	{
		return getOnLadderOrVine(1, false, true, false, isSmall) > 0;
	}

	protected int getOnLadder(int maxResult, boolean faceOnly, boolean isSmall)
	{
		return getOnLadderOrVine(maxResult, faceOnly, true, false, isSmall);
	}

	protected int getOnVine(int maxResult, boolean faceOnly, boolean isSmall)
	{
		return getOnLadderOrVine(maxResult, faceOnly, false, true, isSmall);
	}

	protected int getOnLadderOrVine(int maxResult, boolean faceOnly, boolean isSmall)
	{
		return getOnLadderOrVine(maxResult, faceOnly, true, true, isSmall);
	}

	protected int getOnLadderOrVine(int maxResult, boolean faceOnly, boolean ladder, boolean vine, boolean isSmall)
	{
		int i = MathHelper.floor_double(sp.posX);
		int minj = MathHelper.floor_double(sp.boundingBox.minY);
		int k = MathHelper.floor_double(sp.posZ);

		if(Config.isStandardBaseClimb())
		{
			Block block = sp.worldObj.getBlock(i, minj, k);
			if(ladder)
				if(vine)
					return Orientation.isClimbable(sp.worldObj, i, minj, k) ? 1 : 0;
				else
					return block != Block.getBlockFromName("vine") && Orientation.isClimbable(sp.worldObj, i, minj, k) ? 1 : 0;
			else
				if(vine)
					return block == Block.getBlockFromName("vine") && Orientation.isClimbable(sp.worldObj, i, minj, k) ? 1 : 0;
				else
					return 0;
		}
		else
		{
			if(isSmall)
				minj--;

			HashSet<Orientation> facedOnlyTo = null;
			if(faceOnly)
				facedOnlyTo = Orientation.getClimbingOrientations(sp, true, false);

			int result = 0;
			int maxj = MathHelper.floor_double(sp.boundingBox.minY + Math.ceil(sp.boundingBox.maxY - sp.boundingBox.minY)) - 1;
			for(int j = minj; j <= maxj; j++)
			{
				Block block = sp.worldObj.getBlock(i, j, k);
				if(ladder)
				{
					boolean localLadder = Orientation.isKnownLadder(block);
					Orientation localLadderOrientation = null;
					if(localLadder)
					{
						localLadderOrientation = Orientation.getKnownLadderOrientation(sp.worldObj, i, j, k);
						if(facedOnlyTo == null || facedOnlyTo.contains(localLadderOrientation))
							result++;
					}

					for(Orientation direction : facedOnlyTo != null ? facedOnlyTo : Orientation.Orthogonals)
					{
						if(result >= maxResult)
							return result;

						if(direction != localLadderOrientation)
						{
							Block remoteBlock = sp.worldObj.getBlock(i + direction._i, j, k + direction._k);
							if(Orientation.isKnownLadder(remoteBlock))
							{
								Orientation remoteLadderOrientation = Orientation.getKnownLadderOrientation(sp.worldObj, i + direction._i, j, k + direction._k);
								if(remoteLadderOrientation.rotate(180) == direction)
									result++;
							}
						}
					}
				}

				if(result >= maxResult)
					return result;

				if(vine && Orientation.isVine(block))
					if(facedOnlyTo == null)
						result++;
					else
					{
						Iterator<Orientation> iterator = facedOnlyTo.iterator();
						while(iterator.hasNext())
						{
							Orientation climbOrientation = iterator.next();
							if(climbOrientation.hasVineOrientation(sp.worldObj, i, j, k) && climbOrientation.isRemoteSolid(sp.worldObj, i, j, k))
							{
								result++;
								break;
							}
						}
					}

				if(result >= maxResult)
					return result;
			}
			return result;
		}
	}

	public boolean climbingUpIsBlockedByLadder()
	{
		if(sp.isCollidedHorizontally && sp.isCollidedVertically && !sp.onGround && esp.movementInput.moveForward > 0F)
		{
			Orientation orientation = Orientation.getOrientation(sp, 20F, true, false);
			if(orientation != null)
			{
				int i = MathHelper.floor_double(sp.posX);
				int j = MathHelper.floor_double(sp.boundingBox.maxY);
				int k = MathHelper.floor_double(sp.posZ);
				if(Orientation.isLadder(sp.worldObj.getBlock(i, j, k)))
					return Orientation.getKnownLadderOrientation(sp.worldObj, i, j, k) == orientation;
			}
		}
		return false;
	}

	public boolean climbingUpIsBlockedByTrapDoor()
	{
		if(sp.isCollidedHorizontally && sp.isCollidedVertically && !sp.onGround && esp.movementInput.moveForward > 0F)
		{
			Orientation orientation = Orientation.getOrientation(sp, 20F, true, false);
			if(orientation != null)
			{
				int i = MathHelper.floor_double(sp.posX);
				int j = MathHelper.floor_double(sp.boundingBox.maxY);
				int k = MathHelper.floor_double(sp.posZ);
				if(Orientation.isTrapDoor(sp.worldObj.getBlock(i, j, k)))
					return Orientation.getOpenTrapDoorOrientation(sp.worldObj, i, j, k) == orientation;
			}
		}
		return false;
	}

	public boolean climbingUpIsBlockedByCobbleStoneWall()
	{
		if(sp.isCollidedHorizontally && sp.isCollidedVertically && !sp.onGround && esp.movementInput.moveForward > 0F)
		{
			Orientation orientation = Orientation.getOrientation(sp, 20F, true, false);
			if(orientation != null)
			{
				int i = MathHelper.floor_double(sp.posX);
				int j = MathHelper.floor_double(sp.boundingBox.maxY);
				int k = MathHelper.floor_double(sp.posZ);
				if(sp.worldObj.getBlock(i, j, k) == Block.getBlockFromName("cobblestone_wall"))
					return !((BlockWall)Block.getBlockFromName("cobblestone_wall")).canConnectWallTo(sp.worldObj, i - orientation._i, j, k - orientation._k);
			}
		}
		return false;
	}

	private List<?> getPlayerSolidBetween(double yMin, double yMax, double horizontalTolerance)
	{
		double minY = sp.boundingBox.minY;
		double maxY = sp.boundingBox.maxY;
		sp.boundingBox.minY = yMin;
		sp.boundingBox.maxY = yMax;

		List<?> result = sp.worldObj.getCollidingBoundingBoxes(sp, horizontalTolerance == 0 ? sp.boundingBox : sp.boundingBox.contract(-horizontalTolerance, 0, -horizontalTolerance));

		sp.boundingBox.minY = minY;
		sp.boundingBox.maxY = maxY;

		return result;
	}

	protected boolean isPlayerInSolidBetween(double yMin, double yMax)
	{
		return getPlayerSolidBetween(yMin, yMax, 0).size() > 0;
	}

	protected double getMaxPlayerSolidBetween(double yMin, double yMax, double horizontalTolerance)
	{
		List<?> solids = getPlayerSolidBetween(yMin, yMax, horizontalTolerance);
		double result = yMin;
		for(int i = 0; i < solids.size(); i++)
		{
			AxisAlignedBB box = (AxisAlignedBB)solids.get(i);
			if(isCollided(box, yMin, yMax, horizontalTolerance))
				result = Math.max(result, box.maxY);
		}
		return Math.min(result, yMax);
	}

	protected double getMinPlayerSolidBetween(double yMin, double yMax, double horizontalTolerance)
	{
		List<?> solids = getPlayerSolidBetween(yMin, yMax, horizontalTolerance);
		double result = yMax;
		for(int i = 0; i < solids.size(); i++)
		{
			AxisAlignedBB box = (AxisAlignedBB)solids.get(i);
			if(isCollided(box, yMin, yMax, horizontalTolerance))
				result = Math.min(result, box.minY);
		}
		return Math.max(result, yMin);
	}

	protected boolean isInLiquid()
	{
		return
			getMaxPlayerLiquidBetween(sp.boundingBox.minY, sp.boundingBox.maxY) != sp.boundingBox.minY ||
			getMinPlayerLiquidBetween(sp.boundingBox.minY, sp.boundingBox.maxY) != sp.boundingBox.maxY;
	}

	protected double getMaxPlayerLiquidBetween(double yMin, double yMax)
	{
		int i = MathHelper.floor_double(sp.posX);
		int jMin = MathHelper.floor_double(yMin);
		int jMax = MathHelper.floor_double(yMax);
		int k = MathHelper.floor_double(sp.posZ);

		for(int j = jMax; j >= jMin; j--)
		{
			float swimWaterBorder = getLiquidBorder(i, j, k);
			if(swimWaterBorder > 0)
				return j + swimWaterBorder;
		}
		return yMin;
	}

	protected double getMinPlayerLiquidBetween(double yMin, double yMax)
	{
		int i = MathHelper.floor_double(sp.posX);
		int jMin = MathHelper.floor_double(yMin);
		int jMax = MathHelper.floor_double(yMax);
		int k = MathHelper.floor_double(sp.posZ);

		for(int j = jMin; j <= jMax; j++)
		{
			float swimWaterBorder = getLiquidBorder(i, j, k);
			if(swimWaterBorder > 0)
				if(j > yMin)
					return j;
				else if(j + swimWaterBorder > yMin)
					return yMin;
		}
		return yMax;
	}

	public boolean isCollided(AxisAlignedBB box, double yMin, double yMax, double horizontalTolerance)
	{
		return
			box.maxX >= sp.boundingBox.minX - horizontalTolerance &&
			box.minX <= sp.boundingBox.maxX + horizontalTolerance &&
			box.maxY >= yMin &&
			box.minY <= yMax &&
			box.maxZ >= sp.boundingBox.minZ - horizontalTolerance &&
			box.minZ <= sp.boundingBox.maxZ + horizontalTolerance;
	}

	private boolean isBlockTranslucent(int i, int j, int k)
	{
		return sp.worldObj.isBlockNormalCubeDefault(i, j, k, false);
	}

	public boolean pushOutOfBlocks(double d, double d1, double d2, boolean top)
	{
		int i = MathHelper.floor_double(d);
		int j = MathHelper.floor_double(d1);
		int k = MathHelper.floor_double(d2);
		double d3 = d - i;
		double d4 = d2 - k;
		if(isBlockTranslucent(i, j, k) || (top && isBlockTranslucent(i, j + 1, k)))
		{
			boolean flag = !isBlockTranslucent(i - 1, j, k) && (!top || !isBlockTranslucent(i - 1, j + 1, k));
			boolean flag1 = !isBlockTranslucent(i + 1, j, k) && (!top || !isBlockTranslucent(i + 1, j + 1, k));
			boolean flag2 = !isBlockTranslucent(i, j, k - 1) && (!top || !isBlockTranslucent(i, j + 1, k - 1));
			boolean flag3 = !isBlockTranslucent(i, j, k + 1) && (!top || !isBlockTranslucent(i, j + 1, k + 1));
			byte byte0 = -1;
			double d5 = 9999D;
			if(flag && d3 < d5)
			{
				d5 = d3;
				byte0 = 0;
			}
			if(flag1 && 1.0D - d3 < d5)
			{
				d5 = 1.0D - d3;
				byte0 = 1;
			}
			if(flag2 && d4 < d5)
			{
				d5 = d4;
				byte0 = 4;
			}
			if(flag3 && 1.0D - d4 < d5)
			{
				byte0 = 5;
			}
			float f = 0.1F;
			if(byte0 == 0)
			{
				sp.motionX = -f;
			}
			if(byte0 == 1)
			{
				sp.motionX = f;
			}
			if(byte0 == 4)
			{
				sp.motionZ = -f;
			}
			if(byte0 == 5)
			{
				sp.motionZ = f;
			}
		}
		return false;
	}

	public boolean isInsideOfMaterial(Material material)
	{
		if(SmartMovingOptions.hasFiniteLiquid && material == Material.water)
		{
			double d = sp.posY + sp.getEyeHeight();
			int i = MathHelper.floor_double(sp.posX);
			int j = MathHelper.floor_float(MathHelper.floor_double(d));
			int k = MathHelper.floor_double(sp.posZ);
			Block l = sp.worldObj.getBlock(i, j, k);
			float border;
			if(l != null && (border = getFiniteLiquidWaterBorder(i, j, k, l)) > 0)
			{
				float f = (1 - border) - 0.1111111F;
				float f1 = (j + 1) - f;
				return d < f1;
			}
			return false;
		}
		return isp.localIsInsideOfMaterial(material);
	}

	public int calculateSeparateCollisions(double par1, double par3, double par5)
	{
		float ySize = sp.ySize;
		boolean isInWeb = isp.getIsInWebField();
		AxisAlignedBB boundingBox = sp.boundingBox.copy();
		boolean onGround = sp.onGround;
		World worldObj = sp.worldObj;
		Entity _this = sp;
		boolean field_9293_aM = sp.field_70135_K;
		float stepHeight = sp.stepHeight;

		ySize *= 0.4F;

		if (isInWeb)
		{
			isInWeb = false;
			par1 *= 0.25D;
			par3 *= 0.05000000074505806D;
			par5 *= 0.25D;
		}

		double d2 = par1;
		double d3 = par3;
		double d4 = par5;
		AxisAlignedBB axisalignedbb = boundingBox.copy();
		boolean flag = onGround && isSneaking();

		if (flag)
		{
			double d5 = 0.050000000000000003D;

			for (; par1 != 0.0D && worldObj.getCollidingBoundingBoxes(_this, boundingBox.getOffsetBoundingBox(par1, -1D, 0.0D)).size() == 0; d2 = par1)
			{
				if (par1 < d5 && par1 >= -d5)
				{
					par1 = 0.0D;
					continue;
				}

				if (par1 > 0.0D)
				{
					par1 -= d5;
				}
				else
				{
					par1 += d5;
				}
			}

			for (; par5 != 0.0D && worldObj.getCollidingBoundingBoxes(_this, boundingBox.getOffsetBoundingBox(0.0D, -1D, par5)).size() == 0; d4 = par5)
			{
				if (par5 < d5 && par5 >= -d5)
				{
					par5 = 0.0D;
					continue;
				}

				if (par5 > 0.0D)
				{
					par5 -= d5;
				}
				else
				{
					par5 += d5;
				}
			}

			while (par1 != 0.0D && par5 != 0.0D && worldObj.getCollidingBoundingBoxes(_this, boundingBox.getOffsetBoundingBox(par1, -1D, par5)).size() == 0)
			{
				if (par1 < d5 && par1 >= -d5)
				{
					par1 = 0.0D;
				}
				else if (par1 > 0.0D)
				{
					par1 -= d5;
				}
				else
				{
					par1 += d5;
				}

				if (par5 < d5 && par5 >= -d5)
				{
					par5 = 0.0D;
				}
				else if (par5 > 0.0D)
				{
					par5 -= d5;
				}
				else
				{
					par5 += d5;
				}

				d2 = par1;
				d4 = par5;
			}
		}

		List<?> list = worldObj.getCollidingBoundingBoxes(_this, boundingBox.addCoord(par1, par3, par5));

		for (int i = 0; i < list.size(); i++)
		{
			par3 = ((AxisAlignedBB)list.get(i)).calculateYOffset(boundingBox, par3);
		}

		boundingBox.offset(0.0D, par3, 0.0D);

		if (!field_9293_aM && d3 != par3)
		{
			par1 = par3 = par5 = 0.0D;
		}

		boolean flag1 = onGround || d3 != par3 && d3 < 0.0D;

		for (int j = 0; j < list.size(); j++)
		{
			par1 = ((AxisAlignedBB)list.get(j)).calculateXOffset(boundingBox, par1);
		}

		boundingBox.offset(par1, 0.0D, 0.0D);

		if (!field_9293_aM && d2 != par1)
		{
			par1 = par3 = par5 = 0.0D;
		}

		for (int k = 0; k < list.size(); k++)
		{
			par5 = ((AxisAlignedBB)list.get(k)).calculateZOffset(boundingBox, par5);
		}

		boundingBox.offset(0.0D, 0.0D, par5);

		if (!field_9293_aM && d4 != par5)
		{
			par1 = par3 = par5 = 0.0D;
		}

		if (stepHeight > 0.0F && flag1 && (flag || ySize < 0.05F) && (d2 != par1 || d4 != par5))
		{
			double d6 = par1;
			double d8 = par3;
			double d10 = par5;
			par1 = d2;
			par3 = stepHeight;
			par5 = d4;
			AxisAlignedBB axisalignedbb1 = boundingBox.copy();
			boundingBox.setBB(axisalignedbb);
			List<?> list1 = worldObj.getCollidingBoundingBoxes(_this, boundingBox.addCoord(par1, par3, par5));

			for (int j2 = 0; j2 < list1.size(); j2++)
			{
				par3 = ((AxisAlignedBB)list1.get(j2)).calculateYOffset(boundingBox, par3);
			}

			boundingBox.offset(0.0D, par3, 0.0D);

			if (!field_9293_aM && d3 != par3)
			{
				par1 = par3 = par5 = 0.0D;
			}

			for (int k2 = 0; k2 < list1.size(); k2++)
			{
				par1 = ((AxisAlignedBB)list1.get(k2)).calculateXOffset(boundingBox, par1);
			}

			boundingBox.offset(par1, 0.0D, 0.0D);

			if (!field_9293_aM && d2 != par1)
			{
				par1 = par3 = par5 = 0.0D;
			}

			for (int l2 = 0; l2 < list1.size(); l2++)
			{
				par5 = ((AxisAlignedBB)list1.get(l2)).calculateZOffset(boundingBox, par5);
			}

			boundingBox.offset(0.0D, 0.0D, par5);

			if (!field_9293_aM && d4 != par5)
			{
				par1 = par3 = par5 = 0.0D;
			}

			if (!field_9293_aM && d3 != par3)
			{
				par1 = par3 = par5 = 0.0D;
			}
			else
			{
				par3 = -stepHeight;

				for (int i3 = 0; i3 < list1.size(); i3++)
				{
					par3 = ((AxisAlignedBB)list1.get(i3)).calculateYOffset(boundingBox, par3);
				}

				boundingBox.offset(0.0D, par3, 0.0D);
			}

			if (d6 * d6 + d10 * d10 >= par1 * par1 + par5 * par5)
			{
				par1 = d6;
				par3 = d8;
				par5 = d10;
				boundingBox.setBB(axisalignedbb1);
			}
			else
			{
				double d11 = boundingBox.minY - (int)boundingBox.minY;

				if (d11 > 0.0D)
				{
					ySize += d11 + 0.01D;
				}
			}
		}

		boolean isCollidedPositiveX = d2 > par1;
		boolean isCollidedNegativeX = d2 < par1;
		boolean isCollidedPositiveY = d3 > par3;
		boolean isCollidedNegativeY = d3 < par3;
		boolean isCollidedPositiveZ = d4 > par5;
		boolean isCollidedNegativeZ = d4 < par5;

		int result = 0;
		if(isCollidedPositiveX)
			result += CollidedPositiveX;
		if(isCollidedNegativeX)
			result += CollidedNegativeX;
		if(isCollidedPositiveY)
			result += CollidedPositiveY;
		if(isCollidedNegativeY)
			result += CollidedNegativeY;
		if(isCollidedPositiveZ)
			result += CollidedPositiveZ;
		if(isCollidedNegativeZ)
			result += CollidedNegativeZ;
		return result;
	}

	public final static int CollidedPositiveX = 1;
	public final static int CollidedNegativeX = 2;
	public final static int CollidedPositiveY = 4;
	public final static int CollidedNegativeY = 8;
	public final static int CollidedPositiveZ = 16;
	public final static int CollidedNegativeZ = 32;

	public boolean isSneaking()
	{
		return sp.isSneaking();
	}

	public void correctOnUpdate(boolean isSmall, boolean reverseMaterialAcceleration)
	{
		double d = sp.posX - sp.prevPosX;
		double d1 = sp.posZ - sp.prevPosZ;
		float f = MathHelper.sqrt_double(d * d + d1 * d1);
		if(f < 0.05F && f > 0.02 && isSmall)
		{
			float f1 = sp.renderYawOffset;

			f1 = ((float)Math.atan2(d1, d) * 180F) / 3.141593F - 90F;

			if(sp.swingProgress > 0.0F)
			{
				f1 = sp.rotationYaw;
			}
			float f4;
			for(f4 = f1 - sp.renderYawOffset; f4 < -180F; f4 += 360F) { }
			for(; f4 >= 180F; f4 -= 360F) { }
			float x = sp.renderYawOffset + f4 * 0.3F;
			float f5;
			for(f5 = sp.rotationYaw - x; f5 < -180F; f5 += 360F) { }
			for(; f5 >= 180F; f5 -= 360F) { }
			if(f5 < -75F)
			{
				f5 = -75F;
			}
			if(f5 >= 75F)
			{
				f5 = 75F;
			}
			sp.renderYawOffset = sp.rotationYaw - f5;
			if(f5 * f5 > 2500F)
			{
				sp.renderYawOffset += f5 * 0.2F;
			}
			for(; sp.renderYawOffset - sp.prevRenderYawOffset < -180F; sp.prevRenderYawOffset -= 360F) { }
			for(; sp.renderYawOffset - sp.prevRenderYawOffset >= 180F; sp.prevRenderYawOffset += 360F) { }
		}

		if(reverseMaterialAcceleration)
			reverseHandleMaterialAcceleration();
	}

	protected double getGapUnderneight()
	{
		return sp.boundingBox.minY - getMaxPlayerSolidBetween(sp.boundingBox.minY - 1.1D, sp.boundingBox.minY, 0);
	}

	protected double getGapOverneight()
	{
		return getMinPlayerSolidBetween(sp.boundingBox.maxY, sp.boundingBox.maxY + 1.1D, 0) - sp.boundingBox.maxY;
	}

	public double getOverGroundHeight(double maximum)
	{
		if(esp != null)
			return (sp.boundingBox.minY - getMaxPlayerSolidBetween(sp.boundingBox.minY - maximum, sp.boundingBox.minY, 0));
		return (sp.boundingBox.minY + 1D - getMaxPlayerSolidBetween(sp.boundingBox.minY - maximum + 1D, sp.boundingBox.minY + 1D, 0.1));
	}

	public Block getOverGroundBlockId(double distance)
	{
		int x = MathHelper.floor_double(sp.posX);
		int y = MathHelper.floor_double(sp.boundingBox.minY);
		int z = MathHelper.floor_double(sp.posZ);
		int minY = y - (int)Math.ceil(distance);

		if(esp == null)
		{
			y++;
			minY++;
		}

		for(; y >= minY; y--)
		{
			Block block = sp.worldObj.getBlock(x, y, z);
			if(block != null)
				return block;
		}
		return null;
	}

	public void reverseHandleMaterialAcceleration()
	{
		AxisAlignedBB axisalignedbb = sp.boundingBox.expand(0.0D, -0.40000000596046448D, 0.0D).contract(0.001D, 0.001D, 0.001D);
		Material material = Material.water;
		Entity entity = sp;

		int i = MathHelper.floor_double(axisalignedbb.minX);
		int j = MathHelper.floor_double(axisalignedbb.maxX + 1.0D);
		int k = MathHelper.floor_double(axisalignedbb.minY);
		int l = MathHelper.floor_double(axisalignedbb.maxY + 1.0D);
		int i1 = MathHelper.floor_double(axisalignedbb.minZ);
		int j1 = MathHelper.floor_double(axisalignedbb.maxZ + 1.0D);
		if(!entity.worldObj.checkChunksExist(i, k, i1, j, l, j1))
		{
			return;
		}

		Vec3 vec3d = Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);
		for(int k1 = i; k1 < j; k1++)
		{
			for(int l1 = k; l1 < l; l1++)
			{
				for(int i2 = i1; i2 < j1; i2++)
				{
					Block block = entity.worldObj.getBlock(k1, l1, i2);
					if(block == null || block.getMaterial() != material)
					{
						continue;
					}
					double d1 = (l1 + 1) - BlockLiquid.getLiquidHeightPercent(entity.worldObj.getBlockMetadata(k1, l1, i2));
					if(l >= d1)
					{
						block.velocityToAddToEntity(entity.worldObj, k1, l1, i2, entity, vec3d);
					}
				}

			}

		}

		if(vec3d.lengthVector() > 0.0D)
		{
			vec3d = vec3d.normalize();
			double d = -0.014D; // instead +0.014D for reversal
			entity.motionX += vec3d.xCoord * d;
			entity.motionY += vec3d.yCoord * d;
			entity.motionZ += vec3d.zCoord * d;
		}
	}
}