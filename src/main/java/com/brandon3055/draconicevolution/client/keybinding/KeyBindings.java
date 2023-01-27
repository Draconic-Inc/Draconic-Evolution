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
public final class KeyBindings {

    public static final KeyBinding placeItem;
    public static final KeyBinding toolConfig;
    public static final KeyBinding toolProfileChange;
    public static final KeyBinding toggleFlight;
    public static final KeyBinding toggleMagnet;

    static {
        placeItem = new KeyBinding("key.placeItem", Keyboard.KEY_P, References.MODNAME);
        toolConfig = new KeyBinding("key.toolConfig", Keyboard.KEY_NONE, References.MODNAME);
        toolProfileChange = new KeyBinding("key.toolProfileChange", Keyboard.KEY_NONE, References.MODNAME);
        toggleFlight = new KeyBinding("key.toggleFlight", Keyboard.KEY_NONE, References.MODNAME);
        toggleMagnet = new KeyBinding("key.toggleMagnet", Keyboard.KEY_NONE, References.MODNAME);
    }

    private KeyBindings() {}

    public static void init() {
        ClientRegistry.registerKeyBinding(placeItem);
        ClientRegistry.registerKeyBinding(toolConfig);
        ClientRegistry.registerKeyBinding(toolProfileChange);
        ClientRegistry.registerKeyBinding(toggleFlight);
        ClientRegistry.registerKeyBinding(toggleMagnet);
    }
}
