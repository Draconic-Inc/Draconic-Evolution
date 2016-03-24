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

package net.smart.moving.render.playerapi;

import net.minecraft.client.model.*;

import api.player.model.*;

import net.smart.moving.render.*;
import net.smart.moving.render.IModelPlayer;
import net.smart.render.playerapi.*;

public class SmartMovingModelPlayerBase extends ModelPlayerBase implements IModelPlayer
{
	private SmartMovingModel model;

	public SmartMovingModelPlayerBase(ModelPlayerAPI modelplayerapi)
	{
		super(modelplayerapi);
	}

	@Override
	public SmartMovingModel getMovingModel()
	{
		if(model == null)
			model = new SmartMovingModel(SmartRender.getPlayerBase(modelPlayer), this);
		return model;
	}

	public void dynamicOverrideAnimateHeadRotation(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		getMovingModel().animateHeadRotation(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
	}

	public void dynamicOverrideAnimateSleeping(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		getMovingModel().animateSleeping(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
	}

	public void dynamicOverrideAnimateArmSwinging(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		getMovingModel().animateArmSwinging(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
	}

	public void dynamicOverrideAnimateRiding(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		getMovingModel().animateRiding(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
	}

	public void dynamicOverrideAnimateLeftArmItemHolding(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		getMovingModel().animateLeftArmItemHolding(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
	}

	public void dynamicOverrideAnimateRightArmItemHolding(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		getMovingModel().animateRightArmItemHolding(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
	}

	public void dynamicOverrideAnimateWorkingBody(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		getMovingModel().animateWorkingBody(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
	}

	public void dynamicOverrideAnimateWorkingArms(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		getMovingModel().animateWorkingArms(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
	}

	public void dynamicOverrideAnimateSneaking(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		getMovingModel().animateSneaking(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
	}

	public void dynamicOverrideAnimateArms(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		getMovingModel().animateArms(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
	}

	public void dynamicOverrideAnimateBowAiming(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		getMovingModel().animateBowAiming(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
	}

	@Override
	public void superAnimateHeadRotation(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		super.dynamic("animateHeadRotation", new Object[] { totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor });
	}

	@Override
	public void superAnimateSleeping(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		super.dynamic("animateSleeping", new Object[] { totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor });
	}

	@Override
	public void superAnimateArmSwinging(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		super.dynamic("animateArmSwinging", new Object[] { totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor });
	}

	@Override
	public void superAnimateRiding(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		super.dynamic("animateRiding", new Object[] { totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor });
	}

	@Override
	public void superAnimateLeftArmItemHolding(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		super.dynamic("animateLeftArmItemHolding", new Object[] { totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor });
	}

	@Override
	public void superAnimateRightArmItemHolding(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		super.dynamic("animateRightArmItemHolding", new Object[] { totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor });
	}

	@Override
	public void superAnimateWorkingBody(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		super.dynamic("animateWorkingBody", new Object[] { totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor });
	}

	@Override
	public void superAnimateWorkingArms(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		super.dynamic("animateWorkingArms", new Object[] { totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor });
	}

	@Override
	public void superAnimateSneaking(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		super.dynamic("animateSneaking", new Object[] { totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor });
	}

	@Override
	public void superApplyAnimationOffsets(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		super.dynamic("animateArms", new Object[] { totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor });
	}

	@Override
	public void superAnimateBowAiming(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		super.dynamic("animateBowAiming", new Object[] { totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor });
	}

	@Deprecated	public ModelRenderer getOuter() { return getMovingModel().md.bipedOuter; }
	@Deprecated	public ModelRenderer getTorso() { return getMovingModel().md.bipedTorso; }
	@Deprecated	public ModelRenderer getBody() { return getMovingModel().md.bipedBody; }
	@Deprecated	public ModelRenderer getBreast() { return getMovingModel().md.bipedBreast; }
	@Deprecated	public ModelRenderer getNeck() { return getMovingModel().md.bipedNeck; }
	@Deprecated	public ModelRenderer getHead() { return getMovingModel().md.bipedHead; }
	@Deprecated	public ModelRenderer getHeadwear() { return getMovingModel().md.bipedHeadwear; }
	@Deprecated	public ModelRenderer getRightShoulder() { return getMovingModel().md.bipedRightShoulder; }
	@Deprecated	public ModelRenderer getRightArm() { return getMovingModel().md.bipedRightArm; }
	@Deprecated	public ModelRenderer getLeftShoulder() { return getMovingModel().md.bipedLeftShoulder; }
	@Deprecated	public ModelRenderer getLeftArm() { return getMovingModel().md.bipedLeftArm; }
	@Deprecated	public ModelRenderer getPelvic() { return getMovingModel().md.bipedPelvic; }
	@Deprecated	public ModelRenderer getRightLeg() { return getMovingModel().md.bipedRightLeg; }
	@Deprecated	public ModelRenderer getLeftLeg() { return getMovingModel().md.bipedLeftLeg; }
	@Deprecated	public ModelRenderer getEars() { return getMovingModel().md.bipedEars; }
	@Deprecated	public ModelRenderer getCloak() { return getMovingModel().md.bipedCloak; }
}