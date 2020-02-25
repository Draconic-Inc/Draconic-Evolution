package com.brandon3055.draconicevolution.client.keybinding;

import com.brandon3055.draconicevolution.DraconicEvolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;

import static net.minecraftforge.client.settings.KeyConflictContext.IN_GAME;

/**
 * Created by Brandon on 14/08/2014.
 */
@OnlyIn(Dist.CLIENT)
public class KeyBindings {


    public static KeyBinding placeItem;
    public static KeyBinding toolConfig;
    public static KeyBinding toolProfileChange;
    public static KeyBinding toggleFlight;
    public static KeyBinding toggleDislocator;
    public static KeyBinding cycleDigAOE;
    public static KeyBinding cycleAttackAOE;

    public static KeyBinding armorProfileChange;


    public static void init() {
        placeItem          = new KeyBinding("key.placeItem",          new CustomContext(IN_GAME, () -> placeItem),          InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_P,         DraconicEvolution.MODNAME);
        toolConfig         = new KeyBinding("key.toolConfig",         new CustomContext(IN_GAME, () -> toolConfig),         InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_C,         DraconicEvolution.MODNAME);
        toolProfileChange  = new KeyBinding("key.toolProfileChange",  new CustomContext(IN_GAME, () -> toolProfileChange),  InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_BACKSLASH, DraconicEvolution.MODNAME);
        toggleFlight       = new KeyBinding("key.toggleFlight",       new CustomContext(IN_GAME, () -> toggleFlight),       InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,   DraconicEvolution.MODNAME);
        toggleDislocator   = new KeyBinding("key.toggleDislocator",   new CustomContext(IN_GAME, () -> toggleDislocator),   InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,   DraconicEvolution.MODNAME);
        cycleDigAOE        = new KeyBinding("key.cycleDigAOE",        new CustomContext(IN_GAME, () -> cycleDigAOE),        InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,   DraconicEvolution.MODNAME);
        cycleAttackAOE     = new KeyBinding("key.cycleAttackAOE",     new CustomContext(IN_GAME, () -> cycleAttackAOE),     InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,   DraconicEvolution.MODNAME);

        armorProfileChange = new KeyBinding("key.armorProfileChange", new CustomContext(IN_GAME, () -> armorProfileChange), KeyModifier.ALT, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_BACKSLASH, DraconicEvolution.MODNAME);

        ClientRegistry.registerKeyBinding(placeItem);
        ClientRegistry.registerKeyBinding(toolConfig);
        ClientRegistry.registerKeyBinding(toolProfileChange);
        ClientRegistry.registerKeyBinding(toggleFlight);
        ClientRegistry.registerKeyBinding(toggleDislocator);
        ClientRegistry.registerKeyBinding(cycleDigAOE);
        ClientRegistry.registerKeyBinding(cycleAttackAOE);

        ClientRegistry.registerKeyBinding(armorProfileChange);
    }


    private static class CustomContext implements IKeyConflictContext {
        private KeyConflictContext context;
        private Supplier<KeyBinding> binding;

        public CustomContext(KeyConflictContext context, Supplier<KeyBinding> binding) {
            this.context = context;
            this.binding = binding;
        }

        @Override
        public boolean isActive() {
            return context.isActive();
        }

        @Override
        public boolean conflicts(IKeyConflictContext other) {
            if (!(other instanceof CustomContext)) {
                return other == context;
            }

            if (((CustomContext) other).context != context) {
                return false;
            }

            KeyBinding otherBind = ((CustomContext) other).binding.get();
            return otherBind.getKey().getKeyCode() == binding.get().getKey().getKeyCode() && otherBind.getKeyModifier() == binding.get().getKeyModifier();
        }
    }
}
