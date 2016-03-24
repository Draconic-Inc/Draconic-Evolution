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

import api.player.model.*;
import api.player.render.*;

import net.smart.moving.*;
import net.smart.render.playerapi.*;

public abstract class SmartMoving
{
	public static final String ID = SmartMovingInfo.ModName;

	public static void register()
	{
		String[] inferiors = new String[] { SmartRender.ID };

		RenderPlayerBaseSorting renderSorting = new RenderPlayerBaseSorting();
		renderSorting.setAfterLocalConstructingInferiors(inferiors);
		renderSorting.setOverrideRenderPlayerInferiors(inferiors);
		renderSorting.setOverrideRotatePlayerInferiors(inferiors);
		renderSorting.setOverrideRenderPlayerSleepInferiors(inferiors);
		RenderPlayerAPI.register(ID, SmartMovingRenderPlayerBase.class, renderSorting);

		ModelPlayerBaseSorting modelSorting = new ModelPlayerBaseSorting();
		modelSorting.setAfterLocalConstructingInferiors(inferiors);
		ModelPlayerAPI.register(ID, SmartMovingModelPlayerBase.class, modelSorting);
	}

	public static SmartMovingRenderPlayerBase getPlayerBase(net.minecraft.client.renderer.entity.RenderPlayer renderPlayer)
	{
		return (SmartMovingRenderPlayerBase)((IRenderPlayerAPI)renderPlayer).getRenderPlayerBase(ID);
	}

	public static SmartMovingModelPlayerBase getPlayerBase(api.player.model.ModelPlayer modelPlayer)
	{
		return (SmartMovingModelPlayerBase)((IModelPlayerAPI)modelPlayer).getModelPlayerBase(ID);
	}
}