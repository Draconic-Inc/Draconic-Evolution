package com.brandon3055.draconicevolution.client.keybinding;

import com.brandon3055.draconicevolution.common.lib.References;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

/**
 * Created by Brandon on 14/08/2014.
 */
@SideOnly(Side.CLIENT)
public class KeyBindings {
	public static KeyBinding placeItem;

	public static void init() {
		placeItem = new KeyBinding("key.pong", Keyboard.KEY_P, "key.categories."+ References.MODID);
		ClientRegistry.registerKeyBinding(placeItem);
	}
}
