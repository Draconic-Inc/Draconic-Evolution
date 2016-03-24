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

import java.util.*;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;

import net.minecraft.entity.*;
import net.minecraft.util.*;

public interface IEntityPlayerMP extends IPacketSender
{
	void sendPacketToTrackedPlayers(FMLProxyPacket packet);

	String getUsername();

	void resetFallDistance();

	void resetTicksForFloatKick();

	void setHeight(float height);

	double getMinY();

	float getHeight();

	void setMaxY(double maxY);

	boolean localIsEntityInsideOpaqueBlock();

	SmartMovingServer getMoving();

	IEntityPlayerMP[] getAllPlayers();

	float doGetHealth();

	AxisAlignedBB getBox();

	AxisAlignedBB expandBox(AxisAlignedBB box, double x, double y, double z);

	List<?> getEntitiesExcludingPlayer(AxisAlignedBB box);

	boolean isDeadEntity(Entity entity);

	void onCollideWithPlayer(Entity entity);

	void localAddExhaustion(float exhaustion);

	void localAddMovementStat(double x, double y, double z);

	void localPlaySound(String soundId, float volume, float pitch);

	boolean localIsSneaking();
}