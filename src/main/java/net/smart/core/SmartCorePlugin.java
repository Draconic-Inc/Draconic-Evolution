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

import cpw.mods.fml.relauncher.*;

@IFMLLoadingPlugin.MCVersion("1.7.10")
public class SmartCorePlugin implements IFMLLoadingPlugin
{
	public static String Version = "1.0.3";

	public static boolean isObfuscated;

	@Override
	public String[] getASMTransformerClass()
	{
		return new String[] { "net.smart.core.SmartCoreTransformer" };
	}

	@Override
	public String getAccessTransformerClass()
	{
		return null;
	}

	@Override
	public String getModContainerClass()
	{
		return "net.smart.core.SmartCoreContainer";
	}

	@Override
	public String getSetupClass()
	{
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data)
	{
		isObfuscated = (Boolean)data.get("runtimeDeobfuscationEnabled");
	}
}