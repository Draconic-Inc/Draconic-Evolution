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

public class SmartCoreTransformation
{
	public final String className;
	public final String methodName;
	public final String[] parameterTypeNames;
	public final String returnType;
	public final String hookClassName;
	public final String beforeHookMethodName;
	public final String afterHookMethodName;

	public SmartCoreTransformation(String className, String methodName, String[] parameterTypeNames, String returnType, String hookClassName, String beforeHookMethodName, String afterHookMethodName)
	{
		this.className = className;
		this.methodName = methodName;
		this.parameterTypeNames = parameterTypeNames;
		this.returnType = returnType;
		this.hookClassName = hookClassName;
		this.beforeHookMethodName = beforeHookMethodName;
		this.afterHookMethodName = afterHookMethodName;
	}

	private static String getDescription(String type)
	{
		if(type == null || type == "void")
			return "V";
		if(type.endsWith("[]"))
			return "[" + getDescription(type.substring(0, type.length() - 2));
		if(type.equals("boolean"))
			return "Z";
		if(type.equals("byte"))
			return "B";
		if(type.equals("short"))
			return "S";
		if(type.equals("int"))
			return "I";
		if(type.equals("float"))
			return "F";
		if(type.equals("long"))
			return "J";
		if(type.equals("double"))
			return "D";
		return "L" + type.replace(".", "/") + ";";
	}

	private static String getDescriptions(String[] parameterTypes)
	{
		String result = "";
		for(int i=0; i<parameterTypes.length; i++)
			result += getDescription(parameterTypes[i]);
		return result + "";
	}

	private static String getMethodDescription(String[] parameterTypes, String resultType)
	{
		return "(" + getDescriptions(parameterTypes) + ")" + getDescription(resultType);
	}

	public String getMethodDesc()
	{
		return getMethodDescription(parameterTypeNames, returnType);
	}

	public String getHookClassDesc()
	{
		return hookClassName.replace(".", "/");
	}

	public String getHookMethodDesc()
	{
		List names = new ArrayList();
		names.add(className);
		for(int i=0; i<parameterTypeNames.length; i++)
			names.add(parameterTypeNames[i]);

		String[] result = new String[names.size()];
		names.toArray(result);
		return getMethodDescription(result, null);
	}
}