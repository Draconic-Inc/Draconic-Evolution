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

import net.minecraft.block.*;
import net.minecraft.client.*;
import net.minecraft.client.particle.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;

public abstract class SmartMoving extends SmartMovingBase
{
	public boolean isSlow;
	public boolean isFast;

	public boolean isClimbing;
	public boolean isHandsVineClimbing;
	public boolean isFeetVineClimbing;

	public boolean isClimbJumping;
	public boolean isClimbBackJumping;
	public boolean isWallJumping;
	public boolean isClimbCrawling;
	public boolean isCrawlClimbing;
	public boolean isCeilingClimbing;
	public boolean isRopeSliding;

	public boolean isDipping;
	public boolean isSwimming;
	public boolean isDiving;
	public boolean isLevitating;
	public boolean isHeadJumping;
	public boolean isCrawling;
	public boolean isSliding;
	public boolean isFlying;

	public int actualHandsClimbType;
	public int actualFeetClimbType;

	public int angleJumpType;

	public float heightOffset;

	private float spawnSlindingParticle;
	private float spawnSwimmingParticle;

	public SmartMoving(EntityPlayer sp, IEntityPlayerSP isp)
	{
		super(sp, isp);
	}

	public boolean isAngleJumping()
	{
		return angleJumpType > 1 && angleJumpType < 7;
	}

	public abstract boolean isJumping();

	public abstract boolean doFlyingAnimation();

	public abstract boolean doFallingAnimation();

	protected void spawnParticles(Minecraft minecraft, double playerMotionX, double playerMotionZ)
	{
		float horizontalSpeedSquare = 0;
		if(isSliding || isSwimming)
			horizontalSpeedSquare = (float)(playerMotionX * playerMotionX + playerMotionZ * playerMotionZ);

		if(isSliding)
		{
			int i = MathHelper.floor_double(sp.posX);
			int j = MathHelper.floor_double(sp.boundingBox.minY - 0.1F);
			int k = MathHelper.floor_double(sp.posZ);
			Block block = sp.worldObj.getBlock(i, j, k);
			if(block != null)
			{
				double posY = sp.boundingBox.minY + 0.1D;
				double motionX = -playerMotionX * 4D;
				double motionY = 1.5D;
				double motionZ = -playerMotionZ * 4D;

				spawnSlindingParticle += horizontalSpeedSquare;

				float maxSpawnSlindingParticle = Config._slideParticlePeriodFactor.value * 0.1F;
				while(spawnSlindingParticle > maxSpawnSlindingParticle)
				{
					double posX = sp.posX + getSpawnOffset();
					double posZ = sp.posZ + getSpawnOffset();
					int metaData = sp.worldObj.getBlockMetadata(i, j, k);
					sp.worldObj.spawnParticle("blockcrack_" + Block.getIdFromBlock(block) + "_" + metaData, posX, posY, posZ, motionX, motionY, motionZ);
					spawnSlindingParticle -= maxSpawnSlindingParticle;
				}
			}
		}

		if(isSwimming)
		{
			float posY = MathHelper.floor_double(sp.boundingBox.minY) + 1.0F;
			int i = (int)Math.floor(sp.posX);
			int j = (int)Math.floor(posY - 0.5);
			int k = (int)Math.floor(sp.posZ);

			Block block = sp.worldObj.getBlock(i, j, k);

			boolean isLava = block != null && isLava(block);
			spawnSwimmingParticle += horizontalSpeedSquare;

			float maxSpawnSwimmingParticle = (isLava ? Config._lavaSwimParticlePeriodFactor.value : Config._swimParticlePeriodFactor.value) * 0.01F;
			while(spawnSwimmingParticle > maxSpawnSwimmingParticle)
			{
				double posX = sp.posX + getSpawnOffset();
				double posZ = sp.posZ + getSpawnOffset();
				EntityFX splash = isLava ? new EntityLavaFX(sp.worldObj, posX, posY, posZ) : new EntitySplashFX(sp.worldObj, posX, posY, posZ, 0, 0, 0);
				splash.motionX = 0;
				splash.motionY = 0.2;
				splash.motionZ = 0;
				minecraft.effectRenderer.addEffect(splash);

				spawnSwimmingParticle -= maxSpawnSwimmingParticle;
			}
		}
	}

	private float getSpawnOffset()
	{
		return (sp.getRNG().nextFloat() - 0.5F) * 2F * sp.width;
	}

	protected void onStartClimbBackJump()
	{
		net.smart.render.SmartRenderRender.getPreviousRendererData(sp).rotateAngleY += isHeadJumping ? Half : Quarter;
		isClimbBackJumping = true;
	}

	protected void onStartWallJump(Float angle)
	{
		if (angle != null)
			net.smart.render.SmartRenderRender.getPreviousRendererData(sp).rotateAngleY = angle / RadiantToAngle;
		isWallJumping = true;
		sp.fallDistance = 0F;
	}
}