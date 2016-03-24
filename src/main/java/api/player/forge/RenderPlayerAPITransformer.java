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

import net.minecraft.launchwrapper.*;

import api.player.model.*;
import api.player.render.*;

public class RenderPlayerAPITransformer implements IClassTransformer
{
	public byte[] transform(String name, String transformedName, byte[] bytes)
	{
		if(transformedName.equals(RenderPlayerClassVisitor.targetClassName))
		{
			Stack<String> models = new Stack<String>();
			models.push(ModelPlayerClassVisitor.deobfuscatedClassReference + ":armor");
			models.push(ModelPlayerClassVisitor.deobfuscatedClassReference + ":chestplate");
			models.push(ModelPlayerClassVisitor.deobfuscatedClassReference + ":main");

			Map<String, Stack<String>> renderConstructorReplacements = new Hashtable<String, Stack<String>>();
			renderConstructorReplacements.put(ModelPlayerClassVisitor.obfuscatedSuperClassReference, models);
			renderConstructorReplacements.put(ModelPlayerClassVisitor.deobfuscateSuperClassReference, models);

			return RenderPlayerClassVisitor.transform(bytes, RenderPlayerAPIPlugin.isObfuscated, renderConstructorReplacements);
		}
		else if(transformedName.equals(ModelPlayerClassVisitor.targetClassName))
			return ModelPlayerClassVisitor.transform(bytes, RenderPlayerAPIPlugin.isObfuscated);
		else
			return bytes;
	}
}