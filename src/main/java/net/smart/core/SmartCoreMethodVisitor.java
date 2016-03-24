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

import org.objectweb.asm.*;

public class SmartCoreMethodVisitor extends MethodVisitor
{
	private final SmartCoreTransformation transformation;

	public SmartCoreMethodVisitor(MethodVisitor paramMethodVisitor, SmartCoreTransformation transformation)
	{
		super(262144, paramMethodVisitor);

		this.transformation = transformation;

		invokeHook(transformation.beforeHookMethodName);
	}

	@Override
	public void visitInsn(int opcode)
	{
		if(opcode == Opcodes.RETURN || opcode == Opcodes.IRETURN || opcode == Opcodes.LRETURN ||opcode == Opcodes.FRETURN || opcode == Opcodes.DRETURN || opcode == Opcodes.ARETURN)
			invokeHook(transformation.afterHookMethodName);
		super.visitInsn(opcode);
	}

	private void invokeHook(String hookName)
	{
		if (hookName == null)
			return;

		mv.visitVarInsn(Opcodes.ALOAD, 0);
		int offset = 1;
		for(int i=0; i<transformation.parameterTypeNames.length; i++)
		{
			String typeName = transformation.parameterTypeNames[i];
			mv.visitVarInsn(LoadOpcode(typeName), offset);
			offset += LoadOffset(typeName);
		}
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, transformation.getHookClassDesc(), hookName, transformation.getHookMethodDesc(), false);
	}

	private static int LoadOpcode(String typeName)
	{
		if(typeName.equals(boolean.class.getName()) || typeName.equals(byte.class.getName()) || typeName.equals(short.class.getName()) || typeName.equals(int.class.getName()))
			return Opcodes.ILOAD;
		if(typeName.equals(long.class.getName()))
			return Opcodes.LLOAD;
		if(typeName.equals(float.class.getName()))
			return Opcodes.FLOAD;
		if(typeName.equals(double.class.getName()))
			return Opcodes.DLOAD;
		return Opcodes.ALOAD;
	}

	private static int LoadOffset(String typeName)
	{
		if(typeName.equals(long.class.getName()) || typeName.equals(double.class.getName()))
			return 2;
		return 1;
	}
}