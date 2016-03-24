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

package net.smart.moving.config;

public class SmartMovingClientConfig extends SmartMovingConfig
{
	public boolean isSneakingEnabled()
	{
		return _sneak.value || !enabled;
	}

	public boolean isStandardBaseClimb()
	{
		return _isStandardBaseClimb.value || !enabled;
	}

	public boolean isSimpleBaseClimb()
	{
		return _isSimpleBaseClimb.value && enabled;
	}

	public boolean isSmartBaseClimb()
	{
		return _isSmartBaseClimb.value && enabled;
	}

	public boolean isFreeBaseClimb()
	{
		return _isFreeBaseClimb.value && enabled;
	}

	public boolean isTotalFreeLadderClimb()
	{
		return isFreeBaseClimb() && _freeBaseLadderClimb.value;
	}

	public boolean isTotalFreeVineClimb()
	{
		return isFreeBaseClimb() && _freeBaseVineClimb.value;
	}

	public boolean isFreeClimbAutoLaddderEnabled()
	{
		return _freeClimbingAutoLaddder.value && enabled;
	}

	public boolean isFreeClimbAutoVineEnabled()
	{
		return _freeClimbingAutoVine.value && enabled;
	}

	public boolean isFreeClimbingEnabled()
	{
		return _freeClimb.value && enabled;
	}

	public boolean isCeilingClimbingEnabled()
	{
		return _ceilingClimbing.value && enabled;
	}

	public boolean isSwimmingEnabled()
	{
		return _swim.value && enabled;
	}

	public boolean isDivingEnabled()
	{
		return _dive.value && enabled;
	}

	public boolean isLavaLikeWaterEnabled()
	{
		return _lavaLikeWater.value && enabled;
	}

	public boolean isFlyingEnabled()
	{
		return _fly.value && enabled;
	}

	public boolean isLevitateSmallEnabled()
	{
		return this._levitateSmall.value && enabled;
	}

	public boolean isRunningEnabled()
	{
		return _run.value || !enabled;
	}

	public boolean isRunExhaustionEnabled()
	{
		return _runExhaustion.value && enabled;
	}

	public boolean isClimbExhaustionEnabled()
	{
		return _climbExhaustion.value && enabled;
	}

	public boolean isCeilingClimbExhaustionEnabled()
	{
		return _ceilingClimbExhaustion.value && enabled;
	}

	public boolean isSprintingEnabled()
	{
		return _sprint.value && enabled;
	}

	public boolean isSprintExhaustionEnabled()
	{
		return _sprintExhaustion.value && enabled;
	}

	public boolean isJumpChargingEnabled()
	{
		return _jumpCharge.value && enabled;
	}

	public boolean isHeadJumpingEnabled()
	{
		return _headJump.value && enabled;
	}

	public boolean isSlidingEnabled()
	{
		return _slide.value && enabled;
	}

	public boolean isCrawlingEnabled()
	{
		return _crawl.value && enabled;
	}

	public boolean isExhaustionLossHungerEnabled()
	{
		return _exhaustionLossHunger.value && _hungerGain.value && enabled;
	}

	public boolean isHungerGainEnabled()
	{
		return _hungerGain.value || !enabled;
	}

	public boolean isLevitationAnimationEnabled()
	{
		return _levitateAnimation.value && enabled;
	}

	public boolean isFallAnimationEnabled()
	{
		return _fallAnimation.value && enabled;
	}

	public final static int Sprinting = 0;
	public final static int Running = 1;
	public final static int Walking = 2;
	public final static int Sneaking = 3;
	public final static int Standing = 4;

	public final static int Up = 0;
	public final static int ChargeUp = 1;
	public final static int Angle = 2;
	public final static int HeadUp = 3;
	public final static int SlideDown = 4;
	public final static int ClimbUp = 5;
	public final static int ClimbUpHandsOnly = 6;
	public final static int ClimbBackUp = 7;
	public final static int ClimbBackUpHandsOnly = 8;
	public final static int ClimbBackHead = 9;
	public final static int ClimbBackHeadHandsOnly = 10;
	public final static int WallUp = 11;
	public final static int WallHead = 12;
	public final static int WallUpSlide = 13;
	public final static int WallHeadSlide = 14;

	public boolean isJumpingEnabled(int speed, int type)
	{
		if(!enabled)
			return true;

		if(type == ChargeUp)
			return _jumpCharge.value;
		if(type == SlideDown)
			return _slide.value;
		if(type == ClimbUp || type == ClimbUpHandsOnly)
			return _climbUpJump.value;
		if(type == ClimbBackUp || type == ClimbBackUpHandsOnly)
			return _climbBackUpJump.value;
		if(type == ClimbBackHead || type == ClimbBackHeadHandsOnly)
			return _climbBackHeadJump.value;

		if(type == WallUp)
			return _wallUpJump.value;
		if(type == WallHead)
			return _wallHeadJump.value;

		if(speed == Sprinting)
			return _sprintJump.value;
		else if(speed == Running)
			return _runJump.value;
		else if(speed == Walking)
			return _walkJump.value;
		else if(speed == Sneaking)
			return _sneakJump.value;
		else if(speed == Standing)
			return _standJump.value;

		return true;
	}

	public boolean isSideJumpEnabled()
	{
		return enabled && this._angleJumpSide.value;
	}

	public boolean isBackJumpEnabled()
	{
		return enabled && this._angleJumpBack.value;
	}

	public boolean isWallJumpEnabled()
	{
		return enabled && this._wallUpJump.value;
	}

	public boolean isJumpExhaustionEnabled(int speed, int type)
	{
		if(!enabled)
			return false;

		boolean result = _jumpExhaustion.value;

		if(type == SlideDown)
			return result && _jumpSlideExhaustion.value;
		else if(type == Angle)
			result &= _angleJumpExhaustion.value;
		else if(type == ClimbUp || type == ClimbUpHandsOnly)
			result &= _climbJumpUpExhaustion.value;
		else if(type == ClimbBackUp || type == ClimbBackUpHandsOnly )
			result &= _climbJumpBackUpExhaustion.value;
		else if(type == ClimbBackHead || type == ClimbBackHeadHandsOnly)
			result &= _climbJumpBackHeadExhaustion.value;
		else if(type == WallUp)
			result &= _wallUpJumpExhaustion.value;
		else if(type == WallHead)
			result &= _wallHeadJumpExhaustion.value;
		else
			result &= _upJumpExhaustion.value;

		if(type == ClimbUp || type == ClimbUpHandsOnly || type == ClimbBackUp || type == ClimbBackUpHandsOnly || type == ClimbBackHead || type == ClimbBackHeadHandsOnly)
			return result && _climbJumpExhaustion.value;

		if(type == WallUp || type == WallHead)
			return result && _wallJumpExhaustion.value;

		if(speed == Sprinting)
			result &= _sprintJumpExhaustion.value;
		else if(speed == Running)
			result &= _runJumpExhaustion.value;
		else if(speed == Walking)
			result &= _walkJumpExhaustion.value;
		else if(speed == Sneaking)
			result &= _sneakJumpExhaustion.value;
		else if(speed == Standing)
			result &= _standJumpExhaustion.value;

		if(type == ChargeUp)
			result |= _jumpChargeExhaustion.value;

		return result;
	}

	public float getJumpExhaustionGain(int speed, int type, float jumpCharge)
	{
		if(!enabled)
			return 0F;

		float result = _baseExhautionGainFactor.value * _jumpExhaustionGainFactor.value;

		if(type == SlideDown)
			return result * _jumpSlideExhaustionGainFactor.value;
		else if(type == Angle)
			result *= _angleJumpExhaustionGainFactor.value;
		else if(type == ClimbUp || type == ClimbUpHandsOnly)
			result *= _climbJumpUpExhaustionGainFactor.value;
		else if(type == ClimbBackUp || type == ClimbBackUpHandsOnly)
			result *= _climbJumpBackUpExhaustionGainFactor.value;
		else if(type == ClimbBackHead || type == ClimbBackHeadHandsOnly)
			result *= _climbJumpBackHeadExhaustionGainFactor.value;
		else if(type == WallUp)
			result *= _wallUpJumpExhaustionGainFactor.value;
		else if(type == WallHead)
			result *= _wallHeadJumpExhaustionGainFactor.value;
		else
			result *= _upJumpExhaustionGainFactor.value;

		if(type == ClimbUp || type == ClimbUpHandsOnly || type == ClimbBackUp || type == ClimbBackUpHandsOnly || type == ClimbBackHead || type == ClimbBackHeadHandsOnly)
			return result * _climbJumpExhaustionGainFactor.value;

		if(type == WallUp || type == WallHead)
			return result * _wallJumpExhaustionGainFactor.value;

		if(speed == Sprinting)
			result *= _sprintJumpExhaustionGainFactor.value;
		else if(speed == Running)
			result *= _runJumpExhaustionGainFactor.value;
		else if(speed == Walking)
			result *= _walkJumpExhaustionGainFactor.value;
		else if(speed == Sneaking)
			result *= _sneakJumpExhaustionGainFactor.value;
		else if(speed == Standing)
			result *= _standJumpExhaustionGainFactor.value;

		if(type == ChargeUp)
		{
			if(!isJumpExhaustionEnabled(speed, Up))
				result = 0;

			result +=
				_baseExhautionGainFactor.value *
				_jumpExhaustionGainFactor.value *
				_upJumpExhaustionGainFactor.value *
				_jumpChargeExhaustionGainFactor.value *
				Math.min(jumpCharge, _jumpChargeMaximum.value) / _jumpChargeMaximum.value;
		}

		return result;
	}

	public float getJumpExhaustionStop(int speed, int type, float jumpCharge)
	{
		float result = _jumpExhaustionStopFactor.value;

		if(type == SlideDown)
			return result * _jumpSlideExhaustionStopFactor.value;
		else if(type == Angle)
			result *= _angleJumpExhaustionStopFactor.value;
		else if(type == ClimbUp || type == ClimbUpHandsOnly)
			result *= _climbJumpUpExhaustionStopFactor.value;
		else if(type == ClimbBackUp || type == ClimbBackUpHandsOnly)
			result *= _climbJumpBackUpExhaustionStopFactor.value;
		else if(type == ClimbBackHead || type == ClimbBackHeadHandsOnly)
			result *= _climbJumpBackHeadExhaustionStopFactor.value;
		else if(type == WallUp)
			result *= _wallUpJumpExhaustionStopFactor.value;
		else if(type == WallHead)
			result *= _wallHeadJumpExhaustionStopFactor.value;
		else
			result *= _upJumpExhaustionStopFactor.value;

		if(type == ClimbUp || type == ClimbUpHandsOnly || type == ClimbBackUp || type == ClimbBackUpHandsOnly || type == ClimbBackHead || type == ClimbBackHeadHandsOnly)
			return result * _climbJumpExhaustionStopFactor.value;

		if(type == WallUp || type == WallHead)
			return result * _wallJumpExhaustionStopFactor.value;

		if(speed == Sprinting)
			result *= _sprintJumpExhaustionStopFactor.value;
		else if(speed == Running)
			result *= _runJumpExhaustionStopFactor.value;
		else if(speed == Walking)
			result *= _walkJumpExhaustionStopFactor.value;
		else if(speed == Sneaking)
			result *= _sneakJumpExhaustionStopFactor.value;
		else if(speed == Standing)
			result *= _standJumpExhaustionStopFactor.value;

		if(type == ChargeUp)
		{
			if(!isJumpExhaustionEnabled(speed, Up))
				result += getJumpExhaustionGain(speed, Up, 0);

			result -=
				_jumpExhaustionStopFactor.value *
				_upJumpExhaustionStopFactor.value *
				_jumpChargeExhaustionStopFactor.value *
				Math.min(jumpCharge, _jumpChargeMaximum.value) / _jumpChargeMaximum.value;
		}

		return result;
	}

	public float getJumpChargeFactor(float jumpCharge)
	{
		if(!enabled || !_jumpCharge.value)
			return 1F;

		jumpCharge = Math.min(jumpCharge, _jumpChargeMaximum.value);
		return 1F + jumpCharge / _jumpChargeMaximum.value * (_jumpChargeFactor.value - 1F);
	}

	public float getHeadJumpFactor(float headJumpCharge)
	{
		if(!enabled || !_headJump.value)
			return 1F;
		headJumpCharge = Math.min(headJumpCharge, _headJumpChargeMaximum.value);
		return (headJumpCharge - 1) / (_headJumpChargeMaximum.value - 1);
	}

	public float getJumpVerticalFactor(int speed, int type)
	{
		if(!enabled)
			return 1F;

		float result = _jumpVerticalFactor.value;

		if(type == Angle)
			return result * _angleJumpVerticalFactor .value;

		if(type == ClimbUp || type == ClimbUpHandsOnly)
			result *= _climbUpJumpVerticalFactor.value;
		if(type == ClimbUpHandsOnly)
			result *= _climbUpJumpHandsOnlyVerticalFactor.value;

		if(type == ClimbBackUp || type == ClimbBackUpHandsOnly)
			result *= _climbBackUpJumpVerticalFactor.value;
		if(type == ClimbBackUpHandsOnly)
			result *= _climbBackUpJumpHandsOnlyVerticalFactor.value;

		if(type == ClimbBackHead || type == ClimbBackHeadHandsOnly)
			result *= _climbBackHeadJumpVerticalFactor.value;
		if(type == ClimbBackHeadHandsOnly)
			result *= _climbBackHeadJumpHandsOnlyVerticalFactor.value;

		if(type == WallUp || type == WallHead)
			result *= _wallUpJumpVerticalFactor.value;
		if(type == WallHead)
			result *= _wallHeadJumpVerticalFactor.value;

		if(type == Angle || type == ClimbUp || type == ClimbUpHandsOnly || type == ClimbBackUp || type == ClimbBackUpHandsOnly || type == ClimbBackHead || type == ClimbBackHeadHandsOnly || type == WallUp || type == WallHead)
			return result;

		if(speed == Sprinting)
			result *= _sprintJumpVerticalFactor.value;
		else if(speed == Running)
			result *= _runJumpVerticalFactor.value;
		else if(speed == Walking)
			result *= _walkJumpVerticalFactor.value;
		else if(speed == Sneaking)
			result *= _sneakJumpVerticalFactor.value;
		else if(speed == Standing)
			result *= _standJumpVerticalFactor.value;

		return result;
	}

	public float getJumpHorizontalFactor(int speed, int type)
	{
		if(!enabled)
			return speed == Running ? 2F : 1F;

		float result = _jumpHorizontalFactor.value;

		if(type == Angle)
			result *= _angleJumpHorizontalFactor.value;

		if(type == ClimbBackUp || type == ClimbBackUpHandsOnly)
			result *= _climbBackUpJumpHorizontalFactor.value;
		if(type == ClimbBackUpHandsOnly)
			result *= _climbBackUpJumpHandsOnlyHorizontalFactor.value;

		if(type == ClimbBackHead || type == ClimbBackHeadHandsOnly)
			result *= _climbBackHeadJumpHorizontalFactor.value;
		if(type == ClimbBackHeadHandsOnly)
			result *= _climbBackHeadJumpHandsOnlyHorizontalFactor.value;

		if(type == WallUp)
			result *= _wallUpJumpHorizontalFactor.value;
		if(type == WallHead)
			result *= _wallHeadJumpHorizontalFactor.value;

		if(type == Angle || type == ClimbUp || type == ClimbUpHandsOnly || type == ClimbBackUp || type == ClimbBackUpHandsOnly || type == ClimbBackHead || type == ClimbBackHeadHandsOnly || type == WallUp || type == WallHead)
			return result;

		if(speed == Sprinting)
			result *= _sprintJumpHorizontalFactor.value;
		else if(speed == Running)
			result *= _runJumpHorizontalFactor.value;
		else if(speed == Walking)
			result *= _walkJumpHorizontalFactor.value;
		else if(speed == Sneaking)
			result *= _sneakJumpHorizontalFactor.value;
		else if(speed == Standing && type != ClimbBackUp && type != ClimbBackUpHandsOnly && type != ClimbBackHead && type != ClimbBackHeadHandsOnly)
			result *= 0F;

		return result;
	}

	@SuppressWarnings("unused")
	public float getMaxHorizontalMotion(int speed, int type, boolean inWater)
	{
		float maxMotion = 0.117852041920949F;
		if(!enabled)
			return speed == Running ? maxMotion * 1.3F : maxMotion;

		if(inWater)
			maxMotion = 0.07839602977037292F;

		if(speed == Sprinting)
			maxMotion *= _sprintFactor.value;
		else if(speed == Running)
			maxMotion *= _runFactor.value;
		else if(speed == Sneaking)
			maxMotion *= _sneakFactor.value;

		return maxMotion;
	}

	public float getMaxExhaustion()
	{
		float result = 0F;
		if(_run.value && _runExhaustion.value)
			result = max(result, _runExhaustionStop.value);
		if(_sprint.value && _sprintExhaustion.value)
			result = max(result, _sprintExhaustionStop.value);

		if(_jump.value)
			for(int i = Sprinting; i <= Standing; i++)
				for(int n = Up; n <= WallHeadSlide; n++)
					if(isJumpExhaustionEnabled(i, n))
						for(int t = 0; t <= 1; t++)
							result = max(result, getJumpExhaustionStop(i, n, t) + getJumpExhaustionGain(i, n, t));

		if(_freeClimb.value && _climbExhaustion.value)
			result = max(result, _climbExhaustionStop.value);
		if(_ceilingClimbing.value && _ceilingClimbExhaustion.value)
			result = max(result, _ceilingClimbExhaustionStop.value);
		return result;
	}

	private static float max(float value, float valueOrInfinite)
	{
		return valueOrInfinite == java.lang.Float.POSITIVE_INFINITY ? value : Math.max(value, valueOrInfinite);
	}

	public float getFactor(boolean hunger, boolean onGround, boolean isStanding, boolean isStill, boolean isSneaking, boolean isRunning, boolean isSprinting, boolean isClimbing, boolean isClimbCrawling, boolean isCeilingClimbing, boolean isDipping, boolean isSwimming, boolean isDiving, boolean isCrawling, boolean isCrawlClimbing)
	{
		isClimbing |= isClimbCrawling;
		isCrawling |= isCrawlClimbing;
		boolean actionOverGound = isClimbing || isCeilingClimbing || isDiving || isSwimming;
		boolean airBorne = !onGround && !actionOverGound;
		isStanding = actionOverGound ? isStill : isStanding;
		isSneaking = isSneaking & !isStanding;

		float factor = hunger ? _baseHungerGainFactor.value : _baseExhautionLossFactor.value;
		if(airBorne)
			factor *= hunger ? 0F : _fallExhautionLossFactor.value;
		else if(isSprinting)
			factor *= hunger ? _sprintingHungerGainFactor.value : _sprintingExhautionLossFactor.value;
		else if(isRunning)
			factor *= hunger ? _runningHungerGainFactor.value : _runningExhautionLossFactor.value;
		else if(isSneaking)
			factor *= hunger ? _sneakingHungerGainFactor.value : _sneakingExhautionLossFactor.value;
		else if(isStanding)
			factor *= hunger ? _standingHungerGainFactor.value : _standingExhautionLossFactor.value;
		else
			factor *= hunger ? _walkingHungerGainFactor.value : _walkingExhautionLossFactor.value;

		if(isClimbing)
			factor *= hunger ? _climbingHungerGainFactor.value : _climbingExhaustionLossFactor.value;
		else if(isCrawling)
			factor *= hunger ? _crawlingHungerGainFactor.value : _crawlingExhaustionLossFactor.value;
		else if(isCeilingClimbing)
			factor *= hunger ? _ceilClimbingHungerGainFactor.value : _ceilClimbingExhaustionLossFactor.value;
		else if(isSwimming)
			factor *= hunger ? _swimmingHungerGainFactor.value : _swimmingExhaustionLossFactor.value;
		else if(isDiving)
			factor *= hunger ? _divingHungerGainFactor.value : _divingExhaustionLossFactor.value;
		else if(isDipping)
			factor *= hunger ? _dippingHungerGainFactor.value : _dippingExhaustionLossFactor.value;
		else if(onGround)
			factor *= hunger ? _normalHungerGainFactor.value : _normalExhaustionLossFactor.value;
		else
			factor *= hunger ? _normalHungerGainFactor.value : _normalExhaustionLossFactor.value;

		return factor;
	}
}