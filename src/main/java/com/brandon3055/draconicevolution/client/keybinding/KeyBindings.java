package com.brandon3055.draconicevolution.client.keybinding;

import com.brandon3055.draconicevolution.DraconicEvolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

/**
 * Created by Brandon on 14/08/2014.
 */
@SideOnly(Side.CLIENT)
public class KeyBindings {
	public static KeyBinding placeItem;
	public static KeyBinding toolConfig;
	public static KeyBinding toolProfileChange;

	public static void init() {
		placeItem = new KeyBinding("key.placeItem", Keyboard.KEY_P, DraconicEvolution.MODNAME);
		toolConfig = new KeyBinding("key.toolConfig", Keyboard.KEY_C, DraconicEvolution.MODNAME);
		toolProfileChange = new KeyBinding("key.toolProfileChange", Keyboard.KEY_BACKSLASH, DraconicEvolution.MODNAME);
		ClientRegistry.registerKeyBinding(placeItem);
		ClientRegistry.registerKeyBinding(toolConfig);
		ClientRegistry.registerKeyBinding(toolProfileChange);
	}
}
