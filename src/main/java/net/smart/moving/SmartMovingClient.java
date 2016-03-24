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

public class SmartMovingClient extends SmartMovingContext implements ISmartMovingClient
{
	private final Map<String, Float> maximumExhaustionValues = new HashMap<String, Float>();
	private boolean nativeUserInterfaceDrawing = true;

	@Override
	public float getMaximumExhaustion()
	{
		float maxExhaustion = Config.getMaxExhaustion();
		if(maximumExhaustionValues.size() > 0)
		{
			Iterator<Float> iterator = maximumExhaustionValues.values().iterator();
			while(iterator.hasNext())
				maxExhaustion = Math.max(iterator.next(), maxExhaustion);
		}
		return maxExhaustion;
	}

	@Override
	public float getMaximumUpJumpCharge()
	{
		return Config._jumpChargeMaximum.value;
	}

	@Override
	public float getMaximumHeadJumpCharge()
	{
		return Config._headJumpChargeMaximum.value;
	}

	@Override
	public void setMaximumExhaustionValue(String key, float value)
	{
		maximumExhaustionValues.put(key, value);
	}

	@Override
	public float getMaximumExhaustionValue(String key)
	{
		return maximumExhaustionValues.get(key);
	}

	@Override
	public boolean removeMaximumExhaustionValue(String key)
	{
		return maximumExhaustionValues.remove(key) != null;
	}

	@Override
	public void setNativeUserInterfaceDrawing(boolean value)
	{
		nativeUserInterfaceDrawing = value;
	}

	@Override
	public boolean getNativeUserInterfaceDrawing()
	{
		return nativeUserInterfaceDrawing;
	}
}