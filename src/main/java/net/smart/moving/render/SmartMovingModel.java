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

package net.smart.moving.render;

import net.minecraft.block.*;
import net.minecraft.client.model.*;
import net.minecraft.util.*;
import net.smart.moving.*;
import net.smart.render.*;

public class SmartMovingModel extends SmartRenderContext
{
	public IModelPlayer imp;
	public ModelBiped mp;
	public net.smart.render.SmartRenderModel md;

	public SmartMovingModel(net.smart.render.IModelPlayer md, IModelPlayer imp)
	{
		this.imp = imp;
		this.md = md.getRenderModel();
		this.mp = this.md.mp;

		if(SmartMovingRender.CurrentMainModel != null)
		{
			isClimb = SmartMovingRender.CurrentMainModel.isClimb;
			isClimbJump = SmartMovingRender.CurrentMainModel.isClimbJump;
			handsClimbType = SmartMovingRender.CurrentMainModel.handsClimbType;
			feetClimbType = SmartMovingRender.CurrentMainModel.feetClimbType;
			isHandsVineClimbing = SmartMovingRender.CurrentMainModel.isHandsVineClimbing;
			isFeetVineClimbing = SmartMovingRender.CurrentMainModel.isFeetVineClimbing;
			isCeilingClimb = SmartMovingRender.CurrentMainModel.isCeilingClimb;
			isSwim = SmartMovingRender.CurrentMainModel.isSwim;
			isDive = SmartMovingRender.CurrentMainModel.isDive;
			isCrawl = SmartMovingRender.CurrentMainModel.isCrawl;
			isCrawlClimb = SmartMovingRender.CurrentMainModel.isCrawlClimb;
			isJump = SmartMovingRender.CurrentMainModel.isJump;
			isHeadJump = SmartMovingRender.CurrentMainModel.isHeadJump;
			isSlide = SmartMovingRender.CurrentMainModel.isSlide;
			isFlying = SmartMovingRender.CurrentMainModel.isFlying;
			isLevitate = SmartMovingRender.CurrentMainModel.isLevitate;
			isFalling = SmartMovingRender.CurrentMainModel.isFalling;
			isGenericSneaking = SmartMovingRender.CurrentMainModel.isGenericSneaking;
			isAngleJumping = SmartMovingRender.CurrentMainModel.isAngleJumping;
			angleJumpType = SmartMovingRender.CurrentMainModel.angleJumpType;
			isRopeSliding = SmartMovingRender.CurrentMainModel.isRopeSliding;

			currentHorizontalSpeedFlattened = SmartMovingRender.CurrentMainModel.currentHorizontalSpeedFlattened;
			smallOverGroundHeight = SmartMovingRender.CurrentMainModel.smallOverGroundHeight;
			overGroundBlock = SmartMovingRender.CurrentMainModel.overGroundBlock;
		}
	}

	@SuppressWarnings("unused")
	private void setRotationAngles(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		final float FrequenceFactor = 0.6662F;

		isStandard = false;

		float currentCameraAngle = md.currentCameraAngle;
		float currentHorizontalAngle = md.currentHorizontalAngle;
		float currentVerticalAngle = md.currentVerticalAngle;
		float forwardRotation = md.forwardRotation;
		float currentVerticalSpeed = md.currentVerticalSpeed;
		float totalVerticalDistance = md.totalVerticalDistance;
		float totalDistance = md.totalDistance;
		double horizontalDistance = md.horizontalDistance;
		float currentSpeed = md.currentSpeed;
		if(!Float.isNaN(currentHorizontalSpeedFlattened))
			currentHorizontalSpeed = currentHorizontalSpeedFlattened;

		ModelRotationRenderer bipedOuter = md.bipedOuter;
		ModelRotationRenderer bipedTorso = md.bipedTorso;
		ModelRotationRenderer bipedBody = md.bipedBody;
		ModelRotationRenderer bipedBreast = md.bipedBreast;
		ModelRotationRenderer bipedHead = md.bipedHead;
		ModelRotationRenderer bipedRightShoulder = md.bipedRightShoulder;
		ModelRotationRenderer bipedRightArm = md.bipedRightArm;
		ModelRotationRenderer bipedLeftShoulder = md.bipedLeftShoulder;
		ModelRotationRenderer bipedLeftArm = md.bipedLeftArm;
		ModelRotationRenderer bipedPelvic = md.bipedPelvic;
		ModelRotationRenderer bipedRightLeg = md.bipedRightLeg;
		ModelRotationRenderer bipedLeftLeg = md.bipedLeftLeg;

		if(isRopeSliding)
		{
			float time = totalTime * 0.15F;

			bipedHead.rotateAngleZ = Between(-Sixteenth, Sixteenth, Normalize(currentCameraAngle - currentHorizontalAngle));
			bipedHead.rotateAngleX = Eighth;
			bipedHead.rotationPointY = 2F;

			bipedOuter.fadeRotateAngleY = false;
			bipedOuter.rotateAngleY = currentHorizontalAngle;
			bipedTorso.rotateAngleX = Sixteenth + Sixtyfourth * MathHelper.cos(time);

			bipedLeftArm.rotateAngleX = bipedRightArm.rotateAngleX = Half - bipedTorso.rotateAngleX;

			bipedRightArm.rotateAngleZ = Sixteenth + Thirtytwoth;
			bipedLeftArm.rotateAngleZ = -Sixteenth - Thirtytwoth;

			bipedRightArm.rotationPointY = bipedLeftArm.rotationPointY = -2F;

			bipedPelvic.rotateAngleX = bipedTorso.rotateAngleX;

			bipedLeftLeg.rotateAngleZ = -Thirtytwoth;
			bipedRightLeg.rotateAngleZ = Thirtytwoth;

			bipedLeftLeg.rotateAngleX = Sixtyfourth * MathHelper.cos(time + Quarter);
			bipedRightLeg.rotateAngleX = Sixtyfourth * MathHelper.cos(time - Quarter);
		}
		else if(isClimb || isCrawlClimb)
		{
			bipedOuter.rotateAngleY = forwardRotation / RadiantToAngle;

			bipedHead.rotateAngleY = 0.0F;
			bipedHead.rotateAngleX = viewVerticalAngelOffset / RadiantToAngle;

			bipedLeftLeg.rotationOrder = ModelRotationRenderer.YZX;
			bipedRightLeg.rotationOrder = ModelRotationRenderer.YZX;

			float handsFrequenceUpFactor, handsDistanceUpFactor, handsDistanceUpOffset, feetFrequenceUpFactor, feetDistanceUpFactor, feetDistanceUpOffset;
			float handsFrequenceSideFactor, handsDistanceSideFactor, handsDistanceSideOffset, feetFrequenceSideFactor, feetDistanceSideFactor, feetDistanceSideOffset;

			int handsClimbType = this.handsClimbType;
			if(isHandsVineClimbing && handsClimbType == HandsClimbing.MiddleGrab)
				handsClimbType = HandsClimbing.UpGrab;

			float verticalSpeed = Math.min(0.5f, currentVerticalSpeed);
			float horizontalSpeed = Math.min(0.5f, currentHorizontalSpeed);

			switch(handsClimbType)
			{
				case HandsClimbing.MiddleGrab:
					handsFrequenceSideFactor = FrequenceFactor;
					handsDistanceSideFactor = 1.0F;
					handsDistanceSideOffset = 0.0F;

					handsFrequenceUpFactor = FrequenceFactor;
					handsDistanceUpFactor = 2F;
					handsDistanceUpOffset = -Quarter;
					break;
				case HandsClimbing.UpGrab:
					handsFrequenceSideFactor = FrequenceFactor;
					handsDistanceSideFactor = 1.0F;
					handsDistanceSideOffset = 0.0F;

					handsFrequenceUpFactor = FrequenceFactor;
					handsDistanceUpFactor = 2F;
					handsDistanceUpOffset = -2.5F;
					break;
				default:
					handsFrequenceSideFactor = FrequenceFactor;
					handsDistanceSideFactor = 1.0F;
					handsDistanceSideOffset = 0.0F;

					handsFrequenceUpFactor = FrequenceFactor;
					handsDistanceUpFactor = 0F;
					handsDistanceUpOffset = -0.5F;
					break;
			}

			switch(feetClimbType)
			{
				case HandsClimbing.UpGrab:
					feetFrequenceUpFactor = FrequenceFactor;
					feetDistanceUpFactor = 0.3F/verticalSpeed;
					feetDistanceUpOffset = -0.3F;

					feetFrequenceSideFactor = FrequenceFactor;
					feetDistanceSideFactor = 0.5F;
					feetDistanceSideOffset = 0.0F;
					break;
				default:
					feetFrequenceUpFactor = FrequenceFactor;
					feetDistanceUpFactor = 0.0F;
					feetDistanceUpOffset = 0.0F;

					feetFrequenceSideFactor = FrequenceFactor;
					feetDistanceSideFactor = 0.0F;
					feetDistanceSideOffset = 0.0F;
					break;
			}

			bipedRightArm.rotateAngleX = MathHelper.cos(totalVerticalDistance * handsFrequenceUpFactor + Half) * verticalSpeed * handsDistanceUpFactor + handsDistanceUpOffset;
			bipedLeftArm.rotateAngleX = MathHelper.cos(totalVerticalDistance * handsFrequenceUpFactor) * verticalSpeed * handsDistanceUpFactor + handsDistanceUpOffset;

			bipedRightArm.rotateAngleY = MathHelper.cos(totalHorizontalDistance * handsFrequenceSideFactor + Quarter) * horizontalSpeed * handsDistanceSideFactor + handsDistanceSideOffset;
			bipedLeftArm.rotateAngleY = MathHelper.cos(totalHorizontalDistance * handsFrequenceSideFactor) * horizontalSpeed * handsDistanceSideFactor + handsDistanceSideOffset;

			if(isHandsVineClimbing)
			{
				bipedLeftArm.rotateAngleY *= 1F + handsFrequenceSideFactor;
				bipedRightArm.rotateAngleY *= 1F + handsFrequenceSideFactor;

				bipedLeftArm.rotateAngleY += Eighth;
				bipedRightArm.rotateAngleY -= Eighth;

				setArmScales(Math.abs(MathHelper.cos(bipedRightArm.rotateAngleX)), Math.abs(MathHelper.cos(bipedLeftArm.rotateAngleX)));
			}

			if(!isFeetVineClimbing)
			{
				bipedRightLeg.rotateAngleX = MathHelper.cos(totalVerticalDistance * feetFrequenceUpFactor) * feetDistanceUpFactor * verticalSpeed + feetDistanceUpOffset;
				bipedLeftLeg.rotateAngleX = MathHelper.cos(totalVerticalDistance * feetFrequenceUpFactor + Half) * feetDistanceUpFactor * verticalSpeed + feetDistanceUpOffset;
			}

			bipedRightLeg.rotateAngleZ = -(MathHelper.cos(totalHorizontalDistance * feetFrequenceSideFactor) - 1.0F) * horizontalSpeed * feetDistanceSideFactor + feetDistanceSideOffset;
			bipedLeftLeg.rotateAngleZ = -(MathHelper.cos(totalHorizontalDistance * feetFrequenceSideFactor + Quarter) + 1.0F) * horizontalSpeed * feetDistanceSideFactor + feetDistanceSideOffset;

			if(isFeetVineClimbing)
			{
				float total = (MathHelper.cos(totalDistance + Half) + 1) * Thirtytwoth + Sixteenth;
				bipedRightLeg.rotateAngleX = -total;
				bipedLeftLeg.rotateAngleX = -total;

				float difference = Math.max(0, MathHelper.cos(totalDistance - Quarter)) * Sixtyfourth;
				bipedLeftLeg.rotateAngleZ += -difference;
				bipedRightLeg.rotateAngleZ += difference;

				setLegScales(Math.abs(MathHelper.cos(bipedRightLeg.rotateAngleX)), Math.abs(MathHelper.cos(bipedLeftLeg.rotateAngleX)));
			}

			if(isCrawlClimb)
			{
				float height = smallOverGroundHeight + 0.25F;
				float bodyLength = 0.7F;
				float legLength = 0.55F;

				float bodyAngleX, legAngleX, legAngleZ;
				if(height < bodyLength)
				{
					bodyAngleX = Math.max(0, (float)Math.acos(height / bodyLength));
					legAngleX = Quarter - bodyAngleX;
					legAngleZ = Thirtytwoth;
				}
				else if(height < bodyLength + legLength)
				{
					bodyAngleX = 0F;
					legAngleX = Math.max(0, (float)Math.acos((height - bodyLength) / legLength));
					legAngleZ = Thirtytwoth * (legAngleX / 1.537F);
				}
				else
				{
					bodyAngleX = 0F;
					legAngleX = 0F;
					legAngleZ = 0F;
				}

				bipedTorso.rotateAngleX = bodyAngleX;

				bipedRightShoulder.rotateAngleX = -bodyAngleX;
				bipedLeftShoulder.rotateAngleX = -bodyAngleX;

				bipedHead.rotateAngleX = -bodyAngleX;

				bipedRightLeg.rotateAngleX = legAngleX;
				bipedLeftLeg.rotateAngleX = legAngleX;

				bipedRightLeg.rotateAngleZ = legAngleZ;
				bipedLeftLeg.rotateAngleZ = -legAngleZ;
			}

			if(handsClimbType == HandsClimbing.NoGrab && feetClimbType != FeetClimbing.NoStep)
			{
				bipedTorso.rotateAngleX = 0.5F;
				bipedHead.rotateAngleX -= 0.5F;
				bipedPelvic.rotateAngleX -= 0.5F;

				bipedTorso.rotationPointZ = -6.0F;
			}
		}
		else if(isClimbJump)
		{
			bipedRightArm.rotateAngleX = Half + Sixteenth;
			bipedLeftArm.rotateAngleX = Half + Sixteenth;

			bipedRightArm.rotateAngleZ = -Thirtytwoth;
			bipedLeftArm.rotateAngleZ = Thirtytwoth;
		}
		else if(isCeilingClimb)
		{
			float distance = totalHorizontalDistance * 0.7F;
			float walkFactor = Factor(currentHorizontalSpeed, 0F, 0.12951545F);
			float standFactor = Factor(currentHorizontalSpeed, 0.12951545F, 0F);
			float horizontalAngle = horizontalDistance < 0.015F ? currentCameraAngle : currentHorizontalAngle;

			bipedLeftArm.rotateAngleX = (MathHelper.cos(distance) * 0.52F + Half) * walkFactor + Half * standFactor;
			bipedRightArm.rotateAngleX = (MathHelper.cos(distance + Half) * 0.52F - Half) * walkFactor - Half * standFactor;

			bipedLeftLeg.rotateAngleX = -MathHelper.cos(distance) * 0.12F * walkFactor;
			bipedRightLeg.rotateAngleX = -MathHelper.cos(distance + Half) * 0.32F * walkFactor;

			float rotateY = MathHelper.cos(distance) * 0.44F * walkFactor;
			bipedOuter.rotateAngleY = rotateY + horizontalAngle;

			bipedRightArm.rotateAngleY = bipedLeftArm.rotateAngleY = -rotateY;
			bipedRightLeg.rotateAngleY = bipedLeftLeg.rotateAngleY = -rotateY;

			bipedHead.rotateAngleY = -rotateY;
		}
		else if(isSwim)
		{
			float distance = totalHorizontalDistance;
			float walkFactor = Factor(currentHorizontalSpeed, 0.15679921F, 0.52264464F);
			float sneakFactor = Math.min(Factor(currentHorizontalSpeed, 0, 0.15679921F),Factor(currentHorizontalSpeed, 0.52264464F, 0.15679921F));
			float standFactor = Factor(currentHorizontalSpeed, 0.15679921F, 0F);
			float standSneakFactor = standFactor + sneakFactor;
			float horizontalAngle = horizontalDistance < (isGenericSneaking ? 0.005 : 0.015F) ? currentCameraAngle : currentHorizontalAngle;

			bipedHead.rotationOrder = ModelRotationRenderer.YXZ;
			bipedHead.rotateAngleY = MathHelper.cos(distance / 2.0F - Quarter) * walkFactor;
			bipedHead.rotateAngleX = -Eighth * standSneakFactor;
			bipedHead.rotationPointZ = -2F;

			bipedOuter.fadeRotateAngleX = true;
			bipedOuter.rotateAngleX = Quarter - Sixteenth * standSneakFactor;
			bipedOuter.rotateAngleY = horizontalAngle;

			bipedBreast.rotateAngleY = bipedBody.rotateAngleY = MathHelper.cos(distance / 2.0F - Quarter) * walkFactor;

			bipedRightArm.rotationOrder = ModelRotationRenderer.YZX;
			bipedLeftArm.rotationOrder = ModelRotationRenderer.YZX;

			bipedRightArm.rotateAngleZ = Quarter + Eighth + MathHelper.cos(totalTime * 0.1F) * standSneakFactor * 0.8F;
			bipedLeftArm.rotateAngleZ = -Quarter - Eighth - MathHelper.cos(totalTime * 0.1F) * standSneakFactor * 0.8F;

			bipedRightArm.rotateAngleX = ((distance * 0.5F) % Whole - Half) * walkFactor + Sixteenth * standSneakFactor;
			bipedLeftArm.rotateAngleX = ((distance * 0.5F + Half) % Whole - Half) * walkFactor + Sixteenth * standSneakFactor;

			bipedRightLeg.rotateAngleX = MathHelper.cos(distance) * 0.52264464F * walkFactor;
			bipedLeftLeg.rotateAngleX = MathHelper.cos(distance + Half) * 0.52264464F * walkFactor;

			float rotateFeetAngleZ = Sixteenth * standSneakFactor + MathHelper.cos(totalTime * 0.1F) * 0.4F * (standFactor - sneakFactor);
			bipedRightLeg.rotateAngleZ = rotateFeetAngleZ;
			bipedLeftLeg.rotateAngleZ = -rotateFeetAngleZ;

			if(scaleLegType != NoScaleStart)
				setLegScales(
					1F + (MathHelper.cos(totalTime * 0.1F + Quarter) - 1F) * 0.15F * sneakFactor,
					1F + (MathHelper.cos(totalTime * 0.1F + Quarter) - 1F) * 0.15F * sneakFactor);

			if(scaleArmType != NoScaleStart)
				setArmScales(
					1F + (MathHelper.cos(totalTime * 0.1F - Quarter) - 1F) * 0.15F * sneakFactor,
					1F + (MathHelper.cos(totalTime * 0.1F - Quarter) - 1F) * 0.15F * sneakFactor);
		}
		else if(isDive)
		{
			float distance = totalDistance * 0.7F;
			float walkFactor = Factor(currentSpeed, 0F, 0.15679921F);
			float standFactor = Factor(currentSpeed, 0.15679921F, 0F);
			float horizontalAngle = totalDistance < (isGenericSneaking ? 0.005 : 0.015F) ? currentCameraAngle : currentHorizontalAngle;

			bipedHead.rotateAngleX = -Eighth;
			bipedHead.rotationPointZ = -2F;

			bipedOuter.fadeRotateAngleX = true;
			bipedOuter.rotateAngleX = isLevitate ? Quarter - Sixteenth : (isJump ? 0F : Quarter - currentVerticalAngle);
			bipedOuter.rotateAngleY = horizontalAngle;

			bipedRightLeg.rotateAngleZ = (MathHelper.cos(distance) + 1F) * 0.52264464F * walkFactor + Sixteenth * standFactor;
			bipedLeftLeg.rotateAngleZ = (MathHelper.cos(distance + Half) - 1F) * 0.52264464F * walkFactor - Sixteenth * standFactor;

			if(scaleLegType != NoScaleStart)
				setLegScales(
					1F + (MathHelper.cos(distance - Quarter) - 1F) * 0.25F * walkFactor,
					1F + (MathHelper.cos(distance - Quarter) - 1F) * 0.25F * walkFactor);

			bipedRightArm.rotateAngleZ = (MathHelper.cos(distance + Half) * 0.52264464F * 2.5F + Quarter) * walkFactor + (Quarter + Eighth) * standFactor;
			bipedLeftArm.rotateAngleZ = (MathHelper.cos(distance) * 0.52264464F * 2.5F - Quarter) * walkFactor - (Quarter + Eighth) * standFactor;

			if(scaleArmType != NoScaleStart)
				setArmScales(
					1F + (MathHelper.cos(distance + Quarter) - 1F) * 0.15F * walkFactor,
					1F + (MathHelper.cos(distance + Quarter) - 1F) * 0.15F * walkFactor);
		}
		else if(isCrawl)
		{
			float distance = totalHorizontalDistance * 1.3F;
			float walkFactor = Factor(currentHorizontalSpeedFlattened, 0F, 0.12951545F);
			float standFactor = Factor(currentHorizontalSpeedFlattened, 0.12951545F, 0F);

			bipedHead.rotateAngleZ = -viewHorizontalAngelOffset / RadiantToAngle;
			bipedHead.rotateAngleX = -Eighth;
			bipedHead.rotationPointZ = -2F;

			bipedTorso.rotationOrder = ModelRotationRenderer.YZX;
			bipedTorso.rotateAngleX = Quarter - Thirtytwoth;
			bipedTorso.rotationPointY = 3F;
			bipedTorso.rotateAngleZ = MathHelper.cos(distance + Quarter) * Sixtyfourth * walkFactor;
			bipedBody.rotateAngleY = MathHelper.cos(distance + Half) * Sixtyfourth * walkFactor;

			bipedRightLeg.rotateAngleX = (MathHelper.cos(distance - Quarter) * Sixtyfourth + Thirtytwoth) * walkFactor + Thirtytwoth * standFactor;
			bipedLeftLeg.rotateAngleX = (MathHelper.cos(distance - Half - Quarter) * Sixtyfourth + Thirtytwoth) * walkFactor + Thirtytwoth * standFactor;

			bipedRightLeg.rotateAngleZ = (MathHelper.cos(distance - Quarter) + 1F) * 0.25F * walkFactor + Thirtytwoth * standFactor;
			bipedLeftLeg.rotateAngleZ = (MathHelper.cos(distance - Quarter) - 1F) * 0.25F * walkFactor - Thirtytwoth * standFactor;

			if(scaleLegType != NoScaleStart)
				setLegScales(
						1F + (MathHelper.cos(distance + Quarter - Quarter) - 1F) * 0.25F * walkFactor,
						1F + (MathHelper.cos(distance - Quarter - Quarter) - 1F) * 0.25F * walkFactor);

			bipedRightArm.rotationOrder = ModelRotationRenderer.YZX;
			bipedLeftArm.rotationOrder = ModelRotationRenderer.YZX;

			bipedRightArm.rotateAngleX = Half + Eighth;
			bipedLeftArm.rotateAngleX = Half + Eighth;

			bipedRightArm.rotateAngleZ = ((MathHelper.cos(distance + Half)) * Sixtyfourth + Thirtytwoth)* walkFactor + Sixteenth * standFactor;
			bipedLeftArm.rotateAngleZ = ((MathHelper.cos(distance + Half)) * Sixtyfourth - Thirtytwoth) * walkFactor - Sixteenth * standFactor;

			bipedRightArm.rotateAngleY = -Quarter;
			bipedLeftArm.rotateAngleY = Quarter;

			if(scaleArmType != NoScaleStart)
				setArmScales(
					1F + (MathHelper.cos(distance + Quarter) - 1F) * 0.15F * walkFactor,
					1F + (MathHelper.cos(distance - Quarter) - 1F) * 0.15F * walkFactor);
		}
		else if(isSlide)
		{
			float distance = totalHorizontalDistance * 0.7F;
			float walkFactor = Factor(currentHorizontalSpeed, 0F, 1F) * 0.8F;

			bipedHead.rotateAngleZ = -viewHorizontalAngelOffset / RadiantToAngle;
			bipedHead.rotateAngleX = -Eighth - Sixteenth;
			bipedHead.rotationPointZ = -2F;

			bipedOuter.fadeRotateAngleY = false;
			bipedOuter.rotateAngleY = currentHorizontalAngle;
			bipedOuter.rotationPointY = 5F;
			bipedOuter.rotateAngleX = Quarter;

			bipedBody.rotationOrder = ModelRotationRenderer.YXZ;
			bipedBody.offsetY = -0.4F;
			bipedBody.rotationPointY = +6.5F;
			bipedBody.rotateAngleX = MathHelper.cos(distance - Eighth) * Sixtyfourth * walkFactor;
			bipedBody.rotateAngleY = MathHelper.cos(distance + Eighth) * Sixtyfourth * walkFactor;

			bipedRightLeg.rotateAngleX = MathHelper.cos(distance + Half) * Sixtyfourth * walkFactor + Sixtyfourth;
			bipedLeftLeg.rotateAngleX = MathHelper.cos(distance + Quarter) * Sixtyfourth * walkFactor + Sixtyfourth;

			bipedRightLeg.rotateAngleZ = Thirtytwoth;
			bipedLeftLeg.rotateAngleZ = -Thirtytwoth;

			bipedRightArm.rotationOrder = ModelRotationRenderer.YZX;
			bipedLeftArm.rotationOrder = ModelRotationRenderer.YZX;

			bipedRightArm.rotateAngleX = MathHelper.cos(distance + Quarter) * Sixtyfourth * walkFactor + Half - Sixtyfourth;
			bipedLeftArm.rotateAngleX = MathHelper.cos(distance - Half) * Sixtyfourth * walkFactor + Half - Sixtyfourth;

			bipedRightArm.rotateAngleZ = Sixteenth;
			bipedLeftArm.rotateAngleZ = -Sixteenth;

			bipedRightArm.rotateAngleY = -Quarter;
			bipedLeftArm.rotateAngleY = Quarter;
		}
		else if(isFlying)
		{
			float distance = totalDistance * 0.08F;
			float walkFactor = Factor(currentSpeed, 0F, 1);
			float standFactor = Factor(currentSpeed, 1F, 0F);
			float time = totalTime * 0.15F;
			float verticalAngle = isJump ? Math.abs(currentVerticalAngle) : currentVerticalAngle;
			float horizontalAngle = horizontalDistance < 0.05F ? currentCameraAngle : currentHorizontalAngle;

			bipedOuter.fadeRotateAngleX = true;
			bipedOuter.rotateAngleX = (Quarter - verticalAngle) * walkFactor;
			bipedOuter.rotateAngleY = horizontalAngle;

			bipedHead.rotateAngleX = -bipedOuter.rotateAngleX / 2F;

			bipedRightArm.rotationOrder = ModelRotationRenderer.XZY;
			bipedLeftArm.rotationOrder = ModelRotationRenderer.XZY;

			bipedRightArm.rotateAngleY = (MathHelper.cos(time) * Sixteenth) * standFactor;
			bipedLeftArm.rotateAngleY = (MathHelper.cos(time) * Sixteenth) * standFactor;

			bipedRightArm.rotateAngleZ = (MathHelper.cos(distance + Half) * Sixtyfourth + (Half - Sixteenth)) * walkFactor + Quarter * standFactor;
			bipedLeftArm.rotateAngleZ = (MathHelper.cos(distance) * Sixtyfourth - (Half - Sixteenth)) * walkFactor - Quarter * standFactor;

			bipedRightLeg.rotateAngleX = MathHelper.cos(distance) * Sixtyfourth * walkFactor + MathHelper.cos(time + Half) * Sixtyfourth * standFactor;
			bipedLeftLeg.rotateAngleX = MathHelper.cos(distance + Half) * Sixtyfourth * walkFactor + MathHelper.cos(time) * Sixtyfourth * standFactor;

			bipedRightLeg.rotateAngleZ = Sixtyfourth;
			bipedLeftLeg.rotateAngleZ = -Sixtyfourth;
		}
		else if(isHeadJump)
		{
			bipedOuter.fadeRotateAngleX = true;
			bipedOuter.rotateAngleX = (Quarter - currentVerticalAngle);
			bipedOuter.rotateAngleY = currentHorizontalAngle;

			bipedHead.rotateAngleX = -bipedOuter.rotateAngleX / 2F;

			float bendFactor = Math.min(Factor(currentVerticalAngle, Quarter, 0), Factor(currentVerticalAngle, -Quarter, 0));
			bipedRightArm.rotateAngleX = bendFactor * -Eighth;
			bipedLeftArm.rotateAngleX = bendFactor * -Eighth;

			bipedRightLeg.rotateAngleX = bendFactor * -Eighth;
			bipedLeftLeg.rotateAngleX = bendFactor * -Eighth;

			float armFactorZ = Factor(currentVerticalAngle, Quarter, -Quarter);
			if(overGroundBlock != null && overGroundBlock.getMaterial().isSolid())
				armFactorZ = Math.min(armFactorZ, smallOverGroundHeight / 5F);

			bipedRightArm.rotateAngleZ = Half - Sixteenth + armFactorZ * Eighth;
			bipedLeftArm.rotateAngleZ = Sixteenth - Half - armFactorZ * Eighth;

			float legFactorZ = Factor(currentVerticalAngle, -Quarter, Quarter);
			bipedRightLeg.rotateAngleZ = Sixtyfourth * legFactorZ;
			bipedLeftLeg.rotateAngleZ = -Sixtyfourth * legFactorZ;
		}
		else if(isFalling)
		{
			float distance = totalDistance * 0.1F;

			bipedRightArm.rotationOrder = ModelRotationRenderer.XZY;
			bipedLeftArm.rotationOrder = ModelRotationRenderer.XZY;

			bipedRightArm.rotateAngleY = (MathHelper.cos(distance + Quarter) * Eighth);
			bipedLeftArm.rotateAngleY = (MathHelper.cos(distance + Quarter) * Eighth);

			bipedRightArm.rotateAngleZ = (MathHelper.cos(distance) * Eighth + Quarter);
			bipedLeftArm.rotateAngleZ = (MathHelper.cos(distance) * Eighth - Quarter);

			bipedRightLeg.rotateAngleX = (MathHelper.cos(distance + Half + Quarter) * Sixteenth + Thirtytwoth);
			bipedLeftLeg.rotateAngleX = (MathHelper.cos(distance + Quarter) * Sixteenth + Thirtytwoth);

			bipedRightLeg.rotateAngleZ = (MathHelper.cos(distance) * Sixteenth + Thirtytwoth);
			bipedLeftLeg.rotateAngleZ = (MathHelper.cos(distance) * Sixteenth - Thirtytwoth);
		}
		else
			isStandard = true;
	}

	private boolean isWorking()
	{
		return mp.onGround > 0F;
	}

	private void animateAngleJumping()
	{
		float angle = angleJumpType * Eighth;
		md.bipedPelvic.rotateAngleY -= md.bipedOuter.rotateAngleY;
		md.bipedPelvic.rotateAngleY += md.currentCameraAngle;

		float backness = 1F - Math.abs(angle - Half) / Quarter;
		float leftness = -Math.min(angle - Half, 0F) / Quarter;
		float rightness = Math.max(angle - Half, 0F) / Quarter;

		md.bipedLeftLeg.rotateAngleX = Thirtytwoth * (1F + rightness);
		md.bipedRightLeg.rotateAngleX = Thirtytwoth * (1F + leftness);
		md.bipedLeftLeg.rotateAngleY = -angle;
		md.bipedRightLeg.rotateAngleY = -angle;
		md.bipedLeftLeg.rotateAngleZ = Thirtytwoth * backness;
		md.bipedRightLeg.rotateAngleZ = -Thirtytwoth * backness;

		md.bipedLeftLeg.rotationOrder = ModelRotationRenderer.ZXY;
		md.bipedRightLeg.rotationOrder = ModelRotationRenderer.ZXY;

		md.bipedLeftArm.rotateAngleZ = -Sixteenth * rightness;
		md.bipedRightArm.rotateAngleZ = Sixteenth * leftness;

		md.bipedLeftArm.rotateAngleX = -Eighth * backness;
		md.bipedRightArm.rotateAngleX = -Eighth * backness;
	}

	private void animateNonStandardWorking(float viewVerticalAngelOffset)
	{
		md.bipedRightShoulder.ignoreSuperRotation = true;
		md.bipedRightShoulder.rotateAngleX = viewVerticalAngelOffset / RadiantToAngle;
		md.bipedRightShoulder.rotateAngleY = md.workingAngle / RadiantToAngle;
		md.bipedRightShoulder.rotateAngleZ = Half;
		md.bipedRightShoulder.rotationOrder = ModelRotationRenderer.ZYX;
		md.bipedRightArm.reset();
	}

	private void animateNonStandardBowAiming(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		md.bipedRightShoulder.ignoreSuperRotation = true;
		md.bipedRightShoulder.rotateAngleY = md.workingAngle / RadiantToAngle;
		md.bipedRightShoulder.rotateAngleZ = Half;
		md.bipedRightShoulder.rotationOrder = ModelRotationRenderer.ZYX;

		md.bipedLeftShoulder.ignoreSuperRotation = true;
		md.bipedLeftShoulder.rotateAngleY = md.workingAngle / RadiantToAngle;
		md.bipedLeftShoulder.rotateAngleZ = Half;
		md.bipedLeftShoulder.rotationOrder = ModelRotationRenderer.ZYX;

		md.bipedRightArm.reset();
		md.bipedLeftArm.reset();

		float headRotateAngleY = md.bipedHead.rotateAngleY;
		float outerRotateAngleY = md.bipedOuter.rotateAngleY;
		float headRotateAngleX = md.bipedHead.rotateAngleX;

		md.bipedHead.rotateAngleY = 0;
		md.bipedOuter.rotateAngleY = 0;
		md.bipedHead.rotateAngleX = 0;

		imp.superAnimateBowAiming(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);

		md.bipedHead.rotateAngleY = headRotateAngleY;
		md.bipedOuter.rotateAngleY = outerRotateAngleY;
		md.bipedHead.rotateAngleX = headRotateAngleX;
	}

	public void animateHeadRotation(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		setRotationAngles(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);

		if(isStandard)
			imp.superAnimateHeadRotation(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
	}

	public void animateSleeping(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		if(isStandard)
			imp.superAnimateSleeping(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
	}

	public void animateArmSwinging(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		if(isStandard)
			if(isAngleJumping)
				animateAngleJumping();
			else
				imp.superAnimateArmSwinging(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
	}

	public void animateRiding(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		if(isStandard)
			imp.superAnimateRiding(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
	}

	public void animateLeftArmItemHolding(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		if(isStandard)
			imp.superAnimateLeftArmItemHolding(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
	}

	public void animateRightArmItemHolding(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		if(isStandard)
			imp.superAnimateRightArmItemHolding(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
	}

	public void animateWorkingBody(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		if(isStandard)
			imp.superAnimateWorkingBody(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
		else if(isWorking())
			animateNonStandardWorking(viewVerticalAngelOffset);
	}

	public void animateWorkingArms(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		if(isStandard || isWorking())
			imp.superAnimateWorkingArms(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
	}

	public void animateSneaking(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		if(isStandard && !isAngleJumping)
			imp.superAnimateSneaking(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
	}

	public void animateArms(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		if(isStandard)
			imp.superApplyAnimationOffsets(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
	}

	public void animateBowAiming(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		if(isStandard)
			imp.superAnimateBowAiming(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
		else
			animateNonStandardBowAiming(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
	}

	private void setArmScales(float rightScale, float leftScale)
	{
		if(scaleArmType == Scale)
		{
			md.bipedRightArm.scaleY = rightScale;
			md.bipedLeftArm.scaleY = leftScale;
		}
		else if(scaleArmType == NoScaleEnd)
		{
			md.bipedRightArm.offsetY -= (1F - rightScale) * 0.5F;
			md.bipedLeftArm.offsetY -= (1F - leftScale) * 0.5F;
		}
	}

	private void setLegScales(float rightScale, float leftScale)
	{
		if(scaleLegType == Scale)
		{
			md.bipedRightLeg.scaleY = rightScale;
			md.bipedLeftLeg.scaleY = leftScale;
		}
		else if(scaleLegType == NoScaleEnd)
		{
			md.bipedRightLeg.offsetY -= (1F - rightScale) * 0.5F;
			md.bipedLeftLeg.offsetY -= (1F - leftScale) * 0.5F;
		}
	}

	private static float Factor(float x, float x0, float x1)
	{
		if(x0 > x1)
		{
			if(x <= x1)
				return 1F;
			if(x >= x0)
				return 0F;
			return (x0 - x) / (x0 - x1);
		}
		else
		{
			if(x >= x1)
				return 1F;
			if(x <= x0)
				return 0F;
			return (x - x0) / (x1 - x0);
		}
	}

	private static float Between(float min, float max, float value)
	{
		if(value < min)
			return min;
		if(value > max)
			return max;
		return value;
	}

	private static float Normalize(float radiant)
	{
		while(radiant > Half)
			radiant -= Whole;
		while(radiant < -Half)
			radiant += Whole;
		return radiant;
	}

	public boolean isStandard;

	public boolean isClimb;
	public boolean isClimbJump;
	public int feetClimbType;
	public int handsClimbType;
	public boolean isHandsVineClimbing;
	public boolean isFeetVineClimbing;
	public boolean isCeilingClimb;

	public boolean isSwim;
	public boolean isDive;
	public boolean isCrawl;
	public boolean isCrawlClimb;
	public boolean isJump;
	public boolean isHeadJump;
	public boolean isFlying;
	public boolean isSlide;
	public boolean isLevitate;
	public boolean isFalling;
	public boolean isGenericSneaking;
	public boolean isAngleJumping;
	public int angleJumpType;
	public boolean isRopeSliding;

	public float currentHorizontalSpeedFlattened;
	public float smallOverGroundHeight;
	public Block overGroundBlock;

	public int scaleArmType;
	public int scaleLegType;
}