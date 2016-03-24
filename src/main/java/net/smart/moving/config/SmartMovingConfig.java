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

import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

import net.smart.properties.*;
import net.smart.properties.Properties;

public class SmartMovingConfig extends SmartMovingProperties
{
	private static final String _smartLadderClimbingSpeedPropertiesFileName = "smart_ladder_climbing_speed_options.txt";
	private static final String _smartClimbingPropertiesFileName = "smart_climbing_options.txt";
	private static final String _smartMovingPropertiesFileName = "smart_moving_options.txt";
	private static final String _smartMovingClientServerPropertiesFileName = "smart_moving_server_options.txt";

	private static final String _slcs = "0.1";
	private static final String _sc = "0.2";
	private static final String _sm_1_0 = "1.0";
	private static final String _sm_1_1 = "1.1";
	private static final String _sm_1_2 = "1.2";
	private static final String _sm_1_3 = "1.3";
	private static final String _sm_1_4 = "1.4";
	private static final String _sm_1_5 = "1.5";
	private static final String _sm_1_6 = "1.6";
	private static final String _sm_1_7 = "1.7";
	private static final String _sm_1_8 = "1.8";
	private static final String _sm_1_9 = "1.9";
	private static final String _sm_1_10 = "1.10";
	private static final String _sm_1_11 = "1.11";
	public static final String _sm_2_0 = "2.0";
	public static final String _sm_2_1 = "2.1";
	public static final String _sm_2_2 = "2.2";
	public static final String _sm_2_3 = "2.3";
	public static final String _sm_2_4 = "2.4";
	public static final String _sm_2_5 = "2.5";
	public static final String _sm_2_6 = "2.6";
	public static final String _sm_3_0 = "3.0";
	public static final String _sm_3_1 = "3.1";
	public static final String _sm_3_2 = "3.2";
	public static final String _sm_current = _sm_3_2;

	private static final String[] _all_sm = new String[] { _sm_3_2, _sm_3_1, _sm_3_0, _sm_2_6, _sm_2_5, _sm_2_4, _sm_2_3, _sm_2_2, _sm_2_1, _sm_2_0, _sm_1_11, _sm_1_10, _sm_1_9, _sm_1_8, _sm_1_7, _sm_1_6, _sm_1_5, _sm_1_4, _sm_1_3, _sm_1_2, _sm_1_1, _sm_1_0 };
	private static final String[] _all_old = new String[] { _sc, _slcs };

	protected static final String[] _pre_sm_1_3 = new String[] { _sm_1_2, _sm_1_1, _sm_1_0 };
	protected static final String[] _pre_sm_1_4 = concat(_sm_1_3, _pre_sm_1_3);
	protected static final String[] _pre_sm_1_5 = concat(_sm_1_4, _pre_sm_1_4);
	protected static final String[] _pre_sm_1_6 = concat(_sm_1_5, _pre_sm_1_5);
	protected static final String[] _pre_sm_1_7 = concat(_sm_1_6, _pre_sm_1_6);
	protected static final String[] _pre_sm_1_8 = concat(_sm_1_7, _pre_sm_1_7);
	protected static final String[] _pre_sm_1_9 = concat(_sm_1_8, _pre_sm_1_8);
	protected static final String[] _pre_sm_1_10 = concat(_sm_1_9, _pre_sm_1_9);
	protected static final String[] _pre_sm_1_11 = concat(_sm_1_10, _pre_sm_1_10);
	protected static final String[] _pre_sm_2_0 = concat(_sm_1_11, _pre_sm_1_11);
	protected static final String[] _pre_sm_2_1 = concat(_sm_2_0, _pre_sm_2_0);
	protected static final String[] _pre_sm_2_2 = concat(_sm_2_1, _pre_sm_2_1);
	protected static final String[] _pre_sm_2_3 = concat(_sm_2_2, _pre_sm_2_2);
	protected static final String[] _pre_sm_2_4 = concat(_sm_2_3, _pre_sm_2_3);
	protected static final String[] _pre_sm_2_5 = concat(_sm_2_4, _pre_sm_2_4);
	protected static final String[] _pre_sm_2_6 = concat(_sm_2_5, _pre_sm_2_5);
	protected static final String[] _pre_sm_3_0 = concat(_sm_2_6, _pre_sm_2_6);
	protected static final String[] _pre_sm_3_1 = concat(_sm_3_0, _pre_sm_3_0);
	protected static final String[] _pre_sm_3_2 = concat(_sm_3_1, _pre_sm_3_1);

	private static final String[] _pre_sm_1_7_post_1_4 = new String[] { _sm_1_6, _sm_1_5 };
	private static final String[] _pre_sm_1_7_post_1_0 = new String[] { _sm_1_6, _sm_1_5, _sm_1_4, _sm_1_3, _sm_1_2, _sm_1_1 };

	public static final String[] _all = concat(_all_sm, _all_old);



	public final Property<Float> _speedFactor = PositiveFactor("move.speed.factor").defaults(1F).comment("Global player speed factor (>= 0)").book("Global Speed", "Below you find the options to manipulate the global speed applied to all speeds.");
	public final Property<Boolean> _speedUser = Creative("move.speed.user").defaults(true, _pre_sm_3_2).comment("To switch on/off in-game speed manipulation");
	public final Property<Float> _speedUserFactor = PositiveFactor("move.speed.user.factor").singular().defaults(0.2F).min(0.0001F).comment("The factor for in-game speed manipulation (>= 0.0001)");
	public final Property<Integer> _speedUserExponent = Integer("move.speed.user.exponent").singular().defaults(0).comment("The exponent for in-game speed manipulation");



	public final Property<String> _baseClimb = String().defaults("free").key("move.climb.base") .key("move.climb.ladder", _pre_sm_1_10).key("climbing.ladder", _all_old).comment("To manipulate the ladder and vine climbing mode (possible values are \"free\", \"smart\", \"simple\" and \"standard\")").book("Climbing", "Below you find all free and ladder climbing options except those for ceiling climbing.");
	public final Property<Boolean> _freeClimb = Unmodified().key("move.climb.free").key("climbing.free", _all_old).comment("To switch on/off free climbing");
	public final Property<Boolean> _freeBaseLadderClimb = Modified().key("move.climb.free.base.ladder").comment("To switch on/off remaining base climbing behavior on ladders while free climbing is enabled for ladders (also see \"move.climb.base\")");
	public final Property<Boolean> _freeBaseVineClimb = Modified().key("move.climb.free.base.vine").defaults(true, _pre_sm_1_11).comment("To switch on/off remaining base climbing behavior on vines while free climbing is enabled for vines (also see \"move.climb.base\")");

	public final Property<Boolean> _isFreeBaseClimb = _baseClimb.is("free").and(_freeClimb);
	public final Property<Boolean> _isSmartBaseClimb = _baseClimb.is("smart").andNot(_isFreeBaseClimb);
	public final Property<Boolean> _isSimpleBaseClimb = _baseClimb.is("simple").andNot(_isFreeBaseClimb).andNot(_isSmartBaseClimb);
	public final Property<Boolean> _isStandardBaseClimb = _isFreeBaseClimb.not().andNot(_isSmartBaseClimb).andNot(_isSimpleBaseClimb);


	public final Property<Float> _freeClimbingUpSpeedFactor = PositiveFactor("move.climb.free.up.speed.factor").comment("Climbing up speed factor relative to default climbing up speed (>= 0)").chapter();
	public final Property<Float> _freeClimbingDownSpeedFactor = PositiveFactor("move.climb.free.down.speed.factor").comment("Climbing down speed factor relative to default climbing down speed (>= 0)");
	public final Property<Float> _freeClimbingHorizontalSpeedFactor = PositiveFactor("move.climb.free.horizontal.speed.factor").comment("Climbing horizontal speed factor relative to default climbing horizontal speed (>= 0)");
	public final Property<Float> _freeClimbingOrthogonalDirectionAngle = Positive("move.climb.free.direction.orthogonal.angle").values(90F, 90F, 180F).comment("Climbing N,S,E,W grabbing angle in degrees");
	public final Property<Float> _freeClimbingDiagonalDirectionAngle = Positive("move.climb.free.direction.diagonal.angle").values(80F, 45F, 180F).comment("Climbing NW,SW,SE,NE grabbing angle in degrees");

	public final Property<Boolean> _freeClimbingAutoLaddder = Unmodified("move.climb.free.ladder.auto").depends(_isFreeBaseClimb).comment("Whether the \"grab\" button will automatically be triggered while being on ladders and looking in the right direction").section();
	public final Property<Boolean> _freeClimbingAutoVine = Unmodified("move.climb.free.vine.auto").depends(_isFreeBaseClimb).comment("Whether the \"grab\" button will automatically be triggered while being on standard climbable vines and looking in the right direction");
	public final Property<Float> _freeOneLadderClimbUpSpeedFactor = PositiveFactor("move.climb.free.ladder.one.up.speed.factor").defaults(1.0153F).defaults(0.71F, _pre_sm_2_4).comment("Additional speed factor when climbing straight up on one ladder block (>= 0)");
	public final Property<Float> _freeBothLadderClimbUpSpeedFactor = IncreasingFactor("move.climb.free.ladder.two.up.speed.factor").defaults(1.43F).comment("Additional speed factor when climbing straight up on two ladder blocks (>= 1)");
	public final Property<Boolean> _freeFenceClimbing = Unmodified("move.climb.free.fence").comment("Climbing over fences");

	public final Property<Float> _freeClimbFallDamageStartDistance = Positive("move.climb.fall.damage.start.distance").values(2F, 1F, 3F).comment("Distance in blocks to fall before suffering fall damage when starting to climb (>= 1, <= 3)").section();
	public final Property<Float> _freeClimbFallDamageFactor = IncreasingFactor("move.climb.fall.damage.factor").defaults(2F).comment("Damage factor applied to the remaining distance (>= 1)");
	public final Property<Float> _freeClimbFallMaximumDistance = Positive("move.climb.fall.maximum.distance").defaults(3F).min(_freeClimbFallDamageStartDistance).comment("Distance in blocks to fall to block all climbing attempts (>= \"move.climb.fall.damage.start.distance\")");

	public final Property<Boolean> _climbExhaustion = Hard("move.climb.exhaustion").comment("To switch on/off exhaustion while climbing").section();
	public final Property<Float> _climbExhaustionStart = Positive("move.climb.exhaustion.start").defaults(60F).defaults(0F, _pre_sm_1_4).comment("Maximum exhaustion to start climbing along ceilings (>= 0)");
	public final Property<Float> _climbExhaustionStop = Positive("move.climb.exhaustion.stop").up(80F, _climbExhaustionStart).defaults(100F, _pre_sm_1_4).comment("Maximum exhaustion to climb (>= 0)");
	public final Property<Float> _climbStrafeExhaustionGain = Positive("move.climb.strafe.exhaustion.gain").defaults(1.1F).defaults(0.75F, _pre_sm_1_4).comment("Exhaustion added every tick while climbing horizontally (>= 0)");
	public final Property<Float> _climbUpExhaustionGain = Positive("move.climb.up.exhaustion.gain").defaults(1.2F).defaults(1F, _pre_sm_1_4).comment("Exhaustion added every tick while climbing up (>= 0)");
	public final Property<Float> _climbDownExhaustionGain = Positive("move.climb.down.exhaustion.gain").defaults(1.05F).defaults(0.5F, _pre_sm_1_4).comment("Exhaustion added every tick while climbing down (>= 0)");
	public final Property<Float> _climbStrafeUpExhaustionGain = Positive("move.climb.strafe.up.exhaustion.gain").defaults(1.3F).comment("Exhaustion added every tick while climbing diagonally up (>= 0)");
	public final Property<Float> _climbStrafeDownExhaustionGain = Positive("move.climb.strafe.down.exhaustion.gain").defaults(1.25F).comment("Exhaustion added every tick while climbing diagonally down (>= 0)");



	public final Property<Boolean> _ceilingClimbing = Unmodified("move.climb.ceiling").comment("To switch on/off climbing along ceilings").book("Ceiling climbing", "Below you find all ceiling climbing options");
	public final Property<Float> _ceilingClimbingSpeedFactor = PositiveFactor("move.climb.ceiling.speed.factor").defaults(0.2F).comment("Speed factor while climbing along ceilings (>= 0, relative to default movement speed)");
	public final Property<String[]> _ceilingClimbConfigurationString = Strings("move.climb.ceiling.configuration").defaults(new String[] { "tile.fenceIron", "tile.trapdoor/0/1/2/3", "tile.trapdoor_iron/0/1/2/3" }).defaults(new String[] { "tile.fenceIron", "tile.trapdoor/0/1/2/3" }, _pre_sm_2_5).comment("To define which blocks are ceiling climbable (syntax: '<blockId/blockName>(/<metadata>)*' seperator: ',')");
	public final Property<Dictionary<Object, Set<Integer>>> _ceilingClimbConfigurationObject = _ceilingClimbConfigurationString.toBlockConfig();

	public final Property<Boolean> _ceilingClimbExhaustion = Hard("move.climb.ceiling.exhaustion").comment("To switch on/off exhaustion while climbing along ceilings").section();
	public final Property<Float> _ceilingClimbExhaustionStart = Positive("move.climb.ceiling.exhaustion.start").defaults(40F).defaults(0F, _pre_sm_1_4).comment("Maximum exhaustion to start climbing along ceilings (>= 0)");
	public final Property<Float> _ceilingClimbExhaustionStop = Positive("move.climb.ceiling.exhaustion.stop").up(60F, _ceilingClimbExhaustionStart).defaults(100F, _pre_sm_1_4).comment("Maximum exhaustion to climbing along ceilings (>= \"move.climb.ceiling.exhaustion.start\")");
	public final Property<Float> _ceilingClimbExhaustionGain = Positive("move.climb.ceiling.exhaustion.gain").defaults(1.3F).defaults(2F, _pre_sm_1_4).comment("Exhaustion added every tick while climbing along ceilings (>= 0)");



	public final Property<Boolean> _swim = Unmodified().key("move.swim").key("climbing.swim", _all_old).comment("To switch on/off swimming").book("Swimming", "Below you find all swimming options");
	public final Property<Float> _swimSpeedFactor = PositiveFactor("move.swim.speed.factor").comment("Speed factor while swimming (>= 0, relative to default movement speed)");
	public final Property<Boolean> _swimDownOnSneak = Unmodified().key("move.swim.down.sneak").defaults(false, _pre_sm_1_6).comment("To switch on/off diving down instead of swimming slow on sneaking while swimming");
	public final Property<Float> _swimParticlePeriodFactor = PositiveFactor("move.swim.particle.period.factor").comment("Swim particle spawning period factor (>= 0)");


	public final Property<Boolean> _dive = Unmodified().key("move.dive").key("climbing.dive", _all_old).comment("To switch on/off diving").book("Diving", "Below you find all diving options");
	public final Property<Float> _diveSpeedFactor = PositiveFactor("move.dive.speed.factor").comment("Speed factor while diving (>= 0, relative to default movement speed)");
	public final Property<Boolean> _diveDownOnSneak = Unmodified().key("move.dive.down.sneak").defaults(false, _pre_sm_1_6).comment("To switch on/off diving down instead of diving slow on sneaking while diving");


	public final Property<Boolean> _lavaLikeWater = Creative("move.lava.water").comment("To switch on/off swimming and diving in lava").book("Lava", "Below you find all lava movement options");
	public final Property<Float> _lavaSwimParticlePeriodFactor = PositiveFactor("move.lava.swim.particle.period.factor").defaults(4F).comment("Lava swim particle spawning period factor (>= 0)");


	public final Property<Boolean> _run = Unmodified("move.run").comment("To switch on/off standard sprinting").book("Standard sprinting", "Below you find the options for standard vanilla Minecraft sprinting (sometimes referred as \"running\" here)");
	public final Property<Float> _runFactor = PositiveFactor("move.run.factor").defaults(1.3F).min(1.1F).comment("Standard sprinting factor (>= 1.1)");
	public final Property<Boolean> _runExhaustion = Hard("move.run.exhaustion").depends(_run).comment("To switch on/off standard sprinting exhaustion");

	public final Property<Float> _runExhaustionStart = Positive("move.exhaustion.run.start").defaults(75F).comment("Maximum exhaustion to start a standard sprint (>= 0)").section();
	public final Property<Float> _runExhaustionStop = Positive("move.exhaustion.run.stop").up(100F, _runExhaustionStart).comment("Maximum exhaustion to continue a standard sprint (>= \"move.exhaustion.run.start\")");
	public final Property<Float> _runExhaustionGainFactor = Positive("move.exhaustion.run.gain.factor").defaults(1.5F).comment("Exhaustion gain factor while standard sprinting (>= 0)");



	public final Property<Boolean> _sprint = Unmodified("move.sprint").comment("To switch on/off generic sprinting").book("Generic sprinting", "Below you find the options for Smart Moving's generic sprinting available for many different smart movings plus standard walking");
	public final Property<Float> _sprintFactor = PositiveFactor("move.sprint.factor").defaults(1.5F).min(_run.eitherOr(_runFactor.plus(0.1F), 1.1F)).comment("Generic sprinting factor (>= 1.1 AND >= 'move.run.factor' + 0.1 if relevant)");
	public final Property<Boolean> _sprintExhaustion = Medium("move.sprint.exhaustion").depends(_sprint).comment("To switch on/off sprinting exhaustion");

	public final Property<Float> _sprintExhaustionStart = Positive("move.exhaustion.sprint.start").defaults(50F).comment("Maximum exhaustion to start a sprint (>= 0)").section();
	public final Property<Float> _sprintExhaustionStop = Positive("move.exhaustion.sprint.stop").up(100F, _sprintExhaustionStart).comment("Maximum exhaustion to continue a sprint (>= \"move.exhaustion.sprint.start\")");
	public final Property<Float> _sprintExhaustionGainFactor = IncreasingFactor("move.exhaustion.sprint.gain.factor").defaults(2F).comment("Exhaustion gain factor while sprinting (>= 0)");



	public final Property<Boolean> _sneak = Unmodified("move.sneak").comment("To switch on/off standard sneaking").book("Generic sneaking", "Below you find the options for Smart Moving's generic sneaking available for many different smart movings. These options also apply to standard sneaking!");
	public final Property<Float> _sneakFactor = DecreasingFactor("move.sneak.factor").defaults(0.3F).comment("Speed factor while sneaking (>= 0, <= 1, relative to default movement speed)");
	public final Property<Boolean> _sneakNameTag = Modified("move.sneak.name").comment("Whether to display a name tag above other standard sneaking players");


	public final Property<Boolean> _crawl = Unmodified("move.crawl").comment("To switch on/off crawling").book("Crawling", "Below you find all crawling options.");
	public final Property<Float> _crawlFactor = DecreasingFactor("move.crawl.factor").defaults(0.15F).comment("Speed factor while crawling (>= 0, <= 1, relative to default movement speed)");
	public final Property<Boolean> _crawlNameTag = Modified("move.crawl.name").comment("Whether to display a name tag above other crawling players");
	public final Property<Boolean> _crawlOverEdge = Unmodified("move.crawl.edge").comment("Whether to allow crawling over edges");



	public final Property<Boolean> _slide = Unmodified("move.slide").comment("To switch on/off sliding").book("Sliding", "Below you find all sliding options.");
	public final Property<Float> _slideControlDegrees = PositiveFactor("move.slide.control.angle").defaults(1F).comment("Sliding control movement factor (>= 0, in degrees per tick)");
	public final Property<Float> _slideSlipperinessFactor = PositiveFactor("move.slide.glide.factor").comment("Slipperiness factor while sliding (>= 0)");
	public final Property<Float> _slidingSpeedStopFactor = PositiveFactor("move.slide.speed.stop.factor").comment("Sliding to crawling transition speed factor (>= 0)");
	public final Property<Float> _slideParticlePeriodFactor = PositiveFactor("move.slide.particle.period.factor").defaults(0.5F).defaults(1F, _pre_sm_1_6).comment("Sliding particle spawning period factor (>= 0)");



	public final Property<Boolean> _fly = Unmodified("move.fly").comment("To switch on/off smart flying").book("Smart flying", "Below you find all options for Smart Moving's own flying mode.");
	public final Property<Float> _flyingSpeedFactor = PositiveFactor("move.fly.speed.factor").comment("To manipulate smart flying speed (>= 0)");



	public final Property<Boolean> _levitateSmall = Unmodified("move.levitate.small").comment("To switch on/off standard flying small size").book("Standard flying", "Below you find the options for standard vanilla Minecraft flying (sometimes referred as \"levitating\" here)");
	public final Property<Boolean> _levitateAnimation = Unmodified("move.levitate.animation").key("move.fly.animation").comment("To switch on/off standard flying animation");



	public final Property<Float> _fallingDistanceMinimum = Positive("move.fall.distance.minimum").defaults(3F).comment("Minimum fall distance for stopping ground based moves like crawling or sliding (>= 0)").book("Falling", "Below you find the options for Smart Moving's falling");
	public final Property<Boolean> _fallAnimation = Unmodified("move.fall.animation").comment("To switch on/off smart falling animation");
	public final Property<Float> _fallAnimationDistanceMinimum = Positive("move.fall.animation.distance.minimum").min(_fallingDistanceMinimum).defaults(3F, _pre_sm_1_6).comment("Minimum fall distance for the smart falling animation (>= 0, >= \"move.fall.animation.distance.minimum\"");



	public final Property<Boolean> _jump = Unmodified("move.jump").comment("To switch on/off jumping").book("Jumping", "Below you find the options for all Smart Moving's different jump types. These options also apply to standard jumping!");
	public final Property<Float> _jumpControlFactor = DecreasingFactor("move.jump.control.factor").defaults(1F).comment("Jumping control movement factor (>= 0, <= 1, relative to default air movement speed)");

	public final Property<Float> _jumpHorizontalFactor = IncreasingFactor("move.jump.horizontal.factor").comment("Horizontal jumping factor relative to actual horizontal movement (>= 1)").section();
	public final Property<Float> _jumpVerticalFactor = PositiveFactor("move.jump.vertical.factor").comment("Vertical jumping factor relative to default jump height (>= 0)");


	public final Property<Boolean> _standJump = Unmodified("move.jump.stand").depends(_jump).comment("To switch on/off jumping while standing (Relevant only if \"move.jump\" is true)").chapter();
	public final Property<Float> _standJumpVerticalFactor = PositiveFactor("move.jump.stand.vertical.factor").comment("Vertical stand jumping factor relative to default jump height (>= 0)");

	public final Property<Boolean> _sneakJump = Unmodified("move.jump.sneak").key("move.sneak.jump", _pre_sm_1_7).depends(_sneak, _jump).comment("To switch on/off jumping while sneaking (Relevant only if nether \"move.sneak\" nor \"move.jump\" are false)").chapter();
	public final Property<Float> _sneakJumpHorizontalFactor = IncreasingFactor("move.jump.sneak.horizontal.factor").key("move.sneak.jump.horizontal.factor", _pre_sm_1_7).comment("Horizontal sneak jumping factor relative to actual horizontal movement (>= 1)");
	public final Property<Float> _sneakJumpVerticalFactor = PositiveFactor("move.jump.sneak.vertical.factor").key("move.sneak.jump.vertical.factor", _pre_sm_1_7).comment("Vertical sneak jumping factor relative to default jump height (>= 0)");

	public final Property<Boolean> _walkJump = Unmodified("move.jump.walk").depends(_jump).comment("To switch on/off jumping while walking (Relevant only if \"move.jump\" is true)").chapter();
	public final Property<Float> _walkJumpHorizontalFactor = IncreasingFactor("move.jump.walk.horizontal.factor").comment("Horizontal walk jumping factor relative to actual horizontal movement (>= 1)");
	public final Property<Float> _walkJumpVerticalFactor = PositiveFactor("move.jump.walk.vertical.factor").comment("Vertical walk jumping factor relative to default jump height (>= 0)");

	public final Property<Boolean> _runJump = Unmodified("move.jump.run").key("move.run.jump", _pre_sm_1_7).depends(_run, _jump).comment("To switch on/off jumping while running (Relevant only if nether \"move.run\" nor \"move.jump\" are false)").chapter();
	public final Property<Float> _runJumpHorizontalFactor = IncreasingFactor("move.jump.run.horizontal.factor").key("move.run.jump.horizontal.factor", _pre_sm_1_7).defaults(2F).comment("Horizontal run jumping factor relative to actual horizontal movement (>= 1)");
	public final Property<Float> _runJumpVerticalFactor = PositiveFactor("move.jump.run.vertical.factor").key("move.run.jump.vertical.factor", _pre_sm_1_7).comment("Vertical run jumping factor relative to default jump height (>= 0)");

	public final Property<Boolean> _sprintJump = Unmodified("move.jump.sprint").key("move.sprint.jump", _pre_sm_1_7).depends(_sprint, _jump).comment("To switch on/off jumping while sprinting (Relevant only if nether \"move.sprint\" nor \"move.jump\" are false)").chapter();
	public final Property<Float> _sprintJumpHorizontalFactor = IncreasingFactor("move.jump.sprint.horizontal.factor").key("move.sprint.jump.horizontal.factor", _pre_sm_1_7).defaults(2F).comment("Horizontal sprint jumping factor relative to actual horizontal movement (>= 1)");
	public final Property<Float> _sprintJumpVerticalFactor = PositiveFactor("move.jump.sprint.vertical.factor").key("move.sprint.jump.vertical.factor", _pre_sm_1_7).comment("Vertical sprint jumping factor relative to default jump height (>= 0)");


	public final Property<Boolean> _jumpCharge = Unmodified("move.jump.charge").depends(_jump).comment("Relevant only if \"move.jump\" is not false").chapter("Charged jumping", "Below you find all charged jump specific options except those for exhaustion.");
	public final Property<Float> _jumpChargeMaximum = Positive("move.jump.charge.maximum").defaults(20F).comment("Maximum jump charge (counts up one per tick) (>= 0)");
	public final Property<Float> _jumpChargeFactor = IncreasingFactor("move.jump.charge.factor").defaults(1.3F).comment("Jump speed factor when completely charged (>= 1)");
	public final Property<Boolean> _jumpChargeCancelOnSneakRelease = Modified("move.jump.charge.sneak.release.cancel").comment("To switch between charged jump and charge cancel on sneak button release while jump charging");


	public final Property<Boolean> _headJump = Unmodified("move.jump.head.charge").key("move.forward.jump.charge", _pre_sm_1_8) .depends(_jump).comment("Relevant only if \"move.jump\" is not false").chapter("Head jumping", "Below you find all head jump and fall specific options except those for exhaustion.");
	public final Property<Float> _headJumpControlFactor = DecreasingFactor("move.jump.head.control.factor").key("move.forward.jump.control.factor", _pre_sm_1_8).defaults(0.2F).comment("Head jump control movement factor (>= 0, <= 1, relative to default air movement speed)");
	public final Property<Float> _headJumpChargeMaximum = Positive("move.jump.head.charge.maximum").key("move.forward.jump.charge.maximum", _pre_sm_1_8).defaults(10F).comment("Maximum head jump charge (counts up one per tick) (>= 0)");

	public final Property<Float> _headFallDamageStartDistance = Positive("move.fall.head.damage.start.distance").key("move.forward.fall.damage.start.distance", _pre_sm_1_8).values(2F, 1F, 3F).comment("Distance in blocks to fall head ahead before suffering fall damage (>= 1, <= 3)").section();
	public final Property<Float> _headFallDamageFactor = IncreasingFactor("move.fall.head.damage.factor").key("move.forward.fall.damage.factor", _pre_sm_1_8).defaults(2F).comment("Damage factor applied to the remaining distance when impacting head ahead (>= 1)");


	public final Property<Boolean> _angleJumpSide = Unmodified("move.jump.angle.side").comment("To switch on/off side jumping").chapter("Side and Back jumping", "Below you find all side and back jump specific options except those for exhaustion.");
	public final Property<Boolean> _angleJumpBack = Unmodified("move.jump.angle.back").comment("To switch on/off back jumping");
	public final Property<Float> _angleJumpHorizontalFactor = PositiveFactor("move.jump.angle.horizontal.factor").defaults(0.3F).defaults(0.4F, _sm_1_3).comment("Horizontal jump speed factor for side and back jumps (>= 0)");
	public final Property<Float> _angleJumpVerticalFactor = PositiveFactor("move.jump.angle.vertical.factor").defaults(0.2F).comment("Vertical jump speed factor for side and back jumps (>= 0)");


	public final Property<Boolean> _climbUpJump = Unmodified("move.jump.climb.up").comment("To switch on/off jumping up while climbing").chapter("Climb jumping", "Below you find all climb up jump specific options except those for exhaustion.");
	public final Property<Float> _climbUpJumpVerticalFactor = DecreasingFactor("move.jump.climb.up.vertical.factor").comment("Vertical jump speed factor for jumping while climbing (>= 0, <= 1)");
	public final Property<Float> _climbUpJumpHandsOnlyVerticalFactor = DecreasingFactor("move.jump.climb.up.hands.only.vertical.factor").defaults(0.8F).comment("Additional vertical jump speed factor for jumping while climbing with hands only (>= 0, <= 1)");


	public final Property<Boolean> _climbBackUpJump = Unmodified("move.jump.climb.back.up").comment("To switch on/off jumping back while climbing").chapter("Climb back jumping", "Below you find all climb back jump specific options except those for exhaustion.");
	public final Property<Float> _climbBackUpJumpVerticalFactor = DecreasingFactor("move.jump.climb.back.up.vertical.factor").defaults(0.2F).defaults(1F, _pre_sm_3_1).comment("Vertical jump speed factor for jumping back while climbing (>= 0, <= 1)");
	public final Property<Float> _climbBackUpJumpHorizontalFactor = DecreasingFactor("move.jump.climb.back.up.horizontal.factor").defaults(0.3F).defaults(1F, _pre_sm_3_1).comment("Horizontal jump speed factor for jumping back while climbing (>= 0, <= 1)");
	public final Property<Float> _climbBackUpJumpHandsOnlyVerticalFactor = DecreasingFactor("move.jump.climb.back.up.hands.only.vertical.factor").defaults(0.8F).comment("Additional vertical jump speed factor for jumping back while climbing with hands only (>= 0, <= 1)");
	public final Property<Float> _climbBackUpJumpHandsOnlyHorizontalFactor = DecreasingFactor("move.jump.climb.back.up.hands.only.horizontal.factor").comment("Additional horizontal jump speed factor for jumping back while climbing with hands only (>= 0, <= 1)");


	public final Property<Boolean> _climbBackHeadJump = Unmodified("move.jump.climb.back.head").comment("To switch on/off head jumping back while climbing").chapter("Climb back head jumping", "Below you find all climb back head jump specific options except those for exhaustion.");
	public final Property<Float> _climbBackHeadJumpVerticalFactor = DecreasingFactor("move.jump.climb.back.head.vertical.factor").defaults(0.2F).defaults(1F, _pre_sm_3_1).comment("Additional vertical jump speed factor for head jumping back while climbing(>= 0, <= 1)");
	public final Property<Float> _climbBackHeadJumpHorizontalFactor = DecreasingFactor("move.jump.climb.back.head.horizontal.factor").defaults(0.3F).defaults(1F, _pre_sm_3_1).comment("Additional horizontal jump speed factor for head jumping back while climbing(>= 0, <= 1)");
	public final Property<Float> _climbBackHeadJumpHandsOnlyVerticalFactor = DecreasingFactor("move.jump.climb.back.head.hands.only.vertical.factor").defaults(0.8F).comment("Additional vertical jump speed factor for head jumping while climbing with hands only (>= 0, <= 1)");
	public final Property<Float> _climbBackHeadJumpHandsOnlyHorizontalFactor = DecreasingFactor("move.jump.climb.back.head.hands.only.horizontal.factor").comment("Additional horizontal jump speed factor for head jumping while climbing with hands only (>= 0, <= 1)");


	public final Property<Boolean> _wallUpJump = Unmodified("move.jump.wall").comment("To switch on/off wall jumping").chapter("Wall jumping", "Below you find all wall jump specific options except those for exhaustion.");
	public final Property<Float> _wallUpJumpVerticalFactor = DecreasingFactor("move.jump.wall.vertical.factor").defaults(0.4F).comment("Vertical jump speed factor for wall jumping (>= 0, <= 1)");
	public final Property<Float> _wallUpJumpHorizontalFactor = DecreasingFactor("move.jump.wall.horizontal.factor").defaults(0.15F).comment("Horizontal jump speed factor for wall jumping (>= 0, <= 1)");
	public final Property<Float> _wallUpJumpFallMaximumDistance = Positive("move.jump.wall.fall.maximum.distance").defaults(2F).comment("Distance in blocks to fall to block all wall jumping attempts");
	public final Property<Float> _wallUpJumpOrthogonalTolerance = Positive("move.jump.wall.orthogonal.tolerance").defaults(5F).comment("Tolerance angle in degree for wall jumping orthogonally (>= 0, <= 45)");


	public final Property<Boolean> _wallHeadJump = Unmodified("move.jump.wall.head").comment("To switch on/off wall head jumping").chapter("Wall head jumping", "Below you find all wall head jump specific options except those for exhaustion.");
	public final Property<Float> _wallHeadJumpVerticalFactor = DecreasingFactor("move.jump.wall.head.vertical.factor").defaults(0.3F).comment("Vertical jump speed factor for wall head jumping (>= 0, <= 1)");
	public final Property<Float> _wallHeadJumpHorizontalFactor = DecreasingFactor("move.jump.wall.head.horizontal.factor").defaults(0.15F).comment("Horizontal jump speed factor for wall head jumping (>= 0, <= 1)");
	public final Property<Float> _wallHeadJumpFallMaximumDistance = Positive("move.jump.wall.head.fall.maximum.distance").defaults(3F).min(_wallUpJumpFallMaximumDistance).comment("Distance in blocks to fall to block all wall head jumping attempts (>= \"move.jump.wall.fall.maximum.distance\")");


	private final Property<Boolean> _old_jumpExhaustion = Hard("move.jump.exhaustion", _pre_sm_1_7);
	private final Property<Float> _old_jumpExhaustionGain = Positive("move.jump.exhaustion.gain", _pre_sm_1_7_post_1_0).key("move.exhaustion.jump.gain", _sm_1_0).defaults(40F).defaults(10F, _pre_sm_1_4);
	private final Property<Float> _old_jumpExhaustionStop = Positive("move.jump.exhaustion.stop", _pre_sm_1_7_post_1_0).key("move.exhaustion.jump.stop", _sm_1_0).defaults(60F).defaults(100F, _pre_sm_1_4);

	private final Property<Boolean> _old_sneakJumpExhaustion = Hard("move.jump.sneak.exhaustion", _pre_sm_1_7_post_1_4).key("move.sneak.jump.exhaustion", _pre_sm_1_4);
	private final Property<Float> _old_sneakJumpExhaustionGain = Positive("move.sneak.jump.exhaustion.gain", _pre_sm_1_7).down(45F, _old_jumpExhaustionGain).defaults(10F, _pre_sm_1_4);
	private final Property<Float> _old_sneakJumpExhaustionStop = Positive("move.sneak.jump.exhaustion.stop", _pre_sm_1_7).up(55F, _old_jumpExhaustionStop).defaults(100F, _pre_sm_1_4);

	private final Property<Boolean> _old_runJumpExhaustion = Medium("move.jump.run.exhaustion", _pre_sm_1_7_post_1_4).key("move.run.jump.exhaustion", _pre_sm_1_4);
	private final Property<Float> _old_runJumpExhaustionGain = Positive("move.run.jump.exhaustion.gain", _pre_sm_1_7).defaults(60F).defaults(20F, _pre_sm_1_4);
	private final Property<Float> _old_runJumpExhaustionStop = Positive("move.run.jump.exhaustion.stop", _pre_sm_1_7).defaults(40F).defaults(100F, _pre_sm_1_4);

	private final Property<Boolean> _old_sprintJumpExhaustion = Medium("move.jump.sprint.exhaustion", _pre_sm_1_7_post_1_4).key("move.sprint.jump.exhaustion", _pre_sm_1_4);
	private final Property<Float> _old_sprintJumpExhaustionGain = Positive("move.sprint.jump.exhaustion.gain", _pre_sm_1_7_post_1_0).key("move.exhaustion.sprint.jump.gain", _sm_1_0).up(65F, _old_runJumpExhaustionGain).defaults(20F, _pre_sm_1_4);
	private final Property<Float> _old_sprintJumpExhaustionStop = Positive("move.sprint.jump.exhaustion.stop", _pre_sm_1_7_post_1_0).key("move.exhaustion.sprint.jump.stop", _sm_1_0).down(35F, _old_runJumpExhaustionStop).defaults(100F, _pre_sm_1_4);


	public final Property<Boolean> _jumpExhaustion = Unmodified("move.jump.exhaustion").comment("To switch on/off jump exhaustion").chapter("Jump exhaustion", "Below you find the exhaustion options for the different jump types. At runtime all relevant options are combined together to form the specific exhaustion value");
	public final Property<Float> _jumpExhaustionGainFactor = PositiveFactor("move.jump.exhaustion.gain.factor").comment("To manipulate the exhaustion increase by a jump (>= 0)");
	public final Property<Float> _jumpExhaustionStopFactor = PositiveFactor("move.jump.exhaustion.stop.factor").comment("To manipulate maximum exhaustion to jump (>= 0)");


	public final Property<Boolean> _upJumpExhaustion = Unmodified("move.jump.up.exhaustion").depends(_jumpExhaustion).comment("To switch on/off up jump exhaustion").chapter();
	public final Property<Float> _upJumpExhaustionGainFactor = PositiveFactor("move.jump.up.exhaustion.gain.factor").comment("To manipulate the exhaustion increase by a jump up (>= 0)");
	public final Property<Float> _upJumpExhaustionStopFactor = PositiveFactor("move.jump.up.exhaustion.stop.factor").comment("To manipulate maximum exhaustion to jump up (>= 0)");


	public final Property<Boolean> _climbJumpExhaustion = Hard("move.jump.climb.exhaustion").depends(_jumpExhaustion).comment("To switch on/off climb jump exhaustion").chapter();
	public final Property<Float> _climbJumpExhaustionGainFactor = PositiveFactor("move.climb.jump.exhaustion.gain.factor").comment("To manipulate the exhaustion increase by jumping while climbing (>= 0)");
	public final Property<Float> _climbJumpExhaustionStopFactor = PositiveFactor("move.climb.jump.exhaustion.stop.factor").comment("To manipulate maximum exhaustion to jumping while climbing (>= 0)");

	public final Property<Boolean> _climbJumpUpExhaustion = Unmodified("move.jump.climb.up.exhaustion").depends(_climbJumpExhaustion).comment("To switch on/off climb up jump exhaustion").section();
	public final Property<Float> _climbJumpUpExhaustionGainFactor = PositiveFactor("move.jump.climb.up.exhaustion.gain.factor").defaults(40F).defaults(1F, _pre_sm_3_1).comment("To manipulate the exhaustion increase by a jump up while climbing (>= 0)");
	public final Property<Float> _climbJumpUpExhaustionStopFactor = PositiveFactor("move.jump.climb.up.exhaustion.stop.factor").defaults(60F).defaults(1F, _pre_sm_3_1).comment("To manipulate maximum exhaustion to jump up while climbing (>= 0)");

	public final Property<Boolean> _climbJumpBackUpExhaustion = Unmodified("move.jump.climb.back.up.exhaustion").depends(_climbJumpExhaustion).comment("To switch on/off climb back jump exhaustion").section();
	public final Property<Float> _climbJumpBackUpExhaustionGainFactor = PositiveFactor("move.jump.climb.back.up.exhaustion.gain.factor").defaults(40F).defaults(1F, _pre_sm_3_1).comment("To manipulate the exhaustion increase by a jump back while climbing (>= 0)");
	public final Property<Float> _climbJumpBackUpExhaustionStopFactor = PositiveFactor("move.jump.climb.back.up.exhaustion.stop.factor").defaults(60F).defaults(1F, _pre_sm_3_1).comment("To manipulate maximum exhaustion to jump back while climbing (>= 0)");

	public final Property<Boolean> _climbJumpBackHeadExhaustion = Unmodified("move.jump.climb.back.head.exhaustion").depends(_climbJumpExhaustion).comment("To switch on/off back climb head jump exhaustion").section();
	public final Property<Float> _climbJumpBackHeadExhaustionGainFactor = PositiveFactor("move.jump.climb.back.head.exhaustion.gain.factor").defaults(20F).defaults(1F, _pre_sm_3_1).comment("To manipulate the exhaustion increase by a head jump back while climbing (>= 0)");
	public final Property<Float> _climbJumpBackHeadExhaustionStopFactor = PositiveFactor("move.jump.climb.back.head.exhaustion.stop.factor").defaults(80F).defaults(1F, _pre_sm_3_1).comment("To manipulate maximum exhaustion to head jump back while climbing (>= 0)");


	public final Property<Boolean> _angleJumpExhaustion = Unmodified("move.jump.angle.exhaustion").depends(_jumpExhaustion).comment("To switch on/off angle jump exhaustion").chapter();
	public final Property<Float> _angleJumpExhaustionGainFactor = PositiveFactor("move.jump.angle.exhaustion.gain.factor").comment("To manipulate the exhaustion increase by a jump to the side or back (>= 0)");
	public final Property<Float> _angleJumpExhaustionStopFactor = PositiveFactor("move.jump.angle.exhaustion.stop.factor").comment("To manipulate maximum exhaustion to jump to the side or back (>= 0)");


	public final Property<Boolean> _wallJumpExhaustion = Medium("move.jump.wall.exhaustion").depends(_jumpExhaustion).comment("To switch on/off wall jump exhaustion").chapter();
	public final Property<Float> _wallJumpExhaustionGainFactor = PositiveFactor("move.jump.wall.exhaustion.gain.factor").comment("To manipulate the exhaustion increase by a wall jump (>= 0)");
	public final Property<Float> _wallJumpExhaustionStopFactor = PositiveFactor("move.jump.wall.exhaustion.stop.factor").comment("To manipulate maximum exhaustion to wall jump (>= 0)");

	public final Property<Boolean> _wallUpJumpExhaustion = Unmodified("move.jump.wall.up.exhaustion").depends(_wallJumpExhaustion).comment("To switch on/off wall up jump exhaustion").section();
	public final Property<Float> _wallUpJumpExhaustionGainFactor = PositiveFactor("move.jump.wall.up.exhaustion.gain.factor").defaults(40F).comment("To manipulate the exhaustion increase by a wall up jump (>= 0)");
	public final Property<Float> _wallUpJumpExhaustionStopFactor = PositiveFactor("move.jump.wall.up.exhaustion.stop.factor").defaults(60F).comment("To manipulate maximum exhaustion to wall up jump (>= 0)");

	public final Property<Boolean> _wallHeadJumpExhaustion = Unmodified("move.jump.wall.head.exhaustion").depends(_wallJumpExhaustion).comment("To switch on/off wall head jump exhaustion").section();
	public final Property<Float> _wallHeadJumpExhaustionGainFactor = PositiveFactor("move.jump.wall.head.exhaustion.gain.factor").defaults(20F).comment("To manipulate the exhaustion increase by a wall head jump (>= 0)");
	public final Property<Float> _wallHeadJumpExhaustionStopFactor = PositiveFactor("move.jump.wall.head.exhaustion.stop.factor").defaults(80F).comment("To manipulate maximum exhaustion to wall head jump (>= 0)");


	public final Property<Boolean> _standJumpExhaustion = Hard("move.jump.stand.exhaustion").depends(_jumpExhaustion).source(_old_jumpExhaustion, _pre_sm_1_7).comment("To switch on/off stand jump exhaustion").chapter();
	public final Property<Float> _standJumpExhaustionGainFactor = PositiveFactor("move.jump.stand.exhaustion.gain.factor").defaults(40F).source(_old_jumpExhaustionGain, _pre_sm_1_7).comment("To manipulate the exhaustion increase by a jump while standing (>= 0)");
	public final Property<Float> _standJumpExhaustionStopFactor = PositiveFactor("move.jump.stand.exhaustion.stop.factor").defaults(60F).source(_old_jumpExhaustionStop, _pre_sm_1_7).comment("To manipulate maximum exhaustion to jump while standing (>= 0)");

	public final Property<Boolean> _sneakJumpExhaustion = Hard("move.jump.sneak.exhaustion").depends(_jumpExhaustion).source(_old_sneakJumpExhaustion, _pre_sm_1_7).comment("To switch on/off sneak jump exhaustion").section();
	public final Property<Float> _sneakJumpExhaustionGainFactor = PositiveFactor("move.jump.sneak.exhaustion.gain.factor").up(40F, _standJumpExhaustionGainFactor).source(_old_sneakJumpExhaustionGain, _pre_sm_1_7).comment("To manipulate the exhaustion increase by a jump while sneaking (>= \"move.jump.stand.exhaustion.gain.factor\")");
	public final Property<Float> _sneakJumpExhaustionStopFactor = PositiveFactor("move.jump.sneak.exhaustion.stop.factor").down(60F, _standJumpExhaustionStopFactor).source(_old_sneakJumpExhaustionStop, _pre_sm_1_7).comment("To manipulate maximum exhaustion to jump while sneaking (>= 0, <= \"move.jump.stand.exhaustion.stop.factor\")");

	public final Property<Boolean> _walkJumpExhaustion = Hard("move.jump.walkexhaustion").depends(_jumpExhaustion).source(_old_jumpExhaustion, _pre_sm_1_7).comment("To switch on/off walk jump exhaustion").section();
	public final Property<Float> _walkJumpExhaustionGainFactor = PositiveFactor("move.jump.walk.exhaustion.gain.factor").up(45F, _sneakJumpExhaustionGainFactor).source(_old_jumpExhaustionGain, _pre_sm_1_7).comment("To manipulate the exhaustion increase by a jump while walking (>= \"move.jump.sneak.exhaustion.gain.factor\")");
	public final Property<Float> _walkJumpExhaustionStopFactor = PositiveFactor("move.jump.walk.exhaustion.stop.factor").down(55F, _sneakJumpExhaustionStopFactor).source(_old_jumpExhaustionStop, _pre_sm_1_7).comment("To manipulate maximum exhaustion to jump while walking (>= 0, <= \"move.jump.sneak.exhaustion.stop.factor\")");

	public final Property<Boolean> _runJumpExhaustion = Medium("move.jump.run.exhaustion").depends(_jumpExhaustion).source(_old_runJumpExhaustion, _pre_sm_1_7).comment("To switch on/off run jump exhaustion").section();
	public final Property<Float> _runJumpExhaustionGainFactor = PositiveFactor("move.jump.run.exhaustion.gain.factor").up(60F, _walkJumpExhaustionGainFactor).source(_old_runJumpExhaustionGain, _pre_sm_1_7).comment("To manipulate the exhaustion increase by a jump while running (>= \"move.jump.walk.exhaustion.gain.factor\")");
	public final Property<Float> _runJumpExhaustionStopFactor = PositiveFactor("move.jump.run.exhaustion.stop.factor").down(40F, _walkJumpExhaustionStopFactor).source(_old_runJumpExhaustionStop, _pre_sm_1_7).comment("To manipulate maximum exhaustion to jump while running (>= 0, <= \"move.jump.walk.exhaustion.stop.factor\")");

	public final Property<Boolean> _sprintJumpExhaustion = Medium("move.jump.sprint.exhaustion").depends(_jumpExhaustion).source(_old_sprintJumpExhaustion, _pre_sm_1_7).comment("To switch on/off sprint jump exhaustion").section();
	public final Property<Float> _sprintJumpExhaustionGainFactor = PositiveFactor("move.jump.sprint.exhaustion.gain.factor").up(65F, _runJumpExhaustionGainFactor).source(_old_sprintJumpExhaustionGain, _pre_sm_1_7).comment("To manipulate the exhaustion increase by a jump while sprinting (>= \"move.jump.run.exhaustion.gain.factor\")");
	public final Property<Float> _sprintJumpExhaustionStopFactor = PositiveFactor("move.jump.sprint.exhaustion.stop.factor").down(35F, _runJumpExhaustionStopFactor).source(_old_sprintJumpExhaustionStop, _pre_sm_1_7).comment("To manipulate maximum exhaustion to jump while sprinting (>= 0, <= \"move.jump.run.exhaustion.stop.factor\")");

	public final Property<Boolean> _jumpChargeExhaustion = Medium("move.jump.charge.exhaustion").depends(_jumpExhaustion).defaults(true, _pre_sm_1_11) .comment("To switch on/off up additional jump charge exhaustion").section();
	public final Property<Float> _jumpChargeExhaustionGainFactor = PositiveFactor("move.jump.charge.exhaustion.gain.factor").defaults(Value(30F)).comment("To manipulate the additional exhaustion for the higher jump (>= 0, is multiplied with the actual charge factor)");
	public final Property<Float> _jumpChargeExhaustionStopFactor = PositiveFactor("move.jump.charge.exhaustion.stop.factor").defaults(Value(30F)).comment("To manipulate the subtractional maximum exhaustion to jump higher (>= 0, is multiplied with the actual charge factor)");

	public final Property<Boolean> _jumpSlideExhaustion = Medium("move.jump.slide.exhaustion").depends(_jumpExhaustion).defaults(true, _pre_sm_1_11).comment("To switch on/off slide jump exhaustion").section();
	public final Property<Float> _jumpSlideExhaustionGainFactor = PositiveFactor("move.jump.slide.exhaustion.gain.factor").defaults(Value(10F)).comment("To manipulate the exhaustion increase by a slide jump (>= 0)");
	public final Property<Float> _jumpSlideExhaustionStopFactor = PositiveFactor("move.jump.slide.exhaustion.stop.factor").defaults(Value(90F)).comment("To manipulate maximum exhaustion to slide jump (>= 0)");



	public final Property<Float> _baseExhautionGainFactor = PositiveFactor("move.exhaustion.gain.factor").comment("Exhaustion gain base factor, set to '0' to disable exhaustion (>= 0)").book("Exhaustion", "Below you find the options for the continuous exhaustion gain/loss factors.");
	public final Property<Float> _baseExhautionLossFactor = PositiveFactor("move.exhaustion.loss.factor").defaults(Value(1F).e(1.2F).h(0.8F)).defaults(1F, _pre_sm_1_5).comment("Exhaustion loss base factor (>= 0)");

	public final Property<Float> _sprintingExhautionLossFactor = PositiveFactor("move.exhaustion.sprint.loss.factor").defaults(0F).comment("Smart sprinting exhaustion loss factor (>= 0)").section();
	public final Property<Float> _runningExhautionLossFactor = PositiveFactor("move.exhaustion.run.loss.factor").up(0.5F, _sprintingExhautionLossFactor).comment("Standard sprinting exhaustion loss factor while (>= 0, >= \"move.exhaustion.sprint.loss.factor\")");
	public final Property<Float> _walkingExhautionLossFactor = PositiveFactor("move.exhaustion.walk.loss.factor").up(1F, _runningExhautionLossFactor).comment("Walking exhaustion loss factor (>= 0, >= \"move.exhaustion.run.loss.factor\")");
	public final Property<Float> _sneakingExhautionLossFactor = PositiveFactor("move.exhaustion.sneak.loss.factor").up(1.5F, _walkingExhautionLossFactor).defaults(1F, _sm_1_1).comment("Sneaking exhaustion loss factor (>= 0, >= \"move.exhaustion.walk.loss.factor\")");
	public final Property<Float> _standingExhautionLossFactor = PositiveFactor("move.exhaustion.stand.loss.factor").up(2F, _sneakingExhautionLossFactor.maximum(1F)).comment("Standing exhaustion loss factor (>= 1, >= \"move.exhaustion.sneak.loss.factor\")");
	public final Property<Float> _fallExhautionLossFactor = PositiveFactor("move.exhaustion.fall.loss.factor").up(2.5F, _standingExhautionLossFactor).comment("Falling exhaustion loss factor (>= \"move.exhaustion.stand.loss.factor\")");

	public final Property<Float> _ceilClimbingExhaustionLossFactor = PositiveFactor("move.exhaustion.climb.ceiling.loss.factor").comment("Ceiling climbing exhaustion loss factor (>= 0)").section();
	public final Property<Float> _climbingExhaustionLossFactor = PositiveFactor("move.exhaustion.climb.loss.factor").comment("Climbing exhaustion loss factor (>= 0)");
	public final Property<Float> _crawlingExhaustionLossFactor = PositiveFactor("move.exhaustion.crawl.loss.factor").comment("Crawling exhaustion loss factor (>= 0)");
	public final Property<Float> _dippingExhaustionLossFactor = PositiveFactor("move.exhaustion.dip.loss.factor").comment("Water walking exhaustion loss factor (>= 0)");
	public final Property<Float> _swimmingExhaustionLossFactor = PositiveFactor("move.exhaustion.swim.loss.factor").comment("Swimming exhaustion loss factor (>= 0)");
	public final Property<Float> _divingExhaustionLossFactor = PositiveFactor("move.exhaustion.dive.loss.factor").comment("Diving exhaustion loss factor (>= 0)");
	public final Property<Float> _normalExhaustionLossFactor = PositiveFactor("move.exhaustion.normal.loss.factor").comment("Normal movement exhaustion loss factor (>= 0)");

	public final Property<Boolean> _exhaustionLossHunger = Unmodified("move.exhaustion.hunger").comment("Whether exhaustion loss increases hunger").section();
	public final Property<Float> _exhaustionLossHungerFactor = PositiveFactor("move.exhaustion.hunger.factor").defaults(Value(0.05F).e(0.02F).h(0.08F)).defaults(0.05F, _pre_sm_1_5).comment("How much hunger is generated for exhaustion loss (>= 0)");
	public final Property<Float> _exhaustionLossFoodLevelMinimum = Positive("move.exhaustion.food.minimum").defaults(4F).comment("Until which food level exhaustion is continuously reduced");



	public final Property<Boolean> _hungerGain = Medium("move.hunger.gain").comment("To switch on/off hunger generation").book("Hunger", "Below you find all hunger gain options.");
	public final Property<Float> _baseHungerGainFactor = PositiveFactor("move.hunger.gain.factor").defaults(Value(1F).e(0.8F).h(1.2F)).defaults(1F, _pre_sm_1_5).comment("Hunger generation base factor (>= 0)");

	public final Property<Float> _sprintingHungerGainFactor = PositiveFactor("move.hunger.sprint.gain.factor").key("move.hunger.gain.sprint.factor", _pre_sm_1_3).comment("Smart sprinting hunger generation factor (>= 0)").section();
	public final Property<Float> _runningHungerGainFactor = PositiveFactor("move.hunger.run.gain.factor").key("move.hunger.gain.run.factor", _pre_sm_1_3).defaults(10F).comment("Standard sprinting hunger generation factor (>= 0)");
	public final Property<Float> _walkingHungerGainFactor = PositiveFactor("move.hunger.walk.gain.factor").key("move.hunger.gain.walk.factor", _pre_sm_1_3).comment("Standard speed movement hunger generation factor (>= 0)");
	public final Property<Float> _sneakingHungerGainFactor = PositiveFactor("move.hunger.sneak.gain.factor").key("move.hunger.gain.sneak.factor", _pre_sm_1_3).comment("Sneaking hunger generation factor (>= 0)");
	public final Property<Float> _standingHungerGainFactor = PositiveFactor("move.hunger.stand.gain.factor").defaults(0F).comment("Sneaking hunger generation factor (>= 0)");

	public final Property<Float> _climbingHungerGainFactor = PositiveFactor("move.hunger.climb.gain.factor").key("move.hunger.gain.climb.factor", _pre_sm_1_3).comment("Climbing hunger generation factor (>= 0)").section();
	public final Property<Float> _crawlingHungerGainFactor = PositiveFactor("move.hunger.crawl.gain.factor").key("move.hunger.gain.crawl.factor", _pre_sm_1_3).comment("Crawling hunger generation factor (>= 0)");
	public final Property<Float> _ceilClimbingHungerGainFactor = PositiveFactor("move.hunger.climb.gain.ceiling.factor").key("move.hunger.gain.climb.ceiling.factor", _pre_sm_1_3).comment("Ceiling climbing hunger generation factor (>= 0)");
	public final Property<Float> _swimmingHungerGainFactor = PositiveFactor("move.hunger.swim.gain.factor").key("move.hunger.gain.swim.factor", _pre_sm_1_3).defaults(1.5F).comment("Swimming hunger generation factor (>= 0)");
	public final Property<Float> _divingHungerGainFactor = PositiveFactor("move.hunger.dive.gain.factor").key("move.hunger.gain.dive.factor", _pre_sm_1_3).defaults(1.5F).comment("Diving hunger generation factor (>= 0)");
	public final Property<Float> _dippingHungerGainFactor = PositiveFactor("move.hunger.dip.gain.factor").key("move.hunger.gain.dip.factor", _pre_sm_1_3).defaults(1.5F).comment("Water walking hunger generation factor (>= 0)");
	public final Property<Float> _normalHungerGainFactor = PositiveFactor("move.hunger.normal.gain.factor").key("move.hunger.gain.ground.factor", _pre_sm_1_3).comment("Normal movement hunger generation factor (>= 0)");

	public final Property<Float> _alwaysHungerGain = Positive("move.hunger.always.gain").defaults(Value(0F).h(0.005F)).defaults(0F, _pre_sm_1_5).comment("Basic hunger per tick (>= 0)").section();



	public final Property<Float> _usageSpeedFactor = DecreasingFactor("move.usage.speed.factor").defaults(0.2F).comment("Speed factor while using an item, if not defined otherwise (>= 0 AND <= 1)").book("Item Usage", "Below you find the options to manipulate the speed factors additionally applied while using an item.");
	public final Property<Float> _usageSwordSpeedFactor = DecreasingFactor("move.usage.sword.speed.factor").defaults(_usageSpeedFactor).comment("Speed factor while blocking with a sword (>= 0 AND <= 1, defaults to \"move.usage.speed.factor\" when not present)");
	public final Property<Float> _usageBowSpeedFactor = DecreasingFactor("move.usage.bow.speed.factor").defaults(_usageSpeedFactor).comment("Speed factor while pulling back a bow (>= 0 AND <= 1, defaults to \"move.usage.speed.factor\" when not present)");
	public final Property<Float> _usageFoodSpeedFactor = DecreasingFactor("move.usage.food.speed.factor").defaults(_usageSpeedFactor).comment("Speed factor while eating food (>= 0 AND <= 1, defaults to \"move.usage.speed.factor\" when not present)");

	public final Property<Boolean> _sprintDuringItemUsage = Modified("move.usage.sprint").comment("To switch on/off generic sprinting while using an item").section();



	public final Property<Boolean> _replaceRopeClimbing = Unmodified("move.mod.rope.replace.climb.rope").comment("Whether to replace the rope climbing of mod pack 'Ropes+'").book("Mod compatibility", "Below you find the options to manipulate how Smart Moving should interact with other mods");



	public final Property<String[]> _survivalConfigKeys = Strings("move.config.survival.keys").singular().defaults(new String[]{"e", "m", "h"}).comment("A list of survival option configuration keys, entries seperated by ','").book("Configuration Management", "Below you find the options to define multiple configuration option sets");
	public final Property<String> _survivalDefaultConfigKey = String("move.config.survival.keys.default").singular().defaults("m").comment("The option configuration to start the next minecraft survival game with (can also be modified via in-game option configuration toggling)");

	public final Property<String[]> _creativeConfigKeys = Strings("move.config.creative.keys").singular().defaults(new String[]{"c"}).defaults(new String[0], _pre_sm_2_3).comment("A list of creative option configuration keys, entries seperated by ','").section();
	public final Property<String> _creativeDefaultConfigKey = String("move.config.creative.keys.default").singular().defaults("c").defaults("", _pre_sm_2_3).comment("The option configuration to start the next minecraft creative game with (can also be modified via in-game option configuration toggling)");

	public final Property<String[]> _adventureConfigKeys = Strings("move.config.adventure.keys").singular().defaults(new String[]{"e", "m", "h"}).comment("A list of adventure option configuration keys, entries seperated by ','").section();
	public final Property<String> _adventureDefaultConfigKey = String("move.config.adventure.keys.default").singular().defaults("m").comment("The option configuration to start the next minecraft adventure game with (can also be modified via in-game option configuration toggling)");

	public final Property<String> _configKeyName = String("move.config.key.name").defaults(Value((String)null).e("Easy").m("Medium").h("Hard")).comment("The display names of the option configuration keys").section();



	public final Property<Boolean> _serverConfig = Modified("move.server.config").singular().comment("Whether the players on this server should be forced to use the configurations on this server").book("Server Management", "Below you find the options to manage your server");

	public final Property<Map<String,String>> _survivalDefaultConfigUserKeys = StringMap("move.server.config.survival.user.keys").singular().comment("The option configuration to start the player's next minecraft survival game with (can also be modified via in-game option configuration toggling)").section();
	public final Property<Map<String,String>> _creativeDefaultConfigUserKeys = StringMap("move.server.config.creative.user.keys").singular().comment("The option configuration to start the player's next minecraft creative game with (can also be modified via in-game option configuration toggling)");
	public final Property<Map<String,String>> _adventureDefaultConfigUserKeys = StringMap("move.server.config.adventure.user.keys").singular().comment("The option configuration to start the player's next minecraft adventure game with (can also be modified via in-game option configuration toggling)");
	public final Property<Map<String,Integer>> _speedUsersExponents = IntegerMap("move.server.speed.user.exponents").singular().comment("The user specific exponents for in-game speed manipulation");

	public final Property<Boolean> _globalConfig = Modified("move.global.config").key("move.config.overwrite", _pre_sm_3_0).key("move.config.send", _pre_sm_2_2).singular().depends(_serverConfig).comment("Whether all players on this server should be forced to use the same global configuration (Relevant only if \"move.server.config\" is true)").section();

	public final Property<String[]> _usersWithChangeConfigRights = Strings("move.global.config.right.user.names").singular().comment("The names of the users that have the right to change the global configuraton in-game, entries seperated by ','").section();
	public final Property<String[]> _usersWithChangeSpeedRights = Strings("move.global.speed.right.user.names").singular().comment("The names of the users that have the right to change the global speed in-game, entries seperated by ','");



	public void changeSpeed(int difference)
	{
		_speedUserExponent.setValue(_speedUserExponent.value + difference);
	}

	public boolean isUserSpeedEnabled()
	{
		return enabled && _speedUser.value;
	}

	public boolean isUserSpeedAlwaysDefault()
	{
		return !_speedUser.value || _speedUserFactor.value == 1F;
	}

	public float getUserSpeedFactor()
	{
		if(isUserSpeedAlwaysDefault() || _speedUserExponent.value == 0)
			return 1F;
		return (float)Math.pow(1F + _speedUserFactor.value, _speedUserExponent.value);
	}

	protected void loadFromProperties(Properties properties)
	{
		try
		{
			load(properties);
		}
		catch(Exception e)
		{
			throw new RuntimeException("Could not load Smart Moving properties from properties", e);
		}
	}

	protected void writeToProperties(Properties properties, List<Property<?>> except)
	{
		try
		{
			write(properties);

			if(except != null)
				for(int i = 0; i < except.size(); i++)
				{
					String key = except.get(i).getCurrentKey();
					if(key != null)
						properties.remove(key);
				}
		}
		catch(Exception e)
		{
			throw new RuntimeException("Could not write Smart Moving properties to properties", e);
		}
	}

	protected void loadFromOptionsFile(File optionsPath)
	{
		Properties slcs_properties = new Properties(_slcs, new File(optionsPath, _smartLadderClimbingSpeedPropertiesFileName));
		Properties sc_properties = new Properties(_sc, new File(optionsPath, _smartClimbingPropertiesFileName));
		Properties sm_cs_properties = new Properties(new File(optionsPath, _smartMovingClientServerPropertiesFileName));
		Properties properties = new Properties(new File(optionsPath, _smartMovingPropertiesFileName));

		try
		{
			load(properties, sm_cs_properties, sc_properties, slcs_properties);
		}
		catch(Exception e)
		{
			throw new RuntimeException("Could not load Smart Moving properties from file", e);
		}
	}

	protected void saveToOptionsFile(File optionsPath)
	{
		try
		{
			save(new File(optionsPath, _smartMovingPropertiesFileName), _sm_current, true, true);
		}
		catch(Exception e)
		{
			throw new RuntimeException("Could not save Smart Moving properties to file", e);
		}
	}

	@Override
	protected void printHeader(PrintWriter printer)
	{
		printer.println("#######################################################################");
		printer.println("#");
		printer.println("# Smart Moving mod configuration file");
		printer.println("# -----------------------------------");
		printer.println("#");
		printer.println("# Modify the values behind the keys in this file to configure the");
		printer.println("# Smart Moving behavior as you like it.");
		printer.println("#");
		printer.println("# * All options you leave at their default value will be automatically");
		printer.println("#   updated when the default value is updated with a new version of");
		printer.println("#   Smart Moving options.");
		printer.println("#");
		printer.println("# * All options you modify will stay the same over the upgrade cycles");
		printer.println("#   as long as they still fall in the allowed range.");
		printer.println("#");
		printer.println("# * The character '!' will be used after the value's text's end to");
		printer.println("#   mark a value that has originally been modified but became the");
		printer.println("#   default value at some point in the update process to avoid it being");
		printer.println("#   updated too with the next default value change.");
		printer.println("#");
		printer.println("# * The '!' mark can also be used to create a 'modified' and so not");
		printer.println("#   automatically updated value identical to the current default value");
		printer.println("#");
		printer.println("# Additionally you can create multiple option configurations and define");
		printer.println("# separate option values for each configuration.");
		printer.println("#");
		printer.println("# * key-value separator is ':'");
		printer.println("# * key-value-pair separator is ';'");
		printer.println("# * a value without key will become the default value");
		printer.println("#");
		printer.println("#######################################################################");
		printer.println();
	}

	@Override
	protected void printVersion(PrintWriter printer, String version, boolean comments)
	{
		if(comments)
			printer.println("# The current version of this Smart Moving options file");

		printer.print("move.options.version");
		printer.print(":");
		printer.print(version);
		printer.println();
	}

	private static Property<Boolean> Creative(String key, String... versions)
	{
		return Modified(key, versions).defaults(Value(false).c(true));
	}

	private static Property<Boolean> Hard(String key, String... versions)
	{
		return Modified(key, versions).defaults(Value(false).h(true)).defaults(false, _pre_sm_1_5);
	}

	private static Property<Boolean> Medium(String key, String... versions)
	{
		return Unmodified(key, versions).defaults(Value(true).e(false)).defaults(true, _pre_sm_1_5);
	}

	protected final static int Unknown = -1;
	protected final static int Survival = 0;
	protected final static int Creative = 1;
	protected final static int Adventure = 2;

	public Object getSpeedPercent()
	{
		float factor = getUserSpeedFactor() * 100F;
		int fraction = 0;
		while(factor < 100F)
		{
			fraction++;
			factor *= 10F;
		}

		int significant = Math.round(factor);
		BigDecimal decimal = new BigDecimal(significant);
		while(fraction-- > 0)
			decimal = decimal.divide(ten);
		return formatter.format(decimal);
	}

	private final static DecimalFormat formatter = new DecimalFormat("0.############################################################");
	private final static BigDecimal ten = new BigDecimal(10);
	public final static Object defaultSpeedPercent = "100";
}