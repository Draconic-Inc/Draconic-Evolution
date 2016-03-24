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

import net.minecraft.client.entity.*;

public class SmartMovingOther extends SmartMoving
{
	public boolean foundAlive;

	public SmartMovingOther(EntityOtherPlayerMP sp)
	{
		super(sp, null);
	}

	public void processStatePacket(long state)
	{
		actualFeetClimbType = (int)(state & 15);
		state >>>= 4;

		actualHandsClimbType = (int)(state & 15);
		state >>>= 4;

		_isJumping = (state & 1) != 0;
		state >>>= 1;

		isDiving = (state & 1) != 0;
		state >>>= 1;

		isDipping = (state & 1) != 0;
		state >>>= 1;

		isSwimming = (state & 1) != 0;
		state >>>= 1;

		isCrawlClimbing = (state & 1) != 0;
		state >>>= 1;

		isCrawling = (state & 1) != 0;
		state >>>= 1;

		isClimbing = (state & 1) != 0;
		state >>>= 1;

		boolean isSmall = (state & 1) != 0;
		heightOffset = isSmall ? -1 : 0;
		sp.height = 1.8F + heightOffset;
		state >>>= 1;

		_doFallingAnimation = (state & 1) != 0;
		state >>>= 1;

		_doFlyingAnimation = (state & 1) != 0;
		state >>>= 1;

		isCeilingClimbing = (state & 1) != 0;
		state >>>= 1;

		isLevitating = (state & 1) != 0;
		state >>>= 1;

		isHeadJumping = (state & 1) != 0;
		state >>>= 1;

		isSliding = (state & 1) != 0;
		state >>>= 1;

		angleJumpType = (int)(state & 7);
		state >>>= 3;

		isFeetVineClimbing = (state & 1) != 0;
		state >>>= 1;

		isHandsVineClimbing = (state & 1) != 0;
		state >>>= 1;

		isClimbJumping = (state & 1) != 0;
		state >>>= 1;

		boolean wasClimbBackJumping = isClimbBackJumping;
		isClimbBackJumping = (state & 1) != 0;
		if(!wasClimbBackJumping && isClimbBackJumping)
			onStartClimbBackJump();
		state >>>= 1;

		isSlow = (state & 1) != 0;
		state >>>= 1;

		isFast = (state & 1) != 0;
		state >>>= 1;

		boolean wasWallJumping = isWallJumping;
		isWallJumping = (state & 1) != 0;
		if(!wasWallJumping && isWallJumping)
			onStartWallJump(null);
		state >>>= 1;

		isRopeSliding = (state & 1) != 0;
	}

	@Override
	public boolean isJumping()
	{
		return _isJumping;
	}

	@Override
	public boolean doFlyingAnimation()
	{
		return _doFlyingAnimation;
	}

	@Override
	public boolean doFallingAnimation()
	{
		return _doFallingAnimation;
	}

	private boolean _isJumping = false;
	private boolean _doFlyingAnimation = false;
	private boolean _doFallingAnimation = false;
}