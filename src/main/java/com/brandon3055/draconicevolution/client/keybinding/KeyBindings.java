package com.brandon3055.draconicevolution.client.keybinding;

import com.brandon3055.draconicevolution.DraconicEvolution;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import static net.minecraftforge.client.settings.KeyConflictContext.IN_GAME;

/**
 * Created by Brandon on 14/08/2014.
 */
@SideOnly(Side.CLIENT)
public class KeyBindings {
    public static KeyBinding placeItem = new KeyBinding("key.placeItem", IN_GAME, Keyboard.KEY_P, DraconicEvolution.MODNAME);
    public static KeyBinding toolConfig = new KeyBinding("key.toolConfig", IN_GAME, Keyboard.KEY_C, DraconicEvolution.MODNAME);
    public static KeyBinding toolProfileChange = new KeyBinding("key.toolProfileChange", IN_GAME, Keyboard.KEY_BACKSLASH, DraconicEvolution.MODNAME);
    public static KeyBinding armorProfileChange = new KeyBinding("key.armorProfileChange", IN_GAME,  KeyModifier.ALT, Keyboard.KEY_BACKSLASH, DraconicEvolution.MODNAME);
    public static KeyBinding toggleFlight = new KeyBinding("key.toggleFlight", IN_GAME, Keyboard.KEY_NONE, DraconicEvolution.MODNAME);
    public static KeyBinding toggleDislocator = new KeyBinding("key.toggleDislocator", IN_GAME, Keyboard.KEY_NONE, DraconicEvolution.MODNAME);
    public static KeyBinding cycleDigAOE = new KeyBinding("key.cycleDigAOE", IN_GAME, Keyboard.KEY_NONE, DraconicEvolution.MODNAME);
    public static KeyBinding cycleAttackAOE = new KeyBinding("key.cycleAttackAOE", IN_GAME, Keyboard.KEY_NONE, DraconicEvolution.MODNAME);
	public static KeyBinding toggleShields = new KeyBinding("key.toggleShields", IN_GAME, Keyboard.KEY_NONE, DraconicEvolution.MODNAME);

    public static void init() {
        ClientRegistry.registerKeyBinding(placeItem);
        ClientRegistry.registerKeyBinding(toolConfig);
        ClientRegistry.registerKeyBinding(toolProfileChange);
        ClientRegistry.registerKeyBinding(armorProfileChange);
        ClientRegistry.registerKeyBinding(toggleFlight);
        ClientRegistry.registerKeyBinding(toggleDislocator);
        ClientRegistry.registerKeyBinding(cycleDigAOE);
        ClientRegistry.registerKeyBinding(cycleAttackAOE);
        ClientRegistry.registerKeyBinding(toggleShields);
    }
}
