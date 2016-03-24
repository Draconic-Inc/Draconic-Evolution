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

import net.minecraft.client.entity.*;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.*;

public class RenderPlayer extends net.smart.render.RenderPlayer implements IRenderPlayer
{
	public RenderPlayer()
	{
		render = new SmartMovingRender(this);
	}

	@Override
	public net.smart.render.IModelPlayer createModel(ModelBiped existing, float f)
	{
		return new ModelPlayer(f);
	}

	@Override
	public void doRender(AbstractClientPlayer entityplayer, double d, double d1, double d2, float f, float renderPartialTicks)
	{
		render.renderPlayer(entityplayer, d, d1, d2, f, renderPartialTicks);
	}

	@Override
	public void superRenderRenderPlayer(AbstractClientPlayer entityplayer, double d, double d1, double d2, float f, float renderPartialTicks)
	{
		super.doRender(entityplayer, d, d1, d2, f, renderPartialTicks);
	}

	@Override
	protected void rotateCorpse(AbstractClientPlayer entityplayer, float totalTime, float actualRotation, float f2)
	{
		render.rotatePlayer(entityplayer, totalTime, actualRotation, f2);
	}

	@Override
	public void superRenderRotatePlayer(AbstractClientPlayer entityplayer, float totalTime, float actualRotation, float f2)
	{
		super.rotateCorpse(entityplayer, totalTime, actualRotation, f2);
	}

	@Override
	protected void renderLivingAt(AbstractClientPlayer entityplayer, double d, double d1, double d2)
	{
		render.renderPlayerAt(entityplayer, d, d1, d2);
	}

	@Override
	public void superRenderRenderPlayerAt(AbstractClientPlayer entityplayer, double d, double d1, double d2)
	{
		super.renderLivingAt(entityplayer, d, d1, d2);
	}

	@Override
	protected void passSpecialRender(EntityLivingBase par1EntityLiving, double par2, double par4, double par6)
	{
		render.renderName(par1EntityLiving, par2, par4, par6);
	}

	@Override
	public void superRenderRenderName(EntityLivingBase par1EntityLiving, double par2, double par4, double par6)
	{
		super.passSpecialRender(par1EntityLiving, par2, par4, par6);
	}

	@Override
	public RenderManager getRenderManager()
	{
		return renderManager;
	}

	@Override
	public IModelPlayer getPlayerModelBipedMain()
	{
		return (ModelPlayer)super.getModelBipedMain();
	}

	@Override
	public IModelPlayer getPlayerModelArmorChestplate()
	{
		return (ModelPlayer)super.getModelArmorChestplate();
	}

	@Override
	public IModelPlayer getPlayerModelArmor()
	{
		return (ModelPlayer)super.getModelArmor();
	}

	@Override
	public IModelPlayer[] getPlayerModels()
	{
		if(allIModelPlayers == null)
			allIModelPlayers = new IModelPlayer[] { getPlayerModelBipedMain(), getPlayerModelArmorChestplate(), getPlayerModelArmor() };
		return allIModelPlayers;
	}

	private IModelPlayer[] allIModelPlayers;

	private final SmartMovingRender render;
}