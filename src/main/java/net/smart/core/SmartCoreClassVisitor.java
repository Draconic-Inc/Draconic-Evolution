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

import java.io.*;
import java.util.*;

import org.objectweb.asm.*;

public final class SmartCoreClassVisitor extends ClassVisitor
{
	public static byte[] transform(byte[] bytes, List<SmartCoreTransformation> transformations)
	{
		try
		{
			ByteArrayInputStream in = new ByteArrayInputStream(bytes);
			ClassReader cr = new ClassReader(in);
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			SmartCoreClassVisitor p = new SmartCoreClassVisitor(cw, transformations);

			cr.accept(p, 0);

			byte[] result = cw.toByteArray();
			in.close();
			return result;
		}
		catch(IOException ioe)
		{
			throw new RuntimeException(ioe);
		}
	}

	private final List<SmartCoreTransformation> transformations;

	public SmartCoreClassVisitor(ClassVisitor classVisitor, List<SmartCoreTransformation> transformations)
	{
		super(262144, classVisitor);
		this.transformations = transformations;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
	{
		MethodVisitor base = super.visitMethod(access, name, desc, signature, exceptions);

		for(SmartCoreTransformation transformation : transformations)
			if(name.equals(transformation.methodName) && desc.equals(transformation.getMethodDesc()))
				return new SmartCoreMethodVisitor(base, transformation);

		return base;
	}
}