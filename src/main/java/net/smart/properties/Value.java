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

public class Value<T>
{
	private int type;

	private T value;
	private Dictionary<String, T> keyValues;
	private List<String> unparsableStrings;

	public Value(int type)
	{
		this.type = type;
	}

	public Value(T value)
	{
		this.value = value;
	}

	public Value(Value<T> value)
	{
		type = value.type;
		this.value = value.value;
		if(value.keyValues != null)
		{
			keyValues = new Hashtable<String, T>();
			Enumeration<String> keys = value.keyValues.keys();
			while(keys.hasMoreElements())
			{
				String key = keys.nextElement();
				keyValues.put(key, value.keyValues.get(key));
			}
		}
	}

	public Value<T> put(T value)
	{
		return put(null, value);
	}

	public T get(String key)
	{
		if(key != null && keyValues != null)
		{
			T value = keyValues.get(key);
			if(value != null)
				return value;
		}
		return value;
	}

	public T getStored(String key)
	{
		if(key == null || key == Null)
			return value;
		if(keyValues != null)
			return keyValues.get(key);
		return null;
	}

	private T get(String key, Value<T> defaultValue)
	{
		T result = get(key);
		if(result == null)
		{
			result = value;
			if(result == null)
				result = defaultValue.value;
		}
		return result;
	}

	public Value<T> put(String key, T value)
	{
		if(key == null || key == Null || key.isEmpty())
			this.value = value;
		else
		{
			if(keyValues == null)
				keyValues = new Hashtable<String, T>();
			keyValues.put(key, value);
		}
		return this;
	}

	public void withDependency(Value<T> dependency, Value<T> defaultValue)
	{
		Iterator<String> iterator = GetAllKeys(this, dependency);
		while(iterator.hasNext())
		{
			String key = iterator.next();
			if(get(key, defaultValue).equals(true) && dependency.get(key).equals(false))
				put(key, (T)(Object)false);
		}
	}

	public void withMinimum(Value<T> minimum, Value<T> defaultValue)
	{
		Iterator<String> iterator = GetAllKeys(this, minimum);
		while(iterator.hasNext())
		{
			String key = iterator.next();
			put(key, (T)(Object)Math.max((Float)get(key, defaultValue), (Float)minimum.get(key)));
		}
	}

	public void withMaximum(Value<T> maximum, Value<T> defaultValue)
	{
		Iterator<String> iterator = GetAllKeys(this, maximum);
		while(iterator.hasNext())
		{
			String key = iterator.next();
			put(key, (T)(Object)Math.min((Float)get(key, defaultValue), (Float)maximum.get(key)));
		}
	}

	public Value<Boolean> is(Value<T> right)
	{
		Value<Boolean> result = new Value<Boolean>(Properties.Boolean);
		Iterator<String> iterator = GetAllKeys(this, right);
		while(iterator.hasNext())
		{
			String key = iterator.next();
			T leftValue = get(key);
			T rightValue = right.get(key);
			result.put(key, leftValue == null && rightValue == null || leftValue != null && rightValue != null && leftValue.equals(rightValue));
		}
		return result;
	}

	public Value<Boolean> and(Value<T> right)
	{
		Value<Boolean> result = new Value<Boolean>(Properties.Boolean);
		Iterator<String> iterator = GetAllKeys(this, right);
		while(iterator.hasNext())
		{
			String key = iterator.next();
			result.put(key, ((Boolean)get(key)) && ((Boolean)right.get(key)));
		}
		return result;
	}

	public Value<Boolean> or(Value<T> right)
	{
		Value<Boolean> result = new Value<Boolean>(Properties.Boolean);
		Iterator<String> iterator = GetAllKeys(this, right);
		while(iterator.hasNext())
		{
			String key = iterator.next();
			result.put(key, ((Boolean)get(key)) || ((Boolean)right.get(key)));
		}
		return result;
	}

	public Value<Boolean> not()
	{
		Value<Boolean> result = new Value<Boolean>(Properties.Boolean);
		Iterator<String> iterator = GetAllKeys(this);
		while(iterator.hasNext())
		{
			String key = iterator.next();
			result.put(key, !((Boolean)get(key)));
		}
		return result;
	}

	public Value<Float> plus(Value<T> right)
	{
		Value<Float> result = new Value<Float>(Properties.Float);
		Iterator<String> iterator = GetAllKeys(this, right);
		while(iterator.hasNext())
		{
			String key = iterator.next();
			result.put(key, ((Float)get(key)) + ((Float)right.get(key)));
		}
		return result;
	}

	public Value<T> eitherOr(Value<T> left, Value<T> right)
	{
		Value<T> result = new Value<T>(Properties.getBaseType(left.type));
		Iterator<String> iterator = GetAllKeys(this, left, right);
		while(iterator.hasNext())
		{
			String key = iterator.next();
			result.put(key, ((Boolean)get(key)) ? left.get(key) : right.get(key));
		}
		return result;
	}

	public Value<Float> maximum(Value<T> right)
	{
		Value<Float> result = new Value<Float>(Properties.Float);
		Iterator<String> iterator = GetAllKeys(this, right);
		while(iterator.hasNext())
		{
			String key = iterator.next();
			result.put(key, Math.max((Float)get(key), (Float)right.get(key)));
		}
		return result;
	}

	public Value<Float> minimum(Value<T> right)
	{
		Value<Float> result = new Value<Float>(Properties.Float);
		Iterator<String> iterator = GetAllKeys(this, right);
		while(iterator.hasNext())
		{
			String key = iterator.next();
			result.put(key, Math.min((Float)get(key), (Float)right.get(key)));
		}
		return result;
	}

	public Value<String> toKeyName()
	{
		Value<String> result = new Value<String>(Properties.String);
		Iterator<String> iterator = GetAllKeys(this);
		while(iterator.hasNext())
		{
			String key = iterator.next();
			result.put(key, toKeyName((Integer)get(key)));
		}
		return result;
	}

	public Value<Integer> toKeyCode()
	{
		Value<Integer> result = new Value<Integer>(Properties.Integer);
		Iterator<String> iterator = GetAllKeys(this);
		while(iterator.hasNext())
		{
			String key = iterator.next();
			result.put(key, toKeyCode((String)get(key)));
		}
		return result;
	}

	public Value<Dictionary<Object, Set<Integer>>> toBlockConfig()
	{
		Value<Dictionary<Object, Set<Integer>>> result = new Value<Dictionary<Object, Set<Integer>>>(Properties.Strings);
		Iterator<String> iterator = GetAllKeys(this);
		while(iterator.hasNext())
		{
			String key = iterator.next();
			result.put(key, toBlockConfig((String[])get(key)));
		}
		return result;
	}

	@Override
	public Value<T> clone()
	{
		return new Value<T>(this);
	}

	public static Iterator<String> GetAllKeys(Value<?>... values)
	{
		return GetAllKeys(null, values);
	}

	public static Iterator<String> GetAllKeys(String[] sorted, Value<?>... values)
	{
		_allkeys.clear();
		for(int i = 0; i < values.length; i++)
		{
			Value<?> value = values[i];
			if(value.keyValues != null)
			{
				Enumeration<String> keys = value.keyValues.keys();
				while(keys.hasMoreElements())
					_allkeys.add(keys.nextElement());
			}
		}
		Collections.sort(_allkeys);
		_allkeys.add(0, Null);

		for(int i = 0, n = 1; sorted != null && i < sorted.length; i++)
		{
			String sort = sorted[i];
			if(sort == null || sort == Null)
				continue;

			if(_allkeys.remove(sort))
				_allkeys.add(n++, sort);
		}

		return _allkeys.iterator();
	}

	@Override
	public boolean equals(Object other)
	{
		if(!(other instanceof Value<?>))
			return false;

		Value<T> otherValue = (Value<T>)other;
		if((value == null) != (otherValue.value == null))
			return false;
		if(value != null && !valuesEqual(value, otherValue.value))
			return false;
		if((keyValues == null ? 0 : keyValues.size()) != (otherValue.keyValues == null ? 0 : otherValue.keyValues.size()))
			return false;
		if(keyValues != null && keyValues.size() != 0)
		{
			Enumeration<String> keys = keyValues.keys();
			while(keys.hasMoreElements())
			{
				String key = keys.nextElement();
				T otherKeyValue = otherValue.keyValues.get(key);
				if(otherKeyValue == null)
					return false;
				T keyValue = keyValues.get(key);
				if(!valuesEqual(keyValue, otherKeyValue))
					return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		return value == null ? 0 : value.hashCode();
	}

	private boolean valuesEqual(T first, T second)
	{
		if(!(first instanceof Object[]))
			return first.equals(second);

		Object[] firstArray = (Object[])first;
		Object[] secondArray = (Object[])second;

		if(firstArray.length != secondArray.length)
			return false;

		for(int i=0; i<firstArray.length; i++)
			if(!firstArray[i].equals(secondArray[i]))
				return false;

		return true;
	}

	public Value<T> load(String content, boolean singular)
	{
		if(content == null || singular)
		{
			value = parsePropertyElement(content);
			return this;
		}

		String[] elements = content.split(";");
		for(int i = 0; i < elements.length; i++)
		{
			String element = elements[i];
			int seperatorIndex = element.indexOf(':');
			String key = null;
			String keyValue = null;
			if(seperatorIndex > 0)
			{
				key = element.substring(0, seperatorIndex);
				keyValue = element.substring(seperatorIndex + 1);
			}
			else
				keyValue = element;

			T value = parsePropertyElement(keyValue);
			if(value != null)
				put(key, value);
			else
			{
				if (unparsableStrings == null)
					unparsableStrings = new ArrayList<String>();
				unparsableStrings.add(keyValue);
			}
		}
		return this;
	}

	public Iterator<String> getUnparsableStrings()
	{
		if(unparsableStrings != null)
			return unparsableStrings.iterator();
		return null;
	}

	public void print(PrintWriter printer, String[] sorted)
	{
		boolean first = true;
		Iterator<String> keys = GetAllKeys(sorted, this);
		while(keys.hasNext())
		{
			String key = keys.next();
			if(key == Null && value == null)
				continue;

			if(!first)
				printer.print(";");
			else
				first = false;

			if(key != Null)
			{
				printer.print(key);
				printer.print(":");
			}

			printDisplayString(printer, get(key));
		}
	}

	@Override
	public String toString()
	{
		StringWriter result = new StringWriter();
		print(new PrintWriter(result), null);
		return result.toString();
	}

	public static String createDisplayString(Object value)
	{
		if(value instanceof String[] || value instanceof Map)
		{
			StringWriter result = new StringWriter();
			printDisplayString(new PrintWriter(result), value);
			return result.toString();
		}

		return getDisplayString(value);
	}

	private static void printDisplayString(PrintWriter printer, Object value)
	{
		if(value instanceof String[])
		{
			boolean first = true;
			String[] values = (String[])value;
			for(int i = 0; i < values.length; i++)
			{
				if(first)
					first = false;
				else
					printer.print(",");
				printer.print(getDisplayString(values[i]));
			}
		}
		else if(value instanceof Map)
		{
			boolean first = true;
			Map<?,?> values = (Map<?,?>)value;
			List<String> keys = new ArrayList<String>(values.size());
			for(Object key : values.keySet())
				keys.add((String)key);
			Collections.sort(keys);
			for(int i = 0; i < keys.size(); i++)
			{
				if(first)
					first = false;
				else
					printer.print(",");
				String key = keys.get(i);
				printer.print(getDisplayString(key));
				printer.print(",");
				printer.print(getDisplayString(values.get(key)));
			}
		}
		else
			printer.print(getDisplayString(value));
	}

	private static String getDisplayString(Object value)
	{
		String result = value.toString();
		if(result.endsWith(".0"))
			result = result.substring(0, result.length() - 2);
		return result;
	}

	private T parsePropertyElement(String stringToParse)
	{
		int baseType = Properties.getBaseType(type);
		if(baseType == Properties.Boolean)
			return (T)tryParseBoolean(stringToParse);
		if(baseType == Properties.Float)
			return (T)tryParseFloat(stringToParse);
		if(baseType == Properties.Integer)
			return (T)tryParseInteger(stringToParse);
		if(baseType == Properties.Strings)
			return (T)tryParseStrings(stringToParse);
		if(baseType == Properties.StringMap)
			return (T)tryParseStringMap(stringToParse);
		if(baseType == Properties.IntegerMap)
			return (T)tryParseIntegerMap(stringToParse);
		if(baseType == Properties.String)
			return (T)tryParseString(stringToParse);
		return null;
	}

	public static Boolean tryParseBoolean(String value)
	{
		try
		{
			if(value != null)
				return value.equals("true") ? Boolean.TRUE : value.equals("false") ? Boolean.FALSE : null;
		}
		catch(Exception e)
		{
		}
		return null;
	}

	public static Float tryParseFloat(String value)
	{
		try
		{
			if(value != null)
				return Float.parseFloat(value);
		}
		catch(Exception e)
		{
		}
		return null;
	}

	public static Integer tryParseInteger(String value)
	{
		try
		{
			if(value != null)
				return Integer.parseInt(value);
		}
		catch(Exception e)
		{
		}
		return null;
	}

	public static String tryParseString(String value)
	{
		return value == null ? null : value;
	}

	public static String[] tryParseStrings(String value)
	{
		return value == null ? null : (value.isEmpty() ? new String[0] : value.split(","));
	}

	public static Map<String,String> tryParseStringMap(String value)
	{
		String[] keyValues = value.split(",");
		Map<String,String> result = new HashMap<String, String>();
		for(int i=0; i<keyValues.length; i++)
		{
			String key = keyValues[i++];
			if(i<keyValues.length)
				result.put(key, keyValues[i]);
		}
		return result;
	}

	public static Map<String,Integer> tryParseIntegerMap(String value)
	{
		String[] keyValues = value.split(",");
		Map<String,Integer> result = new HashMap<String, Integer>();
		for(int i=0; i<keyValues.length; i++)
		{
			String key = keyValues[i++];
			if(i<keyValues.length)
				result.put(key, Integer.parseInt(keyValues[i]));
		}
		return result;
	}

	public Value<T> e(T e)
	{
		return this.put("e", e);
	}

	public Value<T> m(T m)
	{
		return this.put("m", m);
	}

	public Value<T> h(T h)
	{
		return this.put("h", h);
	}

	public Value<T> c(T c)
	{
		return this.put("c", c);
	}

	public Value<T> a(T a)
	{
		return this.put("a", a);
	}

	public final static String Null = "null";
	private final static List<String> _allkeys = new LinkedList<String>();

	public final static Class<?> keyboard = Reflect.LoadClass(Value.class, new Name("org.lwjgl.input.Keyboard"), false);
	public final static Class<?> mouse = Reflect.LoadClass(Value.class, new Name("org.lwjgl.input.Mouse"), false);

	public final static Method _getKeyName = keyboard != null ? Reflect.GetMethod(keyboard, new Name("getKeyName"), int.class) : null;
	public final static Method _getKeyIndex = keyboard != null ? Reflect.GetMethod(keyboard, new Name("getKeyIndex"), String.class) : null;

	public final static Method _getButtonName = mouse != null ? Reflect.GetMethod(mouse, new Name("getButtonName"), int.class) : null;
	public final static Method _getButtonIndex = mouse != null ? Reflect.GetMethod(mouse, new Name("getButtonIndex"), String.class) : null;

	public static String toKeyName(Integer keyCode)
	{
		if(keyCode == null)
			return null;

		if(keyCode >= 0)
			return (String)Reflect.Invoke(_getKeyName, null, keyCode);

		return (String)Reflect.Invoke(_getButtonName, null, keyCode + 100);
	}

	private static Integer toKeyCode(String keyName)
	{
		if(keyName == null)
			return null;

		keyName = keyName.toUpperCase();
		int keyCode = (Integer)Reflect.Invoke(_getKeyIndex, null, keyName);
		if(keyCode > 0)
			return keyCode;

		keyCode = (Integer)Reflect.Invoke(_getButtonIndex, null, keyName);
		if(keyCode >= 0)
			return keyCode - 100;
		return null;
	}

	private static Dictionary<Object, Set<Integer>> toBlockConfig(String[] config)
	{
		if(config == null)
			return null;

		Dictionary<Object, Set<Integer>> result = new Hashtable<Object, Set<Integer>>();
		for(int i=0; i<config.length; i++)
		{
			String[] elements = config[i].split("/");
			if (elements.length > 0)
			{
				Set<Integer> metaDatas = new HashSet<Integer>();

				String blockName = elements[0];
				result.put(blockName, metaDatas);

				if (blockName.matches("[0-9]+"))
					result.put(Integer.parseInt(blockName), metaDatas);

				for(int n=1; n<elements.length; n++)
				{
					String metaDataText = elements[n];
					if (metaDataText.matches("[0-9]+"))
						metaDatas.add(Integer.parseInt(metaDataText));
				}
			}
		}
		return result;
	}
}