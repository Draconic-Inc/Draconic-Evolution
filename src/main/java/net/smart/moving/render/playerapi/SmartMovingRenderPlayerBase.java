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

import net.minecraft.client.entity.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.*;

import api.player.render.*;

import net.smart.moving.render.*;
import net.smart.moving.render.IRenderPlayer;

public class SmartMovingRenderPlayerBase extends RenderPlayerBase implements IRenderPlayer
{
	public SmartMovingRenderPlayerBase(RenderPlayerAPI renderPlayerAPI)
	{
		super(renderPlayerAPI);
	}

	public SmartMovingRender getRenderModel()
	{
		if(render == null)
			render = new SmartMovingRender(this);
		return render;
	}

	@Override
	public void renderPlayer(AbstractClientPlayer entityplayer, double d, double d1, double d2, float f, float renderPartialTicks)
	{
		getRenderModel().renderPlayer(entityplayer, d, d1, d2, f, renderPartialTicks);
	}

	@Override
	public void superRenderRenderPlayer(AbstractClientPlayer entityplayer, double d, double d1, double d2, float f, float renderPartialTicks)
	{
		super.renderPlayer(entityplayer, d, d1, d2, f, renderPartialTicks);
	}

	@Override
	public void rotatePlayer(AbstractClientPlayer entityplayer, float totalTime, float actualRotation, float f2)
	{
		getRenderModel().rotatePlayer(entityplayer, totalTime, actualRotation, f2);
	}

	@Override
	public void superRenderRotatePlayer(AbstractClientPlayer entityplayer, float totalTime, float actualRotation, float f2)
	{
		super.rotatePlayer(entityplayer, totalTime, actualRotation, f2);
	}

	@Override
	public void renderPlayerSleep(AbstractClientPlayer entityplayer, double d, double d1, double d2)
	{
		getRenderModel().renderPlayerAt(entityplayer, d, d1, d2);
	}

	@Override
	public void superRenderRenderPlayerAt(AbstractClientPlayer entityplayer, double d, double d1, double d2)
	{
		super.renderPlayerSleep(entityplayer, d, d1, d2);
	}

	@Override
	public void passSpecialRender(EntityLivingBase entityliving, double d, double d1, double d2)
	{
		getRenderModel().renderName(entityliving, d, d1, d2);
	}

	@Override
	public void superRenderRenderName(EntityLivingBase entityplayer, double d, double d1, double d2)
	{
		super.passSpecialRender(entityplayer, d, d1, d2);
	}

	@Override
	public RenderManager getRenderManager()
	{
		return renderPlayerAPI.getRenderManagerField();
	}

	public boolean isRenderedWithBodyTopAlwaysInAccelerateDirection()
	{
		SmartMovingRender render = getRenderModel();
		return render.modelBipedMain.isFlying || render.modelBipedMain.isSwim || render.modelBipedMain.isDive || render.modelBipedMain.isHeadJump;
	}

	@Override
	public IModelPlayer getPlayerModelArmor()
	{
		return SmartMoving.getPlayerBase((api.player.model.ModelPlayer)renderPlayerAPI.getModelArmorField());
	}

	@Override
	public IModelPlayer getPlayerModelArmorChestplate()
	{
		return SmartMoving.getPlayerBase((api.player.model.ModelPlayer)renderPlayerAPI.getModelArmorChestplateField());
	}

	@Override
	public IModelPlayer getPlayerModelBipedMain()
	{
		return SmartMoving.getPlayerBase((api.player.model.ModelPlayer)renderPlayerAPI.getModelBipedMainField());
	}

	@Override
	public IModelPlayer[] getPlayerModels()
	{
		api.player.model.ModelPlayer[] modelPlayers = api.player.model.ModelPlayerAPI.getAllInstances();
		if(allModelPlayers != null && (allModelPlayers == modelPlayers || modelPlayers.length == 0 && allModelPlayers.length == 0))
			return allIModelPlayers;

		allModelPlayers = modelPlayers;
		allIModelPlayers = new IModelPlayer[modelPlayers.length];
		for(int i=0; i<allIModelPlayers.length; i++)
			allIModelPlayers[i] = SmartMoving.getPlayerBase(allModelPlayers[i]);
		return allIModelPlayers;
	}

	private api.player.model.ModelPlayer[] allModelPlayers;
	private IModelPlayer[] allIModelPlayers;

	private SmartMovingRender render;
}