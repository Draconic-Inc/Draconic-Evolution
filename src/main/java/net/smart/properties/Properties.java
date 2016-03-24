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

package net.smart.properties;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import net.smart.utilities.*;

public class Properties extends java.util.Properties
{
	private static final long serialVersionUID = 5319578641402091067L;

	private static int i = 0;
	public static final int Boolean = i++;
	public static final int Unmodified = i++;
	public static final int Modified = i++;
	public static final int Float = i++;
	public static final int Positive = i++;
	public static final int Negative = i++;
	public static final int PositiveFactor = i++;
	public static final int NegativeFactor = i++;
	public static final int IncreasingFactor = i++;
	public static final int DecreasingFactor = i++;
	public static final int Integer = i++;
	public static final int String = i++;
	public static final int Strings = i++;
	public static final int Operator = i++;
	public static final int Constant = i++;
	public static final int Key = i++;
	public static final int StringMap = i++;
	public static final int IntegerMap = i++;

	public final String version;

	public Properties()
	{
		version = null;
	}

	public Properties(File file)
	{
		load(file);
		version = getProperty("move.options.version");
	}

	public Properties(String version, File file)
	{
		load(file);
		this.version = version;
	}

	protected List<Property<?>> getProperties()
	{
		return getProperties(null);
	}

	protected List<Property<?>> getProperties(Class<?> type)
	{
		List<Property<?>> properties = new ArrayList<Property<?>>();
		if(type != null)
			return addProperties(properties, type, false);
		return addProperties(properties, getClass(), true);
	}

	private List<Property<?>> addProperties(List<Property<?>> list, Class<?> type, boolean base)
	{
		if(base && type.getSuperclass() != null)
			addProperties(list, type.getSuperclass(), base);

		Field[] fields = type.getDeclaredFields();
		for(int i = 0; i < fields.length; i++)
		{
			fields[i].setAccessible(true);
			Object value = Reflect.GetField(fields[i], this);
			addProperties(list, value);
		}
		return list;
	}

	private void addProperties(List<Property<?>> list, Object value)
	{
		if(value instanceof Property)
			list.add((Property<?>)value);
		else if(value instanceof Collection)
		{
			Iterator<?> iterator = ((Collection<?>)value).iterator();
			while(iterator.hasNext())
				addProperties(list, iterator.next());
		}
	}

	public void write(Properties properties)
	{
		write(properties, null);
	}

	public void write(Properties properties, String key)
	{
		List<Property<?>> propertiesToWrite = getProperties();
		for(int i = 0; i < propertiesToWrite.size(); i++)
		{
			Property<?> property = propertiesToWrite.get(i);
			if(property.isPersistent())
				properties.put(property.getCurrentKey(), key == null ? property.getValueString() : property.getKeyValueString(key));
		}
	}

	private boolean load(File file)
	{
		try
		{
			if(file.exists())
				load(new FileInputStream(file));
			return true;
		}
		catch (Exception e)
		{
		}
		return false;
	}

	public static int getBaseType(int type)
	{
		if(type == Properties.Boolean || type == Properties.Unmodified || type == Properties.Modified)
			return Properties.Boolean;
		if(type == Properties.Float || type == Properties.Positive || type == Properties.Negative || type == Properties.PositiveFactor || type == Properties.NegativeFactor || type == Properties.IncreasingFactor || type == Properties.DecreasingFactor)
			return Properties.Float;
		if(type == Properties.Integer)
			return Properties.Integer;
		if(type == Properties.Strings)
			return Properties.Strings;
		if(type == Properties.StringMap)
			return Properties.StringMap;
		if(type == Properties.IntegerMap)
			return Properties.IntegerMap;
		return Properties.String;
	}

	public static String getBaseTypeName(int baseType)
	{
		if(baseType == Properties.Boolean)
			return "boolean";
		if(baseType == Properties.Float)
			return "floating point";
		if(baseType == Properties.Integer)
			return "integer";
		return "string";
	}

	public static Object getDefaultValue(int type)
	{
		if(type == Boolean)
			return false;
		if(type == Unmodified)
			return true;
		if(type == Modified)
			return false;

		if(type == Integer)
			return 0;

		if(type == Float)
			return 0F;
		if(type == Positive)
			return 0F;
		if(type == Negative)
			return 0F;
		if(type == PositiveFactor)
			return 1F;
		if(type == NegativeFactor)
			return 1F;
		if(type == IncreasingFactor)
			return 1F;
		if(type == DecreasingFactor)
			return 1F;

		if(type == String)
			return "";
		if(type == Strings)
			return new String[0];
		if(type == StringMap)
			return new HashMap<String, String>();
		if(type == IntegerMap)
			return new HashMap<String, Integer>();
		return null;
	}

	public static Object getMinimumValue(int type)
	{
		if(type == Properties.Positive)
			return 0F;
		if(type == Properties.PositiveFactor)
			return 0F;
		if(type == Properties.IncreasingFactor)
			return 1F;
		if(type == Properties.DecreasingFactor)
			return 0F;
		return null;
	}

	public static Object getMaximumValue(int type)
	{
		if(type == Properties.Negative)
			return 0F;
		if(type == Properties.NegativeFactor)
			return 0F;
		if(type == Properties.DecreasingFactor)
			return 1F;
		return null;
	}

	public static Property<Boolean> Unmodified()
	{
		return Property(Unmodified);
	}

	public static Property<Boolean> Unmodified(String key, String... versions)
	{
		return Unmodified().key(key, versions);
	}

	public static Property<Boolean> Modified()
	{
		return Property(Modified);
	}

	public static Property<Boolean> Modified(String key, String... versions)
	{
		return Modified().key(key, versions);
	}

	public static Property<Integer> Integer()
	{
		return Property(Integer);
	}

	public static Property<Integer> Integer(String key, String... versions)
	{
		return Integer().key(key, versions);
	}

	public static Property<Float> Float()
	{
		return Property(Float);
	}

	public static Property<Float> Float(String key, String... versions)
	{
		return Float().key(key, versions);
	}

	public static Property<Float> Positive()
	{
		return Property(Positive);
	}

	public static Property<Float> Positive(String key, String... versions)
	{
		return Positive().key(key, versions);
	}

	public static Property<Float> Negative()
	{
		return Property(Negative);
	}

	public static Property<Float> Negative(String key, String... versions)
	{
		return Negative().key(key, versions);
	}

	public static Property<Float> PositiveFactor()
	{
		return Property(PositiveFactor);
	}

	public static Property<Float> PositiveFactor(String key, String... versions)
	{
		return PositiveFactor().key(key, versions);
	}

	public static Property<Float> NegativeFactor()
	{
		return Property(NegativeFactor);
	}

	public static Property<Float> NegativeFactor(String key, String... versions)
	{
		return NegativeFactor().key(key, versions);
	}

	public static Property<Float> IncreasingFactor()
	{
		return Property(IncreasingFactor);
	}

	public static Property<Float> IncreasingFactor(String key, String... versions)
	{
		return IncreasingFactor().key(key, versions);
	}

	public static Property<Float> DecreasingFactor()
	{
		return Property(DecreasingFactor);
	}

	public static Property<Float> DecreasingFactor(String key, String... versions)
	{
		return DecreasingFactor().key(key, versions);
	}

	public static Property<String> String()
	{
		return Property(String);
	}

	public static Property<String> String(String key, String... versions)
	{
		return String().key(key, versions);
	}

	public static Property<String[]> Strings()
	{
		return Property(Strings);
	}

	public static Property<String[]> Strings(String key, String... versions)
	{
		return Strings().key(key, versions);
	}

	public static Property<Map<String,String>> StringMap()
	{
		return Property(StringMap);
	}

	public static Property<Map<String,String>> StringMap(String key, String... versions)
	{
		return StringMap().key(key, versions);
	}

	public static Property<Map<String,Integer>> IntegerMap()
	{
		return Property(IntegerMap);
	}

	public static Property<Map<String,Integer>> IntegerMap(String key, String... versions)
	{
		return IntegerMap().key(key, versions);
	}

	private static Property Property(int type)
	{
		return new Property(type);
	}

	public static Value<Boolean> Value(Boolean base)
	{
		return new Value<Boolean>(Boolean).put(base);
	}

	public static Value<Float> Value(Float base)
	{
		return new Value<Float>(Float).put(base);
	}

	public static Value<String> Value(String base)
	{
		return new Value<String>(String).put(base);
	}

	protected static String[] concat(String left, String[] right)
	{
		return concat(new String[] { left }, right);
	}

	protected static String[] concat(String[] left, String[] right)
	{
		String[] result = new String[left.length + right.length];
		int i = 0;
		for(; i < left.length; i++)
			result[i] = left[i];
		for(; i < result.length; i++)
			result[i] = right[i - left.length];
		return result;
	}
}