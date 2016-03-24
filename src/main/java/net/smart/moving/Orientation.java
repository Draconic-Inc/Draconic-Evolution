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
import net.minecraft.block.material.*;
import net.minecraft.client.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.smart.moving.config.*;
import net.smart.utilities.*;

public class Orientation extends SmartMovingContext
{
	public static final Orientation ZZ = new Orientation(0, 0);

	public static final Orientation PZ = new Orientation(1, 0);
	public static final Orientation ZP = new Orientation(0, 1);
	public static final Orientation NZ = new Orientation(-1, 0);
	public static final Orientation ZN = new Orientation(0, -1);

	public static final Orientation PP = new Orientation(1, 1);
	public static final Orientation NN = new Orientation(-1, -1);
	public static final Orientation PN = new Orientation(1, -1);
	public static final Orientation NP = new Orientation(-1, 1);

	public static final int DefaultMeta = -1;
	public static final int VineFrontMeta = 0;
	public static final int VineSideMeta = 1;

	private static final int top = 2;
	private static final int middle = 1;
	private static final int base = 0;
	private static final int sub = -1;
	private static final int subSub = -2;

	private static final int NoGrab = 0;
	private static final int HalfGrab = 1;
	private static final int AroundGrab = 2;

	public static final HashSet<Orientation> Orthogonals = new HashSet<Orientation>();

	static
	{
		Orthogonals.add(PZ);
		Orthogonals.add(ZP);
		Orthogonals.add(NZ);
		Orthogonals.add(ZN);
	}

	protected int _i, _k;
	private boolean _isDiagonal;
	private float _directionAngle;
	private float _mimimumClimbingAngle;
	private float _maximumClimbingAngle;

	private Orientation(int i, int k)
	{
		_i = i;
		_k = k;
		_isDiagonal = _i != 0 && _k != 0;

		setClimbingAngles();
	}

	@SuppressWarnings("incomplete-switch")
	public Orientation rotate(int angle)
	{
		if (this == ZZ)
			throw new RuntimeException("unrotatable orientation");

		switch(angle)
		{
			case 0:
				return this;
			case 45:
				if(this == PZ)
					return PP;
				if(this == PP)
					return ZP;
				if(this == ZP)
					return NP;
				if(this == NP)
					return NZ;
				if(this == NZ)
					return NN;
				if(this == NN)
					return ZN;
				if(this == ZN)
					return PN;
				if(this == PN)
					return PZ;
				throw new RuntimeException("unknown orientation \"" + this + "\"");
			case -45:
				if(this == PZ)
					return PN;
				if(this == PN)
					return ZN;
				if(this == ZN)
					return NN;
				if(this == NN)
					return NZ;
				if(this == NZ)
					return NP;
				if(this == NP)
					return ZP;
				if(this == ZP)
					return PP;
				if(this == PP)
					return PZ;
				throw new RuntimeException("unknown orientation \"" + this + "\"");
			case 90:
				return rotate(45).rotate(45);
			case -90:
				return rotate(-45).rotate(-45);
			case 135:
				return rotate(180).rotate(-45);
			case -135:
				return rotate(-180).rotate(45);
			case 180:
			case -180:
				if(this == PZ)
					return NZ;
				if(this == PN)
					return NP;
				if(this == ZN)
					return ZP;
				if(this == NN)
					return PP;
				if(this == NZ)
					return PZ;
				if(this == NP)
					return PN;
				if(this == ZP)
					return ZN;
				if(this == PP)
					return NN;
				throw new RuntimeException("unknown orientation");
		}
		throw new RuntimeException("angle \"" + angle + "\" not supported");
	}

	public static Orientation getOrientation(EntityPlayer p, float tolerance, boolean orthogonals, boolean diagonals)
	{
		float rotation = p.rotationYaw % 360F;
		if(rotation < 0)
			rotation += 360F;

		float minimumRotation = rotation - tolerance;
		if(minimumRotation < 0)
			minimumRotation += 360F;

		float maximumRotation = rotation + tolerance;
		if(maximumRotation >= 360F)
			maximumRotation -= 360F;

		if(orthogonals)
		{
			if(NZ.isWithinAngle(minimumRotation, maximumRotation))
				return NZ;
			if(PZ.isWithinAngle(minimumRotation, maximumRotation))
				return PZ;
			if(ZN.isWithinAngle(minimumRotation, maximumRotation))
				return ZN;
			if(ZP.isWithinAngle(minimumRotation, maximumRotation))
				return ZP;
		}
		if(diagonals)
		{
			if(NP.isWithinAngle(minimumRotation, maximumRotation))
				return NP;
			if(PN.isWithinAngle(minimumRotation, maximumRotation))
				return PN;
			if(NN.isWithinAngle(minimumRotation, maximumRotation))
				return NN;
			if(PP.isWithinAngle(minimumRotation, maximumRotation))
				return PP;
		}
		return null;
	}

	public double getHorizontalBorderGap(Entity entity)
	{
		return getHorizontalBorderGap(entity.posX, entity.posZ);
	}

	private double getHorizontalBorderGap()
	{
		return getHorizontalBorderGap(base_id, base_kd);
	}

	private double getHorizontalBorderGap(double i, double k)
	{
		if(this == NZ)
			return i % 1;
		if(this == PZ)
			return 1 - (i % 1);
		if(this == ZN)
			return k % 1;
		if(this == ZP)
			return 1 - (k % 1);
		return 0D;
	}

	public boolean isTunnelAhead(World world, int i, int j, int k)
	{
		Block remoteId = world.getBlock(i + _i, j + 1, k + _k);
		if(isFullEmpty(remoteId))
		{
			Material aboveMaterial = world.getBlock(i + _i, j + 2, k + _k).getMaterial();
			if(aboveMaterial != null && isSolid(aboveMaterial))
				return true;
		}
		return false;
	}

	public static HashSet<Orientation> getClimbingOrientations(EntityPlayer p, boolean orthogonals, boolean diagonals)
	{
		float rotation = p.rotationYaw % 360F;
		if(rotation < 0)
			rotation += 360F;

		if(_getClimbingOrientationsHashSet == null)
			_getClimbingOrientationsHashSet = new HashSet<Orientation>();
		else
			_getClimbingOrientationsHashSet.clear();

		if(orthogonals)
		{
			NZ.addTo(rotation);
			PZ.addTo(rotation);
			ZN.addTo(rotation);
			ZP.addTo(rotation);
		}
		if(diagonals)
		{
			NP.addTo(rotation);
			PN.addTo(rotation);
			NN.addTo(rotation);
			PP.addTo(rotation);
		}
		return _getClimbingOrientationsHashSet;
	}

	private static HashSet<Orientation> _getClimbingOrientationsHashSet = null;

	private void addTo(float rotation)
	{
		if(isRotationForClimbing(rotation))
			_getClimbingOrientationsHashSet.add(this);
	}

	public boolean isFeetLadderSubstitute(World world, int bi, int j, int bk)
	{
		int i = bi + _i;
		int k = bk + _k;

		return isLadderSubstitute(world, i, j, k, middle) > 0 || isLadderSubstitute(world, i, j, k, base) > 0;
	}

	public boolean isHandsLadderSubstitute(World world, int bi, int j, int bk)
	{
		int i= bi + _i;
		int k = bk + _k;

		return isLadderSubstitute(world, i, j, k, middle) > 0 || isLadderSubstitute(world, i, j, k, base) > 0 || isLadderSubstitute(world, i, j, k, sub) > 0;
	}

	private int isLadderSubstitute(World worldObj, int i, int j, int k, int halfOffset)
	{
		world = worldObj;
		remote_i = i;
		all_j = j;
		remote_k = k;
		all_offset = 0;
		return isLadderSubstitute(halfOffset, null);
	}

	public void seekClimbGap(float rotation, World world, int i, double id, double jhd, int k, double kd, boolean isClimbCrawling, boolean isCrawlClimbing, boolean isCrawling, HandsClimbing[] inout_handsClimbing, FeetClimbing[] inout_feetClimbing, ClimbGap out_handsClimbGap, ClimbGap out_feetClimbGap)
	{
		if(isRotationForClimbing(rotation))
		{
			initialize(world, i, id, jhd, k, kd);

			inout_handsClimbing[0] = inout_handsClimbing[0].max(handsClimbing(isClimbCrawling, isCrawlClimbing, isCrawling, _climbGapOuterTemp), out_handsClimbGap, _climbGapOuterTemp);
			inout_feetClimbing[0] = inout_feetClimbing[0].max(feetClimbing(isClimbCrawling, isCrawlClimbing, isCrawling, _climbGapOuterTemp), out_feetClimbGap, _climbGapOuterTemp);
		}
	}

	private HandsClimbing handsClimbing(boolean isClimbCrawling, boolean isCrawlClimbing, boolean isCrawling, ClimbGap out_climbGap)
	{
		out_climbGap.reset();
		_climbGapTemp.reset();

		initializeOffset(3D, isClimbCrawling, isCrawlClimbing, isCrawling);

		HandsClimbing result = HandsClimbing.None;
		int gap;

		if((gap = isLadderSubstitute(middle, _climbGapTemp)) > 0)
			if(jh_offset > 1D - _handClimbingHoldGap)
				result = result.max(HandsClimbing.Up, out_climbGap, _climbGapTemp);
			else
				result = result.max(HandsClimbing.None, out_climbGap, _climbGapTemp); // No climbing (hands not long enough - up)

		if((gap = isLadderSubstitute(base, _climbGapTemp)) > 0)
			if(jh_offset < _handClimbingHoldGap)
				result = result.max(HandsClimbing.BottomHold, out_climbGap, _climbGapTemp); // Climbing speed 1 (pulling weight up) or hold when climbing down
			else
				result = result.max(HandsClimbing.Up, out_climbGap, _climbGapTemp); // Climbing speed 1 (pulling weight up)

		_climbGapTemp.SkipGaps = isClimbCrawling || isCrawlClimbing;

		if((gap = isLadderSubstitute(sub, _climbGapTemp)) > 0 && !(isCrawling && gap > 1))
			if(!isClimbCrawling && gap > 2)
				result = result.max(HandsClimbing.FastUp, out_climbGap, _climbGapTemp); // Climbing speed 1 (pulling upper body into gap)
			else if(isClimbCrawling && gap > 1)
				result = result.max(HandsClimbing.FastUp, out_climbGap, _climbGapTemp); // Climbing speed 1 (crawling into upper gap)
			else // (no gap for balancing upper body)
				if(jh_offset < _handClimbingHoldGap)
					if(grabType == AroundGrab)
						result = result.max(HandsClimbing.Up, out_climbGap, _climbGapTemp); // Lower climbing up ladder
					else
						result = result.max(HandsClimbing.TopHold, out_climbGap, _climbGapTemp); // Lower holding
				else
					if(grabType == AroundGrab)
						result = result.max(HandsClimbing.TopHold, out_climbGap, _climbGapTemp); // Sinking to lower holding level
					else
						result = result.max(HandsClimbing.Sink, out_climbGap, _climbGapTemp); // Sinking to lower holding level

		if((gap = isLadderSubstitute(subSub, _climbGapTemp)) > 0 && !isCrawling)
			if((gap > 2 && !isCrawlClimbing) || grabType == AroundGrab || (gap > 1 && isClimbCrawling)) // (hands not long enough - down)
				if(jh_offset < _handClimbingHoldGap && !isClimbCrawling)
					result = result.max(HandsClimbing.TopHold, out_climbGap, _climbGapTemp); // Upper holding
				else if(isClimbCrawling)
					result = result.max(HandsClimbing.FastUp, out_climbGap, _climbGapTemp); // Sinking to upper holding level
				else
					result = result.max(HandsClimbing.Sink, out_climbGap, _climbGapTemp); // Sinking to upper holding level

		return result;
	}

	private FeetClimbing feetClimbing(boolean isClimbCrawling, boolean isCrawlClimbing, boolean isCrawling, ClimbGap out_climbGap)
	{
		out_climbGap.reset();
		_climbGapTemp.reset();

		initializeOffset(0D, isClimbCrawling, isCrawlClimbing, isCrawling);
		FeetClimbing result = FeetClimbing.None;
		int gap;

		if((gap = isLadderSubstitute(top, _climbGapTemp)) > 0) // No climbing (feet not long enough - up)
			result = result.max(FeetClimbing.None, out_climbGap, _climbGapTemp);

		_climbGapTemp.SkipGaps = isClimbCrawling || isCrawlClimbing;

		if((gap = isLadderSubstitute(middle, _climbGapTemp)) > 0 && !isCrawling)
			if(gap > 3 && !isClimbCrawling)
				if(!isCrawlClimbing)
					result = result.max(FeetClimbing.FastUp, out_climbGap, _climbGapTemp); // Climbing speed 2 (pushing upper body up into big gap)
				else
					result = result.max(FeetClimbing.None, out_climbGap, _climbGapTemp);
			else if((isClimbCrawling || isCrawlClimbing) && gap > 1)
				if(isCrawlClimbing)
					result = result.max(FeetClimbing.BaseWithHands, out_climbGap, _climbGapTemp);
				else
					result = result.max(FeetClimbing.FastUp, out_climbGap, _climbGapTemp);
			else if(gap > 2)
				if(!isClimbCrawling)
					result = result.max(FeetClimbing.SlowUpWithHoldWithoutHands, out_climbGap, _climbGapTemp); // Climbing speed 1 (no gap for balancing upper body)
				else
					result = result.max(FeetClimbing.None, out_climbGap, _climbGapTemp);
			else
				result = result.max(FeetClimbing.TopWithHands,out_climbGap, _climbGapTemp); // Climbing with hands only

		if((gap = isLadderSubstitute(base, _climbGapTemp)) > 0)
			if(gap > 3 && !isCrawling && !isCrawlClimbing)
				result = result.max(FeetClimbing.FastUp, out_climbGap, _climbGapTemp); // Climbing speed 2 (pushing whole body up into big gap)
			else if(gap > 2 && !isCrawling)
				if(!isClimbCrawling)
					if(jh_offset < _handClimbingHoldGap)
						result = result.max(FeetClimbing.SlowUpWithHoldWithoutHands, out_climbGap, _climbGapTemp);
					else
						result = result.max(FeetClimbing.SlowUpWithSinkWithoutHands, out_climbGap, _climbGapTemp); // Climbing speed 1 (no gap for balancing whole body)
				else
					result = result.max(FeetClimbing.None, out_climbGap, _climbGapTemp);
			else
				if(jh_offset < 1D - _handClimbingHoldGap)
					result = result.max(FeetClimbing.BaseWithHands, out_climbGap, _climbGapTemp); // Climbing with hands only
				else
					result = result.max(FeetClimbing.BaseHold, out_climbGap, _climbGapTemp);

		if((isLadderSubstitute(sub, _climbGapTemp)) > 0)
			result = result.max(FeetClimbing.None, out_climbGap, _climbGapTemp); // No climbing (feet not long enough - down)

		if(isCrawlClimbing || isCrawling)
			result = result.max(FeetClimbing.BaseWithHands, out_climbGap, _climbGapTemp);

		return result;
	}

	private int isLadderSubstitute(int local_Offset, ClimbGap out_climbGap)
	{
		initializeLocal(local_Offset);

		int gap;
		if(local_half == 1)
			if(hasHalfHold())
			{
				if(!grabRemote)
				{
					boolean overLadder = isOnLadderOrVine(0) || isOnOpenTrapDoor(0) || isRope(0) || isOnWallRope(0);
					boolean overOverLadder = isOnLadderOrVine(1) || isOnOpenTrapDoor(1) || isRope(1) || isOnWallRope(1);
					boolean overAccessible = isBaseAccessible(1, false, true);
					boolean overOverAccessible = isBaseAccessible(2, false, true);
					boolean overFullAccessible = overAccessible && isFullAccessible(1, grabRemote);
					boolean overOverFullAccessible = overAccessible && isFullExtentAccessible(2, grabRemote);

					if(overLadder)
						if(overOverLadder)
							gap = 1;
						else if(overOverAccessible)
							gap = 1;
						else
							gap = 1;
					else if(overAccessible)
						if(overFullAccessible)
							if(overOverFullAccessible)
								gap = 5;
							else
								gap = crawl ? 3 : 5;
						else if(overOverLadder)
							gap = 5;
						else
							gap = 1;
					else
						gap = 1;
				}
				else if(isBaseAccessible(0))
					if(isUpperHalfFrontEmpty(remote_i, 0, remote_k))
						if(isFullAccessible(1, grabRemote))
							if(isFullExtentAccessible(2, grabRemote))
								gap = 5;
							else if(isJustLowerHalfExtentAccessible(2))
								gap = 4;
							else
								gap = 3;
						else
							gap = 1;
					else
						gap = 1;
				else
					gap = 0;
			}
			else
				gap = 0;
		else
			if(hasBottomHold())
			{
				if(!grabRemote)
				{
					boolean overLadder = isOnLadderOrVine(0) || isOnOpenTrapDoor(0) || isRope(0) || isOnWallRope(0);
					boolean overOverLadder = isOnLadderOrVine(1) || isOnOpenTrapDoor(1) || isRope(1) || isOnWallRope(0);
					boolean overAccessible = isBaseAccessible(0, false, true);
					boolean overOverAccessible = isBaseAccessible(1, false, true);
					boolean overFullAccessible = overAccessible && isFullAccessible(0, grabRemote);
					boolean overOverFullAccessible = overAccessible && isFullExtentAccessible(1, grabRemote);

					if(overLadder)
						if(overOverLadder)
							gap = 1;
						else if(overOverAccessible)
							gap = 1;
						else
							gap = 1;
					else if(overAccessible)
						if(overFullAccessible)
							if(overOverAccessible)
								if(overOverFullAccessible)
									gap = 4;
								else
									gap = crawl ? 2 : 4;
							else
								gap = 2;
						else if(overOverLadder)
							gap = 2;
						else
							gap = 1;
					else
						gap = 1;
				}
				else if(isBaseAccessible(0))
					if(isFullAccessible(0, grabRemote))
						if(isFullExtentAccessible(1, grabRemote))
							gap = 4;
						else
							gap = 2;
					else
						gap = 1;
				else
					gap = 0;
			}
			else
				gap = 0;

		if(out_climbGap != null && gap > 0)
		{
			out_climbGap.Block = grabBlock;
			out_climbGap.Meta = grabMeta;
			out_climbGap.CanStand = gap > 3;
			out_climbGap.MustCrawl = gap > 1 && gap < 4;
			out_climbGap.Direction = this;
		}
		return gap;
	}

	private boolean hasHalfHold()
	{
		if(Config.isFreeBaseClimb())
		{
			if(isOnLadder(0) && isOnLadderFront(0))
				return setHalfGrabType(AroundGrab, getBaseBlockId(0), false);

			if(remoteLadderClimbing(0))
				return setHalfGrabType(AroundGrab, getRemoteBlockId(0), true);
		}

		if(SmartMovingOptions.hasBetterThanWolves || SmartMovingOptions.hasRopesPlus)
		{
			Block id;
			if((id = getRopeId(0)) != null && isHeadedToRope())
				return setHalfGrabType(AroundGrab, id, false);
			if((id = getAnchorId(0)) != null && isOnAnchorFront(0))
				return setHalfGrabType(HalfGrab, id, false);
		}

		Block remoteId = getRemoteBlockId(0);
		if(isEmpty(base_i, 0, base_k))
		{
			if(remoteId == Block.getBlockFromName("iron_bars") && headedToFrontWall(remote_i, 0, remote_k, remoteId))
				return setHalfGrabType(HalfGrab, remoteId);
		}

		Block wallId = getWallBlockId(base_i, 0, base_k);
		if(wallId == Block.getBlockFromName("iron_bars") && headedToBaseWall(0, wallId))
			return setHalfGrabType(HalfGrab, wallId, false);
		if(wallId != null && isOnMiddleLadderFront(0))
			return setHalfGrabType(AroundGrab, remoteId, false);

		if(Config._freeFenceClimbing.value)
		{
			if(isFence(remoteId, remote_i, 0, remote_k) && headedToFrontWall(remote_i, 0, remote_k, remoteId))
				if(!isFence(getBaseBlockId(0), base_i, 0, base_k))
					return setHalfGrabType(HalfGrab, remoteId);
				else if(headedToFrontSideWall(remote_i, 0, remote_k, remoteId))
					return setHalfGrabType(HalfGrab, remoteId);

			Block remoteBelowId = getRemoteBlockId(-1);
			if(isFence(remoteBelowId, remote_i, -1, remote_k) && headedToFrontWall(remote_i, -1, remote_k, remoteBelowId))
				if(!isFence(getBaseBlockId(-1), remote_i, -1, remote_k))
					return setHalfGrabType(HalfGrab, remoteId);
				else if(headedToFrontSideWall(remote_i, -1, remote_k, remoteBelowId))
					return setHalfGrabType(HalfGrab, remoteId);

			if(isFence(wallId, base_i, 0, base_k) && headedToBaseWall(0, wallId))
				return setHalfGrabType(HalfGrab, wallId, false);

			Block belowWallId = getWallBlockId(base_i, -1, base_k);
			if(isFence(belowWallId, base_i, -1, base_k) && headedToBaseWall(-1, belowWallId))
				return setHalfGrabType(HalfGrab, belowWallId, false);

			if(remoteId == Block.getBlockFromName("cobblestone_wall") && !headedToRemoteFlatWall(remoteId, 0))
				return setHalfGrabType(HalfGrab, remoteId);

			if(remoteBelowId == Block.getBlockFromName("cobblestone_wall") && !headedToRemoteFlatWall(remoteBelowId, -1))
				return setHalfGrabType(HalfGrab, remoteBelowId);
		}

		int remoteMetadata = getRemoteBlockMetadata(0);
		if(isBottomHalfBlock(remoteId, remoteMetadata) || (isStairCompact(remoteId) && isBottomStairCompactNotBack(remoteMetadata) && !(isStairCompact(getBaseBlockId(-1)) && isBottomStairCompactFront(getBaseBlockMetadata(-1)))))
			return setHalfGrabType(HalfGrab, remoteId);

		if(isTrapDoor(remoteId) && isClosedTrapDoor(getRemoteBlockMetadata(0)))
			return setHalfGrabType(HalfGrab, remoteId);

		Block baseId = getBaseBlockId(0);
		if(isTrapDoor(baseId) && !isClosedTrapDoor(getBaseBlockMetadata(0)))
			return setHalfGrabType(HalfGrab, baseId, false);

		if(SmartMovingOptions.hasASGrapplingHook && Config._replaceRopeClimbing.value || SmartMovingOptions.hasRopesPlus)
		{
			if(isASRope(baseId) && isASGrapplingHookFront(getBaseBlockMetadata(0)))
				return setHalfGrabType(HalfGrab, baseId, false);
			if(isASRope(remoteId) && rotate(180).isASGrapplingHookFront(remoteMetadata))
				return setHalfGrabType(HalfGrab, remoteId, true);
		}

		if(Config.isFreeBaseClimb())
		{
			int meta = baseVineClimbing(0);
			if(meta > -1)
				return setHalfGrabType(HalfGrab, Block.getBlockFromName("vine"), false, meta);
			meta = remoteVineClimbing(0);
			if(meta > -1)
				return setHalfGrabType(HalfGrab, Block.getBlockFromName("vine"), false, meta);
		}

		return setHalfGrabType(NoGrab, null);
	}

	private boolean hasBottomHold()
	{
		if(Config.isFreeBaseClimb())
		{
			if(isOnLadder(-1) && isOnLadderFront(-1))
				return setBottomGrabType(AroundGrab, getBaseBlockId(-1) , false);

			if(isOnLadder(0) && isOnLadderFront(0))
				return setBottomGrabType(AroundGrab, getBaseBlockId(0), false);

			if(remoteLadderClimbing(-1))
				return setBottomGrabType(AroundGrab, getRemoteBlockId(-1), true);

			if(remoteLadderClimbing(0))
				return setBottomGrabType(AroundGrab, getRemoteBlockId(0), true);
		}

		if(SmartMovingOptions.hasBetterThanWolves || SmartMovingOptions.hasRopesPlus)
		{
			Block id;
			if((id = getRopeId(-1)) != null && isHeadedToRope() || (id = getRopeId(0)) != null && isHeadedToRope())
				return setBottomGrabType(AroundGrab, id, false);
			if((id = getAnchorId(-1)) != null && isOnAnchorFront(-1) || (id = getAnchorId(0)) != null && isOnAnchorFront(0))
				return setBottomGrabType(HalfGrab, id, false);
		}

		Block remoteId = getRemoteBlockId(0);
		Block remoteBelowId = getRemoteBlockId(-1);
		boolean remoteLowerHalfEmpty = isLowerHalfFrontFullEmpty(remote_i, 0, remote_k);
		if(SmartMovingOptions.hasRedPowerWire)
		{
			if(isRedPowerWire(remoteBelowId))
			{
				int coverSides = getRpCoverSides(remote_i, -1, remote_k);
				if((isRedPowerWireFullFront(coverSides) || isRedPowerWireTop(coverSides)) && remoteLowerHalfEmpty)
					return setBottomGrabType(HalfGrab, remoteBelowId);
			}

			if(isRedPowerWire(remoteId))
			{
				int coverSides = getRpCoverSides(remote_i, 0, remote_k);
				if(isRedPowerWireBottom(coverSides) && remoteLowerHalfEmpty)
					return setBottomGrabType(HalfGrab, remoteBelowId);
			}

			Block baseId = getBaseBlockId(-1);
			if(isRedPowerWire(baseId))
			{
				int coverSides = getRpCoverSides(base_i, -1, base_k);
				if(isRedPowerWireFullBack(coverSides) && remoteLowerHalfEmpty)
					return setBottomGrabType(HalfGrab, remoteBelowId);
			}

			if(isRedPowerWire(remoteBelowId))
				return false;
		}

		if(isEmpty(base_i, -1, base_k))
		{
			if(remoteBelowId == Block.getBlockFromName("iron_bars") && headedToFrontWall(remote_i, -1, remote_k, remoteBelowId))
				return setBottomGrabType(HalfGrab, remoteBelowId);
		}

		if(Config._freeFenceClimbing.value)
		{
			Block baseBelowBlockId = getBaseBlockId(-1);
			if(isFence(remoteBelowId, remote_i, -1, remote_k) && headedToFrontWall(remote_i, -1, remote_k, remoteBelowId))
				if(!isFence(baseBelowBlockId, base_i, -1, base_k))
					return setBottomGrabType(HalfGrab, remoteBelowId);
				else if(headedToFrontSideWall(remote_i, -1, remote_k, remoteBelowId))
					return setBottomGrabType(HalfGrab, remoteBelowId);

			if(remoteBelowId == Block.getBlockFromName("cobblestone_wall") && !headedToRemoteFlatWall(remoteBelowId, -1))
				return setHalfGrabType(HalfGrab, remoteBelowId);

			if(remoteId == Block.getBlockFromName("cobblestone_wall") && !headedToRemoteFlatWall(remoteId, 0))
				return setHalfGrabType(HalfGrab, remoteId);
		}

		Block belowWallBlockId = getWallBlockId(base_i, -1, base_k);
		if(belowWallBlockId != null)
		{
			if(isEmpty(base_i - _i, 0, base_k - _k) && isEmpty(base_i - _i, -1, base_k - _k))
			{
				if(belowWallBlockId == Block.getBlockFromName("iron_bars") && headedToBaseWall(-1, belowWallBlockId))
					return setBottomGrabType(HalfGrab, belowWallBlockId, false);
				if(isOnMiddleLadderFront(-1))
					return setHalfGrabType(AroundGrab, remoteId, false);

				if(headedToBaseGrabWall(-1, belowWallBlockId))
					return setBottomGrabType(HalfGrab, belowWallBlockId, false);
			}

			if(Config._freeFenceClimbing.value && isFence(belowWallBlockId, base_i, -1, base_k) && headedToBaseWall(-1, belowWallBlockId))
				return setBottomGrabType(HalfGrab, belowWallBlockId, false);
		}

		int remoteBelowMetadata = getRemoteBlockMetadata(-1);
		if(remoteLowerHalfEmpty && isBaseAccessible(-1, true, false))
			if(isUpperHalfFrontAnySolid(remote_i, -1, remote_k))
				if(!isBottomHalfBlock(remoteBelowId, remoteBelowMetadata))
					if(!isStairCompact(remoteBelowId) || !isBottomStairCompactFront(remoteBelowMetadata))
						if(!isDoor(remoteBelowId) || isDoorTop(remoteBelowMetadata))
							if(!isDoor(getBaseBlockId(0)) || !isDoorFrontBlocked(base_i, 0, base_k))
								if(Config._freeFenceClimbing.value || !isFence(remote_i, -1, remote_k))
									return setBottomGrabType(HalfGrab, remoteBelowId);

		if(isStairCompact(remoteId))
		{
			int remoteMetadata = getRemoteBlockMetadata(0);
			if(isTopStairCompact(remoteMetadata) && !isTopStairCompactBack(remoteMetadata) && isUpperHalfFrontFullSolid(remote_i, -1, remote_k))
				return setBottomGrabType(HalfGrab, remoteBelowId);
		}

		Block baseBelowId = getBaseBlockId(-1);
		int baseBelowMetadata = getBaseBlockMetadata(-1);

		// for trap door bottom hold
		//if(isTrapDoor(remoteId) && isClosedTrapDoor(remote_i, local_offset, remote_k))
		//	return setBottomGrabType(HalfGrab, remoteId);

		// for trap door top hold
		if(isTrapDoor(baseBelowId) && !isClosedTrapDoor(getBaseBlockMetadata(-1)))
			return setBottomGrabType(HalfGrab,baseBelowId, false);

		if(isDoor(baseBelowId) && isDoorTop(baseBelowMetadata) && isDoorFrontBlocked(base_i, -1, base_k) && isBaseAccessible(0))
			return setBottomGrabType(HalfGrab, baseBelowId, false);

		if(SmartMovingOptions.hasASGrapplingHook && Config._replaceRopeClimbing.value || SmartMovingOptions.hasRopesPlus)
		{
			if(isASRope(baseBelowId) && isASGrapplingHookFront(getBaseBlockMetadata(0)))
				return setBottomGrabType(HalfGrab, baseBelowId, false);

			Block baseId = getBaseBlockId(0);
			if(isASRope(baseId) && isASGrapplingHookFront(getBaseBlockMetadata(0)))
				return setBottomGrabType(HalfGrab, baseId, false);

			if(isASRope(remoteBelowId) && rotate(180).isASGrapplingHookFront(remoteBelowMetadata))
				return setHalfGrabType(HalfGrab, remoteBelowId, true);

			if(isASRope(remoteId) && rotate(180).isASGrapplingHookFront(getRemoteBlockMetadata(0)))
				return setHalfGrabType(HalfGrab, remoteId, true);
		}

		if(Config.isFreeBaseClimb())
		{
			int meta = baseVineClimbing(-1);
			if(meta != DefaultMeta)
				return setHalfGrabType(HalfGrab, Block.getBlockFromName("vine"), false, meta);

			meta = baseVineClimbing(0);
			if(meta != DefaultMeta)
				return setHalfGrabType(HalfGrab, Block.getBlockFromName("vine"), false, meta);

			meta = remoteVineClimbing(-1);
			if(meta != DefaultMeta)
				return setHalfGrabType(HalfGrab, Block.getBlockFromName("vine"), false, meta);

			meta = remoteVineClimbing(0);
			if(meta != DefaultMeta)
				return setHalfGrabType(HalfGrab, Block.getBlockFromName("vine"), false, meta);
		}

		return setBottomGrabType(NoGrab, null);
	}

	private boolean setHalfGrabType(int type, Block block)
	{
		return setHalfGrabType(type, block, true);
	}

	private boolean setHalfGrabType(int type, Block block, boolean remote)
	{
		return setHalfGrabType(type, block, remote, -1);
	}

	private boolean setHalfGrabType(int type, Block block, boolean remote, int metaClimb)
	{
		boolean hasGrab = type != NoGrab;
		if(hasGrab && remote && _isDiagonal)
		{
			boolean edgeConnectCCW = rotate(90).isUpperHalfFrontEmpty(base_i, 0, remote_k);
			boolean edgeConnectCW = rotate(-90).isUpperHalfFrontEmpty(remote_i, 0, base_k);
			hasGrab &= edgeConnectCCW && edgeConnectCW;
		}
		return setGrabType(type, block, remote, hasGrab, metaClimb);
	}

	private boolean setBottomGrabType(int type, Block block)
	{
		return setBottomGrabType(type, block, true);
	}

	private boolean setBottomGrabType(int type, Block block, boolean remote)
	{
		return setBottomGrabType(type, block, remote, -1);
	}

	private boolean setBottomGrabType(int type, Block block, boolean remote, int metaClimb)
	{
		boolean hasGrab = type != NoGrab;
		if(hasGrab && remote && _isDiagonal)
		{
			boolean edgeConnectCCW = rotate(90).isLowerHalfFrontFullEmpty(base_i, 0, remote_k);
			boolean edgeConnectCW = rotate(-90).isLowerHalfFrontFullEmpty(remote_i, 0, base_k);
			hasGrab &= edgeConnectCCW && edgeConnectCW;
		}
		return setGrabType(type, block, remote, hasGrab, metaClimb);
	}

	private static boolean setGrabType(int type, Block block, boolean remote, boolean hasGrab, int metaClimb)
	{
		grabRemote = remote;
		grabType = hasGrab ? type : NoGrab;
		grabBlock = block;
		grabMeta = metaClimb;
		return hasGrab;
	}

	@SuppressWarnings("incomplete-switch")
	private boolean setClimbingAngles()
	{
		switch(_i)
		{
			case -1:
				switch(_k)
				{
					case -1:
						return setClimbingAngles(135); //NN
					case 0:
						return setClimbingAngles(90); // NZ
					case 1:
						return setClimbingAngles(45); // NP
				}
				break;
			case 0:
				switch(_k)
				{
					case -1:
						return setClimbingAngles(180); // ZN
					case 0:
						return setClimbingAngles(0, 360); // ZZ
					case 1:
						return setClimbingAngles(0); // ZP
				}
				break;
			case 1:
				switch(_k)
				{
					case -1:
						return setClimbingAngles(225); // PN
					case 0:
						return setClimbingAngles(270); // PZ
					case 1:
						return setClimbingAngles(315); // PP
				}
				break;
		}
		return false;
	}

	private boolean setClimbingAngles(float directionAngle)
	{
		_directionAngle = directionAngle;
		float halfAreaAngle = (_isDiagonal ? Config._freeClimbingDiagonalDirectionAngle.value : Config._freeClimbingOrthogonalDirectionAngle.value) / 2F;
		return setClimbingAngles(directionAngle - halfAreaAngle, directionAngle + halfAreaAngle);
	}

	private boolean setClimbingAngles(float mimimumClimbingAngle, float maximumClimbingAngle)
	{
		if(mimimumClimbingAngle < 0F)
			mimimumClimbingAngle += 360F;

		if(maximumClimbingAngle > 360F)
			maximumClimbingAngle -= 360F;

		_mimimumClimbingAngle = mimimumClimbingAngle;
		_maximumClimbingAngle = maximumClimbingAngle;

		return mimimumClimbingAngle != maximumClimbingAngle;
	}

	private boolean isWithinAngle(float minimumRotation, float maximumRotation)
	{
		return isWithinAngle(_directionAngle, minimumRotation, maximumRotation);
	}

	private boolean isRotationForClimbing(float rotation)
	{
		return isWithinAngle(rotation, _mimimumClimbingAngle, _maximumClimbingAngle);
	}

	private static boolean isWithinAngle(float rotation, float minimumRotation, float maximumRotation)
	{
		if(minimumRotation > maximumRotation)
			return rotation >= minimumRotation || rotation <= maximumRotation;
		return rotation >= minimumRotation && rotation <= maximumRotation;
	}

	private int baseVineClimbing(int j_offset)
	{
		boolean result = isOnVine(j_offset);
		if(result)
		{
			result = isOnVineFront(j_offset);
			if(result)
				return VineFrontMeta;

			if(baseVineClimbing(j_offset, PZ) || baseVineClimbing(j_offset, NZ) || baseVineClimbing(j_offset, ZP) || baseVineClimbing(j_offset, ZN))
				return VineSideMeta;
		}
		return DefaultMeta;
	}

	private boolean baseVineClimbing(int j_offset, Orientation orientation)
	{
		if(orientation == this)
			return false;

		return orientation.rotate(180).hasVineOrientation(world, base_i, local_offset + j_offset, base_k) && orientation.getHorizontalBorderGap() >= 0.65;
	}

	private boolean remoteLadderClimbing(int j_offset)
	{
		return isBehindLadder(j_offset) && isOnLadderBack(j_offset);
	}

	private int remoteVineClimbing(int j_offset)
	{
		if(isBehindVine(j_offset) && isOnVineBack(j_offset))
			return VineFrontMeta;

		if(remoteVineClimbing(j_offset, PZ) || remoteVineClimbing(j_offset, NZ) || remoteVineClimbing(j_offset, ZP) || remoteVineClimbing(j_offset, ZN))
			return VineSideMeta;

		return DefaultMeta;
	}

	private boolean remoteVineClimbing(int j_offset, Orientation orientation)
	{
		if(orientation == this)
			return false;

		int i = base_i - orientation._i;
		int k = base_k - orientation._k;
		return isVine(getBlock(i, j_offset, k)) && orientation.hasVineOrientation(world, i, local_offset + j_offset, k) && orientation.getHorizontalBorderGap() >= 0.65F;
	}

	private static boolean isOnLadder(int j_offset)
	{
		Block block = getBaseBlockId(j_offset);
		if(isLadder(block))
			return true;
		if(isVine(block))
			return false;
		if(isClimbable(world, base_i, local_offset + j_offset, base_k))
			return true;
		return false;
	}

	private static boolean isBehindLadder(int j_offset)
	{
		Block block = getRemoteBlockId(j_offset);
		if(isLadder(block))
			return true;
		if(isVine(block))
			return false;
		if(isClimbable(world, remote_i, local_offset + j_offset, remote_k))
			return true;
		return false;
	}

	private static boolean isOnVine(int j_offset)
	{
		return isVine(getBaseBlockId(j_offset));
	}

	private static boolean isBehindVine(int j_offset)
	{
		return isVine(getRemoteBlockId(j_offset));
	}

	private static boolean isOnLadderOrVine(int j_offset)
	{
		return isLadderOrVine(getBaseBlockId(j_offset)) || isVine(grabBlock);
	}

	public static boolean isLadder(Block block)
	{
		return block == Block.getBlockFromName("ladder");
	}

	public static boolean isVine(Block block)
	{
		return block == Block.getBlockFromName("vine");
	}

	public static boolean isLadderOrVine(Block block)
	{
		return isLadder(block) || isVine(block) || isBlockIdOfType(block, _ladderKitLadderTypes);
	}

	public static boolean isKnownLadder(Block block)
	{
		return isLadder(block) || isBlockIdOfType(block, _ladderKitLadderTypes);
	}

	public static boolean isClimbable(World world, int i, int j, int k)
	{
		Block block = world.getBlock(i, j, k);
		return block != null && block.isLadder(world, i, j, k, Minecraft.getMinecraft().thePlayer);
	}

	private boolean isOnLadderFront(int j_offset)
	{
		return hasLadderOrientation(base_i, j_offset, base_k);
	}

	private boolean isOnLadderBack(int j_offset)
	{
		return rotate(180).hasLadderOrientation(remote_i, j_offset, remote_k);
	}

	private boolean isOnVineFront(int j_offset)
	{
		return hasVineOrientation(world, base_i, local_offset + j_offset, base_k);
	}

	private boolean isOnVineBack(int j_offset)
	{
		return rotate(180).hasVineOrientation(world, remote_i, local_offset + j_offset, remote_k);
	}

	@SuppressWarnings("incomplete-switch")
	private boolean isOnMiddleLadderFront(int j_offset)
	{
		switch(getCarpentersBlockData(base_i, j_offset, base_k))
		{
			case 0:
				if(this == ZN)
					return isTopHalf(base_kd);
				if(this == ZP)
					return !isTopHalf(base_kd);
				break;
			case 1:
				if(this == NZ)
					return isTopHalf(base_id);
				if(this == PZ)
					return !isTopHalf(base_id);
				break;
		}
		return false;
	}

	private static int getCarpentersBlockData(int i, int j_offset, int k)
	{
		if (isExternalBlockType(getBlock(i, j_offset, k), _blockCarpentersLadder))
		{
			TileEntity entity = getBlockTileEntity(i, j_offset, k);
			if (entity != null)
				return (Integer)Reflect.Invoke(_carpentersTEBaseBlockGetData, entity);
		}
		return -1;
	}

	@SuppressWarnings("incomplete-switch")
	public static Orientation getKnownLadderOrientation(World world, int i, int j, int k)
	{
		Block block = world.getBlock(i, j, k);
		int metadata = world.getBlockMetadata(i, j, k);

		if(isBlockIdOfType(block, _ladderKitLadderTypes))
		{
			switch(metadata & 0x3)
			{
				case 1:
					return NZ;
				case 3:
					return PZ;
				case 0:
					return ZP;
				case 2:
					return ZN;
			}
			return null;
		}

		switch(metadata & 0x7)
		{
			case 5:
				return NZ;
			case 4:
				return PZ;
			case 2:
				return ZP;
			case 3:
				return ZN;
		}
		return null;
	}

	public boolean hasVineOrientation(World world, int i, int j, int k)
	{
		int metaData = world.getBlockMetadata(i, j, k);
		if(this == NZ)
			return (metaData & 2) != 0;
		if(this == PZ)
			return (metaData & 8) != 0;
		if(this == ZP)
			return (metaData & 1) != 0;
		if(this == ZN)
			return (metaData & 4) != 0;
		return false;
	}

	private boolean hasLadderOrientation(int i, int j_offset, int k)
	{
		Block block = getBlock(i, j_offset, k);
		int metadata = getBlockMetadata(i, j_offset, k);

		if(isBlockIdOfType(block, _ladderKitLadderTypes))
		{
			metadata &= 0x3;
			if(this == NZ)
				return metadata == 1;
			if(this == PZ)
				return metadata == 3;
			if(this == ZP)
				return metadata == 0;
			if(this == ZN)
				return metadata == 2;
			return false;
		}

		int carpentersBlockData = getCarpentersBlockData(i, j_offset, k);
		if(carpentersBlockData > -1)
			metadata = carpentersBlockData;
		else
			metadata &= 0x7;

		if(this == NZ)
			return metadata == 5;
		if(this == PZ)
			return metadata == 4;
		if(this == ZP)
			return metadata == 2;
		if(this == ZN)
			return metadata == 3;
		return false;
	}

	public boolean isRemoteSolid(World world, int i, int j, int k)
	{
		return isSolid(world.getBlock(i + _i, j, k + _k).getMaterial());
	}

	@SuppressWarnings("incomplete-switch")
	public static Orientation getOpenTrapDoorOrientation(World world, int i, int j, int k)
	{
		int metadata = world.getBlockMetadata(i, j, k);
		if(!isClosedTrapDoor(metadata))
			switch(metadata & 3)
			{
				case 0:
					return ZP;
				case 1:
					return ZN;
				case 2:
					return PZ;
				case 3:
					return NZ;
			}
		return null;
	}

	private boolean isHeadedToRope()
	{
		int iTriple = getTriple(base_id, base_kd);
		int kTriple = getTriple(base_kd, base_id);

		if(iTriple > 0)
			if(kTriple > 0)
				return this == NN;
			else if(kTriple < 0)
				return this == NP;
			else
				return this == NZ;
		else if(iTriple < 0)
			if(kTriple > 0)
				return this == PN;
			else if(kTriple < 0)
				return this == PP;
			else
				return this == PZ;
		else
			if(kTriple > 0)
				return this == ZN;
			else if(kTriple < 0)
				return this == ZP;
			else
				return this == ZZ;
	}

	@SuppressWarnings("incomplete-switch")
	private boolean isOnAnchorFront(int j_offset)
	{
		switch(getBaseBlockMetadata(j_offset))
		{
			case 0:
				return false;
			case 1:
				return false;
			case 2:
				return this._k == 1;
			case 3:
				return this._k == -1;
			case 4:
				return this._i == 1;
			case 5:
				return this._i == -1;
		}
		return false;
	}

	private static Block getRopeId(int j_offset)
	{
		Block block = getBaseBlockId(j_offset);
		if(isRopeId(block))
			return block;
		return null;
	}

	private static boolean isRope(int j_offset)
	{
		return getRopeId(j_offset) != null;
	}

	private static boolean isRopeId(Block block)
	{
		return
			SmartMovingOptions.hasBetterThanWolves && hasBlockName(block, "tile.fcRopeBlock") ||
			SmartMovingOptions.hasRopesPlus && hasBlockName(block, "tile.blockRopeCentral");
	}

	private static Block getAnchorId(int j_offset)
	{
		Block block = getBaseBlockId(j_offset);
		if(isAnchorId(block))
			return block;
		return null;
	}

	private static boolean isAnchorId(Block block)
	{
		return hasBlockName(block, "tile.fcAnchor");
	}

	private static boolean isOnWallRope(int j_offset)
	{
		return (SmartMovingOptions.hasASGrapplingHook || SmartMovingOptions.hasRopesPlus) && isASRope(getBaseBlockId(j_offset));
	}

	private static boolean isASRope(Block block)
	{
		return hasBlockName(block, "tile.blockRope");
	}

	private static boolean isASGrapplingHook(Block block)
	{
		return hasBlockName(block, "tile.blockGrHk");
	}

	private boolean isASGrapplingHookFront(int metaData)
	{
		boolean kPos = metaData % 2 != 0;
		boolean iNeg = (metaData / 2) % 2 != 0;
		boolean kNeg = (metaData / 4) % 2 != 0;
		boolean iPos = (metaData / 8) % 2 != 0;

		if(_i > 0 && iPos || _i < 0 && iNeg)
			if(_k > 0)
				return kPos;
			else if(_k < 0)
				return kNeg;
			else
				return true;

		if(_k > 0 && kPos || _k < 0 && kNeg)
			if(_i > 0)
				return iPos;
			else if(_i < 0)
				return iNeg;
			else
				return true;

		return false;
	}

	private static boolean isOnOpenTrapDoor(int j_offset)
	{
		return isTrapDoor(getBaseBlockId(j_offset)) && !isClosedTrapDoor(getBaseBlockMetadata(j_offset));
	}

	private boolean isTrapDoorFront(int trapDoorMetadata)
	{
		if(this == NZ)
			return (trapDoorMetadata & 3) == 3;
		if(this == PZ)
			return (trapDoorMetadata & 3) == 2;
		if(this == ZP)
			return (trapDoorMetadata & 3) == 0;
		if(this == ZN)
			return (trapDoorMetadata & 3) == 1;
		if(this == PN)
			return (trapDoorMetadata & 3) == 2 || (trapDoorMetadata & 3) == 1;
		if(this == PP)
			return (trapDoorMetadata & 3) == 2 || (trapDoorMetadata & 3) == 0;
		if(this == NN)
			return (trapDoorMetadata & 3) == 3 || (trapDoorMetadata & 3) == 1;
		if(this == NP)
			return (trapDoorMetadata & 3) == 3 || (trapDoorMetadata & 3) == 0;
		return false;
	}

	private boolean isBottomStairCompactNotBack(int stairMetadata)
	{
		return !isTopStairCompact(stairMetadata) && !isStairCompactBack(stairMetadata);
	}

	private boolean isBottomStairCompactFront(int stairMetadata)
	{
		return !isTopStairCompact(stairMetadata) && isStairCompactFront(stairMetadata);
	}

	private boolean isTopStairCompactFront(int stairMetadata)
	{
		return isTopStairCompact(stairMetadata) && isStairCompactFront(stairMetadata);
	}

	private boolean isTopStairCompactBack(int stairMetadata)
	{
		return isTopStairCompact(stairMetadata) && isStairCompactBack(stairMetadata);
	}

	private boolean isStairCompactFront(int stairMetadata)
	{
		stairMetadata = stairMetadata & 3;
		if(this == NZ)
			return stairMetadata == 1;
		if(this == PZ)
			return stairMetadata == 0;
		if(this == ZP)
			return stairMetadata == 2;
		if(this == ZN)
			return stairMetadata == 3;
		if(this == PN)
			return stairMetadata == 0 || stairMetadata == 3;
		if(this == PP)
			return stairMetadata == 0 || stairMetadata == 2;
		if(this == NN)
			return stairMetadata == 1 || stairMetadata == 3;
		if(this == NP)
			return stairMetadata == 1 || stairMetadata == 2;
		return false;
	}

	private boolean isStairCompactBack(int stairMetadata)
	{
		stairMetadata = stairMetadata & 3;
		if(this == NZ)
			return stairMetadata == 0;
		if(this == PZ)
			return stairMetadata == 1;
		if(this == ZP)
			return stairMetadata == 3;
		if(this == ZN)
			return stairMetadata == 2;
		if(this == PN)
			return stairMetadata == 1 || stairMetadata == 2;
		if(this == PP)
			return stairMetadata == 1 || stairMetadata == 3;
		if(this == NN)
			return stairMetadata == 0 || stairMetadata == 2;
		if(this == NP)
			return stairMetadata == 0 || stairMetadata == 3;
		return false;
	}

	private static boolean isTopStairCompact(int stairMetadata)
	{
		return (stairMetadata & 4) != 0;
	}

	private static boolean isRedPowerWireTop(int coverSides)
	{
		return (coverSides >> 1) % 2 == 1;
	}

	private static boolean isRedPowerWireBottom(int coverSides)
	{
		return (coverSides >> 0) % 2 == 1;
	}

	private boolean isRedPowerWireFullFront(int coverSides)
	{
		if(this == NZ)
			return (coverSides >> 5) % 2 == 1;
		if(this == PZ)
			return (coverSides >> 4) % 2 == 1;
		if(this == ZP)
			return (coverSides >> 2) % 2 == 1;
		if(this == ZN)
			return (coverSides >> 3) % 2 == 1;
		if(this == PN)
			return PZ.isRedPowerWireFullFront(coverSides) && ZN.isRedPowerWireFullFront(coverSides);
		if(this == PP)
			return PZ.isRedPowerWireFullFront(coverSides) && ZP.isRedPowerWireFullFront(coverSides);
		if(this == NN)
			return NZ.isRedPowerWireFullFront(coverSides) && ZN.isRedPowerWireFullFront(coverSides);
		if(this == NP)
			return NZ.isRedPowerWireFullFront(coverSides) && ZP.isRedPowerWireFullFront(coverSides);
		return false;
	}

	private boolean isRedPowerWireAnyFront(int coverSides)
	{
		if(this == NZ)
			return (coverSides >> 5) % 2 == 1;
		if(this == PZ)
			return (coverSides >> 4) % 2 == 1;
		if(this == ZP)
			return (coverSides >> 2) % 2 == 1;
		if(this == ZN)
			return (coverSides >> 3) % 2 == 1;
		if(this == PN)
			return PZ.isRedPowerWireFullFront(coverSides) || ZN.isRedPowerWireFullFront(coverSides);
		if(this == PP)
			return PZ.isRedPowerWireFullFront(coverSides) || ZP.isRedPowerWireFullFront(coverSides);
		if(this == NN)
			return NZ.isRedPowerWireFullFront(coverSides) || ZN.isRedPowerWireFullFront(coverSides);
		if(this == NP)
			return NZ.isRedPowerWireFullFront(coverSides) || ZP.isRedPowerWireFullFront(coverSides);
		return false;
	}

	private boolean isRedPowerWireFullBack(int coverSides)
	{
		if(this == NZ)
			return (coverSides >> 4) % 2 == 1;
		if(this == PZ)
			return (coverSides >> 5) % 2 == 1;
		if(this == ZP)
			return (coverSides >> 3) % 2 == 1;
		if(this == ZN)
			return (coverSides >> 2) % 2 == 1;
		if(this == PN)
			return PZ.isRedPowerWireFullBack(coverSides) && ZN.isRedPowerWireFullBack(coverSides);
		if(this == PP)
			return PZ.isRedPowerWireFullBack(coverSides) && ZP.isRedPowerWireFullBack(coverSides);
		if(this == NN)
			return NZ.isRedPowerWireFullBack(coverSides) && ZN.isRedPowerWireFullBack(coverSides);
		if(this == NP)
			return NZ.isRedPowerWireFullBack(coverSides) && ZP.isRedPowerWireFullBack(coverSides);
		return false;
	}

	private boolean isRedPowerWireAnyBack(int coverSides)
	{
		if(this == NZ)
			return (coverSides >> 4) % 2 == 1;
		if(this == PZ)
			return (coverSides >> 5) % 2 == 1;
		if(this == ZP)
			return (coverSides >> 3) % 2 == 1;
		if(this == ZN)
			return (coverSides >> 2) % 2 == 1;
		if(this == PN)
			return PZ.isRedPowerWireFullBack(coverSides) || ZN.isRedPowerWireFullBack(coverSides);
		if(this == PP)
			return PZ.isRedPowerWireFullBack(coverSides) || ZP.isRedPowerWireFullBack(coverSides);
		if(this == NN)
			return NZ.isRedPowerWireFullBack(coverSides) || ZN.isRedPowerWireFullBack(coverSides);
		if(this == NP)
			return NZ.isRedPowerWireFullBack(coverSides) || ZP.isRedPowerWireFullBack(coverSides);
		return false;
	}

	private boolean isFenceGateFront(int metaData)
	{
		int direction = metaData % 4;
		if(this == NZ)
			return direction == 0 || direction == 2;
		if(this == PZ)
			return direction == 0 || direction == 2;
		if(this == ZP)
			return direction == 1 || direction == 3;
		if(this == ZN)
			return direction == 1 || direction == 3;
		return false;
	}

	private boolean headedToFrontWall(int i, int j_offset, int k, Block block)
	{
		boolean zn = getWallFlag(ZN, i, j_offset, k, block);
		boolean zp = getWallFlag(ZP, i, j_offset, k, block);
		boolean nz = getWallFlag(NZ, i, j_offset, k, block);
		boolean pz = getWallFlag(PZ, i, j_offset, k, block);
		boolean allOnNone = getAllWallsOnNoWall(block);

		if(allOnNone && !zn && !zp && !nz && !pz)
			zn = zp = nz = pz = true;

		return
			headedToWall(NZ, pz) ||
			headedToWall(PZ, nz) ||
			headedToWall(ZN, zp) ||
			headedToWall(ZP, zn);
	}

	private boolean headedToFrontSideWall(int i, int j_offset, int k, Block block)
	{
		boolean zn = getWallFlag(ZN, i, j_offset, k, block);
		boolean zp = getWallFlag(ZP, i, j_offset, k, block);
		boolean nz = getWallFlag(NZ, i, j_offset, k, block);
		boolean pz = getWallFlag(PZ, i, j_offset, k, block);
		boolean allOnNone = getAllWallsOnNoWall(block);

		if(allOnNone && !zn && !zp && !nz && !pz)
			zn = zp = nz = pz = true;

		boolean iTop = isTopHalf(base_id);
		boolean kTop = isTopHalf(base_kd);
		if(iTop)
			if(kTop)
				return
					headedToWall(NZ, zp) ||
					headedToWall(PZ, zp) ||
					headedToWall(ZN, pz) ||
					headedToWall(ZP, pz);
			else
				return
					headedToWall(NZ, zn) ||
					headedToWall(PZ, zn) ||
					headedToWall(ZN, pz) ||
					headedToWall(ZP, pz);
		else
			if(kTop)
				return
					headedToWall(NZ, zp) ||
					headedToWall(PZ, zp) ||
					headedToWall(ZN, nz) ||
					headedToWall(ZP, nz);
			else
				return
					headedToWall(NZ, zn) ||
					headedToWall(PZ, zn) ||
					headedToWall(ZN, nz) ||
					headedToWall(ZP, nz);
	}

	private boolean headedToWall(Orientation base, boolean result)
	{
		if(this == base || this == base.rotate(45) || this == base.rotate(-45))
			return result;
		return false;
	}

	private boolean headedToBaseWall(int j_offset, Block block)
	{
		boolean zn = getWallFlag(ZN, base_i, j_offset, base_k, block);
		boolean zp = getWallFlag(ZP, base_i, j_offset, base_k, block);
		boolean nz = getWallFlag(NZ, base_i, j_offset, base_k, block);
		boolean pz = getWallFlag(PZ, base_i, j_offset, base_k, block);
		boolean allOnNone = getAllWallsOnNoWall(block);

		if(allOnNone && !zn && !zp && !nz && !pz)
			zn = zp = nz = pz = true;

		boolean leaf = zn || zp || nz || pz;
		boolean coreOnly = !allOnNone && !leaf;

		boolean iTop = isTopHalf(base_id);
		boolean kTop = isTopHalf(base_kd);
		if(iTop)
			if(kTop)
				return headedToBaseWall(NN, NZ, ZN, zp, nz, pz, zn, coreOnly, leaf);
			else
				return headedToBaseWall(NP, NZ, ZP, zn, nz, pz, zp, coreOnly, leaf);
		else
			if(kTop)
				return headedToBaseWall(PN, PZ, ZN, zp, pz, nz, zn, coreOnly, leaf);
			else
				return headedToBaseWall(PP, PZ, ZP, zn, pz, nz, zp, coreOnly, leaf);
	}

	private boolean headedToBaseWall(Orientation diagonal, Orientation left, Orientation right, boolean leftFront, boolean rightFrontOpposite, boolean rightFront, boolean leftFrontOpposite, boolean co, boolean leaf)
	{
		if(this == diagonal)
			return leaf || co;
		if(this == left)
			return headedToBaseWall(leftFront, rightFrontOpposite, rightFront, leftFrontOpposite, co);
		if(this == right)
			return headedToBaseWall(rightFront, leftFrontOpposite, leftFront, rightFrontOpposite, co);
		return false;
	}

	private static boolean headedToBaseWall(boolean front, boolean sideOpposite, boolean side, boolean frontOpposite, boolean coreOnly)
	{
		return front || sideOpposite && !side || frontOpposite && !front && !side || coreOnly;
	}

	private boolean headedToBaseGrabWall(int j_offset, Block block)
	{
		boolean zn = getWallFlag(ZN, base_i, j_offset, base_k, block);
		boolean zp = getWallFlag(ZP, base_i, j_offset, base_k, block);
		boolean nz = getWallFlag(NZ, base_i, j_offset, base_k, block);
		boolean pz = getWallFlag(PZ, base_i, j_offset, base_k, block);
		boolean allOnNone = getAllWallsOnNoWall(block);

		if(allOnNone && !zn && !zp && !nz && !pz)
			zn = zp = nz = pz = true;

		boolean azn, azp, anz, apz;

		Block aboveBlock = getBlock(base_i, j_offset + 1, base_k);
		if(isFullEmpty(aboveBlock))
			azn = azp = anz = apz = false;
		else if(isWallBlock(aboveBlock, base_i, j_offset + 1, base_k))
		{
			azn = getWallFlag(ZN, base_i, j_offset + 1, base_k, aboveBlock);
			azp = getWallFlag(ZP, base_i, j_offset + 1, base_k, aboveBlock);
			anz = getWallFlag(NZ, base_i, j_offset + 1, base_k, aboveBlock);
			apz = getWallFlag(PZ, base_i, j_offset + 1, base_k, aboveBlock);
			boolean aboveAllOnNone = getAllWallsOnNoWall(aboveBlock);

			if(aboveAllOnNone && !azn && !azp && !anz && !apz)
				azn = azp = anz = apz = true;
		}
		else
			azn = azp = anz = apz = true;

		boolean iTop = isTopHalf(base_id);
		boolean kTop = isTopHalf(base_kd);
		if(iTop)
			if(kTop)
				return headedToBaseGrabWall(-this._i, -this._k, zp, pz, nz, zn, azp, apz, anz, azn);
			else
				return headedToBaseGrabWall(-this._i, this._k, pz, zn, zp, nz, apz, azn, azp, anz);
		else
			if(kTop)
				return headedToBaseGrabWall(this._i, -this._k, nz, zp, zn, pz, anz, azp, azn, apz);
			else
				return headedToBaseGrabWall(this._i, this._k, zn, nz, pz, zp, azn, anz, apz, azp);
	}

	private static boolean headedToBaseGrabWall(int i, int k, boolean front, boolean side, boolean frontOpposite, boolean sideOpposite, boolean aboveFront, boolean aboveSide, boolean aboveFrontOpposite, boolean aboveSideOpposite)
	{
		if(sideOpposite && !aboveSideOpposite && !front && !aboveFront && i == 1)
			return true;
		if(frontOpposite && !aboveFrontOpposite && !side && !aboveSide && k == 1)
			return true;
		if(side && !aboveSide && k >= 0)
			return true;
		if(front && !aboveFront && k >= 0)
			return true;
		if(frontOpposite && !aboveFrontOpposite && !aboveFront && i == 1 && k >= 0)
			return true;
		if(sideOpposite && !aboveSideOpposite && !aboveSide && k == 1 && i >= 0)
			return true;
		return false;
	}

	private boolean headedToRemoteFlatWall(Block block, int j_offset)
	{
		return
			!getWallFlag(this, remote_i, j_offset, remote_k, block) &&
			getWallFlag(this.rotate(90), remote_i, j_offset, remote_k, block) &&
			!getWallFlag(this.rotate(180), remote_i, j_offset, remote_k, block) &&
			getWallFlag(this.rotate(-90), remote_i, j_offset, remote_k, block);
	}

	@SuppressWarnings("incomplete-switch")
	private boolean getWallFlag(Orientation direction, int i, int j_offset, int k, Block block)
	{
		if(block instanceof BlockPane)
			return ((BlockPane)block).canPaneConnectToBlock(getBlock(i + direction._i, j_offset, k + direction._k));
		else if(isFenceBase(block))
		{
			if(block instanceof BlockFence)
				return ((BlockFence)block).canConnectFenceTo(world, i + direction._i, local_offset + j_offset, k + direction._k);
			if(block instanceof BlockWall)
				return ((BlockWall)block).canConnectWallTo(world, i + direction._i, local_offset + j_offset, k + direction._k);
			else if(SmartMovingOptions.hasBetterMisc && _canConnectFenceTo != null)
				return (Boolean)Reflect.Invoke(_canConnectFenceTo, block, world, i + direction._i, local_offset + j_offset, k + direction._k);
		}
		else if (isFenceGate(block))
		{
			int metaData = getBlockMetadata(i, j_offset, k);
			return isClosedFenceGate(metaData) && isFenceGateFront(metaData);
		}
		else
		{
			switch(getCarpentersBlockData(i, j_offset, k))
			{
				case -1:
					break;
				case 0:
					if (direction._k == 0 && direction._i != 0)
						return true;
					break;
				case 1:
					if (direction._i == 0 && direction._k != 0)
						return true;
					break;
			}
		}
		return false;
	}

	private static boolean getAllWallsOnNoWall(Block block)
	{
		return block instanceof BlockPane;
	}

	private static boolean isTopHalf(double d)
	{
		return (int)Math.abs(Math.floor(d * 2D)) % 2 == 1;
	}

	private static int getTriple(double primary, double secondary)
	{
		primary = primary - Math.floor(primary) - 0.5;
		secondary = secondary - Math.floor(secondary) - 0.5;

		if(Math.abs(primary) * 2 < Math.abs(secondary))
			return 0;
		else if(primary > 0)
			return 1;
		else if(primary < 0)
			return -1;
		else
			return 0;
	}

	private static boolean isBottomHalfBlock(Block block, int metadata)
	{
		if(isHalfBlock(block) && isHalfBlockBottomMetaData(metadata))
			return true;
		if(block == Block.getBlockFromName("bed"))
			return true;
		if(SmartMovingOptions.hasBetterThanWolves && isAnchorId(block) && metadata == 1)
			return true;
		return false;
	}

	private static boolean isTopHalfBlock(Block block, int metadata)
	{
		return isHalfBlock(block) && isHalfBlockTopMetaData(metadata);
	}

	private static boolean isHalfBlockBottomMetaData(int metadata)
	{
		return (metadata & 8) == 0;
	}

	private static boolean isHalfBlockTopMetaData(int metadata)
	{
		return (metadata & 8) != 0;
	}

	private static boolean isHalfBlock(Block block)
	{
		return isBlock(block, BlockSlab.class, _knownHalfBlocks) && !((BlockSlab)block).isOpaqueCube();
	}

	private static boolean isStairCompact(Block block)
	{
		return isBlock(block, BlockStairs.class, _knownCompactStairBlocks);
	}

	private boolean isLowerHalfFrontFullEmpty(int i, int j_offset, int k)
	{
		Block block = getBlock(i, j_offset, k);
		boolean empty = isFullEmpty(block);

		if(!empty && SmartMovingOptions.hasRedPowerWire)
		{
			if(isRedPowerWire(block))
			{
				int coverSides = getRpCoverSides(i, j_offset, k);
				if(!isRedPowerWireAnyFront(coverSides))
					empty = true;
			}
		}

		if(!empty && SmartMovingOptions.hasBetterThanWolves)
		{
			if(isAnchorId(block))
			{
				empty = getBlockMetadata(i, j_offset, k) == 0;
			}
		}

		if(!empty && isStairCompact(block) && isTopStairCompactFront(getBlockMetadata(i, j_offset, k)))
			empty = true;

		if(!empty && isHalfBlock(block) && isHalfBlockTopMetaData(getBlockMetadata(i, j_offset, k)))
			empty = true;

		if(!empty && isWallBlock(block, i, j_offset, k) && !headedToFrontWall(i, j_offset, k, block))
			empty = true;

		if(!empty && isDoor(block) && !rotate(180).isDoorFrontBlocked(i, j_offset, k))
			empty = true;

		if(!empty && (SmartMovingOptions.hasASGrapplingHook || SmartMovingOptions.hasRopesPlus) && isASRope(block) && !rotate(180).isASGrapplingHookFront(getRemoteBlockMetadata(j_offset)))
			empty = true;

		if(empty && isBlockIdOfType(block, _ladderKitLadderTypes) && rotate(180).hasLadderOrientation(i, j_offset, k))
			empty = false;

		return empty;
	}

	private boolean isUpperHalfFrontAnySolid(int i, int j_offset, int k)
	{
		Block block = getBlock(i, j_offset, k);
		boolean solid = isUpperHalfFrontFullSolid(i, j_offset, k);
		if(solid && isWallBlock(block, i, j_offset, k) && !headedToFrontWall(i, j_offset, k, block))
			solid = false;
		return solid;
	}

	private static boolean isUpperHalfFrontFullSolid(int i, int j_offset, int k)
	{
		Block block = getBlock(i, j_offset, k);
		if(block == null)
			return false;

		boolean solid = isSolid(block.getMaterial());
		if(solid && block == Block.getBlockFromName("standing_sign"))
			solid = false;
		if(solid && block == Block.getBlockFromName("wall_sign"))
			solid = false;
		if(solid && block instanceof BlockPressurePlate)
			solid = false;
		if(solid && isTrapDoor(block))
			solid = false;
		if(solid && SmartMovingOptions.hasASGrapplingHook && isASGrapplingHook(block))
			solid = false;
		if(solid && isOpenFenceGate(block, getBlockMetadata(i, j_offset, k)))
			solid = false;
		if(solid && isExternalBlockType(block, _blockCarpentersLadder))
			solid = false;
		return solid;
	}

	private static boolean isFullEmpty(Block block)
	{
		if(block == null)
			return true;

		boolean empty = !isSolid(block.getMaterial());
		if(!empty && block == Block.getBlockFromName("standing_sign"))
			empty = true;
		if(!empty && block == Block.getBlockFromName("wall_sign"))
			empty = true;
		if(!empty && block instanceof BlockPressurePlate)
			empty = true;
		if(!empty && (SmartMovingOptions.hasASGrapplingHook || SmartMovingOptions.hasRopesPlus) && isASGrapplingHook(block))
			empty = true;
		if(empty && (SmartMovingOptions.hasASGrapplingHook || SmartMovingOptions.hasRopesPlus) && isASRope(block))
			empty = false;
		return empty;
	}

	private static boolean isFenceBase(Block block)
	{
		return isBlock(block, BlockFence.class, _knownFenceBlocks) || isBlock(block, BlockWall.class, _knownWallBlocks);
	}

	private static boolean isFence(Block block, int i, int j_offset, int k)
	{
		return isFenceBase(block) || isClosedFenceGate(block, getBlockMetadata(i, j_offset, k));
	}

	private static boolean isFence(int i, int j_offset, int k)
	{
		return getFenceId(i, j_offset, k) != null;
	}

	private static Block getFenceId(int i, int j_offset, int k)
	{
		Block block = getBlock(i, j_offset, k);
		if(isFenceBase(block) || isClosedFenceGate(block, getBlockMetadata(i, j_offset, k)))
			return block;
		return null;
	}

	private static boolean isClosedFenceGate(Block block, int metdata)
	{
		return isFenceGate(block) && isClosedFenceGate(metdata);
	}

	private static boolean isFenceGate(Block block)
	{
		return isBlock(block, BlockFenceGate.class, _knownFanceGateBlocks);
	}

	private static boolean isOpenFenceGate(Block block, int metdata)
	{
		return isFenceGate(block) && !isClosedFenceGate(metdata);
	}

	private static boolean isClosedFenceGate(int metdata)
	{
		return (metdata & 4) == 0;
	}

	private static boolean isOpenTrapDoor(int i, int j_offset, int k)
	{
		return isTrapDoor(i, j_offset, k) && !isClosedTrapDoor(getBlockMetadata(i, j_offset, k));
	}

	private static boolean isClosedTrapDoor(int i, int j_offset, int k)
	{
		return isTrapDoor(i, j_offset, k) && isClosedTrapDoor(getBlockMetadata(i, j_offset, k));
	}

	private static boolean isTrapDoor(int i, int j_offset, int k)
	{
		return isTrapDoor(getBlock(i, j_offset, k));
	}

	public static boolean isTrapDoor(Block block)
	{
		return isBlock(block, BlockTrapDoor.class, _knownTrapDoorBlocks);
	}

	private static boolean isBlock(Block block, Class<?> type, Block[] baseBlocks)
	{
		if(block == null)
			return false;

		if(type != null && baseBlocks.length > 1 && isBlockType(block, type))
			return true;

		for(int i=0; i<baseBlocks.length; i++)
			if(baseBlocks[i] != null && block == baseBlocks[i])
				return true;

		if(type != null && isBlockType(block, type))
			return true;

		Class<?> blockType = block.getClass();
		for(int i=0; i<baseBlocks.length; i++)
			if(baseBlocks[i] != null && baseBlocks[i].getClass().isAssignableFrom(blockType))
				return true;

		return false;
	}

	private static boolean isExternalBlockType(Block block, Class<?> externalType)
	{
		return externalType != null && isBlockType(block, externalType);
	}

	private static boolean isBlockType(Block block, Class<?> type)
	{
		return block != null && type.isAssignableFrom(block.getClass());
	}

	public static boolean isClosedTrapDoor(int metaData)
	{
		return (metaData & 4) == 0;
	}

	private static boolean isDoor(Block block)
	{
		return block == Block.getBlockFromName("wooden_door") || block == Block.getBlockFromName("iron_door");
	}

	private static boolean isDoorTop(int metaData)
	{
		return metaData == 8;
	}

	@SuppressWarnings("incomplete-switch")
	private boolean isDoorFrontBlocked(int i, int j_offset, int k)
	{
		int metaData = getBlockMetadata(i, j_offset, k);
		switch(metaData)
		{
			case 8:
				return isDoorFrontBlocked(i, j_offset - 1, k);
			case 4:
			case 1:
				return this._k < 0;
			case 5:
			case 2:
				return this._i > 0;
			case 6:
			case 3:
				return this._k > 0;
			case 7:
			case 0:
				return this._i < 0;
		}

		return true;
	}

	private static Block getWallBlockId(int i, int j_offset, int k)
	{
		Block block = getBlock(i, j_offset, k);
		if(isWallBlock(block, i, j_offset, k))
			return block;
		return null;
	}

	private static boolean isWallBlock(Block block, int i, int j_offset, int k)
	{
		return isBlock(block, BlockPane.class, _knownThinWallBlocks) || isFence(block, i, j_offset, k) || (isExternalBlockType(block, _blockCarpentersLadder) && getCarpentersBlockData(i, j_offset, k) < 2);
	}

	private static boolean isBaseAccessible(int j_offset)
	{
		return isBaseAccessible(j_offset, false, false);
	}

	private static boolean isBaseAccessible(int j_offset, boolean bottom, boolean full)
	{
		Block id = getBaseBlockId(j_offset);
		boolean accessible = isEmpty(base_i, j_offset, base_k);
		if(SmartMovingOptions.hasRedPowerWire && !accessible)
		{
			if(isRedPowerWire(id))
			{
				int coverSides = getRpCoverSides(base_i, j_offset, base_k);
				accessible = !isRedPowerWireBottom(coverSides);

				Block lowerId = getBaseBlockId(j_offset - 1);
				if(isRedPowerWire(lowerId))
				{
					int lowerCoverSides = getRpCoverSides(base_i, j_offset - 1, base_k);
					accessible &= !isRedPowerWireTop(lowerCoverSides);
				}
			}
		}

		if(!accessible && isFullEmpty(id))
			accessible = true;

		if(!accessible && isOpenTrapDoor(base_i, j_offset, base_k))
			accessible = true;

		if(!accessible && bottom && isClosedTrapDoor(base_i, j_offset, base_k))
			accessible = true;

		if(!accessible && !full && isWallBlock(id, base_i, j_offset, base_k))
			accessible = true;

		if(!accessible && !full && (SmartMovingOptions.hasASGrapplingHook || SmartMovingOptions.hasRopesPlus) && isASRope(id))
			accessible = true;

		if(!accessible && isDoor(id))
			accessible = true;

		if(!accessible && isExternalBlockType(id, _blockCarpentersLadder))
			accessible = true;

		return accessible;
	}

	private boolean isRemoteAccessible(int j_offset)
	{
		boolean accessible = isEmpty(remote_i, j_offset, remote_k);
		if(SmartMovingOptions.hasRedPowerWire && !accessible)
		{
			Block id = getRemoteBlockId(j_offset);
			if(isRedPowerWire(id))
			{
				int coverSides = getRpCoverSides(remote_i, j_offset, remote_k);
				accessible = !isRedPowerWireAnyFront(coverSides);

				Block baseId = getBaseBlockId(j_offset);
				if(isRedPowerWire(baseId))
				{
					int baseCoverSides = getRpCoverSides(base_i, j_offset, base_k);
					accessible &= !isRedPowerWireAnyBack(baseCoverSides);
				}
			}
		}

		if(accessible)
		{
			Block id = getBaseBlockId(j_offset);
			if(isTrapDoor(id))
				accessible = !isTrapDoorFront(getBlockMetadata(base_i, j_offset, base_k));

			if(accessible && isDoor(id))
				accessible = !isDoorFrontBlocked(base_i, j_offset, base_k);

			if(remoteLadderClimbing(j_offset))
				accessible = false;
		}

		if(!accessible && isTrapDoor(remote_i, j_offset, remote_k))
			accessible = isClosedTrapDoor(getRemoteBlockMetadata(j_offset));

		if(!accessible)
		{
			Block id = getRemoteBlockId(j_offset);
			if(isWallBlock(id, remote_i, j_offset, remote_k) && !headedToFrontWall(remote_i, j_offset, remote_k, id) && !isFence(remote_i, j_offset - 1, remote_k))
				accessible = true;

			Block belowId = getRemoteBlockId(j_offset - 1);
			if(!accessible && isFence(belowId, remote_i, j_offset - 1, remote_k) && (!headedToFrontWall(remote_i, j_offset - 1, remote_k, belowId) || isWallBlock(getBaseBlockId(j_offset - 1), base_i, j_offset - 1, base_k)))
				if(belowId != Block.getBlockFromName("cobblestone_wall") || headedToRemoteFlatWall(belowId, -1))
					accessible = true;

			if(!accessible && isDoor(id) && !rotate(180).isDoorFrontBlocked(remote_i, j_offset, remote_k))
				accessible = true;

			if((SmartMovingOptions.hasASGrapplingHook || SmartMovingOptions.hasRopesPlus) && isASRope(id) && !rotate(180).isASGrapplingHookFront(getRemoteBlockMetadata(j_offset)))
				accessible = true;
		}

		return accessible;
	}

	private boolean isAccessAccessible(int j_offset)
	{
		if(!_isDiagonal)
			return true;

		return isEmpty(remote_i, j_offset, base_k) && isEmpty(base_i, j_offset, remote_k);
	}

	private boolean isFullExtentAccessible(int j_offset, boolean grabRemote)
	{
		boolean accessible = isFullAccessible(j_offset, grabRemote);
		if(SmartMovingOptions.hasRedPowerWire && accessible)
		{
			Block topId = getRemoteBlockId(j_offset);
			if(isRedPowerWire(topId))
			{
				int coverSides = getRpCoverSides(remote_i, j_offset, remote_k);
				if(isRedPowerWireBottom(coverSides))
					accessible = false;

			}

			Block bottomId = getRemoteBlockId(j_offset - 1);
			if(isRedPowerWire(bottomId))
			{
				int coverSides = getRpCoverSides(remote_i, j_offset - 1, remote_k);
				if(isRedPowerWireTop(coverSides))
					accessible = false;
			}
		}
		return accessible;
	}

	private boolean isJustLowerHalfExtentAccessible(int j_offset)
	{
		Block remoteId = getRemoteBlockId(j_offset);
		int remoteMetaData = getRemoteBlockMetadata(j_offset);

		boolean accessible = false;
		if(!accessible)
			accessible = isTopHalfBlock(remoteId, remoteMetaData);
		if(!accessible)
			accessible = isStairCompact(remoteId) && isTopStairCompactFront(remoteMetaData);
		return accessible;
	}

	private boolean isFullAccessible(int j_offset, boolean grabRemote)
	{
		if(grabRemote)
			return isBaseAccessible(j_offset) && isRemoteAccessible(j_offset) && isAccessAccessible(j_offset);
		return isEmpty(base_i, j_offset, base_k);
	}

	private static boolean isEmpty(int i, int j_offset, int k)
	{
		return isFullEmpty(getBlock(i, j_offset, k)) && !isFence(i, j_offset - 1, k);
	}

	private boolean isUpperHalfFrontEmpty(int i, int j_offset, int k)
	{
		Block block = getBlock(i, j_offset, k);
		boolean empty = isFullEmpty(block);

		if(!empty)
		{
			int metadata = getBlockMetadata(i, j_offset, k);
			if(isBottomHalfBlock(block, metadata))
				empty = true;

			if(!empty && isStairCompact(block) && isBottomStairCompactFront(metadata))
				empty = true;
		}

		if(SmartMovingOptions.hasRedPowerWire && !empty)
		{
			if(isRedPowerWire(block))
			{
				int coverSides = getRpCoverSides(i, j_offset, k);
				if(!isRedPowerWireAnyFront(coverSides))
					empty = true;
			}
		}
		if(!empty && isTrapDoor(block))
			empty = true;

		if(!empty)
		{
			Block wallId = getWallBlockId(i, j_offset, k);
			if(wallId != null && (!headedToFrontWall(i, j_offset, k, wallId) || isWallBlock(getBlock(i - _i, j_offset, k - _k), i - _i, j_offset, k - _k)))
				empty = true;
		}

		if(empty && isBlockIdOfType(block, _ladderKitLadderTypes) && rotate(180).hasLadderOrientation(i, j_offset, k))
			empty = false;

		return empty;
	}

	private static int getRpCoverSides(int i, int j_offset, int k)
	{
		TileEntity tileEntity = getBlockTileEntity(i, j_offset, k);
		Class<?> tileEntityClass = tileEntity.getClass();
		while(!tileEntityClass.getSimpleName().equals("TileCovered"))
			tileEntityClass = tileEntityClass.getSuperclass();
		return (Integer)Reflect.GetField(tileEntityClass, tileEntity, new Name("CoverSides"));
	}

	private static boolean isRedPowerWire(Block block)
	{
		return hasBlockName(block, "tile.rpwire");
	}

	public static int getFiniteLiquidWater(Block block)
	{
		String blockName = getBlockName(block);
		if(blockName == null)
			return 0;
		if(blockName.equals("tile.nocean"))
			return 2;
		if(blockName.equals("tile.nwater_still"))
			return 1;
		return 0;
	}

	private static boolean isSolid(Material material)
	{
		return material.isSolid() && material.blocksMovement();
	}

	private static Block getBlock(int i, int j_offset, int k)
	{
		return world.getBlock(i, local_offset + j_offset, k);
	}

	private static int getBlockMetadata(int i, int j_offset, int k)
	{
		return world.getBlockMetadata(i, local_offset + j_offset, k);
	}

	private static TileEntity getBlockTileEntity(int i, int j_offset, int k)
	{
		return world.getTileEntity(i, local_offset + j_offset, k);
	}

	private static Block getBaseBlockId(int j_offset)
	{
		return world.getBlock(base_i, local_offset + j_offset, base_k);
	}

	private static int getBaseBlockMetadata(int j_offset)
	{
		return world.getBlockMetadata(base_i, local_offset + j_offset, base_k);
	}

	private static boolean isBlockIdOfType(Block block, Class<?>... types)
	{
		if(types == null || block == null)
			return false;

		Class<?> blockType = block.getClass();
		for(Class<?> type : types)
			if(type.isAssignableFrom(blockType))
				return true;

		return false;
	}

	private static Block getRemoteBlockId(int j_offset)
	{
		return world.getBlock(remote_i, local_offset + j_offset, remote_k);
	}

	private static int getRemoteBlockMetadata(int j_offset)
	{
		return world.getBlockMetadata(remote_i, local_offset + j_offset, remote_k);
	}

	private static boolean hasBlockName(Block block, String name)
	{
		String blockName = getBlockName(block);
		return blockName != null && blockName.equals(name);
	}

	private static String getBlockName(Block block)
	{
		if(block == null)
			return null;
		return block.getUnlocalizedName();
	}

	private void initialize(World w, int i, double id, double jhd, int k, double kd)
	{
		world = w;

		base_i = i;
		base_id = id;
		base_jhd = jhd;
		base_k = k;
		base_kd = kd;

		remote_i = i + _i;
		remote_k = k + _k;
	}

	private static void initializeOffset(double offset_halfs, boolean isClimbCrawling, boolean isCrawlClimbing, boolean isCrawling)
	{
		crawl = isClimbCrawling || isCrawlClimbing || isCrawling;

		double offset_jhd = base_jhd + offset_halfs;
		int offset_jh = MathHelper.floor_double(offset_jhd);
		jh_offset = offset_jhd - offset_jh;

		all_j = offset_jh / 2;
		all_offset = offset_jh % 2;
	}

	private static void initializeLocal(int localOffset)
	{
		local_halfOffset = localOffset + all_offset;
		local_half = Math.abs(local_halfOffset) % 2;
		local_offset = all_j + (local_halfOffset - local_half) / 2;
	}

	private final static float _handClimbingHoldGap = Math.min(0.25F, 0.06F * Math.max(Config._freeClimbingUpSpeedFactor.value, Config._freeClimbingDownSpeedFactor.value));

	private static ClimbGap _climbGapTemp = new ClimbGap();
	private static ClimbGap _climbGapOuterTemp = new ClimbGap();

	private static World world;
	private static double base_jhd, jh_offset;
	private static int all_j, all_offset;
	private static int base_i, base_k;
	private static double base_id, base_kd;
	private static int remote_i, remote_k;
	private static boolean crawl;

	private static int local_halfOffset;
	private static int local_half;
	private static int local_offset;

	private static boolean grabRemote;
	private static int grabType;
	private static Block grabBlock;
	private static int grabMeta;

	@Override
	public String toString()
	{
		if(this == ZZ)
			return "ZZ";
		if(this == NZ)
			return "NZ";
		if(this == PZ)
			return "PZ";
		if(this == ZP)
			return "ZP";
		if(this == ZN)
			return "ZN";
		if(this == PN)
			return "PN";
		if(this == PP)
			return "PP";
		if(this == NN)
			return "NN";
		if(this == NP)
			return "NP";
		return "UNKNOWN(" + _i + "," + _k + ")";
	}

	private static final Method _canConnectFenceTo;

	private static final Block[] _knownFanceGateBlocks;
	private static final Block[] _knownFenceBlocks;
	private static final Block[] _knownWallBlocks;
	private static final Block[] _knownHalfBlocks;
	private static final Block[] _knownCompactStairBlocks;
	private static final Block[] _knownTrapDoorBlocks;
	private static final Block[] _knownThinWallBlocks;

	private static final Class<?>[] _ladderKitLadderTypes;
	private static final Class<?> _blockCarpentersLadder;
	private static final Method _carpentersTEBaseBlockGetData;

	static
	{
		Class<?> modFenceBlock = Reflect.LoadClass(Block.class, SmartMovingInstall.ModBlockFence, false);
		_canConnectFenceTo = modFenceBlock != null ? Reflect.GetMethod(modFenceBlock, new Name("canConnectFenceTo"), false, IBlockAccess.class, int.class, int.class, int.class) : null;

		_blockCarpentersLadder = Reflect.LoadClass(Block.class, SmartMovingInstall.CarpentersBlockLadder, false);
		if (_blockCarpentersLadder != null)
		{
			Class<?> carpentersTEBaseBlock = Reflect.LoadClass(Block.class, SmartMovingInstall.CarpentersTEBaseBlock, false);
			_carpentersTEBaseBlockGetData = Reflect.GetMethod(carpentersTEBaseBlock, SmartMovingInstall.CarpentersTEBaseBlock_getData);
		}
		else
			_carpentersTEBaseBlockGetData = null;

		_knownFanceGateBlocks = new Block[] { Block.getBlockFromName("fence_gate") };
		_knownFenceBlocks = new Block[] { Block.getBlockFromName("fence"), Block.getBlockFromName("nether_brick_fence") };
		_knownWallBlocks = new Block[] { Block.getBlockFromName("cobblestone_wall") };
		_knownHalfBlocks = new Block[] {Block.getBlockFromName("stone_slab"), Block.getBlockFromName("double_stone_slab"), Block.getBlockFromName("wooden_slab"), Block.getBlockFromName("double_wooden_slab") };
		_knownCompactStairBlocks = new Block[] { Block.getBlockFromName("stone_stairs"), Block.getBlockFromName("oak_stairs"), Block.getBlockFromName("dark_oak_stairs"), Block.getBlockFromName("brick_stairs"), Block.getBlockFromName("nether_brick_stairs"), Block.getBlockFromName("sandstone_stairs"), Block.getBlockFromName("stone_brick_stairs"), Block.getBlockFromName("birch_stairs"), Block.getBlockFromName("jungle_stairs"), Block.getBlockFromName("spruce_stairs"), Block.getBlockFromName("quartz_stairs"), Block.getBlockFromName("acacia_stairs") };
		_knownTrapDoorBlocks = new Block[] { Block.getBlockFromName("trapdoor") };
		_knownThinWallBlocks = new Block[] { Block.getBlockFromName("iron_bars"), Block.getBlockFromName("glass_pane") };

		Class<?> blockRopeLadder = Reflect.LoadClass(Block.class, SmartMovingInstall.BlockRopeLadder, false);
		Class<?> blockSturdyLadder = Reflect.LoadClass(Block.class, SmartMovingInstall.BlockSturdyLadder, false);
		if(blockRopeLadder != null)
			if(blockSturdyLadder != null)
				_ladderKitLadderTypes = new Class<?>[] { blockRopeLadder, blockSturdyLadder };
			else
				_ladderKitLadderTypes = new Class<?>[] { blockRopeLadder };
		else
			if(blockSturdyLadder != null)
				_ladderKitLadderTypes = new Class<?>[] { blockSturdyLadder };
			else
				_ladderKitLadderTypes = null;
	}
}