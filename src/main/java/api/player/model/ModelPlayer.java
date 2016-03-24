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

package api.player.model;

public class ModelPlayer extends net.minecraft.client.model.ModelBiped
{
	public ModelPlayer()
	{
		this(0.0F);
	}

	public ModelPlayer(float paramFloat)
	{
		this(paramFloat, 0.0F, 64, 32);
	}

	public ModelPlayer(float paramFloat1, float paramFloat2, int paramInt1, int paramInt2)
	{
		super(paramFloat1, paramFloat2, paramInt1, paramInt2);
	}
}