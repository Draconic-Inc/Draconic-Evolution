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
import java.util.*;

public class Property<T>
{
	private static final int printWidth = 69;
	private String comment;
	private String[] header;
	private int gap;

	private boolean explicitlyModified;
	private boolean implicitlyModified;
	private String aquiredString;
	private boolean singular;

	private final int type;
	private String currentVersion;
	private Map<String, Object> versionSources;
	private Map<String, Object> versionDefaults;

	private static int i = 0;
	private static final int Is = i++;
	private static final int And = i++;
	private static final int Or = i++;
	private static final int Not = i++;
	private static final int Plus = i++;
	private static final int EitherOr = i++;
	private static final int Maximum = i++;
	private static final int Minimum = i++;
	private static final int ToKeyName = i++;
	private static final int ToKeyCode = i++;
	private static final int ToBlockConfig = i++;

	public T value;
	private Value<T> systemValue;
	private Value<T> aquiredValue;

	private Object minValue;
	private Object maxValue;

	private Object local;

	private Object left;
	private int operator;
	private Object right;

	private List<Property<Boolean>> depends = null;

	public Property(int type)
	{
		this.type = type;
	}

	private Property(String key)
	{
		this(Properties.Key);
		set(new Value(key));
	}

	private Property(Object left, int operator, Object right)
	{
		this(Properties.Operator);
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	private Property(Object local, int operator, Object left, Object right)
	{
		this(Properties.Operator);
		this.local = local;
		this.operator = operator;
		this.left = left;
		this.right = right;
	}

	public void update(String key)
	{
		value = getKeyValue(key);
	}

	public void setValue(T value)
	{
		this.value = value;
		systemValue = new Value<T>(value);
		aquiredValue = new Value<T>(value);
		implicitlyModified = false;
		explicitlyModified = false;
	}

	public Property<T> singular()
	{
		singular = true;
		return this;
	}

	public Property<Boolean> is(Object value)
	{
		return new Property<Boolean>(this, Is, value);
	}

	public Property<Boolean> and(Object value)
	{
		return new Property<Boolean>(this, And, value);
	}

	public Property<Boolean> or(Object value)
	{
		return new Property<Boolean>(this, Or, value);
	}

	public Property<Boolean> andNot(Property<?> value)
	{
		return and(value.not());
	}

	public Property<Boolean> not()
	{
		return new Property<Boolean>(this, Not, null);
	}

	public Property<Float> plus(Object value)
	{
		return new Property<Float>(this, Plus, value);
	}

	public Property<?> eitherOr(Object either, Object or)
	{
		return new Property<Float>(this, EitherOr, either, or);
	}

	public Property<Float> maximum(Object value)
	{
		return new Property<Float>(this, Maximum, value);
	}

	public Property<Float> minimum(Object value)
	{
		return new Property<Float>(this, Minimum, value);
	}

	public Property<String> toKeyName()
	{
		return new Property<String>(this, ToKeyName, null);
	}

	public Property<Integer> toKeyCode(Integer defaultValue)
	{
		return new Property<Integer>(this, ToKeyCode, null).defaults(defaultValue);
	}

	public Property<Dictionary<Object, Set<Integer>>> toBlockConfig()
	{
		return new Property<Dictionary<Object, Set<Integer>>>(this, ToBlockConfig, null);
	}

	public Property<T> depends(Property<Boolean>... conditions)
	{
		if(depends == null)
			depends = new LinkedList<Property<Boolean>>();

		for(int i = 0; i < conditions.length; i++)
			depends.add(conditions[i]);

		return this;
	}

	public Property<T> values(Object defaultValue, Object minValue, Object maxValue)
	{
		return defaults(defaultValue).range(minValue, maxValue);
	}

	public Property<T> up(Object defaultValue, Object minValue)
	{
		return defaults(defaultValue).min(minValue);
	}

	public Property<T> down(Object defaultValue, Object maxValue)
	{
		return defaults(defaultValue).max(maxValue);
	}

	public Property<T> range(Object minValue, Object maxValue)
	{
		return min(minValue).max(maxValue);
	}

	public Property<T> defaults(Object defaultValue, String... versions)
	{
		versionDefaults = addVersioned(versionDefaults, defaultValue, versions);
		return this;
	}

	public Property<T> min(Object minValue)
	{
		this.minValue = minValue;
		return this;
	}

	public Property<T> max(Object maxValue)
	{
		this.maxValue = maxValue;
		return this;
	}

	public Property<T> key(String key, String... versions)
	{
		if(currentVersion == null)
			if(versions != null && versions.length > 0)
				currentVersion = versions[0];
			else
				currentVersion = Current;

		return source(new Property(key), versions);
	}

	public Property<T> source(Object source, String... versions)
	{
		versionSources = addVersioned(versionSources, source, versions);
		return this;
	}

	public Property<T> comment(String comment)
	{
		this.comment = comment;
		return this;
	}

	public Property<T> section(String... header)
	{
		gap = 1;
		this.header = header;
		return this;
	}

	public Property<T> chapter(String... header)
	{
		gap = 2;
		this.header = header;
		return this;
	}

	public Property<T> book(String... header)
	{
		gap = 3;
		this.header = header;
		return this;
	}

	public void reset()
	{
		explicitlyModified = false;
		implicitlyModified = false;

		value = null;
		systemValue = null;
		aquiredValue = null;

		reset(minValue);
		reset(maxValue);

		reset(left);
		reset(right);
		if(depends != null)
			for(int i = 0; i < depends.size(); i++)
				reset(depends.get(i));
	}

	private static void reset(Object value)
	{
		if(value instanceof Property)
			((Property<?>)value).reset();
	}

	public boolean load(Properties... propertiesList)
	{
		if(systemValue != null)
			return true;

		if(type == Properties.Constant)
			return true;

		if(type == Properties.Operator)
		{
			if(operator == EitherOr)
			{
				if(getValue(left) == null || getValue(right) == null || getValue(local) == null)
					return false;
			}

			if(operator == Is || operator == And || operator == Or || operator == Plus || operator == Maximum || operator == Minimum)
			{
				if(getValue(left) == null || getValue(right) == null)
					return false;
			}

			if(operator == Not || operator == ToKeyName || operator == ToKeyCode || operator == ToBlockConfig)
			{
				if(getValue(left) == null)
					return false;
			}

			Object operatorValue = null;
			if(operator == Is)
				operatorValue = getValue(left).is(getValue(right));
			else if(operator == And)
				operatorValue = getValue(left).and(getValue(right));
			else if(operator == Or)
				operatorValue = getValue(left).or(getValue(right));
			else if(operator == Not)
				operatorValue = getValue(left).not();
			else if(operator == Plus)
				operatorValue = getValue(left).plus(getValue(right));
			else if(operator == EitherOr)
				operatorValue = getValue(local).eitherOr(getValue(left), getValue(right));
			else if(operator == Maximum)
				operatorValue = getValue(left).maximum(getValue(right));
			else if(operator == Minimum)
				operatorValue = getValue(left).minimum(getValue(right));
			else if(operator == ToKeyName)
				operatorValue = getValue(left).toKeyName();
			else if(operator == ToKeyCode)
				operatorValue = getValue(left).toKeyCode();
			else if(operator == ToBlockConfig)
				operatorValue = getValue(left).toBlockConfig();

			if(operatorValue == null)
				throw new RuntimeException("Unknown operator '" + operator + "' found");

			return set(getValue(operatorValue));
		}

		if(propertiesList == null || versionSources == null)
			return false;

		if(depends != null)
		{
			for(int i = 0; i < depends.size(); i++)
				if(getValue(depends.get(i)) == null)
					return false;
		}

		Object minObject = getMinimumValue();
		Value<T> minValue = getValue(minObject);
		if(minObject != null && minValue == null)
			return false;

		Object maxObject = getMaximumValue();
		Value<T> maxValue = getValue(maxObject);
		if(maxObject != null && maxValue == null)
			return false;

		Value<T> defaultValue = getValue(getDefaultValue());
		for(int i = 0; i < propertiesList.length; i++)
		{
			Properties properties = propertiesList[i];
			Object source = getVersionSource(properties.version);
			if(source != null)
			{
				String key = getKey(source);
				aquiredValue = key != null ? getPropertyValue(properties, key) : getValue(source);
				if(aquiredValue != null)
				{
					Value<T> initValue = aquiredValue.clone();
					if(depends != null)
						for(int n = 0; n < depends.size(); n++)
							initValue.withDependency(getValue(depends.get(n)), defaultValue);

					if(minObject != null)
						initValue.withMinimum(minValue, defaultValue);

					if(maxObject != null)
						initValue.withMaximum(maxValue, defaultValue);

					return set(initValue);
				}
				else if(key == null)
					return false;
			}
		}
		return set(defaultValue);
	}

	private boolean set(Value<T> initValue)
	{
		systemValue = initValue;
		update(null);
		return true;
	}

	private static Map<String, Object> addVersioned(Map<String, Object> versioned, Object value, String... versions)
	{
		if(versioned == null)
			versioned = new Hashtable<String, Object>(1);

		if(versions == null || versions.length == 0)
			versions = CurrentArray;

		for(int i = 0; i < versions.length; i++)
			versioned.put(versions[i], value);
		return versioned;
	}

	public T getKeyValue(String key)
	{
		T value = systemValue.get(key);
		if(value == null)
			value = getValue(getDefaultValue()).get(null);
		if(value == null)
			value = (T)Properties.getDefaultValue(type);
		return value;
	}

	private Value<T> getValue(Object value)
	{
		if(value instanceof Property)
		{
			Property<?> property = (Property<?>)value;
			property.load((Properties[])null);
			return (Value<T>)property.systemValue;
		}
		if(value instanceof Value<?>)
			return (Value<T>)value;
		return new Value<T>((T)value);
	}

	private Object getDefaultValue(String version)
	{
		Object defaultValue = null;
		if(versionDefaults != null)
		{
			if(version != null)
				defaultValue = versionDefaults.get(version);
			if(defaultValue == null)
				defaultValue = versionDefaults.get(Current);
		}
		if(defaultValue == null)
			defaultValue = Properties.getDefaultValue(type);
		return defaultValue;
	}

	private Object getDefaultValue()
	{
		return getDefaultValue(Current);
	}

	private Object getMinimumValue()
	{
		if(minValue != null)
			return minValue;
		return Properties.getMinimumValue(type);
	}

	private Object getMaximumValue()
	{
		if(maxValue != null)
			return maxValue;
		return Properties.getMaximumValue(type);
	}

	private Object getVersionSource(String version)
	{
		if(versionSources == null)
			return null;

		Object source = null;
		if(version != null)
			source = versionSources.get(version);
		if(source == null)
			source = versionSources.get(Current);
		return source;
	}

	private Value<T> getPropertyValue(Properties properties, String key)
	{
		String propertyString = properties.getProperty(key);
		if(propertyString != null)
			aquiredString = propertyString;

		String stringToParse = propertyString;
		if(propertyString != null)
		{
			stringToParse = propertyString.trim();
			explicitlyModified = stringToParse.endsWith("!");
			if(explicitlyModified)
				stringToParse = stringToParse.substring(0, stringToParse.length() - 1);
			stringToParse = stringToParse.trim();
		}

		Value<T> value = parsePropertyValue(stringToParse);
		implicitlyModified = stringToParse != null && (value == null || !value.equals(getValue(getDefaultValue(properties.version))));
		if(!explicitlyModified && !implicitlyModified)
			return getValue(getDefaultValue());
		return value;
	}

	private Value<T> parsePropertyValue(String stringToParse)
	{
		if(stringToParse != null)
			return new Value<T>(type).load(stringToParse, singular);
		return null;
	}

	public boolean print(PrintWriter printer, String[] sorted, String version, boolean comments)
	{
		if(!isPersistent() || systemValue == null)
			return false;

		if(getVersionSource(version) == null)
			return false;

		int gap = this.gap + (comment == null ? -1 : 1);
		for(int i = 0; i < gap; i++)
			printer.println();

		if(header != null && header.length > 0)
			printHeader(printer);

		if(comment != null && comments)
		{
			printer.print("# ");
			printer.print(comment);
			printer.println();
		}

		if(aquiredString == null)
		{
			printValue(printer, sorted, false);
			return true;
		}

		boolean error = false;

		Iterator<String> unparsed = aquiredValue.getUnparsableStrings();
		while(unparsed != null && unparsed.hasNext())
		{
			String unparsableString = unparsed.next();
			printErrorPrefix(printer);
			printer.print("Could not interpret string \"");
			printer.print(unparsableString);
			printer.print("\" as ");
			printer.print(Properties.getBaseTypeName(Properties.getBaseType(type)));
			printer.print(" value, used ");
			printer.print(!aquiredString.isEmpty() && aquiredValue.get(null) != null ? "local" : "system");
			printer.print(" default");
			printValuePostfix(printer, null);
			printErrorPostfix(printer);
			error = true;
		}

		Object minObject = getMinimumValue();
		Value<T> minValue = getValue(minObject);

		Object maxObject = getMaximumValue();
		Value<T> maxValue = getValue(maxObject);

		Iterator<String> keys = Value.GetAllKeys(systemValue);
		while(keys.hasNext())
		{
			String key = keys.next();

			T parsedSingleValue = aquiredValue.getStored(key);
			T aquiredSingleValue = aquiredValue.get(key);
			T usedSingleValue = systemValue.getStored(key);

			if(parsedSingleValue == null)
				if(aquiredSingleValue == null)
					continue;
				else if(aquiredSingleValue.equals(usedSingleValue))
					continue;
				else
				{
					// local default value "aquiredSingleValue" invalid for specific key
				}
			else
			{
				if(parsedSingleValue.equals(usedSingleValue))
					continue;
				// else
					// local keyed value "parsedSingleValue" invalid
			}

			if(Properties.getBaseType(type) == Properties.Boolean && depends != null && !depends.isEmpty())
			{
				String dependKey = null;
				for(int i = 0; i < depends.size(); i++)
				{
					Property<Boolean> depend = depends.get(i);
					String currentDependKey = depend.getCurrentKey();
					if(currentDependKey != null && !depend.getKeyValue(key))
					{
						dependKey = currentDependKey;
						break;
					}
				}

				printWarnPrefix(printer);
				printValuePrefix(printer, key);
				printer.print("is ignored because ");

				if(dependKey != null)
				{
					printer.print("the ");

					if(key == Value.Null)
						printer.print("default");
					else
						printer.print("\"" + key + "\"");

					printer.print(" value of property \"");
					printer.print(dependKey);
					printer.print("\" is \"false\"");
				}
				else
					printer.print("one of the restricting expressions evaluated to \"false\"");

				printWarnPostfix(printer);
				error = true;
			}
			else
			{
				printErrorPrefix(printer);
				printValuePrefix(printer, key);
				printer.print("was out of range, used ");

				if(minValue != null && usedSingleValue.equals(minValue.get(key)))
					printer.print("minimum");
				else if(maxValue != null && usedSingleValue.equals(maxValue.get(key)))
					printer.print("maximum");
				else
					printer.print("in-range");

				printValuePostfix(printer, key);
				printErrorPostfix(printer);
				error = true;
			}
		}

		printValue(printer, sorted, error);
		return true;
	}

	private void printValue(PrintWriter printer, String[] sorted, boolean error)
	{
		printer.print(getCurrentKey());
		printer.print(":");

		if(aquiredString != null && (error || explicitlyModified))
			printer.print(aquiredString);
		else if(implicitlyModified && systemValue.equals(getValue(getDefaultValue())))
		{
			printer.print(aquiredString);
			printer.print("!");
		}
		else
			systemValue.print(printer, sorted);
	}

	private void printHeader(PrintWriter printer)
	{
		String title = header[0];
		String body = header.length > 1 ? header[1] : null;
		char separator = gap == 3 ? '=' : (gap == 2 ? '-' : ' ');
		printSeparation(printer, separator, printWidth);
		printer.print("# ");
		printer.println(title);
		if(body != null)
		{
			printSeparation(printer, '-', title.length());
			int lineWidth = printWidth - 2;
			while(true)
			{
				printer.print("# ");
				if(body.length() <= lineWidth)
				{
					printer.println(body);
					break;
				}

				int i;
				for(i = lineWidth; i > 0; i--)
					if(body.charAt(i) == ' ')
						break;

				printer.println(body.substring(0, i));
				body = body.substring(i + 1);
			}
		}
		printSeparation(printer, separator, printWidth);
		printer.println();
	}

	private static void printSeparation(PrintWriter printer, char seperator, int length)
	{
		printer.print("# ");
		for(int i = 0; i < length; i++)
			printer.print(seperator);
		printer.println();
	}

	private void printValuePrefix(PrintWriter printer, String key)
	{
		printer.print("Interpreted ");

		if(key != Value.Null)
		{
			printer.print("\"");
			printer.print(key);
			printer.print("\" ");
		}

		printer.print("value \"");
		printer.print(aquiredValue.get(key));
		printer.print("\" ");
	}

	private void printValuePostfix(PrintWriter printer, String key)
	{
		printer.print(" value \"");
		printer.print(aquiredString == null || aquiredString.isEmpty() ? getValue(getDefaultValue()) : getKeyValue(key));
		printer.print("\" instead");
	}

	private static void printErrorPrefix(PrintWriter printer)
	{
		printErrorPrefix(printer, false);
	}

	private static void printWarnPrefix(PrintWriter printer)
	{
		printErrorPrefix(printer, true);
	}

	private static void printErrorPrefix(PrintWriter printer, boolean warning)
	{
		printer.print("#");
		if(!warning)
			printer.print("!!");
		printer.print("! ");
	}

	private static void printErrorPostfix(PrintWriter printer)
	{
		printErrorPostfix(printer, false);
	}

	private static void printWarnPostfix(PrintWriter printer)
	{
		printErrorPostfix(printer, true);
	}

	private static void printErrorPostfix(PrintWriter printer, boolean warning)
	{
		printer.print(" !");
		if(!warning)
			printer.print("!!");
		printer.println("#");
	}

	public boolean isPersistent()
	{
		return versionSources != null && versionSources.size() > 0;
	}

	public String getCurrentKey()
	{
		if(isPersistent())
			return getKey(getVersionSource(currentVersion));
		return null;
	}

	private static String getKey(Object candidate)
	{
		if(candidate instanceof Property)
		{
			Property<?> property = (Property<?>)candidate;
			if(property.type == Properties.Key)
				return (String)property.value;
		}
		return null;
	}

	@Override
	public String toString()
	{
		if(isPersistent())
			return getCurrentKey();
		return super.toString();
	}

	public String getKeyValueString(String key)
	{
		return getValueString(getKeyValue(key));
	}

	public String getValueString()
	{
		return getValueString(value);
	}

	public String getValueString(T value)
	{
		return value != null ? Value.createDisplayString(value) : null;
	}

	private static final String Current = "";
	private static final String[] CurrentArray = new String[] { Current };
}