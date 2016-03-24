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

package net.smart.moving.config;

import java.util.Iterator;

import net.smart.properties.*;

public class SmartMovingServerConfig extends SmartMovingClientConfig
{
	private Properties properties = new Properties();
	private Properties topProperties = new Properties();

	public void loadFromProperties(String[] propertyArray, boolean top)
	{
		for(int i = 0; i < propertyArray.length - 1; i += 2)
		{
			String key = propertyArray[i];
			String value = propertyArray[i + 1];
			properties.put(key, value);
			if(top)
				topProperties.put(key, value);
		}

		load(top);
	}

	public void load(boolean top)
	{
		if(!top && !topProperties.isEmpty())
		{
			Iterator<?> iterator = topProperties.keySet().iterator();
			while(iterator.hasNext())
			{
				Object topKey = iterator.next();
				properties.put(topKey, topProperties.get(topKey));
			}
		}
		super.loadFromProperties(properties);
	}

	public void reset()
	{
		properties.clear();
		topProperties.clear();
	}
}