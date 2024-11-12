package com.brandon3055.draconicevolution.client.keybinding;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;

/**
 * Created by Brandon on 14/08/2014.
 */
@OnlyIn(Dist.CLIENT)
public class KeyBindings {


    public static KeyMapping placeItem;
    public static KeyMapping toolConfig;
    public static KeyMapping toolModules;
    public static KeyMapping toggleFlight;
    //    public static KeyBinding hudConfig;
    public static KeyMapping toggleMagnet;
    public static KeyMapping dislocatorTeleport;
    public static KeyMapping dislocatorBlink;
    public static KeyMapping dislocatorGui;
    public static KeyMapping dislocatorUp;
    public static KeyMapping dislocatorDown;
//    public static KeyBinding cycleDigAOE;
//    public static KeyBinding cycleAttackAOE;

    public static void init(IEventBus eventBus) {
        //@formatter:off
        placeItem           = new KeyMapping("key.draconicevolution.place_item",            new CustomContext(KeyConflictContext.IN_GAME, () -> placeItem),                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_P,         DraconicEvolution.MODNAME);
        toolConfig          = new KeyMapping("key.draconicevolution.tool_config",           new CustomContext(KeyConflictContext.IN_GAME, () -> toolConfig),               InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C,         DraconicEvolution.MODNAME);
        toggleFlight        = new KeyMapping("key.draconicevolution.toggle_flight",         new CustomContext(KeyConflictContext.IN_GAME, () -> toggleFlight),             InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,   DraconicEvolution.MODNAME);
        toggleMagnet        = new KeyMapping("key.draconicevolution.toggle_magnet",         new CustomContext(KeyConflictContext.IN_GAME, () -> toggleMagnet),             InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,   DraconicEvolution.MODNAME);
        dislocatorTeleport  = new KeyMapping("key.draconicevolution.dislocator_teleport",   new CustomContext(KeyConflictContext.IN_GAME, () -> dislocatorTeleport),       InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,   DraconicEvolution.MODNAME);
        dislocatorBlink     = new KeyMapping("key.draconicevolution.dislocator_blink",      new CustomContext(KeyConflictContext.IN_GAME, () -> dislocatorBlink),          InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,   DraconicEvolution.MODNAME);
        dislocatorGui       = new KeyMapping("key.draconicevolution.dislocator_gui",        new CustomContext(KeyConflictContext.IN_GAME, () -> dislocatorGui),            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,   DraconicEvolution.MODNAME);
        dislocatorUp        = new KeyMapping("key.draconicevolution.dislocator_up",       new CustomContext(KeyConflictContext.IN_GAME, () -> dislocatorUp),           InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,   DraconicEvolution.MODNAME);
        dislocatorDown      = new KeyMapping("key.draconicevolution.dislocator_down",       new CustomContext(KeyConflictContext.IN_GAME, () -> dislocatorDown),           InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,   DraconicEvolution.MODNAME);
//        cycleDigAOE        = new KeyBinding("key.cycleDigAOE",        new CustomContext(IN_GAME, () -> cycleDigAOE),        InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,   DraconicEvolution.MODNAME);
//        cycleAttackAOE     = new KeyBinding("key.cycleAttackAOE",     new CustomContext(IN_GAME, () -> cycleAttackAOE),     InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,   DraconicEvolution.MODNAME);

        toolModules         = new KeyMapping("key.draconicevolution.tool_modules",          new CustomContext(KeyConflictContext.IN_GAME, () -> toolModules),          KeyModifier.SHIFT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C,         DraconicEvolution.MODNAME);
//        hudConfig         = new KeyBinding("key.tool_config",         new CustomContext(IN_GAME, () -> hudConfig),           InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,         DraconicEvolution.MODNAME);
        //@formatter:on

        eventBus.addListener(KeyBindings::registerKeyMappings);
    }

    private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(placeItem);
        event.register(toolConfig);
        event.register(toolModules);
        event.register(toggleFlight);
        event.register(toggleMagnet);
        event.register(dislocatorTeleport);
        event.register(dislocatorBlink);
        event.register(dislocatorGui);
        event.register(dislocatorUp);
        event.register(dislocatorDown);
    }


    private static class CustomContext implements IKeyConflictContext {
        private KeyConflictContext context;
        private Supplier<KeyMapping> binding;

        public CustomContext(KeyConflictContext context, Supplier<KeyMapping> binding) {
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

            KeyMapping otherBind = ((CustomContext) other).binding.get();
            return otherBind.getKey().getValue() == binding.get().getKey().getValue() && otherBind.getKeyModifier() == binding.get().getKeyModifier();
        }
    }
}
