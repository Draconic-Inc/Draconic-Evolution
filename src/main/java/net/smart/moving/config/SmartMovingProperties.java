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

import java.io.*;
import java.util.*;

import net.smart.properties.*;
import net.smart.properties.Properties;

public abstract class SmartMovingProperties extends Properties
{
	public final static String Enabled = "enabled";
	public final static String Disabled = "disabled";
	private final static String[] _defaultKeys = new String[1];

	private int toggler = -2;
	private String[] keys = _defaultKeys;

	public boolean enabled;

	protected void load(Properties... propertiesList) throws Exception
	{
		List<Property<?>> propertiesToLoad = getProperties();
		if(toggler != -2)
		{
			Iterator<Property<?>> iterator = propertiesToLoad.iterator();
			while(iterator.hasNext())
				iterator.next().reset();
		}

		while (propertiesToLoad.size() > 0)
		{
			Iterator<Property<?>> iterator = propertiesToLoad.iterator();
			while(iterator.hasNext())
				if(iterator.next().load(propertiesList))
					iterator.remove();
		}

		toggler = 0;
		update();
	}

	protected void save(File file, String version, boolean header, boolean comments) throws Exception
	{
		List<Property<?>> propertiesToSave = getProperties();

		FileOutputStream stream = new FileOutputStream(file);
		PrintWriter printer = new PrintWriter(stream);

		if(header)
			printHeader(printer);

		if(version != null)
			printVersion(printer, version, comments);

		for(int i = 0; i < propertiesToSave.size(); i++)
			if(propertiesToSave.get(i).print(printer, keys, version, comments) && i < propertiesToSave.size() - 1)
				printer.println();

		printer.close();
	}

	protected abstract void printVersion(PrintWriter printer, String version, boolean comments);

	protected abstract void printHeader(PrintWriter printer);

	public void toggle()
	{
		int length = keys == null ? 0 : keys.length;
		toggler++;
		if(toggler == length)
			toggler = -1;
		update();
	}

	public void setKeys(String[] keys)
	{
		if(keys == null || keys.length == 0)
			keys = _defaultKeys;
		this.keys = keys;
		toggler = 0;
		update();
	}

	public String getKey(int index)
	{
		if(keys[index] == null)
			return Enabled;
		return keys[index];
	}

	public String getNextKey(String key)
	{
		if(key == null || key.equals("disabled"))
			return getKey(0);
		int index;
		for(index = 0; index < keys.length; index++)
			if(key.equals(keys[index]))
				break;
		index++;
		if(index < keys.length)
			return keys[index];
		return Disabled;
	}

	public void setCurrentKey(String key)
	{
		if(key == null || key.equals(Disabled))
			toggler = -1;
		else if(keys.length == 1 && keys[0] == null && key.equals(Enabled))
			toggler = 0;
		else
		{
			for(toggler = 0; toggler < keys.length; toggler++)
				if(key.equals(keys[toggler]))
					break;

			if(toggler == keys.length)
				toggler = -1;
		}
		update();
	}

	public String getCurrentKey()
	{
		if(toggler == -1)
			return Disabled;
		return keys[toggler];
	}

	public boolean hasKey(String key)
	{
		if(Enabled.equals(key))
			return keys[0] == null;
		if(Disabled.equals(key))
			return true;

		for(int i = 0; i < keys.length; i++)
			if(key == null && keys[i] == null || key != null && key.equals(keys[i]))
				return true;
		return false;
	}

	public int getKeyCount()
	{
		return keys.length;
	}

	protected void update()
	{
		List<Property<?>> properties = getProperties();
		Iterator<Property<?>> iterator = properties.iterator();

		String currentKey = getCurrentKey();
		while(iterator.hasNext())
			iterator.next().update(currentKey);
		enabled = toggler != -1;
	}
}