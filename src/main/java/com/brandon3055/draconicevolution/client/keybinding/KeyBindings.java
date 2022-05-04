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

/**
 * Created by Brandon on 14/08/2014.
 */
@OnlyIn(Dist.CLIENT)
public class KeyBindings {


    public static KeyBinding placeItem;
    public static KeyBinding toolConfig;
    public static KeyBinding toolModules;
    public static KeyBinding toggleFlight;
//    public static KeyBinding hudConfig;
    public static KeyBinding toggleMagnet;
    public static KeyBinding dislocatorTeleport;
    public static KeyBinding dislocatorBlink;
    public static KeyBinding dislocatorGui;
    public static KeyBinding dislocatorUp;
    public static KeyBinding dislocatorDown;
//    public static KeyBinding cycleDigAOE;
//    public static KeyBinding cycleAttackAOE;

    public static void init() {
        placeItem           = new KeyBinding("key.draconicevolution.place_item",            new CustomContext(KeyConflictContext.IN_GAME, () -> placeItem),                InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_P,         DraconicEvolution.MODNAME);
        toolConfig          = new KeyBinding("key.draconicevolution.tool_config",           new CustomContext(KeyConflictContext.IN_GAME, () -> toolConfig),               InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_C,         DraconicEvolution.MODNAME);
        toggleFlight        = new KeyBinding("key.draconicevolution.toggle_flight",         new CustomContext(KeyConflictContext.IN_GAME, () -> toggleFlight),             InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,   DraconicEvolution.MODNAME);
        toggleMagnet        = new KeyBinding("key.draconicevolution.toggle_magnet",         new CustomContext(KeyConflictContext.IN_GAME, () -> toggleMagnet),             InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,   DraconicEvolution.MODNAME);
        dislocatorTeleport  = new KeyBinding("key.draconicevolution.dislocator_teleport",   new CustomContext(KeyConflictContext.IN_GAME, () -> dislocatorTeleport),       InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,   DraconicEvolution.MODNAME);
        dislocatorBlink     = new KeyBinding("key.draconicevolution.dislocator_blink",      new CustomContext(KeyConflictContext.IN_GAME, () -> dislocatorBlink),          InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,   DraconicEvolution.MODNAME);
        dislocatorGui       = new KeyBinding("key.draconicevolution.dislocator_gui",        new CustomContext(KeyConflictContext.IN_GAME, () -> dislocatorGui),            InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,   DraconicEvolution.MODNAME);
        dislocatorUp        = new KeyBinding("key.draconicevolution.dislocator_up",       new CustomContext(KeyConflictContext.IN_GAME, () -> dislocatorUp),           InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,   DraconicEvolution.MODNAME);
        dislocatorDown      = new KeyBinding("key.draconicevolution.dislocator_down",       new CustomContext(KeyConflictContext.IN_GAME, () -> dislocatorDown),           InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,   DraconicEvolution.MODNAME);
//        cycleDigAOE        = new KeyBinding("key.cycleDigAOE",        new CustomContext(IN_GAME, () -> cycleDigAOE),        InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,   DraconicEvolution.MODNAME);
//        cycleAttackAOE     = new KeyBinding("key.cycleAttackAOE",     new CustomContext(IN_GAME, () -> cycleAttackAOE),     InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,   DraconicEvolution.MODNAME);

        toolModules         = new KeyBinding("key.draconicevolution.tool_modules",          new CustomContext(KeyConflictContext.IN_GAME, () -> toolModules),          KeyModifier.SHIFT, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_C,         DraconicEvolution.MODNAME);
//        hudConfig         = new KeyBinding("key.tool_config",         new CustomContext(IN_GAME, () -> hudConfig),           InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,         DraconicEvolution.MODNAME);

        ClientRegistry.registerKeyBinding(placeItem);
        ClientRegistry.registerKeyBinding(toolConfig);
        ClientRegistry.registerKeyBinding(toolModules);
        ClientRegistry.registerKeyBinding(toggleFlight);

        ClientRegistry.registerKeyBinding(toggleMagnet);
        ClientRegistry.registerKeyBinding(dislocatorTeleport);
        ClientRegistry.registerKeyBinding(dislocatorBlink);
        ClientRegistry.registerKeyBinding(dislocatorGui);
        ClientRegistry.registerKeyBinding(dislocatorUp);
        ClientRegistry.registerKeyBinding(dislocatorDown);



//        ClientRegistry.registerKeyBinding(hudConfig);
//        ClientRegistry.registerKeyBinding(cycleDigAOE);
//        ClientRegistry.registerKeyBinding(cycleAttackAOE);

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
            return otherBind.getKey().getValue() == binding.get().getKey().getValue() && otherBind.getKeyModifier() == binding.get().getKeyModifier();
        }
    }
}
