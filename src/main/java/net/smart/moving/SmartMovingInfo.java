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

import cpw.mods.fml.common.*;

public class SmartMovingInfo
{
	public static final byte StatePacketId = 0;
	public static final byte ConfigInfoPacketId = 1;
	public static final byte ConfigContentPacketId = 2;
	public static final byte ConfigChangePacketId = 3;
	public static final byte SpeedChangePacketId = 4;
	public static final byte HungerChangePacketId = 5;
	public static final byte SoundPacketId = 6;

	private static final Mod Mod = SmartMovingMod.class.getAnnotation(Mod.class);

	public static final String ModId = Mod.modid();
	public static final String ModName = Mod.name();
	public static final String ModVersion = Mod.version();
	public static final String ModComVersion = SmartMovingMod.ModComVersion;

	public static final String ModComMessage = ModName + " uses communication protocol " + ModComVersion;
	public static final String ModComId = ModName.replace(" ", "") + " " + ModComVersion;

	public static final int DefaultChatId = 0;
	public static final int ConfigChatId = ModName.hashCode();
	public static final int SpeedChatId = ModName.hashCode() + 1;
}