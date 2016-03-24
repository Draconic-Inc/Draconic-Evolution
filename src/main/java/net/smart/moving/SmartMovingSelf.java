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

import java.lang.reflect.*;
import java.util.*;

import net.minecraft.block.*;
import net.minecraft.block.Block.*;
import net.minecraft.client.*;
import net.minecraft.client.entity.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.client.settings.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.player.EntityPlayer.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.network.play.client.*;
import net.minecraft.potion.*;
import net.minecraft.stats.*;
import net.minecraft.util.*;
import net.smart.moving.config.*;
import net.smart.moving.render.*;
import net.smart.utilities.*;

@SuppressWarnings("static-access")
public class SmartMovingSelf extends SmartMoving implements ISmartMovingSelf
{
	private boolean initialized;
	private int multiPlayerInitialized;
	private int updateCounter;
	private float distanceSwom;

	public SmartMovingSelf(EntityPlayer sp, IEntityPlayerSP isp)
	{
		super(sp, isp);

		initialized = false;

		nextClimbDistance = 0;
		distanceClimbedModified = 0;

		exhaustion = 0;
		lastHorizontalCollisionX = 0;
		lastHorizontalCollisionZ = 0;
		lastHungerIncrease = -2;

		prevPacketState = -1;
	}

	public void moveEntityWithHeading(float moveStrafing, float moveForward)
	{
		if(sp.motionX == 0 && prevMotionX < 0.005)
			sp.motionX = prevMotionX;

		if(sp.motionZ == 0 && prevMotionZ < 0.005)
			sp.motionZ = prevMotionZ;

		if(sp.capabilities.isFlying && !Config.isFlyingEnabled())
		{
			double d3 = sp.motionY;
			float f2 = sp.jumpMovementFactor;
			sp.jumpMovementFactor = 0.05F;
			superMoveEntityWithHeading(moveStrafing, moveForward);
			sp.motionY = d3 * 0.59999999999999998D;
			sp.jumpMovementFactor = f2;
		}
		else
		{
			superMoveEntityWithHeading(moveStrafing, moveForward);
		}
	}

	private void superMoveEntityWithHeading(float moveStrafing, float moveForward)
	{
		if(isRunning() && !Config.isRunningEnabled())
			sp.setSprinting(false);

		boolean wasShortInWater = isSwimming || isDiving;
		boolean wasSwimming = isSwimming;
		boolean wasClimbing = isClimbing;
		boolean wasDiving = isDiving;
		boolean wasCeilingClimbing = isCeilingClimbing;
		boolean wasJumpingOutOfWater = isJumpingOutOfWater;

		handleJumping();

		double d_S = sp.posX;
		double d1_S = sp.posY;
		double d2_S = sp.posZ;

		if(sp.isCollidedHorizontally)
		{
			lastHorizontalCollisionX = sp.posX;
			lastHorizontalCollisionZ = sp.posZ;
		}

		float speedFactor = this.getSpeedFactor(moveForward, moveStrafing);

		boolean isLiquidClimbing = Config.isFreeClimbingEnabled() && sp.fallDistance <= 3.0 && wantClimbUp && sp.isCollidedHorizontally && !isDiving;
		boolean handledSwimming = handleSwimming(moveForward, moveStrafing, speedFactor, wasSwimming, wasDiving, isLiquidClimbing, wasJumpingOutOfWater);
		boolean handledLava = handleLava(moveForward, moveStrafing, handledSwimming, isLiquidClimbing);
		boolean handledAlternativeFlying = handleAlternativeFlying(moveForward, moveStrafing, speedFactor, handledSwimming, handledLava);
		handleLand(moveForward, moveStrafing, speedFactor, handledSwimming, handledLava, handledAlternativeFlying, wasShortInWater, wasClimbing, wasCeilingClimbing);

		handleWallJumping();

		double diffX = sp.posX - d_S;
		double diffY = sp.posY - d1_S;
		double diffZ = sp.posZ - d2_S;

		sp.addMovementStat(diffX, diffY, diffZ);

		handleExhaustion(diffX, diffY, diffZ);
	}

	private float getSpeedFactor()
	{
		return Config.enabled ? Config._speedFactor.value * Config.getUserSpeedFactor() * getLandMovementFactor() * 10F / (sp.isSprinting() ? 1.3F : 1F) : 1F;
	}

	private float getSpeedFactor(float moveForward, float moveStrafing)
	{
		float speedFactor = getSpeedFactor();

		if (sp.isUsingItem())
		{
			float itemFactor = 0.2F;
			if(Config.enabled)
			{
				Item item = sp.getItemInUse().getItem();
				if(item instanceof ItemSword)
					itemFactor = Config._usageSwordSpeedFactor.value;
				else if(item instanceof ItemBow)
					itemFactor = Config._usageBowSpeedFactor.value;
				else if(item instanceof ItemFood)
					itemFactor = Config._usageFoodSpeedFactor.value;
				else
					itemFactor = Config._usageSpeedFactor.value;
			}
			speedFactor *= itemFactor;
		}

		if(isCrawling || (isCrawlClimbing && !isClimbCrawling))
			speedFactor *= Config._crawlFactor.value;
		else if(isSlow)
			speedFactor *= Config._sneakFactor.value;

		if(isFast)
			speedFactor *= Config._sprintFactor.value;

		if(isClimbing)
			if(moveStrafing != 0F || moveForward != 0F)
				speedFactor *= Config._freeClimbingHorizontalSpeedFactor.value;
			else if(wantClimbDown && isNeighborClimbing && !(Math.abs(sp.posX - lastHorizontalCollisionX) < 0.05 && Math.abs(sp.posZ - lastHorizontalCollisionZ) < 0.05))
			{
				moveForward = ClimbPullMotion;
				if(isVineOnlyClimbing)
				{
					if(handsEdgeMeta != Orientation.VineFrontMeta && feetEdgeMeta != Orientation.VineFrontMeta)
						moveForward = 0F;
					else
					{
						Orientation orientation = Orientation.getOrientation(sp, 45F, true, false);
						if(orientation != null)
						{
							float gap = (float)orientation.getHorizontalBorderGap(sp);
							float minGap = sp.width / 2;
							float factor = Math.max(0, gap * (1 + minGap) - minGap);
							moveForward = factor * factor * 0.3F;
						}
					}
				}
			}

		if(isCeilingClimbing)
			speedFactor *= Config._ceilingClimbingSpeedFactor.value;

		return speedFactor;
	}

	private boolean handleSwimming(float moveForward, float moveStrafing, float speedFactor, boolean wasSwimming, boolean wasDiving, boolean isLiquidClimbing, boolean wasJumpingOutOfWater)
	{
		boolean handleSwimmingRejected = false;
		boolean handleSwimming = !isFlying && !isLiquidClimbing && (sp.isInWater() || (wasSwimming && isInLiquid()) || (Config.isLavaLikeWaterEnabled() && sp.handleLavaMovement()));
		if(handleSwimming)
		{
			resetClimbing();

			float wasHeightOffset = heightOffset;

			boolean useStandard = !Config.isSwimmingEnabled() && !Config.isDivingEnabled();
			if(sp.ridingEntity != null)
			{
				resetSwimming();
				useStandard = true;
			}

			if(useStandard && isCrawling)
				standupIfPossible();
			else
				resetHeightOffset();

			if(!useStandard)
			{
				resetSwimming();

				int i = MathHelper.floor_double(sp.posX);
				int j = MathHelper.floor_double(sp.boundingBox.minY);
				int k = MathHelper.floor_double(sp.posZ);

				boolean swimming = false;
				boolean diving = false;
				boolean dipping = false;

				double j_offset = sp.boundingBox.minY - j;

				double totalSwimWaterBorder = getMaxPlayerLiquidBetween(sp.boundingBox.maxY - 1.8, sp.boundingBox.maxY + 1.2);
				double minPlayerSwimWaterCeiling = getMinPlayerSolidBetween(sp.boundingBox.maxY - 1.8, sp.boundingBox.maxY + 1.2, 0);
				double realTotalSwimWaterBorder = Math.min(totalSwimWaterBorder, minPlayerSwimWaterCeiling);
				double minPlayerSwimWaterDepth = totalSwimWaterBorder - getMaxPlayerSolidBetween(totalSwimWaterBorder - 2, totalSwimWaterBorder, 0);
				double realMinPlayerSwimWaterDepth = totalSwimWaterBorder - getMaxPlayerSolidBetween(realTotalSwimWaterBorder - 2, realTotalSwimWaterBorder, 0);
				double playerSwimWaterBorder = totalSwimWaterBorder - j - j_offset;

				if(isCrawling && playerSwimWaterBorder > SwimCrawlWaterTopBorder)
					standupIfPossible();

				double motionYDiff = 0;
				boolean couldStandUp = playerSwimWaterBorder >= 0 && minPlayerSwimWaterDepth <= 1.5;

				boolean diveUp = isp.getIsJumpingField();
				boolean diveDown = esp.movementInput.sneak && Config._diveDownOnSneak.value;
				boolean swimDown = esp.movementInput.sneak && Config._swimDownOnSneak.value;

				boolean wantShallowSwim = couldStandUp && (wasSwimming || wasDiving);
				if(wantShallowSwim)
				{
					HashSet<Orientation> orientations = Orientation.getClimbingOrientations(sp, true, true);
					Iterator<Orientation> iterator = orientations.iterator();
					while(iterator.hasNext())
						if(!(wantShallowSwim &= !iterator.next().isTunnelAhead(sp.worldObj, i, j, k)))
							break;
				}

				if(wasSwimming && wantShallowSwim && swimDown)
				{
					swimDown = false;
					isFakeShallowWaterSneaking = true;
				}

				if(isDiving && diveUp && diveDown)
					diveUp = diveDown = false;

				if(isCrawling || isClimbCrawling || isCrawlClimbing)
					isDipping = true;
				else if(playerSwimWaterBorder >= 0 && playerSwimWaterBorder <= 2)
				{
					double offset = playerSwimWaterBorder + 0.1625D; // for fine tuning
					boolean moveSwim = sp.rotationPitch < 0F && esp.movementInput.moveForward > 0F || sp.rotationPitch > 0F && esp.movementInput.moveForward < 0F;
					if(diveUp || moveSwim || wantShallowSwim)
					{
						if(offset < 1.4)
						{
							dipping = true;
							if(offset < 1)
								motionYDiff = -0.02D;
							else
								motionYDiff = -0.01D;
						}
						else if(offset < 1.9)
						{
							swimming = true;
							if(swimDown)
								motionYDiff = -0.05D * (isFast ? Config._sprintFactor.value : 1F);
							else if(offset < 1.5)
								motionYDiff = -0.02D;
							if(offset < 1.6)
								motionYDiff = -0.01D;
							else if(offset < 1.62)
								motionYDiff = -0.005D;
							else if(offset < 1.64)
								motionYDiff = -0.0025D;
							else if(offset < 1.66)
								motionYDiff = -0.00125D;
							else if(offset < 1.664)
								motionYDiff = -0.000625D;
							else if(offset < 1.668)
								motionYDiff = 0D;
							else if(offset < 1.672)
								motionYDiff = 0.000625D;
							else if(offset < 1.676)
								motionYDiff = 0.00125D;
							else if(offset < 1.68)
								motionYDiff = 0.0025D;
							else if(offset < 1.7)
								motionYDiff = 0.005D;
							else if(offset < 1.8)
								motionYDiff = 0.01D;
							else
								motionYDiff = 0.02D;
						}
						else
						{
							diving = true;
							if(diveUp)
								motionYDiff = 0.05D * (isFast ? Config._sprintFactor.value : 1F);
							else if(diveDown)
								motionYDiff = 0.01 - 0.1 * speedFactor;
							else
								motionYDiff = moveSwim ? 0.04D : 0.02D;
						}
					}
					else
					{
						if(offset < 1.5)
						{
							dipping = true;
							if(offset < 1)
								motionYDiff = -0.02D;
							else
								motionYDiff = -0.02D;
						}
						else
						{
							diving = true;
							if(diveDown)
								motionYDiff = 0.01 - 0.1 * speedFactor;
							else if(offset < 1.8)
								motionYDiff = -0.02D;
							else if(offset < 1.82)
								motionYDiff = -0.01D;
							else if(offset < 1.84)
								motionYDiff = -0.005D;
							else if(offset < 1.86)
								motionYDiff = -0.0025D;
							else if(offset < 1.864)
								motionYDiff = -0.00125D;
							else if(offset < 1.868)
								motionYDiff = 0D;
							else if(offset < 1.872)
								motionYDiff = 0.00125D;
							else if(offset < 1.876)
								motionYDiff = 0.0025D;
							else if(offset < 1.88)
								motionYDiff = 0.005D;
							else if(offset < 1.9)
								motionYDiff = 0.01D;
							else
								motionYDiff = 0.01D;
						}
					}
				}
				else if(playerSwimWaterBorder > 2)
				{
					diving = true;
					if(diveUp)
						if(isFast && playerSwimWaterBorder < 2.5 && sp.worldObj.isAirBlock(i, j + 3, k))
							motionYDiff = 0.11D / Config._sprintFactor.value;
						else
							motionYDiff = 0.01 + 0.1 * speedFactor;
					else if(diveDown)
						motionYDiff = 0.01 - 0.1 * speedFactor;
					else
						motionYDiff = 0.01D;
				}
				else
					handleSwimmingRejected = true;

				dippingDepth = (float)playerSwimWaterBorder;
				float playerCrawlWaterBorder = dippingDepth + wasHeightOffset;
				if((isCrawling || isSliding) && playerCrawlWaterBorder < SwimCrawlWaterMaxBorder)
					if(playerCrawlWaterBorder < SwimCrawlWaterTopBorder)
					{
						// continue crawling in shallow water
						setHeightOffset(wasHeightOffset);
						handleSwimmingRejected = true;
					}
					else
					{
						// from crawling in shallow water to swimming/diving
						if(wantShallowSwim)
							move(0, 0.1, 0, true); // to avoid diving in shallow water
						isCrawling = false;
						isDiving = false;
						isSwimming = true;
						isDipping = false;
					}

				if(!handleSwimmingRejected)
				{
					swimming = !useStandard && swimming && Config.isSwimmingEnabled();
					diving = !useStandard && diving && Config.isDivingEnabled();
					dipping = !useStandard && dipping && Config.isSwimmingEnabled();
					useStandard = !swimming && !diving && !dipping;

					if(!useStandard)
					{
						if(diveUp)
							sp.motionY -= 0.039999999105930328D;

						if(swimming)
						{
							sp.motionX *= 0.85D;
							sp.motionY *= 0.85D;
							sp.motionZ *= 0.85D;
						}
						else if(diving)
						{
							sp.motionX *= 0.83D;
							sp.motionY *= 0.83D;
							sp.motionZ *= 0.83D;
						}
						else if(dipping)
						{
							sp.motionX *= 0.80D;
							sp.motionY *= 0.83D;
							sp.motionZ *= 0.80D;
						}
						else
						{
							sp.motionX *= 0.9D;
							sp.motionY *= 0.85D;
							sp.motionZ *= 0.9D;
						}

						boolean moveFlying = true;
						boolean levitating = diving && !diveUp && !diveDown && moveStrafing == 0F && moveForward == 0F;

						if(diving)
							speedFactor *= Config._diveSpeedFactor.value;
						if(swimming)
							speedFactor *= Config._swimSpeedFactor.value;

						if(swimming || diving)
							waterMovementTicks++;
						else
							waterMovementTicks = 0;

						boolean wantJumpOutOfWater = (moveForward != 0 || moveStrafing != 0) && sp.isCollidedHorizontally && diveUp && !isSlow;
						isJumpingOutOfWater = wantJumpOutOfWater && (waterMovementTicks > 10 || sp.onGround || wasJumpingOutOfWater);

						if(diving)
						{
							if(diveUp || diveDown || levitating)
								sp.motionY = (sp.motionY + motionYDiff) * 0.6;
							else
								moveFlying((float)motionYDiff, moveStrafing, moveForward, 0.02F * speedFactor, Options._diveControlVertical.value);
							moveFlying = false;
						}
						else if(swimming && swimDown)
							sp.motionY = (sp.motionY + motionYDiff) * 0.6;
						else if(isJumpingOutOfWater)
							sp.motionY = 0.30000001192092896D;
						else
							sp.motionY += motionYDiff;

						isDiving = diving;
						isLevitating = levitating;
						isSwimming = swimming;
						isShallowDiveOrSwim = couldStandUp && (isDiving || isSwimming);
						isDipping = dipping;

						if(isDiving || isSwimming)
							setHeightOffset(-1F);

						if(isShallowDiveOrSwim && realMinPlayerSwimWaterDepth < SwimCrawlWaterBottomBorder)
						{
							if(isSlow)
							{
								// from swimming/diving in shallow water to crawling in shallow water
								setHeightOffset(-1F);
								isCrawling = true;
								isDiving = false;
								isSwimming = false;
								isShallowDiveOrSwim = false;
								isDipping = true;
							}
							else
							{
								// from swimming/diving in shallow water to walking in shallow water
								resetHeightOffset();
								sp.moveEntity(0, getMaxPlayerSolidBetween(sp.boundingBox.minY, sp.boundingBox.maxY, 0) - sp.boundingBox.minY, 0);
								isCrawling = false;
								isDiving = false;
								isSwimming = false;
								isShallowDiveOrSwim = false;
								isDipping = true;
							}
						}

						if(moveFlying)
							sp.moveFlying(moveStrafing, moveForward, 0.02F * speedFactor);
						sp.moveEntity(sp.motionX, sp.motionY, sp.motionZ);
					}
				}
			}
			else
			{
				isDiving = false;
				isSwimming = false;
				isShallowDiveOrSwim = false;
				isDipping = false;
				isStillSwimmingJump = false;
			}

			if(useStandard)
			{
				resetSwimming();

				if(isCrawling)
					setHeightOffset(wasHeightOffset);

				double dY = sp.posY;
				sp.moveFlying(moveStrafing, moveForward, 0.02F * speedFactor);
				sp.moveEntity(sp.motionX, sp.motionY, sp.motionZ);

				sp.motionX *= 0.80000001192092896D;
				sp.motionY *= 0.80000001192092896D;
				sp.motionZ *= 0.80000001192092896D;
				sp.motionY -= 0.02D;
				if(sp.isCollidedHorizontally && sp.isOffsetPositionInLiquid(sp.motionX, ((sp.motionY + 0.60000002384185791D) - sp.posY) + dY, sp.motionZ))
				{
					sp.motionY = 0.30000001192092896D;
				}
			}
		}

		return handleSwimming && !handleSwimmingRejected;
	}

	private boolean handleLava(float moveForward, float moveStrafing, boolean handledSwimming, boolean isLiquidClimbing)
	{
		boolean handleLava = !isFlying && !handledSwimming && !isLiquidClimbing && sp.handleLavaMovement();
		if(handleLava)
		{
			standupIfPossible();
			resetClimbing();
			resetSwimming();

			double d1 = sp.posY;
			sp.moveFlying(moveStrafing, moveForward, 0.02F);
			sp.moveEntity(sp.motionX, sp.motionY, sp.motionZ);
			sp.motionX *= 0.5D;
			sp.motionY *= 0.5D;
			sp.motionZ *= 0.5D;
			sp.motionY -= 0.02D;
			if(sp.isCollidedHorizontally && sp.isOffsetPositionInLiquid(sp.motionX, ((sp.motionY + 0.60000002384185791D) - sp.posY) + d1, sp.motionZ))
			{
				sp.motionY = 0.30000001192092896D;
			}
		}
		return handleLava;
	}

	private boolean handleAlternativeFlying(float moveForward, float moveStrafing, float speedFactor, boolean handledSwimming, boolean handledLava)
	{
		boolean handleAlternativeFlying = !handledSwimming && !handledLava && sp.capabilities.isFlying && Config.isFlyingEnabled();
		if(handleAlternativeFlying)
		{
			resetSwimming();
			resetClimbing();

			float moveUpward = 0F;
			if(esp.movementInput.sneak)
			{
				sp.motionY += 0.14999999999999999D;
				moveUpward -= 0.98F;
			}
			if(esp.movementInput.jump)
			{
				sp.motionY -= 0.14999999999999999D;
				moveUpward += 0.98F;
			}

			moveFlying(moveUpward, moveStrafing, moveForward, speedFactor * 0.05F * Config._flyingSpeedFactor.value, Options._flyControlVertical.value);

			sp.moveEntity(sp.motionX, sp.motionY, sp.motionZ);

			sp.motionX *= HorizontalAirDamping;
			sp.motionY *= HorizontalAirDamping;
			sp.motionZ *= HorizontalAirDamping;
		}
		return handleAlternativeFlying;
	}

	private void handleLand(float moveForward, float moveStrafing, float speedFactor, boolean handledSwimming, boolean handledLava, boolean handledAlternativeFlying, boolean wasShortInWater, boolean wasClimbing, boolean wasCeilingClimbing)
	{
		if(!handledSwimming && !handledLava && !handledAlternativeFlying)
		{
			resetSwimming();

			if (!grabButton.Pressed)
				fromSwimmingOrDiving(wasShortInWater);

			boolean isOnLadder = isOnLadder(isClimbCrawling);
			boolean isOnVine = isOnVine(isClimbCrawling);

			float horizontalDamping = landMotion(moveForward, moveStrafing, speedFactor, isOnLadder, isOnVine);
			boolean crawlingThroughWeb = (isCrawling || isCrawlClimbing) && isp.getIsInWebField();
			move(sp.motionX, sp.motionY, sp.motionZ, crawlingThroughWeb);

			handleClimbing(isOnLadder, isOnVine, wasClimbing);
			handleCeilingClimbing(wasCeilingClimbing);
			setLandMotions(horizontalDamping);
		}

		landMotionPost(wasShortInWater);
	}

	private void move(double motionX, double motionY, double motionZ, boolean relocate)
	{
		boolean isInWeb = isp.getIsInWebField();
		if(relocate)
			isp.setIsInWebField(false);
		sp.moveEntity(motionX, motionY, motionZ);
		if(relocate)
			isp.setIsInWebField(isInWeb);
	}

	private float landMotion(float moveForward, float moveStrafing, float speedFactor, boolean isOnLadder, boolean isOnVine)
	{
		float horizontalDamping;
		if(sp.onGround && !isJumping)
		{
			Block block = sp.worldObj.getBlock(MathHelper.floor_double(sp.posX), MathHelper.floor_double(sp.boundingBox.minY) - 1, MathHelper.floor_double(sp.posZ));
			if(block != null)
				horizontalDamping = block.slipperiness * HorizontalAirDamping;
			else
				horizontalDamping = HorizontalGroundDamping;

			if(esp.movementInput.jump && isFast && Config.isJumpingEnabled(Config.Sprinting, Config.Up))
				speedFactor *= Config._sprintJumpVerticalFactor.value;
		}
		else
			horizontalDamping = HorizontalAirDamping;

		if(isClimbing && climbingUpIsBlockedByLadder())
			sp.moveFlying(0F, -1F, 0.07F);
		else if(isClimbing && climbingUpIsBlockedByTrapDoor())
			if(sp.isOnLadder())
				sp.moveFlying(0F, -1F, 0.09F);
			else
				sp.moveFlying(0F, -1F, 0.09F);
		else if(isClimbing && climbingUpIsBlockedByCobbleStoneWall())
			sp.moveFlying(0F, -1F, 0.07F);
		else if(!isSliding)
		{
			if(isHeadJumping)
				speedFactor *= Config._headJumpControlFactor.value;
			else if(Config.enabled && !sp.onGround && !sp.capabilities.isFlying && !isFlying)
				speedFactor *= Config._jumpControlFactor.value;

			float f3 = 0.1627714F / (horizontalDamping * horizontalDamping * horizontalDamping);
			float f4 = sp.onGround ? getLandMovementFactor() * f3 : sp.jumpMovementFactor;
			float rawSpeed = sp.isSprinting() ? f4 / 1.3F : f4;
			if(Config.isRunningEnabled() && isRunning() && !isFast)
				speedFactor *= Config._runFactor.value;

			sp.moveFlying(moveStrafing, moveForward, rawSpeed * speedFactor);
		}

		if(sp.onGround && !isJumping)
		{
			Block block = sp.worldObj.getBlock(MathHelper.floor_double(sp.posX), MathHelper.floor_double(sp.boundingBox.minY) - 1, MathHelper.floor_double(sp.posZ));
			if(block != null)
			{
				float slipperiness = block.slipperiness;
				if(isSliding)
				{
					horizontalDamping = 1F / (((1F / slipperiness) - 1F) / 25F * Config._slideSlipperinessFactor.value + 1F) * 0.98F;
					if(moveStrafing != 0 && Config._slideControlDegrees.value > 0)
					{
						double angle = -Math.atan(sp.motionX / sp.motionZ);
						if (!Double.isNaN(angle))
						{
							if(sp.motionZ < 0)
								angle += Math.PI;

							angle -= Config._slideControlDegrees.value / RadiantToAngle * Math.signum(moveStrafing);

							double hMotion = Math.sqrt(sp.motionX * sp.motionX + sp.motionZ * sp.motionZ);
							sp.motionX = hMotion * -Math.sin(angle);
							sp.motionZ = hMotion * Math.cos(angle);
						}
					}
				}
				else
					horizontalDamping = slipperiness * HorizontalAirDamping;
			}
			else
				horizontalDamping = HorizontalGroundDamping;
		}
		else if(isAerodynamic)
			horizontalDamping = HorizontalAirodynamicDamping;
		else
			horizontalDamping = HorizontalAirDamping;

		if(isOnLadder || isOnVine)
		{
			float f4 = 0.15F;
			if(sp.motionX < -f4)
			{
				sp.motionX = -f4;
			}
			if(sp.motionX > f4)
			{
				sp.motionX = f4;
			}
			if(sp.motionZ < (-f4))
			{
				sp.motionZ = -f4;
			}
			if(sp.motionZ > f4)
			{
				sp.motionZ = f4;
			}
			boolean notTotalFreeClimbing = !isClimbing && isOnLadder && !Config.isTotalFreeLadderClimb() || isOnVine && !Config.isTotalFreeVineClimb();
			if(notTotalFreeClimbing)
			{
				sp.fallDistance = 0.0F;
				sp.motionY = Math.max(sp.motionY, -0.15 * getSpeedFactor());
			}
			if(Config.isFreeBaseClimb())
			{
				if(esp.movementInput.sneak && sp.motionY < 0.0D && !sp.onGround && notTotalFreeClimbing)
				{
					sp.motionY = 0.0D;
				}
			}
			else
			{
				if(isp.localIsSneaking() && sp.motionY < 0.0D)
				{
					sp.motionY = 0.0D;
				}
			}
		}
		else if(Config.isFreeClimbAutoLaddderEnabled() && moveForward > 0)
		{
			int j = MathHelper.floor_double(sp.boundingBox.minY);
			double jGap = sp.boundingBox.minY - j;

			if(jGap < 0.1)
			{
				int i = MathHelper.floor_double(sp.posX);
				int k = MathHelper.floor_double(sp.posZ);

				if(Orientation.isLadder(sp.worldObj.getBlock(i, j - 1, k)))
					sp.motionY = Math.max(sp.motionY, 0.0);
			}
		}
		return horizontalDamping;
	}

	private void handleClimbing(boolean isOnLadder, boolean isOnVine, boolean wasClimbing)
	{
		resetClimbing();

		boolean isOnLadderOrVine = isOnLadder || isOnVine;

		if(Config.isStandardBaseClimb() && sp.isCollidedHorizontally && isOnLadderOrVine)
		{
			sp.motionY = 0.2 * getSpeedFactor();
		}

		if(Config.isSimpleBaseClimb() && sp.isCollidedHorizontally && isOnLadderOrVine)
		{
			int i = MathHelper.floor_double(sp.posX);
			int j = MathHelper.floor_double(sp.boundingBox.minY);
			int k = MathHelper.floor_double(sp.posZ);

			boolean feet = Orientation.isClimbable(sp.worldObj, i, j, k);
			boolean hands = Orientation.isClimbable(sp.worldObj, i, j + 1, k);

			if(feet && hands)
				sp.motionY = FastUpMotion;
			else if(feet)
				sp.motionY = FastUpMotion;
			else if(hands)
				sp.motionY = SlowUpMotion;
			else
				sp.motionY = 0.0D;

			sp.motionY *= getSpeedFactor();
		}

		if(Config.isSmartBaseClimb() || Config.isFreeClimbingEnabled())
		{
			double id = sp.posX;
			double jd = sp.boundingBox.minY;
			double kd = sp.posZ;

			int i = MathHelper.floor_double(id);
			int j = MathHelper.floor_double(jd);
			int k = MathHelper.floor_double(kd);

			if(Config.isSmartBaseClimb() && isOnLadderOrVine && sp.isCollidedHorizontally)
			{
				boolean feet = Orientation.isClimbable(sp.worldObj, i, j, k);
				boolean hands = Orientation.isClimbable(sp.worldObj, i, j + 1, k);

				if(feet && hands)
					sp.motionY = FastUpMotion;
				else if(feet)
				{
					boolean handsSubstitute =
						Orientation.PZ.isHandsLadderSubstitute(sp.worldObj, i, j + 1, k) ||
						Orientation.NZ.isHandsLadderSubstitute(sp.worldObj, i, j + 1, k) ||
						Orientation.ZP.isHandsLadderSubstitute(sp.worldObj, i, j + 1, k) ||
						Orientation.ZN.isHandsLadderSubstitute(sp.worldObj, i, j + 1, k);

					if(handsSubstitute)
						sp.motionY = FastUpMotion;
					else
						sp.motionY = SlowUpMotion;
				}
				else if(hands)
				{
					boolean feetSubstitute =
						Orientation.ZZ.isFeetLadderSubstitute(sp.worldObj, i, j, k) ||
						Orientation.PZ.isFeetLadderSubstitute(sp.worldObj, i, j, k) ||
						Orientation.NZ.isFeetLadderSubstitute(sp.worldObj, i, j, k) ||
						Orientation.ZP.isFeetLadderSubstitute(sp.worldObj, i, j, k) ||
						Orientation.ZN.isFeetLadderSubstitute(sp.worldObj, i, j, k);

					if(feetSubstitute)
						sp.motionY = FastUpMotion;
					else
						sp.motionY = SlowUpMotion;
				}
				else
					sp.motionY = 0.0D;

				sp.motionY *= getSpeedFactor();
			}

			if(Config.isFreeClimbingEnabled() && sp.fallDistance <= Config._freeClimbFallMaximumDistance.value && (!isOnLadderOrVine || Config.isFreeBaseClimb()))
			{
				boolean exhaustionAllowsClimbing =
					!Config.isClimbExhaustionEnabled() ||
					(
							exhaustion <= Config._climbExhaustionStop.value &&
							(wasClimbing || exhaustion <= Config._climbExhaustionStart.value)
					);

				boolean preferClimb = false;
				if(wantClimbUp || wantClimbDown)
				{
					if(Config.isClimbExhaustionEnabled())
					{
						maxExhaustionForAction = Math.min(maxExhaustionForAction, Config._climbExhaustionStop.value);
						maxExhaustionToStartAction = Math.min(maxExhaustionToStartAction, Config._climbExhaustionStart.value);
					}
					if(exhaustionAllowsClimbing)
						preferClimb = true;
				}
				if(preferClimb)
				{
					boolean isSmallClimbing = isCrawling || isSliding;
					if(isClimbCrawling || isCrawlClimbing || isSmallClimbing)
						jd += -1D;

					float rotation = sp.rotationYaw % 360F;
					if(rotation < 0)
						rotation += 360F;

					double jh = jd * 2D + 1;

					HandsClimbing handsClimbing = HandsClimbing.None;
					FeetClimbing feetClimbing = FeetClimbing.None;

					inout_handsClimbing[0] = handsClimbing;
					inout_feetClimbing[0] = feetClimbing;

					out_handsClimbGap.reset();
					out_feetClimbGap.reset();

					Orientation.PZ.seekClimbGap(rotation, sp.worldObj, i, id, jh, k, kd, isClimbCrawling, isCrawlClimbing, isSmallClimbing, inout_handsClimbing, inout_feetClimbing, out_handsClimbGap, out_feetClimbGap);
					Orientation.NZ.seekClimbGap(rotation, sp.worldObj, i, id, jh, k, kd, isClimbCrawling, isCrawlClimbing, isSmallClimbing, inout_handsClimbing, inout_feetClimbing, out_handsClimbGap, out_feetClimbGap);
					Orientation.ZP.seekClimbGap(rotation, sp.worldObj, i, id, jh, k, kd, isClimbCrawling, isCrawlClimbing, isSmallClimbing, inout_handsClimbing, inout_feetClimbing, out_handsClimbGap, out_feetClimbGap);
					Orientation.ZN.seekClimbGap(rotation, sp.worldObj, i, id, jh, k, kd, isClimbCrawling, isCrawlClimbing, isSmallClimbing, inout_handsClimbing, inout_feetClimbing, out_handsClimbGap, out_feetClimbGap);

					handsClimbing = inout_handsClimbing[0];
					feetClimbing = inout_feetClimbing[0];

					isNeighborClimbing = handsClimbing != HandsClimbing.None || feetClimbing != FeetClimbing.None;
					hasNeighborClimbGap = out_handsClimbGap.CanStand || out_feetClimbGap.CanStand;
					hasNeighborClimbCrawlGap = out_handsClimbGap.MustCrawl || out_feetClimbGap.MustCrawl;

					if(!isSmallClimbing)
					{
						Orientation.PP.seekClimbGap(rotation, sp.worldObj, i, id, jh, k, kd, isClimbCrawling, isCrawlClimbing, isSmallClimbing, inout_handsClimbing, inout_feetClimbing, out_handsClimbGap, out_feetClimbGap);
						Orientation.NP.seekClimbGap(rotation, sp.worldObj, i, id, jh, k, kd, isClimbCrawling, isCrawlClimbing, isSmallClimbing, inout_handsClimbing, inout_feetClimbing, out_handsClimbGap, out_feetClimbGap);
						Orientation.NN.seekClimbGap(rotation, sp.worldObj, i, id, jh, k, kd, isClimbCrawling, isCrawlClimbing, isSmallClimbing, inout_handsClimbing, inout_feetClimbing, out_handsClimbGap, out_feetClimbGap);
						Orientation.PN.seekClimbGap(rotation, sp.worldObj, i, id, jh, k, kd, isClimbCrawling, isCrawlClimbing, isSmallClimbing, inout_handsClimbing, inout_feetClimbing, out_handsClimbGap, out_feetClimbGap);
					}

					handsClimbing = inout_handsClimbing[0];
					feetClimbing = inout_feetClimbing[0];

					hasClimbGap = out_handsClimbGap.CanStand || out_feetClimbGap.CanStand;
					hasClimbCrawlGap = out_handsClimbGap.MustCrawl || out_feetClimbGap.MustCrawl;

					if(handsClimbing == HandsClimbing.BottomHold && Orientation.isLadder(sp.worldObj.getBlock(i, j + 2, k)))
					{
						Orientation ladderOrientation = Orientation.getKnownLadderOrientation(sp.worldObj, i, j + 2, k);
						int remote_i = i + ladderOrientation._i;
						int remote_k = k + ladderOrientation._k;
						if(!sp.worldObj.getBlock(remote_i, j, remote_k).getMaterial().isSolid() && !sp.worldObj.getBlock(remote_i, j + 1, remote_k).getMaterial().isSolid())
							handsClimbing = HandsClimbing.None;
					}

					if(!grabButton.Pressed && handsClimbing == HandsClimbing.Up && feetClimbing == FeetClimbing.None)
					{
						if(!sp.isCollidedHorizontally && sp.worldObj.isAirBlock(i, j, k) && sp.worldObj.isAirBlock(i, j + 1, k))
							handsClimbing = HandsClimbing.None;
					}

					// feet climbing only with balancing in gaps or combined with hand climbing
					if(feetClimbing.IsRelevant() || handsClimbing.IsRelevant())
					{
						if(wantClimbUp)
						{
							if(isSliding && handsClimbing.IsRelevant())
							{
								isSliding = false;
								isCrawling = true;
							}

							handsClimbing = handsClimbing.ToUp();

							if(feetClimbing == FeetClimbing.FastUp && !(handsClimbing == HandsClimbing.None && sp.onGround && out_feetClimbGap.Block != Block.getBlockFromName("bed")))
							{
								// climbing fast
								setShouldClimbSpeed(FastUpMotion, HandsClimbing.NoGrab, FeetClimbing.DownStep);
							}
							else if((hasClimbGap || hasClimbCrawlGap) && handsClimbing == HandsClimbing.FastUp && (feetClimbing == FeetClimbing.None || feetClimbing == FeetClimbing.BaseWithHands))
							{
								// climb into crawl gap
								setShouldClimbSpeed(feetClimbing == FeetClimbing.None ? SlowUpMotion : FastUpMotion, HandsClimbing.MiddleGrab, FeetClimbing.DownStep);
							}
							else if(feetClimbing.IsRelevant() && handsClimbing.IsRelevant() && !(feetClimbing == FeetClimbing.BaseHold && handsClimbing == HandsClimbing.Sink) && !(handsClimbing == HandsClimbing.Sink && feetClimbing == FeetClimbing.TopWithHands) && !(handsClimbing == HandsClimbing.TopHold && feetClimbing == FeetClimbing.TopWithHands))
							{
								// climbing all limbed
								setShouldClimbSpeed(MediumUpMotion, (hasClimbGap || hasClimbCrawlGap) && !(handsClimbing == HandsClimbing.Sink && feetClimbing == FeetClimbing.BaseWithHands) ? HandsClimbing.MiddleGrab : HandsClimbing.UpGrab, FeetClimbing.DownStep);
							}
							else if(handsClimbing.IsUp())
							{
								// climbing slow
								setShouldClimbSpeed(SlowUpMotion);
							}
							else if(handsClimbing == HandsClimbing.TopHold || feetClimbing == FeetClimbing.BaseHold || (feetClimbing == FeetClimbing.SlowUpWithHoldWithoutHands && handsClimbing == HandsClimbing.None))
							{
								// holding at top
								if (!jumpButton.StartPressed || !(isClimbJumping = tryJump(feetClimbing != FeetClimbing.None ? Config.ClimbUp : Config.ClimbUpHandsOnly, null, null, null)))
								{
									if(handsClimbing == HandsClimbing.Sink && feetClimbing == FeetClimbing.BaseHold || handsClimbing == HandsClimbing.TopHold && feetClimbing == FeetClimbing.TopWithHands)
										setShouldClimbSpeed(HoldMotion, HandsClimbing.MiddleGrab, FeetClimbing.DownStep);
									else
										setShouldClimbSpeed(HoldMotion);
								}
							}
							else if(handsClimbing == HandsClimbing.Sink || (feetClimbing == FeetClimbing.SlowUpWithSinkWithoutHands && handsClimbing == HandsClimbing.None))
							{
								// sinking unwillingly
								setShouldClimbSpeed(SinkDownMotion);
							}
						}
						else if(wantClimbDown)
						{
							handsClimbing = handsClimbing.ToDown();

							if(handsClimbing == HandsClimbing.BottomHold && !feetClimbing.IsIndependentlyRelevant())
							{
								// holding at bottom
								setShouldClimbSpeed(HoldMotion);
							}
							else if(handsClimbing.IsRelevant())
							{
								// sinking willingly
								if(feetClimbing == FeetClimbing.FastUp)
									setShouldClimbSpeed(ClimbDownMotion, HandsClimbing.NoGrab, FeetClimbing.DownStep);
								else if(feetClimbing == FeetClimbing.SlowUpWithHoldWithoutHands)
									setShouldClimbSpeed(ClimbDownMotion);
								else if(feetClimbing == FeetClimbing.TopWithHands)
									setShouldClimbSpeed(ClimbDownMotion);
								else if(feetClimbing == FeetClimbing.BaseWithHands || feetClimbing == FeetClimbing.BaseHold)
									if((handsClimbing != HandsClimbing.None && handsClimbing != HandsClimbing.Up) || (handsClimbing == HandsClimbing.Up && feetClimbing == FeetClimbing.BaseHold))
										setShouldClimbSpeed(ClimbDownMotion);
									else
										setShouldClimbSpeed(SinkDownMotion);
								else
									setShouldClimbSpeed(SinkDownMotion, handsClimbing == HandsClimbing.FastUp ? HandsClimbing.MiddleGrab : HandsClimbing.UpGrab, FeetClimbing.NoStep);
							}

							if(isClimbHolding)
							{
								// holding
								setOnlyShouldClimbSpeed(HoldMotion);

								if(jumpButton.StartPressed)
								{
									boolean handsOnly = feetClimbing != FeetClimbing.None;

									int type = (Options._climbJumpBackHeadOnGrab.value ? grabButton.Pressed : !grabButton.Pressed)
										? (handsOnly ? Config.ClimbBackHead : Config.ClimbBackHeadHandsOnly)
										: (handsOnly ? Config.ClimbBackUp : Config.ClimbBackUpHandsOnly);

									float jumpAngle = sp.rotationYaw + 180F;
									if(tryJump(type, null, null, jumpAngle))
									{
										continueWallJumping = !isHeadJumping;
										isClimbing = false;
										sp.rotationYaw = jumpAngle;
										onStartClimbBackJump();
									}
								}
							}
						}

						if(isClimbing)
							handleCrash(Config._freeClimbFallDamageStartDistance.value, Config._freeClimbFallDamageFactor.value);

						if(wantClimbUp || wantClimbDown)
						{
							if(handsClimbing == HandsClimbing.None)
								actualHandsClimbType = HandsClimbing.NoGrab;
							else if(feetClimbing == FeetClimbing.None)
								actualFeetClimbType = FeetClimbing.NoStep;

							handsEdgeBlock = out_handsClimbGap.Block;
							handsEdgeMeta = out_handsClimbGap.Meta;
							feetEdgeBlock = out_feetClimbGap.Block;
							feetEdgeMeta = out_feetClimbGap.Meta;
						}
					}
				}

				if(SmartMovingOptions.hasRedPowerWire && !isClimbing && wantClimbUp && wasClimbing) // to climb up RedPower wire bottom covers
					sp.motionY = 0.15;

				isHandsVineClimbing = isClimbing && handsEdgeBlock == Block.getBlockFromName("vine");
				isFeetVineClimbing = isClimbing && feetEdgeBlock == Block.getBlockFromName("vine");

				isVineAnyClimbing = isHandsVineClimbing || isFeetVineClimbing;

				isVineOnlyClimbing = isVineAnyClimbing &&
					!(handsEdgeBlock != null && handsEdgeBlock != Block.getBlockFromName("vine") || feetEdgeBlock != null && feetEdgeBlock != Block.getBlockFromName("vine"));
			}
		}
	}

	private void handleCeilingClimbing(boolean wasCeilingClimbing)
	{
		boolean exhaustionAllowsClimbCeiling =
			!Config.isCeilingClimbExhaustionEnabled() ||
			(
					exhaustion <= Config._ceilingClimbExhaustionStop.value &&
					(wasCeilingClimbing || exhaustion <= Config._ceilingClimbExhaustionStart.value)
			);

		boolean climbCeilingCrawlingStartConflict = !Config.isFreeClimbingEnabled() && isCrawling && !wasCrawling;
		boolean couldClimbCeiling = wantClimbCeiling && !isClimbing && (!isCrawling || climbCeilingCrawlingStartConflict) && !isCrawlClimbing;
		if(couldClimbCeiling && Config.isCeilingClimbExhaustionEnabled())
		{
			maxExhaustionForAction = Math.min(maxExhaustionForAction, Config._ceilingClimbExhaustionStop.value);
			maxExhaustionToStartAction = Math.min(maxExhaustionToStartAction, Config._ceilingClimbExhaustionStart.value);
		}

		if(couldClimbCeiling && exhaustionAllowsClimbCeiling)
		{
			double id = sp.posX;
			double jd = sp.boundingBox.maxY + (climbCeilingCrawlingStartConflict ? 1F : 0F);
			double kd = sp.posZ;

			int i = MathHelper.floor_double(id);
			int j = MathHelper.floor_double(jd);
			int k = MathHelper.floor_double(kd);

			Block topBlock = supportsCeilingClimbing(i, j, k);
			Block bottomBlock = supportsCeilingClimbing(i, j + 1, k);

			boolean topCeilingClimbing = topBlock != null;
			boolean bottomCeilingClimbing = bottomBlock != null;

			if(topCeilingClimbing || bottomCeilingClimbing)
			{
				double jgap = 1D - jd + j;
				if(bottomCeilingClimbing)
					jgap++;

				double actuallySolidHeight = getMinPlayerSolidBetween(jd, jd + 0.6, 0.2);
				if(jgap < 1.9 && actuallySolidHeight < jd + 0.5)
				{
					if(jgap > 1.2)
						sp.motionY = 0.12;
					else if(jgap > 1.115)
						sp.motionY = 0.08;
					else
						sp.motionY = 0.04;

					sp.fallDistance = 0.0F;
					isCeilingClimbing = true;
					handsEdgeBlock = topCeilingClimbing ? topBlock : bottomBlock;
				}
			}
		}

		if (isCeilingClimbing && climbCeilingCrawlingStartConflict)
		{
			isCrawling = false;
			this.resetHeightOffset();
			move(0, 1D, 0, true);
		}
	}

	private void setLandMotions(float horizontalDamping)
	{
		sp.motionY -= 0.080000000000000002D;
		sp.motionY *= 0.98000001907348633D;
		sp.motionX *= horizontalDamping;
		sp.motionZ *= horizontalDamping;
	}

	private void handleExhaustion(double diffX, double diffY, double diffZ)
	{
		float hungerIncrease = 0;
		if(Config.enabled)
		{
			boolean isRunning = isRunning();
			boolean isVerticalStill = Math.abs(diffY) < 0.007;
			boolean isStill = isStanding && isVerticalStill;

			if(sp.ridingEntity == null)
			{
				float horizontalMovement = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
				float movement = MathHelper.sqrt_double(horizontalMovement * horizontalMovement + diffY * diffY);

				int relevantMovementFactor = Math.round(movement * 100F);

				if(Config.isHungerGainEnabled())
				{
					float hungerGainFactor = Config.getFactor(true, sp.onGround, isStanding, isStill, isSlow, isRunning, isFast, isClimbing, isClimbCrawling, isCeilingClimbing, isDipping, isSwimming, isDiving, isCrawling, isCrawlClimbing);
					hungerIncrease += Config._alwaysHungerGain.value + relevantMovementFactor * 0.0001F * hungerGainFactor;
				}

				float additionalExhaustion = 0F;
				if(isClimbing && !isStill && Config.isClimbExhaustionEnabled())
				{
					float climbingExhaustion = Config._baseExhautionGainFactor.value;
					if(isVerticalStill)
						climbingExhaustion *= Config._climbStrafeExhaustionGain.value;
					else
					{
						if(!isStanding)
						{
							if(wantClimbUp)
								climbingExhaustion *= Config._climbStrafeUpExhaustionGain.value;
							else if(wantClimbDown)
								climbingExhaustion *= Config._climbStrafeDownExhaustionGain.value;
							else
								climbingExhaustion *= 0F;
						}
						else
						{
							if(wantClimbUp)
								climbingExhaustion *= Config._climbUpExhaustionGain.value;
							else if(wantClimbDown)
								climbingExhaustion *= Config._climbDownExhaustionGain.value;
							else
								climbingExhaustion *= 0F;
						}
					}
					additionalExhaustion += climbingExhaustion;
				}

				if(isCeilingClimbing && !isStanding && Config.isCeilingClimbExhaustionEnabled())
					additionalExhaustion += Config._baseExhautionGainFactor.value * Config._ceilingClimbExhaustionGain.value;

				if(isFast && Config.isSprintExhaustionEnabled())
				{
					if(additionalExhaustion == 0)
						additionalExhaustion = Config._baseExhautionGainFactor.value;

					additionalExhaustion *= Config._sprintExhaustionGainFactor.value;
				}

				if(isRunning() && Config.isRunExhaustionEnabled())
				{
					if(additionalExhaustion == 0)
						additionalExhaustion = Config._baseExhautionGainFactor.value;

					additionalExhaustion *= Config._runExhaustionGainFactor.value;
				}

				if (foreignExhaustionFactor > 0)
				{
					additionalExhaustion += foreignExhaustionFactor * Config._baseExhautionGainFactor.value;

					if(foreignMaxExhaustionForAction == Float.MAX_VALUE)
						foreignMaxExhaustionForAction = Client.getMaximumExhaustion();
					maxExhaustionForAction = Math.min(maxExhaustionForAction, foreignMaxExhaustionForAction);

					if(foreignMaxExhaustionToStartAction == Float.MAX_VALUE)
						foreignMaxExhaustionToStartAction = Client.getMaximumExhaustion();
					maxExhaustionToStartAction = Math.min(maxExhaustionToStartAction, foreignMaxExhaustionToStartAction);
				}

				exhaustion += additionalExhaustion;
			}
			else
				hungerIncrease = -1;

			if(exhaustion > 0)
			{
				boolean exhaustionLossPossible = !Config.isExhaustionLossHungerEnabled() || sp.getFoodStats().getFoodLevel() > Config._exhaustionLossFoodLevelMinimum.value;
				if(exhaustionLossPossible)
				{
					float exhaustionLossFactor = Config.getFactor(false, sp.onGround, isStanding, isStill, isSlow, isRunning, isFast, isClimbing, isClimbCrawling, isCeilingClimbing, isDipping, isSwimming, isDiving, isCrawling, isCrawlClimbing);
					float exhaustionLoss = 1F * exhaustionLossFactor;
					exhaustion -= exhaustionLoss;
					if(Config.isExhaustionLossHungerEnabled())
						hungerIncrease += Config._exhaustionLossHungerFactor.value * exhaustionLoss;
				}
			}

			if(exhaustion < 0)
				exhaustion = 0;

			if(exhaustion == 0)
				maxExhaustionForAction = maxExhaustionToStartAction = Float.NaN;

			if(maxExhaustionForAction == Float.MAX_VALUE)
				maxExhaustionForAction = prevMaxExhaustionForAction;

			if(maxExhaustionToStartAction == Float.MAX_VALUE)
				maxExhaustionToStartAction = prevMaxExhaustionToStartAction;

			foreignExhaustionFactor = 0;
			foreignMaxExhaustionForAction = Float.MAX_VALUE;
			foreignMaxExhaustionToStartAction = Float.MAX_VALUE;
		}
		else
			hungerIncrease = -1;

		if(hungerIncrease != lastHungerIncrease)
		{
			SmartMovingPacketStream.sendHungerChange(SmartMovingComm.instance, hungerIncrease);
			lastHungerIncrease = hungerIncrease;
		}
	}

	@Override
	public float getExhaustion()
	{
		return exhaustion;
	}

	@Override
	public float getUpJumpCharge()
	{
		return jumpCharge;
	}

	@Override
	public float getHeadJumpCharge()
	{
		return headJumpCharge;
	}

	@Override
	public void addExhaustion(float factor)
	{
		if(!Float.isNaN(factor) && factor > 0)
			foreignExhaustionFactor += factor;
	}

	@Override
	public void setMaxExhaustionForAction(float maxExhaustionForAction)
	{
		if(!Float.isNaN(maxExhaustionForAction) && maxExhaustionForAction >= 0)
			foreignMaxExhaustionForAction = Math.min(foreignMaxExhaustionForAction, maxExhaustionForAction);
	}

	@Override
	public void setMaxExhaustionToStartAction(float maxExhaustionToStartAction)
	{
		if(!Float.isNaN(maxExhaustionToStartAction) && maxExhaustionToStartAction >= 0)
			foreignMaxExhaustionToStartAction = Math.min(foreignMaxExhaustionToStartAction, maxExhaustionToStartAction);
	}

	private void landMotionPost(boolean wasShortInWater)
	{
		if (grabButton.Pressed)
			fromSwimmingOrDiving(wasShortInWater);

		if(heightOffset != 0 && isp.getSleepingField())
		{
			// from swimming/diving to sleeping
			resetInternalHeightOffset();
		}
	}

	private void fromSwimmingOrDiving(boolean wasShortInWater)
	{
		boolean isShortInWater = isSwimming || isDiving;
		if(wasShortInWater && !isShortInWater && !isp.getSleepingField())
		{
			// from diving in deep water to walking/sneaking/crawling
			setHeightOffset(-1F);

			double crawlStandUpBottom = getMaxPlayerSolidBetween(sp.boundingBox.minY - 1D, sp.boundingBox.minY, 0);
			double crawlStandUpLiquidCeiling = getMinPlayerLiquidBetween(sp.boundingBox.maxY, sp.boundingBox.maxY + 1.1D);
			double crawlStandUpCeiling = getMinPlayerSolidBetween(sp.boundingBox.maxY, sp.boundingBox.maxY + 1.1D, 0);

			resetHeightOffset();

			if(crawlStandUpCeiling - crawlStandUpBottom < sp.height)
			{
				// from diving in deep water to crawling in small hole
				isCrawling = true;
				isDipping = false;
				setHeightOffset(-1F);
			}
			else if(crawlStandUpLiquidCeiling - crawlStandUpBottom < sp.height)
			{
				// from diving in deep water to crawling below the water
				isCrawling = true;
				contextContinueCrawl = true;
				isDipping = false;
				setHeightOffset(-1F);
			}
			else if(crawlStandUpBottom > sp.boundingBox.minY)
			{
				// from diving in deep water to walking/crawling
				if(isSlow && crawlStandUpBottom > sp.boundingBox.minY + 0.5D)
				{
					// from diving in deep water to crawling
					isCrawling = true;
					isDipping = false;
					setHeightOffset(-1F);
				}
				move(0, (crawlStandUpBottom - sp.boundingBox.minY), 0, true);
			}
		}
	}

	private static ClimbGap out_handsClimbGap = new ClimbGap();
	private static ClimbGap out_feetClimbGap = new ClimbGap();

	private static HandsClimbing[] inout_handsClimbing = new HandsClimbing[1];
	private static FeetClimbing[] inout_feetClimbing = new FeetClimbing[1];

	public boolean wantClimbUp;
	public boolean wantClimbDown;
	public boolean wantSprint;
	public boolean wantCrawlNotClimb;
	public boolean wantClimbCeiling;

	public boolean isStanding;
	public boolean wouldIsSneaking;
	public boolean isVineOnlyClimbing;
	public boolean isVineAnyClimbing;

	public boolean isClimbingStill;
	public boolean isClimbHolding;
	public boolean isNeighborClimbing;
	public boolean hasClimbGap;
	public boolean hasClimbCrawlGap;
	public boolean hasNeighborClimbGap;
	public boolean hasNeighborClimbCrawlGap;

	public float dippingDepth;

	public boolean isJumping;
	public boolean isJumpingOutOfWater;
	public boolean isShallowDiveOrSwim;
	public boolean isFakeShallowWaterSneaking;
	public boolean isStillSwimmingJump;
	public boolean isGroundSprinting;
	public boolean isSprintJump;
	public boolean isAerodynamic;

	public Block handsEdgeBlock;
	public int handsEdgeMeta;
	public Block feetEdgeBlock;
	public int feetEdgeMeta;

	public int waterMovementTicks;

	public float exhaustion;
	public float jumpCharge;
	public float headJumpCharge;
	public boolean blockJumpTillButtonRelease;

	public float maxExhaustionForAction;
	public float maxExhaustionToStartAction;

	public float prevMaxExhaustionForAction = Float.NaN;
	public float prevMaxExhaustionToStartAction = Float.NaN;

	public float foreignExhaustionFactor;
	public float foreignMaxExhaustionForAction = Float.MAX_VALUE;
	public float foreignMaxExhaustionToStartAction = Float.MAX_VALUE;

	public double lastHorizontalCollisionX;
	public double lastHorizontalCollisionZ;
	public float lastHungerIncrease;

	public boolean canTriggerWalking()
	{
		return !isClimbing && !isDiving;
	}

	private void resetClimbing()
	{
		isClimbing = false;
		isHandsVineClimbing = false;
		isFeetVineClimbing = false;
		isVineOnlyClimbing = false;
		isVineAnyClimbing = false;
		isClimbingStill = false;
		isNeighborClimbing = false;
		actualHandsClimbType = HandsClimbing.NoGrab;
		actualFeetClimbType = FeetClimbing.NoStep;
		isCeilingClimbing = false;
	}

	private void resetSwimming()
	{
		dippingDepth = -1;
		isDipping = false;
		isSwimming = false;
		isDiving = false;
		isLevitating = false;
		isShallowDiveOrSwim = false;
		isFakeShallowWaterSneaking = false;
		isJumpingOutOfWater = false;
	}

	private void setShouldClimbSpeed(double value)
	{
		setShouldClimbSpeed(value, HandsClimbing.UpGrab, FeetClimbing.DownStep);
	}

	private void setShouldClimbSpeed(double value, int handsClimbType, int feetClimbType)
	{
		setOnlyShouldClimbSpeed(value);
		actualHandsClimbType = handsClimbType;
		actualFeetClimbType = feetClimbType;
	}

	@SuppressWarnings("incomplete-switch")
	private void setOnlyShouldClimbSpeed(double value)
	{
		isClimbing = true;

		if(this.climbIntoCount > 0)
			value = HoldMotion;

		if(value != HoldMotion)
		{
			float factor = getSpeedFactor();
			if(isFast)
				factor *= Config._sprintFactor.value;
			if(Config.isFreeBaseClimb() && value == MediumUpMotion)
				switch(getOnLadder(Integer.MAX_VALUE, false, isClimbCrawling))
				{
					case 1:
						factor *= Config._freeOneLadderClimbUpSpeedFactor.value;
						break;
					case 2:
						factor *= Config._freeBothLadderClimbUpSpeedFactor.value;
						break;
				}

			if(value > HoldMotion)
				value = ((value - HoldMotion) * Config._freeClimbingUpSpeedFactor.value * factor + HoldMotion);
			else
				value = HoldMotion - (HoldMotion - value) * Config._freeClimbingDownSpeedFactor.value * factor;

			if(hasClimbCrawlGap && isClimbCrawling && value > HoldMotion)
				value = Math.min(CatchCrawlGapMotion, value); // to avoid climbing over really small gaps (RedPowerWire Cover Top / RedPowerWire Cover Bottom)
		}
		else
			isClimbingStill = true;

		boolean relevant = value < 0 || value > sp.motionY;
		if(relevant)
			sp.motionY = value;
		isClimbJumping = !relevant && !isClimbHolding;
	}

	public boolean isOnLadderOrVine()
	{
		return isOnLadderOrVine(isClimbCrawling);
	}

	private double beforeMoveEntityPosX;
	private double beforeMoveEntityPosY;
	private double beforeMoveEntityPosZ;
	private float beforeDistanceWalkedModified;
	private float horizontalCollisionAngle;

	public void beforeMoveEntity(double d, double d1, double d2)
	{
		beforeMoveEntityPosX = sp.posX;
		beforeMoveEntityPosY = sp.posZ;
		beforeMoveEntityPosZ = sp.posY;

		if(esp.movementInput.sneak || sneakToggled)
			if(isSwimming || isDiving || isCrawling || isClimbing || (!Config.isSneakingEnabled() && !isSneaking()))
				sp.ySize = 0F;
			else if(isSlow)
				sp.ySize = 0.6F;
			else
				sp.ySize = 0F;

		if(isSliding || isCrawling)
		{
			beforeDistanceWalkedModified = sp.distanceWalkedModified;
			sp.distanceWalkedModified = Float.MIN_VALUE;
		}

		if(wantWallJumping)
		{
			int collisions = calculateSeparateCollisions(d, d1, d2);
			horizontalCollisionAngle = getHorizontalCollisionangle(
				(collisions & CollidedPositiveZ) != 0,
				(collisions & CollidedNegativeZ) != 0,
				(collisions & CollidedPositiveX) != 0,
				(collisions & CollidedNegativeX) != 0);
		}
	}

	public void moveEntity(double d, double d1, double d2)
	{
		beforeMoveEntity(d, d1, d2);
		isp.localMoveEntity(d, d1, d2);
		afterMoveEntity(d, d1, d2);
	}

	@SuppressWarnings("unused")
	public void afterMoveEntity(double d, double d1, double d2)
	{
		if(isSliding || isCrawling)
			sp.distanceWalkedModified = beforeDistanceWalkedModified;

		if(heightOffset != 0F)
			sp.posY = sp.posY + heightOffset;

		wasOnGround = sp.onGround;

		double d10 = sp.posX - beforeMoveEntityPosX;
		double d12 = sp.posZ - beforeMoveEntityPosY;
		double d13 = sp.posY - beforeMoveEntityPosZ;

		double distance = MathHelper.sqrt_double(d10 * d10 + d12 * d12 + d13 * d13);

		if(isClimbing || isCeilingClimbing)
		{
			distanceClimbedModified += distance * (isClimbing ? 1.2 : 0.9);
			if(distanceClimbedModified > nextClimbDistance)
			{
				Block stepBlock;
				if(isClimbing)
					if(handsEdgeBlock == null)
						if(feetEdgeBlock == null)
							stepBlock = Block.getBlockFromName("cobblestone");
						else
							stepBlock = feetEdgeBlock;
					else
						if(feetEdgeBlock == null)
							stepBlock = handsEdgeBlock;
						else
							stepBlock = nextClimbDistance % 2 != 0 ? feetEdgeBlock : handsEdgeBlock;
				else
					stepBlock = handsEdgeBlock;

				nextClimbDistance++;
				if(stepBlock != null)
				{
					SoundType stepsound = stepBlock.stepSound;
					if(stepsound != null)
						playSound(stepsound.getStepResourcePath(), stepsound.getVolume() * 0.15F, stepsound.getPitch());
				}
			}
		}

		if(isSwimming)
		{
			distanceSwom += distance;
			if(distanceSwom > SwimSoundDistance)
			{
				Random rand = sp.getRNG();
				playSound("random.splash", 0.05F, 1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.4F);

				distanceSwom -= SwimSoundDistance;
			}
		}
	}

	private void playSound(String id, float volume, float pitch)
	{
		Minecraft.getMinecraft().theWorld.playSound(sp.posX, sp.posY - sp.yOffset, sp.posZ, id, volume, pitch, false);
		SmartMovingPacketStream.sendSound(SmartMovingComm.instance, id, volume, pitch);
	}

	@SuppressWarnings("unused")
	public void beforeSleepInBedAt(int i, int j, int k)
	{
		if(!isp.getSleepingField())
			updateEntityActionState(true);
	}

	public EnumStatus sleepInBedAt(int i, int j, int k)
	{
		beforeSleepInBedAt(i, j, k);
		return isp.localSleepInBedAt(i, j, k);
	}

	private void resetHeightOffset()
	{
		sp.boundingBox.minY += heightOffset;
		sp.height -= heightOffset;
		heightOffset = 0F;
	}

	private void resetInternalHeightOffset()
	{
		sp.height -= heightOffset;
		heightOffset = 0F;
	}

	private void setHeightOffset(float offset)
	{
		resetHeightOffset();
		if(offset == 0F)
			return;

		heightOffset = offset;

		sp.boundingBox.minY -= heightOffset;
		sp.height += heightOffset;
	}

	boolean wasOnGround;

	public float getBrightness(float f)
	{
		sp.posY -= heightOffset;
		float result = isp.localGetBrightness(f);
		sp.posY += heightOffset;
		return result;
	}

	public int getBrightnessForRender(float f)
	{
		sp.posY -= heightOffset;
		int result = isp.localGetBrightnessForRender(f);
		sp.posY += heightOffset;
		return result;
	}

	public boolean pushOutOfBlocks(double d, double d1, double d2)
	{
		if(multiPlayerInitialized > 0)
			return false;

		boolean top = false;
		if(heightOffset != 0F)
			top = sp.height > 1F;

		return pushOutOfBlocks(d, d1, d2, top);
	}

	public void beforeOnUpdate()
	{
		prevMotionX = sp.motionX;
		prevMotionY = sp.motionY;
		prevMotionZ = sp.motionZ;

		wasCollidedHorizontally = sp.isCollidedHorizontally;

		isJumping = false;

		if(sp.worldObj.isRemote && updateCounter < 10)
		{
			List<?> chatMessageList = (List<?>)Reflect.GetField(GuiNewChat.class, isp.getMcField().ingameGUI.getChatGUI(), SmartMovingInstall.GuiNewChat_chatMessageList);
			for(int i=0; i<chatMessageList.size(); i++)
				if(SmartMovingComm.processBlockCode(((ChatLine)chatMessageList.get(i)).func_151461_a().getUnformattedText()))
					chatMessageList.remove(i--);
			updateCounter++;
		}
	}

	public void afterOnUpdate()
	{
		correctOnUpdate(isSwimming || isDiving || isDipping || isCrawling, isSwimming);

		spawnParticles(isp.getMcField(), sp.motionX, sp.motionZ);

		float landMovementFactor = getLandMovementFactor();

		if(Config.enabled)
		{
			float perspectiveFactor = landMovementFactor;
			if(isFast || isSprintJump || isRunning())
			{
				if(sp.isSprinting())
					perspectiveFactor /= 1.3F;

				if(isFast || isSprintJump)
				{
					perspectiveFactor *= Options._perspectiveSprintFactor.value;
				}
				else if(isRunning())
				{
					perspectiveFactor *= 1.3F * Options._perspectiveRunFactor.value;
				}
			}

			if(fadingPerspectiveFactor != -1)
				fadingPerspectiveFactor += (perspectiveFactor - fadingPerspectiveFactor) * Options._perspectiveFadeFactor.value;
			else
				fadingPerspectiveFactor = landMovementFactor;
		}

		if(sp.capabilities.disableDamage)
			exhaustion = 0;

		if(sp.capabilities.isFlying)
			sp.fallDistance = 0F;

		if(sp.isCollidedHorizontally)
			collidedHorizontallyTickCount++;
		else
			collidedHorizontallyTickCount = 0;

		addToSendQueue();

		if(wasInventory)
			sp.prevRotationYawHead = sp.rotationYawHead;
		wasInventory = isp.getMcField().currentScreen instanceof GuiInventory;
	}

	boolean wasCapabilitiesIsFlying;

	public void beforeOnLivingUpdate()
	{
		wasCapabilitiesIsFlying = sp.capabilities.isFlying;
	}

	public void afterOnLivingUpdate()
	{
		if(Options._flyWhileOnGround.value && !(sneakButton.Pressed && grabButton.Pressed) && wasCapabilitiesIsFlying && !sp.capabilities.isFlying && sp.onGround)
		{
			sp.cameraYaw = 0;
			sp.prevCameraYaw = 0;
			sp.capabilities.isFlying = true;
			((EntityClientPlayerMP)sp).sendQueue.addToSendQueue(new C13PacketPlayerAbilities(sp.capabilities));
		}
	}

	private float fadingPerspectiveFactor = -1;
	private boolean wasInventory;

	private double jumpMotionX;
	private double jumpMotionZ;

	public void handleJumping()
	{
		if(blockJumpTillButtonRelease && !esp.movementInput.jump)
			blockJumpTillButtonRelease = false;

		if(isSwimming || isDiving)
			return;

		boolean jump = jumpAvoided && sp.onGround && isp.getIsJumpingField() && !sp.isInWater() && !sp.handleLavaMovement();
		if(jump)
		{
			if(sp.boundingBox.minY - getMaxPlayerSolidBetween(sp.boundingBox.minY - 0.2D, sp.boundingBox.minY, 0) >= 0.01D)
				return; // Maybe SPC flying?
		}

		jumpMotionX = sp.motionX;
		jumpMotionZ = sp.motionZ;

		boolean isJumpCharging = false;
		if(Config.isJumpChargingEnabled())
		{
			boolean isJumpChargingPossible = sp.onGround && isStanding;
			isJumpCharging = isJumpChargingPossible && wouldIsSneaking;

			boolean actualJumpCharging = isJumpChargingPossible && (!Config._jumpChargeCancelOnSneakRelease.value || wouldIsSneaking);
			if(actualJumpCharging)
				if(esp.movementInput.jump && (Config._jumpChargeCancelOnSneakRelease.value || wouldIsSneaking))
					jumpCharge++;
				else
				{
					if(jumpCharge > 0)
						tryJump(Config.ChargeUp, null, null, null);
					jumpCharge = 0;
				}
			else
			{
				if(jumpCharge > 0)
					blockJumpTillButtonRelease = true;
				jumpCharge = 0;
			}
		}

		boolean isHeadJumpCharging = false;
		if(Config.isHeadJumpingEnabled())
		{
			isHeadJumpCharging = grabButton.Pressed && (isGroundSprinting || isSprintJump || (isRunning() && sp.onGround)) && !isCrawling;
			if(isHeadJumpCharging)
				if(esp.movementInput.jump)
					headJumpCharge++;
				else
				{
					if(headJumpCharge > 0 && sp.onGround)
						tryJump(Config.HeadUp, null, null, null);
					headJumpCharge = 0;
				}
			else
			{
				if(headJumpCharge > 0)
					blockJumpTillButtonRelease = true;
				headJumpCharge = 0;
			}
		}

		if(esp.movementInput.jump && sp.isInWater() && isDipping)
			if(sp.posY - MathHelper.floor_double(sp.posY) > (isSlow ? 0.37 : 0.6))
			{
				sp.motionY -= 0.039999999105930328D;
				if(!isStillSwimmingJump && sp.onGround && jumpCharge == 0)
				{
					if(tryJump(Config.Up, true, null, null))
					{
						Random rand = sp.getRNG();
						playSound("random.splash", 0.05F, 1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.4F);
					}
				}
			}

		if(jump && !blockJumpTillButtonRelease && !isJumpCharging && !isHeadJumpCharging && !isVineAnyClimbing)
			tryJump(Config.Up, false, null, null);

		int left = 0;
		int back = 0;
		if(leftJumpCount == -1)
			left++;
		if(rightJumpCount == -1)
			left--;
		if(backJumpCount == -1)
			back++;

		if(left != 0 || back != 0)
		{
			int angle;
			if(left > 0)
				angle = back == 0 ? 270 : 225;
			else if(left < 0)
				angle = back == 0 ? 90 : 135;
			else
				angle = 180;

			if(tryJump(Config.Angle, null, null, sp.rotationYaw + angle))
				angleJumpType = ((360 - angle) / 45) % 8;

			leftJumpCount = 0;
			rightJumpCount = 0;
			backJumpCount = 0;
		}
	}

	public void handleWallJumping()
	{
		if(!wantWallJumping || Double.isNaN(horizontalCollisionAngle))
			return;

		int jumpType;
		if(grabButton.Pressed)
		{
			if(sp.fallDistance > Config._wallHeadJumpFallMaximumDistance.value)
				return;
			jumpType = wasCollidedHorizontally ? Config.WallHeadSlide : Config.WallHead;
		}
		else
		{
			if(sp.fallDistance > Config._wallUpJumpFallMaximumDistance.value)
				return;
			jumpType = wasCollidedHorizontally ? Config.WallUpSlide : Config.WallUp;
		}

		float jumpAngle;
		if(!wasCollidedHorizontally)
		{
			float movementAngle = getAngle(jumpMotionZ, -jumpMotionX);
			if(Double.isNaN(movementAngle))
				return;

			jumpAngle = horizontalCollisionAngle * 2 - movementAngle + 180F;
		}
		else
			jumpAngle = horizontalCollisionAngle;

		while(jumpAngle > 360F)
			jumpAngle -= 360F;

		if(Config._wallUpJumpOrthogonalTolerance.value != 0F)
		{
			float aligned = jumpAngle;
			while(aligned > 45F)
				aligned -= 90F;

			if(Math.abs(aligned) < Config._wallUpJumpOrthogonalTolerance.value)
				jumpAngle = Math.round(jumpAngle / 90F) * 90F;
		}

		if(tryJump(jumpType, null, null, jumpAngle))
		{
			continueWallJumping = !isHeadJumping;
			sp.isCollidedHorizontally = false;
			sp.rotationYaw = jumpAngle;
			onStartWallJump(jumpAngle);
		}
	}

	public boolean tryJump(int type, Boolean inWaterOrNull, Boolean isRunningOrNull, Float angle)
	{
		boolean noVertical = false;
		if(type == Config.WallUpSlide || type == Config.WallHeadSlide)
		{
			type = type == Config.WallUpSlide ? Config.WallUp : Config.WallHead;
			noVertical = true;
		}

		boolean inWater = inWaterOrNull != null ? inWaterOrNull : isDipping;
		boolean isRunning = isRunningOrNull != null ? isRunningOrNull : isRunning();
		boolean charged = type == Config.ChargeUp;
		boolean up = type == Config.Up || type == Config.ChargeUp || type == Config.HeadUp || type == Config.ClimbUp || type == Config.ClimbUpHandsOnly || type == Config.ClimbBackUp || type == Config.ClimbBackUpHandsOnly || type == Config.ClimbBackHead || type == Config.ClimbBackHeadHandsOnly || type == Config.Angle || type == Config.WallUp || type == Config.WallHead;
		boolean head = type == Config.HeadUp || type == Config.ClimbBackHead || type == Config.ClimbBackHeadHandsOnly || type == Config.WallHead;

		int speed = getJumpSpeed(isStanding, isSlow, isRunning, isFast, angle);
		boolean enabled = Config.isJumpingEnabled(speed, type);
		if(enabled)
		{
			boolean exhausionEnabled = Config.isJumpExhaustionEnabled(speed, type);
			if(exhausionEnabled)
			{
				float maxExhausionForJump = Config.getJumpExhaustionStop(speed, type, jumpCharge);
				if(exhaustion > maxExhausionForJump)
					return false;

				maxExhaustionToStartAction = Math.min(maxExhaustionToStartAction, maxExhausionForJump);
				maxExhaustionForAction = Math.min(maxExhaustionForAction, maxExhausionForJump + Config.getJumpExhaustionGain(speed, type, jumpCharge));
			}

			float jumpFactor = sp.isPotionActive(Potion.jump) ? 1 + (sp.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.2F : 1;
			float horizontalJumpFactor = Config.getJumpHorizontalFactor(speed, type) * jumpFactor;
			float verticalJumpFactor = Config.getJumpVerticalFactor(speed, type) * jumpFactor;
			float jumpChargeFactor = charged ? Config.getJumpChargeFactor(jumpCharge) : 1F;

			if(!up)
			{
				horizontalJumpFactor = MathHelper.sqrt_float(horizontalJumpFactor * horizontalJumpFactor + verticalJumpFactor * verticalJumpFactor);
				verticalJumpFactor = 0;
			}

			Double maxHorizontalMotion = null;
			double horizontalMotion = MathHelper.sqrt_double(jumpMotionX * jumpMotionX + jumpMotionZ * jumpMotionZ);
			double verticalMotion = -0.078 + 0.498 * verticalJumpFactor * jumpChargeFactor;

			if(horizontalJumpFactor > 1F && !sp.isCollidedHorizontally)
				maxHorizontalMotion = (double)Config.getMaxHorizontalMotion(speed, type, inWater) * getSpeedFactor();

			if(head)
			{
				double normalAngle = Math.atan(verticalMotion / horizontalMotion);
				double totalMotion = Math.sqrt(verticalMotion * verticalMotion + horizontalMotion * horizontalMotion);

				double newAngle = Config.getHeadJumpFactor(headJumpCharge) * normalAngle;
				double newVerticalMotion = totalMotion * Math.sin(newAngle);
				double newHorizontalMotion = totalMotion * Math.cos(newAngle);

				if(maxHorizontalMotion != null)
					maxHorizontalMotion = maxHorizontalMotion * (newHorizontalMotion / horizontalMotion);

				verticalMotion = newVerticalMotion;
				horizontalMotion = newHorizontalMotion;
			}

			if(angle != null)
			{
				float jumpAngle = angle / RadiantToAngle;
				boolean reset = type == Config.WallUp || type == Config.WallHead;

				double horizontal = Math.max(horizontalMotion, horizontalJumpFactor);
				double moveX = -Math.sin(jumpAngle);
				double moveZ = Math.cos(jumpAngle);

				sp.motionX = getJumpMoving(jumpMotionX, moveX, reset, horizontal, horizontalJumpFactor);
				sp.motionZ = getJumpMoving(jumpMotionZ, moveZ, reset, horizontal, horizontalJumpFactor);

				horizontalMotion = 0;
				verticalMotion = verticalJumpFactor;
			}

			if(horizontalMotion > 0)
			{
				double absoluteMotionX = Math.abs(sp.motionX) * horizontalJumpFactor;
				double absoluteMotionZ = Math.abs(sp.motionZ) * horizontalJumpFactor;

				if(maxHorizontalMotion != null)
				{
					absoluteMotionX = Math.min(absoluteMotionX, maxHorizontalMotion * (horizontalJumpFactor * (Math.abs(sp.motionX) / horizontalMotion)));
					absoluteMotionZ = Math.min(absoluteMotionZ, maxHorizontalMotion * (horizontalJumpFactor * (Math.abs(sp.motionZ) / horizontalMotion)));
				}

				sp.motionX = Math.signum(sp.motionX) * absoluteMotionX;
				sp.motionZ = Math.signum(sp.motionZ) * absoluteMotionZ;
			}

			if(up && !noVertical)
			{
				sp.motionY = verticalMotion;
				sp.addStat(StatList.jumpStat, 1);
				isSprintJump = isFast;
			}

			if(exhausionEnabled)
			{
				float exhaustionFromJump = Config.getJumpExhaustionGain(speed, type, jumpCharge);
				exhaustion += exhaustionFromJump;
			}

			if(head)
			{
				isHeadJumping = true;
				setHeightOffset(-1);
			}
			sp.isAirBorne = true;
			isJumping = true;
			onLivingJump();
		}
		return enabled;
	}

	private static double getJumpMoving(double actual, double move, boolean reset, double horizontal, float horizontalJumpFactor)
	{
		if(!reset)
			return actual + move * horizontal;
		else if(Math.signum(actual) != Math.signum(move))
			return move * horizontalJumpFactor;
		else
			return Math.max(Math.abs(actual), Math.abs(move) * horizontal) * Math.signum(move);
	}

	private static int getJumpSpeed(boolean isStanding, boolean isSneaking, boolean isRunning, boolean isSprinting, Float angle)
	{
		isSprinting &= angle == null;
		isRunning &= angle == null;

		if(isSprinting)
			return Config.Sprinting;
		else if(isRunning)
			return Config.Running;
		else if(isSneaking)
			return Config.Sneaking;
		else if(isStanding)
			return Config.Standing;
		else
			return Config.Walking;
	}

	private void standupIfPossible()
	{
		if(heightOffset >= 0)
			return;

		double gapUnderneight = getGapUnderneight();
		boolean groundClose = gapUnderneight < 1D;
		if(!groundClose)
			resetHeightOffset();
		else
		{
			double gapOverneight = groundClose ? getGapOverneight() : -1D;
			boolean standUpPossible = gapUnderneight + gapOverneight >= 1D;

			if(standUpPossible)
				standUp(gapUnderneight);
			else
				toSlidingOrCrawling(gapUnderneight);
		}
	}

	private void standupIfPossible(boolean tryLanding, boolean restoreFromFlying)
	{
		if(heightOffset >= 0)
			return;

		double gapUnderneight = getGapUnderneight();
		boolean groundClose = gapUnderneight < 1D;
		double gapOverneight = groundClose ? getGapOverneight() : -1D;
		boolean standUpPossible = gapUnderneight + gapOverneight >= 1D;

		if(tryLanding && groundClose && standUpPossible)
		{
			isFlying = false;
			sp.capabilities.isFlying = false;
			restoreFromFlying = true;
		}

		if(!restoreFromFlying)
			return;

		if(!groundClose && !sneakButton.Pressed)
			resetHeightOffset();
		else if(standUpPossible && !(sneakButton.Pressed && grabButton.Pressed))
			standUp(gapUnderneight);
		else
			toSlidingOrCrawling(gapUnderneight);
	}

	private void standUp(double gapUnderneight)
	{
		move(0, (1D - gapUnderneight), 0, true);
		isCrawling = false;
		isHeadJumping = false;
		resetHeightOffset();
	}

	private void toSlidingOrCrawling(double gapUnderneight)
	{
		move(0, (-gapUnderneight), 0, true);

		if(Config.isSlidingEnabled() && (grabButton.Pressed || wasHeadJumping))
			isSliding = true;
		else
			wasCrawling = toCrawling();
	}

	private void handleCrash(float fallDamageStartDistance, float fallDamageFactor)
	{
		if(sp.fallDistance >= 2.0F)
			sp.addStat(StatList.distanceFallenStat, (int)Math.round(sp.fallDistance * 100D));

		if(sp.fallDistance >= fallDamageStartDistance)
		{
			sp.attackEntityFrom(DamageSource.fall, (int)Math.ceil((sp.fallDistance - fallDamageStartDistance) * fallDamageFactor));
			distanceClimbedModified = nextClimbDistance; // to force step sound
		}
		sp.fallDistance = 0F;
	}

	public void beforeSetPositionAndRotation()
	{
		if(sp.worldObj.isRemote)
		{
			initialized = false;
			multiPlayerInitialized = 5;
		}
	}

	public void updateEntityActionState(boolean startSleeping)
	{
		jumpAvoided = false;

		prevMaxExhaustionForAction = maxExhaustionForAction;
		prevMaxExhaustionToStartAction = maxExhaustionToStartAction;

		maxExhaustionForAction = Float.MAX_VALUE;
		maxExhaustionToStartAction = Float.MAX_VALUE;

		boolean isLevitating = sp.capabilities.isFlying && !isFlying;
		boolean isRunning = isRunning();

		toggleButton.update(Options.keyBindConfigToggle);
		speedIncreaseButton.update(Options.keyBindSpeedIncrease);
		speedDecreaseButton.update(Options.keyBindSpeedDecrease);

		if(toggleButton.StartPressed)
		{
			if(Config == Options)
				Config.toggle();
			else
				SmartMovingPacketStream.sendConfigChange(SmartMovingComm.instance);
		}

		if(Config.isUserSpeedEnabled() && !Config.isUserSpeedAlwaysDefault() && (speedIncreaseButton.StartPressed || speedDecreaseButton.StartPressed))
		{
			int difference = 0;
			if (speedIncreaseButton.StartPressed)
				difference++;
			if (speedDecreaseButton.StartPressed)
				difference--;

			if(difference != 0)
			{
				if(Config == Options)
					Config.changeSpeed(difference);
				else
					SmartMovingPacketStream.sendSpeedChange(SmartMovingComm.instance, difference, null);
			}
		}

		boolean initializeCrawling = false;
		if(!initialized && !(sp.worldObj.isRemote && multiPlayerInitialized != 0) && !sp.isRiding())
		{
			if(getMaxPlayerSolidBetween(sp.boundingBox.minY, sp.boundingBox.maxY, 0) > sp.boundingBox.minY)
			{
				initializeCrawling = true;
				toCrawling();
			}

			initialized = true;
		}

		if(multiPlayerInitialized > 0)
			multiPlayerInitialized--;

		if(!esp.movementInput.jump)
			isStillSwimmingJump = false;

		if(!startSleeping)
		{
			isp.localUpdateEntityActionState();
			isp.setMoveStrafingField(Math.signum(esp.movementInput.moveStrafe));
			isp.setMoveForwardField(Math.signum(esp.movementInput.moveForward));
			isp.setIsJumpingField(
				esp.movementInput.jump && !isCrawling && !isSliding &&
				!(Config.isHeadJumpingEnabled() && grabButton.Pressed && sp.isSprinting()) &&
				!(Config.isJumpChargingEnabled() && wouldIsSneaking && sp.onGround && isStanding) &&
				!blockJumpTillButtonRelease);
		}

		boolean isRiding = sp.ridingEntity != null;
		boolean isSleeping = isp.getSleepingField();
		boolean disabled = !Config.enabled || isRiding || isSleeping || startSleeping;

		Minecraft minecraft = isp.getMcField();
		GameSettings gameSettings = minecraft.gameSettings;

		forwardButton.update(gameSettings.keyBindForward);
		leftButton.update(gameSettings.keyBindLeft);
		rightButton.update(gameSettings.keyBindRight);
		backButton.update(gameSettings.keyBindBack);
		jumpButton.update(esp.movementInput.jump);
		sprintButton.update(gameSettings.keyBindSprint);
		sneakButton.update(esp.movementInput.sneak);
		grabButton.update(Options.keyBindGrab);

		double horizontalSpeedSquare = sp.motionX * sp.motionX + sp.motionZ * sp.motionZ;
		// double verticalSpeedSquare = (sp.motionY + HoldMotion) * (sp.motionY + HoldMotion);
		// double speedSquare = horizontalSpeedSquare + verticalSpeedSquare;

		boolean blocked = minecraft.currentScreen != null && !minecraft.currentScreen.allowUserInput;

		boolean mustCrawl = false;
		double crawlStandUpBottom = -1;
		if(isCrawling || isClimbCrawling)
		{
			crawlStandUpBottom = getMaxPlayerSolidBetween(sp.boundingBox.minY - (initializeCrawling ? 0D : 1D), sp.boundingBox.minY, Config._crawlOverEdge.value ? 0 : -0.05);
			double crawlStandUpCeiling = getMinPlayerSolidBetween(sp.boundingBox.maxY, sp.boundingBox.maxY + 1.1D, 0);
			mustCrawl = crawlStandUpCeiling - crawlStandUpBottom < sp.height - heightOffset;
		}

		if(esp.capabilities.isFlying && (Config.isFlyingEnabled() || Config.isLevitateSmallEnabled()))
			mustCrawl = false;

		boolean inputContinueCrawl = Options.isCrawlToggleEnabled() ? crawlToggled : sneakButton.Pressed || !Config.isFreeClimbingEnabled() && grabButton.Pressed;
		if(contextContinueCrawl)
		{
			if(inputContinueCrawl || sp.isInWater() || mustCrawl)
				contextContinueCrawl = false;
			else if(isCrawling)
			{
				double crawlStandUpLiquidCeiling = getMinPlayerLiquidBetween(sp.boundingBox.maxY, sp.boundingBox.maxY + 1.1D);
				if(crawlStandUpLiquidCeiling - crawlStandUpBottom >= sp.height + 1F)
					contextContinueCrawl = false;
			}
		}
		boolean wouldWantCrawl =
			!esp.capabilities.isFlying &&
			(
				(isCrawling && (inputContinueCrawl || contextContinueCrawl)) ||
				(
					grabButton.StartPressed &&
					(sneakToggled || sneakButton.Pressed) &&
					sp.onGround
				)
			);

		boolean wantCrawl =
			Config.isCrawlingEnabled() &&
			wouldWantCrawl;

		boolean canCrawl =
			!isSwimming &&
			!isDiving &&
			(!isDipping || (dippingDepth + heightOffset) < SwimCrawlWaterTopBorder) &&
			!isClimbing &&
			sp.fallDistance < Config._fallingDistanceMinimum.value;

		wasCrawling = isCrawling;
		isCrawling =
			canCrawl &&
			(wantCrawl || mustCrawl);

		if(!isCrawling)
			contextContinueCrawl = false;

		if (wasCrawling && !isCrawling && esp.capabilities.isFlying)
			tryJump(Config.Up, null, null, null);

		wantCrawlNotClimb =
			(
				wantCrawlNotClimb ||
				(
						grabButton.StartPressed &&
						!wasCrawling
				)
			) &&
			grabButton.Pressed &&
			esp.movementInput.moveForward > 0F &&
			isCrawling &&
			sp.isCollidedHorizontally;

		boolean isFacedToSolidVine = isFacedToSolidVine(isClimbCrawling);

		boolean wouldWantClimb =
			(
				grabButton.Pressed ||
				(isClimbHolding && sneakButton.Pressed) ||
				(Config.isFreeClimbAutoLaddderEnabled() && isFacedToLadder(isClimbCrawling)) ||
				(Config.isFreeClimbAutoVineEnabled() && isFacedToSolidVine)
			) &&
			(!isSliding || grabButton.Pressed && esp.movementInput.moveForward > 0F) &&
			!isHeadJumping &&
			!wantCrawlNotClimb &&
			!disabled;

		boolean wantClimb =
			Config.isFreeClimbingEnabled() &&
			wouldWantClimb;

		if(!wantClimb || sp.isCollidedVertically)
			isClimbJumping = false;

		if(sp.isCollided)
			isClimbBackJumping = false;

		wantClimbUp =
			wantClimb &&
			esp.movementInput.moveForward > 0F || (isVineAnyClimbing && jumpButton.Pressed && !(sneakButton.Pressed && isFacedToSolidVine))&&
			(!isCrawling || sp.isCollidedHorizontally) &&
			(!isSliding || sp.isCollidedHorizontally);

		wantClimbDown =
			wantClimb &&
			esp.movementInput.moveForward <= 0F &&
			!wantCrawl;

		wantClimbCeiling =
			Config.isCeilingClimbingEnabled() &&
			grabButton.Pressed &&
			!wantCrawlNotClimb &&
			!isSneaking() &&
			!disabled;

		boolean restoreFromFlying = false;

		boolean wasFlying = isFlying;
		isFlying = Config.isFlyingEnabled() && sp.capabilities.isFlying && !isSwimming && !isDiving;
		if(isFlying && !wasFlying)
			setHeightOffset(-1);
		else if(!isFlying && wasFlying)
			restoreFromFlying = true;

		if(!Config.isFlyingEnabled() && Config.isLevitateSmallEnabled())
		{
			if(isLevitating && !wasLevitating)
				setHeightOffset(-1);
			else if(!isLevitating && wasLevitating)
				restoreFromFlying = true;
		}

		wasHeadJumping = isHeadJumping;
		isHeadJumping = isHeadJumping &&
			!sp.onGround &&
			!(isSwimming || isDiving) &&
			!(isFlying || sp.capabilities.isFlying) &&
			!(sp.handleWaterMovement() && sp.motionY < 0) &&
			!sp.handleLavaMovement();

		if(!isHeadJumping)
			isAerodynamic = false;

		if(wasHeadJumping && !isHeadJumping)
			if(sp.onGround)
			{
				handleCrash(Config._headFallDamageStartDistance.value, Config._headFallDamageFactor.value);
				restoreFromFlying = true;
			}

		boolean tryLanding = isFlying && !Options._flyCloseToGround.value && horizontalSpeedSquare < 0.003D && sp.motionY > -0.03D;
		if(restoreFromFlying || tryLanding)
			standupIfPossible(tryLanding, restoreFromFlying);

		if(isSliding && sp.fallDistance > SlideToHeadJumpingFallDistance)
		{
			isSliding = false;
			isHeadJumping = true;
			isAerodynamic = true;
		}

		if(Config.isSlidingEnabled() && grabButton.Pressed && (isGroundSprinting || (wasRunning && !isRunning && sp.onGround)) && !isCrawling && sneakButton.StartPressed && !isDipping)
		{
			setHeightOffset(-1);
			move(0, (-1D), 0, true);
			tryJump(Config.SlideDown, false, wasRunning, null);
			isSliding = true;
			isHeadJumping = false;
			isAerodynamic = false;
		}

		if(isSliding && (!sneakButton.Pressed || horizontalSpeedSquare < Config._slidingSpeedStopFactor.value * 0.01))
		{
			isSliding = false;
			wasCrawling = toCrawling();
		}

		if(isSliding && sp.fallDistance > Config._fallingDistanceMinimum.value)
		{
			isSliding = false;
			wasCrawling = true;
			isCrawling = false;
		}

		boolean sneakContinueInput = Options.isSneakToggleEnabled() ? sneakToggled || sneakButton.StartPressed : sneakButton.Pressed;
		boolean wouldWantSneak =
			!isFlying &&
			!isSliding &&
			!isHeadJumping &&
			!(isDiving && Config._diveDownOnSneak.value) &&
			!(isSwimming && Config._swimDownOnSneak.value && !isFakeShallowWaterSneaking) &&
			sneakContinueInput &&
			!wantCrawl &&
			!mustCrawl &&
			(!Config.isCrawlingEnabled() || !grabButton.Pressed);

		boolean wantSneak =
			Config.isSneakingEnabled() &&
			wouldWantSneak;

		boolean moveButtonPressed = esp.movementInput.moveForward != 0F || esp.movementInput.moveStrafe != 0F;
		boolean moveForwardButtonPressed = esp.movementInput.moveForward > 0F;

		wantSprint =
			Config.isSprintingEnabled() &&
			!isSliding &&
			sprintButton.Pressed &&
			(
				moveForwardButtonPressed ||
				isClimbing ||
				(
					isSwimming &&
					(moveButtonPressed || (sneakButton.Pressed && Config._swimDownOnSneak.value))
				) ||
				(
					isDiving &&
					(moveButtonPressed || jumpButton.Pressed || (sneakButton.Pressed && Config._diveDownOnSneak.value))
				) ||
				(
					isFlying &&
					(moveButtonPressed || jumpButton.Pressed || sneakButton.Pressed)
				)
			) &&
			!disabled;

		boolean exhaustionAllowsRunning =
			!Config.isRunExhaustionEnabled() ||
			(
					exhaustion < Config._runExhaustionStop.value &&
					(wasRunning || exhaustion < Config._runExhaustionStart.value)
			);

		if(isRunning && sp.onGround && Config.isRunExhaustionEnabled())
		{
			maxExhaustionForAction = Math.min(maxExhaustionForAction, Config._runExhaustionStop.value);
			maxExhaustionToStartAction = Math.min(maxExhaustionToStartAction, Config._runExhaustionStart.value);
		}

		if(!exhaustionAllowsRunning && isRunning)
			sp.setSprinting(isRunning = false);

		if(!sp.onGround && isFast && !isClimbing && !isCeilingClimbing && !isDiving && !isSwimming)
			isSprintJump = true;

		boolean exhaustionAllowsSprinting =
			!Config.isSprintExhaustionEnabled() ||
			(
					exhaustion <= Config._sprintExhaustionStop.value &&
					(isFast || isSprintJump || exhaustion <= Config._sprintExhaustionStart.value)
			);

		if(sp.onGround || isFlying || isSwimming || isDiving || sp.handleLavaMovement())
			isSprintJump = false;

		boolean preferSprint = false;
		if(wantSprint && !wantSneak)
		{
			if(!isSprintJump && Config.isSprintExhaustionEnabled())
			{
				maxExhaustionForAction = Math.min(maxExhaustionForAction, Config._sprintExhaustionStop.value);
				maxExhaustionToStartAction = Math.min(maxExhaustionToStartAction, Config._sprintExhaustionStart.value);
			}

			if(exhaustionAllowsSprinting)
				preferSprint = true;
		}

		boolean isClimbSprintSpeed = true;
		if(isClimbing && preferSprint)
		{
			double minTickDistance;
			if(wantClimbUp)
				minTickDistance = 0.07 * Config._freeClimbingUpSpeedFactor.value;
			else if(wantClimbDown)
				minTickDistance = 0.11 * Config._freeClimbingDownSpeedFactor.value;
			else
				minTickDistance = 0.07;

			isClimbSprintSpeed = net.smart.render.statistics.SmartStatisticsFactory.getInstance(sp).getTickDistance() >= minTickDistance;
		}

		boolean canAnySprint = preferSprint && !sp.isBurning() && (Config._sprintDuringItemUsage.value || !sp.isUsingItem());
		boolean canVerticallySprint = canAnySprint && !sp.isCollidedVertically;
		boolean canHorizontallySprint = canAnySprint && collidedHorizontallyTickCount < 3;
		boolean canAllSprint = canHorizontallySprint && canVerticallySprint;

		boolean wasGroundSprinting = isGroundSprinting;
		isGroundSprinting = canHorizontallySprint && sp.onGround && !isSwimming && !isDiving && !isClimbing;
		boolean isSwimSprinting = canHorizontallySprint && isSwimming;
		boolean isDiveSprinting = canAllSprint && isDiving;
		boolean isCeilingSprinting = canHorizontallySprint && isCeilingClimbing;
		boolean isFlyingSprinting = canAllSprint && isFlying;
		boolean isClimbSprinting = canAnySprint && isClimbing && isClimbSprintSpeed;

		isFast =
			isGroundSprinting ||
			isClimbSprinting ||
			isSwimSprinting ||
			isDiveSprinting ||
			isCeilingSprinting ||
			isFlyingSprinting ||
			isClimbSprinting;

		if(isGroundSprinting && !wasGroundSprinting)
		{
			wasRunningWhenSprintStarted = sp.isSprinting();
			sp.setSprinting(isStandupSprintingOrRunning());
		}
		else if(wasGroundSprinting && !isGroundSprinting)
			sp.setSprinting(wasRunningWhenSprintStarted);

		wouldIsSneaking =
			wouldWantSneak &&
			!wantSprint &&
			!isClimbing;

		boolean wasSneaking = isSlow;
		isSlow =
			wantSneak &&
			wouldIsSneaking;

		boolean wantClimbHolding =
			(isClimbHolding && sneakButton.Pressed) ||
			(isClimbing && blocked) ||
			(wantClimb &&
			!isSwimming &&
			!isDiving &&
			!isCrawling &&
			(sneakButton.Pressed || crawlToggled));

		isClimbHolding =
			wantClimbHolding &&
			isClimbing;

		isStanding = horizontalSpeedSquare < 0.0005;

		boolean wasCrawlClimbing = isCrawlClimbing;
		isCrawlClimbing = (wasCrawling || isCrawlClimbing) && isClimbing && isNeighborClimbing && (sneakButton.Pressed || crawlToggled) && esp.movementInput.moveForward > 0F;
		if(isCrawlClimbing)
		{
			boolean canStandUp = !isPlayerInSolidBetween(sp.boundingBox.minY - (isClimbCrawling ? 0.95D : 1D), sp.boundingBox.minY);
			if(canStandUp)
			{
				wasCrawlClimbing = false;
				isCrawlClimbing = false;
				if(!isClimbCrawling)
					resetHeightOffset();
			}

			if(!wasCrawlClimbing)
			{
				wasCrawling = false;
				isCrawling = false;
			}
		}
		else if(wasCrawlClimbing)
		{
			boolean toCrawling = sneakButton.Pressed || crawlToggled;
			if(!isClimbing)
			{
				wasCrawling = toCrawling();

				double minY = sp.boundingBox.minY;
				move(0, (-minY + Math.floor(minY)), 0, true);
			}
			else if(esp.movementInput.moveForward <= 0F)
			{
				wasCrawling = toCrawling;
				isCrawling = toCrawling;

				wantClimbUp = false;
				wantClimbDown = false;

				if(!toCrawling)
					resetHeightOffset();
				double minY = sp.boundingBox.minY;
				move(0, (-minY + Math.floor(minY) + (toCrawling ? 0F : 1F)), 0, true);
			}
			else if(!toCrawling)
			{
				resetHeightOffset();
				double minY = sp.boundingBox.minY;
				move(0, (Math.ceil(minY) - minY), 0, true);
			}
		}

		boolean wasClimbCrawling = isClimbCrawling;
		boolean needClimbCrawling = hasClimbCrawlGap || (hasClimbGap && isClimbHolding);
		boolean canClimbCrawling = wantClimbHolding && wantClimbUp;

		if(climbIntoCount > 1)
			climbIntoCount--;
		else if(isClimbCrawling && !needClimbCrawling && climbIntoCount == 0)
			climbIntoCount = 6;

		isClimbCrawling = canClimbCrawling && ((needClimbCrawling && climbIntoCount == 0) || climbIntoCount > 1);
		if(isClimbCrawling && !wasClimbCrawling)
		{
			setHeightOffset(-1F);

			boolean wasCollidedHorizontally = sp.isCollidedHorizontally; // preserve the horizontal collision state
			move(0, 0.05, 0, true); // to avoid climb crawling into solid when standing with solid above head (SMP: Illegal Stance)
			sp.isCollidedHorizontally = wasCollidedHorizontally; // to avoid climb crawling out of water bug
		}
		else if(!isClimbCrawling && wasClimbCrawling)
		{
			climbIntoCount = 0;
			if(mustCrawl || sneakButton.Pressed || crawlToggled)
			{
				double gapUnderneight = sp.boundingBox.minY - getMaxPlayerSolidBetween(sp.boundingBox.minY - 1D, sp.boundingBox.minY, 0);
				if(gapUnderneight >= 0D && gapUnderneight < 1D)
				{
					wasCrawling = toCrawling();
					move(0, (-gapUnderneight), 0, true);
				}
				else
					resetHeightOffset();
			}
			else
				resetHeightOffset();
		}

		if((wasCrawling && !isCrawling) && !initializeCrawling && !esp.capabilities.isFlying)
		{
			resetHeightOffset();
			move(0, (crawlStandUpBottom - sp.boundingBox.minY), 0, true);
		}
		else if((isCrawling && !wasCrawling) || initializeCrawling)
		{
			setHeightOffset(-1F);

			if(!initializeCrawling || sp.worldObj.isRemote)
				move(0, (-1D), 0, true);

			if(initializeCrawling)
				wasCrawling = toCrawling();
		}

		if(grabButton.StartPressed)
			if(isShallowDiveOrSwim && wouldWantClimb)
			{
				// from swimming/diving in shallow water to walking in shallow water
				resetHeightOffset();
				move(0, (getMaxPlayerSolidBetween(sp.boundingBox.minY, sp.boundingBox.maxY, 0) - sp.boundingBox.minY), 0, true);
				if(jumpButton.Pressed)
					isStillSwimmingJump = true;
			}
			else if(isDipping && wouldWantCrawl && dippingDepth >= SwimCrawlWaterBottomBorder)
				if(dippingDepth >= SwimCrawlWaterMediumBorder)
				{
					// from sneaking in shallow water to swimming/diving in shallow water
					setHeightOffset(-1F);
					move(0, (-1.6F + dippingDepth), 0, true);
					isCrawling = false;
				}
				else
				{
					// from sneaking in shallow water to crawling in shallow water
					setHeightOffset(-1F);
					move(0, (-1D), 0, true);
					wasCrawling = toCrawling();
				}

		isWallJumping = false;

		if(continueWallJumping && (sp.onGround || isClimbing || !jumpButton.Pressed))
			continueWallJumping = false;

		boolean canWallJumping = Config.isWallJumpEnabled() && !isHeadJumping && !sp.onGround && !isClimbing && !isSwimming && !isDiving && !isLevitating && !isFlying;
		boolean triggerWallJumping = false;

		if(Options._wallJumpDoubleClick.value)
		{
			if(canWallJumping)
			{
				if(jumpButton.StartPressed)
				{
					if(wallJumpCount == 0)
						wallJumpCount = Options.wallJumpDoubleClickTicks();
					else
					{
						triggerWallJumping = true;
						wallJumpCount = 0;
					}
				}
				else if(wallJumpCount > 0)
					wallJumpCount--;
			}
			else
				wallJumpCount = 0;
		}
		else
			triggerWallJumping = jumpButton.StartPressed;

		wantWallJumping = canWallJumping &&
			(triggerWallJumping || continueWallJumping ||
			(wantWallJumping && jumpButton.Pressed && !sp.isCollidedHorizontally));

		boolean canAngleJump = !isSleeping && sp.onGround && !isCrawling && !isClimbing && !isClimbCrawling && !isSwimming && !isDiving;
		boolean canSideJump = Config.isSideJumpEnabled() && canAngleJump;
		boolean canLeftJump = canSideJump && !rightButton.Pressed;
		boolean canRightJump = canSideJump && !leftButton.Pressed;
		boolean canBackJump = Config.isBackJumpEnabled() && canAngleJump && !forwardButton.Pressed && !isStandupSprintingOrRunning();

		if(canLeftJump)
		{
			if(leftButton.StartPressed)
			{
				if(leftJumpCount == 0)
					leftJumpCount = Options.angleJumpDoubleClickTicks();
				else
					leftJumpCount = -1;
			}
			else if(leftJumpCount > 0)
				leftJumpCount--;
		}
		else
			leftJumpCount = 0;

		if(canRightJump)
		{
			if(rightButton.StartPressed)
			{
				if(rightJumpCount == 0)
					rightJumpCount = Options.angleJumpDoubleClickTicks();
				else
					rightJumpCount = -1;
			}
			else if(rightJumpCount > 0)
				rightJumpCount--;
		}
		else
			rightJumpCount = 0;

		if(canBackJump)
		{
			if(backButton.StartPressed)
			{
				if(backJumpCount == 0)
					backJumpCount = Options.angleJumpDoubleClickTicks();
				else
					backJumpCount = -1;
			}
			else if(backJumpCount > 0)
				backJumpCount--;
		}
		else
			backJumpCount = 0;

		if(rightJumpCount == -2 && backJumpCount <= 0)
			rightJumpCount = -1;
		if(leftJumpCount == -2 && backJumpCount <= 0)
			leftJumpCount = -1;
		if(backJumpCount == -2 && (leftJumpCount <= 0 || rightJumpCount <= 0))
			backJumpCount = -1;

		if(rightJumpCount == -1 && backJumpCount > 0)
			rightJumpCount = -2;
		if(leftJumpCount == -1 && backJumpCount > 0)
			leftJumpCount = -2;
		if(backJumpCount == -1 && (leftJumpCount > 0 || rightJumpCount > 0))
			backJumpCount = -2;

		if(sp.onGround || sp.isCollidedVertically)
			angleJumpType = 0;

		isRopeSliding = isRopeSliding();

		boolean isSneakToggleEnabled = Options.isSneakToggleEnabled();
		boolean isCrawlToggleEnabled = Options.isCrawlToggleEnabled();

		boolean willStopCrawl = false;
		boolean willStopCrawlStartSneak = false;
		if(isSneakToggleEnabled || isCrawlToggleEnabled)
		{
			if(isCrawling && jumpButton.StopPressed)
				willStopCrawlStartSneak = true;
			if(isCrawling && sneakButton.StopPressed && !ignoreNextStopSneakButtonPressed)
				willStopCrawlStartSneak = true;
			if(!isCrawling && !isCrawlClimbing && !isClimbCrawling)
				willStopCrawl = true;

			willStopCrawl |= willStopCrawlStartSneak;
		}

		boolean willStopSneak = false;
		if(isSneakToggleEnabled)
		{
			if(isCrawling && !willStopCrawlStartSneak)
				willStopSneak = true;
			if(wantSneak && wantSprint && sneakButton.StartPressed && sneakToggled)
			{
				willStopSneak = true;
				ignoreNextStopSneakButtonPressed = true;
			}
			if(wasSneaking && sneakButton.StartPressed)
					willStopSneak = true;
			if(!isSwimming && !isDiving && jumpButton.StopPressed)
				willStopSneak = true;
		}

		boolean willStartSneak = false;
		if(isSneakToggleEnabled)
		{
			if(willStopCrawlStartSneak && sneakButton.StopPressed)
				willStartSneak = true;
			if(isFast && sneakButton.StopPressed && !ignoreNextStopSneakButtonPressed)
				willStartSneak = true;
			if(isSlow && !wasSneaking)
				willStartSneak = true;
		}

		boolean willStartCrawl = false;
		if(isCrawlToggleEnabled)
		{
			if(isCrawling && !wasCrawling)
				willStartCrawl = true;
			if(isClimbCrawling && !wasClimbCrawling)
				willStartCrawl = true;
		}

		if(isSneakToggleEnabled)
		{
			if(willStartSneak)
				sneakToggled = true;
			if(willStopSneak)
				sneakToggled = false;
		}

		if(isCrawlToggleEnabled)
		{
			if(willStartCrawl)
			{
				crawlToggled = true;
				ignoreNextStopSneakButtonPressed = sneakButton.Pressed;
			}
			if(willStopCrawl)
				crawlToggled = false;
		}

		if(sneakButton.StopPressed)
			ignoreNextStopSneakButtonPressed = false;

		wasRunning = isRunning;
		wasLevitating = isLevitating;
	}

	private boolean toCrawling()
	{
		isCrawling = true;
		if(Options.isCrawlToggleEnabled())
			crawlToggled = true;
		ignoreNextStopSneakButtonPressed = true;
		return true;
	}

	public double prevMotionX;
	public double prevMotionY;
	public double prevMotionZ;

	public final Button forwardButton = new Button();
	public final Button leftButton = new Button();
	public final Button rightButton = new Button();
	public final Button backButton = new Button();
	public final Button jumpButton = new Button();
	public final Button sneakButton = new Button();
	public final Button grabButton = new Button();
	public final Button sprintButton = new Button();
	public final Button toggleButton = new Button();
	public final Button speedIncreaseButton = new Button();
	public final Button speedDecreaseButton = new Button();

	public boolean wasRunning;
	public boolean wasLevitating;
	public boolean wasCrawling;
	public boolean wasHeadJumping;

	private boolean contextContinueCrawl;
	private boolean ignoreNextStopSneakButtonPressed;
	private int collidedHorizontallyTickCount;
	private boolean wantWallJumping;
	private boolean continueWallJumping;
	private boolean wasCollidedHorizontally;
	private boolean wasRunningWhenSprintStarted;

	private boolean jumpAvoided;


	private int climbIntoCount;

	private int leftJumpCount;
	private int rightJumpCount;
	private int backJumpCount;
	private int wallJumpCount;

	private int nextClimbDistance;
	public float distanceClimbedModified;

	private boolean sneakToggled = false;
	private boolean crawlToggled = false;

	private int lastWorldPlayerEntitiesSize = -1;
	private int lastWorldPlayerLastEnttyId = -1;

	public void addToSendQueue()
	{
		if(!sp.worldObj.isRemote)
			return;

		boolean isSmall = sp.height < 1;

		long state = 0;
		state |= isp.localIsSneaking() ? 1 : 0;

		state <<= 1;
		state |= isRopeSliding ? 1 : 0;

		state <<= 1;
		state |= isWallJumping ? 1 : 0;

		state <<= 1;
		state |= isFast ? 1 : 0;

		state <<= 1;
		state |= isSlow ? 1 : 0;

		state <<= 1;
		state |= isClimbBackJumping ? 1 : 0;

		state <<= 1;
		state |= isClimbJumping ? 1 : 0;

		state <<= 1;
		state |= isHandsVineClimbing ? 1 : 0;

		state <<= 1;
		state |= isFeetVineClimbing ? 1 : 0;

		state <<= 3;
		state |= angleJumpType;

		state <<= 1;
		state |= isSliding ? 1 : 0;

		state <<= 1;
		state |= isHeadJumping ? 1 : 0;

		state <<= 1;
		state |= isLevitating ? 1 : 0;

		state <<= 1;
		state |= isCeilingClimbing ? 1 : 0;

		state <<= 1;
		state |= doFlyingAnimation() ? 1 : 0;

		state <<= 1;
		state |= doFallingAnimation() ? 1 : 0;

		state <<= 1;
		state |= isSmall ? 1 : 0;

		state <<= 1;
		state |= isClimbing ? 1 : 0;

		state <<= 1;
		state |= isCrawling ? 1 : 0;

		state <<= 1;
		state |= isCrawlClimbing ? 1 : 0;

		state <<= 1;
		state |= isSwimming ? 1 : 0;

		state <<= 1;
		state |= isDipping ? 1 : 0;

		state <<= 1;
		state |= isDiving ? 1 : 0;

		state <<= 1;
		state |= isp.getIsJumpingField() ? 1 : 0;

		state <<= 4;
		state |= actualHandsClimbType;

		state <<= 4;
		state |= actualFeetClimbType;

		boolean sendStatePacket = state != prevPacketState;

		int currentWorldPlayerEntitiesSize = sp.worldObj.playerEntities.size();
		if(currentWorldPlayerEntitiesSize == 0)
		{
			sendStatePacket = false;
			lastWorldPlayerEntitiesSize = currentWorldPlayerEntitiesSize;
			lastWorldPlayerLastEnttyId = -1;
		}
		else
		{
			int currentWorldPlayerLastEnttyId = ((Entity)sp.worldObj.playerEntities.get(currentWorldPlayerEntitiesSize - 1)).getEntityId();
			if(currentWorldPlayerEntitiesSize != lastWorldPlayerEntitiesSize)
			{
				if(currentWorldPlayerEntitiesSize > lastWorldPlayerEntitiesSize)
					sendStatePacket = true;
				lastWorldPlayerEntitiesSize = currentWorldPlayerEntitiesSize;
				lastWorldPlayerLastEnttyId = currentWorldPlayerLastEnttyId;
			}
			else if(currentWorldPlayerLastEnttyId != lastWorldPlayerLastEnttyId)
			{
				sendStatePacket = true;
				lastWorldPlayerLastEnttyId = currentWorldPlayerLastEnttyId;
			}
		}

		if(sendStatePacket)
		{
			SmartMovingPacketStream.sendState(SmartMovingComm.instance, sp.getEntityId(), state);
			prevPacketState = state;
		}
	}

	private long prevPacketState;

	@Override
	public boolean isSneaking()
	{
		if(forceIsSneaking != null)
			return forceIsSneaking;

		return (isSlow && (sp.onGround || isp.getIsInWebField())) || (!Config._sneak.value && wouldIsSneaking && jumpCharge > 0) || ((sp.ridingEntity != null || !Config.enabled) && isp.localIsSneaking()) || (!Config._crawlOverEdge.value && isCrawling && !isClimbing);
	}

	public boolean isStandupSprintingOrRunning()
	{
		return (isFast || sp.isSprinting()) && sp.onGround && !isSliding && !isCrawling;
	}

	public boolean isRunning()
	{
		return sp.isSprinting() && !isFast && sp.onGround;
	}

	public void beforeGetSleepTimer()
	{
		SmartMovingRender.renderGuiIngame(isp.getMcField());
	}

	public void jump()
	{
		jumpAvoided = true;
	}

	public void writeEntityToNBT(NBTTagCompound nBTTagCompound)
	{
		isp.localWriteEntityToNBT(nBTTagCompound);
		NBTTagCompound abilities = nBTTagCompound.getCompoundTag("abilities");
		if(abilities != null && abilities.hasKey("flying"))
			abilities.setBoolean("flying", sp.capabilities.isFlying);
	}

	@Override
	public boolean isJumping()
	{
		return isp.getIsJumpingField();
	}

	@Override
	public boolean doFlyingAnimation()
	{
		if(Config.isFlyingEnabled() || Config.isLevitationAnimationEnabled())
			return sp.capabilities.isFlying;
		return false;
	}

	@Override
	public boolean doFallingAnimation()
	{
		if(Config.isFallAnimationEnabled())
			return !sp.onGround && sp.fallDistance > Config._fallAnimationDistanceMinimum.value;
		return false;
	}

	public void onLivingJump()
	{
		net.minecraftforge.common.ForgeHooks.onLivingJump(sp);
	}

	public float getFOVMultiplier()
	{
		if(!Config.enabled)
			return isp.localGetFOVMultiplier();

		float landMovmentFactor = getLandMovementFactor();
		setLandMovementFactor(fadingPerspectiveFactor);
		float result = isp.localGetFOVMultiplier();
		setLandMovementFactor(landMovmentFactor);
		return result;
	}

	public float getLandMovementFactor()
	{
		return sp.getAIMoveSpeed();
	}

	public void setLandMovementFactor(float landMovementFactor)
	{
		Reflect.SetField(ModifiableAttributeInstance.class, sp.getEntityAttribute(SharedMonsterAttributes.movementSpeed), SmartMovingInstall.ModifiableAttributeInstance_attributeValue, landMovementFactor);
	}

	public static boolean isRopeSliding()
	{
		return onZipLine != null && Reflect.GetField(onZipLine, null) != null;
	}

	public void beforeActivateBlockOrUseItem()
	{
		forceIsSneaking = isp.localIsSneaking();
	}

	public void afterActivateBlockOrUseItem()
	{
		forceIsSneaking = null;
	}

	private Boolean forceIsSneaking;

	private static Class<?> ropesPlusClient = Reflect.LoadClass(SmartMovingInstall.class, SmartMovingInstall.RopesPlusClient, false);
	private static Field onZipLine = ropesPlusClient != null ? Reflect.GetField(ropesPlusClient, SmartMovingInstall.RopesPlusClient_onZipLine, false) : null;
}