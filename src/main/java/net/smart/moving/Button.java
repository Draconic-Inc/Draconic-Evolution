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

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.settings.*;

public class Button extends SmartMovingContext
{
	public boolean Pressed;
	public boolean WasPressed;

	public boolean StartPressed;
	public boolean StopPressed;

	public void update(KeyBinding binding)
	{
		update(Minecraft.getMinecraft().inGameHasFocus && isKeyDown(binding));
	}

	public void update(int keyCode)
	{
		update(Minecraft.getMinecraft().inGameHasFocus && isKeyDown(keyCode));
	}

	public void update(boolean pressed)
	{
		WasPressed = Pressed;
		Pressed = pressed;

		StartPressed = !WasPressed && Pressed;
		StopPressed = WasPressed && !Pressed;
	}

	private static boolean isKeyDown(KeyBinding keyBinding)
	{
		return isKeyDown(keyBinding, keyBinding.isPressed());
	}

	private static boolean isKeyDown(KeyBinding keyBinding, boolean wasDown)
	{
		GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
		if(currentScreen == null || currentScreen.allowUserInput)
			return isKeyDown(keyBinding.getKeyCode());
		return wasDown;
	}

	private static boolean isKeyDown(int keyCode)
	{
		if(keyCode >= 0)
			return Keyboard.isKeyDown(keyCode);
		return Mouse.isButtonDown(keyCode + 100);
	}
}