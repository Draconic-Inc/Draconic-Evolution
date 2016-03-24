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

package net.smart.core;

import java.util.*;

import net.minecraft.launchwrapper.*;

public class SmartCoreTransformer implements IClassTransformer
{
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes)
	{
		List<SmartCoreTransformation> list = null;
		for(SmartCoreTransformation transformation : _transformations)
			if(transformedName.equals(transformation.className))
				(list == null ? (list = new ArrayList<SmartCoreTransformation>()) : list).add(transformation);

		if(list != null)
			return SmartCoreClassVisitor.transform(bytes, list);
		return bytes;
	}

	private static final SmartCoreTransformation[] _transformations = new SmartCoreTransformation[]
	{
		new SmartCoreTransformation
		(
			"net.minecraft.network.NetHandlerPlayServer",
			Name("processPlayer", "a"),
			new String[]
			{
				Name("net.minecraft.network.play.client.C03PacketPlayer", "jd")
			},
			null,
			"net.smart.core.SmartCoreEventHandler",
			"NetHandlerPlayServer_beforeProcessPlayer",
			"NetHandlerPlayServer_afterProcessPlayer"
		),
		new SmartCoreTransformation
		(
			"net.minecraft.network.NetHandlerPlayServer",
			Name("processPlayerBlockPlacement", "a"),
			new String[]
			{
				Name("net.minecraft.network.play.client.C08PacketPlayerBlockPlacement", "jo")
			},
			null,
			"net.smart.core.SmartCoreEventHandler",
			"NetHandlerPlayServer_beforeProcessPlayerBlockPlacement",
			"NetHandlerPlayServer_afterProcessPlayerBlockPlacement"
		),
		new SmartCoreTransformation
		(
			Name("net.minecraft.client.multiplayer.PlayerControllerMP", "bje"),
			Name("onPlayerRightClick", "a"),
			new String[]
			{
				Name("net.minecraft.entity.player.EntityPlayer", "yz"),
				Name("net.minecraft.world.World", "ahb"),
				Name("net.minecraft.item.ItemStack", "add"),
				"int",
				"int",
				"int",
				"int",
				Name("net.minecraft.util.Vec3", "azw")
			},
			"boolean",
			"net.smart.core.SmartCoreEventHandler",
			"PlayerControllerMP_beforeOnPlayerRightClick",
			"PlayerControllerMP_afterOnPlayerRightClick"
		),
		new SmartCoreTransformation
		(
			Name("net.minecraft.server.management.ItemInWorldManager", "qx"),
			Name("activateBlockOrUseItem", "a"),
			new String[]
			{
				Name("net.minecraft.entity.player.EntityPlayer", "yz"),
				Name("net.minecraft.world.World", "ahb"),
				Name("net.minecraft.item.ItemStack", "add"),
				"int",
				"int",
				"int",
				"int",
				"float",
				"float",
				"float"
			},
			"boolean",
			"net.smart.core.SmartCoreEventHandler",
			"ItemInWorldManager_beforeActivateBlockOrUseItem",
			"ItemInWorldManager_afterActivateBlockOrUseItem"
		)
	};

	private static String Name(String deobfuscated, String obfuscated)
	{
		return SmartCorePlugin.isObfuscated ? obfuscated : deobfuscated;
	}
}