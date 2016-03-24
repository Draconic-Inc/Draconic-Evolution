// ==================================================================
// This file is part of Render Player API.
// 
// Render Player API is free software: you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public License
// as published by the Free Software Foundation, either version 3 of
// the License, or (at your option) any later version.
// 
// Render Player API is distributed in the hope that it will be
// useful, but WITHOUT ANY WARRANTY; without even the implied
// warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
// See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public
// License and the GNU General Public License along with Render
// Player API. If not, see <http://www.gnu.org/licenses/>.
// ==================================================================

package api.player.forge;

import java.util.*;

import cpw.mods.fml.relauncher.*;

@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.TransformerExclusions("api.player.forge")
public class RenderPlayerAPIPlugin implements IFMLLoadingPlugin
{
	public static String Version = "1.4";

	public static boolean isObfuscated;

	public String[] getASMTransformerClass()
	{
		return new String[] { "api.player.forge.RenderPlayerAPITransformer" };
	}

	public String getModContainerClass()
	{
		return "api.player.forge.RenderPlayerAPIContainer";
	}

	public String getSetupClass()
	{
		return null;
	}

	public void injectData(Map<String, Object> data)
	{
		isObfuscated = (Boolean)data.get("runtimeDeobfuscationEnabled");
	}

	public String getAccessTransformerClass()
	{
		return null;
	}
}