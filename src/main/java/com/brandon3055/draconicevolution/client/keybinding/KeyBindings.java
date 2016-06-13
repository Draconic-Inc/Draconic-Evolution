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
    public static KeyBinding toolConfig;
    public static KeyBinding toolProfileChange;
    public static KeyBinding toggleFlight;

    public static void init() {
        placeItem = new KeyBinding("key.placeItem", Keyboard.KEY_P, References.MODNAME);
        toolConfig = new KeyBinding("key.toolConfig", Keyboard.KEY_C, References.MODNAME);
        toolProfileChange = new KeyBinding("key.toolProfileChange", Keyboard.KEY_BACKSLASH, References.MODNAME);
        toggleFlight = new KeyBinding("key.toggleFlight", Keyboard.KEY_NONE, References.MODNAME);
        ClientRegistry.registerKeyBinding(placeItem);
        ClientRegistry.registerKeyBinding(toolConfig);
        ClientRegistry.registerKeyBinding(toolProfileChange);
        ClientRegistry.registerKeyBinding(toggleFlight);
    }
}
