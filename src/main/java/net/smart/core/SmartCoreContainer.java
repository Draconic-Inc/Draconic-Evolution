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
import com.google.common.eventbus.*;
import cpw.mods.fml.common.*;

public class SmartCoreContainer extends DummyModContainer
{
	public SmartCoreContainer()
	{
		super(createMetadata());
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller)
	{
		return true;
	}

	private static ModMetadata createMetadata()
	{
		ModMetadata meta = new ModMetadata();

		meta.modId = "SmartCore";
		meta.name = SmartCoreInfo.ModName;
		meta.version = SmartCoreInfo.ModVersion;
		meta.description = "Adds some core hooks required by Smart Moving";
		meta.url = "http://www.minecraftforum.net/topic/738498-";
		meta.authorList = Arrays.asList(new String[] { "Divisor" });

		return meta;
	}
}