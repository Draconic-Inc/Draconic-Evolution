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

import net.smart.utilities.*;

public class SmartMovingInstall
{
	public final static Name RopesPlusCore = new Name("atomicstryker.ropesplus.common.RopesPlusCore");
	public final static Name ModBlockFence = new Name("net.minecraft.src.modBlockFence", "modBlockFence");
	public final static Name MacroModCore = new Name("net.eq2online.macros.core.MacroModCore");
	public final static Name BlockSturdyLadder = new Name("mods.chupmacabre.ladderKit.sturdyLadders.BlockSturdyLadder");
	public final static Name BlockRopeLadder = new Name("mods.chupmacabre.ladderKit.ropeLadders.BlockRopeLadder");

	public final static Name RopesPlusClient = new Name("atomicstryker.ropesplus.client.RopesPlusClient");
	public final static Name RopesPlusClient_onZipLine = new Name("onZipLine");

	public final static Name CarpentersBlockLadder = new Name("com.carpentersblocks.block.BlockCarpentersLadder");
	public final static Name CarpentersTEBaseBlock = new Name("com.carpentersblocks.tileentity.TEBase");
	public final static Name CarpentersTEBaseBlock_getData = new Name("getData");

	public final static Name NetServerHandler_ticksForFloatKick = new Name("floatingTickCount", "field_147365_f", "f");
	public final static Name GuiNewChat_chatMessageList = new Name("chatLines", "field_146252_h", "h");
	public final static Name PlayerControllerMP_currentGameType = new Name("currentGameType", "field_78779_k", "k");
	public final static Name ModifiableAttributeInstance_attributeValue = new Name("cachedValue", "field_111139_h", "h");
}