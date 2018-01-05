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
    public static KeyBinding placeItem = new KeyBinding("key.placeItem", Keyboard.KEY_P, DraconicEvolution.MODNAME);
    public static KeyBinding toolConfig = new KeyBinding("key.toolConfig", Keyboard.KEY_C, DraconicEvolution.MODNAME);
    public static KeyBinding toolProfileChange = new KeyBinding("key.toolProfileChange", Keyboard.KEY_BACKSLASH, DraconicEvolution.MODNAME);
    public static KeyBinding toggleFlight = new KeyBinding("key.toggleFlight", Keyboard.KEY_NONE, DraconicEvolution.MODNAME);


    public static void init() {
        ClientRegistry.registerKeyBinding(placeItem);
        ClientRegistry.registerKeyBinding(toolConfig);
        ClientRegistry.registerKeyBinding(toolProfileChange);
        ClientRegistry.registerKeyBinding(toggleFlight);
    }
}
